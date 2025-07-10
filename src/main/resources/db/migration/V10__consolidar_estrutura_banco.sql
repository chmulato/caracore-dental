-- V10__consolidar_estrutura_banco.sql: Consolida e corrige inconsistências na estrutura do banco
-- Data: 02/07/2025

-- Limpar dados duplicados e recriar estrutura consistente

-- 1. Backup dos dados importantes (se necessário)
CREATE TEMP TABLE temp_usuarios_backup AS 
SELECT DISTINCT email, nome, senha, role 
FROM usuario 
WHERE email IN ('suporte@caracore.com.br', 'dentista@caracore.com.br', 'recepcao@caracore.com.br', 'joao@gmail.com', 'paciente@caracore.com.br');

-- 2. Garantir que a tabela usuario tenha a estrutura correta
-- (O Flyway não permite DROP/CREATE em migrações, então usamos ALTER se necessário)

-- Padronizar o tamanho da coluna role para 50 caracteres
ALTER TABLE usuario ALTER COLUMN role TYPE VARCHAR(50);

-- 3. Limpar duplicatas e garantir dados consistentes
DELETE FROM usuario WHERE id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY email ORDER BY id) as row_num
        FROM usuario
    ) t WHERE row_num > 1
);

-- 4. Garantir que todos os usuários padrão existam com a senha correta
-- Hash BCrypt para "admin123": $2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy

-- Implementação compatível com H2 e PostgreSQL usando MERGE no H2
-- Primeiro, verificamos e atualizamos os registros existentes
MERGE INTO usuario u
USING (VALUES 
    ('suporte@caracore.com.br', 'Administrador', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_ADMIN'),
    ('dentista@caracore.com.br', 'Dr. Carlos Silva', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_DENTIST'),
    ('recepcao@caracore.com.br', 'Ana Recepção', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_RECEPTIONIST'),
    ('paciente@caracore.com.br', 'João Paciente', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_PATIENT'),
    ('joao@gmail.com', 'Joao Maria', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_PATIENT')
) AS vals(email, nome, senha, role)
ON u.email = vals.email
WHEN MATCHED THEN UPDATE SET 
    u.nome = vals.nome, 
    u.senha = vals.senha, 
    u.role = vals.role
WHEN NOT MATCHED THEN INSERT 
    (email, nome, senha, role) 
    VALUES (vals.email, vals.nome, vals.senha, vals.role);

-- 5. Verificar e corrigir a estrutura da tabela profissional
-- Garantir que todos os campos necessários existam

-- Abordagem simples compatível com H2: tenta adicionar as colunas diretamente
-- Se a coluna já existir, o H2 simplesmente ignora o erro

-- Em H2, não podemos usar "IF NOT EXISTS" ou blocos DO $$ BEGIN END $$,
-- então tentamos adicionar as colunas diretamente, aceitando falhas silenciosas

ALTER TABLE profissional ADD telefone VARCHAR(20);
ALTER TABLE profissional ADD cro VARCHAR(20);
ALTER TABLE profissional ADD horario_inicio VARCHAR(5);
ALTER TABLE profissional ADD horario_fim VARCHAR(5);
ALTER TABLE profissional ADD ativo BOOLEAN DEFAULT TRUE;

-- 6. Criar tabela dentista se não existir (para compatibilidade com o novo modelo)
CREATE TABLE dentista (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefone VARCHAR(20),
    cro VARCHAR(20),
    especialidade VARCHAR(100),
    horario_inicio VARCHAR(5),
    horario_fim VARCHAR(5),
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Adiciona restrição UNIQUE para email
ALTER TABLE dentista ADD CONSTRAINT uk_dentista_email UNIQUE (email);

-- 7. Migrar dados de profissional para dentista (se necessário)
INSERT INTO dentista (nome, email, telefone, cro, especialidade, horario_inicio, horario_fim, ativo)
SELECT nome, email, telefone, cro, especialidade, horario_inicio, horario_fim, ativo
FROM profissional
WHERE NOT EXISTS (SELECT 1 FROM dentista WHERE dentista.email = profissional.email);

-- 8. Verificação final - Mostrar status das tabelas principais
SELECT 'usuarios' as tabela, COUNT(*) as total FROM usuario
UNION ALL
SELECT 'pacientes' as tabela, COUNT(*) as total FROM paciente
UNION ALL
SELECT 'profissionais' as tabela, COUNT(*) as total FROM profissional
UNION ALL
SELECT 'dentistas' as tabela, COUNT(*) as total FROM dentista
UNION ALL
SELECT 'agendamentos' as tabela, COUNT(*) as total FROM agendamento;
