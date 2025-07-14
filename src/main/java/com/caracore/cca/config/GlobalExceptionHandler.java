package com.caracore.cca.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador global para tratamento de exceções da aplicação.
 * Captura erros de template, runtime exceptions e outras exceções não tratadas.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    @Autowired
    private RequestMonitoringInterceptor monitoring;

    /**
     * Trata erros de processamento de templates Thymeleaf
     */
    @ExceptionHandler({TemplateInputException.class, TemplateProcessingException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleTemplateException(Exception ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        
        logger.error("[ERROR-{}] Erro de template na URL: {} - {}", errorId, request.getRequestURL(), ex.getMessage(), ex);
        monitoring.logSystemError("Template Error - " + request.getRequestURL(), ex);
        
        ModelAndView mav = new ModelAndView("error/template-error");
        mav.addObject("errorId", errorId);
        mav.addObject("timestamp", timestamp);
        mav.addObject("requestUrl", request.getRequestURL().toString());
        mav.addObject("userMessage", "Ocorreu um erro na renderização da página. Nossa equipe foi notificada.");
        
        // Em desenvolvimento, mostra mais detalhes
        if ("local".equals(activeProfile) || "dev".equals(activeProfile)) {
            mav.addObject("technicalDetails", ex.getMessage());
            mav.addObject("exceptionType", ex.getClass().getSimpleName());
        }
        
        return mav;
    }

    /**
     * Trata erros de autenticação/autorização em templates
     */
    @ExceptionHandler(org.springframework.beans.NotReadablePropertyException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) 
    public ModelAndView handleAuthenticationTemplateError(Exception ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        
        logger.error("[ERROR-{}] Erro de autenticação em template na URL: {} - {}", errorId, request.getRequestURL(), ex.getMessage(), ex);
        
        // Se o erro está relacionado a principal.authorities, redireciona para login
        if (ex.getMessage().contains("principal.authorities")) {
            logger.info("Erro de autenticação detectado, redirecionando para login");
            return new ModelAndView("redirect:/login?error=auth");
        }
        
        ModelAndView mav = new ModelAndView("error/auth-error");
        mav.addObject("errorId", errorId);
        mav.addObject("timestamp", timestamp);
        mav.addObject("userMessage", "Erro de autenticação. Por favor, faça login novamente.");
        
        return mav;
    }

    /**
     * Trata erros gerais de runtime
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        
        logger.error("[ERROR-{}] Erro de runtime na URL: {} - {}", errorId, request.getRequestURL(), ex.getMessage(), ex);
        
        ModelAndView mav = new ModelAndView("error/general-error");
        mav.addObject("errorId", errorId);
        mav.addObject("timestamp", timestamp);
        mav.addObject("requestUrl", request.getRequestURL().toString());
        mav.addObject("userMessage", "Ocorreu um erro interno. Nossa equipe foi notificada automaticamente.");
        
        // Em desenvolvimento, mostra stack trace
        if ("local".equals(activeProfile) || "dev".equals(activeProfile)) {
            mav.addObject("technicalDetails", ex.getMessage());
            mav.addObject("stackTrace", getStackTraceAsString(ex));
        }
        
        return mav;
    }

    /**
     * Trata exceções gerais não capturadas
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        
        logger.error("[ERROR-{}] Erro geral na URL: {} - {}", errorId, request.getRequestURL(), ex.getMessage(), ex);
        
        ModelAndView mav = new ModelAndView("error/general-error");
        mav.addObject("errorId", errorId);
        mav.addObject("timestamp", timestamp);
        mav.addObject("requestUrl", request.getRequestURL().toString());
        mav.addObject("userMessage", "Ocorreu um erro inesperado. Tente novamente em alguns minutos.");
        
        // Em desenvolvimento, mostra detalhes técnicos
        if ("local".equals(activeProfile) || "dev".equals(activeProfile)) {
            mav.addObject("technicalDetails", ex.getMessage());
            mav.addObject("exceptionType", ex.getClass().getSimpleName());
        }
        
        return mav;
    }

    /**
     * Gera um ID único para o erro para facilitar rastreamento
     */
    private String generateErrorId() {
        return "ERR-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    /**
     * Converte stack trace em string para debug
     */
    private String getStackTraceAsString(Exception ex) {
        java.io.StringWriter sw = new java.io.StringWriter();
        ex.printStackTrace(new java.io.PrintWriter(sw));
        String stackTrace = sw.toString();
        
        // Limita o tamanho do stack trace para não sobrecarregar a página
        if (stackTrace.length() > 2000) {
            stackTrace = stackTrace.substring(0, 2000) + "\n... (truncado)";
        }
        
        return stackTrace;
    }
}
