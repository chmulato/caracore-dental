-- Arquivo de dados para H2 - Atualizado para incluir tabelas de prontuário

-- Inserir usuários de teste
INSERT INTO usuario (id, nome, email, senha, role) 
VALUES 
(1, 'Administrador', 'suporte@caracore.com.br', '$2a$10$ktLqQvVHpZl9woajQtomGe9sMOtcJRlRZGGNJZEYt3IJE4Hu5zRXC', 'ROLE_ADMIN'),
(2, 'Dentista Exemplo', 'dentista@caracore.com.br', '$2a$10$ktLqQvVHpZl9woajQtomGe9sMOtcJRlRZGGNJZEYt3IJE4Hu5zRXC', 'ROLE_DENTIST'),
(3, 'Recepcionista Exemplo', 'recepcao@caracore.com.br', '$2a$10$ktLqQvVHpZl9woajQtomGe9sMOtcJRlRZGGNJZEYt3IJE4Hu5zRXC', 'ROLE_RECEPTIONIST'),
(4, 'Joao Maria', 'joao@gmail.com', '$2a$10$ktLqQvVHpZl9woajQtomGe9sMOtcJRlRZGGNJZEYt3IJE4Hu5zRXC', 'ROLE_PATIENT');

-- Inserir dados de paciente
INSERT INTO paciente (id, nome, email, telefone, data_nascimento) 
VALUES 
(1, 'Paciente Teste', 'paciente@teste.com', '(11) 99999-9999', '1990-01-01'),
(2, 'Joao Maria', 'joao@gmail.com', '(41) 99909-7797', '1985-05-12'),
(3, 'Maria Silva', 'maria@example.com', '(41) 99998-8888', '1982-07-25');

-- Inserir profissionais
INSERT INTO profissional (id, nome, especialidade, email, telefone, ativo, cro, horario_inicio, horario_fim) 
VALUES 
(1, 'Dra. Teste', 'Clínico Geral', 'dra.teste@caracore.com.br', '(11) 88888-8888', true, 'CRO-SP 12345', '08:00', '18:00'),
(2, 'Dr. Exemplo', 'Ortodontia', 'dr.exemplo@caracore.com.br', '(11) 77777-7777', true, 'CRO-SP 54321', '09:00', '17:00');

-- Inserir dentistas
INSERT INTO dentista (id, nome, email, telefone, cro, especialidade, horario_inicio, horario_fim, ativo, exposicao_publica) 
VALUES 
(1, 'Dra. Teste', 'dra.teste@caracore.com.br', '(11) 88888-8888', 'CRO-SP 12345', 'Clínico Geral', '08:00', '18:00', true, true),
(2, 'Dr. Exemplo', 'dr.exemplo@caracore.com.br', '(11) 77777-7777', 'CRO-SP 54321', 'Ortodontia', '09:00', '17:00', true, false);

-- Inserir prontuários
INSERT INTO prontuario (id, paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais) 
VALUES 
(1, 1, 1, 'Paciente sem histórico de problemas odontológicos significativos.', 'Alergia a ibuprofeno', 'Vitamina D3', 'Paciente colaborativo, boa higiene oral'),
(2, 2, 1, 'Histórico de cáries múltiplas. Tratamento de canal no dente 16 realizado em 2023.', 'Nenhuma alergia conhecida', 'Anticoncepcional oral', 'Recomendado uso de fio dental diário');

-- Inserir registros de tratamento
INSERT INTO registro_tratamento (id, prontuario_id, dentista_id, procedimento, descricao, dente, material_utilizado, status, valor_procedimento) 
VALUES 
(1, 1, 1, 'Limpeza e Profilaxia', 'Remoção de tártaro e polimento dos dentes', NULL, 'Pasta profilática, jato de bicarbonato', 'CONCLUIDO', 120.00),
(2, 1, 1, 'Aplicação de Flúor', 'Aplicação tópica de flúor para fortalecimento do esmalte', NULL, 'Gel de flúor neutro', 'CONCLUIDO', 50.00),
(3, 2, 2, 'Restauração', 'Restauração de cárie em resina composta', '21', 'Resina composta A2, ácido fosfórico', 'CONCLUIDO', 180.00);
