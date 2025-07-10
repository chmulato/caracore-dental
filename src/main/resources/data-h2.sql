-- Arquivo de dados para H2

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
(1, 'Paciente Teste', 'paciente@teste.com', '(11) 99999-9999', '1990-01-01');

-- Inserir profissionais
INSERT INTO profissional (id, nome, especialidade, email, telefone, ativo) 
VALUES 
(1, 'Dra. Teste', 'Clínico Geral', 'dra.teste@caracore.com.br', '(11) 88888-8888', true),
(2, 'Dr. Exemplo', 'Ortodontia', 'dr.exemplo@caracore.com.br', '(11) 77777-7777', true);
