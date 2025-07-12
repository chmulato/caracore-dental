-- V6__atualizar_senhas_padrao.sql: Define a senha padrão "admin123" para todos os perfis de usuário
-- Data de criação: 01/07/2025

-- Hash BCrypt para a senha "admin123"
-- $2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy

-- Atualiza a senha dos usuários para o valor padrão "admin123"
UPDATE usuario
SET senha = '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy'
WHERE email IN ('suporte@caracore.com.br', 'dentista@caracore.com.br', 'recepcao@caracore.com.br', 'joao@gmail.com');

-- Confirma que as atualizações foram realizadas
SELECT id, email, nome, role, LEFT(senha, 10) || '...' AS senha_parcial
FROM usuario
WHERE email IN ('suporte@caracore.com.br', 'dentista@caracore.com.br', 'recepcao@caracore.com.br', 'joao@gmail.com')
ORDER BY role;
