package com.caracore.cca.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para a página principal de administração.
 * Redireciona para as funcionalidades administrativas principais.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    /**
     * Página principal de administração.
     * Por enquanto redireciona para gestão de usuários.
     */
    @GetMapping
    public String admin() {
        // Redireciona para a gestão de usuários como página principal de admin
        return "redirect:/usuarios";
    }
    
    /**
     * Página de administração do sistema (alternativa)
     */
    @GetMapping("/sistema")
    public String adminSistema(Model model) {
        model.addAttribute("titulo", "Administração do Sistema");
        return "admin/sistema";
    }
}
