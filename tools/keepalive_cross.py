#!/usr/bin/env python3
"""
keepalive_cross.py — Re-aplica cross-connects no OXC2 a cada 60s (workaround enquanto
o problema de persistência não é resolvido).

Uso:
    python3 tools/keepalive_cross.py          # roda em loop até Ctrl+C
    python3 tools/keepalive_cross.py --once   # aplica uma vez e sai

Cross-connects alvo (OXC2 / 172.17.36.22):
    ingress 1 → egress 13
    ingress 2 → egress 11
    ingress 3 → egress 10
    ingress 5 → egress 9
    ingress 6 → egress 15
    ingress 7 → egress 14
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
    {"ingress": 2, "egress": 11},
    {"ingress": 3, "egress": 10},
    {"ingress": 5, "egress": 9},
    {"ingress": 6, "egress": 15},
    {"ingress": 7, "egress": 14},
]}
INTERVAL = 60  # segundos entre re-aplicações (PUT persiste; 60s é suficiente)

def apply_cross():
    """Sobrescreve os cross-connects via PUT (persiste; POST some em 5s). Retorna (ok, status)."""
    # PUT com namespace completo = substitui toda a coleção de forma persistente.
    # NÃO usar POST: o Polatis trata POST como candidato e descarta após ~5s.
    r = requests.put(
        f"{BASE}/api/data/optical-switch:cross-connects",
        auth=AUTH, headers=HDR,
        data=json.dumps({"optical-switch:cross-connects": PAIRS}),
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
print(f"  Pares: 1→13, 2→11, 3→10, 5→9, 6→15, 7→14")
print("=" * 55)

iteration = 0
try:
    while True:
        iteration += 1
        ts = time.strftime("%H:%M:%S")
        ok, code = apply_cross()
        symbol = "✓" if ok else "✗"
        print(f"[{ts}] #{iteration:4d} PUT  {symbol} (HTTP {code})", flush=True)

        if ONCE:
            print("Modo --once: saindo após primeira aplicação.")
            break

        # Verifica após 10s para confirmar que PUT persistiu
        time.sleep(10)
        if not check_cross():
            print(f"[{time.strftime('%H:%M:%S')}]       ⚠ Cross-connects sumiram mesmo com PUT! Re-aplicando...", flush=True)
            apply_cross()
        time.sleep(INTERVAL - 10)

except KeyboardInterrupt:
    print("\n[Interrompido pelo usuário]")
    sys.exit(0)
