-- Dados de exemplo para prontuários
-- V17__Insert_prontuario_sample_data.sql

-- Inserir alguns prontuários de exemplo (assumindo que já temos pacientes e dentistas)
INSERT INTO prontuario (paciente_id, dentista_id, historico_medico, alergias, medicamentos_uso, observacoes_gerais) 
VALUES 
(1, 1, 'Paciente sem histórico de problemas odontológicos significativos. Última consulta há 6 meses.', 
 'Alergia a ibuprofeno', 
 'Vitamina D3 - 1 comprimido por dia',
 'Paciente colaborativo, boa higiene oral'),

(2, 1, 'Histórico de cáries múltiplas. Tratamento de canal no dente 16 realizado em 2023.', 
 'Nenhuma alergia conhecida', 
 'Anticoncepcional oral',
 'Recomendado uso de fio dental diário'),

(3, 2, 'Paciente com bruxismo noturno. Usa placa de proteção.', 
 'Alergia a penicilina', 
 'Relaxante muscular - uso conforme necessário',
 'Necessário acompanhamento do desgaste dental');

-- Inserir alguns registros de tratamento de exemplo
INSERT INTO registro_tratamento (prontuario_id, dentista_id, procedimento, descricao, dente, material_utilizado, status, valor_procedimento)
VALUES 
(1, 1, 'Limpeza e Profilaxia', 'Remoção de tártaro e polimento dos dentes', NULL, 'Pasta profilática, jato de bicarbonato', 'CONCLUIDO', 120.00),
(1, 1, 'Aplicação de Flúor', 'Aplicação tópica de flúor para fortalecimento do esmalte', NULL, 'Gel de flúor neutro', 'CONCLUIDO', 50.00),

(2, 1, 'Restauração', 'Restauração de cárie em resina composta', '21', 'Resina composta A2, ácido fosfórico', 'CONCLUIDO', 180.00),
(2, 1, 'Consulta de Retorno', 'Avaliação pós-restauração', '21', NULL, 'CONCLUIDO', 0.00),

(3, 2, 'Ajuste de Placa Miorrelaxante', 'Ajuste da placa de proteção para bruxismo', NULL, 'Disco de lixa, brocas', 'CONCLUIDO', 80.00),
(3, 2, 'Consulta Preventiva', 'Consulta de acompanhamento do tratamento de bruxismo', NULL, NULL, 'EM_ANDAMENTO', 100.00);

-- Inserir alguns exemplos de tipos de imagens radiológicas comuns
-- (As imagens Base64 reais seriam muito grandes, então usaremos exemplos pequenos)
INSERT INTO imagem_radiologica (prontuario_id, dentista_id, nome_arquivo, tipo_imagem, descricao, imagem_base64, formato_arquivo, tamanho_arquivo, ativo)
VALUES 
(1, 1, 'panoramica_2024_01.jpg', 'Radiografia Panorâmica', 'Radiografia panorâmica de controle', 
 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBxdWFsaXR5ID0gOTAK/9sAQwADAgIDAgIDAwMDBAMDBAUIBQUEBAUKBwcGCAwKDAwLCgsLDQ4SEA0OEQ4LCxAWEBETFBUVFQwPFxgWFBgSFBUU/9sAQwEDBAQFBAUJBQUJFA0LDRQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQU/8AAEQgAEAAQAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMEAQMDBAMAAAAAAAABAgMEEQUhMQYSQVFhByJxEzKBkaEII0KxwRQV0fAkM1KCkuHxIyRistHB0gTCU3Khh6K1/8QAGQEAAwEBAQAAAAAAAAAAAAAAAQIDAAQF/8QAKBEAAhEDAgUEAwEAAAAAAAAAAAECAxEhMQQSImFxkaEyQVKB0fDx/9oADAMBAAIRAxEAPwD5/vLUFmWIGMAAAAASUVORK5CYII=', 
 'jpeg', 2048, true),

(2, 1, 'periapical_21.jpg', 'Radiografia Periapical', 'Periapical do dente 21 pré-restauração', 
 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBxdWFsaXR5ID0gOTAK/9sAQwADAgIDAgIDAwMDBAMDBAUIBQUEBAUKBwcGCAwKDAwLCgsLDQ4SEA0OEQ4LCxAWEBETFBUVFQwPFxgWFBgSFBUU/9sAQwEDBAQFBAUJBQUJFA0LDRQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQU/8AAEQgAEAAQAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMEAQMDBAMAAAAAAAABAgMEEQUhMQYSQVFhByJxEzKBkaEII0KxwRQV0fAkM1KCkuHxIyRistHB0gTCU3Khh6K1/8QAGQEAAwEBAQAAAAAAAAAAAAAAAQIDAAQF/8QAKBEAAhEDAgUEAwEAAAAAAAAAAAECAxEhMQQSImFxkaEyQVKB0fDx/9oADAMBAAIRAxEAPwD5/vLUFmWIGMAAAAASUVORK5CYII=', 
 'jpeg', 1856, true);

-- Inserir comentários informativos
INSERT INTO registro_tratamento (prontuario_id, dentista_id, procedimento, descricao, observacoes, status, valor_procedimento)
VALUES 
(1, 1, 'Orientação de Higiene', 'Orientações sobre técnica de escovação e uso do fio dental', 
 'Paciente demonstrou boa compreensão das orientações. Recomendado retorno em 6 meses.', 'CONCLUIDO', 0.00),

(2, 1, 'Plano de Tratamento', 'Elaboração de plano de tratamento preventivo', 
 'Estabelecido cronograma de 3 consultas para completar o tratamento.', 'PLANEJADO', 0.00);
