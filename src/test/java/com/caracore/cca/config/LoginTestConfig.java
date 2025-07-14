package com.caracore.cca.config;

import com.caracore.cca.service.UsuarioDetailsService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração específica para testes do LoginController
 * Esta configuração substitui o SecurityConfig para evitar conflito de beans
 */
@TestConfiguration
public class LoginTestConfig {
    
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
     * Cria uma versão mockada do UsuarioDetailsService para testes
     */
    @Bean
    @Primary
    public UsuarioDetailsService usuarioDetailsService() {
        return Mockito.mock(UsuarioDetailsService.class);
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
     * Fornece um provider de autenticação para testes
     */
    @Bean
    @Primary
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    /**
     * Configuração simplificada do SecurityFilterChain para testes
     */
    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            );
            
        return http.build();
    }
}
