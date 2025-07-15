package com.caracore.cca.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Gerenciador de Feature Flags para A/B Testing e controle de funcionalidades.
 * Permite alternar entre fluxo único e multi-etapas dinamicamente.
 */
@Component
public class FeatureFlagManager {
    
    private static final Logger logger = LoggerFactory.getLogger(FeatureFlagManager.class);
    
    // Configurações via application.yml
    @Value("${cca.feature-flags.multi-step-flow.enabled:true}")
    private boolean multiStepFlowEnabled;
    
    @Value("${cca.feature-flags.ab-testing.enabled:false}")
    private boolean abTestingEnabled;
    
    @Value("${cca.feature-flags.ab-testing.multi-step-percentage:50}")
    private int multiStepPercentage;
    
    @Value("${cca.feature-flags.debug-mode:false}")
    private boolean debugMode;
    
    private final Random random = new Random();
    
    // Feature Flags disponíveis
    public enum FeatureFlag {
        MULTI_STEP_FLOW("multi-step-flow", "Fluxo de agendamento em múltiplas etapas"),
        AB_TESTING("ab-testing", "Teste A/B entre fluxos"),
        RECAPTCHA_V3("recaptcha-v3", "Google reCAPTCHA v3"),
        WHATSAPP_INTEGRATION("whatsapp-integration", "Integração com WhatsApp"),
        EMAIL_NOTIFICATIONS("email-notifications", "Notificações por email"),
        SMS_NOTIFICATIONS("sms-notifications", "Notificações por SMS"),
        ADVANCED_ANALYTICS("advanced-analytics", "Analytics avançado"),
        CALENDAR_SYNC("calendar-sync", "Sincronização com calendário");
        
        private final String key;
        private final String description;
        
        FeatureFlag(String key, String description) {
            this.key = key;
            this.description = description;
        }
        
        public String getKey() { return key; }
        public String getDescription() { return description; }
    }
    
    /**
     * Verifica se uma feature flag está habilitada
     */
    public boolean isEnabled(FeatureFlag flag) {
        switch (flag) {
            case MULTI_STEP_FLOW:
                return multiStepFlowEnabled;
            case AB_TESTING:
                return abTestingEnabled;
            default:
                return false; // Outras flags desabilitadas por padrão
        }
    }
    
    /**
     * Determina qual fluxo usar baseado no A/B Testing
     */
    public FlowType determineFlowType(HttpServletRequest request) {
        // Se A/B Testing está desabilitado, usar configuração padrão
        if (!abTestingEnabled) {
            return multiStepFlowEnabled ? FlowType.MULTI_STEP : FlowType.SINGLE_PAGE;
        }
        
        // Obter ou gerar assignment para este usuário
        FlowAssignment assignment = getOrCreateFlowAssignment(request);
        
        logger.debug("Fluxo determinado para IP {}: {} (Assignment ID: {})", 
                    getClientIp(request), assignment.getFlowType(), assignment.getAssignmentId());
        
        return assignment.getFlowType();
    }
    
    /**
     * Força um tipo de fluxo específico (para testes)
     */
    public void forceFlowType(HttpServletRequest request, FlowType flowType) {
        if (debugMode) {
            String sessionKey = "forced_flow_type";
            request.getSession().setAttribute(sessionKey, flowType);
            logger.info("Fluxo forçado para {}: {}", getClientIp(request), flowType);
        }
    }
    
    /**
     * Obtém ou cria assignment de fluxo para o usuário
     */
    private FlowAssignment getOrCreateFlowAssignment(HttpServletRequest request) {
        String sessionKey = "flow_assignment";
        FlowAssignment assignment = (FlowAssignment) request.getSession().getAttribute(sessionKey);
        
        // Se já existe assignment, usar o mesmo
        if (assignment != null) {
            return assignment;
        }
        
        // Verificar se há um fluxo forçado (para debug)
        if (debugMode) {
            FlowType forcedType = (FlowType) request.getSession().getAttribute("forced_flow_type");
            if (forcedType != null) {
                assignment = new FlowAssignment(forcedType, "FORCED", LocalDateTime.now());
                request.getSession().setAttribute(sessionKey, assignment);
                return assignment;
            }
        }
        
        // Criar novo assignment baseado na porcentagem configurada
        FlowType assignedFlow = (random.nextInt(100) < multiStepPercentage) ? 
                               FlowType.MULTI_STEP : FlowType.SINGLE_PAGE;
        
        String assignmentId = generateAssignmentId(request);
        assignment = new FlowAssignment(assignedFlow, assignmentId, LocalDateTime.now());
        
        // Salvar na sessão
        request.getSession().setAttribute(sessionKey, assignment);
        
        logger.info("Novo assignment criado - IP: {}, Fluxo: {}, ID: {}", 
                   getClientIp(request), assignedFlow, assignmentId);
        
        return assignment;
    }
    
    /**
     * Gera ID único para o assignment
     */
    private String generateAssignmentId(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String sessionId = request.getSession().getId();
        long timestamp = System.currentTimeMillis();
        
        return String.format("AB_%s_%s_%d", 
                           clientIp.replaceAll("[^0-9]", ""), 
                           sessionId.substring(0, Math.min(8, sessionId.length())), 
                           timestamp % 100000);
    }
    
    /**
     * Coleta métricas do A/B Testing
     */
    public void recordEvent(HttpServletRequest request, ABTestEvent event, Map<String, Object> metadata) {
        if (!abTestingEnabled) {
            return;
        }
        
        FlowAssignment assignment = getOrCreateFlowAssignment(request);
        
        ABTestMetric metric = new ABTestMetric(
            assignment.getAssignmentId(),
            assignment.getFlowType(),
            event,
            getClientIp(request),
            LocalDateTime.now(),
            metadata
        );
        
        // Log da métrica (em produção, salvaria em banco de dados)
        logger.info("AB Test Metric: {}", metric);
        
        // Aqui seria implementada a persistência das métricas
        // Exemplo: abTestMetricsService.save(metric);
    }
    
    /**
     * Obtém informações de debug das feature flags
     */
    public Map<String, Object> getDebugInfo(HttpServletRequest request) {
        Map<String, Object> info = new HashMap<>();
        
        info.put("multi_step_flow_enabled", multiStepFlowEnabled);
        info.put("ab_testing_enabled", abTestingEnabled);
        info.put("multi_step_percentage", multiStepPercentage);
        info.put("debug_mode", debugMode);
        info.put("determined_flow", determineFlowType(request));
        
        FlowAssignment assignment = (FlowAssignment) request.getSession().getAttribute("flow_assignment");
        if (assignment != null) {
            info.put("flow_assignment", Map.of(
                "flow_type", assignment.getFlowType(),
                "assignment_id", assignment.getAssignmentId(),
                "created_at", assignment.getCreatedAt().toString()
            ));
        }
        
        // Status de todas as feature flags
        Map<String, Object> flagsStatus = new HashMap<>();
        for (FeatureFlag flag : FeatureFlag.values()) {
            flagsStatus.put(flag.getKey(), isEnabled(flag));
        }
        info.put("feature_flags", flagsStatus);
        
        return info;
    }
    
    /**
     * Reseta assignment (para testes)
     */
    public void resetAssignment(HttpServletRequest request) {
        if (debugMode) {
            request.getSession().removeAttribute("flow_assignment");
            request.getSession().removeAttribute("forced_flow_type");
            logger.info("Assignment resetado para IP: {}", getClientIp(request));
        }
    }
    
    /**
     * Obtém IP do cliente
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    // Enums e Classes de dados
    
    public enum FlowType {
        SINGLE_PAGE("single-page", "Página Única", "/public/agendamento"),
        MULTI_STEP("multi-step", "Múltiplas Etapas", "/public/agendamento/etapa1");
        
        private final String key;
        private final String displayName;
        private final String entryUrl;
        
        FlowType(String key, String displayName, String entryUrl) {
            this.key = key;
            this.displayName = displayName;
            this.entryUrl = entryUrl;
        }
        
        public String getKey() { return key; }
        public String getDisplayName() { return displayName; }
        public String getEntryUrl() { return entryUrl; }
    }
    
    public enum ABTestEvent {
        FLOW_STARTED("flow_started", "Fluxo iniciado"),
        STEP_COMPLETED("step_completed", "Etapa completada"),
        FLOW_ABANDONED("flow_abandoned", "Fluxo abandonado"),
        FLOW_COMPLETED("flow_completed", "Fluxo completado"),
        ERROR_OCCURRED("error_occurred", "Erro ocorreu"),
        VALIDATION_FAILED("validation_failed", "Validação falhou");
        
        private final String key;
        private final String description;
        
        ABTestEvent(String key, String description) {
            this.key = key;
            this.description = description;
        }
        
        public String getKey() { return key; }
        public String getDescription() { return description; }
    }
    
    public static class FlowAssignment {
        private final FlowType flowType;
        private final String assignmentId;
        private final LocalDateTime createdAt;
        
        public FlowAssignment(FlowType flowType, String assignmentId, LocalDateTime createdAt) {
            this.flowType = flowType;
            this.assignmentId = assignmentId;
            this.createdAt = createdAt;
        }
        
        public FlowType getFlowType() { return flowType; }
        public String getAssignmentId() { return assignmentId; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        
        @Override
        public String toString() {
            return String.format("FlowAssignment{flowType=%s, assignmentId='%s', createdAt=%s}", 
                               flowType, assignmentId, createdAt);
        }
    }
    
    public static class ABTestMetric {
        private final String assignmentId;
        private final FlowType flowType;
        private final ABTestEvent event;
        private final String clientIp;
        private final LocalDateTime timestamp;
        private final Map<String, Object> metadata;
        
        public ABTestMetric(String assignmentId, FlowType flowType, ABTestEvent event, 
                          String clientIp, LocalDateTime timestamp, Map<String, Object> metadata) {
            this.assignmentId = assignmentId;
            this.flowType = flowType;
            this.event = event;
            this.clientIp = clientIp;
            this.timestamp = timestamp;
            this.metadata = metadata != null ? metadata : new HashMap<>();
        }
        
        // Getters
        public String getAssignmentId() { return assignmentId; }
        public FlowType getFlowType() { return flowType; }
        public ABTestEvent getEvent() { return event; }
        public String getClientIp() { return clientIp; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public Map<String, Object> getMetadata() { return metadata; }
        
        @Override
        public String toString() {
            return String.format("ABTestMetric{assignmentId='%s', flowType=%s, event=%s, clientIp='%s', timestamp=%s, metadata=%s}", 
                               assignmentId, flowType, event, clientIp, timestamp, metadata);
        }
    }
}
