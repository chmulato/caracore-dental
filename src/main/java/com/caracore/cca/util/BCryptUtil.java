package com.caracore.cca.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senha = "admin123";
        String hash = encoder.encode(senha);
        System.out.println("Hash BCrypt para '" + senha + "': " + hash);
        System.out.println("Hash para inserção SQL: '" + hash + "'");
    }
}
