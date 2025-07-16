# Agendamento Público - Página Única - Cara Core CCA
# Script PowerShell para iniciar a aplicação

Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "   INICIANDO AGENDAMENTO PÚBLICO - PÁGINA ÚNICA" -ForegroundColor Yellow
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

Set-Location "c:\dev\cara-core_cca"

Write-Host "Limpando target e compilando..." -ForegroundColor Yellow
mvn clean compile -q

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erro na compilação" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host ""
Write-Host "✅ Compilação concluída com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "Iniciando aplicação..." -ForegroundColor Yellow
Write-Host ""
Write-Host "🌐 Acesse o agendamento público em:" -ForegroundColor Cyan
Write-Host "   http://localhost:8080/public/agendamento" -ForegroundColor White
Write-Host ""
Write-Host "📋 Novo formato de página única com accordion:" -ForegroundColor Cyan
Write-Host "   • Etapa 1: Dados Pessoais e Profissional" -ForegroundColor White
Write-Host "   • Etapa 2: Seleção de Data e Horário" -ForegroundColor White  
Write-Host "   • Etapa 3: Confirmação e Finalização" -ForegroundColor White
Write-Host ""
Write-Host "Pressione Ctrl+C para parar o servidor" -ForegroundColor Yellow
Write-Host ""

mvn spring-boot:run
