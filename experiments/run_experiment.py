#!/usr/bin/env python3
"""
run_experiment.py — Executor de experimentos para o laboratório óptico UFABC

Uso:
    python3 run_experiment.py experimento.json
    python3 run_experiment.py experimento.json --dry-run   # mostra ações sem executar
    python3 run_experiment.py experimento.json --validate  # valida o JSON sem executar

Dependências:
    pip3 install requests

Formato do JSON: veja experiments/examples/
"""

import argparse
import csv
import json
import logging
import os
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path

try:
    import requests
except ImportError:
    sys.exit("Dependência não encontrada. Execute: pip3 install requests")


# ── logging ───────────────────────────────────────────────────────────────────

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s  %(levelname)-5s  %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger("optlab-exp")


# ── defaults ──────────────────────────────────────────────────────────────────

DEFAULTS = {
    "oxc2_url":    "http://172.17.36.22:8008",
    "oxc2_user":   "admin",
    "oxc2_pass":   "root",
    "onos_url":    "http://localhost:8181",
    "onos_user":   "onos",
    "onos_pass":   "rocks",
    "monitor_url": "http://localhost:9191",
    "output_dir":  "./results",
}


# ── ExperimentRunner ──────────────────────────────────────────────────────────

class ExperimentRunner:
    """
    Executa um experimento definido em JSON.

    Fluxo:
      pre_actions → [steps: actions → stabilize → measure] × repeat → post_actions
    """

    def __init__(self, spec, dry_run=False):
        self.spec     = spec
        self.dry_run  = dry_run
        self.cfg      = dict(DEFAULTS)
        self.cfg.update(spec.get("config", {}))

        # Sessão HTTP para o OXC2
        self.oxc2 = requests.Session()
        self.oxc2.auth = (self.cfg["oxc2_user"], self.cfg["oxc2_pass"])
        self.oxc2.headers.update({
            "Accept":       "application/yang-data+json",
            "Content-Type": "application/yang-data+json",
        })

        # Sessão HTTP para o ONOS
        self.onos = requests.Session()
        self.onos.auth = (self.cfg["onos_user"], self.cfg["onos_pass"])
        self.onos.headers["Content-Type"] = "application/json"

        # Resultados acumulados
        self.measurements = []

        # Diretório de saída
        ts   = datetime.now().strftime("%Y%m%d_%H%M%S")
        slug = spec.get("name", "experimento").lower().replace(" ", "_")
        self.out_dir = Path(self.cfg["output_dir"]) / f"{ts}_{slug}"
        if not dry_run:
            self.out_dir.mkdir(parents=True, exist_ok=True)

        log.info("=" * 60)
        log.info("Experimento : %s", spec.get("name", "sem nome"))
        log.info("Descrição   : %s", spec.get("description", ""))
        log.info("Saída       : %s", self.out_dir)
        log.info("Dry-run     : %s", dry_run)
        log.info("=" * 60)

    # ── ponto de entrada ──────────────────────────────────────────────────────

    def run(self):
        t0 = time.time()

        log.info("── PRÉ-AÇÕES ─────────────────────────────────────────────")
        self._run_actions(self.spec.get("pre_actions", []), ctx="pre")

        steps = self.spec.get("steps", [])
        for idx, step in enumerate(steps):
            label   = step.get("name", f"step_{idx + 1}")
            desc    = step.get("description", "")
            repeats = max(1, int(step.get("repeat", 1)))

            log.info("")
            log.info("── STEP %d/%d: %s%s ──────",
                     idx + 1, len(steps), label,
                     f"  [{desc}]" if desc else "")

            for r in range(repeats):
                ctx = f"{label}_r{r + 1}" if repeats > 1 else label
                if repeats > 1:
                    log.info("  ▶ Iteração %d/%d", r + 1, repeats)

                # Executa as ações do step
                self._run_actions(step.get("actions", []), ctx=ctx)

                # Aguarda estabilização
                stabilize = float(step.get("stabilize_s", 2))
                if stabilize > 0:
                    log.info("  ⏳ Estabilizando por %.0fs...", stabilize)
                    self._sleep(stabilize)

                # Loop de medições
                measure = step.get("measure")
                if measure:
                    self._measure_loop(
                        ctx=ctx,
                        duration_s=float(measure.get("duration_s", 30)),
                        interval_s=float(measure.get("interval_s", 5)),
                    )

        log.info("")
        log.info("── PÓS-AÇÕES ─────────────────────────────────────────────")
        self._run_actions(self.spec.get("post_actions", []), ctx="post")

        self._save_results()
        elapsed = time.time() - t0
        log.info("")
        log.info("✓ Concluído em %.0fs | %d medições | resultados: %s",
                 elapsed, len(self.measurements), self.out_dir)

    # ── ações ─────────────────────────────────────────────────────────────────

    def _run_actions(self, actions, ctx):
        for action in actions:
            atype = action.get("type", "")
            try:
                self._dispatch(atype, action, ctx)
            except Exception as exc:
                log.error("  ✗ Ação '%s' falhou: %s", atype, exc)
                if action.get("abort_on_error", False):
                    raise

    def _dispatch(self, atype, action, ctx):
        # ── controle de fluxo ─────────────────────────────────────────────────
        if atype == "log":
            log.info("  📋 %s", action.get("message", ""))

        elif atype == "wait":
            secs = float(action.get("seconds", 1))
            log.info("  ⏳ Aguardando %.0fs", secs)
            self._sleep(secs)

        elif atype == "measure":
            snap = self._snapshot(label=action.get("label", ctx))
            if snap:
                self.measurements.append(snap)
                self._log_snap(snap)

        # ── OXC2 ─────────────────────────────────────────────────────────────
        elif atype == "oxc2_add_xconnect":
            self._oxc2_add_xc(int(action["ingress"]), int(action["egress"]))

        elif atype == "oxc2_del_xconnect":
            self._oxc2_del_xc(int(action["ingress"]), int(action["egress"]))

        elif atype == "oxc2_voa":
            self._oxc2_voa(
                port=int(action["port"]),
                mode=action.get("mode", "VOA_MODE_ABSOLUTE"),
                level_db=action.get("level_dB"),
            )

        # ── ONOS ──────────────────────────────────────────────────────────────
        elif atype == "onos_request":
            self._onos_req(
                method=action.get("method", "GET"),
                path=action["path"],
                body=action.get("body"),
            )

        # ── shell ─────────────────────────────────────────────────────────────
        elif atype == "shell":
            self._shell(action["command"])

        else:
            log.warning("  ⚠  Tipo de ação desconhecido: '%s'", atype)

    # ── loop de medição ───────────────────────────────────────────────────────

    def _measure_loop(self, ctx, duration_s, interval_s):
        log.info("  📡 Medindo por %.0fs (intervalo %.0fs)...", duration_s, interval_s)
        end   = time.time() + duration_s
        count = 0
        while time.time() < end:
            snap = self._snapshot(label=f"{ctx}_m{count:03d}")
            if snap:
                self.measurements.append(snap)
                self._log_snap(snap)
                count += 1
            remaining = end - time.time()
            if remaining > 0:
                self._sleep(min(interval_s, remaining))
        log.info("  ✓ %d snapshots coletados", count)

    def _snapshot(self, label):
        try:
            r = requests.get(f"{self.cfg['monitor_url']}/status", timeout=5)
            if r.status_code != 200:
                log.warning("  Monitor HTTP %d", r.status_code)
                return None
            d = r.json()

            return {
                "label":           label,
                "timestamp":       d.get("timestamp", datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%SZ")),
                "pav_flows":       d.get("pavFlowsAdded", ""),
                "lldp_links":      d.get("lldpLinks", ""),
                "padtec_ok":       d.get("padtecAvailable", ""),
                "xconn_count":     len(d.get("crossConnects", [])),
                "oxc2_port_count": len(d.get("oxc2Ports", [])),
                "oxc2_ports":      d.get("oxc2Ports", []),
                "devices":         d.get("devices", []),
            }
        except Exception as exc:
            log.warning("  Snapshot falhou: %s", exc)
            return None

    def _log_snap(self, snap):
        powers = [
            f"p{p['portId']}={float(p['power']):.1f}"
            for p in snap.get("oxc2_ports", [])
            if p.get("power")
        ]
        log.info("  📸 %s | xconn=%s pav=%s | pot: %s",
                 snap["timestamp"],
                 snap["xconn_count"],
                 snap["pav_flows"],
                 "  ".join(powers[:6]) or "—")

    # ── OXC2 REST ─────────────────────────────────────────────────────────────

    def _oxc2_add_xc(self, ingress, egress):
        log.info("  🔗 OXC2 add xconnect %d → %d", ingress, egress)
        if self.dry_run:
            return
        url  = f"{self.cfg['oxc2_url']}/api/data/optical-switch:cross-connects"
        body = {"optical-switch:pair": [{"ingress": ingress, "egress": egress}]}
        r    = self.oxc2.post(url, json=body, timeout=10)
        self._check(r, f"oxc2_add_xconnect {ingress}→{egress}")

    def _oxc2_del_xc(self, ingress, egress):
        log.info("  ✂️  OXC2 del xconnect %d → %d", ingress, egress)
        if self.dry_run:
            return
        url = (f"{self.cfg['oxc2_url']}/api/data/"
               f"optical-switch:cross-connects/pair={ingress},{egress}")
        r   = self.oxc2.delete(url, timeout=10)
        self._check(r, f"oxc2_del_xconnect {ingress}→{egress}")

    def _oxc2_voa(self, port, mode, level_db=None):
        log.info("  🎚️  OXC2 VOA porta %d  mode=%s  level=%s dB",
                 port, mode, level_db if level_db is not None else "N/A")
        if self.dry_run:
            return
        entry = {"port-id": port, "atten-mode": mode}
        if level_db is not None and mode != "VOA_MODE_NONE":
            entry["atten-level"] = float(level_db)
        body = {"optical-switch:voa": {"port": [entry]}}
        url  = f"{self.cfg['oxc2_url']}/api/data/optical-switch:voa"
        r    = self.oxc2.patch(url, json=body, timeout=10)
        self._check(r, f"oxc2_voa porta {port}")

    # ── ONOS REST ─────────────────────────────────────────────────────────────

    def _onos_req(self, method, path, body=None):
        log.info("  🌐 ONOS %s %s", method, path)
        if self.dry_run:
            return
        url = f"{self.cfg['onos_url']}/onos/v1{path}"
        r   = self.onos.request(method, url, json=body, timeout=10)
        self._check(r, f"onos {method} {path}")

    # ── shell ─────────────────────────────────────────────────────────────────

    def _shell(self, command):
        log.info("  💻 shell: %s", command)
        if self.dry_run:
            return
        result = subprocess.run(
            command, shell=True, capture_output=True, text=True, timeout=60
        )
        if result.stdout.strip():
            for line in result.stdout.strip().splitlines()[:5]:
                log.info("     > %s", line)
        if result.returncode != 0:
            log.warning("  exit %d — %s",
                        result.returncode, result.stderr.strip()[:200])

    # ── save ──────────────────────────────────────────────────────────────────

    def _save_results(self):
        if self.dry_run:
            log.info("(dry-run: nenhum resultado salvo)")
            return
        if not self.measurements:
            log.warning("Nenhuma medição coletada.")
            return

        # Cópia da definição do experimento
        with open(self.out_dir / "experiment.json", "w", encoding="utf-8") as f:
            json.dump(self.spec, f, indent=2, ensure_ascii=False)

        # CSV resumo (uma linha por snapshot)
        flat_keys = ["label", "timestamp", "pav_flows", "lldp_links",
                     "padtec_ok", "xconn_count", "oxc2_port_count"]
        with open(self.out_dir / "measurements.csv", "w", newline="", encoding="utf-8") as f:
            w = csv.DictWriter(f, fieldnames=flat_keys, extrasaction="ignore")
            w.writeheader()
            w.writerows(self.measurements)

        # CSV detalhado OXC2 por porta
        port_rows = []
        for snap in self.measurements:
            for p in snap.get("oxc2_ports", []):
                port_rows.append({
                    "label":      snap["label"],
                    "timestamp":  snap["timestamp"],
                    "portId":     p.get("portId", ""),
                    "status":     p.get("status", ""),
                    "peerPort":   p.get("peerPort", ""),
                    "power_dBm":  p.get("power", ""),
                    "attenMode":  p.get("attenMode", ""),
                    "attenLevel": p.get("attenLevel", ""),
                })
        if port_rows:
            with open(self.out_dir / "oxc2_ports.csv", "w", newline="", encoding="utf-8") as f:
                w = csv.DictWriter(f, fieldnames=list(port_rows[0].keys()))
                w.writeheader()
                w.writerows(port_rows)

        # CSV Padtec devices
        dev_rows = []
        for snap in self.measurements:
            for d in snap.get("devices", []):
                row = {"label": snap["label"], "timestamp": snap["timestamp"]}
                row.update({k: v for k, v in d.items() if isinstance(v, (str, int, float, bool))})
                dev_rows.append(row)
        if dev_rows:
            all_keys = list(dict.fromkeys(k for row in dev_rows for k in row))
            with open(self.out_dir / "padtec_devices.csv", "w", newline="", encoding="utf-8") as f:
                w = csv.DictWriter(f, fieldnames=all_keys, extrasaction="ignore")
                w.writeheader()
                w.writerows(dev_rows)

        log.info("Salvos em %s:", self.out_dir)
        for p in sorted(self.out_dir.iterdir()):
            log.info("  %s  (%d bytes)", p.name, p.stat().st_size)

    # ── utilidades ────────────────────────────────────────────────────────────

    def _sleep(self, secs):
        if not self.dry_run and secs > 0:
            time.sleep(secs)

    @staticmethod
    def _check(response, label):
        if response.status_code not in (200, 201, 204):
            log.warning("  HTTP %d para %s: %s",
                        response.status_code, label,
                        response.text[:200])
        else:
            log.info("    → HTTP %d OK", response.status_code)


# ── validação básica ──────────────────────────────────────────────────────────

VALID_ACTION_TYPES = {
    "log", "wait", "measure",
    "oxc2_add_xconnect", "oxc2_del_xconnect", "oxc2_voa",
    "onos_request", "shell",
}

REQUIRED_FIELDS = {
    "oxc2_add_xconnect": ["ingress", "egress"],
    "oxc2_del_xconnect": ["ingress", "egress"],
    "oxc2_voa":          ["port"],
    "onos_request":      ["path"],
    "shell":             ["command"],
}


def validate(spec):
    errors = []
    if "name" not in spec:
        errors.append("Campo 'name' obrigatório.")
    if "steps" not in spec and "pre_actions" not in spec:
        errors.append("Nenhum 'steps' ou 'pre_actions' definido.")

    def check_actions(actions, where):
        for i, a in enumerate(actions):
            t = a.get("type", "")
            if t not in VALID_ACTION_TYPES:
                errors.append(f"{where}[{i}]: tipo desconhecido '{t}'")
            for req in REQUIRED_FIELDS.get(t, []):
                if req not in a:
                    errors.append(f"{where}[{i}]: campo '{req}' obrigatório para tipo '{t}'")

    check_actions(spec.get("pre_actions", []),  "pre_actions")
    check_actions(spec.get("post_actions", []), "post_actions")
    for i, step in enumerate(spec.get("steps", [])):
        check_actions(step.get("actions", []), f"steps[{i}].actions")

    return errors


# ── main ──────────────────────────────────────────────────────────────────────

def main():
    parser = argparse.ArgumentParser(
        description="Executor de experimentos ópticos UFABC",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""Exemplos:
  python3 run_experiment.py examples/01_xconnect_switch.json
  python3 run_experiment.py examples/02_voa_sweep.json --dry-run
  python3 run_experiment.py meu_exp.json --validate
""",
    )
    parser.add_argument("experiment", help="Arquivo JSON do experimento")
    parser.add_argument("--dry-run",  action="store_true",
                        help="Mostra ações sem executar nenhuma chamada HTTP")
    parser.add_argument("--validate", action="store_true",
                        help="Valida o JSON e sai sem executar")
    args = parser.parse_args()

    path = Path(args.experiment)
    if not path.exists():
        sys.exit(f"Arquivo não encontrado: {path}")

    with open(path, encoding="utf-8") as f:
        spec = json.load(f)

    errors = validate(spec)
    if errors:
        log.error("JSON inválido:")
        for e in errors:
            log.error("  • %s", e)
        sys.exit(1)

    if args.validate:
        log.info("✓ JSON válido: %s", path)
        return

    runner = ExperimentRunner(spec, dry_run=args.dry_run)
    runner.run()


if __name__ == "__main__":
    main()
