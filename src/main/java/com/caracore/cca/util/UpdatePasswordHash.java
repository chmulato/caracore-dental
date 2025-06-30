package com.caracore.cca.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utilitário para atualizar diretamente o hash BCrypt no banco de dados
 */
public class UpdatePasswordHash {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/cca_db";
        String username = "cca_user";
        String password = "cca_password";
        String sql = "UPDATE usuario SET senha = ? WHERE email = ?";
        String bcryptHash = "$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy";
        String email = "suporte@caracore.com.br";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bcryptHash);
            stmt.setString(2, email);
            
            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated + " registros atualizados com sucesso.");
            
            System.out.println("Hash BCrypt para usuário " + email + " atualizado para: " + bcryptHash);
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar a senha: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
