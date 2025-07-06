package com.caracore.cca.controller;

import com.caracore.cca.config.RelatorioTestConfig;
import com.caracore.cca.config.TestConfig;
import com.caracore.cca.model.Agendamento;
import com.caracore.cca.model.Dentista;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.service.RelatorioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RelatorioController.class)
@Import({TestConfig.class, RelatorioTestConfig.class})
public class RelatorioControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private RelatorioService relatorioService;

    private Map<String, Object> dadosRelatorioAgendamentos;
    private Map<String, Object> dadosRelatorioPacientes;
    private Map<String, Object> dadosRelatorioDesempenho;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        
        dataInicio = LocalDate.now().minusMonths(1);
        dataFim = LocalDate.now();
        
        // Configurar dados para relatório de agendamentos
        dadosRelatorioAgendamentos = new HashMap<>();
        
        Agendamento agendamento1 = new Agendamento();
        agendamento1.setId(1L);
        agendamento1.setPaciente("Paciente Teste 1");
        agendamento1.setDentista("Dentista Teste 1");
        agendamento1.setDataHora(LocalDateTime.now().minusDays(10));
        agendamento1.setStatus("AGENDADO");
        agendamento1.setDuracaoMinutos(30);
        agendamento1.setTelefoneWhatsapp("(11) 99999-9991");
        
        Agendamento agendamento2 = new Agendamento();
        agendamento2.setId(2L);
        agendamento2.setPaciente("Paciente Teste 2");
        agendamento2.setDentista("Dentista Teste 2");
        agendamento2.setDataHora(LocalDateTime.now().minusDays(5));
        agendamento2.setStatus("REALIZADO");
        agendamento2.setDuracaoMinutos(45);
        agendamento2.setTelefoneWhatsapp("(11) 99999-9992");
        
        List<Agendamento> agendamentos = Arrays.asList(agendamento1, agendamento2);
        
        Map<String, Long> resumoStatus = new HashMap<>();
        resumoStatus.put("AGENDADO", 1L);
        resumoStatus.put("REALIZADO", 1L);
        
        dadosRelatorioAgendamentos.put("agendamentos", agendamentos);
        dadosRelatorioAgendamentos.put("resumoStatus", resumoStatus);
        dadosRelatorioAgendamentos.put("totalAgendamentos", 2L);
        
        // Configurar dados para relatório de pacientes
        dadosRelatorioPacientes = new HashMap<>();
        
        Paciente paciente1 = new Paciente();
        paciente1.setId(1L);
        paciente1.setNome("Paciente Teste 1");
        paciente1.setEmail("paciente1@teste.com");
        paciente1.setTelefone("(11) 99999-9991");
        paciente1.setConsentimentoConfirmado(true);
        paciente1.setDataNascimento(LocalDate.now().minusYears(30));
        
        Paciente paciente2 = new Paciente();
        paciente2.setId(2L);
        paciente2.setNome("Paciente Teste 2");
        paciente2.setEmail("paciente2@teste.com");
        paciente2.setTelefone("(11) 99999-9992");
        paciente2.setConsentimentoConfirmado(false);
        paciente2.setDataNascimento(LocalDate.now().minusYears(25));
        
        List<Paciente> pacientes = Arrays.asList(paciente1, paciente2);
        
        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("totalPacientes", 2L);
        estatisticas.put("comConsentimento", 1L);
        estatisticas.put("comEmail", 2L);
        estatisticas.put("comTelefone", 2L);
        
        dadosRelatorioPacientes.put("pacientes", pacientes);
        dadosRelatorioPacientes.put("estatisticas", estatisticas);
        
        // Configurar dados para relatório de desempenho
        dadosRelatorioDesempenho = new HashMap<>();
        
        Dentista dentista1 = new Dentista();
        dentista1.setId(1L);
        dentista1.setNome("Dentista Teste 1");
        dentista1.setEspecialidade("Clínico Geral");
        
        Dentista dentista2 = new Dentista();
        dentista2.setId(2L);
        dentista2.setNome("Dentista Teste 2");
        dentista2.setEspecialidade("Ortodontista");
        
        List<Dentista> dentistas = Arrays.asList(dentista1, dentista2);
        
        Map<String, Object> resumoDentista1 = new HashMap<>();
        resumoDentista1.put("dentista", dentista1);
        resumoDentista1.put("totalAgendamentos", 10L);
        resumoDentista1.put("realizados", 8L);
        resumoDentista1.put("cancelados", 1L);
        resumoDentista1.put("pendentes", 1L);
        resumoDentista1.put("taxaRealizacao", 80.0);
        
        Map<String, Object> resumoDentista2 = new HashMap<>();
        resumoDentista2.put("dentista", dentista2);
        resumoDentista2.put("totalAgendamentos", 5L);
        resumoDentista2.put("realizados", 3L);
        resumoDentista2.put("cancelados", 1L);
        resumoDentista2.put("pendentes", 1L);
        resumoDentista2.put("taxaRealizacao", 60.0);
        
        List<Map<String, Object>> resumoDentistas = Arrays.asList(resumoDentista1, resumoDentista2);
        
        dadosRelatorioDesempenho.put("dentistas", dentistas);
        dadosRelatorioDesempenho.put("resumoDesempenho", resumoDentistas);
    }

    @Test
    @DisplayName("Deve permitir acesso à página de relatórios para admin")
    @WithMockUser(roles = {"ADMIN"})
    public void devePermitirAcessoAPaginaDeRelatoriosParaAdmin() throws Exception {
        mockMvc.perform(get("/relatorios"))
                .andExpect(status().isOk())
                .andExpect(view().name("relatorios/index"))
                .andExpect(model().attributeExists("dataInicio", "dataFim"));
    }

    @Test
    @DisplayName("Deve gerar relatório de agendamentos para admin")
    @WithMockUser(roles = {"ADMIN"})
    public void deveGerarRelatorioDeAgendamentosParaAdmin() throws Exception {
        when(relatorioService.gerarRelatorioAgendamentos(any(LocalDate.class), any(LocalDate.class), anyString(), anyString()))
                .thenReturn(dadosRelatorioAgendamentos);

        mockMvc.perform(get("/relatorios/agendamentos")
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString())
                .param("tipoStatus", "")
                .param("dentista", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("relatorios/agendamentos"))
                .andExpect(model().attributeExists("agendamentos", "resumoStatus", "totalAgendamentos"));
    }

    @Test
    @DisplayName("Deve gerar relatório de pacientes para admin")
    @WithMockUser(roles = {"ADMIN"})
    public void deveGerarRelatorioDePacientesParaAdmin() throws Exception {
        when(relatorioService.gerarRelatorioPacientes(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(dadosRelatorioPacientes);

        mockMvc.perform(get("/relatorios/pacientes")
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("relatorios/pacientes"))
                .andExpect(model().attributeExists("pacientes", "estatisticas"));
    }

    @Test
    @DisplayName("Deve gerar relatório de desempenho para admin")
    @WithMockUser(roles = {"ADMIN"})
    public void deveGerarRelatorioDeDesempenhoParaAdmin() throws Exception {
        when(relatorioService.gerarRelatorioDesempenho(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(dadosRelatorioDesempenho);

        mockMvc.perform(get("/relatorios/desempenho")
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("relatorios/desempenho"))
                .andExpect(model().attributeExists("dentistas", "resumoDesempenho"));
    }
}
