package com.caracore.cca.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

public class BCryptUtilTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String senha = "admin123";
    private final String hashConhecido = "$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy";
    
    @Test
    public void deveGerarHashDiferenteParaMesmaSenha() {
        // Quando geramos dois hashes para a mesma senha
        String hash1 = encoder.encode(senha);
        String hash2 = encoder.encode(senha);
        
        // Então os hashes devem ser diferentes (devido ao salt aleatório)
        assertNotEquals(hash1, hash2, "Dois hashes da mesma senha devem ser diferentes devido ao salt");
        
        // Mas ambos devem validar corretamente com a senha original
        assertTrue(encoder.matches(senha, hash1), "O primeiro hash deve validar com a senha");
        assertTrue(encoder.matches(senha, hash2), "O segundo hash deve validar com a senha");
    }
    
    @Test
    public void deveValidarSenhaContraHashConhecido() {
        // Quando verificamos uma senha contra um hash conhecido
        boolean corresponde = encoder.matches(senha, hashConhecido);
        
        // Então a validação deve ser bem-sucedida
        assertTrue(corresponde, "A senha deve corresponder ao hash conhecido");
    }
    
    @Test
    public void deveFalharAoValidarSenhaIncorreta() {
        // Quando verificamos uma senha incorreta contra um hash
        boolean corresponde = encoder.matches("senhaErrada", hashConhecido);
        
        // Então a validação deve falhar
        assertFalse(corresponde, "A senha incorreta não deve corresponder ao hash");
    }
    
    @Test
    public void deveVerificarTamanhoDaStringHash() {
        // Quando geramos um hash
        String hash = encoder.encode(senha);
        
        // Então o hash deve ter um tamanho adequado (tipicamente 60 caracteres)
        assertTrue(hash.length() >= 59 && hash.length() <= 61, 
            "O hash BCrypt deve ter aproximadamente 60 caracteres");
        
        // E deve começar com o prefixo padrão $2a$
        assertTrue(hash.startsWith("$2a$"), "O hash deve começar com o prefixo $2a$");
    }
}
