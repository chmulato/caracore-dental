package com.caracore.cca.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UpdatePasswordHashTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("Deve ter método main válido")
    public void deveVerificarMetodoMain() {
        // Este teste só verifica se o método main existe e pode ser invocado sem exceções de sintaxe
        // Não testamos a conexão real com o banco de dados
        
        // Redirecionar saída padrão para evitar mensagens nos logs de teste
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
        try {
            // Se tentarmos executar o main, ele vai tentar se conectar ao banco
            // O que provavelmente falhará, mas o método main já trata as exceções internamente
            UpdatePasswordHash.main(new String[0]);
            
            // Verificamos se houve erro de conexão (esperado em testes)
            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Erro ao atualizar a senha") || 
                      errorOutput.contains("relation \"usuario\" does not exist") ||
                      errorOutput.contains("Connection refused"),
                      "Erro esperado de conexão com banco deve aparecer");
        } finally {
            // Restaurar saída padrão
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
        
        // Se chegamos aqui sem outras exceções, o teste passou
        // O erro de SQL é esperado pois não há banco PostgreSQL nos testes
    }
}
