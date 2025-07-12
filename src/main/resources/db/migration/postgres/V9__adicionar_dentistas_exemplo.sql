-- V9__adicionar_dentistas_exemplo.sql: Adiciona dados de dentistas para exemplo

-- Primeiro, garantir que as colunas existam na tabela profissional
ALTER TABLE profissional ADD COLUMN IF NOT EXISTS telefone VARCHAR(20);
ALTER TABLE profissional ADD COLUMN IF NOT EXISTS cro VARCHAR(20);
ALTER TABLE profissional ADD COLUMN IF NOT EXISTS horario_inicio VARCHAR(5);
ALTER TABLE profissional ADD COLUMN IF NOT EXISTS horario_fim VARCHAR(5);
ALTER TABLE profissional ADD COLUMN IF NOT EXISTS ativo BOOLEAN DEFAULT TRUE;

-- Atualizar o profissional existente
UPDATE profissional 
SET telefone = '(11) 98765-4321', 
    cro = 'SP-12345', 
    horario_inicio = '08:00', 
    horario_fim = '18:00',
    ativo = TRUE
WHERE email = 'dentista@caracore.com.br';

-- Inserir novos dentistas
INSERT INTO profissional (nome, especialidade, email, telefone, cro, horario_inicio, horario_fim, ativo) VALUES
('Dra. Ana Silva', 'Ortodontia', 'ana.silva@caracore.com.br', '(11) 91234-5678', 'SP-23456', '09:00', '17:00', TRUE),
('Dr. Roberto Gomes', 'Implantodontia', 'roberto.gomes@caracore.com.br', '(11) 92345-6789', 'SP-34567', '08:30', '16:30', TRUE),
('Dra. Carla Mendes', 'Endodontia', 'carla.mendes@caracore.com.br', '(11) 93456-7890', 'SP-45678', '10:00', '19:00', TRUE);
