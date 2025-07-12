package com.caracore.cca.dto;

import java.time.LocalDateTime;

/**
 * DTO para transferência de informações básicas sobre imagens radiológicas
 * sem carregar o conteúdo base64 completo, evitando problemas de memória
 * e erros de conversão de tipo no banco de dados.
 */
public class ImagemRadiologicaResumo {
    
    private Long id;
    private String nomeArquivo;
    private String tipoImagem;
    private String descricao;
    private String formatoArquivo;
    private Long tamanhoArquivo;
    private LocalDateTime dataUpload;
    private Long dentistaId;
    private String dentistaNome;

    public ImagemRadiologicaResumo() {
    }

    public ImagemRadiologicaResumo(Long id, String nomeArquivo, String tipoImagem, 
                                  String descricao, String formatoArquivo, 
                                  Long tamanhoArquivo, LocalDateTime dataUpload, 
                                  Long dentistaId, String dentistaNome) {
        this.id = id;
        this.nomeArquivo = nomeArquivo;
        this.tipoImagem = tipoImagem;
        this.descricao = descricao;
        this.formatoArquivo = formatoArquivo;
        this.tamanhoArquivo = tamanhoArquivo;
        this.dataUpload = dataUpload;
        this.dentistaId = dentistaId;
        this.dentistaNome = dentistaNome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getTipoImagem() {
        return tipoImagem;
    }

    public void setTipoImagem(String tipoImagem) {
        this.tipoImagem = tipoImagem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFormatoArquivo() {
        return formatoArquivo;
    }

    public void setFormatoArquivo(String formatoArquivo) {
        this.formatoArquivo = formatoArquivo;
    }

    public Long getTamanhoArquivo() {
        return tamanhoArquivo;
    }

    public void setTamanhoArquivo(Long tamanhoArquivo) {
        this.tamanhoArquivo = tamanhoArquivo;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }

    public Long getDentistaId() {
        return dentistaId;
    }

    public void setDentistaId(Long dentistaId) {
        this.dentistaId = dentistaId;
    }

    public String getDentistaNome() {
        return dentistaNome;
    }

    public void setDentistaNome(String dentistaNome) {
        this.dentistaNome = dentistaNome;
    }
}
