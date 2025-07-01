-- V4__adicionar_paciente_joao_maria.sql: Adiciona um novo paciente - Joao Maria

-- Insere um novo paciente chamado "Joao Maria"
INSERT INTO paciente (nome, email, telefone)
VALUES ('Joao Maria', 'joao@gmail.com', '005541999097797');

-- Nota: Pacientes não possuem senha no modelo atual, pois são gerenciados por usuários do sistema
-- Se for necessário criar um usuário para este paciente:
INSERT INTO usuario (email, nome, senha, role)
VALUES ('joao@gmail.com', 'Joao Maria', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_PATIENT')
ON CONFLICT (email) DO NOTHING;
