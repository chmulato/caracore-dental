package com.caracore.cca.controller;

import com.caracore.cca.service.AgendamentoService;
import com.caracore.cca.service.DentistaService;
import com.caracore.cca.service.PacienteService;
import com.caracore.cca.service.RateLimitService;
import com.caracore.cca.util.UserActivityLogger;
import com.caracore.cca.config.UserActivityInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.thymeleaf.check-template-location=false",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never"
})
@DisplayName("Simple Test for AgendamentoPublicoController")
class AgendamentoPublicoControllerSimpleTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendamentoService agendamentoService;
    
    @MockBean
    private DentistaService dentistaService;
    
    @MockBean
    private PacienteService pacienteService;
    
    @MockBean
    private RateLimitService rateLimitService;
    
    @MockBean
    private UserActivityLogger userActivityLogger;
    
    @MockBean
    private UserActivityInterceptor userActivityInterceptor;

    @BeforeEach
    void setUp() {
        // Mock básico para dentistas ativos
        List<String> dentistasAtivos = Arrays.asList(
            "Dr. João Silva - Clínico Geral",
            "Dra. Maria Santos - Ortodontista"
        );
        when(agendamentoService.listarDentistasAtivos()).thenReturn(dentistasAtivos);
        
        // Mock rate limiting para permitir requisições
        when(rateLimitService.isAllowed(any())).thenReturn(true);
    }

    @Test
    @DisplayName("Deve executar endpoint de teste simples")
    void testEndpointSimple() throws Exception {
        mockMvc.perform(post("/public/test-simple")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Teste executado com sucesso!"));
    }

    @Test
    @DisplayName("Deve testar endpoint de agendamento com logs")
    void testEndpointAgendamento() throws Exception {
        mockMvc.perform(post("/public/agendar")
                .param("paciente", "")
                .param("dentista", "Dr. João Silva - Clínico Geral")
                .param("dataHora", "2025-07-10T10:00")
                .param("telefone", "11999999999")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Todos os campos obrigatórios devem ser preenchidos"));
    }
}
