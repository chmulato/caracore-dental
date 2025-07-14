-- Migração V21: Limpeza completa de dados corrompidos na tabela imagem_radiologica
-- Remove todos os registros que podem causar conflitos de tipo de dados

-- 1. Remover registros onde tamanho_arquivo contém strings base64 ou valores não numéricos
DELETE FROM imagem_radiologica 
WHERE tamanho_arquivo IS NULL 
   OR CAST(tamanho_arquivo AS TEXT) LIKE 'data:image%'
   OR CAST(tamanho_arquivo AS TEXT) LIKE '%base64%'
   OR LENGTH(CAST(tamanho_arquivo AS TEXT)) > 20;

-- 2. Remover registros com conteúdo base64 muito grande que pode estar causando problemas
DELETE FROM imagem_radiologica 
WHERE LENGTH(conteudo_base64) > 10485760;

-- 3. Remover registros órfãos (sem prontuário associado válido)
DELETE FROM imagem_radiologica 
WHERE prontuario_id NOT IN (SELECT id FROM prontuario);
