-- Script to initialize H2 database for tests with the same schema as PostgreSQL
-- This is automatically executed by Spring Boot for DataJpaTest

-- Create agendamento table
CREATE TABLE IF NOT EXISTS agendamento (
    id SERIAL PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL,
    paciente VARCHAR(255) NOT NULL,
    dentista VARCHAR(255) NOT NULL,
    procedimento VARCHAR(255),
    observacoes TEXT
);

-- Create usuario table
CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);
