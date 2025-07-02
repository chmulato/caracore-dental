package com.caracore.cca.controller;

import com.caracore.cca.config.TestConfig;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.service.PacienteService;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
class NovoAgendamentoViewTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.caracore.cca.repository.AgendamentoRepository agendamentoRepository;
    
    @MockBean
    private PacienteService pacienteService;
    
    @MockBean
    private UserActivityLogger userActivityLogger;

    @Test
    @WithMockUser
    @DisplayName("Deve exibir formulário novo agendamento")
    void deveExibirFormularioNovoAgendamento() throws Exception {
        mockMvc.perform(get("/novo-agendamento"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<h2>Novo Agendamento</h2>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"paciente\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"dentista\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"dataHora\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"telefoneWhatsapp\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"whatsappLink\"")));
    }
    
    @Test
    @WithMockUser
    @DisplayName("Deve preencher telefone WhatsApp ao selecionar paciente existente")
    void devePreencherTelefoneAoSelecionarPacienteExistente() throws Exception {
        // Configura um paciente de teste com telefone
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        
        // Configura o mock do serviço
        when(pacienteService.buscarPorNome(anyString())).thenReturn(Collections.singletonList(paciente));
        
        // Executa a requisição com parâmetro de nome do paciente
        mockMvc.perform(get("/novo-agendamento").param("pacienteNome", "João Silva"))
                .andExpect(status().isOk())
                // Verifica se o formulário é exibido com o telefone do paciente pré-preenchido
                .andExpect(model().attributeExists("agendamentoForm"))
                .andExpect(model().attribute("agendamentoForm", org.hamcrest.Matchers.hasProperty(
                    "telefoneWhatsapp", org.hamcrest.Matchers.equalTo("(11) 98765-4321"))))
                .andExpect(model().attribute("agendamentoForm", org.hamcrest.Matchers.hasProperty(
                    "paciente", org.hamcrest.Matchers.equalTo("João Silva"))));
    }
}
