package com.caracore.cca.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Classe para utilitários de monitoramento de requisições.
 */
@Component
public class RequestMonitoringInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestMonitoringInterceptor.class);

    /**
     * Registra informações importantes sobre erros do sistema
     */
    public void logSystemError(String message, Exception ex) {
        logger.error("Sistema Error: {} - {}", message, ex.getMessage(), ex);
    }

    /**
     * Registra informações sobre performance
     */
    public void logPerformanceIssue(String message) {
        logger.warn("Performance Issue: {}", message);
    }
}
