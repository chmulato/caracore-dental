# Configuração de Ambientes - Application.yml

Este documento descreve a configuração dos diferentes ambientes do Sistema de Agendamento Cara Core através dos arquivos `application.yml`.

## Estrutura de Arquivos

```markdown
src/main/resources/
├── application.yml              # Configurações comuns e perfil padrão
├── application-dev.yml          # Desenvolvimento local
├── application-homolog.yml      # Homologação
├── application-prod.yml         # Produção
└── application-test.yml         # Testes automatizados
```

## Arquivo Principal (application.yml)

O arquivo principal contém configurações comuns a todos os ambientes:

- **Configurações do servidor** (porta, context-path, tratamento de erros)
- **Configurações do Spring** (perfil ativo, JPA, internacionalização)
- **Configurações do Swagger/OpenAPI**
- **Configurações de logging**
- **Configurações do Flyway**

### Perfil Padrão

```yaml
spring:
  profiles:
    active: dev  # Perfil padrão para desenvolvimento local
```

## Ambientes Configurados

### 1. Desenvolvimento (dev)

**Arquivo**: `application-dev.yml`

**Características**:

- Banco de dados PostgreSQL local
- Logs em nível DEBUG
- Swagger habilitado
- Hot reload habilitado (DevTools)
- Console H2 habilitado para debug

**Configurações principais**:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cca_db
    username: cca_user
    password: cca_password
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  
  devtools:
    restart:
      enabled: true
```

**Como usar**:

```bash
# Executar com perfil de desenvolvimento (padrão)
mvn spring-boot:run

# Ou explicitamente
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Ou explicitamente resumido
mvn spring-boot:run -D-boot.run.profiles=local
```

### 2. Homologação (homolog)

**Arquivo**: `application-homolog.yml`

**Características**:

- Banco de dados PostgreSQL na nuvem
- Logs em nível INFO
- Swagger habilitado
- SSL habilitado
- Monitoramento com Actuator

**Configurações principais**:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://homolog-db.caracore.com.br:5432/cca_homolog
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  
  ssl:
    key-store: classpath:keystore.p12
```

**Como usar**:

```bash
# Executar com perfil de homologação
mvn spring-boot:run -Dspring-boot.run.profiles=homolog

# Com variáveis de ambiente
DB_USERNAME=user DB_PASSWORD=pass mvn spring-boot:run -Dspring-boot.run.profiles=homolog
```

### 3. Produção (prod)

**Arquivo**: `application-prod.yml`

**Características**:

- Banco de dados PostgreSQL de produção
- Logs em nível WARN
- Swagger desabilitado
- SSL obrigatório
- Configurações de segurança máxima
- Pool de conexões otimizado

**Configurações principais**:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://prod-db.caracore.com.br:5432/cca_prod
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  
springdoc:
  swagger-ui:
    enabled: false
```

**Como usar**:

```bash
# Executar com perfil de produção
java -jar -Dspring.profiles.active=prod target/cca-0.0.1-SNAPSHOT.jar

# Com variáveis de ambiente
DB_USERNAME=user DB_PASSWORD=pass java -jar -Dspring.profiles.active=prod target/cca-0.0.1-SNAPSHOT.jar
```

### 4. Testes (test)

**Arquivo**: `application-test.yml`

**Características**:

- Banco H2 em memória
- Logs mínimos
- Configurações otimizadas para testes
- Dados de teste automáticos

**Configurações principais**:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  
  jpa:
    hibernate:
      ddl-auto: create-drop
  
  sql:
    init:
      mode: always
      data-locations: classpath:data-test.sql
```

**Como usar**:

```bash
# Executar testes (perfil aplicado automaticamente)
mvn test

# Executar com perfil de teste explícito
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

## Variáveis de Ambiente

### Desenvolvimento

Não requer variáveis especiais (valores fixos no arquivo).

### Homologação

```bash
export DB_USERNAME=cca_homolog_user
export DB_PASSWORD=senha_segura_homolog
export SSL_KEYSTORE_PASSWORD=keystore_password
```

### Produção

```bash
export DB_USERNAME=cca_prod_user
export DB_PASSWORD=senha_muito_segura_prod
export SSL_KEYSTORE_PASSWORD=keystore_password_prod
export ADMIN_EMAIL=admin@caracore.com.br
```

## Configurações por Categoria

### 1. Banco de Dados

#### Desenvolvimento

- PostgreSQL local
- DDL automático (update)
- Show SQL habilitado

#### Homologação/Produção

- PostgreSQL remoto
- DDL validação apenas
- Pool de conexões configurado
- SSL habilitado

#### Testes

- H2 em memória
- DDL create-drop
- Dados iniciais automáticos

### 2. Logging

#### Desenvolvimento

```yaml
logging:
  level:
    com.caracore.cca: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
```

#### Homologação

```yaml
logging:
  level:
    com.caracore.cca: INFO
    org.springframework.security: INFO
    org.hibernate.SQL: WARN
```

#### Produção

```yaml
logging:
  level:
    com.caracore.cca: WARN
    org.springframework.security: WARN
    org.hibernate.SQL: ERROR
```

### 3. Swagger/OpenAPI

#### Desenvolvimento/Homologação

```yaml
springdoc:
  swagger-ui:
    enabled: true
    try-it-out-enabled: true
```

#### Produção

```yaml
springdoc:
  swagger-ui:
    enabled: false
```

### 4. Segurança

#### Desenvolvimento

- Configurações relaxadas
- Debug habilitado

#### Homologação

- SSL habilitado
- Configurações intermediárias

#### Produção

- SSL obrigatório
- Configurações máximas de segurança
- CSRF habilitado

## Comandos Úteis

### Verificar Perfil Ativo

```bash
# Via propriedade do sistema
java -Dspring.profiles.active=prod -jar app.jar

# Via variável de ambiente
export SPRING_PROFILES_ACTIVE=prod
java -jar app.jar

# Via argument
java -jar app.jar --spring.profiles.active=prod
```

### Build para Diferentes Ambientes

```bash
# Build padrão (desenvolvimento)
mvn clean package

# Build para homologação
mvn clean package -Dhomolog

# Build para produção
mvn clean package -Dprod
```

### Executar com Configurações Específicas

```bash
# Sobrescrever porta
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"

# Múltiplos perfis
mvn spring-boot:run -Dspring-boot.run.profiles=prod,monitoring

# Configuração externa
java -jar app.jar --spring.config.location=file:./config/
```

## Boas Práticas

### 1. Segredos e Senhas

- **Nunca** commitar senhas nos arquivos YAML
- Usar variáveis de ambiente para produção
- Usar ferramentas como Spring Cloud Config para ambientes complexos

### 2. Configurações Sensíveis

```yaml
# Não fazer
spring:
  datasource:
    password: senha123

# Fazer
spring:
  datasource:
    password: ${DB_PASSWORD:senha_dev}
```

### 3. Hierarquia de Configurações

1. Configurações comuns no `application.yml`
2. Configurações específicas nos `application-{profile}.yml`
3. Variáveis de ambiente sobrescrevem arquivos
4. Argumentos de linha de comando sobrescrevem tudo

### 4. Documentação

- Documentar todas as variáveis de ambiente necessárias
- Manter exemplos atualizados
- Versionar mudanças significativas

## Troubleshooting

### Perfil não está sendo aplicado

1. Verificar se o nome do arquivo está correto (`application-{profile}.yml`)
2. Confirmar se o perfil está sendo ativado corretamente
3. Verificar logs de inicialização para confirmar perfil ativo

### Configurações não estão sendo lidas

1. Verificar sintaxe YAML (indentação)
2. Confirmar se as propriedades existem no Spring Boot
3. Verificar se há conflitos entre arquivos

### Banco de dados não conecta

1. Verificar se as variáveis de ambiente estão definidas
2. Confirmar se o banco está acessível
3. Verificar configurações de firewall/rede

### SSL não funciona

1. Verificar se o keystore está no classpath
2. Confirmar se a senha do keystore está correta
3. Verificar se o certificado não expirou

## Migração Entre Ambientes

### De Desenvolvimento para Homologação

1. Commit todas as mudanças
2. Configurar variáveis de ambiente de homologação
3. Deploy com perfil homolog
4. Executar testes de smoke

### De Homologação para Produção

1. Validar todos os testes em homologação
2. Configurar variáveis de ambiente de produção
3. Fazer backup do banco de produção
4. Deploy com perfil prod
5. Monitorar logs pós-deploy

## Monitoramento

### Logs por Ambiente

- **Dev**: Console + arquivo local
- **Homolog**: Arquivo + sistema centralizado
- **Prod**: Sistema centralizado + alertas

### Métricas

- Spring Boot Actuator habilitado em homolog/prod
- Endpoints de health check
- Métricas de performance do banco

### Alertas

- Configurar alertas para erros em produção
- Monitorar uso de memória/CPU
- Alertas de indisponibilidade do banco
