package com.caracore.cca.service;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.model.Dentista;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.repository.AgendamentoRepository;
import com.caracore.cca.repository.DentistaRepository;
import com.caracore.cca.repository.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private DentistaRepository dentistaRepository;

    /**
     * Gera relatório de agendamentos
     */
    public Map<String, Object> gerarRelatorioAgendamentos(
            LocalDate dataInicio, LocalDate dataFim, String tipoStatus, String dentistaNome) {
        
        logger.info("Gerando dados para relatório de agendamentos de {} até {}", dataInicio, dataFim);
        
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
        
        List<Agendamento> agendamentos = agendamentoRepository.findByDataHoraBetween(inicio, fim);
        
        // Filtrar por status se fornecido
        if (tipoStatus != null && !tipoStatus.isEmpty()) {
            agendamentos = agendamentos.stream()
                    .filter(a -> tipoStatus.equals(a.getStatus()))
                    .collect(Collectors.toList());
        }
        
        // Filtrar por dentista se fornecido
        if (dentistaNome != null && !dentistaNome.isEmpty()) {
            agendamentos = agendamentos.stream()
                    .filter(a -> a.getDentista().contains(dentistaNome))
                    .collect(Collectors.toList());
        }
        
        // Calcular resumo por status
        Map<String, Long> statusCounts = agendamentos.stream()
                .collect(Collectors.groupingBy(Agendamento::getStatus, Collectors.counting()));
                
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("agendamentos", agendamentos);
        resultado.put("resumoStatus", statusCounts);
        resultado.put("totalAgendamentos", agendamentos.size());
        
        return resultado;
    }
    
    /**
     * Gera relatório de pacientes
     */
    public Map<String, Object> gerarRelatorioPacientes(LocalDate dataInicio, LocalDate dataFim) {
        logger.info("Gerando dados para relatório de pacientes de {} até {}", dataInicio, dataFim);
        
        // Neste exemplo, buscamos todos os pacientes
        // Em um sistema real, usaríamos um repositório com query específica para filtrar por data
        List<Paciente> pacientes = pacienteRepository.findAll();
        
        // Estatísticas básicas
        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("totalPacientes", pacientes.size());
        estatisticas.put("comConsentimento", pacientes.stream()
                .filter(p -> p.getConsentimentoConfirmado() != null && p.getConsentimentoConfirmado())
                .count());
        estatisticas.put("comEmail", pacientes.stream()
                .filter(p -> p.getEmail() != null && !p.getEmail().isEmpty())
                .count());
        estatisticas.put("comTelefone", pacientes.stream()
                .filter(p -> p.getTelefone() != null && !p.getTelefone().isEmpty())
                .count());
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("pacientes", pacientes);
        resultado.put("estatisticas", estatisticas);
        
        return resultado;
    }
    
    /**
     * Gera relatório de desempenho por dentista
     */
    public Map<String, Object> gerarRelatorioDesempenho(LocalDate dataInicio, LocalDate dataFim) {
        logger.info("Gerando dados para relatório de desempenho de {} até {}", dataInicio, dataFim);
        
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
        
        // Buscar todos os dentistas ativos
        List<Dentista> dentistas = dentistaRepository.findByAtivoTrue();
        List<Map<String, Object>> resumoDentistas = new ArrayList<>();
        
        // Para cada dentista, buscar agendamentos e calcular estatísticas
        for (Dentista dentista : dentistas) {
            String nomeDentista = dentista.getNome();
            
            // Em um sistema real, usaríamos uma query mais eficiente
            List<Agendamento> agendamentos = agendamentoRepository.findByDataHoraBetween(inicio, fim).stream()
                    .filter(a -> a.getDentista().contains(nomeDentista))
                    .collect(Collectors.toList());
            
            // Contagem por status
            long realizados = agendamentos.stream()
                    .filter(a -> "REALIZADO".equals(a.getStatus()))
                    .count();
            
            long cancelados = agendamentos.stream()
                    .filter(a -> "CANCELADO".equals(a.getStatus()))
                    .count();
            
            long pendentes = agendamentos.stream()
                    .filter(a -> "AGENDADO".equals(a.getStatus()) || "CONFIRMADO".equals(a.getStatus()))
                    .count();
            
            // Calcular taxa de realização
            double taxaRealizacao = agendamentos.isEmpty() ? 0 : (double) realizados / agendamentos.size() * 100;
            
            Map<String, Object> resumoDentista = new HashMap<>();
            resumoDentista.put("dentista", dentista);
            resumoDentista.put("totalAgendamentos", agendamentos.size());
            resumoDentista.put("realizados", realizados);
            resumoDentista.put("cancelados", cancelados);
            resumoDentista.put("pendentes", pendentes);
            resumoDentista.put("taxaRealizacao", taxaRealizacao);
            
            resumoDentistas.add(resumoDentista);
        }
        
        // Ordenar por taxa de realização (do maior para o menor)
        resumoDentistas.sort((d1, d2) -> {
            Double taxa1 = (Double) d1.get("taxaRealizacao");
            Double taxa2 = (Double) d2.get("taxaRealizacao");
            return taxa2.compareTo(taxa1); // ordem decrescente
        });
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("dentistas", dentistas);
        resultado.put("resumoDesempenho", resumoDentistas);
        
        return resultado;
    }
    
    /**
     * Gera arquivo CSV com dados de agendamentos
     */
}
