#!/bin/bash
# Script auxiliar para compilar o PadtecMiddleware

echo "Procurando arquivos .jar na pasta tools e lib..."

# Constrói o classpath (incluindo o diretório atual, pasta src e jars)
CLASSPATH=".:install_gl.jar:../src/main/java"

# Adiciona todos os .jar do diretório atual, se existirem e não forem o install_gl
for jar in *.jar; do
    if [ "$jar" != "install_gl.jar" ]; then
        CLASSPATH="$CLASSPATH:$jar"
    fi
done

# Adiciona os .jar da pasta lib se ela existir
if [ -d "lib" ]; then
    for jar in lib/*.jar; do
        CLASSPATH="$CLASSPATH:$jar"
    done
fi

echo "Classpath gerado: $CLASSPATH"
echo "Compilando PadtecMiddleware.java..."

javac -cp "$CLASSPATH" PadtecMiddleware.java

if [ $? -eq 0 ]; then
    echo "Sucesso! Para iniciar o servidor, execute:"
    echo "java -cp \"$CLASSPATH\" PadtecMiddleware"
else
    echo "Erro na compilação. Verifique se todas as bibliotecas (dependencies) do install_gl.jar também estão na pasta tools."
fi
