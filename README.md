# Driver ONOS - Equipamentos Padtec SPVL4 (UFABC)

Este repositório contém o driver ONOS desenvolvido para a UFABC que se comunica de forma nativa com equipamentos ópticos Padtec (placa SPVL4, Transponders OTN, Amplificadores). A lógica de descoberta de equipamentos, antes executada por scripts isolados, foi totalmente reestruturada para o ecossistema moderno do ONOS (OSGi).

## Como a Arquitetura Funciona?

O projeto utiliza uma **Arquitetura Integrada via Socket TCP Interno**. 

* **O Agente (TailEndController)**: Fica rodando no background na porta 10151 do seu servidor, acionando o hardware Padtec nativamente e exportando um JSON sempre que solicitado.
* **O Driver ONOS (`PadtecDeviceDescription.java`)**: Fica acoplado dentro do ONOS, e usa sockets puros para abrir uma conexão leve na porta 10151, puxar o JSON com os dados (FIBER/OCH, Ganhos e LOS) estruturados e fechar a conexão, desenhando o hardware nativamente na Topologia do ONOS.
* **Alertas do ONOS (`PadtecAlarmConsumer.java`)**: Compartilha do mesmo socket para ler flags de falha (ex: Loss of Signal) e registrar eventos críticos na aba "Alarms".

---

## 1. Como Subir o Laboratório Completo (Passo Único)

Para não precisar rodar vários comandos na mão para levantar a rede (Compilar Agente, ONOS + Polatis + Pica8 + Padtec + Cross-connects), o projeto fornece um script unificado e inteligente (`setup_onos_lab.sh`).

No terminal raiz do projeto, rode:

```bash
sudo ./setup_onos_lab.sh
```

**O que este script faz na ordem exata de dependência:**
1. Entra na pasta `tools/TailEndController/`, compila e roda o Agente Padtec (monitor.sh) em background na porta 10151.
2. Inicia o ONOS localmente via Bazel (`onos-local`).
3. Espera 90 segundos para a API REST do ONOS estabilizar.
4. Ativa as aplicações base essenciais do ONOS via REST (`org.onosproject.openflow`, `org.onosproject.netconf`, `org.onosproject.drivers.optical`, etc).
5. Injeta os JSONs com as credenciais e timeouts estendidos Netconf dos Switches Polatis (OXC 1 e 2).
6. Instala o pacote do Driver da Padtec (`onos-drivers-padtec-2.7.0.oar`) no ONOS (via REST).
7. Injeta o IP de gerência do Padtec na topologia (`padtec-netcfg.json`).
8. Executa o script Python (`add_cross_rest.py`) para criar as rotas cruzadas nos Polatis.

*(Se você fechar o script com Ctrl+C, ele usará um hook seguro para desligar o ONOS e também derrubar a thread do Agente Padtec, não deixando sujeira na memória).*

---

## 2. Como Visualizar na Interface

Após rodar o script e a mensagem de sucesso aparecer, o ONOS registrará o equipamento e buscará as portas via Socket (ele respeita o delay físico do equipamento ligar os sensores).

1. Acesse o painel do ONOS: `http://172.17.36.231:8181/onos/ui` (Usuário: `onos`, Senha: `rocks`).
2. Na aba **Devices**, você deverá ver os dispositivos (Pica8, Polatis e o Padtec-SPVL4).
3. Clique no equipamento Padtec e no botão de **Ports** para ver a lista de Amplificadores (FIBER) e Transponders (OCH) lidos em tempo real do laboratório!

---

## (Opcional) Como Recompilar o Driver do ONOS

Caso você faça alterações no código Java das pastas de `org.onosproject.drivers.padtec`, abra um terminal e execute o Maven antes de rodar o laboratório:

```bash
mvn clean install
```

*(O driver foi configurado no `pom.xml` para embutir corretamente a versão Java 11 no OSGi e ignorar validações estritas para pacotes do ONOS `3.0.0.SNAPSHOT`, garantindo um **BUILD SUCCESS** sem conflitos).*

---

### Estrutura Final do Repositório

* `/src/main/java/.../padtec` -> Classes do Driver ONOS (Port/Device Discovery, PowerConfig, Handshaker, AlarmConsumer).
* `/tools/TailEndController/` -> Lógica e classes originais do Agente Padtec.
* `/tools/` -> Configurações JSON de Topologia (Polatis/Padtec) e Scripts Python.
* `setup_onos_lab.sh` -> O cérebro de implantação contínua (Shell script unificado que inicializa o laboratório do zero).
* `pom.xml` -> Configuração do Maven otimizada para Karaf OSGi.