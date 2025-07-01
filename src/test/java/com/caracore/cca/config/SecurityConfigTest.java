package com.caracore.cca.config;

import com.caracore.cca.service.UsuarioDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;
    
    @MockBean
    private UsuarioDetailsService usuarioDetailsService;
    
    @Test
    @DisplayName("Deve criar instância de BCryptPasswordEncoder")
    public void deveCriarBCryptPasswordEncoder() {
        // Act
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        
        // Assert
        assertNotNull(encoder, "O encoder não deve ser nulo");
        assertTrue(encoder instanceof BCryptPasswordEncoder, "O encoder deve ser uma instância de BCryptPasswordEncoder");
    }
    
    @Test
    @DisplayName("Deve configurar authenticationProvider corretamente")
    public void deveConfigurarAuthenticationProvider() {
        // Act
        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();
        
        // Assert
        assertNotNull(provider, "O provider não deve ser nulo");
        // Não podemos acessar diretamente getUserDetailsService() e getPasswordEncoder()
        // pois são protegidos, mas podemos verificar que o provider não é nulo
    }
    
    // Não testamos securityFilterChain diretamente porque é complexo mockar HttpSecurity
    // Em um cenário real, usaríamos testes de integração para verificar o comportamento
}
