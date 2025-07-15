package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AgendamentoPublicoController usando método direto (sem MockMvc)
 * para evitar problemas com renderização de templates
 */
public class AgendamentoPublicoControllerDirectTest {

    @Test
    void testAgendamentoConfirmadoSuccess() {
        // Mocks
        var agendamentoService = mock(com.caracore.cca.service.AgendamentoService.class);

        var controller = new AgendamentoPublicoController();
        ReflectionTestUtils.setField(controller, "agendamentoService", agendamentoService);

        // Dados de teste
        LocalDateTime dataHora = LocalDateTime.of(2025, 7, 15, 14, 30);
        Agendamento agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setPaciente("João Silva");
        agendamento.setDentista("Dra. Maria Santos");
        agendamento.setDataHora(dataHora);
        agendamento.setStatus("AGENDADO");

        when(agendamentoService.buscarPorId(1L)).thenReturn(Optional.of(agendamento));
        Model model = mock(Model.class);

        String viewName = controller.agendamentoConfirmado(1L, model);

        assertEquals("public/agendamento-confirmado", viewName);
        verify(model).addAttribute("agendamento", agendamento);
        verify(agendamentoService).buscarPorId(1L);
    }

    @Test
    void testAgendamentoConfirmadoNotFound() {
        // Mocks
        var agendamentoService = mock(com.caracore.cca.service.AgendamentoService.class);

        var controller = new AgendamentoPublicoController();
        ReflectionTestUtils.setField(controller, "agendamentoService", agendamentoService);

        when(agendamentoService.buscarPorId(99L)).thenReturn(Optional.empty());
        Model model = mock(Model.class);

        assertThrows(AgendamentoPublicoController.ResourceNotFoundException.class, () -> {
            controller.agendamentoConfirmado(99L, model);
        });

        verify(agendamentoService).buscarPorId(99L);
    }
}