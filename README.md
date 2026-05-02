# Driver ONOS - Equipamentos Padtec SPVL4 (UFABC)

Este repositório contém o driver ONOS desenvolvido para a UFABC que se comunica de forma nativa com equipamentos ópticos Padtec (placa SPVL4, Transponders OTN, Amplificadores). A lógica de descoberta de equipamentos, antes executada por scripts isolados, foi totalmente reestruturada para o ecossistema moderno do ONOS (OSGi).

## Como a Arquitetura Funciona?

O projeto utiliza uma **Arquitetura Totalmente Integrada**. A lógica do `TailEndController` (incluindo o `PadtecMonitorJSON3` e o `PadtecAgentServer`) foi movida para dentro do próprio driver ONOS.

* **O `PadtecManager`**: Um componente OSGi que, ao ser ativado, inicia duas threads: uma para o `PadtecAgentServer` (servidor TCP na porta 10151) e outra para o `PadtecMonitorJSON3` (que se conecta ao `Supervisor` e alimenta o agente com dados).
* **O Driver ONOS (`PadtecDeviceDescription.java`)**: Fica acoplado dentro do ONOS e se comunica com o `PadtecAgentServer` via socket TCP na porta 10151, consumindo os dados em JSON e traduzindo-os para o modelo de portas do ONOS.

---

## 1. Como Compilar o Driver do ONOS

Abra um terminal na raiz do projeto (onde está o arquivo `pom.xml`) e execute o Maven:

```bash
mvn clean install
```

*(O driver foi configurado para embutir as dependências do `TailEndController` e para compilar com Java 11, garantindo um **BUILD SUCCESS** imediato).*

---

## 2. Como Subir o Laboratório Completo

Para não precisar rodar vários comandos na mão para levantar a rede (ONOS + Polatis + Padtec + Cross-connects), o projeto fornece um script automatizado (`setup_onos_lab.sh`).

No terminal raiz, apenas rode:

```bash
sudo ./setup_onos_lab.sh
```

**O que este script faz:**
1. Inicia o ONOS localmente via Bazel (`onos-local`).
2. Espera 90 segundos para a API REST do ONOS estabilizar.
3. Envia via cURL os JSONs com a configuração Netconf dos Switches Polatis (OXC 1 e 2).
4. Instala o Driver da Padtec recém-compilado (`.oar`) no ONOS via REST.
5. Injeta a topologia e o IP de gerência do Padtec (`padtec-netcfg.json`).
6. Executa o script Python (`add_cross_rest.py`) para criar as rotas cruzadas nos Polatis.

---

## 3. Como Visualizar na Interface

Assim que o script finalizar o passo 5, o `PadtecManager` iniciará o agente e o monitor. O driver ONOS então se conectará ao agente e começará a ler os dados.

1. Acesse o painel do ONOS: `http://172.17.36.231:8181/onos/ui` (Usuário: `onos`, Senha: `rocks`).
2. Na aba **Devices**, o equipamento deverá aparecer listado como `TERMINAL_DEVICE` (Nome: `Padtec-SPVL4`).
3. Clique no equipamento e em seguida no botão de **Portas** para ver a lista de Amplificadores (FIBER) e Transponders (OCH) lidos em tempo real!
4. Na aba **Alarms**, se um Transponder físico ficar sem sinal (`LOS = true`), um triângulo crítico aparecerá no painel.

---

### Estrutura Final do Repositório

* `/src/main/java/org/onosproject/drivers/padtec` -> Classes do Driver ONOS.
* `/src/main/java/br/ufabc/controlplane/metropad` -> Código do `TailEndController` integrado.
* `setup_onos_lab.sh` -> O cérebro de implantação contínua (Shell script unificado).
* `pom.xml` -> Configuração do Maven com as dependências do `TailEndController` embutidas.