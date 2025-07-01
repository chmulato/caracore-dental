package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.repository.AgendamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

@WebMvcTest(AgendamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
@org.springframework.context.annotation.Import(com.caracore.cca.config.TestConfig.class)
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendamentoRepository agendamentoRepository;

    @Test
    @WithMockUser
    @DisplayName("Deve retornar status 200 na listagem")
    void deveRetornarStatus200NaListagem() throws Exception {
        Mockito.when(agendamentoRepository.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(MockMvcRequestBuilders.get("/agendamentos"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
