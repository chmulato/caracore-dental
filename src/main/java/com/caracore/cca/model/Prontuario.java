package com.caracore.cca.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade que representa o prontuário odontológico de um paciente.
 */
@Entity
@Table(name = "prontuario")
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentista_id", nullable = false)
    private Dentista dentista;

    @Column(name = "historico_medico", columnDefinition = "TEXT")
    private String historicoMedico;

    @Column(name = "alergias", columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "medicamentos_uso", columnDefinition = "TEXT")
    private String medicamentosUso;

    @Column(name = "observacoes_gerais", columnDefinition = "TEXT")
    private String observacoesGerais;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagemRadiologica> imagensRadiologicas = new ArrayList<>();

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroTratamento> registrosTratamentos = new ArrayList<>();

    public Prontuario() {
        this.dataCriacao = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public Prontuario(Paciente paciente, Dentista dentista) {
        this();
        this.paciente = paciente;
        this.dentista = dentista;
        // Estabelece associação bidirecional
        if (paciente != null) {
            paciente.setProntuario(this);
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Dentista getDentista() {
        return dentista;
    }

    public void setDentista(Dentista dentista) {
        this.dentista = dentista;
    }

    public String getHistoricoMedico() {
        return historicoMedico;
    }

    public void setHistoricoMedico(String historicoMedico) {
        this.historicoMedico = historicoMedico;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getMedicamentosUso() {
        return medicamentosUso;
    }

    public void setMedicamentosUso(String medicamentosUso) {
        this.medicamentosUso = medicamentosUso;
    }

    public String getObservacoesGerais() {
        return observacoesGerais;
    }

    public void setObservacoesGerais(String observacoesGerais) {
        this.observacoesGerais = observacoesGerais;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public List<ImagemRadiologica> getImagensRadiologicas() {
        return imagensRadiologicas;
    }

    public void setImagensRadiologicas(List<ImagemRadiologica> imagensRadiologicas) {
        this.imagensRadiologicas = imagensRadiologicas;
    }

    public List<RegistroTratamento> getRegistrosTratamentos() {
        return registrosTratamentos;
    }

    public void setRegistrosTratamentos(List<RegistroTratamento> registrosTratamentos) {
        this.registrosTratamentos = registrosTratamentos;
    }

    // Métodos auxiliares
    public void adicionarImagemRadiologica(ImagemRadiologica imagem) {
        imagensRadiologicas.add(imagem);
        imagem.setProntuario(this);
    }

    public void removerImagemRadiologica(ImagemRadiologica imagem) {
        imagensRadiologicas.remove(imagem);
        imagem.setProntuario(null);
    }

    public void adicionarRegistroTratamento(RegistroTratamento registro) {
        registrosTratamentos.add(registro);
        registro.setProntuario(this);
    }

    public void removerRegistroTratamento(RegistroTratamento registro) {
        registrosTratamentos.remove(registro);
        registro.setProntuario(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prontuario that = (Prontuario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Prontuario{" +
                "id=" + id +
                ", paciente=" + (paciente != null ? paciente.getNome() : "null") +
                ", dentista=" + (dentista != null ? dentista.getNome() : "null") +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}
