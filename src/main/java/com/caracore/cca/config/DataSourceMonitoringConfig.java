package com.caracore.cca.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;

/**
 * Configuração de monitoramento do pool de conexões
 * Fornece logs e métricas para HikariCP
 */
@Configuration
@EnableScheduling
public class DataSourceMonitoringConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceMonitoringConfig.class);

    private final DataSource dataSource;

    public DataSourceMonitoringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Log periódico das métricas do pool de conexões
     */
    @Scheduled(fixedRate = 300000) // A cada 5 minutos
    public void logPoolMetrics() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            
            logger.info("=== MÉTRICAS DO POOL DE CONEXÕES ===");
            logger.info("Pool Name: {}", hikariDataSource.getPoolName());
            logger.info("Active Connections: {}", hikariDataSource.getHikariPoolMXBean().getActiveConnections());
            logger.info("Idle Connections: {}", hikariDataSource.getHikariPoolMXBean().getIdleConnections());
            logger.info("Total Connections: {}", hikariDataSource.getHikariPoolMXBean().getTotalConnections());
            logger.info("Threads Awaiting Connection: {}", hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
            logger.info("Maximum Pool Size: {}", hikariDataSource.getMaximumPoolSize());
            logger.info("Minimum Idle: {}", hikariDataSource.getMinimumIdle());
            logger.info("=====================================");
        }
    }

    /**
     * Método para obter métricas do pool programaticamente
     */
    public String getPoolStatus() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            
            return String.format(
                "Pool: %s | Active: %d | Idle: %d | Total: %d | Waiting: %d | Max: %d | Min: %d",
                hikariDataSource.getPoolName(),
                hikariDataSource.getHikariPoolMXBean().getActiveConnections(),
                hikariDataSource.getHikariPoolMXBean().getIdleConnections(),
                hikariDataSource.getHikariPoolMXBean().getTotalConnections(),
                hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection(),
                hikariDataSource.getMaximumPoolSize(),
                hikariDataSource.getMinimumIdle()
            );
        }
        return "DataSource não é HikariDataSource";
    }

    /**
     * Verifica se o pool está saudável
     */
    public boolean isPoolHealthy() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            
            try {
                // Verifica se o pool não está fechado
                if (hikariDataSource.isClosed()) {
                    return false;
                }
                
                // Testa uma conexão
                try (var connection = hikariDataSource.getConnection()) {
                    return connection.isValid(5);
                }
            } catch (Exception e) {
                logger.error("Erro ao verificar saúde do pool: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }
}
