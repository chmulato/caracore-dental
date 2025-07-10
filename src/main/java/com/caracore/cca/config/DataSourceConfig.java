package com.caracore.cca.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Configuração personalizada do pool de conexões HikariCP
 * Otimizada para PostgreSQL com diferentes configurações por ambiente
 */
@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    private final Environment env;

    public DataSourceConfig(Environment env) {
        this.env = env;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        try {
            String activeProfile = getActiveProfile();
            logger.info("Configurando DataSource para perfil: {}", activeProfile);

            HikariConfig config = new HikariConfig();

            // Configurações básicas com verificação de nulos
            String jdbcUrl = env.getProperty("spring.datasource.url");
            String username = env.getProperty("spring.datasource.username");
            String password = env.getProperty("spring.datasource.password");
            String driverClassName = env.getProperty("spring.datasource.driver-class-name");

            if (jdbcUrl == null) {
                // Se não houver URL configurada, use H2 em memória como fallback
                jdbcUrl = "jdbc:h2:mem:cca_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
                username = "sa";
                password = "";
                driverClassName = "org.h2.Driver";
                logger.warn("URL do banco de dados não configurada! Usando H2 em memória como fallback");
            }

            // Aplicar as configurações
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username != null ? username : "");
            config.setPassword(password != null ? password : "");

            // Verificar se o driver está disponível antes de configurá-lo
            if (driverClassName != null && !driverClassName.isEmpty()) {
                try {
                    Class.forName(driverClassName);
                    config.setDriverClassName(driverClassName);
                } catch (ClassNotFoundException e) {
                    logger.warn("Driver JDBC não encontrado: {}. Tentando usar driver automático.", driverClassName);
                    // HikariCP tentará determinar o driver automaticamente pela URL
                }
            }

            // Configurações específicas por ambiente
            configureByEnvironment(config, activeProfile);

            // Configurações gerais de otimização
            configureGeneral(config);

            // Configurações de monitoramento
            configureMonitoring(config, activeProfile);

            HikariDataSource dataSource = new HikariDataSource(config);
            logger.info("DataSource configurado com sucesso - Pool: {}, Max: {}, Min: {}", 
                    config.getPoolName(), config.getMaximumPoolSize(), config.getMinimumIdle());

            return dataSource;
        } catch (Exception e) {
            logger.error("Erro ao configurar DataSource: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao configurar o DataSource: " + e.getMessage(), e);
        }
    }

    private void configureByEnvironment(HikariConfig config, String profile) {
        // Se estiver usando H2, configure de forma mais simples
        if (isH2Database()) {
            configureForH2(config);
            return;
        }
        
        switch (profile) {
            case "local":
                configureLocal(config);
                break;
            case "dev":
                configureDev(config);
                break;
            case "homolog":
                configureHomolog(config);
                break;
            case "prod":
                configureProd(config);
                break;
            case "test":
                configureTest(config);
                break;
            default:
                configureDefault(config);
        }
    }
    
    private void configureForH2(HikariConfig config) {
        config.setPoolName("CCA-H2-Test-Pool");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(900000);
        logger.info("Configuração para H2 aplicada - Pool simplificado para testes");
    }
    
    private void configureTest(HikariConfig config) {
        config.setPoolName("CCA-Test-Pool");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(900000);
        logger.info("Configuração TEST aplicada - Pool simplificado para testes unitários");
    }

    private void configureLocal(HikariConfig config) {
        config.setPoolName("CCA-Local-Pool");
        config.setMaximumPoolSize(15);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        logger.info("Configuração LOCAL aplicada - Pool otimizado para desenvolvimento");
    }

    private void configureDev(HikariConfig config) {
        config.setPoolName("CCA-Dev-Pool");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(3);
        config.setConnectionTimeout(20000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1800000);
        logger.info("Configuração DEV aplicada - Pool básico para desenvolvimento");
    }

    private void configureHomolog(HikariConfig config) {
        config.setPoolName("CCA-Homolog-Pool");
        config.setMaximumPoolSize(15);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        logger.info("Configuração HOMOLOG aplicada - Pool para testes");
    }

    private void configureProd(HikariConfig config) {
        config.setPoolName("CCA-Production-Pool");
        config.setMaximumPoolSize(25);
        config.setMinimumIdle(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        config.setIsolateInternalQueries(true);
        logger.info("Configuração PRODUÇÃO aplicada - Pool otimizado para alta performance");
    }

    private void configureDefault(HikariConfig config) {
        config.setPoolName("CCA-Default-Pool");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(3);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        logger.info("Configuração DEFAULT aplicada");
    }

    private void configureGeneral(HikariConfig config) {
        // Configurações gerais de otimização
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        config.setAutoCommit(false);
        config.setAllowPoolSuspension(false);
        
        // Verifica se é H2 antes de definir comandos específicos do PostgreSQL
        if (!isH2Database()) {
            // Configurações específicas do PostgreSQL
            config.setConnectionInitSql("SET search_path TO public");
            config.addDataSourceProperty("ApplicationName", "CCA-Application");
            config.addDataSourceProperty("connectTimeout", "30");
            config.addDataSourceProperty("socketTimeout", "30");
            config.addDataSourceProperty("tcpKeepAlive", "true");
            config.addDataSourceProperty("logUnclosedConnections", "true");
        }
        
        logger.debug("Configurações gerais aplicadas");
    }
    
    private boolean isH2Database() {
        String url = env.getProperty("spring.datasource.url", "");
        return url.contains("h2") || url.contains("jdbc:h2:");
    }

    private void configureMonitoring(HikariConfig config, String profile) {
        // Monitoramento JMX habilitado para todos os ambientes
        config.setRegisterMbeans(true);
        
        // Configurações específicas de monitoramento por ambiente
        if ("prod".equals(profile)) {
            // Em produção, configurações mais restritivas
            config.setMetricRegistry(null); // Desabilitar métricas Dropwizard se não usado
        } else {
            // Em desenvolvimento, mais detalhes de debug
            config.setLeakDetectionThreshold(30000); // 30 segundos para detectar vazamentos
        }
        
        logger.debug("Configurações de monitoramento aplicadas para perfil: {}", profile);
    }

    private String getActiveProfile() {
        String[] profiles = env.getActiveProfiles();
        if (profiles.length > 0) {
            return profiles[0];
        }
        return "default";
    }
}
