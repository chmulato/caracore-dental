package com.caracore.cca.repository;

import com.caracore.cca.model.RegistroTratamento;
import com.caracore.cca.model.RegistroTratamento.StatusTratamento;
import com.caracore.cca.model.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações com RegistroTratamento.
 */
@Repository
public interface RegistroTratamentoRepository extends JpaRepository<RegistroTratamento, Long> {

    /**
     * Busca registros de tratamento por prontuário ordenados por data
     */
    List<RegistroTratamento> findByProntuarioOrderByDataTratamentoDesc(Prontuario prontuario);

    /**
     * Busca registros por ID do prontuário
     */
    @Query("SELECT r FROM RegistroTratamento r WHERE r.prontuario.id = :prontuarioId ORDER BY r.dataTratamento DESC")
    List<RegistroTratamento> findByProntuarioIdOrderByDataTratamentoDesc(@Param("prontuarioId") Long prontuarioId);

    /**
     * Busca registros por status
     */
    @Query("SELECT r FROM RegistroTratamento r WHERE r.prontuario.id = :prontuarioId AND r.status = :status ORDER BY r.dataTratamento DESC")
    List<RegistroTratamento> findByProntuarioIdAndStatus(@Param("prontuarioId") Long prontuarioId, @Param("status") StatusTratamento status);

    /**
     * Busca registros por dentista
     */
    @Query("SELECT r FROM RegistroTratamento r WHERE r.dentista.id = :dentistaId ORDER BY r.dataTratamento DESC")
    List<RegistroTratamento> findByDentistaIdOrderByDataTratamentoDesc(@Param("dentistaId") Long dentistaId);

    /**
     * Busca tratamentos em andamento por dentista
     */
    @Query("SELECT r FROM RegistroTratamento r WHERE r.dentista.id = :dentistaId AND r.status = 'EM_ANDAMENTO' ORDER BY r.dataTratamento DESC")
    List<RegistroTratamento> findTratamentosEmAndamentoPorDentista(@Param("dentistaId") Long dentistaId);

    /**
     * Busca registros por procedimento
     */
    @Query("SELECT r FROM RegistroTratamento r WHERE r.procedimento LIKE %:procedimento% ORDER BY r.dataTratamento DESC")
    List<RegistroTratamento> findByProcedimentoContaining(@Param("procedimento") String procedimento);

    /**
     * Busca registros por período
     */
    @Query("SELECT r FROM RegistroTratamento r WHERE r.dataTratamento BETWEEN :dataInicio AND :dataFim ORDER BY r.dataTratamento DESC")
    List<RegistroTratamento> findByDataTratamentoBetween(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    /**
     * Conta registros por status em um prontuário
     */
    @Query("SELECT COUNT(r) FROM RegistroTratamento r WHERE r.prontuario.id = :prontuarioId AND r.status = :status")
    Long countByProntuarioIdAndStatus(@Param("prontuarioId") Long prontuarioId, @Param("status") StatusTratamento status);

    /**
     * Calcula valor total dos tratamentos por prontuário
     */
    @Query("SELECT COALESCE(SUM(r.valorProcedimento), 0) FROM RegistroTratamento r WHERE r.prontuario.id = :prontuarioId")
    Double sumValorProcedimentoByProntuarioId(@Param("prontuarioId") Long prontuarioId);

    /**
     * Busca últimos tratamentos do paciente
     */
    @Query("SELECT r FROM RegistroTratamento r WHERE r.prontuario.id = :prontuarioId ORDER BY r.dataTratamento DESC")
    List<RegistroTratamento> findUltimosTratamentosPorProntuario(@Param("prontuarioId") Long prontuarioId, org.springframework.data.domain.Pageable pageable);
}
