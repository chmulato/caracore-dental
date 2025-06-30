-- V1__inicial.sql: Criação das tabelas principais do sistema

CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS paciente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS profissional (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    especialidade VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS agendamento (
    id SERIAL PRIMARY KEY,
    paciente_id INTEGER REFERENCES paciente(id),
    profissional_id INTEGER REFERENCES profissional(id),
    data_hora TIMESTAMP NOT NULL,
    descricao VARCHAR(255),
    paciente VARCHAR(255),
    dentista VARCHAR(255)
);
