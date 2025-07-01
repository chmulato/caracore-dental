package com.caracore.cca.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;

import javax.sql.DataSource;
import java.sql.SQLException;

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
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:/schema-test.sql")
            .addScript("classpath:/data.sql");
            
        return builder.build();
    }
    
    /**
     * Initialize the database with test data
     */
    @Bean
    public boolean initializeTestDatabase(DataSource dataSource) throws ScriptException, SQLException {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema-test.sql"));
        populator.addScript(new ClassPathResource("db/testdata.sql"));
        populator.execute(dataSource);
        return true;
    }
}
