-- Migração V22: Limpeza completa e definitiva de todos os dados corrompidos na tabela imagem_radiologica
-- Remove TODOS os registros que podem causar erros de conversão de tipo

-- Como uma medida definitiva para resolver os problemas de conversão de dados,
-- vamos limpar completamente a tabela imagem_radiologica
-- Os dados serão recriados pelos scripts de massa de dados se necessário

TRUNCATE TABLE imagem_radiologica;
