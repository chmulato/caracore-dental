package com.caracore.cca.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa um agendamento de consulta no sistema.
 * 
 * Status possíveis:
 * - AGENDADO: Consulta agendada (status inicial)
 * - CONFIRMADO: Paciente confirmou presença
 * - REAGENDADO: Consulta foi reagendada
 * - REALIZADO: Consulta foi realizada
 * - CANCELADO: Consulta foi cancelada
 * - NAO_COMPARECEU: Paciente não compareceu
 */
@Entity
@Table(name = "agendamento")
@Schema(description = "Representa um agendamento de consulta odontológica")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do agendamento", example = "1")
    private Long id;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nome do paciente", example = "João Silva", required = true)
    private String paciente;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nome do dentista", example = "Dr. João Silva - Clínico Geral", required = true)
    private String dentista;

    @Column(name = "data_hora", nullable = false)
    @Schema(description = "Data e hora do agendamento", example = "2025-07-10T10:00:00", required = true)
    private LocalDateTime dataHora;
    
    @Column(length = 1000)
    @Schema(description = "Observações sobre o agendamento", example = "Consulta de rotina")
    private String observacao;
    
    @Column(nullable = false, length = 20)
    @Schema(description = "Status do agendamento", example = "AGENDADO", allowableValues = {"AGENDADO", "CONFIRMADO", "REAGENDADO", "REALIZADO", "CANCELADO", "NAO_COMPARECEU"})
    private String status = "AGENDADO";
    
    @Column(name = "duracao_minutos")
    @Schema(description = "Duração da consulta em minutos", example = "30")
    private Integer duracaoMinutos = 30;
    
    @Column(name = "telefone_whatsapp", length = 20)
    @Schema(description = "Telefone do paciente para contato via WhatsApp", example = "11999999999")
    private String telefoneWhatsapp;
    
    @Column(name = "data_criacao", nullable = false)
    @Schema(description = "Data de criação do agendamento", example = "2025-07-05T21:30:00")
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Construtores
    public Agendamento() {
    }

    public Agendamento(String paciente, String dentista, LocalDateTime dataHora) {
        this.paciente = paciente;
        this.dentista = dentista;
        this.dataHora = dataHora;
        this.dataCriacao = LocalDateTime.now();
    }

    // Métodos de conveniência
    public boolean isRealizado() {
        return "REALIZADO".equals(this.status);
    }
    
    public boolean isCancelado() {
        return "CANCELADO".equals(this.status);
    }
    
    public boolean isConfirmado() {
        return "CONFIRMADO".equals(this.status);
    }
    
    public boolean isPendente() {
        return "AGENDADO".equals(this.status);
    }
    
    public boolean isReagendado() {
        return "REAGENDADO".equals(this.status);
    }
    
    public String getStatusFormatado() {
        return switch (this.status) {
            case "AGENDADO" -> "Agendado";
            case "CONFIRMADO" -> "Confirmado";
            case "REAGENDADO" -> "Reagendado";
            case "REALIZADO" -> "Realizado";
            case "CANCELADO" -> "Cancelado";
            case "NAO_COMPARECEU" -> "Não Compareceu";
            default -> this.status;
        };
    }
    
    public String getStatusCor() {
        return switch (this.status) {
            case "AGENDADO" -> "primary";
            case "CONFIRMADO" -> "success";
            case "REAGENDADO" -> "warning";
            case "REALIZADO" -> "info";
            case "CANCELADO" -> "danger";
            case "NAO_COMPARECEU" -> "secondary";
            default -> "light";
        };
    }

    // Método executado antes de persistir/atualizar
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
    
    public String getPaciente() { 
        return paciente; 
    }
    
    public void setPaciente(String paciente) { 
        this.paciente = paciente; 
    }
    
    public String getDentista() { 
        return dentista; 
    }
    
    public void setDentista(String dentista) { 
        this.dentista = dentista; 
    }
    
    public LocalDateTime getDataHora() { 
        return dataHora; 
    }
    
    public void setDataHora(LocalDateTime dataHora) { 
        this.dataHora = dataHora; 
    }
    
    public String getObservacao() { 
        return observacao; 
    }
    
    public void setObservacao(String observacao) { 
        this.observacao = observacao; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public Integer getDuracaoMinutos() { 
        return duracaoMinutos; 
    }
    
    public void setDuracaoMinutos(Integer duracaoMinutos) { 
        this.duracaoMinutos = duracaoMinutos; 
    }
    
    public String getTelefoneWhatsapp() { 
        return telefoneWhatsapp; 
    }
    
    public void setTelefoneWhatsapp(String telefoneWhatsapp) { 
        this.telefoneWhatsapp = telefoneWhatsapp; 
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
        Agendamento that = (Agendamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Agendamento{" +
                "id=" + id +
                ", paciente='" + paciente + '\'' +
                ", dentista='" + dentista + '\'' +
                ", dataHora=" + dataHora +
                ", status='" + status + '\'' +
                '}';
    }
}