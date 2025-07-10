package com.caracore.cca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import org.mockito.Mockito;

/**
 * Configuração específica para testes de segurança
 * Esta configuração é utilizada nos testes de controladores que dependem de autenticação
 */
@TestConfiguration
@EnableWebMvc
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityTestConfig {
    
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Bean
    @Primary
    public AccessDeniedHandler accessDeniedHandler() {
        return Mockito.mock(AccessDeniedHandler.class);
    }
    
    /**
     * Cria uma instância de BCryptPasswordEncoder para testes de segurança
     * Removida a anotação @Primary para evitar conflito
     */
    @Bean
    public BCryptPasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Fornece o bean para testes de segurança necessário para Spring Security 6+
     * Este bean é necessário quando usamos requestMatchers() em configurações de segurança
     * Nome alterado para evitar conflito com o bean padrão do Spring
     */
    @Bean
    @Primary
    public HandlerMappingIntrospector testMvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }
    
    /**
     * Configuração de segurança específica para testes
     * Esta configuração simplificada permite o teste de controladores com autenticação
     * e desabilita CSRF para facilitar os testes de POST
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/login", "/agendar", "/api/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/pacientes/**").hasAnyRole("ADMIN", "DENTIST", "RECEPTIONIST")
                .requestMatchers("/dentista/**").hasAnyRole("ADMIN", "DENTIST")
                .requestMatchers("/recepcao/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                .requestMatchers("/paciente/**").hasAnyRole("ADMIN", "PATIENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/authenticate")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            // Configure access denied handler
            .exceptionHandling(handling -> handling
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint((request, response, authException) -> {
                    // Para testes, redirecionar usuários não autenticados para página de login
                    response.sendRedirect("/login");
                })
            )
            // Desabilita CSRF para facilitar testes de POST
            .csrf(csrf -> csrf.disable());
            
        return http.build();
    }
}
