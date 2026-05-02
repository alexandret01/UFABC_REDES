#!/bin/bash
echo "==> Compilando todas as classes Java do projeto TailEndController (PadtecAgent)"

# Monta o classpath dinamicamente com todas as bibliotecas da pasta lib/
CP="./lib/*:."

# Encontra todos os arquivos .java dentro de br/ e da raiz da pasta e os compila de uma só vez
/usr/lib/jvm/jdk-18.0.2.1/bin/javac -cp "$CP" $(find . -name "*.java")

if [ $? -eq 0 ]; then
    echo "==> Compilação concluída com SUCESSO!"
else
    echo "==> ERRO na compilação!"
fi
