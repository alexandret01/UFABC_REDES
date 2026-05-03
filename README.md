# Driver ONOS — Equipamentos Padtec SPVL4 (UFABC)

Driver ONOS para integração nativa com equipamentos ópticos Padtec (placa SPVL4, Transponders OTN, Amplificadores) no laboratório de redes ópticas da UFABC.

---

## Arquitetura

```
┌────────────────────────────────────────────────────────────────────┐
│                        ONOS (Java 11)                              │
│                                                                    │
│  PadtecManager        PadtecDeviceProvider   PadtecLinkProvider   │
│  (OSGi Component)     (OSGi Component)       (OSGi Component)     │
│       │                     │                      │              │
│       │ ProcessBuilder      │ Timer 45s            │ Timer 60s    │
│       ▼                     ▼                      ▼              │
│  [Java 18 process]    injectDevice()         injectLinks()        │
│  PadtecMonitorJSON3   updatePorts()          (lê /home/sdn/       │
│  + PadtecAgentServer  updatePortStats()       padtec-links.json)  │
│       │  TCP :10151         │ TCP :10151           │              │
│       └─────────────────────┘                      │              │
│                                                     ▼              │
│  Behaviours do driver (padtec-drivers.xml):    ONOS Topology      │
│    PadtecDeviceDescription  → portas + anotações  (links ópticos  │
│    PadtecPowerConfig        → potência real        Padtec↔Polatis)│
│    PadtecAlarmConsumer      → 6 tipos de alarme                   │
│    PadtecLambdaQuery        → OchSignal por canal DWDM            │
│    PadtecPortAdmin          → isEnabled real                      │
└────────────────────────────────────────────────────────────────────┘
         │ SNMP/proprietário
         ▼
   Supervisor Padtec
   172.17.36.50:8886
```

**Fluxo completo:**
1. ONOS ativa → `PadtecManager` detecta se porta 10151 está livre
2. Se livre, lança `PadtecMonitorJSON3` via `ProcessBuilder` (Java 18, `/home/sdn/TailEndController`)
3. O monitor conecta ao hardware real (`172.17.36.50`) e inicia `PadtecAgentServer` na porta TCP 10151
4. O monitor coleta todas as métricas e entra em **loop contínuo a cada 60s**
5. Após 45s, `PadtecDeviceProvider` lê o JSON e registra device + portas no ONOS
6. A cada 60s: portas são **re-lidas** (atualiza potência/isLOS/canais) e estatísticas são atualizadas
7. Após 60s, `PadtecLinkProvider` injeta links ópticos Padtec↔Polatis (requer `padtec-links.json`)
8. `PadtecAlarmConsumer` gera alarmes para cada porta com falha detectada (LOS, LOF, BDI, FEC alto, etc.)
9. `PadtecPowerConfig` expõe potência real (dBm) para o plano de controle óptico do ONOS

**Não é necessário rodar `monitor.sh` manualmente.**

---

## Pré-requisitos

| Componente | Versão | Localização |
|------------|--------|-------------|
| ONOS | 2.7.0 / 3.0.0-SNAPSHOT | `/tmp/onos-3.0.0-SNAPSHOT` |
| Java (ONOS) | 11 | `/tmp/onos-3.0.0-SNAPSHOT-jdk` |
| Java (Monitor) | 18 | `/usr/lib/jvm/jdk-18.0.2.1` |
| TailEndController | — | `/home/sdn/TailEndController` |
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
# Inicia ONOS + Polatis + Padtec + cross-connects
sudo ./setup_onos_lab.sh
```

**O script automatiza:**
1. Inicia o ONOS (Karaf/Bazel)
2. Aguarda a API REST estabilizar
3. Ativa apps necessárias (incl. `faultmanagement` para alarmes e `optical-model`)
4. Configura os switches Polatis via NETCONF
5. Instala o driver Padtec (`.oar`)
6. Injeta a topologia (`padtec-netcfg.json`)
7. Cria cross-connects nos Polatis (`add_cross_rest.py`)

Após ~45s do ONOS subir, o equipamento Padtec aparece automaticamente com as portas reais.

> **Nota:** Os cross-connects dos Polatis não persistem após reinício do hardware. O `setup_onos_lab.sh` os recria automaticamente a cada execução.

---

## Como atualizar o driver (após mudança de código)

```bash
# 1. Compilar
mvn clean package -DskipTests

# 2. Remover versão antiga
curl -u onos:rocks -X DELETE \
  http://localhost:8181/onos/v1/applications/org.onosproject.drivers.padtec

sleep 3

# 3. Instalar nova versão
curl -u onos:rocks -X POST \
  -H "Content-Type: application/octet-stream" \
  "http://localhost:8181/onos/v1/applications?activate=true" \
  --data-binary @target/onos-drivers-padtec-2.7.0.oar
```

---

## Topologia verificada do laboratório

```
Padtec (172.17.36.50)          Polatis-1 (172.17.36.21)
  porta 1 (T100DCT#2,  C28) ──▶ porta 1 (INPUT) ──[cross 1→10]──▶ porta 10 (OUTPUT)
  porta 2 (T100DCT#27, C24) ──▶ porta 2 (INPUT) ──[cross 2→9] ──▶ porta  9 (OUTPUT)

Padtec (172.17.36.50)          Polatis-2 (172.17.36.22)
  porta 3 (T25DC26,  Trsp.) ──▶ porta 1 (INPUT) ──[cross 1→10]──▶ porta 10 (OUTPUT)
```

Para recalibrar os cross-connects após reinício dos Polatis:
```bash
python3 tools/add_cross_rest.py
```

---

## Configurar links ópticos Padtec ↔ Polatis

O arquivo de topologia já está preenchido com os valores reais em `tools/padtec-links.example.json`.
Copie-o para `/home/sdn/padtec-links.json`:

```bash
cp tools/padtec-links.example.json /home/sdn/padtec-links.json
```

O `PadtecLinkProvider` lê este arquivo 60s após o ONOS subir e injeta os links automaticamente.

---

## Extrair dados de monitoramento para análise

O script `tools/coletar_padtec_onos.py` coleta todos os dados via ONOS REST API:

```bash
pip3 install requests

# Resumo legível no terminal (snapshot único)
python3 tools/coletar_padtec_onos.py

# Loop contínuo salvando CSV (para análise histórica)
python3 tools/coletar_padtec_onos.py --loop --intervalo 60 --csv /home/sdn/padtec_historico.csv

# Só alarmes ativos
python3 tools/coletar_padtec_onos.py --alarmes

# JSON completo (para integração com outros sistemas)
python3 tools/coletar_padtec_onos.py --json > /tmp/snapshot.json
```

O CSV gerado contém uma linha por porta a cada ciclo com todos os campos de monitoramento.

---

## Verificação

```bash
# Device e anotações (lastCollected, supervisor)
curl -s -u onos:rocks \
  http://localhost:8181/onos/v1/devices/padtec:172.17.36.50 | python3 -m json.tool

# Portas com canal, inputPower, outputPower, isLOS (atualiza a cada 60s)
curl -s -u onos:rocks \
  http://localhost:8181/onos/v1/devices/padtec:172.17.36.50/ports | python3 -m json.tool

# Estatísticas de porta (potência × 1000 como contadores; fecErrors em packetsRxErrors)
curl -s -u onos:rocks \
  http://localhost:8181/onos/v1/statistics/ports/padtec:172.17.36.50 | python3 -m json.tool

# Alarmes ativos
# NOTA: ONOS 3.0.0-SNAPSHOT — bug de packaging registra faultmanagement no contexto "onos/dhcp"
curl -s -u onos:rocks \
  "http://localhost:8181/onos/dhcp/alarms?devId=padtec:172.17.36.50" | python3 -m json.tool

# Links ópticos injetados
curl -s -u onos:rocks \
  "http://localhost:8181/onos/v1/links?device=padtec:172.17.36.50" | python3 -m json.tool

# Log do monitor Java 18
tail -f /tmp/padtec_monitor.log
```

---

## Dados expostos no ONOS

### Device (`padtec:172.17.36.50`)
| Anotação | Exemplo |
|----------|---------|
| `lastCollected` | `2026-05-03_10-32-39` (atualiza a cada 60s) |
| `supervisor` | `172.17.36.50:8886` |

### Portas OCH — OTNTransponder (T100DCT-*)

#### Interface WDM
| Anotação | Descrição |
|----------|-----------|
| `neName` | Nome do equipamento (ex: `T100DCT-4GTT2L#2`) |
| `channel` | Canal DWDM (ex: `C28`, `C24`) |
| `lambda` | Comprimento de onda em nm |
| `inputPowerWDM` | Potência de entrada WDM em dBm (ou `N/A`) |
| `outputPowerWDM` | Potência de saída WDM em dBm (ou `N/A`) |
| `isLOS` | `true` se sem sinal óptico |
| `isLOF` | `true` se frame OTN perdido |
| `isOff` | `true` se transmissor desligado |

#### ODU-k (qualidade do sinal)
| Anotação | Descrição |
|----------|-----------|
| `bip8Rate` | Taxa de erro BIP-8 |
| `beiRate` | Taxa de erro BEI |
| `isBDI` | `true` se far-end reportou defeito (Backward Defect Indicator) |

#### FEC
| Anotação | Descrição |
|----------|-----------|
| `fecName` | Nome do algoritmo FEC (ex: `Reed-Solomon`) |
| `fecErrors` | Contador de erros FEC corrigidos (também em `packetsRxErrors`) |
| `fecRate` | Taxa de erro FEC (BER pós-FEC) |
| `fecRxEnabled` | `true` se FEC de recepção habilitado |
| `fecTxEnabled` | `true` se FEC de transmissão habilitado |

#### Interface Cliente
| Anotação | Descrição |
|----------|-----------|
| `inputPowerClient` | Potência de entrada do lado cliente em dBm |
| `outputPowerClient` | Potência de saída do lado cliente em dBm |
| `clientLambda` | Comprimento de onda da interface cliente em nm |
| `isClientLOS` | `true` se sem sinal na porta cliente |
| `isClientLOF` | `true` se frame perdido na porta cliente |
| `isClientOff` | `true` se interface cliente desligada |

### Portas OCH — Transponder genérico (T25DC*)
| Anotação | Descrição |
|----------|-----------|
| `neName` | Nome do equipamento |
| `channel` | Canal DWDM |
| `lambda` | Comprimento de onda em nm |
| `inputPower` | Potência de entrada em dBm (ou `N/A`) |
| `outputPower` | Potência de saída em dBm (ou `N/A`) |
| `isLOS` | `true` se sem sinal óptico |

### Portas FIBER — Amplifier
| Anotação | Descrição |
|----------|-----------|
| `neName` | Nome do equipamento |
| `gain` | Ganho do amplificador em dB |
| `powerInput` | Potência de entrada em dBm (ou `N/A`) |
| `powerOutput` | Potência de saída em dBm (ou `N/A`) |
| `isLOS` | `true` se sem sinal óptico |
| `isAGC` | `true` se modo AGC (Automatic Gain Control) |

### Estatísticas de porta (`/onos/v1/statistics/ports/...`)
| Campo ONOS | Dado real |
|------------|-----------|
| `packetsReceived` | inputPower × 1000 (dBm preservado como long) |
| `packetsSent` | outputPower × 1000 |
| `packetsRxErrors` | fecErrors (contador real de erros FEC — só OTNTransponder) |

### Alarmes
Gerados automaticamente por `PadtecAlarmConsumer` com base nas anotações de porta:

| Condição | Severidade | Campo |
|----------|-----------|-------|
| Sem sinal óptico | **CRITICAL** | `isLOS=true` |
| Loss of Frame OTN | **MAJOR** | `isLOF=true` |
| Backward Defect Indicator | **MAJOR** | `isBDI=true` |
| Sem sinal na porta cliente | **MAJOR** | `isClientLOS=true` |
| Loss of Frame na porta cliente | **MINOR** | `isClientLOF=true` |
| Taxa de erro FEC alta (>1e-4) | **WARNING** | `fecRate` |

Visíveis em:
```bash
# ONOS 3.0.0-SNAPSHOT: endpoint registrado em "onos/dhcp" por bug de packaging Karaf
curl -s -u onos:rocks "http://localhost:8181/onos/dhcp/alarms?devId=padtec:172.17.36.50"
```

### PowerConfig (API óptica do ONOS)
Disponível para o plano de controle óptico via `PowerConfig`:
- `currentPower()` → `gain` (Amplifier) ou `outputPower`/`outputPowerWDM` (Transponder)
- `currentInputPower()` → `powerInput` (Amplifier) ou `inputPower`/`inputPowerWDM` (Transponder)

---

## Interface gráfica

Acesse: `http://172.17.36.231:8181/onos/ui` (usuário: `onos`, senha: `rocks`)

- **Devices** → `Padtec-SPVL4` aparece com portas ativas
- **Ports** → lista transponders com canal, status e potência em tempo real (refresh 60s)
- **Alarms** → alarmes LOS/LOF/BDI/FEC críticos quando um equipamento falha
- **Topology** → links ópticos Padtec↔Polatis (6 links bidirecionais ACTIVE)

---

## Estrutura do repositório

```
src/main/java/org/onosproject/drivers/padtec/
├── PadtecManager.java              # Lança PadtecMonitorJSON3 via ProcessBuilder
├── PadtecDeviceProvider.java       # Registra device + portas + refresh 60s + estatísticas
├── PadtecLinkProvider.java         # Injeta links ópticos Padtec↔Polatis no ONOS
├── PadtecDeviceDescription.java    # Driver behaviour: discoverPortDetails, discoverPortStatistics
├── PadtecAlarmConsumer.java        # 6 tipos de alarme lidos das anotações de porta
├── PadtecPowerConfig.java          # Potência real via anotações de porta (PowerConfig)
├── PadtecLambdaQuery.java          # Canal real → OchSignal correto (grade ITU-T 50GHz)
├── PadtecPortAdmin.java            # isEnabled() real via DeviceService
├── PadtecFlowRuleProgrammable.java # Somente leitura (sem flows)
├── PadtecLinkDiscovery.java        # Stub (links injetados pelo PadtecLinkProvider)
├── GnmiHandshaker.java             # DeviceHandshaker
└── PadtecDriversLoader.java

src/main/resources/
├── padtec-drivers.xml              # Declaração do driver e todos os behaviours
└── OSGI-INF/

Outros/TailEndController/
├── PadtecMonitorJSON3.java         # Processo Java 18: coleta contínua (loop 60s)
├── PadtecAgentServer.java          # Servidor TCP :10151 que serve o JSON ao ONOS
└── lib/                            # JARs proprietários Padtec (Java 18 + .so nativos)

tools/
├── coletar_padtec_onos.py          # Script Python para extração de dados via ONOS REST
├── padtec-links.example.json       # Topologia real verificada (Padtec↔Polatis)
├── add_cross_rest.py               # Cross-connects nos Polatis (reexecutar após reinício)
├── netconf-cfg1.json               # Config NETCONF Polatis-1 (172.17.36.21)
├── netconf-cfg2.json               # Config NETCONF Polatis-2 (172.17.36.22)
└── ...

setup_onos_lab.sh                   # Script de inicialização completo do lab
pom.xml                             # Build Maven (Java 11, OSGi bundle)
padtec-netcfg.json                  # Network config do device Padtec no ONOS
```

---

## Notas de implementação

### Por que o driver não importa a biblioteca Padtec diretamente

Há duas barreiras intransponíveis:
- **Java incompatível**: ONOS roda em Java 11 (OSGi Karaf). A lib Padtec exige Java 18 + arquivos `.so` nativos (JNI).
- **OSGi classloader**: O Karaf isola classloaders por bundle. JARs não-OSGi (`br.com.padtec.v3.*`) não podem ser carregados no container.

A solução é o **bridge TCP**: `PadtecMonitorJSON3` (Java 18) chama o SDK real e publica os dados via `PadtecAgentServer` (TCP :10151). O driver ONOS (Java 11) lê o JSON via socket.

### Bug ONOS 3.0.0-SNAPSHOT — endpoint de alarmes

O app `faultmanagement` no ONOS 3.0.0-SNAPSHOT registra o contexto REST em `onos/dhcp` em vez de `onos/v1` por um bug de packaging no Karaf. Use:
```
GET http://localhost:8181/onos/dhcp/alarms
```
em vez de `/onos/v1/alarms`.
