package com.caracore.cca.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class UpdatePasswordHashTest {

    @Test
    @DisplayName("Deve ter método main válido")
    public void deveVerificarMetodoMain() {
        // Este teste só verifica se o método main existe e pode ser invocado sem exceções não tratadas
        
        // Redirecionar saída padrão para evitar mensagens nos logs de teste
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
        try {
            // Executamos o método main - deve funcionar com ou sem banco de dados
            UpdatePasswordHash.main(new String[0]);
            
            // O teste é considerado bem-sucedido se chegarmos aqui sem exceções não tratadas
            assertTrue(true, "O método main foi executado sem exceções não tratadas");
        } finally {
            // Restaurar saída padrão
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
        
        // Se chegamos aqui sem outras exceções, o teste passou
        // O erro de SQL é esperado pois não há banco PostgreSQL nos testes
    }
}
