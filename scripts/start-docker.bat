@echo off
:: Script para iniciar os containers Docker necessários para o ambiente CCA
:: Utiliza o docker-compose.yml na raiz do projeto

echo Iniciando containers Docker para o ambiente CCA...
echo.

cd "%~dp0.."
echo Diretório atual: %CD%

echo Verificando docker-compose.yml...
if not exist docker-compose.yml (
    echo ERRO: Arquivo docker-compose.yml não encontrado!
    exit /b 1
)

echo Iniciando containers...
docker-compose up -d

echo.
echo Containers iniciados. Para parar, execute: docker-compose down
echo.
echo Iniciando aplicação com perfil local...
echo mvn spring-boot:run -Dspring.profiles.active=local
echo.
