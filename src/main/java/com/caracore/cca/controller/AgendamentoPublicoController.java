package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.service.AgendamentoService;
import com.caracore.cca.service.PacienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador responsável pelo agendamento online público.
 * 
 * Este controller não requer autenticação, permitindo que pacientes
 * agendem consultas diretamente pelo site.
 */
@Controller
public class AgendamentoPublicoController {

    private static final Logger logger = LoggerFactory.getLogger(AgendamentoPublicoController.class);

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private PacienteService pacienteService;

    /**
     * Página principal de agendamento online
     */
    @GetMapping("/public/agendamento")
    public String agendamentoOnline(Model model) {
        logger.info("Acessando página de agendamento online público");
        
        List<String> dentistas = agendamentoService.listarDentistas();
        model.addAttribute("titulo", "Agendamento Online");
        model.addAttribute("dentistas", dentistas);
        
        return "public/agendamento-online";
    }

    /**
     * Processar agendamento público
     */
    @PostMapping("/public/agendamento")
    public String processarAgendamentoPublico(@RequestParam(required = false) String paciente, 
                                           @RequestParam(required = false) String dentista,
                                           @RequestParam(required = false) String dataHora,
                                           @RequestParam(required = false) String telefone,
                                           @RequestParam(required = false) String email,
                                           Model model) {
        logger.info("Processando agendamento público para paciente: {}, dentista: {}, dataHora: {}", 
                  paciente, dentista, dataHora);
        
        try {
            // Validação básica - note que campos são required=false para que o controller possa receber valores nulos
            if (paciente == null || paciente.isEmpty() || dentista == null || dentista.isEmpty() || dataHora == null || dataHora.isEmpty()) {
                model.addAttribute("error", "Todos os campos são obrigatórios");
                List<String> dentistas = agendamentoService.listarDentistas();
                model.addAttribute("dentistas", dentistas);
                model.addAttribute("titulo", "Agendamento Online");
                return "public/agendamento-online";
            }
            
            // Criar agendamento
            Agendamento agendamento = new Agendamento();
            agendamento.setPaciente(paciente);
            agendamento.setDentista(dentista);
            agendamento.setDataHora(LocalDateTime.parse(dataHora));
            agendamento.setStatus("AGENDADO");
            agendamento.setObservacao("Agendamento online");
            agendamento.setTelefoneWhatsapp(telefone);
            agendamento.setDuracaoMinutos(30);
            
            agendamento = agendamentoService.salvar(agendamento);
            
            return "redirect:/public/agendamento-confirmado?id=" + agendamento.getId();
        } catch (Exception e) {
            logger.error("Erro ao processar agendamento", e);
            model.addAttribute("error", "Ocorreu um erro ao processar o agendamento");
            List<String> dentistas = agendamentoService.listarDentistas();
            model.addAttribute("dentistas", dentistas);
            model.addAttribute("titulo", "Agendamento Online");
            return "public/agendamento-online";
        }
    }

    /**
     * Página de confirmação de agendamento
     * 
     * Para testes unitários, em vez de renderizar a página completa, apenas verificamos
     * se o agendamento existe e o adicionamos ao modelo
     */
    @GetMapping("/public/agendamento-confirmado")
    public String agendamentoConfirmado(@RequestParam Long id, Model model) {
        logger.info("Exibindo confirmação de agendamento público - ID: {}", id);
        
        Optional<Agendamento> agendamento = agendamentoService.buscarPorId(id);
        if (agendamento.isPresent()) {
            model.addAttribute("agendamento", agendamento.get());
            return "public/agendamento-confirmado";
        } else {
            // Se não encontrar o agendamento, retorna 404
            throw new ResourceNotFoundException("Agendamento não encontrado");
        }
    }
    
    /**
     * Exception handler para recursos não encontrados
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleResourceNotFound() {
        // Retorna 404 sem corpo
    }
    
    /**
     * Classe de exceção para recursos não encontrados
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
        
        private static final long serialVersionUID = 1L;
    }

    /**
     * API para obter horários disponíveis
     */
    @GetMapping("/api/public/horarios-disponiveis")
    @ResponseBody
    public Map<String, Object> obterHorariosDisponiveisPublico(@RequestParam(required = false) String dentista,
                                                         @RequestParam(required = false) String data) {
        logger.info("API: Obtendo horários disponíveis públicos - Dentista: {}, Data: {}", dentista, data);
        
        // Validar parâmetros
        if (dentista == null || dentista.isEmpty() || data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Parâmetros inválidos");
        }
        
        try {
            LocalDateTime dataInicio = LocalDateTime.parse(data + "T00:00:00");
            List<String> horarios = agendamentoService.getHorariosDisponiveisPorData(dentista, dataInicio);
            return Map.of("horarios", horarios);
        } catch (Exception e) {
            logger.error("Erro ao buscar horários disponíveis", e);
            throw new IllegalArgumentException("Formato de data inválido");
        }
    }

    /**
     * Exception handler para requisições inválidas nas APIs
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Map.of("erro", ex.getMessage());
    }

    /**
     * API para verificar disponibilidade de horário específico
     */
    @GetMapping("/api/public/verificar-disponibilidade")
    @ResponseBody
    public Map<String, Object> verificarDisponibilidadePublica(@RequestParam String dentista,
                                                     @RequestParam String dataHora) {
        logger.info("API: Verificando disponibilidade pública - Dentista: {}, DataHora: {}", 
                   dentista, dataHora);
        
        try {
            LocalDateTime dataHoraVerificacao = LocalDateTime.parse(dataHora);
            
            // Verificar se há conflito
            boolean disponivel = !agendamentoService.verificarConflitoHorario(
                dentista, dataHoraVerificacao, null);
            
            return Map.of("disponivel", disponivel);
            
        } catch (Exception e) {
            logger.error("Erro ao verificar disponibilidade pública", e);
            return Map.of("disponivel", false);
        }
    }

    /**
     * API para listar dentistas
     */
    @GetMapping("/api/public/dentistas")
    @ResponseBody
    public Map<String, Object> listarDentistasPublico() {
        logger.info("API: Obtendo lista de dentistas públicos");
        
        List<String> dentistas = agendamentoService.listarDentistas();
        return Map.of("dentistas", dentistas);
    }
}
