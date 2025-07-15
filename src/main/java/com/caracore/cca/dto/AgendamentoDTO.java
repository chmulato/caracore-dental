package com.caracore.cca.dto;

import java.time.LocalDateTime;

public class AgendamentoDTO {
    private String pacienteNome;
    private String pacienteTelefone;
    private String pacienteEmail;
    private String dentistaNome;
    private LocalDateTime dataHoraConsulta;
    
    // Construtores
    public AgendamentoDTO() {
    }
    
    public AgendamentoDTO(String pacienteNome, String pacienteTelefone, String pacienteEmail,
                          String dentistaNome, LocalDateTime dataHoraConsulta) {
        this.pacienteNome = pacienteNome;
        this.pacienteTelefone = pacienteTelefone;
        this.pacienteEmail = pacienteEmail;
        this.dentistaNome = dentistaNome;
        this.dataHoraConsulta = dataHoraConsulta;
    }
    
    // Getters e Setters
    public String getPacienteNome() {
        return pacienteNome;
    }
    
    public void setPacienteNome(String pacienteNome) {
        this.pacienteNome = pacienteNome;
    }
    
    public String getPacienteTelefone() {
        return pacienteTelefone;
    }
    
    public void setPacienteTelefone(String pacienteTelefone) {
        this.pacienteTelefone = pacienteTelefone;
    }
    
    public String getPacienteEmail() {
        return pacienteEmail;
    }
    
    public void setPacienteEmail(String pacienteEmail) {
        this.pacienteEmail = pacienteEmail;
    }
    
    public String getDentistaNome() {
        return dentistaNome;
    }
    
    public void setDentistaNome(String dentistaNome) {
        this.dentistaNome = dentistaNome;
    }
    
    public LocalDateTime getDataHoraConsulta() {
        return dataHoraConsulta;
    }
    
    public void setDataHoraConsulta(LocalDateTime dataHoraConsulta) {
        this.dataHoraConsulta = dataHoraConsulta;
    }
    
    @Override
    public String toString() {
        return "AgendamentoDTO{" +
                "pacienteNome='" + pacienteNome + '\'' +
                ", pacienteTelefone='" + pacienteTelefone + '\'' +
                ", pacienteEmail='" + pacienteEmail + '\'' +
                ", dentistaNome='" + dentistaNome + '\'' +
                ", dataHoraConsulta=" + dataHoraConsulta +
                '}';
    }
}
