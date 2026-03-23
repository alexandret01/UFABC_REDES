# Driver ONOS - Equipamentos Padtec (Integração)

Este repositório contém o driver ONOS desenvolvido para a UFABC que se comunica com equipamentos ópticos Padtec (placa SPVL4, Transponders OTN).
A lógica de descoberta de equipamentos (originalmente em scripts bash/java do projeto "onos-devices") foi totalmente migrada para dentro do próprio ecossistema do ONOS.

## Como funciona?

Em vez de usar gNMI genérico, o Driver (na classe `PadtecDeviceDescription.java`) agora usa as bibliotecas nativas da Padtec (`br.ufabc.equipment.Supervisor`) para conectar-se diretamente ao IP de gerência dos equipamentos (ex: `172.17.36.50`).

Os scripts e arquivos JSON que antes ficavam espalhados agora estão contidos em uma única pasta: `/tools`.

## Como compilar?

Na raiz do projeto (onde está o `pom.xml`), execute:
```bash
mvn clean install
```

*(Obs: as bibliotecas `br.ufabc.*` e `br.com.padtec.*` estão "mockadas" [stubs] no código fonte apenas para permitir a compilação do driver neste ambiente de desenvolvimento isolado. Ao levar o projeto para o laboratório final, não esqueça de colocar os `.jar` reais na pasta do ONOS e excluir os stubs da pasta `src/main/java/br/`)*

## Como rodar e configurar o Laboratório?

Em vez de rodar múltiplos scripts manualmente, o projeto possui um script unificado que:
1. Inicia o ONOS;
2. Configura a rede dos switches Polatis;
3. Instala o Driver Padtec;
4. Cria os cross-connects (Python).

Basta executar:
```bash
./setup_onos_lab.sh
```

### Configurando Dispositivos Individualmente:
Você pode configurar os IPs dos equipamentos Padtec criando um arquivo JSON (baseado no `padtec-netcfg.json`) e enviando-o via ONOS:
```bash
onos-netcfg localhost padtec-netcfg.json
```

O dispositivo será registrado no ONOS com uma URI contendo seu IP de gerência (ex: `device:padtec:172.17.36.50`). A partir daí, o ONOS chamará o código internamente para listar os Amplificadores e Transponders como Portas Ópticas.