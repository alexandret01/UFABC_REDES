#!/usr/bin/env python3
"""
status_lab.py — Snapshot rápido do estado do laboratório:
  1. Cross-connects no OXC2 (Polatis 172.17.36.22)
  2. Sinal óptico nos transponders Padtec (via ONOS REST)
  3. Alarmes ativos no Padtec

Uso:
    python3 tools/status_lab.py
    python3 tools/status_lab.py --watch   # repete a cada 15s
"""

import sys
import time
import json
import requests
from urllib3.exceptions import InsecureRequestWarning
requests.packages.urllib3.disable_warnings(InsecureRequestWarning)

# ── Configuração ─────────────────────────────────────────────
OXC2_BASE   = "http://172.17.36.22:8008"
OXC2_AUTH   = ("admin", "root")
OXC2_HDR    = {"Accept": "application/yang-data+json",
               "Content-Type": "application/yang-data+json"}

ONOS_BASE   = "http://localhost:8181"
ONOS_AUTH   = ("onos", "rocks")
PADTEC_ID   = "padtec:172.17.36.50"
# ─────────────────────────────────────────────────────────────

def get_cross():
    try:
        r = requests.get(f"{OXC2_BASE}/api/data/optical-switch:cross-connects",
                         auth=OXC2_AUTH, headers=OXC2_HDR, verify=False, timeout=5)
        return r.status_code, r.text
    except Exception as e:
        return None, str(e)

def get_padtec_ports():
    try:
        r = requests.get(f"{ONOS_BASE}/onos/v1/devices/{PADTEC_ID}/ports",
                         auth=ONOS_AUTH, timeout=5)
        if r.status_code == 200:
            return r.json().get("ports", [])
    except Exception:
        pass
    return []

def get_padtec_alarms():
    try:
        # ONOS 2.7 com faultmanagement usa contexto /onos/v1/alarms
        for path in ["/onos/v1/alarms", "/onos/dhcp/alarms"]:
            r = requests.get(f"{ONOS_BASE}{path}",
                             auth=ONOS_AUTH,
                             params={"devId": PADTEC_ID},
                             timeout=5)
            if r.status_code == 200:
                return r.json().get("alarms", [])
    except Exception:
        pass
    return []

def snapshot():
    import datetime
    ts = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"\n{'─'*60}")
    print(f"  STATUS DO LAB  {ts}")
    print(f"{'─'*60}")

    # ── OXC2 Cross-connects ──────────────────────────────────
    print("\n[OXC2] Cross-connects (172.17.36.22)")
    code, body = get_cross()
    if code is None:
        print(f"  ✗ Sem resposta: {body}")
    elif code == 204 or not body or len(body) < 5:
        print(f"  ✗ VAZIO (HTTP {code}) — cross-connects não configurados!")
    else:
        try:
            data = json.loads(body)
            # Polatis retorna {"optical-switch:cross-connects": {"pair": [...]}}
            root = data.get("optical-switch:cross-connects", data)
            pairs = root.get("pair", [])
            if pairs:
                print(f"  ✓ {len(pairs)} par(es) configurado(s):")
                for p in pairs:
                    print(f"    ingress {p.get('ingress','?')} → egress {p.get('egress','?')}")
            else:
                print(f"  ✗ Resposta sem 'pair': {body[:200]}")
        except Exception:
            print(f"  HTTP {code}: {body[:200]}")

    # ── Padtec Ports ─────────────────────────────────────────
    print(f"\n[Padtec] Sinal óptico ({PADTEC_ID})")
    ports = get_padtec_ports()
    if not ports:
        print("  ✗ Sem dados (ONOS inacessível ou device offline)")
    for p in ports:
        ann      = p.get("annotations", {})
        num      = p.get("port", "?")
        enabled  = p.get("isEnabled", False)
        ptype    = p.get("type", "?")
        name     = ann.get("neName", f"Porta {num}")
        isLOS    = ann.get("isLOS", "")
        rx_wdm   = ann.get("inputPowerWDM")  or ann.get("inputPower")  or "N/A"
        tx_wdm   = ann.get("outputPowerWDM") or ann.get("outputPower") or "N/A"
        rx_cli   = ann.get("inputPowerClient", "")
        isBDI    = ann.get("isBDI", "")
        fec_err  = ann.get("fecErrors", "")
        fec_rate = ann.get("fecRate", "")

        los_flag = " ⚠ LOS" if str(isLOS).lower() == "true" else ""
        bdi_flag = " ⚠ BDI" if str(isBDI).lower() == "true" else ""
        en_sym   = "✓" if str(enabled).lower() == "true" else "✗"

        print(f"\n  Porta {num} [{en_sym}] {name} ({ptype}){los_flag}{bdi_flag}")
        print(f"    WDM Rx={rx_wdm} dBm  Tx={tx_wdm} dBm")
        if rx_cli:
            print(f"    CLI Rx={rx_cli} dBm")
        if fec_err or fec_rate:
            print(f"    FEC erros={fec_err} rate={fec_rate}")

    # ── Alarmes ──────────────────────────────────────────────
    alarms = get_padtec_alarms()
    print(f"\n[Alarmes] ({len(alarms)} ativo(s))")
    if not alarms:
        print("  ✓ Nenhum alarme ativo")
    for a in alarms:
        print(f"  [{a.get('severity','?')}] {a.get('description','?')}")

    print()


if __name__ == "__main__":
    watch = "--watch" in sys.argv
    if watch:
        print("Modo watch — atualizando a cada 15s. Ctrl+C para parar.")
        try:
            while True:
                snapshot()
                time.sleep(15)
        except KeyboardInterrupt:
            print("\nParado.")
    else:
        snapshot()
