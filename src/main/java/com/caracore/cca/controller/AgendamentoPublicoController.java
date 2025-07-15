package com.caracore.cca.controller;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.service.AgendamentoService;
import com.caracore.cca.service.RateLimitService;
import com.caracore.cca.service.CaptchaService;
import com.caracore.cca.util.AgendamentoSessionManager;
import com.caracore.cca.util.AgendamentoFlowController;
import com.caracore.cca.util.FeatureFlagManager;
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
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    private RateLimitService rateLimitService;
    
    @Autowired
    private CaptchaService captchaService;
    
    @Autowired
    private AgendamentoSessionManager sessionManager;
    
    @Autowired
    private AgendamentoFlowController flowController;
    
    @Autowired
    private FeatureFlagManager featureFlagManager;
    
    @Autowired
    private Environment environment;

    /**
     * Página principal de agendamento online com A/B Testing
     */
    @GetMapping("/public/agendamento")
    public String agendamentoOnline(Model model, HttpServletRequest request) {
        logger.info("Acessando página de agendamento online público");
        
        try {
            // Determinar qual fluxo usar baseado em Feature Flags e A/B Testing
            FeatureFlagManager.FlowType flowType = featureFlagManager.determineFlowType(request);
            
            // Registrar evento de início do fluxo para A/B Testing
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("user_agent", request.getHeader("User-Agent"));
            metadata.put("referer", request.getHeader("Referer"));
            featureFlagManager.recordEvent(request, FeatureFlagManager.ABTestEvent.FLOW_STARTED, metadata);
            
            // Se for fluxo multi-etapas, redirecionar para Etapa 1
            if (flowType == FeatureFlagManager.FlowType.MULTI_STEP) {
                logger.info("Redirecionando para fluxo multi-etapas - IP: {}", getClientIp(request));
                return "redirect:/public/agendamento/etapa1";
            }
            
            // Continuar com fluxo de página única
            logger.info("Usando fluxo de página única - IP: {}", getClientIp(request));
            
            // Usar apenas dentistas ativos para exposição pública
            List<String> dentistas = agendamentoService.listarDentistasAtivos();
            model.addAttribute("titulo", "Agendamento Online");
            model.addAttribute("dentistas", dentistas);
            model.addAttribute("flowType", flowType.getKey());
            
            // Adicionar informações do reCAPTCHA se habilitado
            if (captchaService.isEnabled()) {
                model.addAttribute("recaptchaEnabled", true);
                model.addAttribute("recaptchaSiteKey", captchaService.getSiteKey());
                return "public/agendamento-online-captcha";
            } else {
                model.addAttribute("recaptchaEnabled", false);
                return "public/agendamento-online";
            }
        } catch (Exception e) {
            logger.error("Erro ao carregar página de agendamento", e);
            model.addAttribute("error", "Erro interno do servidor");
            
            // Registrar erro para A/B Testing
            Map<String, Object> errorMetadata = new HashMap<>();
            errorMetadata.put("error_message", e.getMessage());
            errorMetadata.put("error_class", e.getClass().getSimpleName());
            featureFlagManager.recordEvent(request, FeatureFlagManager.ABTestEvent.ERROR_OCCURRED, errorMetadata);
            
            // Em caso de erro, usar o template simples
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
                                           @RequestParam(required = false) String captchaToken,
                                           Model model,
                                           HttpServletRequest request) {
        logger.info("Processando agendamento público para paciente: {}, dentista: {}, dataHora: {}", 
                  paciente, dentista, dataHora);
        try {
            // Validação do reCAPTCHA (se habilitado)
            if (captchaService.isEnabled()) {
                String clientIp = getClientIp(request);
                if (!captchaService.validateCaptcha(captchaToken, clientIp)) {
                    logger.warn("Captcha inválido do IP: {}", clientIp);
                    model.addAttribute("error", "Captcha inválido. Verifique e tente novamente.");
                    return prepareModelAndReturnTemplate(model);
                }
            }
            
            // Validação básica - note que campos são required=false para que o controller possa receber valores nulos
            if (paciente == null || paciente.isEmpty() || dentista == null || dentista.isEmpty() || dataHora == null || dataHora.isEmpty()) {
                model.addAttribute("error", "Todos os campos são obrigatórios");
                return prepareModelAndReturnTemplate(model);
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
                    return prepareModelAndReturnTemplate(model);
                }
            }
            if (dataHoraAgendamento.isBefore(LocalDateTime.now())) {
                model.addAttribute("error", "Não é possível agendar consultas no passado");
                return prepareModelAndReturnTemplate(model);
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
            return prepareModelAndReturnTemplate(model);
        }
    }
    
    /**
     * Método auxiliar para preparar o modelo e retornar o template correto
     */
    private String prepareModelAndReturnTemplate(Model model) {
        List<String> dentistas = agendamentoService.listarDentistasAtivos();
        model.addAttribute("dentistas", dentistas);
        model.addAttribute("titulo", "Agendamento Online");
        
        // Adicionar informações do reCAPTCHA se habilitado
        if (captchaService.isEnabled()) {
            model.addAttribute("recaptchaEnabled", true);
            model.addAttribute("recaptchaSiteKey", captchaService.getSiteKey());
            return "public/agendamento-online-captcha";
        } else {
            model.addAttribute("recaptchaEnabled", false);
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

    // ========== FLUXO DE ETAPAS ==========
    
    /**
     * Landing page inteligente que redireciona baseado em A/B Testing
     */
    @GetMapping("/public/agendamento/landing")
    public String agendamentoLanding(Model model, HttpServletRequest request) {
        logger.info("Acessando landing page inteligente");
        
        // Determinar fluxo usando A/B Testing
        FeatureFlagManager.FlowType flowType = featureFlagManager.determineFlowType(request);
        
        // Registrar evento de entrada via landing
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("entry_point", "landing");
        metadata.put("determined_flow", flowType.getKey());
        featureFlagManager.recordEvent(request, FeatureFlagManager.ABTestEvent.FLOW_STARTED, metadata);
        
        logger.info("Landing page redirecionando para: {} - IP: {}", flowType.getDisplayName(), getClientIp(request));
        
        // Redirecionar para o fluxo apropriado
        return "redirect:" + flowType.getEntryUrl();
    }
    
    /**
     * Endpoint para monitoramento da sessão (apenas para desenvolvimento)
     */
    @GetMapping("/public/agendamento/session-info")
    @ResponseBody
    public Map<String, Object> sessionInfo(HttpServletRequest request) {
        if (!Arrays.asList(environment.getActiveProfiles()).contains("local") && 
            !Arrays.asList(environment.getActiveProfiles()).contains("h2")) {
            return Map.of("error", "Endpoint disponível apenas em ambiente de desenvolvimento");
        }
        
        return sessionManager.getSessionInfo(request);
    }
    
    /**
     * Endpoint para debug do fluxo de navegação (apenas para desenvolvimento)
     */
    @GetMapping("/public/agendamento/flow-info")
    @ResponseBody
    public Map<String, Object> flowInfo(HttpServletRequest request) {
        if (!Arrays.asList(environment.getActiveProfiles()).contains("local") && 
            !Arrays.asList(environment.getActiveProfiles()).contains("h2")) {
            return Map.of("error", "Endpoint disponível apenas em ambiente de desenvolvimento");
        }
        
        return flowController.getFlowDebugInfo(request);
    }
    
    /**
     * Endpoint para navegação inteligente - redireciona para etapa apropriada
     */
    @GetMapping("/public/agendamento/navigate")
    public String smartNavigate(HttpServletRequest request) {
        logger.info("Navegação inteligente solicitada");
        
        AgendamentoFlowController.Etapa nextStep = flowController.getNextRecommendedStep(request);
        
        logger.info("Redirecionando para: {}", nextStep.getTitulo());
        return "redirect:" + nextStep.getUrl();
    }
    
    /**
     * Endpoint para debug de Feature Flags (apenas desenvolvimento)
     */
    @GetMapping("/public/agendamento/feature-flags")
    @ResponseBody
    public Map<String, Object> featureFlagsInfo(HttpServletRequest request) {
        if (!Arrays.asList(environment.getActiveProfiles()).contains("local") && 
            !Arrays.asList(environment.getActiveProfiles()).contains("h2")) {
            return Map.of("error", "Endpoint disponível apenas em ambiente de desenvolvimento");
        }
        
        return featureFlagManager.getDebugInfo(request);
    }
    
    /**
     * Endpoint para forçar tipo de fluxo (apenas desenvolvimento)
     */
    @PostMapping("/public/agendamento/force-flow")
    @ResponseBody
    public Map<String, Object> forceFlowType(HttpServletRequest request, 
                                            @RequestParam String flowType) {
        if (!Arrays.asList(environment.getActiveProfiles()).contains("local") && 
            !Arrays.asList(environment.getActiveProfiles()).contains("h2")) {
            return Map.of("error", "Endpoint disponível apenas em ambiente de desenvolvimento");
        }
        
        try {
            FeatureFlagManager.FlowType type = FeatureFlagManager.FlowType.valueOf(flowType.toUpperCase().replace("-", "_"));
            featureFlagManager.forceFlowType(request, type);
            
            return Map.of(
                "success", true, 
                "message", "Fluxo forçado para: " + type.getDisplayName(),
                "flowType", type.getKey()
            );
        } catch (IllegalArgumentException e) {
            return Map.of(
                "success", false, 
                "error", "Tipo de fluxo inválido. Use: SINGLE_PAGE ou MULTI_STEP"
            );
        }
    }
    
    /**
     * Endpoint para resetar assignment A/B (apenas desenvolvimento)
     */
    @PostMapping("/public/agendamento/reset-assignment")
    @ResponseBody
    public Map<String, Object> resetAssignment(HttpServletRequest request) {
        if (!Arrays.asList(environment.getActiveProfiles()).contains("local") && 
            !Arrays.asList(environment.getActiveProfiles()).contains("h2")) {
            return Map.of("error", "Endpoint disponível apenas em ambiente de desenvolvimento");
        }
        
        featureFlagManager.resetAssignment(request);
        FeatureFlagManager.FlowType newFlow = featureFlagManager.determineFlowType(request);
        
        return Map.of(
            "success", true, 
            "message", "Assignment resetado",
            "newFlowType", newFlow.getKey()
        );
    }
    
    /**
     * Etapa 1: Dados pessoais e escolha do dentista
     */
    @GetMapping("/public/agendamento/etapa1")
    public String agendamentoEtapa1(Model model, HttpServletRequest request,
                                   @RequestParam(value = "error", required = false) String errorMessage) {
        logger.info("Acessando Etapa 1 do agendamento");
        
        try {
            // Registrar acesso à Etapa 1 para A/B Testing
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("step", "1");
            metadata.put("has_error_message", errorMessage != null);
            featureFlagManager.recordEvent(request, FeatureFlagManager.ABTestEvent.STEP_COMPLETED, metadata);
            
            // Usar FlowController para validação inteligente de acesso
            String redirectUrl = flowController.handleSmartNavigation(request, AgendamentoFlowController.Etapa.ETAPA_1);
            if (redirectUrl != null) {
                return redirectUrl;
            }
            
            // Inicializar sessão se necessário
            if (!sessionManager.isSessionValid(request)) {
                sessionManager.iniciarSessao(request);
            }
            
            // Limpar etapas posteriores se voltou para cá
            flowController.cleanForwardStepsOnBackNavigation(request, AgendamentoFlowController.Etapa.ETAPA_1);
            
            // Carregar lista de dentistas ativos
            List<String> dentistas = agendamentoService.listarDentistasAtivos();
            model.addAttribute("dentistas", dentistas);
            model.addAttribute("titulo", "Etapa 1 - Dados Pessoais");
            
            // Adicionar navegação breadcrumb
            AgendamentoFlowController.NavigationBreadcrumb breadcrumb = 
                flowController.buildBreadcrumb(request, AgendamentoFlowController.Etapa.ETAPA_1);
            model.addAttribute("breadcrumb", breadcrumb);
            
            // Recuperar dados da sessão se existirem
            AgendamentoSessionManager.AgendamentoSessionData sessionData = sessionManager.recuperarDados(request);
            if (sessionData != null) {
                if (sessionData.getPaciente() != null) model.addAttribute("paciente", sessionData.getPaciente());
                if (sessionData.getTelefone() != null) model.addAttribute("telefone", sessionData.getTelefone());
                if (sessionData.getEmail() != null) model.addAttribute("email", sessionData.getEmail());
                if (sessionData.getDentista() != null) model.addAttribute("dentista", sessionData.getDentista());
            }
            
            // Adicionar mensagem de erro de redirecionamento se existir
            if (errorMessage != null && !errorMessage.trim().isEmpty()) {
                model.addAttribute("error", errorMessage);
            }
            
            return "public/agendamento-etapa1";
        } catch (Exception e) {
            logger.error("Erro ao carregar Etapa 1", e);
            model.addAttribute("error", "Erro ao carregar página. Tente novamente.");
            return "public/agendamento-etapa1";
        }
    }
    
    /**
     * Processar Etapa 1 e avançar para Etapa 2
     */
    @PostMapping("/public/agendamento/etapa1")
    public String processarEtapa1(@RequestParam String paciente,
                                 @RequestParam String telefone,
                                 @RequestParam(required = false) String email,
                                 @RequestParam String dentista,
                                 HttpServletRequest request,
                                 Model model) {
        logger.info("Processando Etapa 1 - Paciente: {}, Dentista: {}", paciente, dentista);
        
        try {
            // Validar dados obrigatórios
            if (paciente == null || paciente.trim().isEmpty()) {
                // Registrar falha de validação
                Map<String, Object> errorMetadata = new HashMap<>();
                errorMetadata.put("step", "1");
                errorMetadata.put("validation_field", "paciente");
                featureFlagManager.recordEvent(request, FeatureFlagManager.ABTestEvent.VALIDATION_FAILED, errorMetadata);
                
                model.addAttribute("error", "Nome do paciente é obrigatório");
                return agendamentoEtapa1(model, request, null);
            }
            
            if (telefone == null || telefone.trim().isEmpty()) {
                // Registrar falha de validação
                Map<String, Object> errorMetadata = new HashMap<>();
                errorMetadata.put("step", "1");
                errorMetadata.put("validation_field", "telefone");
                featureFlagManager.recordEvent(request, FeatureFlagManager.ABTestEvent.VALIDATION_FAILED, errorMetadata);
                
                model.addAttribute("error", "Telefone é obrigatório");
                return agendamentoEtapa1(model, request, null);
            }
            
            if (dentista == null || dentista.trim().isEmpty()) {
                // Registrar falha de validação
                Map<String, Object> errorMetadata = new HashMap<>();
                errorMetadata.put("step", "1");
                errorMetadata.put("validation_field", "dentista");
                featureFlagManager.recordEvent(request, FeatureFlagManager.ABTestEvent.VALIDATION_FAILED, errorMetadata);
                
                model.addAttribute("error", "Seleção do profissional é obrigatória");
                return agendamentoEtapa1(model, request, null);
            }
            
            // Salvar dados na sessão usando o SessionManager
            sessionManager.salvarEtapa1(request, paciente, telefone, email != null ? email : "", dentista);
            
            // Registrar conclusão bem-sucedida da Etapa 1
            Map<String, Object> successMetadata = new HashMap<>();
            successMetadata.put("step", "1");
            successMetadata.put("has_email", email != null && !email.trim().isEmpty());
            successMetadata.put("dentista", dentista);
            featureFlagManager.recordEvent(request, FeatureFlagManager.ABTestEvent.STEP_COMPLETED, successMetadata);
            
            logger.info("Dados da Etapa 1 salvos na sessão. Redirecionando para Etapa 2");
            return "redirect:/public/agendamento/etapa2";
            
        } catch (AgendamentoSessionManager.SessionExpiredException e) {
            logger.warn("Sessão expirada durante processamento da Etapa 1");
            model.addAttribute("error", "Sessão expirada. Por favor, reinicie o processo.");
            return agendamentoEtapa1(model, request, null);
        } catch (Exception e) {
            logger.error("Erro ao processar Etapa 1", e);
            model.addAttribute("error", "Erro interno. Tente novamente.");
            return agendamentoEtapa1(model, request, null);
        }
    }
    
    /**
     * Etapa 2: Seleção de data e horário
     */
    @GetMapping("/public/agendamento/etapa2")
    public String agendamentoEtapa2(Model model, HttpServletRequest request,
                                   @RequestParam(value = "error", required = false) String errorMessage) {
        logger.info("Acessando Etapa 2 do agendamento");
        
        try {
            // Usar FlowController para validação inteligente de acesso
            String redirectUrl = flowController.handleSmartNavigation(request, AgendamentoFlowController.Etapa.ETAPA_2);
            if (redirectUrl != null) {
                return redirectUrl;
            }
            
            // Limpar etapas posteriores se voltou para cá
            flowController.cleanForwardStepsOnBackNavigation(request, AgendamentoFlowController.Etapa.ETAPA_2);
            
            // Renovar sessão para manter ativa
            sessionManager.renovarSessao(request);
            
            // Recuperar dados da sessão
            AgendamentoSessionManager.AgendamentoSessionData sessionData = sessionManager.recuperarDados(request);
            
            // Adicionar dados ao model
            model.addAttribute("paciente", sessionData.getPaciente());
            model.addAttribute("telefone", sessionData.getTelefone());
            model.addAttribute("email", sessionData.getEmail());
            model.addAttribute("dentista", sessionData.getDentista());
            model.addAttribute("titulo", "Etapa 2 - Seleção de Horário");
            
            // Adicionar navegação breadcrumb
            AgendamentoFlowController.NavigationBreadcrumb breadcrumb = 
                flowController.buildBreadcrumb(request, AgendamentoFlowController.Etapa.ETAPA_2);
            model.addAttribute("breadcrumb", breadcrumb);
            
            // Adicionar dados já selecionados se existirem
            if (sessionData.getData() != null) model.addAttribute("data", sessionData.getData());
            if (sessionData.getHorario() != null) model.addAttribute("horario", sessionData.getHorario());
            
            // Adicionar mensagem de erro de redirecionamento se existir
            if (errorMessage != null && !errorMessage.trim().isEmpty()) {
                model.addAttribute("error", errorMessage);
            }
            
            return "public/agendamento-etapa2";
        } catch (Exception e) {
            logger.error("Erro ao carregar Etapa 2", e);
            model.addAttribute("error", "Erro ao carregar página. Tente novamente.");
            return "redirect:/public/agendamento/etapa1";
        }
    }
    
    /**
     * Processar Etapa 2 e avançar para Etapa 3
     */
    @PostMapping("/public/agendamento/etapa2")
    public String processarEtapa2(@RequestParam String data,
                                 @RequestParam String horario,
                                 HttpServletRequest request,
                                 Model model) {
        logger.info("Processando Etapa 2 - Data: {}, Horário: {}", data, horario);
        
        try {
            // Verificar se dados da Etapa 1 ainda existem
            if (!sessionManager.isSessionValid(request) || !sessionManager.hasEtapa1Data(request)) {
                logger.warn("Dados da Etapa 1 perdidos na sessão");
                return "redirect:/public/agendamento/etapa1";
            }
            
            // Validar dados obrigatórios
            if (data == null || data.trim().isEmpty()) {
                model.addAttribute("error", "Seleção de data é obrigatória");
                return agendamentoEtapa2(model, request, null);
            }
            
            if (horario == null || horario.trim().isEmpty()) {
                model.addAttribute("error", "Seleção de horário é obrigatória");
                return agendamentoEtapa2(model, request, null);
            }
            
            // Salvar dados na sessão usando o SessionManager
            sessionManager.salvarEtapa2(request, data, horario);
            
            logger.info("Dados da Etapa 2 salvos na sessão. Redirecionando para Etapa 3");
            return "redirect:/public/agendamento/etapa3";
            
        } catch (AgendamentoSessionManager.SessionExpiredException e) {
            logger.warn("Sessão expirada durante processamento da Etapa 2");
            model.addAttribute("error", "Sessão expirada. Por favor, reinicie o processo.");
            return "redirect:/public/agendamento/etapa1";
        } catch (Exception e) {
            logger.error("Erro ao processar Etapa 2", e);
            model.addAttribute("error", "Erro interno. Tente novamente.");
            return agendamentoEtapa2(model, request, null);
        }
    }
    
    /**
     * Etapa 3: Confirmação e finalização
     */
    @GetMapping("/public/agendamento/etapa3")
    public String agendamentoEtapa3(Model model, HttpServletRequest request,
                                   @RequestParam(value = "error", required = false) String errorMessage) {
        logger.info("Acessando Etapa 3 do agendamento");
        
        try {
            // Usar FlowController para validação inteligente de acesso
            String redirectUrl = flowController.handleSmartNavigation(request, AgendamentoFlowController.Etapa.ETAPA_3);
            if (redirectUrl != null) {
                return redirectUrl;
            }
            
            // Renovar sessão para manter ativa
            sessionManager.renovarSessao(request);
            
            // Recuperar dados da sessão
            AgendamentoSessionManager.AgendamentoSessionData sessionData = sessionManager.recuperarDados(request);
            
            // Adicionar todos os dados ao model para exibição
            model.addAttribute("paciente", sessionData.getPaciente());
            model.addAttribute("telefone", sessionData.getTelefone());
            model.addAttribute("email", sessionData.getEmail());
            model.addAttribute("dentista", sessionData.getDentista());
            model.addAttribute("data", java.time.LocalDate.parse(sessionData.getData()));
            model.addAttribute("horario", sessionData.getHorario());
            model.addAttribute("titulo", "Etapa 3 - Confirmação");
            
            // Adicionar navegação breadcrumb
            AgendamentoFlowController.NavigationBreadcrumb breadcrumb = 
                flowController.buildBreadcrumb(request, AgendamentoFlowController.Etapa.ETAPA_3);
            model.addAttribute("breadcrumb", breadcrumb);
            
            // Adicionar configuração do reCAPTCHA se habilitado
            if (captchaService.isEnabled()) {
                model.addAttribute("recaptchaEnabled", true);
                model.addAttribute("recaptchaSiteKey", captchaService.getSiteKey());
            } else {
                model.addAttribute("recaptchaEnabled", false);
            }
            
            // Adicionar mensagem de erro de redirecionamento se existir
            if (errorMessage != null && !errorMessage.trim().isEmpty()) {
                model.addAttribute("error", errorMessage);
            }
            
            return "public/agendamento-etapa3";
        } catch (Exception e) {
            logger.error("Erro ao carregar Etapa 3", e);
            model.addAttribute("error", "Erro ao carregar página. Tente novamente.");
            return "redirect:/public/agendamento/etapa1";
        }
    }
    
    /**
     * Finalizar agendamento das etapas
     */
    @PostMapping("/public/agendamento/finalizar")
    public String finalizarAgendamento(HttpServletRequest request,
                                     @RequestParam(name = "g-recaptcha-response", required = false) String recaptchaResponse,
                                     Model model) {
        logger.info("Finalizando agendamento do fluxo de etapas");
        
        try {
            // Verificar se todos os dados estão completos
            if (!sessionManager.hasCompleteData(request)) {
                logger.warn("Dados incompletos na sessão para finalização");
                return "redirect:/public/agendamento/etapa1";
            }
            
            // Recuperar todos os dados da sessão
            AgendamentoSessionManager.AgendamentoSessionData sessionData = sessionManager.recuperarDados(request);
            
            // Validar reCAPTCHA se habilitado
            if (captchaService.isEnabled()) {
                String clientIp = getClientIp(request);
                if (recaptchaResponse == null || !captchaService.validateCaptcha(recaptchaResponse, clientIp)) {
                    model.addAttribute("error", "Verificação de segurança falhou. Tente novamente.");
                    return agendamentoEtapa3(model, request, null);
                }
            }
            
            // Criar dataHora combinando data e horário
            LocalDateTime dataHoraAgendamento;
            try {
                String dataHoraStr = sessionData.getData() + "T" + sessionData.getHorario();
                if (!sessionData.getHorario().contains(":")) {
                    dataHoraStr = sessionData.getData() + "T" + sessionData.getHorario() + ":00";
                }
                dataHoraAgendamento = LocalDateTime.parse(dataHoraStr);
            } catch (Exception e) {
                logger.error("Erro ao parsear data/hora: {} + {}", sessionData.getData(), sessionData.getHorario(), e);
                model.addAttribute("error", "Formato de data/hora inválido");
                return agendamentoEtapa3(model, request, null);
            }
            
            // Validar se não é no passado
            if (dataHoraAgendamento.isBefore(LocalDateTime.now())) {
                model.addAttribute("error", "Não é possível agendar consultas no passado");
                return agendamentoEtapa3(model, request, null);
            }
            
            // Criar agendamento
            Agendamento agendamento = new Agendamento();
            agendamento.setPaciente(sessionData.getPaciente());
            agendamento.setDentista(sessionData.getDentista());
            agendamento.setDataHora(dataHoraAgendamento);
            agendamento.setStatus("AGENDADO");
            agendamento.setObservacao(String.format("Agendamento online - fluxo de etapas (SessionID: %s)", 
                                                  sessionData.getSessionId()));
            agendamento.setTelefoneWhatsapp(sessionData.getTelefone());
            agendamento.setDuracaoMinutos(30);
            
            // Salvar agendamento
            agendamento = agendamentoService.salvar(agendamento);
            
            // Registrar conclusão bem-sucedida do fluxo completo
            Map<String, Object> completionMetadata = new HashMap<>();
            completionMetadata.put("agendamento_id", agendamento.getId());
            completionMetadata.put("dentista", sessionData.getDentista());
            completionMetadata.put("data_agendamento", sessionData.getData());
            completionMetadata.put("horario_agendamento", sessionData.getHorario());
            completionMetadata.put("total_steps", 3);
            featureFlagManager.recordEvent(request, FeatureFlagManager.ABTestEvent.FLOW_COMPLETED, completionMetadata);
            
            // Limpar dados da sessão
            sessionManager.limparSessao(request);
            
            logger.info("Agendamento finalizado com sucesso - ID: {}, SessionID: {}", 
                       agendamento.getId(), sessionData.getSessionId());
            return "redirect:/public/agendamento-confirmado?id=" + agendamento.getId();
            
        } catch (AgendamentoSessionManager.SessionExpiredException e) {
            logger.warn("Sessão expirada durante finalização");
            model.addAttribute("error", "Sessão expirada. Por favor, reinicie o processo.");
            return "redirect:/public/agendamento/etapa1";
        } catch (Exception e) {
            logger.error("Erro ao finalizar agendamento", e);
            model.addAttribute("error", "Erro ao finalizar agendamento. Tente novamente.");
            return agendamentoEtapa3(model, request, null);
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
    @PostMapping("/public/test-simple")
    @ResponseBody
    public String testeSimpleDebug() {
        return "Teste executado com sucesso!";
    }
    
    /**
     * Endpoint para obter configurações do reCAPTCHA
     */
    @Operation(
        summary = "Obter configurações do reCAPTCHA",
        description = "Retorna as configurações necessárias para integrar o reCAPTCHA no frontend"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configurações retornadas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"enabled\": true, \"siteKey\": \"6LeIxAcTAAAAAGG...\"}"))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping(value = "/public/api/recaptcha-config", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRecaptchaConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("enabled", captchaService.isEnabled());
            config.put("siteKey", captchaService.getSiteKey());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(config);
        } catch (Exception e) {
            logger.error("Erro ao obter configurações do reCAPTCHA", e);
            return ResponseEntity.internalServerError().build();
        }
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
            @Parameter(description = "Token do reCAPTCHA", required = false, example = "03AGdBq...")
            @RequestParam(required = false) String captchaToken,
            HttpServletRequest request) {
        
        String clientIp = getClientIp(request);
        logger.info("Tentativa de agendamento público - IP: {}", clientIp);
        
        try {
            // 1. Validação do reCAPTCHA (se habilitado)
            if (captchaService.isEnabled()) {
                if (!captchaService.validateCaptcha(captchaToken, clientIp)) {
                    logger.warn("Captcha inválido do IP: {}", clientIp);
                    return ResponseEntity.badRequest()
                            .contentType(MediaType.TEXT_PLAIN)
                            .body("Captcha inválido. Verifique e tente novamente.");
                }
            }
            
            // 2. Validação e sanitização básica
            logger.info("DEBUG: Validando input - paciente: '{}', dentista: '{}', dataHora: '{}', telefone: '{}'", 
                       paciente, dentista, dataHora, telefone);
            ValidationResult validation = validateAndSanitizeInput(paciente, dentista, dataHora, telefone);
            logger.info("DEBUG: Resultado da validação - válido: {}, mensagem: '{}'", 
                       validation.isValid(), validation.getErrorMessage());
            if (!validation.isValid()) {
                logger.warn("Dados inválidos do IP: {} - {}", clientIp, validation.getErrorMessage());
                return ResponseEntity.badRequest()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(validation.getErrorMessage());
            }
            
            // 3. Rate Limiting por IP
            if (!rateLimitService.isAllowed(clientIp)) {
                logger.warn("Rate limit excedido para IP: {}", clientIp);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Muitas tentativas. Tente novamente em alguns minutos.");
            }
            
            // 4. Validação de horário comercial (depois da validação básica)
            if (!isBusinessHours()) {
                logger.warn("Tentativa de agendamento fora do horário comercial - IP: {}", clientIp);
                return ResponseEntity.badRequest()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Agendamentos só podem ser feitos durante horário comercial (Segunda a Sexta, 8h às 18h).");
            }
            
            // 5. Validação de data/hora
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
            
            // 6. Validação de dentista ativo
            List<String> dentistasAtivos = agendamentoService.listarDentistasAtivos();
            if (!dentistasAtivos.contains(dentista)) {
                logger.warn("Dentista indisponível - IP: {}, Dentista: {}", clientIp, dentista);
                return ResponseEntity.badRequest()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Dentista não disponível para agendamento público");
            }
            
            // 7. Criar agendamento com auditoria
            Agendamento agendamento = createSecureAgendamento(paciente, dentista, dataHoraAgendamento, telefone, clientIp);
            agendamento = agendamentoService.salvar(agendamento);
            
            // 8. Log de auditoria
            logger.info("Agendamento público criado com sucesso - ID: {}, IP: {}, Paciente: {}, Captcha: {}", 
                       agendamento.getId(), clientIp, paciente, captchaService.isEnabled() ? "Validado" : "Desabilitado");
            
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
        
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr != null ? remoteAddr : "127.0.0.1";
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
        // Para testes, permitir bypass do horário comercial
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return true;
        }
        
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
