@echo off
REM ====================================================================
REM Cara Core Agendamento (CCA) - Script de Inicialização Windows BAT
REM Sistema de agendamento para consultórios odontológicos
REM ====================================================================

setlocal enabledelayedexpansion

REM Configurações
set APP_NAME=Cara Core Agendamento (CCA)
set APP_PORT=8080

echo ============================================
echo  %APP_NAME%
echo  Script de Inicialização Simplificado
echo ============================================
echo.

REM Verificar se estamos no diretório correto
if not exist "pom.xml" (
    echo [ERRO] pom.xml não encontrado. Execute este script do diretório raiz do projeto.
    pause
    exit /b 1
)

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Java não encontrado. Instale Java 17+
    pause
    exit /b 1
) else (
    echo [OK] Java encontrado
)

REM Verificar Maven
mvn --version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Maven não encontrado. Instale Maven 3.8+
    pause
    exit /b 1
) else (
    echo [OK] Maven encontrado
)

echo.
echo Iniciando aplicação...
echo.
echo ============================================
echo  SISTEMA INICIADO COM SUCESSO!
echo ============================================
echo.
echo Aplicação: http://localhost:%APP_PORT%
echo.
echo Usuários de Teste:
echo   Admin:         suporte@caracore.com.br  / admin123
echo   Dentista:      dentista@caracore.com.br / admin123
echo   Recepcionista: recepcao@caracore.com.br / admin123
echo   Paciente:      paciente@caracore.com.br / admin123
echo.
echo Console H2: http://localhost:%APP_PORT%/h2-console
echo   JDBC URL: jdbc:h2:mem:testdb
echo   User: sa / Password: (vazio)
echo.
echo Para parar a aplicação, pressione Ctrl+C
echo.

REM Executar aplicação
mvn spring-boot:run

pause
