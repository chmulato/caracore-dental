package com.caracore.cca.controller;

import com.caracore.cca.model.*;
import com.caracore.cca.service.DentistaService;
import com.caracore.cca.service.PacienteService;
import com.caracore.cca.service.ProntuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@WebMvcTest(ProntuarioController.class)
@Import({com.caracore.cca.config.SecurityConfig.class})
class ProntuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProntuarioService prontuarioService;

    @MockBean
    private DentistaService dentistaService;

    @MockBean
    private PacienteService pacienteService;

    @MockBean
    private ObjectMapper objectMapper;
    
    @MockBean
    private com.caracore.cca.service.UsuarioDetailsService usuarioDetailsService;
    
    @MockBean
    private org.springframework.security.web.access.AccessDeniedHandler accessDeniedHandler;

    // Entidades de teste
    private Dentista dentista;
    private Dentista outroDentista;
    private Paciente paciente;
    private Paciente pacienteCompleto;
    private Prontuario prontuario;
    private Prontuario prontuarioCompleto;
    private ImagemRadiologica imagem;
    private ImagemRadiologica imagemCompleta;
    private RegistroTratamento tratamento;

    @BeforeEach
    void setUp() {
        // Configuração do dentista principal
        dentista = new Dentista();
        dentista.setId(1L);
        dentista.setEmail("carlos@dentista.com");
        dentista.setNome("Dr. Carlos Silva");
        dentista.setCro("SP123456");
        dentista.setTelefone("(11) 99999-9999");
        dentista.setEspecialidade("Ortodontia");

        // Configuração de outro dentista para testes de acesso
        outroDentista = new Dentista();
        outroDentista.setId(2L);
        outroDentista.setEmail("outro@dentista.com");
        outroDentista.setNome("Dr. João Santos");
        outroDentista.setCro("SP654321");

        // Configuração do paciente básico
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("Maria Silva");
        paciente.setEmail("maria@email.com");
        paciente.setTelefone("(11) 88888-8888");
        paciente.setDataNascimento(LocalDate.of(1990, 5, 15));

        // Configuração do paciente completo com dados LGPD
        pacienteCompleto = new Paciente();
        pacienteCompleto.setId(2L);
        pacienteCompleto.setNome("José Santos");
        pacienteCompleto.setEmail("jose@email.com");
        pacienteCompleto.setTelefone("(11) 77777-7777");
        pacienteCompleto.setDataNascimento(LocalDate.of(1985, 3, 20));
        pacienteCompleto.setCpf("123.456.789-00");
        pacienteCompleto.setRg("12.345.678-9");
        pacienteCompleto.setEndereco("Rua das Flores, 123 - São Paulo/SP");
        pacienteCompleto.setProfissao("Engenheiro");
        pacienteCompleto.setEstadoCivil("Casado");
        pacienteCompleto.setContatoEmergencia("Ana Santos - (11) 66666-6666");
        pacienteCompleto.setConvenio("Unimed");
        pacienteCompleto.setNumeroConvenio("123456789");
        pacienteCompleto.setConsentimentoLgpd(true);
        pacienteCompleto.setDataConsentimentoLgpd(LocalDate.now());

        // Configuração do prontuário básico
        prontuario = new Prontuario();
        prontuario.setId(1L);
        prontuario.setPaciente(paciente);
        prontuario.setDentista(dentista);

        // Configuração do prontuário completo
        prontuarioCompleto = new Prontuario();
        prontuarioCompleto.setId(2L);
        prontuarioCompleto.setPaciente(pacienteCompleto);
        prontuarioCompleto.setDentista(dentista);
        prontuarioCompleto.setQueixaPrincipal("Dor no dente posterior direito");
        prontuarioCompleto.setHistoricoMedico("Hipertensão controlada");
        prontuarioCompleto.setAlergias("Penicilina");
        prontuarioCompleto.setMedicamentosUso("Losartana 50mg");
        prontuarioCompleto.setObservacoes("Paciente ansioso, necessita sedação leve");

        // Configuração da imagem radiológica básica
        imagem = new ImagemRadiologica();
        imagem.setId(1L);
        imagem.setProntuario(prontuario);
        imagem.setNomeArquivo("rx_test.jpg");
        imagem.setTipoImagem("Radiografia Panorâmica");
        imagem.setDentistaUpload(dentista);

        // Configuração da imagem radiológica completa
        imagemCompleta = new ImagemRadiologica();
        imagemCompleta.setId(2L);
        imagemCompleta.setProntuario(prontuarioCompleto);
        imagemCompleta.setNomeArquivo("rx_periapical.png");
        imagemCompleta.setTipoImagem("Radiografia Periapical");
        imagemCompleta.setDescricao("Imagem para avaliação endodôntica do dente 16");
        imagemCompleta.setDentistaUpload(dentista);

        // Configuração do registro de tratamento
        tratamento = new RegistroTratamento(prontuarioCompleto, dentista, "Restauração");
        tratamento.setId(1L);
        tratamento.setDescricao("Restauração em resina composta");
        tratamento.setDente("16");
        tratamento.setMaterialUtilizado("Resina composta");
        tratamento.setObservacoes("Isolamento absoluto realizado");
        tratamento.setValorProcedimento(150.0);
        tratamento.setStatus(RegistroTratamento.StatusTratamento.CONCLUIDO);
    }

    @Test
    @DisplayName("Deve listar prontuários do dentista autenticado")
    @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
    void deveListarProntuariosDoDentista() throws Exception {
        // Given
        List<Prontuario> prontuarios = Arrays.asList(prontuario);
        when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
        when(prontuarioService.buscarProntuariosPorDentista(1L)).thenReturn(prontuarios);

        // When & Then
        mockMvc.perform(get("/prontuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("prontuarios/meus-prontuarios"))
                .andExpect(model().attributeExists("prontuarios"))
                .andExpect(model().attribute("prontuarios", prontuarios));

        verify(dentistaService).buscarPorEmail("carlos@dentista.com");
        verify(prontuarioService).buscarProntuariosPorDentista(1L);
    }

    @Test
    @DisplayName("Deve visualizar prontuário de paciente")
    @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
    void deveVisualizarProntuarioPaciente() throws Exception {
        // Given
        when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
        when(pacienteService.buscarPorId(1L)).thenReturn(Optional.of(paciente));
        when(prontuarioService.buscarOuCriarProntuario(1L, 1L)).thenReturn(prontuario);
        when(prontuarioService.buscarImagensProntuario(1L)).thenReturn(Arrays.asList(imagem));
        when(prontuarioService.buscarRegistrosTratamento(1L)).thenReturn(Arrays.asList(tratamento));

        // When & Then
        mockMvc.perform(get("/prontuarios/paciente/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("prontuarios/visualizar"))
                .andExpect(model().attributeExists("paciente"))
                .andExpect(model().attributeExists("prontuario"))
                .andExpect(model().attributeExists("imagens"))
                .andExpect(model().attributeExists("tratamentos"));

        verify(pacienteService).buscarPorId(1L);
        verify(prontuarioService).buscarOuCriarProntuario(1L, 1L);
    }

    @Test
    @DisplayName("Deve fazer upload de imagem com sucesso")
    @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
    void deveFazerUploadImagemComSucesso() throws Exception {
        // Given
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "test.jpg", "image/jpeg", "test image data".getBytes());

        when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
        when(prontuarioService.adicionarImagemRadiologica(eq(1L), any(), anyString(), anyString(), eq(1L)))
                .thenReturn(imagem);

        // When & Then
        mockMvc.perform(multipart("/prontuarios/1/imagem/upload")
                        .file(arquivo)
                        .param("tipoImagem", "Radiografia Panorâmica")
                        .param("descricao", "Exame inicial")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prontuarios/paciente/1"));

        verify(prontuarioService).adicionarImagemRadiologica(eq(1L), any(), eq("Radiografia Panorâmica"), eq("Exame inicial"), eq(1L));
    }

    @Test
    @DisplayName("Deve fazer upload AJAX de imagem")
    @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
    void deveFazerUploadAjaxImagem() throws Exception {
        // Given
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("imagemBase64", "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAAAAAAAD");
        requestData.put("nomeArquivo", "test.jpg");
        requestData.put("tipoImagem", "Radiografia Panorâmica");
        requestData.put("descricao", "Teste");

        when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
        when(prontuarioService.adicionarImagemBase64(anyLong(), anyString(), anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(imagem);

        // When & Then
        mockMvc.perform(post("/prontuarios/1/imagem/upload-ajax")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"imagemBase64\":\"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAAAAAAAD\",\"nomeArquivo\":\"test.jpg\",\"tipoImagem\":\"Radiografia Panorâmica\",\"descricao\":\"Teste\"}")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Deve adicionar tratamento com sucesso")
    @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
    void deveAdicionarTratamento() throws Exception {
        // Given
        when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
        when(prontuarioService.adicionarRegistroTratamento(anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString(), anyString(), anyDouble()))
                .thenReturn(tratamento);

        // When & Then
        mockMvc.perform(post("/prontuarios/1/tratamento")
                        .param("procedimento", "Restauração")
                        .param("descricao", "Restauração em resina")
                        .param("dente", "16")
                        .param("materialUtilizado", "Resina composta")
                        .param("observacoes", "Isolamento absoluto")
                        .param("valorProcedimento", "150.0")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prontuarios/paciente/1"));

        verify(prontuarioService).adicionarRegistroTratamento(eq(1L), eq(1L), eq("Restauração"), eq("Restauração em resina"), eq("16"), eq("Resina composta"), eq("Isolamento absoluto"), eq(150.0));
    }

    @Test
    @DisplayName("Deve visualizar imagem em tela cheia")
    @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
    void deveVisualizarImagemTelaCheia() throws Exception {
        // Given
        when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
        when(prontuarioService.buscarImagemPorId(1L)).thenReturn(imagem);

        // When & Then
        mockMvc.perform(get("/prontuarios/imagem/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("prontuarios/visualizar-imagem"))
                .andExpect(model().attributeExists("imagem"));

        verify(prontuarioService).buscarImagemPorId(1L);
    }

    @Test
    @DisplayName("Deve remover imagem com sucesso")
    @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
    void deveRemoverImagem() throws Exception {
        // Given
        when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
        doNothing().when(prontuarioService).removerImagemRadiologica(1L);

        // When & Then
        mockMvc.perform(delete("/prontuarios/imagem/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(prontuarioService).removerImagemRadiologica(1L);
    }

    @Test
    @DisplayName("Deve retornar erro quando dentista não encontrado")
    @WithMockUser(username = "inexistente@email.com", roles = "DENTIST")
    void deveRetornarErroQuandoDentistaNaoEncontrado() throws Exception {
        // Given
        when(dentistaService.buscarPorEmail("inexistente@email.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/prontuarios"))
                .andExpect(status().isInternalServerError());

        verify(dentistaService).buscarPorEmail("inexistente@email.com");
        verify(prontuarioService, never()).buscarProntuariosPorDentista(anyLong());
    }

    @Test
    @DisplayName("Deve tratar erro no upload de imagem")
    @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
    void deveTratarErroUploadImagem() throws Exception {
        // Given
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "test.jpg", "image/jpeg", "test image data".getBytes());

        when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
        when(prontuarioService.adicionarImagemRadiologica(eq(1L), any(), anyString(), anyString(), eq(1L)))
                .thenThrow(new RuntimeException("Erro no upload"));

        // When & Then
        mockMvc.perform(multipart("/prontuarios/1/imagem/upload")
                        .file(arquivo)
                        .param("tipoImagem", "Radiografia Panorâmica")
                        .param("descricao", "Exame inicial")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prontuarios/paciente/1?erro=Erro no upload"));
    }

    @Test
    @DisplayName("Deve verificar acesso negado para imagem de outro dentista")
    @WithMockUser(username = "outro@dentista.com", roles = "DENTIST")
    void deveVerificarAcessoNegadoParaImagemOutroDentista() throws Exception {
        // Given
        Dentista outroDentista = new Dentista();
        outroDentista.setId(2L);
        outroDentista.setEmail("outro@dentista.com");

        when(dentistaService.buscarPorEmail("outro@dentista.com")).thenReturn(Optional.of(outroDentista));
        when(prontuarioService.buscarImagemPorId(1L)).thenReturn(imagem);

        // When & Then
        mockMvc.perform(get("/prontuarios/imagem/1"))
                .andExpect(status().isInternalServerError());
    }
}
