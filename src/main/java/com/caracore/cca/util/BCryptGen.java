package com.caracore.cca.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitário para geração rápida de hashes BCrypt para senhas.
 * Use este utilitário para gerar senhas para novos usuários
 * ou para resetar senhas existentes.
 */
public class BCryptGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senha = args.length > 0 ? args[0] : "admin123";
        String hash = encoder.encode(senha);
        System.out.println("Hash BCrypt para '" + senha + "': " + hash);
        System.out.println("Hash para inserção SQL: '" + hash + "'");
    }
}
