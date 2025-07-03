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
    
    @Test
    @DisplayName("Deve criar paciente com campos LGPD padrão")
    void deveCriarPacienteComCamposLgpdPadrao() {
        // Quando
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        
        // Então
        assertThat(paciente.getConsentimentoLgpd()).isEqualTo(false);
        assertThat(paciente.getConsentimentoConfirmado()).isEqualTo(false);
        assertThat(paciente.getDataConsentimento()).isNull();
    }
    
    @Test
    @DisplayName("Deve permitir marcar consentimento LGPD como enviado")
    void deveMarcarConsentimentoLgpdEnviado() {
        // Dado
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        
        // Quando
        paciente.setConsentimentoLgpd(true);
        paciente.setDataConsentimento(agora);
        
        // Então
        assertThat(paciente.getConsentimentoLgpd()).isTrue();
        assertThat(paciente.getDataConsentimento()).isEqualTo(agora);
        assertThat(paciente.getConsentimentoConfirmado()).isFalse(); // Ainda não confirmado
    }
    
    @Test
    @DisplayName("Deve permitir confirmar recebimento do consentimento LGPD")
    void deveConfirmarRecebimentoConsentimentoLgpd() {
        // Dado
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        
        // Quando
        paciente.setConsentimentoLgpd(true);
        paciente.setDataConsentimento(agora);
        paciente.setConsentimentoConfirmado(true);
        
        // Então
        assertThat(paciente.getConsentimentoLgpd()).isTrue();
        assertThat(paciente.getConsentimentoConfirmado()).isTrue();
        assertThat(paciente.getDataConsentimento()).isEqualTo(agora);
    }
    
    @Test
    @DisplayName("Deve permitir alterar status de consentimento LGPD")
    void deveAlterarStatusConsentimentoLgpd() {
        // Dado
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        
        // Quando - marcar como enviado
        paciente.setConsentimentoLgpd(true);
        assertThat(paciente.getConsentimentoLgpd()).isTrue();
        
        // Quando - desmarcar (se necessário)
        paciente.setConsentimentoLgpd(false);
        assertThat(paciente.getConsentimentoLgpd()).isFalse();
        
        // Quando - confirmar recebimento
        paciente.setConsentimentoConfirmado(true);
        assertThat(paciente.getConsentimentoConfirmado()).isTrue();
        
        // Quando - reverter confirmação
        paciente.setConsentimentoConfirmado(false);
        assertThat(paciente.getConsentimentoConfirmado()).isFalse();
    }
    
    @Test
    @DisplayName("Deve aceitar data de consentimento nula inicialmente")
    void deveAceitarDataConsentimentoNula() {
        // Dado
        Paciente paciente = new Paciente("João Silva", "joao@teste.com", "(11) 98765-4321");
        
        // Quando não define data
        paciente.setConsentimentoLgpd(true);
        
        // Então
        assertThat(paciente.getDataConsentimento()).isNull();
        assertThat(paciente.getConsentimentoLgpd()).isTrue();
    }
    
    @Test
    @DisplayName("Deve criar paciente com nome social conforme Portaria 2.836/2011")
    void deveCriarPacienteComNomeSocial() {
        // Dado
        Paciente paciente = new Paciente("João Silva Santos", "joao@teste.com", "(11) 98765-4321");
        
        // Quando
        paciente.setNomeSocial("João Silva");
        
        // Então
        assertThat(paciente.getNome()).isEqualTo("João Silva Santos");
        assertThat(paciente.getNomeSocial()).isEqualTo("João Silva");
    }
    
    @Test
    @DisplayName("Deve permitir nome social nulo")
    void devePermitirNomeSocialNulo() {
        // Dado
        Paciente paciente = new Paciente("Maria Oliveira", "maria@teste.com", "(11) 98765-4321");
        
        // Quando
        paciente.setNomeSocial(null);
        
        // Então
        assertThat(paciente.getNomeSocial()).isNull();
        assertThat(paciente.getNome()).isEqualTo("Maria Oliveira");
    }
    
    @Test
    @DisplayName("Deve permitir alterar nome social")
    void deveAlterarNomeSocial() {
        // Dado
        Paciente paciente = new Paciente("Ana Paula Silva", "ana@teste.com", "(11) 98765-4321");
        
        // Quando - definir nome social
        paciente.setNomeSocial("Ana Silva");
        assertThat(paciente.getNomeSocial()).isEqualTo("Ana Silva");
        
        // Quando - alterar nome social
        paciente.setNomeSocial("Ana Paula");
        assertThat(paciente.getNomeSocial()).isEqualTo("Ana Paula");
        
        // Quando - remover nome social
        paciente.setNomeSocial(null);
        assertThat(paciente.getNomeSocial()).isNull();
    }
    
    @ParameterizedTest
    @DisplayName("Deve aceitar diferentes opções de gênero conforme autodeclaração")
    @ValueSource(strings = {
        "FEMININO", 
        "MASCULINO",
        "NAO_BINARIO",
        "OUTRO",
        "PREFERE_NAO_INFORMAR"
    })
    void deveAceitarDiferentesOpcoesGenero(String genero) {
        // Dado
        Paciente paciente = new Paciente("Paciente Teste", "teste@teste.com", "(11) 98765-4321");
        
        // Quando
        paciente.setGenero(genero);
        
        // Então
        assertThat(paciente.getGenero()).isEqualTo(genero);
    }
    
    @Test
    @DisplayName("Deve permitir gênero nulo")
    void devePermitirGeneroNulo() {
        // Dado
        Paciente paciente = new Paciente("Paciente Teste", "teste@teste.com", "(11) 98765-4321");
        
        // Quando
        paciente.setGenero(null);
        
        // Então
        assertThat(paciente.getGenero()).isNull();
    }
    
    @Test
    @DisplayName("Deve permitir alterar gênero autodeclarado")
    void deveAlterarGeneroAutodeclarado() {
        // Dado
        Paciente paciente = new Paciente("Paciente Teste", "teste@teste.com", "(11) 98765-4321");
        
        // Quando - definir gênero
        paciente.setGenero("FEMININO");
        assertThat(paciente.getGenero()).isEqualTo("FEMININO");
        
        // Quando - alterar gênero
        paciente.setGenero("NAO_BINARIO");
        assertThat(paciente.getGenero()).isEqualTo("NAO_BINARIO");
        
        // Quando - preferir não informar
        paciente.setGenero("PREFERE_NAO_INFORMAR");
        assertThat(paciente.getGenero()).isEqualTo("PREFERE_NAO_INFORMAR");
    }
    
    @Test
    @DisplayName("Deve manter compatibilidade com campos existentes quando adicionar nome social e gênero")
    void deveManterCompatibilidadeComCamposExistentes() {
        // Dado
        Paciente paciente = new Paciente("Roberto Silva Santos", "roberto@teste.com", "(11) 98765-4321");
        
        // Quando
        paciente.setNomeSocial("Roberto Santos");
        paciente.setGenero("MASCULINO");
        paciente.setConsentimentoLgpd(true);
        paciente.setDataConsentimento(java.time.LocalDateTime.now());
        
        // Então - todos os campos devem coexistir
        assertThat(paciente.getNome()).isEqualTo("Roberto Silva Santos");
        assertThat(paciente.getNomeSocial()).isEqualTo("Roberto Santos");
        assertThat(paciente.getGenero()).isEqualTo("MASCULINO");
        assertThat(paciente.getEmail()).isEqualTo("roberto@teste.com");
        assertThat(paciente.getTelefone()).isEqualTo("(11) 98765-4321");
        assertThat(paciente.getConsentimentoLgpd()).isTrue();
        assertThat(paciente.getDataConsentimento()).isNotNull();
    }
    
    @Test
    @DisplayName("Deve criar paciente completo conforme Portaria 2.836/2011 e LGPD")
    void deveCriarPacienteCompletoConformePortariaELgpd() {
        // Dado
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        
        // Quando
        Paciente paciente = new Paciente("Maria José da Silva", "maria@teste.com", "(11) 98765-4321");
        paciente.setNomeSocial("Maria Silva");
        paciente.setGenero("FEMININO");
        paciente.setConsentimentoLgpd(true);
        paciente.setConsentimentoConfirmado(true);
        paciente.setDataConsentimento(agora);
        
        // Então
        assertThat(paciente.getNome()).isEqualTo("Maria José da Silva");
        assertThat(paciente.getNomeSocial()).isEqualTo("Maria Silva");
        assertThat(paciente.getGenero()).isEqualTo("FEMININO");
        assertThat(paciente.getEmail()).isEqualTo("maria@teste.com");
        assertThat(paciente.getTelefone()).isEqualTo("(11) 98765-4321");
        assertThat(paciente.getConsentimentoLgpd()).isTrue();
        assertThat(paciente.getConsentimentoConfirmado()).isTrue();
        assertThat(paciente.getDataConsentimento()).isEqualTo(agora);
    }
}
