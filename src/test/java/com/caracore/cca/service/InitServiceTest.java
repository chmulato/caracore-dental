package com.caracore.cca.service;

import com.caracore.cca.model.Usuario;
import com.caracore.cca.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private InitService initService;

    @Test
    @DisplayName("Deve criar usuário admin se não existir")
    void deveCriarAdminSeNaoExistir() {
        // Arrange
        when(usuarioRepository.findByEmail("suporte@caracore.com.br")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("dentista@teste.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("recepcao@teste.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("paciente@teste.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCodificada");
        
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
    void naoDeveCriarAdminSeJaExistir() {
        // Arrange
        Usuario adminExistente = new Usuario();
        adminExistente.setEmail("suporte@caracore.com.br");
        adminExistente.setNome("Administrador");
        adminExistente.setSenha("$2a$10$existingHash");
        adminExistente.setRole("ROLE_ADMIN");
        
        when(usuarioRepository.findByEmail("suporte@caracore.com.br")).thenReturn(Optional.of(adminExistente));
        when(usuarioRepository.findByEmail("dentista@teste.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("recepcao@teste.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("paciente@teste.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCodificada");
        
        // Act
        initService.init();
        
        // Assert
        verify(usuarioRepository, never()).save(argThat(usuario -> 
            usuario.getEmail().equals("suporte@caracore.com.br")
        ));
        
        // Verificar que os outros usuários foram criados
        verify(usuarioRepository).save(argThat(usuario -> 
            usuario.getEmail().equals("dentista@teste.com") &&
            usuario.getRole().equals("ROLE_DENTIST")
        ));
        
        verify(usuarioRepository).save(argThat(usuario -> 
            usuario.getEmail().equals("recepcao@teste.com") &&
            usuario.getRole().equals("ROLE_RECEPTIONIST")
        ));
        
        verify(usuarioRepository).save(argThat(usuario -> 
            usuario.getEmail().equals("paciente@teste.com") &&
            usuario.getRole().equals("ROLE_PATIENT")
        ));
    }
    
    @Test
    @DisplayName("Deve criar usuários padrões que não existem")
    void deveCriarUsuariosPadroes() {
        // Configurar mock para simular que nenhum usuário existe
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCodificada");

        // Chamar o método init
        initService.init();

        // Verificar se foram chamados os métodos de criação para todos os perfis
        verify(usuarioRepository, times(4)).findByEmail(anyString());
        verify(usuarioRepository, times(4)).save(any(Usuario.class));
        
        // Capturar e verificar um dos usuários criados
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(4)).save(usuarioCaptor.capture());
        
        // Verificar se os quatro perfis diferentes foram criados
        boolean temAdmin = false;
        boolean temDentista = false;
        boolean temRecepcionista = false;
        boolean temPaciente = false;
        
        for (Usuario usuario : usuarioCaptor.getAllValues()) {
            if ("ROLE_ADMIN".equals(usuario.getRole())) temAdmin = true;
            if ("ROLE_DENTIST".equals(usuario.getRole())) temDentista = true;
            if ("ROLE_RECEPTIONIST".equals(usuario.getRole())) temRecepcionista = true;
            if ("ROLE_PATIENT".equals(usuario.getRole())) temPaciente = true;
        }
        
        assertTrue(temAdmin, "Deve criar um usuário administrador");
        assertTrue(temDentista, "Deve criar um usuário dentista");
        assertTrue(temRecepcionista, "Deve criar um usuário recepcionista");
        assertTrue(temPaciente, "Deve criar um usuário paciente");
    }

    @Test
    @DisplayName("Deve redefinir senha do usuário")
    void deveRedefinirSenhaDoUsuario() {
        // Configurar mock
        Usuario usuario = new Usuario();
        usuario.setEmail("dentista@teste.com");
        
        when(usuarioRepository.findByEmail("dentista@teste.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        
        // Executar
        boolean resultado = initService.redefinirSenhaUsuarioPadrao("dentista@teste.com");
        
        // Verificar
        assertTrue(resultado);
        assertEquals("senhaCodificada", usuario.getSenha());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve retornar falso ao tentar redefinir senha de usuário inexistente")
    void deveRetornarFalsoAoRedefinirSenhaDeUsuarioInexistente() {
        // Configurar mock
        when(usuarioRepository.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());
        
        // Executar
        boolean resultado = initService.redefinirSenhaUsuarioPadrao("naoexiste@teste.com");
        
        // Verificar
        assertFalse(resultado);
        verify(usuarioRepository, never()).save(any());
    }
}
