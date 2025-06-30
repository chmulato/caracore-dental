package com.caracore.cca.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgendamentoFormTest {

    @Test
    @DisplayName("Deve criar e acessar os campos do AgendamentoForm corretamente")
    void deveCriarEManipularAgendamentoForm() {
        AgendamentoForm form = new AgendamentoForm();
        form.setPaciente("Maria Teste");
        form.setDentista("Dr. Dentista");
        form.setDataHora(java.time.LocalDateTime.of(2025, 7, 1, 10, 0));

        assertThat(form.getPaciente()).isEqualTo("Maria Teste");
        assertThat(form.getDentista()).isEqualTo("Dr. Dentista");
        assertThat(form.getDataHora()).isEqualTo(java.time.LocalDateTime.of(2025, 7, 1, 10, 0));
    }
}
