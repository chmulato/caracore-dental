package com.caracore.cca.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class AgendamentoForm {

    @NotBlank(message = "O nome do paciente é obrigatório")
    private String paciente;

    @NotBlank(message = "O nome do dentista é obrigatório")
    private String dentista;

    @NotBlank(message = "O telefone WhatsApp é obrigatório")
    @Pattern(regexp = "^\\(?[1-9]{2}\\)? ?(?:9[1-9]|[2-9])[0-9]{3}\\-?[0-9]{4}$", 
             message = "Telefone inválido. Use o formato: (99) 99999-9999")
    private String telefoneWhatsapp;

    @NotNull(message = "A data e hora são obrigatórias")
    @Future(message = "A data e hora devem ser no futuro")
    private LocalDateTime dataHora;
    
    @Size(max = 500, message = "A observação não pode ter mais de 500 caracteres")
    private String observacao;
    
    // Duração estimada em minutos (padrão 30 minutos)
    private Integer duracaoMinutos = 30;
    
    // Status inicial do agendamento
    private String status = "AGENDADO";

    // Getters e Setters
    public String getPaciente() { return paciente; }
    public void setPaciente(String paciente) { this.paciente = paciente; }
    
    public String getDentista() { return dentista; }
    public void setDentista(String dentista) { this.dentista = dentista; }
    
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    
    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getTelefoneWhatsapp() { return telefoneWhatsapp; }
    public void setTelefoneWhatsapp(String telefoneWhatsapp) { this.telefoneWhatsapp = telefoneWhatsapp; }
}