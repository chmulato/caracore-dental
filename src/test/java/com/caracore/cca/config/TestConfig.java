package com.caracore.cca.config;

import com.caracore.cca.util.UserActivityLogger;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração para testes que fornece beans simulados necessários
 */
@TestConfiguration
public class TestConfig implements WebMvcConfigurer {
    
    /**
     * Cria uma instância mockada de UserActivityLogger para uso em testes
     * Esta é necessária porque vários controladores e interceptadores
     * dependem do UserActivityLogger
     */
    @Bean
    @Primary
    public UserActivityLogger userActivityLogger() {
        return Mockito.mock(UserActivityLogger.class);
    }
    
    /**
     * Fornece um encoder BCrypt para testes
     */
    @Bean
    @Primary
    public BCryptPasswordEncoder testPasswordEncoder() {
        // Nome alterado para evitar conflito com o bean em SecurityConfig
        return new BCryptPasswordEncoder();
    }
    
    // Removed HandlerMappingIntrospector bean to avoid conflicts with Spring Boot auto-configuration
}
