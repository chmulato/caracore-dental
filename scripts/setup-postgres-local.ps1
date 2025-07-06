# Script PowerShell para configuração do banco PostgreSQL local
# Sistema Cara Core Agendamento (CCA)

Write-Host "🏥 Configurando banco PostgreSQL local para CCA..." -ForegroundColor Green

# Verificar se o PostgreSQL está instalado
if (-not (Get-Command psql -ErrorAction SilentlyContinue)) {
    Write-Host "❌ PostgreSQL não está instalado." -ForegroundColor Red
    Write-Host "📦 Instale o PostgreSQL primeiro:" -ForegroundColor Yellow
    Write-Host "   - Windows: https://www.postgresql.org/download/windows/" -ForegroundColor Cyan
    Write-Host "   - Chocolatey: choco install postgresql" -ForegroundColor Cyan
    Write-Host "   - Scoop: scoop install postgresql" -ForegroundColor Cyan
    exit 1
}

# Verificar se o PostgreSQL está rodando
$pgService = Get-Service -Name "*postgresql*" -ErrorAction SilentlyContinue | Where-Object { $_.Status -eq "Running" }
if (-not $pgService) {
    Write-Host "⚠️  PostgreSQL não está rodando." -ForegroundColor Yellow
    Write-Host "🚀 Tentando iniciar PostgreSQL..." -ForegroundColor Blue
    
    try {
        $pgService = Get-Service -Name "*postgresql*" | Select-Object -First 1
        Start-Service $pgService.Name
        Write-Host "✅ PostgreSQL iniciado com sucesso." -ForegroundColor Green
    }
    catch {
        Write-Host "❌ Não foi possível iniciar o PostgreSQL automaticamente." -ForegroundColor Red
        Write-Host "   Inicie manualmente e execute este script novamente." -ForegroundColor Yellow
        exit 1
    }
}

Write-Host "✅ PostgreSQL está rodando." -ForegroundColor Green

# Configurar variáveis
$DB_NAME = "cca_local"
$DB_USER = "cca_user"
$DB_PASSWORD = "cca_password"

Write-Host "📝 Configuração:" -ForegroundColor Blue
Write-Host "   - Banco: $DB_NAME" -ForegroundColor Cyan
Write-Host "   - Usuário: $DB_USER" -ForegroundColor Cyan
Write-Host "   - Senha: $DB_PASSWORD" -ForegroundColor Cyan

# Definir PGPASSWORD para comandos PostgreSQL
$env:PGPASSWORD = "postgres"  # Assumindo senha padrão do postgres

# Verificar se o usuário existe
try {
    $userExists = psql -h localhost -U postgres -d postgres -tAc "SELECT 1 FROM pg_roles WHERE rolname='$DB_USER'" 2>$null
    if ($userExists -eq "1") {
        Write-Host "⚠️  Usuário '$DB_USER' já existe." -ForegroundColor Yellow
        $recreateUser = Read-Host "🤔 Deseja recriar o usuário? (s/N)"
        if ($recreateUser -match "^[Ss]$") {
            Write-Host "🗑️  Removendo usuário existente..." -ForegroundColor Yellow
            dropuser -h localhost -U postgres $DB_USER 2>$null
            $userExists = $null
        }
        else {
            Write-Host "⏭️  Mantendo usuário existente." -ForegroundColor Blue
        }
    }
}
catch {
    Write-Host "⚠️  Erro ao verificar usuário. Tentando criar..." -ForegroundColor Yellow
    $userExists = $null
}

# Verificar se o banco existe
try {
    $dbExists = psql -h localhost -U postgres -lqt 2>$null | Select-String $DB_NAME
    if ($dbExists) {
        Write-Host "⚠️  Banco '$DB_NAME' já existe." -ForegroundColor Yellow
        $recreateDb = Read-Host "🤔 Deseja recriar o banco? (s/N)"
        if ($recreateDb -match "^[Ss]$") {
            Write-Host "🗑️  Removendo banco existente..." -ForegroundColor Yellow
            dropdb -h localhost -U postgres $DB_NAME 2>$null
            $dbExists = $null
        }
        else {
            Write-Host "⏭️  Mantendo banco existente." -ForegroundColor Blue
        }
    }
}
catch {
    Write-Host "⚠️  Erro ao verificar banco. Tentando criar..." -ForegroundColor Yellow
    $dbExists = $null
}

# Criar usuário se necessário
if (-not $userExists -or $recreateUser -match "^[Ss]$") {
    Write-Host "👤 Criando usuário $DB_USER..." -ForegroundColor Blue
    try {
        createuser -h localhost -U postgres -d -r -s $DB_USER
        psql -h localhost -U postgres -c "ALTER USER $DB_USER PASSWORD '$DB_PASSWORD';"
        Write-Host "✅ Usuário criado com sucesso." -ForegroundColor Green
    }
    catch {
        Write-Host "❌ Erro ao criar usuário: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Criar banco se necessário
if (-not $dbExists) {
    Write-Host "🏗️  Criando banco $DB_NAME..." -ForegroundColor Blue
    try {
        createdb -h localhost -U postgres -O $DB_USER $DB_NAME
        Write-Host "✅ Banco criado com sucesso." -ForegroundColor Green
    }
    catch {
        Write-Host "❌ Erro ao criar banco: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Conceder permissões
Write-Host "🔐 Configurando permissões..." -ForegroundColor Blue
try {
    psql -h localhost -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;"
    Write-Host "✅ Permissões configuradas." -ForegroundColor Green
}
catch {
    Write-Host "⚠️  Erro ao configurar permissões." -ForegroundColor Yellow
}

# Testar conexão
Write-Host "🔍 Testando conexão..." -ForegroundColor Blue
$env:PGPASSWORD = $DB_PASSWORD
try {
    $result = psql -h localhost -U $DB_USER -d $DB_NAME -c "SELECT version();" 2>$null
    if ($result) {
        Write-Host "✅ Conexão testada com sucesso!" -ForegroundColor Green
    }
    else {
        throw "Conexão falhou"
    }
}
catch {
    Write-Host "❌ Erro ao testar conexão." -ForegroundColor Red
    Write-Host "   Verifique as configurações e tente novamente." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🎉 Configuração concluída!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Para usar o banco PostgreSQL local:" -ForegroundColor Blue
Write-Host "   mvn spring-boot:run '-Dspring.profiles.active=local'" -ForegroundColor Cyan
Write-Host ""
Write-Host "🔧 Variáveis de ambiente (opcional):" -ForegroundColor Blue
Write-Host "   `$env:DB_USERNAME='$DB_USER'" -ForegroundColor Cyan
Write-Host "   `$env:DB_PASSWORD='$DB_PASSWORD'" -ForegroundColor Cyan
Write-Host ""
Write-Host "🌐 Conexão:" -ForegroundColor Blue
Write-Host "   Host: localhost" -ForegroundColor Cyan
Write-Host "   Porta: 5432" -ForegroundColor Cyan
Write-Host "   Banco: $DB_NAME" -ForegroundColor Cyan
Write-Host "   Usuário: $DB_USER" -ForegroundColor Cyan
Write-Host "   Senha: $DB_PASSWORD" -ForegroundColor Cyan
Write-Host ""
Write-Host "💻 Comando para conectar manualmente:" -ForegroundColor Blue
Write-Host "   psql -h localhost -U $DB_USER -d $DB_NAME" -ForegroundColor Cyan
Write-Host ""

# Limpar variável de ambiente
Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
