package com.caracore.cca.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitário para gerar hashes BCrypt para novas senhas
 */
public class BCryptUtil {
    public static void main(String[] args) {
        String senha = args.length > 0 ? args[0] : "admin123";
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(senha);
        
        System.out.println("Senha: " + senha);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println("Hash para inserção SQL: '" + hash + "'");
    }
}
