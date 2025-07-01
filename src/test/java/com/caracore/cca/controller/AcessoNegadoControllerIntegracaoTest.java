package com.caracore.cca.controller;

import com.caracore.cca.config.TestConfig;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.RequestDispatcher;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Testes de integração para o controlador de acesso negado.
 * Foca em testar a integração com o UserActivityLogger para diferentes
 * perfis de usuário e em diferentes cenários de acesso negado.
 */
@WebMvcTest(AcessoNegadoController.class)
@Import(TestConfig.class)
public class AcessoNegadoControllerIntegracaoTest {

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
        
        // Resetamos o mock antes de cada teste
        reset(userActivityLogger);
    }

    /**
     * Teste parametrizado para verificar o comportamento do AcessoNegadoController
     * com diferentes perfis de usuário
     */
    @ParameterizedTest
    @ValueSource(strings = {"ROLE_ADMIN", "ROLE_DENTIST", "ROLE_RECEPTIONIST", "ROLE_PATIENT"})
    @DisplayName("Deve registrar log de auditoria para diferentes perfis de usuário")
    public void testLogAuditoriaParaDiferentesPerfis(String role) throws Exception {
        // Configurar o nome de usuário baseado no papel
        String username = role.equals("ROLE_ADMIN") ? "admin@teste.com" :
                          role.equals("ROLE_DENTIST") ? "dentista@teste.com" :
                          role.equals("ROLE_RECEPTIONIST") ? "recepcao@teste.com" :
                          "paciente@teste.com";
        
        // Configurar URL restrita com base no perfil
        String urlRestrita = role.equals("ROLE_ADMIN") ? "/sistema/configuracao" :
                            role.equals("ROLE_DENTIST") ? "/admin/relatorios" :
                            role.equals("ROLE_RECEPTIONIST") ? "/dentista/prontuarios" :
                            "/admin/usuarios";
        
        mockMvc.perform(get("/acesso-negado")
                .with(request -> {
                    request.setUserPrincipal(
                            new UsernamePasswordAuthenticationToken(
                                username, 
                                "password", 
                                Collections.singletonList(new SimpleGrantedAuthority(role))
                            )
                    );
                    return request;
                })
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, urlRestrita)
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isOk())
                .andExpect(view().name("acesso-negado"));
        
        // Verificar que o log de auditoria foi chamado com os parâmetros corretos
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq("URL: " + urlRestrita),
            eq("Acesso negado")
        );
    }
    
    @Test
    @DisplayName("Deve testar cenário onde existem múltiplas tentativas de acesso para o mesmo usuário")
    @WithMockUser(username = "admin@teste.com", roles = {"ADMIN"})
    public void testMultiplasTentativasDeAcessoMesmoUsuario() throws Exception {
        String[] urlsRestritas = {
            "/financeiro/relatorios", 
            "/admin/usuarios/excluir", 
            "/sistema/backup"
        };
        
        for (String url : urlsRestritas) {
            mockMvc.perform(get("/acesso-negado")
                    .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, url)
                    .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                    .andExpect(status().isOk());
        }
        
        // Verificar que o logger foi chamado uma vez para cada URL
        verify(userActivityLogger, times(urlsRestritas.length)).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            Mockito.anyString(),
            eq("Acesso negado")
        );
        
        // Usar ArgumentCaptor para capturar todos os valores de URL passados
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(userActivityLogger, times(urlsRestritas.length)).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            urlCaptor.capture(),
            eq("Acesso negado")
        );
        
        // Verificar que todas as URLs foram registradas
        for (String url : urlsRestritas) {
            assertTrue(urlCaptor.getAllValues().contains("URL: " + url),
                    "O log deveria conter a URL: " + url);
        }
    }
    
    @Test
    @DisplayName("Deve testar acesso direto ao AcessoNegadoController com SecurityContext manual")
    public void testAcessoDiretoComSecurityContextManual() {
        // Preparar um SecurityContext com uma autenticação específica
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "testuser@example.com", 
                "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_DENTIST"))
        );
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
        
        // Preparar o request com uma URL específica
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/admin/financeiro");
        
        // Criar e configurar o controller diretamente
        AcessoNegadoController controller = new AcessoNegadoController();
        controller.userActivityLogger = userActivityLogger;
        
        // Chamar o método diretamente
        String viewName = controller.acessoNegado(request);
        
        // Verificar o nome da view retornada
        assertEquals("acesso-negado", viewName);
        
        // Verificar que o log foi registrado corretamente
        verify(userActivityLogger).logActivity(
            eq("ACESSO_NEGADO"),
            eq("Tentativa de acesso a recurso não autorizado"),
            eq("URL: /admin/financeiro"),
            eq("Acesso negado")
        );
        
        // Limpar o SecurityContext
        SecurityContextHolder.clearContext();
    }
}
