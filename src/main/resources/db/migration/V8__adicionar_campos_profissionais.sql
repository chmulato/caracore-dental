-- V8__adicionar_campos_profissionais.sql: Adiciona campos para gerenciamento de profissionais

-- Adicionar coluna telefone na tabela profissional
ALTER TABLE profissional ADD COLUMN IF NOT EXISTS telefone VARCHAR(20);

-- Adicionar coluna CRO na tabela profissional
ALTER TABLE profissional ADD COLUMN IF NOT EXISTS cro VARCHAR(20);

-- Adicionar coluna de horário de início
ALTER TABLE profissional ADD COLUMN IF NOT EXISTS horario_inicio VARCHAR(5);

-- Adicionar coluna de horário de fim
ALTER TABLE profissional ADD COLUMN IF NOT EXISTS horario_fim VARCHAR(5);

-- Adicionar coluna de status (ativo/inativo)
ALTER TABLE profissional ADD COLUMN IF NOT EXISTS ativo BOOLEAN DEFAULT TRUE;
