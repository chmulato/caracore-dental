# Configuração do Pool de Conexões - CCA

## Visão Geral

O sistema CCA utiliza **HikariCP** como pool de conexões para PostgreSQL, com configurações otimizadas para cada ambiente.

## Configurações por Ambiente

### Local (Desenvolvimento)

- **Pool Size**: 15 máximo, 5 mínimo
- **Timeouts**: Otimizados para desenvolvimento
- **Monitoramento**: Detecção de vazamentos em 60s

### Homologação

- **Pool Size**: 15 máximo, 5 mínimo
- **Timeouts**: Configuração intermediária
- **Monitoramento**: Habilitado

### Produção

- **Pool Size**: 25 máximo, 10 mínimo
- **Timeouts**: Otimizados para performance
- **Monitoramento**: JMX habilitado

## Arquivos de Configuração

### 1. Configuração Java

- `DataSourceConfig.java` - Configuração programática do pool
- `DataSourceMonitoringConfig.java` - Monitoramento e health checks

### 2. Configuração YAML

- `application-local.yml` - Configuração para desenvolvimento
- `application-homolog.yml` - Configuração para homologação
- `application-prod.yml` - Configuração para produção

## Monitoramento

### Health Check

Acesse: `http://localhost:8080/actuator/health`

```json
{
  "status": "UP",
  "components": {
    "hikariHealthIndicator": {
      "status": "UP",
      "details": {
        "poolName": "CCA-Local-Pool",
        "activeConnections": 2,
        "idleConnections": 3,
        "totalConnections": 5,
        "threadsAwaitingConnection": 0,
        "maximumPoolSize": 15,
        "minimumIdle": 5
      }
    }
  }
}
```

### Informações do Pool

Acesse: `http://localhost:8080/actuator/info`

```json
{
  "hikari": {
    "poolName": "CCA-Local-Pool",
    "jdbcUrl": "jdbc:postgresql://localhost:5432/cca_db",
    "driverClassName": "org.postgresql.Driver",
    "maximumPoolSize": 15,
    "minimumIdle": 5,
    "connectionTimeout": 30000,
    "idleTimeout": 600000,
    "maxLifetime": 1800000,
    "leakDetectionThreshold": 60000
  }
}
```

### Logs de Monitoramento

O sistema gera logs automáticos a cada 5 minutos:

```markdown
INFO  - === MÉTRICAS DO POOL DE CONEXÕES ===
INFO  - Pool Name: CCA-Local-Pool
INFO  - Active Connections: 2
INFO  - Idle Connections: 3
INFO  - Total Connections: 5
INFO  - Threads Awaiting Connection: 0
INFO  - Maximum Pool Size: 15
INFO  - Minimum Idle: 5
INFO  - =====================================
```

## Configurações Específicas

### PostgreSQL Otimizations

```yaml
datasource:
  hikari:
    # Configurações específicas do PostgreSQL
    connection-init-sql: "SET search_path TO public"
    connection-test-query: "SELECT 1"
    # Propriedades do driver
    data-source-properties:
      ApplicationName: CCA-Application
      connectTimeout: 30
      socketTimeout: 30
      tcpKeepAlive: true
      logUnclosedConnections: true
```

### Detecção de Vazamentos

```yaml
datasource:
  hikari:
    leak-detection-threshold: 60000  # 60 segundos
```

### Monitoramento JMX

```yaml
datasource:
  hikari:
    register-mbeans: true
```

## Troubleshooting

### Problemas Comuns

#### 1. Pool Esgotado

**Sintoma**: `HikariPool-1 - Connection is not available`

**Solução**:

- Verificar vazamentos de conexão
- Aumentar `maximum-pool-size`
- Verificar `leak-detection-threshold`

#### 2. Timeout de Conexão

**Sintoma**: `Connection is not available, request timed out`

**Solução**:

- Aumentar `connection-timeout`
- Verificar latência de rede
- Verificar configuração do banco

#### 3. Conexões Ociosas

**Sintoma**: Muitas conexões ociosas no banco

**Solução**:

- Reduzir `minimum-idle`
- Ajustar `idle-timeout`
- Verificar padrão de uso da aplicação

### Comandos de Diagnóstico

#### Verificar Status do Pool

```bash
curl http://localhost:8080/actuator/health/hikariHealthIndicator
```

#### Verificar Métricas

```bash
curl http://localhost:8080/actuator/metrics | grep hikari
```

#### Logs de Debug

```yaml
logging:
  level:
    com.zaxxer.hikari: DEBUG
    com.caracore.cca.config.DataSourceConfig: DEBUG
```

## Ajustes de Performance

### Para Aplicações com Alto Volume

```yaml
datasource:
  hikari:
    maximum-pool-size: 50
    minimum-idle: 20
    connection-timeout: 20000
    validation-timeout: 3000
```

### Para Aplicações com Baixo Volume

```yaml
datasource:
  hikari:
    maximum-pool-size: 10
    minimum-idle: 2
    idle-timeout: 300000
    max-lifetime: 900000
```

### Para Desenvolvimento

```yaml
datasource:
  hikari:
    maximum-pool-size: 5
    minimum-idle: 1
    leak-detection-threshold: 30000  # 30 segundos
```

## Testes

Para executar os testes do pool de conexões:

```bash
mvn test -Dtest=DataSourceConfigTest
```

Os testes verificam:

- Configuração correta do pool
- Aquisição de conexões
- Múltiplas conexões simultâneas
- Métricas do pool
- Configurações específicas

## Melhores Práticas

1. **Always close connections**: Use try-with-resources
2. **Monitor metrics**: Configure alertas para métricas críticas
3. **Tune for workload**: Ajuste configurações baseado no uso real
4. **Test configurations**: Sempre teste mudanças em homologação
5. **Set leak detection**: Configure detecção de vazamentos adequada
6. **Use health checks**: Configure health checks para monitoramento

## Referências

- [HikariCP Documentation](https://github.com/brettwooldridge/HikariCP)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/documentation/)
- [Spring Boot DataSource Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.datasource)
