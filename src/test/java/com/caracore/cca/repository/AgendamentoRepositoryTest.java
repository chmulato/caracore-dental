package com.caracore.cca.repository;

import com.caracore.cca.model.Agendamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AgendamentoRepositoryTest {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Test
    @DisplayName("Deve salvar e buscar um agendamento")
    void deveSalvarEBuscarAgendamento() {
        Agendamento agendamento = new Agendamento();
        agendamento.setDataHora(LocalDateTime.now());
        agendamento.setPaciente("Teste Paciente");
        agendamento.setDentista("Dr. Teste");
        agendamentoRepository.save(agendamento);

        var agendamentos = agendamentoRepository.findAll();
        assertThat(agendamentos).isNotEmpty();
        boolean found = agendamentos.stream().anyMatch(a -> "Teste Paciente".equals(a.getPaciente()));
        assertThat(found).isTrue();
    }
}
