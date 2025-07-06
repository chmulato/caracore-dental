package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.service.AgendamentoService;
import com.caracore.cca.service.PacienteService;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para o AgendamentoPublicoController
 * Incluindo testes para novas funcionalidades de controle de exposição pública
 */
@WebMvcTest(controllers = AgendamentoPublicoController.class)
@AutoConfigureMockMvc(addFilters = false)
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
    private com.caracore.cca.service.RateLimitService rateLimitService;
    
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
        when(agendamentoService.listarDentistasAtivos()).thenReturn(List.of("Dr. Maria Santos"));

        // Act & Assert
        mockMvc.perform(post("/public/agendamento")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paciente", "João Silva")
                .param("dentista", "Dr. Maria Santos")
                .param("dataHora", "2025-07-10T10:00:00")
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
        mockMvc.perform(get("/public/api/horarios-disponiveis")
                .param("dentista", dentista)
                .param("data", data))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        verify(agendamentoService).getHorariosDisponiveisPorData(eq(dentista), any(LocalDateTime.class));
    }

    @Test
    void testGetHorariosDisponiveisPublicoComParametrosInvalidos() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/public/api/horarios-disponiveis")
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
        when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistas);

        // Act & Assert
        mockMvc.perform(get("/public/api/dentistas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(agendamentoService).listarDentistasAtivos();
    }

    // --- NOVOS TESTES PARA CONTROLE DE EXPOSIÇÃO PÚBLICA ---

    @Test
    @DisplayName("Deve usar listarDentistasAtivos na página de agendamento público")
    void testAgendamentoOnlineUsaDentistasAtivos() throws Exception {
        // Arrange
        List<String> dentistasAtivos = Arrays.asList(
            "Dr. João Silva - Clínico Geral",
            "Dra. Maria Santos - Ortodontista"
        );
        when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivos);

        // Act & Assert
        mockMvc.perform(get("/public/agendamento"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/agendamento-online"))
                .andExpect(model().attribute("dentistas", dentistasAtivos))
                .andExpect(model().attribute("titulo", "Agendamento Online"));

        verify(agendamentoService, times(1)).listarDentistasAtivos();
        verify(agendamentoService, never()).listarDentistas(); // Não deve usar o método antigo
    }

    @Test
    @DisplayName("API pública de dentistas deve retornar apenas dentistas ativos")
    void testApiPublicaDentistasRetornaDentistasAtivos() throws Exception {
        // Arrange
        List<String> dentistasAtivos = Arrays.asList(
            "Dr. João Silva - Clínico Geral",
            "Dra. Ana Costa - Periodontista"
        );
        when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivos);

        // Act & Assert
        mockMvc.perform(get("/public/api/dentistas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("Dr. João Silva - Clínico Geral"))
                .andExpect(jsonPath("$[1]").value("Dra. Ana Costa - Periodontista"));

        verify(agendamentoService, times(1)).listarDentistasAtivos();
        verify(agendamentoService, never()).listarDentistas(); // Não deve usar o método antigo
    }

    @Test
    @DisplayName("Processamento de agendamento com erro deve usar dentistas ativos na recuperação")
    void testProcessamentoAgendamentoComErroUsaDentistasAtivos() throws Exception {
        // Arrange
        List<String> dentistasAtivos = Arrays.asList("Dr. João Silva - Clínico Geral");
        when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivos);
        
        // Simular erro no salvamento
        when(agendamentoService.salvar(any(Agendamento.class)))
                .thenThrow(new RuntimeException("Erro simulado"));

        // Act & Assert
        mockMvc.perform(post("/public/agendamento")
                .param("paciente", "João Silva")
                .param("dentista", "Dr. João Silva - Clínico Geral")
                .param("dataHora", "2025-07-10T10:00:00")
                .param("telefone", "11999999999")
                .param("email", "joao@teste.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/agendamento-online"))
                .andExpect(model().attribute("error", "Ocorreu um erro ao processar o agendamento"))
                .andExpect(model().attribute("dentistas", dentistasAtivos));

        verify(agendamentoService, times(1)).listarDentistasAtivos();
        verify(agendamentoService, never()).listarDentistas(); // Não deve usar o método antigo
    }

    @Test
    @DisplayName("Validação de campos obrigatórios deve usar dentistas ativos")
    void testValidacaoCamposObrigatoriosUsaDentistasAtivos() throws Exception {
        // Arrange
        List<String> dentistasAtivos = Arrays.asList("Dr. João Silva - Clínico Geral");
        when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivos);

        // Act & Assert - Enviar requisição com campos faltando
        mockMvc.perform(post("/public/agendamento")
                .param("paciente", "") // Campo vazio
                .param("dentista", "Dr. João Silva - Clínico Geral")
                .param("dataHora", "2025-07-10T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/agendamento-online"))
                .andExpect(model().attribute("error", "Todos os campos são obrigatórios"))
                .andExpect(model().attribute("dentistas", dentistasAtivos));

        verify(agendamentoService, times(1)).listarDentistasAtivos();
        verify(agendamentoService, never()).listarDentistas(); // Não deve usar o método antigo
        verify(agendamentoService, never()).salvar(any()); // Não deve tentar salvar
    }

    @Test
    @DisplayName("API de horários disponíveis públicos deve funcionar corretamente")
    void testApiHorariosDisponiveisPublicos() throws Exception {
        // Arrange
        String dentista = "Dr. João Silva - Clínico Geral";
        String data = "2025-07-10";
        List<String> horariosDisponiveis = Arrays.asList("08:00", "08:30", "09:00", "09:30");
        
        when(agendamentoService.getHorariosDisponiveisPorData(
            eq(dentista), any(LocalDateTime.class)))
            .thenReturn(horariosDisponiveis);

        // Act & Assert
        mockMvc.perform(get("/public/api/horarios-disponiveis")
                .param("dentista", dentista)
                .param("data", data)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("08:00"))
                .andExpect(jsonPath("$[1]").value("08:30"));

        verify(agendamentoService, times(1)).getHorariosDisponiveisPorData(eq(dentista), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("API de verificação de disponibilidade pública deve funcionar")
    void testApiVerificacaoDisponibilidadePublica() throws Exception {
        // Arrange
        String dentista = "Dr. João Silva - Clínico Geral";
        String dataHora = "2025-07-10T10:00:00";
        
        when(agendamentoService.verificarConflitoHorario(
            eq(dentista), any(LocalDateTime.class), eq(null)))
            .thenReturn(false); // Sem conflito

        // Act & Assert
        mockMvc.perform(get("/api/public/verificar-disponibilidade")
                .param("dentista", dentista)
                .param("dataHora", dataHora)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.disponivel").value(true));

        verify(agendamentoService, times(1)).verificarConflitoHorario(eq(dentista), any(LocalDateTime.class), eq(null));
    }

    @Test
    @DisplayName("API de verificação deve retornar indisponível quando há conflito")
    void testApiVerificacaoDisponibilidadeComConflito() throws Exception {
        // Arrange
        String dentista = "Dr. João Silva - Clínico Geral";
        String dataHora = "2025-07-10T10:00:00";
        
        when(agendamentoService.verificarConflitoHorario(
            eq(dentista), any(LocalDateTime.class), eq(null)))
            .thenReturn(true); // Com conflito

        // Act & Assert
        mockMvc.perform(get("/api/public/verificar-disponibilidade")
                .param("dentista", dentista)
                .param("dataHora", dataHora)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.disponivel").value(false));

        verify(agendamentoService, times(1)).verificarConflitoHorario(eq(dentista), any(LocalDateTime.class), eq(null));
    }

}
