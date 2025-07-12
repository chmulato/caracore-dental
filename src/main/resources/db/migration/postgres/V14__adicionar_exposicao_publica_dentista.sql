-- Migration V14: Adicionar controle de exposição pública para dentistas
-- Data: 05/07/2025
-- Descrição: Adiciona campo para controlar se o dentista aparece na agenda pública

-- Adicionar coluna para controle de exposição pública
ALTER TABLE profissional 
ADD COLUMN IF NOT EXISTS exposto_publicamente BOOLEAN DEFAULT TRUE;

-- Comentário explicativo da coluna
COMMENT ON COLUMN profissional.exposto_publicamente IS 'Controla se o dentista aparece na agenda pública online';

-- Atualizar dentistas existentes para que sejam expostos publicamente por padrão
UPDATE profissional 
SET exposto_publicamente = TRUE 
WHERE exposto_publicamente IS NULL;
