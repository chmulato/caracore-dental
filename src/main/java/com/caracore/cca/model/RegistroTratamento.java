package com.caracore.cca.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa um registro de tratamento no prontuário.
 */
@Entity
@Table(name = "registro_tratamento")
public class RegistroTratamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private Prontuario prontuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentista_id", nullable = false)
    private Dentista dentista;

    @Column(name = "data_tratamento", nullable = false)
    private LocalDateTime dataTratamento;

    @Column(name = "procedimento", nullable = false, length = 255)
    private String procedimento;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "dente", length = 50)
    private String dente; // Ex: "16", "21", "46", etc.

    @Column(name = "material_utilizado", columnDefinition = "TEXT")
    private String materialUtilizado;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "valor_procedimento")
    private Double valorProcedimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusTratamento status = StatusTratamento.EM_ANDAMENTO;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    public enum StatusTratamento {
        PLANEJADO("Planejado"),
        EM_ANDAMENTO("Em Andamento"),
        CONCLUIDO("Concluído"),
        CANCELADO("Cancelado"),
        ADIADO("Adiado");

        private final String descricao;

        StatusTratamento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public RegistroTratamento() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        this.dataTratamento = LocalDateTime.now();
    }

    public RegistroTratamento(Prontuario prontuario, Dentista dentista, String procedimento) {
        this();
        this.prontuario = prontuario;
        this.dentista = dentista;
        this.procedimento = procedimento;
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
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

    public Dentista getDentista() {
        return dentista;
    }

    public void setDentista(Dentista dentista) {
        this.dentista = dentista;
    }

    public LocalDateTime getDataTratamento() {
        return dataTratamento;
    }

    public void setDataTratamento(LocalDateTime dataTratamento) {
        this.dataTratamento = dataTratamento;
    }

    public String getProcedimento() {
        return procedimento;
    }

    public void setProcedimento(String procedimento) {
        this.procedimento = procedimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDente() {
        return dente;
    }

    public void setDente(String dente) {
        this.dente = dente;
    }

    public String getMaterialUtilizado() {
        return materialUtilizado;
    }

    public void setMaterialUtilizado(String materialUtilizado) {
        this.materialUtilizado = materialUtilizado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Double getValorProcedimento() {
        return valorProcedimento;
    }

    public void setValorProcedimento(Double valorProcedimento) {
        this.valorProcedimento = valorProcedimento;
    }

    public StatusTratamento getStatus() {
        return status;
    }

    public void setStatus(StatusTratamento status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistroTratamento that = (RegistroTratamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RegistroTratamento{" +
                "id=" + id +
                ", procedimento='" + procedimento + '\'' +
                ", dente='" + dente + '\'' +
                ", status=" + status +
                ", dataTratamento=" + dataTratamento +
                '}';
    }
}
