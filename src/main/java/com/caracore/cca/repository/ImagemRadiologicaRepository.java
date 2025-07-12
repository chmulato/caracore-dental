package com.caracore.cca.repository;

import com.caracore.cca.model.ImagemRadiologica;
import com.caracore.cca.model.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações com ImagemRadiologica.
 */
@Repository
public interface ImagemRadiologicaRepository extends JpaRepository<ImagemRadiologica, Long> {

    /**
     * Busca todas as imagens ativas de um prontuário
     */
    List<ImagemRadiologica> findByProntuarioAndAtivoTrueOrderByDataUploadDesc(Prontuario prontuario);

    /**
     * Busca imagens por ID do prontuário
     */
    @Query("SELECT i FROM ImagemRadiologica i WHERE i.prontuario.id = :prontuarioId AND i.ativo = true ORDER BY i.dataUpload DESC")
    List<ImagemRadiologica> findByProntuarioIdAndAtivoTrue(@Param("prontuarioId") Long prontuarioId);

    /**
     * Busca imagens por tipo
     */
    @Query("SELECT i FROM ImagemRadiologica i WHERE i.prontuario.id = :prontuarioId AND i.tipoImagem = :tipoImagem AND i.ativo = true ORDER BY i.dataUpload DESC")
    List<ImagemRadiologica> findByProntuarioIdAndTipoImagemAndAtivoTrue(@Param("prontuarioId") Long prontuarioId, @Param("tipoImagem") String tipoImagem);

    /**
     * Busca imagens por dentista
     */
    @Query("SELECT i FROM ImagemRadiologica i WHERE i.dentista.id = :dentistaId AND i.ativo = true ORDER BY i.dataUpload DESC")
    List<ImagemRadiologica> findByDentistaIdAndAtivoTrue(@Param("dentistaId") Long dentistaId);

    /**
     * Busca imagens por período
     */
    @Query("SELECT i FROM ImagemRadiologica i WHERE i.dataUpload BETWEEN :dataInicio AND :dataFim AND i.ativo = true ORDER BY i.dataUpload DESC")
    List<ImagemRadiologica> findByDataUploadBetweenAndAtivoTrue(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    /**
     * Conta imagens ativas por prontuário
     */
    @Query("SELECT COUNT(i) FROM ImagemRadiologica i WHERE i.prontuario.id = :prontuarioId AND i.ativo = true")
    Long countByProntuarioIdAndAtivoTrue(@Param("prontuarioId") Long prontuarioId);

    /**
     * Calcula tamanho total das imagens por prontuário
     */
    @Query("SELECT COALESCE(SUM(i.tamanhoArquivo), 0) FROM ImagemRadiologica i WHERE i.prontuario.id = :prontuarioId AND i.ativo = true")
    Long sumTamanhoArquivoByProntuarioIdAndAtivoTrue(@Param("prontuarioId") Long prontuarioId);

    /**
     * Busca tipos de imagem únicos
     */
    @Query("SELECT DISTINCT i.tipoImagem FROM ImagemRadiologica i WHERE i.ativo = true ORDER BY i.tipoImagem")
    List<String> findDistinctTiposImagem();
}
