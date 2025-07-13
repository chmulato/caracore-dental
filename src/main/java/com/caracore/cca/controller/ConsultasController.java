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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador responsável pela gestão de consultas agendadas.
 * 
 * Níveis de acesso:
 * - ADMIN: acesso completo (visualizar, criar, editar, cancelar, excluir)
 * - RECEPTIONIST: pode visualizar, criar, editar e cancelar agendamentos
 * - DENTIST: pode visualizar agendamentos e marcar como realizado
 */
@Controller
@RequestMapping("/consultas")
public class ConsultasController {

    private static final Logger logger = LoggerFactory.getLogger(ConsultasController.class);

    @Autowired
    private AgendamentoService agendamentoService;

    /**
     * Lista todas as consultas agendadas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String listarConsultas(Model model,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String dataInicio,
                                 @RequestParam(required = false) String dataFim,
                                 @RequestParam(required = false) String dentista) {
        logger.info("Listando consultas - Status: {}, Data Início: {}, Data Fim: {}, Dentista: {}", 
                   status, dataInicio, dataFim, dentista);
                   
        // Adiciona o activeLink para destacar o item correto no menu lateral
        model.addAttribute("activeLink", "consultas");
        
        List<Agendamento> agendamentos;
        
        // Aplicar filtros
        if (status != null && !status.isEmpty()) {
            agendamentos = agendamentoService.listarTodos().stream()
                    .filter(a -> status.equals(a.getStatus()))
                    .toList();
        } else if (dataInicio != null && dataFim != null && !dataInicio.isEmpty() && !dataFim.isEmpty()) {
            LocalDateTime inicio = LocalDateTime.parse(dataInicio + "T00:00:00");
            LocalDateTime fim = LocalDateTime.parse(dataFim + "T23:59:59");
            agendamentos = agendamentoService.buscarPorPeriodo(inicio, fim);
        } else if (dentista != null && !dentista.isEmpty()) {
            agendamentos = agendamentoService.buscarPorDentista(dentista);
        } else {
            agendamentos = agendamentoService.listarTodos();
        }
        
        // Organizar agendamentos por status para melhor visualização
        Map<String, List<Agendamento>> agendamentosPorStatus = new HashMap<>();
        agendamentosPorStatus.put("AGENDADO", agendamentos.stream()
                .filter(a -> "AGENDADO".equals(a.getStatus()))
                .sorted((a1, a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
                .toList());
        agendamentosPorStatus.put("CONFIRMADO", agendamentos.stream()
                .filter(a -> "CONFIRMADO".equals(a.getStatus()))
                .sorted((a1, a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
                .toList());
        agendamentosPorStatus.put("REAGENDADO", agendamentos.stream()
                .filter(a -> "REAGENDADO".equals(a.getStatus()))
                .sorted((a1, a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
                .toList());
        agendamentosPorStatus.put("REALIZADO", agendamentos.stream()
                .filter(a -> "REALIZADO".equals(a.getStatus()))
                .sorted((a1, a2) -> a2.getDataHora().compareTo(a1.getDataHora())) // Mais recentes primeiro
                .toList());
        agendamentosPorStatus.put("CANCELADO", agendamentos.stream()
                .filter(a -> "CANCELADO".equals(a.getStatus()))
                .sorted((a1, a2) -> a2.getDataHora().compareTo(a1.getDataHora()))
                .toList());
        
        // Consultas de hoje
        LocalDateTime inicioHoje = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fimHoje = inicioHoje.plusDays(1).minusSeconds(1);
        List<Agendamento> consultasHoje = agendamentos.stream()
                .filter(a -> a.getDataHora().isAfter(inicioHoje) && 
                           a.getDataHora().isBefore(fimHoje))
                .sorted((a1, a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
                .toList();
        
        model.addAttribute("agendamentos", agendamentos);
        model.addAttribute("agendamentosPorStatus", agendamentosPorStatus);
        model.addAttribute("consultasHoje", consultasHoje);
        
        return "consultas/lista";
    }

    /**
     * Exibe detalhes de uma consulta específica
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String detalhesConsulta(@PathVariable Long id, Model model) {
        logger.info("Exibindo detalhes da consulta ID: {}", id);
        Optional<Agendamento> agendamento = agendamentoService.buscarPorId(id);
        
        if (agendamento.isPresent()) {
            model.addAttribute("agendamento", agendamento.get());
            return "consultas/detalhes";
        } else {
            logger.warn("Consulta com ID {} não encontrada", id);
            return "redirect:/consultas?erro=consulta-nao-encontrada";
        }
    }

    /**
     * Busca consultas por diferentes critérios
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String buscarConsultas(@RequestParam(required = false) String termo,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String dentista,
                                 Model model) {
        logger.info("Buscando consultas - Termo: {}, Status: {}, Dentista: {}", termo, status, dentista);
        
        List<Agendamento> agendamentos;
        
        if (termo != null && !termo.trim().isEmpty()) {
            // Busca geral por termo
            agendamentos = agendamentoService.listarTodos().stream()
                    .filter(a -> a.getPaciente().toLowerCase().contains(termo.toLowerCase()) ||
                               a.getDentista().toLowerCase().contains(termo.toLowerCase()) ||
                               (a.getObservacao() != null && a.getObservacao().toLowerCase().contains(termo.toLowerCase())))
                    .toList();
        } else if (status != null && !status.isEmpty()) {
            // Busca por status
            agendamentos = agendamentoService.listarTodos().stream()
                    .filter(a -> status.equals(a.getStatus()))
                    .toList();
        } else if (dentista != null && !dentista.isEmpty()) {
            // Busca por dentista
            agendamentos = agendamentoService.buscarPorDentista(dentista);
        } else {
            // Sem filtros - listar todos
            agendamentos = agendamentoService.listarTodos();
        }
        
        model.addAttribute("agendamentos", agendamentos);
        model.addAttribute("termoBusca", termo);
        model.addAttribute("statusFiltro", status);
        model.addAttribute("dentistaFiltro", dentista);
        
        return "consultas/lista";
    }

    /**
     * Confirma uma consulta (muda status para CONFIRMADO)
     */
    @PostMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public String confirmarConsulta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Confirmando consulta ID: {}", id);
        
        if (agendamentoService.confirmarAgendamento(id)) {
            redirectAttributes.addFlashAttribute("sucesso", "Consulta confirmada com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Erro ao confirmar consulta. Consulta não encontrada.");
        }
        
        return "redirect:/consultas/" + id;
    }

    /**
     * Cancela uma consulta
     */
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public String cancelarConsulta(@PathVariable Long id, 
                                  @RequestParam(required = false) String motivo,
                                  RedirectAttributes redirectAttributes) {
        logger.info("Cancelando consulta ID: {} - Motivo: {}", id, motivo);
        
        if (agendamentoService.cancelarAgendamento(id, motivo)) {
            redirectAttributes.addFlashAttribute("sucesso", "Consulta cancelada com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cancelar consulta. Consulta não encontrada.");
        }
        
        return "redirect:/consultas/" + id;
    }

    /**
     * Marca consulta como realizada
     */
    @PostMapping("/{id}/realizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String realizarConsulta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Marcando consulta ID: {} como realizada", id);
        
        if (agendamentoService.marcarComoRealizado(id)) {
            redirectAttributes.addFlashAttribute("sucesso", "Consulta marcada como realizada!");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Erro ao marcar consulta como realizada. Consulta não encontrada.");
        }
        
        return "redirect:/consultas/" + id;
    }

    /**
     * Reagenda uma consulta
     */
    @PostMapping("/{id}/reagendar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public String reagendarConsulta(@PathVariable Long id, 
                                   @RequestParam String novaDataHora,
                                   RedirectAttributes redirectAttributes) {
        logger.info("Reagendando consulta ID: {} para nova data: {}", id, novaDataHora);
        
        try {
            LocalDateTime novaData = LocalDateTime.parse(novaDataHora);
            
            if (agendamentoService.reagendar(id, novaData)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                redirectAttributes.addFlashAttribute("sucesso", 
                    "Consulta reagendada com sucesso para " + novaData.format(formatter));
            } else {
                redirectAttributes.addFlashAttribute("erro", "Erro ao reagendar consulta. Consulta não encontrada.");
            }
        } catch (Exception e) {
            logger.error("Erro ao processar data de reagendamento: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("erro", "Formato de data/hora inválido.");
        }
        
        return "redirect:/consultas/" + id;
    }

    /**
     * Exclui uma consulta (apenas administradores)
     */
    @PostMapping("/{id}/excluir")
    @PreAuthorize("hasRole('ADMIN')")
    public String excluirConsulta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Excluindo consulta ID: {}", id);
        
        if (agendamentoService.excluir(id)) {
            redirectAttributes.addFlashAttribute("sucesso", "Consulta excluída permanentemente!");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir consulta. Consulta não encontrada.");
        }
        
        return "redirect:/consultas";
    }

    /**
     * API para verificar conflitos de horário
     */
    @GetMapping("/api/verificar-conflito")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @ResponseBody
    public Map<String, Object> verificarConflito(@RequestParam String dentista,
                                                @RequestParam String dataHora,
                                                @RequestParam(required = false) Long agendamentoId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDateTime data = LocalDateTime.parse(dataHora);
            boolean temConflito = agendamentoService.verificarConflitoHorario(dentista, data, agendamentoId);
            
            response.put("temConflito", temConflito);
            if (temConflito) {
                response.put("mensagem", "Já existe um agendamento para este dentista neste horário");
            } else {
                response.put("mensagem", "Horário disponível");
            }
            
        } catch (Exception e) {
            response.put("erro", true);
            response.put("mensagem", "Erro ao verificar conflito: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * Dashboard com estatísticas das consultas
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String dashboard(Model model) {
        logger.info("Exibindo dashboard de consultas");
        
        List<Agendamento> todosAgendamentos = agendamentoService.listarTodos();
        
        // Estatísticas gerais
        long totalConsultas = todosAgendamentos.size();
        long consultasAgendadas = todosAgendamentos.stream().filter(a -> "AGENDADO".equals(a.getStatus())).count();
        long consultasConfirmadas = todosAgendamentos.stream().filter(a -> "CONFIRMADO".equals(a.getStatus())).count();
        long consultasRealizadas = todosAgendamentos.stream().filter(a -> "REALIZADO".equals(a.getStatus())).count();
        long consultasCanceladas = todosAgendamentos.stream().filter(a -> "CANCELADO".equals(a.getStatus())).count();
        long consultasReagendadas = todosAgendamentos.stream().filter(a -> "REAGENDADO".equals(a.getStatus())).count();
        long consultasFalta = todosAgendamentos.stream().filter(a -> "FALTA".equals(a.getStatus())).count();
        
        // Consultas de hoje
        LocalDateTime inicioHoje = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fimHoje = inicioHoje.plusDays(1).minusSeconds(1);
        List<Agendamento> consultasHojeLista = todosAgendamentos.stream()
                .filter(a -> a.getDataHora().isAfter(inicioHoje) && 
                           a.getDataHora().isBefore(fimHoje))
                .sorted((a1, a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
                .toList();
        
        // Próximas consultas (próximos 7 dias)
        LocalDateTime inicioSemana = LocalDateTime.now();
        LocalDateTime fimSemana = inicioSemana.plusDays(7);
        List<Agendamento> proximasConsultas = todosAgendamentos.stream()
                .filter(a -> a.getDataHora().isAfter(inicioSemana) && 
                           a.getDataHora().isBefore(fimSemana))
                .filter(a -> !"CANCELADO".equals(a.getStatus()))
                .sorted((a1, a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
                .toList();
        
        // Taxa de confirmação
        double taxaConfirmacao = totalConsultas > 0 ? 
            (double) consultasConfirmadas / (consultasAgendadas + consultasConfirmadas) * 100 : 0;
        
        model.addAttribute("totalConsultas", totalConsultas);
        model.addAttribute("consultasAgendadas", consultasAgendadas);
        model.addAttribute("consultasConfirmadas", consultasConfirmadas);
        model.addAttribute("consultasRealizadas", consultasRealizadas);
        model.addAttribute("consultasCanceladas", consultasCanceladas);
        model.addAttribute("consultasReagendadas", consultasReagendadas);
        model.addAttribute("consultasFalta", consultasFalta);
        model.addAttribute("consultasHoje", consultasHojeLista.size());
        model.addAttribute("consultasHojeLista", consultasHojeLista);
        model.addAttribute("proximasConsultas", proximasConsultas);
        model.addAttribute("taxaConfirmacao", Math.round(taxaConfirmacao));
        model.addAttribute("consultasEstaSemana", proximasConsultas.size());
        
        return "consultas/dashboard";
    }

    /**
     * Exibe formulário para reagendar consulta
     */
    @GetMapping("/{id}/reagendar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public String formularioReagendamento(@PathVariable Long id, Model model) {
        logger.info("Exibindo formulário de reagendamento para consulta ID: {}", id);
        
        Optional<Agendamento> agendamento = agendamentoService.buscarPorId(id);
        if (agendamento.isPresent()) {
            model.addAttribute("agendamento", agendamento.get());
            return "consultas/reagendar";
        } else {
            logger.warn("Consulta com ID {} não encontrada para reagendamento", id);
            return "redirect:/consultas?erro=consulta-nao-encontrada";
        }
    }

    /**
     * API para verificar conflitos de horário
     */
    @GetMapping("/verificar-conflito")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    @ResponseBody
    public Map<String, Object> verificarConflito(@RequestParam("dataHora") String dataHora,
                                                 @RequestParam("duracao") Integer duracao,
                                                 @RequestParam("dentistaId") Long dentistaId,
                                                 @RequestParam(value = "agendamentoId", required = false) Long agendamentoId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDateTime dataHoraObj = LocalDateTime.parse(dataHora);
            // Verificação simples - buscar agendamentos no mesmo horário
            List<Agendamento> agendamentos = agendamentoService.buscarPorDentistaEPeriodo(
                dentistaId.toString(), dataHoraObj, dataHoraObj.plusMinutes(duracao));
            
            boolean temConflito = agendamentos.stream()
                    .anyMatch(a -> !a.getId().equals(agendamentoId) && 
                                 !"CANCELADO".equals(a.getStatus()));
            
            response.put("conflito", temConflito);
            if (temConflito) {
                response.put("mensagem", "Já existe uma consulta agendada neste horário para este dentista");
            } else {
                response.put("mensagem", "Horário disponível");
            }
            
        } catch (Exception e) {
            response.put("erro", true);
            response.put("mensagem", "Erro ao verificar conflito: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * API para obter horários disponíveis
     */
    @GetMapping("/horarios-disponiveis")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @ResponseBody
    public Map<String, Object> obterHorariosDisponiveis(@RequestParam("data") String data,
                                                        @RequestParam("dentistaId") Long dentistaId,
                                                        @RequestParam(value = "agendamentoId", required = false) Long agendamentoId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Horários padrão (das 8h às 18h, intervalos de 30 minutos)
            List<String> horariosDisponiveis = new ArrayList<>();
            for (int hora = 8; hora < 18; hora++) {
                for (int minuto = 0; minuto < 60; minuto += 30) {
                    String horario = String.format("%02d:%02d", hora, minuto);
                    LocalDateTime dataHoraObj = LocalDateTime.parse(data + "T" + horario);
                    
                    // Verificação simples - buscar agendamentos no horário
                    List<Agendamento> agendamentos = agendamentoService.buscarPorDentistaEPeriodo(
                        dentistaId.toString(), dataHoraObj, dataHoraObj.plusMinutes(30));
                    
                    boolean disponivel = agendamentos.stream()
                            .noneMatch(a -> !a.getId().equals(agendamentoId) && 
                                          !"CANCELADO".equals(a.getStatus()));
                    
                    if (disponivel) {
                        horariosDisponiveis.add(horario);
                    }
                }
            }
            
            response.put("horarios", horariosDisponiveis);
            
        } catch (Exception e) {
            response.put("erro", true);
            response.put("mensagem", "Erro ao buscar horários disponíveis: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * API para obter dados de agendamentos (para calendário)
     */
    @GetMapping("/api/eventos")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    @ResponseBody
    public List<Map<String, Object>> obterEventosCalendario() {
        List<Agendamento> agendamentos = agendamentoService.listarTodos();
        
        return agendamentos.stream()
                .filter(a -> !"CANCELADO".equals(a.getStatus()))
                .map(a -> {
                    Map<String, Object> evento = new HashMap<>();
                    evento.put("id", a.getId());
                    evento.put("title", a.getPaciente() + " - " + a.getDentista());
                    evento.put("start", a.getDataHora().toString());
                    evento.put("end", a.getDataHora().plusMinutes(a.getDuracaoMinutos()).toString());
                    evento.put("backgroundColor", getCorStatus(a.getStatus()));
                    evento.put("borderColor", getCorStatus(a.getStatus()));
                    evento.put("status", a.getStatus());
                    return evento;
                })
                .toList();
    }

    private String getCorStatus(String status) {
        return switch (status) {
            case "AGENDADO" -> "#007bff";    // Azul
            case "CONFIRMADO" -> "#28a745";  // Verde
            case "REAGENDADO" -> "#ffc107";  // Amarelo
            case "REALIZADO" -> "#17a2b8";   // Azul claro
            case "CANCELADO" -> "#dc3545";   // Vermelho
            default -> "#6c757d";            // Cinza
        };
    }
}
