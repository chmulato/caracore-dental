package com.caracore.cca.repository;

import com.caracore.cca.model.Dentista;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DentistaRepositoryTest {

    @Autowired
    private DentistaRepository dentistaRepository;

    @Test
    @DisplayName("Deve salvar e buscar dentista por nome")
    void testSalvarEBuscarPorNome() {
        Dentista dentista = new Dentista();
        dentista.setNome("Dr. Teste");
        dentista.setEmail("teste@caracore.com.br");
        dentista.setAtivo(true);
        dentistaRepository.save(dentista);

        List<Dentista> encontrados = dentistaRepository.findByNomeContainingIgnoreCase("teste");
        assertThat(encontrados).isNotEmpty();
        assertThat(encontrados.get(0).getNome()).containsIgnoringCase("teste");
    }

    @Test
    @DisplayName("Deve buscar apenas dentistas ativos")
    void testBuscarAtivos() {
        Dentista ativo = new Dentista();
        ativo.setNome("Ativo");
        ativo.setAtivo(true);
        dentistaRepository.save(ativo);

        Dentista inativo = new Dentista();
        inativo.setNome("Inativo");
        inativo.setAtivo(false);
        dentistaRepository.save(inativo);

        List<Dentista> ativos = dentistaRepository.findByAtivoTrue();
        assertThat(ativos).extracting(Dentista::getNome).contains("Ativo");
        assertThat(ativos).extracting(Dentista::getNome).doesNotContain("Inativo");
    }
}
