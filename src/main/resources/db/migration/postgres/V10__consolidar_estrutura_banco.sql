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

-- Implementação compatível com PostgreSQL usando DO/BEGIN/END
DO $$ 
BEGIN
    -- Inserir ou atualizar suporte@caracore.com.br
    IF EXISTS (SELECT 1 FROM usuario WHERE email = 'suporte@caracore.com.br') THEN
        UPDATE usuario SET 
            nome = 'Administrador',
            senha = '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy',
            role = 'ROLE_ADMIN'
        WHERE email = 'suporte@caracore.com.br';
    ELSE
        INSERT INTO usuario (email, nome, senha, role)
        VALUES ('suporte@caracore.com.br', 'Administrador', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_ADMIN');
    END IF;

    -- Inserir ou atualizar dentista@caracore.com.br
    IF EXISTS (SELECT 1 FROM usuario WHERE email = 'dentista@caracore.com.br') THEN
        UPDATE usuario SET 
            nome = 'Dr. Carlos Silva',
            senha = '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy',
            role = 'ROLE_DENTIST'
        WHERE email = 'dentista@caracore.com.br';
    ELSE
        INSERT INTO usuario (email, nome, senha, role)
        VALUES ('dentista@caracore.com.br', 'Dr. Carlos Silva', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_DENTIST');
    END IF;

    -- Inserir ou atualizar recepcao@caracore.com.br
    IF EXISTS (SELECT 1 FROM usuario WHERE email = 'recepcao@caracore.com.br') THEN
        UPDATE usuario SET 
            nome = 'Ana Recepção',
            senha = '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy',
            role = 'ROLE_RECEPTIONIST'
        WHERE email = 'recepcao@caracore.com.br';
    ELSE
        INSERT INTO usuario (email, nome, senha, role)
        VALUES ('recepcao@caracore.com.br', 'Ana Recepção', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_RECEPTIONIST');
    END IF;
    
    -- Inserir ou atualizar paciente@caracore.com.br
    IF EXISTS (SELECT 1 FROM usuario WHERE email = 'paciente@caracore.com.br') THEN
        UPDATE usuario SET 
            nome = 'João Paciente',
            senha = '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy',
            role = 'ROLE_PATIENT'
        WHERE email = 'paciente@caracore.com.br';
    ELSE
        INSERT INTO usuario (email, nome, senha, role)
        VALUES ('paciente@caracore.com.br', 'João Paciente', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_PATIENT');
    END IF;
    
    -- Inserir ou atualizar joao@gmail.com
    IF EXISTS (SELECT 1 FROM usuario WHERE email = 'joao@gmail.com') THEN
        UPDATE usuario SET 
            nome = 'Joao Maria',
            senha = '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy',
            role = 'ROLE_PATIENT'
        WHERE email = 'joao@gmail.com';
    ELSE
        INSERT INTO usuario (email, nome, senha, role)
        VALUES ('joao@gmail.com', 'Joao Maria', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_PATIENT');
    END IF;
END $$;

-- 6. Criar tabela dentista se não existir (para compatibilidade com o novo modelo)
CREATE TABLE IF NOT EXISTS dentista (
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

-- Adiciona restrição UNIQUE para email apenas se não existir
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_dentista_email') THEN
        ALTER TABLE dentista ADD CONSTRAINT uk_dentista_email UNIQUE (email);
    END IF;
END $$;

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
