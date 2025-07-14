-- V6__sample_patients_and_prontuarios_h2.sql: Dados de exemplo para pacientes e prontuários

-- Inserir pacientes de exemplo
INSERT INTO paciente (nome, email, telefone, data_nascimento, genero, nome_social, consentimento_lgpd, consentimento_confirmado, data_consentimento) VALUES
('Maria Silva Santos', 'maria.silva@email.com', '11987654321', '1985-03-15', 'Feminino', null, true, true, NOW),
('João Carlos Oliveira', 'joao.oliveira@email.com', '11976543210', '1978-11-22', 'Masculino', null, true, true, NOW),
('Ana Beatriz Costa', 'ana.costa@email.com', '11965432109', '1992-07-08', 'Feminino', null, true, true, NOW),
('Carlos Eduardo Santos', 'carlos.santos@email.com', '11954321098', '1980-04-30', 'Masculino', null, true, true, NOW),
('Fernanda Lima Pereira', 'fernanda.lima@email.com', '11943210987', '1988-12-12', 'Feminino', null, true, true, NOW),
('Roberto Silva Junior', 'roberto.junior@email.com', '11932109876', '1975-09-03', 'Masculino', null, true, true, NOW),
('Patricia Alves Rodrigues', 'patricia.alves@email.com', '11921098765', '1990-01-25', 'Feminino', null, true, true, NOW),
('Antonio Carlos Ferreira', 'antonio.ferreira@email.com', '11910987654', '1982-06-18', 'Masculino', null, true, true, NOW),
('Juliana Mendes Costa', 'juliana.mendes@email.com', '11909876543', '1995-10-14', 'Feminino', null, true, true, NOW),
('Ricardo Souza Lima', 'ricardo.souza@email.com', '11898765432', '1987-02-28', 'Masculino', null, true, true, NOW);

-- Inserir profissionais/dentistas de exemplo
INSERT INTO profissional (nome, email, telefone, cro, especialidade, horario_inicio, horario_fim, ativo, exposto_publicamente) VALUES
('Dr. Pedro Henrique Silva', 'pedro.silva@dentista.com', '11987001001', 'CRO-SP-12345', 'Clínico Geral', '08:00', '18:00', true, true),
('Dra. Carla Fernanda Costa', 'carla.costa@dentista.com', '11987001002', 'CRO-SP-12346', 'Ortodontista', '08:00', '17:00', true, true),
('Dr. Eduardo Almeida Santos', 'eduardo.santos@dentista.com', '11987001003', 'CRO-SP-12347', 'Endodontista', '09:00', '18:00', true, true),
('Dra. Mariana Oliveira Lima', 'mariana.lima@dentista.com', '11987001004', 'CRO-SP-12348', 'Periodontista', '08:00', '17:00', true, true),
('Dr. Rafael Barbosa Silva', 'rafael.barbosa@dentista.com', '11987001005', 'CRO-SP-12349', 'Cirurgião Oral', '10:00', '19:00', true, true);

-- Aguardar um pouco para garantir que os dados foram inseridos
-- Inserir prontuários para os pacientes
INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais, data_criacao, data_ultima_atualizacao) VALUES
(1, 1, 'Histórico de cáries recorrentes. Última limpeza há 8 meses.', 'Alergia a penicilina', 'Anticoncepcional, Vitamina D', 'Paciente colaborativa, boa higiene oral.', NOW, NOW),
(2, 2, 'Tratamento ortodôntico anterior (2010-2013). Bruxismo noturno.', 'Nenhuma alergia conhecida', 'Relaxante muscular (noturno)', 'Necessita de placa de bruxismo. Controle periódico.', NOW, NOW),
(3, 3, 'Tratamento de canal no dente 26 em 2020. Sensibilidade dentária.', 'Alergia a látex', 'Nenhum', 'Paciente ansiosa, prefere atendimento matutino.', NOW, NOW),
(4, 4, 'Doença periodontal leve. Fumante (10 cigarros/dia).', 'Nenhuma alergia conhecida', 'Medicamento para pressão arterial', 'Orientado sobre cessação do tabagismo. Retorno em 3 meses.', NOW, NOW),
(5, 5, 'Cirurgia de extração dos sisos (2019). Apinhamento dental leve.', 'Alergia a aspirina', 'Anticoncepcional', 'Avaliação para possível tratamento ortodôntico.', NOW, NOW),
(6, 1, 'Diabetes tipo 2 controlado. Xerostomia frequente.', 'Nenhuma alergia conhecida', 'Metformina, Losartana', 'Controle glicêmico bom. Cuidados especiais com cicatrização.', NOW, NOW),
(7, 2, 'Primeira consulta. Sem histórico odontológico relevante.', 'Nenhuma alergia conhecida', 'Nenhum', 'Paciente jovem, boa saúde geral. Orientações preventivas.', NOW, NOW),
(8, 3, 'Múltiplos tratamentos de canal. Próteses antigas.', 'Alergia a anestésicos locais (lidocaína)', 'Anti-hipertensivo', 'Necessita substituição de próteses. Usar anestésico alternativo.', NOW, NOW),
(9, 4, 'Gravidez (2º trimestre). Gengivite gravídica.', 'Nenhuma alergia conhecida', 'Ácido fólico, Sulfato ferroso', 'Tratamentos eletivos adiados para pós-parto. Profilaxias permitidas.', NOW, NOW),
(10, 5, 'Prática de esportes de contato. Fratura dental anterior (2021).', 'Nenhuma alergia conhecida', 'Whey protein, Creatina', 'Recomendado protetor bucal. Restauração estética realizada.', NOW, NOW);

-- Inserir alguns registros de tratamento
INSERT INTO registro_tratamento (prontuario_id, dentista_id, procedimento, descricao, dente, status, material_utilizado, valor_procedimento, observacoes, data_tratamento, data_criacao, data_atualizacao) VALUES
(1, 1, 'Limpeza e Profilaxia', 'Remoção de tártaro e polimento dental', 'Todos', 'CONCLUIDO', 'Pasta profilática, Flúor', 120.00, 'Paciente orientada sobre técnica de escovação.', NOW, NOW, NOW),
(1, 1, 'Restauração', 'Restauração em resina composta', '16', 'PLANEJADO', 'Resina fotopolimerizável', 180.00, 'Cárie média na face oclusal.', NOW, NOW, NOW),
(2, 2, 'Consulta Ortodôntica', 'Avaliação e planejamento ortodôntico', 'Todos', 'CONCLUIDO', 'Moldagem, Radiografias', 200.00, 'Indicado aparelho móvel superior.', NOW, NOW, NOW),
(3, 3, 'Tratamento Endodôntico', 'Retratamento de canal', '26', 'EM_ANDAMENTO', 'Limas, Cimento endodôntico', 800.00, '2ª sessão agendada para próxima semana.', NOW, NOW, NOW),
(4, 4, 'Raspagem Periodontal', 'Raspagem e alisamento radicular por quadrante', 'Q1', 'CONCLUIDO', 'Curetas, Ultrassom', 300.00, 'Boa resposta ao tratamento. Continuar outros quadrantes.', NOW, NOW, NOW),
(5, 5, 'Consulta Cirúrgica', 'Avaliação para implante', '23', 'PLANEJADO', 'Tomografia, Guia cirúrgico', 150.00, 'Indicado implante após cicatrização.', NOW, NOW, NOW),
(6, 1, 'Aplicação de Flúor', 'Aplicação tópica de flúor', 'Todos', 'CONCLUIDO', 'Flúor em gel', 80.00, 'Paciente diabético, reforçar cuidados.', NOW, NOW, NOW),
(7, 2, 'Exame Clínico Inicial', 'Primeira consulta e orientações', 'Todos', 'CONCLUIDO', 'Ficha clínica', 100.00, 'Paciente sem alterações. Retorno em 6 meses.', NOW, NOW, NOW),
(8, 3, 'Consulta de Urgência', 'Tratamento de dor aguda', '47', 'CONCLUIDO', 'Medicação, Curativo', 120.00, 'Pulpite aguda. Agendado tratamento definitivo.', NOW, NOW, NOW),
(9, 4, 'Profilaxia Gestante', 'Limpeza especial para gestante', 'Todos', 'CONCLUIDO', 'Pasta sem flúor', 100.00, 'Técnica adaptada para gestação. Sem intercorrências.', NOW, NOW, NOW);

-- Inserir alguns agendamentos de exemplo
INSERT INTO agendamento (paciente, dentista, data_hora, status, observacao, duracao_minutos, telefone_whatsapp, data_criacao) VALUES
('Maria Silva Santos', 'Dr. Pedro Henrique Silva - Clínico Geral', '2025-07-15 10:00:00', 'AGENDADO', 'Retorno para avaliação de restauração', 30, '11987654321', NOW),
('João Carlos Oliveira', 'Dra. Carla Fernanda Costa - Ortodontista', '2025-07-15 14:00:00', 'CONFIRMADO', 'Instalação de aparelho móvel', 60, '11976543210', NOW),
('Ana Beatriz Costa', 'Dr. Eduardo Almeida Santos - Endodontista', '2025-07-16 09:30:00', 'AGENDADO', 'Continuação do tratamento de canal', 90, '11965432109', NOW),
('Carlos Eduardo Santos', 'Dra. Mariana Oliveira Lima - Periodontista', '2025-07-16 15:00:00', 'AGENDADO', 'Raspagem Q2', 45, '11954321098', NOW),
('Fernanda Lima Pereira', 'Dr. Rafael Barbosa Silva - Cirurgião Oral', '2025-07-17 11:00:00', 'AGENDADO', 'Consulta para implante', 30, '11943210987', NOW),
('Roberto Silva Junior', 'Dr. Pedro Henrique Silva - Clínico Geral', '2025-07-17 16:00:00', 'AGENDADO', 'Controle diabético e limpeza', 45, '11932109876', NOW),
('Patricia Alves Rodrigues', 'Dra. Carla Fernanda Costa - Ortodontista', '2025-07-18 08:30:00', 'AGENDADO', 'Primeira consulta ortodôntica', 45, '11921098765', NOW),
('Antonio Carlos Ferreira', 'Dr. Eduardo Almeida Santos - Endodôntico', '2025-07-18 13:00:00', 'AGENDADO', 'Urgência - dor de dente', 60, '11910987654', NOW),
('Juliana Mendes Costa', 'Dra. Mariana Oliveira Lima - Periodontista', '2025-07-21 10:30:00', 'AGENDADO', 'Consulta pré-natal odontológica', 30, '11909876543', NOW),
('Ricardo Souza Lima', 'Dr. Rafael Barbosa Silva - Cirurgião Oral', '2025-07-21 14:30:00', 'AGENDADO', 'Avaliação pós-trauma dental', 45, '11898765432', NOW);
