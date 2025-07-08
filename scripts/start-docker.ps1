# Script para iniciar os containers Docker necessários para o ambiente CCA
# Utiliza o docker-compose.yml na raiz do projeto

Write-Host "Iniciando containers Docker para o ambiente CCA..." -ForegroundColor Green
Write-Host ""

# Volta para o diretório raiz do projeto
Set-Location -Path "$PSScriptRoot\.."
Write-Host "Diretório atual: $(Get-Location)" -ForegroundColor Cyan

# Verifica se existe o arquivo docker-compose.yml
if (-not (Test-Path "docker-compose.yml")) {
    Write-Host "ERRO: Arquivo docker-compose.yml não encontrado!" -ForegroundColor Red
    exit 1
}

# Inicia os containers
Write-Host "Iniciando containers..." -ForegroundColor Yellow
docker-compose up -d

Write-Host ""
Write-Host "Containers iniciados. Para parar, execute: docker-compose down" -ForegroundColor Green
Write-Host ""
Write-Host "Iniciando aplicação com perfil local..." -ForegroundColor Cyan
Write-Host "mvn spring-boot:run -Dspring.profiles.active=local" -ForegroundColor White
