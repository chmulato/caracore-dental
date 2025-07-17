-- Migration V13: Melhorias no modelo de Agendamento
-- Data: 03/07/2025
-- Descrição: Adiciona novos campos e melhorias na estrutura da tabela agendamento

-- Adicionar novos campos para gestão completa de agendamentos
ALTER TABLE agendamento 
ADD COLUMN IF NOT EXISTS duracao_minutos INTEGER DEFAULT 30;

ALTER TABLE agendamento 
ADD COLUMN IF NOT EXISTS telefone_whatsapp VARCHAR(20);

ALTER TABLE agendamento 
ADD COLUMN IF NOT EXISTS data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE agendamento 
ADD COLUMN IF NOT EXISTS data_atualizacao TIMESTAMP;

-- Alterar tamanho da coluna observacao para comportar mais texto
ALTER TABLE agendamento 
RENAME COLUMN descricao TO observacao;

ALTER TABLE agendamento 
ALTER COLUMN observacao TYPE VARCHAR(1000);

-- Alterar tamanho das colunas de texto
ALTER TABLE agendamento 
ALTER COLUMN paciente TYPE VARCHAR(100);

ALTER TABLE agendamento 
ALTER COLUMN dentista TYPE VARCHAR(100);

ALTER TABLE agendamento
ADD COLUMN IF NOT EXISTS status VARCHAR(20);

ALTER TABLE agendamento 
ALTER COLUMN status TYPE VARCHAR(20);

-- Atualizar registros com status NULL para valor padrão antes de tornar NOT NULL
UPDATE agendamento 
SET status = 'AGENDADO' 
WHERE status IS NULL;

-- Adicionar constraints de not null onde necessário
ALTER TABLE agendamento 
ALTER COLUMN paciente SET NOT NULL;

ALTER TABLE agendamento 
ALTER COLUMN dentista SET NOT NULL;

ALTER TABLE agendamento 
ALTER COLUMN data_hora SET NOT NULL;

ALTER TABLE agendamento 
ALTER COLUMN status SET NOT NULL;

ALTER TABLE agendamento 
ALTER COLUMN data_criacao SET NOT NULL;

-- Renomear coluna dataHora para data_hora (se ainda não foi feito)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'agendamento' AND column_name = 'datahora') THEN
        ALTER TABLE agendamento RENAME COLUMN datahora TO data_hora;
    END IF;
END $$;

-- Criar índices para melhorar performance
CREATE INDEX IF NOT EXISTS idx_agendamento_data_hora ON agendamento(data_hora);
CREATE INDEX IF NOT EXISTS idx_agendamento_dentista ON agendamento(dentista);
CREATE INDEX IF NOT EXISTS idx_agendamento_paciente ON agendamento(paciente);
CREATE INDEX IF NOT EXISTS idx_agendamento_status ON agendamento(status);
CREATE INDEX IF NOT EXISTS idx_agendamento_data_criacao ON agendamento(data_criacao);

-- Índice composto para consultas por dentista e data
CREATE INDEX IF NOT EXISTS idx_agendamento_dentista_data ON agendamento(dentista, data_hora);

-- Comentários para documentação
COMMENT ON COLUMN agendamento.duracao_minutos IS 'Duração da consulta em minutos (padrão: 30)';
COMMENT ON COLUMN agendamento.telefone_whatsapp IS 'Telefone WhatsApp do paciente para comunicação';
COMMENT ON COLUMN agendamento.data_criacao IS 'Data e hora de criação do agendamento';
COMMENT ON COLUMN agendamento.data_atualizacao IS 'Data e hora da última atualização';
COMMENT ON COLUMN agendamento.status IS 'Status: AGENDADO, CONFIRMADO, REAGENDADO, REALIZADO, CANCELADO, NAO_COMPARECEU';

-- Atualizar registros existentes com valores padrão
UPDATE agendamento 
SET data_criacao = CURRENT_TIMESTAMP 
WHERE data_criacao IS NULL;
