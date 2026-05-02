#!/bin/bash
echo "==> Compilando e iniciando o Agente Padtec SIMPLES..."
cd "$(dirname "$0")"

# Usa o Java 11 padrão da máquina, sem dependências externas
javac PadtecAgent.java

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao compilar o PadtecAgent.java"
    exit 1
fi

echo "Compilação OK. Iniciando o servidor na porta 10151..."
java PadtecAgent
