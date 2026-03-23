#!/bin/bash
# =========================================================================
# Script Unificado de Setup do Laboratório ONOS - UFABC
# Agrupa as funções de: start_onos.sh, polatis.sh, polatis_netconf.sh e etc.
# =========================================================================

echo "========================================="
echo "  Iniciando Setup do Laboratório ONOS"
echo "========================================="

# 1. Iniciar o ONOS
echo "[1/4] Iniciando o ONOS com Bazel..."
cd /home/sdn/onos27/onos
sudo bazel run --host_force_python=PY2 onos-local -- clean &
ONOS_PID=$!

echo "Aguardando o ONOS subir (30 segundos)..."
sleep 30

# 2. Carregar configurações da Rede via REST API (OXC Polatis)
echo "[2/4] Enviando configurações Netconf para os Switches Polatis..."

# Envia a cfg do Polatis 1 (antigo polatis_netconf.sh)
if [ -f "tools/netconf-cfg1.json" ]; then
    curl -sS -X POST -H "content-type:application/json" \
         http://localhost:8181/onos/v1/network/configuration \
         -d @tools/netconf-cfg1.json --user onos:rocks
    echo -e "\n  -> Polatis 1 configurado."
else
    echo "  [AVISO] Arquivo tools/netconf-cfg1.json não encontrado!"
fi

# Envia a cfg do Polatis 2 (antigo polatis_netconf2.sh)
if [ -f "tools/netconf-cfg2.json" ]; then
    curl -sS -X POST -H "content-type:application/json" \
         http://localhost:8181/onos/v1/network/configuration \
         -d @tools/netconf-cfg2.json --user onos:rocks
    echo -e "\n  -> Polatis 2 configurado."
else
    echo "  [AVISO] Arquivo tools/netconf-cfg2.json não encontrado!"
fi

# 3. Carregar o Driver da Padtec
echo "[3/4] Instalando Driver ONOS Padtec..."
cd /home/sdn/onos27/drivers/padtec/UFABC_REDES
onos-app localhost install! target/onos-drivers-padtec-2.7.0.oar
echo "  -> Driver Padtec instalado."

# 4. Criar Cross-Connects (antigo add_cross_rest.py)
echo "[4/4] Aplicando conexões cross-connect..."
if [ -f "tools/add_cross_rest.py" ]; then
    sudo python3 tools/add_cross_rest.py
    echo "  -> Cross-connects criados."
else
    echo "  [AVISO] Arquivo tools/add_cross_rest.py não encontrado!"
fi

echo "========================================="
echo " Setup concluído com sucesso!"
echo " Acesse a interface web: http://172.17.36.231:8181/onos/ui"
echo " (Para parar o ONOS, encerre este script com Ctrl+C)"
echo "========================================="

# Mantém o script rodando enquanto o ONOS estiver ativo
wait $ONOS_PID
