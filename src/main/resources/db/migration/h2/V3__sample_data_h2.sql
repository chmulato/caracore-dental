-- V3__sample_data_h2.sql: Dados de exemplo para H2

-- Inserir dados de prontuários para H2 (sem usar blocos PL/SQL)
-- Garantir que existam pacientes e dentistas
INSERT INTO paciente (nome, email, telefone)
SELECT 'Ana Silva', 'ana@example.com', '(41) 98888-7777'
WHERE NOT EXISTS (SELECT 1 FROM paciente WHERE email = 'ana@example.com');

-- Inserir prontuário de exemplo adicional
INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais)
SELECT 
    (SELECT id FROM paciente WHERE email = 'ana@example.com'), 
    (SELECT id FROM dentista WHERE email = 'maria.dentista@example.com'), 
    'Histórico de cáries múltiplas. Tratamento de canal no dente 16 realizado em 2023.',
    'Nenhuma alergia conhecida',
    'Anticoncepcional oral',
    'Recomendado uso de fio dental diário'
WHERE EXISTS (SELECT 1 FROM paciente WHERE email = 'ana@example.com')
  AND EXISTS (SELECT 1 FROM dentista WHERE email = 'maria.dentista@example.com');

-- Inserir registro de tratamento adicional
INSERT INTO registro_tratamento (prontuario_id, dentista_id, procedimento, descricao, dente, material_utilizado, status, valor_procedimento)
SELECT 
    (SELECT id FROM prontuario WHERE paciente_id = (SELECT id FROM paciente WHERE email = 'ana@example.com')),
    (SELECT id FROM dentista WHERE email = 'maria.dentista@example.com'),
    'Restauração', 
    'Restauração de cárie em resina composta', 
    '21', 
    'Resina composta A2, ácido fosfórico', 
    'CONCLUIDO', 
    180.00
WHERE EXISTS (SELECT 1 FROM prontuario WHERE paciente_id = (SELECT id FROM paciente WHERE email = 'ana@example.com'));
