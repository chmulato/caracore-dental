package com.caracore.cca.service;

import com.caracore.cca.model.Usuario;
import com.caracore.cca.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve carregar usuário por email quando usuário existe")
    public void deveCarregarUsuarioPorEmailQuandoExiste() {
        // Arrange
        String email = "admin@test.com";
        Usuario mockUsuario = new Usuario();
        mockUsuario.setEmail(email);
        mockUsuario.setNome("Admin");
        mockUsuario.setSenha("$2a$10$hashedPassword");
        mockUsuario.setRole("ROLE_ADMIN");

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(mockUsuario));

        // Act
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails, "UserDetails não deve ser nulo");
        assertEquals(email, userDetails.getUsername(), "Username deve ser igual ao email");
        assertEquals(mockUsuario.getSenha(), userDetails.getPassword(), "Password deve ser igual");
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(mockUsuario.getRole())),
                "Autoridades devem conter o perfil do usuário");
        
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário não existe")
    public void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        // Arrange
        String email = "naoexiste@test.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            usuarioDetailsService.loadUserByUsername(email);
        });

        String expectedMessage = "Usuário não encontrado com email: " + email;
        String actualMessage = exception.getMessage();
        
        assertTrue(actualMessage.contains(expectedMessage));
        verify(usuarioRepository, times(1)).findByEmail(email);
    }
}
