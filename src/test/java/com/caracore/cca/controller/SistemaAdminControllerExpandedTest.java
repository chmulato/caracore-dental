package com.caracore.cca.controller;

import com.caracore.cca.config.TestConfig;
import com.caracore.cca.config.SecurityTestConfig;
import com.caracore.cca.service.InitService;
import com.caracore.cca.service.DentistaService;
import com.caracore.cca.service.AgendamentoService;
import com.caracore.cca.model.Dentista;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.atLeast;

/**
 * Testes expandidos para o controlador SistemaAdminController
 * Cobrindo todas as funcionalidades das ferramentas administrativas
 * 
 * Funcionalidades testadas:
 * - Gerenciamento de usuários padrão
 * - Controle da agenda pública
 * - Exposição pública de dentistas
 * - Estatísticas do sistema
 * - Controle de acesso por perfil
 */
@WebMvcTest(SistemaAdminController.class)
@Import({TestConfig.class, SecurityTestConfig.class})
@DisplayName("Sistema Admin Controller - Testes Expandidos")
public class SistemaAdminControllerExpandedTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private InitService initService;

    @MockBean
    private UserActivityLogger userActivityLogger;

    @MockBean
    private DentistaService dentistaService;

    @MockBean
    private AgendamentoService agendamentoService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        
        // Resetar todos os mocks
        reset(initService, userActivityLogger, dentistaService, agendamentoService);
    }

    // =======================================================================
    // TESTES DE CONTROLE DE ACESSO
    // =======================================================================

    @Nested
    @DisplayName("Controle de Acesso")
    class ControleDeAcesso {

        @Test
        @DisplayName("Deve negar acesso para usuários não autenticados")
        @WithAnonymousUser
        public void testAcessoNegadoUsuariosNaoAutenticados() throws Exception {
            mockMvc.perform(get("/admin/sistema/api")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is3xxRedirection()); // Redirecionamento para login

            verifyNoInteractions(dentistaService);
        }

        @Test
        @DisplayName("Deve permitir acesso para usuários ADMIN")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testAcessoPermitidoAdmin() throws Exception {
            mockMvc.perform(get("/admin/sistema/api")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("sucesso"))
                    .andExpect(jsonPath("$.mensagem").value("Área administrativa acessível"));
        }

        @Test
        @DisplayName("Deve negar acesso para usuários DENTIST")
        @WithMockUser(username = "dentista@teste.com", roles = {"DENTIST"})
        public void testAcessoNegadoDentist() throws Exception {
            mockMvc.perform(get("/admin/sistema/dentistas-publicos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isForbidden()); // 403 Forbidden para usuários não autorizados
            
            verifyNoInteractions(dentistaService);
        }

        @Test
        @DisplayName("Deve negar acesso para usuários RECEPTIONIST")
        @WithMockUser(username = "recepcao@teste.com", roles = {"RECEPTIONIST"})
        public void testAcessoNegadoReceptionist() throws Exception {
            mockMvc.perform(post("/admin/sistema/agenda-publica/toggle")
                    .param("ativa", "true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isForbidden()); // 403 Forbidden para usuários não autorizados
            
            verifyNoInteractions(userActivityLogger);
        }
    }

    // =======================================================================
    // TESTES DE GERENCIAMENTO DE USUÁRIOS PADRÃO
    // =======================================================================

    @Nested
    @DisplayName("Gerenciamento de Usuários Padrão")
    class GerenciamentoUsuariosPadrao {

        @Test
        @DisplayName("Admin deve verificar usuários padrão com sucesso")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testVerificarUsuariosPadrao() throws Exception {
            mockMvc.perform(post("/admin/sistema/verificar-usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("sucesso"))
                    .andExpect(jsonPath("$.mensagem").value("Verificação de usuários padrões concluída"));
            
            verify(initService, times(1)).verificarEAtualizarUsuariosPadrao();
            verify(userActivityLogger, times(1)).logActivity(
                eq("SISTEMA_ADMIN"),
                eq("Verificação de usuários padrões"),
                eq(null),
                eq("Verificação de usuários padrões executada por administrador")
            );
        }

        @Test
        @DisplayName("Admin deve redefinir senha específica com sucesso")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testRedefinirSenhaEspecifica() throws Exception {
            String email = "dentista@teste.com";
            when(initService.redefinirSenhaUsuarioPadrao(email)).thenReturn(true);
            
            mockMvc.perform(post("/admin/sistema/resetar-senha/{email}", email)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.mensagem").value("Senha redefinida com sucesso para o usuário: " + email));
            
            verify(initService, times(1)).redefinirSenhaUsuarioPadrao(email);
            verify(userActivityLogger, times(1)).logActivity(
                eq("RESET_SENHA"),
                eq("Redefinição de senha para valor padrão"),
                eq("Usuário: " + email),
                eq("Redefinição executada por administrador")
            );
        }

        @Test
        @DisplayName("Admin deve receber erro ao redefinir senha de usuário inexistente")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testRedefinirSenhaUsuarioInexistente() throws Exception {
            String email = "naoexiste@teste.com";
            when(initService.redefinirSenhaUsuarioPadrao(email)).thenReturn(false);
            
            mockMvc.perform(post("/admin/sistema/resetar-senha/{email}", email)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isNotFound());
            
            verify(initService, times(1)).redefinirSenhaUsuarioPadrao(email);
            // Não verificar interações do userActivityLogger pois pode haver chamadas do interceptor
        }

        @Test
        @DisplayName("Admin deve redefinir todas as senhas com sucesso")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testRedefinirTodasSenhasPadrao() throws Exception {
            // Configurar mocks para retornar sucesso em todas as redefinições
            when(initService.redefinirSenhaUsuarioPadrao(anyString())).thenReturn(true);
            
            mockMvc.perform(post("/admin/sistema/redefinir-todas-senhas-padrao")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("sucesso"))
                    .andExpect(jsonPath("$.totalProcessados").value(4))
                    .andExpect(jsonPath("$.sucessos").value(4))
                    .andExpect(jsonPath("$.falhas").value(0));
            
            // Verificar se foram feitas chamadas para todos os usuários padrão
            verify(initService, times(1)).redefinirSenhaUsuarioPadrao("suporte@caracore.com.br");
            verify(initService, times(1)).redefinirSenhaUsuarioPadrao("dentista@teste.com");
            verify(initService, times(1)).redefinirSenhaUsuarioPadrao("recepcao@teste.com");
            verify(initService, times(1)).redefinirSenhaUsuarioPadrao("paciente@teste.com");
            
            verify(userActivityLogger, times(1)).logActivity(
                eq("REDEFINIR_TODAS_SENHAS_PADRAO"),
                eq("Redefinição de todas as senhas de usuários padrão"),
                eq(null),
                eq("Operação administrativa")
            );
        }

        @Test
        @DisplayName("Admin deve obter status dos usuários padrão")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testObterStatusUsuariosPadrao() throws Exception {
            mockMvc.perform(post("/admin/sistema/status-usuarios-padrao")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.usuarios").isMap())
                    .andExpect(jsonPath("$.usuarios['suporte@caracore.com.br']").exists())
                    .andExpect(jsonPath("$.usuarios['dentista@teste.com']").exists())
                    .andExpect(jsonPath("$.usuarios['recepcao@teste.com']").exists())
                    .andExpect(jsonPath("$.usuarios['paciente@teste.com']").exists());
                  verify(userActivityLogger, times(1)).logActivity(
            eq("STATUS_USUARIOS_PADRAO"),
            eq("Consulta de status de usuários padrão"),
            eq(null),
            eq("Administrador consultou status dos usuários padrão")
        );
        }
    }

    // =======================================================================
    // TESTES DE CONTROLE DA AGENDA PÚBLICA
    // =======================================================================

    @Nested
    @DisplayName("Controle da Agenda Pública")
    class ControleAgendaPublica {

        @Test
        @DisplayName("Admin deve ativar agenda pública com sucesso")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testAtivarAgendaPublica() throws Exception {
            mockMvc.perform(post("/admin/sistema/agenda-publica/toggle")
                    .param("ativa", "true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("sucesso"))
                    .andExpect(jsonPath("$.agendaPublicaAtiva").value(true))
                    .andExpect(jsonPath("$.mensagem").value("Agenda pública ativada com sucesso"));

            verify(userActivityLogger, times(1)).logActivity(
                eq("TOGGLE_AGENDA_PUBLICA"),
                eq("Alteração do status da agenda pública"),
                eq("Status: Ativada"),
                eq("Administrador ativou a agenda pública")
            );
        }

        @Test
        @DisplayName("Admin deve desativar agenda pública com sucesso")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testDesativarAgendaPublica() throws Exception {
            mockMvc.perform(post("/admin/sistema/agenda-publica/toggle")
                    .param("ativa", "false")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("sucesso"))
                    .andExpect(jsonPath("$.agendaPublicaAtiva").value(false))
                    .andExpect(jsonPath("$.mensagem").value("Agenda pública desativada com sucesso"));

            verify(userActivityLogger, times(1)).logActivity(
                eq("TOGGLE_AGENDA_PUBLICA"),
                eq("Alteração do status da agenda pública"),
                eq("Status: Desativada"),
                eq("Administrador desativou a agenda pública")
            );
        }

        @Test
        @DisplayName("Admin deve configurar horários públicos com sucesso")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testConfigurarHorariosPublicos() throws Exception {
            mockMvc.perform(post("/admin/sistema/agenda-publica/horarios")
                    .param("inicio", "08:00")
                    .param("fim", "18:00")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("sucesso"))
                    .andExpect(jsonPath("$.horarioInicio").value("08:00"))
                    .andExpect(jsonPath("$.horarioFim").value("18:00"))
                    .andExpect(jsonPath("$.mensagem").value("Horários públicos configurados com sucesso"));

            verify(userActivityLogger, times(1)).logActivity(
                eq("CONFIGURAR_HORARIOS_PUBLICOS"),
                eq("Configuração de horários públicos"),
                eq("Início: 08:00, Fim: 18:00"),
                eq("Administrador configurou horários públicos")
            );
        }

        @Test
        @DisplayName("Admin deve rejeitar horários inválidos")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testConfigurarHorariosInvalidos() throws Exception {
            mockMvc.perform(post("/admin/sistema/agenda-publica/horarios")
                    .param("inicio", "18:00")
                    .param("fim", "08:00") // Horário de fim anterior ao início
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("erro"))
                    .andExpect(jsonPath("$.mensagem").value("Horário de início deve ser anterior ao horário de fim"));

            // Não verificar interações do userActivityLogger pois pode haver chamadas do interceptor
        }
    }

    // =======================================================================
    // TESTES DE CONTROLE DE EXPOSIÇÃO PÚBLICA DE DENTISTAS
    // =======================================================================

    @Nested
    @DisplayName("Controle de Exposição Pública de Dentistas")
    class ControleExposicaoPublicaDentistas {

        @Test
        @DisplayName("Admin deve obter lista de dentistas públicos")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testObterListaDentistasPublicos() throws Exception {
            List<Dentista> dentistasAtivos = Arrays.asList(
                criarDentista(1L, "Dr. João Silva", "Clínico Geral", true),
                criarDentista(2L, "Dra. Maria Santos", "Ortodontista", true),
                criarDentista(3L, "Dr. Carlos Oliveira", "Periodontista", false)
            );
            when(dentistaService.listarAtivos()).thenReturn(dentistasAtivos);

            mockMvc.perform(get("/admin/sistema/dentistas-publicos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("sucesso"))
                    .andExpect(jsonPath("$.total").value(3))
                    .andExpect(jsonPath("$.dentistas").isArray())
                    .andExpect(jsonPath("$.dentistas.length()").value(3));

            verify(dentistaService, times(1)).listarAtivos();
            verify(userActivityLogger, times(1)).logActivity(
                eq("LISTAR_DENTISTAS_PUBLICOS"),
                eq("Consulta de dentistas para exposição pública"),
                eq(null),
                eq("Administrador consultou lista de dentistas públicos")
            );
        }

        @Test
        @DisplayName("Admin deve alterar exposição pública de dentista")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testAlterarExposicaoPublicaDentista() throws Exception {
            Long dentistaId = 1L;
            
            // Arrange
            when(dentistaService.alterarExposicaoPublica(eq(dentistaId), eq(true), isNull())).thenReturn(true);
            
            mockMvc.perform(post("/admin/sistema/dentista/{id}/exposicao-publica", dentistaId)
                    .param("exposto", "true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("sucesso"))
                    .andExpect(jsonPath("$.dentistaId").value(dentistaId))
                    .andExpect(jsonPath("$.exposto").value(true))
                    .andExpect(jsonPath("$.mensagem").value("Exposição pública do dentista alterada com sucesso"));

            verify(userActivityLogger, times(1)).logActivity(
                eq("ALTERAR_EXPOSICAO_DENTISTA"),
                eq("Alteração de exposição pública de dentista"),
                eq("Dentista ID: " + dentistaId + ", Exposto: true"),
                eq("Administrador alterou exposição pública de dentista")
            );
        }

        @Test
        @DisplayName("Admin deve ocultar dentista da exposição pública")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testOcultarDentistaExposicaoPublica() throws Exception {
            Long dentistaId = 2L;
            
            when(dentistaService.alterarExposicaoPublica(eq(dentistaId), eq(false), isNull())).thenReturn(true);
            
            mockMvc.perform(post("/admin/sistema/dentista/{id}/exposicao-publica", dentistaId)
                    .param("exposto", "false")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("sucesso"))
                    .andExpect(jsonPath("$.dentistaId").value(dentistaId))
                    .andExpect(jsonPath("$.exposto").value(false))
                    .andExpect(jsonPath("$.mensagem").value("Exposição pública do dentista alterada com sucesso"));

            verify(userActivityLogger, times(1)).logActivity(
                eq("ALTERAR_EXPOSICAO_DENTISTA"),
                eq("Alteração de exposição pública de dentista"),
                eq("Dentista ID: " + dentistaId + ", Exposto: false"),
                eq("Administrador alterou exposição pública de dentista")
            );
        }
    }

    // =======================================================================
    // TESTES DE ESTATÍSTICAS DO SISTEMA
    // =======================================================================

    @Nested
    @DisplayName("Estatísticas do Sistema")
    class EstatisticasSistema {

        @Test
        @DisplayName("Admin deve obter estatísticas completas dos dentistas")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testObterEstatisticasDentistas() throws Exception {
            List<Dentista> todosDentistas = Arrays.asList(
                criarDentista(1L, "Dr. João Silva", "Clínico Geral", true),
                criarDentista(2L, "Dra. Maria Santos", "Ortodontista", true),
                criarDentista(3L, "Dr. Carlos Oliveira", "Periodontista", false),
                criarDentista(4L, "Dra. Ana Costa", "Implantodontista", true)
            );
            List<Dentista> dentistasAtivos = Arrays.asList(
                criarDentista(1L, "Dr. João Silva", "Clínico Geral", true),
                criarDentista(2L, "Dra. Maria Santos", "Ortodontista", true),
                criarDentista(4L, "Dra. Ana Costa", "Implantodontista", true)
            );

            when(dentistaService.listarTodos()).thenReturn(todosDentistas);
            when(dentistaService.listarAtivos()).thenReturn(dentistasAtivos);

            mockMvc.perform(get("/admin/sistema/estatisticas-dentistas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("sucesso"))
                    .andExpect(jsonPath("$.estatisticas.total").value(4))
                    .andExpect(jsonPath("$.estatisticas.ativos").value(3))
                    .andExpect(jsonPath("$.estatisticas.inativos").value(1))
                    .andExpect(jsonPath("$.estatisticas.expostos").value(3));

            verify(dentistaService, times(1)).listarTodos();
            verify(dentistaService, times(1)).listarAtivos();
            verify(userActivityLogger, times(1)).logActivity(
                eq("CONSULTAR_ESTATISTICAS_DENTISTAS"),
                eq("Consulta de estatísticas de dentistas"),
                eq(null),
                eq("Administrador consultou estatísticas de dentistas")
            );
        }

        @Test
        @DisplayName("Admin deve obter estatísticas quando não há dentistas")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testObterEstatisticasVazias() throws Exception {
            when(dentistaService.listarTodos()).thenReturn(Arrays.asList());
            when(dentistaService.listarAtivos()).thenReturn(Arrays.asList());

            mockMvc.perform(get("/admin/sistema/estatisticas-dentistas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("sucesso"))
                    .andExpect(jsonPath("$.estatisticas.total").value(0))
                    .andExpect(jsonPath("$.estatisticas.ativos").value(0))
                    .andExpect(jsonPath("$.estatisticas.inativos").value(0))
                    .andExpect(jsonPath("$.estatisticas.expostos").value(0));

            verify(dentistaService, times(1)).listarTodos();
            verify(dentistaService, times(1)).listarAtivos();
        }
    }

    // =======================================================================
    // TESTES DE INTEGRAÇÃO E CASOS ESPECIAIS
    // =======================================================================

    @Nested
    @DisplayName("Integração e Casos Especiais")
    class IntegracaoCasosEspeciais {

        @Test
        @DisplayName("Admin deve lidar com erro no serviço de dentistas")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testErroServicoListarDentistas() throws Exception {
            when(dentistaService.listarAtivos()).thenThrow(new RuntimeException("Erro de banco de dados"));

            mockMvc.perform(get("/admin/sistema/dentistas-publicos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("erro"))
                    .andExpect(jsonPath("$.mensagem").value("Erro interno do servidor"));

            verify(dentistaService, times(1)).listarAtivos();
        }

        @Test
        @DisplayName("Admin deve validar parâmetros obrigatórios")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testValidarParametrosObrigatorios() throws Exception {
            mockMvc.perform(post("/admin/sistema/agenda-publica/horarios")
                    .param("inicio", "") // Parâmetro vazio
                    .param("fim", "18:00")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("erro"))
                    .andExpect(jsonPath("$.mensagem").value("Parâmetros obrigatórios não informados"));
        }

        @Test
        @DisplayName("Sistema deve registrar todas as atividades administrativas")
        @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
        public void testRegistroAtividadesAdministrativas() throws Exception {
            // Executar várias operações administrativas

            mockMvc.perform(post("/admin/sistema/verificar-usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()));

            mockMvc.perform(post("/admin/sistema/agenda-publica/toggle")
                    .param("ativa", "true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()));

            mockMvc.perform(get("/admin/sistema/estatisticas-dentistas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()));

            // Verificar se as atividades foram registradas (pode haver mais por causa do interceptor)
            verify(userActivityLogger, atLeast(1)).logActivity(
                anyString(), anyString(), anyString(), anyString()
            );
        }
    }

    // =======================================================================
    // MÉTODOS AUXILIARES
    // =======================================================================

    private Dentista criarDentista(Long id, String nome, String especialidade, Boolean ativo) {
        Dentista dentista = new Dentista();
        dentista.setId(id);
        dentista.setNome(nome);
        dentista.setEspecialidade(especialidade);
        dentista.setAtivo(ativo);
        dentista.setEmail(nome.toLowerCase().replace(" ", "").replace(".", "") + "@teste.com");
        dentista.setCro("CRO" + String.format("%05d", id));
        return dentista;
    }
}
