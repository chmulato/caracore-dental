package com.caracore.cca.controller;

import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.caracore.cca.config.TestSecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para o PublicController
 */
@WebMvcTest(PublicController.class)
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserActivityLogger userActivityLogger;

    @Test
    void testAgendamentoLanding() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/public/agendamento-landing"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/agendamento-landing"));
    }

    @Test
    void testPublicIndex() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/public/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/public/agendamento-landing"));
    }
}
