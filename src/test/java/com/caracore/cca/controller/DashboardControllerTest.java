package com.caracore.cca.controller;

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

import com.caracore.cca.config.DashboardTestConfig;
import com.caracore.cca.service.UsuarioDetailsService;
import com.caracore.cca.util.UserActivityLogger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe de teste para o DashboardController.
 * Testa o acesso autenticado e as atribuições de modelo para diferentes perfis de usuário.
 */
@WebMvcTest(controllers = DashboardController.class)
@Import(DashboardTestConfig.class)
public class DashboardControllerTest {

    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    @MockBean
    private UserActivityLogger userActivityLogger;
    
    @MockBean
    private UsuarioDetailsService usuarioDetailsService;
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @Test
    @DisplayName("Deve negar acesso ao dashboard para usuário não autenticado")
    @WithAnonymousUser
    public void deveNegarAcessoAoDashboardParaUsuarioNaoAutenticado() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Deve permitir acesso ao dashboard para usuário autenticado")
    @WithMockUser(username = "usuario_teste", roles = {})
    public void devePermitirAcessoAoDashboardParaUsuarioAutenticado() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("username", "roles"))
                .andExpect(model().attribute("username", "usuario_teste"))
                .andExpect(model().attribute("isAdmin", false))
                .andExpect(model().attribute("isDentist", false))
                .andExpect(model().attribute("isReceptionist", false))
                .andExpect(model().attribute("isPatient", false));
    }

    @Test
    @DisplayName("Deve configurar modelo corretamente para usuário com perfil ADMIN")
    @WithMockUser(username = "admin_teste", roles = {"ADMIN"})
    public void deveConfigurarModeloCorretamenteParaPerfilAdmin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("username", "roles"))
                .andExpect(model().attribute("username", "admin_teste"))
                .andExpect(model().attribute("isAdmin", true))
                .andExpect(model().attribute("isDentist", false))
                .andExpect(model().attribute("isReceptionist", false))
                .andExpect(model().attribute("isPatient", false));
    }

    @Test
    @DisplayName("Deve configurar modelo corretamente para usuário com perfil DENTIST")
    @WithMockUser(username = "dentista_teste", roles = {"DENTIST"})
    public void deveConfigurarModeloCorretamenteParaPerfilDentista() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("username", "roles"))
                .andExpect(model().attribute("username", "dentista_teste"))
                .andExpect(model().attribute("isAdmin", false))
                .andExpect(model().attribute("isDentist", true))
                .andExpect(model().attribute("isReceptionist", false))
                .andExpect(model().attribute("isPatient", false));
    }

    @Test
    @DisplayName("Deve configurar modelo corretamente para usuário com perfil RECEPTIONIST")
    @WithMockUser(username = "recepcionista_teste", roles = {"RECEPTIONIST"})
    public void deveConfigurarModeloCorretamenteParaPerfilRecepcionista() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("username", "roles"))
                .andExpect(model().attribute("username", "recepcionista_teste"))
                .andExpect(model().attribute("isAdmin", false))
                .andExpect(model().attribute("isDentist", false))
                .andExpect(model().attribute("isReceptionist", true))
                .andExpect(model().attribute("isPatient", false));
    }

    @Test
    @DisplayName("Deve configurar modelo corretamente para usuário com perfil PATIENT")
    @WithMockUser(username = "paciente_teste", roles = {"PATIENT"})
    public void deveConfigurarModeloCorretamenteParaPerfilPaciente() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("username", "roles"))
                .andExpect(model().attribute("username", "paciente_teste"))
                .andExpect(model().attribute("isAdmin", false))
                .andExpect(model().attribute("isDentist", false))
                .andExpect(model().attribute("isReceptionist", false))
                .andExpect(model().attribute("isPatient", true));
    }

    @Test
    @DisplayName("Deve configurar modelo corretamente para usuário com múltiplos perfis")
    @WithMockUser(username = "multiperfil_teste", roles = {"ADMIN", "DENTIST"})
    public void deveConfigurarModeloCorretamenteParaUsuarioComMultiplosPerfis() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("username", "roles"))
                .andExpect(model().attribute("username", "multiperfil_teste"))
                .andExpect(model().attribute("isAdmin", true))
                .andExpect(model().attribute("isDentist", true))
                .andExpect(model().attribute("isReceptionist", false))
                .andExpect(model().attribute("isPatient", false));
    }
}
