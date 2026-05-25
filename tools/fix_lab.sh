#!/bin/bash
# =============================================================================
# fix_lab.sh — Corrige o estado do laboratório de uma vez:
#   1. Remove OXC2 do ONOS (impede que o driver apague cross-connects)
#   2. Mata qualquer keepalive antigo
#   3. Aplica cross-connects via PUT (ingress 1→13, ingress 5→9)
#   4. Inicia novo keepalive em background
#   5. Mostra status final
#
# Uso:
#   bash tools/fix_lab.sh
# =============================================================================

ONOS="http://localhost:8181"
AUTH="onos:rocks"
OXC2_ID="netconf:172.17.36.22:830"

echo "================================================================"
echo "  fix_lab.sh — Restauração do estado do laboratório"
echo "================================================================"

# ── 1. Remover OXC2 do ONOS ─────────────────────────────────────────────────
echo ""
echo "[1/5] Removendo OXC2 do ONOS..."
CFG_CODE=$(curl -sS -o /dev/null -w "%{http_code}" -X DELETE -u "$AUTH" \
     "$ONOS/onos/v1/network/configuration/devices/$OXC2_ID")
DEV_CODE=$(curl -sS -o /dev/null -w "%{http_code}" -X DELETE -u "$AUTH" \
     "$ONOS/onos/v1/devices/$OXC2_ID")
echo "  netcfg DELETE: HTTP $CFG_CODE"
echo "  device DELETE: HTTP $DEV_CODE"
sleep 2

# ── 2. Conferir que OXC2 sumiu ──────────────────────────────────────────────
DEVICE_COUNT=$(curl -sS -u "$AUTH" "$ONOS/onos/v1/devices" | python3 -c \
    "import sys,json; d=json.load(sys.stdin); print(len(d.get('devices',[])))" 2>/dev/null)
echo "  Dispositivos no ONOS agora: $DEVICE_COUNT (esperado: 3 — PAV1, PAV2, Padtec)"

# ── 3. Matar keepalives antigos ──────────────────────────────────────────────
echo ""
echo "[2/5] Matando keepalive(s) antigo(s)..."
OLD_PIDS=$(pgrep -f "keepalive_cross.py" 2>/dev/null)
if [ -n "$OLD_PIDS" ]; then
    echo "  Matando PIDs: $OLD_PIDS"
    kill $OLD_PIDS 2>/dev/null
    sleep 1
else
    echo "  Nenhum keepalive em execução."
fi

# ── 4. Aplicar cross-connects via PUT ────────────────────────────────────────
echo ""
echo "[3/5] Aplicando cross-connects no OXC2 (PUT)..."
HTTP_CODE=$(curl -sS -o /dev/null -w "%{http_code}" \
    -X PUT \
    -u admin:root \
    -H "Accept: application/yang-data+json" \
    -H "Content-Type: application/yang-data+json" \
    -d '{"optical-switch:cross-connects":{"pair":[{"ingress":1,"egress":13},{"ingress":5,"egress":9}]}}' \
    "http://172.17.36.22:8008/api/data/optical-switch:cross-connects")
if [[ "$HTTP_CODE" == "200" || "$HTTP_CODE" == "201" || "$HTTP_CODE" == "204" ]]; then
    echo "  ✓ PUT HTTP $HTTP_CODE — cross-connects aplicados (1→13, 5→9)"
else
    echo "  ✗ PUT HTTP $HTTP_CODE — ERRO! OXC2 inacessível?"
fi

# ── 5. Iniciar keepalive ──────────────────────────────────────────────────────
echo ""
echo "[4/5] Iniciando keepalive_cross.py em background..."
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
if [ -f "$SCRIPT_DIR/keepalive_cross.py" ]; then
    nohup python3 "$SCRIPT_DIR/keepalive_cross.py" > /tmp/keepalive_cross.log 2>&1 &
    KPID=$!
    sleep 2
    if kill -0 $KPID 2>/dev/null; then
        echo "  ✓ keepalive rodando (PID $KPID)"
        echo "  Log: /tmp/keepalive_cross.log"
    else
        echo "  ✗ keepalive morreu imediatamente! Verifique: /tmp/keepalive_cross.log"
        tail -5 /tmp/keepalive_cross.log
    fi
else
    echo "  ✗ tools/keepalive_cross.py não encontrado!"
fi

# ── 6. Status final ───────────────────────────────────────────────────────────
echo ""
echo "[5/5] Verificando estado final..."
sleep 3

# Cross-connects
CROSS=$(curl -sS -u admin:root \
    -H "Accept: application/yang-data+json" \
    "http://172.17.36.22:8008/api/data/optical-switch:cross-connects" 2>/dev/null)
if echo "$CROSS" | python3 -c "import sys,json; d=json.load(sys.stdin); pairs=d.get('optical-switch:cross-connects',d).get('pair',[]); [print(f'  ✓ ingress {p[\"ingress\"]} → egress {p[\"egress\"]}') for p in pairs]" 2>/dev/null; then
    :
else
    echo "  ✗ Não foi possível ler cross-connects: $CROSS"
fi

# ONOS devices
echo ""
echo "  Dispositivos ONOS:"
curl -sS -u "$AUTH" "$ONOS/onos/v1/devices" 2>/dev/null | python3 -c "
import sys, json
try:
    d = json.load(sys.stdin)
    for dev in d.get('devices', []):
        did = dev.get('id','?')
        drv = dev.get('driver','?')
        avail = '✓' if dev.get('available') else '✗'
        print(f'    {avail} {did}  [{drv}]')
except Exception as e:
    print(f'    Erro: {e}')
" 2>/dev/null

echo ""
echo "================================================================"
echo "  Pronto! Para monitorar continuamente:"
echo "    python3 tools/status_lab.py --watch"
echo "  Para ver log do keepalive:"
echo "    tail -f /tmp/keepalive_cross.log"
echo "================================================================"
