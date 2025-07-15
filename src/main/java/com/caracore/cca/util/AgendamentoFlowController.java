package com.caracore.cca.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de fluxo inteligente para navegação entre etapas do agendamento.
 * Implementa redirecionamentos dinâmicos, validação de sequência e controle de acesso.
 */
@Component
public class AgendamentoFlowController {
    
    private static final Logger logger = LoggerFactory.getLogger(AgendamentoFlowController.class);
    
    @Autowired
    private AgendamentoSessionManager sessionManager;
    
    // Definição do fluxo de etapas
    public enum Etapa {
        ETAPA_1(1, "/public/agendamento/etapa1", "Dados Pessoais"),
        ETAPA_2(2, "/public/agendamento/etapa2", "Seleção de Horário"),
        ETAPA_3(3, "/public/agendamento/etapa3", "Confirmação"),
        FINALIZADO(4, "/public/agendamento-confirmado", "Agendamento Confirmado");
        
        private final int numero;
        private final String url;
        private final String titulo;
        
        Etapa(int numero, String url, String titulo) {
            this.numero = numero;
            this.url = url;
            this.titulo = titulo;
        }
        
        public int getNumero() { return numero; }
        public String getUrl() { return url; }
        public String getTitulo() { return titulo; }
        
        public static Etapa fromNumero(int numero) {
            for (Etapa etapa : values()) {
                if (etapa.numero == numero) {
                    return etapa;
                }
            }
            return ETAPA_1; // Default
        }
    }
    
    /**
     * Valida se o usuário pode acessar uma etapa específica
     */
    public FlowValidationResult validateAccess(HttpServletRequest request, Etapa etapaDesejada) {
        logger.debug("Validando acesso à {} - IP: {}", etapaDesejada.getTitulo(), getClientIp(request));
        
        // Sempre permitir acesso à Etapa 1
        if (etapaDesejada == Etapa.ETAPA_1) {
            return FlowValidationResult.permitido();
        }
        
        // Verificar se a sessão é válida
        if (!sessionManager.isSessionValid(request)) {
            logger.info("Sessão inválida/expirada - redirecionando para Etapa 1");
            return FlowValidationResult.negado("Sessão expirada", Etapa.ETAPA_1);
        }
        
        // Verificar sequência de etapas
        switch (etapaDesejada) {
            case ETAPA_1:
                // Sempre permitido
                break;
                
            case ETAPA_2:
                if (!sessionManager.hasEtapa1Data(request)) {
                    logger.warn("Tentativa de acesso à Etapa 2 sem dados da Etapa 1");
                    return FlowValidationResult.negado("Dados da Etapa 1 ausentes", Etapa.ETAPA_1);
                }
                break;
                
            case ETAPA_3:
                if (!sessionManager.hasEtapa1Data(request)) {
                    logger.warn("Tentativa de acesso à Etapa 3 sem dados da Etapa 1");
                    return FlowValidationResult.negado("Dados da Etapa 1 ausentes", Etapa.ETAPA_1);
                }
                if (!sessionManager.hasEtapa2Data(request)) {
                    logger.warn("Tentativa de acesso à Etapa 3 sem dados da Etapa 2");
                    return FlowValidationResult.negado("Dados da Etapa 2 ausentes", Etapa.ETAPA_2);
                }
                break;
                
            case FINALIZADO:
                if (!sessionManager.hasCompleteData(request)) {
                    logger.warn("Tentativa de finalização sem dados completos");
                    return FlowValidationResult.negado("Dados incompletos", getNextRequiredStep(request));
                }
                break;
        }
        
        return FlowValidationResult.permitido();
    }
    
    /**
     * Determina a próxima etapa recomendada baseada no estado atual da sessão
     */
    public Etapa getNextRecommendedStep(HttpServletRequest request) {
        if (!sessionManager.isSessionValid(request)) {
            return Etapa.ETAPA_1;
        }
        
        if (!sessionManager.hasEtapa1Data(request)) {
            return Etapa.ETAPA_1;
        }
        
        if (!sessionManager.hasEtapa2Data(request)) {
            return Etapa.ETAPA_2;
        }
        
        return Etapa.ETAPA_3; // Pronto para confirmação
    }
    
    /**
     * Determina a próxima etapa obrigatória que está faltando
     */
    private Etapa getNextRequiredStep(HttpServletRequest request) {
        if (!sessionManager.hasEtapa1Data(request)) {
            return Etapa.ETAPA_1;
        }
        if (!sessionManager.hasEtapa2Data(request)) {
            return Etapa.ETAPA_2;
        }
        return Etapa.ETAPA_3;
    }
    
    /**
     * Gera URL de redirecionamento baseada na validação de fluxo
     */
    public String buildRedirectUrl(FlowValidationResult validationResult) {
        if (validationResult.isPermitido()) {
            return null; // Sem redirecionamento necessário
        }
        
        String baseUrl = "redirect:" + validationResult.getEtapaRedirecionamento().getUrl();
        
        // Adicionar parâmetros de erro se necessário
        if (validationResult.getMensagemErro() != null) {
            baseUrl += "?error=" + java.net.URLEncoder.encode(
                validationResult.getMensagemErro(), 
                java.nio.charset.StandardCharsets.UTF_8
            );
        }
        
        return baseUrl;
    }
    
    /**
     * Constrói navegação breadcrumb para a interface
     */
    public NavigationBreadcrumb buildBreadcrumb(HttpServletRequest request, Etapa etapaAtual) {
        NavigationBreadcrumb breadcrumb = new NavigationBreadcrumb();
        
        // Etapa 1
        breadcrumb.addStep(Etapa.ETAPA_1, 
                          sessionManager.hasEtapa1Data(request) ? StepStatus.COMPLETED : 
                          (etapaAtual == Etapa.ETAPA_1 ? StepStatus.CURRENT : StepStatus.PENDING));
        
        // Etapa 2
        breadcrumb.addStep(Etapa.ETAPA_2, 
                          sessionManager.hasEtapa2Data(request) ? StepStatus.COMPLETED : 
                          (etapaAtual == Etapa.ETAPA_2 ? StepStatus.CURRENT : StepStatus.PENDING));
        
        // Etapa 3
        breadcrumb.addStep(Etapa.ETAPA_3, 
                          sessionManager.hasCompleteData(request) && etapaAtual != Etapa.ETAPA_3 ? StepStatus.COMPLETED : 
                          (etapaAtual == Etapa.ETAPA_3 ? StepStatus.CURRENT : StepStatus.PENDING));
        
        return breadcrumb;
    }
    
    /**
     * Processa navegação inteligente - permite voltar etapas sem perder dados
     */
    public String handleSmartNavigation(HttpServletRequest request, Etapa etapaDesejada) {
        FlowValidationResult validation = validateAccess(request, etapaDesejada);
        
        if (!validation.isPermitido()) {
            logger.info("Redirecionamento necessário: {} -> {}", 
                       etapaDesejada.getTitulo(), validation.getEtapaRedirecionamento().getTitulo());
            return buildRedirectUrl(validation);
        }
        
        // Renovar sessão em navegação válida
        sessionManager.renovarSessao(request);
        
        return null; // Permitir acesso normal
    }
    
    /**
     * Limpa dados de etapas posteriores quando o usuário volta
     */
    public void cleanForwardStepsOnBackNavigation(HttpServletRequest request, Etapa etapaAtual) {
        AgendamentoSessionManager.AgendamentoSessionData sessionData = sessionManager.recuperarDados(request);
        
        if (sessionData == null) return;
        
        // Se voltou para Etapa 1, limpar dados das etapas 2 e 3
        if (etapaAtual == Etapa.ETAPA_1) {
            sessionManager.limparDadosEtapa2(request);
            logger.info("Dados das etapas posteriores limpos - volta para Etapa 1");
        }
        // Se voltou para Etapa 2, limpar apenas dados da etapa 3
        else if (etapaAtual == Etapa.ETAPA_2) {
            sessionManager.limparDadosEtapa3(request);
            logger.info("Dados da Etapa 3 limpos - volta para Etapa 2");
        }
    }
    
    /**
     * Obtém informações de debug do fluxo atual
     */
    public Map<String, Object> getFlowDebugInfo(HttpServletRequest request) {
        Map<String, Object> info = new HashMap<>();
        
        info.put("session_valid", sessionManager.isSessionValid(request));
        info.put("has_etapa1_data", sessionManager.hasEtapa1Data(request));
        info.put("has_etapa2_data", sessionManager.hasEtapa2Data(request));
        info.put("has_complete_data", sessionManager.hasCompleteData(request));
        info.put("next_recommended_step", getNextRecommendedStep(request).getTitulo());
        info.put("client_ip", getClientIp(request));
        info.put("timestamp", LocalDateTime.now().toString());
        
        return info;
    }
    
    /**
     * Utilitário para obter IP do cliente
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
    
    // Classes auxiliares
    
    public static class FlowValidationResult {
        private final boolean permitido;
        private final String mensagemErro;
        private final Etapa etapaRedirecionamento;
        
        private FlowValidationResult(boolean permitido, String mensagemErro, Etapa etapaRedirecionamento) {
            this.permitido = permitido;
            this.mensagemErro = mensagemErro;
            this.etapaRedirecionamento = etapaRedirecionamento;
        }
        
        public static FlowValidationResult permitido() {
            return new FlowValidationResult(true, null, null);
        }
        
        public static FlowValidationResult negado(String mensagem, Etapa redirecionarPara) {
            return new FlowValidationResult(false, mensagem, redirecionarPara);
        }
        
        public boolean isPermitido() { return permitido; }
        public String getMensagemErro() { return mensagemErro; }
        public Etapa getEtapaRedirecionamento() { return etapaRedirecionamento; }
    }
    
    public static class NavigationBreadcrumb {
        private final Map<Etapa, StepStatus> steps = new HashMap<>();
        
        public void addStep(Etapa etapa, StepStatus status) {
            steps.put(etapa, status);
        }
        
        public Map<Etapa, StepStatus> getSteps() { return steps; }
        
        public StepStatus getStatus(Etapa etapa) {
            return steps.getOrDefault(etapa, StepStatus.PENDING);
        }
    }
    
    public enum StepStatus {
        PENDING("pending", "Pendente"),
        CURRENT("current", "Atual"),
        COMPLETED("completed", "Concluído");
        
        private final String cssClass;
        private final String label;
        
        StepStatus(String cssClass, String label) {
            this.cssClass = cssClass;
            this.label = label;
        }
        
        public String getCssClass() { return cssClass; }
        public String getLabel() { return label; }
    }
}
