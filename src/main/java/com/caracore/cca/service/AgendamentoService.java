package com.caracore.cca.service;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.repository.AgendamentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pela lógica de negócio relacionada aos agendamentos.
 */
@Service
@Transactional
public class AgendamentoService {

    private static final Logger logger = LoggerFactory.getLogger(AgendamentoService.class);

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    /**
     * Lista todos os agendamentos do sistema
     */
    public List<Agendamento> listarTodos() {
        logger.info("Listando todos os agendamentos");
        return agendamentoRepository.findAll();
    }

    /**
     * Busca agendamento por ID
     */
    public Optional<Agendamento> buscarPorId(Long id) {
        logger.info("Buscando agendamento por ID: {}", id);
        return agendamentoRepository.findById(id);
    }

    /**
     * Busca agendamentos por nome do paciente
     */
    public List<Agendamento> buscarPorPaciente(String nomePaciente) {
        logger.info("Buscando agendamentos para o paciente: {}", nomePaciente);
        return agendamentoRepository.findByPacienteContainingIgnoreCase(nomePaciente);
    }

    /**
     * Busca agendamentos por dentista
     */
    public List<Agendamento> buscarPorDentista(String dentista) {
        logger.info("Buscando agendamentos para o dentista: {}", dentista);
        return agendamentoRepository.findByDentistaContainingIgnoreCase(dentista);
    }

    /**
     * Busca agendamentos em um período específico
     */
    public List<Agendamento> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        logger.info("Buscando agendamentos entre {} e {}", inicio, fim);
        return agendamentoRepository.findByDataHoraBetween(inicio, fim);
    }

    /**
     * Busca agendamentos por dentista e período
     */
    public List<Agendamento> buscarPorDentistaEPeriodo(String dentista, LocalDateTime inicio, LocalDateTime fim) {
        logger.info("Buscando agendamentos para {} entre {} e {}", dentista, inicio, fim);
        return agendamentoRepository.findByDentistaAndDataHoraBetween(dentista, inicio, fim);
    }

    /**
     * Salva um novo agendamento
     */
    public Agendamento salvar(Agendamento agendamento) {
        logger.info("Salvando agendamento para paciente: {} com dentista: {} em {}", 
                   agendamento.getPaciente(), agendamento.getDentista(), agendamento.getDataHora());
        
        if (agendamento.getStatus() == null || agendamento.getStatus().isEmpty()) {
            agendamento.setStatus("AGENDADO");
        }
        
        return agendamentoRepository.save(agendamento);
    }

    /**
     * Atualiza um agendamento existente
     */
    public Agendamento atualizar(Agendamento agendamento) {
        logger.info("Atualizando agendamento ID: {} - Status: {}", 
                   agendamento.getId(), agendamento.getStatus());
        return agendamentoRepository.save(agendamento);
    }

    /**
     * Confirma um agendamento (muda status para CONFIRMADO)
     */
    public boolean confirmarAgendamento(Long id) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            agendamento.setStatus("CONFIRMADO");
            agendamentoRepository.save(agendamento);
            logger.info("Agendamento ID {} confirmado", id);
            return true;
        }
        logger.warn("Agendamento ID {} não encontrado para confirmação", id);
        return false;
    }

    /**
     * Cancela um agendamento (muda status para CANCELADO)
     */
    public boolean cancelarAgendamento(Long id, String motivo) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            agendamento.setStatus("CANCELADO");
            if (motivo != null && !motivo.isEmpty()) {
                String observacaoAtual = agendamento.getObservacao() != null ? agendamento.getObservacao() : "";
                agendamento.setObservacao(observacaoAtual + "\nCancelado: " + motivo);
            }
            agendamentoRepository.save(agendamento);
            logger.info("Agendamento ID {} cancelado. Motivo: {}", id, motivo);
            return true;
        }
        logger.warn("Agendamento ID {} não encontrado para cancelamento", id);
        return false;
    }

    /**
     * Marca um agendamento como realizado (muda status para REALIZADO)
     */
    public boolean marcarComoRealizado(Long id) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            agendamento.setStatus("REALIZADO");
            agendamentoRepository.save(agendamento);
            logger.info("Agendamento ID {} marcado como realizado", id);
            return true;
        }
        logger.warn("Agendamento ID {} não encontrado para marcar como realizado", id);
        return false;
    }

    /**
     * Reagenda uma consulta (atualiza data/hora)
     */
    public boolean reagendar(Long id, LocalDateTime novaDataHora) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            LocalDateTime dataAnterior = agendamento.getDataHora();
            agendamento.setDataHora(novaDataHora);
            agendamento.setStatus("REAGENDADO");
            
            String observacaoAtual = agendamento.getObservacao() != null ? agendamento.getObservacao() : "";
            agendamento.setObservacao(observacaoAtual + "\nReagendado de: " + dataAnterior + " para: " + novaDataHora);
            
            agendamentoRepository.save(agendamento);
            logger.info("Agendamento ID {} reagendado de {} para {}", id, dataAnterior, novaDataHora);
            return true;
        }
        logger.warn("Agendamento ID {} não encontrado para reagendamento", id);
        return false;
    }

    /**
     * Verifica se existe conflito de horário para um dentista
     */
    public boolean verificarConflitoHorario(String dentista, LocalDateTime dataHora, Long agendamentoId) {
        LocalDateTime inicio = dataHora.minusMinutes(30);
        LocalDateTime fim = dataHora.plusMinutes(30);
        
        List<Agendamento> agendamentosConflito = agendamentoRepository.findByDentistaAndDataHoraBetween(dentista, inicio, fim);
        
        // Remove o próprio agendamento da verificação (para casos de atualização)
        if (agendamentoId != null) {
            agendamentosConflito.removeIf(a -> a.getId().equals(agendamentoId));
        }
        
        // Filtra apenas agendamentos não cancelados
        agendamentosConflito = agendamentosConflito.stream()
                .filter(a -> !"CANCELADO".equals(a.getStatus()))
                .toList();
        
        boolean temConflito = !agendamentosConflito.isEmpty();
        
        if (temConflito) {
            logger.warn("Conflito de horário detectado para {} em {}", dentista, dataHora);
        }
        
        return temConflito;
    }

    /**
     * Exclui um agendamento (apenas para administradores)
     */
    public boolean excluir(Long id) {
        if (agendamentoRepository.existsById(id)) {
            agendamentoRepository.deleteById(id);
            logger.info("Agendamento ID {} excluído permanentemente", id);
            return true;
        }
        logger.warn("Agendamento ID {} não encontrado para exclusão", id);
        return false;
    }

    /**
     * Conta agendamentos por status
     */
    public long contarPorStatus(String status) {
        return agendamentoRepository.findAll().stream()
                .filter(a -> status.equals(a.getStatus()))
                .count();
    }

    /**
     * Lista agendamentos futuros (útil para dashboard)
     */
    public List<Agendamento> listarProximosAgendamentos() {
        LocalDateTime agora = LocalDateTime.now();
        return agendamentoRepository.findAll().stream()
                .filter(a -> a.getDataHora().isAfter(agora))
                .filter(a -> !"CANCELADO".equals(a.getStatus()))
                .sorted((a1, a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
                .limit(10)
                .toList();
    }

    /**
     * Lista agendamentos por status
     */
    public List<Agendamento> buscarPorStatus(String status) {
        return agendamentoRepository.findAll().stream()
                .filter(a -> status.equals(a.getStatus()))
                .sorted((a1, a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
                .toList();
    }

    /**
     * Lista todos os dentistas com agendamentos
     */
    public List<String> listarDentistas() {
        return agendamentoRepository.findAll().stream()
                .map(Agendamento::getDentista)
                .distinct()
                .filter(d -> d != null && !d.trim().isEmpty())
                .sorted()
                .toList();
    }

    /**
     * Verifica se um horário está disponível para agendamento
     */
    public boolean isHorarioDisponivel(String dentista, LocalDateTime dataHora) {
        // Verifica se não há conflito de horário
        return !verificarConflitoHorario(dentista, dataHora, null);
    }

    /**
     * Gera lista de horários disponíveis para um dentista em uma data específica
     */
    public List<String> getHorariosDisponiveisPorData(String dentista, LocalDateTime data) {
        List<String> horariosDisponiveis = new ArrayList<>();
        
        // Horários padrão de atendimento (8h às 18h, intervalos de 30 minutos)
        LocalDateTime inicioExpediente = data.withHour(8).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimExpediente = data.withHour(18).withMinute(0).withSecond(0).withNano(0);
        
        LocalDateTime horarioAtual = inicioExpediente;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        while (horarioAtual.isBefore(fimExpediente)) {
            // Pula horário de almoço (12h às 13h)
            if (horarioAtual.getHour() == 12) {
                horarioAtual = horarioAtual.plusHours(1);
                continue;
            }
            
            if (isHorarioDisponivel(dentista, horarioAtual)) {
                horariosDisponiveis.add(horarioAtual.format(formatter));
            }
            
            horarioAtual = horarioAtual.plusMinutes(30);
        }
        
        return horariosDisponiveis;
    }

    /**
     * Busca agendamentos por dentista, data e status
     */
    public List<Agendamento> buscarPorDentistaDataStatus(String dentista, LocalDateTime data, String status) {
        LocalDateTime inicioData = data.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimData = data.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        return agendamentoRepository.findAll().stream()
                .filter(a -> dentista == null || dentista.equals(a.getDentista()))
                .filter(a -> status == null || status.equals(a.getStatus()))
                .filter(a -> a.getDataHora().isAfter(inicioData) && a.getDataHora().isBefore(fimData))
                .sorted((a1, a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
                .toList();
    }
}
