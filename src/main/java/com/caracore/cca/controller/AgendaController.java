package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.service.AgendamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador responsável pelas telas de agenda e calendário.
 * 
 * Funcionalidades:
 * - Agenda geral (calendário)
 * - Agenda por profissional
 * - APIs para FullCalendar.js
 */
@Controller
@RequestMapping("/agenda")
public class AgendaController {

    private static final Logger logger = LoggerFactory.getLogger(AgendaController.class);

    @Autowired
    private AgendamentoService agendamentoService;

    /**
     * Página principal da agenda (calendário)
     */
    @GetMapping("/calendario")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String calendario(Model model) {
        logger.info("Acessando página de calendário da agenda");
        return "agenda/calendario";
    }

    /**
     * Agenda por profissional
     */
    @GetMapping("/profissional")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String agendaPorProfissional(Model model,
                                       @RequestParam(required = false) String dentista) {
        logger.info("Acessando agenda por profissional - Dentista: {}", dentista);
        
        // Lista de profissionais disponíveis
        List<String> profissionais = List.of(
            "Dr. Ana Silva - Clínico Geral",
            "Dr. Carlos Oliveira - Ortodontista", 
            "Dra. Mariana Santos - Endodontista",
            "Dr. Roberto Pereira - Periodontista"
        );
        
        model.addAttribute("profissionais", profissionais);
        model.addAttribute("dentistaAtual", dentista != null ? dentista : profissionais.get(0));
        
        return "agenda/profissional";
    }

    /**
     * API para obter eventos do calendário (FullCalendar.js)
     */
    @GetMapping("/api/eventos")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    @ResponseBody
    public List<Map<String, Object>> obterEventosCalendario(@RequestParam(required = false) String start,
                                                           @RequestParam(required = false) String end,
                                                           @RequestParam(required = false) String dentista,
                                                           @RequestParam(required = false) String status) {
        logger.info("API eventos calendário - Start: {}, End: {}, Dentista: {}, Status: {}", 
                   start, end, dentista, status);
        
        try {
            // Buscar agendamentos
            var agendamentos = agendamentoService.listarTodos();
            
            // Aplicar filtros se fornecidos
            if (dentista != null && !dentista.isEmpty()) {
                agendamentos = agendamentos.stream()
                        .filter(a -> dentista.equals(a.getDentista()))
                        .toList();
            }
            
            if (status != null && !status.isEmpty()) {
                agendamentos = agendamentos.stream()
                        .filter(a -> status.equals(a.getStatus()))
                        .toList();
            }
            
            // Converter para formato FullCalendar
            List<Map<String, Object>> eventos = new ArrayList<>();
            
            for (var agendamento : agendamentos) {
                if ("CANCELADO".equals(agendamento.getStatus())) {
                    continue; // Não exibir cancelados no calendário
                }
                
                Map<String, Object> evento = new HashMap<>();
                evento.put("id", agendamento.getId());
                evento.put("title", agendamento.getPaciente() + " - " + agendamento.getDentista().split(" - ")[0]);
                evento.put("start", agendamento.getDataHora().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                evento.put("end", agendamento.getDataHora()
                        .plusMinutes(agendamento.getDuracaoMinutos() != null ? agendamento.getDuracaoMinutos() : 30)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                
                // Propriedades estendidas
                Map<String, Object> extendedProps = new HashMap<>();
                extendedProps.put("paciente", agendamento.getPaciente());
                extendedProps.put("dentista", agendamento.getDentista());
                extendedProps.put("status", agendamento.getStatus());
                extendedProps.put("duracao", agendamento.getDuracaoMinutos());
                extendedProps.put("telefone", agendamento.getTelefoneWhatsapp());
                extendedProps.put("observacao", agendamento.getObservacao());
                
                evento.put("extendedProps", extendedProps);
                
                // Cor baseada no status
                evento.put("backgroundColor", getCorStatus(agendamento.getStatus()));
                evento.put("borderColor", getCorStatus(agendamento.getStatus()));
                
                eventos.add(evento);
            }
            
            logger.info("Retornando {} eventos para o calendário", eventos.size());
            return eventos;
            
        } catch (Exception e) {
            logger.error("Erro ao buscar eventos do calendário", e);
            return new ArrayList<>();
        }
    }

    /**
     * API para agenda de profissional específico
     */
    @GetMapping("/api/profissional")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    @ResponseBody
    public Map<String, Object> obterAgendaProfissional(@RequestParam String dentista,
                                                      @RequestParam(required = false) String inicio,
                                                      @RequestParam(required = false) String fim) {
        logger.info("API agenda profissional - Dentista: {}, Início: {}, Fim: {}", dentista, inicio, fim);
        
        try {
            Map<String, Object> response = new HashMap<>();
            
            // Buscar agendamentos do profissional
            var agendamentos = agendamentoService.buscarPorDentista(dentista);
            
            // Filtrar por período se fornecido
            if (inicio != null && fim != null) {
                LocalDateTime dataInicio = LocalDateTime.parse(inicio + "T00:00:00");
                LocalDateTime dataFim = LocalDateTime.parse(fim + "T23:59:59");
                
                agendamentos = agendamentos.stream()
                        .filter(a -> a.getDataHora().isAfter(dataInicio) && a.getDataHora().isBefore(dataFim))
                        .toList();
            }
            
            // Converter agendamentos
            List<Map<String, Object>> agendamentosJson = new ArrayList<>();
            for (var agendamento : agendamentos) {
                Map<String, Object> agendamentoJson = new HashMap<>();
                agendamentoJson.put("id", agendamento.getId());
                agendamentoJson.put("paciente", agendamento.getPaciente());
                agendamentoJson.put("dentista", agendamento.getDentista());
                agendamentoJson.put("dataHora", agendamento.getDataHora().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                agendamentoJson.put("duracaoMinutos", agendamento.getDuracaoMinutos());
                agendamentoJson.put("status", agendamento.getStatus());
                agendamentoJson.put("observacao", agendamento.getObservacao());
                agendamentoJson.put("telefoneWhatsapp", agendamento.getTelefoneWhatsapp());
                
                agendamentosJson.add(agendamentoJson);
            }
            
            // Estatísticas
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalConsultas", agendamentos.size());
            stats.put("consultasHoje", agendamentos.stream()
                    .filter(a -> a.getDataHora().toLocalDate().equals(LocalDateTime.now().toLocalDate()))
                    .count());
            stats.put("horasDisponiveis", calcularHorasDisponiveis(agendamentos));
            stats.put("taxaOcupacao", calcularTaxaOcupacao(agendamentos));
            
            response.put("agendamentos", agendamentosJson);
            response.put("stats", stats);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Erro ao buscar agenda do profissional: " + dentista, e);
            return Map.of("error", "Erro ao carregar agenda");
        }
    }

    /**
     * API para horários disponíveis de um profissional em uma data
     */
    @GetMapping("/api/horarios-disponiveis")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    @ResponseBody
    public Map<String, Object> obterHorariosDisponiveis(@RequestParam String dentista,
                                                        @RequestParam String data) {
        logger.info("API horários disponíveis - Dentista: {}, Data: {}", dentista, data);
        
        try {
            Map<String, Object> response = new HashMap<>();
            
            // Gerar horários disponíveis (8h às 18h, intervalos de 30min)
            List<String> horariosDisponiveis = new ArrayList<>();
            
            for (int hora = 8; hora < 18; hora++) {
                horariosDisponiveis.add(String.format("%02d:00", hora));
                horariosDisponiveis.add(String.format("%02d:30", hora));
            }
            
            // Buscar agendamentos existentes na data
            LocalDateTime dataInicio = LocalDateTime.parse(data + "T00:00:00");
            LocalDateTime dataFim = dataInicio.plusDays(1).minusSeconds(1);
            
            var agendamentosData = agendamentoService.buscarPorPeriodo(dataInicio, dataFim)
                    .stream()
                    .filter(a -> dentista.equals(a.getDentista()))
                    .filter(a -> !"CANCELADO".equals(a.getStatus()))
                    .toList();
            
            // Remover horários ocupados
            for (var agendamento : agendamentosData) {
                String horario = agendamento.getDataHora().format(DateTimeFormatter.ofPattern("HH:mm"));
                horariosDisponiveis.remove(horario);
            }
            
            response.put("horarios", horariosDisponiveis);
            response.put("data", data);
            response.put("dentista", dentista);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Erro ao buscar horários disponíveis", e);
            return Map.of("error", "Erro ao carregar horários");
        }
    }

    /**
     * Redirecionar para agenda padrão
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String agenda() {
        return "redirect:/agenda/calendario";
    }

    // Métodos auxiliares

    private String getCorStatus(String status) {
        return switch (status) {
            case "AGENDADO" -> "#3788d8";
            case "CONFIRMADO" -> "#28a745";
            case "REAGENDADO" -> "#fd7e14";
            case "REALIZADO" -> "#6f42c1";
            case "CANCELADO" -> "#dc3545";
            case "NAO_COMPARECEU" -> "#6c757d";
            default -> "#dee2e6";
        };
    }

    private int calcularHorasDisponiveis(List<Agendamento> agendamentos) {
        // Simplificado: 8h por dia útil menos agendamentos
        int horasPorDia = 8;
        int diasUteis = 5;
        int totalHoras = horasPorDia * diasUteis;
        int horasOcupadas = agendamentos.size(); // Simplificado
        
        return Math.max(0, totalHoras - horasOcupadas);
    }

    private int calcularTaxaOcupacao(List<Agendamento> agendamentos) {
        int horasDisponiveis = calcularHorasDisponiveis(agendamentos);
        int horasOcupadas = agendamentos.size();
        int totalHoras = horasDisponiveis + horasOcupadas;
        
        return totalHoras > 0 ? (horasOcupadas * 100) / totalHoras : 0;
    }
}
