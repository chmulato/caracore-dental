package com.caracore.cca.service;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.model.Dentista;
import com.caracore.cca.repository.AgendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para as novas funcionalidades do AgendamentoService
 * relacionadas ao controle de exposição pública
 */
@ExtendWith(MockitoExtension.class)
public class AgendamentoServiceExposicaoPublicaTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private DentistaService dentistaService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private List<Agendamento> agendamentosMock;
    private List<Dentista> dentistasMock;

    @BeforeEach
    void setUp() {
        // Criar agendamentos mock para testes
        agendamentosMock = Arrays.asList(
            criarAgendamento(1L, "João Silva", "Dr. João Silva - Clínico Geral"),
            criarAgendamento(2L, "Maria Santos", "Dra. Maria Santos - Ortodontista"),
            criarAgendamento(3L, "Carlos Oliveira", "Dr. Carlos Oliveira - Periodontista")
        );

        // Criar dentistas mock para testes
        dentistasMock = Arrays.asList(
            criarDentista(1L, "Dr. João Silva", "Clínico Geral", true, true),
            criarDentista(2L, "Dra. Maria Santos", "Ortodontista", true, true),
            criarDentista(3L, "Dr. Carlos Oliveira", "Periodontista", true, true)
        );
    }

    @Test
    @DisplayName("Deve listar todos os dentistas únicos dos agendamentos")
    void testListarDentistas() {
        // Arrange
        when(agendamentoRepository.findAll()).thenReturn(agendamentosMock);

        // Act
        List<String> dentistas = agendamentoService.listarDentistas();

        // Assert
        assertNotNull(dentistas);
        assertEquals(3, dentistas.size());
        assertTrue(dentistas.contains("Dr. João Silva - Clínico Geral"));
        assertTrue(dentistas.contains("Dra. Maria Santos - Ortodontista"));
        assertTrue(dentistas.contains("Dr. Carlos Oliveira - Periodontista"));
        
        verify(agendamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve filtrar dentistas nulos ou vazios ao listar")
    void testListarDentistasFiltrandoNulos() {
        // Arrange
        List<Agendamento> agendamentosComNulos = Arrays.asList(
            criarAgendamento(1L, "João Silva", "Dr. João Silva - Clínico Geral"),
            criarAgendamento(2L, "Maria Santos", null), // Dentista nulo
            criarAgendamento(3L, "Carlos Oliveira", ""), // Dentista vazio
            criarAgendamento(4L, "Ana Costa", "   ") // Dentista só com espaços
        );
        when(agendamentoRepository.findAll()).thenReturn(agendamentosComNulos);

        // Act
        List<String> dentistas = agendamentoService.listarDentistas();

        // Assert
        assertNotNull(dentistas);
        assertEquals(1, dentistas.size());
        assertEquals("Dr. João Silva - Clínico Geral", dentistas.get(0));
        
        verify(agendamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há agendamentos")
    void testListarDentistasVazio() {
        // Arrange
        when(agendamentoRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<String> dentistas = agendamentoService.listarDentistas();

        // Assert
        assertNotNull(dentistas);
        assertTrue(dentistas.isEmpty());
        
        verify(agendamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve listar dentistas ativos para exposição pública")
    void testListarDentistasAtivos() {
        // Arrange
        when(dentistaService.listarAtivosExpostosPublicamente()).thenReturn(dentistasMock);

        // Act
        List<String> dentistasAtivos = agendamentoService.listarDentistasAtivos();

        // Assert
        assertNotNull(dentistasAtivos);
        assertEquals(3, dentistasAtivos.size());
        assertTrue(dentistasAtivos.contains("Dr. João Silva - Clínico Geral"));
        assertTrue(dentistasAtivos.contains("Dra. Maria Santos - Ortodontista"));
        assertTrue(dentistasAtivos.contains("Dr. Carlos Oliveira - Periodontista"));
        
        verify(dentistaService, times(1)).listarAtivosExpostosPublicamente();
    }

    @Test
    @DisplayName("Deve verificar disponibilidade de horário para agendamento público")
    void testIsHorarioDisponivel() {
        // Arrange
        String dentista = "Dr. João Silva - Clínico Geral";
        LocalDateTime dataHora = LocalDateTime.of(2025, 7, 10, 10, 0);
        
        // Mock do método verificarConflitoHorario (sem conflito)
        List<Agendamento> agendamentosSemConflito = Arrays.asList();
        when(agendamentoRepository.findByDentistaAndDataHoraBetween(
            eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(agendamentosSemConflito);

        // Act
        boolean disponivel = agendamentoService.isHorarioDisponivel(dentista, dataHora);

        // Assert
        assertTrue(disponivel);
    }

    @Test
    @DisplayName("Deve identificar conflito de horário para agendamento público")
    void testIsHorarioDisponivelComConflito() {
        // Arrange
        String dentista = "Dr. João Silva - Clínico Geral";
        LocalDateTime dataHora = LocalDateTime.of(2025, 7, 10, 10, 0);
        
        // Mock do método verificarConflitoHorario (com conflito)
        Agendamento conflito = criarAgendamento(99L, "Paciente Conflito", dentista);
        conflito.setDataHora(dataHora);
        conflito.setStatus("AGENDADO");
        
        when(agendamentoRepository.findByDentistaAndDataHoraBetween(
            eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(conflito));

        // Act
        boolean disponivel = agendamentoService.isHorarioDisponivel(dentista, dataHora);

        // Assert
        assertFalse(disponivel);
    }

    @Test
    @DisplayName("Deve gerar horários disponíveis para data específica")
    void testGetHorariosDisponiveisPorData() {
        // Arrange
        String dentista = "Dr. João Silva - Clínico Geral";
        LocalDateTime data = LocalDateTime.of(2025, 7, 10, 0, 0);
        
        // Mock sem conflitos para horários padrão
        when(agendamentoRepository.findByDentistaAndDataHoraBetween(
            eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList());

        // Act
        List<String> horariosDisponiveis = agendamentoService.getHorariosDisponiveisPorData(dentista, data);

        // Assert
        assertNotNull(horariosDisponiveis);
        assertFalse(horariosDisponiveis.isEmpty());
        
        // Verificar se contém horários esperados (08:00, 08:30, 09:00, etc.)
        assertTrue(horariosDisponiveis.contains("08:00"));
        assertTrue(horariosDisponiveis.contains("08:30"));
        assertTrue(horariosDisponiveis.contains("09:00"));
        
        // Verificar se não contém horário de almoço
        assertFalse(horariosDisponiveis.contains("12:00"));
        assertFalse(horariosDisponiveis.contains("12:30"));
        
        // Verificar se termina antes das 18h
        assertFalse(horariosDisponiveis.contains("18:00"));
    }

    @Test
    @DisplayName("Deve excluir horários ocupados da lista de disponíveis")
    void testGetHorariosDisponiveisComOcupacao() {
        // Arrange
        String dentista = "Dr. João Silva - Clínico Geral";
        LocalDateTime data = LocalDateTime.of(2025, 7, 10, 0, 0);
        
        // Criar agendamento ocupando às 09:00
        Agendamento agendamentoOcupado = criarAgendamento(99L, "Paciente Ocupado", dentista);
        agendamentoOcupado.setDataHora(LocalDateTime.of(2025, 7, 10, 9, 0));
        agendamentoOcupado.setStatus("AGENDADO");
        
        // Mock que retorna conflito apenas para 09:00
        when(agendamentoRepository.findByDentistaAndDataHoraBetween(
            eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenAnswer(invocation -> {
                LocalDateTime inicio = invocation.getArgument(1);
                LocalDateTime fim = invocation.getArgument(2);
                
                // Se o intervalo inclui 09:00, retorna o conflito
                if (inicio.getHour() == 8 && inicio.getMinute() == 30 && fim.getHour() == 9 && fim.getMinute() == 30) {
                    return Arrays.asList(agendamentoOcupado);
                }
                return Arrays.asList();
            });

        // Act
        List<String> horariosDisponiveis = agendamentoService.getHorariosDisponiveisPorData(dentista, data);

        // Assert
        assertNotNull(horariosDisponiveis);
        assertTrue(horariosDisponiveis.contains("08:00"));
        assertTrue(horariosDisponiveis.contains("08:30"));
        // O horário 09:00 deve estar ocupado devido ao conflito
        assertFalse(horariosDisponiveis.contains("09:00"));
        assertTrue(horariosDisponiveis.contains("09:30"));
    }

    // --- MÉTODOS AUXILIARES ---

    private Agendamento criarAgendamento(Long id, String paciente, String dentista) {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(id);
        agendamento.setPaciente(paciente);
        agendamento.setDentista(dentista);
        agendamento.setDataHora(LocalDateTime.now().plusDays(1));
        agendamento.setStatus("AGENDADO");
        agendamento.setObservacao("Teste");
        agendamento.setDuracaoMinutos(30);
        return agendamento;
    }

    private Dentista criarDentista(Long id, String nome, String especialidade, boolean ativo, boolean expostoPublicamente) {
        Dentista dentista = new Dentista();
        dentista.setId(id);
        dentista.setNome(nome);
        dentista.setEspecialidade(especialidade);
        dentista.setAtivo(ativo);
        dentista.setExpostoPublicamente(expostoPublicamente);
        return dentista;
    }
}
