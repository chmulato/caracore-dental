package com.caracore.cca.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    @DisplayName("Deve criar um usuário corretamente")
    public void deveCriarUsuario() {
        // Arrange
        Long id = 1L;
        String email = "teste@exemplo.com";
        String nome = "Usuario Teste";
        String senha = "senha123";
        String role = "ROLE_USER";

        // Act
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail(email);
        usuario.setNome(nome);
        usuario.setSenha(senha);
        usuario.setRole(role);

        // Assert
        assertEquals(id, usuario.getId(), "ID deve ser igual ao valor definido");
        assertEquals(email, usuario.getEmail(), "Email deve ser igual ao valor definido");
        assertEquals(nome, usuario.getNome(), "Nome deve ser igual ao valor definido");
        assertEquals(senha, usuario.getSenha(), "Senha deve ser igual ao valor definido");
        assertEquals(role, usuario.getRole(), "Role deve ser igual ao valor definido");
    }

    @Test
    @DisplayName("Deve criar um usuário através do construtor com parâmetros")
    public void deveCriarUsuarioComConstrutor() {
        // Arrange
        String email = "teste@exemplo.com";
        String nome = "Usuario Teste";
        String senha = "senha123";
        String role = "ROLE_USER";

        // Act
        Usuario usuario = new Usuario(email, nome, senha, role);

        // Assert
        assertEquals(email, usuario.getEmail(), "Email deve ser igual ao valor definido");
        assertEquals(nome, usuario.getNome(), "Nome deve ser igual ao valor definido");
        assertEquals(senha, usuario.getSenha(), "Senha deve ser igual ao valor definido");
        assertEquals(role, usuario.getRole(), "Role deve ser igual ao valor definido");
    }

    @Test
    @DisplayName("Deve comparar usuários corretamente pelo método equals")
    public void deveCompararUsuariosCorretamente() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setEmail("teste1@exemplo.com");

        Usuario usuario2 = new Usuario();
        usuario2.setId(1L);
        usuario2.setEmail("teste2@exemplo.com");

        Usuario usuario3 = new Usuario();
        usuario3.setId(2L);
        usuario3.setEmail("teste1@exemplo.com");

        // Assert
        assertEquals(usuario1, usuario1, "Um usuário deve ser igual a si mesmo");
        assertEquals(usuario1, usuario2, "Usuários com mesmo ID devem ser iguais");
        assertNotEquals(usuario1, usuario3, "Usuários com IDs diferentes não devem ser iguais");
        assertNotEquals(usuario1, null, "Um usuário não deve ser igual a null");
        assertNotEquals(usuario1, new Object(), "Um usuário não deve ser igual a um objeto de outro tipo");
    }

    @Test
    @DisplayName("Usuários com mesmo ID devem ter o mesmo hashCode")
    public void usuariosComMesmoIdDevemTerMesmoHashcode() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setEmail("teste1@exemplo.com");

        Usuario usuario2 = new Usuario();
        usuario2.setId(1L);
        usuario2.setEmail("teste2@exemplo.com");

        // Assert
        assertEquals(usuario1.hashCode(), usuario2.hashCode(),
                "Usuários com mesmo ID devem ter o mesmo hashCode");
    }
}
