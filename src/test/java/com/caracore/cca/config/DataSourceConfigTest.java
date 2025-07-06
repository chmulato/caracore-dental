package com.caracore.cca.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para validar a configuração do pool de conexões
 */
@SpringBootTest
@ActiveProfiles("test")
class DataSourceConfigTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDataSourceConfiguration() {
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof HikariDataSource);
        
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        assertNotNull(hikariDataSource.getPoolName());
        assertTrue(hikariDataSource.getMaximumPoolSize() > 0);
        assertTrue(hikariDataSource.getMinimumIdle() > 0);
        assertTrue(hikariDataSource.getConnectionTimeout() > 0);
        assertTrue(hikariDataSource.getIdleTimeout() > 0);
        assertTrue(hikariDataSource.getMaxLifetime() > 0);
    }

    @Test
    void testConnectionAcquisition() throws SQLException {
        assertNotNull(dataSource);
        
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
            assertTrue(connection.isValid(5));
        }
    }

    @Test
    void testMultipleConnections() throws SQLException {
        assertNotNull(dataSource);
        
        // Testa múltiplas conexões simultâneas
        try (Connection conn1 = dataSource.getConnection();
             Connection conn2 = dataSource.getConnection()) {
            
            assertNotNull(conn1);
            assertNotNull(conn2);
            assertFalse(conn1.isClosed());
            assertFalse(conn2.isClosed());
            assertTrue(conn1.isValid(5));
            assertTrue(conn2.isValid(5));
            
            // Verifica se são conexões diferentes
            assertNotEquals(conn1, conn2);
        }
    }

    @Test
    void testPoolMetrics() {
        assertTrue(dataSource instanceof HikariDataSource);
        
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        
        // Verifica métricas básicas
        assertTrue(hikariDataSource.getHikariPoolMXBean().getTotalConnections() >= 0);
        assertTrue(hikariDataSource.getHikariPoolMXBean().getActiveConnections() >= 0);
        assertTrue(hikariDataSource.getHikariPoolMXBean().getIdleConnections() >= 0);
        assertTrue(hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection() >= 0);
    }

    @Test
    void testPoolConfiguration() {
        assertTrue(dataSource instanceof HikariDataSource);
        
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        
        // Verifica configurações específicas
        assertNotNull(hikariDataSource.getPoolName());
        assertEquals("SELECT 1", hikariDataSource.getConnectionTestQuery());
        assertFalse(hikariDataSource.isAutoCommit());
        assertEquals(5000, hikariDataSource.getValidationTimeout());
        assertFalse(hikariDataSource.isAllowPoolSuspension());
        assertTrue(hikariDataSource.isRegisterMbeans());
    }
}
