package com.caracore.cca.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class VerificarHash {
    public static void main(String[] args) {
        String senhaPlana = args.length > 0 ? args[0] : "admin123";
        String hashArmazenado = args.length > 1 ? args[1] : "$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy";
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean corresponde = encoder.matches(senhaPlana, hashArmazenado);
        
        System.out.println("Senha: " + senhaPlana);
        System.out.println("Hash: " + hashArmazenado);
        System.out.println("Corresponde: " + corresponde);
    }
}
