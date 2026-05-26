#!/bin/bash
# =============================================================================
# pav_setup.sh — Configura as portas dos switches Pica8 PAV1 e PAV2.
#
# Problema identificado:
#   te-1/1/49 (onde conecta DC5/DC6) tem up-mode="false" em ambos os switches.
#   Com up-mode=false, o ONOS não vê tráfego nessa porta → hosts não são
#   descobertos → fwd app não instala flows → ping não funciona.
#
# Este script:
#   1. Habilita up-mode=true em te-1/1/49 (porta dos servidores)
#   2. Garante up-mode=true em te-1/1/50 e te-1/1/51 (portas dos transponders)
#   3. Verifica o estado final
#
# Uso:
#   bash tools/pav_setup.sh
#   bash tools/pav_setup.sh pav1   (só PAV1)
#   bash tools/pav_setup.sh pav2   (só PAV2)
# =============================================================================

PAV1="172.17.36.210"
PAV2="172.17.36.211"
PASSWORDS=("admin" "pica8" "waldman" "root" "Pica8@1234" "user")
TARGET="${1:-both}"

SSH_OPTS="-o StrictHostKeyChecking=no -o ConnectTimeout=5 -o PasswordAuthentication=yes"

# Comandos XorPlus (Pica8 CLI) para habilitar as portas
CMDS='configure
set interface te-1/1/49 up-mode true
set interface te-1/1/50 up-mode true
set interface te-1/1/51 up-mode true
commit
run show interface te-1/1/49
run show interface te-1/1/50
exit'

try_ssh() {
    local host=$1
    local name=$2
    echo ""
    echo "--- $name ($host) ---"

    # Tenta com sshpass se disponível
    if command -v sshpass &>/dev/null; then
        for pwd in "${PASSWORDS[@]}"; do
            echo -n "  Tentando admin@$host senha='$pwd'... "
            OUT=$(timeout 15 sshpass -p "$pwd" ssh $SSH_OPTS admin@$host "$CMDS" 2>&1)
            if [ $? -eq 0 ]; then
                echo "CONECTOU!"
                echo "$OUT"
                return 0
            else
                echo "falhou"
            fi
        done
    else
        echo "  [sshpass não disponível — tentando sem senha]"
        echo "  Instale com: sudo apt-get install sshpass"
        echo ""
        echo "  Comandos para executar manualmente em $name:"
        echo "    ssh admin@$host"
        echo "    Depois cole:"
        echo "$CMDS"
    fi

    echo "  FALHA: nenhuma senha funcionou para $name"
    echo "  Acesse fisicamente ou via console serial."
    return 1
}

echo "============================================================"
echo "  pav_setup.sh — Configuração de portas PAV1/PAV2"
echo "  Objetivo: up-mode=true em te-1/1/49, te-1/1/50, te-1/1/51"
echo "============================================================"

if [ "$TARGET" = "both" ] || [ "$TARGET" = "pav1" ]; then
    try_ssh "$PAV1" "PAV1"
fi

if [ "$TARGET" = "both" ] || [ "$TARGET" = "pav2" ]; then
    try_ssh "$PAV2" "PAV2"
fi

echo ""
echo "============================================================"
echo "  Verifique o ONOS após ~30s:"
echo "    curl -s -u onos:rocks http://localhost:8181/onos/v1/hosts"
echo "  Hosts descobertos = DC5/DC6 enviaram tráfego pelo switch."
echo "============================================================"
