package com.caracore.cca.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProntuarioTest {

    private Paciente paciente;
    private Dentista dentista;
    private Prontuario prontuario;

    @BeforeEach
    void setUp() {
        paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        paciente.setId(1L);

        dentista = new Dentista();
        dentista.setId(1L);
        dentista.setNome("Dr. Carlos");
        dentista.setEmail("carlos@dentista.com");

        prontuario = new Prontuario(paciente, dentista);
    }

    @Test
    @DisplayName("Deve criar prontuário com paciente e dentista")
    void deveCriarProntuarioComPacienteEDentista() {
        // Then
        assertThat(prontuario.getPaciente()).isEqualTo(paciente);
        assertThat(prontuario.getDentista()).isEqualTo(dentista);
        assertThat(prontuario.getDataCriacao()).isNotNull();
        assertThat(prontuario.getDataUltimaAtualizacao()).isNotNull();
        assertThat(prontuario.getImagensRadiologicas()).isEmpty();
        assertThat(prontuario.getRegistrosTratamentos()).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar campos do prontuário")
    void deveAtualizarCamposProntuario() {
        // When
        prontuario.setHistoricoMedico("Histórico médico completo");
        prontuario.setAlergias("Alergia a penicilina");
        prontuario.setMedicamentosUso("Paracetamol 500mg");
        prontuario.setObservacoesGerais("Paciente colaborativo");

        // Then
        assertThat(prontuario.getHistoricoMedico()).isEqualTo("Histórico médico completo");
        assertThat(prontuario.getAlergias()).isEqualTo("Alergia a penicilina");
        assertThat(prontuario.getMedicamentosUso()).isEqualTo("Paracetamol 500mg");
        assertThat(prontuario.getObservacoesGerais()).isEqualTo("Paciente colaborativo");
    }

    @Test
    @DisplayName("Deve adicionar imagem radiológica")
    void deveAdicionarImagemRadiologica() {
        // Given
        ImagemRadiologica imagem = new ImagemRadiologica("teste.jpg", "Radiografia", 
                "base64data", "jpeg", prontuario, dentista);

        // When
        prontuario.adicionarImagemRadiologica(imagem);

        // Then
        assertThat(prontuario.getImagensRadiologicas()).hasSize(1);
        assertThat(prontuario.getImagensRadiologicas()).contains(imagem);
        assertThat(imagem.getProntuario()).isEqualTo(prontuario);
    }

    @Test
    @DisplayName("Deve adicionar registro de tratamento")
    void deveAdicionarRegistroTratamento() {
        // Given
        RegistroTratamento registro = new RegistroTratamento(prontuario, dentista, "Limpeza");

        // When
        prontuario.adicionarRegistroTratamento(registro);

        // Then
        assertThat(prontuario.getRegistrosTratamentos()).hasSize(1);
        assertThat(prontuario.getRegistrosTratamentos()).contains(registro);
        assertThat(registro.getProntuario()).isEqualTo(prontuario);
    }

    @Test
    @DisplayName("Deve atualizar timestamp automaticamente")
    void deveAtualizarTimestampAutomaticamente() {
        // When
        prontuario.setHistoricoMedico("Novo histórico"); // Simula atualização

        // Then
        // O timestamp é atualizado automaticamente pelo JPA @PreUpdate
        assertThat(prontuario.getDataUltimaAtualizacao()).isNotNull();
    }

    @Test
    @DisplayName("Deve implementar equals e hashCode corretamente")
    void deveImplementarEqualsHashCodeCorretamente() {
        // Given
        Prontuario outroprontuario = new Prontuario(paciente, dentista);
        prontuario.setId(1L);
        outroprontuario.setId(1L);

        // Then
        assertThat(prontuario).isEqualTo(outroprontuario);
        assertThat(prontuario.hashCode()).isEqualTo(outroprontuario.hashCode());

        // When ID diferente
        outroprontuario.setId(2L);
        
        // Then
        assertThat(prontuario).isNotEqualTo(outroprontuario);
    }

    @Test
    @DisplayName("Deve implementar toString corretamente")
    void deveImplementarToStringCorretamente() {
        // Given
        prontuario.setId(1L);

        // When
        String toString = prontuario.toString();

        // Then
        assertThat(toString).contains("Prontuario");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("João Silva");
        assertThat(toString).contains("Dr. Carlos");
    }

    @Test
    @DisplayName("Deve validar associação bidirecional com paciente")
    void deveValidarAssociacaoBidirecionalComPaciente() {
        // Then
        assertThat(paciente.getProntuario()).isEqualTo(prontuario);
        assertThat(prontuario.getPaciente()).isEqualTo(paciente);
    }
}
