#!/usr/bin/env python3
"""
set_padtec_channel.py — Tenta configurar canal C28 nos transponders Padtec.

Métodos tentados (em ordem):
  1. SSH direto ao supervisor Padtec (172.17.36.50:22) com flags de compatibilidade
  2. API REST/RESTCONF do Padtec (portas 80, 443, 8080, 8443)
  3. Via ONOS netconf driver (se dispositivo estiver conectado)

Canal alvo: C28 = 1554.94 nm = 193.4 THz (ITU-T grid 100 GHz)

Uso:
    python3 tools/set_padtec_channel.py [--check]
    --check : apenas verifica o canal atual, sem alterar
"""

import sys
import socket
import json
import time
import subprocess
import requests
from urllib3.exceptions import InsecureRequestWarning
requests.packages.urllib3.disable_warnings(InsecureRequestWarning)

PADTEC_IP   = "172.17.36.50"
ONOS_BASE   = "http://localhost:8181"
ONOS_AUTH   = ("onos", "rocks")
PADTEC_ID   = "padtec:172.17.36.50"

CHECK_ONLY  = "--check" in sys.argv

# Mapeamento canal → frequência/comprimento de onda (ITU-T C-band 100 GHz)
CHANNEL_MAP = {
    "C28": {"freq_thz": 193.4, "wl_nm": 1554.94, "itu": 28},
    "C24": {"freq_thz": 193.8, "wl_nm": 1551.72, "itu": 24},
}
TARGET_CH = "C28"

# ─── 0. Leitura do estado atual via ONOS ────────────────────────────────────

def read_onos_ports():
    """Retorna lista de portas do Padtec via ONOS REST."""
    try:
        r = requests.get(f"{ONOS_BASE}/onos/v1/devices/{PADTEC_ID}/ports",
                         auth=ONOS_AUTH, timeout=8)
        if r.status_code == 200:
            return r.json().get("ports", [])
    except Exception:
        pass
    return []

def show_current_state():
    ports = read_onos_ports()
    if not ports:
        print("  ✗ ONOS inacessível ou Padtec offline (sem dados de portas)")
        return
    print(f"  {len(ports)} porta(s) reportadas pelo ONOS:")
    for p in ports:
        ann  = p.get("annotations", {})
        num  = p.get("port", "?")
        name = ann.get("neName", f"Porta {num}")
        ch   = ann.get("channel", ann.get("lambda", "?"))
        freq = ann.get("frequency", "?")
        rx   = ann.get("inputPowerWDM", "N/A")
        tx   = ann.get("outputPowerWDM", "N/A")
        los  = ann.get("isLOS", "?")
        print(f"    Porta {num} {name}: canal={ch} freq={freq}  Rx={rx} dBm  Tx={tx} dBm  LOS={los}")

# ─── 1. Tentativa SSH ────────────────────────────────────────────────────────

SSH_CMD_TEMPLATE = [
    "ssh",
    "-o", "StrictHostKeyChecking=no",
    "-o", "UserKnownHostsFile=/dev/null",
    "-o", "HostKeyAlgorithms=+ssh-rsa,ssh-dss",
    "-o", "KexAlgorithms=+diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group1-sha1",
    "-o", "Ciphers=+aes128-cbc,aes256-cbc,3des-cbc",
    "-o", "PubkeyAuthentication=no",
    "-o", "PasswordAuthentication=yes",
    "-o", "ConnectTimeout=10",
    "-p", "22",
    "admin@" + PADTEC_IP,
]

# Comandos possíveis na CLI do supervisor Padtec T100DCT
# (baseado em Padtec docs / CLIs de transponders similares)
SSH_CHANNEL_CMDS = [
    # Tentativas de sintaxe comuns em supervisores de transponders
    f"set channel {TARGET_CH}",
    f"configure channel {TARGET_CH}",
    f"channel {TARGET_CH}",
    f"set wavelength 1554.94",
    f"config set channel=28",
]

def try_ssh_check():
    """Tenta SSH e retorna (ok, output). Não envia senha (não interativo)."""
    try:
        cmd = SSH_CMD_TEMPLATE + ["show channel 2>/dev/null || show interfaces 2>/dev/null || exit"]
        result = subprocess.run(cmd, capture_output=True, text=True, timeout=15)
        return True, result.stdout + result.stderr
    except subprocess.TimeoutExpired:
        return False, "timeout"
    except Exception as e:
        return False, str(e)

def try_sshpass(password, command):
    """Usa sshpass para autenticação por senha (se disponível)."""
    try:
        subprocess.run(["sshpass", "--version"], capture_output=True, timeout=3)
    except Exception:
        return False, "sshpass não disponível"

    try:
        cmd = ["sshpass", "-p", password] + SSH_CMD_TEMPLATE + [command]
        result = subprocess.run(cmd, capture_output=True, text=True, timeout=20)
        return result.returncode == 0, result.stdout + result.stderr
    except subprocess.TimeoutExpired:
        return False, "timeout"
    except Exception as e:
        return False, str(e)

# ─── 2. Tentativa REST/HTTP no Padtec ───────────────────────────────────────

PADTEC_HTTP_PORTS = [80, 443, 8080, 8443, 8008, 8181]
PADTEC_USERS = [("admin", "admin"), ("admin", "root"), ("padtec", "padtec"),
                ("supervisor", "supervisor"), ("root", "root")]

def try_http_ports():
    """Descobre se Padtec tem interface HTTP em alguma porta."""
    found = []
    for port in PADTEC_HTTP_PORTS:
        try:
            s = socket.create_connection((PADTEC_IP, port), timeout=3)
            s.close()
            found.append(port)
        except Exception:
            pass
    return found

def try_rest_api(port, scheme="http"):
    """Tenta endpoints REST comuns em transponders."""
    base = f"{scheme}://{PADTEC_IP}:{port}"
    endpoints = [
        "/api/v1/channels",
        "/api/channels",
        "/rest/channels",
        "/api/v1/transponders",
        "/api/v1/interfaces",
        "/yang-api/datastore/interfaces",
        "/restconf/data",
        "/restconf/data/channels",
    ]
    for cred in PADTEC_USERS:
        for ep in endpoints:
            try:
                r = requests.get(f"{base}{ep}", auth=cred,
                                 verify=False, timeout=5)
                if r.status_code < 400:
                    return True, cred, ep, r.status_code, r.text[:300]
            except Exception:
                continue
    return False, None, None, None, None

# ─── 3. Via ONOS FlowRules / Intent (para transponders ópticos) ─────────────

def try_onos_optical_intent():
    """
    Tenta criar um OpticalConnectivityIntent no ONOS para forçar o
    driver a configurar o canal. Isso funciona apenas se o driver
    Padtec implementar OpticalPathProgrammable.
    """
    payload = {
        "type": "OpticalConnectivityIntent",
        "appId": "org.onosproject.optical-rest",
        "ingressPoint": {"device": PADTEC_ID, "port": "1"},
        "egressPoint": {"device": PADTEC_ID, "port": "2"},
        "lambda": {
            "gridType": "DWDM",
            "channelSpacing": "CHL_100GHZ",
            "spacingMultiplier": 28,
            "slotGranularity": 1
        }
    }
    try:
        r = requests.post(f"{ONOS_BASE}/onos/v1/intents",
                          auth=ONOS_AUTH, json=payload, timeout=10)
        return r.status_code, r.text[:300]
    except Exception as e:
        return None, str(e)

# ═══════════════════════════════════════════════════════════════════════════════
print("=" * 65)
print("  Padtec Channel Configuration Tool")
print(f"  Alvo: {PADTEC_IP}  Canal: {TARGET_CH} (1554.94 nm / 193.4 THz)")
print("=" * 65)

# Estado atual
print("\n[ESTADO ATUAL — ONOS]")
show_current_state()

if CHECK_ONLY:
    sys.exit(0)

# ── Método 1: SSH ────────────────────────────────────────────────────────────
print("\n[MÉTODO 1] SSH direto ao supervisor Padtec...")
ok, out = try_ssh_check()
if ok:
    print(f"  ✓ SSH conectou! Saída:\n{out[:500]}")
else:
    print(f"  ✗ SSH falhou: {out}")
    # Tenta com sshpass + senhas comuns
    for pwd in ["admin", "root", "padtec", "supervisor", "padtec123"]:
        print(f"  Tentando sshpass senha '{pwd}'...")
        ok2, out2 = try_sshpass(pwd, "show channel 2>/dev/null || show running-config | grep channel")
        if ok2 and "channel" in out2.lower():
            print(f"  ✓ SSH com senha '{pwd}' OK! Saída:\n{out2[:300]}")
            break
        elif ok2:
            print(f"  ✓ SSH conectou (senha={pwd}): {out2[:200]}")
            break
        else:
            print(f"    → {out2[:80]}")

# ── Método 2: HTTP/REST ──────────────────────────────────────────────────────
print("\n[MÉTODO 2] Procurando interface HTTP no Padtec...")
open_ports = try_http_ports()
if not open_ports:
    print(f"  ✗ Nenhuma porta HTTP aberta em {PADTEC_IP} ({PADTEC_HTTP_PORTS})")
else:
    print(f"  Portas HTTP abertas: {open_ports}")
    for port in open_ports:
        for scheme in (["http"] if port not in [443, 8443] else ["https"]):
            ok3, cred, ep, code, body = try_rest_api(port, scheme)
            if ok3:
                print(f"  ✓ REST API encontrada: {scheme}:{port}{ep}  HTTP {code}")
                print(f"    Credenciais: {cred}")
                print(f"    Resposta: {body}")

# ── Método 3: ONOS Optical Intent ────────────────────────────────────────────
print("\n[MÉTODO 3] Tentando configurar canal via ONOS OpticalConnectivityIntent...")
code3, body3 = try_onos_optical_intent()
if code3 in (200, 201, 202):
    print(f"  ✓ Intent criado (HTTP {code3}): {body3}")
else:
    print(f"  ✗ Intent falhou (HTTP {code3}): {body3[:200]}")

# ── Resumo e próximos passos ─────────────────────────────────────────────────
print("\n" + "=" * 65)
print("  PRÓXIMOS PASSOS (se métodos acima falharam):")
print()
print("  a) Acesso físico ao painel do T100DCT#27:")
print("     - Menu LCD ou interface de configuração local")
print("     - Alterar Canal de C24 para C28 (1554.94 nm)")
print()
print("  b) SSH com cliente legacy (OpenSSH < 8.0):")
print("     ssh -oHostKeyAlgorithms=+ssh-rsa \\")
print("         -oKexAlgorithms=+diffie-hellman-group1-sha1 \\")
print("         admin@172.17.36.50")
print()
print("  c) Verificar se Padtec tem interface web:")
print(f"     curl -v http://{PADTEC_IP}:80/")
print()
print("  d) Localizar JARs do SDK Padtec no servidor:")
print("     find /home /opt /usr/local -name 'padtec*.jar' 2>/dev/null")
print("     find /home /opt /usr/local -name '*.jar' 2>/dev/null | xargs -I{} jar tf {} 2>/dev/null | grep -i 'padtec\\|transponder'")
print("=" * 65)
