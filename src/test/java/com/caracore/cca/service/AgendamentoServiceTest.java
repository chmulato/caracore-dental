package com.caracore.cca.service;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.repository.AgendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AgendamentoService
 */
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Agendamento agendamentoTeste;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        agendamentoTeste = new Agendamento();
        agendamentoTeste.setId(1L);
        agendamentoTeste.setPaciente("João Silva");
        agendamentoTeste.setDentista("Dr. Maria Santos");
        // Definir data com pelo menos 2 dias no futuro em horário comercial
        agendamentoTeste.setDataHora(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0));
        agendamentoTeste.setStatus("AGENDADO");
        agendamentoTeste.setObservacao("Consulta de rotina");
        agendamentoTeste.setDuracaoMinutos(30);
    }

    @Test
    void testListarTodos() {
        // Arrange
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findAll()).thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(agendamentoTeste.getId(), resultado.get(0).getId());
        verify(agendamentoRepository).findAll();
    }

    @Test
    void testBuscarPorId() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoTeste));

        // Act
        Optional<Agendamento> resultado = agendamentoService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(agendamentoTeste.getId(), resultado.get().getId());
        verify(agendamentoRepository).findById(1L);
    }

    @Test
    void testBuscarPorIdNaoEncontrado() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Agendamento> resultado = agendamentoService.buscarPorId(1L);

        // Assert
        assertFalse(resultado.isPresent());
        verify(agendamentoRepository).findById(1L);
    }

    @Test
    void testSalvar() {
        // Arrange
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoTeste);

        // Act
        Agendamento resultado = agendamentoService.salvar(agendamentoTeste);

        // Assert
        assertNotNull(resultado);
        assertEquals(agendamentoTeste.getId(), resultado.getId());
        assertEquals("AGENDADO", resultado.getStatus());
        verify(agendamentoRepository).save(agendamentoTeste);
    }

    @Test
    void testConfirmarAgendamento() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoTeste));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoTeste);

        // Act
        boolean resultado = agendamentoService.confirmarAgendamento(1L);

        // Assert
        assertTrue(resultado);
        assertEquals("CONFIRMADO", agendamentoTeste.getStatus());
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(agendamentoTeste);
    }

    @Test
    void testConfirmarAgendamentoNaoEncontrado() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean resultado = agendamentoService.confirmarAgendamento(1L);

        // Assert
        assertFalse(resultado);
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void testCancelarAgendamento() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoTeste));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoTeste);

        // Act
        boolean resultado = agendamentoService.cancelarAgendamento(1L, "Motivo do cancelamento");

        // Assert
        assertTrue(resultado);
        assertEquals("CANCELADO", agendamentoTeste.getStatus());
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(agendamentoTeste);
    }

    @Test
    void testCancelarAgendamentoNaoEncontrado() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean resultado = agendamentoService.cancelarAgendamento(1L, "Motivo do cancelamento");

        // Assert
        assertFalse(resultado);
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void testMarcarComoRealizado() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoTeste));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoTeste);

        // Act
        boolean resultado = agendamentoService.marcarComoRealizado(1L);

        // Assert
        assertTrue(resultado);
        assertEquals("REALIZADO", agendamentoTeste.getStatus());
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(agendamentoTeste);
    }

    @Test
    void testMarcarComoRealizadoNaoEncontrado() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean resultado = agendamentoService.marcarComoRealizado(1L);

        // Assert
        assertFalse(resultado);
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void testReagendar() {
        // Arrange
        // Nova data com 3 dias no futuro em horário comercial para evitar problemas de 24h
        LocalDateTime novaDataHora = LocalDateTime.now().plusDays(3).withHour(14).withMinute(0).withSecond(0).withNano(0);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoTeste));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoTeste);

        // Act
        boolean resultado = agendamentoService.reagendar(1L, novaDataHora);

        // Assert
        assertTrue(resultado);
        assertEquals("REAGENDADO", agendamentoTeste.getStatus());
        assertEquals(novaDataHora, agendamentoTeste.getDataHora());
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(agendamentoTeste);
    }

    @Test
    void testReagendarNaoEncontrado() {
        // Arrange
        LocalDateTime novaDataHora = LocalDateTime.now().plusDays(3).withHour(14).withMinute(0).withSecond(0).withNano(0);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean resultado = agendamentoService.reagendar(1L, novaDataHora);

        // Assert
        assertFalse(resultado);
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void testExcluir() {
        // Arrange
        when(agendamentoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(agendamentoRepository).deleteById(1L);

        // Act
        agendamentoService.excluir(1L);

        // Assert
        verify(agendamentoRepository).existsById(1L);
        verify(agendamentoRepository).deleteById(1L);
    }

    @Test
    void testExcluirNaoEncontrado() {
        // Arrange
        when(agendamentoRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> agendamentoService.excluir(1L));
        verify(agendamentoRepository).existsById(1L);
        verify(agendamentoRepository, never()).deleteById(1L);
    }

    @Test
    void testBuscarPorPaciente() {
        // Arrange
        String nomePaciente = "João";
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findByPacienteContainingIgnoreCase(nomePaciente)).thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.buscarPorPaciente(nomePaciente);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendamentoRepository).findByPacienteContainingIgnoreCase(nomePaciente);
    }

    @Test
    void testBuscarPorDentista() {
        // Arrange
        String dentista = "Dr. Maria";
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findByDentistaContainingIgnoreCase(dentista)).thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.buscarPorDentista(dentista);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendamentoRepository).findByDentistaContainingIgnoreCase(dentista);
    }

    @Test
    void testBuscarPorPeriodo() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(1);
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findByDataHoraBetween(inicio, fim)).thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.buscarPorPeriodo(inicio, fim);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendamentoRepository).findByDataHoraBetween(inicio, fim);
    }

    @Test
    void testVerificarConflitoHorario() {
        // Arrange
        String dentista = "Dr. Maria Santos";
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        List<Agendamento> agendamentos = new ArrayList<>();
        when(agendamentoRepository.findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(agendamentos);

        // Act
        boolean resultado = agendamentoService.verificarConflitoHorario(dentista, dataHora, null);

        // Assert
        assertFalse(resultado);
        verify(agendamentoRepository).findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testVerificarConflitoHorarioComConflito() {
        // Arrange
        String dentista = "Dr. Maria Santos";
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(agendamentos);

        // Act
        boolean resultado = agendamentoService.verificarConflitoHorario(dentista, dataHora, null);

        // Assert
        assertTrue(resultado);
        verify(agendamentoRepository).findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testBuscarPorStatus() {
        // Arrange
        String status = "AGENDADO";
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findAll()).thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.buscarPorStatus(status);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(status, resultado.get(0).getStatus());
        verify(agendamentoRepository).findAll();
    }

    @Test
    void testListarDentistas() {
        // Arrange
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        
        Agendamento agendamento2 = new Agendamento();
        agendamento2.setDentista("Dr. João Oliveira");
        agendamentos.add(agendamento2);
        
        when(agendamentoRepository.findAll()).thenReturn(agendamentos);

        // Act
        List<String> resultado = agendamentoService.listarDentistas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains("Dr. Maria Santos"));
        assertTrue(resultado.contains("Dr. João Oliveira"));
        verify(agendamentoRepository).findAll();
    }

    @Test
    void testIsHorarioDisponivel() {
        // Arrange
        String dentista = "Dr. Maria Santos";
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        when(agendamentoRepository.findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        // Act
        boolean resultado = agendamentoService.isHorarioDisponivel(dentista, dataHora);

        // Assert
        assertTrue(resultado);
        verify(agendamentoRepository).findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class));
        assertTrue(resultado);
        verify(agendamentoRepository).findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetHorariosDisponiveisPorData() {
        // Arrange
        String dentista = "Dr. Maria Santos";
        LocalDateTime data = LocalDateTime.now().plusDays(1);
        when(agendamentoRepository.findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        // Act
        List<String> resultado = agendamentoService.getHorariosDisponiveisPorData(dentista, data);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        // Verifica se tem horários no formato HH:mm
        assertTrue(resultado.stream().allMatch(h -> h.matches("\\d{2}:\\d{2}")));
        // Verifica se não tem horário de almoço (12:00)
        assertFalse(resultado.contains("12:00"));
        assertFalse(resultado.contains("12:30"));
    }

    @Test
    void testBuscarPorDentistaDataStatus() {
        // Arrange
        String dentista = "Dr. Maria Santos";
        // Usar a mesma data do agendamentoTeste (2 dias no futuro)
        LocalDateTime data = LocalDateTime.now().plusDays(2);
        String status = "AGENDADO";
        
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findAll()).thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.buscarPorDentistaDataStatus(dentista, data, status);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(dentista, resultado.get(0).getDentista());
        assertEquals(status, resultado.get(0).getStatus());
        verify(agendamentoRepository).findAll();
    }

    @Test
    void testBuscarPorDentistaDataStatusComFiltroNulo() {
        // Arrange
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findAll()).thenReturn(agendamentos);

        // Act - usar a mesma data do agendamentoTeste
        List<Agendamento> resultado = agendamentoService.buscarPorDentistaDataStatus(null, LocalDateTime.now().plusDays(2), null);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendamentoRepository).findAll();
    }

    @Test
    void testListarDentistasComNomeVazio() {
        // Arrange
        List<Agendamento> agendamentos = new ArrayList<>();
        Agendamento agendamentoVazio = new Agendamento();
        agendamentoVazio.setDentista(""); // Nome vazio
        agendamentos.add(agendamentoVazio);
        
        Agendamento agendamentoNulo = new Agendamento();
        agendamentoNulo.setDentista(null); // Nome nulo
        agendamentos.add(agendamentoNulo);
        
        agendamentos.add(agendamentoTeste); // Nome válido
        when(agendamentoRepository.findAll()).thenReturn(agendamentos);

        // Act
        List<String> resultado = agendamentoService.listarDentistas();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Dr. Maria Santos", resultado.get(0));
    }

    @Test
    void testIsHorarioNaoDisponivel() {
        // Arrange
        String dentista = "Dr. Maria Santos";
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(agendamentos);

        // Act
        boolean resultado = agendamentoService.isHorarioDisponivel(dentista, dataHora);

        // Assert
        assertFalse(resultado);
        verify(agendamentoRepository).findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve rejeitar agendamento com horário fora do expediente")
    void testRejeitarHorarioForaExpediente() {
        // Arrange
        String dentista = "Dr. Maria Santos";
        LocalDateTime dataHoraForaExpediente = LocalDateTime.now().plusDays(1)
            .withHour(18).withMinute(30).withSecond(0).withNano(0);
        
        // Act
        List<String> horariosDisponiveis = agendamentoService.getHorariosDisponiveisPorData(dentista, dataHoraForaExpediente);

        // Assert
        assertFalse(horariosDisponiveis.contains("18:30"));
    }

    @Test
    @DisplayName("Deve rejeitar agendamento em horário de almoço")
    void testRejeitarHorarioAlmoco() {
        // Arrange
        String dentista = "Dr. Maria Santos";
        LocalDateTime horarioAlmoco = LocalDateTime.now().plusDays(1)
            .withHour(12).withMinute(0).withSecond(0).withNano(0);
        
        // Act
        List<String> horariosDisponiveis = agendamentoService.getHorariosDisponiveisPorData(dentista, horarioAlmoco);

        // Assert
        assertFalse(horariosDisponiveis.contains("12:00"));
        assertFalse(horariosDisponiveis.contains("12:30"));
    }

    @Test
    @DisplayName("Deve verificar conflito em horário já agendado")
    void testVerificarConflitoHorarioExistente() {
        // Arrange
        String dentista = "Dr. Maria Santos";
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1)
            .withHour(14).withMinute(0).withSecond(0).withNano(0);
        
        // Simula consulta existente no mesmo horário
        Agendamento consultaExistente = new Agendamento();
        consultaExistente.setDentista(dentista);
        consultaExistente.setDataHora(dataHora);
        consultaExistente.setDuracaoMinutos(30);
        
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(consultaExistente);
        
        when(agendamentoRepository.findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(agendamentos);

        // Act
        boolean resultado = agendamentoService.verificarConflitoHorario(dentista, dataHora, null);

        // Assert
        assertTrue(resultado);
        verify(agendamentoRepository).findByDentistaAndDataHoraBetween(
                eq(dentista), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve permitir reagendamento com antecedência adequada")
    void testPermitirReagendamentoComAntecedencia() {
        // Arrange
        LocalDateTime dataAtual = LocalDateTime.now();
        agendamentoTeste.setDataHora(dataAtual.plusDays(2)); // Mais de 24h de antecedência
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoTeste));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoTeste);

        LocalDateTime novaData = dataAtual.plusDays(3)
            .withHour(14).withMinute(0).withSecond(0).withNano(0);

        // Act
        boolean resultado = agendamentoService.reagendar(1L, novaData);

        // Assert
        assertTrue(resultado);
        assertEquals("REAGENDADO", agendamentoTeste.getStatus());
        assertEquals(novaData, agendamentoTeste.getDataHora());
        verify(agendamentoRepository).save(agendamentoTeste);
    }

    @Test
    @DisplayName("Deve rejeitar reagendamento sem antecedência mínima")
    void testRejeitarReagendamentoSemAntecedencia() {
        // Arrange
        LocalDateTime dataAtual = LocalDateTime.now();
        agendamentoTeste.setDataHora(dataAtual.plusHours(1)); // Apenas 1 hora de antecedência
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoTeste));

        LocalDateTime novaData = dataAtual.plusDays(1);

        // Act & Assert
        boolean resultado = agendamentoService.reagendar(1L, novaData);
        
        // Assert
        assertFalse(resultado);
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }
}
