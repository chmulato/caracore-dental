# Scripts de Configuração - CCA

Esta pasta contém scripts utilitários para configuração e manutenção do sistema CCA.

## 🐘 Setup PostgreSQL Local

Scripts para configurar rapidamente um banco PostgreSQL local para desenvolvimento.

### Linux/macOS

```bash
# Tornar executável
chmod +x scripts/setup-postgres-local.sh

# Executar
./scripts/setup-postgres-local.sh
```

### Windows (PowerShell)

```powershell
# Executar como Administrador
.\scripts\setup-postgres-local.ps1
```

### O que os scripts fazem:

1. ✅ **Verificam** se PostgreSQL está instalado e rodando
2. 🏗️ **Criam** banco `cca_local` 
3. 👤 **Criam** usuário `cca_user` com senha `cca_password`
4. 🔐 **Configuram** permissões adequadas
5. 🔍 **Testam** a conexão
6. 📋 **Exibem** informações de uso

### Após executar o script:

```bash
# Executar aplicação com PostgreSQL local
mvn spring-boot:run -Dspring.profiles.active=local
```

## 🔧 Configuração Manual (Alternativa)

Se preferir configurar manualmente:

```sql
-- Conectar como postgres
psql -U postgres

-- Criar usuário
CREATE USER cca_user WITH PASSWORD 'cca_password';
ALTER USER cca_user CREATEDB;

-- Criar banco
CREATE DATABASE cca_local OWNER cca_user;
GRANT ALL PRIVILEGES ON DATABASE cca_local TO cca_user;

-- Sair
\q
```

## 🌐 Variáveis de Ambiente (Opcionais)

```bash
# Linux/macOS
export DB_USERNAME=cca_user
export DB_PASSWORD=cca_password

# Windows (PowerShell)
$env:DB_USERNAME='cca_user'
$env:DB_PASSWORD='cca_password'

# Windows (CMD)
set DB_USERNAME=cca_user
set DB_PASSWORD=cca_password
```

## 📊 Verificar Configuração

```bash
# Testar conexão
psql -h localhost -U cca_user -d cca_local -c "SELECT version();"

# Ver bancos disponíveis
psql -h localhost -U cca_user -l

# Conectar ao banco
psql -h localhost -U cca_user -d cca_local
```

## 🐳 Alternativa com Docker

Se preferir usar Docker em vez de PostgreSQL nativo:

```bash
# Executar PostgreSQL com Docker
docker run --name cca-postgres \
  -e POSTGRES_DB=cca_local \
  -e POSTGRES_USER=cca_user \
  -e POSTGRES_PASSWORD=cca_password \
  -p 5432:5432 \
  -d postgres:15

# Verificar se está rodando
docker ps

# Parar o container
docker stop cca-postgres

# Iniciar novamente
docker start cca-postgres
```

## 🎯 Perfis de Aplicação

| Perfil | Banco | Descrição |
|--------|-------|-----------|
| `dev` (padrão) | H2 (memória) | Desenvolvimento rápido |
| `local` | PostgreSQL local | Desenvolvimento com BD real |
| `homolog` | PostgreSQL remoto | Homologação |
| `prod` | PostgreSQL produção | Produção |
| `test` | H2 (memória) | Testes automatizados |

## 🚀 Comandos de Execução

```bash
# H2 (padrão)
mvn spring-boot:run

# PostgreSQL local
mvn spring-boot:run -Dspring.profiles.active=local

# Homologação
mvn spring-boot:run -Dspring.profiles.active=homolog

# Produção
mvn spring-boot:run -Dspring.profiles.active=prod
```

## 🛠️ Troubleshooting

### PostgreSQL não inicia

```bash
# Linux/Ubuntu
sudo systemctl start postgresql
sudo systemctl enable postgresql

# macOS
brew services start postgresql

# Windows
# Usar Services.msc ou pgAdmin
```

### Erro de conexão

1. Verificar se PostgreSQL está rodando na porta 5432
2. Verificar firewall/antivírus
3. Conferir credenciais
4. Testar conexão manual com psql

### Erro de permissão

```sql
-- Conceder permissões adicionais
GRANT ALL PRIVILEGES ON DATABASE cca_local TO cca_user;
ALTER USER cca_user CREATEDB;
ALTER USER cca_user SUPERUSER;
```

---

Para mais informações sobre configuração de ambientes, consulte:
- `doc/APPLICATION_YML_GUIDE.md`
- `doc/CONFIGURACAO_AMBIENTES.md`
