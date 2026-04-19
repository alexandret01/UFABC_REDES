# Driver ONOS - Equipamentos Padtec SPVL4 (UFABC)

Este repositório contém o driver ONOS desenvolvido para a UFABC que se comunica de forma nativa com equipamentos ópticos Padtec (placa SPVL4, Transponders OTN, Amplificadores). A lógica de descoberta de equipamentos, antes executada por scripts isolados, foi totalmente reestruturada para o ecossistema moderno do ONOS (OSGi).

## Como a Arquitetura Funciona?

O projeto utiliza uma **Arquitetura de Middleware Externo via REST API**. 

Isso resolve os antigos conflitos de dependências ("Jar Hell") do motor OSGi (Karaf) do ONOS com os arquivos proprietários (`install_gl.jar`, `snmp4j`, etc) da Padtec. O ONOS no laboratório roda a versão `3.0.0.SNAPSHOT` (compilada com Java 11).

* **O Middleware (`PadtecMiddleware.java`)**: Roda de forma independente (standalone) como um microsserviço no Linux, carregando as bibliotecas da Padtec (`br.ufabc.equipment.*` e `install_gl.jar`) e ouvindo chamadas HTTP na porta 8080.
* **O Driver ONOS (`PadtecDeviceDescription.java`)**: Fica acoplado dentro do ONOS, agindo como um cliente REST que consome os dados (FIBER/OCH, Ganhos e LOS) estruturados no padrão JSON gerados pelo Middleware.
* **Alertas do ONOS (`PadtecAlarmConsumer.java`)**: Fica acoplado dentro do ONOS como um cliente REST, injetando alertas (ex: Loss of Signal) na aba "Alarms" se a placa falhar.

---

## 1. Como Compilar o Driver do ONOS

Abra um terminal na raiz do projeto (onde está o arquivo `pom.xml`) e execute o Maven:

```bash
mvn clean install
```

*(O driver foi configurado no `pom.xml` para embutir corretamente a versão Java 11 no OSGi e ignorar validações estritas para pacotes do ONOS `3.0.0.SNAPSHOT`, garantindo um **BUILD SUCCESS** sem conflitos).*

---

## 2. Como Compilar e Iniciar o Middleware (Conexão Física Padtec)

Antes do ONOS conectar no equipamento, você precisa colocar o servidor Middleware no ar, pois ele é quem conversa diretamente com a placa física e o `Supervisor`.

1. Certifique-se de que o arquivo `install_gl.jar` esteja na pasta `tools/`.
2. Certifique-se de que exista a pasta `tools/lib/` contendo todas as bibliotecas auxiliares do projeto original (ex: `PadtecOptinet.jar`, `snmp4j.jar`, etc).
3. Abra um terminal na pasta `tools/`, dê permissão e rode o script de compilação atualizado (que encontra todas as classes dependentes):

```bash
cd tools
chmod +x compile_middleware.sh
./compile_middleware.sh
```

4. Se compilar com sucesso, o script imprimirá o comando exato que você deve copiar e colar no terminal para iniciar o servidor.

```bash
# Exemplo gerado pelo script:
java -cp ".:install_gl.jar:lib/PadtecOptinet.jar:../target/classes" PadtecMiddleware
```
*(O console imprimirá: `Middleware Padtec rodando na porta 8080...` e ficará bloqueado ouvindo conexões)*

---

## 3. Como Subir o Laboratório Completo

Para não precisar rodar vários comandos na mão para levantar a rede (ONOS + Polatis + Pica8 + Padtec + Cross-connects), o projeto fornece um script unificado (`setup_onos_lab.sh`).

Em um NOVO terminal (mantendo o Middleware rodando no terminal anterior), vá para a raiz do projeto e rode:

```bash
sudo ./setup_onos_lab.sh
```

**O que este script faz na ordem:**
1. Inicia o ONOS localmente via Bazel (`onos-local`).
2. Espera 90 segundos para a API REST do ONOS subir totalmente.
3. Ativa as aplicações base essenciais do ONOS via REST (`org.onosproject.openflow`, `org.onosproject.netconf`, `org.onosproject.drivers.optical`, etc).
4. Injeta os JSONs com as credenciais e timeouts estendidos Netconf dos Switches Polatis (OXC 1 e 2).
5. Instala o pacote do Driver da Padtec recém-compilado (`onos-drivers-padtec-2.7.0.oar`) no ONOS.
6. Injeta o IP de gerência do Padtec na topologia (`padtec-netcfg.json`).
7. Executa o script Python (`add_cross_rest.py`) para criar as rotas cruzadas nos Polatis.

---

## 4. O Segredo dos 30 Segundos e Visualização na Interface

**Atenção:** Assim que o script `setup_onos_lab.sh` mostrar "Setup concluído com sucesso", o ONOS estará ocupado conectando e lendo a árvore de dados dos switches Polatis (o que pode demorar e gerar logs de `Failed to get netconf data tree` que são normais enquanto estabiliza).

Para não competir com os Polatis, a classe `PadtecDeviceProvider` possui um **timer embutido de 30 segundos**. 

1. Aguarde **30 a 40 segundos**.
2. Observe o log do ONOS. Aparecerá: `Injetando equipamento Padtec padtec:172.17.36.50 no core do ONOS...`
3. Imediatamente após, observe o terminal do seu **Middleware**. O ONOS enviará uma requisição REST e o Middleware fará a ponte com o Supervisor.
4. Acesse o painel do ONOS: `http://172.17.36.231:8181/onos/ui` (Usuário: `onos`, Senha: `rocks`).
5. Na aba **Devices**, você deverá ver os **5 dispositivos** (2 Pica8, 2 Polatis e o Padtec-SPVL4).
6. Clique no equipamento Padtec e no botão de **Ports** para ver a lista de Amplificadores (FIBER) e Transponders (OCH) lidos em tempo real do laboratório!

---

### Estrutura Final do Repositório

* `/src/main/java/.../padtec` -> Classes do Driver ONOS (Provider, Port/Device Discovery, PowerConfig, AlarmConsumer).
* `/tools` -> Pasta independente. Contém o servidor `PadtecMiddleware.java`, o compilador inteligente, e as configurações JSON de Topologia (Polatis/Padtec).
* `/tools/lib/` -> Bibliotecas dependentes do `install_gl.jar`.
* `setup_onos_lab.sh` -> O cérebro de implantação contínua (Shell script unificado que inicializa o laboratório do zero).
* `pom.xml` -> Configuração do Maven otimizada para Karaf OSGi Java 11 (maven-bundle-plugin v4.2.1) suportando bibliotecas do ONOS v3.0.0.SNAPSHOT.