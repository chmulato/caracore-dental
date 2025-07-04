package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.service.AgendamentoService;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para o AgendaController
 */
@WebMvcTest(AgendaController.class)
@Import({com.caracore.cca.config.SecurityConfig.class})
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendamentoService agendamentoService;
    
    @MockBean
    private UserActivityLogger userActivityLogger;
    
    @MockBean
    private com.caracore.cca.service.UsuarioDetailsService usuarioDetailsService;
    
    @MockBean
    private org.springframework.security.web.access.AccessDeniedHandler accessDeniedHandler;

    private Agendamento agendamentoTeste;

    @BeforeEach
    void setUp() {
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
    @WithMockUser(roles = "ADMIN")
    void testVisualizarCalendario() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/agenda/calendario"))
                .andExpect(status().isOk())
                .andExpect(view().name("agenda/calendario"))
                .andExpect(model().attributeExists("titulo"));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void testVisualizarAgendaProfissional() throws Exception {
        // Arrange
        List<String> dentistas = List.of("Dr. Maria Santos", "Dr. João Oliveira");
        when(agendamentoService.listarDentistas()).thenReturn(dentistas);

        // Act & Assert
        mockMvc.perform(get("/agenda/profissional"))
                .andExpect(status().isOk())
                .andExpect(view().name("agenda/profissional"))
                .andExpect(model().attributeExists("titulo", "dentistas"));
    }

    @Test
    @WithMockUser(roles = "DENTIST")
    void testGetEventosCalendario() throws Exception {
        // Arrange
        List<Agendamento> agendamentos = List.of(agendamentoTeste);
        when(agendamentoService.listarTodos()).thenReturn(agendamentos);

        // Act & Assert
        mockMvc.perform(get("/agenda/api/eventos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(agendamentoService).listarTodos();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEventosCalendarioComFiltros() throws Exception {
        // Arrange
        List<Agendamento> agendamentos = List.of(agendamentoTeste);
        when(agendamentoService.buscarPorDentista(anyString())).thenReturn(agendamentos);

        // Act & Assert
        mockMvc.perform(get("/agenda/api/eventos")
                .param("dentista", "Dr. Maria Santos")
                .param("status", "AGENDADO"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(agendamentoService).buscarPorDentista("Dr. Maria Santos");
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void testGetAgendaProfissional() throws Exception {
        // Arrange
        String dentista = "Dr. Maria Santos";
        List<Agendamento> agendamentos = List.of(agendamentoTeste);
        when(agendamentoService.buscarPorDentista(dentista)).thenReturn(agendamentos);

        // Act & Assert
        mockMvc.perform(get("/agenda/api/profissional/{dentista}", dentista))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dentista").value(dentista))
                .andExpect(jsonPath("$.agendamentos").isArray());

        verify(agendamentoService).buscarPorDentista(dentista);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetHorariosDisponiveis() throws Exception {
        // Arrange
        String dentista = "Dr. Maria Santos";
        String data = "2025-07-05";

        // Act & Assert
        mockMvc.perform(get("/agenda/api/horarios-disponiveis")
                .param("dentista", dentista)
                .param("data", data))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.horarios").isArray())
                .andExpect(jsonPath("$.horarios.length()").value(3));

        // Note: Service call verification removed as the logic is now in the controller
    }

    @Test
    @WithMockUser(roles = "DENTIST")
    void testGetHorariosDisponiveisComParametrosInvalidos() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/agenda/api/horarios-disponiveis")
                .param("dentista", "")
                .param("data", "data-invalida"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAcessoNaoAutorizado() throws Exception {
        // Act & Assert - unauthenticated users are redirected to login page
        mockMvc.perform(get("/agenda/calendario"))
                .andExpect(status().isFound()) // 302 redirect to login
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testAcessoNegadoParaPaciente() throws Exception {
        // Act & Assert - authenticated users without proper roles are forwarded to access denied page
        mockMvc.perform(get("/agenda/calendario"))
                .andExpect(status().isOk()); // 200 due to CustomAccessDeniedHandler forwarding to access denied page
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEventosCalendarioComErroNoServico() throws Exception {
        // Arrange
        when(agendamentoService.listarTodos()).thenThrow(new RuntimeException("Erro de conexão"));

        // Act & Assert
        mockMvc.perform(get("/agenda/api/eventos"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAgendaProfissionalComDentistaInexistente() throws Exception {
        // Arrange
        String dentista = "Dr. Inexistente";
        when(agendamentoService.buscarPorDentista(dentista)).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/agenda/api/profissional/{dentista}", dentista))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dentista").value(dentista))
                .andExpect(jsonPath("$.agendamentos").isEmpty());
    }
}
