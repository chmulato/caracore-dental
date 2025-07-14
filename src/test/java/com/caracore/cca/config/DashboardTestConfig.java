package com.caracore.cca.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * Configuração específica para testes do DashboardController
 * Esta configuração permite testar o controller sem depender de toda a infraestrutura
 */
@TestConfiguration
@EnableWebMvc
public class DashboardTestConfig {
    
    /**
     * Fornece uma implementação mockada de RequestMonitoringInterceptor para testes
     * Necessário porque o GlobalExceptionHandler depende deste bean
     */
    @Bean
    @Primary
    public RequestMonitoringInterceptor requestMonitoringInterceptor() {
        return Mockito.mock(RequestMonitoringInterceptor.class);
    }
    
    /**
     * Fornece um encoder BCrypt para testes
     */
    @Bean
    @Primary
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Fornece o bean mvcHandlerMappingIntrospector necessário para Spring Security 6+
     */
    @Bean
    @Primary
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }
    
    /**
     * Configuração de segurança específica para testes do dashboard
     * Esta configuração simplificada exige autenticação apenas para o endpoint /dashboard
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                .requestMatchers("/dashboard").authenticated()
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            );
            
        // Desabilita CSRF para testes
        http.csrf(csrf -> csrf.disable());
        
        return http.build();
    }
}
