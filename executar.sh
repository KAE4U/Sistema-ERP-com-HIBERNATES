#!/usr/bin/env sh
# Compila (se necessario) e abre o Sistema ERP. Requer apenas Java 17+ instalado.
cd "$(dirname "$0")" || exit 1

if [ ! -f config.local.properties ]; then
  cp config.local.properties.example config.local.properties
  echo "Criei o arquivo config.local.properties."
  echo "Abra-o, troque SUA_SENHA pela senha do banco e rode este script de novo."
  exit 1
fi

if [ ! -f target/sistema-erp-hibernate.jar ]; then
  echo "Compilando o projeto (so na primeira vez)..."
  ./mvnw -q clean package -DskipTests || exit 1
fi

java -jar target/sistema-erp-hibernate.jar
