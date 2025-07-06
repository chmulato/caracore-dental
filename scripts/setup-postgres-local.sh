#!/bin/bash

# Script para configuração do banco PostgreSQL local
# Sistema Cara Core Agendamento (CCA)

echo "🏥 Configurando banco PostgreSQL local para CCA..."

# Verificar se o PostgreSQL está instalado
if ! command -v psql &> /dev/null; then
    echo "❌ PostgreSQL não está instalado."
    echo "📦 Instale o PostgreSQL primeiro:"
    echo "   - Ubuntu/Debian: sudo apt install postgresql postgresql-contrib"
    echo "   - CentOS/RHEL: sudo yum install postgresql postgresql-server"
    echo "   - macOS: brew install postgresql"
    echo "   - Windows: https://www.postgresql.org/download/windows/"
    exit 1
fi

# Verificar se o PostgreSQL está rodando
if ! systemctl is-active --quiet postgresql 2>/dev/null && ! brew services list | grep postgresql | grep started &>/dev/null; then
    echo "⚠️  PostgreSQL não está rodando."
    echo "🚀 Iniciando PostgreSQL..."
    
    # Tentar iniciar o PostgreSQL (diferentes sistemas)
    if command -v systemctl &> /dev/null; then
        sudo systemctl start postgresql
    elif command -v brew &> /dev/null; then
        brew services start postgresql
    else
        echo "❌ Não foi possível iniciar o PostgreSQL automaticamente."
        echo "   Inicie manualmente e execute este script novamente."
        exit 1
    fi
fi

echo "✅ PostgreSQL está rodando."

# Configurar variáveis
DB_NAME="cca_local"
DB_USER="cca_user"
DB_PASSWORD="cca_password"

echo "📝 Configuração:"
echo "   - Banco: $DB_NAME"
echo "   - Usuário: $DB_USER"
echo "   - Senha: $DB_PASSWORD"

# Verificar se o usuário existe
USER_EXISTS=$(sudo -u postgres psql -tAc "SELECT 1 FROM pg_roles WHERE rolname='$DB_USER'" 2>/dev/null)

if [ "$USER_EXISTS" = "1" ]; then
    echo "⚠️  Usuário '$DB_USER' já existe."
    read -p "🤔 Deseja recriar o usuário? (s/N): " RECREATE_USER
    if [[ $RECREATE_USER =~ ^[Ss]$ ]]; then
        echo "🗑️  Removendo usuário existente..."
        sudo -u postgres dropuser $DB_USER 2>/dev/null || true
    else
        echo "⏭️  Mantendo usuário existente."
    fi
fi

# Verificar se o banco existe
DB_EXISTS=$(sudo -u postgres psql -lqt | cut -d \| -f 1 | grep -qw $DB_NAME && echo "1" || echo "0")

if [ "$DB_EXISTS" = "1" ]; then
    echo "⚠️  Banco '$DB_NAME' já existe."
    read -p "🤔 Deseja recriar o banco? (s/N): " RECREATE_DB
    if [[ $RECREATE_DB =~ ^[Ss]$ ]]; then
        echo "🗑️  Removendo banco existente..."
        sudo -u postgres dropdb $DB_NAME 2>/dev/null || true
        DB_EXISTS="0"
    else
        echo "⏭️  Mantendo banco existente."
    fi
fi

# Criar usuário se necessário
if [ "$USER_EXISTS" != "1" ] || [[ $RECREATE_USER =~ ^[Ss]$ ]]; then
    echo "👤 Criando usuário $DB_USER..."
    sudo -u postgres createuser -d -r -s $DB_USER
    sudo -u postgres psql -c "ALTER USER $DB_USER PASSWORD '$DB_PASSWORD';"
    echo "✅ Usuário criado com sucesso."
fi

# Criar banco se necessário
if [ "$DB_EXISTS" = "0" ]; then
    echo "🏗️  Criando banco $DB_NAME..."
    sudo -u postgres createdb -O $DB_USER $DB_NAME
    echo "✅ Banco criado com sucesso."
fi

# Conceder permissões
echo "🔐 Configurando permissões..."
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;"

# Testar conexão
echo "🔍 Testando conexão..."
if PGPASSWORD=$DB_PASSWORD psql -h localhost -U $DB_USER -d $DB_NAME -c "SELECT version();" >/dev/null 2>&1; then
    echo "✅ Conexão testada com sucesso!"
else
    echo "❌ Erro ao testar conexão."
    echo "   Verifique as configurações e tente novamente."
    exit 1
fi

echo ""
echo "🎉 Configuração concluída!"
echo ""
echo "📋 Para usar o banco PostgreSQL local:"
echo "   mvn spring-boot:run -Dspring.profiles.active=local"
echo ""
echo "🔧 Variáveis de ambiente (opcional):"
echo "   export DB_USERNAME=$DB_USER"
echo "   export DB_PASSWORD=$DB_PASSWORD"
echo ""
echo "🌐 Conexão:"
echo "   Host: localhost"
echo "   Porta: 5432"
echo "   Banco: $DB_NAME"
echo "   Usuário: $DB_USER"
echo "   Senha: $DB_PASSWORD"
echo ""
echo "💻 Comando para conectar manualmente:"
echo "   psql -h localhost -U $DB_USER -d $DB_NAME"
echo ""
