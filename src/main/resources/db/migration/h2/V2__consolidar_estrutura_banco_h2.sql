-- V10__consolidar_estrutura_banco_h2.sql: Versão H2-compatível
-- Versão modificada para H2 em vez de PostgreSQL

-- Adicionando restrição única para email na tabela dentista usando sintaxe compatível com H2
ALTER TABLE dentista ADD CONSTRAINT IF NOT EXISTS uk_dentista_email UNIQUE (email);

-- Garante que as tabelas principais tenham todos os campos necessários
-- Nada a fazer aqui, pois o esquema inicial já contém todas as colunas necessárias

-- Inserir ou atualizar dados de usuários padrão
MERGE INTO usuario AS target
USING (VALUES 
    ('suporte@caracore.com.br', 'Administrador', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_ADMIN'),
    ('dentista@caracore.com.br', 'Dr. Carlos Silva', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_DENTIST'),
    ('recepcao@caracore.com.br', 'Ana Recepção', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_RECEPTIONIST'),
    ('paciente@caracore.com.br', 'João Paciente', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_PATIENT'),
    ('joao@gmail.com', 'Joao Maria', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_PATIENT')
) AS vals(email, nome, senha, role)
ON target.email = vals.email
WHEN MATCHED THEN UPDATE SET 
    target.nome = vals.nome, 
    target.senha = vals.senha, 
    target.role = vals.role
WHEN NOT MATCHED THEN INSERT 
    (email, nome, senha, role) 
    VALUES (vals.email, vals.nome, vals.senha, vals.role);

-- Migrar dados de profissional para dentista (se necessário)
INSERT INTO dentista (nome, email, telefone, cro, especialidade, horario_inicio, horario_fim, ativo)
SELECT nome, email, telefone, cro, especialidade, horario_inicio, horario_fim, ativo
FROM profissional
WHERE NOT EXISTS (SELECT 1 FROM dentista WHERE dentista.email = profissional.email);
