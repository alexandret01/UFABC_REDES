#!/usr/bin/env python3
"""
fix_cross_persist.py — Diagnostica e corrige a persistência dos cross-connects no OXC2 (Polatis).

Problema: POST em /api/data/optical-switch:cross-connects retorna HTTP 201,
mas os pares somem em ~30 segundos (GET retorna HTTP 204).

Hipóteses testadas neste script:
  1. POST entra em candidate config — precisa de NETCONF commit para ir ao running
  2. PUT substitui toda a lista (idempotente) — pode ser mais persistente que POST
  3. A URL base pode ter um lock de sessão — tentar datastores explícitos
  4. Salvar via /api/operations/ietf-netconf:commit
"""

import requests
import json
import time
from urllib3.exceptions import InsecureRequestWarning
requests.packages.urllib3.disable_warnings(category=InsecureRequestWarning)

BASE   = "http://172.17.36.22:8008"
AUTH   = ("admin", "root")
HDR    = {
    "Accept":       "application/yang-data+json",
    "Content-Type": "application/yang-data+json",
}

PAIRS = {"pair": [
    {"ingress": 1, "egress": 13},
    {"ingress": 5, "egress": 9}
]}

def get_cross():
    r = requests.get(f"{BASE}/api/data/optical-switch:cross-connects",
                     auth=AUTH, headers=HDR, verify=False)
    return r.status_code, r.text

def show_cross(label=""):
    code, body = get_cross()
    if label:
        print(f"\n[GET cross-connects] {label}")
    if code == 204 or not body:
        print(f"  Status {code} — VAZIO (sem cross-connects)")
    else:
        print(f"  Status {code}")
        try:
            print("  " + json.dumps(json.loads(body), indent=2).replace("\n", "\n  "))
        except Exception:
            print(" ", body)
    return code

# ---------------------------------------------------------------------------
# Passo 0 — estado atual
# ---------------------------------------------------------------------------
print("=" * 60)
print("PASSO 0 — Estado atual dos cross-connects")
print("=" * 60)
show_cross()

# ---------------------------------------------------------------------------
# Passo 1 — DELETE explícito antes de recriar (garantir estado limpo)
# ---------------------------------------------------------------------------
print("\n" + "=" * 60)
print("PASSO 1 — DELETE todos os cross-connects")
print("=" * 60)
r = requests.delete(f"{BASE}/api/data/optical-switch:cross-connects",
                    auth=AUTH, headers=HDR, verify=False)
print(f"  DELETE status: {r.status_code}")
time.sleep(1)
show_cross("após DELETE")

# ---------------------------------------------------------------------------
# Passo 2 — PUT (substitui toda a coleção — semântica "replace")
# ---------------------------------------------------------------------------
print("\n" + "=" * 60)
print("PASSO 2 — PUT cross-connects (replace total)")
print("=" * 60)
r = requests.put(
    f"{BASE}/api/data/optical-switch:cross-connects",
    auth=AUTH, headers=HDR,
    data=json.dumps({"optical-switch:cross-connects": PAIRS}),
    verify=False
)
print(f"  PUT status: {r.status_code}")
if r.text:
    print(" ", r.text[:300])

time.sleep(1)
show_cross("imediatamente após PUT")

# ---------------------------------------------------------------------------
# Passo 3 — Tentar commit RESTCONF (NETCONF candidate → running)
# ---------------------------------------------------------------------------
print("\n" + "=" * 60)
print("PASSO 3 — Tentar RESTCONF commit")
print("=" * 60)

# Forma 1: operação padrão ietf-netconf
for url in [
    f"{BASE}/api/operations/ietf-netconf:commit",
    f"{BASE}/api/operations/commit",
    f"{BASE}/restconf/operations/ietf-netconf:commit",
]:
    r = requests.post(url, auth=AUTH, headers=HDR, data="{}", verify=False)
    print(f"  POST {url.replace(BASE,'')} → {r.status_code} {r.text[:100] if r.text else ''}")
    if r.status_code in (200, 204):
        print("  ✓ commit aceito!")
        break

time.sleep(1)
show_cross("após tentativa de commit")

# ---------------------------------------------------------------------------
# Passo 4 — Tentar datastore running explícito
# ---------------------------------------------------------------------------
print("\n" + "=" * 60)
print("PASSO 4 — POST/PUT direto no datastore 'running'")
print("=" * 60)

for url in [
    f"{BASE}/api/data/ietf-datastores:running/optical-switch:cross-connects",
    f"{BASE}/restconf/data/optical-switch:cross-connects",
    f"{BASE}/api/running/optical-switch:cross-connects",
]:
    r = requests.put(url, auth=AUTH, headers=HDR,
                     data=json.dumps({"optical-switch:cross-connects": PAIRS}),
                     verify=False)
    print(f"  PUT {url.replace(BASE,'')} → {r.status_code}")
    if r.status_code in (200, 201, 204):
        print("  ✓ aceito!")

time.sleep(1)
show_cross("após tentativa datastore running")

# ---------------------------------------------------------------------------
# Passo 5 — POST original + verificação a cada 10s por 60s
# ---------------------------------------------------------------------------
print("\n" + "=" * 60)
print("PASSO 5 — POST original + monitoramento 60s")
print("=" * 60)

# Limpa e re-aplica via POST original
requests.delete(f"{BASE}/api/data/optical-switch:cross-connects",
                auth=AUTH, headers=HDR, verify=False)
time.sleep(0.5)

r = requests.post(
    f"{BASE}/api/data/optical-switch:cross-connects",
    auth=AUTH, headers=HDR,
    data=json.dumps(PAIRS),
    verify=False
)
print(f"  POST status: {r.status_code}")

for t in [5, 10, 20, 30, 45, 60]:
    time.sleep(5 if t == 5 else 5)
    elapsed = t
    code, body = get_cross()
    status = "OK ✓" if code == 200 else f"SUMIU ✗ ({code})"
    print(f"  t={elapsed:3d}s → {status}")
    if code != 200:
        print(f"  → Cross-connects perdidos em {elapsed}s!")
        break

# ---------------------------------------------------------------------------
# Passo 6 — Inspecionar schema/capabilities para encontrar operação de save
# ---------------------------------------------------------------------------
print("\n" + "=" * 60)
print("PASSO 6 — Listar operações RESTCONF disponíveis")
print("=" * 60)

for url in [
    f"{BASE}/api/",
    f"{BASE}/api/operations",
    f"{BASE}/.well-known/host-meta",
    f"{BASE}/restconf",
]:
    r = requests.get(url, auth=AUTH, headers=HDR, verify=False)
    if r.status_code == 200:
        print(f"  GET {url.replace(BASE,'')} → {r.status_code}")
        print("  " + r.text[:400].replace("\n", "\n  "))
        print()

print("\n" + "=" * 60)
print("DIAGNÓSTICO CONCLUÍDO")
print("=" * 60)
