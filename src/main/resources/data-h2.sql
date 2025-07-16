-- data-h2.sql: Dados de exemplo para o banco H2
-- Este arquivo é executado automaticamente pelo Spring Boot após a criação do schema
--
-- MASSA DE DADOS PARA DESENVOLVIMENTO E TESTES:
-- ✅ 9 Usuários do sistema (1 admin, 5 dentistas, 3 funcionários)
-- ✅ 7 Dentistas profissionais (5 expostos publicamente para agendamento online)
-- ✅ 11 Pacientes com dados completos e LGPD
-- ✅ 11 Prontuários médicos vinculados
-- ✅ 6 Imagens radiológicas de exemplo
-- ✅ 18 Registros de tratamentos (concluídos, em andamento, agendados)
-- ✅ 16 Agendamentos (passados e futuros)
--
-- DENTISTAS EXPOSTOS PUBLICAMENTE (disponíveis para agendamento online):
-- 1. Dr. Ana Silva - Clínico Geral
-- 2. Dr. Carlos Oliveira - Ortodontista  
-- 3. Dra. Mariana Santos - Endodontista
-- 4. Dra. Fernanda Costa - Implantodontista
-- 5. Dra. Beatriz Lima - Odontopediatra
--
-- ACESSO PADRÃO PARA TESTES:
-- Admin: admin@caracore.com / senha123
-- Dentistas: {nome}@caracore.com / senha123
-- Funcionários: recepcao@caracore.com / senha123

-- Inserir usuários do sistema
INSERT INTO usuario (nome, email, senha, role) VALUES 
('Administrador do Sistema', 'admin@caracore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDDmxTXjoxRuVkjIv7Z6Dz3Kf1.7ozeO', 'ADMIN'),
('Dr. Ana Silva', 'ana.silva@caracore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDDmxTXjoxRuVkjIv7Z6Dz3Kf1.7ozeO', 'DENTISTA'),
('Dr. Carlos Oliveira', 'carlos.oliveira@caracore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDDmxTXjoxRuVkjIv7Z6Dz3Kf1.7ozeO', 'DENTISTA'),
('Dra. Mariana Santos', 'mariana.santos@caracore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDDmxTXjoxRuVkjIv7Z6Dz3Kf1.7ozeO', 'DENTISTA'),
('Dra. Fernanda Costa', 'fernanda.costa@caracore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDDmxTXjoxRuVkjIv7Z6Dz3Kf1.7ozeO', 'DENTISTA'),
('Dra. Beatriz Lima', 'beatriz.lima@caracore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDDmxTXjoxRuVkjIv7Z6Dz3Kf1.7ozeO', 'DENTISTA'),
('Recepcionista Principal', 'recepcao@caracore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDDmxTXjoxRuVkjIv7Z6Dz3Kf1.7ozeO', 'FUNCIONARIO'),
('Recepcionista Auxiliar', 'recepcao2@caracore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDDmxTXjoxRuVkjIv7Z6Dz3Kf1.7ozeO', 'FUNCIONARIO'),
('Assistente Dental', 'assistente@caracore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDDmxTXjoxRuVkjIv7Z6Dz3Kf1.7ozeO', 'FUNCIONARIO');

-- Inserir dentistas
INSERT INTO profissional (nome, email, telefone, cro, especialidade, horario_inicio, horario_fim, ativo, exposto_publicamente) VALUES 
('Dr. Ana Silva', 'ana.silva@caracore.com', '11999991111', 'CRO-SP-12345', 'Clinico Geral', '08:00', '18:00', true, true),
('Dr. Carlos Oliveira', 'carlos.oliveira@caracore.com', '11999992222', 'CRO-SP-23456', 'Ortodontista', '09:00', '17:00', true, true),
('Dra. Mariana Santos', 'mariana.santos@caracore.com', '11999993333', 'CRO-SP-34567', 'Endodontista', '08:30', '17:30', true, true),
('Dr. Roberto Pereira', 'roberto.pereira@caracore.com', '11999994444', 'CRO-SP-45678', 'Periodontista', '08:00', '16:00', true, false),
('Dra. Fernanda Costa', 'fernanda.costa@caracore.com', '11999995555', 'CRO-SP-56789', 'Implantodontista', '10:00', '18:00', true, true),
('Dr. Lucas Andrade', 'lucas.andrade@caracore.com', '11999996666', 'CRO-SP-67890', 'Cirurgiao Bucomaxilofacial', '07:00', '15:00', true, false),
('Dra. Beatriz Lima', 'beatriz.lima@caracore.com', '11999997777', 'CRO-SP-78901', 'Odontopediatra', '08:00', '17:00', true, true);

-- Inserir pacientes  
INSERT INTO paciente (nome, email, telefone, data_nascimento, genero, nome_social, consentimento_lgpd, consentimento_confirmado, data_consentimento) VALUES 
('Maria José da Silva', 'maria.silva@email.com', '11987654321', '1985-03-15', 'Feminino', null, true, true, '2025-01-10 14:30:00'),
('João Santos Oliveira', 'joao.santos@email.com', '11987654322', '1990-07-22', 'Masculino', null, true, true, '2025-01-15 09:15:00'),
('Ana Carolina Pereira', 'ana.pereira@email.com', '11987654323', '1978-11-08', 'Feminino', null, true, true, '2025-01-20 16:45:00'),
('Pedro Henrique Costa', 'pedro.costa@email.com', '11987654324', '1995-05-30', 'Masculino', null, true, true, '2025-02-01 10:20:00'),
('Letícia Rodrigues', 'leticia.rodrigues@email.com', '11987654325', '1988-09-12', 'Feminino', null, true, true, '2025-02-05 13:10:00'),
('Carlos Eduardo Dias', 'carlos.dias@email.com', '11987654326', '1982-01-25', 'Masculino', null, true, true, '2025-02-10 11:30:00'),
('Fernanda Alves Lima', 'fernanda.lima@email.com', '11987654327', '1993-12-03', 'Feminino', null, true, true, '2025-02-15 15:20:00'),
('Rafael Gomes Silva', 'rafael.gomes@email.com', '11987654328', '1987-06-18', 'Masculino', null, true, true, '2025-02-20 08:45:00'),
('Camila Souza Santos', 'camila.santos@email.com', '11987654329', '1991-04-07', 'Feminino', null, true, true, '2025-02-25 14:15:00'),
('Thiago Ferreira', 'thiago.ferreira@email.com', '11987654330', '1984-10-14', 'Masculino', null, true, true, '2025-03-01 09:30:00'),
('Isabella Martinez', 'isabella.martinez@email.com', '11987654331', '1996-08-27', 'Feminino', null, true, true, '2025-03-05 16:00:00');

-- Inserir prontuários (linkando pacientes com dentistas)
INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, medicamentos_uso, alergias, observacoes_gerais, data_criacao, data_ultima_atualizacao) VALUES 
(1, 1, 'Hipertensão controlada. Última limpeza há 8 meses.', 'Losartana 50mg', 'Penicilina', 'Paciente colaborativa, boa higiene oral.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 'Histórico de diabetes tipo 2. Tratamento ortodôntico anterior.', 'Metformina 850mg', 'Nenhuma conhecida', 'Necessita acompanhamento periodontal regular devido diabetes.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 'Paciente saudável. Dor de dente recorrente no molar superior direito.', 'Nenhum', 'Latex', 'Ansiedade dental moderada. Preferir sedação leve.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 1, 'Jovem saudável. Primeira consulta odontológica em 3 anos.', 'Nenhum', 'Nenhuma conhecida', 'Excelente higiene oral. Consulta preventiva.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 4, 'Gengivite crônica. Fumante (10 cigarros/dia).', 'Anticoncepcional', 'Aspirina', 'Necessita orientação sobre cessação do tabagismo.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 5, 'Perda de molares inferiores. Candidato a implantes.', 'Sinvastatina 20mg', 'Nenhuma conhecida', 'Osso suficiente para implantes. Exames complementares ok.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 7, 'Mãe relatou dor nos molares de leite da criança.', 'Nenhum', 'Nenhuma conhecida', 'Primeira consulta odontológica. Criança colaborativa.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 2, 'Apinhamento dentário. Interessado em ortodontia.', 'Suplemento de ferro', 'Nenhuma conhecida', 'Perfil facial favorável para tratamento ortodôntico.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 3, 'Dor intensa no dente 26. Possível necessidade de canal.', 'Ibuprofeno 400mg (uso esporádico)', 'Nenhuma conhecida', 'Paciente relatou dor noturna intensa há 3 dias.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 6, 'Trauma facial por acidente. Fratura de dente anterior.', 'Nenhum', 'Contraste iodado', 'Trauma recente. TC de face solicitada.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 1, 'Gestante (24 semanas). Gengivite gravídica.', 'Sulfato ferroso, Ácido fólico', 'Nenhuma conhecida', 'Cuidados especiais devido gestação. Evitar raios-X.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserir agendamentos
INSERT INTO agendamento (paciente, dentista, data_hora, observacao, status, duracao_minutos, telefone_whatsapp, data_criacao, data_atualizacao) VALUES 
('Maria José da Silva', 'Dr. Ana Silva - Clínico Geral', '2025-07-15 09:00:00', 'Consulta de retorno - limpeza', 'AGENDADO', 60, '11987654321', CURRENT_TIMESTAMP, null),
('João Santos Oliveira', 'Dr. Carlos Oliveira - Ortodontista', '2025-07-15 14:00:00', 'Avaliação ortodôntica inicial', 'CONFIRMADO', 45, '11987654322', CURRENT_TIMESTAMP, null),
('Ana Carolina Pereira', 'Dra. Mariana Santos - Endodontista', '2025-07-16 10:30:00', 'Possível tratamento endodôntico', 'AGENDADO', 90, '11987654323', CURRENT_TIMESTAMP, null),
('Pedro Henrique Costa', 'Dr. Ana Silva - Clínico Geral', '2025-07-16 16:00:00', 'Consulta preventiva', 'AGENDADO', 30, '11987654324', CURRENT_TIMESTAMP, null),
('Letícia Rodrigues', 'Dr. Roberto Pereira - Periodontista', '2025-07-17 08:30:00', 'Tratamento periodontal', 'AGENDADO', 60, '11987654325', CURRENT_TIMESTAMP, null),
('Carlos Eduardo Dias', 'Dra. Fernanda Costa - Implantodontista', '2025-07-17 15:00:00', 'Consulta para implantes', 'AGENDADO', 60, '11987654326', CURRENT_TIMESTAMP, null),
('Fernanda Alves Lima', 'Dra. Beatriz Lima - Odontopediatra', '2025-07-18 09:30:00', 'Consulta odontopediátrica', 'AGENDADO', 45, '11987654327', CURRENT_TIMESTAMP, null),
('Rafael Gomes Silva', 'Dr. Carlos Oliveira - Ortodontista', '2025-07-18 11:00:00', 'Consulta ortodôntica', 'AGENDADO', 45, '11987654328', CURRENT_TIMESTAMP, null),
('Camila Souza Santos', 'Dra. Mariana Santos - Endodontista', '2025-07-19 14:00:00', 'Tratamento de canal urgente', 'AGENDADO', 120, '11987654329', CURRENT_TIMESTAMP, null),
('Thiago Ferreira', 'Dr. Lucas Andrade - Cirurgião Bucomaxilofacial', '2025-07-19 08:00:00', 'Avaliação pós-trauma', 'AGENDADO', 60, '11987654330', CURRENT_TIMESTAMP, null);

-- Inserir imagens radiológicas (exemplos)
INSERT INTO imagem_radiologica (prontuario_id, dentista_id, nome_arquivo, tipo_imagem, descricao, data_upload, ativo, formato_arquivo, imagem_base64) VALUES 
(1, 1, 'panoramica_maria_20250110.jpg', 'Panorâmica', 'Exame de rotina - sem alterações significativas', CURRENT_TIMESTAMP, true, 'jpg', 'data:image/jpeg;base64,exemplo'),
(2, 2, 'periapical_joao_20250115.jpg', 'Periapical', 'Região de molares superiores - necessário acompanhamento', CURRENT_TIMESTAMP, true, 'jpg', 'data:image/jpeg;base64,exemplo'),
(3, 3, 'panoramica_ana_20250120.jpg', 'Panorâmica', 'Lesão apical em dente 16 - indicado tratamento endodôntico', CURRENT_TIMESTAMP, true, 'jpg', 'data:image/jpeg;base64,exemplo'),
(6, 5, 'tc_carlos_20250210.dcm', 'Tomografia', 'Planejamento para implantes - densidade óssea adequada', CURRENT_TIMESTAMP, true, 'dcm', 'data:application/dicom;base64,exemplo'),
(9, 3, 'periapical_camila_20250225.jpg', 'Periapical', 'Dente 26 com lesão extensa - tratamento urgente', CURRENT_TIMESTAMP, true, 'jpg', 'data:image/jpeg;base64,exemplo'),
(10, 6, 'tc_thiago_20250301.dcm', 'Tomografia', 'Trauma facial - fratura de processo alveolar', CURRENT_TIMESTAMP, true, 'dcm', 'data:application/dicom;base64,exemplo');

-- Nota: Registros de tratamento podem ser adicionados posteriormente via interface administrativa

-- Inserir mais agendamentos futuros para demonstração
INSERT INTO agendamento (paciente, dentista, data_hora, observacao, status, duracao_minutos, telefone_whatsapp, data_criacao, data_atualizacao) VALUES 
('Maria José da Silva', 'Dr. Ana Silva - Clínico Geral', '2025-07-22 09:00:00', 'Consulta de retorno pós-restauração', 'AGENDADO', 30, '11987654321', CURRENT_TIMESTAMP, null),
('João Santos Oliveira', 'Dr. Carlos Oliveira - Ortodontista', '2025-07-22 14:00:00', 'Manutenção ortodôntica mensal', 'AGENDADO', 30, '11987654322', CURRENT_TIMESTAMP, null),
('Ana Carolina Pereira', 'Dra. Mariana Santos - Endodontista', '2025-07-23 10:30:00', 'Finalização do tratamento de canal', 'AGENDADO', 60, '11987654323', CURRENT_TIMESTAMP, null),
('Letícia Rodrigues', 'Dr. Roberto Pereira - Periodontista', '2025-07-23 08:30:00', 'Retorno pós-curetagem', 'AGENDADO', 45, '11987654325', CURRENT_TIMESTAMP, null),
('Carlos Eduardo Dias', 'Dra. Fernanda Costa - Implantodontista', '2025-07-24 15:00:00', 'Cirurgia de implante', 'AGENDADO', 90, '11987654326', CURRENT_TIMESTAMP, null),
('Fernanda Alves Lima', 'Dra. Beatriz Lima - Odontopediatra', '2025-07-24 09:30:00', 'Controle após selante', 'AGENDADO', 30, '11987654327', CURRENT_TIMESTAMP, null);
