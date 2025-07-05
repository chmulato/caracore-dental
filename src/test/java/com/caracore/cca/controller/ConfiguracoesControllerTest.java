package com.caracore.cca.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Classe de teste para o ConfiguracoesController.
 * Testa o comportamento do controller de configurações.
 */
public class ConfiguracoesControllerTest {

    private ConfiguracoesController controller;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new ConfiguracoesController();
    }

    @Test
    @DisplayName("Deve retornar view name configuracoes/index e configurar modelo")
    public void deveExibirPaginaDeConfiguracoes() {
        // Given
        Model model = mock(Model.class);
        
        // When
        String viewName = controller.configuracoes(model);
        
        // Then
        assertThat(viewName).isEqualTo("configuracoes/index");
        verify(model).addAttribute("titulo", "Configurações do Sistema");
        verify(model).addAttribute("activeLink", "configuracoes");
    }
}