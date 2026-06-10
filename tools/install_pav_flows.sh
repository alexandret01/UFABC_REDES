#!/bin/bash
# =============================================================================
# install_pav_flows.sh — Instala flows OpenFlow nos switches PAV1 e PAV2
#                        para conectar DC5/DC6 ao caminho óptico.
#
# Problema: up-mode=false em te-1/1/49 impede que o ONOS veja tráfego dos
# servidores via packet-in. Com flows explícitos, o switch encaminha direto
# sem precisar de packet-in/host discovery.
#
# Flows instalados (prioridade 40000, permanentes):
#   PAV1: porta 49 (DC5)  → porta 51 (T100DCT#2 cliente)
#   PAV1: porta 51 (T100DCT#2 cliente) → porta 49 (DC5)
#   PAV2: porta 49 (DC6)  → porta 50 (T100DCT#27 cliente)
#   PAV2: porta 50 (T100DCT#27 cliente) → porta 49 (DC6)
#
# Uso:
#   bash tools/install_pav_flows.sh
#   bash tools/install_pav_flows.sh --delete   (remove os flows)
# =============================================================================

ONOS="http://localhost:8181"
AUTH="onos:rocks"
PAV1="of:5e3ec454441280b9"
PAV2="of:5e3ec454443294fb"

# Descobere automaticamente quais ports conectam ao caminho óptico (LLDP)
# PAV1:51 ↔ PAV2:50 (confirmado por LLDP discovery)
PAV1_OPT_PORT=51   # porta de PAV1 que vai ao transponder
PAV2_OPT_PORT=50   # porta de PAV2 que vai ao transponder
DC_PORT=49         # porta onde DC5/DC6 se conectam

MODE="${1:-install}"

delete_flows() {
    local dev=$1
    echo "  Removendo flows em $dev ..."
    IDS=$(curl -s -u "$AUTH" "$ONOS/onos/v1/flows/$dev" | \
        python3 -c "
import sys,json
flows = json.load(sys.stdin).get('flows',[])
ids = [f['id'] for f in flows if f.get('priority',0) == 40000]
print(' '.join(ids))
" 2>/dev/null)
    for id in $IDS; do
        CODE=$(curl -sS -o /dev/null -w "%{http_code}" -X DELETE -u "$AUTH" \
            "$ONOS/onos/v1/flows/$dev/$id")
        echo "    DELETE $id → HTTP $CODE"
    done
}

install_flow() {
    local dev=$1
    local in_port=$2
    local out_port=$3
    local desc=$4

    BODY=$(cat <<JSON
{
  "priority": 40000,
  "timeout": 0,
  "isPermanent": true,
  "deviceId": "$dev",
  "treatment": {
    "instructions": [{"type": "OUTPUT", "port": "$out_port"}]
  },
  "selector": {
    "criteria": [{"type": "IN_PORT", "port": "$in_port"}]
  }
}
JSON
)
    CODE=$(curl -sS -o /dev/null -w "%{http_code}" \
        -X POST \
        -H "content-type:application/json" \
        -u "$AUTH" \
        "$ONOS/onos/v1/flows/$dev" \
        -d "$BODY")
    [ "$CODE" = "201" ] && echo "  ✓ $desc  (in=$in_port → out=$out_port)  HTTP $CODE" \
                        || echo "  ✗ $desc  HTTP $CODE"
}

echo "================================================================"
echo "  install_pav_flows.sh — Flows OpenFlow PAV1/PAV2"
echo "  PAV1: $PAV1"
echo "  PAV2: $PAV2"
echo "================================================================"

if [ "$MODE" = "--delete" ]; then
    echo ""
    echo "Removendo flows de prioridade 40000..."
    delete_flows "$PAV1"
    delete_flows "$PAV2"
    echo "Pronto."
    exit 0
fi

# Desativa fwd antes de instalar flows (fwd instala flows reativos que conflitam)
echo ""
echo "[Pre] Desativando org.onosproject.fwd (reactive forwarding)..."
FWD_CODE=$(curl -sS -o /dev/null -w "%{http_code}" -X DELETE -u "$AUTH" \
     "$ONOS/onos/v1/applications/org.onosproject.fwd/active" 2>/dev/null)
[ "$FWD_CODE" = "204" ] || [ "$FWD_CODE" = "200" ] || [ "$FWD_CODE" = "404" ] \
    && echo "  ✓ fwd inativo (HTTP $FWD_CODE)" \
    || echo "  ⚠ fwd: HTTP $FWD_CODE"

echo ""
echo "[PAV1] Instalando flows bridge porta $DC_PORT ↔ porta $PAV1_OPT_PORT ..."
install_flow "$PAV1" "$DC_PORT"       "$PAV1_OPT_PORT" "PAV1: DC5 → T100DCT#2"
install_flow "$PAV1" "$PAV1_OPT_PORT" "$DC_PORT"       "PAV1: T100DCT#2 → DC5"

echo ""
echo "[PAV2] Instalando flows bridge porta $DC_PORT ↔ porta $PAV2_OPT_PORT ..."
install_flow "$PAV2" "$DC_PORT"       "$PAV2_OPT_PORT" "PAV2: DC6 → T100DCT#27"
install_flow "$PAV2" "$PAV2_OPT_PORT" "$DC_PORT"       "PAV2: T100DCT#27 → DC6"

echo ""
echo "[Verificação] Flows instalados nos PAVs:"
for dev in "$PAV1" "$PAV2"; do
    curl -s -u "$AUTH" "$ONOS/onos/v1/flows/$dev" | python3 -c "
import sys,json
flows = json.load(sys.stdin).get('flows',[])
hi = [f for f in flows if f.get('priority',0) == 40000]
for f in hi:
    sel = [c.get('port','?') for c in f.get('selector',{}).get('criteria',[]) if c.get('type')=='IN_PORT']
    out = [i.get('port','?') for i in f.get('treatment',{}).get('instructions',[]) if i.get('type')=='OUTPUT']
    print(f'  {f[\"deviceId\"][:20]}  in={sel}  out={out}  state={f.get(\"state\",\"?\")}')
" 2>/dev/null
done

echo ""
echo "================================================================"
echo "  Flows instalados. DC5 e DC6 agora têm caminho L2 através dos"
echo "  transponders. Próximo passo: configurar roteamento L3."
echo ""
echo "  Se DC5 e DC6 estiverem na mesma /24 ou com rotas configuradas:"
echo "    ping -I enp1s0 10.0.1.2   (a partir de DC5)"
echo ""
echo "  Para remover os flows: bash tools/install_pav_flows.sh --delete"
echo "================================================================"
