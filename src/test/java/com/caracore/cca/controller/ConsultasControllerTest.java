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
        agendamentoTeste.setPaciente("João Silva");
        agendamentoTeste.setDentista("Dr. Maria Santos");
        agendamentoTeste.setDataHora(LocalDateTime.now().plusDays(1));
        agendamentoTeste.setStatus("AGENDADO");
        agendamentoTeste.setObservacao("Consulta de rotina");
        agendamentoTeste.setDuracaoMinutos(30);
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
        when(agendamentoService.listarTodos()).thenReturn(agendamentos);

        // Act
        String viewName = consultasController.listarConsultas(model, "AGENDADO", null, null, null);

        // Assert
        assertEquals("consultas/lista", viewName);
        verify(agendamentoService).listarTodos();
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
        verify(redirectAttributes).addFlashAttribute("erro", "Erro ao confirmar consulta. Consulta não encontrada.");
    }

    @Test
    void testCancelarConsulta() {
        // Arrange
        when(agendamentoService.cancelarAgendamento(1L, "Motivo teste")).thenReturn(true);

        // Act
        String viewName = consultasController.cancelarConsulta(1L, "Motivo teste", redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1", viewName);
        verify(agendamentoService).cancelarAgendamento(1L, "Motivo teste");
        verify(redirectAttributes).addFlashAttribute("sucesso", "Consulta cancelada com sucesso!");
    }

    @Test
    void testCancelarConsultaFalha() {
        // Arrange
        when(agendamentoService.cancelarAgendamento(1L, "Motivo teste")).thenReturn(false);

        // Act
        String viewName = consultasController.cancelarConsulta(1L, "Motivo teste", redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1", viewName);
        verify(agendamentoService).cancelarAgendamento(1L, "Motivo teste");
        verify(redirectAttributes).addFlashAttribute("erro", "Erro ao cancelar consulta. Consulta não encontrada.");
    }

    @Test
    void testRealizarConsulta() {
        // Arrange
        when(agendamentoService.marcarComoRealizado(1L)).thenReturn(true);

        // Act
        String viewName = consultasController.realizarConsulta(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1", viewName);
        verify(agendamentoService).marcarComoRealizado(1L);
        verify(redirectAttributes).addFlashAttribute("sucesso", "Consulta marcada como realizada!");
    }

    @Test
    void testRealizarConsultaFalha() {
        // Arrange
        when(agendamentoService.marcarComoRealizado(1L)).thenReturn(false);

        // Act
        String viewName = consultasController.realizarConsulta(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1", viewName);
        verify(agendamentoService).marcarComoRealizado(1L);
        verify(redirectAttributes).addFlashAttribute("erro", "Erro ao marcar consulta como realizada. Consulta não encontrada.");
    }

    @Test
    void testExcluirConsulta() {
        // Arrange
        when(agendamentoService.excluir(1L)).thenReturn(true);

        // Act
        String viewName = consultasController.excluirConsulta(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas", viewName);
        verify(agendamentoService).excluir(1L);
        verify(redirectAttributes).addFlashAttribute("sucesso", "Consulta excluída permanentemente!");
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
        when(agendamentoService.reagendar(eq(1L), any(LocalDateTime.class))).thenReturn(true);

        // Act
        String viewName = consultasController.reagendarConsulta(1L, novaDataHora, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1", viewName);
        verify(agendamentoService).reagendar(eq(1L), any(LocalDateTime.class));
        verify(redirectAttributes).addFlashAttribute(eq("sucesso"), any(String.class));
    }

    @Test
    void testReagendarConsultaErro() {
        // Arrange
        String novaDataHora = "2024-12-01T10:00";
        when(agendamentoService.reagendar(eq(1L), any(LocalDateTime.class))).thenReturn(false);

        // Act
        String viewName = consultasController.reagendarConsulta(1L, novaDataHora, redirectAttributes);

        // Assert
        assertEquals("redirect:/consultas/1", viewName);
        verify(agendamentoService).reagendar(eq(1L), any(LocalDateTime.class));
        verify(redirectAttributes).addFlashAttribute("erro", "Erro ao reagendar consulta. Consulta não encontrada.");
    }

    @Test
    void testVerificarConflito() {
        // Arrange
        String dataHora = "2024-12-01T10:00";
        Integer duracao = 30;
        Long dentistaId = 1L;
        when(agendamentoService.buscarPorDentistaEPeriodo(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        // Act
        var response = consultasController.verificarConflito(dataHora, duracao, dentistaId, null);

        // Assert
        assertNotNull(response);
        verify(agendamentoService).buscarPorDentistaEPeriodo(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testVerificarConflitoComConflito() {
        // Arrange
        String dataHora = "2024-12-01T10:00";
        Integer duracao = 30;
        Long dentistaId = 1L;
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoService.buscarPorDentistaEPeriodo(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(agendamentos);

        // Act
        var response = consultasController.verificarConflito(dataHora, duracao, dentistaId, null);

        // Assert
        assertNotNull(response);
        verify(agendamentoService).buscarPorDentistaEPeriodo(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testObterHorariosDisponiveis() {
        // Arrange
        String data = "2024-12-01";
        Long dentistaId = 1L;

        // Act
        var response = consultasController.obterHorariosDisponiveis(data, dentistaId, null);

        // Assert
        assertNotNull(response);
        assertTrue(response.containsKey("horarios"));
        @SuppressWarnings("unchecked")
        List<String> horarios = (List<String>) response.get("horarios");
        assertFalse(horarios.isEmpty());
    }
}
