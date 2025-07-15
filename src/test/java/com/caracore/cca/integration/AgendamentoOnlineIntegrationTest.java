package com.caracore.cca.integration;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.service.AgendamentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integração para o fluxo completo de agendamento online
 * Simula o cenário real de um usuário selecionando dia e hora
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AgendamentoOnlineIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendamentoService agendamentoService;

    @Test
    @DisplayName("Fluxo completo: Seleção de dentista → Data → Hora → Agendamento")
    public void testFluxoCompletoAgendamentoOnline() throws Exception {
        
        // 1. Simular página inicial de agendamento
        when(agendamentoService.listarDentistasAtivos())
                .thenReturn(Arrays.asList(
                    "Dr. João Silva - Clínico Geral",
                    "Dra. Ana Costa - Periodontista", 
                    "Dr. Carlos Oliveira - Ortodontista"
                ));

        mockMvc.perform(get("/public/agendamento"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/agendamento-online"))
                .andExpect(model().attributeExists("dentistas"));

        // 2. Simular busca de horários disponíveis para uma data específica
        String dentistaSelecionado = "Dr. João Silva - Clínico Geral";
        String dataSelecionada = "2025-07-20"; // Data futura
        
        List<String> horariosDisponiveis = Arrays.asList(
            "08:00", "08:30", "09:00", "10:00", "14:00", "15:00", "16:00"
        );
        
        when(agendamentoService.getHorariosDisponiveisPorData(
            eq(dentistaSelecionado), 
            any(LocalDateTime.class)))
            .thenReturn(horariosDisponiveis);

        mockMvc.perform(get("/public/api/horarios-disponiveis")
                .param("dentista", dentistaSelecionado)
                .param("data", dataSelecionada)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$[0]").value("08:00"))
                .andExpect(jsonPath("$[4]").value("14:00"));

        // 3. Simular verificação de disponibilidade de horário específico
        String horarioSelecionado = "09:00";
        String dataHoraSelecionada = dataSelecionada + "T" + horarioSelecionado + ":00";
        
        when(agendamentoService.verificarConflitoHorario(
            eq(dentistaSelecionado), 
            any(LocalDateTime.class), 
            eq(null)))
            .thenReturn(false); // Sem conflito = disponível

        mockMvc.perform(get("/api/public/verificar-disponibilidade")
                .param("dentista", dentistaSelecionado)
                .param("dataHora", dataHoraSelecionada)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.disponivel").value(true));

        // 4. Simular agendamento completo
        Agendamento agendamentoMock = new Agendamento();
        agendamentoMock.setId(1L);
        agendamentoMock.setPaciente("João Teste");
        agendamentoMock.setDentista(dentistaSelecionado);
        agendamentoMock.setDataHora(LocalDateTime.parse(dataHoraSelecionada));
        agendamentoMock.setStatus("AGENDADO");
        
        when(agendamentoService.salvar(any(Agendamento.class)))
                .thenReturn(agendamentoMock);
        
        when(agendamentoService.listarDentistasAtivos())
                .thenReturn(Arrays.asList(dentistaSelecionado));

        mockMvc.perform(post("/public/agendar")
                .param("paciente", "João Teste")
                .param("telefone", "(11) 99999-9999")
                .param("email", "joao.teste@email.com")
                .param("dentista", dentistaSelecionado)
                .param("dataHora", dataHoraSelecionada)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Agendamento realizado com sucesso!"));
    }

    @Test
    @DisplayName("Cenário: Horário indisponível - deve mostrar alternativas")
    public void testHorarioIndisponivelMostraAlternativas() throws Exception {
        
        String dentista = "Dra. Ana Costa - Periodontista";
        String dataHoraOcupada = "2025-07-20T10:00:00";
        
        // Simular horário já ocupado
        when(agendamentoService.verificarConflitoHorario(
            eq(dentista), 
            any(LocalDateTime.class), 
            eq(null)))
            .thenReturn(true); // Com conflito = indisponível

        mockMvc.perform(get("/api/public/verificar-disponibilidade")
                .param("dentista", dentista)
                .param("dataHora", dataHoraOcupada)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.disponivel").value(false));

        // Verificar horários alternativos disponíveis
        List<String> horariosAlternativos = Arrays.asList(
            "08:00", "08:30", "09:00", "10:30", "14:00", "15:00"
        );
        
        when(agendamentoService.getHorariosDisponiveisPorData(
            eq(dentista), 
            any(LocalDateTime.class)))
            .thenReturn(horariosAlternativos);

        mockMvc.perform(get("/public/api/horarios-disponiveis")
                .param("dentista", dentista)
                .param("data", "2025-07-20")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(6))
                // Verificar que 10:00 não está na lista (está ocupado)
                .andExpect(jsonPath("$[?(@=='10:00')]").doesNotExist())
                // Mas 10:30 está disponível
                .andExpect(jsonPath("$[?(@=='10:30')]").exists());
    }

    @Test
    @DisplayName("Cenário: Diferentes dentistas têm horários diferentes")
    public void testDiferentesDentistasHorariosDiferentes() throws Exception {
        
        String data = "2025-07-20";
        
        // Dr. João Silva - Clínico Geral (horários da manhã)
        when(agendamentoService.getHorariosDisponiveisPorData(
            eq("Dr. João Silva - Clínico Geral"), 
            any(LocalDateTime.class)))
            .thenReturn(Arrays.asList("08:00", "09:00", "10:00", "11:00"));

        // Dra. Ana Costa - Periodontista (horários da tarde)
        when(agendamentoService.getHorariosDisponiveisPorData(
            eq("Dra. Ana Costa - Periodontista"), 
            any(LocalDateTime.class)))
            .thenReturn(Arrays.asList("14:00", "15:00", "16:00", "17:00"));

        // Testar Dr. João Silva
        mockMvc.perform(get("/public/api/horarios-disponiveis")
                .param("dentista", "Dr. João Silva - Clínico Geral")
                .param("data", data)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("08:00"))
                .andExpect(jsonPath("$[3]").value("11:00"));

        // Testar Dra. Ana Costa
        mockMvc.perform(get("/public/api/horarios-disponiveis")
                .param("dentista", "Dra. Ana Costa - Periodontista")
                .param("data", data)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("14:00"))
                .andExpect(jsonPath("$[3]").value("17:00"));
    }

    @Test
    @DisplayName("Cenário: Validação de parâmetros inválidos")
    public void testValidacaoParametrosInvalidos() throws Exception {
        
        // Testar sem dentista
        mockMvc.perform(get("/public/api/horarios-disponiveis")
                .param("data", "2025-07-20")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Testar sem data
        mockMvc.perform(get("/public/api/horarios-disponiveis")
                .param("dentista", "Dr. João Silva")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Testar data inválida
        mockMvc.perform(get("/public/api/horarios-disponiveis")
                .param("dentista", "Dr. João Silva")
                .param("data", "data-inválida")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
