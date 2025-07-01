package com.caracore.cca.repository;

import com.caracore.cca.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de banco de dados relacionadas aos pacientes.
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    
    /**
     * Encontra um paciente pelo seu endereço de e-mail.
     * 
     * @param email o email do paciente
     * @return um Optional contendo o paciente, se encontrado
     */
    Optional<Paciente> findByEmail(String email);
    
    /**
     * Encontra pacientes cujo nome contém o texto fornecido (case insensitive).
     * 
     * @param nome parte do nome a ser pesquisado
     * @return lista de pacientes que correspondem ao critério de busca
     */
    List<Paciente> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Encontra pacientes pelo número de telefone.
     * 
     * @param telefone o número de telefone do paciente
     * @return lista de pacientes com o telefone especificado
     */
    List<Paciente> findByTelefone(String telefone);
}
