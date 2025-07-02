package com.caracore.cca.controller;

import com.caracore.cca.model.Dentista;
import com.caracore.cca.service.DentistaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Usamos um teste mais simples, sem o Spring Context
@ExtendWith(MockitoExtension.class)
class DentistaControllerTest {

    @Mock
    private DentistaService dentistaService;

    @InjectMocks
    private DentistaController dentistaController;

    @Test
    @DisplayName("Deve exibir a lista de dentistas")
    void testListarDentistas() {
        // Preparação
        Dentista d1 = new Dentista();
        d1.setNome("Dentista 1");
        Dentista d2 = new Dentista();
        d2.setNome("Dentista 2");
        when(dentistaService.listarTodos()).thenReturn(Arrays.asList(d1, d2));
        
        // Mock do Model
        Model model = mock(Model.class);
        
        // Execução
        String viewName = dentistaController.listarDentistas(model);
        
        // Verificação
        assertThat(viewName).isEqualTo("dentistas/lista");
        verify(model).addAttribute(eq("dentistas"), eq(Arrays.asList(d1, d2)));
    }

    @Test
    @DisplayName("Deve exibir detalhes do dentista")
    void testDetalhesDentista() {
        // Preparação
        Dentista d = new Dentista();
        d.setId(1L);
        d.setNome("Dentista Detalhe");
        when(dentistaService.buscarPorId(anyLong())).thenReturn(Optional.of(d));
        
        // Mocks
        Model model = mock(Model.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        
        // Execução
        String viewName = dentistaController.detalhesDentista(1L, model, redirectAttributes);
        
        // Verificação
        assertThat(viewName).isEqualTo("dentistas/detalhes");
        verify(model).addAttribute(eq("dentista"), eq(d));
    }
    
    @Test
    @DisplayName("Deve redirecionar quando dentista não for encontrado")
    void testDetalhesDentistaInexistente() {
        // Preparação
        when(dentistaService.buscarPorId(anyLong())).thenReturn(Optional.empty());
        
        // Mocks
        Model model = mock(Model.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        
        // Execução
        String viewName = dentistaController.detalhesDentista(999L, model, redirectAttributes);
        
        // Verificação
        assertThat(viewName).isEqualTo("redirect:/dentistas");
        verify(redirectAttributes).addFlashAttribute(eq("erro"), anyString());
    }
}
