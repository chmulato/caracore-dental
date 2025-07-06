# Configuração dos arquivos application.yml

Este documento explica a estrutura e configuração dos arquivos `application.yml` para o projeto CCA (Cara Core Agendamento).

## Estrutura dos Arquivos

### 1. application.yml (Principal)

Arquivo principal que contém configurações comuns para todos os ambientes:

- **Configurações do servidor** (porta, context-path, error handling)
- **Configurações do Spring** (profiles, JPA, internacionalização)
- **Configurações do Swagger/OpenAPI** (documentação da API)
- **Configurações de logging** (padrões, arquivos, rotação)
- **Configurações do Flyway** (migração de banco)

### 2. application-dev.yml (Desenvolvimento)

Configurações específicas para o ambiente de desenvolvimento com H2:

- **Banco de dados**: H2 Database (em memória) - `jdbc:h2:mem:devdb`
- **Credenciais**: `sa` / (sem senha)
- **JPA**: `ddl-auto: create-drop` para recriar schema
- **Logging**: Nível DEBUG para debugging
- **Swagger**: Habilitado para desenvolvimento
- **DevTools**: Habilitado para hot-reload
- **H2 Console**: Habilitado em `/h2-console`
- **Cache**: Desabilitado
- **Mocks**: Habilitados para serviços externos

### 3. application-local.yml (Desenvolvimento Local com PostgreSQL)

Configurações para desenvolvimento local usando PostgreSQL:

- **Banco de dados**: PostgreSQL local (`localhost:5432/cca_local`)
- **Credenciais**: `cca_user` / `cca_password` (via variáveis de ambiente)
- **JPA**: `ddl-auto: update` para permitir alterações automáticas
- **Flyway**: Habilitado para migrations
- **Logging**: Nível DEBUG detalhado
- **Swagger**: Habilitado com try-it-out
- **DevTools**: Habilitado para hot-reload
- **Serviços externos**: Configuráveis via variáveis de ambiente
- **Actuator**: Endpoints expandidos para monitoramento

### 4. application-homolog.yml (Homologação)

Configurações para ambiente de homologação:

- **Banco de dados**: PostgreSQL remoto com variáveis de ambiente
- **JPA**: `ddl-auto: validate` - apenas validação
- **Logging**: Nível INFO, menos verboso
- **Swagger**: Habilitado mas com limitações
- **Cache**: Habilitado (Caffeine)
- **Variáveis de ambiente**: Suporte para configuração externa

### 4. application-prod.yml (Produção)

Configurações otimizadas e seguras para produção:

- **Banco de dados**: PostgreSQL com pool otimizado
- **JPA**: `ddl-auto: validate` - sem alterações automáticas
- **Logging**: Nível WARN, logs em arquivo rotativo
- **Swagger**: DESABILITADO por segurança
- **Cache**: Habilitado e otimizado
- **Segurança**: Headers de segurança, HTTPS obrigatório
- **Monitoramento**: Actuator com Prometheus

### 5. application-test.yml (Testes)

Configurações para execução de testes:

- **Banco de dados**: H2 em memória
- **JPA**: `ddl-auto: create-drop` para recriar schema
- **Logging**: Nível WARN para não poluir logs
- **Swagger**: Desabilitado
- **Mocks**: Habilitados para todos os serviços externos
- **Porta**: Aleatória para evitar conflitos

## Configuração do Banco de Dados

### Desenvolvimento com H2 (Perfil dev)

O H2 não requer configuração adicional - é criado automaticamente em memória.

### Desenvolvimento Local com PostgreSQL (Perfil local)

Para configurar o banco PostgreSQL localmente:

```bash
# Criar banco de dados
createdb cca_local

# Criar usuário
psql -c "CREATE USER cca_user WITH PASSWORD 'cca_password';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE cca_local TO cca_user;"

# Configurar variáveis de ambiente (opcional)
export DB_USERNAME=cca_user
export DB_PASSWORD=cca_password
```
psql -c "GRANT ALL PRIVILEGES ON DATABASE cca_dev TO cca_dev;"
```

### Alternativa com Docker

```bash
docker run --name cca-postgres-dev \
  -e POSTGRES_DB=cca_dev \
  -e POSTGRES_USER=cca_dev \
  -e POSTGRES_PASSWORD=cca_dev_password \
  -p 5432:5432 -d postgres:13
```

## Variáveis de Ambiente

### Homologação

```bash
export DB_USERNAME=cca_homolog
export DB_PASSWORD=your_homolog_password
export WHATSAPP_API_URL=https://api-homolog.whatsapp.com
export WHATSAPP_API_KEY=your_homolog_api_key
export JWT_SECRET=your_homolog_jwt_secret
```

### Produção

```bash
export DB_HOST=prod-db.caracore.com.br
export DB_PORT=5432
export DB_NAME=cca_prod
export DB_USERNAME=cca_prod
export DB_PASSWORD=your_production_password
export WHATSAPP_API_URL=https://api.whatsapp.com
export WHATSAPP_API_KEY=your_production_api_key
export JWT_SECRET=your_production_jwt_secret
export SMTP_HOST=smtp.gmail.com
export SMTP_USERNAME=your_email@gmail.com
export SMTP_PASSWORD=your_email_password
```

## Executando com Perfis Específicos

### Linha de Comando

```bash
# Desenvolvimento com H2 (padrão)
mvn spring-boot:run

# Desenvolvimento com PostgreSQL local
mvn spring-boot:run -Dspring.profiles.active=local

# Homologação
mvn spring-boot:run -Dspring.profiles.active=homolog

# Produção
mvn spring-boot:run -Dspring.profiles.active=prod

# Testes
mvn test
```

### Docker

```bash
# Desenvolvimento
docker run -e SPRING_PROFILES_ACTIVE=dev your-app

# Homologação
docker run -e SPRING_PROFILES_ACTIVE=homolog your-app

# Produção
docker run -e SPRING_PROFILES_ACTIVE=prod your-app
```

## Segurança e Boas Práticas

### 1. Senhas e Secrets

- **Nunca** commitar senhas ou secrets no código
- Usar variáveis de ambiente ou Azure Key Vault
- Rodar scripts de geração de senhas seguras

### 2. Configurações por Ambiente

- **Desenvolvimento**: Permissivo para facilitar desenvolvimento
- **Homologação**: Intermediário para testes
- **Produção**: Restritivo e seguro

### 3. Logging

- **Desenvolvimento**: Logs detalhados para debugging
- **Produção**: Logs mínimos para performance e segurança
- **Rotação**: Configurada para evitar crescimento excessivo

### 4. Monitoramento

- **Homologação**: Métricas básicas
- **Produção**: Métricas completas com Prometheus
- **Saúde**: Endpoints de health check

## Troubleshooting

### Erro de Conexão com Banco

```bash
# Verificar se o PostgreSQL está rodando
pg_isready -h localhost -p 5432

# Verificar logs da aplicação
tail -f logs/cca-application.log
```

### Erro de Autenticação

```bash
# Verificar usuário e senha
psql -h localhost -U cca_dev -d cca_dev
```

### Swagger não aparece

- Verificar se o perfil tem Swagger habilitado
- Acessar: http://localhost:8080/swagger-ui.html
- Verificar logs para erros de configuração

## Arquivos Relacionados

- `src/main/resources/application.yml` - Configurações principais
- `src/main/resources/application-{profile}.yml` - Configurações específicas
- `src/main/resources/logback-spring.xml` - Configurações de logging
- `src/main/resources/db/migration/` - Scripts de migração
- `doc/CONFIGURACAO_AMBIENTES.md` - Guia detalhado de configuração
- `doc/SWAGGER_README.md` - Documentação do Swagger
- `doc/EXEMPLOS_CONFIGURACAO.md` - Exemplos práticos

## Próximos Passos

1. Configurar banco de dados local
2. Testar aplicação em cada perfil
3. Configurar variáveis de ambiente
4. Implementar CI/CD com perfis específicos
5. Configurar monitoramento em produção
