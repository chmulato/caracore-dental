-- Script para atualizar as roles no banco de dados, adicionando o prefixo ROLE_ quando necessário
UPDATE usuario SET role = 'ROLE_ADMIN' WHERE role = 'ADMIN';
UPDATE usuario SET role = 'ROLE_DENTIST' WHERE role = 'DENTIST';
UPDATE usuario SET role = 'ROLE_RECEPTIONIST' WHERE role = 'RECEPTIONIST';
