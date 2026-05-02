#!/bin/bash
# Script para iniciar o agente Padtec simples para testes (TCP 10151)

cd "$(dirname "$0")"

echo "Removendo .class antigos..."
rm -f SimplePadtecAgent.class

echo "Compilando SimplePadtecAgent..."
javac SimplePadtecAgent.java

if [ $? -eq 0 ]; then
    echo "Sucesso! Iniciando..."
    java SimplePadtecAgent
else
    echo "Erro na compilação do Agente Simples."
fi
