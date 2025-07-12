package com.caracore.cca.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa uma imagem radiológica associada a um prontuário.
 * As imagens são armazenadas em formato Base64.
 */
@Entity
@Table(name = "imagem_radiologica")
public class ImagemRadiologica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private Prontuario prontuario;

    @Column(name = "nome_arquivo", nullable = false, length = 255)
    private String nomeArquivo;

    @Column(name = "tipo_imagem", nullable = false, length = 100)
    private String tipoImagem; // Ex: "Radiografia Panorâmica", "Periapical", "Bitewing", etc.

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Lob
    @Column(name = "imagem_base64", nullable = false, columnDefinition = "TEXT")
    private String imagemBase64;

    @Column(name = "formato_arquivo", nullable = false, length = 10)
    private String formatoArquivo; // Ex: "jpg", "png", "jpeg"

    @Column(name = "tamanho_arquivo")
    private Long tamanhoArquivo; // Tamanho em bytes

    @Column(name = "data_upload", nullable = false)
    private LocalDateTime dataUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentista_id", nullable = false)
    private Dentista dentista; // Dentista que fez o upload

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public ImagemRadiologica() {
        this.dataUpload = LocalDateTime.now();
        this.ativo = true;
    }

    public ImagemRadiologica(String nomeArquivo, String tipoImagem, String imagemBase64, 
                           String formatoArquivo, Prontuario prontuario, Dentista dentista) {
        this();
        this.nomeArquivo = nomeArquivo;
        this.tipoImagem = tipoImagem;
        this.imagemBase64 = imagemBase64;
        this.formatoArquivo = formatoArquivo;
        this.prontuario = prontuario;
        this.dentista = dentista;
        this.tamanhoArquivo = calcularTamanhoBase64(imagemBase64);
    }

    /**
     * Calcula o tamanho aproximado do arquivo baseado na string Base64
     */
    private Long calcularTamanhoBase64(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return 0L;
        }
        // Remove o prefixo data:image/...;base64, se presente
        String cleanBase64 = base64String.replaceFirst("^data:image/[^;]+;base64,", "");
        // Cada 4 caracteres Base64 representam 3 bytes originais
        return (long) (cleanBase64.length() * 3L / 4L);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prontuario getProntuario() {
        return prontuario;
    }

    public void setProntuario(Prontuario prontuario) {
        this.prontuario = prontuario;
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

    public String getImagemBase64() {
        return imagemBase64;
    }

    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
        this.tamanhoArquivo = calcularTamanhoBase64(imagemBase64);
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

    public Dentista getDentista() {
        return dentista;
    }

    public void setDentista(Dentista dentista) {
        this.dentista = dentista;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    /**
     * Retorna o tamanho do arquivo formatado em KB ou MB
     */
    public String getTamanhoFormatado() {
        if (tamanhoArquivo == null || tamanhoArquivo == 0) {
            return "0 KB";
        }
        
        if (tamanhoArquivo < 1024) {
            return tamanhoArquivo + " bytes";
        } else if (tamanhoArquivo < 1024 * 1024) {
            return String.format("%.1f KB", tamanhoArquivo / 1024.0);
        } else {
            return String.format("%.1f MB", tamanhoArquivo / (1024.0 * 1024.0));
        }
    }

    /**
     * Retorna a URL da imagem para exibição no HTML (data URL)
     */
    public String getDataUrl() {
        if (imagemBase64 == null || imagemBase64.isEmpty()) {
            return null;
        }
        
        // Se já tem o prefixo data:image, retorna como está
        if (imagemBase64.startsWith("data:image/")) {
            return imagemBase64;
        }
        
        // Caso contrário, adiciona o prefixo baseado no formato
        return "data:image/" + formatoArquivo + ";base64," + imagemBase64;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImagemRadiologica that = (ImagemRadiologica) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ImagemRadiologica{" +
                "id=" + id +
                ", nomeArquivo='" + nomeArquivo + '\'' +
                ", tipoImagem='" + tipoImagem + '\'' +
                ", formatoArquivo='" + formatoArquivo + '\'' +
                ", tamanho=" + getTamanhoFormatado() +
                ", dataUpload=" + dataUpload +
                '}';
    }
}
