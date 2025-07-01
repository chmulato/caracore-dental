package com.caracore.cca.controller;

import com.caracore.cca.model.Paciente;
import com.caracore.cca.repository.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador responsável pelas operações relacionadas a pacientes.
 * 
 * Níveis de acesso:
 * - ADMIN: acesso completo (visualizar, criar, editar e excluir pacientes)
 * - RECEPTIONIST: pode visualizar, criar e editar pacientes, mas não excluir
 * - DENTIST: acesso somente leitura (visualizar lista e detalhes de pacientes)
 */
@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private static final Logger logger = LoggerFactory.getLogger(PacienteController.class);
    
    private final PacienteRepository pacienteRepository;

    public PacienteController(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    /**
     * Exibe a lista de todos os pacientes.
     * Acessível para administradores, dentistas e recepcionistas.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String listarPacientes(Model model) {
        logger.info("Listando todos os pacientes");
        List<Paciente> pacientes = pacienteRepository.findAll();
        model.addAttribute("pacientes", pacientes);
        return "pacientes/lista";
    }

    /**
     * Exibe o formulário para adicionar um novo paciente.
     * Acessível apenas para administradores e recepcionistas.
     * Dentistas não têm permissão para criar novos pacientes no sistema.
     */
    @GetMapping("/novo")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public String formNovoCliente(Model model) {
        logger.info("Exibindo formulário para novo paciente");
        model.addAttribute("paciente", new Paciente());
        return "pacientes/form";
    }

    /**
     * Salva um novo paciente no banco de dados ou atualiza um existente.
     * Acessível apenas para administradores e recepcionistas.
     * Dentistas não têm permissão para criar ou modificar dados de pacientes.
     */
    @PostMapping("/salvar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public String salvarPaciente(@ModelAttribute Paciente paciente) {
        logger.info("Salvando novo paciente: {}", paciente.getNome());
        pacienteRepository.save(paciente);
        return "redirect:/pacientes";
    }

    /**
     * Exibe os detalhes de um paciente específico.
     * Acessível para todos os perfis (administradores, dentistas e recepcionistas).
     * Esta é uma operação de leitura que permite que todos os usuários autenticados 
     * visualizem informações detalhadas dos pacientes.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String detalhesPaciente(@PathVariable Long id, Model model) {
        logger.info("Exibindo detalhes do paciente ID: {}", id);
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        
        if (paciente.isPresent()) {
            model.addAttribute("paciente", paciente.get());
            return "pacientes/detalhes";
        } else {
            logger.warn("Paciente com ID {} não encontrado", id);
            return "redirect:/pacientes?erro=paciente-nao-encontrado";
        }
    }

    /**
     * Exibe o formulário para edição de um paciente existente.
     * Acessível apenas para administradores e recepcionistas.
     * Dentistas podem visualizar os detalhes do paciente, mas não podem editar suas informações.
     */
    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public String editarPaciente(@PathVariable Long id, Model model) {
        logger.info("Preparando formulário para editar paciente ID: {}", id);
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        
        if (paciente.isPresent()) {
            model.addAttribute("paciente", paciente.get());
            return "pacientes/form";
        } else {
            logger.warn("Paciente com ID {} não encontrado para edição", id);
            return "redirect:/pacientes?erro=paciente-nao-encontrado";
        }
    }

    /**
     * Exclui um paciente do sistema.
     * Operação crítica acessível APENAS para administradores.
     * Receptionistas e dentistas não têm permissão para excluir pacientes.
     */
    @GetMapping("/{id}/excluir")
    @PreAuthorize("hasRole('ADMIN')")
    public String excluirPaciente(@PathVariable Long id) {
        logger.info("Excluindo paciente ID: {}", id);
        
        if (pacienteRepository.existsById(id)) {
            pacienteRepository.deleteById(id);
            return "redirect:/pacientes?sucesso=paciente-excluido";
        } else {
            logger.warn("Tentativa de excluir paciente inexistente ID: {}", id);
            return "redirect:/pacientes?erro=paciente-nao-encontrado";
        }
    }

    /**
     * Busca pacientes por nome.
     * Acessível para administradores, dentistas e recepcionistas.
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String buscarPacientes(@RequestParam String termo, Model model) {
        logger.info("Buscando pacientes com o termo: {}", termo);
        List<Paciente> pacientes = pacienteRepository.findByNomeContainingIgnoreCase(termo);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("termoBusca", termo);
        return "pacientes/lista";
    }
}
