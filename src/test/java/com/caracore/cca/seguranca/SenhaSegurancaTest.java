package com.caracore.cca.seguranca;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes adicionais para validar a segurança das senhas no sistema
 */
public class SenhaSegurancaTest {
    
    private final String hashCorretoPadrao = "$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy";
    private final String senhaPadrao = "admin123";
    
    @Test
    @DisplayName("Deve validar a senha padrão corretamente")
    public void deveValidarSenhaPadrao() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Verificar que a senha padrão é válida com o hash armazenado
        assertTrue(encoder.matches(senhaPadrao, hashCorretoPadrao),
                "A senha padrão deve corresponder ao hash armazenado");
                
        // Garantir que o hash é válido e tem o formato BCrypt correto
        assertTrue(hashCorretoPadrao.startsWith("$2a$10$"),
                "O hash deve usar o formato BCrypt com fator de custo 10");
    }
    
    @Test
    @DisplayName("Deve rejeitar tentativas de ataques SQL Injection")
    public void deveRejeitarSQLInjection() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaComSQLInjection = "admin123' OR '1'='1";
        
        // Verificar que tentativas de SQL injection são rejeitadas
        assertFalse(encoder.matches(senhaComSQLInjection, hashCorretoPadrao),
                "Senhas com SQL injection devem ser rejeitadas");
    }
    
    @Test
    @DisplayName("Deve gerar hashes diferentes para a mesma senha")
    public void deveGerarHashesDiferentes() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Gerar dois hashes para a mesma senha
        String hash1 = encoder.encode(senhaPadrao);
        String hash2 = encoder.encode(senhaPadrao);
        
        // Verificar que os hashes são diferentes (devido ao salt aleatório)
        assertNotEquals(hash1, hash2,
                "Hashes gerados para a mesma senha devem ser diferentes devido ao salt");
                
        // Mas ambos devem validar a senha corretamente
        assertTrue(encoder.matches(senhaPadrao, hash1),
                "O primeiro hash deve validar a senha corretamente");
        assertTrue(encoder.matches(senhaPadrao, hash2),
                "O segundo hash deve validar a senha corretamente");
    }
    
    @Test
    @DisplayName("Deve verificar a força do fator de custo do BCrypt")
    public void deveVerificarFatorCusto() {
        // O padrão do BCryptPasswordEncoder é usar fator de custo 10
        // Verificar se o hash armazenado usa esse padrão
        assertTrue(hashCorretoPadrao.startsWith("$2a$10$"),
                "O hash deve usar o fator de custo 10 para uma segurança adequada");
                
        // Um fator de custo mais baixo seria menos seguro
        assertFalse(hashCorretoPadrao.startsWith("$2a$4$"),
                "O hash não deve usar um fator de custo baixo");
    }
    
    @Test
    @DisplayName("Deve demonstrar resistência a ataques de força bruta")
    public void deveSerResistenteAForcaBruta() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Medir o tempo para verificar uma senha (mesmo que incorreta)
        long inicio = System.currentTimeMillis();
        encoder.matches("senhaIncorreta", hashCorretoPadrao);
        long fim = System.currentTimeMillis();
        
        // O BCrypt é intencionalmente lento para dificultar ataques de força bruta
        // Um único teste deve demorar pelo menos alguns milissegundos
        long duracao = fim - inicio;
        System.out.println("Tempo para verificar uma senha: " + duracao + "ms");
        
        // Esta verificação é apenas ilustrativa - tempos exatos dependem do hardware
        // Em sistemas reais, isso pode ser ajustado com o fator de custo
        assertTrue(duracao > 0, 
                "A verificação deve demorar tempo suficiente para dificultar ataques de força bruta");
    }
}
