# Exemplos Práticos - Configuração de Ambientes

Este documento fornece exemplos práticos de como executar a aplicação em diferentes ambientes.

## Desenvolvimento Local

### 1. Executar com perfil padrão (dev)

```bash
# Usar configurações de desenvolvimento (padrão)
mvn spring-boot:run

# A aplicação iniciará em:
# - Porta: 8080
# - Banco: PostgreSQL local (localhost:5432/cca_db)
# - Logs: DEBUG habilitado
# - Swagger: http://localhost:8080/swagger-ui.html
```

### 2. Executar com banco H2 para testes

```bash
# Usar perfil de teste para desenvolvimento rápido
mvn spring-boot:run -Dspring-boot.run.profiles=test

# Características:
# - Banco H2 em memória
# - Console H2: http://localhost:8080/h2-console
# - Dados de teste carregados automaticamente
```

### 3. Configurar ambiente Docker

```bash
# Subir PostgreSQL com Docker
docker-compose up -d

# Verificar se o banco está rodando
docker ps

# Executar aplicação
mvn spring-boot:run
```

## Homologação

### 1. Preparar variáveis de ambiente

```bash
# Criar arquivo .env-homolog
cat > .env-homolog << EOF
DB_USERNAME=cca_homolog_user
DB_PASSWORD=senha_segura_homolog
SSL_KEYSTORE_PASSWORD=keystore_pass
ADMIN_EMAIL=admin-homolog@caracore.com.br
EOF

# Carregar variáveis
source .env-homolog
```

### 2. Executar com perfil de homologação

```bash
# Build da aplicação
mvn clean package -DskipTests

# Executar com perfil homolog
java -jar -Dspring.profiles.active=homolog target/cca-0.0.1-SNAPSHOT.jar

# Ou usando Maven
mvn spring-boot:run -Dspring-boot.run.profiles=homolog
```

### 3. Validar ambiente

```bash
# Verificar health check
curl http://localhost:8080/actuator/health

# Testar endpoint público
curl http://localhost:8080/public/api/dentistas

# Acessar Swagger
# http://localhost:8080/swagger-ui.html
```

## Produção

### 1. Preparar servidor de produção

```bash
# Criar usuário específico
sudo useradd -m -s /bin/bash cca-app

# Criar diretórios
sudo mkdir -p /opt/cca/config
sudo mkdir -p /opt/cca/logs
sudo chown -R cca-app:cca-app /opt/cca
```

### 2. Configurar variáveis de ambiente

```bash
# Criar arquivo de configuração de produção
sudo tee /opt/cca/config/application-prod.env << EOF
DB_USERNAME=cca_prod_user
DB_PASSWORD=senha_muito_segura_prod
SSL_KEYSTORE_PASSWORD=keystore_password_prod
ADMIN_EMAIL=admin@caracore.com.br
JAVA_OPTS="-Xms512m -Xmx2048m -server"
EOF

# Ajustar permissões
sudo chmod 600 /opt/cca/config/application-prod.env
sudo chown cca-app:cca-app /opt/cca/config/application-prod.env
```

### 3. Deploy da aplicação

```bash
# Fazer backup da versão anterior
sudo cp /opt/cca/cca-current.jar /opt/cca/cca-backup-$(date +%Y%m%d_%H%M%S).jar

# Copiar nova versão
sudo cp target/cca-0.0.1-SNAPSHOT.jar /opt/cca/cca-current.jar
sudo chown cca-app:cca-app /opt/cca/cca-current.jar

# Carregar variáveis e executar
sudo -u cca-app bash -c "
source /opt/cca/config/application-prod.env
cd /opt/cca
java \$JAVA_OPTS -Dspring.profiles.active=prod -jar cca-current.jar
"
```

### 4. Configurar service do systemd

```bash
# Criar arquivo de serviço
sudo tee /etc/systemd/system/cca.service << EOF
[Unit]
Description=Sistema de Agendamento Cara Core
After=network.target

[Service]
Type=simple
User=cca-app
Group=cca-app
WorkingDirectory=/opt/cca
EnvironmentFile=/opt/cca/config/application-prod.env
ExecStart=/usr/bin/java \${JAVA_OPTS} -Dspring.profiles.active=prod -jar /opt/cca/cca-current.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Habilitar e iniciar serviço
sudo systemctl daemon-reload
sudo systemctl enable cca
sudo systemctl start cca

# Verificar status
sudo systemctl status cca
```

## Monitoramento e Logs

### 1. Verificar logs em tempo real

```bash
# Desenvolvimento
tail -f logs/cca-application.log

# Produção com systemd
sudo journalctl -u cca -f

# Produção com arquivo
tail -f /opt/cca/logs/cca-application.log
```

### 2. Verificar saúde da aplicação

```bash
# Health check
curl http://localhost:8080/actuator/health

# Métricas (se habilitado)
curl http://localhost:8080/actuator/metrics

# Info da aplicação
curl http://localhost:8080/actuator/info
```

### 3. Monitorar banco de dados

```bash
# Verificar conexões PostgreSQL
sudo -u postgres psql -c "SELECT count(*) FROM pg_stat_activity;"

# Verificar status do Docker (se usar)
docker stats postgres-cca
```

## Troubleshooting Comum

### 1. Erro de conexão com banco

```bash
# Verificar se PostgreSQL está rodando
sudo systemctl status postgresql

# Verificar conectividade
telnet localhost 5432

# Testar credenciais
psql -h localhost -U cca_user -d cca_db -c "SELECT version();"
```

### 2. Porta já em uso

```bash
# Verificar processo usando a porta
lsof -i :8080

# Matar processo se necessário
kill -9 <PID>

# Ou usar porta diferente
java -jar -Dserver.port=8081 app.jar
```

### 3. Problemas de memória

```bash
# Verificar uso de memória
free -h

# Verificar processo Java
top -p $(pgrep java)

# Ajustar heap size
java -Xms512m -Xmx1024m -jar app.jar
```

### 4. SSL não funciona

```bash
# Verificar keystore
keytool -list -keystore classpath:keystore.p12 -storetype PKCS12

# Gerar novo keystore se necessário
keytool -genkeypair -alias cca -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650
```

## Scripts de Automação

### 1. Script de deploy para produção

```bash
#!/bin/bash
# deploy-prod.sh

set -e

echo "=== Deploy Produção CCA ==="

# Fazer backup
BACKUP_FILE="/opt/cca/backup/cca-$(date +%Y%m%d_%H%M%S).jar"
mkdir -p /opt/cca/backup
cp /opt/cca/cca-current.jar "$BACKUP_FILE"

# Parar serviço
sudo systemctl stop cca

# Deploy nova versão
cp target/cca-0.0.1-SNAPSHOT.jar /opt/cca/cca-current.jar
chown cca-app:cca-app /opt/cca/cca-current.jar

# Iniciar serviço
sudo systemctl start cca

# Verificar saúde
sleep 30
if curl -f http://localhost:8080/actuator/health; then
    echo "Deploy realizado com sucesso!"
else
    echo "Falha no deploy, restaurando backup..."
    sudo systemctl stop cca
    cp "$BACKUP_FILE" /opt/cca/cca-current.jar
    sudo systemctl start cca
    exit 1
fi
```

### 2. Script de monitoramento

```bash
#!/bin/bash
# monitor.sh

APP_URL="http://localhost:8080"
LOG_FILE="/var/log/cca-monitor.log"

while true; do
    if curl -f "$APP_URL/actuator/health" > /dev/null 2>&1; then
        echo "$(date): ✅ Aplicação OK" >> "$LOG_FILE"
    else
        echo "$(date): ❌ Aplicação com problemas" >> "$LOG_FILE"
        # Aqui você pode adicionar notificações (email, Slack, etc.)
    fi
    sleep 60
done
```

### 3. Script de limpeza de logs

```bash
#!/bin/bash
# cleanup-logs.sh

LOG_DIR="/opt/cca/logs"
RETENTION_DAYS=30

find "$LOG_DIR" -name "*.log" -type f -mtime +$RETENTION_DAYS -delete
find "$LOG_DIR/archived" -name "*.log" -type f -mtime +$RETENTION_DAYS -delete

echo "$(date): Limpeza de logs concluída" >> "$LOG_DIR/cleanup.log"
```

## Integração Contínua

### 1. Pipeline básico (exemplo para GitHub Actions)

```yaml
# .github/workflows/deploy.yml
name: Deploy CCA

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
    
    - name: Deploy to staging
      run: |
        # Comandos de deploy para homologação
        ./scripts/deploy-staging.sh
```

### 2. Testes automatizados por ambiente

```bash
# Testar ambiente de desenvolvimento
mvn test -Dspring.profiles.active=test

# Testar ambiente de homologação
mvn test -Dspring.profiles.active=homolog -Dtest.base-url=https://homolog-api.caracore.com.br

# Smoke tests em produção
curl -f https://api.caracore.com.br/actuator/health
```

## Backup e Recuperação

### 1. Backup do banco de dados

```bash
# Backup automático diário
pg_dump -h localhost -U cca_user cca_db > backup-$(date +%Y%m%d).sql

# Backup com compressão
pg_dump -h localhost -U cca_user cca_db | gzip > backup-$(date +%Y%m%d).sql.gz
```

### 2. Restauração

```bash
# Restaurar backup
psql -h localhost -U cca_user cca_db < backup-20250705.sql

# Restaurar backup comprimido
gunzip -c backup-20250705.sql.gz | psql -h localhost -U cca_user cca_db
```
