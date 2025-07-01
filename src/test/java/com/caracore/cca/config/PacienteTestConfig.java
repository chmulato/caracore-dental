package com.caracore.cca.config;

import com.caracore.cca.util.UserActivityLogger;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança específica para testes do PacienteController
 * Essa configuração garante que as restrições de método funcionem adequadamente
 */
@TestConfiguration
@EnableMethodSecurity(prePostEnabled = true)
public class PacienteTestConfig {

    /**
     * Cria uma instância mockada de UserActivityLogger para uso em testes
     */
    @Bean
    @Primary
    public UserActivityLogger userActivityLogger() {
        return Mockito.mock(UserActivityLogger.class);
    }
    
    /**
     * Configuração de segurança que não define regras de URL
     * permitindo que as anotações @PreAuthorize do controlador sejam usadas
     */
    @Bean
    @Primary
    public SecurityFilterChain pacienteTestSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para simplificar os testes
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                // Não definimos regras específicas para /pacientes, deixando para as anotações @PreAuthorize
                // As demais URLs requerem autenticação 
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
