package com.caracore.cca.controller;

import com.caracore.cca.config.TestConfig;
import com.caracore.cca.config.SecurityTestConfig;
import com.caracore.cca.service.InitService;
import com.caracore.cca.service.DentistaService;
import com.caracore.cca.model.Dentista;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Testes para o controlador SistemaAdminController
 * Verifica o comportamento dos endpoints administrativos para gerenciar usuários padrão
 * 
 * Níveis de acesso:
 * - ADMIN: acesso completo a todos os endpoints administrativos
 * - Outros perfis (DENTIST, RECEPTIONIST, PATIENT): sem acesso a nenhum endpoint administrativo
 */
@WebMvcTest(SistemaAdminController.class)
@Import({TestConfig.class, SecurityTestConfig.class})
public class SistemaAdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private InitService initService;

    @MockBean
    private UserActivityLogger userActivityLogger;

    @MockBean
    private DentistaService dentistaService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        
        // Resetamos o mock antes de cada teste para garantir contagens de chamadas corretas
        reset(initService, userActivityLogger, dentistaService);
    }

    // --- TESTES DE CONTROLE DE ACESSO ---

    @Test
    @DisplayName("Deve negar acesso para usuários não autenticados")
    @WithAnonymousUser
    public void testAcessoNegadoUsuariosNaoAutenticados() throws Exception {
        mockMvc.perform(post("/admin/sistema/verificar-usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().is3xxRedirection()); // 302 Redirect para usuários anônimos (redirecionado para página de login)
        
        verifyNoInteractions(initService);
        verifyNoInteractions(userActivityLogger);
    }

    @Test
    @DisplayName("Deve negar acesso para usuários com papel DENTIST")
    @WithMockUser(username = "dentista@teste.com", roles = {"DENTIST"})
    public void testAcessoNegadoUsuariosDentista() throws Exception {
        mockMvc.perform(post("/admin/sistema/verificar-usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isForbidden()); // 403 Forbidden para usuários não autorizados
        
        verifyNoInteractions(initService);
        verifyNoInteractions(userActivityLogger);
    }

    @Test
    @DisplayName("Deve negar acesso para usuários com papel RECEPTIONIST")
    @WithMockUser(username = "recepcao@teste.com", roles = {"RECEPTIONIST"})
    public void testAcessoNegadoUsuariosRecepcionista() throws Exception {
        mockMvc.perform(post("/admin/sistema/verificar-usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isForbidden()); // 403 Forbidden para usuários não autorizados
        
        verifyNoInteractions(initService);
        verifyNoInteractions(userActivityLogger);
    }

    @Test
    @DisplayName("Deve negar acesso para usuários com papel PATIENT")
    @WithMockUser(username = "paciente@teste.com", roles = {"PATIENT"})
    public void testAcessoNegadoUsuariosPaciente() throws Exception {
        mockMvc.perform(post("/admin/sistema/verificar-usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isForbidden()); // 403 Forbidden para usuários não autorizados
        
        verifyNoInteractions(initService);
        verifyNoInteractions(userActivityLogger);
    }

    // --- TESTES DE VERIFICAÇÃO DE USUÁRIOS PADRÃO ---

    @Test
    @DisplayName("Admin deve poder verificar usuários padrão")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testVerificarUsuariosPadrao() throws Exception {
        mockMvc.perform(post("/admin/sistema/verificar-usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensagem").value("Verificação de usuários padrões concluída"));
        // Verifica se o método foi chamado
        verify(initService, times(1)).verificarEAtualizarUsuariosPadrao();
        
        // Verifica se o log de auditoria foi registrado
        verify(userActivityLogger, times(1)).logActivity(
            eq("SISTEMA_ADMIN"),
            eq("Verificação de usuários padrões"),
            eq(null),
            eq("Verificação de usuários padrões executada por administrador")
        );
    }
    
    // --- TESTES DE REDEFINIÇÃO DE SENHA ---
    
    @Test
    @DisplayName("Admin deve poder redefinir senha com sucesso")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testRedefinirSenhaComSucesso() throws Exception {
        // Configurar mock
        String emailTeste = "dentista@teste.com";
        when(initService.redefinirSenhaUsuarioPadrao(emailTeste)).thenReturn(true);
        
        // Executar
        mockMvc.perform(post("/admin/sistema/resetar-senha/{email}", emailTeste)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensagem").value("Senha redefinida com sucesso para o usuário: " + emailTeste));
        
        // Verificar
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao(emailTeste);
        
        // Verifica se o log de auditoria foi registrado
        verify(userActivityLogger, times(1)).logActivity(
            eq("RESET_SENHA"),
            eq("Redefinição de senha para valor padrão"),
            eq("Usuário: " + emailTeste),
            eq("Redefinição executada por administrador")
        );
    }
    
    @Test
    @DisplayName("Admin deve receber não encontrado quando usuário não existe")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testRedefinirSenhaUsuarioNaoExistente() throws Exception {
        // Configurar mock
        String emailTeste = "naoexiste@teste.com";
        when(initService.redefinirSenhaUsuarioPadrao(emailTeste)).thenReturn(false);
        
        // Executar
        mockMvc.perform(post("/admin/sistema/resetar-senha/{email}", emailTeste)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isNotFound());
        
        // Verificar
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao(emailTeste);
        
        // Nenhum log de auditoria deve ser registrado para senha não redefinida
        verify(userActivityLogger, times(0)).logActivity(
            any(String.class), any(String.class), any(String.class), any(String.class)
        );
    }
    
    // --- TESTES DE REDEFINIÇÃO DE TODAS AS SENHAS ---

    @Test
    @DisplayName("Admin deve poder redefinir todas as senhas de usuários padrão")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testRedefinirTodasSenhasPadrao() throws Exception {
        // Configurar mocks para todos os usuários padrão
        when(initService.redefinirSenhaUsuarioPadrao("suporte@caracore.com.br")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("dentista@teste.com")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("recepcao@teste.com")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("paciente@teste.com")).thenReturn(true);
        
        // Simular endpoint que redefine todas as senhas
        mockMvc.perform(post("/admin/sistema/redefinir-todas-senhas-padrao")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // Verificar chamadas ao serviço
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("suporte@caracore.com.br");
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("dentista@teste.com");
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("recepcao@teste.com");
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("paciente@teste.com");
        
        // Verificar log de auditoria
        verify(userActivityLogger, times(1)).logActivity(
            eq("REDEFINIR_TODAS_SENHAS_PADRAO"),
            eq("Redefinição de todas as senhas de usuários padrão"),
            eq(null),
            eq("Operação administrativa")
        );
    }
    
    @Test
    @DisplayName("Admin deve receber relatório parcial quando alguns usuários não são encontrados")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testRedefinirTodasSenhasComUsuarioNaoEncontrado() throws Exception {
        // Configurar mocks para alguns usuários encontrados e outros não
        when(initService.redefinirSenhaUsuarioPadrao("suporte@caracore.com.br")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("dentista@teste.com")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("recepcao@teste.com")).thenReturn(false);
        when(initService.redefinirSenhaUsuarioPadrao("paciente@teste.com")).thenReturn(true);
        
        // Simular endpoint que redefine todas as senhas
        mockMvc.perform(post("/admin/sistema/redefinir-todas-senhas-padrao")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // Verificar chamadas ao serviço
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("suporte@caracore.com.br");
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("dentista@teste.com");
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("recepcao@teste.com");
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("paciente@teste.com");
        
        // Verificar log de auditoria (ainda é registrado mesmo com falhas parciais)
        verify(userActivityLogger, times(1)).logActivity(
            eq("REDEFINIR_TODAS_SENHAS_PADRAO"),
            eq("Redefinição de todas as senhas de usuários padrão"),
            eq(null),
            eq("Operação administrativa")
        );
    }

    // --- TESTES DAS NOVAS FUNCIONALIDADES DE FERRAMENTAS ADMINISTRATIVAS ---

    @Test
    @DisplayName("Admin deve obter lista de dentistas públicos com sucesso")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testObterDentistasPublicos() throws Exception {
        // Arrange
        List<Dentista> dentistasAtivos = Arrays.asList(
            criarDentista(1L, "Dr. João Silva", "Clínico Geral", true),
            criarDentista(2L, "Dra. Maria Santos", "Ortodontista", true)
        );
        when(dentistaService.listarAtivos()).thenReturn(dentistasAtivos);

        // Act & Assert
        mockMvc.perform(get("/admin/sistema/dentistas-publicos")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("sucesso"))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.dentistas").isArray());

        verify(dentistaService, times(1)).listarAtivos();
        verify(userActivityLogger, times(1)).logActivity(
            eq("LISTAR_DENTISTAS_PUBLICOS"),
            eq("Consulta de dentistas para exposição pública"),
            eq(null),
            eq("Administrador consultou lista de dentistas públicos")
        );
    }

    @Test
    @DisplayName("Admin deve alterar exposição pública de dentista com sucesso")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testAlterarExposicaoPublica() throws Exception {
        // Arrange
        when(dentistaService.alterarExposicaoPublica(eq(1L), eq(true), isNull())).thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(post("/admin/sistema/dentista/1/exposicao-publica")
                .param("exposto", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("sucesso"))
                .andExpect(jsonPath("$.dentistaId").value(1))
                .andExpect(jsonPath("$.exposto").value(true));

        verify(userActivityLogger, times(1)).logActivity(
            eq("ALTERAR_EXPOSICAO_DENTISTA"),
            eq("Alteração de exposição pública de dentista"),
            eq("Dentista ID: 1, Exposto: true"),
            eq("Administrador alterou exposição pública de dentista")
        );
    }

    @Test
    @DisplayName("Admin deve ativar agenda pública com sucesso")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testToggleAgendaPublicaAtivar() throws Exception {
        // Act & Assert
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
    public void testToggleAgendaPublicaDesativar() throws Exception {
        // Act & Assert
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
        // Act & Assert
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
    @DisplayName("Admin deve obter estatísticas de dentistas com sucesso")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testObterEstatisticasDentistas() throws Exception {
        // Arrange
        List<Dentista> todosDentistas = Arrays.asList(
            criarDentista(1L, "Dr. João Silva", "Clínico Geral", true),
            criarDentista(2L, "Dra. Maria Santos", "Ortodontista", true),
            criarDentista(3L, "Dr. Carlos Oliveira", "Periodontista", false)
        );
        List<Dentista> dentistasAtivos = Arrays.asList(
            criarDentista(1L, "Dr. João Silva", "Clínico Geral", true),
            criarDentista(2L, "Dra. Maria Santos", "Ortodontista", true)
        );

        when(dentistaService.listarTodos()).thenReturn(todosDentistas);
        when(dentistaService.listarAtivos()).thenReturn(dentistasAtivos);

        // Act & Assert
        mockMvc.perform(get("/admin/sistema/estatisticas-dentistas")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("sucesso"))
                .andExpect(jsonPath("$.estatisticas.total").value(3))
                .andExpect(jsonPath("$.estatisticas.ativos").value(2))
                .andExpect(jsonPath("$.estatisticas.inativos").value(1))
                .andExpect(jsonPath("$.estatisticas.expostos").value(2));

        verify(dentistaService, times(1)).listarTodos();
        verify(dentistaService, times(1)).listarAtivos();
    }

    // --- TESTES DE CONTROLE DE ACESSO PARA NOVAS FUNCIONALIDADES ---

    @Test
    @DisplayName("Deve negar acesso a dentistas públicos para usuário DENTIST")
    @WithMockUser(username = "dentista@teste.com", roles = {"DENTIST"})
    public void testAcessoNegadoDentistasPublicosDentist() throws Exception {
        mockMvc.perform(get("/admin/sistema/dentistas-publicos")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isForbidden()); // 403 Forbidden para usuários não autorizados

        verifyNoInteractions(dentistaService);
        verifyNoInteractions(userActivityLogger);
    }

    @Test
    @DisplayName("Deve negar acesso a toggle agenda pública para usuário RECEPTIONIST")
    @WithMockUser(username = "recepcao@teste.com", roles = {"RECEPTIONIST"})
    public void testAcessoNegadoToggleAgendaPublicaReceptionist() throws Exception {
        mockMvc.perform(post("/admin/sistema/agenda-publica/toggle")
                .param("ativa", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isForbidden()); // 403 Forbidden para usuários não autorizados

        verifyNoInteractions(userActivityLogger);
    }

    @Test
    @DisplayName("Deve negar acesso para usuário anônimo em estatísticas")
    @WithAnonymousUser
    public void testAcessoNegadoEstatisticasAnonimo() throws Exception {
        mockMvc.perform(get("/admin/sistema/estatisticas-dentistas")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().is3xxRedirection()); // Redirecionamento para login

        verifyNoInteractions(dentistaService);
    }

    // --- MÉTODOS AUXILIARES ---

    private Dentista criarDentista(Long id, String nome, String especialidade, Boolean ativo) {
        Dentista dentista = new Dentista();
        dentista.setId(id);
        dentista.setNome(nome);
        dentista.setEspecialidade(especialidade);
        dentista.setAtivo(ativo);
        dentista.setEmail(nome.toLowerCase().replace(" ", "") + "@teste.com");
        dentista.setCro("CRO" + id);
        return dentista;
    }
}
