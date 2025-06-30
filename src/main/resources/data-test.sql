-- Massa de dados inicial para o MVP Cara Core Agendamento

-- Script desabilitado para evitar erro de chave duplicada em testes. Renomeie ou mova para test/resources se necessário.

-- Usuários
INSERT INTO usuario (id, nome, email, senha, role) VALUES
  (1, 'Administrador', 'suporte@caracore.com.br', '$2a$10$EXEMPLOSENHACRIPTOGRAFADA', 'ADMIN'),
  (2, 'Dentista Exemplo', 'dentista@caracore.com.br', '$2a$10$EXEMPLOSENHACRIPTOGRAFADA', 'DENTIST'),
  (3, 'Recepcionista Exemplo', 'recepcao@caracore.com.br', '$2a$10$EXEMPLOSENHACRIPTOGRAFADA', 'RECEPTIONIST');

-- Pacientes
INSERT INTO paciente (id, nome, email, telefone) VALUES
  (1, 'Paciente Teste', 'paciente@exemplo.com', '(11) 99999-0000');

-- Profissionais
INSERT INTO profissional (id, nome, especialidade, email) VALUES
  (1, 'Dr. Dentista Exemplo', 'Odontologia', 'dentista@caracore.com.br');

-- Agendamentos
INSERT INTO agendamento (id, paciente_id, profissional_id, data_hora, descricao) VALUES
  (1, 1, 1, '2025-07-01 09:00:00', 'Consulta inicial');
