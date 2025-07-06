package com.caracore.cca.model.enums;

/**
 * Enum para representar os diferentes status de um agendamento
 */
public enum StatusAgendamento {
    PENDENTE("Pendente"),
    CONFIRMADO("Confirmado"),
    REALIZADO("Realizado"),
    CANCELADO("Cancelado"),
    REMARCADO("Remarcado"),
    FALTOU("Faltou");
    
    private final String descricao;
    
    StatusAgendamento(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}
