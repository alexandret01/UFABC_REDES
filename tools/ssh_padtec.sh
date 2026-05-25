#!/bin/bash
# =============================================================================
# ssh_padtec.sh — Conecta ao supervisor Padtec T100DCT (172.17.36.50)
#
# O servidor Padtec usa OpenSSH antigo com RSA-1024, rejeitado por clientes
# modernos. Este script tenta múltiplas combinações de flags para contornar.
#
# Uso:
#   bash tools/ssh_padtec.sh              # tentativas automáticas
#   bash tools/ssh_padtec.sh shell        # abre shell interativo (melhor opção)
#   bash tools/ssh_padtec.sh cmd "show channel"   # executa um comando
# =============================================================================

HOST="172.17.36.50"
USER="admin"
PASSWORDS=("admin" "root" "padtec" "supervisor" "padtec123" "T100DCT" "Padtec@123")

MODE="${1:-auto}"
CMD="${2:-}"

# Flags de compatibilidade para servidores SSH antigos
COMPAT_FLAGS=(
    "-o StrictHostKeyChecking=no"
    "-o UserKnownHostsFile=/dev/null"
    "-o HostKeyAlgorithms=+ssh-rsa,ssh-dss"
    "-o KexAlgorithms=+diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group1-sha1"
    "-o Ciphers=+aes128-cbc,aes256-cbc,3des-cbc,blowfish-cbc"
    "-o MACs=+hmac-sha1,hmac-md5"
    "-o PubkeyAuthentication=no"
    "-o PasswordAuthentication=yes"
    "-o ConnectTimeout=10"
    "-o LogLevel=ERROR"
)

SSH_BASE="ssh ${COMPAT_FLAGS[*]} -p 22 ${USER}@${HOST}"

if [ "$MODE" = "shell" ]; then
    echo "Abrindo shell interativo no Padtec (172.17.36.50)..."
    echo "Senhas a tentar: ${PASSWORDS[*]}"
    echo ""
    exec $SSH_BASE
fi

if [ "$MODE" = "cmd" ] && [ -n "$CMD" ]; then
    echo "Executando: $CMD"
    for pwd in "${PASSWORDS[@]}"; do
        if command -v sshpass &>/dev/null; then
            OUT=$(sshpass -p "$pwd" $SSH_BASE "$CMD" 2>&1)
            if [ $? -eq 0 ]; then
                echo "OK (senha=$pwd):"
                echo "$OUT"
                exit 0
            fi
        else
            # Sem sshpass — tenta apenas uma vez (vai pedir senha interativamente)
            $SSH_BASE "$CMD"
            exit $?
        fi
    done
    echo "Todas as senhas falharam."
    exit 1
fi

# ── Modo automático: diagnóstico completo ────────────────────────────────────
echo "================================================================"
echo "  ssh_padtec.sh — Diagnóstico de conectividade"
echo "  Alvo: $USER@$HOST"
echo "================================================================"
echo ""

# 1. Teste de porta
echo "[1] Testando porta 22..."
if timeout 5 bash -c "cat < /dev/null > /dev/tcp/$HOST/22" 2>/dev/null; then
    echo "  ✓ Porta 22 está aberta"
else
    echo "  ✗ Porta 22 inacessível!"
    exit 1
fi

# 2. Banner SSH
echo ""
echo "[2] Banner SSH:"
timeout 5 bash -c "echo '' | nc $HOST 22 2>/dev/null | head -1" 2>/dev/null || echo "  (sem resposta)"

# 3. ssh-keyscan
echo ""
echo "[3] Chave do host (ssh-keyscan):"
ssh-keyscan -t rsa,dss,ecdsa,ed25519 -p 22 $HOST 2>/dev/null | head -5 || echo "  (falhou)"

# 4. Tentativa de conexão sem sshpass (verbose para diagnóstico)
echo ""
echo "[4] Teste de conexão SSH (verbose):"
timeout 10 ssh \
    -o StrictHostKeyChecking=no \
    -o UserKnownHostsFile=/dev/null \
    -o HostKeyAlgorithms=+ssh-rsa,ssh-dss \
    -o KexAlgorithms=+diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group1-sha1 \
    -o Ciphers=+aes128-cbc,aes256-cbc,3des-cbc \
    -o PubkeyAuthentication=no \
    -o PasswordAuthentication=yes \
    -o ConnectTimeout=8 \
    -v \
    -p 22 \
    ${USER}@${HOST} \
    exit 2>&1 | grep -E "^(debug1: SSH|debug1: kex|debug1: Host|debug1: Auth|Authenticated|Permission|Connection|ssh_exchange|debug1: Remote)" | head -20

# 5. Tentativa com sshpass
echo ""
echo "[5] Tentativa com sshpass (se disponível):"
if ! command -v sshpass &>/dev/null; then
    echo "  sshpass não instalado. Instale com: sudo apt-get install sshpass"
    echo ""
    echo "  Após instalar, tente:"
    for pwd in "${PASSWORDS[@]}"; do
        echo "    sshpass -p '$pwd' $SSH_BASE 'show version || echo ok'"
    done
else
    for pwd in "${PASSWORDS[@]}"; do
        echo -n "  Tentando senha '$pwd'... "
        OUT=$(timeout 12 sshpass -p "$pwd" $SSH_BASE "show version 2>/dev/null || show channel 2>/dev/null || whoami 2>/dev/null || exit 0" 2>&1)
        RC=$?
        if [ $RC -eq 0 ]; then
            echo "✓ CONECTOU!"
            echo "  Saída: $OUT"
            echo ""
            echo "  Para abrir shell interativo:"
            echo "    sshpass -p '$pwd' $SSH_BASE"
            break
        else
            echo "falhou (rc=$RC): $(echo $OUT | head -c 100)"
        fi
    done
fi

echo ""
echo "================================================================"
echo "  Se SSH ainda falhar, tente via cliente PuTTY/MobaXterm ou"
echo "  acesso físico ao painel do transponder."
echo "================================================================"
