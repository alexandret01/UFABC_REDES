#!/bin/bash
# =============================================================================
# setup_dc_hosts.sh — Configura as interfaces de rede dos servidores DC5 e DC6
#                     para o caminho óptico PAV1↔transponders↔PAV2.
#
# Topologia:
#   DC5 (172.17.36.208): enp1s0 → 10.0.0.2/24  →  PAV1 porta 49
#   DC6 (172.17.36.214): enp1s0 → 10.0.1.2/24  →  PAV2 porta 49
#
# O caminho óptico (PAV1:51↔PAV2:50 via transponders) cria uma ponte L2
# transparente. Ambas as interfaces ficam no mesmo domínio L2, portanto:
#   - DC5 faz `ip route add 10.0.1.0/24 dev enp1s0` para alcançar DC6
#   - DC6 faz `ip route add 10.0.0.0/24 dev enp1s0` para alcançar DC5
#   - ARP funciona porque o switch entrega broadcasts para a outra ponta.
#
# Pré-requisito: flows OpenFlow instalados nos PAVs (install_pav_flows.sh)
#
# Uso:
#   bash tools/setup_dc_hosts.sh            (configura DC5 e DC6)
#   bash tools/setup_dc_hosts.sh --status   (só verifica, não configura)
#   bash tools/setup_dc_hosts.sh --test     (configura + testa ping)
# =============================================================================

DC5_MGMT="172.17.36.208"
DC6_MGMT="172.17.36.214"
DC5_OPT_IP="10.0.0.2/24"
DC6_OPT_IP="10.0.1.2/24"
DC5_OPT_NET="10.0.0.0/24"
DC6_OPT_NET="10.0.1.0/24"
DC5_OPT_ADDR="10.0.0.2"
DC6_OPT_ADDR="10.0.1.2"
OPT_IFACE="enp1s0"
SSH_USER="jaquison"
SSH_PASS="jaquison123"
SSH_OPTS="-o StrictHostKeyChecking=no -o ConnectTimeout=10"

MODE="${1:-configure}"

ssh_cmd() {
    local host=$1
    shift
    if command -v sshpass &>/dev/null; then
        sshpass -p "$SSH_PASS" ssh $SSH_OPTS "$SSH_USER@$host" "$@"
    else
        echo "[AVISO] sshpass não encontrado. Execute manualmente em $host:"
        echo "  $@"
        return 1
    fi
}

check_dc() {
    local host=$1
    local name=$2
    local expected_ip=$3
    local peer_net=$4

    echo ""
    echo "── $name ($host) ──────────────────────────────────────────"

    # Interface IP
    IP_OUT=$(ssh_cmd "$host" "ip addr show $OPT_IFACE 2>/dev/null" 2>/dev/null)
    if echo "$IP_OUT" | grep -q "$expected_ip"; then
        echo "  ✓ $OPT_IFACE: $expected_ip configurado"
    else
        CURRENT=$(echo "$IP_OUT" | grep "inet " | awk '{print $2}')
        echo "  ✗ $OPT_IFACE: esperado $expected_ip, atual: ${CURRENT:-'sem IP'}"
    fi

    # Rota para peer
    ROUTE=$(ssh_cmd "$host" "ip route show dev $OPT_IFACE 2>/dev/null" 2>/dev/null)
    if echo "$ROUTE" | grep -q "$peer_net"; then
        echo "  ✓ Rota $peer_net dev $OPT_IFACE presente"
    else
        echo "  ✗ Rota $peer_net dev $OPT_IFACE AUSENTE"
    fi
}

configure_dc() {
    local host=$1
    local name=$2
    local ip=$3
    local peer_net=$4

    echo ""
    echo "── Configurando $name ($host) ──────────────────────────────"

    # Verifica IP atual
    CURRENT=$(ssh_cmd "$host" "ip addr show $OPT_IFACE 2>/dev/null | grep 'inet '" 2>/dev/null)
    if echo "$CURRENT" | grep -q "$ip"; then
        echo "  ✓ IP $ip já configurado"
    else
        echo "  Adicionando IP $ip em $OPT_IFACE ..."
        ssh_cmd "$host" "sudo ip addr add $ip dev $OPT_IFACE 2>/dev/null || true" 2>/dev/null
        ssh_cmd "$host" "sudo ip link set $OPT_IFACE up 2>/dev/null || true" 2>/dev/null
        # Verifica após configurar
        sleep 1
        NEW=$(ssh_cmd "$host" "ip addr show $OPT_IFACE 2>/dev/null | grep 'inet '" 2>/dev/null)
        if echo "$NEW" | grep -q "$ip"; then
            echo "  ✓ IP $ip configurado com sucesso"
        else
            echo "  ✗ Falha ao configurar IP — tente manualmente:"
            echo "    ssh $SSH_USER@$host"
            echo "    sudo ip addr add $ip dev $OPT_IFACE"
            echo "    sudo ip link set $OPT_IFACE up"
        fi
    fi

    # Rota para rede peer
    ROUTE=$(ssh_cmd "$host" "ip route show dev $OPT_IFACE 2>/dev/null" 2>/dev/null)
    if echo "$ROUTE" | grep -q "$peer_net"; then
        echo "  ✓ Rota $peer_net já existe"
    else
        echo "  Adicionando rota $peer_net dev $OPT_IFACE ..."
        ssh_cmd "$host" "sudo ip route add $peer_net dev $OPT_IFACE 2>/dev/null || true" 2>/dev/null
        sleep 1
        NEW_ROUTE=$(ssh_cmd "$host" "ip route show dev $OPT_IFACE 2>/dev/null" 2>/dev/null)
        if echo "$NEW_ROUTE" | grep -q "$peer_net"; then
            echo "  ✓ Rota $peer_net adicionada"
        else
            echo "  ✗ Falha ao adicionar rota — tente manualmente:"
            echo "    sudo ip route add $peer_net dev $OPT_IFACE"
        fi
    fi
}

test_ping() {
    echo ""
    echo "── Teste de conectividade ──────────────────────────────────"
    echo "  Testando ping DC5 (${DC5_OPT_ADDR}) → DC6 (${DC6_OPT_ADDR}) ..."
    RESULT=$(ssh_cmd "$DC5_MGMT" \
        "ping -c 4 -I $OPT_IFACE $DC6_OPT_ADDR 2>&1" 2>/dev/null)
    if echo "$RESULT" | grep -q "bytes from"; then
        echo "  ✓ PING FUNCIONANDO!"
        echo "$RESULT" | grep -E "(bytes from|packet loss|rtt)"
    else
        echo "  ✗ Ping falhou"
        echo "$RESULT" | tail -5
    fi

    echo ""
    echo "  Testando ping DC6 (${DC6_OPT_ADDR}) → DC5 (${DC5_OPT_ADDR}) ..."
    RESULT2=$(ssh_cmd "$DC6_MGMT" \
        "ping -c 4 -I $OPT_IFACE $DC5_OPT_ADDR 2>&1" 2>/dev/null)
    if echo "$RESULT2" | grep -q "bytes from"; then
        echo "  ✓ PING FUNCIONANDO!"
        echo "$RESULT2" | grep -E "(bytes from|packet loss|rtt)"
    else
        echo "  ✗ Ping falhou"
        echo "$RESULT2" | tail -5
    fi
}

echo "================================================================"
echo "  setup_dc_hosts.sh — Configuração de hosts DC5 / DC6"
echo "  DC5: $DC5_MGMT  →  $OPT_IFACE = $DC5_OPT_IP"
echo "  DC6: $DC6_MGMT  →  $OPT_IFACE = $DC6_OPT_IP"
echo "================================================================"

if [ "$MODE" = "--status" ]; then
    check_dc "$DC5_MGMT" "DC5" "$DC5_OPT_IP" "$DC6_OPT_NET"
    check_dc "$DC6_MGMT" "DC6" "$DC6_OPT_IP" "$DC5_OPT_NET"
elif [ "$MODE" = "--test" ]; then
    configure_dc "$DC5_MGMT" "DC5" "$DC5_OPT_IP" "$DC6_OPT_NET"
    configure_dc "$DC6_MGMT" "DC6" "$DC6_OPT_IP" "$DC5_OPT_NET"
    test_ping
else
    # Modo padrão: configure
    configure_dc "$DC5_MGMT" "DC5" "$DC5_OPT_IP" "$DC6_OPT_NET"
    configure_dc "$DC6_MGMT" "DC6" "$DC6_OPT_IP" "$DC5_OPT_NET"
fi

echo ""
echo "================================================================"
echo "  Comandos manuais se o script falhar:"
echo ""
echo "  # DC5:"
echo "  ssh $SSH_USER@$DC5_MGMT"
echo "  sudo ip addr add $DC5_OPT_IP dev $OPT_IFACE"
echo "  sudo ip link set $OPT_IFACE up"
echo "  sudo ip route add $DC6_OPT_NET dev $OPT_IFACE"
echo "  ping -c 4 -I $OPT_IFACE $DC6_OPT_ADDR"
echo ""
echo "  # DC6:"
echo "  ssh $SSH_USER@$DC6_MGMT"
echo "  sudo ip addr add $DC6_OPT_IP dev $OPT_IFACE"
echo "  sudo ip link set $OPT_IFACE up"
echo "  sudo ip route add $DC5_OPT_NET dev $OPT_IFACE"
echo "  ping -c 4 -I $OPT_IFACE $DC5_OPT_ADDR"
echo "================================================================"
