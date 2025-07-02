package com.caracore.cca.service;

import com.caracore.cca.model.Usuario;
import com.caracore.cca.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InitService {

    private static final Logger logger = LoggerFactory.getLogger(InitService.class);
    
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public InitService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        criarUsuarioSeNaoExistir("suporte@caracore.com.br", "Administrador", "admin123", "ROLE_ADMIN");
        criarUsuarioSeNaoExistir("dentista@caracore.com.br", "Dr. Carlos Silva", "admin123", "ROLE_DENTIST");
        criarUsuarioSeNaoExistir("recepcao@caracore.com.br", "Ana Recepção", "admin123", "ROLE_RECEPTIONIST");
        criarUsuarioSeNaoExistir("paciente@caracore.com.br", "João Paciente", "admin123", "ROLE_PATIENT");
    }
    
    /**
     * Cria um usuário se ele ainda não existir no banco de dados
     * 
     * @param email Email do usuário (identificador único)
     * @param nome Nome do usuário
     * @param senha Senha em texto plano (será codificada)
     * @param role Perfil do usuário (ROLE_ADMIN, ROLE_DENTIST, ROLE_RECEPTIONIST, ROLE_PATIENT)
     */
    private void criarUsuarioSeNaoExistir(String email, String nome, String senha, String role) {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            logger.info("Criando usuário {} ({})", nome, role);
            
            String senhaEncriptada = passwordEncoder.encode(senha);
            
            Usuario usuario = new Usuario(
                    email,
                    nome,
                    senhaEncriptada,
                    role
            );
            
            usuarioRepository.save(usuario);
            logger.info("Usuário criado com sucesso. Email: {}, Senha: {}", email, senha);
        } else {
            logger.info("Usuário {} já existe", email);
        }
    }
    
    /**
     * Verifica se todos os usuários padrões estão presentes no sistema.
     * Caso algum esteja ausente, cria novamente.
     * Este método pode ser chamado via endpoint administrativo se necessário.
     */
    @Transactional
    public void verificarEAtualizarUsuariosPadrao() {
        logger.info("Verificando usuários padrões do sistema");
        
        criarUsuarioSeNaoExistir("suporte@caracore.com.br", "Administrador", "admin123", "ROLE_ADMIN");
        criarUsuarioSeNaoExistir("dentista@caracore.com.br", "Dr. Carlos Silva", "admin123", "ROLE_DENTIST");
        criarUsuarioSeNaoExistir("recepcao@caracore.com.br", "Ana Recepção", "admin123", "ROLE_RECEPTIONIST");
        criarUsuarioSeNaoExistir("paciente@caracore.com.br", "João Paciente", "admin123", "ROLE_PATIENT");
        
        logger.info("Verificação de usuários padrões concluída");
    }
    
    /**
     * Redefine a senha de um usuário para o valor padrão
     * @param email Email do usuário
     * @return true se o usuário foi encontrado e a senha redefinida, false caso contrário
     */
    @Transactional
    public boolean redefinirSenhaUsuarioPadrao(String email) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            String novaSenha = "admin123";
            
            usuario.setSenha(passwordEncoder.encode(novaSenha));
            usuarioRepository.save(usuario);
            logger.info("Senha do usuário {} redefinida para o valor padrão", email);
            return true;
        } else {
            logger.warn("Usuário {} não encontrado para redefinir senha", email);
            return false;
        }
    }
}
