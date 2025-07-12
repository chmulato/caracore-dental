package com.caracore.cca.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * Testes para validar a configuração do pool de conexões
 * Esta versão usa uma abordagem mais isolada, sem depender do contexto completo do Spring
 */
class DataSourceConfigTest {

    private DataSource dataSource;
    private HikariDataSource hikariDataSource;
    
    @Mock
    private Environment env;
    
    private AutoCloseable mocks;
    
    @BeforeEach
    void setup() {
        mocks = openMocks(this);
        
        // Configura o mock do Environment para simular o ambiente de teste
        when(env.getActiveProfiles()).thenReturn(new String[]{"test"});
        when(env.getProperty("spring.datasource.url")).thenReturn("jdbc:h2:mem:testdb");
        when(env.getProperty("spring.datasource.username")).thenReturn("sa");
        when(env.getProperty("spring.datasource.password")).thenReturn("");
        when(env.getProperty("spring.datasource.driver-class-name")).thenReturn("org.h2.Driver");
        
        // Cria uma instância real do DataSourceConfig com o ambiente mockado
        DataSourceConfig config = new DataSourceConfig(env);
        dataSource = config.dataSource();
        
        assertTrue(dataSource instanceof HikariDataSource);
        hikariDataSource = (HikariDataSource) dataSource;
    }
    
    @AfterEach
    void cleanup() throws Exception {
        if (hikariDataSource != null && !hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
        
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    void testDataSourceConfiguration() {
        // Verificações básicas
        assertNotNull(dataSource, "DataSource não deve ser nulo");
        assertTrue(dataSource instanceof HikariDataSource, 
            "DataSource deve ser uma instância de HikariDataSource");
        
        try {
            // Verificações de configuração básica
            assertNotNull(hikariDataSource.getPoolName(), 
                "O nome do pool não deve ser nulo");
            assertTrue(hikariDataSource.getMaximumPoolSize() > 0, 
                "MaximumPoolSize deve ser maior que zero");
            assertTrue(hikariDataSource.getMinimumIdle() >= 0, 
                "MinimumIdle deve ser não-negativo");
            assertTrue(hikariDataSource.getConnectionTimeout() > 0, 
                "ConnectionTimeout deve ser maior que zero");
            assertTrue(hikariDataSource.getIdleTimeout() >= 0, 
                "IdleTimeout deve ser não-negativo");
            assertTrue(hikariDataSource.getMaxLifetime() > 0, 
                "MaxLifetime deve ser maior que zero");
            
            // Verificar se o JDBC URL está correto para ambiente de teste
            String jdbcUrl = hikariDataSource.getJdbcUrl();
            assertNotNull(jdbcUrl, "JDBC URL não deve ser nulo");
            
            // Em ambiente de teste, espera-se uma URL H2
            assertTrue(jdbcUrl.contains("h2") && jdbcUrl.contains("mem:"), 
                "URL deve ser H2 em memória no ambiente de teste");
        } catch (Exception e) {
            fail("Erro ao verificar configurações do DataSource: " + e.getMessage());
        }
    }

    @Test
    void testConnectionAcquisition() {
        assertNotNull(dataSource);
        
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
            assertTrue(connection.isValid(2));  // Reduzindo o timeout para 2 segundos
        } catch (SQLException e) {
            // Em caso de erro de conexão, fornecemos mais detalhes para diagnóstico
            fail("Falha ao obter conexão: " + e.getMessage() + ", SQLState: " + e.getSQLState() + 
                 ", Error Code: " + e.getErrorCode());
        }
    }

    @Test
    void testMultipleConnections() {
        assertNotNull(dataSource);
        
        // Testa múltiplas conexões simultâneas
        Connection conn1 = null;
        Connection conn2 = null;
        
        try {
            conn1 = dataSource.getConnection();
            conn2 = dataSource.getConnection();
            
            assertNotNull(conn1);
            assertNotNull(conn2);
            assertFalse(conn1.isClosed());
            assertFalse(conn2.isClosed());
            assertTrue(conn1.isValid(2));  // Reduzindo o timeout para 2 segundos
            assertTrue(conn2.isValid(2));  // Reduzindo o timeout para 2 segundos
            
            // Verifica se são conexões diferentes
            assertNotEquals(conn1, conn2);
            
        } catch (SQLException e) {
            fail("Falha ao obter múltiplas conexões: " + e.getMessage());
        } finally {
            // Certifique-se de fechar as conexões, mesmo em caso de falha
            if (conn1 != null) {
                try {
                    conn1.close();
                } catch (SQLException ignored) {}
            }
            if (conn2 != null) {
                try {
                    conn2.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    @Test
    void testPoolConfiguration() {
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof HikariDataSource);
        
        try {
            // Verifica configurações específicas
            String poolName = hikariDataSource.getPoolName();
            assertNotNull(poolName, "O nome do pool não deve ser nulo");
            assertTrue(poolName.contains("Pool"), "O nome do pool deve conter 'Pool'");
            
            // Estas configurações podem variar dependendo do ambiente, então verificamos apenas 
            // se estão dentro de limites razoáveis
            
            // ValidationTimeout - deve ser um valor positivo
            assertTrue(hikariDataSource.getValidationTimeout() > 0, 
                "ValidationTimeout deve ser positivo");
                
            // MaximumPoolSize - deve ser pelo menos 1
            assertTrue(hikariDataSource.getMaximumPoolSize() >= 1, 
                "MaximumPoolSize deve ser pelo menos 1");
            
            // MinimumIdle - não pode ser maior que MaximumPoolSize
            assertTrue(hikariDataSource.getMinimumIdle() <= hikariDataSource.getMaximumPoolSize(), 
                "MinimumIdle não pode ser maior que MaximumPoolSize");
                
            // ConnectionTimeout - deve ser razoável
            assertTrue(hikariDataSource.getConnectionTimeout() > 0, 
                "ConnectionTimeout deve ser positivo");
                
            // IdleTimeout - deve ser positivo se MinimumIdle < MaximumPoolSize
            if (hikariDataSource.getMinimumIdle() < hikariDataSource.getMaximumPoolSize()) {
                assertTrue(hikariDataSource.getIdleTimeout() > 0, 
                    "IdleTimeout deve ser positivo quando MinimumIdle < MaximumPoolSize");
            }
            
            // MaxLifetime - deve ser maior que IdleTimeout
            if (hikariDataSource.getIdleTimeout() > 0) {
                assertTrue(hikariDataSource.getMaxLifetime() > hikariDataSource.getIdleTimeout(), 
                    "MaxLifetime deve ser maior que IdleTimeout");
            } else {
                assertTrue(hikariDataSource.getMaxLifetime() > 0,
                    "MaxLifetime deve ser maior que zero");
            }
            
            // Verificações que devem ser constantes em todos os ambientes
            assertFalse(hikariDataSource.isAllowPoolSuspension(), 
                "AllowPoolSuspension deve ser falso");
            
        } catch (Exception e) {
            fail("Falha ao verificar configuração do pool: " + e.getMessage());
        }
    }
}
