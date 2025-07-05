package com.caracore.cca.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para a página de configurações do sistema.
 * Apenas administradores podem acessar essas configurações.
 */
@Controller
@RequestMapping("/configuracoes")
@PreAuthorize("hasRole('ADMIN')")
public class ConfiguracoesController {

    /**
     * Página principal de configurações do sistema.
     */
    @GetMapping
    public String configuracoes(Model model) {
        model.addAttribute("titulo", "Configurações do Sistema");
        model.addAttribute("activeLink", "configuracoes");
        return "configuracoes/index";
    }
}
