-- Script para inserir dados de teste apenas (não criar tabelas)
-- As tabelas já são criadas pelos scripts de migração

-- Inserir usuário admin para testes
INSERT INTO usuario (email, nome, senha, role) 
VALUES ('admin@teste.com', 'Administrador de Testes', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_ADMIN');

-- Inserir alguns agendamentos para testes
INSERT INTO agendamento (data_hora, paciente, dentista, procedimento, observacoes)
VALUES ('2023-12-01 10:00:00', 'Paciente Teste 1', 'Dentista Teste', 'Limpeza', 'Observação teste 1');

INSERT INTO agendamento (data_hora, paciente, dentista, procedimento, observacoes)
VALUES ('2023-12-01 14:30:00', 'Paciente Teste 2', 'Dentista Teste', 'Extração', 'Observação teste 2');
