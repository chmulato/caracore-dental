package com.caracore.cca.config;

import com.caracore.cca.service.UsuarioDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários simples para a configuração de segurança
 * Evitamos usar @SpringBootTest para não precisar configurar todo o contexto
 * apenas para testar métodos simples que não dependem do contexto completo
 */
@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @Mock
    private UsuarioDetailsService usuarioDetailsService;
    
    @InjectMocks
    private SecurityConfig securityConfig;
    
    /**
     * Teste simples para verificar que o passwordEncoder é criado corretamente
     */
    @Test
    @DisplayName("Deve criar instância de BCryptPasswordEncoder")
    public void deveCriarBCryptPasswordEncoder() {
        // Act
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        
        // Assert
        assertNotNull(encoder, "O encoder não deve ser nulo");
        assertTrue(encoder instanceof BCryptPasswordEncoder, "O encoder deve ser uma instância de BCryptPasswordEncoder");
    }
    
    /**
     * Teste para verificar que o authenticationProvider é configurado corretamente
     */
    @Test
    @DisplayName("Deve configurar authenticationProvider corretamente")
    public void deveConfigurarAuthenticationProvider() {
        // Arrange
        // Configura o passwordEncoder primeiro, pois o authenticationProvider depende dele
        securityConfig.passwordEncoder();
        
        // Act
        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();
        
        // Assert
        assertNotNull(provider, "O provider não deve ser nulo");
    }
    
    // Não testamos securityFilterChain diretamente porque é complexo mockar HttpSecurity
    // Em um cenário real, usaríamos testes de integração para verificar o comportamento
}
