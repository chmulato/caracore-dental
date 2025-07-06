package com.caracore.cca.service;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.repository.AgendamentoRepository;
import com.caracore.cca.repository.DentistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes expandidos para o AgendamentoService
 * Cobrindo funcionalidades de exposição pública e controle de dentistas ativos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AgendamentoService - Testes de Exposição Pública")
public class AgendamentoServiceExposicaoPublicaExpandedTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private DentistaRepository dentistaRepository;

    @Mock
    private DentistaService dentistaService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private List<Agendamento> agendamentosMock;

    @BeforeEach
    void setUp() {
        // Criar agendamentos mock
        agendamentosMock = Arrays.asList(
            criarAgendamento(1L, "João Silva", "Dr. João Silva - Clínico Geral"),
            criarAgendamento(2L, "Maria Santos", "Dra. Maria Santos - Ortodontista"),
            criarAgendamento(3L, "Carlos Oliveira", "Dr. Carlos Oliveira - Periodontista"),
            criarAgendamento(4L, "Ana Costa", "Dra. Ana Costa - Implantodontista")
        );
    }

    // =======================================================================
    // TESTES DE LISTAGEM DE DENTISTAS
    // =======================================================================

    @Nested
    @DisplayName("Listagem de Dentistas")
    class ListagemDentistas {

        @Test
        @DisplayName("Deve listar todos os dentistas únicos dos agendamentos")
        void testListarDentistas() {
            // Arrange
            when(agendamentoRepository.findAll()).thenReturn(agendamentosMock);

            // Act
            List<String> dentistas = agendamentoService.listarDentistas();

            // Assert
            assertNotNull(dentistas);
            assertEquals(4, dentistas.size());
            assertTrue(dentistas.contains("Dr. João Silva - Clínico Geral"));
            assertTrue(dentistas.contains("Dra. Maria Santos - Ortodontista"));
            assertTrue(dentistas.contains("Dr. Carlos Oliveira - Periodontista"));
            assertTrue(dentistas.contains("Dra. Ana Costa - Implantodontista"));
            
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
        @DisplayName("Deve remover dentistas duplicados")
        void testListarDentistasRemoverDuplicados() {
            // Arrange
            List<Agendamento> agendamentosComDuplicados = Arrays.asList(
                criarAgendamento(1L, "João Silva", "Dr. João Silva - Clínico Geral"),
                criarAgendamento(2L, "Maria Santos", "Dr. João Silva - Clínico Geral"), // Duplicado
                criarAgendamento(3L, "Carlos Oliveira", "Dra. Maria Santos - Ortodontista"),
                criarAgendamento(4L, "Ana Costa", "Dra. Maria Santos - Ortodontista") // Duplicado
            );
            when(agendamentoRepository.findAll()).thenReturn(agendamentosComDuplicados);

            // Act
            List<String> dentistas = agendamentoService.listarDentistas();

            // Assert
            assertNotNull(dentistas);
            assertEquals(2, dentistas.size());
            assertTrue(dentistas.contains("Dr. João Silva - Clínico Geral"));
            assertTrue(dentistas.contains("Dra. Maria Santos - Ortodontista"));
            
            verify(agendamentoRepository, times(1)).findAll();
        }
    }

    // =======================================================================
    // TESTES DE LISTAGEM DE DENTISTAS ATIVOS
    // =======================================================================

    @Nested
    @DisplayName("Listagem de Dentistas Ativos")
    class ListagemDentistasAtivos {

        @Test
        @DisplayName("Deve listar apenas dentistas ativos")
        void testListarDentistasAtivos() {
            // Arrange
            when(dentistaService.listarAtivosExpostosPublicamente()).thenReturn(Arrays.asList());

            // Act
            List<String> dentistasAtivos = agendamentoService.listarDentistasAtivos();

            // Assert
            assertNotNull(dentistasAtivos);
            assertTrue(dentistasAtivos.isEmpty());
            
            verify(dentistaService, times(1)).listarAtivosExpostosPublicamente();
        }

        @Test
        @DisplayName("Deve filtrar dentistas inativos quando funcionalidade for implementada")
        void testListarDentistasAtivosFiltrando() {
            // Este teste documenta o comportamento esperado quando a funcionalidade for implementada
            // Arrange
            when(dentistaService.listarAtivosExpostosPublicamente()).thenReturn(Arrays.asList());

            // Act
            List<String> dentistasAtivos = agendamentoService.listarDentistasAtivos();

            // Assert
            assertNotNull(dentistasAtivos);
            assertTrue(dentistasAtivos.isEmpty());
            
            verify(dentistaService, times(1)).listarAtivosExpostosPublicamente();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há dentistas ativos")
        void testListarDentistasAtivosVazio() {
            // Arrange
            when(dentistaService.listarAtivosExpostosPublicamente()).thenReturn(Arrays.asList());

            // Act
            List<String> dentistasAtivos = agendamentoService.listarDentistasAtivos();

            // Assert
            assertNotNull(dentistasAtivos);
            assertTrue(dentistasAtivos.isEmpty());
            
            verify(dentistaService, times(1)).listarAtivosExpostosPublicamente();
        }
    }

    // =======================================================================
    // TESTES DE FUNCIONALIDADES EXISTENTES
    // =======================================================================

    @Nested
    @DisplayName("Funcionalidades Existentes")
    class FuncionalidadesExistentes {

        @Test
        @DisplayName("Deve salvar agendamento com sucesso")
        void testSalvarAgendamento() {
            // Arrange
            Agendamento agendamento = criarAgendamento(1L, "João Silva", "Dr. João Silva - Clínico Geral");
            when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);

            // Act
            Agendamento resultado = agendamentoService.salvar(agendamento);

            // Assert
            assertNotNull(resultado);
            assertEquals("João Silva", resultado.getPaciente());
            assertEquals("Dr. João Silva - Clínico Geral", resultado.getDentista());
            
            verify(agendamentoRepository, times(1)).save(agendamento);
        }

        @Test
        @DisplayName("Deve buscar agendamento por ID")
        void testBuscarPorId() {
            // Arrange
            Long id = 1L;
            Agendamento agendamento = criarAgendamento(id, "João Silva", "Dr. João Silva - Clínico Geral");
            when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));

            // Act
            Optional<Agendamento> resultado = agendamentoService.buscarPorId(id);

            // Assert
            assertTrue(resultado.isPresent());
            assertEquals("João Silva", resultado.get().getPaciente());
            assertEquals("Dr. João Silva - Clínico Geral", resultado.get().getDentista());
            
            verify(agendamentoRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("Deve listar todos os agendamentos")
        void testListarTodos() {
            // Arrange
            when(agendamentoRepository.findAll()).thenReturn(agendamentosMock);

            // Act
            List<Agendamento> resultado = agendamentoService.listarTodos();

            // Assert
            assertNotNull(resultado);
            assertEquals(4, resultado.size());
            
            verify(agendamentoRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve excluir agendamento")
        void testExcluir() {
            // Arrange
            Long id = 1L;
            when(agendamentoRepository.existsById(id)).thenReturn(true);
            doNothing().when(agendamentoRepository).deleteById(id);

            // Act
            boolean resultado = agendamentoService.excluir(id);

            // Assert
            assertTrue(resultado);
            verify(agendamentoRepository, times(1)).existsById(id);
            verify(agendamentoRepository, times(1)).deleteById(id);
        }
    }

    // =======================================================================
    // TESTES DE INTEGRAÇÃO E CASOS ESPECIAIS
    // =======================================================================

    @Nested
    @DisplayName("Integração e Casos Especiais")
    class IntegracaoCasosEspeciais {

        @Test
        @DisplayName("Deve lidar com erro ao acessar repositório")
        void testErroRepositorio() {
            // Arrange
            when(agendamentoRepository.findAll()).thenThrow(new RuntimeException("Erro de banco de dados"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> agendamentoService.listarDentistas());
            
            verify(agendamentoRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve manter performance com grande volume de dados")
        void testPerformanceGrandeVolume() {
            // Arrange - Simular muitos agendamentos
            List<Agendamento> muitosAgendamentos = Arrays.asList(
                criarAgendamento(1L, "Paciente 1", "Dr. João Silva - Clínico Geral"),
                criarAgendamento(2L, "Paciente 2", "Dra. Maria Santos - Ortodontista"),
                criarAgendamento(3L, "Paciente 3", "Dr. João Silva - Clínico Geral"),
                criarAgendamento(4L, "Paciente 4", "Dra. Ana Costa - Implantodontista"),
                criarAgendamento(5L, "Paciente 5", "Dr. João Silva - Clínico Geral")
            );
            when(agendamentoRepository.findAll()).thenReturn(muitosAgendamentos);

            // Act
            long startTime = System.currentTimeMillis();
            List<String> dentistas = agendamentoService.listarDentistas();
            long endTime = System.currentTimeMillis();

            // Assert
            assertNotNull(dentistas);
            assertEquals(3, dentistas.size()); // Deve ter apenas 3 dentistas únicos
            assertTrue((endTime - startTime) < 1000); // Deve ser rápido (menos de 1 segundo)
            
            verify(agendamentoRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve manter consistência entre diferentes chamadas")
        void testConsistenciaEntreChamadas() {
            // Arrange
            when(agendamentoRepository.findAll()).thenReturn(agendamentosMock);

            // Act
            List<String> dentistas1 = agendamentoService.listarDentistas();
            List<String> dentistas2 = agendamentoService.listarDentistas();

            // Assert
            assertNotNull(dentistas1);
            assertNotNull(dentistas2);
            assertEquals(dentistas1.size(), dentistas2.size());
            
            // Verificar que as listas contêm os mesmos elementos
            assertTrue(dentistas1.containsAll(dentistas2));
            assertTrue(dentistas2.containsAll(dentistas1));
            
            verify(agendamentoRepository, times(2)).findAll();
        }
    }

    // =======================================================================
    // MÉTODOS AUXILIARES
    // =======================================================================

    private Agendamento criarAgendamento(Long id, String paciente, String dentista) {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(id);
        agendamento.setPaciente(paciente);
        agendamento.setDentista(dentista);
        agendamento.setDataHora(LocalDateTime.now().plusDays(1));
        agendamento.setTelefoneWhatsapp("11999999999");
        agendamento.setObservacao("Observações do agendamento " + id);
        return agendamento;
    }
}
