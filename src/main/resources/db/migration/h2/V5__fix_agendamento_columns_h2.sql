-- V5__fix_agendamento_columns_h2.sql: Corrigir colunas da tabela agendamento para H2

-- Adicionar colunas que faltam na tabela agendamento
ALTER TABLE agendamento ADD duracao_minutos INT DEFAULT 30;
ALTER TABLE agendamento ADD telefone_whatsapp VARCHAR(20);
ALTER TABLE agendamento ADD data_criacao TIMESTAMP DEFAULT NOW;
ALTER TABLE agendamento ADD data_atualizacao TIMESTAMP;

-- Atualizar dados existentes
UPDATE agendamento SET data_criacao = NOW WHERE data_criacao IS NULL;
