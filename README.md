# Driver ONOS — Equipamentos Padtec SPVL4 (UFABC)

Driver ONOS para integração nativa com equipamentos ópticos Padtec (placa SPVL4, Transponders OTN, Amplificadores) no laboratório de redes ópticas da UFABC.

---

## Arquitetura

```
┌─────────────────────────────────────────────────────┐
│                    ONOS (Java 11)                   │
│                                                     │
│  PadtecManager          PadtecDeviceProvider        │
│  (OSGi Component)       (OSGi Component)            │
│       │                       │                     │
│       │ ProcessBuilder        │ Timer 45s           │
│       ▼                       ▼                     │
│  [Java 18 process]     injectDevice()               │
│  PadtecMonitorJSON3    updatePorts()                │
│  + PadtecAgentServer   updatePortStatistics()       │
│       │  TCP :10151           │ TCP :10151          │
│       └───────────────────────┘                     │
└─────────────────────────────────────────────────────┘
         │ SNMP/proprietário
         ▼
   Supervisor Padtec
   172.17.36.50:8886
```

**Fluxo completo:**
1. ONOS ativa → `PadtecManager` detecta se porta 10151 está livre
2. Se livre, lança `PadtecMonitorJSON3` via `ProcessBuilder` (Java 18, `/home/sdn/TailEndController`)
3. O monitor conecta ao hardware real (`172.17.36.50`) e inicia `PadtecAgentServer` na porta TCP 10151
4. Após 45s, `PadtecDeviceProvider` conecta ao agente, lê o JSON e registra o device + portas no ONOS
5. A cada 60s, estatísticas de porta são atualizadas automaticamente

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
3. Configura os switches Polatis via NETCONF
4. Instala o driver Padtec (`.oar`)
5. Injeta a topologia (`padtec-netcfg.json`)
6. Cria cross-connects nos Polatis (`add_cross_rest.py`)

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

## Verificação

```bash
# Device e anotações (lastCollected, supervisor)
curl -s -u onos:rocks \
  http://localhost:8181/onos/v1/devices/padtec:172.17.36.50 | python3 -m json.tool

# Portas com canal, inputPower, outputPower, isLOS
curl -s -u onos:rocks \
  http://localhost:8181/onos/v1/devices/padtec:172.17.36.50/ports | python3 -m json.tool

# Estatísticas de porta (atualiza a cada 60s)
curl -s -u onos:rocks \
  http://localhost:8181/onos/v1/statistics/ports/padtec:172.17.36.50 | python3 -m json.tool

# Alarmes LOS ativos
curl -s -u onos:rocks \
  "http://localhost:8181/onos/v1/alarms?devId=padtec:172.17.36.50" | python3 -m json.tool

# Log do monitor Java 18
tail -f /tmp/padtec_monitor.log
```

---

## Dados expostos no ONOS

### Device (`padtec:172.17.36.50`)
| Anotação | Exemplo |
|----------|---------|
| `lastCollected` | `2026-05-03_10-32-39` |
| `supervisor` | `172.17.36.50:8886` |

### Portas (OCH)
| Anotação | Descrição |
|----------|-----------|
| `neName` | Nome do equipamento (ex: `T100DCT-4GTT2L#2`) |
| `type` | `OTNTransponder` ou `Transponder` |
| `channel` | Canal DWDM (ex: `C28`, `C24`) |
| `isLOS` | `true` se sem sinal óptico |
| `inputPower` | Potência de entrada em dBm (ou `N/A`) |
| `outputPower` | Potência de saída em dBm (ou `N/A`) |
| `lambda` | Comprimento de onda em nm |

### Alarmes
- Gerado automaticamente quando `isLOS=true` → severidade **CRITICAL**
- Visível em `/onos/v1/alarms` e na aba **Alarms** da GUI

---

## Interface gráfica

Acesse: `http://172.17.36.231:8181/onos/ui` (usuário: `onos`, senha: `rocks`)

- **Devices** → `Padtec-SPVL4` aparece como `OPTICAL_AMPLIFIER` com 3 portas OCH
- **Ports** → lista os transponders com canal e status em tempo real
- **Alarms** → alarmes LOS críticos quando um transponder perde sinal

---

## Estrutura do repositório

```
src/main/java/org/onosproject/drivers/padtec/
├── PadtecManager.java            # Lança PadtecMonitorJSON3 via ProcessBuilder
├── PadtecDeviceProvider.java     # Registra device + portas + estatísticas no ONOS
├── PadtecDeviceDescription.java  # Driver: discoverPortDetails, discoverPortStatistics
├── PadtecAlarmConsumer.java      # Gera alarmes LOS a partir do JSON do hardware
├── PadtecLambdaQuery.java        # Lambdas disponíveis (banda C, grid 50 GHz)
├── PadtecFlowRuleProgrammable.java
├── PadtecDriversLoader.java
└── ...

src/main/resources/
├── padtec-drivers.xml            # Declaração do driver e behaviours
└── OSGI-INF/                     # Metadados OSGi gerados automaticamente

setup_onos_lab.sh                 # Script de inicialização completo do lab
pom.xml                           # Build Maven (Java 11, OSGi bundle)
```
