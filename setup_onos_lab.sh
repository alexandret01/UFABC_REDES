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
  "org.onosproject.fwd"
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
)

for app in "${APPS[@]}"; do
  curl -sS -X POST -u "$AUTH" "http://$ONOS_IP:8181/onos/v1/applications/$app/active" > /dev/null
done

if [ -f "tools/netconf-cfg1.json" ]; then
    curl -sS -X POST -H "content-type:application/json" \
         "http://$ONOS_IP:8181/onos/v1/network/configuration" \
         -d @tools/netconf-cfg1.json --user "$AUTH" > /dev/null
fi
if [ -f "tools/netconf-cfg2.json" ]; then
    curl -sS -X POST -H "content-type:application/json" \
         "http://$ONOS_IP:8181/onos/v1/network/configuration" \
         -d @tools/netconf-cfg2.json --user "$AUTH" > /dev/null
fi

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

# Criar Cross-Connects
if [ -f "tools/add_cross_rest.py" ]; then
    python3 tools/add_cross_rest.py > /dev/null
    echo "  -> Cross-connects criados."
fi

echo "========================================="
echo " Setup concluído com sucesso!"
echo " "
echo " O ONOS já registrou o dispositivo Padtec."
echo " O PadtecDeviceProvider aguardará 10s e fará uma requisição TCP"
echo " ao PadtecAgentServer na porta 10151 para ler o JSON gerado."
echo " "
echo " Acesse a interface web: http://172.17.36.231:8181/onos/ui"
echo " (Para abortar tudo, dê Ctrl+C)"
echo "========================================="

trap "kill $AGENT_PID; kill $ONOS_PID" EXIT
wait $ONOS_PID
