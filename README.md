# Driver ONOS — Equipamentos Padtec SPVL4 (UFABC)

Driver ONOS para integração nativa com equipamentos ópticos Padtec (placa SPVL4, Transponders OTN, Amplificadores) no laboratório de redes ópticas da UFABC.

---

## Arquitetura

```
┌────────────────────────────────────────────────────────────────┐
│                        ONOS (Java 11)                          │
│                                                                │
│  PadtecManager        PadtecDeviceProvider   PadtecLinkProvider│
│  (OSGi Component)     (OSGi Component)       (OSGi Component)  │
│       │                     │                      │           │
│       │ ProcessBuilder      │ Timer 45s            │ Timer 60s │
│       ▼                     ▼                      ▼           │
│  [Java 18 process]    injectDevice()         injectLinks()     │
│  PadtecMonitorJSON3   updatePorts()    (lê /home/sdn/          │
│  + PadtecAgentServer  updatePortStats() padtec-links.json)     │
│       │  TCP :10151         │ TCP :10151           │           │
│       └─────────────────────┘                      │           │
│                                                     ▼           │
│  PadtecDeviceDescription  (driver behaviours)  ONOS Topology   │
│  PadtecPowerConfig        (lê anotações reais  (links ópticos  │
│  PadtecAlarmConsumer       de porta)            Padtec↔Polatis)│
│  PadtecLambdaQuery                                             │
└────────────────────────────────────────────────────────────────┘
         │ SNMP/proprietário
         ▼
   Supervisor Padtec
   172.17.36.50:8886
```

**Fluxo completo:**
1. ONOS ativa → `PadtecManager` detecta se porta 10151 está livre
2. Se livre, lança `PadtecMonitorJSON3` via `ProcessBuilder` (Java 18, `/home/sdn/TailEndController`)
3. O monitor conecta ao hardware real (`172.17.36.50`) e inicia `PadtecAgentServer` na porta TCP 10151
4. Após 45s, `PadtecDeviceProvider` lê o JSON e registra device + portas no ONOS
5. A cada 60s: portas são **re-lidas** (atualiza potência/isLOS/canais) e estatísticas são atualizadas
6. Após 60s, `PadtecLinkProvider` injeta links ópticos Padtec↔Polatis (requer `padtec-links.json`)
7. `PadtecAlarmConsumer` gera alarmes CRITICAL para cada porta com `isLOS=true`
8. `PadtecPowerConfig` expõe potência real (dBm) para o plano de controle óptico do ONOS

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
3. Ativa apps necessárias (incl. `faultmanagement` para alarmes)
4. Configura os switches Polatis via NETCONF
5. Instala o driver Padtec (`.oar`)
6. Injeta a topologia (`padtec-netcfg.json`)
7. Cria cross-connects nos Polatis (`add_cross_rest.py`)

Após ~45s do ONOS subir, o equipamento Padtec aparece automaticamente com as portas reais.

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

## Configurar links ópticos Padtec ↔ Polatis

Para que a topologia do ONOS mostre os links físicos entre o Padtec e os Polatis:

### 1. Descubra os IDs dos Polatis no ONOS
```bash
curl -s -u onos:rocks http://localhost:8181/onos/v1/devices | python3 -m json.tool
# Procure por manufacturer "Polatis" e copie o campo "id"

# Veja as portas de cada Polatis:
curl -s -u onos:rocks http://localhost:8181/onos/v1/devices/DEVICE_ID/ports | python3 -m json.tool
```

### 2. Crie o arquivo de topologia
```bash
# Baseie-se no exemplo:
cp /home/sdn/onos27/drivers/padtec/git/UFABC_REDES/tools/padtec-links.example.json \
   /home/sdn/padtec-links.json

# Edite com os IDs reais:
nano /home/sdn/padtec-links.json
```

Formato:
```json
[
  { "padtecPort": 1, "remoteDevice": "netconf:192.168.1.10/830", "remotePort": 5 },
  { "padtecPort": 2, "remoteDevice": "netconf:192.168.1.10/830", "remotePort": 7 },
  { "padtecPort": 3, "remoteDevice": "netconf:192.168.1.11/830", "remotePort": 3 }
]
```

### 3. Reinstale o driver para recarregar os links
```bash
# (mesmo procedimento de atualização acima)
```

---

## Verificação

```bash
# Device e anotações (lastCollected, supervisor)
curl -s -u onos:rocks \
  http://localhost:8181/onos/v1/devices/padtec:172.17.36.50 | python3 -m json.tool

# Portas com canal, inputPower, outputPower, isLOS (atualiza a cada 60s)
curl -s -u onos:rocks \
  http://localhost:8181/onos/v1/devices/padtec:172.17.36.50/ports | python3 -m json.tool

# Estatísticas de porta (inputPower/outputPower × 1000 como contadores)
curl -s -u onos:rocks \
  http://localhost:8181/onos/v1/statistics/ports/padtec:172.17.36.50 | python3 -m json.tool

# Alarmes LOS ativos (requer faultmanagement ativo)
# NOTA: No ONOS 3.0.0-SNAPSHOT o faultmanagement registra o contexto em "onos/dhcp" por bug de empacotamento
curl -s -u onos:rocks \
  "http://localhost:8181/onos/dhcp/alarms?devId=padtec:172.17.36.50" | python3 -m json.tool

# Todos os alarmes (sem filtro)
curl -s -u onos:rocks http://localhost:8181/onos/dhcp/alarms | python3 -m json.tool

# Links ópticos injetados
curl -s -u onos:rocks \
  "http://localhost:8181/onos/v1/links?device=padtec:172.17.36.50" | python3 -m json.tool

# Ativar faultmanagement manualmente (se não ativado pelo setup_onos_lab.sh):
curl -u onos:rocks -X POST \
  http://localhost:8181/onos/v1/applications/org.onosproject.faultmanagement/active

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
| Anotação | Descrição |
|----------|-----------|
| `neName` | Nome do equipamento (ex: `T100DCT-4GTT2L#2`) |
| `type` | `OTNTransponder` |
| `channel` | Canal DWDM (ex: `C28`, `C24`) |
| `isLOS` | `true` se sem sinal óptico |

### Portas OCH — Transponder (T25DC*)
| Anotação | Descrição |
|----------|-----------|
| `neName` | Nome do equipamento |
| `type` | `Transponder` |
| `channel` | Canal DWDM |
| `isLOS` | `true` se sem sinal óptico |
| `inputPower` | Potência de entrada em dBm (ou `N/A`) |
| `outputPower` | Potência de saída em dBm (ou `N/A`) |
| `lambda` | Comprimento de onda em nm |

### Portas FIBER — Amplifier
| Anotação | Descrição |
|----------|-----------|
| `neName` | Nome do equipamento |
| `gain` | Ganho do amplificador em dB |
| `isLOS` | `true` se sem sinal óptico |
| `isAGC` | `true` se modo AGC (Automatic Gain Control) |
| `powerInput` | Potência de entrada em dBm (ou `N/A`) |
| `powerOutput` | Potência de saída em dBm (ou `N/A`) |

### Alarmes
- Gerado automaticamente quando `isLOS=true` → severidade **CRITICAL**
- Visível em `/onos/v1/alarms` e na aba **Alarms** da GUI
- Requer app `org.onosproject.faultmanagement` ativo

### PowerConfig (API óptica do ONOS)
Disponível para o plano de controle óptico do ONOS via `PowerConfig`:
- `currentPower()` → lê `gain` (Amplifier) ou `outputPower` (Transponder)
- `currentInputPower()` → lê `powerInput` (Amplifier) ou `inputPower` (Transponder)

---

## Interface gráfica

Acesse: `http://172.17.36.231:8181/onos/ui` (usuário: `onos`, senha: `rocks`)

- **Devices** → `Padtec-SPVL4` aparece como `OPTICAL_AMPLIFIER`
- **Ports** → lista os transponders com canal, status e potência em tempo real (refresh 60s)
- **Alarms** → alarmes LOS críticos quando um transponder perde sinal
- **Topology** → links ópticos Padtec↔Polatis (requer `padtec-links.json` configurado)

---

## Estrutura do repositório

```
src/main/java/org/onosproject/drivers/padtec/
├── PadtecManager.java            # Lança PadtecMonitorJSON3 via ProcessBuilder
├── PadtecDeviceProvider.java     # Registra device + portas + refresh 60s + estatísticas
├── PadtecLinkProvider.java       # Injeta links ópticos Padtec↔Polatis no ONOS
├── PadtecDeviceDescription.java  # Driver: discoverPortDetails, discoverPortStatistics
├── PadtecAlarmConsumer.java      # Alarmes LOS críticos (requer faultmanagement)
├── PadtecPowerConfig.java        # Potência real via anotações de porta (PowerConfig)
├── PadtecLambdaQuery.java        # Lambdas: canal real ou grade C completa (fallback)
├── PadtecPortAdmin.java          # isEnabled() real via DeviceService
├── PadtecFlowRuleProgrammable.java
├── PadtecDriversLoader.java
└── ...

src/main/resources/
├── padtec-drivers.xml            # Declaração do driver e behaviours (incl. AlarmConsumer)
└── OSGI-INF/                     # Metadados OSGi gerados automaticamente

tools/
├── padtec-links.example.json     # Exemplo de topologia Padtec↔Polatis (copie para /home/sdn/)
├── netconf-cfg1.json             # Config Polatis 1
├── netconf-cfg2.json             # Config Polatis 2
└── add_cross_rest.py             # Cross-connects Polatis

setup_onos_lab.sh                 # Script de inicialização completo do lab
pom.xml                           # Build Maven (Java 11, OSGi bundle)
padtec-netcfg.json                # Network config do device Padtec no ONOS
```
