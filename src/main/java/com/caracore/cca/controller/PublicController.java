package com.caracore.cca.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para páginas públicas (sem autenticação)
 */
@Controller
@RequestMapping("/public")
public class PublicController {

    /**
     * Página inicial do agendamento online
     */
    @GetMapping("/agendamento-landing")
    public String agendamentoLanding() {
        return "public/agendamento-landing";
    }

    /**
     * Redirect para facilitar o acesso
     */
    @GetMapping("/")
    public String publicIndex() {
        return "redirect:/public/agendamento-landing";
    }
}
