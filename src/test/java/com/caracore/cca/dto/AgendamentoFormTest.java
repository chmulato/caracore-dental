package com.caracore.cca.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AgendamentoFormTest {

    private Validator validator;
    private AgendamentoForm form;
    
    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        form = new AgendamentoForm();
        
        // Configuração de valores válidos padrão para todos os testes
        form.setPaciente("Maria Teste");
        form.setDentista("Dr. Dentista");
        form.setDataHora(LocalDateTime.now().plusDays(1));
        form.setTelefoneWhatsapp("(11) 98765-4321");
    }

    @Test
    @DisplayName("Deve criar e acessar os campos do AgendamentoForm corretamente")
    void deveCriarEManipularAgendamentoForm() {
        // Quando acesso os getters
        String paciente = form.getPaciente();
        String dentista = form.getDentista();
        LocalDateTime dataHora = form.getDataHora();
        String telefoneWhatsapp = form.getTelefoneWhatsapp();
        
        // Então os valores devem corresponder aos setters
        assertThat(paciente).isEqualTo("Maria Teste");
        assertThat(dentista).isEqualTo("Dr. Dentista");
        assertThat(dataHora).isAfter(LocalDateTime.now());
        assertThat(telefoneWhatsapp).isEqualTo("(11) 98765-4321");
    }
    
    @Test
    @DisplayName("Deve validar todos os campos do formulário")
    void deveValidarTodosCampos() {
        // Dados válidos
        form.setPaciente("Maria Teste");
        form.setDentista("Dr. Dentista");
        form.setDataHora(LocalDateTime.now().plusDays(1));
        form.setTelefoneWhatsapp("(11) 98765-4321");
        form.setObservacao("Observação de teste");
        form.setDuracaoMinutos(30);
        form.setStatus("AGENDADO");
        
        // Validação
        Set<ConstraintViolation<AgendamentoForm>> violations = validator.validate(form);
        
        // Não deve haver violações
        assertThat(violations).isEmpty();
    }
    
    @ParameterizedTest
    @DisplayName("Deve aceitar formatos válidos de telefone WhatsApp")
    @ValueSource(strings = {
        "(11) 98765-4321", 
        "(21) 99876-5432", 
        "(31) 92345-6789", 
        "(41) 91234-5678",
        "(11) 987654321"
    })
    void deveAceitarFormatosValidosTelefone(String telefoneValido) {
        // Dado um formulário com telefone válido
        form.setTelefoneWhatsapp(telefoneValido);
        
        // Quando valido o formulário
        Set<ConstraintViolation<AgendamentoForm>> violations = validator.validate(form);
        
        // Então não deve haver violações
        assertThat(violations).isEmpty();
    }
    
    @ParameterizedTest
    @DisplayName("Deve rejeitar formatos inválidos de telefone")
    @ValueSource(strings = {"123456789", "(00) 12345-678", "texto", "(99) ABCD-EFGH"})
    void deveRejeitarFormatosInvalidosTelefone(String telefoneInvalido) {
        // Dado um formulário com telefone inválido
        form.setTelefoneWhatsapp(telefoneInvalido);
        
        // Quando valido o formulário
        Set<ConstraintViolation<AgendamentoForm>> violations = validator.validate(form);
        
        // Então deve haver violação de formato
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefoneWhatsapp")))
                .isTrue();
    }
    
    @Test
    @DisplayName("Deve rejeitar telefone WhatsApp nulo")
    void deveRejeitarTelefoneNulo() {
        // Dado um formulário com telefone nulo
        form.setTelefoneWhatsapp(null);
        
        // Quando valido o formulário
        Set<ConstraintViolation<AgendamentoForm>> violations = validator.validate(form);
        
        // Então deve haver violação
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefoneWhatsapp")))
                .isTrue();
    }
    
    @Test
    @DisplayName("Deve rejeitar telefone WhatsApp vazio")
    void deveRejeitarTelefoneVazio() {
        // Dado um formulário com telefone vazio
        form.setTelefoneWhatsapp("");
        
        // Quando valido o formulário
        Set<ConstraintViolation<AgendamentoForm>> violations = validator.validate(form);
        
        // Então deve haver violação
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefoneWhatsapp")))
                .isTrue();
    }
    
    @Test
    @DisplayName("Deve validar mensagem de erro específica para telefone inválido")
    void deveValidarMensagemErroTelefoneInvalido() {
        // Dado um formulário com telefone inválido
        form.setTelefoneWhatsapp("123");
        
        // Quando valido o formulário
        Set<ConstraintViolation<AgendamentoForm>> violations = validator.validate(form);
        
        // Então deve haver violação com mensagem específica
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("telefoneWhatsapp"))
                .map(ConstraintViolation::getMessage)
                .anyMatch(msg -> msg.contains("Telefone inválido")))
                .isTrue();
    }
}
