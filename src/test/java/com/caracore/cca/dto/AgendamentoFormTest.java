package com.caracore.cca.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.caracore.cca.model.Agendamento;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgendamentoFormTest {

    private Validator validator;
    private AgendamentoForm form;
    
    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        form = new AgendamentoForm();
        
        // Configuração de valores válidos padrão para todos os testes
        // Usa horário comercial válido (10h da manhã do próximo dia)
        form.setPaciente("Maria Teste");
        form.setDentista("Dr. Dentista");
        form.setDataHora(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
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
        // Dados válidos com horário comercial
        form.setPaciente("Maria Teste");
        form.setDentista("Dr. Dentista");
        form.setDataHora(LocalDateTime.now().plusDays(1).withHour(14).withMinute(30));
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

    @Test
    @DisplayName("Deve validar horário comercial")
    void deveValidarHorarioComercial() {
        // Testa horário antes do expediente (7h)
        form.setDataHora(LocalDateTime.now().plusDays(1).withHour(7).withMinute(0));
        Set<ConstraintViolation<AgendamentoForm>> violations = validator.validate(form);
        assertThat(violations).isNotEmpty();

        // Testa horário após o expediente (19h)
        form.setDataHora(LocalDateTime.now().plusDays(1).withHour(19).withMinute(0));
        violations = validator.validate(form);
        assertThat(violations).isNotEmpty();

        // Testa horário válido (14h)
        form.setDataHora(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0));
        violations = validator.validate(form);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve validar tamanho máximo da observação")
    void deveValidarTamanhoMaximoObservacao() {
        // Usa horário comercial válido para o teste
        form.setDataHora(LocalDateTime.now().plusDays(1).withHour(15).withMinute(0));
        
        // Cria uma observação com 501 caracteres (excede o limite de 500)
        StringBuilder obsLonga = new StringBuilder();
        for (int i = 0; i < 501; i++) {
            obsLonga.append("a");
        }
        form.setObservacao(obsLonga.toString());
        
        Set<ConstraintViolation<AgendamentoForm>> violations = validator.validate(form);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("observacao"))
                .map(ConstraintViolation::getMessage)
                .anyMatch(msg -> msg.contains("não pode ter mais de 500 caracteres")))
                .isTrue();

        // Testa com observação no limite (500 caracteres)
        form.setObservacao(obsLonga.substring(0, 500));
        violations = validator.validate(form);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve validar data futura")
    void deveValidarDataFutura() {
        // Testa data no passado (mesmo mantendo horário comercial)
        form.setDataHora(LocalDateTime.now().minusDays(1).withHour(10).withMinute(0));
        Set<ConstraintViolation<AgendamentoForm>> violations = validator.validate(form);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("dataHora"))
                .map(ConstraintViolation::getMessage)
                .anyMatch(msg -> msg.contains("devem ser no futuro")))
                .isTrue();

        // Testa data atual (mas fora do horário comercial para garantir falha)
        form.setDataHora(LocalDateTime.now().withHour(7).withMinute(0));
        violations = validator.validate(form);
        assertThat(violations).isNotEmpty();

        // Testa data futura com horário comercial válido
        form.setDataHora(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0));
        violations = validator.validate(form);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve validar valores padrão")
    void deveValidarValoresPadrao() {
        AgendamentoForm novoForm = new AgendamentoForm();
        
        // Verifica duração padrão (30 minutos)
        assertThat(novoForm.getDuracaoMinutos()).isEqualTo(30);
        
        // Verifica status padrão (AGENDADO)
        assertThat(novoForm.getStatus()).isEqualTo("AGENDADO");
        
        // Verifica que outros campos começam nulos
        assertThat(novoForm.getPaciente()).isNull();
        assertThat(novoForm.getDentista()).isNull();
        assertThat(novoForm.getDataHora()).isNull();
        assertThat(novoForm.getObservacao()).isNull();
    }


}
