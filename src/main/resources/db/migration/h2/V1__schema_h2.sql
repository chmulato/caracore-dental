-- V1__schema_h2.sql: Script de criação de tabelas para H2

-- Tabelas principais
CREATE TABLE IF NOT EXISTS usuario (
    id IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS paciente (
    id IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefone VARCHAR(20),
    data_nascimento DATE,
    genero VARCHAR(20),
    nome_social VARCHAR(100),
    aceite_lgpd BOOLEAN DEFAULT FALSE,
    data_aceite_lgpd TIMESTAMP
);

CREATE TABLE IF NOT EXISTS profissional (
    id IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    especialidade VARCHAR(100),
    email VARCHAR(100),
    telefone VARCHAR(20),
    cro VARCHAR(20),
    horario_inicio VARCHAR(5),
    horario_fim VARCHAR(5),
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS dentista (
    id IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    telefone VARCHAR(20),
    cro VARCHAR(20),
    especialidade VARCHAR(100),
    horario_inicio VARCHAR(5),
    horario_fim VARCHAR(5),
    ativo BOOLEAN DEFAULT TRUE,
    exposicao_publica BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);

CREATE TABLE IF NOT EXISTS agendamento (
    id IDENTITY PRIMARY KEY,
    paciente_id INT REFERENCES paciente(id),
    profissional_id INT REFERENCES profissional(id),
    data_hora TIMESTAMP NOT NULL,
    descricao VARCHAR(255),
    paciente VARCHAR(255),
    dentista VARCHAR(255),
    status VARCHAR(20) DEFAULT 'AGENDADO',
    observacao TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    via_plataforma BOOLEAN DEFAULT FALSE
);

-- Tabela de prontuários
CREATE TABLE IF NOT EXISTS prontuario (
    id IDENTITY PRIMARY KEY,
    paciente_id BIGINT NOT NULL UNIQUE,
    dentista_id BIGINT NOT NULL,
    historico_medico TEXT,
    alergias TEXT,
    medicamentos_uso TEXT,
    observacoes_gerais TEXT,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    data_ultima_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    
    CONSTRAINT fk_prontuario_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id),
    CONSTRAINT fk_prontuario_dentista FOREIGN KEY (dentista_id) REFERENCES dentista(id)
);

-- Tabela de imagens radiológicas
CREATE TABLE IF NOT EXISTS imagem_radiologica (
    id IDENTITY PRIMARY KEY,
    prontuario_id BIGINT NOT NULL,
    dentista_id BIGINT NOT NULL,
    nome_arquivo VARCHAR(255) NOT NULL,
    tipo_imagem VARCHAR(100) NOT NULL,
    descricao TEXT,
    imagem_base64 TEXT NOT NULL,
    formato_arquivo VARCHAR(10) NOT NULL,
    tamanho_arquivo BIGINT,
    data_upload TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT fk_imagem_prontuario FOREIGN KEY (prontuario_id) REFERENCES prontuario(id),
    CONSTRAINT fk_imagem_dentista FOREIGN KEY (dentista_id) REFERENCES dentista(id)
);

-- Tabela de registros de tratamento
CREATE TABLE IF NOT EXISTS registro_tratamento (
    id IDENTITY PRIMARY KEY,
    prontuario_id BIGINT NOT NULL,
    dentista_id BIGINT NOT NULL,
    data_tratamento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    procedimento VARCHAR(255) NOT NULL,
    descricao TEXT,
    dente VARCHAR(50),
    material_utilizado TEXT,
    observacoes TEXT,
    valor_procedimento DECIMAL(10,2),
    status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    
    CONSTRAINT fk_registro_prontuario FOREIGN KEY (prontuario_id) REFERENCES prontuario(id),
    CONSTRAINT fk_registro_dentista FOREIGN KEY (dentista_id) REFERENCES dentista(id)
);

-- Inserir dados iniciais necessários
INSERT INTO usuario (nome, email, senha, role)
VALUES 
('Administrador', 'suporte@caracore.com.br', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_ADMIN'),
('Dr. Carlos Silva', 'dentista@caracore.com.br', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_DENTIST'),
('Ana Recepção', 'recepcao@caracore.com.br', '$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy', 'ROLE_RECEPTIONIST');

-- Inserir dentistas
INSERT INTO dentista (nome, email, telefone, cro, especialidade, horario_inicio, horario_fim, ativo)
VALUES 
('Dr. Carlos Silva', 'dentista@caracore.com.br', '(41) 99999-1234', 'CRO-PR 12345', 'Clínico Geral', '08:00', '18:00', TRUE),
('Dra. Maria Santos', 'maria.dentista@example.com', '(41) 98765-4321', 'CRO-PR 54321', 'Ortodontia', '09:00', '17:00', TRUE);

-- Inserir pacientes
INSERT INTO paciente (nome, email, telefone)
VALUES 
('Joao Maria', 'joao@gmail.com', '(41) 99909-7797'),
('Ana Silva', 'ana@example.com', '(41) 98888-7777'),
('Pedro Santos', 'pedro@example.com', '(41) 97777-6666');

-- Inserir prontuário de exemplo
INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais)
VALUES
(1, 1, 'Paciente sem histórico de problemas odontológicos significativos.', 'Alergia a ibuprofeno', 'Vitamina D3', 'Paciente colaborativo, boa higiene oral');

-- Inserir registro de tratamento de exemplo
INSERT INTO registro_tratamento (prontuario_id, dentista_id, procedimento, descricao, status, valor_procedimento)
VALUES
(1, 1, 'Limpeza e Profilaxia', 'Remoção de tártaro e polimento dos dentes', 'CONCLUIDO', 120.00);
