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

-- Adicionando mais pacientes para testes completos de usabilidade (faixa etária variada)
INSERT INTO paciente (nome, email, telefone, consentimento_confirmado, consentimento_lgpd, data_consentimento, nome_social, genero, data_nascimento)
VALUES 
-- Pacientes com necessidades especiais
('Roberto Mendes', 'roberto.mendes@exemplo.com', '(41) 91234-5678', true, true, '2025-07-01 09:45:00', NULL, 'Masculino', '1982-09-15'),
('Camila Rodrigues', 'camila.r@exemplo.com', '(41) 98876-5432', true, true, '2025-07-02 14:20:00', NULL, 'Feminino', '1990-05-23'),
-- Pacientes com condições específicas (para testar casos de uso especiais)
('Vinícius Cardoso', 'vinicius@exemplo.com', '(41) 99912-3456', true, true, '2025-07-03 10:30:00', NULL, 'Masculino', '1975-11-08'),
('Laura Batista', 'laura.b@exemplo.com', '(41) 98765-9876', true, true, '2025-07-03 16:15:00', NULL, 'Feminino', '1995-03-29'),
-- Pacientes estrangeiros
('John Smith', 'john.smith@example.com', '(41) 91122-3344', true, true, '2025-07-04 09:00:00', NULL, 'Masculino', '1980-12-10'),
('Maria Rodriguez', 'maria.rodriguez@example.com', '(41) 92233-4455', true, true, '2025-07-04 13:45:00', NULL, 'Feminino', '1988-08-17'),
-- Pacientes idosos com condições específicas
('Antônio Gomes', 'antonio.g@exemplo.com', '(41) 93344-5566', true, true, '2025-07-05 10:00:00', NULL, 'Masculino', '1945-04-05'),
('Helena Ferreira', 'helena.f@exemplo.com', '(41) 94455-6677', true, true, '2025-07-05 14:30:00', NULL, 'Feminino', '1940-06-12'),
-- Pacientes jovens adultos
('Matheus Alves', 'matheus.a@exemplo.com', '(41) 95566-7788', true, true, '2025-07-06 11:15:00', NULL, 'Masculino', '2000-07-20'),
('Carolina Torres', 'carolina.t@exemplo.com', '(41) 96677-8899', true, true, '2025-07-06 15:45:00', NULL, 'Feminino', '2003-02-15');

-- Adicionando pacientes infantis com responsáveis (para testar casos específicos odontopediátricos)
INSERT INTO paciente (nome, email, telefone, consentimento_confirmado, consentimento_lgpd, data_consentimento, nome_social, genero, data_nascimento, responsavel_nome, responsavel_telefone, responsavel_email)
VALUES 
('Lucas Moreira', 'resp.lucas@exemplo.com', '(41) 97788-9900', true, true, '2025-07-07 09:30:00', NULL, 'Masculino', '2017-10-25', 'Eduardo Moreira', '(41) 97788-9901', 'eduardo.m@exemplo.com'),
('Isabela Santos', 'resp.isabela@exemplo.com', '(41) 98899-0011', true, true, '2025-07-07 13:00:00', NULL, 'Feminino', '2019-05-30', 'Márcia Santos', '(41) 98899-0012', 'marcia.s@exemplo.com'),
('Gabriel Lima', 'resp.gabriel@exemplo.com', '(41) 99900-1122', true, true, '2025-07-08 10:45:00', NULL, 'Masculino', '2016-03-15', 'Rafael Lima', '(41) 99900-1123', 'rafael.l@exemplo.com'),
('Beatriz Costa', 'resp.beatriz@exemplo.com', '(41) 90011-2233', true, true, '2025-07-08 14:15:00', NULL, 'Feminino', '2018-09-22', 'Patrícia Costa', '(41) 90011-2234', 'patricia.c@exemplo.com');

-- Inserindo mais dentistas com especialidades variadas
INSERT INTO profissional (nome, especialidade, email, telefone, cro, horario_inicio, horario_fim, ativo, exposto_publicamente)
VALUES 
('Dr. Felipe Almeida', 'Implantodontia', 'felipe.almeida@caracore.com.br', '(41) 93333-5555', 'CRO-PR 67890', '08:30', '17:30', true, true),
('Dra. Gabriela Santos', 'Odontopediatria', 'gabriela.santos@caracore.com.br', '(41) 92222-6666', 'CRO-PR 78901', '09:00', '18:00', true, true),
('Dr. Henrique Costa', 'Prótese Dentária', 'henrique.costa@caracore.com.br', '(41) 91111-7777', 'CRO-PR 89012', '10:00', '19:00', true, true);

-- Criando um procedimento para adicionar prontuários mais detalhados com casos específicos para os novos pacientes
DO $$
DECLARE
    paciente_rec RECORD;
    dentista_rec RECORD;
    prontuario_id BIGINT;
    tratamento_id BIGINT;
    imagem_id BIGINT;
    hoje DATE := CURRENT_DATE;
    
    -- Array de condições específicas para os pacientes
    condicoes TEXT[] := ARRAY[
        'Diabetes tipo 2 - Controla com metformina',
        'Hipertensão controlada com medicação',
        'Asma - Usa bombinha quando necessário',
        'Hipotireoidismo em tratamento com levotiroxina',
        'Artrite reumatoide - Tratamento contínuo',
        'Histórico de AVC em 2023 - Em recuperação',
        'Marca-passo cardíaco - Implantado em 2024',
        'Epilepsia controlada com medicação',
        'Histórico de câncer bucal em remissão',
        'Prótese de quadril - Cirurgia em 2022'
    ];
    
    -- Array de procedimentos odontológicos
    procedimentos TEXT[] := ARRAY[
        'Implante dentário',
        'Tratamento de canal',
        'Coroa protética',
        'Raspagem e alisamento radicular',
        'Clareamento dental',
        'Restauração de resina',
        'Aplicação de selante',
        'Exodontia de terceiro molar',
        'Tratamento de gengivite',
        'Prótese parcial removível'
    ];
    
    -- Array de materiais odontológicos
    materiais TEXT[] := ARRAY[
        'Implante de titânio Nobel Biocare',
        'Guta-percha e cimento resinoso',
        'Cerâmica e.max CAD',
        'Ultrassom e curetas periodontais',
        'Gel clareador Whiteness HP 35%',
        'Resina Z350 XT 3M ESPE',
        'Selante Fluroshield Dentsply',
        'Kit cirúrgico estéril',
        'Clorexidina 0,12% e PerioGard',
        'Resina acrílica termopolimerizável'
    ];
    
    -- Array de descrições detalhadas de procedimentos
    descricoes TEXT[] := ARRAY[
        'Colocação de implante unitário com enxerto ósseo localizado',
        'Tratamento endodôntico em três sessões com medicação intracanal',
        'Instalação de coroa total em cerâmica sobre núcleo metálico',
        'Terapia periodontal completa com remoção de cálculo subgengival',
        'Sessão de clareamento de consultório com proteção gengival',
        'Restauração classe II em resina composta com isolamento absoluto',
        'Aplicação preventiva de selante em molares permanentes',
        'Exodontia de dente incluso com osteotomia',
        'Tratamento não-cirúrgico de gengivite com prescrição de enxaguante',
        'Confecção e instalação de PPR com estrutura metálica'
    ];
    
    -- Para controlar a atribuição de condições
    contador_condicao INT := 1;
BEGIN
    -- Selecionar pacientes recém-adicionados (do Roberto Mendes em diante)
    FOR paciente_rec IN SELECT id, nome FROM paciente 
                      WHERE nome IN ('Roberto Mendes', 'Camila Rodrigues', 'Vinícius Cardoso', 'Laura Batista', 
                                    'John Smith', 'Maria Rodriguez', 'Antônio Gomes', 'Helena Ferreira',
                                    'Matheus Alves', 'Carolina Torres', 'Lucas Moreira', 'Isabela Santos',
                                    'Gabriel Lima', 'Beatriz Costa')
                      ORDER BY id
    LOOP
        -- Seleciona um dentista para este paciente
        SELECT id, nome, especialidade INTO dentista_rec FROM profissional 
        WHERE especialidade IS NOT NULL
        ORDER BY CASE 
            -- Direciona crianças para odontopediatria
            WHEN paciente_rec.nome IN ('Lucas Moreira', 'Isabela Santos', 'Gabriel Lima', 'Beatriz Costa') 
                THEN especialidade = 'Odontopediatria'::TEXT 
            ELSE RANDOM() 
        END DESC
        LIMIT 1;
        
        -- Escolhe uma condição específica
        IF contador_condicao > array_length(condicoes, 1) THEN
            contador_condicao := 1;
        END IF;
        
        -- Insere o prontuário com dados específicos para cada paciente
        INSERT INTO prontuario (
            paciente_id, 
            dentista_id, 
            historico_medico, 
            alergias, 
            medicamentos_uso, 
            observacoes_gerais, 
            data_criacao
        ) VALUES (
            paciente_rec.id,
            dentista_rec.id,
            CASE 
                WHEN paciente_rec.nome = 'Roberto Mendes' THEN 'Paciente com diabetes tipo 2 controlada. Necessita monitoramento glicêmico antes de procedimentos longos.'
                WHEN paciente_rec.nome = 'Camila Rodrigues' THEN 'Histórico de tratamento ortodôntico há 5 anos. Paciente com bruxismo severo.'
                WHEN paciente_rec.nome = 'Vinícius Cardoso' THEN 'Paciente com válvula cardíaca artificial. Necessita de profilaxia antibiótica.'
                WHEN paciente_rec.nome = 'Laura Batista' THEN 'Paciente ansiosa, preferência por sedação consciente em procedimentos invasivos.'
                WHEN paciente_rec.nome = 'John Smith' THEN 'Estrangeiro residente. Realizou diversos tratamentos em seu país de origem.'
                WHEN paciente_rec.nome = 'Maria Rodriguez' THEN 'Paciente hispânica com anomalia congênita na formação do esmalte.'
                WHEN paciente_rec.nome = 'Antônio Gomes' THEN 'Idoso com osteoporose e tomando anticoagulantes. Procedimentos cirúrgicos requerem cuidado especial.'
                WHEN paciente_rec.nome = 'Helena Ferreira' THEN 'Histórico de AVC, faz uso de anticoagulante. Dificuldade de abertura bucal prolongada.'
                WHEN paciente_rec.nome = 'Matheus Alves' THEN 'Jovem com histórico de trauma dental anterior em incisivos centrais.'
                WHEN paciente_rec.nome = 'Carolina Torres' THEN 'Paciente com disfunção temporomandibular moderada. Usa placa oclusal noturna.'
                WHEN paciente_rec.nome IN ('Lucas Moreira', 'Isabela Santos', 'Gabriel Lima', 'Beatriz Costa') THEN 'Criança em fase de transição da dentição decídua para permanente. Primeira visita ao dentista.'
                ELSE 'Histórico médico sem particularidades relevantes para o tratamento odontológico.'
            END,
            CASE 
                WHEN paciente_rec.nome = 'Roberto Mendes' THEN 'Alergia a Penicilina e derivados'
                WHEN paciente_rec.nome = 'Laura Batista' THEN 'Alergia a látex. Utilizar luvas nitrílicas.'
                WHEN paciente_rec.nome = 'John Smith' THEN 'Alergia a iodo e contrastes iodados'
                WHEN paciente_rec.nome = 'Carolina Torres' THEN 'Alergia a AINEs, especialmente diclofenaco'
                WHEN paciente_rec.nome = 'Beatriz Costa' THEN 'Alergia a corantes artificiais'
                ELSE 'Sem alergias conhecidas'
            END,
            CASE 
                WHEN paciente_rec.nome = 'Roberto Mendes' THEN 'Metformina 850mg 2x/dia, Losartana 50mg 1x/dia'
                WHEN paciente_rec.nome = 'Vinícius Cardoso' THEN 'Varfarina 5mg 1x/dia'
                WHEN paciente_rec.nome = 'Maria Rodriguez' THEN 'Levotiroxina 75mcg 1x/dia'
                WHEN paciente_rec.nome = 'Antônio Gomes' THEN 'Alendronato sódico 70mg 1x/semana, AAS 100mg 1x/dia'
                WHEN paciente_rec.nome = 'Helena Ferreira' THEN 'AAS 100mg 1x/dia, Enalapril 10mg 2x/dia'
                WHEN paciente_rec.nome = 'Carolina Torres' THEN 'Anticoncepcional oral e relaxante muscular para DTM'
                WHEN paciente_rec.nome = 'Lucas Moreira' THEN 'Suplemento vitamínico infantil'
                ELSE 'Sem medicações de uso contínuo'
            END,
            CASE 
                WHEN paciente_rec.nome = 'Roberto Mendes' THEN 'Necessidade de monitorar níveis glicêmicos antes de procedimentos longos. Paciente prefere atendimento matutino.'
                WHEN paciente_rec.nome = 'Camila Rodrigues' THEN 'Paciente relata episódios frequentes de sensibilidade dentária. Recomendado uso de dentifrício dessensibilizante.'
                WHEN paciente_rec.nome = 'Vinícius Cardoso' THEN 'INR deve ser verificado antes de procedimentos invasivos. Último valor: 2.5 (dentro da faixa terapêutica).'
                WHEN paciente_rec.nome = 'Laura Batista' THEN 'Forte reflexo de vômito. Usar técnicas de distração e anestesia tópica antes de procedimentos.'
                WHEN paciente_rec.nome = 'John Smith' THEN 'Comunica-se bem em português, mas prefere explicações técnicas em inglês.'
                WHEN paciente_rec.nome = 'Maria Rodriguez' THEN 'Paciente muito cuidadosa com higiene oral. Usa fio dental e escova interdental diariamente.'
                WHEN paciente_rec.nome = 'Antônio Gomes' THEN 'Apresenta tremor essencial que pode dificultar procedimentos. Agendar sessões mais curtas.'
                WHEN paciente_rec.nome = 'Helena Ferreira' THEN 'Necessita de apoio para acessar a cadeira odontológica. Mobilidade reduzida no lado direito.'
                WHEN paciente_rec.nome = 'Matheus Alves' THEN 'Praticante de esportes de contato. Recomendado uso de protetor bucal durante atividades esportivas.'
                WHEN paciente_rec.nome = 'Carolina Torres' THEN 'Paciente relata episódios frequentes de estresse que intensificam o bruxismo.'
                WHEN paciente_rec.nome = 'Lucas Moreira' THEN 'Criança colaborativa mas com leve ansiedade. Abordagem lúdica recomendada.'
                WHEN paciente_rec.nome = 'Isabela Santos' THEN 'Primeira experiência odontológica. Apresenta medo de agulhas.'
                WHEN paciente_rec.nome = 'Gabriel Lima' THEN 'Histórico familiar de problemas ortodônticos. Monitorar desenvolvimento da arcada.'
                WHEN paciente_rec.nome = 'Beatriz Costa' THEN 'Criança com déficit de atenção. Sessões curtas e uso de técnicas de gerenciamento comportamental.'
                ELSE 'Sem observações especiais no momento.'
            END,
            hoje - (contador_condicao * INTERVAL '3 day')
        ) RETURNING id INTO prontuario_id;
        
        -- Criar entre 2 e 5 registros de tratamento para cada prontuário
        FOR i IN 1..FLOOR(RANDOM() * 4) + 2 LOOP
            -- Calcula um índice para selecionar procedimentos, descrições e materiais
            DECLARE
                idx INT := (contador_condicao + i) % array_length(procedimentos, 1);
                IF idx = 0 THEN
                    idx := 1;
                END IF;
                
                -- Determina um status baseado na data do tratamento
                status_trat TEXT;
                data_trat TIMESTAMP;
            BEGIN
                -- Define a data do tratamento
                data_trat := hoje - ((contador_condicao * 3 + i) * INTERVAL '2 day');
                
                -- Define o status com base na data
                IF data_trat < hoje - INTERVAL '7 day' THEN
                    status_trat := 'CONCLUIDO';
                ELSIF data_trat < hoje THEN
                    status_trat := 'EM_ANDAMENTO';
                ELSE
                    status_trat := 'PLANEJADO';
                END IF;
                
                -- Insere o registro de tratamento
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
                    data_trat,
                    procedimentos[idx],
                    descricoes[idx],
                    -- Dentes variados para diferentes procedimentos
                    CASE 
                        -- Implantes geralmente em molares ou pré-molares
                        WHEN procedimentos[idx] = 'Implante dentário' THEN 
                            (FLOOR(RANDOM() * 2) + 1)::text || (FLOOR(RANDOM() * 3) + 6)::text
                        -- Tratamentos de canal geralmente em pré-molares e molares
                        WHEN procedimentos[idx] = 'Tratamento de canal' THEN 
                            (FLOOR(RANDOM() * 2) + 1)::text || (FLOOR(RANDOM() * 3) + 4)::text
                        -- Coroas em diversos dentes
                        WHEN procedimentos[idx] = 'Coroa protética' THEN 
                            (FLOOR(RANDOM() * 4) + 1)::text || (FLOOR(RANDOM() * 6) + 1)::text
                        -- Raspagem não especifica dente
                        WHEN procedimentos[idx] = 'Raspagem e alisamento radicular' THEN 
                            NULL
                        -- Clareamento para todos os dentes visíveis
                        WHEN procedimentos[idx] = 'Clareamento dental' THEN 
                            NULL
                        -- Restaurações em diversos dentes
                        WHEN procedimentos[idx] = 'Restauração de resina' THEN 
                            (FLOOR(RANDOM() * 4) + 1)::text || (FLOOR(RANDOM() * 8) + 1)::text
                        -- Selantes em molares permanentes
                        WHEN procedimentos[idx] = 'Aplicação de selante' THEN 
                            (FLOOR(RANDOM() * 2) + 1)::text || '6'
                        -- Exodontia geralmente de terceiros molares
                        WHEN procedimentos[idx] = 'Exodontia de terceiro molar' THEN 
                            (FLOOR(RANDOM() * 2) + 1)::text || '8'
                        -- Gengivite não especifica dente
                        WHEN procedimentos[idx] = 'Tratamento de gengivite' THEN 
                            NULL
                        -- Próteses para vários dentes
                        WHEN procedimentos[idx] = 'Prótese parcial removível' THEN 
                            NULL
                        ELSE
                            (FLOOR(RANDOM() * 4) + 1)::text || (FLOOR(RANDOM() * 8) + 1)::text
                    END,
                    materiais[idx],
                    status_trat,
                    -- Valores diferentes por procedimento
                    CASE 
                        WHEN procedimentos[idx] = 'Implante dentário' THEN 2500.00 + (RANDOM() * 1000)
                        WHEN procedimentos[idx] = 'Tratamento de canal' THEN 800.00 + (RANDOM() * 400)
                        WHEN procedimentos[idx] = 'Coroa protética' THEN 1200.00 + (RANDOM() * 500)
                        WHEN procedimentos[idx] = 'Raspagem e alisamento radicular' THEN 250.00 + (RANDOM() * 100)
                        WHEN procedimentos[idx] = 'Clareamento dental' THEN 1000.00 + (RANDOM() * 500)
                        WHEN procedimentos[idx] = 'Restauração de resina' THEN 180.00 + (RANDOM() * 120)
                        WHEN procedimentos[idx] = 'Aplicação de selante' THEN 80.00 + (RANDOM() * 40)
                        WHEN procedimentos[idx] = 'Exodontia de terceiro molar' THEN 500.00 + (RANDOM() * 300)
                        WHEN procedimentos[idx] = 'Tratamento de gengivite' THEN 200.00 + (RANDOM() * 100)
                        WHEN procedimentos[idx] = 'Prótese parcial removível' THEN 1800.00 + (RANDOM() * 700)
                        ELSE 100.00 + (RANDOM() * 150)
                    END,
                    data_trat
                ) RETURNING id INTO tratamento_id;
            END;
        END LOOP;
        
        -- Adicionar imagens radiológicas mais específicas para certos prontuários
        IF paciente_rec.nome IN ('Roberto Mendes', 'Vinícius Cardoso', 'Antônio Gomes', 'Matheus Alves', 'Lucas Moreira') THEN
            -- Insere radiografia panorâmica
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
                'panoramica_' || REPLACE(paciente_rec.nome, ' ', '_') || '.jpg',
                'Radiografia Panorâmica',
                CASE
                    WHEN paciente_rec.nome = 'Roberto Mendes' THEN 'Avaliação para tratamento de implante nos dentes 36 e 37'
                    WHEN paciente_rec.nome = 'Vinícius Cardoso' THEN 'Radiografia para avaliação de lesão periapical no dente 25'
                    WHEN paciente_rec.nome = 'Antônio Gomes' THEN 'Avaliação para prótese total mandibular sobre implantes'
                    WHEN paciente_rec.nome = 'Matheus Alves' THEN 'Avaliação de fratura no incisivo central após trauma'
                    WHEN paciente_rec.nome = 'Lucas Moreira' THEN 'Acompanhamento de erupção de dentes permanentes'
                    ELSE 'Radiografia panorâmica para avaliação geral'
                END,
                'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBxdWFsaXR5ID0gOTAK/9sAQwADAgIDAgIDAwMDBAMDBAUIBQUEBAUKBwcGCAwKDAwLCgsLDQ4SEA0OEQ4LCxAWEBETFBUVFQwPFxgWFBgSFBUU/9sAQwEDBAQFBAUJBQUJFA0LDRQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQU/8AAEQgAEAAQAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMEAQMDBAMAAAAAAAABAgMEEQUhMQYSQVFhByJxEzKBkaEII0KxwRQV0fAkM1KCkuHxIyRistHB0gTCU3Khh6K1/8QAGQEAAwEBAQAAAAAAAAAAAAAAAQIDAAQF/8QAKBEAAhEDAgUEAwEAAAAAAAAAAAECAxEhMQQSImFxkaEyQVKB0fDx/9oADAMBAAIRAxEAPwD5/vLUFmWIGMAAAAASUVORK5CYII=',
                'jpeg',
                (FLOOR(RANDOM() * 500) + 100) * 1024,
                hoje - (contador_condicao * INTERVAL '1 day')
            ) RETURNING id INTO imagem_id;
            
            -- Para alguns pacientes, adicionar também uma radiografia periapical
            IF paciente_rec.nome IN ('Vinícius Cardoso', 'Matheus Alves') THEN
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
                    'periapical_' || REPLACE(paciente_rec.nome, ' ', '_') || '.jpg',
                    'Radiografia Periapical',
                    CASE
                        WHEN paciente_rec.nome = 'Vinícius Cardoso' THEN 'Detalhamento da lesão periapical no dente 25'
                        WHEN paciente_rec.nome = 'Matheus Alves' THEN 'Detalhamento da fratura no incisivo central'
                        ELSE 'Radiografia periapical para avaliação detalhada'
                    END,
                    'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBxdWFsaXR5ID0gOTAK/9sAQwADAgIDAgIDAwMDBAMDBAUIBQUEBAUKBwcGCAwKDAwLCgsLDQ4SEA0OEQ4LCxAWEBETFBUVFQwPFxgWFBgSFBUU/9sAQwEDBAQFBAUJBQUJFA0LDRQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQU/8AAEQgAEAAQAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMEAQMDBAMAAAAAAAABAgMEEQUhMQYSQVFhByJxEzKBkaEII0KxwRQV0fAkM1KCkuHxIyRistHB0gTCU3Khh6K1/8QAGQEAAwEBAQAAAAAAAAAAAAAAAQIDAAQF/8QAKBEAAhEDAgUEAwEAAAAAAAAAAAECAxEhMQQSImFxkaEyQVKB0fDx/9oADAMBAAIRAxEAPwD5/vLUFmWIGMAAAAASUVORK5CYII=',
                    'jpeg',
                    (FLOOR(RANDOM() * 300) + 50) * 1024,
                    hoje - (contador_condicao * INTERVAL '1 day' + INTERVAL '2 hour')
                );
            END IF;
        END IF;
        
        contador_condicao := contador_condicao + 1;
    END LOOP;
END
$$;

-- Inserir alguns usuários dentistas adicionais
INSERT INTO usuario (email, nome, senha, role)
VALUES 
('felipe.almeida@caracore.com.br', 'Dr. Felipe Almeida', '$2a$10$ktLqQvVHpZl9woajQtomGe9sMOtcJRlRZGGNJZEYt3IJE4Hu5zRXC', 'ROLE_DENTIST'),
('gabriela.santos@caracore.com.br', 'Dra. Gabriela Santos', '$2a$10$ktLqQvVHpZl9woajQtomGe9sMOtcJRlRZGGNJZEYt3IJE4Hu5zRXC', 'ROLE_DENTIST'),
('henrique.costa@caracore.com.br', 'Dr. Henrique Costa', '$2a$10$ktLqQvVHpZl9woajQtomGe9sMOtcJRlRZGGNJZEYt3IJE4Hu5zRXC', 'ROLE_DENTIST')
ON CONFLICT (email) DO NOTHING;

-- Adicionar registros de evolução para alguns tratamentos
INSERT INTO evolucao_tratamento (registro_tratamento_id, data_evolucao, descricao, dentista_id)
SELECT 
    rt.id,
    rt.data_tratamento + INTERVAL '7 day',
    CASE 
        WHEN rt.procedimento = 'Implante dentário' THEN 'Implante osseointegrado com sucesso. Paciente sem queixas de dor ou desconforto.'
        WHEN rt.procedimento = 'Tratamento de canal' THEN 'Tratamento concluído. Radiografia periapical de controle apresenta boa resposta.'
        WHEN rt.procedimento = 'Coroa protética' THEN 'Coroa ajustada e cimentada. Oclusão verificada e ajustada.'
        WHEN rt.procedimento = 'Restauração de resina' THEN 'Restauração em bom estado. Contornos e pontos de contato adequados.'
        ELSE 'Evolução satisfatória. Paciente sem queixas.'
    END,
    rt.dentista_id
FROM registro_tratamento rt
WHERE rt.status = 'CONCLUIDO'
ORDER BY RANDOM()
LIMIT 10;

-- Adicionar alguns comentários nos prontuários
INSERT INTO comentario_prontuario (prontuario_id, dentista_id, texto, data_criacao)
SELECT 
    p.id,
    p.dentista_id,
    CASE 
        WHEN ROW_NUMBER() OVER () % 5 = 0 THEN 'Paciente extremamente colaborativo(a). Mantém boa higiene oral.'
        WHEN ROW_NUMBER() OVER () % 5 = 1 THEN 'Recomendado retorno em 6 meses para avaliação de rotina.'
        WHEN ROW_NUMBER() OVER () % 5 = 2 THEN 'Paciente relatou sensibilidade ao frio. Avaliar possível recessão gengival.'
        WHEN ROW_NUMBER() OVER () % 5 = 3 THEN 'Radiografias sugerem necessidade de avaliação ortodôntica.'
        ELSE 'Discutidas opções de tratamento para reabilitação oral. Paciente solicitou orçamento detalhado.'
    END,
    CURRENT_TIMESTAMP - (ROW_NUMBER() OVER () * INTERVAL '2 day')
FROM prontuario p
ORDER BY RANDOM()
LIMIT 15;

-- Registrar alguns exames complementares
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
