package com.caracore.cca.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Entidade que representa um paciente no sistema.
 */
@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "nome_social", nullable = true, length = 100)
    private String nomeSocial; // Nome social conforme Portaria nº 2.836/2011

    @Column(name = "genero", nullable = true, length = 50)
    private String genero; // Gênero conforme autodeclaração do paciente

    private String email;

    @Column(name = "telefone", nullable = true, length = 20)
    private String telefone; // Armazena o número de WhatsApp do paciente

    @Column(name = "data_nascimento", nullable = true)
    private LocalDate dataNascimento; // Data de nascimento do paciente (opcional)

    @Column(name = "consentimento_lgpd", nullable = false)
    private Boolean consentimentoLgpd = false; // Consentimento LGPD enviado via WhatsApp

    @Column(name = "consentimento_confirmado", nullable = false)
    private Boolean consentimentoConfirmado = false; // Confirmação do recebimento do consentimento

    @Column(name = "data_consentimento")
    private java.time.LocalDateTime dataConsentimento; // Data/hora do envio do consentimento

    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Prontuario prontuario;

    public Paciente() {
    }

    public Paciente(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeSocial() {
        return nomeSocial;
    }

    public void setNomeSocial(String nomeSocial) {
        this.nomeSocial = nomeSocial;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    /**
     * Calcula a idade do paciente com base na data de nascimento
     * @return idade em anos, ou null se data de nascimento não estiver informada
     */
    public Integer getIdade() {
        if (dataNascimento == null) {
            return null;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public Boolean getConsentimentoLgpd() {
        return consentimentoLgpd;
    }

    public void setConsentimentoLgpd(Boolean consentimentoLgpd) {
        this.consentimentoLgpd = consentimentoLgpd;
    }

    public Boolean getConsentimentoConfirmado() {
        return consentimentoConfirmado;
    }

    public void setConsentimentoConfirmado(Boolean consentimentoConfirmado) {
        this.consentimentoConfirmado = consentimentoConfirmado;
    }

    public java.time.LocalDateTime getDataConsentimento() {
        return dataConsentimento;
    }

    public void setDataConsentimento(java.time.LocalDateTime dataConsentimento) {
        this.dataConsentimento = dataConsentimento;
    }

    public Prontuario getProntuario() {
        return prontuario;
    }

    public void setProntuario(Prontuario prontuario) {
        this.prontuario = prontuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return Objects.equals(id, paciente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
