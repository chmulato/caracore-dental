package com.caracore.cca.service;

import com.caracore.cca.model.Usuario;
import com.caracore.cca.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InitServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private InitService initService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar usuário admin se não existir")
    public void deveCriarAdminSeNaoExistir() {
        // Arrange
        when(usuarioRepository.findByEmail("suporte@caracore.com.br")).thenReturn(Optional.empty());
        
        // Act
        initService.init();
        
        // Assert
        verify(usuarioRepository).save(argThat(usuario -> 
            usuario.getEmail().equals("suporte@caracore.com.br") &&
            usuario.getNome().equals("Administrador") &&
            usuario.getRole().equals("ROLE_ADMIN")
        ));
    }

    @Test
    @DisplayName("Não deve criar admin se já existir")
    public void naoDeveCriarAdminSeJaExistir() {
        // Arrange
        Usuario adminExistente = new Usuario();
        adminExistente.setEmail("suporte@caracore.com.br");
        adminExistente.setNome("Administrador");
        adminExistente.setSenha("$2a$10$existingHash");
        adminExistente.setRole("ROLE_ADMIN");
        
        when(usuarioRepository.findByEmail("suporte@caracore.com.br")).thenReturn(Optional.of(adminExistente));
        
        // Act
        initService.init();
        
        // Assert
        verify(usuarioRepository, never()).save(any());
    }
}
