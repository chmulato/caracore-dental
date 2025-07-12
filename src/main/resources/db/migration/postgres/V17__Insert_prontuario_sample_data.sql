-- Dados de exemplo para prontuários
-- V17__Insert_prontuario_sample_data.sql

-- Primeiro vamos garantir que existem pacientes e dentistas no sistema
-- Verificar se já temos o paciente Joao Maria
DO $$
DECLARE
    joao_id INTEGER;
    dentista_id INTEGER;
BEGIN
    -- Tentar localizar o paciente João
    SELECT id INTO joao_id FROM paciente WHERE email = 'joao@gmail.com' LIMIT 1;
    
    -- Tentar localizar um dentista
    SELECT id INTO dentista_id FROM profissional WHERE email = 'dentista@caracore.com.br' LIMIT 1;
    
    -- Se não encontrarmos o João, não podemos inserir prontuário, então não fazemos nada
    IF joao_id IS NOT NULL AND dentista_id IS NOT NULL THEN
        -- Inserir prontuário para João
        INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais)
        VALUES 
        (joao_id, dentista_id, 'Paciente sem histórico de problemas odontológicos significativos. Última consulta há 6 meses.',
         'Alergia a ibuprofeno', 
         'Vitamina D3 - 1 comprimido por dia',
         'Paciente colaborativo, boa higiene oral');
         
        -- Inserir registros de tratamento para o João
        INSERT INTO registro_tratamento (prontuario_id, dentista_id, procedimento, descricao, material_utilizado, status, valor_procedimento)
        SELECT 
            (SELECT id FROM prontuario WHERE paciente_id = joao_id),
            dentista_id,
            'Limpeza e Profilaxia',
            'Remoção de tártaro e polimento dos dentes',
            'Pasta profilática, jato de bicarbonato',
            'CONCLUIDO',
            120.00;
            
        -- Inserir imagem radiológica de exemplo
        INSERT INTO imagem_radiologica (prontuario_id, dentista_id, nome_arquivo, tipo_imagem, descricao, imagem_base64, formato_arquivo, tamanho_arquivo, ativo)
        SELECT 
            (SELECT id FROM prontuario WHERE paciente_id = joao_id),
            dentista_id,
            'panoramica_joao_2025.jpg',
            'Radiografia Panorâmica',
            'Radiografia panorâmica de controle',
            'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBxdWFsaXR5ID0gOTAK/9sAQwADAgIDAgIDAwMDBAMDBAUIBQUEBAUKBwcGCAwKDAwLCgsLDQ4SEA0OEQ4LCxAWEBETFBUVFQwPFxgWFBgSFBUU/9sAQwEDBAQFBAUJBQUJFA0LDRQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQU/8AAEQgAEAAQAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMEAQMDBAMAAAAAAAABAgMEEQUhMQYSQVFhByJxEzKBkaEII0KxwRQV0fAkM1KCkuHxIyRistHB0gTCU3Khh6K1/8QAGQEAAwEBAQAAAAAAAAAAAAAAAQIDAAQF/8QAKBEAAhEDAgUEAwEAAAAAAAAAAAECAxEhMQQSImFxkaEyQVKB0fDx/9oADAMBAAIRAxEAPwD5/vLUFmWIGMAAAAASUVORK5CYII=',
            'jpeg',
            2048,
            true;
    END IF;
    
    -- Adicionamos paciente Ana para exemplo
    INSERT INTO paciente (nome, email, telefone, consentimento_confirmado, consentimento_lgpd)
    SELECT 'Ana Silva', 'ana@example.com', '(41) 98888-7777', true, true
    WHERE NOT EXISTS (SELECT 1 FROM paciente WHERE email = 'ana@example.com');
    
    -- Obtemos o ID da Ana
    SELECT id INTO joao_id FROM paciente WHERE email = 'ana@example.com' LIMIT 1;
    
    -- Se temos a Ana e um dentista, criamos outro prontuário
    IF joao_id IS NOT NULL AND dentista_id IS NOT NULL THEN
        -- Inserir prontuário para Ana
        INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais)
        VALUES 
        (joao_id, dentista_id, 'Histórico de cáries múltiplas. Tratamento de canal no dente 16 realizado em 2023.',
         'Nenhuma alergia conhecida', 
         'Anticoncepcional oral',
         'Recomendado uso de fio dental diário');
         
        -- Inserir registro de tratamento para Ana
        INSERT INTO registro_tratamento (prontuario_id, dentista_id, procedimento, descricao, dente, material_utilizado, status, valor_procedimento)
        SELECT 
            (SELECT id FROM prontuario WHERE paciente_id = joao_id),
            dentista_id,
            'Restauração',
            'Restauração de cárie em resina composta',
            '21',
            'Resina composta A2, ácido fosfórico',
            'CONCLUIDO',
            180.00;
    END IF;
END $$;
