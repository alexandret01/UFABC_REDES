#!/bin/bash
# =============================================================================
# run_set_channel.sh — Compila e executa SetChannelC28 no ambiente correto.
#
# PROBLEMA: O SDK Padtec (lib/ e br/) reside em Outros/TailEndController/.
# Esses arquivos NÃO estão no git (são binários/gerados).
# Este script copia SetChannelC28.java para lá, compila, executa e limpa.
#
# Uso:
#   bash tools/TailEndController/run_set_channel.sh
#   bash tools/TailEndController/run_set_channel.sh "T100DCT-4GTT2L#27" C28
# =============================================================================

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
REPO_ROOT="$( cd "$SCRIPT_DIR/../.." && pwd )"
SDK_DIR="$REPO_ROOT/Outros/TailEndController"
SRC="$SCRIPT_DIR/SetChannelC28.java"
JAVA="/usr/lib/jvm/jdk-18.0.2.1/bin/java"
JAVAC="/usr/lib/jvm/jdk-18.0.2.1/bin/javac"

echo "============================================================"
echo "  run_set_channel.sh — Configuração de canal Padtec"
echo "  SDK dir: $SDK_DIR"
echo "============================================================"

# Verifica pré-requisitos
if [ ! -f "$SRC" ]; then
    echo "ERRO: $SRC não encontrado."
    echo "Você está na branch main? Execute: git checkout main && git pull"
    exit 1
fi

if [ ! -d "$SDK_DIR/lib" ]; then
    echo "ERRO: $SDK_DIR/lib não encontrado."
    echo "O SDK Padtec precisa estar em Outros/TailEndController/lib/"
    exit 1
fi

if [ ! -x "$JAVAC" ]; then
    JAVAC=$(which javac 2>/dev/null || echo "javac")
    JAVA=$(which java 2>/dev/null || echo "java")
fi

# Copia o fonte para o SDK dir (onde estão lib/ e br/)
echo "[1/3] Copiando SetChannelC28.java para $SDK_DIR ..."
cp "$SRC" "$SDK_DIR/SetChannelC28.java"

# Compila DE DENTRO do SDK dir (assim encontra lib/* e br/ com classes Padtec)
echo "[2/3] Compilando..."
cd "$SDK_DIR"
$JAVAC -cp "./lib/*:." SetChannelC28.java
echo "  -> Compilado com sucesso."

# Executa
echo "[3/3] Executando SetChannelC28 $@..."
echo ""
$JAVA -Djava.library.path=./lib/ -cp "./lib/*:." SetChannelC28 "$@"
RC=$?

# Limpa o .java copiado (deixa o .class para reuso)
rm -f "$SDK_DIR/SetChannelC28.java"

echo ""
echo "============================================================"
[ $RC -eq 0 ] && echo "  Resultado: SUCESSO (canal configurado ou já correto)" \
             || echo "  Resultado: FALHA (rc=$RC) — veja mensagem acima"
echo "============================================================"
exit $RC
