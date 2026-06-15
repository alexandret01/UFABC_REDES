#!/bin/bash
# =============================================================================
# start_lab.sh — Script central do Laboratório Óptico UFABC
#
# Sobe em sequência:
#   1. Agente Padtec TCP (Java 18, :10151)
#   2. ONOS via Bazel (limpo ou reutilizando instância)
#   3. Apps ONOS obrigatórias + driver Padtec
#   4. Configuração de rede (netcfg Padtec, links topologia)
#   5. Keepalive OXC2 (cross-connects via PUT a cada 60s)
#   6. Dashboard OpticalLab Monitor (porta 9191)
#
# Uso:
#   sudo ./start_lab.sh              # subida completa
#   sudo ./start_lab.sh --skip-onos  # pula reinício do ONOS (já está rodando)
#   sudo ./start_lab.sh --dashboard-only  # só instala/reinicia o dashboard
# =============================================================================

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ONOS_DIR="/home/sdn/onos27/onos"
ONOS_IP="127.0.0.1"
AUTH="onos:rocks"
ONOS_URL="http://$ONOS_IP:8181"
JAVA18="/usr/lib/jvm/jdk-18.0.2.1/bin"
LOG_DIR="/tmp/lab-logs"
OPTICALLAB_OAR="$PROJECT_DIR/opticallab-app/target/onos-app-opticallab-1.0.0.oar"
PADTEC_OAR="$PROJECT_DIR/target/onos-drivers-padtec-2.7.0.oar"

SKIP_ONOS=false
DASHBOARD_ONLY=false
for arg in "$@"; do
    [[ "$arg" == "--skip-onos" ]]      && SKIP_ONOS=true
    [[ "$arg" == "--dashboard-only" ]] && DASHBOARD_ONLY=true
done

mkdir -p "$LOG_DIR"

# ── Helpers ──────────────────────────────────────────────────────────────────

log()  { echo "[$(date '+%H:%M:%S')] $*"; }
ok()   { echo "[$(date '+%H:%M:%S')] ✓ $*"; }
warn() { echo "[$(date '+%H:%M:%S')] ⚠ $*"; }
fail() { echo "[$(date '+%H:%M:%S')] ✗ $*" >&2; exit 1; }

onos_post() {
    curl -sS -X POST -u "$AUTH" "$ONOS_URL$1" "${@:2}" > /dev/null 2>&1
}
onos_delete() {
    curl -sS -X DELETE -u "$AUTH" "$ONOS_URL$1" > /dev/null 2>&1
}
onos_ready() {
    curl -sf -u "$AUTH" "$ONOS_URL/onos/v1/devices" > /dev/null 2>&1
}
app_active() {
    local state
    state=$(curl -sf -u "$AUTH" "$ONOS_URL/onos/v1/applications/$1" 2>/dev/null \
            | python3 -c "import sys,json; print(json.load(sys.stdin).get('state',''))" 2>/dev/null || true)
    [[ "$state" == "ACTIVE" ]]
}

# ── FASE 1: Agente Padtec ────────────────────────────────────────────────────

start_padtec_agent() {
    log "FASE 1 — Agente Padtec TCP (:10151)"

    # Mata instância anterior se existir
    local old_pid
    old_pid=$(pgrep -f "PadtecMonitorJSON3" 2>/dev/null | head -1 || true)
    if [[ -n "$old_pid" ]]; then
        warn "Agente já rodando (PID $old_pid) — encerrando..."
        kill "$old_pid" 2>/dev/null || true
        sleep 2
    fi

    local agent_dir="$PROJECT_DIR/Outros/TailEndController"
    if [[ ! -d "$agent_dir" ]]; then
        warn "Pasta TailEndController não encontrada — agente não iniciado."
        return
    fi

    cd "$agent_dir"
    log "  Compilando PadtecMonitorJSON3..."
    "$JAVA18/javac" -cp "./lib/*:." PadtecMonitorJSON3.java PadtecAgentServer.java \
        > "$LOG_DIR/agent-compile.log" 2>&1 \
        || { warn "Falha na compilação do agente (ver $LOG_DIR/agent-compile.log)"; cd "$PROJECT_DIR"; return; }

    nohup "$JAVA18/java" \
        -Dorg.apache.logging.log4j.level=INFO \
        -Djava.library.path=./lib/ \
        -cp "./lib/*:." \
        PadtecMonitorJSON3 \
        > "$LOG_DIR/padtec-agent.log" 2>&1 &
    AGENT_PID=$!
    ok "Agente Padtec iniciado (PID $AGENT_PID) — log: $LOG_DIR/padtec-agent.log"
    cd "$PROJECT_DIR"
    sleep 3
}

# ── FASE 2: ONOS ─────────────────────────────────────────────────────────────

start_onos() {
    log "FASE 2 — ONOS via Bazel (pode demorar ~90s)"

    if [[ ! -d "$ONOS_DIR" ]]; then
        fail "Diretório ONOS não encontrado: $ONOS_DIR"
    fi

    cd "$ONOS_DIR"
    sudo bazel run --host_force_python=PY2 onos-local -- clean \
        > "$LOG_DIR/onos.log" 2>&1 &
    ONOS_PID=$!
    ok "ONOS iniciado (PID $ONOS_PID) — log: $LOG_DIR/onos.log"
    cd "$PROJECT_DIR"

    log "  Aguardando ONOS responder na porta 8181..."
    local waited=0
    until onos_ready || [[ $waited -ge 120 ]]; do
        sleep 5; waited=$((waited + 5))
        echo -n "."
    done
    echo ""
    if ! onos_ready; then
        fail "ONOS não respondeu em 120s. Verifique $LOG_DIR/onos.log"
    fi
    ok "ONOS pronto (${waited}s)"
}

# ── FASE 3: Apps ONOS + Driver Padtec ────────────────────────────────────────

configure_onos() {
    log "FASE 3 — Ativando apps e configurando rede"

    local apps=(
        "org.onosproject.openflow"
        "org.onosproject.proxyarp"
        "org.onosproject.layout"
        "org.onosproject.openflow-base"
        "org.onosproject.openflow-message"
        "org.onosproject.ofagent"
        "org.onosproject.drivers"
        "org.onosproject.optical-model"
        "org.onosproject.optical-rest"
        "org.onosproject.drivers.optical"
        "org.onosproject.netconf"
        "org.onosproject.drivers.polatis.netconf"
        "org.onosproject.drivers.polatis.openflow"
        "org.onosproject.faultmanagement"
        "org.onosproject.linkdiscovery"
        "org.onosproject.netcfglinksprovider"
    )

    for app in "${apps[@]}"; do
        onos_post "/onos/v1/applications/$app/active"
    done
    ok "Apps base ativadas"

    # Desativar reactive forwarding (conflita com flows PAV explícitos)
    onos_delete "/onos/v1/applications/org.onosproject.fwd/active"
    ok "org.onosproject.fwd desativado"

    # OXC1 — hardware com defeito: NÃO registrar no ONOS.
    # O GUI2 quebra (ModelCache) quando recebe eventos de device não-registrado.

    # OXC2 — remove do ONOS para evitar que o driver apague os cross-connects
    onos_delete "/onos/v1/network/configuration/devices/netconf:172.17.36.22:830"
    onos_delete "/onos/v1/devices/netconf:172.17.36.22:830"
    ok "OXC2 removido do ONOS (gerenciado via REST direto)"

    # Driver Padtec
    if [[ ! -f "$PADTEC_OAR" ]]; then
        warn "OAR Padtec não encontrado ($PADTEC_OAR) — compilando..."
        (cd "$PROJECT_DIR" && mvn clean package -DskipTests -q) \
            || fail "Falha ao compilar o driver Padtec"
    fi
    curl -sS -X POST -u "$AUTH" \
        -H "content-type:application/octet-stream" \
        "$ONOS_URL/onos/v1/applications?activate=true" \
        --data-binary @"$PADTEC_OAR" > /dev/null
    ok "Driver Padtec instalado"

    # Netcfg Padtec (registra device padtec:172.17.36.50)
    if [[ -f "padtec-netcfg.json" ]]; then
        onos_post "/onos/v1/network/configuration" \
            -H "content-type:application/json" -d @padtec-netcfg.json
        ok "Device Padtec registrado no netcfg"
    fi

    # Links estáticos de topologia (apaga antes para forçar CONFIG_ADDED)
    if [[ -f "tools/lab-topology.json" ]]; then
        onos_delete "/onos/v1/network/configuration/links"
        sleep 1
        onos_post "/onos/v1/network/configuration" \
            -H "content-type:application/json" -d @tools/lab-topology.json
        ok "Links de topologia injetados (PAV↔Padtec DIRECT + Padtec↔OXC2 OPTICAL)"
    fi
}

# ── FASE 4: Keepalive OXC2 ───────────────────────────────────────────────────

start_keepalive() {
    log "FASE 4 — Keepalive OXC2 cross-connects"

    pkill -f "keepalive_cross.py" 2>/dev/null || true
    sleep 1

    if [[ -f "tools/keepalive_cross.py" ]]; then
        nohup python3 tools/keepalive_cross.py \
            > "$LOG_DIR/keepalive-oxc2.log" 2>&1 &
        KEEPALIVE_PID=$!
        sleep 3  # aguarda primeira aplicação dos cross-connects
        ok "Keepalive OXC2 iniciado (PID $KEEPALIVE_PID) — log: $LOG_DIR/keepalive-oxc2.log"
    else
        warn "keepalive_cross.py não encontrado"
    fi
}

# ── FASE 5: Dashboard OpticalLab (porta 9191) ─────────────────────────────────

start_dashboard() {
    log "FASE 5 — Dashboard OpticalLab Monitor (porta 9191)"

    # Compila se o OAR não existir ou se a fonte for mais recente
    local src_dir="$PROJECT_DIR/opticallab-app/src"
    if [[ ! -f "$OPTICALLAB_OAR" ]] || \
       find "$src_dir" -newer "$OPTICALLAB_OAR" -name "*.java" 2>/dev/null | grep -q .; then
        log "  Compilando opticallab-app..."
        (cd "$PROJECT_DIR/opticallab-app" && mvn clean package -q) \
            > "$LOG_DIR/opticallab-build.log" 2>&1 \
            || { warn "Falha no build do dashboard (ver $LOG_DIR/opticallab-build.log)"; return; }
        ok "opticallab-app compilado"
    fi

    # Remove versão anterior se instalada
    if app_active "br.ufabc.opticallab"; then
        curl -sS -X DELETE -u "$AUTH" \
            "$ONOS_URL/onos/v1/applications/br.ufabc.opticallab" > /dev/null 2>&1 || true
        sleep 2
    fi

    curl -sS -X POST -u "$AUTH" \
        -H "content-type:application/octet-stream" \
        "$ONOS_URL/onos/v1/applications?activate=true" \
        --data-binary @"$OPTICALLAB_OAR" > /dev/null \
        || { warn "Falha ao instalar dashboard no ONOS"; return; }

    # Aguarda porta 9191 subir (até 15s)
    local waited=0
    until ss -tlnp 2>/dev/null | grep -q ":9191" || [[ $waited -ge 15 ]]; do
        sleep 2; waited=$((waited + 2))
    done

    if ss -tlnp 2>/dev/null | grep -q ":9191"; then
        ok "Dashboard ativo em http://172.17.36.231:9191"
    else
        warn "Dashboard instalado mas porta 9191 ainda não abriu (aguarde alguns segundos)"
    fi
}

# ── Main ──────────────────────────────────────────────────────────────────────

cd "$PROJECT_DIR"

echo "============================================================"
echo "   Laboratório Óptico UFABC — Inicialização Central"
echo "============================================================"

AGENT_PID=""
ONOS_PID=""
KEEPALIVE_PID=""

if $DASHBOARD_ONLY; then
    log "Modo: apenas dashboard"
    if ! onos_ready; then
        fail "ONOS não está acessível em $ONOS_URL. Suba o ONOS primeiro."
    fi
    start_dashboard
else
    start_padtec_agent

    if $SKIP_ONOS; then
        log "FASE 2 — ONOS (pulando — usando instância em execução)"
        if ! onos_ready; then
            fail "ONOS não está acessível em $ONOS_URL. Remova --skip-onos."
        fi
        ok "ONOS já está rodando"
    else
        start_onos
    fi

    configure_onos
    start_keepalive
    start_dashboard
fi

echo ""
echo "============================================================"
echo "   ✓ Laboratório pronto!"
echo ""
echo "   ONOS UI   → http://172.17.36.231:8181/onos/ui"
echo "   Dashboard → http://172.17.36.231:9191"
echo ""
echo "   Logs em: $LOG_DIR/"
echo "     padtec-agent.log   — agente TCP Padtec"
echo "     onos.log           — processo ONOS"
echo "     keepalive-oxc2.log — cross-connects OXC2"
echo "     opticallab-build.log — build do dashboard"
echo ""
echo "   Verificações rápidas:"
echo "     bash tools/check_lab.sh"
echo "     bash tools/install_pav_flows.sh"
echo "============================================================"

# Mantém script rodando enquanto ONOS estiver ativo
if [[ -n "$ONOS_PID" ]]; then
    trap "log 'Encerrando...'; kill $AGENT_PID $KEEPALIVE_PID 2>/dev/null; exit 0" INT TERM
    wait "$ONOS_PID"
fi
