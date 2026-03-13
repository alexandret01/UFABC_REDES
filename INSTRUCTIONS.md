# Instruções de Instalação e Uso - Driver Padtec ONOS

## 1. Compilação

Na raiz do projeto (`D:/Projetos/UFABC/UFABC_REDES`), execute:

```bash
mvn clean install
```

Isso irá gerar o arquivo `target/onos-drivers-padtec-2.7.0.oar`.

## 2. Instalação no ONOS

Com o ONOS rodando, execute:

```bash
onos-app localhost install! target/onos-drivers-padtec-2.7.0.oar
```

## 3. Configuração da Rede

Edite o arquivo `padtec-netcfg.json` com o IP correto do seu equipamento Padtec SPVL4. Em seguida, envie a configuração para o ONOS:

```bash
onos-netcfg localhost padtec-netcfg.json
```

## 4. Verificação

No CLI do ONOS (`onos localhost`), verifique se o dispositivo apareceu e se as portas foram descobertas:

```bash
devices
ports
```

Para ver as estatísticas (se o equipamento estiver respondendo gNMI):

```bash
portstats device:padtec:1
```

## 5. Troubleshooting

Se o dispositivo não conectar, verifique os logs do ONOS:

```bash
log:display | grep padtec
```
