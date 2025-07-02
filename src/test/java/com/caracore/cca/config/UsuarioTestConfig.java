package com.caracore.cca.config;

import com.caracore.cca.service.InitService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuração de teste específica para testes do UsuarioController
 */
@TestConfiguration
public class UsuarioTestConfig {
    
    /**
     * Fornece um mock do InitService para os testes de usuários
     */
    @Bean
    @Primary
    public InitService initService() {
        return Mockito.mock(InitService.class);
    }
}
