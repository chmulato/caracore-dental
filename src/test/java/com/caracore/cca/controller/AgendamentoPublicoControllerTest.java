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
import static org.hamcrest.Matchers.containsString;

/**
 * Testes para o AgendamentoPublicoController
 * Incluindo testes para novas funcionalidades de controle de exposição pública
 */
@WebMvcTest(controllers = AgendamentoPublicoController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.thymeleaf.prefix=classpath:/templates/",
    "spring.thymeleaf.check-template-location=false",
    "spring.profiles.active=test"
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
    private com.caracore.cca.service.CaptchaService captchaService;
    
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
        
        // Configurar mocks padrão
        when(rateLimitService.isAllowed(any())).thenReturn(true);
        when(captchaService.isEnabled()).thenReturn(false); // Desabilitado por padrão nos testes
        when(captchaService.validateCaptcha(any(), any())).thenReturn(true);
        
        // Limpar configurações que possam interferir nos testes
        reset(agendamentoService);
        
        // Por padrão, usar o novo método de listagem de dentistas ativos
        List<String> dentistasAtivos = Arrays.asList("Dr. Maria Santos", "Dr. João Silva - Clínico Geral");
        when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivos);
    }

    @Test
    void testVisualizarAgendamentoOnline() throws Exception {
        // Arrange
        List<String> dentistasAtivos = List.of("Dr. Maria Santos", "Dr. João Oliveira");
        when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivos);

        // Act & Assert
        mockMvc.perform(get("/public/agendamento"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/agendamento-online"))
                .andExpect(model().attributeExists("titulo", "dentistas"));
                
        // Verificar que o método correto foi chamado
        verify(agendamentoService, times(1)).listarDentistasAtivos();
        verify(agendamentoService, never()).listarDentistas(); // Não deve usar o método antigo
    }

    @Test
    void testProcessarAgendamentoPublico() throws Exception {
        // Arrange
        when(agendamentoService.salvar(any(Agendamento.class))).thenReturn(agendamentoTeste);
        when(agendamentoService.listarDentistasAtivos()).thenReturn(List.of("Dr. Maria Santos"));

        // Usar uma data claramente no futuro (um ano à frente)
        // Como o teste está sendo executado com data do sistema em 2025-07-10, usamos 2026-07-10
        String dataFutura = "2026-07-10T10:00:00";
        
        // Act & Assert
        mockMvc.perform(post("/public/agendamento")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paciente", "João Silva")
                .param("dentista", "Dr. Maria Santos")
                .param("dataHora", dataFutura)
                .param("telefone", "(11) 99999-9999")
                .param("email", "joao@email.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/public/agendamento-confirmado*"));

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
                .param("data", data)
                .accept(MediaType.APPLICATION_JSON))
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
                .param("data", "data-invalida")
                .accept(MediaType.APPLICATION_JSON))
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
                .param("dataHora", dataHora)
                .accept(MediaType.APPLICATION_JSON))
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
                .param("dataHora", dataHora)
                .accept(MediaType.APPLICATION_JSON))
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
        mockMvc.perform(get("/public/api/dentistas")
                .accept(MediaType.APPLICATION_JSON))
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
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
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
        
        // Usar uma data futura (um ano à frente da data do contexto de teste - 2025-07-10)
        String dataFutura = "2026-07-10T10:00:00";
        
        // Simular erro no salvamento
        when(agendamentoService.salvar(any(Agendamento.class)))
                .thenThrow(new RuntimeException("Erro simulado"));
                
        // Certifica-se de que o método antigo não está configurado 
        // para evitar que seja usado em vez do novo
        when(agendamentoService.listarDentistas()).thenReturn(Arrays.asList("Dentista Errado"));

        // Act & Assert
        mockMvc.perform(post("/public/agendamento")
                .param("paciente", "João Silva")
                .param("dentista", "Dr. João Silva - Clínico Geral")
                .param("dataHora", dataFutura)
                .param("telefone", "11999999999")
                .param("email", "joao@teste.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/agendamento-online"))
                .andExpect(model().attribute("error", "Ocorreu um erro ao processar o agendamento"))
                .andExpect(model().attribute("dentistas", dentistasAtivos));

        // Verificar que o método correto foi chamado
        verify(agendamentoService, times(1)).listarDentistasAtivos();
        verify(agendamentoService, never()).listarDentistas(); // Não deve usar o método antigo
        verify(agendamentoService, times(1)).salvar(any(Agendamento.class)); // Verificar que tentou salvar
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
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
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
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
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
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.disponivel").value(false));

        verify(agendamentoService, times(1)).verificarConflitoHorario(eq(dentista), any(LocalDateTime.class), eq(null));
    }

    @Test
    void testObterConfiguracoesRecaptcha() throws Exception {
        // Arrange
        when(captchaService.isEnabled()).thenReturn(true);
        when(captchaService.getSiteKey()).thenReturn("test-site-key");

        // Act & Assert
        mockMvc.perform(get("/public/api/recaptcha-config")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.siteKey").value("test-site-key"));
    }

    @Test
    void testObterConfiguracoesRecaptchaDesabilitado() throws Exception {
        // Arrange
        when(captchaService.isEnabled()).thenReturn(false);
        when(captchaService.getSiteKey()).thenReturn("");

        // Act & Assert
        mockMvc.perform(get("/public/api/recaptcha-config")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.siteKey").value(""));
    }

    @Test
    void testAgendarConsultaComCaptchaHabilitado() throws Exception {
        // Arrange
        when(captchaService.isEnabled()).thenReturn(true);
        when(captchaService.validateCaptcha("valid-token", "127.0.0.1")).thenReturn(true);
        when(agendamentoService.listarDentistasAtivos()).thenReturn(List.of("Dr. Maria Santos"));
        when(agendamentoService.salvar(any(Agendamento.class))).thenReturn(agendamentoTeste);

        // Data futura em horário comercial (14h)
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(1).withHour(14).withMinute(30);

        // Act & Assert
        mockMvc.perform(post("/public/agendar")
                .param("paciente", "João Silva")
                .param("dentista", "Dr. Maria Santos")
                .param("dataHora", dataFutura.toString())
                .param("telefone", "(11) 99999-9999")
                .param("captchaToken", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Agendamento realizado com sucesso!"));

        verify(captchaService).validateCaptcha("valid-token", "127.0.0.1");
    }

    @Test
    void testAgendarConsultaComCaptchaInvalido() throws Exception {
        // Arrange
        when(captchaService.isEnabled()).thenReturn(true);
        when(captchaService.validateCaptcha("invalid-token", "127.0.0.1")).thenReturn(false);

        // Data futura em horário comercial (14h)
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(1).withHour(14).withMinute(30);

        // Act & Assert
        mockMvc.perform(post("/public/agendar")
                .param("paciente", "João Silva")
                .param("dentista", "Dr. Maria Santos")
                .param("dataHora", dataFutura.toString())
                .param("telefone", "(11) 99999-9999")
                .param("captchaToken", "invalid-token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("Captcha")))
                .andExpect(content().string(containsString("lido")));

        verify(captchaService).validateCaptcha("invalid-token", "127.0.0.1");
        verify(agendamentoService, never()).salvar(any());
    }

    @Test
    void testAgendarConsultaComCaptchaDesabilitado() throws Exception {
        // Arrange
        when(captchaService.isEnabled()).thenReturn(false);
        when(agendamentoService.listarDentistasAtivos()).thenReturn(List.of("Dr. Maria Santos"));
        when(agendamentoService.salvar(any(Agendamento.class))).thenReturn(agendamentoTeste);

        // Data futura em horário comercial (14h)
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(1).withHour(14).withMinute(30);

        // Act & Assert
        mockMvc.perform(post("/public/agendar")
                .param("paciente", "João Silva")
                .param("dentista", "Dr. Maria Santos")
                .param("dataHora", dataFutura.toString())
                .param("telefone", "(11) 99999-9999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Agendamento realizado com sucesso!"));

        verify(captchaService, never()).validateCaptcha(any(), any());
    }

}
