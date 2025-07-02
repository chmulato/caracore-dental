package com.caracore.cca.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PacienteTest {

    @Test
    @DisplayName("Deve criar paciente com todos os campos")
    void deveCriarPacienteCompleto() {
        // Quando
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        
        // Então
        assertThat(paciente.getNome()).isEqualTo("João Silva");
        assertThat(paciente.getEmail()).isEqualTo("joao@teste.com");
        assertThat(paciente.getTelefone()).isEqualTo("(11) 98765-4321");
    }
    
    @Test
    @DisplayName("Deve criar paciente com construtor vazio")
    void deveCriarPacienteComConstrutorVazio() {
        // Quando
        Paciente paciente = new Paciente();
        paciente.setNome("Maria Oliveira");
        paciente.setEmail("maria@teste.com");
        paciente.setTelefone("(11) 97654-3210");
        paciente.setId(1L);
        
        // Então
        assertThat(paciente.getNome()).isEqualTo("Maria Oliveira");
        assertThat(paciente.getEmail()).isEqualTo("maria@teste.com");
        assertThat(paciente.getTelefone()).isEqualTo("(11) 97654-3210");
        assertThat(paciente.getId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Deve permitir atualizar telefone WhatsApp")
    void deveAtualizarTelefoneWhatsApp() {
        // Dado
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        
        // Quando
        paciente.setTelefone("(11) 91234-5678");
        
        // Então
        assertThat(paciente.getTelefone()).isEqualTo("(11) 91234-5678");
    }
    
    @ParameterizedTest
    @DisplayName("Deve aceitar diferentes formatos de telefone WhatsApp")
    @ValueSource(strings = {
        "(11) 98765-4321", 
        "11987654321",
        "(11) 987654321",
        "11 9 8765-4321",
        "+55 11 98765-4321"
    })
    void deveAceitarDiferentesFormatosTelefone(String telefone) {
        // Dado
        Paciente paciente = new Paciente();
        
        // Quando
        paciente.setTelefone(telefone);
        
        // Então
        assertThat(paciente.getTelefone()).isEqualTo(telefone);
    }
    
    @Test
    @DisplayName("Deve permitir telefone nulo")
    void devePermitirTelefoneNulo() {
        // Dado
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", null);
        
        // Então
        assertThat(paciente.getTelefone()).isNull();
        
        // Quando atualizar para valor não nulo
        paciente.setTelefone("(11) 98765-4321");
        
        // Então
        assertThat(paciente.getTelefone()).isEqualTo("(11) 98765-4321");
    }
    
    @Test
    @DisplayName("Deve comparar pacientes pelo ID")
    void deveCompararPacientesPeloId() {
        // Dado
        Paciente paciente1 = new Paciente("João", "joao@teste.com", "(11) 98765-4321");
        paciente1.setId(1L);
        
        Paciente paciente2 = new Paciente("João", "joao@teste.com", "(11) 98765-4321");
        paciente2.setId(1L);
        
        Paciente paciente3 = new Paciente("Maria", "maria@teste.com", "(11) 97654-3210");
        paciente3.setId(2L);
        
        // Então
        assertThat(paciente1).isEqualTo(paciente2);
        assertThat(paciente1).isNotEqualTo(paciente3);
        assertThat(paciente1).isNotEqualTo(null);
        assertThat(paciente1).isNotEqualTo("String");
    }
    
    @Test
    @DisplayName("Deve gerar hash code baseado no ID")
    void deveGerarHashCodeBaseadoNoId() {
        // Dado
        Paciente paciente1 = new Paciente("João", "joao@teste.com", "(11) 98765-4321");
        paciente1.setId(1L);
        
        Paciente paciente2 = new Paciente("Diferente", "outro@teste.com", "(22) 98765-4321");
        paciente2.setId(1L);
        
        // Então
        assertThat(paciente1.hashCode()).isEqualTo(paciente2.hashCode());
    }
    
    @Test
    @DisplayName("Deve gerar representação String correta")
    void deveGerarToStringCorretamente() {
        // Dado
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        paciente.setId(1L);
        
        // Quando
        String resultado = paciente.toString();
        
        // Então
        assertThat(resultado).contains("João Silva");
        assertThat(resultado).contains("joao@teste.com");
        assertThat(resultado).contains("(11) 98765-4321");
        assertThat(resultado).contains("id=1");
    }
    
    @Test
    @DisplayName("Deve identificar pacientes com mesmo ID e telefones diferentes como iguais")
    void deveIdentificarPacientesMesmoIdTelefoneDiferenteComoIguais() {
        // Dado
        Paciente paciente1 = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        paciente1.setId(1L);
        
        Paciente paciente2 = new Paciente("João Silva", "joao@teste.com", "(11) 99999-9999");
        paciente2.setId(1L);
        
        // Então
        assertThat(paciente1).isEqualTo(paciente2);
        assertThat(paciente1.getTelefone()).isNotEqualTo(paciente2.getTelefone());
    }
}
