package com.caracore.cca.repository;

import com.caracore.cca.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @BeforeEach
    public void setup() {
        // Clean up database before each test
        entityManager.getEntityManager().createQuery("DELETE FROM Usuario").executeUpdate();
    }

    @Test
    @DisplayName("Deve encontrar usuário pelo email quando existir")
    public void deveEncontrarUsuarioPeloEmail() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@exemplo.com");
        usuario.setNome("Usuario Teste");
        usuario.setSenha("$2a$10$hashedPassword");
        usuario.setRole("ROLE_USER");
        
        entityManager.persist(usuario);
        entityManager.flush();

        // Act
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail("teste@exemplo.com");

        // Assert
        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getEmail()).isEqualTo("teste@exemplo.com");
        assertThat(usuarioEncontrado.get().getNome()).isEqualTo("Usuario Teste");
    }

    @Test
    @DisplayName("Deve retornar vazio quando não encontrar usuário pelo email")
    public void deveRetornarVazioQuandoNaoEncontrarUsuarioPeloEmail() {
        // Act
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail("naoexiste@exemplo.com");

        // Assert
        assertThat(usuarioEncontrado).isEmpty();
    }

    @Test
    @DisplayName("Deve distinguir entre maiúsculas e minúsculas no email")
    public void deveDistinguirEntreMaiusculasEMinusculasNoEmail() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setEmail("usuario@exemplo.com");
        usuario.setNome("Usuario Teste");
        usuario.setSenha("$2a$10$hashedPassword");
        usuario.setRole("ROLE_USER");
        
        entityManager.persist(usuario);
        entityManager.flush();

        // Act
        Optional<Usuario> usuarioMinusculo = usuarioRepository.findByEmail("usuario@exemplo.com");
        Optional<Usuario> usuarioMaiusculo = usuarioRepository.findByEmail("USUARIO@exemplo.com");

        // Assert
        assertThat(usuarioMinusculo).isPresent();
        assertThat(usuarioMaiusculo).isEmpty();
    }
}
