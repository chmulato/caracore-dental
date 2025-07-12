package com.caracore.cca.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    @Override
    public void handle(HttpServletRequest request, 
                      HttpServletResponse response,
                      AccessDeniedException accessDeniedException) 
            throws IOException, ServletException {
        
        logger.warn("Acesso negado para requisição: {}", request.getRequestURI());
        
        if (request.getHeader("X-Requested-With") != null && 
            request.getHeader("X-Requested-With").equals("XMLHttpRequest")) {
            // Requisição AJAX
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado");
        } else {
            // Requisição normal
            response.sendRedirect("/acesso-negado");
        }
    }
}
