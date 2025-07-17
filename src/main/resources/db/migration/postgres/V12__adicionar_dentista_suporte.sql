-- Script para adicionar usuário suporte como dentista
INSERT INTO dentista (nome, email, telefone, cro, especialidade, horario_inicio, horario_fim, ativo)
SELECT 'Administrador Suporte', 'suporte@caracore.com.br', '(11) 99999-9999', 'CRO-SP-123456', 'Clínico Geral', '08:00', '18:00', true
WHERE NOT EXISTS (SELECT 1 FROM dentista WHERE email = 'suporte@caracore.com.br');
