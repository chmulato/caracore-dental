package com.caracore.cca.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class VerificarHashTest {

    private final String validHash = "$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy";
    private final String senha = "admin123";

    @Test
    @DisplayName("Deve verificar hash BCrypt com senha correspondente")
    public void deveVerificarHashComSenhaCorrespondente() {
        // Arrange
        String[] args = {senha, validHash};
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Act
        try {
            VerificarHash.main(args);
            String output = outContent.toString();
            
            // Assert
            assertTrue(output.contains("Corresponde: true"), 
                    "A saída deve indicar que a senha corresponde ao hash");
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    @DisplayName("Deve verificar hash BCrypt com senha incorreta")
    public void deveVerificarHashComSenhaIncorreta() {
        // Arrange
        String[] args = {"senhaErrada", validHash};
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Act
        try {
            VerificarHash.main(args);
            String output = outContent.toString();
            
            // Assert
            assertTrue(output.contains("Corresponde: false"), 
                    "A saída deve indicar que a senha não corresponde ao hash");
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    @DisplayName("Deve usar valores padrão quando argumentos não são fornecidos")
    public void deveUsarValoresPadraoQuandoArgumentosNaoFornecidos() {
        // Arrange
        String[] args = {};
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Act
        try {
            VerificarHash.main(args);
            String output = outContent.toString();
            
            // Assert
            assertTrue(output.contains("Senha: admin123"), 
                    "Deve usar senha padrão 'admin123'");
            assertTrue(output.contains("Corresponde: true"), 
                    "A saída deve indicar que a senha padrão corresponde ao hash padrão");
        } finally {
            System.setOut(originalOut);
        }
    }
}
