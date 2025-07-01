package com.caracore.cca.controller;

import com.caracore.cca.config.PacienteTestConfig;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
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

@WebMvcTest(PacienteController.class)
@Import({TestConfig.class, PacienteTestConfig.class})
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

    @Test
    @DisplayName("Deve exibir formulário para adicionar novo paciente")
    @WithMockUser(roles = {"ADMIN"})
    public void deveExibirFormularioParaAdicionarNovoPaciente() throws Exception {
        mockMvc.perform(get("/pacientes/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/form"))
                .andExpect(model().attributeExists("paciente"));
    }

    @Test
    @DisplayName("Deve salvar paciente e redirecionar para lista")
    @WithMockUser(roles = {"ADMIN"})
    public void deveSalvarPacienteERedirecionar() throws Exception {
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteTeste);

        mockMvc.perform(post("/pacientes/salvar")
                        .param("nome", "Novo Paciente")
                        .param("email", "novo@teste.com")
                        .param("telefone", "(22) 98888-7777")
                        .with(csrf())) // Adiciona token CSRF
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pacientes"));
    }

    @Test
    @DisplayName("Deve exibir detalhes de um paciente")
    @WithMockUser(roles = {"ADMIN"})
    public void deveExibirDetalhesDePaciente() throws Exception {
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.of(pacienteTeste));

        mockMvc.perform(get("/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/detalhes"))
                .andExpect(model().attributeExists("paciente"))
                .andExpect(model().attribute("paciente", pacienteTeste));
    }

    @Test
    @DisplayName("Deve redirecionar para edição de paciente")
    @WithMockUser(roles = {"ADMIN"})
    public void deveRedirecionarParaEdicaoDePaciente() throws Exception {
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.of(pacienteTeste));

        mockMvc.perform(get("/pacientes/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/form"))
                .andExpect(model().attributeExists("paciente"))
                .andExpect(model().attribute("paciente", pacienteTeste));
    }

    @Test
    @DisplayName("Deve excluir paciente e redirecionar")
    @WithMockUser(roles = {"ADMIN"})
    public void deveExcluirPacienteERedirecionar() throws Exception {
        when(pacienteRepository.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(get("/pacientes/1/excluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pacientes?sucesso=paciente-excluido"));
    }

    @Test
    @DisplayName("Deve buscar pacientes por nome")
    @WithMockUser(roles = {"RECEPTIONIST"})
    public void deveBuscarPacientesPorNome() throws Exception {
        List<Paciente> pacientes = Collections.singletonList(pacienteTeste);
        when(pacienteRepository.findByNomeContainingIgnoreCase(anyString())).thenReturn(pacientes);

        mockMvc.perform(get("/pacientes/buscar")
                        .param("termo", "Teste"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/lista"))
                .andExpect(model().attributeExists("pacientes"))
                .andExpect(model().attribute("pacientes", pacientes));
    }
}
