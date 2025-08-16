package com.caracore.cca.service;

import com.caracore.cca.model.Usuario;
import com.caracore.cca.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioDetailsService.class);
    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Tentativa de autenticação para usuário: {}", email);
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado com email: " + email);
                });

        logger.info("Usuário encontrado: {}, com perfil: {}", usuario.getNome(), usuario.getRole());
        
    // Normaliza role para garantir prefixo ROLE_ exigido por hasRole/hasAnyRole
    String rawRole = usuario.getRole();
    String normalizedRole = rawRole != null && rawRole.startsWith("ROLE_") ? rawRole : "ROLE_" + rawRole;
    return new User(
        usuario.getEmail(),
        usuario.getSenha(),
        Collections.singletonList(new SimpleGrantedAuthority(normalizedRole))
    );
    }
}
