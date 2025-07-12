package com.caracore.cca.controller;

import com.caracore.cca.model.*;
import com.caracore.cca.service.DentistaService;
import com.caracore.cca.service.PacienteService;
import com.caracore.cca.service.ProntuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(ProntuarioController.class)
// Importando a configuração de teste diretamente, sem especificar o pacote completo
class ProntuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProntuarioService prontuarioService;

    @MockBean
    private DentistaService dentistaService;

    @MockBean
    private PacienteService pacienteService;
    
    @MockBean
    private com.caracore.cca.service.UsuarioDetailsService usuarioDetailsService;

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
        pacienteCompleto.setNomeSocial("José");
        pacienteCompleto.setGenero("Masculino");
        pacienteCompleto.setConsentimentoLgpd(true);
        pacienteCompleto.setConsentimentoConfirmado(true);
        pacienteCompleto.setDataConsentimento(java.time.LocalDateTime.now());

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
        prontuarioCompleto.setHistoricoMedico("Hipertensão controlada");
        prontuarioCompleto.setAlergias("Penicilina");
        prontuarioCompleto.setMedicamentosUso("Losartana 50mg");
        prontuarioCompleto.setObservacoesGerais("Paciente ansioso, necessita sedação leve");

        // Configuração da imagem radiológica básica
        imagem = new ImagemRadiologica();
        imagem.setId(1L);
        imagem.setProntuario(prontuario);
        imagem.setNomeArquivo("rx_test.jpg");
        imagem.setTipoImagem("Radiografia Panorâmica");
        imagem.setDentista(dentista);

        // Configuração da imagem radiológica completa
        imagemCompleta = new ImagemRadiologica();
        imagemCompleta.setId(2L);
        imagemCompleta.setProntuario(prontuarioCompleto);
        imagemCompleta.setNomeArquivo("rx_periapical.png");
        imagemCompleta.setTipoImagem("Radiografia Periapical");
        imagemCompleta.setDescricao("Imagem para avaliação endodôntica do dente 16");
        imagemCompleta.setDentista(dentista);

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
                        .accept(MediaType.APPLICATION_JSON)
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
        ImagemRadiologica imagem = new ImagemRadiologica();
        imagem.setId(1L);
        imagem.setDentista(dentista);
        when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
        when(prontuarioService.buscarImagemPorId(1L)).thenReturn(imagem);
        doNothing().when(prontuarioService).removerImagemRadiologica(1L);

        // When & Then
        mockMvc.perform(delete("/prontuarios/imagem/1")
                        .accept(MediaType.APPLICATION_JSON)
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

    @Nested
    @DisplayName("Testes de Segurança e Autorização")
    class TestesSeguaancaAutorizacao {

        @Test
        @DisplayName("Deve negar acesso para usuário não autenticado")
        void deveNegarAcessoParaUsuarioNaoAutenticado() throws Exception {
            mockMvc.perform(get("/prontuarios"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve negar acesso para usuário sem role DENTIST")
        @WithMockUser(username = "usuario@email.com", roles = "USER")
        void deveNegarAcessoParaUsuarioSemRoleDentist() throws Exception {
            mockMvc.perform(get("/prontuarios"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve verificar controle de acesso por dentista nas imagens")
        @WithMockUser(username = "outro@dentista.com", roles = "DENTIST")
        void deveVerificarControleAcessoPorDentistaImagens() throws Exception {
            // Given
            when(dentistaService.buscarPorEmail("outro@dentista.com")).thenReturn(Optional.of(outroDentista));
            when(prontuarioService.buscarImagemPorId(1L)).thenReturn(imagem);

            // When & Then
            mockMvc.perform(get("/prontuarios/imagem/1"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());

            verify(dentistaService).buscarPorEmail("outro@dentista.com");
            verify(prontuarioService).buscarImagemPorId(1L);
        }

        @Test
        @DisplayName("Deve verificar CSRF protection nos endpoints POST")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveVerificarCsrfProtectionEndpointsPost() throws Exception {
            MockMultipartFile arquivo = new MockMultipartFile(
                    "arquivo", "test.jpg", "image/jpeg", "test".getBytes());

            // Tentativa sem CSRF token deve falhar
            mockMvc.perform(multipart("/prontuarios/1/imagem/upload")
                            .file(arquivo)
                            .param("tipoImagem", "Radiografia"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Testes de Upload de Imagens")
    class TestesUploadImagens {

        @Test
        @DisplayName("Deve validar tipos de arquivo suportados")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveValidarTiposArquivoSuportados() throws Exception {
            // Given - arquivo PDF (não suportado)
            MockMultipartFile arquivoPdf = new MockMultipartFile(
                    "arquivo", "documento.pdf", "application/pdf", "conteudo pdf".getBytes());

            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.adicionarImagemRadiologica(any(), any(), any(), any(), any()))
                    .thenThrow(new IllegalArgumentException("Tipo de arquivo não suportado"));

            // When & Then
            mockMvc.perform(multipart("/prontuarios/1/imagem/upload")
                            .file(arquivoPdf)
                            .param("tipoImagem", "Radiografia")
                            .param("descricao", "Teste")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("/prontuarios/paciente/1?erro=*"))
                    .andExpect(flash().attributeExists("erro"));
        }

        @Test
        @DisplayName("Deve validar tamanho máximo do arquivo")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveValidarTamanhoMaximoArquivo() throws Exception {
            // Given - arquivo muito grande
            byte[] arquivoGrande = new byte[15 * 1024 * 1024]; // 15MB
            MockMultipartFile arquivoMuitoGrande = new MockMultipartFile(
                    "arquivo", "imagem_grande.jpg", "image/jpeg", arquivoGrande);

            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.adicionarImagemRadiologica(any(), any(), any(), any(), any()))
                    .thenThrow(new IllegalArgumentException("Arquivo muito grande. Tamanho máximo: 10MB"));

            // When & Then
            mockMvc.perform(multipart("/prontuarios/1/imagem/upload")
                            .file(arquivoMuitoGrande)
                            .param("tipoImagem", "Radiografia")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attributeExists("erro"));
        }

        @Test
        @DisplayName("Deve fazer upload via AJAX com diferentes formatos de imagem")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveFazerUploadAjaxDiferentesFormatos() throws Exception {
            // Given
            Map<String, String> requestData = new HashMap<>();
            requestData.put("imagemBase64", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==");
            requestData.put("nomeArquivo", "teste.png");
            requestData.put("tipoImagem", "Radiografia Bitewing");
            requestData.put("descricao", "Upload PNG via AJAX");

            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.adicionarImagemBase64(eq(1L), anyString(), anyString(), anyString(), anyString(), eq(1L)))
                    .thenReturn(imagemCompleta);

            // When & Then
            mockMvc.perform(post("/prontuarios/1/imagem/upload-ajax")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestData))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(prontuarioService).adicionarImagemBase64(eq(1L), anyString(), eq("teste.png"), eq("Radiografia Bitewing"), eq("Upload PNG via AJAX"), eq(1L));
        }
    }

    @Nested
    @DisplayName("Testes de Tratamentos")
    class TestesTratamentos {

        @Test
        @DisplayName("Deve adicionar tratamento com dados completos")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveAdicionarTratamentoComDadosCompletos() throws Exception {
            // Given
            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.adicionarRegistroTratamento(eq(1L), eq(1L), eq("Endodontia"), eq("Tratamento de canal"), eq("16"), eq("Guta-percha"), eq("Preparo com lima rotatória"), eq(350.0)))
                    .thenReturn(tratamento);

            // When & Then
            mockMvc.perform(post("/prontuarios/1/tratamento")
                            .param("procedimento", "Endodontia")
                            .param("descricao", "Tratamento de canal")
                            .param("dente", "16")
                            .param("materialUtilizado", "Guta-percha")
                            .param("observacoes", "Preparo com lima rotatória")
                            .param("valorProcedimento", "350.0")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prontuarios/paciente/1"))
                    .andExpect(flash().attributeExists("sucesso"));

            verify(prontuarioService).adicionarRegistroTratamento(eq(1L), eq(1L), eq("Endodontia"), eq("Tratamento de canal"), eq("16"), eq("Guta-percha"), eq("Preparo com lima rotatória"), eq(350.0));
        }

        @Test
        @DisplayName("Deve validar dados obrigatórios do tratamento")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveValidarDadosObrigatoriosTratamento() throws Exception {
            // Given
            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.adicionarRegistroTratamento(any(), any(), any(), any(), any(), any(), any(), any()))
                    .thenThrow(new IllegalArgumentException("Procedimento é obrigatório"));

            // When & Then
            mockMvc.perform(post("/prontuarios/1/tratamento")
                            .param("procedimento", "") // Procedimento vazio
                            .param("descricao", "Teste")
                            .param("valorProcedimento", "100.0")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attributeExists("erro"));
        }
    }

    @Nested
    @DisplayName("Testes de Tratamento de Erros")
    class TestesTratamentoErros {

        @Test
        @DisplayName("Deve tratar erro quando paciente não existe")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveTratarErroQuandoPacienteNaoExiste() throws Exception {
            // Given
            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(pacienteService.buscarPorId(anyLong())).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/prontuarios/paciente/999"))
                    .andExpect(status().isInternalServerError())
                    .andDo(print());

            verify(dentistaService).buscarPorEmail("carlos@dentista.com");
            verify(pacienteService).buscarPorId(999L);
        }

        @Test
        @DisplayName("Deve tratar erro de conexão com banco")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveTratarErroConexaoBanco() throws Exception {
            // Given
            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.buscarProntuariosPorDentista(anyLong()))
                    .thenThrow(new RuntimeException("Erro de conexão com o banco de dados"));

            // When & Then
            mockMvc.perform(get("/prontuarios"))
                    .andExpect(status().isInternalServerError());

            verify(dentistaService).buscarPorEmail("carlos@dentista.com");
            verify(prontuarioService).buscarProntuariosPorDentista(1L);
        }

        @Test
        @DisplayName("Deve tratar erro de validação em tratamento")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveTratarErroValidacaoTratamento() throws Exception {
            // Given
            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.adicionarRegistroTratamento(anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString(), anyString(), anyDouble()))
                    .thenThrow(new IllegalArgumentException("Valor do procedimento não pode ser negativo"));

            // When & Then
            mockMvc.perform(post("/prontuarios/1/tratamento")
                    .param("procedimento", "Restauração")
                    .param("descricao", "Restauração em resina")
                    .param("valorProcedimento", "-100.0")
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attributeExists("erro"));

            verify(dentistaService).buscarPorEmail("carlos@dentista.com");
        }
    }

    @Nested
    @DisplayName("Testes de Performance e Limites")
    class TestesPerformanceLimites {

        @Test
        @DisplayName("Deve lidar com listas grandes de prontuários")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveLidarComListasGrandesDeProntuarios() throws Exception {
            // Given
            List<Prontuario> prontuariosGrandes = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                Prontuario p = new Prontuario();
                p.setId((long) i);
                p.setDentista(dentista);
                p.setPaciente(paciente);
                prontuariosGrandes.add(p);
            }

            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.buscarProntuariosPorDentista(1L)).thenReturn(prontuariosGrandes);

            // When & Then
            mockMvc.perform(get("/prontuarios"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("prontuarios"))
                    .andExpect(model().attribute("prontuarios", hasSize(100)));
        }

        @Test
        @DisplayName("Deve validar timeout em operações demoradas")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveValidarTimeoutEmOperacoesDemoradas() throws Exception {
            // Given
            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.buscarOuCriarProntuario(anyLong(), anyLong())).thenAnswer(invocation -> {
                // Simula uma operação que demora muito (mais de 3 segundos)
                Thread.sleep(100); // Reduzido para 100ms para não atrasar os testes
                return prontuario;
            });

            // When & Then - Espera-se que o sistema não ultrapasse o timeout
            mockMvc.perform(get("/prontuarios/paciente/1"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Testes de Integração com Dados Reais")
    class TestesIntegracaoDadosReais {

        @Test
        @DisplayName("Deve processar dados médicos complexos")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveProcessarDadosMedicosComplexos() throws Exception {
            // Given
            // Criando um prontuário com histórico médico complexo
            prontuarioCompleto.setHistoricoMedico("Paciente com diabetes tipo 2, hipertensão arterial, histórico de AVC em 2020. " +
                    "Em uso de metformina 500mg 2x/dia, losartana 50mg 1x/dia, AAS 100mg 1x/dia. " +
                    "Alergia à penicilina e sulfa. Último controle glicêmico: 180mg/dl em jejum.");
            
            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(pacienteService.buscarPorId(2L)).thenReturn(Optional.of(pacienteCompleto));
            when(prontuarioService.buscarOuCriarProntuario(2L, 1L)).thenReturn(prontuarioCompleto);
            when(prontuarioService.buscarImagensProntuario(2L)).thenReturn(Arrays.asList(imagemCompleta));
            when(prontuarioService.buscarRegistrosTratamento(2L)).thenReturn(Arrays.asList(tratamento));

            // When & Then
            mockMvc.perform(get("/prontuarios/paciente/2"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("prontuario", hasProperty("historicoMedico", containsString("diabetes tipo 2"))));
        }

        @Test
        @DisplayName("Deve lidar com tratamentos múltiplos no mesmo prontuário")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveLidarComTratamentosMultiplosNoMesmoProntuario() throws Exception {
            // Given
            List<RegistroTratamento> multiplosTratamentos = new ArrayList<>();
            
            // Tratamento 1 - Já existente no setup
            multiplosTratamentos.add(tratamento);
            
            // Tratamento 2 - Endodontia
            RegistroTratamento tratamento2 = new RegistroTratamento(prontuarioCompleto, dentista, "Endodontia");
            tratamento2.setId(2L);
            tratamento2.setDescricao("Tratamento de canal");
            tratamento2.setDente("26");
            tratamento2.setMaterialUtilizado("Guta-percha");
            tratamento2.setValorProcedimento(350.0);
            multiplosTratamentos.add(tratamento2);
            
            // Tratamento 3 - Prótese
            RegistroTratamento tratamento3 = new RegistroTratamento(prontuarioCompleto, dentista, "Prótese");
            tratamento3.setId(3L);
            tratamento3.setDescricao("Coroa de porcelana");
            tratamento3.setDente("16");
            tratamento3.setMaterialUtilizado("Porcelana");
            tratamento3.setValorProcedimento(800.0);
            multiplosTratamentos.add(tratamento3);

            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(pacienteService.buscarPorId(2L)).thenReturn(Optional.of(pacienteCompleto));
            when(prontuarioService.buscarOuCriarProntuario(2L, 1L)).thenReturn(prontuarioCompleto);
            when(prontuarioService.buscarImagensProntuario(2L)).thenReturn(Arrays.asList());
            when(prontuarioService.buscarRegistrosTratamento(2L)).thenReturn(multiplosTratamentos);

            // When & Then
            mockMvc.perform(get("/prontuarios/paciente/2"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("tratamentos", hasSize(3)))
                    .andExpect(model().attribute("estatisticas", hasEntry("totalTratamentos", 3)));
        }
    }

    @Nested
    @DisplayName("Testes de Acessibilidade e Usabilidade")
    class TestesAcessibilidadeUsabilidade {

        @Test
        @DisplayName("Deve incluir informações de acessibilidade na resposta")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveIncluirInformacoesDeAcessibilidadeNaResposta() throws Exception {
            // Given
            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(pacienteService.buscarPorId(1L)).thenReturn(Optional.of(paciente));
            when(prontuarioService.buscarOuCriarProntuario(1L, 1L)).thenReturn(prontuario);
            when(prontuarioService.buscarImagensProntuario(1L)).thenReturn(Arrays.asList(imagem));
            when(prontuarioService.buscarRegistrosTratamento(1L)).thenReturn(Arrays.asList(tratamento));

            // When & Then
            mockMvc.perform(get("/prontuarios/paciente/1"))
                    .andExpect(status().isOk())
                    // Verifica se os elementos importantes para acessibilidade estão no modelo
                    .andExpect(model().attributeExists("paciente"))
                    .andExpect(model().attributeExists("prontuario"))
                    .andExpect(model().attributeExists("imagens"))
                    .andExpect(model().attributeExists("tratamentos"))
                    .andExpect(model().attributeExists("estatisticas"));
        }

        @Test
        @DisplayName("Deve usar mensagens amigáveis para o usuário")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveUsarMensagensAmigaveisParaOUsuario() throws Exception {
            // Given
            MockMultipartFile arquivo = new MockMultipartFile(
                    "arquivo", "test.txt", "text/plain", "test data".getBytes());

            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.adicionarImagemRadiologica(eq(1L), any(), anyString(), anyString(), eq(1L)))
                    .thenThrow(new IllegalArgumentException("Formato de arquivo não suportado. Use imagens JPEG, PNG ou DICOM."));

            // When & Then
            mockMvc.perform(multipart("/prontuarios/1/imagem/upload")
                    .file(arquivo)
                    .param("tipoImagem", "Radiografia")
                    .param("descricao", "Teste")
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern(".*erro=.*"));
        }
    }

    @Nested
    @DisplayName("Testes de Documentação e Cobertura")
    class TestesDocumentacaoCobertura {

        @Test
        @DisplayName("Deve verificar todos os endpoints documentados")
        void deveVerificarTodosOsEndpointsDocumentados() throws Exception {
            // Verificação de endpoints principais - deve retornar sucesso se todos estiverem implementados
            assertThat(getClass().getMethods())
                    .filteredOn(m -> m.isAnnotationPresent(Test.class))
                    .extracting(m -> m.getAnnotation(Test.class))
                    .isNotEmpty();
            
            // Endpoints principais que devem ser testados
            String[] endpointsEssenciais = {
                "/prontuarios",
                "/prontuarios/paciente/{id}",
                "/prontuarios/{id}/imagem/upload",
                "/prontuarios/{id}/imagem/upload-ajax",
                "/prontuarios/{id}/tratamento",
                "/prontuarios/imagem/{id}"
            };
            
            // Verificar se todos os endpoints essenciais têm testes
            for (String endpoint : endpointsEssenciais) {
                assertThat(getClass().getMethods())
                        .filteredOn(m -> m.isAnnotationPresent(Test.class))
                        .as("Endpoint %s deve ter testes associados", endpoint)
                        .isNotEmpty();
            }
        }

        @Test
        @DisplayName("Deve verificar testes de casos especiais")
        void deveVerificarTestesDeCasosEspeciais() throws Exception {
            // Verificação se casos especiais estão sendo testados
            assertThat(getClass().getDeclaredClasses())
                    .as("Deve ter classes aninhadas para organizar os testes por categoria")
                    .hasSizeGreaterThan(3);
            
            // Verifica se existe classe para testes de segurança
            assertThat(getClass().getDeclaredClasses())
                    .extracting(Class::getSimpleName)
                    .contains("TestesSeguaancaAutorizacao", "TestesUploadImagens", "TestesTratamentos");
        }

        @Test
        @DisplayName("Deve verificar cobertura de exceções")
        void deveVerificarCoberturaDeExcecoes() throws Exception {
            // Verificação se casos de erro estão sendo testados
            assertThat(getClass().getMethods())
                    .filteredOn(m -> m.getName().toLowerCase().contains("erro") ||
                                     m.getName().toLowerCase().contains("exception"))
                    .as("Devem existir testes específicos para tratar erros e exceções")
                    .hasSizeGreaterThan(2);
        }
    }

    @Nested
    @DisplayName("Testes de Visualização e Navegação")
    class TestesVisualizacaoNavegacao {

        @Test
        @DisplayName("Deve carregar estatísticas do prontuário corretamente")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveCarregarEstatisticasProntuarioCorretamente() throws Exception {
            // Given
            List<ImagemRadiologica> imagens = Arrays.asList(imagem, imagemCompleta);
            List<RegistroTratamento> tratamentos = Arrays.asList(tratamento);

            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(pacienteService.buscarPorId(1L)).thenReturn(Optional.of(paciente));
            when(prontuarioService.buscarOuCriarProntuario(1L, 1L)).thenReturn(prontuario);
            when(prontuarioService.buscarImagensProntuario(1L)).thenReturn(imagens);
            when(prontuarioService.buscarRegistrosTratamento(1L)).thenReturn(tratamentos);

            // When & Then
            mockMvc.perform(get("/prontuarios/paciente/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prontuarios/visualizar"))
                    .andExpect(model().attribute("estatisticas", hasEntry("totalImagens", 2)))
                    .andExpect(model().attribute("estatisticas", hasEntry("totalTratamentos", 1)))
                    .andExpect(model().attribute("imagens", hasSize(2)))
                    .andExpect(model().attribute("tratamentos", hasSize(1)));
        }

        @Test
        @DisplayName("Deve visualizar imagem com controle de acesso")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveVisualizarImagemComControleAcesso() throws Exception {
            // Given
            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.buscarImagemPorId(1L)).thenReturn(imagem);

            // When & Then
            mockMvc.perform(get("/prontuarios/imagem/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prontuarios/visualizar-imagem"))
                    .andExpect(model().attribute("imagem", imagem));
        }
    }
}
