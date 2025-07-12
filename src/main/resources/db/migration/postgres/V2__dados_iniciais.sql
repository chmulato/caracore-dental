-- V2__dados_iniciais.sql: Inserção de dados iniciais para o MVP

INSERT INTO usuario (nome, email, senha, role) VALUES
  ('Administrador', 'suporte@caracore.com.br', '$2a$10$EXEMPLOSENHACRIPTOGRAFADA', 'ROLE_ADMIN'),
  ('Dentista Exemplo', 'dentista@caracore.com.br', '$2a$10$EXEMPLOSENHACRIPTOGRAFADA', 'ROLE_DENTIST'),
  ('Recepcionista Exemplo', 'recepcao@caracore.com.br', '$2a$10$EXEMPLOSENHACRIPTOGRAFADA', 'ROLE_RECEPTIONIST');

INSERT INTO paciente (nome, email, telefone) VALUES
  ('Paciente Teste', 'paciente@exemplo.com', '(11) 99999-0000');

INSERT INTO profissional (nome, especialidade, email) VALUES
  ('Dr. Dentista Exemplo', 'Odontologia', 'dentista@caracore.com.br');

INSERT INTO agendamento (paciente_id, profissional_id, data_hora, descricao, paciente, dentista) VALUES
  (1, 1, '2025-07-01 09:00:00', 'Consulta inicial', 'Paciente Teste', 'Dr. Dentista Exemplo');
