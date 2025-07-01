package com.caracore.cca.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador responsável pelo dashboard principal do sistema.
 * Este é o ponto de entrada após o login bem-sucedido.
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    
    /**
     * Exibe o dashboard principal adaptado ao perfil do usuário.
     * O dashboard mostra informações diferentes dependendo do perfil (ADMIN, DENTIST, RECEPTIONIST, PATIENT).
     */
    @GetMapping
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        logger.info("Usuário {} acessando o dashboard", username);
        
        // Adiciona informações do usuário ao modelo para exibição no template
        model.addAttribute("username", username);
        model.addAttribute("roles", auth.getAuthorities());
        model.addAttribute("isAdmin", auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("isDentist", auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DENTIST")));
        model.addAttribute("isReceptionist", auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RECEPTIONIST")));
        model.addAttribute("isPatient", auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT")));
        
        return "dashboard";
    }
}
