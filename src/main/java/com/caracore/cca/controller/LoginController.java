package com.caracore.cca.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        
        if (error != null) {
            logger.warn("Erro de login detectado");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            logger.info("Autenticação atual: {}", auth);
            model.addAttribute("error", "Nome de usuário ou senha inválidos.");
        }

        if (logout != null) {
            logger.info("Logout realizado com sucesso");
            model.addAttribute("message", "Logout realizado com sucesso.");
        }
        
        logger.info("Página de login solicitada");
        return "login";
    }
}
