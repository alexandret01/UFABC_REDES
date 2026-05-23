# Driver ONOS — Equipamentos Padtec SPVL4 (UFABC)

Driver ONOS para integração nativa com equipamentos ópticos Padtec (placa SPVL4, Transponders OTN, Amplificadores) no laboratório OPTINET multi-camada da UFABC.

---

## Topologia do Laboratório

```
DC5 (172.17.36.208)                             DC6 (172.17.36.214)
10.0.0.2/24                                     10.0.1.2/24
    │ enp1s0                                         │ enp1s0
    ▼                                                ▼
PAV1 - Pica8 P3295                          PAV2 - Pica8 P3295
172.17.36.210                               172.17.36.211
of:5e3ec454441280b9                         of:5e3ec454443294fb
    │ porta 49 (SFP+ 10G)                       │ porta 49 (SFP+ 10G)
    │ DIRECT                                     │ DIRECT
    ▼                                            ▼
Padtec porta 4                          Padtec porta 5
(T100DCT#2 cliente 10G LR)              (T100DCT#27 cliente 10G LR)
    │                                            │
    ▼                                            ▼
Padtec porta 1                          Padtec porta 2
(T100DCT#2 WDM, canal C28)              (T100DCT#27 WDM, canal C24)
    │ OPTICAL                                    │ OPTICAL
    ▼                                            ▼
OXC2/Polatis porta 1 (ingress)          OXC2/Polatis porta 5 (ingress)
    │                                            │
    └──── cross-connect 1→13 ───────────────────┘ (TX do #2 → RX do #27)
    └──── cross-connect 5→9  ───────────────────┘ (TX do #27 → RX do #2)
    │                                            │
OXC2/Polatis porta 13 (egress)          OXC2/Polatis porta 9 (egress)
    │                                            │
    └──────── RX do T100DCT#27 ◄─────────────────┘
    └──────── RX do T100DCT#2  ◄─────────────────┘

Supervisor Padtec: 172.17.36.50:8886
OXC2 (Polatis): 172.17.36.22:830 (NETCONF) / 172.17.36.22:8008 (REST)
OXC1 (Polatis): 172.17.36.21 — COM DEFEITO (credenciais NETCONF desconhecidas)
```

### Fluxo de dados completo
```
DC5 → PAV1 → Padtec T100DCT#2 (cliente) → Padtec T100DCT#2 (WDM)
    → OXC2 (cross-connect 1→13 e 5→9)
    → Padtec T100DCT#27 (WDM) → Padtec T100DCT#27 (cliente) → PAV2 → DC6
```

---

## Dispositivos no ONOS (4 total)

| Device ID | Tipo | Status | IP |
|---|---|---|---|
| `padtec:172.17.36.50` | Padtec SPVL4 | AVAILABLE | 172.17.36.50 |
| `netconf:172.17.36.22:830` | Polatis OXC2 | AVAILABLE | 172.17.36.22 |
| `of:5e3ec454441280b9` | Pica8 PAV1 | AVAILABLE | 172.17.36.210 |
| `of:5e3ec454443294fb` | Pica8 PAV2 | AVAILABLE | 172.17.36.211 |

> **OXC1 (172.17.36.21)**: equipamento com defeito. Responde ping mas NETCONF falha com autenticação — credenciais desconhecidas. Não aparece no ONOS.

---

## Portas do Padtec (5 portas)

| Porta | Tipo | Equipamento | Função |
|---|---|---|---|
| 1 | OCH (WDM) | T100DCT-4GTT2L#2 | Enlace WDM → OXC2/porta 1 |
| 2 | OCH (WDM) | T100DCT-4GTT2L#27 | Enlace WDM → OXC2/porta 5 |
| 3 | OCH (WDM) | T25DC26-4BRE4L#10 | Amplificador (sem link ativo) |
| 4 | COPPER 10G | T100DCT-4GTT2L#2 | Cliente LR → PAV1/porta 49 |
| 5 | COPPER 10G | T100DCT-4GTT2L#27 | Cliente LR → PAV2/porta 49 |

---

## Links no ONOS (4 bidirecionais)

| Link | Tipo |
|---|---|
| `padtec:172.17.36.50/1 ↔ netconf:172.17.36.22:830/1` | OPTICAL |
| `padtec:172.17.36.50/2 ↔ netconf:172.17.36.22:830/5` | OPTICAL |
| `padtec:172.17.36.50/4 ↔ of:5e3ec454441280b9/49` | DIRECT |
| `padtec:172.17.36.50/5 ↔ of:5e3ec454443294fb/49` | DIRECT |

Links definidos em `tools/lab-topology.json`. Links espúrios (OXC1, amplificador) bloqueados com `"allowed": false`.

---

## Arquitetura do Driver

```
┌────────────────────────────────────────────────────────────────────┐
│                        ONOS (Java 11)                              │
│                                                                    │
│  PadtecDeviceProvider        PadtecDeviceDescription               │
│  (OSGi, polling ~20s)        (driver behaviour)                    │
│       │ TCP :10151                 │ TCP :10151                    │
│       └──────────────┬────────────┘                               │
│                      ▼                                             │
│               PadtecAgentServer                                    │
│               (TCP :10151 — serve JSON)                            │
│                      │                                             │
│               PadtecMonitorJSON3                                   │
│               (Java 18 — coleta contínua)                         │
│                      │ SDK nativo Padtec                           │
│                      ▼                                             │
│               Supervisor 172.17.36.50:8886                         │
└────────────────────────────────────────────────────────────────────┘
```

**Por que bridge TCP?** A lib Padtec exige Java 18 + JNI (`.so` nativos), incompatível com o OSGi do ONOS (Java 11). O `PadtecMonitorJSON3` roda como processo Java 18 separado e expõe os dados via socket TCP.

---

## Pré-requisitos

| Componente | Versão | Localização |
|---|---|---|
| ONOS | 3.0.0-SNAPSHOT | `/home/sdn/onos27/onos` |
| Java (ONOS) | 11 | sistema |
| Java (Monitor) | 18 | `/usr/lib/jvm/jdk-18.0.2.1` |
| TailEndController | — | `Outros/TailEndController/` |
| Hardware Padtec | SPVL4 | `172.17.36.50:8886` |

---

## Como compilar

```bash
cd /home/sdn/onos27/drivers/padtec/git/UFABC_REDES
mvn clean package -DskipTests
# Gera: target/onos-drivers-padtec-2.7.0.oar
```

---

## Como subir o laboratório

```bash
./setup_onos_lab.sh
```

**O script automatiza:**
1. Compila e inicia o agente Padtec (Java 18, TCP :10151)
2. Inicia o ONOS (Bazel, limpo)
3. Ativa apps necessárias (openflow, fwd, proxyarp, netconf, optical-model, faultmanagement, netcfglinksprovider, etc.)
4. Registra OXC2 via NETCONF (`tools/netconf-cfg2.json`)
5. Registra OXC1 via NETCONF (`tools/netconf-cfg1.json`) — ficará UNAVAILABLE por defeito
6. Instala o driver Padtec (`.oar`)
7. Injeta configuração do device Padtec (`padtec-netcfg.json`)
8. Aplica cross-connects no OXC2 (`tools/add_cross_rest.py`)
9. Injeta links estáticos da topologia (`tools/lab-topology.json`)

> **Atenção:** Os cross-connects do Polatis **não persistem após reinício** do hardware. Sempre rode `python3 tools/add_cross_rest.py` após ligar o OXC2.

---

## Como atualizar o driver (após mudança de código)

```bash
# 1. Compilar
mvn clean package -DskipTests

# 2. Remover versão antiga
curl -u onos:rocks -X DELETE \
  http://localhost:8181/onos/v1/applications/org.onosproject.drivers.padtec

sleep 2

# 3. Instalar nova versão
curl -u onos:rocks -X POST \
  -H "Content-Type: application/octet-stream" \
  "http://localhost:8181/onos/v1/applications?activate=true" \
  --data-binary @target/onos-drivers-padtec-2.7.0.oar

# 4. Reinjetar links (força CONFIG_ADDED)
curl -X DELETE -u onos:rocks http://localhost:8181/onos/v1/network/configuration/links
sleep 1
curl -X POST -H "content-type:application/json" \
  http://localhost:8181/onos/v1/network/configuration \
  -d @tools/lab-topology.json -u onos:rocks
```

---

## Cross-connects OXC2

```bash
# Verificar se estão ativos
curl -sS -u admin:root \
  "http://172.17.36.22:8008/api/data/optical-switch:cross-connects" \
  -H "Accept: application/yang-data+json"
# Esperado: HTTP 200 com pares ingress=1/egress=13 e ingress=5/egress=9

# Reaplicar (após reinício do Polatis)
python3 tools/add_cross_rest.py
```

---

## Extrair dados de monitoramento

### Potência óptica e alarmes por porta
```bash
curl -sS -u onos:rocks \
  http://localhost:8181/onos/v1/devices/padtec:172.17.36.50/ports \
  | python3 -c "
import json, sys
for p in json.load(sys.stdin)['ports']:
    ann = p['annotations']
    print(f\"=== Porta {p['port']} — {ann.get('neName','?')} ===\")
    for k,v in sorted(ann.items()):
        print(f'  {k:22} = {v}')
    print()
"
```

### Resumo rápido (sinal + alarme)
```bash
curl -sS -u onos:rocks \
  http://localhost:8181/onos/v1/devices/padtec:172.17.36.50/ports \
  | python3 -c "
import json, sys
for p in json.load(sys.stdin)['ports'][:3]:
    ann = p['annotations']
    rx  = ann.get('inputPowerWDM', ann.get('powerInput', 'N/A'))
    tx  = ann.get('outputPowerWDM', ann.get('powerOutput', 'N/A'))
    print(f\"Porta {p['port']} {ann.get('neName','?'):25} isLOS={ann.get('isLOS','?')} | RX={rx} dBm | TX={tx} dBm\")
"
```

### Estatísticas de porta (potência como long)
```bash
curl -sS -u onos:rocks \
  http://localhost:8181/onos/v1/statistics/ports/padtec:172.17.36.50 \
  | python3 -c "
import json, sys
for s in json.load(sys.stdin).get('statistics', []):
    for p in s.get('ports', []):
        rx  = p.get('packetsReceived', 0) / 1000
        tx  = p.get('packetsSent', 0) / 1000
        err = p.get('packetsRxErrors', 0)
        print(f\"Porta {p['port']:2} | RX: {rx:+.1f} dBm | TX: {tx:+.1f} dBm | FEC erros: {err}\")
"
```

### Alarmes ativos
```bash
curl -sS -u onos:rocks http://localhost:8181/onos/v1/alarms \
  | python3 -c "
import json, sys
alarms = json.load(sys.stdin).get('alarms', [])
print(f'Total: {len(alarms)} alarme(s)')
for a in alarms:
    print(f\"  [{a['severity']}] {a['description']}\")
"
```

### Links ativos
```bash
curl -sS -u onos:rocks http://localhost:8181/onos/v1/links | python3 -c "
import json, sys
data = json.load(sys.stdin)
for l in data['links']:
    src = l['src']['device'] + '/' + str(l['src']['port'])
    dst = l['dst']['device'] + '/' + str(l['dst']['port'])
    print(f\"{l['type']:8} | {src} -> {dst}\")
print(f'Total: {len(data[\"links\"])} links')
"
```

### JSON bruto do agente TCP
```bash
python3 -c "
import socket, json
s = socket.socket(); s.connect(('127.0.0.1', 10151))
data = b''
while True:
    chunk = s.recv(4096)
    if not chunk: break
    data += chunk
s.close()
obj = json.loads(data.decode())
devices = obj if isinstance(obj, list) else obj.get('devices', [])
for d in devices:
    print(f\"{d['name']:30} type={d['type']:15}\", end=' ')
    m = d.get('metrics', {})
    for k in ['inputPowerWDM','outputPowerWDM','inputPower','outputPower','isLOS','channel','gain']:
        if k in m: print(f'{k}={m[k]}', end=' ')
    print()
"
```

---

## Verificação rápida do estado do lab

```bash
# Devices
curl -sS -u onos:rocks http://localhost:8181/onos/v1/devices | python3 -c "
import json,sys
for d in json.load(sys.stdin)['devices']:
    print(f\"{'AVAIL' if d['available'] else 'UNAVAIL':8} | {d['id']}\")
"

# Links (esperado: 8 entradas = 4 bidirecionais)
curl -sS -u onos:rocks http://localhost:8181/onos/v1/links | python3 -c "
import json,sys; d=json.load(sys.stdin)
print(f'{len(d[\"links\"])} links')
for l in d['links']:
    print(f\"  {l['type']:8} {l['src']['device'].split(':')[-1]}/{l['src']['port']} -> {l['dst']['device'].split(':')[-1]}/{l['dst']['port']}\")
"

# Alarmes
curl -sS -u onos:rocks http://localhost:8181/onos/v1/alarms | python3 -c "
import json,sys; d=json.load(sys.stdin)
print(f'{len(d.get(\"alarms\",[]))} alarme(s)')
for a in d.get('alarms',[]): print(f'  [{a[\"severity\"]}] {a[\"description\"][:70]}')
"
```

---

## Dados expostos no ONOS

### Portas OCH — OTNTransponder (T100DCT-*)

#### Interface WDM
| Anotação | Descrição |
|---|---|
| `neName` | Nome do equipamento (ex: `T100DCT-4GTT2L#2`) |
| `channel` | Canal DWDM (ex: `C28`, `C24`) |
| `lambda` | Comprimento de onda em nm |
| `inputPowerWDM` | Potência RX WDM em dBm (ou `N/A`) |
| `outputPowerWDM` | Potência TX WDM em dBm |
| `isLOS` | `true` se sem sinal WDM |
| `isLOF` | `true` se frame OTN perdido |
| `isOff` | `true` se transmissor desligado |

#### ODU-k
| Anotação | Descrição |
|---|---|
| `bip8Rate` | Taxa de erro BIP-8 |
| `beiRate` | Taxa de erro BEI |
| `isBDI` | `true` se far-end reportou defeito |

#### FEC
| Anotação | Descrição |
|---|---|
| `fecName` | Algoritmo FEC (ex: `Reed-Solomon`) |
| `fecErrors` | Contador de erros FEC corrigidos |
| `fecRate` | BER pós-FEC |
| `fecRxEnabled` | FEC de recepção habilitado |
| `fecTxEnabled` | FEC de transmissão habilitado |

#### Interface Cliente (porta COPPER)
| Anotação | Descrição |
|---|---|
| `inputPowerClient` | Potência RX cliente em dBm |
| `outputPowerClient` | Potência TX cliente em dBm |
| `isClientLOS` | `true` se sem sinal na porta cliente |
| `isClientLOF` | `true` se frame perdido na porta cliente |
| `side` | `"client"` (identifica porta cliente) |

### Portas OCH — Transponder genérico (T25DC*)
| Anotação | Descrição |
|---|---|
| `inputPower` | Potência RX em dBm |
| `outputPower` | Potência TX em dBm |
| `isLOS` | `true` se sem sinal |

### Portas FIBER — Amplifier
| Anotação | Descrição |
|---|---|
| `gain` | Ganho em dB |
| `powerInput` | Potência de entrada em dBm |
| `powerOutput` | Potência de saída em dBm |
| `isAGC` | Modo Automatic Gain Control |

### Estatísticas de porta
| Campo ONOS | Dado real |
|---|---|
| `packetsReceived` | inputPower × 1000 (dBm como long) |
| `packetsSent` | outputPower × 1000 |
| `packetsRxErrors` | fecErrors (só OTNTransponder) |

### Alarmes (gerados por `PadtecAlarmConsumer`)
| Condição | Severidade |
|---|---|
| `isLOS=true` | **CRITICAL** |
| `isLOF=true` | **MAJOR** |
| `isBDI=true` | **MAJOR** |
| `isClientLOS=true` | **MAJOR** |
| `isClientLOF=true` | **MINOR** |
| `fecRate > 1e-4` | **WARNING** |

---

## Interface gráfica

Acesse: `http://172.17.36.231:8181/onos/ui` (usuário: `onos`, senha: `rocks`)

- **Devices** → 4 devices (Padtec, OXC2, PAV1, PAV2)
- **Ports** → 5 portas Padtec com canal, potência e status em tempo real
- **Links** → 4 links bidirecionais (2 OPTICAL + 2 DIRECT)
- **Alarms** → alarmes LOS/LOF/BDI/FEC quando há falha óptica

---

## Problemas conhecidos / Pendências

| Item | Status |
|---|---|
| OXC1 (172.17.36.21) com defeito | Credenciais NETCONF desconhecidas — HTTP 401 em todas as tentativas |
| LOS nos transponders | OXC2 portas 9 e 13 precisam de fibras conectadas nos RX do Padtec |
| Rota L3 DC5↔DC6 | Subnets 10.0.0.0/24 e 10.0.1.0/24 — requer `ip route add` em cada servidor |
| Senha dos servidores DC5/DC6 | SSH com usuário `optinet` — senha desconhecida |
| FEC desabilitado | `fecRxEnabled=false` e `fecTxEnabled=false` nos transponders |

---

## Estrutura do repositório

```
src/main/java/org/onosproject/drivers/padtec/
├── PadtecDeviceDescription.java    # discoverPortDetails + discoverPortStatistics
├── PadtecDeviceProvider.java       # Registra device + polling ~20s + portas + stats
├── PadtecAlarmConsumer.java        # 6 tipos de alarme via anotações de porta
├── PadtecPowerConfig.java          # PowerConfig — potência real para plano óptico
├── PadtecLambdaQuery.java          # Canal DWDM → OchSignal (grade ITU-T 50GHz)
├── PadtecPortAdmin.java            # isEnabled() real
├── PadtecFlowRuleProgrammable.java # Somente leitura
└── ...

Outros/TailEndController/
├── PadtecMonitorJSON3.java         # Processo Java 18: coleta contínua do hardware
├── PadtecAgentServer.java          # Servidor TCP :10151
└── lib/                            # JARs + .so nativos Padtec (Java 18)

tools/
├── lab-topology.json               # Links estáticos + supressões (allowed=false)
├── add_cross_rest.py               # Cross-connects OXC2: 1→13 e 5→9
├── netconf-cfg1.json               # OXC1 (172.17.36.21) — COM DEFEITO
├── netconf-cfg2.json               # OXC2 (172.17.36.22) — ativo
├── padtec-links.example.json       # Topologia física verificada
└── start_agent.sh                  # Inicia agente TCP manualmente

setup_onos_lab.sh                   # Setup completo do lab (ONOS + agente + links)
padtec-netcfg.json                  # Network config do Padtec no ONOS
pom.xml                             # Build Maven (Java 11, OSGi)
```
