-- Test data for H2 - Enhanced for AgendamentoPublicoController tests
-- Clean up first
DELETE FROM agendamento;
DELETE FROM dentista;
DELETE FROM profissional;
DELETE FROM paciente;
DELETE FROM usuario;

-- Insert users
INSERT INTO usuario (id, email, nome, senha, role) VALUES
(1, 'admin@teste.com', 'Administrador de Testes', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_ADMIN'),
(2, 'joao.silva@teste.com', 'Dr. João Silva', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_DENTIST'),
(3, 'maria.santos@teste.com', 'Dra. Maria Santos', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_DENTIST');

-- Insert professionals with proper structure
INSERT INTO profissional (id, nome, especialidade, email, telefone, cro, horario_inicio, horario_fim, ativo, exposto_publicamente) VALUES
(1, 'Dr. João Silva', 'Clínico Geral', 'joao.silva@teste.com', '(11) 91234-5678', 'SP-12345', '08:00', '18:00', TRUE, TRUE),
(2, 'Dra. Maria Santos', 'Ortodontista', 'maria.santos@teste.com', '(11) 92345-6789', 'SP-23456', '09:00', '17:00', TRUE, TRUE),
(3, 'Dra. Ana Costa', 'Implantodontista', 'ana.costa@teste.com', '(11) 93456-7890', 'SP-34567', '08:30', '16:30', TRUE, TRUE);

-- Insert patients
INSERT INTO paciente (id, nome, email, telefone) VALUES
(1, 'João Silva', 'joao.paciente@teste.com', '(11) 99999-0001'),
(2, 'Maria Santos', 'maria.paciente@teste.com', '(11) 99999-0002'),
(3, 'Ana Costa', 'ana.paciente@teste.com', '(11) 99999-0003');

-- Insert appointments
INSERT INTO agendamento (id, paciente_id, profissional_id, data_hora, descricao, paciente, dentista, telefone_whatsapp, observacao, status) VALUES
(1, 1, 1, '2025-07-10 10:00:00', 'Consulta de rotina', 'João Silva', 'Dr. João Silva - Clínico Geral', '11999999999', 'Observações do agendamento 1', 'AGENDADO'),
(2, 2, 2, '2025-07-11 14:00:00', 'Avaliação ortodôntica', 'Maria Santos', 'Dra. Maria Santos - Ortodontista', '11999999999', 'Observações do agendamento 2', 'AGENDADO'),
(3, 3, 3, '2025-07-12 09:30:00', 'Consulta implante', 'Ana Costa', 'Dra. Ana Costa - Implantodontista', '11999999999', 'Observações do agendamento 3', 'AGENDADO');

-- Reset sequences
ALTER SEQUENCE IF EXISTS usuario_id_seq RESTART WITH 4;
ALTER SEQUENCE IF EXISTS profissional_id_seq RESTART WITH 4;
ALTER SEQUENCE IF EXISTS paciente_id_seq RESTART WITH 4;
ALTER SEQUENCE IF EXISTS agendamento_id_seq RESTART WITH 4;
