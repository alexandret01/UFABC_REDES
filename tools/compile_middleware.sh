#!/bin/bash
# Script auxiliar para compilar o PadtecMiddleware

echo "Procurando arquivos .jar na pasta tools e lib..."

# Define o diretório raiz do projeto (assumindo que 'tools' está diretamente sob a raiz)
PROJECT_ROOT="$(dirname "$(pwd)")"

# Classpath para o compilador (javac) e runtime (java)
# Inclui o install_gl.jar e todos os jars na pasta lib
CLASSPATH="install_gl.jar"
if [ -d "lib" ]; then
    for jar in lib/*.jar; do
        CLASSPATH="$CLASSPATH:$jar"
    done
fi

# Adiciona o diretório atual para que o PadtecMiddleware.class seja encontrado no runtime
# e o diretório de classes compiladas do projeto principal (se houver)
RUNTIME_CLASSPATH=".:$CLASSPATH:$PROJECT_ROOT/target/classes"

echo "Classpath para javac/java: $CLASSPATH"
echo "Sourcepath para javac: $PROJECT_ROOT/src/main/java"
echo "Compilando PadtecMiddleware.java e suas dependências..."

# Remove arquivos .class antigos para garantir uma compilação limpa
rm -f *.class
rm -f lib/*.class # Remove classes compiladas na pasta lib, se houver

# Compila PadtecMiddleware.java e todos os arquivos .java em src/main/java/br/ufabc e src/main/java/br/com/padtec
# O -d . garante que as classes compiladas sejam colocadas no diretório atual (tools/)
javac -cp "$CLASSPATH" -sourcepath "$PROJECT_ROOT/src/main/java" -d . \
      PadtecMiddleware.java \
      "$PROJECT_ROOT/src/main/java/br/ufabc/equipment/Amplifiers.java" \
      "$PROJECT_ROOT/src/main/java/br/ufabc/equipment/Supervisor.java" \
      "$PROJECT_ROOT/src/main/java/br/ufabc/equipment/Transponders.java" \
      "$PROJECT_ROOT/src/main/java/br/com/padtec/v3/data/ne/NE.java" \
      "$PROJECT_ROOT/src/main/java/br/com/padtec/v3/data/ne/Amplifier.java" \
      "$PROJECT_ROOT/src/main/java/br/com/padtec/v3/data/ne/Transponder.java" \
      "$PROJECT_ROOT/src/main/java/br/com/padtec/v3/data/ne/TrpOTNTerminal.java"

if [ $? -eq 0 ]; then
    echo "Sucesso! Para iniciar o servidor, execute:"
    echo "java -cp \"$RUNTIME_CLASSPATH\" PadtecMiddleware"
else
    echo "Erro na compilação. Verifique se todas as bibliotecas (dependencies) do install_gl.jar e os arquivos .java em src/main/java estão corretos."
fi
