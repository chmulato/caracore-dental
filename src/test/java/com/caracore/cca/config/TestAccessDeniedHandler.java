package com.caracore.cca.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Manipulador de acesso negado específico para testes.
 * Sempre retorna 403 Forbidden em vez de redirecionar.
 */
public class TestAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(TestAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            logger.info("[TEST] Usuário: {} tentou acessar uma URL protegida: {}", 
                    auth.getName(), request.getRequestURI());
        } else {
            logger.info("[TEST] Acesso anônimo negado para a URL: {}", request.getRequestURI());
        }
        
        // Definir status 403 sem redirecionamento, para uso em testes
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado");
    }
}
