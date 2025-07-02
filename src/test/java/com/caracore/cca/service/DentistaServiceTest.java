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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve listar todos os dentistas")
    void testListarTodos() {
        Dentista d1 = new Dentista();
        d1.setNome("Dentista 1");
        Dentista d2 = new Dentista();
        d2.setNome("Dentista 2");
        when(dentistaRepository.findAll()).thenReturn(Arrays.asList(d1, d2));
        List<Dentista> result = dentistaService.listarTodos();
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Deve buscar dentista por ID")
    void testBuscarPorId() {
        Dentista d = new Dentista();
        d.setId(1L);
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(d));
        Optional<Dentista> result = dentistaService.buscarPorId(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve salvar dentista e registrar atividade")
    void testSalvar() {
        Dentista d = new Dentista();
        d.setNome("Novo Dentista");
        // Simula que o dentista salvo recebe um ID do banco
        Dentista dentistaComId = new Dentista();
        dentistaComId.setId(10L);
        dentistaComId.setNome("Novo Dentista");
        when(dentistaRepository.save(any(Dentista.class))).thenReturn(dentistaComId);
        Dentista salvo = dentistaService.salvar(d, principal);
        assertThat(salvo.getNome()).isEqualTo("Novo Dentista");
        assertThat(salvo.getId()).isEqualTo(10L);
        verify(activityLogger, atLeastOnce()).logActivity(any(), any(), any(), any());
    }
}
