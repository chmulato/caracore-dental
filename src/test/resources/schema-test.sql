-- Script to initialize H2 database for tests with the same schema as PostgreSQL
-- This is automatically executed by Spring Boot for DataJpaTest


CREATE TABLE agendamento (
    id BIGINT IDENTITY PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL,
    paciente VARCHAR(255) NOT NULL,
    dentista VARCHAR(255) NOT NULL,
    observacao VARCHAR(1000),
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
    data_criacao TIMESTAMP,
    data_atualizacao TIMESTAMP,
    duracao_minutos INT,
    telefone_whatsapp VARCHAR(20),
    paciente_id BIGINT,
    profissional_id BIGINT,
    descricao VARCHAR(255)
);


CREATE TABLE usuario (
    id BIGINT IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);


CREATE TABLE paciente (
    id BIGINT IDENTITY PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    telefone VARCHAR(50)
);


CREATE TABLE profissional (
    id BIGINT IDENTITY PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    especialidade VARCHAR(255),
    email VARCHAR(255),
    telefone VARCHAR(50),
    cro VARCHAR(50),
    horario_inicio VARCHAR(10),
    horario_fim VARCHAR(10),
    ativo BOOLEAN,
    exposto_publicamente BOOLEAN
);


CREATE TABLE dentista (
    id BIGINT IDENTITY PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);
