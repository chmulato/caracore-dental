package com.caracore.cca.controller.form;

import com.caracore.cca.model.Agendamento;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class AgendamentoForm {

    @NotBlank(message = "Nome do paciente é obrigatório")
    @Size(min = 3, message = "Nome do paciente deve ter no mínimo {min} caracteres")
    private String paciente;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (99) 99999-9999")
    private String telefone;

    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Nome do dentista é obrigatório")
    private String dentista;

    @NotNull(message = "Data e hora são obrigatórios")
    @Future(message = "A data e hora devem ser futuras")
    private LocalDateTime dataHora;

    @Size(max = 500, message = "Observação deve ter no máximo {max} caracteres")
    private String observacao;

    public Agendamento toAgendamento() {
        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(this.paciente);
        agendamento.setDentista(this.dentista);
        agendamento.setDataHora(this.dataHora);
        agendamento.setObservacao(this.observacao);
        agendamento.setStatus("AGENDADO");
        agendamento.setDuracaoMinutos(30);
        return agendamento;
    }

    // Getters e Setters
    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
