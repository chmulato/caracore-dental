package com.caracore.cca.controller;

import com.caracore.cca.config.TestConfig;
import com.caracore.cca.service.InitService;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes para o controlador SistemaAdminController
 * Verifica o comportamento dos endpoints administrativos para gerenciar usuários padrão
 */
@WebMvcTest(SistemaAdminController.class)
@Import(TestConfig.class)
public class SistemaAdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private InitService initService;

    @MockBean
    private UserActivityLogger userActivityLogger;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        
        // Resetamos o mock antes de cada teste para garantir contagens de chamadas corretas
        reset(initService, userActivityLogger);
    }

    @Test
    @DisplayName("Acesso negado para usuários não autenticados")
    public void testAcessoNegadoUsuariosNaoAutenticados() throws Exception {
        mockMvc.perform(post("/admin/sistema/verificar-usuarios"))
                .andExpect(status().isUnauthorized());
        
        verifyNoInteractions(initService);
        verifyNoInteractions(userActivityLogger);
    }

    @Test
    @DisplayName("Acesso negado para usuários não administradores")
    @WithMockUser(username = "dentista@teste.com", roles = {"DENTIST"})
    public void testAcessoNegadoUsuariosNaoAdministradores() throws Exception {
        mockMvc.perform(post("/admin/sistema/verificar-usuarios"))
                .andExpect(status().isForbidden());
        
        verifyNoInteractions(initService);
        verifyNoInteractions(userActivityLogger);
    }

    @Test
    @DisplayName("Deve permitir verificação de usuários padrão")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testVerificarUsuariosPadrao() throws Exception {
        mockMvc.perform(post("/admin/sistema/verificar-usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().string("Verificação de usuários padrões concluída"));
        
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
    
    @Test
    @DisplayName("Deve permitir redefinição de senha com sucesso")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testRedefinirSenhaComSucesso() throws Exception {
        // Configurar mock
        String emailTeste = "dentista@teste.com";
        when(initService.redefinirSenhaUsuarioPadrao(emailTeste)).thenReturn(true);
        
        // Executar
        mockMvc.perform(post("/admin/sistema/resetar-senha/{email}", emailTeste))
                .andExpect(status().isOk())
                .andExpect(content().string("Senha redefinida com sucesso para o usuário: " + emailTeste));
        
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
    @DisplayName("Deve retornar não encontrado quando usuário não existe")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testRedefinirSenhaUsuarioNaoExistente() throws Exception {
        // Configurar mock
        String emailTeste = "naoexiste@teste.com";
        when(initService.redefinirSenhaUsuarioPadrao(emailTeste)).thenReturn(false);
        
        // Executar
        mockMvc.perform(post("/admin/sistema/resetar-senha/{email}", emailTeste))
                .andExpect(status().isNotFound());
        
        // Verificar
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao(emailTeste);
        
        // Nenhum log de auditoria deve ser registrado para senha não redefinida
        verify(userActivityLogger, times(0)).logActivity(
            anyString(), anyString(), anyString(), anyString()
        );
    }
    
    @Test
    @DisplayName("Deve usar ArgumentCaptor para verificar detalhes do log")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testLogAuditoriaDetalhadoComCaptor() throws Exception {
        // Configurar mock
        String emailTeste = "suporte@caracore.com.br";
        when(initService.redefinirSenhaUsuarioPadrao(emailTeste)).thenReturn(true);
        
        // Criar um ArgumentCaptor para capturar os parâmetros passados para o userActivityLogger
        ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detailsCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> affectedEntitiesCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> observationsCaptor = ArgumentCaptor.forClass(String.class);
        
        // Executar
        mockMvc.perform(post("/admin/sistema/resetar-senha/{email}", emailTeste))
                .andExpect(status().isOk());
        
        // Capturar os argumentos passados para o método logActivity
        verify(userActivityLogger, times(1)).logActivity(
            actionCaptor.capture(),
            detailsCaptor.capture(),
            affectedEntitiesCaptor.capture(),
            observationsCaptor.capture()
        );
        
        // Verificar os valores capturados
        assertEquals("RESET_SENHA", actionCaptor.getValue(), "Ação deve ser RESET_SENHA");
        assertEquals("Redefinição de senha para valor padrão", detailsCaptor.getValue(), "Detalhes não correspondem ao esperado");
        assertEquals("Usuário: " + emailTeste, affectedEntitiesCaptor.getValue(), "Entidades afetadas não correspondem ao esperado");
        assertEquals("Redefinição executada por administrador", observationsCaptor.getValue(), "Observação não corresponde ao esperado");
    }
    
    @Test
    @DisplayName("Deve testar função para redefinir todas as senhas de usuários padrão")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testRedefinirTodasSenhasPadrao() throws Exception {
        // Configurar mocks para todos os usuários padrão
        when(initService.redefinirSenhaUsuarioPadrao("suporte@caracore.com.br")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("dentista@teste.com")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("recepcao@teste.com")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("paciente@teste.com")).thenReturn(true);
        
        // Simular endpoint que redefine todas as senhas
        mockMvc.perform(post("/admin/sistema/redefinir-todas-senhas-padrao"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(contains("\"status\":\"concluído\"")))
                .andExpect(content().string(contains("\"suporte@caracore.com.br\":\"redefinida\"")))
                .andExpect(content().string(contains("\"dentista@teste.com\":\"redefinida\"")))
                .andExpect(content().string(contains("\"recepcao@teste.com\":\"redefinida\"")))
                .andExpect(content().string(contains("\"paciente@teste.com\":\"redefinida\"")));
        
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
    @DisplayName("Deve testar função para redefinir todas as senhas com usuário não encontrado")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testRedefinirTodasSenhasComUsuarioNaoEncontrado() throws Exception {
        // Configurar mocks para alguns usuários encontrados e outros não
        when(initService.redefinirSenhaUsuarioPadrao("suporte@caracore.com.br")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("dentista@teste.com")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("recepcao@teste.com")).thenReturn(false);
        when(initService.redefinirSenhaUsuarioPadrao("paciente@teste.com")).thenReturn(true);
        
        // Simular endpoint que redefine todas as senhas
        mockMvc.perform(post("/admin/sistema/redefinir-todas-senhas-padrao"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(contains("\"status\":\"concluído\"")))
                .andExpect(content().string(contains("\"suporte@caracore.com.br\":\"redefinida\"")))
                .andExpect(content().string(contains("\"dentista@teste.com\":\"redefinida\"")))
                .andExpect(content().string(contains("\"recepcao@teste.com\":\"falha\"")))
                .andExpect(content().string(contains("\"paciente@teste.com\":\"redefinida\"")));
        
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
    
    @Test
    @DisplayName("Deve testar endpoint de verificação de status de usuários padrão")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testStatusUsuariosPadrao() throws Exception {
        // Configurar mocks para simular usuários existentes e não existentes
        when(initService.redefinirSenhaUsuarioPadrao("suporte@caracore.com.br")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("dentista@teste.com")).thenReturn(true);
        when(initService.redefinirSenhaUsuarioPadrao("recepcao@teste.com")).thenReturn(false);
        when(initService.redefinirSenhaUsuarioPadrao("paciente@teste.com")).thenReturn(true);
        
        // Executar
        mockMvc.perform(post("/admin/sistema/status-usuarios-padrao"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(contains("\"status\":\"sucesso\"")))
                .andExpect(content().string(contains("\"suporte@caracore.com.br\"")))
                .andExpect(content().string(contains("\"existe\":true")))
                .andExpect(content().string(contains("\"perfil\":\"ROLE_ADMIN\"")))
                .andExpect(content().string(contains("\"recepcao@teste.com\"")))
                .andExpect(content().string(contains("\"existe\":false")));
        
        // Verificar chamadas ao serviço para todos os usuários padrão
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("suporte@caracore.com.br");
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("dentista@teste.com");
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("recepcao@teste.com");
        verify(initService, times(1)).redefinirSenhaUsuarioPadrao("paciente@teste.com");
        
        // Verificar log de auditoria
        verify(userActivityLogger, times(1)).logActivity(
            eq("VERIFICAR_STATUS_USUARIOS"),
            eq("Verificação do status dos usuários padrão"),
            eq(null),
            eq("Consulta administrativa")
        );
    }
}
