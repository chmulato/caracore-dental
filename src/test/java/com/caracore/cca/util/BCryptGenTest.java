package com.caracore.cca.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class BCryptGenTest {

    @Test
    @DisplayName("Deve gerar hash BCrypt válido")
    public void deveGerarHashBCryptValido() {
        // Arrange
        String[] args = {"senhaSegura123"};
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Act
        try {
            BCryptGen.main(args);
            String output = outContent.toString();
            
            // Assert
            assertTrue(output.contains("Hash BCrypt para 'senhaSegura123'"), 
                    "A saída deve mostrar a senha fornecida");
            
            // Extrair o hash da saída para validá-lo
            String[] lines = output.split("\n");
            if (lines.length >= 1) {
                String line = lines[0];
                int index = line.indexOf("Hash BCrypt para 'senhaSegura123': ") + 
                            "Hash BCrypt para 'senhaSegura123': ".length();
                if (index >= 0 && index < line.length()) {
                    String hash = line.substring(index).trim();
                    assertTrue(hash.startsWith("$2a$"), "O hash gerado deve começar com o prefixo BCrypt");
                    assertTrue(encoder.matches("senhaSegura123", hash), 
                            "O hash gerado deve validar com a senha original");
                }
            }
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    @DisplayName("Deve usar senha padrão quando não fornecida")
    public void deveUsarSenhaPadraoQuandoNaoFornecida() {
        // Arrange
        String[] args = {};
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Act
        try {
            BCryptGen.main(args);
            String output = outContent.toString();
            
            // Assert
            assertTrue(output.contains("Hash BCrypt para 'admin123'"), 
                    "Deve usar a senha padrão 'admin123'");
        } finally {
            System.setOut(originalOut);
        }
    }
}
