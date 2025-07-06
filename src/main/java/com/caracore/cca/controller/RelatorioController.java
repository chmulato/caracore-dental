package com.caracore.cca.controller;

import com.caracore.cca.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;

/**
 * Controlador para gerenciamento de relatórios
 */
@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioController.class);

    @Autowired
    private RelatorioService relatorioService;

    /**
     * Página principal de relatórios
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String paginaRelatorios(Model model) {
        logger.info("Acessando página de relatórios");
        model.addAttribute("dataInicio", LocalDate.now().minusMonths(1));
        model.addAttribute("dataFim", LocalDate.now());
        return "relatorios/index";
    }

    /**
     * Relatório de Agendamentos
     */
    @GetMapping("/agendamentos")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'RECEPTIONIST')")
    public String relatorioAgendamentos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String tipoStatus,
            @RequestParam(required = false) String dentista,
            Model model) {
        
        logger.info("Gerando relatório de agendamentos: {} até {}, status: {}, dentista: {}", 
                dataInicio, dataFim, tipoStatus, dentista);
        
        Map<String, Object> dadosRelatorio = relatorioService.gerarRelatorioAgendamentos(
                dataInicio, dataFim, tipoStatus, dentista);
        
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("tipoStatus", tipoStatus);
        model.addAttribute("dentista", dentista);
        model.addAttribute("agendamentos", dadosRelatorio.get("agendamentos"));
        model.addAttribute("resumoStatus", dadosRelatorio.get("resumoStatus"));
        model.addAttribute("totalAgendamentos", dadosRelatorio.get("totalAgendamentos"));
        
        return "relatorios/agendamentos";
    }

    /**
     * Relatório de Pacientes
     */
    @GetMapping("/pacientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public String relatorioPacientes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Model model) {
        
        if (dataInicio == null) dataInicio = LocalDate.now().minusMonths(3);
        if (dataFim == null) dataFim = LocalDate.now();
        
        logger.info("Gerando relatório de pacientes: {} até {}", dataInicio, dataFim);
        
        Map<String, Object> dadosRelatorio = relatorioService.gerarRelatorioPacientes(dataInicio, dataFim);
        
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("pacientes", dadosRelatorio.get("pacientes"));
        model.addAttribute("estatisticas", dadosRelatorio.get("estatisticas"));
        
        return "relatorios/pacientes";
    }

    // Os métodos de exportação para CSV e PDF foram removidos em favor da
    // geração de PDF e CSV diretamente no lado do cliente usando html2pdf.js

    /**
     * Relatório de desempenho por dentista
     */
    @GetMapping("/desempenho")
    @PreAuthorize("hasRole('ADMIN')")
    public String relatorioDesempenho(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Model model) {
        
        if (dataInicio == null) dataInicio = LocalDate.now().minusMonths(1);
        if (dataFim == null) dataFim = LocalDate.now();
        
        logger.info("Gerando relatório de desempenho: {} até {}", dataInicio, dataFim);
        
        Map<String, Object> dadosRelatorio = relatorioService.gerarRelatorioDesempenho(dataInicio, dataFim);
        
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("dentistas", dadosRelatorio.get("dentistas"));
        model.addAttribute("resumoDesempenho", dadosRelatorio.get("resumoDesempenho"));
        
        return "relatorios/desempenho";
    }
}
