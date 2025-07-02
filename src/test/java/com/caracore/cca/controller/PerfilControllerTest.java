package com.caracore.cca.controller;

import com.caracore.cca.config.PerfilTestConfig;
import com.caracore.cca.model.Usuario;
import com.caracore.cca.repository.UsuarioRepository;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe de teste para o PerfilController.
 * Testa as funcionalidades de visualização de perfil e alteração de senha.
 */
@WebMvcTest(controllers = PerfilController.class)
@Import(PerfilTestConfig.class)
public class PerfilControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserActivityLogger userActivityLogger;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("Deve negar acesso ao perfil para usuário não autenticado")
    public void deveNegarAcessoAoPerfilParaUsuarioNaoAutenticado() throws Exception {
        mockMvc.perform(get("/perfil"))
                .andExpect(status().isUnauthorized()); // Status 401 Unauthorized
    }

    @Test
    @DisplayName("Deve exibir perfil para usuário autenticado")
    @WithMockUser(username = "usuario@teste.com")
    public void deveExibirPerfilParaUsuarioAutenticado() throws Exception {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Nome do Usuário");
        usuario.setEmail("usuario@teste.com");
        usuario.setRole("ROLE_ADMIN");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(usuario));

        // Act & Assert
        mockMvc.perform(get("/perfil"))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil/perfil"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attribute("usuario", usuario));

        verify(userActivityLogger).logActivity(
                eq("VISUALIZAR_PERFIL"),
                eq("Visualização de perfil"),
                isNull(),
                eq("Usuário visualizou seu perfil")
        );
    }

    @Test
    @DisplayName("Deve redirecionar para dashboard quando usuário não for encontrado")
    @WithMockUser(username = "naoexiste@teste.com")
    public void deveRedirecionarParaDashboardQuandoUsuarioNaoForEncontrado() throws Exception {
        // Arrange
        when(usuarioRepository.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/perfil"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard?erro=usuario-nao-encontrado"));

        verify(userActivityLogger, never()).logActivity(anyString(), anyString(), any(), anyString());
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso quando todos os dados estiverem corretos")
    @WithMockUser(username = "usuario@teste.com")
    public void deveAlterarSenhaComSucessoQuandoDadosCorretos() throws Exception {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Nome do Usuário");
        usuario.setEmail("usuario@teste.com");
        usuario.setSenha("senhaAtualCriptografada");
        usuario.setRole("ROLE_ADMIN");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual", "senhaAtualCriptografada")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaCriptografada");
        when(usuarioRepository.atualizarSenha("usuario@teste.com", "novaSenhaCriptografada")).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/perfil/alterar-senha")
                        .with(csrf())
                        .param("senhaAtual", "senhaAtual")
                        .param("novaSenha", "novaSenha123")
                        .param("confirmacaoSenha", "novaSenha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"))
                .andExpect(flash().attribute("sucesso", "Senha alterada com sucesso"));

        // Verify repository call
        verify(usuarioRepository).atualizarSenha("usuario@teste.com", "novaSenhaCriptografada");
        
        // Verify activity log
        verify(userActivityLogger).logActivity(
                eq("ALTERAR_SENHA"),
                eq("Alteração de senha realizada"),
                isNull(),
                eq("Usuário alterou sua senha")
        );
    }

    @Test
    @DisplayName("Deve falhar ao alterar senha quando senha atual estiver incorreta")
    @WithMockUser(username = "usuario@teste.com")
    public void deveFalharAoAlterarSenhaQuandoSenhaAtualIncorreta() throws Exception {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Nome do Usuário");
        usuario.setEmail("usuario@teste.com");
        usuario.setSenha("senhaAtualCriptografada");
        usuario.setRole("ROLE_ADMIN");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "senhaAtualCriptografada")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/perfil/alterar-senha")
                        .with(csrf())
                        .param("senhaAtual", "senhaErrada")
                        .param("novaSenha", "novaSenha123")
                        .param("confirmacaoSenha", "novaSenha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"))
                .andExpect(flash().attribute("erro", "Senha atual incorreta"));

        // Verify repository was not called
        verify(usuarioRepository, never()).atualizarSenha(anyString(), anyString());
        verify(userActivityLogger, never()).logActivity(anyString(), anyString(), any(), anyString());
    }

    @Test
    @DisplayName("Deve falhar ao alterar senha quando as senhas novas não coincidem")
    @WithMockUser(username = "usuario@teste.com")
    public void deveFalharAoAlterarSenhaQuandoSenhasNaoCoincidentes() throws Exception {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Nome do Usuário");
        usuario.setEmail("usuario@teste.com");
        usuario.setSenha("senhaAtualCriptografada");
        usuario.setRole("ROLE_ADMIN");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual", "senhaAtualCriptografada")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/perfil/alterar-senha")
                        .with(csrf())
                        .param("senhaAtual", "senhaAtual")
                        .param("novaSenha", "novaSenha123")
                        .param("confirmacaoSenha", "outraSenha456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"))
                .andExpect(flash().attribute("erro", "Nova senha e confirmação não coincidem"));

        // Verify repository was not called
        verify(usuarioRepository, never()).atualizarSenha(anyString(), anyString());
        verify(userActivityLogger, never()).logActivity(anyString(), anyString(), any(), anyString());
    }

    @Test
    @DisplayName("Deve falhar ao alterar senha quando a nova senha for muito curta")
    @WithMockUser(username = "usuario@teste.com")
    public void deveFalharAoAlterarSenhaQuandoSenhaMuitoCurta() throws Exception {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Nome do Usuário");
        usuario.setEmail("usuario@teste.com");
        usuario.setSenha("senhaAtualCriptografada");
        usuario.setRole("ROLE_ADMIN");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual", "senhaAtualCriptografada")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/perfil/alterar-senha")
                        .with(csrf())
                        .param("senhaAtual", "senhaAtual")
                        .param("novaSenha", "123") // Senha muito curta
                        .param("confirmacaoSenha", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"))
                .andExpect(flash().attribute("erro", "A nova senha deve ter pelo menos 6 caracteres"));

        // Verify repository was not called
        verify(usuarioRepository, never()).atualizarSenha(anyString(), anyString());
        verify(userActivityLogger, never()).logActivity(anyString(), anyString(), any(), anyString());
    }

    @Test
    @DisplayName("Deve falhar ao alterar senha quando usuário não for encontrado")
    @WithMockUser(username = "naoexiste@teste.com")
    public void deveFalharAoAlterarSenhaQuandoUsuarioNaoEncontrado() throws Exception {
        // Arrange
        when(usuarioRepository.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/perfil/alterar-senha")
                        .with(csrf())
                        .param("senhaAtual", "senhaAtual")
                        .param("novaSenha", "novaSenha123")
                        .param("confirmacaoSenha", "novaSenha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"))
                .andExpect(flash().attribute("erro", "Usuário não encontrado"));

        // Verify repository was not called
        verify(usuarioRepository, never()).atualizarSenha(anyString(), anyString());
        verify(userActivityLogger, never()).logActivity(anyString(), anyString(), any(), anyString());
    }
}
