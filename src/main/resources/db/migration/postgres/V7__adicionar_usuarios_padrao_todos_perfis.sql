-- Script para criar os usuários padrão para todos os perfis
-- Hash BCrypt para "admin123": $2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy

-- Usuário ADMIN já criado no V3, apenas garantir que existe
INSERT INTO usuario (email, nome, senha, role)
SELECT 'suporte@caracore.com.br', 'Administrador', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'suporte@caracore.com.br');

-- Usuário DENTIST
INSERT INTO usuario (email, nome, senha, role)
SELECT 'dentista@caracore.com.br', 'Dr. Carlos Silva', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_DENTIST'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'dentista@caracore.com.br');

-- Usuário RECEPTIONIST
INSERT INTO usuario (email, nome, senha, role)
SELECT 'recepcao@caracore.com.br', 'Ana Recepção', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_RECEPTIONIST'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'recepcao@caracore.com.br');

-- Usuário PATIENT
INSERT INTO usuario (email, nome, senha, role)
SELECT 'paciente@caracore.com.br', 'João Paciente', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_PATIENT'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'paciente@caracore.com.br');
