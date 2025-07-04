package com.caracore.cca.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChainTest(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/public/**", "/css/**", "/js/**", "/api/public/**").permitAll()
                .anyRequest().permitAll()
            )
            .httpBasic(basic -> {});
            
        return http.build();
    }
}
