-- Script para atualizar a senha do usuário Joao Maria para "admin123"
-- Data de criação: 01/07/2025

-- Senha em BCrypt: $2a$10$FTqrd6n.1eDvDUXLkDEQ/OjaePwNJC7DD8qDmrmctiXT3DmuY73QC
-- A senha em texto simples é: admin123

-- Atualiza a senha do usuário com email joao@gmail.com
UPDATE usuario 
SET senha = '$2a$10$FTqrd6n.1eDvDUXLkDEQ/OjaePwNJC7DD8qDmrmctiXT3DmuY73QC' 
WHERE email = 'joao@gmail.com';

-- Confirma que a atualização foi realizada
SELECT id, email, nome, role, LEFT(senha, 10) || '...' AS senha_parcial
FROM usuario
WHERE email = 'joao@gmail.com';
