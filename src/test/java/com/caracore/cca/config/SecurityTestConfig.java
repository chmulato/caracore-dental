package com.caracore.cca.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * Configuração específica para o teste SecurityConfigTest
 */
@TestConfiguration
@EnableWebMvc
public class SecurityTestConfig {
    
    /**
     * Cria uma instância de BCryptPasswordEncoder para testes de SecurityConfig
     * Este bean tem prioridade mais alta que o da SecurityConfig
     */
    @Bean
    @Primary
    public BCryptPasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Fornece o bean mvcHandlerMappingIntrospector necessário para Spring Security 6+
     * Este bean é necessário quando usamos requestMatchers() em configurações de segurança
     */
    @Bean
    @Primary
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }
}
