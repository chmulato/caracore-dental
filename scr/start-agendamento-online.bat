@echo off
echo.
echo ===============================================
echo    INICIANDO AGENDAMENTO PÚBLICO - PÁGINA ÚNICA
echo ===============================================
echo.

cd /d "c:\dev\cara-core_cca"

echo Limpando target e compilando...
call mvn clean compile -q

if %ERRORLEVEL% neq 0 (
    echo ❌ Erro na compilação
    pause
    exit /b 1
)

echo.
echo ✅ Compilação concluída com sucesso!
echo.
echo Iniciando aplicação...
echo.
echo 🌐 Acesse o agendamento público em:
echo    http://localhost:8080/public/agendamento
echo.
echo 📋 Novo formato de página única com accordion:
echo    • Etapa 1: Dados Pessoais e Profissional
echo    • Etapa 2: Seleção de Data e Horário  
echo    • Etapa 3: Confirmação e Finalização
echo.
echo Pressione Ctrl+C para parar o servidor
echo.

call mvn spring-boot:run
