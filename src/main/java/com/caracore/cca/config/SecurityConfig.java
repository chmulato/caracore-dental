package com.caracore.cca.config;

import com.caracore.cca.service.UsuarioDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    private final UsuarioDetailsService usuarioDetailsService;
    
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(UsuarioDetailsService usuarioDetailsService) {
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // Usando cost factor 10 conforme especificado no README
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configurando regras de segurança com autenticação padrão");
        
        http
            .authorizeHttpRequests(authorize -> authorize
                // Recursos públicos
                .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**", "/favicon.ico").permitAll()
                // Páginas públicas
                .requestMatchers("/", "/login", "/agendar", "/api/public/**", "/public/**").permitAll()
                // Páginas administrativas
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Gerenciamento de usuários
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                // Páginas do dentista
                .requestMatchers("/dentista/**").hasAnyRole("ADMIN", "DENTIST")
                // Páginas da recepção
                .requestMatchers("/recepcao/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                // Páginas do paciente
                .requestMatchers("/paciente/**").hasAnyRole("ADMIN", "PATIENT")
                // Todas as outras páginas requerem autenticação
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
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("caraCoreCcaSecureKey")
                .tokenValiditySeconds(86400) // 1 dia
                .rememberMeParameter("remember-me")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // Opcional: desativa para endpoints de API se necessário
            )
            .exceptionHandling(handling -> handling
                .accessDeniedHandler(accessDeniedHandler)
            )
            .authenticationProvider(authenticationProvider());

        logger.info("Configuração de segurança concluída com sucesso");
        return http.build();
    }
}
