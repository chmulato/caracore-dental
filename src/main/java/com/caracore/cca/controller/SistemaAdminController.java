package com.caracore.cca.controller;

import com.caracore.cca.service.InitService;
import com.caracore.cca.util.UserActivityLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador administrativo para gerenciar usuários padrões do sistema
 * Estes endpoints devem ser acessados apenas por administradores
 */
@RestController
@RequestMapping("/admin/sistema")
@PreAuthorize("hasRole('ADMIN')")
public class SistemaAdminController {

    private final InitService initService;
    private final UserActivityLogger userActivityLogger;

    public SistemaAdminController(InitService initService, UserActivityLogger userActivityLogger) {
        this.initService = initService;
        this.userActivityLogger = userActivityLogger;
    }

    /**
     * Verifica e recria usuários padrões que possam estar faltando
     */
    @PostMapping("/verificar-usuarios")
    public ResponseEntity<String> verificarUsuarios() {
        initService.verificarEAtualizarUsuariosPadrao();
        
        userActivityLogger.logActivity(
            "SISTEMA_ADMIN", 
            "Verificação de usuários padrões",
            null,
            "Verificação de usuários padrões executada por administrador"
        );
        
        return ResponseEntity.ok("Verificação de usuários padrões concluída");
    }
    
    /**
     * Redefine a senha de um usuário para o valor padrão
     */
    @PostMapping("/resetar-senha/{email}")
    public ResponseEntity<String> resetarSenha(@PathVariable String email) {
        boolean sucesso = initService.redefinirSenhaUsuarioPadrao(email);
        
        if (sucesso) {
            userActivityLogger.logActivity(
                "RESET_SENHA", 
                "Redefinição de senha para valor padrão",
                "Usuário: " + email,
                "Redefinição executada por administrador"
            );
            
            return ResponseEntity.ok("Senha redefinida com sucesso para o usuário: " + email);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Redefine as senhas de todos os usuários padrão para seus valores iniciais
     */
    @PostMapping("/redefinir-todas-senhas-padrao")
    public ResponseEntity<?> redefinirTodasSenhasPadrao() {
        // Obter os emails dos usuários padrão
        String[] emailsPadrao = {
            "suporte@caracore.com.br", 
            "dentista@teste.com", 
            "recepcao@teste.com", 
            "paciente@teste.com"
        };
        
        Map<String, Object> resposta = new HashMap<>();
        Map<String, String> resultados = new HashMap<>();
        
        for (String email : emailsPadrao) {
            boolean sucesso = initService.redefinirSenhaUsuarioPadrao(email);
            resultados.put(email, sucesso ? "redefinida" : "falha");
        }
        
        userActivityLogger.logActivity(
            "REDEFINIR_TODAS_SENHAS_PADRAO", 
            "Redefinição de todas as senhas de usuários padrão",
            null,
            "Operação administrativa"
        );
        
        resposta.put("status", "concluído");
        resposta.put("resultados", resultados);
        
        return ResponseEntity.ok(resposta);
    }
    
    /**
     * Obtém informações sobre os usuários padrão do sistema
     */
    @RequestMapping("/status-usuarios-padrao")
    public ResponseEntity<?> obterStatusUsuariosPadrao() {
        // Obter os emails dos usuários padrão
        String[] emailsPadrao = {
            "suporte@caracore.com.br", 
            "dentista@teste.com", 
            "recepcao@teste.com", 
            "paciente@teste.com"
        };
        
        Map<String, Object> resposta = new HashMap<>();
        Map<String, Object> usuarios = new HashMap<>();
        
        // Verificar status de cada usuário padrão
        for (String email : emailsPadrao) {
            boolean existe = !initService.redefinirSenhaUsuarioPadrao(email) ? false : true;
            
            Map<String, Object> dadosUsuario = new HashMap<>();
            dadosUsuario.put("existe", existe);
            dadosUsuario.put("perfil", getPerfil(email));
            
            usuarios.put(email, dadosUsuario);
        }
        
        userActivityLogger.logActivity(
            "VERIFICAR_STATUS_USUARIOS", 
            "Verificação do status dos usuários padrão",
            null,
            "Consulta administrativa"
        );
        
        resposta.put("status", "sucesso");
        resposta.put("usuarios", usuarios);
        
        return ResponseEntity.ok(resposta);
    }
    
    /**
     * Obtém o perfil correspondente a um email de usuário padrão
     */
    private String getPerfil(String email) {
        if ("suporte@caracore.com.br".equals(email)) {
            return "ROLE_ADMIN";
        } else if ("dentista@teste.com".equals(email)) {
            return "ROLE_DENTIST";
        } else if ("recepcao@teste.com".equals(email)) {
            return "ROLE_RECEPTIONIST";
        } else if ("paciente@teste.com".equals(email)) {
            return "ROLE_PATIENT";
        } else {
            return "DESCONHECIDO";
        }
    }
}
