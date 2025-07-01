package com.caracore.cca.util;

import com.caracore.cca.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Utilitário para registrar atividades de usuários no sistema.
 * Esta classe facilita o logging consistente de ações dos usuários
 * para fins de auditoria, segurança e análise de uso.
 */
@Component
public class UserActivityLogger {

    private static final Logger logger = LoggerFactory.getLogger("USER_ACTIVITY");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Registra uma atividade de usuário no sistema
     * 
     * @param action Tipo de ação realizada (LOGIN, LOGOUT, CREATE, etc.)
     * @param details Detalhes da ação
     * @param affectedEntities Entidades afetadas pela ação
     * @param observations Observações adicionais
     */
    public void logActivity(String action, String details, String affectedEntities, String observations) {
        String username = getCurrentUsername().orElse("anonymous");
        String timestamp = LocalDateTime.now().format(formatter);
        String ip = getClientIp().orElse("unknown");
        String userAgent = getUserAgent().orElse("unknown");
        
        StringBuilder logEntry = new StringBuilder();
        logEntry.append(String.format("[%s] %s - %s", timestamp, username, action)).append("\n");
        logEntry.append(String.format("IP: %s", ip)).append("\n");
        logEntry.append(String.format("Dispositivo: %s", userAgent)).append("\n");
        logEntry.append(String.format("Detalhes: %s", details)).append("\n");
        
        if (affectedEntities != null && !affectedEntities.isEmpty()) {
            logEntry.append(String.format("Entidades afetadas: %s", affectedEntities)).append("\n");
        }
        
        if (observations != null && !observations.isEmpty()) {
            logEntry.append(String.format("Observações: %s", observations)).append("\n");
        }
        
        logger.info(logEntry.toString());
    }
    
    /**
     * Registra um login de usuário
     * 
     * @param username Nome de usuário
     * @param success Se o login foi bem-sucedido
     * @param observation Observações adicionais
     */
    public void logLogin(String username, boolean success, String observation) {
        String action = success ? "LOGIN" : "FAILED_LOGIN";
        String details = success ? "Login bem-sucedido" : "Tentativa de login falhou";
        logActivity(action, details, null, observation);
    }
    
    /**
     * Registra um logout de usuário
     * 
     * @param observation Observações adicionais
     */
    public void logLogout(String observation) {
        logActivity("LOGOUT", "Logout do sistema", null, observation);
    }
    
    /**
     * Registra a criação de uma entidade
     * 
     * @param entityType Tipo da entidade (Usuario, Paciente, etc.)
     * @param entityId ID da entidade
     * @param entityName Nome descritivo da entidade
     * @param observation Observações adicionais
     */
    public void logCreate(String entityType, Long entityId, String entityName, String observation) {
        String action = "CREATE_" + entityType.toUpperCase();
        String details = "Criou novo registro de " + entityType.toLowerCase();
        String affected = entityType + " ID " + entityId + " (" + entityName + ")";
        logActivity(action, details, affected, observation);
    }
    
    /**
     * Registra a modificação de uma entidade
     * 
     * @param entityType Tipo da entidade (Usuario, Paciente, etc.)
     * @param entityId ID da entidade
     * @param entityName Nome descritivo da entidade
     * @param observation Observações adicionais
     */
    public void logModify(String entityType, Long entityId, String entityName, String observation) {
        String action = "MODIFY_" + entityType.toUpperCase();
        String details = "Modificou registro de " + entityType.toLowerCase();
        String affected = entityType + " ID " + entityId + " (" + entityName + ")";
        logActivity(action, details, affected, observation);
    }
    
    /**
     * Registra a exclusão de uma entidade
     * 
     * @param entityType Tipo da entidade (Usuario, Paciente, etc.)
     * @param entityId ID da entidade
     * @param entityName Nome descritivo da entidade
     * @param observation Observações adicionais
     */
    public void logDelete(String entityType, Long entityId, String entityName, String observation) {
        String action = "DELETE_" + entityType.toUpperCase();
        String details = "Excluiu registro de " + entityType.toLowerCase();
        String affected = entityType + " ID " + entityId + " (" + entityName + ")";
        logActivity(action, details, affected, observation);
    }
    
    /**
     * Obtém o nome de usuário atual do contexto de segurança
     */
    private Optional<String> getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return Optional.of(auth.getName());
        }
        return Optional.empty();
    }
    
    /**
     * Obtém o IP do cliente atual
     */
    private Optional<String> getClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return Optional.of(ip);
            }
        } catch (Exception e) {
            logger.debug("Erro ao obter IP do cliente", e);
        }
        return Optional.empty();
    }
    
    /**
     * Obtém o User-Agent do cliente atual
     */
    private Optional<String> getUserAgent() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String userAgent = request.getHeader("User-Agent");
                return Optional.ofNullable(userAgent);
            }
        } catch (Exception e) {
            logger.debug("Erro ao obter User-Agent", e);
        }
        return Optional.empty();
    }
}
