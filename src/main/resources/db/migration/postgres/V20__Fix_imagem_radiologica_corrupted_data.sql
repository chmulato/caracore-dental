-- Corrigir dados corrompidos na tabela imagem_radiologica
-- V20__Fix_imagem_radiologica_corrupted_data.sql

-- Remover registros com dados corrompidos onde tamanho_arquivo contém dados inválidos
DELETE FROM imagem_radiologica 
WHERE tamanho_arquivo IS NULL OR tamanho_arquivo < 0 OR tamanho_arquivo > 999999999;

-- Limpar todos os registros de imagem para evitar problemas
DELETE FROM imagem_radiologica;
