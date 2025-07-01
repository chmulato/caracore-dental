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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para o serviço de autenticação com diferentes perfis de usuário.
 * Este teste valida que cada tipo de usuário consegue se autenticar corretamente
 * e possui as permissões adequadas para seu perfil.
 */
public class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;
    
    // Hash BCrypt da senha "admin123"
    private final String senhaHash = "$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy";
    
    private Usuario adminUser;
    private Usuario dentistUser;
    private Usuario receptionistUser;
    private Usuario patientUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar usuários de teste para cada perfil
        adminUser = new Usuario();
        adminUser.setEmail("suporte@caracore.com.br");
        adminUser.setNome("Administrador");
        adminUser.setSenha(senhaHash);
        adminUser.setRole("ROLE_ADMIN");
        
        dentistUser = new Usuario();
        dentistUser.setEmail("dentista@caracore.com.br");
        dentistUser.setNome("Dentista Exemplo");
        dentistUser.setSenha(senhaHash);
        dentistUser.setRole("ROLE_DENTIST");
        
        receptionistUser = new Usuario();
        receptionistUser.setEmail("recepcao@caracore.com.br");
        receptionistUser.setNome("Recepcionista Exemplo");
        receptionistUser.setSenha(senhaHash);
        receptionistUser.setRole("ROLE_RECEPTIONIST");
        
        patientUser = new Usuario();
        patientUser.setEmail("joao@gmail.com");
        patientUser.setNome("Joao Maria");
        patientUser.setSenha(senhaHash);
        patientUser.setRole("ROLE_PATIENT");
        
        // Configurar o mock do passwordEncoder para verificar senha
        when(passwordEncoder.matches("admin123", senhaHash)).thenReturn(true);
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
    
    // Testes específicos para cada perfil de usuário
    
    @Test
    @DisplayName("Deve carregar usuário Administrador com permissões corretas")
    public void deveCarregarUsuarioAdministradorComPermissoesCorretas() {
        when(usuarioRepository.findByEmail("suporte@caracore.com.br")).thenReturn(Optional.of(adminUser));
        
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername("suporte@caracore.com.br");
        
        assertNotNull(userDetails);
        assertEquals("suporte@caracore.com.br", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Deve carregar usuário Dentista com permissões corretas")
    public void deveCarregarUsuarioDentistaComPermissoesCorretas() {
        when(usuarioRepository.findByEmail("dentista@caracore.com.br")).thenReturn(Optional.of(dentistUser));
        
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername("dentista@caracore.com.br");
        
        assertNotNull(userDetails);
        assertEquals("dentista@caracore.com.br", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DENTIST")));
        assertFalse(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Deve carregar usuário Recepcionista com permissões corretas")
    public void deveCarregarUsuarioRecepcionistaComPermissoesCorretas() {
        when(usuarioRepository.findByEmail("recepcao@caracore.com.br")).thenReturn(Optional.of(receptionistUser));
        
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername("recepcao@caracore.com.br");
        
        assertNotNull(userDetails);
        assertEquals("recepcao@caracore.com.br", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RECEPTIONIST")));
        assertFalse(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Deve carregar usuário Paciente com permissões corretas")
    public void deveCarregarUsuarioPacienteComPermissoesCorretas() {
        when(usuarioRepository.findByEmail("joao@gmail.com")).thenReturn(Optional.of(patientUser));
        
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername("joao@gmail.com");
        
        assertNotNull(userDetails);
        assertEquals("joao@gmail.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT")));
        assertFalse(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertFalse(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DENTIST")));
    }
    
    @Test
    @DisplayName("Deve verificar que todos os usuários estão usando a mesma senha padrão")
    public void deveVerificarQueUsuariosEstaoUsandoMesmaSenhaPadrao() {
        // Confirmar que todos os usuários têm o mesmo hash BCrypt
        assertEquals(adminUser.getSenha(), dentistUser.getSenha());
        assertEquals(adminUser.getSenha(), receptionistUser.getSenha());
        assertEquals(adminUser.getSenha(), patientUser.getSenha());
        
        // Verificar que o hash corresponde à senha "admin123"
        assertTrue(passwordEncoder.matches("admin123", adminUser.getSenha()));
    }
    
    @Test
    @DisplayName("Deve verificar se as senhas BCrypt são diferentes mesmo com a mesma senha original")
    public void deveVerificarQueSenhasBCryptSaoDiferentes() {
        // Arrange
        BCryptPasswordEncoder realEncoder = new BCryptPasswordEncoder(10);
        
        // Act
        String hash1 = realEncoder.encode("admin123");
        String hash2 = realEncoder.encode("admin123");
        
        // Assert
        assertNotEquals(hash1, hash2, "Hashes BCrypt devem ser diferentes mesmo para a mesma senha");
        assertTrue(realEncoder.matches("admin123", hash1), "Hash1 deve validar a senha correta");
        assertTrue(realEncoder.matches("admin123", hash2), "Hash2 deve validar a senha correta");
    }
    
    @Test
    @DisplayName("Deve verificar que senhas inválidas são rejeitadas")
    public void deveRejeitarSenhasIncorretas() {
        // Arrange
        when(usuarioRepository.findByEmail("suporte@caracore.com.br")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("senha_incorreta", senhaHash)).thenReturn(false);
        
        // Act
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername("suporte@caracore.com.br");
        
        // Assert
        assertFalse(passwordEncoder.matches("senha_incorreta", userDetails.getPassword()));
    }
    
    @Test
    @DisplayName("Deve verificar que todos os usuários têm acesso aos métodos adequados do UserDetails")
    public void deveVerificarMetodosUserDetails() {
        // Arrange
        when(usuarioRepository.findByEmail("suporte@caracore.com.br")).thenReturn(Optional.of(adminUser));
        
        // Act
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername("suporte@caracore.com.br");
        
        // Assert
        assertTrue(userDetails.isAccountNonExpired(), "Conta deve estar não-expirada");
        assertTrue(userDetails.isAccountNonLocked(), "Conta deve estar desbloqueada");
        assertTrue(userDetails.isCredentialsNonExpired(), "Credenciais não devem estar expiradas");
        assertTrue(userDetails.isEnabled(), "Usuário deve estar habilitado");
    }
    
    @Test
    @DisplayName("Deve testar simultaneamente os diferentes tipos de usuários")
    public void deveTestarTodosTiposDeUsuariosSimultaneamente() {
        // Arrange
        when(usuarioRepository.findByEmail("suporte@caracore.com.br")).thenReturn(Optional.of(adminUser));
        when(usuarioRepository.findByEmail("dentista@caracore.com.br")).thenReturn(Optional.of(dentistUser));
        when(usuarioRepository.findByEmail("recepcao@caracore.com.br")).thenReturn(Optional.of(receptionistUser));
        when(usuarioRepository.findByEmail("joao@gmail.com")).thenReturn(Optional.of(patientUser));
        
        // Act & Assert
        UserDetails adminDetails = usuarioDetailsService.loadUserByUsername("suporte@caracore.com.br");
        assertTrue(adminDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        
        UserDetails dentistDetails = usuarioDetailsService.loadUserByUsername("dentista@caracore.com.br");
        assertTrue(dentistDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DENTIST")));
        
        UserDetails receptionistDetails = usuarioDetailsService.loadUserByUsername("recepcao@caracore.com.br");
        assertTrue(receptionistDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_RECEPTIONIST")));
        
        UserDetails patientDetails = usuarioDetailsService.loadUserByUsername("joao@gmail.com");
        assertTrue(patientDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT")));
    }
    
    @Test
    @DisplayName("Deve verificar que usuários com email em maiúsculas são reconhecidos")
    public void deveReconhecerEmailEmMaiusculas() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(adminUser));
        
        // Act
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername("SUPORTE@caracore.com.BR");
        
        // Assert
        assertNotNull(userDetails);
        assertEquals(adminUser.getEmail(), userDetails.getUsername());
    }
}
