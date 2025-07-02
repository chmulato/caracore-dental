# Análise do Versionamento dos Scripts do Banco de Dados

## Estado Atual (02/07/2025)

### Scripts de Migração Flyway Identificados:
1. `V1__inicial.sql` - Criação das tabelas principais
2. `V2__dados_iniciais.sql` - Inserção de dados iniciais para MVP
3. `V3__criar_tabela_usuario.sql` - Criação da tabela usuario (DUPLICADA)
4. `V4__adicionar_paciente_joao_maria.sql` - Adiciona paciente específico
5. `V5__atualizar_senha_joao_maria.sql` - Atualiza senha do paciente
6. `V6__atualizar_senhas_padrao.sql` - Define senha padrão para todos
7. `V7__adicionar_usuarios_padrao_todos_perfis.sql` - Cria usuários padrão (DUPLICADO)
8. `V8__adicionar_campos_profissionais.sql` - Adiciona campos na tabela profissional
9. `V9__adicionar_dentistas_exemplo.sql` - Adiciona dados de dentistas exemplo
10. `V10__consolidar_estrutura_banco.sql` - **NOVO** - Consolida e corrige inconsistências

## Problemas Identificados:

### 1. **Duplicação de Estruturas**

- **V1** e **V3**: Ambos criam a tabela `usuario`
- **V2** e **V7**: Ambos inserem dados de usuários
- Uso inconsistente de `CREATE TABLE` vs `CREATE TABLE IF NOT EXISTS`

### 2. **Inconsistências de Schema**

- Tamanhos diferentes para a coluna `role`:
  - V1: `VARCHAR(30)`
  - V3: `VARCHAR(50)`
- Estruturas de tabelas podem diferir entre V1 e V3

### 3. **Gestão de Dados**

- Múltiplas inserções dos mesmos usuários
- Falta de controle de duplicatas
- Possíveis conflitos de chaves primárias

### 4. **Versionamento Semântico**

- Falta de padrão claro na nomenclatura
- Alguns scripts fazem múltiplas operações (estrutura + dados)

## Soluções Implementadas em V10:

### ✅ **Consolidação da Estrutura**

- Padronização da coluna `role` para `VARCHAR(50)`
- Remoção de registros duplicados
- Criação da tabela `dentista` com estrutura completa

### ✅ **Limpeza de Dados**
- Remoção de duplicatas usando ROW_NUMBER()
- Uso de `ON CONFLICT DO UPDATE` para evitar duplicações futuras
- Migração de dados de `profissional` para `dentista`

### ✅ **Verificação de Integridade**
- Query final para verificar contagem de registros
- Estrutura consistente entre todas as tabelas

## Recomendações para o Futuro:

### 1. **Padrão de Nomenclatura**
```
V{numero}__{acao}_{entidade}_{descricao}.sql
Exemplos:
- V11__create_table_consulta.sql
- V12__alter_table_paciente_add_cpf.sql
- V13__insert_data_especialidades.sql
```

### 2. **Separação de Responsabilidades**
- **Estrutura**: Um script apenas para DDL (CREATE, ALTER, DROP)
- **Dados**: Scripts separados para DML (INSERT, UPDATE, DELETE)
- **Índices**: Scripts específicos para otimizações

### 3. **Ambiente de Teste**
- Sempre testar migrações em ambiente de desenvolvimento
- Usar transações quando possível
- Documentar rollback procedures

### 4. **Controle de Versão**
- Nunca modificar scripts já aplicados em produção
- Usar apenas novos scripts para correções
- Manter changelog atualizado

## Status Pós V10:
- ✅ Estruturas consolidadas
- ✅ Duplicatas removidas  
- ✅ Dados consistentes
- ✅ Tabela `dentista` criada e populada
- ✅ Compatibilidade mantida com código existente

## Próximos Passos:
1. Executar `mvn flyway:migrate` para aplicar V10
2. Verificar integridade dos dados
3. Executar testes da aplicação
4. Implementar padrões recomendados para futuras migrações
