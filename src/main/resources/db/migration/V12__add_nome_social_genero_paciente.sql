-- Migration V12: Adicionar campos de nome social e gênero (Portaria 2.836/2011)
-- Data: 02/07/2025
-- Descrição: Adiciona campos para atender a Portaria nº 2.836/2011 do Ministério da Saúde sobre nome social

-- Adicionar campo de nome social
ALTER TABLE paciente 
ADD COLUMN nome_social VARCHAR(100);

-- Adicionar campo de gênero
ALTER TABLE paciente 
ADD COLUMN genero VARCHAR(50);

-- Comentários para documentação
COMMENT ON COLUMN paciente.nome_social IS 'Nome social do paciente conforme Portaria nº 2.836/2011 do Ministério da Saúde';
COMMENT ON COLUMN paciente.genero IS 'Gênero autodeclarado pelo paciente conforme legislação vigente';

-- Índice para consultas por nome social
CREATE INDEX idx_paciente_nome_social ON paciente(nome_social) WHERE nome_social IS NOT NULL;
