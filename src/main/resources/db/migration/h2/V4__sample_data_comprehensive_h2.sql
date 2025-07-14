-- V5__sample_data_comprehensive_h2.sql: Massa de dados abrangente para H2
-- Criação de dados realistas compatíveis com a estrutura da tabela H2

-- =====================================================
-- INSERIR DENTISTAS ADICIONAIS
-- =====================================================

INSERT INTO dentista (nome, email, telefone, cro, especialidade, ativo) VALUES
('Dr. Carlos Eduardo Santos', 'carlos.santos@caracore.com', '(41) 99887-1234', 'PR-15432', 'Ortodontia', TRUE),
('Dra. Fernanda Lima', 'fernanda.lima@caracore.com', '(41) 99765-4321', 'PR-18765', 'Endodontia', TRUE),
('Dr. Roberto Silva', 'roberto.silva@caracore.com', '(41) 99654-7890', 'PR-12987', 'Cirurgia Oral', TRUE),
('Dra. Patricia Costa', 'patricia.costa@caracore.com', '(41) 99543-2109', 'PR-20145', 'Periodontia', TRUE),
('Dr. André Oliveira', 'andre.oliveira@caracore.com', '(41) 99432-8765', 'PR-17823', 'Implantodontia', TRUE);

-- =====================================================
-- INSERIR PACIENTES ADICIONAIS
-- =====================================================

INSERT INTO paciente (nome, email, telefone, data_nascimento, genero, aceite_lgpd) VALUES
('Carlos Roberto Ferreira', 'carlos.ferreira@email.com', '(41) 98765-4321', '1985-03-15', 'MASCULINO', TRUE),
('Mariana Santos Silva', 'mariana.santos@email.com', '(41) 97654-3210', '1990-07-22', 'FEMININO', TRUE),
('João Pedro Costa', 'joao.costa@email.com', '(41) 96543-2109', '1978-11-08', 'MASCULINO', TRUE),
('Ana Carolina Lima', 'ana.lima@email.com', '(41) 95432-1098', '1992-05-30', 'FEMININO', TRUE),
('Eduardo Silva Oliveira', 'eduardo.oliveira@email.com', '(41) 94321-0987', '1988-09-12', 'MASCULINO', TRUE),
('Fernanda Costa Santos', 'fernanda.santos@email.com', '(41) 93210-9876', '1995-02-18', 'FEMININO', TRUE),
('Ricardo Pereira Lima', 'ricardo.lima@email.com', '(41) 92109-8765', '1982-12-05', 'MASCULINO', TRUE),
('Juliana Alves Costa', 'juliana.costa@email.com', '(41) 91098-7654', '1991-08-28', 'FEMININO', TRUE);

-- =====================================================
-- INSERIR PRONTUÁRIOS COM RELACIONAMENTOS
-- =====================================================

-- Prontuário para Carlos Roberto Ferreira
INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais) 
VALUES (
    (SELECT id FROM paciente WHERE email = 'carlos.ferreira@email.com'),
    (SELECT id FROM dentista WHERE cro = 'PR-15432'),
    'Paciente com histórico de gengivite crônica. Realizou tratamento ortodôntico na adolescência.',
    'Alérgico à penicilina',
    'Losartana 50mg - Hipertensão',
    'Recomendado controle rigoroso da placa bacteriana. Retorno em 6 meses.'
);

-- Prontuário para Mariana Santos Silva
INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais) 
VALUES (
    (SELECT id FROM paciente WHERE email = 'mariana.santos@email.com'),
    (SELECT id FROM dentista WHERE cro = 'PR-18765'),
    'Paciente jovem, sem antecedentes odontológicos significativos. Primeira consulta.',
    'Nenhuma alergia conhecida',
    'Anticoncepcional oral',
    'Orientações de higiene bucal. Paciente muito colaborativa.'
);

-- Prontuário para João Pedro Costa
INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais) 
VALUES (
    (SELECT id FROM paciente WHERE email = 'joao.costa@email.com'),
    (SELECT id FROM dentista WHERE cro = 'PR-12987'),
    'Paciente com múltiplas extrações dentárias. Necessita reabilitação protética.',
    'Alergia à dipirona',
    'Metformina 850mg - Diabetes tipo 2',
    'Diabético controlado. Cuidados especiais durante procedimentos cirúrgicos.'
);

-- Prontuário para Ana Carolina Lima
INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais) 
VALUES (
    (SELECT id FROM paciente WHERE email = 'ana.lima@email.com'),
    (SELECT id FROM dentista WHERE cro = 'PR-20145'),
    'Paciente com doença periodontal moderada. Realizou raspagem e alisamento radicular.',
    'Nenhuma alergia conhecida',
    'Não faz uso de medicamentos',
    'Melhora significativa após tratamento periodontal. Manter higiene rigorosa.'
);

-- =====================================================
-- INSERIR REGISTROS DE TRATAMENTO DETALHADOS
-- =====================================================

-- Tratamentos para Carlos Roberto Ferreira
INSERT INTO registro_tratamento (prontuario_id, dentista_id, procedimento, descricao, dente, material_utilizado, status, valor_procedimento, data_tratamento) VALUES
(
    (SELECT id FROM prontuario WHERE paciente_id = (SELECT id FROM paciente WHERE email = 'carlos.ferreira@email.com')),
    (SELECT id FROM dentista WHERE cro = 'PR-15432'),
    'Profilaxia',
    'Limpeza dental profissional com remoção de tártaro',
    'Todos',
    'Pasta profilática, flúor gel',
    'CONCLUIDO',
    150.00,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM prontuario WHERE paciente_id = (SELECT id FROM paciente WHERE email = 'carlos.ferreira@email.com')),
    (SELECT id FROM dentista WHERE cro = 'PR-15432'),
    'Restauração',
    'Restauração de cárie classe II com resina composta',
    '17',
    'Resina composta A3, sistema adesivo',
    'CONCLUIDO',
    280.00,
    CURRENT_TIMESTAMP
);

-- Tratamentos para Mariana Santos Silva
INSERT INTO registro_tratamento (prontuario_id, dentista_id, procedimento, descricao, dente, material_utilizado, status, valor_procedimento, data_tratamento) VALUES
(
    (SELECT id FROM prontuario WHERE paciente_id = (SELECT id FROM paciente WHERE email = 'mariana.santos@email.com')),
    (SELECT id FROM dentista WHERE cro = 'PR-18765'),
    'Consulta Inicial',
    'Avaliação clínica completa e planejamento',
    'N/A',
    'N/A',
    'CONCLUIDO',
    120.00,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM prontuario WHERE paciente_id = (SELECT id FROM paciente WHERE email = 'mariana.santos@email.com')),
    (SELECT id FROM dentista WHERE cro = 'PR-18765'),
    'Tratamento Endodôntico',
    'Tratamento de canal do incisivo central superior',
    '11',
    'Instrumentos endodônticos, guta-percha',
    'EM_ANDAMENTO',
    850.00,
    CURRENT_TIMESTAMP
);

-- =====================================================
-- INSERIR IMAGENS RADIOLÓGICAS
-- =====================================================

-- Imagens para Carlos Roberto Ferreira
INSERT INTO imagem_radiologica (prontuario_id, dentista_id, tipo_imagem, descricao, nome_arquivo, imagem_base64, formato_arquivo, tamanho_arquivo) VALUES
(
    (SELECT id FROM prontuario WHERE paciente_id = (SELECT id FROM paciente WHERE email = 'carlos.ferreira@email.com')),
    (SELECT id FROM dentista WHERE cro = 'PR-15432'),
    'PANORAMICA',
    'Radiografia panorâmica inicial',
    'panoramica_carlos_20240115.jpg',
    'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==',
    'JPEG',
    1024
),
(
    (SELECT id FROM prontuario WHERE paciente_id = (SELECT id FROM paciente WHERE email = 'carlos.ferreira@email.com')),
    (SELECT id FROM dentista WHERE cro = 'PR-15432'),
    'PERIAPICAL',
    'Radiografia periapical do dente 17 pré-restauração',
    'periapical_17_carlos_20240120.jpg',
    'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==',
    'JPEG',
    512
);

-- Imagens para Mariana Santos Silva
INSERT INTO imagem_radiologica (prontuario_id, dentista_id, tipo_imagem, descricao, nome_arquivo, imagem_base64, formato_arquivo, tamanho_arquivo) VALUES
(
    (SELECT id FROM prontuario WHERE paciente_id = (SELECT id FROM paciente WHERE email = 'mariana.santos@email.com')),
    (SELECT id FROM dentista WHERE cro = 'PR-18765'),
    'PANORAMICA',
    'Radiografia panorâmica de avaliação inicial',
    'panoramica_mariana_20240210.jpg',
    'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==',
    'JPEG',
    2048
),
(
    (SELECT id FROM prontuario WHERE paciente_id = (SELECT id FROM paciente WHERE email = 'mariana.santos@email.com')),
    (SELECT id FROM dentista WHERE cro = 'PR-18765'),
    'PERIAPICAL',
    'Radiografia periapical do dente 11 para endodontia',
    'periapical_11_mariana_20240215.jpg',
    'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==',
    'JPEG',
    1536
);

-- =====================================================
-- COMENTÁRIOS FINAIS
-- =====================================================

-- Total de registros criados:
-- Dentistas: 5 adicionais (7 total)
-- Pacientes: 8 adicionais (11 total)  
-- Prontuários: 4 adicionais (5 total)
-- Registros de Tratamento: 4 tratamentos
-- Imagens Radiológicas: 4 imagens
-- 
-- Esta massa de dados fornece um conjunto abrangente para:
-- - Demonstração de funcionalidades
-- - Testes de interface
-- - Validação de relatórios
-- - Simulação de cenários reais de uso
