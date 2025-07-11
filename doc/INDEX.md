# Índice da Documentação - Cara Core Agendamento (CCA)

- **Sistema de Agendamento para Consultórios Odontológicos**

Este diretório contém toda a documentação técnica, estratégica e operacional do projeto CCA. A organização segue uma lógica construtiva para facilitar o desenvolvimento e manutenção da aplicação.

---

## Status e Controle do Projeto

```markdown
|----------------------------------------------------------------------|------------------------------------------------------|------------------|
| Documento                                                            | Descrição                                            | Responsabilidade |
|----------------------------------------------------------------------|------------------------------------------------------|------------------|
| [STATUS_ATUAL.md](../STATUS_ATUAL.md)                                | Status completo do desenvolvimento e funcionalidades | Gestão de Projeto |
| [FUNCIONALIDADES_IMPLEMENTADAS.md](FUNCIONALIDADES_IMPLEMENTADAS.md) | Lista detalhada de funcionalidades já implementadas  | Desenvolvimento   |
| [PROBLEMAS_PENDENTES.md](PROBLEMAS_PENDENTES.md)                     | Problemas conhecidos e suas soluções                 | Suporte Técnico           |
|----------------------------------------------------------------------|------------------------------------------------------|-------------------|
```

---

## Arquitetura e Configuração de Sistema

### Configuração de Ambiente

```markdown
|--------------------------------------------------------|---------------------------------------------|-------------------------|
| Documento                                              | Aplicação                                   | Fase de Desenvolvimento |
|--------------------------------------------------------|---------------------------------------------|-------------------------|
| [APPLICATION_YML_GUIDE.md](APPLICATION_YML_GUIDE.md)   | Configuração de profiles e propriedades     | Setup Inicial           |
| [CONFIGURACAO_AMBIENTES.md](CONFIGURACAO_AMBIENTES.md) | Setup de ambientes (local, teste, produção) | DevOps                  |
| [SCRIPTS_AMBIENTE.md](SCRIPTS_AMBIENTE.md)             | Automação de inicialização                  | Produtividade           |
|--------------------------------------------------------|---------------------------------------------|-------------------------|
```

### Banco de Dados e Performance

```markdown
|------------------------------------------------------------------|---------------------------------------|----------------------|
| Documento                                                        | Foco Técnico                          | Impacto              |
|------------------------------------------------------------------|---------------------------------------|----------------------|
| [POOL_CONEXOES.md](POOL_CONEXOES.md)                             | Otimização de conexões com PostgreSQL | Performance          |
| [VERSIONAMENTO_BANCO_ANALISE.md](VERSIONAMENTO_BANCO_ANALISE.md) | Controle de migrations com Flyway     | Integridade de Dados |
|------------------------------------------------------------------|---------------------------------------|----------------------|
```

---

## Segurança e Compliance

```markdown
|------------------------------------------------------------------------------|-----------------------------------|----------------|
| Documento                                                                    | Área de Aplicação                 | Regulamentação |
|------------------------------------------------------------------------------|-----------------------------------|----------------|
| [ESTRATEGIAS_SEGURANCA_AGENDAMENTO.md](ESTRATEGIAS_SEGURANCA_AGENDAMENTO.md) | Proteção de dados de agendamentos | LGPD           |
| [USER_ACTIVITY_LOG.md](USER_ACTIVITY_LOG.md)                                 | Auditoria de ações dos usuários   | Compliance     |
| [ACESSO_AGENDA_PUBLICA.md](ACESSO_AGENDA_PUBLICA.md)                         | Controle de autorização           | Segurança      |
|------------------------------------------------------------------------------|-----------------------------------|----------------|
```

---

## Funcionalidades de Negócio

```markdown
|----------------------------------------------------------------|---------------------------------|-----------------|
| Documento                                                      | Módulo do Sistema               | Stakeholder     |
|----------------------------------------------------------------|---------------------------------|-----------------|
| [STATUS_CONSULTAS_AGENDADAS.md](STATUS_CONSULTAS_AGENDADAS.md) | Gestão de consultas e workflows | Usuários Finais |
|----------------------------------------------------------------|---------------------------------|-----------------|
```

---

## Integração e APIs

```markdown
|------------------------------------------------------|---------------------------------------|------------------------------|
| Documento                                            | Tecnologia                            | Finalidade                   |
|------------------------------------------------------|---------------------------------------|------------------------------|
| [SWAGGER_README.md](SWAGGER_README.md)               | OpenAPI 3.0 para documentação de APIs | Desenvolvimento e Integração |
| [EXEMPLOS_CONFIGURACAO.md](EXEMPLOS_CONFIGURACAO.md) | Configurações práticas do sistema     | Implementação                |
|------------------------------------------------------|---------------------------------------|------------------------------|
```

---

## Planejamento Estratégico e Business

### Documentos de Negócio

```markdown
|-----------------------------------------------------------------|-----------------------------------------|------------------|
| Documento                                                       | Aplicação no Negócio                    | Público-Alvo     |
|-----------------------------------------------------------------|-----------------------------------------|------------------|
| [plano_negocio_agenda.md](plan/plano_negocio_agenda.md)         | Modelagem de negócio para agendamentos  | Gestão Comercial |
| [plano_negocios_automacao.md](plan/plano_negocios_automacao.md) | Estratégias de automação de processos   | Operações        |
| [plano_vendas_agenda.md](plan/plano_vendas_agenda.md)           | Estratégias de comercialização          | Vendas           |
|-----------------------------------------------------------------|-----------------------------------------|------------------|
```

### Roadmap e Desenvolvimento

```markdown
|-------------------------------------------------------------------------|----------------------|-------------------------|
| Documento                                                               | Fase do Projeto      | Aplicação               |
|-------------------------------------------------------------------------|----------------------|-------------------------|
| [plano_estrategico_divulgacao.md](plan/plano_estrategico_divulgacao.md) | Lançamento           | Marketing e Comunicação |
| [plano_estrategico_prototipo.md](plan/plano_estrategico_prototipo.md)   | MVP                  | Validação de Conceito   |
| [plano_estrategico_tecnologia.md](plan/plano_estrategico_tecnologia.md) | Evolução Tecnológica | Arquitetura de Software |
|-------------------------------------------------------------------------|----------------------|-------------------------|
```

---

## Especificações Técnicas

### Frontend e Interface

```markdown
|-----------------------------------------------------|---------------------------------|-----------------------|
| Documento                                           | Componente                      | Framework             |
|-----------------------------------------------------|---------------------------------|-----------------------|
| [checklist_frontend.md](tech/checklist_frontend.md) | Validação de desenvolvimento    | Thymeleaf + Bootstrap |
| [telas_e_design.md](tech/telas_e_design.md)         | Especificação de interfaces     | UI/UX                 |
| [cronograma_telas.md](tech/cronograma_telas.md)     | Planejamento de desenvolvimento | Gestão de Projeto     |
|-----------------------------------------------------|---------------------------------|-----------------------|
```

### Arquitetura de Software

```markdown
|-----------------------------------------------|-----------------------------------------|----------------------|
| Documento                                     | Área Técnica                            | Framework/Tecnologia |
|-----------------------------------------------|-----------------------------------------|----------------------|
| [requisitos.md](tech/requisitos.md)           | Especificação funcional e não funcional | Spring Boot          |
| [tecnologia.md](tech/tecnologia.md)           | Decisões arquiteturais                  | Java 17 + PostgreSQL |
| [roadmap_upgrade.md](tech/roadmap_upgrade.md) | Evolução tecnológica                    | Stack Completo       |
|-----------------------------------------------|-----------------------------------------|----------------------|
```

### Integrações Externas

```markdown
|-------------------------------------------------------|-----------------------|-------------------------|
| Documento                                             | Sistema Externo       | Status de Implementação |
|-------------------------------------------------------|-----------------------|-------------------------|
| [whatsapp_integracao.md](tech/whatsapp_integracao.md) | WhatsApp Business API | Em desenvolvimento      |
|-------------------------------------------------------|-----------------------|-------------------------|
```

---

## Fluxo de Trabalho por Função

### Desenvolvedor Backend/Fullstack

**Sequência recomendada para setup e desenvolvimento:**

1. **Ambiente Base:** [CONFIGURACAO_AMBIENTES.md](CONFIGURACAO_AMBIENTES.md) - Setup inicial da aplicação
2. **Arquitetura:** [tech/tecnologia.md](tech/tecnologia.md) - Compreensão do stack tecnológico  
3. **Requisitos:** [tech/requisitos.md](tech/requisitos.md) - Especificações funcionais
4. **Status Atual:** [STATUS_ATUAL.md](../STATUS_ATUAL.md) - Estado atual do desenvolvimento

### Gestor de Projeto/Product Owner

**Documentos para acompanhamento e planejamento:**

1. **Visão Geral:** [FUNCIONALIDADES_IMPLEMENTADAS.md](FUNCIONALIDADES_IMPLEMENTADAS.md) - Features disponíveis
2. **Roadmap:** [tech/roadmap_upgrade.md](tech/roadmap_upgrade.md) - Planejamento de evolução
3. **Business:** Documentos na pasta [plan/](plan/) - Estratégia de negócio
4. **Problemas:** [PROBLEMAS_PENDENTES.md](PROBLEMAS_PENDENTES.md) - Issues em andamento

### DevOps/Infraestrutura

**Configuração de ambientes e deploy:**

1. **Scripts:** [SCRIPTS_AMBIENTE.md](SCRIPTS_AMBIENTE.md) - Automação de setup
2. **Configuração:** [APPLICATION_YML_GUIDE.md](APPLICATION_YML_GUIDE.md) - Profiles e propriedades
3. **Banco de Dados:** [POOL_CONEXOES.md](POOL_CONEXOES.md) - Otimização de performance
4. **Migrations:** [VERSIONAMENTO_BANCO_ANALISE.md](VERSIONAMENTO_BANCO_ANALISE.md) - Controle de versão

### Arquiteto de Software

**Decisões técnicas e estruturais:**

1. **Stack:** [tech/tecnologia.md](tech/tecnologia.md) - Decisões arquiteturais
2. **APIs:** [SWAGGER_README.md](SWAGGER_README.md) - Documentação de interfaces
3. **Segurança:** [ESTRATEGIAS_SEGURANCA_AGENDAMENTO.md](ESTRATEGIAS_SEGURANCA_AGENDAMENTO.md) - Políticas de segurança
4. **Integrações:** [tech/whatsapp_integracao.md](tech/whatsapp_integracao.md) - Sistemas externos

---

## Padrões de Documentação

### Estrutura Padrão de Documentos

- **Cabeçalho:** Título, descrição e contexto
- **Objetivo:** Finalidade e aplicação do documento  
- **Conteúdo Principal:** Informações técnicas e procedimentos
- **Referências:** Links para documentos relacionados
- **Histórico:** Data de criação e atualizações

### Classificação de Status

- **Implementado** - Funcionalidade em produção
- **Em Desenvolvimento** - Feature sendo desenvolvida
- **Planejado** - Item no roadmap
- **Pendente** - Aguardando recursos ou decisões

### Responsabilidades de Atualização

- **Desenvolvedores:** Documentação técnica e de configuração
- **Gestão:** Documentos de negócio e roadmap
- **DevOps:** Configuração de ambiente e scripts
- **Arquitetura:** Decisões técnicas e padrões

---

## Informações do Projeto

**Organização:** Cara Core Informática  
**Sistema:** Agendamento para Consultórios Odontológicos  
**Tecnologia Principal:** Spring Boot 3.2.6 + Java 17  
**Banco de Dados:** PostgreSQL 16.9  
**Versão da Documentação:** 2.0  
**Última Atualização:** 11/07/2025

---

**Nota:** Esta documentação segue princípios de desenvolvimento ágil e é atualizada continuamente conforme a evolução do projeto.
