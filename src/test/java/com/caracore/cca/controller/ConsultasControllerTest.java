package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.model.Dentista;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.service.AgendamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para ConsultasController
 */
class ConsultasControllerTest {

    @Mock
    private AgendamentoService agendamentoService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ConsultasController consultasController;

    private MockMvc mockMvc;

    private Agendamento agendamentoTeste;
    private Paciente pacienteTeste;
    private Dentista dentistaTeste;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(consultasController).build();
        
        // Configurar dados de teste
        pacienteTeste = new Paciente();
        pacienteTeste.setId(1L);
        pacienteTeste.setNome("João Silva");
        pacienteTeste.setTelefone("(11) 99999-9999");
        pacienteTeste.setEmail("joao@email.com");

        dentistaTeste = new Dentista();
        dentistaTeste.setId(1L);
        dentistaTeste.setNome("Dr. Maria Santos");
        dentistaTeste.setCro("12345");
        dentistaTeste.setEspecialidade("Clínica Geral");

        agendamentoTeste = new Agendamento();
        agendamentoTeste.setId(1L);
        agendamentoTeste.setPaciente(pacienteTeste);
        agendamentoTeste.setDentista(dentistaTeste);
        agendamentoTeste.setDataHoraAgendamento(LocalDateTime.now().plusDays(1));
        agendamentoTeste.setStatus("AGENDADO");
        agendamentoTeste.setObservacoes("Consulta de rotina");
        agendamentoTeste.setDuracao(30);
    }

    @Test
    void testListarConsultas() {
        // Arrange
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoService.listarTodos()).thenReturn(agendamentos);

        // Act
        String viewName = consultasController.listarConsultas(model, null, null, null, null);

        // Assert
        assertEquals("consultas/lista", viewName);
        verify(agendamentoService).listarTodos();
        verify(model).addAttribute(eq("agendamentos"), any(List.class));
        verify(model).addAttribute(eq("agendamentosPorStatus"), any());
        verify(model).addAttribute(eq("consultasHoje"), any());
    }

    @Test
    void testListarConsultasComFiltroStatus() {
        // Arrange
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoService.listarPorStatus("AGENDADO")).thenReturn(agendamentos);

        // Act
        String viewName = consultasController.listarConsultas(model, "AGENDADO", null, null, null);

        // Assert
        assertEquals("consultas/lista", viewName);
        verify(agendamentoService).listarPorStatus("AGENDADO");
        verify(model).addAttribute(eq("agendamentos"), any(List.class));
    }

    @Test
    void testDetalhesConsulta() {
        // Arrange
        when(agendamentoService.buscarPorId(1L)).thenReturn(Optional.of(agendamentoTeste));

        // Act
        String viewName = consultasController.detalhesConsulta(1L, model);

        // Assert
        assertEquals("consultas/detalhes", viewName);
        verify(agendamentoService).buscarPorId(1L);
        verify(model).addAttribute("agendamento", agendamentoTeste);
    }

    @Test
    void testDetalhesConsultaNaoEncontrada() {
        // Arrange
        when(agendamentoService.buscarPorId(1L)).thenReturn(Optional.empty());

        // Act
        String viewName = consultasController.detalhesConsulta(1L, model);

        // Assert
        assertEquals("redirect:/consultas?erro=consulta-nao-encontrada", viewName);
        verify(agendamentoService).buscarPorId(1L);
    }

    @Test
    void testConfirmarConsulta() {
        // Arrange
        when(agendamentoService.confirmarAgendamento(1L)).thenReturn(true);

        // Act
        String viewName = consultasController.confirmarConsulta(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1", viewName);
        verify(agendamentoService).confirmarAgendamento(1L);
        verify(redirectAttributes).addFlashAttribute("sucesso", "Consulta confirmada com sucesso!");
    }

    @Test
    void testConfirmarConsultaErro() {
        // Arrange
        when(agendamentoService.confirmarAgendamento(1L)).thenReturn(false);

        // Act
        String viewName = consultasController.confirmarConsulta(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1", viewName);
        verify(agendamentoService).confirmarAgendamento(1L);
        verify(redirectAttributes).addFlashAttribute("erro", "Erro ao confirmar consulta.");
    }

    @Test
    void testCancelarConsulta() {
        // Arrange
        when(agendamentoService.cancelarAgendamento(1L)).thenReturn(true);

        // Act
        String viewName = consultasController.cancelarConsulta(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1", viewName);
        verify(agendamentoService).cancelarAgendamento(1L);
        verify(redirectAttributes).addFlashAttribute("sucesso", "Consulta cancelada com sucesso!");
    }

    @Test
    void testMarcarComoRealizada() {
        // Arrange
        when(agendamentoService.marcarComoRealizada(1L)).thenReturn(true);

        // Act
        String viewName = consultasController.marcarComoRealizada(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1", viewName);
        verify(agendamentoService).marcarComoRealizada(1L);
        verify(redirectAttributes).addFlashAttribute("sucesso", "Consulta marcada como realizada!");
    }

    @Test
    void testExcluirConsulta() {
        // Arrange
        doNothing().when(agendamentoService).excluir(1L);

        // Act
        String viewName = consultasController.excluirConsulta(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas", viewName);
        verify(agendamentoService).excluir(1L);
        verify(redirectAttributes).addFlashAttribute("sucesso", "Consulta excluída com sucesso!");
    }

    @Test
    void testDashboard() {
        // Arrange
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoService.listarTodos()).thenReturn(agendamentos);

        // Act
        String viewName = consultasController.dashboard(model);

        // Assert
        assertEquals("consultas/dashboard", viewName);
        verify(agendamentoService).listarTodos();
        verify(model).addAttribute(eq("totalConsultas"), anyLong());
        verify(model).addAttribute(eq("consultasAgendadas"), anyLong());
        verify(model).addAttribute(eq("consultasConfirmadas"), anyLong());
        verify(model).addAttribute(eq("taxaConfirmacao"), anyLong());
    }

    @Test
    void testFormularioReagendamento() {
        // Arrange
        when(agendamentoService.buscarPorId(1L)).thenReturn(Optional.of(agendamentoTeste));

        // Act
        String viewName = consultasController.formularioReagendamento(1L, model);

        // Assert
        assertEquals("consultas/reagendar", viewName);
        verify(agendamentoService).buscarPorId(1L);
        verify(model).addAttribute("agendamento", agendamentoTeste);
    }

    @Test
    void testReagendarConsulta() {
        // Arrange
        String novaDataHora = "2024-12-01T10:00";
        String motivoReagendamento = "Conflito de horário";
        when(agendamentoService.reagendarAgendamento(eq(1L), any(LocalDateTime.class), eq(motivoReagendamento)))
                .thenReturn(true);
        when(agendamentoService.buscarPorId(1L)).thenReturn(Optional.of(agendamentoTeste));

        // Act
        String viewName = consultasController.reagendarConsulta(1L, novaDataHora, motivoReagendamento, 
                                                              null, null, null, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1", viewName);
        verify(agendamentoService).reagendarAgendamento(eq(1L), any(LocalDateTime.class), eq(motivoReagendamento));
        verify(redirectAttributes).addFlashAttribute("sucesso", "Consulta reagendada com sucesso!");
    }

    @Test
    void testReagendarConsultaErro() {
        // Arrange
        String novaDataHora = "2024-12-01T10:00";
        String motivoReagendamento = "Conflito de horário";
        when(agendamentoService.reagendarAgendamento(eq(1L), any(LocalDateTime.class), eq(motivoReagendamento)))
                .thenReturn(false);

        // Act
        String viewName = consultasController.reagendarConsulta(1L, novaDataHora, motivoReagendamento, 
                                                              null, null, null, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1/reagendar", viewName);
        verify(agendamentoService).reagendarAgendamento(eq(1L), any(LocalDateTime.class), eq(motivoReagendamento));
        verify(redirectAttributes).addFlashAttribute("erro", "Erro ao reagendar consulta. Verifique se o horário está disponível.");
    }

    @Test
    void testVerificarConflito() {
        // Arrange
        String dataHora = "2024-12-01T10:00";
        Integer duracao = 30;
        Long dentistaId = 1L;
        when(agendamentoService.verificarConflito(any(LocalDateTime.class), eq(duracao), eq(dentistaId), isNull()))
                .thenReturn(false);

        // Act
        var response = consultasController.verificarConflito(dataHora, duracao, dentistaId, null);

        // Assert
        assertNotNull(response);
        assertFalse((Boolean) response.get("conflito"));
        assertEquals("Horário disponível", response.get("mensagem"));
    }

    @Test
    void testVerificarConflitoComConflito() {
        // Arrange
        String dataHora = "2024-12-01T10:00";
        Integer duracao = 30;
        Long dentistaId = 1L;
        when(agendamentoService.verificarConflito(any(LocalDateTime.class), eq(duracao), eq(dentistaId), isNull()))
                .thenReturn(true);

        // Act
        var response = consultasController.verificarConflito(dataHora, duracao, dentistaId, null);

        // Assert
        assertNotNull(response);
        assertTrue((Boolean) response.get("conflito"));
        assertEquals("Já existe uma consulta agendada neste horário para este dentista", response.get("mensagem"));
    }

    @Test
    void testObterHorariosDisponiveis() {
        // Arrange
        String data = "2024-12-01";
        Long dentistaId = 1L;
        when(agendamentoService.verificarConflito(any(LocalDateTime.class), eq(30), eq(dentistaId), isNull()))
                .thenReturn(false);

        // Act
        var response = consultasController.obterHorariosDisponiveis(data, dentistaId, null);

        // Assert
        assertNotNull(response);
        assertTrue(response.containsKey("horarios"));
        @SuppressWarnings("unchecked")
        List<String> horarios = (List<String>) response.get("horarios");
        assertFalse(horarios.isEmpty());
        assertTrue(horarios.contains("08:00"));
        assertTrue(horarios.contains("09:00"));
    }
}
