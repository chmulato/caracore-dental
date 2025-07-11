# ====================================================================
# Cara Core Agendamento (CCA) - Script de Inicialização Windows
# Sistema de agendamento para consultórios odontológicos
# ====================================================================

param(
    [string]$Profile = "local",
    [switch]$SkipTests = $false,
    [switch]$CleanBuild = $false,
    [switch]$Help = $false
)

# Configurações
$APP_NAME = "Cara Core Agendamento (CCA)"
$JAVA_MIN_VERSION = "17"
$MAVEN_MIN_VERSION = "3.8"
$POSTGRES_DEFAULT_PORT = "5432"
$APP_PORT = "8080"

# Cores para output
$RED = "Red"
$GREEN = "Green"
$YELLOW = "Yellow"
$BLUE = "Cyan"
$RESET = "White"

function Write-ColorOutput {
    param([string]$Message, [string]$Color = "White")
    Write-Host $Message -ForegroundColor $Color
}

function Show-Help {
    Write-ColorOutput "============================================" $BLUE
    Write-ColorOutput " $APP_NAME - Script de Inicialização" $BLUE
    Write-ColorOutput "============================================" $BLUE
    Write-Host ""
    Write-ColorOutput "USO:" $YELLOW
    Write-Host "  .\start-environment.ps1 [OPÇÕES]"
    Write-Host ""
    Write-ColorOutput "OPÇÕES:" $YELLOW
    Write-Host "  -Profile <perfil>    Perfil do Spring (local, test, prod) [padrão: local]"
    Write-Host "  -SkipTests          Pular execução dos testes"
    Write-Host "  -CleanBuild         Executar limpeza completa antes do build"
    Write-Host "  -Help               Exibir esta ajuda"
    Write-Host ""
    Write-ColorOutput "EXEMPLOS:" $YELLOW
    Write-Host "  .\start-environment.ps1                    # Executar com perfil local"
    Write-Host "  .\start-environment.ps1 -Profile test     # Executar com perfil de teste"
    Write-Host "  .\start-environment.ps1 -CleanBuild       # Executar com limpeza completa"
    Write-Host "  .\start-environment.ps1 -SkipTests        # Executar sem testes"
    Write-Host ""
    Write-ColorOutput "PERFIS DISPONÍVEIS:" $YELLOW
    Write-Host "  local  - PostgreSQL local (requer banco configurado)"
    Write-Host "  test   - H2 in-memory (desenvolvimento rápido)"
    Write-Host ""
}

function Test-JavaVersion {
    try {
        $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString().Split('"')[1] }
        $majorVersion = $javaVersion.Split('.')[0]
        
        if ([int]$majorVersion -ge [int]$JAVA_MIN_VERSION) {
            Write-ColorOutput "✓ Java $javaVersion encontrado" $GREEN
            return $true
        } else {
            Write-ColorOutput "✗ Java $JAVA_MIN_VERSION+ requerido. Encontrado: $javaVersion" $RED
            return $false
        }
    } catch {
        Write-ColorOutput "✗ Java não encontrado. Instale Java $JAVA_MIN_VERSION+" $RED
        return $false
    }
}

function Test-MavenVersion {
    try {
        $mavenVersion = mvn --version 2>&1 | Select-String "Apache Maven" | ForEach-Object { $_.ToString().Split(' ')[2] }
        Write-ColorOutput "✓ Maven $mavenVersion encontrado" $GREEN
        return $true
    } catch {
        Write-ColorOutput "✗ Maven não encontrado. Instale Maven $MAVEN_MIN_VERSION+" $RED
        return $false
    }
}

function Test-PostgreSQL {
    if ($Profile -eq "local") {
        try {
            $pgResult = Test-NetConnection -ComputerName "localhost" -Port $POSTGRES_DEFAULT_PORT -WarningAction SilentlyContinue
            if ($pgResult.TcpTestSucceeded) {
                Write-ColorOutput "✓ PostgreSQL detectado na porta $POSTGRES_DEFAULT_PORT" $GREEN
                return $true
            } else {
                Write-ColorOutput "⚠ PostgreSQL não detectado. Usando H2 como fallback." $YELLOW
                return $false
            }
        } catch {
            Write-ColorOutput "⚠ Não foi possível verificar PostgreSQL. Usando H2 como fallback." $YELLOW
            return $false
        }
    }
    return $true
}

function Show-SystemInfo {
    Write-ColorOutput "============================================" $BLUE
    Write-ColorOutput " $APP_NAME" $BLUE
    Write-ColorOutput " Sistema de Inicialização" $BLUE
    Write-ColorOutput "============================================" $BLUE
    Write-Host ""
    Write-ColorOutput "Verificando pré-requisitos..." $YELLOW
    Write-Host ""
}

function Show-StartupInfo {
    Write-Host ""
    Write-ColorOutput "============================================" $GREEN
    Write-ColorOutput " SISTEMA INICIADO COM SUCESSO!" $GREEN
    Write-ColorOutput "============================================" $GREEN
    Write-Host ""
    Write-ColorOutput "🌐 Aplicação:" $BLUE
    Write-Host "   http://localhost:$APP_PORT"
    Write-Host ""
    Write-ColorOutput "👤 Usuários de Teste:" $BLUE
    Write-Host "   Admin:        suporte@caracore.com.br  / admin123"
    Write-Host "   Dentista:     dentista@caracore.com.br / admin123"
    Write-Host "   Recepcionista: recepcao@caracore.com.br / admin123"
    Write-Host "   Paciente:     paciente@caracore.com.br / admin123"
    Write-Host ""
    if ($Profile -eq "test" -or -not (Test-PostgreSQL)) {
        Write-ColorOutput "🗄️ Console H2:" $BLUE
        Write-Host "   http://localhost:$APP_PORT/h2-console"
        Write-Host "   JDBC URL: jdbc:h2:mem:testdb"
        Write-Host "   User: sa / Password: (vazio)"
    }
    Write-Host ""
    Write-ColorOutput "Para parar a aplicação, pressione Ctrl+C" $YELLOW
    Write-Host ""
}

# Função principal
function Start-Application {
    if ($Help) {
        Show-Help
        return
    }

    Show-SystemInfo

    # Verificar pré-requisitos
    $javaOk = Test-JavaVersion
    $mavenOk = Test-MavenVersion
    $postgresOk = Test-PostgreSQL

    if (-not ($javaOk -and $mavenOk)) {
        Write-Host ""
        Write-ColorOutput "❌ Pré-requisitos não atendidos. Instale as dependências necessárias." $RED
        return 1
    }

    # Ajustar perfil se PostgreSQL não estiver disponível
    if ($Profile -eq "local" -and -not $postgresOk) {
        Write-ColorOutput "Alterando para perfil 'test' (H2) devido à indisponibilidade do PostgreSQL." $YELLOW
        $Profile = "test"
    }

    Write-Host ""
    Write-ColorOutput "Configuração:" $BLUE
    Write-Host "  Perfil: $Profile"
    Write-Host "  Pular Testes: $SkipTests"
    Write-Host "  Limpeza: $CleanBuild"
    Write-Host ""

    try {
        # Executar limpeza se solicitado
        if ($CleanBuild) {
            Write-ColorOutput "Executando limpeza..." $YELLOW
            mvn clean
            if ($LASTEXITCODE -ne 0) {
                Write-ColorOutput "❌ Falha na limpeza" $RED
                return 1
            }
        }

        # Executar testes se não foi solicitado para pular
        if (-not $SkipTests) {
            Write-ColorOutput "Executando testes..." $YELLOW
            mvn test
            if ($LASTEXITCODE -ne 0) {
                Write-ColorOutput "❌ Falha nos testes" $RED
                return 1
            }
        }

        # Iniciar aplicação
        Write-ColorOutput "Iniciando aplicação..." $YELLOW
        Show-StartupInfo
        
        if ($Profile -eq "local") {
            mvn spring-boot:run -Dspring.profiles.active=local
        } else {
            mvn spring-boot:run -Dspring.profiles.active=$Profile
        }

    } catch {
        Write-ColorOutput "❌ Erro durante a execução: $($_.Exception.Message)" $RED
        return 1
    }
}

# Executar aplicação
Start-Application
