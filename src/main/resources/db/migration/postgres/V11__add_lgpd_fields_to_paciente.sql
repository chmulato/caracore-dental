-- Migration V11: Adicionar campos de consentimento LGPD para pacientes
-- Data: 02/07/2025
-- Descrição: Adiciona campos para controle de consentimento LGPD via WhatsApp

-- Adicionar campo de consentimento LGPD
ALTER TABLE paciente 
ADD COLUMN consentimento_lgpd BOOLEAN NOT NULL DEFAULT FALSE;

-- Adicionar campo de confirmação do consentimento
ALTER TABLE paciente 
ADD COLUMN consentimento_confirmado BOOLEAN NOT NULL DEFAULT FALSE;

-- Adicionar campo de data/hora do consentimento
ALTER TABLE paciente 
ADD COLUMN data_consentimento TIMESTAMP;

-- Comentários para documentação
COMMENT ON COLUMN paciente.consentimento_lgpd IS 'Indica se o consentimento LGPD foi enviado via WhatsApp';
COMMENT ON COLUMN paciente.consentimento_confirmado IS 'Indica se o paciente confirmou o recebimento do consentimento LGPD';
COMMENT ON COLUMN paciente.data_consentimento IS 'Data e hora do envio do consentimento LGPD';

-- Índice para consultas por status de consentimento
CREATE INDEX idx_paciente_consentimento ON paciente(consentimento_lgpd, consentimento_confirmado);
