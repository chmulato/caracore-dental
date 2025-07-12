package com.caracore.cca.service;

import com.caracore.cca.model.Dentista;
import com.caracore.cca.repository.DentistaRepository;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DentistaServiceTest {

    @Mock
    private DentistaRepository dentistaRepository;
    @Mock
    private UserActivityLogger activityLogger;
    @Mock
    private Principal principal;

    @InjectMocks
    private DentistaService dentistaService;

    private Dentista dentista1;
    private Dentista dentista2;
    private Dentista dentistaInativo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        dentista1 = new Dentista();
        dentista1.setId(1L);
        dentista1.setNome("Dr. João Silva");
        dentista1.setEmail("joao@teste.com");
        dentista1.setAtivo(true);
        dentista1.setExpostoPublicamente(true);
        
        dentista2 = new Dentista();
        dentista2.setId(2L);
        dentista2.setNome("Dra. Maria Santos");
        dentista2.setEmail("maria@teste.com");
        dentista2.setAtivo(true);
        dentista2.setExpostoPublicamente(false);
        
        dentistaInativo = new Dentista();
        dentistaInativo.setId(3L);
        dentistaInativo.setNome("Dr. Pedro Costa");
        dentistaInativo.setEmail("pedro@teste.com");
        dentistaInativo.setAtivo(false);
        dentistaInativo.setExpostoPublicamente(false);
        
        when(principal.getName()).thenReturn("admin");
    }

    @Test
    @DisplayName("Deve listar todos os dentistas")
    void deveListarTodosDentistas() {
        // Given
        when(dentistaRepository.findAll()).thenReturn(Arrays.asList(dentista1, dentista2, dentistaInativo));
        
        // When
        List<Dentista> result = dentistaService.listarTodos();
        
        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(dentista1, dentista2, dentistaInativo);
        verify(dentistaRepository).findAll();
    }

    @Test
    @DisplayName("Deve listar apenas dentistas ativos")
    void deveListarApenasDentistasAtivos() {
        // Given
        when(dentistaRepository.findByAtivoTrue()).thenReturn(Arrays.asList(dentista1, dentista2));
        
        // When
        List<Dentista> result = dentistaService.listarAtivos();
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dentista1, dentista2);
        assertThat(result).doesNotContain(dentistaInativo);
        verify(dentistaRepository).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve listar apenas dentistas ativos e expostos publicamente")
    void deveListarDentistasAtivosExpostosPublicamente() {
        // Given
        when(dentistaRepository.findByAtivoTrueAndExpostoPublicamenteTrue()).thenReturn(Arrays.asList(dentista1));
        
        // When
        List<Dentista> result = dentistaService.listarAtivosExpostosPublicamente();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(dentista1);
        assertThat(result).doesNotContain(dentista2, dentistaInativo);
        verify(dentistaRepository).findByAtivoTrueAndExpostoPublicamenteTrue();
    }

    @Test
    @DisplayName("Deve buscar dentista por ID")
    void deveBuscarDentistaPorId() {
        // Given
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista1));
        
        // When
        Optional<Dentista> result = dentistaService.buscarPorId(1L);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(dentista1);
        verify(dentistaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando dentista não encontrado por ID")
    void deveRetornarOptionalVazioQuandoDentistaNaoEncontradoPorId() {
        // Given
        when(dentistaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        Optional<Dentista> result = dentistaService.buscarPorId(999L);
        
        // Then
        assertThat(result).isEmpty();
        verify(dentistaRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve buscar dentista por email")
    void deveBuscarDentistaPorEmail() {
        // Given
        when(dentistaRepository.findByEmail("joao@teste.com")).thenReturn(Optional.of(dentista1));
        
        // When
        Optional<Dentista> result = dentistaService.buscarPorEmail("joao@teste.com");
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(dentista1);
        verify(dentistaRepository).findByEmail("joao@teste.com");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando dentista não encontrado por email")
    void deveRetornarOptionalVazioQuandoDentistaNaoEncontradoPorEmail() {
        // Given
        when(dentistaRepository.findByEmail("inexistente@teste.com")).thenReturn(Optional.empty());
        
        // When
        Optional<Dentista> result = dentistaService.buscarPorEmail("inexistente@teste.com");
        
        // Then
        assertThat(result).isEmpty();
        verify(dentistaRepository).findByEmail("inexistente@teste.com");
    }

    @Test
    @DisplayName("Deve buscar dentistas por termo")
    void deveBuscarDentistasPorTermo() {
        // Given
        when(dentistaRepository.buscarPorTermo("Silva")).thenReturn(Arrays.asList(dentista1));
        
        // When
        List<Dentista> result = dentistaService.buscarPorTermo("Silva");
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(dentista1);
        verify(dentistaRepository).buscarPorTermo("Silva");
    }

    @Test
    @DisplayName("Deve listar todos quando termo de busca for nulo")
    void deveListarTodosQuandoTermoBuscaNulo() {
        // Given
        when(dentistaRepository.findAll()).thenReturn(Arrays.asList(dentista1, dentista2));
        
        // When
        List<Dentista> result = dentistaService.buscarPorTermo(null);
        
        // Then
        assertThat(result).hasSize(2);
        verify(dentistaRepository).findAll();
        verify(dentistaRepository, never()).buscarPorTermo(anyString());
    }

    @Test
    @DisplayName("Deve listar todos quando termo de busca for vazio")
    void deveListarTodosQuandoTermoBuscaVazio() {
        // Given
        when(dentistaRepository.findAll()).thenReturn(Arrays.asList(dentista1, dentista2));
        
        // When
        List<Dentista> result = dentistaService.buscarPorTermo("   ");
        
        // Then
        assertThat(result).hasSize(2);
        verify(dentistaRepository).findAll();
        verify(dentistaRepository, never()).buscarPorTermo(anyString());
    }

    @Test
    @DisplayName("Deve salvar novo dentista e registrar atividade")
    void deveSalvarNovoDentistaERegistrarAtividade() {
        // Given
        Dentista novoDentista = new Dentista();
        novoDentista.setNome("Dr. Novo Dentista");
        
        Dentista dentistaSalvo = new Dentista();
        dentistaSalvo.setId(10L);
        dentistaSalvo.setNome("Dr. Novo Dentista");
        
        when(dentistaRepository.save(novoDentista)).thenReturn(dentistaSalvo);
        
        // When
        Dentista result = dentistaService.salvar(novoDentista, principal);
        
        // Then
        assertThat(result).isEqualTo(dentistaSalvo);
        verify(dentistaRepository).save(novoDentista);
        verify(activityLogger).logActivity(
            "CADASTRAR_DENTISTA",
            "admin",
            "10",
            "Dentista cadastrado: Dr. Novo Dentista"
        );
    }

    @Test
    @DisplayName("Deve atualizar dentista existente e registrar atividade")
    void deveAtualizarDentistaExistenteERegistrarAtividade() {
        // Given
        dentista1.setNome("Dr. João Silva Atualizado");
        when(dentistaRepository.save(dentista1)).thenReturn(dentista1);
        
        // When
        Dentista result = dentistaService.salvar(dentista1, principal);
        
        // Then
        assertThat(result).isEqualTo(dentista1);
        verify(dentistaRepository).save(dentista1);
        verify(activityLogger).logActivity(
            "ATUALIZAR_DENTISTA",
            "admin",
            "1",
            "Dentista atualizado: Dr. João Silva Atualizado"
        );
    }

    @Test
    @DisplayName("Deve salvar dentista sem principal e usar 'sistema' como usuário")
    void deveSalvarDentistaSemPrincipalEUsarSistemaComoUsuario() {
        // Given
        Dentista novoDentista = new Dentista();
        novoDentista.setNome("Dr. Sistema");
        
        Dentista dentistaSalvo = new Dentista();
        dentistaSalvo.setId(20L);
        dentistaSalvo.setNome("Dr. Sistema");
        
        when(dentistaRepository.save(novoDentista)).thenReturn(dentistaSalvo);
        
        // When
        Dentista result = dentistaService.salvar(novoDentista, null);
        
        // Then
        assertThat(result).isEqualTo(dentistaSalvo);
        verify(activityLogger).logActivity(
            "CADASTRAR_DENTISTA",
            "sistema",
            "20",
            "Dentista cadastrado: Dr. Sistema"
        );
    }

    @Test
    @DisplayName("Deve excluir dentista (soft delete) e registrar atividade")
    void deveExcluirDentistaERegistrarAtividade() {
        // Given
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista1));
        when(dentistaRepository.save(dentista1)).thenReturn(dentista1);
        
        // When
        boolean result = dentistaService.excluir(1L, principal);
        
        // Then
        assertThat(result).isTrue();
        assertThat(dentista1.getAtivo()).isFalse();
        verify(dentistaRepository).findById(1L);
        verify(dentistaRepository).save(dentista1);
        verify(activityLogger).logActivity(
            "EXCLUIR_DENTISTA",
            "admin",
            "1",
            "Dentista desativado: Dr. João Silva"
        );
    }

    @Test
    @DisplayName("Deve retornar false ao tentar excluir dentista inexistente")
    void deveRetornarFalseAoTentarExcluirDentistaInexistente() {
        // Given
        when(dentistaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        boolean result = dentistaService.excluir(999L, principal);
        
        // Then
        assertThat(result).isFalse();
        verify(dentistaRepository).findById(999L);
        verify(dentistaRepository, never()).save(any());
        verify(activityLogger, never()).logActivity(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve alterar exposição pública para true e registrar atividade")
    void deveAlterarExposicaoPublicaParaTrueERegistrarAtividade() {
        // Given
        dentista2.setExpostoPublicamente(false); // Inicialmente false
        when(dentistaRepository.findById(2L)).thenReturn(Optional.of(dentista2));
        when(dentistaRepository.save(dentista2)).thenReturn(dentista2);
        
        // When
        boolean result = dentistaService.alterarExposicaoPublica(2L, true, principal);
        
        // Then
        assertThat(result).isTrue();
        assertThat(dentista2.getExpostoPublicamente()).isTrue();
        verify(dentistaRepository).findById(2L);
        verify(dentistaRepository).save(dentista2);
        verify(activityLogger).logActivity(
            "ALTERAR_EXPOSICAO_DENTISTA",
            "admin",
            "2",
            "Exposição pública alterada para ativo - Dentista: Dra. Maria Santos"
        );
    }

    @Test
    @DisplayName("Deve alterar exposição pública para false e registrar atividade")
    void deveAlterarExposicaoPublicaParaFalseERegistrarAtividade() {
        // Given
        dentista1.setExpostoPublicamente(true); // Inicialmente true
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista1));
        when(dentistaRepository.save(dentista1)).thenReturn(dentista1);
        
        // When
        boolean result = dentistaService.alterarExposicaoPublica(1L, false, principal);
        
        // Then
        assertThat(result).isTrue();
        assertThat(dentista1.getExpostoPublicamente()).isFalse();
        verify(dentistaRepository).findById(1L);
        verify(dentistaRepository).save(dentista1);
        verify(activityLogger).logActivity(
            "ALTERAR_EXPOSICAO_DENTISTA",
            "admin",
            "1",
            "Exposição pública alterada para inativo - Dentista: Dr. João Silva"
        );
    }

    @Test
    @DisplayName("Deve retornar false ao tentar alterar exposição de dentista inexistente")
    void deveRetornarFalseAoTentarAlterarExposicaoDentistaInexistente() {
        // Given
        when(dentistaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        boolean result = dentistaService.alterarExposicaoPublica(999L, true, principal);
        
        // Then
        assertThat(result).isFalse();
        verify(dentistaRepository).findById(999L);
        verify(dentistaRepository, never()).save(any());
        verify(activityLogger, never()).logActivity(anyString(), anyString(), anyString(), anyString());
    }
}
