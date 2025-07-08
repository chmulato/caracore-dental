#!/bin/bash
# Script para iniciar os containers Docker necessários para o ambiente CCA
# Utiliza o docker-compose.yml na raiz do projeto

echo "Iniciando containers Docker para o ambiente CCA..."
echo

# Volta para o diretório raiz do projeto
cd "$(dirname "$0")/.."
echo "Diretório atual: $(pwd)"

# Verifica se existe o arquivo docker-compose.yml
if [ ! -f "docker-compose.yml" ]; then
    echo "ERRO: Arquivo docker-compose.yml não encontrado!"
    exit 1
fi

# Inicia os containers
echo "Iniciando containers..."
docker-compose up -d

echo
echo "Containers iniciados. Para parar, execute: docker-compose down"
echo
echo "Iniciando aplicação com perfil local..."
echo "mvn spring-boot:run -Dspring.profiles.active=local"
