#!/bin/bash
# =============================================================================
# deploy.sh — Compila e instala o Optical Lab Monitor no ONOS.
#
# Pré-requisitos:
#   - Maven 3.6+ (mvn)
#   - ONOS rodando em localhost:8181 (onos:rocks)
#   - Java 11+
#
# Uso:
#   bash opticallab-app/deploy.sh           (build + deploy)
#   bash opticallab-app/deploy.sh --build   (só build, não instala)
#   bash opticallab-app/deploy.sh --remove  (desinstala o app do ONOS)
# =============================================================================

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
APP_NAME="br.ufabc.opticallab"
OAR_FILE="$SCRIPT_DIR/target/onos-app-opticallab-1.0.0.oar"
ONOS="http://localhost:8181"
AUTH="onos:rocks"
MODE="${1:-deploy}"

build() {
    echo "================================================================"
    echo "  Compilando opticallab-app com Maven..."
    echo "================================================================"
    cd "$SCRIPT_DIR"
    mvn clean package -DskipTests -q
    if [ $? -ne 0 ]; then
        echo "✗ BUILD FALHOU. Verifique os erros acima."
        exit 1
    fi
    echo "✓ Build concluído: $OAR_FILE"
}

deploy() {
    if [ ! -f "$OAR_FILE" ]; then
        echo "OAR não encontrado. Compilando primeiro..."
        build
    fi

    echo ""
    echo "================================================================"
    echo "  Instalando app no ONOS..."
    echo "================================================================"

    # Remove versão anterior se já existir (ONOS retorna 409 ao re-instalar mesma versão)
    EXISTING=$(curl -sS -o /dev/null -w "%{http_code}" -u "$AUTH" \
        "$ONOS/onos/v1/applications/$APP_NAME")
    if [ "$EXISTING" = "200" ]; then
        echo "  Removendo versão anterior..."
        curl -sS -o /dev/null -X DELETE -u "$AUTH" "$ONOS/onos/v1/applications/$APP_NAME"
        sleep 2
    fi

    CODE=$(curl -sS -o /tmp/deploy_result.txt -w "%{http_code}" \
        -X POST \
        -H "content-type:application/octet-stream" \
        --data-binary @"$OAR_FILE" \
        -u "$AUTH" \
        "$ONOS/onos/v1/applications?activate=true")

    if [ "$CODE" = "200" ] || [ "$CODE" = "201" ]; then
        echo "  ✓ App instalado e ativado (HTTP $CODE)"
    else
        echo "  ✗ Falha ao instalar (HTTP $CODE)"
        cat /tmp/deploy_result.txt
        exit 1
    fi

    sleep 2
    echo ""
    echo "  Status do app:"
    curl -s -u "$AUTH" "$ONOS/onos/v1/applications/$APP_NAME" | \
        python3 -c "
import sys,json
try:
    a=json.load(sys.stdin)
    print(f'  Nome:    {a.get(\"name\",\"?\")}'   )
    print(f'  Estado:  {a.get(\"state\",\"?\")}'  )
    print(f'  Versão:  {a.get(\"version\",\"?\")}'  )
except Exception as e:
    print(f'  Erro: {e}')
" 2>/dev/null

    echo ""
    echo "================================================================"
    echo "  Dashboard: http://$(hostname -I | awk '{print $1}'):9191/ui"
    echo "  REST API:  http://localhost:9191/status"
    echo "  Dataset:   http://localhost:9191/dataset.csv"
    echo "  (servidor HTTP embutido na porta 9191 — sem depender do pax-web ONOS)"
    echo "================================================================"
}

remove() {
    echo "Desinstalando $APP_NAME..."
    CODE=$(curl -sS -o /dev/null -w "%{http_code}" \
        -X DELETE -u "$AUTH" \
        "$ONOS/onos/v1/applications/$APP_NAME")
    [ "$CODE" = "204" ] && echo "  ✓ App removido" || echo "  ✗ HTTP $CODE"
}

case "$MODE" in
    --build)  build  ;;
    --remove) remove ;;
    *)        build && deploy ;;
esac
