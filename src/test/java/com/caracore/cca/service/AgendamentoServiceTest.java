package com.caracore.cca.service;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.model.Dentista;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.repository.AgendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
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
    private Paciente pacienteTeste;
    private Dentista dentistaTeste;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar dados de teste
        pacienteTeste = new Paciente();
        pacienteTeste.setId(1L);
        pacienteTeste.setNome("João Silva");
        pacienteTeste.setTelefone("(11) 99999-9999");
        pacienteTeste.setEmail("joao@email.com");

        dentistaTeste = new Dentista();
        dentistaTeste.setId(1L);
        dentistaTeste.setNome("Dr. Maria Santos");
        dentistaTeste.setCro("12345");
        dentistaTeste.setEspecialidade("Clínica Geral");

        agendamentoTeste = new Agendamento();
        agendamentoTeste.setId(1L);
        agendamentoTeste.setPaciente(pacienteTeste);
        agendamentoTeste.setDentista(dentistaTeste);
        agendamentoTeste.setDataHoraAgendamento(LocalDateTime.now().plusDays(1));
        agendamentoTeste.setStatus("AGENDADO");
        agendamentoTeste.setObservacoes("Consulta de rotina");
        agendamentoTeste.setDuracao(30);
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
        verify(agendamentoRepository).save(agendamentoTeste);
    }

    @Test
    void testAtualizar() {
        // Arrange
        when(agendamentoRepository.existsById(1L)).thenReturn(true);
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoTeste);

        // Act
        Agendamento resultado = agendamentoService.atualizar(agendamentoTeste);

        // Assert
        assertNotNull(resultado);
        assertEquals(agendamentoTeste.getId(), resultado.getId());
        verify(agendamentoRepository).existsById(1L);
        verify(agendamentoRepository).save(agendamentoTeste);
    }

    @Test
    void testAtualizarNaoEncontrado() {
        // Arrange
        when(agendamentoRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            agendamentoService.atualizar(agendamentoTeste);
        });
        verify(agendamentoRepository).existsById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
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
        boolean resultado = agendamentoService.cancelarAgendamento(1L);

        // Assert
        assertTrue(resultado);
        assertEquals("CANCELADO", agendamentoTeste.getStatus());
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(agendamentoTeste);
    }

    @Test
    void testMarcarComoRealizada() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoTeste));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoTeste);

        // Act
        boolean resultado = agendamentoService.marcarComoRealizada(1L);

        // Assert
        assertTrue(resultado);
        assertEquals("REALIZADO", agendamentoTeste.getStatus());
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(agendamentoTeste);
    }

    @Test
    void testReagendarAgendamento() {
        // Arrange
        LocalDateTime novaDataHora = LocalDateTime.now().plusDays(2);
        String motivo = "Conflito de horário";
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoTeste));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoTeste);

        // Act
        boolean resultado = agendamentoService.reagendarAgendamento(1L, novaDataHora, motivo);

        // Assert
        assertTrue(resultado);
        assertEquals("REAGENDADO", agendamentoTeste.getStatus());
        assertEquals(novaDataHora, agendamentoTeste.getDataHoraAgendamento());
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(agendamentoTeste);
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
        assertThrows(RuntimeException.class, () -> {
            agendamentoService.excluir(1L);
        });
        verify(agendamentoRepository).existsById(1L);
        verify(agendamentoRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void testVerificarConflito() {
        // Arrange
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        Integer duracao = 30;
        Long dentistaId = 1L;
        
        List<Agendamento> agendamentosConflitantes = new ArrayList<>();
        when(agendamentoRepository.findConflitosHorario(any(LocalDateTime.class), any(LocalDateTime.class), 
                                                       eq(dentistaId), isNull()))
                .thenReturn(agendamentosConflitantes);

        // Act
        boolean resultado = agendamentoService.verificarConflito(dataHora, duracao, dentistaId, null);

        // Assert
        assertFalse(resultado);
        verify(agendamentoRepository).findConflitosHorario(any(LocalDateTime.class), any(LocalDateTime.class), 
                                                          eq(dentistaId), isNull());
    }

    @Test
    void testVerificarConflitoComConflito() {
        // Arrange
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        Integer duracao = 30;
        Long dentistaId = 1L;
        
        List<Agendamento> agendamentosConflitantes = new ArrayList<>();
        agendamentosConflitantes.add(agendamentoTeste);
        when(agendamentoRepository.findConflitosHorario(any(LocalDateTime.class), any(LocalDateTime.class), 
                                                       eq(dentistaId), isNull()))
                .thenReturn(agendamentosConflitantes);

        // Act
        boolean resultado = agendamentoService.verificarConflito(dataHora, duracao, dentistaId, null);

        // Assert
        assertTrue(resultado);
        verify(agendamentoRepository).findConflitosHorario(any(LocalDateTime.class), any(LocalDateTime.class), 
                                                          eq(dentistaId), isNull());
    }

    @Test
    void testListarPorStatus() {
        // Arrange
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findByStatusOrderByDataHoraAgendamentoAsc("AGENDADO"))
                .thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.listarPorStatus("AGENDADO");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("AGENDADO", resultado.get(0).getStatus());
        verify(agendamentoRepository).findByStatusOrderByDataHoraAgendamentoAsc("AGENDADO");
    }

    @Test
    void testListarPorPeriodo() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(1);
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findByDataHoraAgendamentoBetweenOrderByDataHoraAgendamentoAsc(inicio, fim))
                .thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.listarPorPeriodo(inicio, fim);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendamentoRepository).findByDataHoraAgendamentoBetweenOrderByDataHoraAgendamentoAsc(inicio, fim);
    }

    @Test
    void testBuscarPorDentista() {
        // Arrange
        Long dentistaId = 1L;
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findByDentistaIdOrderByDataHoraAgendamentoAsc(dentistaId))
                .thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.buscarPorDentista(dentistaId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendamentoRepository).findByDentistaIdOrderByDataHoraAgendamentoAsc(dentistaId);
    }

    @Test
    void testBuscarPorPaciente() {
        // Arrange
        Long pacienteId = 1L;
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findByPacienteIdOrderByDataHoraAgendamentoDesc(pacienteId))
                .thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.buscarPorPaciente(pacienteId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendamentoRepository).findByPacienteIdOrderByDataHoraAgendamentoDesc(pacienteId);
    }

    @Test
    void testListarHoje() {
        // Arrange
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findAgendamentosHoje()).thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.listarHoje();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendamentoRepository).findAgendamentosHoje();
    }

    @Test
    void testListarProximosAgendamentos() {
        // Arrange
        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(agendamentoTeste);
        when(agendamentoRepository.findProximosAgendamentos()).thenReturn(agendamentos);

        // Act
        List<Agendamento> resultado = agendamentoService.listarProximosAgendamentos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendamentoRepository).findProximosAgendamentos();
    }
}
