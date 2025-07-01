package com.caracore.cca.controller;

import com.caracore.cca.util.UserActivityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador para a página de acesso negado.
 */
@Controller
public class AcessoNegadoController {

    private static final Logger logger = LoggerFactory.getLogger(AcessoNegadoController.class);
    
    @Autowired
    public UserActivityLogger userActivityLogger;
    
    /**
     * Redireciona para a página de acesso negado.
     * 
     * @return O nome da view acesso-negado
     */
    @GetMapping("/acesso-negado")
    public String acessoNegado(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "usuário anônimo";
        String requestedUrl = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        if (requestedUrl != null) {
            logger.warn("Acesso negado para o usuário {} ao tentar acessar {}", username, requestedUrl);
            userActivityLogger.logActivity("ACESSO_NEGADO", "Tentativa de acesso a recurso não autorizado", 
                                          "URL: " + requestedUrl, "Acesso negado");
        } else {
            logger.warn("Acesso negado para o usuário {}", username);
            userActivityLogger.logActivity("ACESSO_NEGADO", "Tentativa de acesso a recurso não autorizado", 
                                          null, "Acesso negado");
        }
        
        return "acesso-negado";
    }
}
