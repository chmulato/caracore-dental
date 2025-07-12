package com.caracore.cca.repository;

import com.caracore.cca.model.Prontuario;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.model.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações com Prontuario.
 */
@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    /**
     * Busca o prontuário de um paciente específico
     */
    Optional<Prontuario> findByPaciente(Paciente paciente);

    /**
     * Busca o prontuário pelo ID do paciente
     */
    @Query("SELECT p FROM Prontuario p WHERE p.paciente.id = :pacienteId")
    Optional<Prontuario> findByPacienteId(@Param("pacienteId") Long pacienteId);

    /**
     * Busca todos os prontuários de um dentista específico
     */
    List<Prontuario> findByDentista(Dentista dentista);

    /**
     * Busca prontuários por dentista ordenados por data de atualização
     */
    @Query("SELECT p FROM Prontuario p WHERE p.dentista.id = :dentistaId ORDER BY p.dataUltimaAtualizacao DESC")
    List<Prontuario> findByDentistaIdOrderByDataUltimaAtualizacaoDesc(@Param("dentistaId") Long dentistaId);

    /**
     * Verifica se já existe prontuário para o paciente
     */
    @Query("SELECT COUNT(p) > 0 FROM Prontuario p WHERE p.paciente.id = :pacienteId")
    boolean existsByPacienteId(@Param("pacienteId") Long pacienteId);

    /**
     * Busca prontuários com imagens radiológicas
     */
    @Query("SELECT DISTINCT p FROM Prontuario p " +
           "LEFT JOIN FETCH p.imagensRadiologicas i " +
           "WHERE i.ativo = true")
    List<Prontuario> findProntuariosComImagensAtivas();

    /**
     * Busca prontuários atualizados recentemente
     */
    @Query("SELECT p FROM Prontuario p WHERE p.dataUltimaAtualizacao >= :dataLimite ORDER BY p.dataUltimaAtualizacao DESC")
    List<Prontuario> findProntuariosAtualizadosRecentemente(@Param("dataLimite") java.time.LocalDateTime dataLimite);
}
