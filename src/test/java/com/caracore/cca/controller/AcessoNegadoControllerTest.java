package com.caracore.cca.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class AcessoNegadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve retornar página de acesso negado")
    public void testAcessoNegado() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/acesso-negado"))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.view().name("acesso-negado"));
    }
}
