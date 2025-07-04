package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.service.AgendamentoService;
import com.caracore.cca.service.PacienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class AgendamentoPublicoTestController {

    private static final Logger logger = LoggerFactory.getLogger(AgendamentoPublicoTestController.class);

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
        model.addAttribute("dentistas", dentistas);
        model.addAttribute("titulo", "Agendamento Online");
        
        return "public/agendamento-online";
    }

    /**
     * Processar agendamento público
     */
    @PostMapping("/public/agendamento")
    public String processarAgendamentoPublico(
            @RequestParam(required = false) String paciente,
            @RequestParam(required = false) String dentista,
            @RequestParam(required = false) String dataHora,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String email,
            Model model) {
        
        logger.info("Processando agendamento público");
        
        if (paciente == null || paciente.isEmpty() || dentista == null || dentista.isEmpty() || dataHora == null || dataHora.isEmpty()) {
            model.addAttribute("error", "Dados inválidos");
            return "public/agendamento-online";
        }
        
        try {
            Agendamento agendamento = new Agendamento();
            agendamento.setPaciente(paciente);
            agendamento.setDentista(dentista);
            agendamento.setDataHora(LocalDateTime.parse(dataHora));
            agendamento.setTelefoneWhatsapp(telefone);
            agendamento.setStatus("AGENDADO");
            agendamento.setDuracaoMinutos(30);
            
            agendamento = agendamentoService.salvar(agendamento);
            
            return "redirect:/public/agendamento-confirmado?id=" + agendamento.getId();
        } catch (Exception e) {
            logger.error("Erro ao salvar agendamento", e);
            model.addAttribute("error", "Erro ao processar agendamento");
            return "public/agendamento-online";
        }
    }
    
    /**
     * Confirmação de agendamento
     */
    @GetMapping("/public/agendamento-confirmado")
    public String agendamentoConfirmado(@RequestParam Long id, Model model) {
        logger.info("Exibindo confirmação de agendamento - ID: {}", id);
        
        Optional<Agendamento> agendamentoOpt = agendamentoService.buscarPorId(id);
        if (agendamentoOpt.isPresent()) {
            model.addAttribute("agendamento", agendamentoOpt.get());
            return "public/agendamento-confirmado";
        } else {
            throw new NotFoundException("Agendamento não encontrado com ID: " + id);
        }
    }
    

    
    /**
     * API para obter dentistas disponíveis
     */
    @GetMapping("/api/public/dentistas")
    @ResponseBody
    public Map<String, Object> listarDentistasPublico() {
        logger.info("API: Obtendo lista de dentistas");
        
        List<String> dentistas = agendamentoService.listarDentistas();
        Map<String, Object> response = new HashMap<>();
        response.put("dentistas", dentistas);
        
        return response;
    }
    
    /**
     * API para obter horários disponíveis
     */
    @GetMapping("/api/public/horarios-disponiveis")
    @ResponseBody
    public Map<String, Object> getHorariosDisponiveisPublico(
            @RequestParam String dentista,
            @RequestParam String data) {
        
        logger.info("API: Obtendo horários disponíveis - Dentista: {}, Data: {}", dentista, data);
        
        try {
            if (dentista.isEmpty() || data.isEmpty()) {
                throw new IllegalArgumentException("Parâmetros inválidos");
            }
            
            LocalDateTime dataDateTime = LocalDateTime.parse(data + "T00:00");
            List<String> horarios = agendamentoService.getHorariosDisponiveisPorData(dentista, dataDateTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("horarios", horarios);
            return response;
        } catch (IllegalArgumentException e) {
            throw e; // Will be handled by @ExceptionHandler
        } catch (Exception e) {
            logger.error("Erro ao obter horários disponíveis", e);
            throw new IllegalArgumentException("Erro ao processar data: " + e.getMessage());
        }
    }
    
    /**
     * API para verificar disponibilidade de horário
     */
    @GetMapping("/api/public/verificar-disponibilidade")
    @ResponseBody
    public Map<String, Object> verificarDisponibilidadePublica(
            @RequestParam String dentista,
            @RequestParam String dataHora) {
        
        logger.info("API: Verificando disponibilidade - Dentista: {}, DataHora: {}", dentista, dataHora);
        
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dataHora);
            boolean conflito = agendamentoService.verificarConflitoHorario(dentista, dateTime, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("disponivel", !conflito);
            return response;
        } catch (Exception e) {
            logger.error("Erro ao verificar disponibilidade", e);
            Map<String, Object> response = new HashMap<>();
            response.put("disponivel", false);
            return response;
        }
    }
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public void handleBadRequest() {
        // Return 400 Bad Request
    }
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public void handleNotFound() {
        // Return 404 Not Found with empty body
    }
    
    public static class NotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        
        public NotFoundException(String message) {
            super(message);
        }
    }
}
