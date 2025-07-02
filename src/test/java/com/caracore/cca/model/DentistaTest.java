package com.caracore.cca.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DentistaTest {

    @Test
    @DisplayName("Deve criar e manipular um dentista corretamente")
    void testCriarDentista() {
        Dentista dentista = new Dentista();
        dentista.setId(1L);
        dentista.setNome("Dr. Teste");
        dentista.setEspecialidade("Ortodontia");
        dentista.setEmail("teste@caracore.com.br");
        dentista.setTelefone("(11) 99999-9999");
        dentista.setCro("SP-12345");
        dentista.setHorarioInicio("08:00");
        dentista.setHorarioFim("18:00");
        dentista.setAtivo(true);

        assertThat(dentista.getId()).isEqualTo(1L);
        assertThat(dentista.getNome()).isEqualTo("Dr. Teste");
        assertThat(dentista.getEspecialidade()).isEqualTo("Ortodontia");
        assertThat(dentista.getEmail()).isEqualTo("teste@caracore.com.br");
        assertThat(dentista.getTelefone()).isEqualTo("(11) 99999-9999");
        assertThat(dentista.getCro()).isEqualTo("SP-12345");
        assertThat(dentista.getHorarioInicio()).isEqualTo("08:00");
        assertThat(dentista.getHorarioFim()).isEqualTo("18:00");
        assertThat(dentista.getAtivo()).isTrue();
    }
}
