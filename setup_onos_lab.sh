#!/bin/bash
# =========================================================================
# Script Unificado de Setup do Laboratório ONOS - UFABC
# =========================================================================

PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

echo "========================================="
echo "  Iniciando Setup do Laboratório ONOS"
echo "========================================="

# 0. Iniciar o Agente Padtec SIMPLES em background
echo "[0/4] Iniciando o Agente Padtec SIMPLES em background..."
cd "$PROJECT_DIR/tools"
chmod +x start_agent.sh
nohup ./start_agent.sh > padtec_agent.log 2>&1 &
AGENT_PID=$!
echo "  -> Agente Padtec iniciado na porta 10151 (PID: $AGENT_PID)."
cd "$PROJECT_DIR"

# 1. Iniciar o ONOS
echo "[1/4] Iniciando o ONOS com Bazel..."
cd /home/sdn/onos27/onos
sudo bazel run --host_force_python=PY2 onos-local -- clean &
ONOS_PID=$!

echo "Aguardando o ONOS subir completamente (90 segundos)..."
sleep 90

# 2. Ativar aplicações base do ONOS
echo "[2/4] Ativando aplicações base do ONOS..."
ONOS_IP="127.0.0.1"
AUTH="onos:rocks"
APPS=("org.onosproject.openflow" "org.onosproject.netconf" "org.onosproject.drivers.optical")
for app in "${APPS[@]}"; do
  curl -sS -X POST -u "$AUTH" "http://$ONOS_IP:8181/onos/v1/applications/$app/active"
done
echo ""

# 3. Instalar e Configurar o Driver Padtec
echo "[3/4] Instalando e Configurando o Driver Padtec..."
curl -sS -X POST -H "content-type:application/octet-stream" \
     "http://$ONOS_IP:8181/onos/v1/applications?activate=true" \
     --data-binary @target/onos-drivers-padtec-2.7.0.oar --user "$AUTH"
echo -e "\n  -> Driver Padtec instalado via REST."

if [ -f "padtec-netcfg.json" ]; then
    curl -sS -X POST -H "content-type:application/json" \
         "http://$ONOS_IP:8181/onos/v1/network/configuration" \
         -d @padtec-netcfg.json --user "$AUTH"
    echo -e "\n  -> Dispositivo Padtec configurado."
fi

echo "========================================="
echo " Setup concluído com sucesso!"
echo " Acesse a interface web: http://172.17.36.231:8181/onos/ui"
echo " (Para parar o ONOS e o Agente, encerre este script com Ctrl+C)"
echo "========================================="

trap "kill $AGENT_PID; kill $ONOS_PID" EXIT
wait $ONOS_PID
