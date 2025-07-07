package com.caracore.cca.controller;

import com.caracore.cca.config.TestConfig;
import com.caracore.cca.config.SecurityTestConfig;
import com.caracore.cca.service.RelatorioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(RelatorioController.class)
@Import({TestConfig.class, SecurityTestConfig.class})
public class RelatorioControllerExportTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private RelatorioService relatorioService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("Deve carregar página de relatórios com scripts de exportação")
    @WithMockUser(roles = {"ADMIN"})
    public void deveCarregarPaginaRelatoriosComScriptsExportacao() throws Exception {
        mockMvc.perform(get("/relatorios"))
                .andExpect(status().isOk())
                .andExpect(view().name("relatorios/index"))
                .andExpect(model().attributeExists("dataInicio"))
                .andExpect(model().attributeExists("dataFim"));
    }

    @Test
    @DisplayName("Deve carregar relatório de agendamentos com funcionalidades de exportação")
    @WithMockUser(roles = {"ADMIN"})
    public void deveCarregarRelatorioAgendamentosComExportacao() throws Exception {
        // Preparar dados mock
        Map<String, Object> dadosMock = new HashMap<>();
        dadosMock.put("agendamentos", Arrays.asList());
        dadosMock.put("resumoStatus", new HashMap<>());
        dadosMock.put("totalAgendamentos", 0);

        when(relatorioService.gerarRelatorioAgendamentos(
                any(LocalDate.class), any(LocalDate.class), isNull(), isNull()))
                .thenReturn(dadosMock);

        mockMvc.perform(get("/relatorios/agendamentos")
                .param("dataInicio", "2025-01-01")
                .param("dataFim", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("relatorios/agendamentos"))
                .andExpect(model().attributeExists("agendamentos"))
                .andExpect(model().attributeExists("resumoStatus"))
                .andExpect(model().attributeExists("totalAgendamentos"))
                .andExpect(content().string(containsString("html2pdf.bundle.min.js")))
                .andExpect(content().string(containsString("report-exporter.js")))
                .andExpect(content().string(containsString("report-manager.js")))
                .andExpect(content().string(containsString("btnExportPDF")))
                .andExpect(content().string(containsString("btnExportCSV")));
    }

    @Test
    @DisplayName("Deve carregar relatório de pacientes com funcionalidades de exportação")
    @WithMockUser(roles = {"ADMIN"})
    public void deveCarregarRelatorioPacientesComExportacao() throws Exception {
        // Preparar dados mock
        Map<String, Object> dadosMock = new HashMap<>();
        dadosMock.put("pacientes", Arrays.asList());
        
        // Preparar estatísticas mock com todos os campos necessários
        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("totalPacientes", 100);
        estatisticas.put("comConsentimento", 85);
        estatisticas.put("comEmail", 75);
        estatisticas.put("comTelefone", 90);
        dadosMock.put("estatisticas", estatisticas);

        when(relatorioService.gerarRelatorioPacientes(
                any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(dadosMock);

        mockMvc.perform(get("/relatorios/pacientes"))
                .andExpect(status().isOk())
                .andExpect(view().name("relatorios/pacientes"))
                .andExpect(model().attributeExists("pacientes"))
                .andExpect(model().attributeExists("estatisticas"))
                .andExpect(content().string(containsString("html2pdf.bundle.min.js")))
                .andExpect(content().string(containsString("report-exporter.js")))
                .andExpect(content().string(containsString("report-manager.js")))
                .andExpect(content().string(containsString("btnExportPDF")))
                .andExpect(content().string(containsString("btnExportCSV")));
    }

    @Test
    @DisplayName("Deve carregar relatório de desempenho com funcionalidades de exportação")
    @WithMockUser(roles = {"ADMIN"})
    public void deveCarregarRelatorioDesempenhoComExportacao() throws Exception {
        // Preparar dados mock
        Map<String, Object> dadosMock = new HashMap<>();
        dadosMock.put("dentistas", Arrays.asList());
        dadosMock.put("resumoDesempenho", new HashMap<>());

        when(relatorioService.gerarRelatorioDesempenho(
                any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(dadosMock);

        mockMvc.perform(get("/relatorios/desempenho"))
                .andExpect(status().isOk())
                .andExpect(view().name("relatorios/desempenho"))
                .andExpect(model().attributeExists("dentistas"))
                .andExpect(model().attributeExists("resumoDesempenho"))
                .andExpect(content().string(containsString("html2pdf.bundle.min.js")))
                .andExpect(content().string(containsString("report-exporter.js")))
                .andExpect(content().string(containsString("report-manager.js")))
                .andExpect(content().string(containsString("btnExportPDF")));
    }

    @Test
    @DisplayName("Deve negar acesso a relatórios para usuários não autorizados")
    @WithMockUser(roles = {"PATIENT"})
    public void deveNegarAcessoRelatoriosParaUsuariosNaoAutorizados() throws Exception {
        mockMvc.perform(get("/relatorios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve permitir acesso a relatórios para dentistas")
    @WithMockUser(roles = {"DENTIST"})
    public void devePermitirAcessoRelatoriosParaDentistas() throws Exception {
        mockMvc.perform(get("/relatorios"))
                .andExpect(status().isOk())
                .andExpect(view().name("relatorios/index"));
    }

    @Test
    @DisplayName("Deve permitir acesso a relatórios para recepcionistas")
    @WithMockUser(roles = {"RECEPTIONIST"})
    public void devePermitirAcessoRelatoriosParaRecepcionistas() throws Exception {
        mockMvc.perform(get("/relatorios"))
                .andExpect(status().isOk())
                .andExpect(view().name("relatorios/index"));
    }

    @Test
    @DisplayName("Deve incluir CSS de relatórios nos templates")
    @WithMockUser(roles = {"ADMIN"})
    public void deveIncluirCSSRelatoriosNosTemplates() throws Exception {
        // Preparar dados mock
        Map<String, Object> dadosMock = new HashMap<>();
        dadosMock.put("agendamentos", Arrays.asList());
        dadosMock.put("resumoStatus", new HashMap<>());
        dadosMock.put("totalAgendamentos", 0);

        when(relatorioService.gerarRelatorioAgendamentos(
                any(LocalDate.class), any(LocalDate.class), isNull(), isNull()))
                .thenReturn(dadosMock);

        mockMvc.perform(get("/relatorios/agendamentos")
                .param("dataInicio", "2025-01-01")
                .param("dataFim", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("reports.css")));
    }

    @Test
    @DisplayName("Deve validar permissões específicas para relatório de pacientes")
    @WithMockUser(roles = {"DENTIST"})
    public void deveValidarPermissoesEspecificasParaRelatorioPacientes() throws Exception {
        // Dentistas não têm acesso ao relatório de pacientes
        mockMvc.perform(get("/relatorios/pacientes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve validar permissões específicas para relatório de desempenho")
    @WithMockUser(roles = {"RECEPTIONIST"})
    public void deveValidarPermissoesEspecificasParaRelatorioDesempenho() throws Exception {
        // Recepcionistas não têm acesso ao relatório de desempenho
        mockMvc.perform(get("/relatorios/desempenho"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve incluir elemento relatorioContent para exportação")
    @WithMockUser(roles = {"ADMIN"})
    public void deveIncluirElementoRelatorioContentParaExportacao() throws Exception {
        // Preparar dados mock
        Map<String, Object> dadosMock = new HashMap<>();
        dadosMock.put("agendamentos", Arrays.asList());
        dadosMock.put("resumoStatus", new HashMap<>());
        dadosMock.put("totalAgendamentos", 0);

        when(relatorioService.gerarRelatorioAgendamentos(
                any(LocalDate.class), any(LocalDate.class), isNull(), isNull()))
                .thenReturn(dadosMock);

        mockMvc.perform(get("/relatorios/agendamentos")
                .param("dataInicio", "2025-01-01")
                .param("dataFim", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"relatorioContent\"")));
    }

    @Test
    @DisplayName("Deve incluir botões com classes de exportação corretas")
    @WithMockUser(roles = {"ADMIN"})
    public void deveIncluirBotoesComClassesExportacaoCorretas() throws Exception {
        // Preparar dados mock
        Map<String, Object> dadosMock = new HashMap<>();
        dadosMock.put("agendamentos", Arrays.asList());
        dadosMock.put("resumoStatus", new HashMap<>());
        dadosMock.put("totalAgendamentos", 0);

        when(relatorioService.gerarRelatorioAgendamentos(
                any(LocalDate.class), any(LocalDate.class), isNull(), isNull()))
                .thenReturn(dadosMock);

        mockMvc.perform(get("/relatorios/agendamentos")
                .param("dataInicio", "2025-01-01")
                .param("dataFim", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("export-button")))
                .andExpect(content().string(containsString("bi-file-earmark-pdf")))
                .andExpect(content().string(containsString("bi-file-earmark-excel")));
    }
}
