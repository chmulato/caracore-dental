package com.caracore.cca.controller;

import com.caracore.cca.config.TestConfig;
import com.caracore.cca.config.SecurityTestConfig;
import com.caracore.cca.config.UsuarioTestConfig;
import com.caracore.cca.model.Usuario;
import com.caracore.cca.repository.UsuarioRepository;
import com.caracore.cca.service.InitService;
import com.caracore.cca.util.UserActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({UsuarioController.class, AcessoNegadoController.class})
@Import({TestConfig.class, SecurityTestConfig.class, UsuarioTestConfig.class})
@ActiveProfiles("test")
public class UsuarioControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private UserActivityLogger userActivityLogger;

    @MockBean
    private InitService initService;

    private Usuario usuarioAdmin;
    private Usuario usuarioDentista;
    private Usuario usuarioRecepcao;
    private Usuario usuarioPaciente;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        // Cria usuários para testes
        usuarioAdmin = new Usuario();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setNome("Administrador de Testes");
        usuarioAdmin.setEmail("suporte@caracore.com.br");
        usuarioAdmin.setSenha("$2a$10$senha_hash_admin");
        usuarioAdmin.setRole("ROLE_ADMIN");

        usuarioDentista = new Usuario();
        usuarioDentista.setId(2L);
        usuarioDentista.setNome("Dentista Teste");
        usuarioDentista.setEmail("dentista@caracore.com.br");
        usuarioDentista.setSenha("$2a$10$senha_hash_dentista");
        usuarioDentista.setRole("ROLE_DENTIST");

        usuarioRecepcao = new Usuario();
        usuarioRecepcao.setId(3L);
        usuarioRecepcao.setNome("Recepção Teste");
        usuarioRecepcao.setEmail("recepcao@caracore.com.br");
        usuarioRecepcao.setSenha("$2a$10$senha_hash_recepcao");
        usuarioRecepcao.setRole("ROLE_RECEPTIONIST");

        usuarioPaciente = new Usuario();
        usuarioPaciente.setId(4L);
        usuarioPaciente.setNome("Paciente Teste");
        usuarioPaciente.setEmail("paciente@caracore.com.br");
        usuarioPaciente.setSenha("$2a$10$senha_hash_paciente");
        usuarioPaciente.setRole("ROLE_PATIENT");
    }

    // --- TESTES DE ACESSO À LISTAGEM DE USUÁRIOS ---

    @Test
    @DisplayName("Deve negar acesso à lista de usuários para usuário não autenticado")
    @WithAnonymousUser
    public void deveNegarAcessoAListaDeUsuariosParaUsuarioNaoAutenticado() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Deve negar acesso à lista de usuários para usuário não-admin")
    @WithMockUser(roles = {"DENTIST"})
    public void deveNegarAcessoAListaDeUsuariosParaUsuarioNaoAdmin() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden()); // 403 Forbidden para usuários não autorizados
    }

    @Test
    @DisplayName("Deve permitir acesso à lista de usuários para admin")
    @WithMockUser(roles = {"ADMIN"})
    public void devePermitirAcessoAListaDeUsuariosParaAdmin() throws Exception {
        List<Usuario> usuarios = Arrays.asList(usuarioAdmin, usuarioDentista, usuarioRecepcao, usuarioPaciente);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Reset do mock antes do teste
        reset(userActivityLogger);

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/lista"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attribute("usuarios", usuarios));

        verify(userActivityLogger, times(1)).logActivity(
                eq("LISTAR_USUARIOS"), 
                anyString(), 
                isNull(), 
                anyString());
    }

    // --- TESTES DE DETALHES DE USUÁRIO ---

    @Test
    @DisplayName("Deve exibir detalhes de um usuário existente para admin")
    @WithMockUser(roles = {"ADMIN"})
    public void deveExibirDetalhesDeUsuarioExistenteParaAdmin() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAdmin));

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/detalhes"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attribute("usuario", usuarioAdmin))
                .andExpect(model().attributeExists("usuarioPadrao"))
                .andExpect(model().attribute("usuarioPadrao", true));

        verify(userActivityLogger, times(1)).logActivity(
                eq("VISUALIZAR_USUARIO"), 
                anyString(), 
                anyString(), 
                anyString());
    }

    @Test
    @DisplayName("Deve redirecionar para lista quando usuário não existe")
    @WithMockUser(roles = {"ADMIN"})
    public void deveRedirecionarParaListaQuandoUsuarioNaoExiste() throws Exception {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attributeExists("erro"));
    }

    // --- TESTES DE BUSCA DE USUÁRIOS ---

    @Test
    @DisplayName("Deve buscar usuários por termo sem filtro de perfil")
    @WithMockUser(roles = {"ADMIN"})
    public void deveBuscarUsuariosPorTermoSemFiltroPerfil() throws Exception {
        List<Usuario> resultados = Arrays.asList(usuarioAdmin, usuarioDentista);
        when(usuarioRepository.findByEmailContainingIgnoreCaseOrNomeContainingIgnoreCase("teste", "teste"))
                .thenReturn(resultados);

        mockMvc.perform(get("/usuarios/buscar")
                .param("termo", "teste"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/lista"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attribute("usuarios", resultados))
                .andExpect(model().attributeExists("termoBusca"))
                .andExpect(model().attribute("termoBusca", "teste"));
    }

    @Test
    @DisplayName("Deve buscar usuários por termo com filtro de perfil")
    @WithMockUser(roles = {"ADMIN"})
    public void deveBuscarUsuariosPorTermoComFiltroPerfil() throws Exception {
        List<Usuario> resultadosSemFiltro = Arrays.asList(usuarioAdmin, usuarioDentista);
        List<Usuario> resultadosFiltrados = Arrays.asList(usuarioDentista);

        when(usuarioRepository.findByEmailContainingIgnoreCaseOrNomeContainingIgnoreCase("teste", "teste"))
                .thenReturn(resultadosSemFiltro);

        mockMvc.perform(get("/usuarios/buscar")
                .param("termo", "teste")
                .param("perfil", "ROLE_DENTIST"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/lista"));
    }

    // --- TESTES DE FORMULÁRIO DE NOVO USUÁRIO ---

    @Test
    @DisplayName("Deve exibir formulário de novo usuário para admin")
    @WithMockUser(roles = {"ADMIN"})
    public void deveExibirFormularioNovoUsuarioParaAdmin() throws Exception {
        mockMvc.perform(get("/usuarios/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"))
                .andExpect(model().attributeExists("usuario"));
    }

    // --- TESTES DE SALVAR NOVO USUÁRIO ---

    @Test
    @DisplayName("Deve salvar novo usuário com dados válidos")
    @WithMockUser(roles = {"ADMIN"})
    public void deveSalvarNovoUsuarioComDadosValidos() throws Exception {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome("Novo Usuario");
        novoUsuario.setEmail("novo@caracore.com.br");
        novoUsuario.setSenha("senha123");
        novoUsuario.setRole("ROLE_DENTIST");

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/usuarios/salvar")
                .with(csrf())
                .param("nome", novoUsuario.getNome())
                .param("email", novoUsuario.getEmail())
                .param("senha", novoUsuario.getSenha())
                .param("role", novoUsuario.getRole()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attributeExists("sucesso"));

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Não deve salvar usuário com email já cadastrado")
    @WithMockUser(roles = {"ADMIN"})
    public void naoDeveSalvarUsuarioComEmailJaCadastrado() throws Exception {
        when(usuarioRepository.findByEmail("suporte@caracore.com.br"))
                .thenReturn(Optional.of(usuarioAdmin));

        mockMvc.perform(post("/usuarios/salvar")
                .with(csrf())
                .param("nome", "Novo Admin")
                .param("email", "suporte@caracore.com.br")
                .param("senha", "senha123")
                .param("role", "ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"))
                .andExpect(model().attributeHasFieldErrors("usuario", "email"));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // --- TESTES DE EDIÇÃO DE USUÁRIO ---

    @Test
    @DisplayName("Deve exibir formulário de edição para usuário existente")
    @WithMockUser(roles = {"ADMIN"})
    public void deveExibirFormularioEdicaoParaUsuarioExistente() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAdmin));

        mockMvc.perform(get("/usuarios/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attribute("usuario", usuarioAdmin));
    }

    @Test
    @DisplayName("Deve redirecionar para lista ao tentar editar usuário inexistente")
    @WithMockUser(roles = {"ADMIN"})
    public void deveRedirecionarParaListaAoTentarEditarUsuarioInexistente() throws Exception {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/999/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attributeExists("erro"));
    }

    // --- TESTES DE ATUALIZAÇÃO DE USUÁRIO ---

    @Test
    @DisplayName("Deve atualizar dados de usuário existente")
    @WithMockUser(roles = {"ADMIN"})
    public void deveAtualizarDadosDeUsuarioExistente() throws Exception {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioDentista));
        when(usuarioRepository.findByEmail("dentista_atualizado@caracore.com.br")).thenReturn(Optional.empty());

        mockMvc.perform(post("/usuarios/2/atualizar")
                .with(csrf())
                .param("id", "2")
                .param("nome", "Dentista Atualizado")
                .param("email", "dentista_atualizado@caracore.com.br")
                .param("senha", "") // Não atualiza a senha
                .param("role", "ROLE_DENTIST"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attributeExists("sucesso"));

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve redefinir senha de usuário existente")
    @WithMockUser(roles = {"ADMIN"})
    public void deveRedefinirSenhaDeUsuarioExistente() throws Exception {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioDentista));

        mockMvc.perform(post("/usuarios/2/atualizar")
                .with(csrf())
                .param("id", "2")
                .param("nome", usuarioDentista.getNome())
                .param("email", usuarioDentista.getEmail())
                .param("role", usuarioDentista.getRole())
                .param("resetarSenha", "true"))
                .andExpect(status().is3xxRedirection());

        verify(initService, times(1)).redefinirSenhaUsuarioPadrao(usuarioDentista.getEmail());
    }

    // --- TESTES DE EXCLUSÃO DE USUÁRIO ---

    @Test
    @DisplayName("Deve excluir usuário normal")
    @WithMockUser(roles = {"ADMIN"})
    public void deveExcluirUsuarioNormal() throws Exception {
        Usuario usuarioNormal = new Usuario();
        usuarioNormal.setId(10L);
        usuarioNormal.setNome("Usuario Normal");
        usuarioNormal.setEmail("normal@example.com");
        usuarioNormal.setSenha("$2a$10$senha_hash");
        usuarioNormal.setRole("ROLE_DENTIST");

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioNormal));

        mockMvc.perform(post("/usuarios/10/excluir")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attributeExists("sucesso"));

        verify(usuarioRepository, times(1)).delete(usuarioNormal);
    }

    @Test
    @DisplayName("Não deve excluir usuário padrão do sistema")
    @WithMockUser(roles = {"ADMIN"})
    public void naoDeveExcluirUsuarioPadraoDoSistema() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAdmin));

        mockMvc.perform(post("/usuarios/1/excluir")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attributeExists("erro"));

        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }
}
