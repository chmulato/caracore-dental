package com.caracore.cca.controller;

import com.caracore.cca.model.Usuario;
import com.caracore.cca.repository.UsuarioRepository;
import com.caracore.cca.util.UserActivityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserActivityLogger userActivityLogger;

    @Autowired
    public PerfilController(UsuarioRepository usuarioRepository, 
                           BCryptPasswordEncoder passwordEncoder,
                           UserActivityLogger userActivityLogger) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.userActivityLogger = userActivityLogger;
    }

    @GetMapping
    public String verPerfil(Authentication authentication, Model model) {
        String emailUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(emailUsuario);
        
        if (usuarioOpt.isEmpty()) {
            return "redirect:/dashboard?erro=usuario-nao-encontrado";
        }
        
        Usuario usuario = usuarioOpt.get();
        model.addAttribute("usuario", usuario);
        
        // Registrar atividade de visualização de perfil
        userActivityLogger.logActivity(
            "VISUALIZAR_PERFIL", 
            "Visualização de perfil", 
            null, 
            "Usuário visualizou seu perfil"
        );
        
        return "perfil/perfil";
    }
    
    @PostMapping("/alterar-senha")
    public String alterarSenha(
            @RequestParam("senhaAtual") String senhaAtual,
            @RequestParam("novaSenha") String novaSenha,
            @RequestParam("confirmacaoSenha") String confirmacaoSenha,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        String emailUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(emailUsuario);
        
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado");
            return "redirect:/perfil";
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar se a senha atual está correta
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            redirectAttributes.addFlashAttribute("erro", "Senha atual incorreta");
            return "redirect:/perfil";
        }
        
        // Verificar se as senhas novas coincidem
        if (!novaSenha.equals(confirmacaoSenha)) {
            redirectAttributes.addFlashAttribute("erro", "Nova senha e confirmação não coincidem");
            return "redirect:/perfil";
        }
        
        // Verificar se a nova senha tem pelo menos 6 caracteres
        if (novaSenha.length() < 6) {
            redirectAttributes.addFlashAttribute("erro", "A nova senha deve ter pelo menos 6 caracteres");
            return "redirect:/perfil";
        }
        
        // Criptografar e atualizar a senha
        String senhaCriptografada = passwordEncoder.encode(novaSenha);
        usuarioRepository.atualizarSenha(emailUsuario, senhaCriptografada);
        
        // Registrar atividade
        userActivityLogger.logActivity(
            "ALTERAR_SENHA", 
            "Alteração de senha realizada", 
            null, 
            "Usuário alterou sua senha"
        );
        
        redirectAttributes.addFlashAttribute("sucesso", "Senha alterada com sucesso");
        return "redirect:/perfil";
    }
}
