package com.caracore.cca.controller;

import com.caracore.cca.config.LoginTestConfig;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoginController.class)
@Import(LoginTestConfig.class)
public class LoginControllerTest {

    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    @MockBean
    private UserActivityLogger userActivityLogger;
    
    // UsuarioDetailsService is now provided by LoginTestConfig
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @Test
    @DisplayName("Deve exibir página de login sem parâmetros")
    @WithAnonymousUser
    public void deveExibirPaginaLoginSemParametros() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeDoesNotExist("message"));
    }

    @Test
    @DisplayName("Deve exibir página de login com erro")
    @WithAnonymousUser
    public void deveExibirPaginaLoginComErro() throws Exception {
        mockMvc.perform(get("/login").param("error", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", "Nome de usuário ou senha inválidos."))
                .andExpect(model().attributeDoesNotExist("message"));
    }

    @Test
    @DisplayName("Deve exibir página de login após logout")
    @WithAnonymousUser
    public void deveExibirPaginaLoginAposLogout() throws Exception {
        mockMvc.perform(get("/login").param("logout", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attribute("message", "Logout realizado com sucesso."));
    }
}
