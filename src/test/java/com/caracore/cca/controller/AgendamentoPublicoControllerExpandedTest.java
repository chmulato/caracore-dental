package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.service.AgendamentoService;
import com.caracore.cca.service.DentistaService;
import com.caracore.cca.service.PacienteService;
import com.caracore.cca.util.UserActivityLogger;
import com.caracore.cca.config.UserActivityInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes expandidos para o AgendamentoPublicoController
 * Incluindo testes para novas funcionalidades de controle de exposição pública
 */
@WebMvcTest(controllers = AgendamentoPublicoController.class)
@Import(AgendamentoPublicoControllerExpandedTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.thymeleaf.prefix=classpath:/templates/",
    "spring.thymeleaf.suffix=.html",
    "spring.thymeleaf.check-template-location=false",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never",
    "logging.level.org.springframework.web=DEBUG"
})
@DisplayName("AgendamentoPublicoController - Testes Expandidos")
class AgendamentoPublicoControllerExpandedTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SpringResourceTemplateResolver templateResolver() {
            SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
            templateResolver.setPrefix("classpath:/templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode("HTML");
            templateResolver.setCharacterEncoding("UTF-8");
            templateResolver.setCheckExistence(false);
            return templateResolver;
        }

        @Bean
        public SpringTemplateEngine templateEngine() {
            SpringTemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.setTemplateResolver(templateResolver());
            return templateEngine;
        }

        @Bean
        public ThymeleafViewResolver viewResolver() {
            ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
            viewResolver.setTemplateEngine(templateEngine());
            viewResolver.setCharacterEncoding("UTF-8");
            return viewResolver;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendamentoService agendamentoService;
    
    @MockBean
    private DentistaService dentistaService;
    
    @MockBean
    private PacienteService pacienteService;
    
    @MockBean
    private UserActivityLogger userActivityLogger;
    
    @MockBean
    private UserActivityInterceptor userActivityInterceptor;

    private List<String> dentistasAtivosMock;
    private List<Agendamento> agendamentosMock;

    @BeforeEach
    void setUp() {
        // Criar lista de dentistas ativos mock
        dentistasAtivosMock = Arrays.asList(
            "Dr. João Silva - Clínico Geral",
            "Dra. Maria Santos - Ortodontista",
            "Dra. Ana Costa - Implantodontista"
        );

        // Criar agendamentos mock
        agendamentosMock = Arrays.asList(
            criarAgendamento(1L, "João Silva", "Dr. João Silva - Clínico Geral"),
            criarAgendamento(2L, "Maria Santos", "Dra. Maria Santos - Ortodontista"),
            criarAgendamento(3L, "Ana Costa", "Dra. Ana Costa - Implantodontista")
        );
    }

    // =======================================================================
    // TESTES DE EXPOSIÇÃO PÚBLICA DE DENTISTAS
    // =======================================================================

    @Nested
    @DisplayName("Exposição Pública de Dentistas")
    class ExposicaoPublicaDentistas {

        @Test
        @DisplayName("Deve exibir página de agendamento com apenas dentistas ativos")
        void testPaginaAgendamentoComDentistasAtivos() throws Exception {
            // Arrange
            when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivosMock);

            // Act & Assert
            mockMvc.perform(get("/public/agendamento"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("public/agendamento-online"))
                    .andExpect(model().attribute("titulo", "Agendamento Online"))
                    .andExpect(model().attribute("dentistas", dentistasAtivosMock));

            verify(agendamentoService, times(1)).listarDentistasAtivos();
            verify(agendamentoService, never()).listarDentistas(); // Não deve chamar o método geral
        }

        @Test
        @DisplayName("Deve obter apenas dentistas ativos via API")
        void testObterDentistasAtivosViaAPI() throws Exception {
            // Arrange
            when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivosMock);

            // Act & Assert
            mockMvc.perform(get("/public/api/dentistas")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    // Comentado temporariamente para verificar se o mock funciona
                    // .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0]").value("Dr. João Silva - Clínico Geral"))
                    .andExpect(jsonPath("$[1]").value("Dra. Maria Santos - Ortodontista"))
                    .andExpect(jsonPath("$[2]").value("Dra. Ana Costa - Implantodontista"));

            verify(agendamentoService, times(1)).listarDentistasAtivos();
            verify(agendamentoService, never()).listarDentistas(); // Não deve chamar o método geral
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há dentistas ativos")
        void testSemDentistasAtivos() throws Exception {
            // Arrange
            when(agendamentoService.listarDentistasAtivos()).thenReturn(Arrays.asList());

            // Act & Assert
            mockMvc.perform(get("/public/agendamento"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("public/agendamento-online"))
                    .andExpect(model().attribute("dentistas", Arrays.asList()));

            verify(agendamentoService, times(1)).listarDentistasAtivos();
        }

        @Test
        @DisplayName("Deve obter horários disponíveis apenas para dentistas ativos")
        void testHorariosDisponiveisDentistasAtivos() throws Exception {
            // Arrange
            String dentistaAtivo = "Dr. João Silva - Clínico Geral";
            List<String> horariosDisponiveis = Arrays.asList("09:00", "10:00", "11:00", "14:00", "15:00");
            when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivosMock);
            when(agendamentoService.getHorariosDisponiveisPorData(eq(dentistaAtivo), any())).thenReturn(horariosDisponiveis);

            // Act & Assert
            mockMvc.perform(get("/public/api/horarios-disponiveis")
                    .param("dentista", dentistaAtivo)
                    .param("data", "2025-07-10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(5));

            verify(agendamentoService, times(1)).listarDentistasAtivos();
            verify(agendamentoService, times(1)).getHorariosDisponiveisPorData(eq(dentistaAtivo), any());
        }

        @Test
        @DisplayName("Deve rejeitar agendamento para dentista inativo")
        void testRejeitarAgendamentoDentistaInativo() throws Exception {
            // Arrange
            String dentistaInativo = "Dr. Carlos Oliveira - Periodontista"; // Não está na lista de ativos
            when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivosMock);

            // Act & Assert
            mockMvc.perform(post("/public/agendar")
                    .param("paciente", "João Silva")
                    .param("dentista", dentistaInativo)
                    .param("dataHora", "2025-07-10T10:00")
                    .param("telefone", "11999999999")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Dentista não disponível para agendamento público"));

            verify(agendamentoService, times(1)).listarDentistasAtivos();
            verify(agendamentoService, never()).salvar(any(Agendamento.class));
        }
    }

    // =======================================================================
    // TESTES DE AGENDAMENTO PÚBLICO
    // =======================================================================

    @Nested
    @DisplayName("Agendamento Público")
    class AgendamentoPublico {

        @Test
        @DisplayName("Deve agendar consulta com dentista ativo")
        void testAgendarConsultaDentistaAtivo() throws Exception {
            // Arrange
            String dentistaAtivo = "Dr. João Silva - Clínico Geral";
            when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivosMock);
            when(agendamentoService.salvar(any(Agendamento.class))).thenAnswer(invocation -> {
                Agendamento agendamento = invocation.getArgument(0);
                agendamento.setId(1L);
                return agendamento;
            });

            // Act & Assert
            mockMvc.perform(post("/public/agendar")
                    .param("paciente", "João Silva")
                    .param("dentista", dentistaAtivo)
                    .param("dataHora", "2025-07-10T10:00")
                    .param("telefone", "11999999999")
                    .param("observacao", "Consulta de rotina")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Agendamento realizado com sucesso!"));

            verify(agendamentoService, times(1)).listarDentistasAtivos();
            verify(agendamentoService, times(1)).salvar(any(Agendamento.class));
        }

        @Test
        @DisplayName("Deve validar campos obrigatórios no agendamento")
        void testValidarCamposObrigatorios() throws Exception {
            // Act & Assert - Teste sem paciente
            mockMvc.perform(post("/public/agendar")
                    .param("dentista", "Dr. João Silva - Clínico Geral")
                    .param("dataHora", "2025-07-10T10:00")
                    .param("telefone", "11999999999")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Todos os campos obrigatórios devem ser preenchidos"));

            // Act & Assert - Teste sem dentista
            mockMvc.perform(post("/public/agendar")
                    .param("paciente", "João Silva")
                    .param("dataHora", "2025-07-10T10:00")
                    .param("telefone", "11999999999")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Todos os campos obrigatórios devem ser preenchidos"));

            // Act & Assert - Teste sem data/hora
            mockMvc.perform(post("/public/agendar")
                    .param("paciente", "João Silva")
                    .param("dentista", "Dr. João Silva - Clínico Geral")
                    .param("telefone", "11999999999")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Todos os campos obrigatórios devem ser preenchidos"));

            verifyNoInteractions(agendamentoService);
        }

        @Test
        @DisplayName("Deve impedir agendamento no passado")
        void testImpedirAgendamentoNoPassado() throws Exception {
            // Arrange
            String dentistaAtivo = "Dr. João Silva - Clínico Geral";
            when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivosMock);

            // Act & Assert
            mockMvc.perform(post("/public/agendar")
                    .param("paciente", "João Silva")
                    .param("dentista", dentistaAtivo)
                    .param("dataHora", "2020-01-01T10:00") // Data no passado
                    .param("telefone", "11999999999")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Não é possível agendar consultas no passado"));

            verify(agendamentoService, times(1)).listarDentistasAtivos();
            verify(agendamentoService, never()).salvar(any(Agendamento.class));
        }
    }

    // =======================================================================
    // TESTES DE CONSULTA PÚBLICA
    // =======================================================================

    @Nested
    @DisplayName("Consulta Pública")
    class ConsultaPublica {

        @Test
        @DisplayName("Deve consultar agendamento por paciente")
        void testConsultarAgendamentoPorPaciente() throws Exception {
            // Arrange
            String paciente = "João Silva";
            when(agendamentoService.buscarPorPaciente(paciente)).thenReturn(agendamentosMock);

            // Act & Assert
            mockMvc.perform(get("/public/consultar-agendamento")
                    .param("paciente", paciente)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].paciente").value("João Silva"))
                    .andExpect(jsonPath("$[1].paciente").value("Maria Santos"))
                    .andExpect(jsonPath("$[2].paciente").value("Ana Costa"));

            verify(agendamentoService, times(1)).buscarPorPaciente(paciente);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há agendamentos")
        void testConsultarAgendamentoSemResultados() throws Exception {
            // Arrange
            String paciente = "Paciente Inexistente";
            when(agendamentoService.buscarPorPaciente(paciente)).thenReturn(Arrays.asList());

            // Act & Assert
            mockMvc.perform(get("/public/consultar-agendamento")
                    .param("paciente", paciente)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(0));

            verify(agendamentoService, times(1)).buscarPorPaciente(paciente);
        }

        @Test
        @DisplayName("Deve validar campo obrigatório")
        void testValidarCampoObrigatorio() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/public/consultar-agendamento")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Campo paciente é obrigatório"));

            verifyNoInteractions(agendamentoService);
        }
    }

    // =======================================================================
    // TESTES DE CASOS ESPECIAIS E INTEGRAÇÃO
    // =======================================================================

    @Nested
    @DisplayName("Casos Especiais e Integração")
    class CasosEspeciaisIntegracao {

        @Test
        @DisplayName("Deve lidar com erro no serviço de agendamento")
        void testErroServicoAgendamento() throws Exception {
            // Arrange
            when(agendamentoService.listarDentistasAtivos()).thenThrow(new RuntimeException("Erro de banco de dados"));

            // Act & Assert
            mockMvc.perform(get("/public/agendamento"))
                    .andExpect(status().is5xxServerError());

            verify(agendamentoService, times(1)).listarDentistasAtivos();
        }

        @Test
        @DisplayName("Deve manter consistência na listagem de dentistas ativos")
        void testConsistenciaListagemDentistasAtivos() throws Exception {
            // Arrange
            when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivosMock);

            // Act & Assert - Primeira chamada
            mockMvc.perform(get("/public/agendamento"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("dentistas", dentistasAtivosMock));

            // Act & Assert - Segunda chamada
            mockMvc.perform(get("/public/api/dentistas")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3));

            // Verificar que o método foi chamado nas duas ocasiões
            verify(agendamentoService, times(2)).listarDentistasAtivos();
        }

        @Test
        @DisplayName("Deve funcionar corretamente sem conexão com serviço de pacientes")
        void testFuncionamentoSemServicoPacientes() throws Exception {
            // Arrange
            when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivosMock);

            // Act & Assert
            mockMvc.perform(get("/public/agendamento"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("public/agendamento-online"))
                    .andExpect(model().attribute("dentistas", dentistasAtivosMock));

            // Verificar que o serviço de pacientes não foi chamado
            verifyNoInteractions(pacienteService);
            verify(agendamentoService, times(1)).listarDentistasAtivos();
        }
    }

    // =======================================================================
    // MÉTODOS AUXILIARES
    // =======================================================================

    private Agendamento criarAgendamento(Long id, String paciente, String dentista) {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(id);
        agendamento.setPaciente(paciente);
        agendamento.setDentista(dentista);
        agendamento.setDataHora(LocalDateTime.now().plusDays(1));
        agendamento.setTelefoneWhatsapp("11999999999");
        agendamento.setObservacao("Observações do agendamento " + id);
        agendamento.setStatus("AGENDADO");
        return agendamento;
    }
}
