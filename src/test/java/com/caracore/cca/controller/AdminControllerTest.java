package com.caracore.cca.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.caracore.cca.config.TestConfig;
import com.caracore.cca.service.UsuarioDetailsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe de teste para o AdminController.
 * Testa o acesso restrito a administradores e o redirecionamento para a página de usuários.
 */
@WebMvcTest(AdminController.class)
@Import(TestConfig.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    @DisplayName("Deve redirecionar para página de usuários na rota admin")
    @WithMockUser(username = "admin_teste", roles = {"ADMIN"})
    public void deveRedirecionarParaPaginaDeUsuarios() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"));
    }
}
