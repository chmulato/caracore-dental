package com.caracore.cca.repository;

import com.caracore.cca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.senha = :novaSenha WHERE u.email = :email")
    int atualizarSenha(String email, String novaSenha);
    
    /**
     * Busca usuários pelo email ou nome contendo o termo especificado (case insensitive)
     */
    List<Usuario> findByEmailContainingIgnoreCaseOrNomeContainingIgnoreCase(String email, String nome);
}
