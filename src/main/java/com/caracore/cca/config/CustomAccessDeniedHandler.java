package com.caracore.cca.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Manipulador personalizado para acessos negados.
 * Registra informações detalhadas e redireciona para a página de acesso negado.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            logger.warn("Usuário: {} tentou acessar uma URL protegida: {}", 
                    auth.getName(), request.getRequestURI());
        } else {
            logger.warn("Acesso anônimo negado para a URL: {}", request.getRequestURI());
        }
        
        // Forward para o controller de acesso negado em vez de redirect
        // Isso garante que o Spring use o controller e não tente encontrar um recurso estático
        request.getRequestDispatcher("/acesso-negado").forward(request, response);
    }
}
