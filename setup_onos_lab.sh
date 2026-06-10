#!/bin/bash
# =========================================================================
# Script Unificado de Setup do Laboratório ONOS - Foco em PADTEC
# =========================================================================

PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

echo "========================================="
echo "  Iniciando Setup do Laboratório ONOS"
echo "========================================="

# 0. Parar qualquer processo antigo
echo "[0/4] Limpando processos antigos..."
sleep 2

# 1. Iniciar o Agente Padtec
echo "[1/4] Compilando e Iniciando o Agente Padtec (TCP 10151)..."
if [ -d "$PROJECT_DIR/Outros/TailEndController" ]; then
    cd "$PROJECT_DIR/Outros/TailEndController"
    # Usa o Java 18 padrão da máquina optinet
    /usr/lib/jvm/jdk-18.0.2.1/bin/javac -cp "./lib/*:." PadtecMonitorJSON3.java PadtecAgentServer.java

    if [ $? -eq 0 ]; then
        # Roda o Agente em background com nohup e o classpath correto
        nohup /usr/lib/jvm/jdk-18.0.2.1/bin/java -Dorg.apache.logging.log4j.level=INFO -Djava.library.path=./lib/ -cp "./lib/*:." PadtecMonitorJSON3 > padtec_agent.log 2>&1 &
        AGENT_PID=$!
        echo "  -> Agente Padtec (Original) iniciado. PID: $AGENT_PID."
    else
        echo "  [ERRO] Falha ao compilar PadtecMonitorJSON3. Verifique os imports e a pasta lib/."
    fi
    cd "$PROJECT_DIR"
else
    echo "  [AVISO] Pasta Outros/TailEndController não encontrada. O Agente não será iniciado."
fi

# 2. Iniciar o ONOS
echo "[2/4] Iniciando o ONOS com Bazel (Start Limpo)..."
cd /home/sdn/onos27/onos
sudo bazel run --host_force_python=PY2 onos-local -- clean &
ONOS_PID=$!

echo "Aguardando o ONOS subir completamente (90 segundos)..."
sleep 90
cd "$PROJECT_DIR"

# 3. Ativar aplicações base e Configurar Rede (Polatis e Pica8)
echo "[3/4] Ativando aplicações e configurando a rede..."
ONOS_IP="127.0.0.1"
AUTH="onos:rocks"

APPS=(
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
  # Alarmes Padtec: expõe /onos/v1/alarms e processa AlarmConsumer
  "org.onosproject.faultmanagement"
  # Links estáticos e descoberta
  "org.onosproject.linkdiscovery"
  # Publica links definidos no netcfg (Polatis <-> Pica8) na topologia
  "org.onosproject.netcfglinksprovider"
)

for app in "${APPS[@]}"; do
  curl -sS -X POST -u "$AUTH" "http://$ONOS_IP:8181/onos/v1/applications/$app/active" > /dev/null
done

# Garantir que reactive forwarding (fwd) está DESATIVADO.
# Com fwd ativo, o ONOS intercepta todos os pacotes e instala flows reativos nos
# PAVs, sobrescrevendo / conflitando com os flows explícitos (prioridade 40000)
# que bridgeiam DC5↔DC6 via caminho óptico.
echo "  -> Desativando org.onosproject.fwd (reactive forwarding)..."
curl -sS -X DELETE -u "$AUTH" \
     "http://$ONOS_IP:8181/onos/v1/applications/org.onosproject.fwd/active" > /dev/null 2>&1
echo "  -> fwd desativado. Flows nos PAVs controlados exclusivamente por install_pav_flows.sh."

# OXC1 (172.17.36.21) — com defeito, mas registrado no ONOS (5 dispositivos na topologia)
if [ -f "tools/netconf-cfg1.json" ]; then
    curl -sS -X POST -H "content-type:application/json" \
         "http://$ONOS_IP:8181/onos/v1/network/configuration" \
         -d @tools/netconf-cfg1.json --user "$AUTH" > /dev/null
    echo "  -> OXC1 (172.17.36.21) registrado no ONOS [COM DEFEITO — ficará offline/unreachable]."
fi

# OXC2 (172.17.36.22) — gerenciado APENAS via REST direto (keepalive_cross.py).
# NÃO registrar no ONOS via NETCONF: o driver polatis-netconf limpa os
# cross-connects a cada ~8 min durante a sync periódica.
# Se OXC2 ainda estiver no ONOS de uma execução anterior, remove agora.
echo "  -> OXC2: removendo do ONOS (se existir) para evitar conflito com keepalive..."
curl -sS -X DELETE -u "$AUTH" \
     "http://$ONOS_IP:8181/onos/v1/network/configuration/devices/netconf:172.17.36.22:830" \
     > /dev/null 2>&1
curl -sS -X DELETE -u "$AUTH" \
     "http://$ONOS_IP:8181/onos/v1/devices/netconf:172.17.36.22:830" \
     > /dev/null 2>&1
echo "  -> OXC2 gerenciado via REST direto (keepalive). Cross-connects não serão apagados pelo ONOS."

# 4. Instalar e Configurar o Driver Padtec
echo "[4/4] Instalando e Configurando o Driver Padtec..."

# Instala o seu driver recém-compilado
curl -sS -X POST -H "content-type:application/octet-stream" \
     "http://$ONOS_IP:8181/onos/v1/applications?activate=true" \
     --data-binary @target/onos-drivers-padtec-2.7.0.oar --user "$AUTH"
echo -e "\n  -> Driver Padtec instalado."

# Injeta a configuração do Padtec para o ONOS
if [ -f "padtec-netcfg.json" ]; then
    curl -sS -X POST -H "content-type:application/json" \
         "http://$ONOS_IP:8181/onos/v1/network/configuration" \
         -d @padtec-netcfg.json --user "$AUTH"
    echo -e "  -> Dispositivo Padtec configurado no netcfg."
fi

# Criar Cross-Connects nos Polatis e manter vivos (o OXC2 Polatis apaga após ~30s)
# O keepalive_cross.py re-aplica os pares a cada 20s em background.
if [ -f "tools/keepalive_cross.py" ]; then
    nohup python3 tools/keepalive_cross.py > /tmp/keepalive_cross.log 2>&1 &
    KEEPALIVE_PID=$!
    echo "  -> Cross-connect keepalive iniciado (PID: $KEEPALIVE_PID). Pares: 1→13, 2→11, 3→10, 5→9, 6→15, 7→14."
    echo "     Log: /tmp/keepalive_cross.log"
    sleep 2  # aguarda primeira aplicação
elif [ -f "tools/add_cross_rest.py" ]; then
    python3 tools/add_cross_rest.py > /dev/null
    echo "  -> Cross-connects criados no OXC2 (1→13, 2→11, 3→10, 5→9, 6→15, 7→14). OXC1 fora de uso."
    echo "  [AVISO] Sem keepalive — os pares podem desaparecer em ~30s."
fi

# Injetar links estáticos da topologia do lab (Polatis outputs <-> Pica8 SFP+)
# Nota: apagar antes de reinjetar garante que o netcfglinksprovider receba
# o evento CONFIG_ADDED mesmo em re-execuções (sem delete, re-POST é no-op).
if [ -f "tools/lab-topology.json" ]; then
    curl -sS -X DELETE -u "$AUTH" \
         "http://$ONOS_IP:8181/onos/v1/network/configuration/links" > /dev/null 2>&1
    sleep 1
    curl -sS -X POST -H "content-type:application/json" \
         "http://$ONOS_IP:8181/onos/v1/network/configuration" \
         -d @tools/lab-topology.json --user "$AUTH" > /dev/null
    echo "  -> Links da topologia injetados (PAV↔Padtec DIRECT + Padtec↔OXC2 OPTICAL)."
fi

echo "========================================="
echo " Setup concluído com sucesso!"
echo " "
echo " Processos ativos:"
echo "   - Agente Padtec (TCP 10151)"
echo "   - ONOS (http://172.17.36.231:8181/onos/ui)"
echo "   - Cross-connect keepalive (re-aplica OXC2 a cada 60s via PUT)"
echo " "
echo " Para verificar cross-connects:"
echo "   curl -s -u admin:root http://172.17.36.22:8008/api/data/optical-switch:cross-connects"
echo " Para verificar sinal Padtec:"
echo "   python3 tools/coletar_padtec_onos.py"
echo " Log keepalive: /tmp/keepalive_cross.log"
echo " "
echo " (Para abortar tudo, dê Ctrl+C)"
echo "========================================="

trap "kill $AGENT_PID $KEEPALIVE_PID $ONOS_PID 2>/dev/null" EXIT
wait $ONOS_PID
