package com.caracore.cca.model;

import jakarta.persistence.*;
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

    private String email;

    @Column(name = "telefone", nullable = true, length = 20)
    private String telefone; // Armazena o número de WhatsApp do paciente

    @Column(name = "consentimento_lgpd", nullable = false)
    private Boolean consentimentoLgpd = false; // Consentimento LGPD enviado via WhatsApp

    @Column(name = "consentimento_confirmado", nullable = false)
    private Boolean consentimentoConfirmado = false; // Confirmação do recebimento do consentimento

    @Column(name = "data_consentimento")
    private java.time.LocalDateTime dataConsentimento; // Data/hora do envio do consentimento

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
