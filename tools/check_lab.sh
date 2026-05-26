#!/bin/bash
# =============================================================================
# check_lab.sh — Diagnóstico rápido do estado completo do laboratório.
#
# Verifica todos os componentes: OXC2, transponders, ONOS, hosts, keepalive.
#
# Uso:
#   bash tools/check_lab.sh
#   bash tools/check_lab.sh --watch    (repete a cada 30s)
# =============================================================================

ONOS="http://localhost:8181"
AUTH="onos:rocks"
AGENT_HOST="localhost"
AGENT_PORT=10151
OXC2="http://172.17.36.22:8008"

WATCH=false
[ "$1" = "--watch" ] && WATCH=true

check_once() {
    echo ""
    echo "════════════════════════════════════════════════════════════"
    echo "  LAB STATUS — $(date '+%Y-%m-%d %H:%M:%S')"
    echo "════════════════════════════════════════════════════════════"

    # ── Keepalive ──────────────────────────────────────────────────
    KPID=$(pgrep -f keepalive_cross.py 2>/dev/null | head -1)
    if [ -n "$KPID" ]; then
        LAST=$(tail -1 /tmp/keepalive_cross.log 2>/dev/null)
        echo "  Keepalive  : ✓ PID=$KPID | $LAST"
    else
        echo "  Keepalive  : ✗ NÃO ESTÁ RODANDO!"
        echo "               Execute: nohup python3 tools/keepalive_cross.py > /tmp/keepalive_cross.log 2>&1 &"
    fi

    # ── Cross-connects OXC2 ────────────────────────────────────────
    CROSS=$(curl -s -u admin:root "$OXC2/api/data/optical-switch:cross-connects" 2>/dev/null)
    if echo "$CROSS" | grep -q "ingress"; then
        PAIRS=$(echo "$CROSS" | python3 -c "
import sys, re
pairs = re.findall(r'<ingress>(\d+)</ingress>\s*<egress>(\d+)</egress>', sys.stdin.read())
print(', '.join([f'{i}→{e}' for i,e in pairs]))
" 2>/dev/null)
        echo "  OXC2 xconn: ✓ $PAIRS"
    else
        echo "  OXC2 xconn: ✗ AUSENTES ou OXC2 inacessível!"
    fi

    # ── Transponders via agente TCP ────────────────────────────────
    AGENT_DATA=$(echo '{"command":"get_status"}' | nc -w 3 $AGENT_HOST $AGENT_PORT 2>/dev/null)
    if [ -n "$AGENT_DATA" ]; then
        echo "$AGENT_DATA" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    for d in data.get('devices', []):
        if 'OTN' not in d.get('type',''):
            continue
        m = d['metrics']
        name = d['name']
        ch   = m.get('channel','?')
        rx   = m.get('inputPowerWDM')
        los  = m.get('isLOS', True)
        bdi  = m.get('isBDI', True)
        ok   = '✓' if (not los and not bdi) else '✗'
        rx_s = f'{rx:.2f} dBm' if rx is not None else 'None (LOS)'
        print(f'  {ok} {name}: canal={ch}  RX_WDM={rx_s}  LOS={los}  BDI={bdi}')
except Exception as e:
    print(f'  Erro ao parsear agente: {e}')
" 2>/dev/null
    else
        echo "  Transpond. : ✗ Agente TCP não responde em $AGENT_HOST:$AGENT_PORT"
    fi

    # ── ONOS devices ──────────────────────────────────────────────
    DEVICES=$(curl -s -u "$AUTH" "$ONOS/onos/v1/devices" 2>/dev/null)
    if [ -n "$DEVICES" ]; then
        COUNT=$(echo "$DEVICES" | python3 -c "import sys,json; print(len(json.load(sys.stdin).get('devices',[])))" 2>/dev/null)
        echo "$DEVICES" | python3 -c "
import sys, json
try:
    devs = json.load(sys.stdin)['devices']
    for d in devs:
        ok = '✓' if d['available'] else '✗'
        drv = d.get('driver','?')
        # Avisa se OXC2 está no ONOS (problema!)
        warn = ' ⚠️  OXC2 NO ONOS — REMOVA!' if '172.17.36.22' in d['id'] else ''
        print(f'  {ok} {d[\"id\"]} [{drv}]{warn}')
except Exception as e:
    print(f'  Erro: {e}')
" 2>/dev/null
    else
        echo "  ONOS       : ✗ não responde"
    fi

    # ── ONOS links ────────────────────────────────────────────────
    LINKS=$(curl -s -u "$AUTH" "$ONOS/onos/v1/links" 2>/dev/null)
    LLDP=$(echo "$LINKS" | python3 -c "
import sys, json
try:
    links = json.load(sys.stdin)['links']
    optical = [l for l in links if l.get('type','') in ('DIRECT','OPTICAL')
               and 'of:' in l['src']['device'] and 'of:' in l['dst']['device']]
    if optical:
        for l in optical:
            print(f'  ✓ LLDP: {l[\"src\"][\"device\"]}:{l[\"src\"][\"port\"]} ↔ {l[\"dst\"][\"device\"]}:{l[\"dst\"][\"port\"]}')
    else:
        print('  ✗ Nenhum link LLDP PAV1↔PAV2 (caminho óptico inativo ou up-mode=false)')
except:
    pass
" 2>/dev/null)
    [ -n "$LLDP" ] && echo "$LLDP" || echo "  LLDP       : ✗ sem link entre switches"

    # ── ONOS hosts ────────────────────────────────────────────────
    HOSTS=$(curl -s -u "$AUTH" "$ONOS/onos/v1/hosts" 2>/dev/null | \
        python3 -c "import sys,json; h=json.load(sys.stdin).get('hosts',[]); print(len(h))" 2>/dev/null)
    if [ "${HOSTS:-0}" -gt 0 ] 2>/dev/null; then
        echo "  Hosts ONOS : ✓ $HOSTS host(s) descobertos"
    else
        echo "  Hosts ONOS : ✗ 0 hosts (DC5/DC6 não enviaram tráfego ou up-mode=false)"
    fi

    # ── Resumo ────────────────────────────────────────────────────
    echo ""
    echo "  Branch git : $(git -C "$(dirname "${BASH_SOURCE[0]}")/.." branch 2>/dev/null | grep '*' | sed 's/\* //')"
    echo "════════════════════════════════════════════════════════════"
}

if $WATCH; then
    while true; do
        check_once
        sleep 30
    done
else
    check_once
fi
