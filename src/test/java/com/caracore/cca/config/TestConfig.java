package com.caracore.cca.config;

import com.caracore.cca.util.UserActivityLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
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
     * Fornece um encoder BCrypt para testes básicos não relacionados à segurança
     * Nome alterado para evitar conflito com o bean em SecurityTestConfig
     */
    @Bean
    @Primary
    public BCryptPasswordEncoder basicTestPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Fornece uma implementação de AccessDeniedHandler específica para testes
     * Em vez de redirecionar para uma página de acesso negado, esta implementação
     * simplesmente retorna status 403 Forbidden, o que facilita os testes
     */
    @Bean
    @Primary
    public AccessDeniedHandler accessDeniedHandler() {
        return new TestAccessDeniedHandler();
    }
    
    /**
     * Fornece um ObjectMapper para testes para evitar problemas de configuração
     * durante a inicialização do contexto Spring
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
