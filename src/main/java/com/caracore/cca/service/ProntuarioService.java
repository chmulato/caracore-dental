package com.caracore.cca.service;

import com.caracore.cca.dto.ImagemRadiologicaResumo;
import com.caracore.cca.model.*;
import com.caracore.cca.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gerenciamento de prontuários médicos.
 */
@Service
@Transactional
public class ProntuarioService {

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Autowired
    private ImagemRadiologicaRepository imagemRadiologicaRepository;

    @Autowired
    private RegistroTratamentoRepository registroTratamentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private DentistaRepository dentistaRepository;

    /**
     * Busca ou cria um prontuário para o paciente
     */
    public Prontuario buscarOuCriarProntuario(Long pacienteId, Long dentistaId) {
        Optional<Prontuario> prontuarioExistente = prontuarioRepository.findByPacienteId(pacienteId);
        
        if (prontuarioExistente.isPresent()) {
            return prontuarioExistente.get();
        }

        // Criar novo prontuário
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        
        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado"));

        Prontuario novoProntuario = new Prontuario(paciente, dentista);
        return prontuarioRepository.save(novoProntuario);
    }

    /**
     * Busca prontuário por ID do paciente
     */
    public Optional<Prontuario> buscarProntuarioPorPaciente(Long pacienteId) {
        return prontuarioRepository.findByPacienteId(pacienteId);
    }

    /**
     * Atualiza informações do prontuário
     */
    public Prontuario atualizarProntuario(Long prontuarioId, String historicoMedico, 
                                        String alergias, String medicamentosUso, String observacoesGerais) {
        Prontuario prontuario = prontuarioRepository.findById(prontuarioId)
                .orElseThrow(() -> new RuntimeException("Prontuário não encontrado"));

        prontuario.setHistoricoMedico(historicoMedico);
        prontuario.setAlergias(alergias);
        prontuario.setMedicamentosUso(medicamentosUso);
        prontuario.setObservacoesGerais(observacoesGerais);

        return prontuarioRepository.save(prontuario);
    }

    /**
     * Adiciona uma imagem radiológica ao prontuário
     */
    public ImagemRadiologica adicionarImagemRadiologica(Long prontuarioId, MultipartFile arquivo, 
                                                      String tipoImagem, String descricao, Long dentistaId) throws IOException {
        Prontuario prontuario = prontuarioRepository.findById(prontuarioId)
                .orElseThrow(() -> new RuntimeException("Prontuário não encontrado"));

        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado"));

        // Validar tipo de arquivo
        String contentType = arquivo.getContentType();
        if (!isImagemValida(contentType)) {
            throw new RuntimeException("Tipo de arquivo não suportado. Use apenas JPG, PNG ou JPEG.");
        }

        // Validar tamanho (máximo 10MB)
        if (arquivo.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("Arquivo muito grande. Tamanho máximo: 10MB.");
        }

        // Converter para Base64
        byte[] bytes = arquivo.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);

        // Extrair formato do arquivo
        String formato = extrairFormato(contentType);

        ImagemRadiologica imagem = new ImagemRadiologica(
                arquivo.getOriginalFilename(),
                tipoImagem,
                base64,
                formato,
                prontuario,
                dentista
        );
        imagem.setDescricao(descricao);

        ImagemRadiologica imagemSalva = imagemRadiologicaRepository.save(imagem);
        prontuario.adicionarImagemRadiologica(imagemSalva);

        return imagemSalva;
    }

    /**
     * Adiciona imagem radiológica a partir de Base64
     */
    public ImagemRadiologica adicionarImagemBase64(Long prontuarioId, String imagemBase64, 
                                                 String nomeArquivo, String tipoImagem, 
                                                 String descricao, Long dentistaId) {
        Prontuario prontuario = prontuarioRepository.findById(prontuarioId)
                .orElseThrow(() -> new RuntimeException("Prontuário não encontrado"));

        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado"));

        // Extrair formato da string Base64 se presente
        String formato = "jpeg"; // padrão
        if (imagemBase64.startsWith("data:image/")) {
            String[] parts = imagemBase64.split(";")[0].split("/");
            if (parts.length > 1) {
                formato = parts[1];
            }
            // Remove o prefixo data:image/...;base64,
            imagemBase64 = imagemBase64.substring(imagemBase64.indexOf(",") + 1);
        }

        ImagemRadiologica imagem = new ImagemRadiologica(
                nomeArquivo,
                tipoImagem,
                imagemBase64,
                formato,
                prontuario,
                dentista
        );
        imagem.setDescricao(descricao);

        ImagemRadiologica imagemSalva = imagemRadiologicaRepository.save(imagem);
        prontuario.adicionarImagemRadiologica(imagemSalva);

        return imagemSalva;
    }

    /**
     * Remove uma imagem radiológica (soft delete)
     */
    public void removerImagemRadiologica(Long imagemId) {
        ImagemRadiologica imagem = imagemRadiologicaRepository.findById(imagemId)
                .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));
        
        imagem.setAtivo(false);
        imagemRadiologicaRepository.save(imagem);
    }

    /**
     * Adiciona um registro de tratamento
     */
    public RegistroTratamento adicionarRegistroTratamento(Long prontuarioId, Long dentistaId,
                                                        String procedimento, String descricao,
                                                        String dente, String materialUtilizado,
                                                        String observacoes, Double valorProcedimento) {
        Prontuario prontuario = prontuarioRepository.findById(prontuarioId)
                .orElseThrow(() -> new RuntimeException("Prontuário não encontrado"));

        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado"));

        RegistroTratamento registro = new RegistroTratamento(prontuario, dentista, procedimento);
        registro.setDescricao(descricao);
        registro.setDente(dente);
        registro.setMaterialUtilizado(materialUtilizado);
        registro.setObservacoes(observacoes);
        registro.setValorProcedimento(valorProcedimento);

        RegistroTratamento registroSalvo = registroTratamentoRepository.save(registro);
        prontuario.adicionarRegistroTratamento(registroSalvo);

        return registroSalvo;
    }

    /**
     * Atualiza status de um tratamento
     */
    public RegistroTratamento atualizarStatusTratamento(Long registroId, RegistroTratamento.StatusTratamento novoStatus) {
        RegistroTratamento registro = registroTratamentoRepository.findById(registroId)
                .orElseThrow(() -> new RuntimeException("Registro de tratamento não encontrado"));

        registro.setStatus(novoStatus);
        return registroTratamentoRepository.save(registro);
    }

    /**
     * Busca imagens do prontuário
     */
    public List<ImagemRadiologica> buscarImagensProntuario(Long prontuarioId) {
        return imagemRadiologicaRepository.findByProntuarioIdAndAtivoTrue(prontuarioId);
    }

    /**
     * Busca imagens do prontuário sem carregar o conteúdo base64
     * Usa um DTO para evitar problemas com PostgreSQL e sobrecarga de memória
     */
    public List<ImagemRadiologicaResumo> buscarImagensProntuarioResumo(Long prontuarioId) {
        return imagemRadiologicaRepository.findResumoByProntuarioIdAndAtivoTrue(prontuarioId);
    }

    /**
     * Busca registros de tratamento do prontuário
     */
    public List<RegistroTratamento> buscarRegistrosTratamento(Long prontuarioId) {
        return registroTratamentoRepository.findByProntuarioIdOrderByDataTratamentoDesc(prontuarioId);
    }

    /**
     * Busca últimos tratamentos do paciente
     */
    public List<RegistroTratamento> buscarUltimosTratamentos(Long prontuarioId, int limite) {
        return registroTratamentoRepository.findUltimosTratamentosPorProntuario(
                prontuarioId, PageRequest.of(0, limite));
    }

    /**
     * Busca prontuários por dentista
     */
    public List<Prontuario> buscarProntuariosPorDentista(Long dentistaId) {
        return prontuarioRepository.findByDentistaIdOrderByDataUltimaAtualizacaoDesc(dentistaId);
    }
    
    /**
     * Busca todos os prontuários do sistema
     * Método para uso exclusivo de administradores
     */
    public List<Prontuario> buscarTodosProntuarios() {
        return prontuarioRepository.findAll();
    }

    /**
     * Calcula estatísticas do prontuário
     */
    public ProntuarioEstatisticas calcularEstatisticas(Long prontuarioId) {
        Long totalImagens = imagemRadiologicaRepository.countByProntuarioIdAndAtivoTrue(prontuarioId);
        Long tamanhoTotalImagens = imagemRadiologicaRepository.sumTamanhoArquivoByProntuarioIdAndAtivoTrue(prontuarioId);
        Long totalTratamentos = registroTratamentoRepository.countByProntuarioIdAndStatus(
                prontuarioId, RegistroTratamento.StatusTratamento.CONCLUIDO);
        Double valorTotalTratamentos = registroTratamentoRepository.sumValorProcedimentoByProntuarioId(prontuarioId);

        return new ProntuarioEstatisticas(totalImagens, tamanhoTotalImagens, totalTratamentos, valorTotalTratamentos);
    }

    /**
     * Busca uma imagem radiológica por ID
     */
    public ImagemRadiologica buscarImagemPorId(Long imagemId) {
        return imagemRadiologicaRepository.findById(imagemId)
                .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));
    }

    // Métodos auxiliares privados
    private boolean isImagemValida(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png")
        );
    }

    private String extrairFormato(String contentType) {
        if (contentType == null) return "jpeg";
        
        if (contentType.equals("image/jpeg") || contentType.equals("image/jpg")) {
            return "jpeg";
        } else if (contentType.equals("image/png")) {
            return "png";
        }
        return "jpeg";
    }

    /**
     * Classe para retornar estatísticas do prontuário
     */
    public static class ProntuarioEstatisticas {
        private final Long totalImagens;
        private final Long tamanhoTotalImagens;
        private final Long totalTratamentos;
        private final Double valorTotalTratamentos;

        public ProntuarioEstatisticas(Long totalImagens, Long tamanhoTotalImagens, 
                                    Long totalTratamentos, Double valorTotalTratamentos) {
            this.totalImagens = totalImagens;
            this.tamanhoTotalImagens = tamanhoTotalImagens;
            this.totalTratamentos = totalTratamentos;
            this.valorTotalTratamentos = valorTotalTratamentos;
        }

        // Getters
        public Long getTotalImagens() { return totalImagens; }
        public Long getTamanhoTotalImagens() { return tamanhoTotalImagens; }
        public Long getTotalTratamentos() { return totalTratamentos; }
        public Double getValorTotalTratamentos() { return valorTotalTratamentos; }
        
        public String getTamanhoFormatado() {
            if (tamanhoTotalImagens == null || tamanhoTotalImagens == 0) {
                return "0 KB";
            }
            
            if (tamanhoTotalImagens < 1024) {
                return tamanhoTotalImagens + " bytes";
            } else if (tamanhoTotalImagens < 1024 * 1024) {
                return String.format("%.1f KB", tamanhoTotalImagens / 1024.0);
            } else {
                return String.format("%.1f MB", tamanhoTotalImagens / (1024.0 * 1024.0));
            }
        }
    }
}
