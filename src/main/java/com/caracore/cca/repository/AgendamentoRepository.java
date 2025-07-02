package com.caracore.cca.repository;

import com.caracore.cca.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    
    /**
     * Encontra todos os agendamentos para um paciente específico
     */
    List<Agendamento> findByPacienteContainingIgnoreCase(String paciente);
    
    /**
     * Encontra todos os agendamentos para um dentista específico
     */
    List<Agendamento> findByDentistaContainingIgnoreCase(String dentista);
    
    /**
     * Encontra agendamentos para um dentista em um intervalo de datas
     */
    List<Agendamento> findByDentistaAndDataHoraBetween(String dentista, LocalDateTime inicio, LocalDateTime fim);
}