package com.caracore.cca.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
class NovoAgendamentoViewTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.caracore.cca.repository.AgendamentoRepository agendamentoRepository;

    @Test
    @WithMockUser
    @DisplayName("Deve exibir formulário novo agendamento")
    void deveExibirFormularioNovoAgendamento() throws Exception {
        mockMvc.perform(get("/novo-agendamento"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<h1>Novo Agendamento</h1>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"paciente\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"dentista\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"dataHora\"")));
    }
}
