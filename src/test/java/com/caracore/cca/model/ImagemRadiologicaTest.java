package com.caracore.cca.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImagemRadiologicaTest {

    private Paciente paciente;
    private Dentista dentista;
    private Prontuario prontuario;
    private ImagemRadiologica imagem;

    @BeforeEach
    void setUp() {
        paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        paciente.setId(1L);

        dentista = new Dentista();
        dentista.setId(1L);
        dentista.setNome("Dr. Carlos");
        dentista.setEmail("carlos@dentista.com");

        prontuario = new Prontuario(paciente, dentista);
        prontuario.setId(1L);

        imagem = new ImagemRadiologica("radiografia.jpg", "Radiografia Panorâmica",
                "base64data", "jpeg", prontuario, dentista);
    }

    @Test
    @DisplayName("Deve criar imagem radiológica com dados corretos")
    void deveCriarImagemRadiologicaComDadosCorretos() {
        // Then
        assertThat(imagem.getNomeArquivo()).isEqualTo("radiografia.jpg");
        assertThat(imagem.getTipoImagem()).isEqualTo("Radiografia Panorâmica");
        assertThat(imagem.getImagemBase64()).isEqualTo("base64data");
        assertThat(imagem.getFormatoArquivo()).isEqualTo("jpeg");
        assertThat(imagem.getProntuario()).isEqualTo(prontuario);
        assertThat(imagem.getDentista()).isEqualTo(dentista);
        assertThat(imagem.getDataUpload()).isNotNull();
        assertThat(imagem.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve calcular tamanho do arquivo corretamente")
    void deveCalcularTamanhoArquivoCorretamente() {
        // Given
        String base64 = "SGVsbG8gV29ybGQ="; // "Hello World" em base64 (16 chars = 12 bytes decodificados)
        imagem.setImagemBase64(base64);

        // When
        Long tamanho = imagem.getTamanhoArquivo();

        // Then - Tamanho calculado automaticamente: 16 chars * 3/4 = 12 bytes
        assertThat(tamanho).isEqualTo(12L);
    }

    @Test
    @DisplayName("Deve gerar data URL corretamente")
    void deveGerarDataUrlCorretamente() {
        // When
        String dataUrl = imagem.getDataUrl();

        // Then
        assertThat(dataUrl).startsWith("data:image/jpeg;base64,");
        assertThat(dataUrl).contains("base64data");
    }

    @Test
    @DisplayName("Deve formatar tamanho para display")
    void deveFormatarTamanhoParaDisplay() {
        // Given - 1536 chars base64 = 1152 bytes decodificados = 1.1 KB
        imagem.setImagemBase64("a".repeat(1536));

        // When
        String tamanhoFormatado = imagem.getTamanhoFormatado();

        // Then
        assertThat(tamanhoFormatado).isEqualTo("1,1 KB");
    }

    @Test
    @DisplayName("Deve formatar tamanho em MB")
    void deveFormatarTamanhoEmMB() {
        // Given - 2097152 chars base64 = 1572864 bytes decodificados = 1.5 MB
        imagem.setImagemBase64("a".repeat(2097152));

        // When
        String tamanhoFormatado = imagem.getTamanhoFormatado();

        // Then
        assertThat(tamanhoFormatado).isEqualTo("1,5 MB");
    }

    @Test
    @DisplayName("Deve formatar tamanho em bytes")
    void deveFormatarTamanhoEmBytes() {
        // Given - 512 chars base64 = 384 bytes decodificados
        imagem.setImagemBase64("a".repeat(512));

        // When
        String tamanhoFormatado = imagem.getTamanhoFormatado();

        // Then
        assertThat(tamanhoFormatado).isEqualTo("384 bytes");
    }

    @Test
    @DisplayName("Deve retornar zero KB para imagem vazia")
    void deveRetornarZeroKBParaImagemVazia() {
        // Given
        imagem.setImagemBase64("");

        // When
        String tamanhoFormatado = imagem.getTamanhoFormatado();

        // Then
        assertThat(tamanhoFormatado).isEqualTo("0 KB");
    }

    @Test
    @DisplayName("Deve retornar zero KB para imagem nula")
    void deveRetornarZeroKBParaImagemNula() {
        // Given
        imagem.setImagemBase64(null);

        // When
        String tamanhoFormatado = imagem.getTamanhoFormatado();

        // Then
        assertThat(tamanhoFormatado).isEqualTo("0 KB");
    }

    @Test
    @DisplayName("Deve definir e obter descrição")
    void deveDefinirEObterDescricao() {
        // When
        imagem.setDescricao("Radiografia pré-operatória");

        // Then
        assertThat(imagem.getDescricao()).isEqualTo("Radiografia pré-operatória");
    }

    @Test
    @DisplayName("Deve marcar como inativo")
    void deveMarcarComoInativo() {
        // When
        imagem.setAtivo(false);

        // Then
        assertThat(imagem.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve implementar equals e hashCode corretamente")
    void deveImplementarEqualsHashCodeCorretamente() {
        // Given
        ImagemRadiologica outraImagem = new ImagemRadiologica("radiografia.jpg", 
                "Radiografia Panorâmica", "base64data", "jpeg", prontuario, dentista);
        imagem.setId(1L);
        outraImagem.setId(1L);

        // Then
        assertThat(imagem).isEqualTo(outraImagem);
        assertThat(imagem.hashCode()).isEqualTo(outraImagem.hashCode());

        // When ID diferente
        outraImagem.setId(2L);
        
        // Then
        assertThat(imagem).isNotEqualTo(outraImagem);
    }

    @Test
    @DisplayName("Deve implementar toString corretamente")
    void deveImplementarToStringCorretamente() {
        // Given
        imagem.setId(1L);

        // When
        String toString = imagem.toString();

        // Then
        assertThat(toString).contains("ImagemRadiologica");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("radiografia.jpg");
        assertThat(toString).contains("Radiografia Panorâmica");
    }

    @Test
    @DisplayName("Deve gerar data URL com diferentes formatos")
    void deveGerarDataUrlComDiferentesFormatos() {
        // Test PNG
        imagem.setFormatoArquivo("png");
        assertThat(imagem.getDataUrl()).startsWith("data:image/png;base64,");

        // Test JPEG
        imagem.setFormatoArquivo("jpeg");
        assertThat(imagem.getDataUrl()).startsWith("data:image/jpeg;base64,");

        // Test JPG
        imagem.setFormatoArquivo("jpg");
        assertThat(imagem.getDataUrl()).startsWith("data:image/jpg;base64,");
    }
}
