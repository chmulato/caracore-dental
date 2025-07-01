package com.caracore.cca.config;

import com.caracore.cca.util.UserActivityLogger;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuração para testes que fornece beans simulados necessários
 */
@TestConfiguration
public class TestConfig {
    
    /**
     * Cria uma instância de UserActivityLogger para uso em testes
     * Esta é necessária porque vários controladores e interceptadores
     * dependem do UserActivityLogger
     */
    @Bean
    @Primary
    public UserActivityLogger userActivityLogger() {
        return new UserActivityLogger();
    }
}
