CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Usuário inicial com a senha "admin123" (BCrypt)
-- Usando MERGE em vez de ON CONFLICT para compatibilidade com H2
MERGE INTO usuario (email, nome, senha, role)
KEY (email)
VALUES ('suporte@caracore.com.br', 'Administrador', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_ADMIN');
