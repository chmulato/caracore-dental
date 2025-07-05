package com.caracore.cca.controller;

import com.caracore.cca.config.MockThymeleafConfig;
import com.caracore.cca.model.Agendamento;
import com.caracore.cca.service.AgendamentoService;
import com.caracore.cca.service.PacienteService;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para o AgendamentoPublicoController
 */
@WebMvcTest(controllers = AgendamentoPublicoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MockThymeleafConfig.class)
@TestPropertySource(properties = {
    "spring.thymeleaf.prefix=classpath:/templates/",
    "spring.thymeleaf.check-template-location=false"
})
class AgendamentoPublicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendamentoService agendamentoService;
    
    @MockBean
    private PacienteService pacienteService;
    
    @MockBean
    private UserActivityLogger userActivityLogger;

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
    void testVisualizarAgendamentoOnline() throws Exception {
        // Arrange
        List<String> dentistas = List.of("Dr. Maria Santos", "Dr. João Oliveira");
        when(agendamentoService.listarDentistas()).thenReturn(dentistas);

        // Act & Assert
        mockMvc.perform(get("/public/agendamento"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/agendamento-online"))
                .andExpect(model().attributeExists("titulo", "dentistas"));
    }

    @Test
    void testProcessarAgendamentoPublico() throws Exception {
        // Arrange
        when(agendamentoService.salvar(any(Agendamento.class))).thenReturn(agendamentoTeste);

        // Act & Assert
        mockMvc.perform(post("/public/agendamento")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paciente", "João Silva")
                .param("dentista", "Dr. Maria Santos")
                .param("dataHora", "2025-07-05T10:00")
                .param("telefone", "(11) 99999-9999")
                .param("email", "joao@email.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/public/agendamento-confirmado?id=1"));

        verify(agendamentoService).salvar(any(Agendamento.class));
    }

    @Test
    void testProcessarAgendamentoPublicoComDadosInvalidos() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/public/agendamento")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paciente", "")
                .param("dentista", "")
                .param("dataHora", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("public/agendamento-online"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testVisualizarConfirmacaoAgendamento() throws Exception {
        // Arrange
        when(agendamentoService.buscarPorId(1L)).thenReturn(Optional.of(agendamentoTeste));

        // Act & Assert
        mockMvc.perform(get("/public/agendamento-confirmado")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/agendamento-confirmado"))
                .andExpect(model().attributeExists("agendamento"));
    }

    @Test
    void testVisualizarConfirmacaoAgendamentoNaoEncontrado() throws Exception {
        // Arrange
        when(agendamentoService.buscarPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/public/agendamento-confirmado")
                .param("id", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetHorariosDisponiveisPublico() throws Exception {
        // Arrange
        String dentista = "Dr. Maria Santos";
        String data = "2025-07-05";
        List<String> horarios = List.of("08:00", "08:30", "09:00");
        when(agendamentoService.getHorariosDisponiveisPorData(eq(dentista), any(LocalDateTime.class)))
                .thenReturn(horarios);

        // Act & Assert
        mockMvc.perform(get("/api/public/horarios-disponiveis")
                .param("dentista", dentista)
                .param("data", data))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.horarios").isArray())
                .andExpect(jsonPath("$.horarios.length()").value(3));

        verify(agendamentoService).getHorariosDisponiveisPorData(eq(dentista), any(LocalDateTime.class));
    }

    @Test
    void testGetHorariosDisponiveisPublicoComParametrosInvalidos() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/public/horarios-disponiveis")
                .param("dentista", "")
                .param("data", "data-invalida"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerificarDisponibilidadePublica() throws Exception {
        // Arrange
        String dentista = "Dr. Maria Santos";
        String dataHora = "2025-07-05T10:00";
        when(agendamentoService.verificarConflitoHorario(eq(dentista), any(LocalDateTime.class), isNull()))
                .thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/public/verificar-disponibilidade")
                .param("dentista", dentista)
                .param("dataHora", dataHora))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.disponivel").value(true));

        verify(agendamentoService).verificarConflitoHorario(eq(dentista), any(LocalDateTime.class), isNull());
    }

    @Test
    void testVerificarDisponibilidadePublicaIndisponivel() throws Exception {
        // Arrange
        String dentista = "Dr. Maria Santos";
        String dataHora = "2025-07-05T10:00";
        when(agendamentoService.verificarConflitoHorario(eq(dentista), any(LocalDateTime.class), isNull()))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/public/verificar-disponibilidade")
                .param("dentista", dentista)
                .param("dataHora", dataHora))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.disponivel").value(false));
    }

    @Test
    void testProcessarAgendamentoPublicoComErroNoServico() throws Exception {
        // Arrange
        when(agendamentoService.salvar(any(Agendamento.class))).thenThrow(new RuntimeException("Erro de conexão"));

        // Act & Assert
        mockMvc.perform(post("/public/agendamento")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paciente", "João Silva")
                .param("dentista", "Dr. Maria Santos")
                .param("dataHora", "2025-07-05T10:00")
                .param("telefone", "(11) 99999-9999")
                .param("email", "joao@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/agendamento-online"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testGetDentistasPublico() throws Exception {
        // Arrange
        List<String> dentistas = List.of("Dr. Maria Santos", "Dr. João Oliveira");
        when(agendamentoService.listarDentistas()).thenReturn(dentistas);

        // Act & Assert
        mockMvc.perform(get("/api/public/dentistas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dentistas").isArray())
                .andExpect(jsonPath("$.dentistas.length()").value(2));

        verify(agendamentoService).listarDentistas();
    }


}
