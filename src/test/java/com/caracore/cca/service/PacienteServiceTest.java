package com.caracore.cca.service;

import com.caracore.cca.model.Paciente;
import com.caracore.cca.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve buscar todos os pacientes")
    void deveBuscarTodosPacientes() {
        // Dado uma lista de pacientes
        List<Paciente> pacientes = Arrays.asList(
                new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321"),
                new Paciente("Maria Oliveira", "maria@teste.com", "(11) 91234-5678")
        );
        
        when(pacienteRepository.findAll()).thenReturn(pacientes);
        
        // Quando buscar todos
        List<Paciente> resultado = pacienteService.buscarTodos();
        
        // Então deve retornar a lista completa
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");
        assertThat(resultado.get(1).getNome()).isEqualTo("Maria Oliveira");
    }
    
    @Test
    @DisplayName("Deve buscar paciente por ID")
    void deveBuscarPacientePorId() {
        // Dado um paciente existente
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        paciente.setId(1L);
        
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.findById(2L)).thenReturn(Optional.empty());
        
        // Quando buscar por ID existente
        Optional<Paciente> resultado1 = pacienteService.buscarPorId(1L);
        
        // Então deve retornar o paciente
        assertThat(resultado1).isPresent();
        assertThat(resultado1.get().getNome()).isEqualTo("João Silva");
        
        // Quando buscar por ID inexistente
        Optional<Paciente> resultado2 = pacienteService.buscarPorId(2L);
        
        // Então deve retornar vazio
        assertThat(resultado2).isEmpty();
    }
    
    @Test
    @DisplayName("Deve salvar novo paciente")
    void deveSalvarNovoPaciente() {
        // Dado um novo paciente
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        
        // Mock do repositório
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);
        
        // Quando salvar
        Paciente resultado = pacienteService.salvar(paciente);
        
        // Então deve retornar o paciente salvo
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getTelefone()).isEqualTo("(11) 98765-4321");
        
        // Verificar chamada do repositório
        verify(pacienteRepository, times(1)).save(paciente);
    }
    
    @Test
    @DisplayName("Deve atualizar paciente existente")
    void deveAtualizarPacienteExistente() {
        // Dado um paciente existente
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        paciente.setId(1L);
        
        // Mock do repositório
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(pacienteRepository.save(paciente)).thenReturn(paciente);
        
        // Quando atualizar
        Paciente resultado = pacienteService.atualizar(paciente);
        
        // Então deve retornar o paciente atualizado
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        
        // Verificar chamadas do repositório
        verify(pacienteRepository).existsById(1L);
        verify(pacienteRepository).save(paciente);
    }
    
    @Test
    @DisplayName("Deve rejeitar atualização de paciente inexistente")
    void deveRejeitarAtualizacaoPacienteInexistente() {
        // Dado um paciente com ID inexistente
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        paciente.setId(99L);
        
        // Mock do repositório
        when(pacienteRepository.existsById(99L)).thenReturn(false);
        
        // Quando tentar atualizar, então deve lançar exceção
        assertThatThrownBy(() -> pacienteService.atualizar(paciente))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("não encontrado");
        
        // Verificar que save não foi chamado
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }
    
    @Test
    @DisplayName("Deve buscar paciente por nome")
    void deveBuscarPacientePorNome() {
        // Dado uma lista de pacientes com nomes similares
        List<Paciente> pacientes = Arrays.asList(
                new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321"),
                new Paciente("João Oliveira", "joao.oliveira@teste.com", "(11) 91234-5678")
        );
        
        // Mock do repositório
        when(pacienteRepository.findByNomeContainingIgnoreCase("João"))
            .thenReturn(pacientes);
        when(pacienteRepository.findByNomeContainingIgnoreCase("Silva"))
            .thenReturn(Collections.singletonList(pacientes.get(0)));
        
        // Quando buscar por nome parcial
        List<Paciente> resultado1 = pacienteService.buscarPorNome("João");
        
        // Então deve retornar todos que contenham o nome
        assertThat(resultado1).hasSize(2);
        
        // Quando buscar por sobrenome específico
        List<Paciente> resultado2 = pacienteService.buscarPorNome("Silva");
        
        // Então deve retornar apenas o paciente com esse sobrenome
        assertThat(resultado2).hasSize(1);
        assertThat(resultado2.get(0).getNome()).isEqualTo("João Silva");
    }
    
    @Test
    @DisplayName("Deve verificar se paciente existe por ID")
    void deveVerificarExistenciaPaciente() {
        // Mock do repositório
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(pacienteRepository.existsById(2L)).thenReturn(false);
        
        // Verificações
        assertThat(pacienteService.existePorId(1L)).isTrue();
        assertThat(pacienteService.existePorId(2L)).isFalse();
    }
    
    @Test
    @DisplayName("Deve atualizar apenas o telefone do paciente")
    void deveAtualizarApenasTelefonePaciente() {
        // Dado um paciente existente
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 99999-9999");
        paciente.setId(1L);
        
        // Mock do repositório
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Quando buscar e atualizar telefone
        Optional<Paciente> pacienteOpt = pacienteService.buscarPorId(1L);
        assertThat(pacienteOpt).isPresent();
        
        Paciente pacienteEncontrado = pacienteOpt.get();
        pacienteEncontrado.setTelefone("(11) 98765-4321"); // Atualiza só o telefone
        
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        Paciente resultado = pacienteService.atualizar(pacienteEncontrado);
        
        // Então deve retornar o paciente com telefone atualizado e demais dados inalterados
        assertThat(resultado.getTelefone()).isEqualTo("(11) 98765-4321"); // Novo telefone
        assertThat(resultado.getNome()).isEqualTo("João Silva"); // Nome inalterado
        assertThat(resultado.getEmail()).isEqualTo("joao@teste.com"); // Email inalterado
    }
    
    @Test
    @DisplayName("Deve buscar pacientes com telefone WhatsApp específico")
    void deveEncontrarPacienteComTelefoneEspecifico() {
        // Dado pacientes com diferentes telefones
        Paciente paciente1 = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        paciente1.setId(1L);
        Paciente paciente2 = new Paciente("Maria Oliveira", "maria@teste.com", "(11) 91234-5678");
        paciente2.setId(2L);
        
        // Mock do repositório para busca de todos os pacientes
        when(pacienteRepository.findAll())
            .thenReturn(Arrays.asList(paciente1, paciente2));
        
        // Quando buscar todos os pacientes
        List<Paciente> todosPacientes = pacienteService.buscarTodos();
        
        // Então deve ser possível filtrar por telefone manualmente
        List<Paciente> pacientesComTelefoneEspecifico = todosPacientes.stream()
            .filter(p -> p.getTelefone() != null && p.getTelefone().contains("98765"))
            .toList();
        
        // Verificações
        assertThat(pacientesComTelefoneEspecifico).hasSize(1);
        assertThat(pacientesComTelefoneEspecifico.get(0).getNome()).isEqualTo("João Silva");
        assertThat(pacientesComTelefoneEspecifico.get(0).getTelefone()).isEqualTo("(11) 98765-4321");
    }
    
    // --- TESTES DE FUNCIONALIDADES LGPD ---

    @Test
    @DisplayName("Deve salvar paciente com campos LGPD padrão")
    void deveSalvarPacienteComCamposLgpdPadrao() {
        // Dado um paciente sem configurações LGPD específicas
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);
        
        // Quando salvar o paciente
        Paciente resultado = pacienteService.salvar(paciente);
        
        // Então deve ter os valores padrão LGPD
        assertThat(resultado.getConsentimentoLgpd()).isEqualTo(false);
        assertThat(resultado.getConsentimentoConfirmado()).isEqualTo(false);
        assertThat(resultado.getDataConsentimento()).isNull();
        verify(pacienteRepository).save(paciente);
    }

    @Test
    @DisplayName("Deve salvar paciente com consentimento LGPD enviado")
    void deveSalvarPacienteComConsentimentoLgpdEnviado() {
        // Dado um paciente com consentimento LGPD marcado como enviado
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        paciente.setConsentimentoLgpd(true);
        paciente.setDataConsentimento(agora);
        
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);
        
        // Quando salvar o paciente
        Paciente resultado = pacienteService.salvar(paciente);
        
        // Então deve manter as configurações LGPD
        assertThat(resultado.getConsentimentoLgpd()).isTrue();
        assertThat(resultado.getDataConsentimento()).isEqualTo(agora);
        assertThat(resultado.getConsentimentoConfirmado()).isFalse();
        verify(pacienteRepository).save(paciente);
    }

    @Test
    @DisplayName("Deve salvar paciente com consentimento LGPD confirmado")
    void deveSalvarPacienteComConsentimentoLgpdConfirmado() {
        // Dado um paciente com consentimento LGPD completo
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        paciente.setConsentimentoLgpd(true);
        paciente.setConsentimentoConfirmado(true);
        paciente.setDataConsentimento(agora);
        
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);
        
        // Quando salvar o paciente
        Paciente resultado = pacienteService.salvar(paciente);
        
        // Então deve manter todas as configurações LGPD
        assertThat(resultado.getConsentimentoLgpd()).isTrue();
        assertThat(resultado.getConsentimentoConfirmado()).isTrue();
        assertThat(resultado.getDataConsentimento()).isEqualTo(agora);
        verify(pacienteRepository).save(paciente);
    }

    @Test
    @DisplayName("Deve atualizar paciente preservando dados LGPD existentes")
    void deveAtualizarPacientePreservandoDadosLgpd() {
        // Dado um paciente existente com dados LGPD
        Paciente pacienteExistente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        pacienteExistente.setId(1L);
        java.time.LocalDateTime dataOriginal = java.time.LocalDateTime.now().minusDays(1);
        pacienteExistente.setConsentimentoLgpd(true);
        pacienteExistente.setConsentimentoConfirmado(true);
        pacienteExistente.setDataConsentimento(dataOriginal);
        
        // Dado um paciente atualizado com mesmo ID
        Paciente pacienteAtualizado = new Paciente("João Silva Santos", "joao.santos@teste.com", "(11) 98765-4321");
        pacienteAtualizado.setId(1L);
        pacienteAtualizado.setConsentimentoLgpd(true);
        pacienteAtualizado.setConsentimentoConfirmado(true);
        pacienteAtualizado.setDataConsentimento(dataOriginal);
        
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteAtualizado);
        
        // Quando atualizar o paciente
        Paciente resultado = pacienteService.atualizar(pacienteAtualizado);
        
        // Então deve preservar os dados LGPD
        assertThat(resultado.getConsentimentoLgpd()).isTrue();
        assertThat(resultado.getConsentimentoConfirmado()).isTrue();
        assertThat(resultado.getDataConsentimento()).isEqualTo(dataOriginal);
        assertThat(resultado.getNome()).isEqualTo("João Silva Santos");
        assertThat(resultado.getEmail()).isEqualTo("joao.santos@teste.com");
        verify(pacienteRepository).save(pacienteAtualizado);
    }

    @Test
    @DisplayName("Deve buscar pacientes com consentimento LGPD pendente")
    void deveBuscarPacientesComConsentimentoLgpdPendente() {
        // Dado pacientes com diferentes status LGPD
        Paciente pacienteComLgpd = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        pacienteComLgpd.setConsentimentoLgpd(true);
        pacienteComLgpd.setConsentimentoConfirmado(true);
        
        Paciente pacienteSemLgpd = new Paciente("Maria Oliveira", "maria@teste.com", "(11) 91234-5678");
        pacienteSemLgpd.setConsentimentoLgpd(false);
        pacienteSemLgpd.setConsentimentoConfirmado(false);
        
        when(pacienteRepository.findAll())
            .thenReturn(Arrays.asList(pacienteComLgpd, pacienteSemLgpd));
        
        // Quando buscar todos os pacientes
        List<Paciente> todosPacientes = pacienteService.buscarTodos();
        
        // Então deve ser possível filtrar por status LGPD
        List<Paciente> pacientesSemConsentimento = todosPacientes.stream()
            .filter(p -> !p.getConsentimentoLgpd() || !p.getConsentimentoConfirmado())
            .toList();
        
        assertThat(pacientesSemConsentimento).hasSize(1);
        assertThat(pacientesSemConsentimento.get(0).getNome()).isEqualTo("Maria Oliveira");
    }

    @Test
    @DisplayName("Deve permitir alterar apenas status LGPD do paciente")
    void devePermitirAlterarApenasStatusLgpdPaciente() {
        // Dado um paciente existente
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        paciente.setId(1L);
        paciente.setConsentimentoLgpd(false);
        paciente.setConsentimentoConfirmado(false);
        
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Quando alterar apenas o status LGPD
        paciente.setConsentimentoLgpd(true);
        paciente.setDataConsentimento(java.time.LocalDateTime.now());
        Paciente resultado = pacienteService.atualizar(paciente);
        
        // Então deve alterar apenas os campos LGPD
        assertThat(resultado.getConsentimentoLgpd()).isTrue();
        assertThat(resultado.getDataConsentimento()).isNotNull();
        assertThat(resultado.getConsentimentoConfirmado()).isFalse(); // Não foi alterado
        assertThat(resultado.getNome()).isEqualTo("João Silva"); // Dados pessoais inalterados
        assertThat(resultado.getEmail()).isEqualTo("joao@teste.com");
        assertThat(resultado.getTelefone()).isEqualTo("(11) 98765-4321");
    }
}
