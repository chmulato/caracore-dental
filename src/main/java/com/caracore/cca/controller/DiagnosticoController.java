package com.caracore.cca.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador de diagnóstico para ajudar a identificar problemas de autenticação e autorização.
 * Este controlador deve ser usado apenas para fins de diagnóstico e debug.
 */
@Controller
@RequestMapping("/diagnostico")
public class DiagnosticoController {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticoController.class);

    /**
     * Exibe informações detalhadas sobre o usuário autenticado e suas permissões.
     * 
     * @param model O modelo a ser preenchido com informações de diagnóstico
     * @return Uma resposta JSON com informações de diagnóstico
     */
    @GetMapping("/auth-info")
    @ResponseBody
    public Map<String, Object> authInfo() {
        Map<String, Object> info = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null) {
            info.put("status", "Não autenticado");
            return info;
        }
        
        info.put("nome", auth.getName());
        info.put("autenticado", auth.isAuthenticated());
        info.put("tipo_autenticacao", auth.getClass().getSimpleName());
        
        // Coletar todas as autoridades/roles
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        info.put("roles_brutas", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        // Verificar roles específicas
        info.put("tem_role_admin", authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        
        info.put("tem_role_dentist", authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DENTIST")));
        
        info.put("tem_role_receptionist", authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RECEPTIONIST")));
        
        info.put("tem_role_patient", authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT")));

        // Adicionar verificação de acesso a endpoints específicos
        info.put("pode_listar_pacientes", 
                authorities.stream().anyMatch(a -> 
                    a.getAuthority().equals("ROLE_ADMIN") || 
                    a.getAuthority().equals("ROLE_DENTIST") || 
                    a.getAuthority().equals("ROLE_RECEPTIONIST")));
        
        info.put("pode_cadastrar_pacientes", 
                authorities.stream().anyMatch(a -> 
                    a.getAuthority().equals("ROLE_ADMIN") || 
                    a.getAuthority().equals("ROLE_RECEPTIONIST")));
        
        info.put("pode_excluir_pacientes", 
                authorities.stream().anyMatch(a -> 
                    a.getAuthority().equals("ROLE_ADMIN")));
        
        // Log das informações para referência
        logger.info("Diagnóstico de autenticação solicitado para o usuário: {}", auth.getName());
        logger.info("Roles: {}", authorities);
        
        return info;
    }
    
    /**
     * Exibe uma página HTML com informações de diagnóstico sobre o usuário atual.
     */
    @GetMapping("/usuario-atual")
    public String usuarioAtual(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("autenticado", auth.isAuthenticated());
            
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            model.addAttribute("authorities", authorities);
            
            // Verificar roles específicas
            model.addAttribute("isAdmin", authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
            
            model.addAttribute("isDentist", authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DENTIST")));
            
            model.addAttribute("isReceptionist", authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_RECEPTIONIST")));
            
            model.addAttribute("isPatient", authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT")));
        }
        
        return "diagnostico/usuario-atual";
    }
}
