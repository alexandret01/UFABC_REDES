#!/bin/bash
echo "==> Compilando PadtecMonitorJSON3 e dependencias estritas (Ignorando lixo do projeto)..."

# Monta o classpath dinamicamente com todas as bibliotecas da pasta lib/
CP="./lib/*:."

# Tenta encontrar o compilador Java 18 correto no Ubuntu
JAVAC_CMD="javac"
if [ -x "/usr/lib/jvm/jdk-18.0.2.1/bin/javac" ]; then
    JAVAC_CMD="/usr/lib/jvm/jdk-18.0.2.1/bin/javac"
elif [ -x "/usr/lib/jvm/java-18-openjdk-amd64/bin/javac" ]; then
    JAVAC_CMD="/usr/lib/jvm/java-18-openjdk-amd64/bin/javac"
fi

# Compila APENAS os dois arquivos principais.
# O -d . força a criação dos .class dentro da estrutura de pacotes correta (ex: br/ufabc/...)
$JAVAC_CMD -sourcepath . -d . -cp "$CP" PadtecMonitorJSON3.java PadtecAgentServer.java

if [ $? -eq 0 ]; then
    echo "==> Compilação concluída com SUCESSO!"
else
    echo "==> ERRO na compilação!"
fi
