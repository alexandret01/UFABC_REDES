#!/usr/bin/env python3
"""
coletar_padtec_onos.py — Extrai dados de monitoramento do Padtec via ONOS REST API.

Uso:
  # Coleta única e imprime no terminal:
  python3 coletar_padtec_onos.py

  # Loop contínuo salvando CSV:
  python3 coletar_padtec_onos.py --loop --intervalo 60 --csv /tmp/padtec_historico.csv

  # Apenas alarmes ativos:
  python3 coletar_padtec_onos.py --alarmes

  # Exportar JSON completo:
  python3 coletar_padtec_onos.py --json

Requer: pip install requests (Python 3.6+)
"""

import argparse
import csv
import json
import sys
import time
from datetime import datetime

try:
    import requests
    from requests.exceptions import ConnectionError, Timeout
except ImportError:
    sys.exit("Instale o requests:  pip install requests")

# ---------------------------------------------------------------------------
# Configuração
# ---------------------------------------------------------------------------
ONOS_BASE  = "http://localhost:8181"
ONOS_USER  = "onos"
ONOS_PASS  = "rocks"
DEVICE_ID  = "padtec:172.17.36.50"
ALARM_PATH = "/onos/dhcp/alarms"      # bug ONOS 3.0.0-SNAPSHOT: contexto 'dhcp'

# Campos de anotação de porta que queremos extrair (mesma ordem no CSV)
PORT_FIELDS = [
    # Identidade
    "neName", "type",
    # WDM / Transponder genérico
    "channel", "lambda",
    "inputPower", "outputPower",
    "inputPowerWDM", "outputPowerWDM",
    "isLOS", "isLOF", "isOff",
    # ODU-k
    "bip8Rate", "beiRate", "isBDI",
    # FEC
    "fecName", "fecErrors", "fecRate", "fecRxEnabled", "fecTxEnabled",
    # Interface cliente
    "inputPowerClient", "outputPowerClient", "clientLambda",
    "isClientLOS", "isClientLOF", "isClientOff",
    # Amplificadores
    "gain", "powerInput", "powerOutput", "isAGC",
]


# ---------------------------------------------------------------------------
# Helpers HTTP
# ---------------------------------------------------------------------------

def _get(path, params=None):
    """GET em ONOS; retorna dict ou None em caso de erro."""
    url = ONOS_BASE + path
    try:
        r = requests.get(url, auth=(ONOS_USER, ONOS_PASS),
                         params=params, timeout=10)
        if r.status_code == 200:
            return r.json()
        print(f"[WARN] {url} → HTTP {r.status_code}", file=sys.stderr)
        return None
    except (ConnectionError, Timeout) as e:
        print(f"[ERRO] Não conseguiu conectar ao ONOS em {url}: {e}", file=sys.stderr)
        return None


# ---------------------------------------------------------------------------
# Coleta de dados
# ---------------------------------------------------------------------------

def coletar_device():
    """Retorna anotações do device (lastCollected, supervisor)."""
    data = _get(f"/onos/v1/devices/{DEVICE_ID}")
    if not data:
        return {}
    ann = data.get("annotations", {})
    return {
        "deviceId":      data.get("id", DEVICE_ID),
        "type":          data.get("type", ""),
        "mfr":           data.get("mfr", ""),
        "hw":            data.get("hw", ""),
        "sw":            data.get("sw", ""),
        "lastCollected": ann.get("lastCollected", ""),
        "supervisor":    ann.get("supervisor", ""),
        "available":     data.get("available", False),
    }


def coletar_portas():
    """
    Retorna lista de dicts com todos os campos de cada porta.
    Cada dict inclui: portNumber, isEnabled, portType + todos PORT_FIELDS.
    """
    data = _get(f"/onos/v1/devices/{DEVICE_ID}/ports")
    if not data:
        return []

    portas = []
    for p in data.get("ports", []):
        ann = p.get("annotations", {})
        row = {
            "portNumber": p.get("port", ""),
            "isEnabled":  p.get("isEnabled", ""),
            "portType":   p.get("type", ""),
        }
        for field in PORT_FIELDS:
            row[field] = ann.get(field, "")
        portas.append(row)
    return portas


def coletar_estatisticas():
    """
    Retorna dict portNumber → stats (packetsReceived=inputPower×1000, etc.)
    """
    data = _get(f"/onos/v1/statistics/ports/{DEVICE_ID}")
    if not data:
        return {}

    stats = {}
    # A API retorna lista de dispositivos; filtra o Padtec
    for entry in data.get("statistics", []):
        if entry.get("device") == DEVICE_ID:
            for ps in entry.get("ports", []):
                port = ps.get("port", "")
                stats[port] = {
                    "packetsReceived":  ps.get("packetsReceived", 0),
                    "packetsSent":      ps.get("packetsSent", 0),
                    "packetsRxErrors":  ps.get("packetsRxErrors", 0),
                    "packetsTxErrors":  ps.get("packetsTxErrors", 0),
                    "durationSec":      ps.get("durationSec", 0),
                }
    return stats


def coletar_alarmes():
    """Retorna lista de alarmes ativos para o device Padtec."""
    data = _get(ALARM_PATH, params={"devId": DEVICE_ID})
    if not data:
        return []
    return data.get("alarms", [])


def coletar_links():
    """Retorna links ópticos onde o Padtec é origem ou destino."""
    data = _get("/onos/v1/links", params={"device": DEVICE_ID})
    if not data:
        return []
    return data.get("links", [])


# ---------------------------------------------------------------------------
# Saída formatada
# ---------------------------------------------------------------------------

def imprimir_resumo(device, portas, stats, alarmes, links):
    ts = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"\n{'='*70}")
    print(f"  Padtec SPVL4 — {ts}")
    print(f"{'='*70}")

    # Device
    print(f"\n[DEVICE]")
    print(f"  ID          : {device.get('deviceId', '?')}")
    print(f"  Disponível  : {device.get('available', '?')}")
    print(f"  Última coleta: {device.get('lastCollected', '?')}")
    print(f"  Supervisor  : {device.get('supervisor', '?')}")

    # Portas
    print(f"\n[PORTAS] ({len(portas)} porta(s))")
    for p in portas:
        port_num  = p['portNumber']
        enabled   = "✓" if str(p.get('isEnabled', '')).lower() == 'true' else "✗"
        ne_name   = p.get('neName', '?')
        port_type = p.get('type', p.get('portType', '?'))
        channel   = p.get('channel', '')
        isLOS     = p.get('isLOS', '')

        # Potência — prioriza WDM, depois genérico
        rx = p.get('inputPowerWDM') or p.get('inputPower') or p.get('powerInput') or 'N/A'
        tx = p.get('outputPowerWDM') or p.get('outputPower') or p.get('powerOutput') or 'N/A'

        # Estatísticas (potência × 1000)
        st = stats.get(str(port_num), {})
        rx_stat = st.get('packetsReceived', 0)
        tx_stat = st.get('packetsSent', 0)

        los_flag = " ⚠ LOS!" if str(isLOS).lower() == 'true' else ""
        print(f"\n  Porta {port_num}: [{enabled}] {ne_name} ({port_type}){los_flag}")
        if channel:
            print(f"    Canal     : {channel}")
        lam = p.get('lambda', '')
        if lam and lam != 'N/A':
            print(f"    Lambda    : {lam} nm")
        print(f"    Rx (dBm)  : {rx}  |  Tx (dBm) : {tx}")

        # FEC (só OTN)
        fec_name = p.get('fecName', '')
        fec_err  = p.get('fecErrors', '')
        fec_rate = p.get('fecRate', '')
        if fec_name:
            print(f"    FEC       : {fec_name}  erros={fec_err}  rate={fec_rate}")

        # ODU-k
        bip8 = p.get('bip8Rate', '')
        bei  = p.get('beiRate', '')
        bdi  = p.get('isBDI', '')
        if any(v and v != 'N/A' for v in [bip8, bei, bdi]):
            print(f"    ODU-k     : BIP8={bip8}  BEI={bei}  BDI={bdi}")

        # Cliente
        cl_los = p.get('isClientLOS', '')
        cl_rx  = p.get('inputPowerClient', '')
        cl_tx  = p.get('outputPowerClient', '')
        if any(v and v != 'N/A' for v in [cl_los, cl_rx, cl_tx]):
            cl_los_flag = " ⚠" if str(cl_los).lower() == 'true' else ""
            print(f"    Cliente   : Rx={cl_rx} dBm  Tx={cl_tx} dBm  LOS={cl_los}{cl_los_flag}")

        # Amplificador
        gain = p.get('gain', '')
        agc  = p.get('isAGC', '')
        if gain and gain != 'N/A':
            print(f"    Ganho     : {gain} dB  AGC={agc}")

        if rx_stat or tx_stat:
            print(f"    Stats     : Rx={rx_stat/1000:.3f} dBm  Tx={tx_stat/1000:.3f} dBm (×1000)")

    # Alarmes
    print(f"\n[ALARMES] ({len(alarmes)} ativo(s))")
    if not alarmes:
        print("  Nenhum alarme ativo.")
    for a in alarmes:
        sev  = a.get('severity', '?')
        desc = a.get('description', '?')
        ts_r = a.get('timeRaised', 0)
        ts_r_str = datetime.fromtimestamp(ts_r / 1000).strftime("%Y-%m-%d %H:%M:%S") if ts_r else "?"
        print(f"  [{sev}] {desc}")
        print(f"          Gerado em: {ts_r_str}")

    # Links
    print(f"\n[LINKS] ({len(links)} link(s))")
    if not links:
        print("  Nenhum link óptico registrado.")
        print("  (Configure /home/sdn/padtec-links.json para injetar topologia)")
    for lk in links:
        src = lk.get('src', {})
        dst = lk.get('dst', {})
        ltype = lk.get('type', '')
        print(f"  {src.get('device','?')}:{src.get('port','?')} "
              f"→ {dst.get('device','?')}:{dst.get('port','?')}  [{ltype}]")

    print()


def imprimir_json(device, portas, stats, alarmes, links):
    output = {
        "timestamp": datetime.now().isoformat(),
        "device": device,
        "ports": [],
        "statistics": stats,
        "alarms": alarmes,
        "links": links,
    }
    for p in portas:
        port_num = str(p['portNumber'])
        p_out = dict(p)
        p_out["statistics"] = stats.get(port_num, {})
        output["ports"].append(p_out)
    print(json.dumps(output, indent=2, ensure_ascii=False))


def imprimir_alarmes(alarmes):
    if not alarmes:
        print("Nenhum alarme ativo.")
        return
    for a in alarmes:
        ts_r = a.get('timeRaised', 0)
        ts_r_str = datetime.fromtimestamp(ts_r / 1000).strftime("%Y-%m-%d %H:%M:%S") if ts_r else "?"
        print(f"[{a.get('severity','?')}] {a.get('description','?')}  @ {ts_r_str}")


# ---------------------------------------------------------------------------
# CSV
# ---------------------------------------------------------------------------

CSV_COLUMNS = (
    ["timestamp", "portNumber", "isEnabled", "portType"]
    + PORT_FIELDS
    + ["stat_rxPower_dBm", "stat_txPower_dBm", "stat_rxErrors", "stat_txErrors"]
)


def salvar_csv(caminho, portas, stats, timestamp):
    """Abre o CSV em modo append (cria cabeçalho se novo) e adiciona uma linha por porta."""
    import os
    novo = not os.path.exists(caminho)

    with open(caminho, "a", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=CSV_COLUMNS, extrasaction="ignore")
        if novo:
            writer.writeheader()

        for p in portas:
            port_num = str(p['portNumber'])
            st = stats.get(port_num, {})
            row = {"timestamp": timestamp}
            row.update(p)
            rx_raw = st.get('packetsReceived', 0)
            tx_raw = st.get('packetsSent', 0)
            row["stat_rxPower_dBm"] = rx_raw / 1000.0 if rx_raw else ""
            row["stat_txPower_dBm"] = tx_raw / 1000.0 if tx_raw else ""
            row["stat_rxErrors"]    = st.get('packetsRxErrors', "")
            row["stat_txErrors"]    = st.get('packetsTxErrors', "")
            writer.writerow(row)

    print(f"[{timestamp}] CSV atualizado: {caminho}")


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    parser = argparse.ArgumentParser(
        description="Extrai dados de monitoramento Padtec via ONOS REST API"
    )
    parser.add_argument("--loop",      action="store_true",
                        help="Executa em loop contínuo")
    parser.add_argument("--intervalo", type=int, default=60,
                        help="Intervalo em segundos entre coletas (padrão: 60)")
    parser.add_argument("--csv",       type=str, default="",
                        help="Salva dados em CSV (ex: /tmp/padtec.csv)")
    parser.add_argument("--json",      action="store_true",
                        help="Imprime saída em JSON em vez de texto formatado")
    parser.add_argument("--alarmes",   action="store_true",
                        help="Exibe apenas alarmes ativos")
    parser.add_argument("--onos",      type=str, default="",
                        help="Base URL do ONOS (ex: http://192.168.1.1:8181)")
    parser.add_argument("--device",    type=str, default="",
                        help=f"Device ID (padrão: {DEVICE_ID})")
    args = parser.parse_args()

    global ONOS_BASE, DEVICE_ID
    if args.onos:
        ONOS_BASE = args.onos.rstrip("/")
    if args.device:
        DEVICE_ID = args.device

    print(f"ONOS: {ONOS_BASE}  |  Device: {DEVICE_ID}", file=sys.stderr)

    def ciclo():
        ts = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        device  = coletar_device()
        portas  = coletar_portas()
        stats   = coletar_estatisticas()
        alarmes = coletar_alarmes()
        links   = coletar_links()

        if not device and not portas:
            print(f"[{ts}] Nenhum dado disponível. ONOS respondendo?", file=sys.stderr)
            return

        if args.alarmes:
            imprimir_alarmes(alarmes)
        elif args.json:
            imprimir_json(device, portas, stats, alarmes, links)
        else:
            imprimir_resumo(device, portas, stats, alarmes, links)

        if args.csv:
            salvar_csv(args.csv, portas, stats, ts)

    if args.loop:
        print(f"Loop a cada {args.intervalo}s. Ctrl+C para parar.", file=sys.stderr)
        while True:
            try:
                ciclo()
            except KeyboardInterrupt:
                print("\nParado pelo usuário.")
                break
            except Exception as e:
                print(f"[ERRO] {e}", file=sys.stderr)
            time.sleep(args.intervalo)
    else:
        ciclo()


if __name__ == "__main__":
    main()
