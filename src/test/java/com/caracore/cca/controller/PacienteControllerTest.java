package com.caracore.cca.controller;

import com.caracore.cca.config.PacienteTestConfig;
import com.caracore.cca.config.SecurityTestConfig;
import com.caracore.cca.config.TestConfig;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.repository.PacienteRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PacienteController.class)
@Import({TestConfig.class, PacienteTestConfig.class, SecurityTestConfig.class})
@ActiveProfiles("test")
public class PacienteControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private PacienteRepository pacienteRepository;

    private Paciente pacienteTeste;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        pacienteTeste = new Paciente();
        pacienteTeste.setId(1L);
        pacienteTeste.setNome("Paciente Teste");
        pacienteTeste.setEmail("paciente@teste.com");
        pacienteTeste.setTelefone("(11) 99999-9999");
    }

    // --- TESTES DE ACESSO À LISTAGEM ---

    @Test
    @DisplayName("Deve negar acesso à lista de pacientes para usuário não autenticado")
    @WithAnonymousUser
    public void deveNegarAcessoAListaDePacientesParaUsuarioNaoAutenticado() throws Exception {
        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isForbidden()); // 403 é esperado para usuário não autenticado na configuração atual
    }

    @Test
    @DisplayName("Deve permitir acesso à lista de pacientes para admin")
    @WithMockUser(roles = {"ADMIN"})
    public void devePermitirAcessoAListaDePacientesParaAdmin() throws Exception {
        List<Paciente> pacientes = Collections.singletonList(pacienteTeste);
        when(pacienteRepository.findAll()).thenReturn(pacientes);

        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/lista"))
                .andExpect(model().attributeExists("pacientes"))
                .andExpect(model().attribute("pacientes", pacientes));
    }

    @Test
    @DisplayName("Deve permitir acesso à lista de pacientes para dentista")
    @WithMockUser(roles = {"DENTIST"})
    public void devePermitirAcessoAListaDePacientesParaDentista() throws Exception {
        List<Paciente> pacientes = Collections.singletonList(pacienteTeste);
        when(pacienteRepository.findAll()).thenReturn(pacientes);

        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/lista"))
                .andExpect(model().attributeExists("pacientes"));
    }

    @Test
    @DisplayName("Deve permitir acesso à lista de pacientes para recepcionista")
    @WithMockUser(roles = {"RECEPTIONIST"})
    public void devePermitirAcessoAListaDePacientesParaRecepcionista() throws Exception {
        List<Paciente> pacientes = Arrays.asList(pacienteTeste);
        when(pacienteRepository.findAll()).thenReturn(pacientes);

        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/lista"));
    }

    @Test
    @DisplayName("Deve negar acesso à lista de pacientes para paciente")
    @WithMockUser(roles = {"PATIENT"})
    public void deveNegarAcessoAListaDePacientesParaPaciente() throws Exception {
        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isForbidden());
    }

    // --- TESTES DE CRIAÇÃO DE PACIENTES ---

    @Test
    @DisplayName("Deve permitir acesso ao formulário para adicionar novo paciente para admin")
    @WithMockUser(roles = {"ADMIN"})
    public void devePermitirFormularioNovoParaAdmin() throws Exception {
        mockMvc.perform(get("/pacientes/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/form"))
                .andExpect(model().attributeExists("paciente"));
    }

    @Test
    @DisplayName("Deve permitir acesso ao formulário para adicionar novo paciente para recepcionista")
    @WithMockUser(roles = {"RECEPTIONIST"})
    public void devePermitirFormularioNovoParaRecepcionista() throws Exception {
        mockMvc.perform(get("/pacientes/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/form"))
                .andExpect(model().attributeExists("paciente"));
    }

    @Test
    @DisplayName("Deve negar acesso ao formulário para adicionar novo paciente para dentista")
    @WithMockUser(roles = {"DENTIST"})
    public void deveNegarFormularioNovoParaDentista() throws Exception {
        mockMvc.perform(get("/pacientes/novo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve negar acesso ao formulário para adicionar novo paciente para paciente")
    @WithMockUser(roles = {"PATIENT"})
    public void deveNegarFormularioNovoParaPaciente() throws Exception {
        mockMvc.perform(get("/pacientes/novo"))
                .andExpect(status().isForbidden());
    }

    // --- TESTES DE SALVAMENTO DE PACIENTES ---

    @Test
    @DisplayName("Admin deve poder salvar paciente")
    @WithMockUser(roles = {"ADMIN"})
    public void devePermitirSalvarPacienteParaAdmin() throws Exception {
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteTeste);

        mockMvc.perform(post("/pacientes/salvar")
                        .param("nome", "Novo Paciente")
                        .param("email", "novo@teste.com")
                        .param("telefone", "(22) 98888-7777")
                        .with(csrf())) 
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pacientes"));
    }

    @Test
    @DisplayName("Recepcionista deve poder salvar paciente")
    @WithMockUser(roles = {"RECEPTIONIST"})
    public void devePermitirSalvarPacienteParaRecepcionista() throws Exception {
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteTeste);

        mockMvc.perform(post("/pacientes/salvar")
                        .param("nome", "Novo Paciente")
                        .param("email", "novo@teste.com")
                        .param("telefone", "(22) 98888-7777")
                        .with(csrf())) 
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pacientes"));
    }

    @Test
    @DisplayName("Dentista não deve poder salvar paciente")
    @WithMockUser(roles = {"DENTIST"})
    public void deveNegarSalvarPacienteParaDentista() throws Exception {
        mockMvc.perform(post("/pacientes/salvar")
                        .param("nome", "Novo Paciente")
                        .param("email", "novo@teste.com")
                        .param("telefone", "(22) 98888-7777")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Paciente não deve poder salvar paciente")
    @WithMockUser(roles = {"PATIENT"})
    public void deveNegarSalvarPacienteParaPaciente() throws Exception {
        mockMvc.perform(post("/pacientes/salvar")
                        .param("nome", "Novo Paciente")
                        .param("email", "novo@teste.com")
                        .param("telefone", "(22) 98888-7777")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    // --- TESTES DE VISUALIZAÇÃO DE DETALHES ---

    @Test
    @DisplayName("Admin deve poder visualizar detalhes do paciente")
    @WithMockUser(roles = {"ADMIN"})
    public void devePermitirVisualizarDetalhesParaAdmin() throws Exception {
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.of(pacienteTeste));

        mockMvc.perform(get("/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/detalhes"))
                .andExpect(model().attributeExists("paciente"))
                .andExpect(model().attribute("paciente", pacienteTeste));
    }

    @Test
    @DisplayName("Dentista deve poder visualizar detalhes do paciente")
    @WithMockUser(roles = {"DENTIST"})
    public void devePermitirVisualizarDetalhesParaDentista() throws Exception {
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.of(pacienteTeste));

        mockMvc.perform(get("/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/detalhes"))
                .andExpect(model().attributeExists("paciente"));
    }

    @Test
    @DisplayName("Recepcionista deve poder visualizar detalhes do paciente")
    @WithMockUser(roles = {"RECEPTIONIST"})
    public void devePermitirVisualizarDetalhesParaRecepcionista() throws Exception {
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.of(pacienteTeste));

        mockMvc.perform(get("/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/detalhes"))
                .andExpect(model().attributeExists("paciente"));
    }

    @Test
    @DisplayName("Paciente não deve poder visualizar detalhes de pacientes")
    @WithMockUser(roles = {"PATIENT"})
    public void deveNegarVisualizarDetalhesParaPaciente() throws Exception {
        mockMvc.perform(get("/pacientes/1"))
                .andExpect(status().isForbidden());
    }

    // --- TESTES DE EDIÇÃO DE PACIENTES ---

    @Test
    @DisplayName("Admin deve poder acessar formulário de edição")
    @WithMockUser(roles = {"ADMIN"})
    public void devePermitirEdicaoParaAdmin() throws Exception {
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.of(pacienteTeste));

        mockMvc.perform(get("/pacientes/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/form"))
                .andExpect(model().attributeExists("paciente"));
    }

    @Test
    @DisplayName("Recepcionista deve poder acessar formulário de edição")
    @WithMockUser(roles = {"RECEPTIONIST"})
    public void devePermitirEdicaoParaRecepcionista() throws Exception {
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.of(pacienteTeste));

        mockMvc.perform(get("/pacientes/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/form"))
                .andExpect(model().attributeExists("paciente"));
    }

    @Test
    @DisplayName("Dentista não deve poder acessar formulário de edição")
    @WithMockUser(roles = {"DENTIST"})
    public void deveNegarEdicaoParaDentista() throws Exception {
        mockMvc.perform(get("/pacientes/1/editar"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Paciente não deve poder acessar formulário de edição")
    @WithMockUser(roles = {"PATIENT"})
    public void deveNegarEdicaoParaPaciente() throws Exception {
        mockMvc.perform(get("/pacientes/1/editar"))
                .andExpect(status().isForbidden());
    }

    // --- TESTES DE EXCLUSÃO DE PACIENTES ---

    @Test
    @DisplayName("Admin deve poder excluir paciente")
    @WithMockUser(roles = {"ADMIN"})
    public void devePermitirExclusaoParaAdmin() throws Exception {
        when(pacienteRepository.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(get("/pacientes/1/excluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pacientes?sucesso=paciente-excluido"));
    }

    @Test
    @DisplayName("Recepcionista não deve poder excluir paciente")
    @WithMockUser(roles = {"RECEPTIONIST"})
    public void deveNegarExclusaoParaRecepcionista() throws Exception {
        mockMvc.perform(get("/pacientes/1/excluir"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Dentista não deve poder excluir paciente")
    @WithMockUser(roles = {"DENTIST"})
    public void deveNegarExclusaoParaDentista() throws Exception {
        mockMvc.perform(get("/pacientes/1/excluir"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Paciente não deve poder excluir paciente")
    @WithMockUser(roles = {"PATIENT"})
    public void deveNegarExclusaoParaPaciente() throws Exception {
        mockMvc.perform(get("/pacientes/1/excluir"))
                .andExpect(status().isForbidden());
    }

    // --- TESTES DE BUSCA DE PACIENTES ---

    @Test
    @DisplayName("Admin deve poder buscar pacientes")
    @WithMockUser(roles = {"ADMIN"})
    public void devePermitirBuscaParaAdmin() throws Exception {
        List<Paciente> pacientes = Collections.singletonList(pacienteTeste);
        when(pacienteRepository.findByNomeContainingIgnoreCase(anyString())).thenReturn(pacientes);

        mockMvc.perform(get("/pacientes/buscar")
                        .param("termo", "Teste"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/lista"))
                .andExpect(model().attributeExists("pacientes"))
                .andExpect(model().attribute("pacientes", pacientes));
    }

    @Test
    @DisplayName("Recepcionista deve poder buscar pacientes")
    @WithMockUser(roles = {"RECEPTIONIST"})
    public void devePermitirBuscaParaRecepcionista() throws Exception {
        List<Paciente> pacientes = Collections.singletonList(pacienteTeste);
        when(pacienteRepository.findByNomeContainingIgnoreCase(anyString())).thenReturn(pacientes);

        mockMvc.perform(get("/pacientes/buscar")
                        .param("termo", "Teste"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/lista"))
                .andExpect(model().attributeExists("pacientes"));
    }

    @Test
    @DisplayName("Dentista deve poder buscar pacientes")
    @WithMockUser(roles = {"DENTIST"})
    public void devePermitirBuscaParaDentista() throws Exception {
        List<Paciente> pacientes = Collections.singletonList(pacienteTeste);
        when(pacienteRepository.findByNomeContainingIgnoreCase(anyString())).thenReturn(pacientes);

        mockMvc.perform(get("/pacientes/buscar")
                        .param("termo", "Teste"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/lista"))
                .andExpect(model().attributeExists("pacientes"));
    }

    @Test
    @DisplayName("Paciente não deve poder buscar pacientes")
    @WithMockUser(roles = {"PATIENT"})
    public void deveNegarBuscaParaPaciente() throws Exception {
        mockMvc.perform(get("/pacientes/buscar")
                        .param("termo", "Teste"))
                .andExpect(status().isForbidden());
    }

    // --- TESTES DE FUNCIONALIDADES LGPD ---

    @Test
    @DisplayName("Deve salvar paciente com campos LGPD padrão")
    @WithMockUser(roles = {"ADMIN"})
    public void deveSalvarPacienteComCamposLgpdPadrao() throws Exception {
        // Configurar mock
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteTeste);

        mockMvc.perform(post("/pacientes/salvar")
                .with(csrf())
                .param("nome", "João Silva")
                .param("email", "joao@teste.com")
                .param("telefone", "(11) 98765-4321")
                .param("consentimentoLgpd", "false")
                .param("consentimentoConfirmado", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pacientes"));
    }

    @Test
    @DisplayName("Deve salvar paciente com consentimento LGPD enviado")
    @WithMockUser(roles = {"ADMIN"})
    public void deveSalvarPacienteComConsentimentoLgpdEnviado() throws Exception {
        // Configurar mock
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteTeste);

        mockMvc.perform(post("/pacientes/salvar")
                .with(csrf())
                .param("nome", "João Silva")
                .param("email", "joao@teste.com")
                .param("telefone", "(11) 98765-4321")
                .param("consentimentoLgpd", "true")
                .param("consentimentoConfirmado", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pacientes"));
    }

    @Test
    @DisplayName("Deve salvar paciente com consentimento LGPD confirmado")
    @WithMockUser(roles = {"ADMIN"})
    public void deveSalvarPacienteComConsentimentoLgpdConfirmado() throws Exception {
        // Configurar mock
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteTeste);

        mockMvc.perform(post("/pacientes/salvar")
                .with(csrf())
                .param("nome", "João Silva")
                .param("email", "joao@teste.com")
                .param("telefone", "(11) 98765-4321")
                .param("consentimentoLgpd", "true")
                .param("consentimentoConfirmado", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pacientes"));
    }

    @Test
    @DisplayName("Deve rejeitar cadastro sem telefone WhatsApp")
    @WithMockUser(roles = {"ADMIN"})
    public void deveRejeitarCadastroSemTelefoneWhatsApp() throws Exception {
        mockMvc.perform(post("/pacientes/salvar")
                .with(csrf())
                .param("nome", "João Silva")
                .param("email", "joao@teste.com")
                .param("telefone", "")  // Telefone vazio
                .param("consentimentoLgpd", "false")
                .param("consentimentoConfirmado", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pacientes/novo"));
    }

    @Test
    @DisplayName("Admin deve poder confirmar consentimento LGPD via API")
    @WithMockUser(roles = {"ADMIN"})
    public void devePermitirConfirmarConsentimentoLgpdViaAPI() throws Exception {
        // Setup
        pacienteTeste.setConsentimentoLgpd(true);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteTeste));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteTeste);

        mockMvc.perform(post("/pacientes/1/confirmar-lgpd")
                .with(csrf())
                .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Consentimento LGPD confirmado com sucesso!"));
    }

    @Test
    @DisplayName("Recepcionista deve poder confirmar consentimento LGPD via API")
    @WithMockUser(roles = {"RECEPTIONIST"})
    public void devePermitirRecepcionistaConfirmarConsentimentoLgpdViaAPI() throws Exception {
        // Setup
        pacienteTeste.setConsentimentoLgpd(true);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteTeste));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteTeste);

        mockMvc.perform(post("/pacientes/1/confirmar-lgpd")
                .with(csrf())
                .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Dentista não deve poder confirmar consentimento LGPD via API")
    @WithMockUser(roles = {"DENTIST"})
    public void deveNegarDentistaConfirmarConsentimentoLgpdViaAPI() throws Exception {
        mockMvc.perform(post("/pacientes/1/confirmar-lgpd")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve tratar erro ao confirmar LGPD para paciente inexistente")
    @WithMockUser(roles = {"ADMIN"})
    public void deveTratarErroAoConfirmarLgpdPacienteInexistente() throws Exception {
        when(pacienteRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/pacientes/999/confirmar-lgpd")
                .with(csrf())
                .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Paciente não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar data de nascimento do paciente se existir na base de dados")
    @WithMockUser(roles = {"ADMIN"})
    public void deveRetornarDataNascimentoSeExistir() throws Exception {
        java.time.LocalDate dataNascimento = java.time.LocalDate.of(1990, 5, 20);
        pacienteTeste.setDataNascimento(dataNascimento);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteTeste));

        mockMvc.perform(get("/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/detalhes"))
                .andExpect(model().attributeExists("paciente"))
                .andExpect(model().attribute("paciente", org.hamcrest.Matchers.hasProperty("dataNascimento", org.hamcrest.Matchers.equalTo(dataNascimento))));
    }
}
