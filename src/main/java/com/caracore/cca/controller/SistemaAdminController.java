package com.caracore.cca.controller;

import com.caracore.cca.service.InitService;
import com.caracore.cca.service.DentistaService;
import com.caracore.cca.util.UserActivityLogger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador administrativo para gerenciar usuários padrões do sistema.
 * Todos os endpoints deste controlador são restritos apenas a administradores.
 * 
 * Níveis de acesso:
 * - ADMIN: acesso completo a todas as funcionalidades administrativas do sistema
 * - Outros perfis (DENTIST, RECEPTIONIST, PATIENT): sem acesso a nenhuma funcionalidade administrativa
 * 
 * A anotação @PreAuthorize no nível da classe restringe automaticamente todos os endpoints
 * para permitir acesso apenas por usuários com o papel ADMIN.
 */
@RestController
@RequestMapping(value = "/admin/sistema", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasRole('ADMIN')")
public class SistemaAdminController {

    private final InitService initService;
    private final UserActivityLogger userActivityLogger;
    private final DentistaService dentistaService;

    public SistemaAdminController(InitService initService, UserActivityLogger userActivityLogger, DentistaService dentistaService) {
        this.initService = initService;
        this.userActivityLogger = userActivityLogger;
        this.dentistaService = dentistaService;
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
        
        resposta.put("status", "sucesso");
        resposta.put("totalProcessados", emailsPadrao.length);
        resposta.put("sucessos", (int) resultados.values().stream().filter(r -> "redefinida".equals(r)).count());
        resposta.put("falhas", (int) resultados.values().stream().filter(r -> "falha".equals(r)).count());
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
            "STATUS_USUARIOS_PADRAO", 
            "Consulta de status de usuários padrão",
            null,
            "Administrador consultou status dos usuários padrão"
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

    /**
     * Obtém lista de dentistas com status de exposição pública
     */
    @GetMapping("/dentistas-publicos")
    public ResponseEntity<?> obterDentistasPublicos() {
        try {
            var dentistasAtivos = dentistaService.listarAtivos();
            Map<String, Object> resposta = new HashMap<>();
            
            resposta.put("status", "sucesso");
            resposta.put("dentistas", dentistasAtivos);
            resposta.put("total", dentistasAtivos.size());
            
            userActivityLogger.logActivity(
                "LISTAR_DENTISTAS_PUBLICOS", 
                "Consulta de dentistas para exposição pública",
                null,
                "Administrador consultou lista de dentistas públicos"
            );
            
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "erro", "mensagem", "Erro interno do servidor"));
        }
    }

    /**
     * Controla exposição pública de um dentista específico
     */
    @PostMapping("/dentista/{id}/exposicao-publica")
    public ResponseEntity<?> alterarExposicaoPublica(@PathVariable Long id, @RequestParam Boolean exposto) {
        try {
            // Usar o DentistaService para alterar a exposição pública
            boolean sucesso = dentistaService.alterarExposicaoPublica(id, exposto, null);
            
            if (sucesso) {
                userActivityLogger.logActivity(
                    "ALTERAR_EXPOSICAO_DENTISTA", 
                    "Alteração de exposição pública de dentista",
                    "Dentista ID: " + id + ", Exposto: " + exposto,
                    "Administrador alterou exposição pública de dentista"
                );
                
                Map<String, Object> resposta = new HashMap<>();
                resposta.put("status", "sucesso");
                resposta.put("mensagem", "Exposição pública do dentista alterada com sucesso");
                resposta.put("dentistaId", id);
                resposta.put("exposto", exposto);
                
                return ResponseEntity.ok(resposta);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "erro", "mensagem", "Erro ao alterar exposição pública"));
        }
    }

    /**
     * Controla status global da agenda pública
     */
    @PostMapping("/agenda-publica/toggle")
    public ResponseEntity<?> toggleAgendaPublica(@RequestParam Boolean ativa) {
        try {
            // Implementar lógica para ativar/desativar agenda pública globalmente
            // Por enquanto, apenas log da ação
            
            userActivityLogger.logActivity(
                "TOGGLE_AGENDA_PUBLICA", 
                "Alteração do status da agenda pública",
                "Status: " + (ativa ? "Ativada" : "Desativada"),
                "Administrador " + (ativa ? "ativou" : "desativou") + " a agenda pública"
            );
            
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("status", "sucesso");
            resposta.put("mensagem", "Agenda pública " + (ativa ? "ativada" : "desativada") + " com sucesso");
            resposta.put("agendaPublicaAtiva", ativa);
            
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "erro", "mensagem", "Erro ao alterar status da agenda pública"));
        }
    }

    /**
     * Configura horários de funcionamento para agenda pública
     */
    @PostMapping("/agenda-publica/horarios")
    public ResponseEntity<?> configurarHorariosPublicos(@RequestParam String inicio, @RequestParam String fim) {
        try {
            // Validação de parâmetros obrigatórios
            if (inicio == null || inicio.trim().isEmpty() || fim == null || fim.trim().isEmpty()) {
                Map<String, Object> erro = new java.util.HashMap<>();
                erro.put("status", "erro");
                erro.put("mensagem", "Parâmetros obrigatórios não informados");
                return ResponseEntity.badRequest().body(erro);
            }
            
            // Validação: início deve ser anterior ao fim
            java.time.LocalTime horaInicio = java.time.LocalTime.parse(inicio);
            java.time.LocalTime horaFim = java.time.LocalTime.parse(fim);
            if (!horaInicio.isBefore(horaFim)) {
                Map<String, Object> erro = new java.util.HashMap<>();
                erro.put("status", "erro");
                erro.put("mensagem", "Horário de início deve ser anterior ao horário de fim");
                return ResponseEntity.badRequest().body(erro);
            }
            // Implementar lógica para salvar horários de funcionamento público
            // Por enquanto, apenas log da ação
            
            userActivityLogger.logActivity(
                "CONFIGURAR_HORARIOS_PUBLICOS", 
                "Configuração de horários públicos",
                "Início: " + inicio + ", Fim: " + fim,
                "Administrador configurou horários públicos"
            );
            
            Map<String, Object> resposta = new java.util.HashMap<>();
            resposta.put("status", "sucesso");
            resposta.put("mensagem", "Horários públicos configurados com sucesso");
            resposta.put("horarioInicio", inicio);
            resposta.put("horarioFim", fim);
            
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "erro", "mensagem", "Erro ao configurar horários públicos"));
        }
    }

    /**
     * Obtém estatísticas de dentistas para dashboard administrativo
     */
    @GetMapping("/estatisticas-dentistas")
    public ResponseEntity<?> obterEstatisticasDentistas() {
        try {
            var todosDentistas = dentistaService.listarTodos();
            var dentistasAtivos = dentistaService.listarAtivos();
            
            Map<String, Object> resposta = new HashMap<>();
            Map<String, Integer> estatisticas = new HashMap<>();
            
            estatisticas.put("total", todosDentistas.size());
            estatisticas.put("ativos", dentistasAtivos.size());
            estatisticas.put("inativos", todosDentistas.size() - dentistasAtivos.size());
            // Implementar contagem de dentistas expostos publicamente
            estatisticas.put("expostos", dentistasAtivos.size()); // Placeholder
            
            resposta.put("status", "sucesso");
            resposta.put("estatisticas", estatisticas);
            
            userActivityLogger.logActivity(
                "CONSULTAR_ESTATISTICAS_DENTISTAS", 
                "Consulta de estatísticas de dentistas",
                null,
                "Administrador consultou estatísticas de dentistas"
            );
            
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "erro", "mensagem", "Erro ao carregar estatísticas"));
        }
    }

    /**
     * Endpoint principal da área administrativa
     */
    @GetMapping("/api")
    public ResponseEntity<?> homeAdmin() {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("status", "sucesso");
        resposta.put("mensagem", "Área administrativa acessível");
        return ResponseEntity.ok(resposta);
    }
}
