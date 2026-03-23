#!/bin/bash
# =========================================================================
# Script Unificado de Setup do Laboratório ONOS - UFABC
# Agrupa as funções de: start_onos.sh, polatis.sh, polatis_netconf.sh e etc.
# =========================================================================

# Descobre o diretório absoluto de onde o script está sendo chamado
PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

echo "========================================="
echo "  Iniciando Setup do Laboratório ONOS"
echo "========================================="

# 1. Iniciar o ONOS
echo "[1/4] Iniciando o ONOS com Bazel..."
cd /home/sdn/onos27/onos
sudo bazel run --host_force_python=PY2 onos-local -- clean &
ONOS_PID=$!

echo "Aguardando o ONOS subir completamente (90 segundos)..."
sleep 90

# Volta para o diretório do projeto
cd "$PROJECT_DIR"

# 2. Carregar configurações da Rede via REST API (OXC Polatis)
echo "[2/4] Enviando configurações Netconf para os Switches Polatis..."

if [ -f "tools/netconf-cfg1.json" ]; then
    curl -sS -X POST -H "content-type:application/json" \
         http://localhost:8181/onos/v1/network/configuration \
         -d @tools/netconf-cfg1.json --user onos:rocks
    echo -e "\n  -> Polatis 1 configurado."
else
    echo "  [AVISO] Arquivo tools/netconf-cfg1.json não encontrado no diretório: $PROJECT_DIR/tools"
fi

if [ -f "tools/netconf-cfg2.json" ]; then
    curl -sS -X POST -H "content-type:application/json" \
         http://localhost:8181/onos/v1/network/configuration \
         -d @tools/netconf-cfg2.json --user onos:rocks
    echo -e "\n  -> Polatis 2 configurado."
else
    echo "  [AVISO] Arquivo tools/netconf-cfg2.json não encontrado no diretório: $PROJECT_DIR/tools"
fi

# 3. Carregar o Driver da Padtec
echo "[3/4] Instalando Driver ONOS Padtec..."
# Envia o driver usando cURL para evitar problemas com o comando onos-app não estar no PATH
curl -sS -X POST -H "content-type:application/octet-stream" \
     http://localhost:8181/onos/v1/applications?activate=true \
     --data-binary @target/onos-drivers-padtec-2.7.0.oar --user onos:rocks
echo -e "\n  -> Driver Padtec instalado via REST."

# Envia também as configurações de rede do Padtec para ele aparecer na topologia!
if [ -f "padtec-netcfg.json" ]; then
    echo "Enviando configurações de rede do Padtec..."
    curl -sS -X POST -H "content-type:application/json" \
         http://localhost:8181/onos/v1/network/configuration \
         -d @padtec-netcfg.json --user onos:rocks
    echo -e "\n  -> Padtec configurado."
else
    echo "  [AVISO] Arquivo padtec-netcfg.json não encontrado no diretório: $PROJECT_DIR"
fi

# 4. Criar Cross-Connects (antigo add_cross_rest.py)
echo "[4/4] Aplicando conexões cross-connect..."
if [ -f "tools/add_cross_rest.py" ]; then
    python3 tools/add_cross_rest.py
    echo "  -> Cross-connects criados."
else
    echo "  [AVISO] Arquivo tools/add_cross_rest.py não encontrado no diretório: $PROJECT_DIR/tools"
fi

echo "========================================="
echo " Setup concluído com sucesso!"
echo " Acesse a interface web: http://172.17.36.231:8181/onos/ui"
echo " (Para parar o ONOS, encerre este script com Ctrl+C)"
echo "========================================="

# Mantém o script rodando enquanto o ONOS estiver ativo
wait $ONOS_PID
