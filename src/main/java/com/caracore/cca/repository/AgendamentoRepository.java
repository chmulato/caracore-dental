package com.caracore.cca.repository;

import com.caracore.cca.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    /**
     * Encontra agendamentos em um intervalo de datas (independente do dentista)
     */
    List<Agendamento> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    
    /**
     * Encontra agendamentos por status
     */
    List<Agendamento> findByStatus(String status);
    
    /**
     * Encontra agendamentos por status ordenados por data
     */
    List<Agendamento> findByStatusOrderByDataHoraAsc(String status);
    
    /**
     * Encontra agendamentos futuros (depois da data atual)
     */
    @Query("SELECT a FROM Agendamento a WHERE a.dataHora > :dataAtual ORDER BY a.dataHora ASC")
    List<Agendamento> findProximosAgendamentos(@Param("dataAtual") LocalDateTime dataAtual);
    
    /**
     * Encontra agendamentos do dia atual
     */
    @Query("SELECT a FROM Agendamento a WHERE DATE(a.dataHora) = DATE(:data) ORDER BY a.dataHora ASC")
    List<Agendamento> findAgendamentosDoDay(@Param("data") LocalDateTime data);
    
    /**
     * Encontra agendamentos por dentista e data específica
     */
    @Query("SELECT a FROM Agendamento a WHERE a.dentista LIKE %:dentista% AND DATE(a.dataHora) = DATE(:data) ORDER BY a.dataHora ASC")
    List<Agendamento> findByDentistaAndData(@Param("dentista") String dentista, @Param("data") LocalDateTime data);
    
    /**
     * Conta agendamentos por status
     */
    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.status = :status")
    Long countByStatus(@Param("status") String status);
    
    /**
     * Busca agendamentos por termo geral (paciente, dentista ou observação)
     */
    @Query("SELECT a FROM Agendamento a WHERE " +
           "LOWER(a.paciente) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(a.dentista) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(a.observacao) LIKE LOWER(CONCAT('%', :termo, '%')) " +
           "ORDER BY a.dataHora DESC")
    List<Agendamento> buscarPorTermo(@Param("termo") String termo);
    
    /**
     * Encontra agendamentos que podem ter conflito de horário
     */
    @Query("SELECT a FROM Agendamento a WHERE a.dentista = :dentista AND " +
           "a.dataHora BETWEEN :inicio AND :fim AND a.status != 'CANCELADO'")
    List<Agendamento> findPossiveisConflitos(@Param("dentista") String dentista, 
                                           @Param("inicio") LocalDateTime inicio, 
                                           @Param("fim") LocalDateTime fim);
}