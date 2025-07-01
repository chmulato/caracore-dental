package com.caracore.cca.controller;

import com.caracore.cca.config.TestConfig;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(TestConfig.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserActivityLogger userActivityLogger;

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
