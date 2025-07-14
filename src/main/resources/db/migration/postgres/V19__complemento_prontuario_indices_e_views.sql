-- V19__complemento_prontuario_indices_e_views.sql
-- Script complementar ao V18, adicionando índices, views e ajustes nos dados
-- Data: 13/07/2025

-- 1. Verificação e correção de consistência de dados

-- Verifica se há prontuários sem registros de tratamento e adiciona pelo menos um registro padrão
DO $$
DECLARE
    prontuario_sem_tratamento RECORD;
    dentista_id INTEGER;
BEGIN
    FOR prontuario_sem_tratamento IN
        SELECT p.id, p.dentista_id 
        FROM prontuario p
        LEFT JOIN registro_tratamento rt ON p.id = rt.prontuario_id
        WHERE rt.id IS NULL
    LOOP
        dentista_id := prontuario_sem_tratamento.dentista_id;
        
        -- Insere um registro de tratamento inicial/padrão
        INSERT INTO registro_tratamento (prontuario_id, dentista_id, procedimento, descricao, material_utilizado, status, data_registro, valor_procedimento)
        VALUES (
            prontuario_sem_tratamento.id, 
            dentista_id, 
            'Avaliação Inicial', 
            'Primeira avaliação do paciente', 
            'N/A', 
            'CONCLUIDO', 
            CURRENT_TIMESTAMP, 
            0.00
        );
        
        RAISE NOTICE 'Adicionado registro de tratamento padrão para o prontuário %', prontuario_sem_tratamento.id;
    END LOOP;
END;
$$;

-- 2. Otimização: Adicionando índices para melhorar a performance de consultas comuns

-- Índice para busca de prontuários por nome de paciente (join comum)
CREATE INDEX IF NOT EXISTS idx_paciente_nome ON paciente (nome);

-- Índice para busca por data de tratamentos
CREATE INDEX IF NOT EXISTS idx_registro_tratamento_data ON registro_tratamento (data_registro);

-- Índice para busca de imagens por prontuário
CREATE INDEX IF NOT EXISTS idx_imagem_prontuario ON imagem_radiologica (prontuario_id);

-- Índice para busca por status de tratamento (comum em relatórios)
CREATE INDEX IF NOT EXISTS idx_registro_tratamento_status ON registro_tratamento (status);

-- Índice adicional para exames complementares
CREATE INDEX IF NOT EXISTS idx_exame_complementar_tipo_data ON exame_complementar (tipo_exame, data_exame);

-- 3. Criação de Views para facilitar consultas comuns

-- View para relatório completo de prontuário
CREATE OR REPLACE VIEW vw_prontuario_completo AS
SELECT 
    pr.id AS prontuario_id,
    pa.id AS paciente_id,
    pa.nome AS paciente_nome,
    pa.email AS paciente_email,
    pa.telefone AS paciente_telefone,
    pa.data_nascimento,
    pa.genero,
    d.id AS dentista_id,
    d.nome AS dentista_nome,
    d.especialidade AS dentista_especialidade,
    pr.historico_medico,
    pr.alergias,
    pr.medicamentos_uso,
    pr.observacoes_gerais,
    pr.data_criacao,
    pr.data_ultima_atualizacao
FROM 
    prontuario pr
    JOIN paciente pa ON pr.paciente_id = pa.id
    JOIN profissional d ON pr.dentista_id = d.id;

-- View para relatório de tratamentos
CREATE OR REPLACE VIEW vw_tratamentos_por_paciente AS
SELECT 
    pa.id AS paciente_id,
    pa.nome AS paciente_nome,
    pr.id AS prontuario_id,
    rt.id AS tratamento_id,
    rt.procedimento,
    rt.descricao,
    rt.material_utilizado,
    rt.status,
    rt.data_registro,
    rt.valor_procedimento,
    d.nome AS dentista_nome
FROM 
    paciente pa
    JOIN prontuario pr ON pa.id = pr.paciente_id
    JOIN registro_tratamento rt ON pr.id = rt.prontuario_id
    JOIN profissional d ON rt.dentista_id = d.id
ORDER BY 
    pa.nome, rt.data_registro DESC;

-- View para contagem e estatísticas de prontuários
CREATE OR REPLACE VIEW vw_estatisticas_prontuarios AS
SELECT
    d.nome AS dentista_nome,
    d.especialidade,
    COUNT(DISTINCT pr.id) AS total_prontuarios,
    COUNT(DISTINCT rt.id) AS total_tratamentos,
    COUNT(DISTINCT CASE WHEN rt.status = 'EM_ANDAMENTO' THEN rt.id END) AS tratamentos_em_andamento,
    COUNT(DISTINCT CASE WHEN rt.status = 'CONCLUIDO' THEN rt.id END) AS tratamentos_concluidos,
    COUNT(DISTINCT CASE WHEN rt.status = 'CANCELADO' THEN rt.id END) AS tratamentos_cancelados,
    COALESCE(SUM(rt.valor_procedimento), 0) AS valor_total_procedimentos
FROM
    profissional d
    LEFT JOIN prontuario pr ON d.id = pr.dentista_id
    LEFT JOIN registro_tratamento rt ON pr.id = rt.prontuario_id
GROUP BY
    d.id, d.nome, d.especialidade
ORDER BY
    total_prontuarios DESC;

-- 4. Função para facilitar busca de prontuários por critérios diversos
CREATE OR REPLACE FUNCTION buscar_prontuarios(
    p_nome_paciente VARCHAR DEFAULT NULL,
    p_dentista_id BIGINT DEFAULT NULL,
    p_procedimento VARCHAR DEFAULT NULL,
    p_data_inicio TIMESTAMP DEFAULT NULL,
    p_data_fim TIMESTAMP DEFAULT NULL,
    p_status VARCHAR DEFAULT NULL
)
RETURNS TABLE (
    prontuario_id BIGINT,
    paciente_id BIGINT,
    paciente_nome VARCHAR,
    dentista_id BIGINT,
    dentista_nome VARCHAR,
    data_criacao TIMESTAMP,
    procedimentos TEXT,
    total_tratamentos BIGINT,
    valor_total NUMERIC(10,2)
) AS $$
BEGIN
    RETURN QUERY
    WITH tratamentos_agrupados AS (
        SELECT 
            rt.prontuario_id,
            STRING_AGG(rt.procedimento, ', ') AS procedimentos,
            COUNT(rt.id) AS total_tratamentos,
            SUM(rt.valor_procedimento) AS valor_total
        FROM 
            registro_tratamento rt
        WHERE
            (p_procedimento IS NULL OR rt.procedimento ILIKE '%' || p_procedimento || '%') AND
            (p_data_inicio IS NULL OR rt.data_registro >= p_data_inicio) AND
            (p_data_fim IS NULL OR rt.data_registro <= p_data_fim) AND
            (p_status IS NULL OR rt.status = p_status)
        GROUP BY 
            rt.prontuario_id
    )
    SELECT 
        pr.id AS prontuario_id,
        pa.id AS paciente_id,
        pa.nome AS paciente_nome,
        d.id AS dentista_id,
        d.nome AS dentista_nome,
        pr.data_criacao,
        COALESCE(ta.procedimentos, 'Sem tratamentos') AS procedimentos,
        COALESCE(ta.total_tratamentos, 0) AS total_tratamentos,
        COALESCE(ta.valor_total, 0.00) AS valor_total
    FROM 
        prontuario pr
        JOIN paciente pa ON pr.paciente_id = pa.id
        JOIN profissional d ON pr.dentista_id = d.id
        LEFT JOIN tratamentos_agrupados ta ON pr.id = ta.prontuario_id
    WHERE
        (p_nome_paciente IS NULL OR pa.nome ILIKE '%' || p_nome_paciente || '%') AND
        (p_dentista_id IS NULL OR pr.dentista_id = p_dentista_id)
    ORDER BY
        pa.nome;
END;
$$ LANGUAGE plpgsql;

-- 5. Trigger para atualizar automaticamente a data_ultima_atualizacao do prontuário
CREATE OR REPLACE FUNCTION update_prontuario_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    -- Atualiza a data de última atualização do prontuário quando houver qualquer alteração nos tratamentos
    UPDATE prontuario
    SET data_ultima_atualizacao = CURRENT_TIMESTAMP
    WHERE id = NEW.prontuario_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicação do trigger para registro_tratamento
DROP TRIGGER IF EXISTS trigger_update_prontuario_timestamp ON registro_tratamento;
CREATE TRIGGER trigger_update_prontuario_timestamp
AFTER INSERT OR UPDATE ON registro_tratamento
FOR EACH ROW
EXECUTE FUNCTION update_prontuario_timestamp();

-- Aplicação do trigger para imagem_radiologica
DROP TRIGGER IF EXISTS trigger_update_prontuario_timestamp_imagem ON imagem_radiologica;
CREATE TRIGGER trigger_update_prontuario_timestamp_imagem
AFTER INSERT OR UPDATE ON imagem_radiologica
FOR EACH ROW
EXECUTE FUNCTION update_prontuario_timestamp();

-- 6. Adicionar enum para tipos comuns de procedimentos (para padronização)
DO $$
BEGIN
    -- Verificar se o tipo já existe antes de criar
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tipo_procedimento') THEN
        CREATE TYPE tipo_procedimento AS ENUM (
            'AVALIACAO_INICIAL',
            'LIMPEZA_PROFILAXIA',
            'RESTAURACAO',
            'EXTRACAO',
            'ENDODONTIA',
            'PERIODONTIA',
            'PROTESE_FIXA',
            'PROTESE_REMOVIVEL',
            'IMPLANTE',
            'ORTODONTIA',
            'CIRURGIA_BUCOMAXILOFACIAL',
            'CLAREAMENTO',
            'RADIOGRAFIA',
            'TOMOGRAFIA',
            'CONSULTA_RETORNO'
        );
        RAISE NOTICE 'Tipo ENUM tipo_procedimento criado com sucesso';
    ELSE
        RAISE NOTICE 'Tipo ENUM tipo_procedimento já existe, não será recriado';
    END IF;
END
$$;

-- 7. Adicionar comentários às tabelas para documentação
COMMENT ON TABLE prontuario IS 'Registro principal do prontuário do paciente com informações gerais de saúde';
COMMENT ON TABLE registro_tratamento IS 'Registros de procedimentos e tratamentos realizados no paciente';
COMMENT ON TABLE imagem_radiologica IS 'Imagens radiológicas e de exames odontológicos do paciente';
COMMENT ON TABLE exame_complementar IS 'Exames complementares realizados como parte do tratamento';
COMMENT ON VIEW vw_prontuario_completo IS 'Visão consolidada com dados completos do prontuário, paciente e dentista';
COMMENT ON VIEW vw_tratamentos_por_paciente IS 'Relatório de todos os tratamentos agrupados por paciente';
COMMENT ON VIEW vw_estatisticas_prontuarios IS 'Estatísticas de prontuários e tratamentos por dentista';
COMMENT ON FUNCTION buscar_prontuarios IS 'Função para busca flexível de prontuários por múltiplos critérios';

-- 8. Melhoria: Adicionar coluna para classificação de risco/urgência nos tratamentos
ALTER TABLE registro_tratamento ADD COLUMN IF NOT EXISTS nivel_urgencia VARCHAR(20) DEFAULT 'NORMAL';
COMMENT ON COLUMN registro_tratamento.nivel_urgencia IS 'Nível de urgência do tratamento: EMERGENCIA, URGENTE, PRIORITARIO, NORMAL, MANUTENCAO';

-- Atualiza valores para os registros existentes
UPDATE registro_tratamento
SET nivel_urgencia = CASE
    WHEN procedimento ILIKE '%emergência%' OR procedimento ILIKE '%emergencia%' OR procedimento ILIKE '%urgente%' OR procedimento ILIKE '%dor%' THEN 'URGENTE'
    WHEN procedimento ILIKE '%cirurgia%' OR procedimento ILIKE '%extração%' OR procedimento ILIKE '%extracao%' THEN 'PRIORITARIO'
    WHEN procedimento ILIKE '%controle%' OR procedimento ILIKE '%manutenção%' OR procedimento ILIKE '%manutencao%' OR procedimento ILIKE '%rotina%' THEN 'MANUTENCAO'
    ELSE 'NORMAL'
END;
