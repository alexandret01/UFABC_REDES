#!/usr/bin/env python3
"""
scan_oxc2_ports.py — Descobre em qual porta de entrada do OXC2 cada transponder está conectado.

Problema: T100DCT#27 não recebe sinal (Rx=N/A) apesar de OXC2(1→13) estar configurado.
Diagnóstico: testa cada uma das 8 entradas do OXC2 para ver qual entrega sinal à saída 13.

Método:
  Para cada ingress i em [1..8]:
    1. PUT cross-connect i → 13  (mantém 5→9 para o outro sentido)
    2. Aguarda 8s para o sinal estabilizar
    3. Lê inputPowerWDM da Porta 2 (T100DCT#27) via ONOS
    4. Se Rx ≠ N/A → encontrou a porta correta!

Uso:
    python3 tools/scan_oxc2_ports.py

AVISO: Este script altera temporariamente os cross-connects do OXC2.
       Ao final, restaura a configuração original (melhor ingress encontrado ou porta 1).
"""

import requests
import json
import time
import sys
from urllib3.exceptions import InsecureRequestWarning
requests.packages.urllib3.disable_warnings(InsecureRequestWarning)

OXC2_BASE  = "http://172.17.36.22:8008"
OXC2_AUTH  = ("admin", "root")
OXC2_HDR   = {"Accept": "application/yang-data+json",
               "Content-Type": "application/yang-data+json"}

ONOS_BASE  = "http://localhost:8181"
ONOS_AUTH  = ("onos", "rocks")
PADTEC_ID  = "padtec:172.17.36.50"

EGRESS_27  = 13   # saída do OXC2 que vai ao T100DCT#27 RX
EGRESS_2   = 9    # saída do OXC2 que vai ao T100DCT#2 RX
INGRESS_27 = 5    # entrada conhecida: T100DCT#27 TX → OXC2 porta 5 (confirmado)

WAIT_S = 8        # segundos para sinal estabilizar após PUT

# ─────────────────────────────────────────────────────────────

def put_cross(ing_a, egr_a, ing_b, egr_b):
    """PUT dois pares de cross-connects no OXC2."""
    body = {"optical-switch:cross-connects": {
        "pair": [
            {"ingress": ing_a, "egress": egr_a},
            {"ingress": ing_b, "egress": egr_b},
        ]
    }}
    r = requests.put(
        f"{OXC2_BASE}/api/data/optical-switch:cross-connects",
        auth=OXC2_AUTH, headers=OXC2_HDR,
        data=json.dumps(body), verify=False, timeout=10
    )
    return r.status_code

def read_rx_wdm(port_number):
    """Lê inputPowerWDM de uma porta do Padtec. Retorna float ou None."""
    try:
        r = requests.get(
            f"{ONOS_BASE}/onos/v1/devices/{PADTEC_ID}/ports",
            auth=ONOS_AUTH, timeout=8
        )
        if r.status_code != 200:
            return None
        for p in r.json().get("ports", []):
            if str(p.get("port")) == str(port_number):
                val = p.get("annotations", {}).get("inputPowerWDM", "N/A")
                if val and val != "N/A":
                    return float(val)
    except Exception:
        pass
    return None

def read_is_los(port_number):
    """Retorna isLOS de uma porta."""
    try:
        r = requests.get(
            f"{ONOS_BASE}/onos/v1/devices/{PADTEC_ID}/ports",
            auth=ONOS_AUTH, timeout=8
        )
        if r.status_code != 200:
            return None
        for p in r.json().get("ports", []):
            if str(p.get("port")) == str(port_number):
                return p.get("annotations", {}).get("isLOS", "?")
    except Exception:
        pass
    return None

# ─────────────────────────────────────────────────────────────
print("=" * 60)
print("  Scan de portas OXC2 — encontrar TX do T100DCT#2")
print(f"  Mantendo: ingress {INGRESS_27} → egress {EGRESS_2} (T100DCT#27→#2)")
print(f"  Testando: ingress X → egress {EGRESS_27} (T100DCT#2→#27)")
print(f"  Monitorando: Padtec Porta 2 (T100DCT#27) WDM Rx")
print("=" * 60)

best_ingress = None
best_rx = None

for ingress in range(1, 9):
    if ingress == INGRESS_27:
        print(f"\n  ingress {ingress}: pulando (em uso por T100DCT#27 TX)")
        continue

    code = put_cross(ingress, EGRESS_27, INGRESS_27, EGRESS_2)
    print(f"\n  ingress {ingress} → egress {EGRESS_27}  [PUT HTTP {code}]", end="", flush=True)

    if code not in (200, 201, 204):
        print(f"  ERRO: HTTP {code}, pulando")
        continue

    # Aguarda sinal estabilizar
    for _ in range(WAIT_S):
        time.sleep(1)
        print(".", end="", flush=True)

    rx = read_rx_wdm(2)   # porta 2 = T100DCT#27
    los = read_is_los(2)

    if rx is not None:
        print(f"  Rx={rx:.2f} dBm  isLOS={los}")
        if best_rx is None or rx > best_rx:
            best_rx = rx
            best_ingress = ingress
        if rx > -35.0:   # sinal razoável encontrado
            print(f"\n  ✓ SINAL ENCONTRADO! ingress {ingress} → egress {EGRESS_27}")
            print(f"    Rx = {rx:.2f} dBm na T100DCT#27")
            break
    else:
        print(f"  Rx=N/A  isLOS={los}")

# ─── Resultado ───────────────────────────────────────────────
print("\n" + "=" * 60)
if best_ingress is not None:
    print(f"  RESULTADO: T100DCT#2 TX → OXC2 ingress {best_ingress}")
    print(f"  Melhor sinal encontrado: {best_rx:.2f} dBm")
    print(f"\n  Cross-connects corretos:")
    print(f"    ingress {best_ingress} → egress {EGRESS_27}  (T100DCT#2→#27)")
    print(f"    ingress {INGRESS_27} → egress {EGRESS_2}  (T100DCT#27→#2)")
    # Aplica a configuração correta definitivamente
    put_cross(best_ingress, EGRESS_27, INGRESS_27, EGRESS_2)
    print(f"\n  ✓ Configuração aplicada via PUT (persistente).")
else:
    print(f"  RESULTADO: Nenhuma entrada entregou sinal ao T100DCT#27.")
    print(f"  Provável causa: fibra OXC2/saída {EGRESS_27} → T100DCT#27 RX desconectada.")
    print(f"  Verificação física necessária na bancada.")
    # Restaura configuração original
    put_cross(1, EGRESS_27, INGRESS_27, EGRESS_2)
    print(f"\n  Configuração original restaurada (ingress 1 → egress {EGRESS_27}).")

print("=" * 60)

# ─── Leitura final de ambos os transponders ──────────────────
print("\n[Leitura final]")
for port, name in [(1, "T100DCT#2"), (2, "T100DCT#27")]:
    rx = read_rx_wdm(port)
    los = read_is_los(port)
    rx_str = f"{rx:.2f} dBm" if rx is not None else "N/A"
    print(f"  Porta {port} {name}: WDM Rx={rx_str}  isLOS={los}")
