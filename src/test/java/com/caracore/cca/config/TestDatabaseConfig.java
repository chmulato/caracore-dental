package com.caracore.cca.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Configuração para fornecer um banco de dados em memória para testes
 */
@TestConfiguration
@Profile("test")
public class TestDatabaseConfig {

    /**
     * Configura um banco de dados H2 em memória para testes
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName("testdb;MODE=PostgreSQL")
            .build();
    }
    
    /**
     * Inicializador para popular o banco de dados de teste
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema-test.sql"));
        populator.addScript(new ClassPathResource("data-test.sql"));
        populator.setContinueOnError(true);
        
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}
