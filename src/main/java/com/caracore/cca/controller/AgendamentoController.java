package com.caracore.cca.controller;

import com.caracore.cca.dto.AgendamentoForm;
import com.caracore.cca.model.Agendamento;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.repository.AgendamentoRepository;
import com.caracore.cca.service.PacienteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AgendamentoController {

    private static final Logger logger = LoggerFactory.getLogger(AgendamentoController.class);
    private final AgendamentoRepository repository;
    private final PacienteService pacienteService;
    
    // Lista de dentistas para o dropdown
    private static final List<String> DENTISTAS = Arrays.asList(
        "Dr. Ana Silva - Clínico Geral", 
        "Dr. Carlos Oliveira - Ortodontista", 
        "Dra. Mariana Santos - Endodontista", 
        "Dr. Roberto Pereira - Periodontista"
    );
    
    public AgendamentoController(AgendamentoRepository repository, PacienteService pacienteService) {
        this.repository = repository;
        this.pacienteService = pacienteService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("agendamentos", repository.findAll());
        return "index";
    }

    @GetMapping("/agendamentos")
    @ResponseBody
    public List<Agendamento> listarAgendamentos() {
        return repository.findAll();
    }

    @GetMapping("/novo-agendamento")
    public String novoAgendamentoView(Model model, @RequestParam(required = false) String pacienteNome) {
        // Inicializa o formulário com valores padrão
        AgendamentoForm form = new AgendamentoForm();
        form.setDuracaoMinutos(30);
        form.setStatus("AGENDADO");
        
        // Se um nome de paciente foi fornecido, preenche os dados do paciente
        if (pacienteNome != null && !pacienteNome.isEmpty()) {
            form.setPaciente(pacienteNome);
            List<Paciente> pacientes = pacienteService.buscarPorNome(pacienteNome);
            if (!pacientes.isEmpty()) {
                Paciente paciente = pacientes.get(0);
                if (paciente.getTelefone() != null) {
                    form.setTelefoneWhatsapp(paciente.getTelefone());
                }
            }
        }
        
        model.addAttribute("agendamentoForm", form);
        model.addAttribute("dentistas", DENTISTAS);
        
        return "novo-agendamento";
    }
    
    // Mapeamento adicional para compatibilidade com o menu
    @GetMapping("/novo")
    public String novoAgendamentoViewAlias(Model model, @RequestParam(required = false) String pacienteNome) {
        return novoAgendamentoView(model, pacienteNome);
    }
    
    @GetMapping("/api/verificar-horario")
    @ResponseBody
    public Map<String, Object> verificarHorario(@RequestParam String dentista, @RequestParam String dataHora) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verifica apenas se é horário comercial e dia útil
            // Em uma versão mais completa, verificaria conflitos no banco
            LocalDateTime dateTime = LocalDateTime.parse(dataHora);
            
            int hora = dateTime.getHour();
            int diaSemana = dateTime.getDayOfWeek().getValue(); // 1 = segunda, 7 = domingo
            
            boolean horarioValido = hora >= 8 && hora < 18;
            boolean diaUtil = diaSemana >= 1 && diaSemana <= 5;
            
            if (!diaUtil) {
                response.put("valido", false);
                response.put("mensagem", "Atendimento indisponível nos finais de semana");
            } else if (!horarioValido) {
                response.put("valido", false);
                response.put("mensagem", "Horário fora do expediente (8h às 18h)");
            } else {
                response.put("valido", true);
                response.put("mensagem", "Horário disponível para agendamento");
            }
            
        } catch (Exception e) {
            response.put("valido", false);
            response.put("mensagem", "Formato de data/hora inválido");
        }
        
        return response;
    }

    @PostMapping("/agendar")
    public String agendar(
            @ModelAttribute("agendamentoForm") @Valid AgendamentoForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("dentistas", DENTISTAS);
            return "novo-agendamento";
        }
        
        try {
            // Validações adicionais
            LocalDateTime dataHora = form.getDataHora();
            int hora = dataHora.getHour();
            int diaSemana = dataHora.getDayOfWeek().getValue();
            
            if (diaSemana > 5) {
                model.addAttribute("erro", "Não é possível agendar nos finais de semana");
                model.addAttribute("dentistas", DENTISTAS);
                return "novo-agendamento";
            }
            
            if (hora < 8 || hora >= 18) {
                model.addAttribute("erro", "O horário deve estar entre 8h e 18h");
                model.addAttribute("dentistas", DENTISTAS);
                return "novo-agendamento";
            }
            
            // Atualiza ou cria o paciente com o telefone WhatsApp
            String nomePaciente = form.getPaciente();
            String telefoneWhatsapp = form.getTelefoneWhatsapp();
            
            // Busca o paciente pelo nome - esta parte deve ficar fora do try-catch para os testes funcionarem
            List<Paciente> pacientesEncontrados = pacienteService.buscarPorNome(nomePaciente);
            Paciente paciente;
            
            if (!pacientesEncontrados.isEmpty()) {
                // Atualiza o telefone do paciente existente
                paciente = pacientesEncontrados.get(0);
                paciente.setTelefone(telefoneWhatsapp); // Mantém o formato para exibição
                pacienteService.atualizar(paciente); // Chamada ao método que está sendo verificada nos testes
                logger.info("Telefone WhatsApp do paciente {} atualizado: {}", nomePaciente, telefoneWhatsapp);
            } else {
                // Cria um novo paciente se não existir
                paciente = new Paciente(nomePaciente, null, telefoneWhatsapp); // Mantém o formato para exibição
                pacienteService.salvar(paciente); // Chamada ao método que está sendo verificada nos testes
                logger.info("Novo paciente criado: {} com telefone WhatsApp: {}", nomePaciente, telefoneWhatsapp);
            }
            
            // Cria e salva o agendamento
            Agendamento agendamento = new Agendamento();
            agendamento.setPaciente(form.getPaciente());
            agendamento.setDentista(form.getDentista());
            agendamento.setDataHora(form.getDataHora());
            agendamento.setObservacao(form.getObservacao());
            agendamento.setStatus(form.getStatus());
            
            repository.save(agendamento);
            
            // Formatação da data para exibição
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String dataFormatada = agendamento.getDataHora().format(formatter);
            
            logger.info("Novo agendamento criado: {} (WhatsApp: {}) com {} em {}", 
                    form.getPaciente(), form.getTelefoneWhatsapp(), form.getDentista(), dataFormatada);
            
            // Em testes, redirectAttributes pode ser null
            if (redirectAttributes != null) {
                redirectAttributes.addFlashAttribute("sucesso", 
                    "Agendamento criado com sucesso para " + agendamento.getPaciente() + 
                    " (WhatsApp: " + telefoneWhatsapp + ") com " + agendamento.getDentista() + " em " + dataFormatada);
            }
                
            return "redirect:/";
        } catch (Exception e) {
            logger.error("Erro ao salvar agendamento", e);
            model.addAttribute("erro", "Ocorreu um erro ao salvar o agendamento: " + e.getMessage());
            model.addAttribute("dentistas", DENTISTAS);
            return "novo-agendamento";
        }
    }
    
    @GetMapping("/api/buscar-paciente")
    @ResponseBody
    public Map<String, Object> buscarPacientePorNome(@RequestParam String nome) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Paciente> pacientes = pacienteService.buscarPorNome(nome);
            if (!pacientes.isEmpty()) {
                Paciente paciente = pacientes.get(0);
                response.put("encontrado", true);
                response.put("nome", paciente.getNome());
                response.put("telefone", paciente.getTelefone());
                response.put("email", paciente.getEmail());
            } else {
                response.put("encontrado", false);
                response.put("mensagem", "Paciente não encontrado");
            }
        } catch (Exception e) {
            response.put("encontrado", false);
            response.put("mensagem", "Erro ao buscar paciente: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Gera um link para o WhatsApp Web a partir de um número de telefone
     * 
     * @param telefone O número de telefone no formato (99) 99999-9999
     * @return Um link para abrir o WhatsApp Web com o número
     */
    public String gerarLinkWhatsApp(String telefone) {
        if (telefone == null || telefone.isEmpty()) {
            return "#";
        }
        
        // Remove caracteres não numéricos
        String numeroLimpo = telefone.replaceAll("\\D", "");
        
        // Verifica se o número tem pelo menos 10 dígitos (DDD + número)
        if (numeroLimpo.length() < 10) {
            return "#";
        }
        
        // Formata o número para o WhatsApp (com código do Brasil +55)
        return "https://wa.me/55" + numeroLimpo;
    }
    
    @GetMapping("/api/gerar-link-whatsapp")
    @ResponseBody
    public Map<String, String> gerarLinkWhatsAppEndpoint(@RequestParam String telefone) {
        Map<String, String> response = new HashMap<>();
        String link = gerarLinkWhatsApp(telefone);
        
        response.put("link", link);
        response.put("telefoneOriginal", telefone);
        
        // Se foi gerado um link válido
        if (!link.equals("#")) {
            response.put("valido", "true");
            response.put("mensagem", "Link gerado com sucesso");
        } else {
            response.put("valido", "false");
            response.put("mensagem", "Número de telefone inválido");
        }
        
        return response;
    }
    
    @GetMapping("/api/whatsapp")
    public String redirecionarWhatsapp(@RequestParam String telefone) {
        String link = gerarLinkWhatsApp(telefone);
        return "redirect:" + link;
    }
}