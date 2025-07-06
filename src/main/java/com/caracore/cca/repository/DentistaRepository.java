package com.caracore.cca.repository;

import com.caracore.cca.model.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {

    List<Dentista> findByAtivoTrue();
    
    List<Dentista> findByAtivoTrueAndExpostoPublicamenteTrue();
    
    List<Dentista> findByNomeContainingIgnoreCase(String nome);
    
    List<Dentista> findByAtivo(boolean ativo);

    @Query("SELECT d FROM Dentista d WHERE " +
           "LOWER(d.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(d.especialidade) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(d.email) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Dentista> buscarPorTermo(@Param("termo") String termo);
}
