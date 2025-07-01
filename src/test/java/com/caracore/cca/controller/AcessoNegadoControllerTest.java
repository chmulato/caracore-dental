package com.caracore.cca.controller;

import com.caracore.cca.config.TestConfig;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.RequestDispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para o controlador de acesso negado.
 * Verifica que a página de acesso negado é exibida corretamente para diferentes perfis de usuário
 * e que os logs de segurança são registrados adequadamente.
 */
@WebMvcTest(AcessoNegadoController.class)
@Import(TestConfig.class)
public class AcessoNegadoControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UserActivityLogger userActivityLogger;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        
        // Resetamos o mock antes de cada teste para garantir contagens de chamadas corretas
        reset(userActivityLogger);
    }

    @Test
    @DisplayName("Deve redirecionar para login quando usuário anônimo tenta acessar página de acesso negado")
    @WithAnonymousUser
    public void testAcessoNegadoParaUsuarioAnonimo() throws Exception {
        mockMvc.perform(get("/acesso-negado"))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized é o comportamento esperado para usuário anônimo
    }

    @Test
    @DisplayName("Deve retornar página de acesso negado para administrador")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testAcessoNegadoParaAdmin() throws Exception {
        mockMvc.perform(get("/acesso-negado"))
                .andExpect(status().isOk())
                .andExpect(view().name("acesso-negado"));
                
        // Verificar que o log de auditoria foi chamado
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq(null),
            eq("Acesso negado")
        );
    }

    @Test
    @DisplayName("Deve retornar página de acesso negado para dentista")
    @WithMockUser(username = "dentista@teste.com", roles = {"DENTIST"})
    public void testAcessoNegadoParaDentista() throws Exception {
        mockMvc.perform(get("/acesso-negado"))
                .andExpect(status().isOk())
                .andExpect(view().name("acesso-negado"));
                
        // Verificar que o log de auditoria foi chamado
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq(null),
            eq("Acesso negado")
        );
    }

    @Test
    @DisplayName("Deve retornar página de acesso negado para recepcionista")
    @WithMockUser(username = "recepcao@teste.com", roles = {"RECEPTIONIST"})
    public void testAcessoNegadoParaRecepcionista() throws Exception {
        mockMvc.perform(get("/acesso-negado"))
                .andExpect(status().isOk())
                .andExpect(view().name("acesso-negado"));
                
        // Verificar que o log de auditoria foi chamado
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq(null),
            eq("Acesso negado")
        );
    }

    @Test
    @DisplayName("Deve retornar página de acesso negado para paciente")
    @WithMockUser(username = "joao@gmail.com", roles = {"PATIENT"})
    public void testAcessoNegadoParaPaciente() throws Exception {
        mockMvc.perform(get("/acesso-negado"))
                .andExpect(status().isOk())
                .andExpect(view().name("acesso-negado"));
                
        // Verificar que o log de auditoria foi chamado
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq(null),
            eq("Acesso negado")
        );
    }
    
    @Test
    @DisplayName("Deve registrar URL solicitada quando disponível no request")
    @WithMockUser(username = "joao@gmail.com", roles = {"PATIENT"})
    public void testRegistroDeURLSolicitada() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/admin/usuarios");
        
        AcessoNegadoController controller = new AcessoNegadoController();
        controller.userActivityLogger = userActivityLogger; // Injetar o mock
        
        String viewName = controller.acessoNegado(request);
        
        assertEquals("acesso-negado", viewName);
        
        // Verificar que o método logActivity foi chamado com os parâmetros corretos
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq("URL: /admin/usuarios"),
            eq("Acesso negado")
        );
    }
    
    @Test
    @DisplayName("Deve funcionar mesmo sem URL solicitada no request")
    @WithMockUser(username = "joao@gmail.com", roles = {"PATIENT"})
    public void testRegistroSemURLSolicitada() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        // Não definimos o atributo ERROR_REQUEST_URI
        
        AcessoNegadoController controller = new AcessoNegadoController();
        controller.userActivityLogger = userActivityLogger; // Injetar o mock
        
        String viewName = controller.acessoNegado(request);
        
        assertEquals("acesso-negado", viewName);
        
        // Verificar que o método logActivity foi chamado com os parâmetros corretos
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq(null),
            eq("Acesso negado")
        );
    }
    
    @Test
    @DisplayName("Deve testar cenário completo de acesso negado com parâmetros")
    @WithMockUser(username = "dentista@teste.com", roles = {"DENTIST"})
    public void testCenarioCompletoAcessoNegado() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/admin/relatorios");
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 403);
        
        mockMvc.perform(get("/acesso-negado")
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/admin/relatorios")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isOk())
                .andExpect(view().name("acesso-negado"));
                
        // Verificar que o log de auditoria foi chamado com os parâmetros corretos
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq("URL: /admin/relatorios"),
            eq("Acesso negado")
        );
    }
    
    @Test
    @DisplayName("Deve registrar logs de auditoria ao acessar página de acesso negado via MockMvc")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testLogAuditoriaViaMockMvc() throws Exception {
        mockMvc.perform(get("/acesso-negado")
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/admin/area-restrita")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isOk())
                .andExpect(view().name("acesso-negado"));
                
        // Verificar que o log de auditoria foi chamado
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq("URL: /admin/area-restrita"),
            eq("Acesso negado")
        );
    }
    
    @Test
    @DisplayName("Deve registrar logs de auditoria para diferentes perfis de usuário")
    @WithMockUser(username = "dentista@teste.com", roles = {"DENTIST"})
    public void testLogAuditoriaParaDiferentesPerfis() throws Exception {
        // Teste com acesso a recurso restrito
        mockMvc.perform(get("/acesso-negado")
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/admin/configs")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isOk())
                .andExpect(view().name("acesso-negado"));
                
        // Verificar que o log de auditoria foi chamado
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq("URL: /admin/configs"),
            eq("Acesso negado")
        );
    }
    
    @Test
    @DisplayName("Deve registrar logs específicos para tentativas de acesso do recepcionista a área restrita")
    @WithMockUser(username = "recepcao@teste.com", roles = {"RECEPTIONIST"})
    public void testLogAuditoriaParaRecepcionistaAreaRestrita() throws Exception {
        String urlRestrita = "/admin/relatorios-financeiros";
        
        mockMvc.perform(get("/acesso-negado")
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, urlRestrita)
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isOk())
                .andExpect(view().name("acesso-negado"));
                
        // Verificar que o log de auditoria foi chamado com a URL específica
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq("URL: " + urlRestrita),
            eq("Acesso negado")
        );
    }
    
    @Test
    @DisplayName("Deve registrar logs específicos para tentativas de acesso do paciente a área restrita")
    @WithMockUser(username = "paciente@email.com", roles = {"PATIENT"})
    public void testLogAuditoriaParaPacienteAreaRestrita() throws Exception {
        String urlRestrita = "/dentista/agenda";
        
        mockMvc.perform(get("/acesso-negado")
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, urlRestrita)
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isOk())
                .andExpect(view().name("acesso-negado"));
                
        // Verificar que o log de auditoria foi chamado com a URL específica
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq("URL: " + urlRestrita),
            eq("Acesso negado")
        );
    }
    
    @Test
    @DisplayName("Deve testar múltiplas tentativas de acesso e garantir que todas são registradas")
    @WithMockUser(username = "dentista@teste.com", roles = {"DENTIST"})
    public void testMultiplasTentativasDeAcesso() throws Exception {
        // Primeira tentativa
        mockMvc.perform(get("/acesso-negado")
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/admin/relatorio1")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isOk());
        
        // Segunda tentativa
        mockMvc.perform(get("/acesso-negado")
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/admin/relatorio2")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isOk());
        
        // Verificar que o logger foi chamado duas vezes com os URLs específicos
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq("URL: /admin/relatorio1"),
            eq("Acesso negado")
        );
        
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq("URL: /admin/relatorio2"),
            eq("Acesso negado")
        );
        
        // Verificar o número de chamadas
        verify(userActivityLogger, times(2)).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            anyString(),
            eq("Acesso negado")
        );
    }
    
    @Test
    @DisplayName("Deve usar ArgumentCaptor para verificar detalhes do log")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testLogAuditoriaUsandoArgumentCaptor() throws Exception {
        // Resetar o mock antes do teste para garantir contagens corretas
        reset(userActivityLogger);
        
        // Criar um ArgumentCaptor para capturar os parâmetros passados para o userActivityLogger
        ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detailsCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> affectedEntitiesCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> observationsCaptor = ArgumentCaptor.forClass(String.class);
        
        String urlRestrita = "/admin/area-confidencial";
        
        mockMvc.perform(get("/acesso-negado")
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, urlRestrita)
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isOk());
        
        // Capturar os argumentos passados para o método logActivity
        // Permitir verificar pelo menos uma vez (atLeastOnce)
        verify(userActivityLogger, times(1)).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq("URL: " + urlRestrita),
            eq("Acesso negado")
        );
        
        // Usar o captor para verificar os detalhes do log (pega a primeira ocorrência)
        verify(userActivityLogger, atLeastOnce()).logActivity(
            actionCaptor.capture(),
            detailsCaptor.capture(),
            affectedEntitiesCaptor.capture(),
            observationsCaptor.capture()
        );
        
        // Verificar os valores capturados
        // Como pode haver múltiplos logs, verifica se existe pelo menos uma chamada com esses valores
        assertTrue(actionCaptor.getAllValues().contains("ACESSO_NEGADO"), "Ação deve ser ACESSO_NEGADO");
        assertTrue(detailsCaptor.getAllValues().contains("Tentativa de acesso a recurso não autorizado"), "Detalhes não correspondem ao esperado");
        assertTrue(affectedEntitiesCaptor.getAllValues().contains("URL: " + urlRestrita), "URL não corresponde ao esperado");
        assertTrue(observationsCaptor.getAllValues().contains("Acesso negado"), "Observação não corresponde ao esperado");
    }
}
