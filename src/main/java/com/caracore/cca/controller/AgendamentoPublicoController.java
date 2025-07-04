package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.service.AgendamentoService;
import com.caracore.cca.service.PacienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Controlador responsável pelo agendamento online público.
 * 
 * Este controller não requer autenticação, permitindo que pacientes
 * agendem consultas diretamente pelo site.
 */
@Controller
@RequestMapping("/public")
public class AgendamentoPublicoController {

    private static final Logger logger = LoggerFactory.getLogger(AgendamentoPublicoController.class);

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private PacienteService pacienteService;

    // Lista de profissionais para agendamento público
    private static final List<Map<String, Object>> PROFISSIONAIS = List.of(
        Map.of("id", "1", "nome", "Dr. Ana Silva", "especialidade", "Clínico Geral", "disponivel", true),
        Map.of("id", "2", "nome", "Dr. Carlos Oliveira", "especialidade", "Ortodontista", "disponivel", true),
        Map.of("id", "3", "nome", "Dra. Mariana Santos", "especialidade", "Endodontista", "disponivel", true),
        Map.of("id", "4", "nome", "Dr. Roberto Pereira", "especialidade", "Periodontista", "disponivel", true)
    );

    // Lista de serviços disponíveis
    private static final List<Map<String, Object>> SERVICOS = List.of(
        Map.of("id", "1", "nome", "Consulta de Rotina", "descricao", "Avaliação geral e limpeza", "preco", "R$ 150,00", "duracao", 60),
        Map.of("id", "2", "nome", "Restauração", "descricao", "Tratamento de cáries", "preco", "R$ 250,00", "duracao", 90),
        Map.of("id", "3", "nome", "Canal", "descricao", "Tratamento endodôntico", "preco", "R$ 800,00", "duracao", 120),
        Map.of("id", "4", "nome", "Aparelho Ortodôntico", "descricao", "Avaliação e colocação", "preco", "R$ 3.500,00", "duracao", 90)
    );

    /**
     * Página principal de agendamento online
     */
    @GetMapping("/agendar")
    public String agendamentoOnline(Model model) {
        logger.info("Acessando página de agendamento online público");
        
        model.addAttribute("profissionais", PROFISSIONAIS);
        model.addAttribute("servicos", SERVICOS);
        
        return "public/agendamento-online";
    }

    /**
     * API para obter profissionais disponíveis
     */
    @GetMapping("/api/profissionais")
    @ResponseBody
    public List<Map<String, Object>> obterProfissionais() {
        logger.info("API: Obtendo lista de profissionais");
        return PROFISSIONAIS;
    }

    /**
     * API para obter serviços de um profissional
     */
    @GetMapping("/api/profissionais/{id}/servicos")
    @ResponseBody
    public List<Map<String, Object>> obterServicosProfissional(@PathVariable String id) {
        logger.info("API: Obtendo serviços do profissional ID: {}", id);
        
        // Por enquanto, todos os profissionais oferecem todos os serviços
        // Em uma implementação real, isso seria filtrado por especialidade
        return SERVICOS;
    }

    /**
     * API para obter horários disponíveis
     */
    @GetMapping("/api/horarios-disponiveis")
    @ResponseBody
    public Map<String, Object> obterHorariosDisponiveis(@RequestParam String profissionalId,
                                                        @RequestParam String servicoId,
                                                        @RequestParam String data) {
        logger.info("API: Obtendo horários disponíveis - Profissional: {}, Serviço: {}, Data: {}", 
                   profissionalId, servicoId, data);
        
        try {
            Map<String, Object> response = new HashMap<>();
            
            // Encontrar profissional
            String nomeProfissional = PROFISSIONAIS.stream()
                    .filter(p -> profissionalId.equals(p.get("id")))
                    .map(p -> p.get("nome") + " - " + p.get("especialidade"))
                    .findFirst()
                    .orElse("Profissional não encontrado");
            
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
                    .filter(a -> nomeProfissional.equals(a.getDentista()))
                    .filter(a -> !"CANCELADO".equals(a.getStatus()))
                    .toList();
            
            // Remover horários ocupados
            for (var agendamento : agendamentosData) {
                String horario = agendamento.getDataHora().format(DateTimeFormatter.ofPattern("HH:mm"));
                horariosDisponiveis.remove(horario);
                
                // Remover também horários seguintes baseado na duração
                int duracao = agendamento.getDuracaoMinutos() != null ? agendamento.getDuracaoMinutos() : 30;
                for (int i = 30; i < duracao; i += 30) {
                    String horarioSeguinte = agendamento.getDataHora()
                            .plusMinutes(i)
                            .format(DateTimeFormatter.ofPattern("HH:mm"));
                    horariosDisponiveis.remove(horarioSeguinte);
                }
            }
            
            response.put("horarios", horariosDisponiveis);
            response.put("data", data);
            response.put("profissional", nomeProfissional);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Erro ao buscar horários disponíveis", e);
            return Map.of("error", "Erro ao carregar horários disponíveis");
        }
    }

    /**
     * API para confirmar agendamento
     */
    @PostMapping("/api/confirmar-agendamento")
    @ResponseBody
    public Map<String, Object> confirmarAgendamento(@RequestBody Map<String, Object> dadosAgendamento) {
        logger.info("API: Confirmando agendamento público - Dados: {}", dadosAgendamento);
        
        try {
            // Extrair dados do agendamento
            String profissionalId = (String) dadosAgendamento.get("profissionalId");
            String servicoId = (String) dadosAgendamento.get("servicoId");
            String data = (String) dadosAgendamento.get("data");
            String horario = (String) dadosAgendamento.get("horario");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> dadosPaciente = (Map<String, Object>) dadosAgendamento.get("paciente");
            
            // Encontrar profissional
            String nomeProfissional = PROFISSIONAIS.stream()
                    .filter(p -> profissionalId.equals(p.get("id")))
                    .map(p -> p.get("nome") + " - " + p.get("especialidade"))
                    .findFirst()
                    .orElse("Profissional não encontrado");
            
            // Encontrar serviço
            Map<String, Object> servico = SERVICOS.stream()
                    .filter(s -> servicoId.equals(s.get("id")))
                    .findFirst()
                    .orElse(null);
            
            if (servico == null) {
                return Map.of("success", false, "error", "Serviço não encontrado");
            }
            
            // Criar/atualizar paciente
            String nomePaciente = (String) dadosPaciente.get("nome");
            String emailPaciente = (String) dadosPaciente.get("email");
            String telefonePaciente = (String) dadosPaciente.get("whatsapp");
            
            Paciente paciente = null;
            
            // Buscar paciente existente por email ou telefone
            List<Paciente> pacientesExistentes = pacienteService.buscarPorNome(nomePaciente);
            if (!pacientesExistentes.isEmpty()) {
                paciente = pacientesExistentes.get(0);
                // Atualizar dados
                paciente.setEmail(emailPaciente);
                paciente.setTelefone(telefonePaciente);
            } else {
                // Criar novo paciente
                paciente = new Paciente();
                paciente.setNome(nomePaciente);
                paciente.setEmail(emailPaciente);
                paciente.setTelefone(telefonePaciente);
                
            }
            
            pacienteService.salvar(paciente);
            logger.info("Paciente salvo/atualizado: {}", nomePaciente);
            
            // Criar agendamento
            LocalDateTime dataHoraAgendamento = LocalDateTime.parse(data + "T" + horario + ":00");
            
            Agendamento agendamento = new Agendamento();
            agendamento.setPaciente(nomePaciente);
            agendamento.setDentista(nomeProfissional);
            agendamento.setDataHora(dataHoraAgendamento);
            agendamento.setObservacao("Agendamento online - " + servico.get("nome") + 
                                    (dadosPaciente.containsKey("observacoes") ? 
                                     "\n\nObservações: " + dadosPaciente.get("observacoes") : ""));
            agendamento.setStatus("AGENDADO");
            agendamento.setDuracaoMinutos((Integer) servico.get("duracao"));
            agendamento.setTelefoneWhatsapp(telefonePaciente);
            
            agendamentoService.salvar(agendamento);
            logger.info("Agendamento público criado com sucesso - ID: {}", agendamento.getId());
            
            // Montar resposta de sucesso
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("agendamentoId", agendamento.getId());
            response.put("mensagem", "Agendamento confirmado com sucesso!");
            
            Map<String, Object> resumo = new HashMap<>();
            resumo.put("profissional", nomeProfissional);
            resumo.put("servico", servico.get("nome"));
            resumo.put("data", dataHoraAgendamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            resumo.put("horario", dataHoraAgendamento.format(DateTimeFormatter.ofPattern("HH:mm")));
            resumo.put("paciente", nomePaciente);
            resumo.put("telefone", telefonePaciente);
            resumo.put("preco", servico.get("preco"));
            
            response.put("resumo", resumo);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Erro ao confirmar agendamento público", e);
            return Map.of("success", false, "error", "Erro interno do servidor: " + e.getMessage());
        }
    }

    /**
     * API para verificar disponibilidade de um horário específico
     */
    @GetMapping("/api/verificar-disponibilidade")
    @ResponseBody
    public Map<String, Object> verificarDisponibilidade(@RequestParam String profissionalId,
                                                       @RequestParam String data,
                                                       @RequestParam String horario) {
        logger.info("API: Verificando disponibilidade - Profissional: {}, Data: {}, Horário: {}", 
                   profissionalId, data, horario);
        
        try {
            // Encontrar profissional
            String nomeProfissional = PROFISSIONAIS.stream()
                    .filter(p -> profissionalId.equals(p.get("id")))
                    .map(p -> p.get("nome") + " - " + p.get("especialidade"))
                    .findFirst()
                    .orElse("Profissional não encontrado");
            
            LocalDateTime dataHoraVerificacao = LocalDateTime.parse(data + "T" + horario + ":00");
            
            // Verificar se há conflito
            boolean disponivel = !agendamentoService.verificarConflitoHorario(
                nomeProfissional, dataHoraVerificacao, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("disponivel", disponivel);
            response.put("mensagem", disponivel ? 
                "Horário disponível" : "Horário já ocupado");
            
            return response;
            
        } catch (Exception e) {
            logger.error("Erro ao verificar disponibilidade", e);
            return Map.of("disponivel", false, "mensagem", "Erro ao verificar disponibilidade");
        }
    }

    /**
     * Página de confirmação de agendamento
     */
    @GetMapping("/agendamento-confirmado/{id}")
    public String agendamentoConfirmado(@PathVariable Long id, Model model) {
        logger.info("Exibindo confirmação de agendamento público - ID: {}", id);
        
        try {
            var agendamento = agendamentoService.buscarPorId(id);
            if (agendamento.isPresent()) {
                model.addAttribute("agendamento", agendamento.get());
                return "public/agendamento-confirmado";
            } else {
                logger.warn("Agendamento não encontrado para confirmação - ID: {}", id);
                return "redirect:/public/agendar?erro=agendamento-nao-encontrado";
            }
        } catch (Exception e) {
            logger.error("Erro ao exibir confirmação de agendamento", e);
            return "redirect:/public/agendar?erro=erro-interno";
        }
    }

    /**
     * Redirecionar agendamento público para rota principal
     */
    @GetMapping("/agendamento")
    public String redirecionarAgendamento() {
        return "redirect:/public/agendar";
    }
}
