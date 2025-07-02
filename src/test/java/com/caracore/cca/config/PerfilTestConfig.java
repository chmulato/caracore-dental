package com.caracore.cca.config;

import com.caracore.cca.util.UserActivityLogger;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração específica para testes do PerfilController.
 */
@TestConfiguration
public class PerfilTestConfig {

    @Bean
    @Primary
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public UserActivityLogger userActivityLogger() {
        return new UserActivityLogger();
    }
}
