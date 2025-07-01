-- Script para criar os usuários padrão para todos os perfis
-- Senha criptografada para "senha123": $2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy
-- Senha para admin123 é diferente para o administrador

-- Usuário ADMIN já criado no V3, apenas garantir que existe
INSERT INTO usuario (email, nome, senha, role)
VALUES ('suporte@caracore.com.br', 'Administrador', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_ADMIN')
ON CONFLICT (email) DO NOTHING;

-- Usuário DENTIST
INSERT INTO usuario (email, nome, senha, role)
VALUES ('dentista@teste.com', 'Dr. Carlos Silva', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_DENTIST')
ON CONFLICT (email) DO NOTHING;

-- Usuário RECEPTIONIST
INSERT INTO usuario (email, nome, senha, role)
VALUES ('recepcao@teste.com', 'Ana Recepção', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_RECEPTIONIST')
ON CONFLICT (email) DO NOTHING;

-- Usuário PATIENT
INSERT INTO usuario (email, nome, senha, role)
VALUES ('paciente@teste.com', 'João Paciente', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_PATIENT')
ON CONFLICT (email) DO NOTHING;
