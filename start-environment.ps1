# Script de conveniência para iniciar o ambiente CCA
# Chama o script completo localizado na pasta scripts

Write-Host "Iniciando ambiente CCA..." -ForegroundColor Green
Write-Host ""

# Obtém o diretório do script atual
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Definition

# Chama o script completo
& "$scriptPath\scripts\start-docker.ps1"
