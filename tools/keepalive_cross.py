#!/usr/bin/env python3
"""
keepalive_cross.py — Re-aplica cross-connects no OXC2 a cada 20s (workaround enquanto
o problema de persistência não é resolvido).

Uso:
    python3 tools/keepalive_cross.py          # roda em loop até Ctrl+C
    python3 tools/keepalive_cross.py --once   # aplica uma vez e sai

Cross-connects alvo (OXC2 / 172.17.36.22):
    ingress 1 → egress 13   (T100DCT#2 TX  → T100DCT#27 RX)
    ingress 5 → egress 9    (T100DCT#27 TX → T100DCT#2  RX)
"""

import requests
import json
import time
import sys
from urllib3.exceptions import InsecureRequestWarning
requests.packages.urllib3.disable_warnings(category=InsecureRequestWarning)

BASE  = "http://172.17.36.22:8008"
AUTH  = ("admin", "root")
HDR   = {
    "Accept":       "application/yang-data+json",
    "Content-Type": "application/yang-data+json",
}
PAIRS = {"pair": [
    {"ingress": 1, "egress": 13},
    {"ingress": 5, "egress": 9}
]}
INTERVAL = 20  # segundos entre re-aplicações

def apply_cross():
    """Apaga e recria os cross-connects. Retorna True se OK."""
    # DELETE primeiro para evitar conflitos
    requests.delete(f"{BASE}/api/data/optical-switch:cross-connects",
                    auth=AUTH, headers=HDR, verify=False, timeout=10)
    time.sleep(0.3)
    r = requests.post(
        f"{BASE}/api/data/optical-switch:cross-connects",
        auth=AUTH, headers=HDR,
        data=json.dumps(PAIRS),
        verify=False, timeout=10
    )
    return r.status_code in (200, 201, 204), r.status_code

def check_cross():
    """Verifica se os cross-connects estão presentes. Retorna True se sim."""
    try:
        r = requests.get(f"{BASE}/api/data/optical-switch:cross-connects",
                         auth=AUTH, headers=HDR, verify=False, timeout=10)
        return r.status_code == 200 and len(r.text) > 10
    except Exception:
        return False

ONCE = "--once" in sys.argv

print("=" * 55)
print("  OXC2 Cross-Connect Keepalive")
print(f"  OXC2: {BASE}")
print(f"  Pares: 1→13, 5→9")
print("=" * 55)

iteration = 0
try:
    while True:
        iteration += 1
        ts = time.strftime("%H:%M:%S")
        ok, code = apply_cross()
        symbol = "✓" if ok else "✗"
        print(f"[{ts}] #{iteration:4d} POST {symbol} (HTTP {code})", flush=True)

        if ONCE:
            print("Modo --once: saindo após primeira aplicação.")
            break

        # Monitora durante o intervalo
        time.sleep(5)
        if not check_cross():
            print(f"[{time.strftime('%H:%M:%S')}]       ⚠ Cross-connects já sumiram após 5s!", flush=True)
        time.sleep(INTERVAL - 5)

except KeyboardInterrupt:
    print("\n[Interrompido pelo usuário]")
    sys.exit(0)
