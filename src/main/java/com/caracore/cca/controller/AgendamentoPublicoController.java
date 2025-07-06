package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.service.AgendamentoService;
import com.caracore.cca.service.PacienteService;
import com.caracore.cca.service.RateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Controlador responsável pelo agendamento online público.
 * 
 * Este controller não requer autenticação, permitindo que pacientes
 * agendem consultas diretamente pelo site.
 */
@Controller
@Tag(name = "Agendamento Público", description = "Endpoints públicos para agendamento de consultas")
public class AgendamentoPublicoController {

    private static final Logger logger = LoggerFactory.getLogger(AgendamentoPublicoController.class);

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private RateLimitService rateLimitService;

    /**
     * Página principal de agendamento online
     */
    @GetMapping("/public/agendamento")
    public String agendamentoOnline(Model model) {
        logger.info("Acessando página de agendamento online público");
        
        try {
            // Usar apenas dentistas ativos para exposição pública
            List<String> dentistas = agendamentoService.listarDentistasAtivos();
            model.addAttribute("titulo", "Agendamento Online");
            model.addAttribute("dentistas", dentistas);
            
            return "public/agendamento-online";
        } catch (Exception e) {
            logger.error("Erro ao carregar página de agendamento", e);
            model.addAttribute("error", "Erro interno do servidor");
            return "public/agendamento-online";
        }
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
                List<String> dentistas = agendamentoService.listarDentistasAtivos();
                model.addAttribute("dentistas", dentistas);
                model.addAttribute("titulo", "Agendamento Online");
                return "public/agendamento-online";
            }
            // Validação: impedir agendamento no passado
            LocalDateTime dataHoraAgendamento;
            try {
                dataHoraAgendamento = LocalDateTime.parse(dataHora);
            } catch (Exception e) {
                // Se não conseguir fazer parse, tenta adicionar segundos
                try {
                    dataHoraAgendamento = LocalDateTime.parse(dataHora + ":00");
                } catch (Exception e2) {
                    model.addAttribute("error", "Formato de data/hora inválido");
                    List<String> dentistas = agendamentoService.listarDentistasAtivos();
                    model.addAttribute("dentistas", dentistas);
                    model.addAttribute("titulo", "Agendamento Online");
                    return "public/agendamento-online";
                }
            }
            if (dataHoraAgendamento.isBefore(LocalDateTime.now())) {
                model.addAttribute("error", "Não é possível agendar consultas no passado");
                List<String> dentistas = agendamentoService.listarDentistasAtivos();
                model.addAttribute("dentistas", dentistas);
                model.addAttribute("titulo", "Agendamento Online");
                return "public/agendamento-online";
            }
            // Criar agendamento
            Agendamento agendamento = new Agendamento();
            agendamento.setPaciente(paciente);
            agendamento.setDentista(dentista);
            agendamento.setDataHora(dataHoraAgendamento);
            agendamento.setStatus("AGENDADO");
            agendamento.setObservacao("Agendamento online");
            agendamento.setTelefoneWhatsapp(telefone);
            agendamento.setDuracaoMinutos(30);
            agendamento = agendamentoService.salvar(agendamento);
            return "redirect:/public/agendamento-confirmado?id=" + agendamento.getId();
        } catch (Exception e) {
            logger.error("Erro ao processar agendamento", e);
            model.addAttribute("error", "Ocorreu um erro ao processar o agendamento");
            List<String> dentistas = agendamentoService.listarDentistasAtivos();
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
    @Operation(
        summary = "Obter horários disponíveis para agendamento",
        description = "Retorna lista de horários disponíveis para um dentista em uma data específica"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de horários disponíveis retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "array", example = "[\"09:00\", \"10:00\", \"14:00\"]"))),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou ausentes"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping(value = "/public/api/horarios-disponiveis", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<String>> obterHorariosDisponiveisPublico(
            @Parameter(description = "Nome do dentista", required = true, example = "Dr. João Silva - Clínico Geral")
            @RequestParam(required = false) String dentista,
            @Parameter(description = "Data para consulta no formato YYYY-MM-DD", required = true, example = "2025-07-10")
            @RequestParam(required = false) String data) {
        logger.info("API: Obtendo horários disponíveis públicos - Dentista: {}, Data: {}", dentista, data);
        
        // Validar parâmetros
        if (dentista == null || dentista.isEmpty() || data == null || data.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            LocalDateTime dataInicio = LocalDateTime.parse(data + "T00:00:00");
            List<String> horarios = agendamentoService.getHorariosDisponiveisPorData(dentista, dataInicio);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(horarios);
        } catch (Exception e) {
            logger.error("Erro ao buscar horários disponíveis", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * API para consultar agendamentos por paciente
     */
    @Operation(
        summary = "Consultar agendamentos por paciente",
        description = "Retorna lista de agendamentos para um paciente específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Agendamento.class))),
        @ApiResponse(responseCode = "400", description = "Nome do paciente não informado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping(value = "/public/consultar-agendamento", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> consultarAgendamentoPorPaciente(
            @Parameter(description = "Nome do paciente", required = true, example = "João Silva")
            @RequestParam(required = false) String paciente) {
        logger.info("API: Consultando agendamentos por paciente: {}", paciente);
        
        // Validar parâmetros
        if (paciente == null || paciente.isEmpty()) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Campo paciente é obrigatório");
        }
        
        try {
            List<Agendamento> agendamentos = agendamentoService.buscarPorPaciente(paciente);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(agendamentos);
        } catch (Exception e) {
            logger.error("Erro ao buscar agendamentos por paciente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Exception handler for RuntimeException (internal server errors)
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Erro interno do servidor", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
    }
    
    /**
     * Exception handler para requisições inválidas nas APIs
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * API para verificar disponibilidade de horário específico
     */
    @Operation(
        summary = "Verificar disponibilidade de horário específico",
        description = "Verifica se um horário específico está disponível para agendamento com um dentista"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"disponivel\": true}"))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/api/public/verificar-disponibilidade")
    @ResponseBody
    public Map<String, Object> verificarDisponibilidadePublica(
            @Parameter(description = "Nome do dentista", required = true, example = "Dr. João Silva - Clínico Geral")
            @RequestParam String dentista,
            @Parameter(description = "Data e hora no formato ISO", required = true, example = "2025-07-10T10:00")
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
     * API para listar dentistas ativos para agendamento público
     */
    @Operation(
        summary = "Listar dentistas disponíveis para agendamento público",
        description = "Retorna lista de dentistas ativos e expostos publicamente para agendamento"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de dentistas retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "array", example = "[\"Dr. João Silva - Clínico Geral\", \"Dra. Maria Santos - Ortodontista\"]"))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping(value = "/public/api/dentistas", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> listarDentistasPublico() {
        logger.info("API: Obtendo lista de dentistas públicos (apenas ativos)");
        
        try {
            // Usar apenas dentistas ativos para exposição pública
            List<String> dentistas = agendamentoService.listarDentistasAtivos();
            return dentistas;
        } catch (Exception e) {
            logger.error("Erro ao listar dentistas públicos", e);
            throw new RuntimeException("Erro interno do servidor", e);
        }
    }

    /**
     * Endpoint REST para agendamento público (para testes automatizados)
     */
    @PostMapping(value = "/public/api/agendar", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public Map<String, Object> agendarConsultaPublicaApi(
            @RequestParam(required = false) String paciente,
            @RequestParam(required = false) String dentista,
            @RequestParam(required = false) String dataHora,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String email) {
        Map<String, Object> resposta = new HashMap<>();
        // Validação básica
        if (paciente == null || paciente.isEmpty() || dentista == null || dentista.isEmpty() || dataHora == null || dataHora.isEmpty()) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Todos os campos obrigatórios devem ser preenchidos");
            return resposta;
        }
        // Validação: impedir agendamento no passado
        LocalDateTime dataHoraAgendamento = LocalDateTime.parse(dataHora);
        if (dataHoraAgendamento.isBefore(LocalDateTime.now())) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Não é possível agendar consultas no passado");
            return resposta;
        }
        // Validação: dentista ativo
        List<String> dentistasAtivos = agendamentoService.listarDentistasAtivos();
        if (!dentistasAtivos.contains(dentista)) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Dentista não disponível para agendamento público");
            return resposta;
        }
        // Criar agendamento
        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setDentista(dentista);
        agendamento.setDataHora(dataHoraAgendamento);
        agendamento.setStatus("AGENDADO");
        agendamento.setObservacao("Agendamento online");
        agendamento.setTelefoneWhatsapp(telefone);
        agendamento.setDuracaoMinutos(30);
        agendamento = agendamentoService.salvar(agendamento);
        resposta.put("status", "sucesso");
        resposta.put("mensagem", "Agendamento realizado com sucesso!");
        resposta.put("id", agendamento.getId());
        return resposta;
    }

    /**
     * Método de teste simples
     */
    @PostMapping(value = "/public/test")
    @ResponseBody
    public String testeSimples() {
        System.out.println("DEBUG: Método testeSimples CHAMADO!");
        return "Teste funcionando!";
    }
    
    /**
     * Endpoint de teste simples para debug
     */
    @PostMapping(value = "/public/test-simple", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String testeSimpleDebug() {
        logger.info("=== TESTE SIMPLE DEBUG EXECUTADO ===");
        String response = "Teste executado com sucesso!";
        logger.info("Retornando resposta: {}", response);
        return response;
    }
    
    /**
     * Endpoint REST compatível com os testes automatizados (resposta texto)
     */
    @Operation(
        summary = "Agendar consulta publicamente",
        description = "Cria um novo agendamento de consulta sem necessidade de autenticação"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agendamento realizado com sucesso",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Agendamento realizado com sucesso!"))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios ausentes",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Todos os campos obrigatórios devem ser preenchidos"))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping(value = "/public/agendar")
    @ResponseBody
    public ResponseEntity<String> agendarConsultaPublicaCompat(
            @Parameter(description = "Nome do paciente", required = true, example = "João Silva")
            @RequestParam(required = false) String paciente,
            @Parameter(description = "Nome do dentista", required = true, example = "Dr. João Silva - Clínico Geral")
            @RequestParam(required = false) String dentista,
            @Parameter(description = "Data e hora do agendamento no formato ISO", required = true, example = "2025-07-10T10:00")
            @RequestParam(required = false) String dataHora,
            @Parameter(description = "Telefone para contato (WhatsApp)", required = false, example = "11999999999")
            @RequestParam(required = false) String telefone,
            HttpServletRequest request) {
        
        String clientIp = getClientIp(request);
        logger.info("Tentativa de agendamento público - IP: {}", clientIp);
        
        try {
            // 1. Validação e sanitização básica (primeiro para testes)
            ValidationResult validation = validateAndSanitizeInput(paciente, dentista, dataHora, telefone);
            if (!validation.isValid()) {
                logger.warn("Dados inválidos do IP: {} - {}", clientIp, validation.getErrorMessage());
                return ResponseEntity.badRequest()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(validation.getErrorMessage());
            }
            
            // 2. Rate Limiting por IP
            if (!rateLimitService.isAllowed(clientIp)) {
                logger.warn("Rate limit excedido para IP: {}", clientIp);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Muitas tentativas. Tente novamente em alguns minutos.");
            }
            
            // 3. Validação de horário comercial (depois da validação básica)
            if (!isBusinessHours()) {
                logger.warn("Tentativa de agendamento fora do horário comercial - IP: {}", clientIp);
                return ResponseEntity.badRequest()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Agendamentos só podem ser feitos durante horário comercial (Segunda a Sexta, 8h às 18h).");
            }
            
            // 4. Validação de data/hora
            LocalDateTime dataHoraAgendamento;
            try {
                dataHoraAgendamento = LocalDateTime.parse(dataHora);
            } catch (Exception e) {
                logger.warn("Formato de data inválido - IP: {}, DataHora: {}", clientIp, dataHora);
                return ResponseEntity.badRequest()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Formato de data/hora inválido");
            }
            
            if (dataHoraAgendamento.isBefore(LocalDateTime.now())) {
                logger.warn("Data no passado - IP: {}, DataHora: {}", clientIp, dataHoraAgendamento);
                return ResponseEntity.badRequest()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Não é possível agendar consultas no passado");
            }
            
            // 5. Validação de dentista ativo
            List<String> dentistasAtivos = agendamentoService.listarDentistasAtivos();
            if (!dentistasAtivos.contains(dentista)) {
                logger.warn("Dentista indisponível - IP: {}, Dentista: {}", clientIp, dentista);
                return ResponseEntity.badRequest()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Dentista não disponível para agendamento público");
            }
            
            // 6. Criar agendamento com auditoria
            Agendamento agendamento = createSecureAgendamento(paciente, dentista, dataHoraAgendamento, telefone, clientIp);
            agendamento = agendamentoService.salvar(agendamento);
            
            // 7. Log de auditoria
            logger.info("Agendamento público criado com sucesso - ID: {}, IP: {}, Paciente: {}", 
                       agendamento.getId(), clientIp, paciente);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Agendamento realizado com sucesso!");
            
        } catch (Exception e) {
            logger.error("Erro interno ao processar agendamento - IP: {}", clientIp, e);
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Erro interno do servidor");
        }
    }
    
    // ========== MÉTODOS AUXILIARES DE SEGURANÇA ==========
    
    /**
     * Obtém o IP real do cliente considerando proxies
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        
        String xrHeader = request.getHeader("X-Real-IP");
        if (xrHeader != null && !xrHeader.isEmpty()) {
            return xrHeader.trim();
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Valida e sanitiza os dados de entrada
     */
    private ValidationResult validateAndSanitizeInput(String paciente, String dentista, String dataHora, String telefone) {
        // Validação de campos obrigatórios
        if (paciente == null || paciente.trim().isEmpty() || 
            dentista == null || dentista.trim().isEmpty() || 
            dataHora == null || dataHora.trim().isEmpty()) {
            return new ValidationResult(false, "Todos os campos obrigatórios devem ser preenchidos");
        }
        
        // Validação do nome do paciente (apenas letras, espaços e acentos)
        String pacienteClean = paciente.trim();
        if (!Pattern.matches("^[a-zA-ZÀ-ÿ\\s]{2,100}$", pacienteClean)) {
            return new ValidationResult(false, "Nome do paciente inválido (apenas letras e espaços, 2-100 caracteres)");
        }
        
        // Validação do telefone se fornecido
        if (telefone != null && !telefone.trim().isEmpty()) {
            String telefoneNumerico = telefone.replaceAll("\\D", "");
            if (!Pattern.matches("^\\d{10,11}$", telefoneNumerico)) {
                return new ValidationResult(false, "Formato de telefone inválido (10-11 dígitos)");
            }
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Verifica se está dentro do horário comercial
     */
    private boolean isBusinessHours() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int dayOfWeek = now.getDayOfWeek().getValue();
        
        // Segunda a sexta (1-5), 8h às 18h
        return dayOfWeek >= 1 && dayOfWeek <= 5 && hour >= 8 && hour <= 18;
    }
    
    /**
     * Cria um agendamento com informações de auditoria
     */
    private Agendamento createSecureAgendamento(String paciente, String dentista, LocalDateTime dataHora, String telefone, String clientIp) {
        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente.trim());
        agendamento.setDentista(dentista.trim());
        agendamento.setDataHora(dataHora);
        agendamento.setStatus("PENDENTE_CONFIRMACAO");
        agendamento.setObservacao(String.format("Agendamento online seguro - IP: %s", clientIp));
        agendamento.setTelefoneWhatsapp(telefone != null ? telefone.trim() : null);
        agendamento.setDuracaoMinutos(30);
        
        return agendamento;
    }
    
    /**
     * Classe auxiliar para resultado de validação
     */
    private static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
