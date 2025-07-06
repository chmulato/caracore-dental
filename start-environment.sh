#!/bin/bash
# Script de conveniência para iniciar o ambiente CCA
# Chama o script completo localizado na pasta scripts

echo "Iniciando ambiente CCA..."
echo

# Obtém o diretório do script atual
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Chama o script completo
"$SCRIPT_DIR/scripts/start-docker.sh"
