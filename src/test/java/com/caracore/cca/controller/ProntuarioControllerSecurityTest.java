package com.caracore.cca.controller;

import com.caracore.cca.config.TestConfig;
import com.caracore.cca.config.SecurityTestConfig;
import com.caracore.cca.model.*;
import com.caracore.cca.service.DentistaService;
import com.caracore.cca.service.PacienteService;
import com.caracore.cca.service.ProntuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = ProntuarioController.class)
@Import({TestConfig.class, SecurityTestConfig.class})
@ActiveProfiles("test")
@DisplayName("Testes de Segurança do ProntuarioController")
class ProntuarioControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProntuarioService prontuarioService;

    @MockBean
    private DentistaService dentistaService;

    @MockBean
    private PacienteService pacienteService;
    
    @MockBean
    private com.caracore.cca.service.UsuarioDetailsService usuarioDetailsService;

    private Dentista dentista;
    private Dentista outroDentista;
    private ImagemRadiologica imagem;

    @BeforeEach
    void setUp() {
        dentista = new Dentista();
        dentista.setId(1L);
        dentista.setEmail("carlos@dentista.com");
        dentista.setNome("Dr. Carlos Silva");
        dentista.setCro("SP123456");

        outroDentista = new Dentista();
        outroDentista.setId(2L);
        outroDentista.setEmail("outro@dentista.com");
        outroDentista.setNome("Dr. João Santos");
        outroDentista.setCro("SP654321");

        imagem = new ImagemRadiologica();
        imagem.setId(1L);
        imagem.setNomeArquivo("rx_test.jpg");
        imagem.setTipoImagem("Radiografia Panorâmica");
        imagem.setDentista(dentista);
    }

    @Nested
    @DisplayName("Testes de Segurança e Autorização")
    class TestesSegurancaAutorizacao {

        @Test
        @DisplayName("Deve negar acesso para usuário não autenticado")
        void deveNegarAcessoParaUsuarioNaoAutenticado() throws Exception {
            mockMvc.perform(get("/prontuarios"))
                   .andDo(print())
                   .andExpect(status().is3xxRedirection())
                   .andExpect(redirectedUrl("http://localhost/login"));
        }

        @Test
        @DisplayName("Deve negar acesso para usuário sem role DENTIST")
        @WithMockUser(username = "usuario@email.com", roles = "USER")
        void deveNegarAcessoParaUsuarioSemRoleDentist() throws Exception {
            mockMvc.perform(get("/prontuarios")
                    .header("X-Requested-With", "XMLHttpRequest"))  // Indica que é uma requisição AJAX
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("Acesso negado"));
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
        when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
        
        // Teste CSRF no upload de imagem
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "test.jpg", "image/jpeg", "test".getBytes());

        // Tentativa sem CSRF token deve falhar com status 302 (redireção)
        // porque o Spring Security redireciona para a página de login por padrão
        mockMvc.perform(multipart("/prontuarios/1/imagem/upload")
                .file(arquivo)
                .param("tipoImagem", "Radiografia")
                .param("descricao", "Teste"))
                .andExpect(status().is3xxRedirection());

        // Teste CSRF no adicionar tratamento
        mockMvc.perform(post("/prontuarios/1/tratamento")
                .param("procedimento", "Restauração")
                .param("descricao", "Teste")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection());

        // Teste CSRF no upload AJAX
        String jsonContent = "{\"imagemBase64\":\"data:image/jpeg;base64,test\",\"nomeArquivo\":\"test.jpg\",\"tipoImagem\":\"Radiografia\"}";
        mockMvc.perform(post("/prontuarios/1/imagem/upload-ajax")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .header("X-Requested-With", "XMLHttpRequest")) // Marca como AJAX para obter 403
                .andExpect(status().isForbidden());
    }
    }

    @Nested
    @DisplayName("Testes de Validação e Limites")
    class TestesValidacaoLimites {

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
    }

    @Nested
    @DisplayName("Testes de Tratamento de Erros")
    class TestesTratamentoErros {

        @Test
        @DisplayName("Deve tratar erro quando paciente não existe")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveTratarErroQuandoPacienteNaoExiste() throws Exception {
            // Quando o paciente não existe, o controller retorna antes de chamar dentistaService.buscarPorEmail()
            // Então não precisamos mockar esse método aqui
            when(pacienteService.buscarPorId(anyLong())).thenReturn(Optional.empty());

            mockMvc.perform(get("/prontuarios/paciente/999"))
                    .andExpect(status().isInternalServerError())
                    .andDo(print());

            // Não verificamos dentistaService.buscarPorEmail() porque não é chamado neste cenário
            verify(pacienteService).buscarPorId(999L);
        }

        @Test
        @DisplayName("Deve tratar erro de conexão com banco")
        @WithMockUser(username = "carlos@dentista.com", roles = "DENTIST")
        void deveTratarErroConexaoBanco() throws Exception {
            when(dentistaService.buscarPorEmail("carlos@dentista.com")).thenReturn(Optional.of(dentista));
            when(prontuarioService.buscarProntuariosPorDentista(anyLong()))
                    .thenThrow(new RuntimeException("Erro de conexão com o banco de dados"));

            mockMvc.perform(get("/prontuarios"))
                    .andExpect(status().isInternalServerError());

            verify(dentistaService).buscarPorEmail("carlos@dentista.com");
            verify(prontuarioService).buscarProntuariosPorDentista(1L);
        }
    }
}
