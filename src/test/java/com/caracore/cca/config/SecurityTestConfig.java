package com.caracore.cca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * Configuração específica para testes de segurança
 * Esta configuração é utilizada nos testes de controladores que dependem de autenticação
 */
@TestConfiguration
@EnableWebMvc
public class SecurityTestConfig {
    
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;
    
    /**
     * Cria uma instância de BCryptPasswordEncoder para testes de segurança
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
