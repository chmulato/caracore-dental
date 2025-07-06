package com.caracore.cca.config;

import com.caracore.cca.service.RelatorioService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuração para testes relacionados a relatórios
 */
@TestConfiguration
public class RelatorioTestConfig {
    
    /**
     * Fornece um mock do RelatorioService para testes
     */
    @Bean
    @Primary
    public RelatorioService relatorioService() {
        return Mockito.mock(RelatorioService.class);
    }
}
