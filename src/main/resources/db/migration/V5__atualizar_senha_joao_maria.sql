-- V5__atualizar_senha_joao_maria.sql: Atualiza a senha do paciente Joao Maria para "admin123"

-- Atualiza a senha do usuário com email joao@gmail.com
UPDATE usuario 
SET senha = '$2a$10$FTqrd6n.1eDvDUXLkDEQ/OjaePwNJC7DD8qDmrmctiXT3DmuY73QC' 
WHERE email = 'joao@gmail.com';
