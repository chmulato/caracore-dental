package com.caracore.cca.controller;

import com.caracore.cca.dto.AgendamentoForm;
import com.caracore.cca.model.Agendamento;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.repository.AgendamentoRepository;
import com.caracore.cca.service.PacienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgendamentoControllerTest {
    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private PacienteService pacienteService;

    private AgendamentoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Corrigido: passar os dois mocks necessários ao construtor
        controller = new AgendamentoController(agendamentoRepository, pacienteService);
    }

    @Test
    @DisplayName("Deve gerar link do WhatsApp corretamente")
    void deveGerarLinkWhatsApp() {
        // Testes para diferentes formatos de telefone
        String link1 = controller.gerarLinkWhatsApp("(11) 98765-4321");
        String link2 = controller.gerarLinkWhatsApp("11987654321");
        String link3 = controller.gerarLinkWhatsApp("(11)98765-4321");
        
        // Todos devem gerar o mesmo link
        assertThat(link1).isEqualTo("https://wa.me/5511987654321");
        assertThat(link2).isEqualTo("https://wa.me/5511987654321");
        assertThat(link3).isEqualTo("https://wa.me/5511987654321");
    }
    
    @Test
    @DisplayName("Deve tratar telefones inválidos corretamente")
    void deveTratarTelefonesInvalidos() {
        // Telefones inválidos ou incompletos
        String link1 = controller.gerarLinkWhatsApp(null);
        String link2 = controller.gerarLinkWhatsApp("");
        String link3 = controller.gerarLinkWhatsApp("123");
        
        // Devem retornar "#"
        assertThat(link1).isEqualTo("#");
        assertThat(link2).isEqualTo("#");
        assertThat(link3).isEqualTo("#");
    }
    
    @ParameterizedTest
    @DisplayName("Deve gerar links WhatsApp para diferentes formatos de telefone")
    @ValueSource(strings = {
        "(11) 98765-4321", 
        "11987654321",
        "11 98765 4321",
        "(11) 987654321",
        "11 9 8765 4321"
    })
    void deveGerarLinkWhatsAppParaDiferentesFormatos(String telefone) {
        String link = controller.gerarLinkWhatsApp(telefone);
        assertThat(link).isEqualTo("https://wa.me/5511987654321");
    }
    
    @Test
    @DisplayName("Endpoint gerarLinkWhatsAppEndpoint deve retornar resposta correta")
    void deveRetornarRespostaCorretaLinkWhatsapp() {
        // Dado um telefone válido
        String telefone = "(11) 98765-4321";
        
        // Quando chamar o endpoint
        Map<String, String> resposta = controller.gerarLinkWhatsAppEndpoint(telefone);
        
        // Então deve retornar informações corretas
        assertThat(resposta.get("link")).isEqualTo("https://wa.me/5511987654321");
        assertThat(resposta.get("telefoneOriginal")).isEqualTo(telefone);
        assertThat(resposta.get("valido")).isEqualTo("true");
    }
    
    @Test
    @DisplayName("Endpoint gerarLinkWhatsAppEndpoint deve retornar erro para telefone inválido")
    void deveRetornarErroLinkWhatsappTelefoneInvalido() {
        // Dado um telefone inválido
        String telefone = "123";
        
        // Quando chamar o endpoint
        Map<String, String> resposta = controller.gerarLinkWhatsAppEndpoint(telefone);
        
        // Então deve retornar erro
        assertThat(resposta.get("link")).isEqualTo("#");
        assertThat(resposta.get("telefoneOriginal")).isEqualTo(telefone);
        assertThat(resposta.get("valido")).isEqualTo("false");
        assertThat(resposta.get("mensagem")).isEqualTo("Número de telefone inválido");
    }
    
    @Test
    @DisplayName("Endpoint buscarPacientePorNome deve retornar dados do paciente")
    void deveRetornarDadosPaciente() {
        // Dados de teste
        String nome = "Maria da Silva";
        Paciente paciente = new Paciente(nome, "maria@teste.com", "(11) 98765-4321");
        
        // Mock do serviço
        when(pacienteService.buscarPorNome(eq(nome)))
                .thenReturn(Collections.singletonList(paciente));
        
        try {
            // Quando chamar o endpoint
            Map<String, Object> resposta = controller.buscarPacientePorNome(nome);
            
            // Então deve retornar os dados corretos
            assertThat(resposta.get("encontrado")).isEqualTo(true); // Usamos isEqualTo em vez de isTrue
            assertThat(resposta.get("nome")).isEqualTo(nome);
            assertThat(resposta.get("telefone")).isEqualTo("(11) 98765-4321");
            assertThat(resposta.get("email")).isEqualTo("maria@teste.com");
            
            // Verificar que o método foi chamado
            verify(pacienteService).buscarPorNome(eq(nome));
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(false).withFailMessage("Não deveria lançar exceção: " + e.getMessage()).isTrue();
        }
    }
    
    @Test
    @DisplayName("Endpoint buscarPacientePorNome deve tratar paciente não encontrado")
    void deveTratarPacienteNaoEncontrado() {
        // Nome a ser buscado
        String nome = "NomeInexistente";
        
        // Mock do serviço retornando lista vazia
        when(pacienteService.buscarPorNome(eq(nome)))
                .thenReturn(Collections.emptyList());
        
        try {
            // Quando chamar o endpoint
            Map<String, Object> resposta = controller.buscarPacientePorNome(nome);
            
            // Então deve retornar não encontrado
            assertThat(resposta.get("encontrado")).isEqualTo(false); // Usamos isEqualTo em vez de isFalse
            assertThat(resposta.get("mensagem")).isEqualTo("Paciente não encontrado");
            
            // Verificar que o método foi chamado
            verify(pacienteService).buscarPorNome(eq(nome));
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(false).withFailMessage("Não deveria lançar exceção: " + e.getMessage()).isTrue();
        }
    }
    
    @Test
    @DisplayName("Deve atualizar telefone do paciente ao agendar")
    void deveAtualizarTelefonePaciente() {
        // Mock dos objetos necessários
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        
        // Configurar form
        AgendamentoForm form = new AgendamentoForm();
        form.setPaciente("Maria da Silva");
        form.setDentista("Dr. Teste");
        form.setDataHora(LocalDateTime.of(2025, 7, 7, 10, 0)); // Uma segunda-feira
        form.setTelefoneWhatsapp("(11) 98765-4321");
        form.setStatus("AGENDADO");
        form.setDuracaoMinutos(30);
        
        // Mock do paciente existente
        Paciente pacienteExistente = new Paciente("Maria da Silva", "maria@teste.com", "(11) 99999-8888");
        pacienteExistente.setId(1L); // Importante: definir um ID para o paciente
        
        when(pacienteService.buscarPorNome(eq("Maria da Silva")))
                .thenReturn(Collections.singletonList(pacienteExistente));
                
        when(pacienteService.atualizar(any(Paciente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Mock do repositório
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Executar método
        String resultado = controller.agendar(form, bindingResult, model, null);
        
        // Verificações
        verify(pacienteService).atualizar(any(Paciente.class));
        
        // Verificar que o telefone foi atualizado no objeto paciente
        assertThat(pacienteExistente.getTelefone()).isEqualTo("(11) 98765-4321");
        
        // Verificar resultado
        assertThat(resultado).isEqualTo("redirect:/");
    }
    
    @Test
    @DisplayName("Deve criar novo paciente quando não existir")
    void deveCriarNovoPaciente() {
        // Mock dos objetos necessários
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        
        // Configurar form com paciente que não existe
        AgendamentoForm form = new AgendamentoForm();
        form.setPaciente("Novo Paciente");
        form.setDentista("Dr. Teste");
        form.setDataHora(LocalDateTime.of(2025, 7, 7, 10, 0)); // Uma segunda-feira
        form.setTelefoneWhatsapp("(11) 98765-4321");
        form.setStatus("AGENDADO");
        form.setDuracaoMinutos(30);
        
        // Mock do serviço retornando lista vazia (paciente não existe)
        when(pacienteService.buscarPorNome(eq("Novo Paciente")))
                .thenReturn(Collections.emptyList());
                
        // Mock do serviço salvar
        when(pacienteService.salvar(any(Paciente.class)))
                .thenAnswer(invocation -> {
                    Paciente p = invocation.getArgument(0);
                    p.setId(1L); // Simulando que o banco de dados atribuiu um ID
                    return p;
                });
        
        // Mock do repositório
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Executar método
        String resultado = controller.agendar(form, bindingResult, model, null);
        
        // Verificações
        verify(pacienteService).salvar(any(Paciente.class));
        verify(pacienteService, times(0)).atualizar(any(Paciente.class)); // Não deve chamar atualizar
        
        // Verificar resultado
        assertThat(resultado).isEqualTo("redirect:/");
    }
    
    @Test
    @DisplayName("Deve retornar ao formulário com erro se a validação falhar")
    void deveRetornarFormularioComErroSeDadosInvalidos() {
        // Mock dos objetos necessários
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);
        AgendamentoForm form = new AgendamentoForm(); // Form com dados inválidos

        // Simular erro de validação
        when(bindingResult.hasErrors()).thenReturn(true);

        // Executar método
        String resultado = controller.agendar(form, bindingResult, model, null);

        // Verificações
        // Não deve salvar nem paciente nem agendamento
        verify(pacienteService, times(0)).salvar(any(Paciente.class));
        verify(agendamentoRepository, times(0)).save(any(Agendamento.class));
        
        // Deve adicionar a lista de dentistas de volta ao model
        verify(model).addAttribute(eq("dentistas"), any(List.class));

        // Deve retornar para a view do formulário
        assertThat(resultado).isEqualTo("novo-agendamento");
    }
    
    @Test
    @DisplayName("Deve carregar página de novo agendamento através do endpoint /novo")
    void deveCarregarPaginaNovoAgendamentoEndpointNovo() {
        // Mock do model
        Model model = mock(Model.class);
        
        // Executar método através do endpoint /novo
        String resultado = controller.novoAgendamentoViewAlias(model, null);
        
        // Verificações
        assertThat(resultado).isEqualTo("novo-agendamento");
        
        // Verificar que os atributos foram adicionados ao model
        verify(model).addAttribute(eq("agendamentoForm"), any(AgendamentoForm.class));
        verify(model).addAttribute(eq("dentistas"), any(List.class));
    }
    
    @Test
    @DisplayName("Deve carregar página de novo agendamento com dados do paciente através do endpoint /novo")
    void deveCarregarPaginaNovoAgendamentoComPacienteEndpointNovo() {
        // Mock do model
        Model model = mock(Model.class);
        String nomePaciente = "Maria da Silva";
        
        // Mock do paciente existente
        Paciente paciente = new Paciente(nomePaciente, "maria@teste.com", "(11) 98765-4321");
        when(pacienteService.buscarPorNome(eq(nomePaciente)))
                .thenReturn(Collections.singletonList(paciente));
        
        // Executar método através do endpoint /novo com parâmetro de paciente
        String resultado = controller.novoAgendamentoViewAlias(model, nomePaciente);
        
        // Verificações
        assertThat(resultado).isEqualTo("novo-agendamento");
        
        // Verificar que o serviço foi chamado
        verify(pacienteService).buscarPorNome(eq(nomePaciente));
        
        // Verificar que os atributos foram adicionados ao model
        verify(model).addAttribute(eq("agendamentoForm"), any(AgendamentoForm.class));
        verify(model).addAttribute(eq("dentistas"), any(List.class));
    }
}
