# UFABC Optical Lab Monitor — ONOS App

App ONOS que monitora o laboratório óptico multi-camada da UFABC em tempo real.

## O que faz

- **Coleta a cada 60s** de três fontes:
  - Agente Padtec TCP (`localhost:10151`) → transponders e amplificadores
  - OXC2 REST (`172.17.36.22:8008`) → cross-connects
  - ONOS services → flows PAV, links LLDP, disponibilidade Padtec

- **Dataset histórico** (últimas 24h = 1440 pontos) exportável como CSV

- **Dashboard HTML** acessível em `/onos/opticallab/ui`

- **REST API** com autenticação ONOS (`onos:rocks`)

## Endpoints

| Método | Path | Retorna |
|--------|------|---------|
| GET | `/onos/opticallab/ui` | Dashboard HTML |
| GET | `/onos/opticallab/api/status` | Snapshot atual (JSON) |
| GET | `/onos/opticallab/api/history?limit=N` | Últimos N pontos (JSON) |
| GET | `/onos/opticallab/api/dataset.csv` | Dataset completo (CSV download) |
| GET | `/onos/opticallab/api/info` | Informações do app |

## Build e Deploy

```bash
# No servidor ONOS (precisa de Maven 3.6+ e Java 11)
cd /home/sdn/onos27/drivers/padtec/git/UFABC_REDES
git checkout feat/optical-lab-gui
git pull

# Build + deploy em um comando:
bash opticallab-app/deploy.sh

# Ou passo a passo:
cd opticallab-app
mvn clean package -DskipTests
curl -X POST -H "content-type:application/octet-stream" \
     --data-binary @target/onos-app-opticallab-1.0.0.oar \
     -u onos:rocks \
     "http://localhost:8181/onos/v1/applications?activate=true"
```

## Verificar instalação

```bash
# Verificar se o app está ACTIVE
curl -s -u onos:rocks http://localhost:8181/onos/v1/applications/br.ufabc.opticallab

# Testar a API (aguardar ~10s após instalar para a primeira coleta)
curl -s -u onos:rocks http://localhost:8181/onos/opticallab/api/status | python3 -m json.tool

# Abrir dashboard no browser
xdg-open http://localhost:8181/onos/opticallab/ui
# ou no host Windows: http://<ip-servidor>:8181/onos/opticallab/ui
```

## Desinstalar

```bash
bash opticallab-app/deploy.sh --remove
# ou
curl -X DELETE -u onos:rocks \
     http://localhost:8181/onos/v1/applications/br.ufabc.opticallab
```

## Campos do Dataset CSV

```
timestamp, deviceName, type, channel, lambda,
inputPower, outputPower, inputPowerWDM, outputPowerWDM,
gain, powerInput, powerOutput, isAGC,
isLOS, isLOF, isBDI, isClientLOS, isClientLOF,
fecRate, fecErrors,
oxc2Pairs, pavFlowsAdded, lldpLinks
```

## Estrutura do projeto

```
opticallab-app/
├── pom.xml                              # Maven (ONOS 2.7.0)
├── deploy.sh                            # Script de build + deploy
└── src/main/
    ├── java/org/onosproject/opticallab/
    │   ├── OpticalLabApp.java           # @Component OSGi — ciclo de vida
    │   ├── OpticalLabCollector.java     # Coleta das 3 fontes
    │   ├── OpticalLabStore.java         # Buffer circular (24h)
    │   ├── DataPoint.java               # Modelo de dados + CSV/JSON
    │   └── OpticalLabWebResource.java   # REST API + Dashboard HTML
    └── resources/
        ├── app.xml                      # Metadados ONOS
        └── WEB-INF/web.xml              # Configuração JAX-RS
```

## Troubleshooting

| Sintoma | Causa provável | Solução |
|---------|---------------|---------|
| App não aparece em `/onos/v1/applications` | OAR inválido ou dependência faltando | Verificar `mvn package` sem erros |
| `/api/status` retorna 204 | Primeira coleta ainda não rodou | Aguardar 10s após instalar |
| Devices vazios | Agente Padtec não responde em 10151 | `bash tools/setup_onos_lab.sh` |
| Cross-connects vazios | OXC2 inacessível | `bash tools/fix_lab.sh` |
| Dashboard sem dados | Todos os anteriores | `bash tools/check_lab.sh` |
