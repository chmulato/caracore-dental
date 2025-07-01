package com.caracore.cca.service;

import com.caracore.cca.model.Paciente;
import com.caracore.cca.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Serviço para gerenciamento de pacientes.
 * Implementa a lógica de negócios relacionada aos pacientes.
 */
@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    /**
     * Busca todos os pacientes cadastrados
     */
    public List<Paciente> buscarTodos() {
        return pacienteRepository.findAll();
    }

    /**
     * Busca um paciente pelo ID
     */
    public Optional<Paciente> buscarPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    /**
     * Salva um novo paciente
     */
    @Transactional
    public Paciente salvar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    /**
     * Atualiza os dados de um paciente existente
     */
    @Transactional
    public Paciente atualizar(Paciente paciente) {
        if (!pacienteRepository.existsById(paciente.getId())) {
            throw new IllegalArgumentException("Paciente com id " + paciente.getId() + " não encontrado");
        }
        return pacienteRepository.save(paciente);
    }

    /**
     * Exclui um paciente pelo ID
     */
    @Transactional
    public boolean excluir(Long id) {
        if (!pacienteRepository.existsById(id)) {
            return false;
        }
        pacienteRepository.deleteById(id);
        return true;
    }

    /**
     * Busca pacientes pelo nome (ou parte do nome)
     */
    public List<Paciente> buscarPorNome(String nome) {
        return pacienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Verifica se um paciente existe pelo ID
     */
    public boolean existePorId(Long id) {
        return pacienteRepository.existsById(id);
    }
}
