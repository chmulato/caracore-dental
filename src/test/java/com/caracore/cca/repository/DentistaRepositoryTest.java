package com.caracore.cca.repository;

import com.caracore.cca.model.Dentista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DentistaRepositoryTest {

    @Autowired
    private DentistaRepository dentistaRepository;

    private Dentista dentistaAtivo;
    private Dentista dentistaAtivoExposto;
    private Dentista dentistaInativo;

    @BeforeEach
    void setUp() {
        dentistaRepository.deleteAll();
        
        dentistaAtivo = new Dentista();
        dentistaAtivo.setNome("Dr. João Silva");
        dentistaAtivo.setEmail("joao@teste.com");
        dentistaAtivo.setEspecialidade("Ortodontia");
        dentistaAtivo.setAtivo(true);
        dentistaAtivo.setExpostoPublicamente(false);
        dentistaRepository.save(dentistaAtivo);

        dentistaAtivoExposto = new Dentista();
        dentistaAtivoExposto.setNome("Dra. Maria Santos");
        dentistaAtivoExposto.setEmail("maria@teste.com");
        dentistaAtivoExposto.setEspecialidade("Implantodontia");
        dentistaAtivoExposto.setAtivo(true);
        dentistaAtivoExposto.setExpostoPublicamente(true);
        dentistaRepository.save(dentistaAtivoExposto);

        dentistaInativo = new Dentista();
        dentistaInativo.setNome("Dr. Pedro Costa");
        dentistaInativo.setEmail("pedro@teste.com");
        dentistaInativo.setEspecialidade("Endodontia");
        dentistaInativo.setAtivo(false);
        dentistaInativo.setExpostoPublicamente(false);
        dentistaRepository.save(dentistaInativo);
    }

    @Test
    @DisplayName("Deve salvar e buscar dentista por nome")
    void deveSalvarEBuscarDentistaPorNome() {
        // When
        List<Dentista> encontrados = dentistaRepository.findByNomeContainingIgnoreCase("silva");
        
        // Then
        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getNome()).containsIgnoringCase("silva");
        assertThat(encontrados.get(0).getEmail()).isEqualTo("joao@teste.com");
    }

    @Test
    @DisplayName("Deve buscar dentista por nome case insensitive")
    void deveBuscarDentistaPorNomeCaseInsensitive() {
        // When
        List<Dentista> encontradosMinusculo = dentistaRepository.findByNomeContainingIgnoreCase("maria");
        List<Dentista> encontradosMaiusculo = dentistaRepository.findByNomeContainingIgnoreCase("MARIA");
        List<Dentista> encontradosMisto = dentistaRepository.findByNomeContainingIgnoreCase("MaRiA");
        
        // Then
        assertThat(encontradosMinusculo).hasSize(1);
        assertThat(encontradosMaiusculo).hasSize(1);
        assertThat(encontradosMisto).hasSize(1);
        assertThat(encontradosMinusculo.get(0)).isEqualTo(encontradosMaiusculo.get(0));
    }

    @Test
    @DisplayName("Deve buscar apenas dentistas ativos")
    void deveBuscarApenasDentistasAtivos() {
        // When
        List<Dentista> ativos = dentistaRepository.findByAtivoTrue();
        
        // Then
        assertThat(ativos).hasSize(2);
        assertThat(ativos).extracting(Dentista::getNome)
            .contains("Dr. João Silva", "Dra. Maria Santos")
            .doesNotContain("Dr. Pedro Costa");
        assertThat(ativos).allMatch(Dentista::getAtivo);
    }

    @Test
    @DisplayName("Deve buscar dentistas por status ativo")
    void deveBuscarDentistasPorStatusAtivo() {
        // When
        List<Dentista> ativos = dentistaRepository.findByAtivo(true);
        List<Dentista> inativos = dentistaRepository.findByAtivo(false);
        
        // Then
        assertThat(ativos).hasSize(2);
        assertThat(inativos).hasSize(1);
        assertThat(inativos.get(0).getNome()).isEqualTo("Dr. Pedro Costa");
    }

    @Test
    @DisplayName("Deve buscar apenas dentistas ativos e expostos publicamente")
    void deveBuscarDentistasAtivosEExpostosPublicamente() {
        // When
        List<Dentista> ativosExpostos = dentistaRepository.findByAtivoTrueAndExpostoPublicamenteTrue();
        
        // Then
        assertThat(ativosExpostos).hasSize(1);
        assertThat(ativosExpostos.get(0).getNome()).isEqualTo("Dra. Maria Santos");
        assertThat(ativosExpostos.get(0).getAtivo()).isTrue();
        assertThat(ativosExpostos.get(0).getExpostoPublicamente()).isTrue();
    }

    @Test
    @DisplayName("Deve buscar dentista por email")
    void deveBuscarDentistaPorEmail() {
        // When
        Optional<Dentista> encontrado = dentistaRepository.findByEmail("maria@teste.com");
        
        // Then
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Dra. Maria Santos");
        assertThat(encontrado.get().getEmail()).isEqualTo("maria@teste.com");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para email inexistente")
    void deveRetornarOptionalVazioParaEmailInexistente() {
        // When
        Optional<Dentista> encontrado = dentistaRepository.findByEmail("inexistente@teste.com");
        
        // Then
        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar dentistas por termo no nome")
    void deveBuscarDentistasPorTermoNoNome() {
        // When
        List<Dentista> encontrados = dentistaRepository.buscarPorTermo("Silva");
        
        // Then
        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getNome()).contains("Silva");
    }

    @Test
    @DisplayName("Deve buscar dentistas por termo na especialidade")
    void deveBuscarDentistasPorTermoNaEspecialidade() {
        // When
        List<Dentista> encontrados = dentistaRepository.buscarPorTermo("Ortodontia");
        
        // Then
        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getEspecialidade()).isEqualTo("Ortodontia");
        assertThat(encontrados.get(0).getNome()).isEqualTo("Dr. João Silva");
    }

    @Test
    @DisplayName("Deve buscar dentistas por termo no email")
    void deveBuscarDentistasPorTermoNoEmail() {
        // When
        List<Dentista> encontrados = dentistaRepository.buscarPorTermo("maria");
        
        // Then
        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getEmail()).contains("maria");
        assertThat(encontrados.get(0).getNome()).isEqualTo("Dra. Maria Santos");
    }

    @Test
    @DisplayName("Deve buscar dentistas por termo case insensitive")
    void deveBuscarDentistasPorTermoCaseInsensitive() {
        // When
        List<Dentista> encontradosMinusculo = dentistaRepository.buscarPorTermo("implantodontia");
        List<Dentista> encontradosMaiusculo = dentistaRepository.buscarPorTermo("IMPLANTODONTIA");
        List<Dentista> encontradosMisto = dentistaRepository.buscarPorTermo("ImPlAnToDonTiA");
        
        // Then
        assertThat(encontradosMinusculo).hasSize(1);
        assertThat(encontradosMaiusculo).hasSize(1);
        assertThat(encontradosMisto).hasSize(1);
        assertThat(encontradosMinusculo.get(0)).isEqualTo(encontradosMaiusculo.get(0));
        assertThat(encontradosMinusculo.get(0).getEspecialidade()).isEqualTo("Implantodontia");
    }

    @Test
    @DisplayName("Deve buscar dentistas incluindo inativos quando usar buscarPorTermo")
    void deveBuscarDentistasIncluindoInativosQuandoUsarBuscarPorTermo() {
        // When
        List<Dentista> encontrados = dentistaRepository.buscarPorTermo("Pedro");
        
        // Then
        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getNome()).contains("Pedro");
        assertThat(encontrados.get(0).getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando termo não encontrado")
    void deveRetornarListaVaziaQuandoTermoNaoEncontrado() {
        // When
        List<Dentista> encontrados = dentistaRepository.buscarPorTermo("termoInexistente");
        
        // Then
        assertThat(encontrados).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar dentistas por múltiplos critérios simultaneamente")
    void deveBuscarDentistasPorMultiplosCriteriosSimultaneamente() {
        // When - buscar por termo que aparece em nome 
        List<Dentista> encontrados = dentistaRepository.buscarPorTermo("Dr");
        
        // Then - Deve encontrar todos os 3 dentistas (Dr. João Silva, Dra. Maria Santos e Dr. Pedro Costa)
        assertThat(encontrados).hasSize(3);
        assertThat(encontrados).extracting(Dentista::getNome)
            .allMatch(nome -> nome.contains("Dr"));
    }
}
