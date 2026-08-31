#!/bin/bash
# Script de execução rápida da Clínica Veterinária

SCRIPT_DIR="$(cd "$(dirname "$(readlink -f "$0")")" && pwd)"
cd "$SCRIPT_DIR"

if [ -x "/home/gabriel/Applications/jdk21/bin/java" ]; then
    JAVA_CMD="/home/gabriel/Applications/jdk21/bin/java"
elif command -v java >/dev/null 2>&1; then
    JAVA_CMD="java"
else
    echo "Erro: Java não encontrado." >&2
    exit 1
fi

JAR_FILE="$SCRIPT_DIR/target/clinica-veterinaria-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Compilando e gerando pacote executável..."
    export JAVA_HOME=/home/gabriel/Applications/jdk21
    export PATH=$JAVA_HOME/bin:/home/gabriel/Applications/apache-maven-3.9.9/bin:$PATH
    mvn package -DskipTests
fi

echo "Iniciando Sistema de Clínica Veterinária..."
exec "$JAVA_CMD" -jar "$JAR_FILE" "$@"
