package com.caracore.cca.service;

import com.caracore.cca.model.Usuario;
import com.caracore.cca.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
        if (usuarioRepository.findByEmail("suporte@caracore.com.br").isEmpty()) {
            logger.info("Criando usuário administrador inicial");
            
            // Hash BCrypt gerado para "admin123"
            String senhaEncriptada = "$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy";
            
            Usuario admin = new Usuario(
                    "suporte@caracore.com.br",
                    "Administrador",
                    senhaEncriptada,
                    "ROLE_ADMIN"
            );
            
            usuarioRepository.save(admin);
            logger.info("Usuário administrador criado com sucesso. Email: suporte@caracore.com.br, Senha: admin123");
        } else {
            logger.info("Usuário administrador já existe");
        }
    }
}
