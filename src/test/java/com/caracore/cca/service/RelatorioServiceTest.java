package com.caracore.cca.service;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.model.Dentista;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.repository.AgendamentoRepository;
import com.caracore.cca.repository.DentistaRepository;
import com.caracore.cca.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RelatorioServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private DentistaRepository dentistaRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private List<Agendamento> agendamentosMock;
    private List<Paciente> pacientesMock;
    private List<Dentista> dentistasMock;

    @BeforeEach
    public void setup() {
        dataInicio = LocalDate.now().minusMonths(1);
        dataFim = LocalDate.now();

        // Configurar agendamentos de teste
        Agendamento agendamento1 = new Agendamento();
        agendamento1.setId(1L);
        agendamento1.setPaciente("Paciente Teste 1");
        agendamento1.setDentista("Dentista Teste 1");
        agendamento1.setDataHora(LocalDateTime.now().minusDays(15));
        agendamento1.setStatus("AGENDADO");
        agendamento1.setTelefoneWhatsapp("(11) 99999-9991");
        agendamento1.setDuracaoMinutos(30);

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setId(2L);
        agendamento2.setPaciente("Paciente Teste 2");
        agendamento2.setDentista("Dentista Teste 1");
        agendamento2.setDataHora(LocalDateTime.now().minusDays(10));
        agendamento2.setStatus("REALIZADO");
        agendamento2.setTelefoneWhatsapp("(11) 99999-9992");
        agendamento2.setDuracaoMinutos(45);

        Agendamento agendamento3 = new Agendamento();
        agendamento3.setId(3L);
        agendamento3.setPaciente("Paciente Teste 3");
        agendamento3.setDentista("Dentista Teste 2");
        agendamento3.setDataHora(LocalDateTime.now().minusDays(5));
        agendamento3.setStatus("CANCELADO");
        agendamento3.setTelefoneWhatsapp("(11) 99999-9993");
        agendamento3.setDuracaoMinutos(60);

        agendamentosMock = Arrays.asList(agendamento1, agendamento2, agendamento3);

        // Configurar pacientes de teste
        Paciente paciente1 = new Paciente();
        paciente1.setId(1L);
        paciente1.setNome("Paciente Teste 1");
        paciente1.setEmail("paciente1@teste.com");
        paciente1.setTelefone("(11) 99999-9991");
        paciente1.setConsentimentoConfirmado(true);

        Paciente paciente2 = new Paciente();
        paciente2.setId(2L);
        paciente2.setNome("Paciente Teste 2");
        paciente2.setEmail("paciente2@teste.com");
        paciente2.setTelefone("(11) 99999-9992");
        paciente2.setConsentimentoConfirmado(false);

        Paciente paciente3 = new Paciente();
        paciente3.setId(3L);
        paciente3.setNome("Paciente Teste 3");
        paciente3.setEmail(null);
        paciente3.setTelefone("(11) 99999-9993");
        paciente3.setConsentimentoConfirmado(true);

        pacientesMock = Arrays.asList(paciente1, paciente2, paciente3);

        // Configurar dentistas de teste
        Dentista dentista1 = new Dentista();
        dentista1.setId(1L);
        dentista1.setNome("Dentista Teste 1");
        dentista1.setEspecialidade("Clínico Geral");
        dentista1.setAtivo(true);

        Dentista dentista2 = new Dentista();
        dentista2.setId(2L);
        dentista2.setNome("Dentista Teste 2");
        dentista2.setEspecialidade("Ortodontista");
        dentista2.setAtivo(true);

        dentistasMock = Arrays.asList(dentista1, dentista2);
    }

    @Test
    @DisplayName("Deve gerar relatório de agendamentos corretamente")
    public void deveGerarRelatorioDeAgendamentosCorretamente() {
        // Arrange
        when(agendamentoRepository.findByDataHoraBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(agendamentosMock);

        // Act
        Map<String, Object> resultado = relatorioService.gerarRelatorioAgendamentos(
                dataInicio, dataFim, null, null);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("agendamentos"));
        assertTrue(resultado.containsKey("resumoStatus"));
        assertTrue(resultado.containsKey("totalAgendamentos"));

        @SuppressWarnings("unchecked")
        List<Agendamento> agendamentos = (List<Agendamento>) resultado.get("agendamentos");
        assertEquals(3, agendamentos.size());

        @SuppressWarnings("unchecked")
        Map<String, Long> resumoStatus = (Map<String, Long>) resultado.get("resumoStatus");
        assertEquals(1L, resumoStatus.get("AGENDADO"));
        assertEquals(1L, resumoStatus.get("REALIZADO"));
        assertEquals(1L, resumoStatus.get("CANCELADO"));

        assertEquals(3, resultado.get("totalAgendamentos"));

        verify(agendamentoRepository).findByDataHoraBetween(
                dataInicio.atStartOfDay(),
                dataFim.atTime(LocalTime.MAX));
    }

    @Test
    @DisplayName("Deve filtrar agendamentos por status corretamente")
    public void deveFiltrarAgendamentosPorStatusCorretamente() {
        // Arrange
        when(agendamentoRepository.findByDataHoraBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(agendamentosMock);
        
        // Act
        Map<String, Object> resultado = relatorioService.gerarRelatorioAgendamentos(
                dataInicio, dataFim, "REALIZADO", null);
        
        // Assert
        assertNotNull(resultado);
        
        @SuppressWarnings("unchecked")
        List<Agendamento> agendamentos = (List<Agendamento>) resultado.get("agendamentos");
        assertEquals(1, agendamentos.size());
        assertEquals("REALIZADO", agendamentos.get(0).getStatus());
        
        assertEquals(1, resultado.get("totalAgendamentos"));
    }

    @Test
    @DisplayName("Deve filtrar agendamentos por dentista corretamente")
    public void deveFiltrarAgendamentosPorDentistaCorretamente() {
        // Arrange
        when(agendamentoRepository.findByDataHoraBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(agendamentosMock);
        
        // Act
        Map<String, Object> resultado = relatorioService.gerarRelatorioAgendamentos(
                dataInicio, dataFim, null, "Dentista Teste 1");
        
        // Assert
        assertNotNull(resultado);
        
        @SuppressWarnings("unchecked")
        List<Agendamento> agendamentos = (List<Agendamento>) resultado.get("agendamentos");
        assertEquals(2, agendamentos.size());
        assertTrue(agendamentos.stream().allMatch(a -> a.getDentista().contains("Dentista Teste 1")));
        
        assertEquals(2, resultado.get("totalAgendamentos"));
    }

    @Test
    @DisplayName("Deve gerar relatório de pacientes corretamente")
    public void deveGerarRelatorioDePacientesCorretamente() {
        // Arrange
        when(pacienteRepository.findAll()).thenReturn(pacientesMock);
        
        // Act
        Map<String, Object> resultado = relatorioService.gerarRelatorioPacientes(dataInicio, dataFim);
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("pacientes"));
        assertTrue(resultado.containsKey("estatisticas"));
        
        @SuppressWarnings("unchecked")
        List<Paciente> pacientes = (List<Paciente>) resultado.get("pacientes");
        assertEquals(3, pacientes.size());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> estatisticas = (Map<String, Object>) resultado.get("estatisticas");
        assertEquals(3, ((Number)estatisticas.get("totalPacientes")).intValue());
        assertEquals(2, ((Number)estatisticas.get("comConsentimento")).intValue());
        assertEquals(2, ((Number)estatisticas.get("comEmail")).intValue());
        assertEquals(3, ((Number)estatisticas.get("comTelefone")).intValue());
        
        verify(pacienteRepository).findAll();
    }

    @Test
    @DisplayName("Deve gerar relatório de desempenho corretamente")
    public void deveGerarRelatorioDeDesempenhoCorretamente() {
        // Arrange
        when(dentistaRepository.findByAtivoTrue()).thenReturn(dentistasMock);
        when(agendamentoRepository.findByDataHoraBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(agendamentosMock);
        
        // Act
        Map<String, Object> resultado = relatorioService.gerarRelatorioDesempenho(dataInicio, dataFim);
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("dentistas"));
        assertTrue(resultado.containsKey("resumoDesempenho"));
        
        @SuppressWarnings("unchecked")
        List<Dentista> dentistas = (List<Dentista>) resultado.get("dentistas");
        assertEquals(2, dentistas.size());
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resumoDesempenho = (List<Map<String, Object>>) resultado.get("resumoDesempenho");
        assertEquals(2, resumoDesempenho.size());
        
        // Verificar que está ordenado por taxa de realização (decrescente)
        Map<String, Object> primeiroDentista = resumoDesempenho.get(0);
        Dentista dentistaDados = (Dentista) primeiroDentista.get("dentista");
        
        // O Dentista Teste 1 deve estar no topo pois tem 50% de taxa de realização (1/2)
        // comparado com Dentista Teste 2 que tem 0% (0/1)
        assertEquals("Dentista Teste 1", dentistaDados.getNome());
        assertEquals(2, ((Number)primeiroDentista.get("totalAgendamentos")).intValue());
        assertEquals(1, ((Number)primeiroDentista.get("realizados")).intValue());
        assertEquals(50.0, primeiroDentista.get("taxaRealizacao"));
        
        verify(dentistaRepository).findByAtivoTrue();
        verify(agendamentoRepository, atLeastOnce()).findByDataHoraBetween(
                dataInicio.atStartOfDay(),
                dataFim.atTime(LocalTime.MAX));
    }

    // Testes de exportação PDF e CSV foram removidos pois a geração agora é feita no frontend
    // usando html2pdf.js e JavaScript
}
