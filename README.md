# Driver ONOS - Equipamentos Padtec SPVL4 (UFABC)

Este repositório contém o driver ONOS desenvolvido para a UFABC que se comunica de forma nativa com equipamentos ópticos Padtec (placa SPVL4, Transponders OTN, Amplificadores). A lógica de descoberta de equipamentos, antes executada por scripts isolados, foi totalmente reestruturada para o ecossistema moderno do ONOS (OSGi).

## Como a Arquitetura Funciona?

O projeto utiliza uma **Arquitetura de Middleware Externo via REST API**. 

Isso resolve os antigos conflitos de dependências ("Jar Hell") do motor OSGi (Karaf) do ONOS com o arquivo proprietário `install_gl.jar` da Padtec. 
* **O Middleware (`PadtecMiddleware.java`)**: Roda de forma independente (standalone) como um microsserviço no Linux, carregando as bibliotecas da Padtec (`br.ufabc.equipment.*`) e ouvindo chamadas HTTP.
* **O Driver ONOS (`PadtecDeviceDescription.java`)**: Fica acoplado dentro do ONOS, agindo como um cliente REST que consome os dados (FIBER/OCH, Ganhos e LOS) estruturados no padrão JSON gerados pelo Middleware.
* **Alertas do ONOS (`PadtecAlarmConsumer.java`)**: Fica acoplado dentro do ONOS como um cliente REST, injetando alertas (ex: Loss of Signal) na aba "Alarms" se a placa falhar.

---

## 1. Como Iniciar o Middleware (Conexão Física Padtec)

Antes do ONOS conectar no equipamento, você precisa colocar o servidor Middleware no ar, pois ele é quem conversa diretamente com a placa física e o `Supervisor`.

1. Abra um terminal na pasta `tools`.
2. Garanta que o arquivo `install_gl.jar` esteja lá dentro.
3. Compile e rode o servidor Java informando o classpath:

```bash
cd tools
javac -cp "install_gl.jar:." PadtecMiddleware.java
java -cp "install_gl.jar:." PadtecMiddleware
```
*(O console imprimirá: `Middleware Padtec rodando na porta 8080...`)*

---

## 2. Como Compilar o Driver do ONOS

Abra um NOVO terminal na raiz do projeto (onde está o arquivo `pom.xml`) e execute o Maven:

```bash
mvn clean install
```

*(O driver foi configurado para cravar o bytecode do compilador em Java 11 e ignorar travas de pacotes no OSGi `Import-Package`, garantindo um **BUILD SUCCESS** imediato).*

---

## 3. Como Subir o Laboratório Completo

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

## 4. Como Visualizar na Interface

Assim que o script finalizar o passo 5, o ONOS tentará se conectar à placa Padtec (ex: IP `172.17.36.50`).
O driver mandará uma requisição para o seu **Middleware (Terminal 1)**, que irá ler a placa física real, montar o JSON das portas e responder.

1. Acesse o painel do ONOS: `http://172.17.36.231:8181/onos/ui` (Usuário: `onos`, Senha: `rocks`).
2. Na aba **Devices**, o equipamento deverá aparecer listado como `TERMINAL_DEVICE` (Nome: `Padtec-SPVL4`).
3. Clique no equipamento e em seguida no botão de **Portas** para ver a lista de Amplificadores (FIBER) e Transponders (OCH) lidos em tempo real!
4. Na aba **Alarms**, se um Transponder físico ficar sem sinal (`LOS = true`), um triângulo crítico aparecerá no painel.

---

### Estrutura Final do Repositório

* `/src/main/java/.../padtec` -> Classes do Driver ONOS (Port/Device Discovery, PowerConfig, Handshaker, AlarmConsumer).
* `/tools` -> Scripts legados, Configurações JSON de Topologia (Polatis/Padtec), arquivo `install_gl.jar` e o servidor `PadtecMiddleware.java` que o integra.
* `setup_onos_lab.sh` -> O cérebro de implantação contínua (Shell script unificado).
* `pom.xml` -> Configuração do pacote OSGi otimizada para o Karaf (Java 11).