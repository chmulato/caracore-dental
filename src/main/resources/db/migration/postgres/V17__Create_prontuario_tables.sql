-- Migration para criar tabela de prontuários
-- V16__Create_prontuario_tables.sql

-- Tabela de prontuários
CREATE TABLE prontuario (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL UNIQUE,
    dentista_id BIGINT NOT NULL,
    historico_medico TEXT,
    alergias TEXT,
    medicamentos_uso TEXT,
    observacoes_gerais TEXT,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_ultima_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_prontuario_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id),
    CONSTRAINT fk_prontuario_dentista FOREIGN KEY (dentista_id) REFERENCES profissional(id)
);

-- Tabela de imagens radiológicas
CREATE TABLE imagem_radiologica (
    id BIGSERIAL PRIMARY KEY,
    prontuario_id BIGINT NOT NULL,
    dentista_id BIGINT NOT NULL,
    nome_arquivo VARCHAR(255) NOT NULL,
    tipo_imagem VARCHAR(100) NOT NULL,
    descricao TEXT,
    imagem_base64 TEXT NOT NULL,
    formato_arquivo VARCHAR(10) NOT NULL,
    tamanho_arquivo BIGINT,
    data_upload TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT fk_imagem_prontuario FOREIGN KEY (prontuario_id) REFERENCES prontuario(id),
    CONSTRAINT fk_imagem_dentista FOREIGN KEY (dentista_id) REFERENCES profissional(id)
);

-- Tabela de registros de tratamento
CREATE TABLE registro_tratamento (
    id BIGSERIAL PRIMARY KEY,
    prontuario_id BIGINT NOT NULL,
    dentista_id BIGINT NOT NULL,
    data_tratamento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    procedimento VARCHAR(255) NOT NULL,
    descricao TEXT,
    dente VARCHAR(50),
    material_utilizado TEXT,
    observacoes TEXT,
    valor_procedimento DECIMAL(10,2),
    status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_registro_prontuario FOREIGN KEY (prontuario_id) REFERENCES prontuario(id),
    CONSTRAINT fk_registro_dentista FOREIGN KEY (dentista_id) REFERENCES profissional(id),
    CONSTRAINT chk_status_tratamento CHECK (status IN ('PLANEJADO', 'EM_ANDAMENTO', 'CONCLUIDO', 'CANCELADO', 'ADIADO'))
);

-- Tabela de exames complementares
CREATE TABLE exame_complementar (
    id BIGSERIAL PRIMARY KEY,
    prontuario_id BIGINT NOT NULL,
    dentista_id BIGINT NOT NULL,
    tipo_exame VARCHAR(100) NOT NULL,
    data_exame TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resultado TEXT,
    observacoes TEXT,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_exame_prontuario FOREIGN KEY (prontuario_id) REFERENCES prontuario(id),
    CONSTRAINT fk_exame_dentista FOREIGN KEY (dentista_id) REFERENCES profissional(id)
);

-- Índices para melhor performance
CREATE INDEX idx_prontuario_paciente ON prontuario(paciente_id);
CREATE INDEX idx_prontuario_dentista ON prontuario(dentista_id);
CREATE INDEX idx_prontuario_data_atualizacao ON prontuario(data_ultima_atualizacao);

CREATE INDEX idx_imagem_prontuario ON imagem_radiologica(prontuario_id);
CREATE INDEX idx_imagem_dentista ON imagem_radiologica(dentista_id);
CREATE INDEX idx_imagem_ativo ON imagem_radiologica(ativo);
CREATE INDEX idx_imagem_tipo ON imagem_radiologica(tipo_imagem);
CREATE INDEX idx_imagem_data_upload ON imagem_radiologica(data_upload);

CREATE INDEX idx_registro_prontuario ON registro_tratamento(prontuario_id);
CREATE INDEX idx_registro_dentista ON registro_tratamento(dentista_id);
CREATE INDEX idx_registro_status ON registro_tratamento(status);
CREATE INDEX idx_registro_data_tratamento ON registro_tratamento(data_tratamento);
CREATE INDEX idx_registro_procedimento ON registro_tratamento(procedimento);

CREATE INDEX idx_exame_prontuario ON exame_complementar(prontuario_id);
CREATE INDEX idx_exame_dentista ON exame_complementar(dentista_id);
CREATE INDEX idx_exame_tipo ON exame_complementar(tipo_exame);
CREATE INDEX idx_exame_data ON exame_complementar(data_exame);

-- Comentários para documentação
COMMENT ON TABLE prontuario IS 'Prontuário odontológico dos pacientes';
COMMENT ON TABLE imagem_radiologica IS 'Imagens radiológicas armazenadas em Base64';
COMMENT ON TABLE registro_tratamento IS 'Histórico de tratamentos e procedimentos realizados';
COMMENT ON TABLE exame_complementar IS 'Exames complementares realizados como parte do tratamento';

COMMENT ON COLUMN imagem_radiologica.imagem_base64 IS 'Imagem codificada em Base64 para armazenamento no banco';
COMMENT ON COLUMN imagem_radiologica.tamanho_arquivo IS 'Tamanho aproximado do arquivo em bytes';
COMMENT ON COLUMN registro_tratamento.dente IS 'Numeração do dente conforme padrão odontológico (ex: 16, 21, 46)';
COMMENT ON COLUMN registro_tratamento.status IS 'Status do tratamento: PLANEJADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO, ADIADO';
COMMENT ON COLUMN exame_complementar.tipo_exame IS 'Tipo de exame: Tomografia, Radiografia, Exame de Saliva, etc.';
COMMENT ON COLUMN exame_complementar.resultado IS 'Resultado textual do exame complementar';
