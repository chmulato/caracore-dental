-- V18__massa_de_dados_para_testes.sql
-- Inserção de massa de dados para testes de usabilidade do sistema prontuário
-- Data: 12/07/2025

-- Mais pacientes para testes de usabilidade
INSERT INTO paciente (nome, email, telefone, consentimento_confirmado, consentimento_lgpd, data_consentimento, nome_social, genero, data_nascimento)
VALUES 
-- Adultos
('Ana Silva', 'ana.silva@exemplo.com', '(41) 98765-4321', true, true, '2025-06-15 10:30:00', NULL, 'Feminino', '1985-03-12'),
('Carlos Oliveira', 'carlos@exemplo.com', '(41) 97654-3210', true, true, '2025-06-16 09:15:00', NULL, 'Masculino', '1978-07-22'),
('Mariana Costa', 'mariana@exemplo.com', '(41) 98877-6655', true, true, '2025-06-17 14:45:00', NULL, 'Feminino', '1990-11-05'),
('Ricardo Souza', 'ricardo@exemplo.com', '(41) 99988-7766', true, true, '2025-06-18 16:20:00', NULL, 'Masculino', '1982-04-30'),
('Juliana Ferreira', 'juliana@exemplo.com', '(41) 98765-0987', true, true, '2025-06-19 11:10:00', NULL, 'Feminino', '1975-09-15'),
('Fernando Santos', 'fernando@exemplo.com', '(41) 99876-5432', true, true, '2025-06-20 08:30:00', NULL, 'Masculino', '1988-12-03'),
-- Casos especiais
('Gabriela Martins', 'gabriela@exemplo.com', '(41) 98888-5555', true, true, '2025-06-21 13:45:00', 'Gabriel Martins', 'Masculino', '1992-02-28'),
('Lucas Pereira', 'lucas@exemplo.com', '(41) 97777-6666', false, true, '2025-06-22 10:30:00', NULL, 'Masculino', '1980-05-17'),
('Patrícia Lima', 'patricia@exemplo.com', '(41) 96666-7777', true, false, NULL, NULL, 'Feminino', '1987-08-21'),
-- Idosos
('José Almeida', 'jose@exemplo.com', '(41) 95555-8888', true, true, '2025-06-23 09:00:00', NULL, 'Masculino', '1950-01-05'),
('Maria Conceição', 'maria.c@exemplo.com', '(41) 94444-9999', true, true, '2025-06-24 14:15:00', NULL, 'Feminino', '1945-07-10'),
-- Adolescentes/crianças
('Pedro Alves', 'resp.pedro@exemplo.com', '(41) 93333-0000', true, true, '2025-06-25 16:30:00', NULL, 'Masculino', '2012-11-22'),
('Sophia Rocha', 'resp.sophia@exemplo.com', '(41) 92222-1111', true, true, '2025-06-26 10:45:00', NULL, 'Feminino', '2010-03-18'),
('Miguel Torres', 'resp.miguel@exemplo.com', '(41) 91111-2222', true, true, '2025-06-27 08:30:00', NULL, 'Masculino', '2015-06-30');

-- Inserção de mais dentistas para testes
INSERT INTO profissional (nome, especialidade, email, telefone, cro, horario_inicio, horario_fim, ativo, exposto_publicamente)
VALUES 
('Dra. Amanda Cardoso', 'Ortodontia', 'amanda.cardoso@caracore.com.br', '(41) 98888-1111', 'CRO-PR 12345', '08:00', '17:00', true, true),
('Dr. Bruno Martins', 'Endodontia', 'bruno.martins@caracore.com.br', '(41) 97777-2222', 'CRO-PR 23456', '09:00', '18:00', true, true),
('Dra. Camila Sousa', 'Periodontia', 'camila.sousa@caracore.com.br', '(41) 96666-3333', 'CRO-PR 34567', '08:30', '17:30', true, true),
('Dr. Daniel Oliveira', 'Cirurgia Bucomaxilofacial', 'daniel.oliveira@caracore.com.br', '(41) 95555-4444', 'CRO-PR 45678', '10:00', '19:00', true, false),
('Dra. Eduarda Lima', 'Odontopediatria', 'eduarda.lima@caracore.com.br', '(41) 94444-5555', 'CRO-PR 56789', '08:00', '16:00', true, true);

-- Inserção de prontuários para os pacientes
DO $$
DECLARE
    paciente_rec RECORD;
    dentista_rec RECORD;
    prontuario_id BIGINT;
    tratamento_id BIGINT;
    paciente_counter INT := 0;
BEGIN
    -- Para cada paciente
    FOR paciente_rec IN SELECT id, nome FROM paciente WHERE id NOT IN (SELECT paciente_id FROM prontuario) ORDER BY id
    LOOP
        paciente_counter := paciente_counter + 1;
        
        -- Seleciona um dentista para este paciente (rotacionando)
        SELECT id, nome INTO dentista_rec FROM profissional 
        WHERE especialidade IS NOT NULL 
        ORDER BY id 
        OFFSET (paciente_counter % (SELECT COUNT(*) FROM profissional WHERE especialidade IS NOT NULL)) 
        LIMIT 1;
        
        -- Insere o prontuário
        INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais, data_criacao)
        VALUES (
            paciente_rec.id, 
            dentista_rec.id,
            CASE 
                WHEN paciente_counter % 5 = 0 THEN 'Histórico de tratamento ortodôntico na adolescência. Remoção de sisos em 2020.'
                WHEN paciente_counter % 5 = 1 THEN 'Paciente relata dor ocasional nos molares superiores. Histórico familiar de problemas periodontais.'
                WHEN paciente_counter % 5 = 2 THEN 'Primeira consulta. Sem histórico odontológico anterior.'
                WHEN paciente_counter % 5 = 3 THEN 'Paciente realizou tratamento de canal no dente 26 há 2 anos.'
                ELSE 'Paciente com histórico de bruxismo noturno. Usa placa de mordida durante o sono.'
            END,
            CASE 
                WHEN paciente_counter % 4 = 0 THEN 'Alergia a anestésicos com sulfitos'
                WHEN paciente_counter % 4 = 1 THEN 'Alergia a látex'
                WHEN paciente_counter % 4 = 2 THEN 'Alergia a penicilina'
                ELSE 'Sem alergias conhecidas'
            END,
            CASE 
                WHEN paciente_counter % 3 = 0 THEN 'Atenolol 25mg 1x/dia, Vitamina D 1000UI/dia'
                WHEN paciente_counter % 3 = 1 THEN 'Sem medicações de uso contínuo'
                ELSE 'Anticoncepcional oral'
            END,
            CASE 
                WHEN paciente_counter % 6 = 0 THEN 'Paciente ansioso em procedimentos odontológicos. Recomenda-se abordagem calma e explicativa.'
                WHEN paciente_counter % 6 = 1 THEN 'Boa higiene oral. Uso regular de fio dental.'
                WHEN paciente_counter % 6 = 2 THEN 'Paciente com refluxo gastroesofágico que pode afetar o esmalte dental.'
                WHEN paciente_counter % 6 = 3 THEN 'Paciente relata sensibilidade a alimentos gelados.'
                WHEN paciente_counter % 6 = 4 THEN 'Necessita de profilaxia antibiótica antes de procedimentos invasivos devido a válvula cardíaca.'
                ELSE 'Paciente colaborativo e sem observações especiais.'
            END,
            CURRENT_TIMESTAMP - (paciente_counter * INTERVAL '1 day')
        ) RETURNING id INTO prontuario_id;
        
        -- Registros de tratamento para cada prontuário (2-4 registros por prontuário)
        FOR i IN 1..FLOOR(RANDOM() * 3) + 2 LOOP
            INSERT INTO registro_tratamento (
                prontuario_id, 
                dentista_id, 
                data_tratamento, 
                procedimento, 
                descricao, 
                dente, 
                material_utilizado, 
                status, 
                valor_procedimento,
                data_criacao
            ) VALUES (
                prontuario_id,
                dentista_rec.id,
                CURRENT_TIMESTAMP - ((paciente_counter * 3 + i) * INTERVAL '1 day'),
                CASE 
                    WHEN i % 5 = 0 THEN 'Limpeza e Profilaxia'
                    WHEN i % 5 = 1 THEN 'Restauração'
                    WHEN i % 5 = 2 THEN 'Aplicação de Flúor'
                    WHEN i % 5 = 3 THEN 'Extração'
                    ELSE 'Avaliação Clínica'
                END,
                CASE 
                    WHEN i % 5 = 0 THEN 'Remoção de tártaro e polimento dos dentes'
                    WHEN i % 5 = 1 THEN 'Restauração de cárie em resina composta'
                    WHEN i % 5 = 2 THEN 'Aplicação tópica de flúor para fortalecimento do esmalte'
                    WHEN i % 5 = 3 THEN 'Extração de dente com mobilidade aumentada'
                    ELSE 'Avaliação inicial com exame clínico completo'
                END,
                CASE 
                    WHEN i % 5 = 0 THEN NULL
                    WHEN i % 5 = 1 THEN (FLOOR(RANDOM() * 4) + 1)::text || (FLOOR(RANDOM() * 8) + 1)::text
                    WHEN i % 5 = 2 THEN NULL
                    WHEN i % 5 = 3 THEN (FLOOR(RANDOM() * 4) + 1)::text || (FLOOR(RANDOM() * 8) + 1)::text
                    ELSE NULL
                END,
                CASE 
                    WHEN i % 5 = 0 THEN 'Pasta profilática, jato de bicarbonato'
                    WHEN i % 5 = 1 THEN 'Resina composta A' || (FLOOR(RANDOM() * 3) + 1)::text || ', ácido fosfórico'
                    WHEN i % 5 = 2 THEN 'Gel de flúor neutro'
                    WHEN i % 5 = 3 THEN 'Anestésico local, material para sutura'
                    ELSE NULL
                END,
                CASE 
                    WHEN i % 3 = 0 THEN 'CONCLUIDO'
                    WHEN i % 3 = 1 THEN 'EM_ANDAMENTO'
                    ELSE 'PLANEJADO'
                END,
                CASE 
                    WHEN i % 5 = 0 THEN 120.00
                    WHEN i % 5 = 1 THEN 180.00
                    WHEN i % 5 = 2 THEN 50.00
                    WHEN i % 5 = 3 THEN 250.00
                    ELSE 100.00
                END,
                CURRENT_TIMESTAMP - ((paciente_counter * 3 + i) * INTERVAL '1 day')
            ) RETURNING id INTO tratamento_id;
        END LOOP;
        
        -- Imagens radiológicas para alguns prontuários (não todos, para simular situação real)
        IF paciente_counter % 3 = 0 THEN
            INSERT INTO imagem_radiologica (
                prontuario_id, 
                dentista_id, 
                nome_arquivo, 
                tipo_imagem, 
                descricao, 
                imagem_base64, 
                formato_arquivo, 
                tamanho_arquivo, 
                data_upload
            ) VALUES (
                prontuario_id,
                dentista_rec.id,
                'radiografia_' || paciente_rec.nome || '.jpg',
                CASE 
                    WHEN paciente_counter % 4 = 0 THEN 'Radiografia Panorâmica'
                    WHEN paciente_counter % 4 = 1 THEN 'Radiografia Periapical'
                    WHEN paciente_counter % 4 = 2 THEN 'Radiografia Interproximal'
                    ELSE 'Radiografia Oclusal'
                END,
                'Radiografia para avaliação de ' || 
                CASE 
                    WHEN paciente_counter % 4 = 0 THEN 'toda a arcada dentária'
                    WHEN paciente_counter % 4 = 1 THEN 'possível lesão periapical'
                    WHEN paciente_counter % 4 = 2 THEN 'cáries interproximais'
                    ELSE 'estruturas do palato'
                END,
                'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBxdWFsaXR5ID0gOTAK/9sAQwADAgIDAgIDAwMDBAMDBAUIBQUEBAUKBwcGCAwKDAwLCgsLDQ4SEA0OEQ4LCxAWEBETFBUVFQwPFxgWFBgSFBUU/9sAQwEDBAQFBAUJBQUJFA0LDRQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQU/8AAEQgAEAAQAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMEAQMDBAMAAAAAAAABAgMEEQUhMQYSQVFhByJxEzKBkaEII0KxwRQV0fAkM1KCkuHxIyRistHB0gTCU3Khh6K1/8QAGQEAAwEBAQAAAAAAAAAAAAAAAQIDAAQF/8QAKBEAAhEDAgUEAwEAAAAAAAAAAAECAxEhMQQSImFxkaEyQVKB0fDx/9oADAMBAAIRAxEAPwD5/vLUFmWIGMAAAAASUVORK5CYII=',
                'jpeg',
                (FLOOR(RANDOM() * 100) + 10) * 1024,
                CURRENT_TIMESTAMP - (paciente_counter * INTERVAL '1 day')
            );
        END IF;
    END LOOP;
END
$$;

-- Inserir alguns usuários com perfil de dentista para testes
INSERT INTO usuario (email, nome, senha, role)
VALUES 
('amanda.cardoso@caracore.com.br', 'Dra. Amanda Cardoso', '$2a$10$ktLqQvVHpZl9woajQtomGe9sMOtcJRlRZGGNJZEYt3IJE4Hu5zRXC', 'ROLE_DENTIST'),
('bruno.martins@caracore.com.br', 'Dr. Bruno Martins', '$2a$10$ktLqQvVHpZl9woajQtomGe9sMOtcJRlRZGGNJZEYt3IJE4Hu5zRXC', 'ROLE_DENTIST'),
('camila.sousa@caracore.com.br', 'Dra. Camila Sousa', '$2a$10$ktLqQvVHpZl9woajQtomGe9sMOtcJRlRZGGNJZEYt3IJE4Hu5zRXC', 'ROLE_DENTIST')
ON CONFLICT (email) DO NOTHING;

-- Adicionar exames complementares para alguns prontuários
INSERT INTO exame_complementar (prontuario_id, tipo_exame, data_exame, resultado, observacoes, dentista_id)
SELECT 
    p.id,
    CASE 
        WHEN ROW_NUMBER() OVER () % 4 = 0 THEN 'Tomografia Computadorizada Cone Beam'
        WHEN ROW_NUMBER() OVER () % 4 = 1 THEN 'Exame de Saliva'
        WHEN ROW_NUMBER() OVER () % 4 = 2 THEN 'Teste de Sensibilidade Pulpar'
        ELSE 'Análise de Oclusão'
    END,
    CURRENT_TIMESTAMP - (ROW_NUMBER() OVER () * INTERVAL '3 day'),
    CASE 
        WHEN ROW_NUMBER() OVER () % 4 = 0 THEN 'Densidade óssea adequada para procedimento de implante.'
        WHEN ROW_NUMBER() OVER () % 4 = 1 THEN 'pH 6.8, fluxo normal, capacidade tampão reduzida.'
        WHEN ROW_NUMBER() OVER () % 4 = 2 THEN 'Teste positivo com resposta normal nos dentes 11, 12 e 21. Teste negativo no dente 22.'
        ELSE 'Contatos prematuros nos dentes 16 e 46. Desgaste seletivo realizado.'
    END,
    CASE 
        WHEN ROW_NUMBER() OVER () % 4 = 0 THEN 'Exame realizado para planejamento de implante.'
        WHEN ROW_NUMBER() OVER () % 4 = 1 THEN 'Recomendado uso de enxaguante com pH neutro.'
        WHEN ROW_NUMBER() OVER () % 4 = 2 THEN 'Recomendado tratamento endodôntico no dente 22.'
        ELSE 'Ajuste realizado. Acompanhamento em 30 dias.'
    END,
    p.dentista_id
FROM prontuario p
ORDER BY RANDOM()
LIMIT 8;