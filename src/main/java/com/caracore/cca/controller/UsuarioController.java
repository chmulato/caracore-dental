package com.caracore.cca.controller;

import com.caracore.cca.model.Usuario;
import com.caracore.cca.repository.UsuarioRepository;
import com.caracore.cca.service.InitService;
import com.caracore.cca.util.UserActivityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para gerenciar usuários do sistema
 * As operações de listagem e gerenciamento só podem ser acessadas por administradores
 */
@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UserActivityLogger userActivityLogger;
    private final BCryptPasswordEncoder passwordEncoder;
    private final InitService initService;
    
    private static final List<String> EMAIL_USUARIOS_PADRAO = Arrays.asList(
        "suporte@caracore.com.br", 
        "dentista@caracore.com.br", 
        "recepcao@caracore.com.br", 
        "paciente@caracore.com.br"
    );

    @Autowired
    public UsuarioController(
            UsuarioRepository usuarioRepository, 
            UserActivityLogger userActivityLogger,
            BCryptPasswordEncoder passwordEncoder,
            InitService initService) {
        this.usuarioRepository = usuarioRepository;
        this.userActivityLogger = userActivityLogger;
        this.passwordEncoder = passwordEncoder;
        this.initService = initService;
    }

    /**
     * Lista todos os usuários do sistema
     * Acessível apenas para administradores
     */
    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        
        // Registrar atividade
        userActivityLogger.logActivity(
            "LISTAR_USUARIOS", 
            "Acesso à listagem de usuários", 
            null, 
            "Administrador acessou a lista de usuários"
        );
        
        return "usuarios/lista";
    }
    
    /**
     * Exibe detalhes de um usuário específico
     */
    @GetMapping("/{id}")
    public String detalharUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado");
            return "redirect:/usuarios";
        }
        
        Usuario usuario = usuarioOpt.get();
        model.addAttribute("usuario", usuario);
        
        // Verifica se é um usuário padrão do sistema
        boolean isUsuarioPadrao = EMAIL_USUARIOS_PADRAO.contains(usuario.getEmail());
        model.addAttribute("usuarioPadrao", isUsuarioPadrao);
        
        // Registrar atividade
        userActivityLogger.logActivity(
            "VISUALIZAR_USUARIO", 
            "Visualização de detalhes de usuário", 
            "ID: " + id, 
            "Administrador visualizou detalhes do usuário"
        );
        
        return "usuarios/detalhes";
    }
    
    /**
     * Busca usuários por nome/email e opcionalmente filtra por perfil
     */
    @GetMapping("/buscar")
    public String buscarUsuarios(
            @RequestParam String termo, 
            @RequestParam(required = false) String perfil,
            Model model) {
        
        List<Usuario> usuarios;
        
        // Busca usuários por termo e filtra por perfil se especificado
        if (perfil != null && !perfil.isEmpty()) {
            usuarios = usuarioRepository.findByEmailContainingIgnoreCaseOrNomeContainingIgnoreCase(termo, termo)
                    .stream()
                    .filter(u -> u.getRole().equals(perfil))
                    .toList();
        } else {
            usuarios = usuarioRepository.findByEmailContainingIgnoreCaseOrNomeContainingIgnoreCase(termo, termo);
        }
        
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("termoBusca", termo);
        model.addAttribute("perfil", perfil);
        
        // Registrar atividade
        userActivityLogger.logActivity(
            "BUSCAR_USUARIOS", 
            "Busca de usuários realizada", 
            "Termo: " + termo + (perfil != null && !perfil.isEmpty() ? ", Perfil: " + perfil : ""), 
            "Administrador realizou busca de usuários"
        );
        
        return "usuarios/lista";
    }
    
    /**
     * Exibe formulário para criar novo usuário
     */
    @GetMapping("/novo")
    public String formNovoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        
        // Registrar atividade
        userActivityLogger.logActivity(
            "FORM_NOVO_USUARIO", 
            "Acesso ao formulário de novo usuário", 
            null, 
            "Administrador acessou o formulário de criação de usuário"
        );
        
        return "usuarios/form";
    }
    
    /**
     * Salva um novo usuário
     */
    @PostMapping("/salvar")
    public String salvarUsuario(@Valid Usuario usuario, BindingResult result, RedirectAttributes redirectAttributes) {
        // Validação manual para a senha
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            result.rejectValue("senha", "error.usuario", "A senha é obrigatória para novos usuários");
        } else if (usuario.getSenha().length() < 6) {
            result.rejectValue("senha", "error.usuario", "A senha deve ter no mínimo 6 caracteres");
        }
        
        if (result.hasErrors()) {
            return "usuarios/form";
        }
        
        // Verificar se o email já existe
        Optional<Usuario> existente = usuarioRepository.findByEmail(usuario.getEmail());
        if (existente.isPresent()) {
            result.rejectValue("email", "error.usuario", "Este email já está cadastrado");
            return "usuarios/form";
        }
        
        // Criptografar a senha
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        
        // Salvar o usuário
        usuarioRepository.save(usuario);
        
        // Registrar atividade
        userActivityLogger.logActivity(
            "SALVAR_USUARIO", 
            "Novo usuário criado", 
            "Email: " + usuario.getEmail() + ", Perfil: " + usuario.getRole(), 
            "Administrador criou um novo usuário"
        );
        
        redirectAttributes.addFlashAttribute("sucesso", "Usuário cadastrado com sucesso");
        return "redirect:/usuarios";
    }
    
    /**
     * Exibe formulário para editar usuário existente
     */
    @GetMapping("/{id}/editar")
    public String formEditarUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "usuario-nao-encontrado");
            return "redirect:/usuarios";
        }
        
        model.addAttribute("usuario", usuarioOpt.get());
        
        // Registrar atividade
        userActivityLogger.logActivity(
            "FORM_EDITAR_USUARIO", 
            "Acesso ao formulário de edição de usuário", 
            "ID: " + id, 
            "Administrador acessou o formulário de edição de usuário"
        );
        
        return "usuarios/form";
    }
    
    /**
     * Atualiza um usuário existente
     */
    @PostMapping("/{id}/atualizar")
    public String atualizarUsuario(
            @PathVariable Long id, 
            @Valid Usuario usuario, 
            BindingResult result, 
            @RequestParam(required = false) boolean resetarSenha,
            RedirectAttributes redirectAttributes) {
        
        // Verificar se o usuário existe
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "usuario-nao-encontrado");
            return "redirect:/usuarios";
        }
        
        Usuario usuarioAtual = usuarioExistente.get();
        
        // Verificar se o email foi alterado e já existe para outro usuário
        if (!usuario.getEmail().equals(usuarioAtual.getEmail())) {
            Optional<Usuario> emailExistente = usuarioRepository.findByEmail(usuario.getEmail());
            if (emailExistente.isPresent() && !emailExistente.get().getId().equals(id)) {
                result.rejectValue("email", "error.usuario", "Este email já está cadastrado para outro usuário");
                return "usuarios/form";
            }
        }
        
        if (result.hasErrors()) {
            return "usuarios/form";
        }
        
        // Manter a senha atual ou resetar para o padrão, caso solicitado
        if (resetarSenha) {
            // Usar o serviço existente para redefinir a senha para o padrão
            initService.redefinirSenhaUsuarioPadrao(usuario.getEmail());
            
            // Registrar atividade
            userActivityLogger.logActivity(
                "RESETAR_SENHA_USUARIO", 
                "Senha de usuário redefinida para padrão", 
                "ID: " + id + ", Email: " + usuario.getEmail(), 
                "Administrador redefiniu a senha do usuário para o padrão"
            );
        } else if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
            // Se uma nova senha foi fornecida, validar e criptografar
            if (usuario.getSenha().length() < 6) {
                result.rejectValue("senha", "error.usuario", "A senha deve ter no mínimo 6 caracteres");
                return "usuarios/form";
            }
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        } else {
            // Manter a senha atual
            usuario.setSenha(usuarioAtual.getSenha());
        }
        
        // Atualizar os dados do usuário
        usuarioRepository.save(usuario);
        
        // Registrar atividade
        userActivityLogger.logActivity(
            "ATUALIZAR_USUARIO", 
            "Usuário atualizado", 
            "ID: " + id + ", Email: " + usuario.getEmail(), 
            "Administrador atualizou dados do usuário"
        );
        
        redirectAttributes.addFlashAttribute("sucesso", "Usuário atualizado com sucesso");
        return "redirect:/usuarios";
    }
    
    /**
     * Exclui um usuário
     */
    @PostMapping("/{id}/excluir")
    public String excluirUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "usuario-nao-encontrado");
            return "redirect:/usuarios";
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar se é um usuário padrão do sistema (não deve ser excluído)
        if (EMAIL_USUARIOS_PADRAO.contains(usuario.getEmail())) {
            redirectAttributes.addFlashAttribute("erro", "Usuários padrão do sistema não podem ser excluídos");
            return "redirect:/usuarios";
        }
        
        // Excluir o usuário
        usuarioRepository.delete(usuario);
        
        // Registrar atividade
        userActivityLogger.logActivity(
            "EXCLUIR_USUARIO", 
            "Usuário excluído", 
            "ID: " + id + ", Email: " + usuario.getEmail(), 
            "Administrador excluiu um usuário"
        );
        
        redirectAttributes.addFlashAttribute("sucesso", "Usuário excluído com sucesso");
        return "redirect:/usuarios";
    }
    
    /**
     * Redireciona para a página de acesso negado quando 
     * um usuário sem permissão tenta acessar a lista
     */
    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }
}
