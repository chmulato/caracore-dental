package com.caracore.cca.service;

import com.caracore.cca.model.*;
import com.caracore.cca.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProntuarioServiceTest {

    @Mock
    private ProntuarioRepository prontuarioRepository;

    @Mock
    private ImagemRadiologicaRepository imagemRadiologicaRepository;

    @Mock
    private RegistroTratamentoRepository registroTratamentoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private DentistaRepository dentistaRepository;

    @Mock
    private MultipartFile arquivo;

    @InjectMocks
    private ProntuarioService prontuarioService;

    private Paciente paciente;
    private Dentista dentista;
    private Prontuario prontuario;
    private ImagemRadiologica imagem;
    private RegistroTratamento registro;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup dados de teste
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
        imagem.setId(1L);
        
        registro = new RegistroTratamento(prontuario, dentista, "Limpeza dental");
        registro.setId(1L);
    }

    @Test
    @DisplayName("Deve buscar prontuário existente por paciente")
    void deveBuscarProntuarioExistentePorPaciente() {
        // Given
        when(prontuarioRepository.findByPacienteId(1L)).thenReturn(Optional.of(prontuario));
        
        // When
        Prontuario resultado = prontuarioService.buscarOuCriarProntuario(1L, 1L);
        
        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPaciente().getId()).isEqualTo(1L);
        
        verify(prontuarioRepository).findByPacienteId(1L);
        verify(prontuarioRepository, never()).save(any(Prontuario.class));
    }

    @Test
    @DisplayName("Deve criar novo prontuário quando não existe")
    void deveCriarNovoProntuarioQuandoNaoExiste() {
        // Given
        when(prontuarioRepository.findByPacienteId(1L)).thenReturn(Optional.empty());
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista));
        when(prontuarioRepository.save(any(Prontuario.class))).thenReturn(prontuario);
        
        // When
        Prontuario resultado = prontuarioService.buscarOuCriarProntuario(1L, 1L);
        
        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPaciente()).isEqualTo(paciente);
        assertThat(resultado.getDentista()).isEqualTo(dentista);
        
        verify(prontuarioRepository).save(any(Prontuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente não encontrado")
    void deveLancarExcecaoQuandoPacienteNaoEncontrado() {
        // Given
        when(prontuarioRepository.findByPacienteId(1L)).thenReturn(Optional.empty());
        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> prontuarioService.buscarOuCriarProntuario(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Paciente não encontrado");
    }

    @Test
    @DisplayName("Deve buscar prontuário por paciente")
    void deveBuscarProntuarioPorPaciente() {
        // Given
        when(prontuarioRepository.findByPacienteId(1L)).thenReturn(Optional.of(prontuario));
        
        // When
        Optional<Prontuario> resultado = prontuarioService.buscarProntuarioPorPaciente(1L);
        
        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve atualizar informações do prontuário")
    void deveAtualizarInformacoesProntuario() {
        // Given
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario));
        when(prontuarioRepository.save(any(Prontuario.class))).thenReturn(prontuario);
        
        // When
        Prontuario resultado = prontuarioService.atualizarProntuario(1L, 
                "Histórico médico", "Alergia a penicilina", "Paracetamol", "Observações");
        
        // Then
        assertThat(resultado).isNotNull();
        verify(prontuarioRepository).save(prontuario);
    }

    @Test
    @DisplayName("Deve adicionar imagem radiológica com arquivo válido")
    void deveAdicionarImagemRadiologicaComArquivoValido() throws IOException {
        // Given
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario));
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista));
        when(arquivo.getContentType()).thenReturn("image/jpeg");
        when(arquivo.getSize()).thenReturn(1024L * 1024L); // 1MB
        when(arquivo.getBytes()).thenReturn("fake image data".getBytes());
        when(arquivo.getOriginalFilename()).thenReturn("teste.jpg");
        when(imagemRadiologicaRepository.save(any(ImagemRadiologica.class))).thenReturn(imagem);
        
        // When
        ImagemRadiologica resultado = prontuarioService.adicionarImagemRadiologica(
                1L, arquivo, "Radiografia Panorâmica", "Descrição teste", 1L);
        
        // Then
        assertThat(resultado).isNotNull();
        verify(imagemRadiologicaRepository).save(any(ImagemRadiologica.class));
    }

    @Test
    @DisplayName("Deve rejeitar arquivo com tipo inválido")
    void deveRejeitarArquivoComTipoInvalido() throws IOException {
        // Given
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario));
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista));
        when(arquivo.getContentType()).thenReturn("application/pdf");
        
        // When & Then
        assertThatThrownBy(() -> prontuarioService.adicionarImagemRadiologica(
                1L, arquivo, "Radiografia", "Descrição", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Tipo de arquivo não suportado. Use apenas JPG, PNG ou JPEG.");
    }

    @Test
    @DisplayName("Deve rejeitar arquivo muito grande")
    void deveRejeitarArquivoMuitoGrande() throws IOException {
        // Given
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario));
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista));
        when(arquivo.getContentType()).thenReturn("image/jpeg");
        when(arquivo.getSize()).thenReturn(15L * 1024L * 1024L); // 15MB
        
        // When & Then
        assertThatThrownBy(() -> prontuarioService.adicionarImagemRadiologica(
                1L, arquivo, "Radiografia", "Descrição", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Arquivo muito grande. Tamanho máximo: 10MB.");
    }

    @Test
    @DisplayName("Deve adicionar imagem Base64")
    void deveAdicionarImagemBase64() {
        // Given
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario));
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista));
        when(imagemRadiologicaRepository.save(any(ImagemRadiologica.class))).thenReturn(imagem);
        
        String imagemBase64 = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQ...";
        
        // When
        ImagemRadiologica resultado = prontuarioService.adicionarImagemBase64(
                1L, imagemBase64, "teste.jpg", "Radiografia", "Descrição", 1L);
        
        // Then
        assertThat(resultado).isNotNull();
        verify(imagemRadiologicaRepository).save(any(ImagemRadiologica.class));
    }

    @Test
    @DisplayName("Deve remover imagem radiológica (soft delete)")
    void deveRemoverImagemRadiologica() {
        // Given
        when(imagemRadiologicaRepository.findById(1L)).thenReturn(Optional.of(imagem));
        when(imagemRadiologicaRepository.save(any(ImagemRadiologica.class))).thenReturn(imagem);
        
        // When
        prontuarioService.removerImagemRadiologica(1L);
        
        // Then
        verify(imagemRadiologicaRepository).save(imagem);
        assertThat(imagem.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve adicionar registro de tratamento")
    void deveAdicionarRegistroTratamento() {
        // Given
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario));
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista));
        when(registroTratamentoRepository.save(any(RegistroTratamento.class))).thenReturn(registro);
        
        // When
        RegistroTratamento resultado = prontuarioService.adicionarRegistroTratamento(
                1L, 1L, "Limpeza", "Descrição", "12", "Material", "Obs", 150.0);
        
        // Then
        assertThat(resultado).isNotNull();
        verify(registroTratamentoRepository).save(any(RegistroTratamento.class));
    }

    @Test
    @DisplayName("Deve atualizar status do tratamento")
    void deveAtualizarStatusTratamento() {
        // Given
        when(registroTratamentoRepository.findById(1L)).thenReturn(Optional.of(registro));
        when(registroTratamentoRepository.save(any(RegistroTratamento.class))).thenReturn(registro);
        
        // When
        RegistroTratamento resultado = prontuarioService.atualizarStatusTratamento(
                1L, RegistroTratamento.StatusTratamento.CONCLUIDO);
        
        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatus()).isEqualTo(RegistroTratamento.StatusTratamento.CONCLUIDO);
        verify(registroTratamentoRepository).save(registro);
    }

    @Test
    @DisplayName("Deve buscar imagens do prontuário")
    void deveBuscarImagensProntuario() {
        // Given
        List<ImagemRadiologica> imagens = Arrays.asList(imagem);
        when(imagemRadiologicaRepository.findByProntuarioIdAndAtivoTrue(1L)).thenReturn(imagens);
        
        // When
        List<ImagemRadiologica> resultado = prontuarioService.buscarImagensProntuario(1L);
        
        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(imagem);
    }

    @Test
    @DisplayName("Deve buscar registros de tratamento")
    void deveBuscarRegistrosTratamento() {
        // Given
        List<RegistroTratamento> registros = Arrays.asList(registro);
        when(registroTratamentoRepository.findByProntuarioIdOrderByDataTratamentoDesc(1L))
                .thenReturn(registros);
        
        // When
        List<RegistroTratamento> resultado = prontuarioService.buscarRegistrosTratamento(1L);
        
        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(registro);
    }

    @Test
    @DisplayName("Deve buscar últimos tratamentos")
    void deveBuscarUltimosTratamentos() {
        // Given
        List<RegistroTratamento> registros = Arrays.asList(registro);
        when(registroTratamentoRepository.findUltimosTratamentosPorProntuario(eq(1L), any(PageRequest.class)))
                .thenReturn(registros);
        
        // When
        List<RegistroTratamento> resultado = prontuarioService.buscarUltimosTratamentos(1L, 5);
        
        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
        verify(registroTratamentoRepository).findUltimosTratamentosPorProntuario(eq(1L), any(PageRequest.class));
    }

    @Test
    @DisplayName("Deve buscar prontuários por dentista")
    void deveBuscarProntuariosPorDentista() {
        // Given
        List<Prontuario> prontuarios = Arrays.asList(prontuario);
        when(prontuarioRepository.findByDentistaIdOrderByDataUltimaAtualizacaoDesc(1L))
                .thenReturn(prontuarios);
        
        // When
        List<Prontuario> resultado = prontuarioService.buscarProntuariosPorDentista(1L);
        
        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(prontuario);
    }

    @Test
    @DisplayName("Deve calcular estatísticas do prontuário")
    void deveCalcularEstatisticasProntuario() {
        // Given
        when(imagemRadiologicaRepository.countByProntuarioIdAndAtivoTrue(1L)).thenReturn(5L);
        when(imagemRadiologicaRepository.sumTamanhoArquivoByProntuarioIdAndAtivoTrue(1L)).thenReturn(2048L);
        when(registroTratamentoRepository.countByProntuarioIdAndStatus(1L, 
                RegistroTratamento.StatusTratamento.CONCLUIDO)).thenReturn(3L);
        when(registroTratamentoRepository.sumValorProcedimentoByProntuarioId(1L)).thenReturn(450.0);
        
        // When
        ProntuarioService.ProntuarioEstatisticas resultado = 
                prontuarioService.calcularEstatisticas(1L);
        
        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalImagens()).isEqualTo(5L);
        assertThat(resultado.getTamanhoTotalImagens()).isEqualTo(2048L);
        assertThat(resultado.getTotalTratamentos()).isEqualTo(3L);
        assertThat(resultado.getValorTotalTratamentos()).isEqualTo(450.0);
        assertThat(resultado.getTamanhoFormatado()).isEqualTo("2,0 KB");
    }

    @Test
    @DisplayName("Deve buscar imagem por ID")
    void deveBuscarImagemPorId() {
        // Given
        when(imagemRadiologicaRepository.findById(1L)).thenReturn(Optional.of(imagem));
        
        // When
        ImagemRadiologica resultado = prontuarioService.buscarImagemPorId(1L);
        
        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando imagem não encontrada")
    void deveLancarExcecaoQuandoImagemNaoEncontrada() {
        // Given
        when(imagemRadiologicaRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> prontuarioService.buscarImagemPorId(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Imagem não encontrada");
    }

    @Test
    @DisplayName("Deve formatar tamanho corretamente nas estatísticas")
    void deveFormatarTamanhoCorretamenteNasEstatisticas() {
        // Teste para bytes
        ProntuarioService.ProntuarioEstatisticas estatisticas1 = 
                new ProntuarioService.ProntuarioEstatisticas(1L, 512L, 1L, 100.0);
        assertThat(estatisticas1.getTamanhoFormatado()).isEqualTo("512 bytes");
        
        // Teste para KB
        ProntuarioService.ProntuarioEstatisticas estatisticas2 = 
                new ProntuarioService.ProntuarioEstatisticas(1L, 1536L, 1L, 100.0);
        assertThat(estatisticas2.getTamanhoFormatado()).isEqualTo("1,5 KB");
        
        // Teste para MB
        ProntuarioService.ProntuarioEstatisticas estatisticas3 = 
                new ProntuarioService.ProntuarioEstatisticas(1L, 2097152L, 1L, 100.0);
        assertThat(estatisticas3.getTamanhoFormatado()).isEqualTo("2,0 MB");
        
        // Teste para zero
        ProntuarioService.ProntuarioEstatisticas estatisticas4 = 
                new ProntuarioService.ProntuarioEstatisticas(1L, 0L, 1L, 100.0);
        assertThat(estatisticas4.getTamanhoFormatado()).isEqualTo("0 KB");
    }

    @Test
    @DisplayName("Deve tratar valores nulos nas estatísticas")
    void deveTratarValoresNulosNasEstatisticas() {
        // Given
        when(imagemRadiologicaRepository.countByProntuarioIdAndAtivoTrue(1L)).thenReturn(0L);
        when(imagemRadiologicaRepository.sumTamanhoArquivoByProntuarioIdAndAtivoTrue(1L)).thenReturn(null);
        when(registroTratamentoRepository.countByProntuarioIdAndStatus(1L, 
                RegistroTratamento.StatusTratamento.CONCLUIDO)).thenReturn(0L);
        when(registroTratamentoRepository.sumValorProcedimentoByProntuarioId(1L)).thenReturn(null);
        
        // When
        ProntuarioService.ProntuarioEstatisticas resultado = 
                prontuarioService.calcularEstatisticas(1L);
        
        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalImagens()).isEqualTo(0L);
        assertThat(resultado.getTamanhoTotalImagens()).isNull();
        assertThat(resultado.getTotalTratamentos()).isEqualTo(0L);
        assertThat(resultado.getValorTotalTratamentos()).isNull();
        assertThat(resultado.getTamanhoFormatado()).isEqualTo("0 KB");
    }

    @Test
    @DisplayName("Deve retornar tipos de imagem do banco quando existem dados")
    void deveRetornarTiposImagemDoBanco() {
        // Given
        List<String> tiposExistentes = Arrays.asList(
            "Radiografia Panorâmica", 
            "Radiografia Periapical", 
            "Tomografia"
        );
        when(imagemRadiologicaRepository.findDistinctTiposImagem()).thenReturn(tiposExistentes);

        // When
        List<String> resultado = prontuarioService.buscarTiposImagem();

        // Then
        assertThat(resultado).isEqualTo(tiposExistentes);
        assertThat(resultado).hasSize(3);
        verify(imagemRadiologicaRepository).findDistinctTiposImagem();
    }

    @Test
    @DisplayName("Deve retornar tipos padrão quando não há dados no banco")
    void deveRetornarTiposPadraoQuandoNaoHaDados() {
        // Given
        when(imagemRadiologicaRepository.findDistinctTiposImagem()).thenReturn(Arrays.asList());

        // When
        List<String> resultado = prontuarioService.buscarTiposImagem();

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).contains(
            "Radiografia Panorâmica",
            "Radiografia Periapical", 
            "Radiografia Bitewing",
            "Tomografia"
        );
        assertThat(resultado).hasSize(9);
        verify(imagemRadiologicaRepository).findDistinctTiposImagem();
    }
}
