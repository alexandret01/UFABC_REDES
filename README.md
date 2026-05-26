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
(T100DCT#2 WDM, canal C28)              (T100DCT#27 WDM, canal C28*)
                                        * C28 exigido — atualmente em C24 (pendente)
    │ OPTICAL                                    │ OPTICAL
    ▼                                            ▼
OXC2/Polatis porta 1 (ingress)          OXC2/Polatis porta 5 (ingress)
    │                                            │
    └──── cross-connect 1→13 ────────────────────┘ (TX do #2 → RX do #27)
    └──── cross-connect 5→9  ────────────────────┘ (TX do #27 → RX do #2)
    │                                            │
OXC2/Polatis porta 13 (egress)          OXC2/Polatis porta 9 (egress)
    │                                            │
    └──────── RX do T100DCT#27 ◄────────────────┘
    └──────── RX do T100DCT#2  ◄────────────────┘

Supervisor Padtec : 172.17.36.50:8886
OXC2 (Polatis)    : 172.17.36.22:830 (NETCONF) / 172.17.36.22:8008 (REST)
OXC1 (Polatis)    : 172.17.36.21 — COM DEFEITO (credenciais NETCONF desconhecidas)
```

### Fluxo de dados completo
```
DC5 → PAV1 → Padtec T100DCT#2 (cliente) → Padtec T100DCT#2 (WDM)
    → OXC2 (cross-connect 1→13 e 5→9)
    → Padtec T100DCT#27 (WDM) → Padtec T100DCT#27 (cliente) → PAV2 → DC6
```

---

## ✅ GUIA COMPLETO — Colocar o lab para funcionar (DC5 ↔ DC6 ping)

### Pré-requisito: estar na branch `main`

```bash
cd /home/sdn/onos27/drivers/padtec/git/UFABC_REDES
git stash            # salva arquivos locais não comitados
git checkout main
git pull origin main
git branch           # deve mostrar * main
```

> **Por que isso importa:** todos os scripts de correção (`fix_lab.sh`, `run_set_channel.sh`,
> `check_lab.sh`, `pav_setup.sh`) e o `setup_onos_lab.sh` corrigido **só existem na branch main**.
> O servidor historicamente ficava na branch `fix/lambda-query-guava` (versão antiga).

---

### PASSO 1 — Iniciar o laboratório

```bash
./setup_onos_lab.sh
```

Aguarda ~2 minutos. O script inicia: agente Padtec TCP, ONOS, apps necessárias,
keepalive OXC2. OXC2 **não** é registrado no ONOS (evita que o driver apague cross-connects).

---

### PASSO 2 — Verificar / corrigir estado do lab

```bash
bash tools/check_lab.sh
```

Diagnóstico em ~10 segundos. O que verificar:
- `✓ Keepalive` com PID e última iteração
- `✓ OXC2 xconn: 1→13, 5→9`
- `✓` nos dois transponders com `LOS=False BDI=False`
- `✓ LLDP: PAV1:51 ↔ PAV2:50` (prova que caminho óptico está transparente)
- **Sem** `⚠️ OXC2 NO ONOS` — se aparecer, execute `bash tools/fix_lab.sh`

Se algo estiver errado:
```bash
bash tools/fix_lab.sh
```

---

### PASSO 3 — Configurar canal C28 no T100DCT#27

O T100DCT#27 precisa estar em **C28** (mesmo canal que o T100DCT#2).
Após reinicializações, o equipamento volta ao canal salvo (C24 por padrão).

```bash
bash tools/TailEndController/run_set_channel.sh
```

O script copia o fonte para `Outros/TailEndController/` (onde estão os JARs do SDK),
compila, executa e limpa. Tenta setar C28 via PPMv3.

**Se o comando PPMv3 não funcionar** (setChannel marcado como "NOT WORKING" no SDK):
- Acesse **fisicamente o painel frontal do T100DCT#27** e mude o canal para C28.
- Salve a configuração para que persista após reinício.

Confirme o sucesso:
```bash
bash tools/check_lab.sh
# Deve mostrar: T100DCT#27: canal=C28  RX_WDM=-9.x dBm  LOS=False
```

---

### PASSO 4 — Habilitar portas dos switches PAV1/PAV2

A porta `te-1/1/49` (onde DC5/DC6 se conectam) tem `up-mode="false"` por padrão.
Com isso, o ONOS não vê o tráfego dos servidores e não aprende os hosts.

```bash
bash tools/pav_setup.sh
```

O script tenta SSH em PAV1 (`172.17.36.210`) e PAV2 (`172.17.36.211`) com senhas comuns
e executa `set interface te-1/1/49 up-mode true` via CLI XorPlus.

**Se SSH falhar**, acesse manualmente via console ou painel:
```
ssh admin@172.17.36.210    (PAV1)
configure
set interface te-1/1/49 up-mode true
set interface te-1/1/50 up-mode true
commit
```

---

### PASSO 5 — Configurar roteamento em DC5 e DC6

DC5 (`10.0.0.2/24`) e DC6 (`10.0.1.2/24`) estão em sub-redes diferentes.
É preciso adicionar rotas estáticas em ambos.

```bash
# Tentativas de SSH (senha conhecida: waldman ou outra)
ssh sdn@172.17.36.208    # DC5
ssh sdn@172.17.36.214    # DC6
```

Após conectar em **DC5**:
```bash
sudo ip route add 10.0.1.0/24 via 10.0.0.1 dev enp1s0
# Se não houver gateway: ip route add 10.0.1.2/32 dev enp1s0
```

Após conectar em **DC6**:
```bash
sudo ip route add 10.0.0.0/24 via 10.0.1.1 dev enp1s0
# Se não houver gateway: ip route add 10.0.0.2/32 dev enp1s0
```

**Alternativa mais simples:** mudar DC6 para a mesma sub-rede de DC5 (`10.0.0.3/24`).

---

### PASSO 6 — Testar ping

```bash
# No DC5:
ping -c 4 10.0.1.2

# Ou do optinet com traceroute para diagnóstico:
traceroute -n 10.0.1.2 -s 10.0.0.2
```

Sequência de eventos esperada no primeiro ping:
1. DC5 gera ARP → PAV1 recebe → ONOS aprende MAC de DC5
2. ONOS instala flow em PAV1 (in=porta49, out=porta51) e PAV2 (in=porta50, out=porta49)
3. ARP chega em DC6 → DC6 responde → ONOS aprende MAC de DC6
4. Flows bidirecional instalados → pings subsequentes fluem direto

---

### Diagnóstico rápido de problemas

| Sintoma | Causa provável | Solução |
|---|---|---|
| LOS/BDI nos transponders | Cross-connects apagados pelo ONOS | `bash tools/fix_lab.sh` |
| LOS nos transponders mas xconn OK | T100DCT#27 em C24 (não C28) | `bash tools/TailEndController/run_set_channel.sh` ou acesso físico |
| 0 hosts no ONOS | `up-mode=false` nas portas dos PAVs | `bash tools/pav_setup.sh` |
| Ping falha com "No route to host" | Rotas não configuradas em DC5/DC6 | Passos 5 acima |
| OXC2 no ONOS (4 devices) | `setup_onos_lab.sh` antigo ou `oxc2-display.json` aplicado | `bash tools/fix_lab.sh` |
| `fix_lab.sh` não encontrado | Branch errada (`fix/lambda-query-guava`) | `git checkout main && git pull` |

---

## Dispositivos no ONOS (3 gerenciados + OXC2 externo)

| Device ID | Tipo | Status | IP |
|---|---|---|---|
| `padtec:172.17.36.50` | Padtec SPVL4 | AVAILABLE | 172.17.36.50 |
| `of:5e3ec454441280b9` | Pica8 PAV1 | AVAILABLE | 172.17.36.210 |
| `of:5e3ec454443294fb` | Pica8 PAV2 | AVAILABLE | 172.17.36.211 |

> **OXC2 (172.17.36.22)**: gerenciado **diretamente via REST** pelo `keepalive_cross.py`,
> **fora do ONOS** para evitar que o driver `polatis-netconf` limpe os cross-connects durante
> sync periódico. Topologia usa links estáticos (`lab-topology.json`) para representar as
> conexões Padtec↔OXC2 no mapa do ONOS.

> **OXC1 (172.17.36.21)**: equipamento com defeito. Responde ping mas NETCONF falha com
> autenticação — credenciais desconhecidas. Não aparece no ONOS.

---

## Portas do Padtec (5 portas)

| Porta | Tipo | Equipamento | Função |
|---|---|---|---|
| 1 | OCH (WDM) | T100DCT-4GTT2L#2  | Enlace WDM → OXC2/porta 1 (ingress) |
| 2 | OCH (WDM) | T100DCT-4GTT2L#27 | Enlace WDM → OXC2/porta 5 (ingress) |
| 3 | OCH (WDM) | T25DC26-4BRE4L#10 | Amplificador (sem link ativo) |
| 4 | COPPER 10G | T100DCT-4GTT2L#2  | Cliente LR → PAV1/porta 49 |
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

keepalive_cross.py (processo independente, loop a cada 60s)
    └── PUT http://172.17.36.22:8008/api/data/optical-switch:cross-connects
           └── cross-connects 1→13 e 5→9 (persistentes via PUT)
```

**Por que bridge TCP?** A lib Padtec exige Java 18 + JNI (`.so` nativos), incompatível com o OSGi do ONOS (Java 11). O `PadtecMonitorJSON3` roda como processo Java 18 separado e expõe os dados via socket TCP.

**Por que keepalive externo?** O driver `polatis-netconf` do ONOS faz sync periódico com o OXC2 e apaga os cross-connects a cada ~8 minutos. A solução é remover OXC2 do ONOS e manter os cross-connects via `keepalive_cross.py` usando `PUT` (que persiste no datastore running do Polatis).

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
4. Registra OXC1 via NETCONF (`tools/netconf-cfg1.json`) — ficará UNAVAILABLE por defeito
5. Instala o driver Padtec (`.oar`)
6. Injeta configuração do device Padtec (`padtec-netcfg.json`)
7. Inicia `keepalive_cross.py` em background (aplica cross-connects OXC2 via PUT a cada 60s)
8. Injeta links estáticos da topologia (`tools/lab-topology.json`)

> **Importante:** OXC2 **não** é mais registrado via NETCONF no ONOS. Os cross-connects são
> gerenciados exclusivamente pelo `keepalive_cross.py`. Log em `/tmp/keepalive_cross.log`.

### Correção rápida (se cross-connects sumirem ou OXC2 voltou ao ONOS)

```bash
bash tools/fix_lab.sh
```

O script: remove OXC2 do ONOS → mata keepalives antigos → aplica PUT → inicia keepalive novo.

---

## Cross-connects OXC2 (gerenciamento externo)

O Polatis OXC2 tem dois datastores: **candidate** (temporário) e **running** (persistente).
- `POST` → candidate → **expirado em ~5 segundos** ❌
- `PUT`  → running  → **persiste indefinidamente** ✅

O driver NETCONF do ONOS, quando conectado ao OXC2, limpa o running periodicamente (~8 min).
Por isso o OXC2 é gerenciado fora do ONOS.

```bash
# Ver cross-connects ativos
curl -s -u admin:root \
  "http://172.17.36.22:8008/api/data/optical-switch:cross-connects" \
  -H "Accept: application/yang-data+json" | python3 -m json.tool

# Reaplicar manualmente (após reinício do Polatis)
python3 tools/add_cross_rest.py

# Iniciar keepalive em background
nohup python3 tools/keepalive_cross.py > /tmp/keepalive_cross.log 2>&1 &

# Ver log do keepalive
tail -f /tmp/keepalive_cross.log

# Descobrir em qual porta do OXC2 um transponder está conectado
python3 tools/scan_oxc2_ports.py
```

---

## Canal C28 — Configuração do T100DCT#27

Ambos os transponders precisam estar no **canal C28 (1554.94 nm / 193.8 THz)** para que o
link coerente faça lock. T100DCT#2 já está em C28. T100DCT#27 tem C24 salvo como padrão
e reverte para C24 após reinicializações.

### Método correto (via SDK Padtec — Java 18)

```bash
bash tools/TailEndController/run_set_channel.sh
# Para um transponder específico:
bash tools/TailEndController/run_set_channel.sh "T100DCT-4GTT2L#27" C28
```

> **Por que esse script?** O SDK Padtec usa classes em `Outros/TailEndController/lib/` e `br/`
> que **não estão no git** (são binários locais). O script copia `SetChannelC28.java` para
> aquela pasta, compila de lá (onde o compilador encontra as dependências) e executa.
> Compilar de outro diretório resulta em `package br.com.padtec.v3.data.ne does not exist`.

### Se o comando PPMv3 não funcionar

O método `setChannel()` do SDK está marcado como `// NOT WORKING` no código original.
Alternativas em ordem de preferência:

1. **Acesso físico** ao painel frontal do T100DCT#27 (mais confiável, persiste após reboot)
2. **SSH ao supervisor**: `bash tools/ssh_padtec.sh`
3. **Verificação apenas**: `python3 tools/set_padtec_channel.py --check`

### SSH ao supervisor Padtec

O servidor SSH do Padtec usa RSA-1024 (rejeitado por OpenSSH ≥ 8.8). Para conectar:

```bash
# Método 1: script wrapper (tenta múltiplas combinações)
bash tools/ssh_padtec.sh

# Método 2: manual com sshpass
sshpass -p admin ssh \
  -oHostKeyAlgorithms=+ssh-rsa \
  -oKexAlgorithms=+diffie-hellman-group14-sha1,diffie-hellman-group1-sha1 \
  -oCiphers=+aes128-cbc,3des-cbc \
  -oPubkeyAuthentication=no \
  admin@172.17.36.50

# Método 3: cliente SSH legado (se disponível)
ssh -oHostKeyAlgorithms=+ssh-rsa admin@172.17.36.50
```

Após conectar, os prováveis comandos CLI do supervisor Padtec:

```
show channel               # verificar canal atual
set channel C28            # ou: configure channel 28
set wavelength 1554.94     # alternativa por comprimento de onda
```

### Alternativa: acesso físico

Se SSH continuar bloqueado, configure o canal diretamente no **painel LCD do T100DCT#27**
ou via cabo console RS-232 (se disponível no equipamento).

---

## Estado do sinal óptico (verificado)

```bash
# Snapshot rápido: cross-connects + potência Padtec + alarmes
python3 tools/status_lab.py

# Watch contínuo (atualiza a cada 15s)
python3 tools/status_lab.py --watch
```

**Valores esperados com o caminho óptico estabelecido:**

| Porta | Equipamento | WDM Rx | isLOS |
|---|---|---|---|
| 1 | T100DCT#2  | ~-28 a -29 dBm | false |
| 2 | T100DCT#27 | ~-9 dBm        | false |

> **Nota:** A potência recebida na T100DCT#2 (-28 dBm) é baixa mas acima do threshold de LOS.
> A assimetria (34 dB de perda vs ~14 dB no outro sentido) sugere conector sujo ou cabo com
> maior atenuação no trecho OXC2/porta9 → T100DCT#2 RX.

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

## Extrair dados de monitoramento

### Snapshot completo (recomendado)
```bash
python3 tools/coletar_padtec_onos.py
# Com CSV:
python3 tools/coletar_padtec_onos.py --loop --intervalo 60 --csv /tmp/padtec.csv
# Só alarmes:
python3 tools/coletar_padtec_onos.py --alarmes
# JSON bruto:
python3 tools/coletar_padtec_onos.py --json
```

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
    los = ann.get('isLOS','?')
    bdi = ann.get('isBDI','?')
    print(f\"Porta {p['port']} {ann.get('neName','?'):25} isLOS={los} isBDI={bdi} | RX={rx} dBm | TX={tx} dBm\")
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
# Status completo (cross-connects + sinal + alarmes)
python3 tools/status_lab.py

# Devices no ONOS
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

# Cross-connects OXC2
curl -s -u admin:root \
  "http://172.17.36.22:8008/api/data/optical-switch:cross-connects" \
  -H "Accept: application/yang-data+json" | python3 -m json.tool

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

- **Devices** → 3 devices online (Padtec, PAV1, PAV2) + OXC2 representado via links estáticos
- **Ports** → 5 portas Padtec com canal, potência e status em tempo real
- **Links** → 4 links bidirecionais (2 OPTICAL + 2 DIRECT)
- **Alarms** → alarmes LOS/LOF/BDI/FEC quando há falha óptica

---

## Problemas conhecidos / Pendências

| Item | Status |
|---|---|
| **Canal T100DCT#27** | **⚠ CRÍTICO** — está em C24 (1551.72 nm), precisa ser C28 (1554.94 nm) para lock coerente com T100DCT#2 |
| OXC1 (172.17.36.21) com defeito | Credenciais NETCONF desconhecidas — HTTP 401 em todas as tentativas |
| OXC2 fora do ONOS | Driver `polatis-netconf` limpa cross-connects periodicamente; OXC2 gerenciado via REST externo |
| LOF nos transponders | Loss of Frame OTN — sinal óptico presente mas framing pendente; esperado enquanto os canais não fizerem lock coerente |
| T100DCT#2 sinal fraco | Rx ~-28 dBm no trecho OXC2/porta9→T100DCT#2 RX (atenuação ~34 dB); verificar conector |
| SSH ao supervisor Padtec | `ssh admin@172.17.36.50` falha com "Invalid key length" — servidor usa RSA-1024 rejeitado por OpenSSH ≥ 8.8. Ver `tools/ssh_padtec.sh` |
| Rota L3 DC5↔DC6 | Subnets 10.0.0.0/24 e 10.0.1.0/24 — requer `ip route add` nos servidores DC5/DC6 |
| FEC desabilitado | `fecRxEnabled=false` e `fecTxEnabled=false` nos transponders |

---

## Lições aprendidas — OXC2 Polatis REST API

| Método HTTP | Endpoint | Comportamento |
|---|---|---|
| `POST` | `/api/data/optical-switch:cross-connects` | Candidato (~5s) ❌ |
| `PUT`  | `/api/data/optical-switch:cross-connects` | Running (persiste) ✅ |
| `DELETE` | `/api/data/optical-switch:cross-connects` | Remove todos |

Body correto para `PUT`:
```json
{
  "optical-switch:cross-connects": {
    "pair": [
      {"ingress": 1, "egress": 13},
      {"ingress": 5, "egress": 9}
    ]
  }
}
```

Body para `POST` (diferente — sem namespace):
```json
{
  "pair": [
    {"ingress": 1, "egress": 13},
    {"ingress": 5, "egress": 9}
  ]
}
```

---

## Arquivos de configuração — conteúdo completo

### `padtec-netcfg.json` — registra o Padtec no ONOS

```json
{
  "devices": {
    "padtec:172.17.36.50": {
      "basic": {
        "name": "Padtec-SPVL4",
        "driver": "padtec"
      }
    }
  }
}
```

> ID do device: `padtec:172.17.36.50` (prefixo `padtec:` definido no driver).
> Driver: `padtec` (nome registrado em `app.xml` / `driver.xml` do OAR).

---

### `tools/netconf-cfg1.json` — OXC1 (COM DEFEITO)

```json
{
  "devices": {
    "netconf:172.17.36.21:830": {
      "netconf": {
        "ip": "172.17.36.21", "port": 830,
        "username": "root", "password": "root",
        "connect-timeout": 30, "reply-timeout": 30, "idle-timeout": 300
      },
      "basic": { "driver": "polatis-netconf", "name": "OXC1-DEFEITO" }
    }
  }
}
```

> Injetado no ONOS apenas para aparecer na topologia. Ficará UNAVAILABLE
> pois as credenciais reais do OXC1 são desconhecidas.

---

### `tools/netconf-cfg2.json` — OXC2 (REFERÊNCIA — NÃO injetar no ONOS)

```json
{
  "devices": {
    "netconf:172.17.36.22:830": {
      "netconf": {
        "ip": "172.17.36.22", "port": 830,
        "username": "admin", "password": "root",
        "connect-timeout": 30, "reply-timeout": 30, "idle-timeout": 300
      },
      "basic": { "driver": "polatis-netconf" }
    }
  }
}
```

> **NÃO usar** este arquivo com `setup_onos_lab.sh`. Se o ONOS conectar ao OXC2
> via NETCONF, o driver `polatis-netconf` vai limpar os cross-connects a cada ~8 min.
> O OXC2 é gerenciado exclusivamente pelo `keepalive_cross.py` via REST direto.

---

### `tools/lab-topology.json` — links estáticos da topologia

Define 4 links bidirecionais (8 entradas no total) entre os dispositivos:

| Link (origem → destino) | Tipo | Significado físico |
|---|---|---|
| `padtec/.../1 ↔ netconf:172.17.36.22:830/1` | OPTICAL | T100DCT#2 WDM ↔ OXC2 porta 1 |
| `padtec/.../2 ↔ netconf:172.17.36.22:830/5` | OPTICAL | T100DCT#27 WDM ↔ OXC2 porta 5 |
| `of:5e3ec454441280b9/49 ↔ padtec/.../4` | DIRECT | PAV1/porta49 ↔ T100DCT#2 cliente |
| `of:5e3ec454443294fb/49 ↔ padtec/.../5` | DIRECT | PAV2/porta49 ↔ T100DCT#27 cliente |

Links com `"allowed": false` (suprimidos):
- Padtec porta 1 ↔ OXC1/porta 1 (OXC1 com defeito)
- Padtec porta 2 ↔ OXC1/porta 2
- Padtec porta 3 ↔ OXC2/porta 1 (amplificador — sem rota ativa)

```bash
# Injetar (ou reinjetar) os links:
curl -X DELETE -u onos:rocks http://localhost:8181/onos/v1/network/configuration/links
sleep 1
curl -X POST -H "content-type:application/json" \
  http://localhost:8181/onos/v1/network/configuration \
  -d @tools/lab-topology.json -u onos:rocks
```

---

### IPs e credenciais de todos os dispositivos

| Dispositivo | IP | Porta | Protocolo | Usuário | Senha |
|---|---|---|---|---|---|
| Servidor optinet (ONOS) | 172.17.36.231 | 8181 | HTTP REST | `onos` | `rocks` |
| Padtec supervisor | 172.17.36.50 | 8886 | PPMv3/TCP (SDK) | — | — |
| Padtec SSH | 172.17.36.50 | 22 | SSH (RSA-1024) | `admin` | `admin`? |
| OXC2 Polatis REST | 172.17.36.22 | 8008 | HTTP REST | `admin` | `root` |
| OXC2 Polatis NETCONF | 172.17.36.22 | 830 | NETCONF/SSH | `admin` | `root` |
| OXC1 Polatis (DEFEITO) | 172.17.36.21 | 830 | NETCONF/SSH | desconhecido | desconhecido |
| PAV1 Pica8 | 172.17.36.210 | 6653 | OpenFlow | — | — |
| PAV2 Pica8 | 172.17.36.211 | 6653 | OpenFlow | — | — |
| DC5 | 172.17.36.208 | — | — | — | — |
| DC6 | 172.17.36.214 | — | — | — | — |
| Agente Padtec TCP | localhost | 10151 | TCP/JSON | — | — |

---

### Canais DWDM dos transponders

| Transponder | Canal atual | Canal necessário | Frequência | Comprimento de onda |
|---|---|---|---|---|
| T100DCT-4GTT2L#2  | C28 ✅ | C28 | 193.4 THz | 1554.94 nm |
| T100DCT-4GTT2L#27 | C24 ❌ | C28 | 193.4 THz | 1554.94 nm |

> Os dois precisam estar no **mesmo canal** para que o link coerente (QPSK/DP-QPSK) faça lock.

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
├── add_cross_rest.py               # Cross-connects OXC2 via PUT: 1→13 e 5→9
├── keepalive_cross.py              # Loop: re-aplica cross-connects a cada 60s
├── scan_oxc2_ports.py              # Descobre qual porta do OXC2 está ligada a cada transponder
├── fix_cross_persist.py            # Diagnóstico: testa POST vs PUT vs commit RESTCONF
├── fix_lab.sh                      # Correção completa do lab: remove OXC2 ONOS + reinicia keepalive
├── status_lab.py                   # Snapshot: cross-connects + sinal Padtec + alarmes
├── set_padtec_channel.py           # Tenta configurar canal C28 no Padtec (SSH/REST/ONOS)
├── ssh_padtec.sh                   # SSH ao supervisor Padtec com flags de compatibilidade RSA-1024
├── coletar_padtec_onos.py          # Coleta completa via ONOS REST (CSV/JSON/alarmes)
├── netconf-cfg1.json               # OXC1 (172.17.36.21) — COM DEFEITO
├── netconf-cfg2.json               # OXC2 (172.17.36.22) — referência (NÃO registrar no ONOS)
└── start_agent.sh                  # Inicia agente TCP manualmente

setup_onos_lab.sh                   # Setup completo do lab (ONOS + agente + keepalive + links)
padtec-netcfg.json                  # Network config do Padtec no ONOS
pom.xml                             # Build Maven (Java 11, OSGi)
```
