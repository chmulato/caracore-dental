# Índice da Documentação - Cara Core Dental - Agendamentos

## Sistema de Agendamento para Consultórios Odontológicos

Este diretório contém toda a documentação técnica, estratégica e operacional do projeto CCA. A organização segue uma hierarquia de relevância para facilitar o desenvolvimento e manutenção da aplicação.

---

## DOCUMENTAÇÃO CRÍTICA - Uso Diário

### Status e Controle Principal

```markdown
|----------------------------------------------------------------------|------------------------------------------------------|-------------------|
| Documento                                                            | Descrição                                            | Responsabilidade  |
|----------------------------------------------------------------------|------------------------------------------------------|-------------------|
| [STATUS_ATUAL.md](../STATUS_ATUAL.md)                                | Status completo multi-ambiente (14/07/2025)          | Gestão de Projeto |
| [FUNCIONALIDADES_IMPLEMENTADAS.md](FUNCIONALIDADES_IMPLEMENTADAS.md) | Lista completa de features implementadas             | Desenvolvimento   |
| [INDEX.md](INDEX.md)                                                 | Índice principal da documentação                     | Documentação      |
|----------------------------------------------------------------------|------------------------------------------------------|-------------------|
```

### Configuração Essencial (Setup Obrigatório)

```markdown
|--------------------------------------------------------|---------------------------------------------|-------------------------|
| Documento                                              | Aplicação                                   | Criticidade             |
|--------------------------------------------------------|---------------------------------------------|-------------------------|
| [CONFIGURACAO_AMBIENTES.md](CONFIGURACAO_AMBIENTES.md) | Setup Docker + H2 + PostgreSQL              | CRÍTICO - Setup         |
| [APPLICATION_YML_GUIDE.md](APPLICATION_YML_GUIDE.md)   | Configuração de profiles multi-ambiente     | CRÍTICO - Config        |
| [POOL_CONEXOES.md](POOL_CONEXOES.md)                   | HikariCP para H2 e PostgreSQL               | CRÍTICO - Performance   |
|--------------------------------------------------------|---------------------------------------------|-------------------------|
```

### Sistema de Prontuário (Funcionalidade Principal)

```markdown
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
| Documento                                                                | Descrição                                   | Status/Atualização   |
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
| [SISTEMA_PRONTUARIO_RESUMO.md](SISTEMA_PRONTUARIO_RESUMO.md)             | Sistema completo de prontuários             | Implementado (13/07) |
| [PRONTUARIO_ARQUITETURA_DTO.md](PRONTUARIO_ARQUITETURA_DTO.md)           | Padrão DTO para performance                 | Implementado (13/07) |
| [PRONTUARIO_TESTES_UNITARIOS.md](PRONTUARIO_TESTES_UNITARIOS.md)         | 545/545 testes passando                     | 100% Cobertura       |
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
```

### Sistema de Agendamento Online (Reformulado)

```markdown
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
| Documento                                                                | Descrição                                   | Status/Atualização   |
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
| [AGENDAMENTO_ONLINE.md](AGENDAMENTO_ONLINE.md)                           | Sistema com navegação JavaScript por seções| Reformulado (16/07)  |
| [AGENDAMENTO_REFORMULADO_16_07.md](AGENDAMENTO_REFORMULADO_16_07.md)     | Documentação completa da reformulação      | Novo (16/07)         |
| [TESTS_AGENDAMENTO_PUBLICO.md](TESTS_AGENDAMENTO_PUBLICO.md)             | Validação e cobertura de testes             | Verificado (15/07)   |
| [CORRECOES_AGENDAMENTO_FORM_TEST.md](CORRECOES_AGENDAMENTO_FORM_TEST.md) | Correções nos testes do AgendamentoForm     | Corrigido (15/07)    |
| [CORRECOES_AGENDAMENTO_SERVICE_TEST.md](CORRECOES_AGENDAMENTO_SERVICE_TEST.md) | Correções nos testes do AgendamentoService | Corrigido (15/07)  |
| [CORRECOES_PUBLIC_CONTROLLER_TEST.md](CORRECOES_PUBLIC_CONTROLLER_TEST.md) | Correções nos testes do PublicController   | Corrigido (15/07)    |
| [CONFIGURACAO_RECAPCHA.md](CONFIGURACAO_RECAPCHA.md)                     | Configuração reCAPTCHA para sistema único   | Atualizado (15/07)   |
| [FLUXO_ETAPAS_IMPLEMENTADO.md](FLUXO_ETAPAS_IMPLEMENTADO.md)             | Histórico do sistema de etapas (removido)   | Deprecado            |
| [AGENDAMENTO_ONLINE_TRES_ETAPAS.md](AGENDAMENTO_ONLINE_TRES_ETAPAS.md)   | Sistema antigo de múltiplas páginas         | Removido (15/07)     |
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
```

### Novidades Recentes (Julho 2025)

**Sistema Multi-Ambiente Implementado:**

- **Profile H2** (`application-h2.yml`): Desenvolvimento rápido com banco em memória
- **Profile Local** (`application-local.yml`): PostgreSQL via Docker para testes realísticos  
- **Docker Integration**: Setup automático do PostgreSQL 15 para desenvolvimento
- **Pool de Conexões Otimizado**: Configurações específicas para cada ambiente
- **22 Migrações Flyway**: Aplicadas com sucesso em ambos os ambientes

**🆕 Agendamento Público Reformulado (16/07/2025):**

- **Sistema Accordion REMOVIDO**: Eliminado em favor de navegação JavaScript pura
- **3 Seções de Navegação**: Dados Pessoais → Calendário → Confirmação
- **Calendário FullCalendar Otimizado**: Layout expandido (700px+), responsivo e profissional
- **Interface Premium**: Design moderno com gradientes, sombras e animações CSS
- **Validação PostgreSQL**: 9 profissionais ativos carregando corretamente
- **UX/UI Superior**: Navegação fluida, indicadores visuais e feedback em tempo real

**Status Atual:** 🟢 VERDE - Sistema estável e pronto para homologação

---

## DOCUMENTAÇÃO IMPORTANTE - Apoio ao Desenvolvimento

### Segurança e Compliance

```markdown
|------------------------------------------------------------------------------|-----------------------------------|----------------|
| Documento                                                                    | Área de Aplicação                 | Regulamentação |
|------------------------------------------------------------------------------|-----------------------------------|----------------|
| [ESTRATEGIAS_SEGURANCA_AGENDAMENTO.md](ESTRATEGIAS_SEGURANCA_AGENDAMENTO.md) | Proteção LGPD de agendamentos     | LGPD           |
| [USER_ACTIVITY_LOG.md](USER_ACTIVITY_LOG.md)                                 | Auditoria de ações                | Compliance     |
| [ACESSO_AGENDA_PUBLICA.md](ACESSO_AGENDA_PUBLICA.md)                         | Controle de autorização           | Segurança      |
| [CONFIGURACAO_RECAPCHA.md](CONFIGURACAO_RECAPCHA.md)                         | Implementação e configuração do Recaptcha | Segurança      |
|------------------------------------------------------------------------------|-----------------------------------|----------------|
```

### Problemas e Manutenção

```markdown
|----------------------------------------------------------------|---------------------------------|-----------------|
| Documento                                                      | Módulo/Finalidade               | Relevância      |
|----------------------------------------------------------------|---------------------------------|-----------------|
| [PROBLEMAS_PENDENTES.md](PROBLEMAS_PENDENTES.md)               | 546 linhas de issues/melhorias  | ALTO - Gestão   |
| [STATUS_CONSULTAS_AGENDADAS.md](STATUS_CONSULTAS_AGENDADAS.md) | Sistema de consultas            | ALTO - Feature  |
|----------------------------------------------------------------|---------------------------------|-----------------|
```

### APIs e Integrações

```markdown
|------------------------------------------------------|---------------------------------------|------------------------------|
| Documento                                            | Tecnologia                            | Finalidade                   |
|------------------------------------------------------|---------------------------------------|------------------------------|
| [SWAGGER_README.md](SWAGGER_README.md)               | OpenAPI 3.0 + endpoints               | ALTO - API Docs              |
| [EXEMPLOS_CONFIGURACAO.md](EXEMPLOS_CONFIGURACAO.md) | Configurações práticas                | MÉDIO - Implementação        |
| [CONFIGURACAO_RECAPCHA.md](CONFIGURACAO_RECAPCHA.md) | Google Recaptcha                      | Segurança/Validação          |
|------------------------------------------------------|---------------------------------------|------------------------------|
```

---

## DOCUMENTAÇÃO COMPLEMENTAR - Consulta Conforme Necessário

### Estratégia e Negócio

```markdown
|-----------------------------------------------------------------|-----------------------------------------|------------------|
| Documento                                                       | Aplicação no Negócio                    | Público-Alvo     |
|-----------------------------------------------------------------|-----------------------------------------|------------------|
| [PLANO_DE_NEGOCIO.md](plan/PLANO_DE_NEGOCIO.md)                 | Plano de negócio completo               | Gestão Comercial |
| [VENDAS.md](plan/VENDAS.md)                                     | Estratégia de vendas                    | Vendas           |
| [ESTRATEGIA.md](plan/ESTRATEGIA.md)                             | Estratégia técnica/comercial            | Operações        |
| [STARTUP_CARA_CORE_DENTAL.md](STARTUP_CARA_CORE_DENTAL.md)      | Plano de startup                        | Investidores     |
| [MELHORIAS_STARTUP.md](MELHORIAS_STARTUP.md)                    | Framework de KPIs                       | Gestão Executiva |
|-----------------------------------------------------------------|-----------------------------------------|------------------|
```

### Interface e UX

```markdown
|----------------------------------------------------------|---------------------------------|-----------------------|
| Documento                                                | Componente                      | Framework             |
|----------------------------------------------------------|---------------------------------|-----------------------|
| [INTERFACE_USUARIO.md](INTERFACE_USUARIO.md)             | Navegação e experiência         | UI/UX                 |
| [NAVEGACAO_IMPLEMENTACAO.md](NAVEGACAO_IMPLEMENTACAO.md) | Sistema de navegação dual       | Thymeleaf             |
| [CHECKLIST_FRONT_END.md](tech/CHECKLIST_FRONT_END.md)    | Validação de desenvolvimento    | Bootstrap + jQuery    |
| [TELAS_E_DESIGN.md](tech/TELAS_E_DESIGN.md)              | Especificação de interfaces     | Design System         |
|----------------------------------------------------------|---------------------------------|-----------------------|
```

### Infraestrutura e Scripts

```markdown
|------------------------------------------------------------------|---------------------------------------------|-------------------------|
| Documento                                                        | Foco Técnico                                | Uso                     |
|------------------------------------------------------------------|---------------------------------------------|-------------------------|
| [SCRIPTS_AMBIENTE.md](SCRIPTS_AMBIENTE.md)                       | Automação de setup                          | DevOps                  |
| [VERSIONAMENTO_BANCO.md](VERSIONAMENTO_BANCO.md)                 | Controle Flyway migrations                  | Integridade BD          |
|------------------------------------------------------------------|---------------------------------------------|-------------------------|
```

---

## DOCUMENTAÇÃO TÉCNICA ESPECIALIZADA

### Arquitetura de Software

```markdown
|---------------------------------------|-----------------------------------------|----------------------|
| Documento                             | Área Técnica                            | Framework/Tecnologia |
|---------------------------------------|-----------------------------------------|----------------------|
| [TECNOLOGIAS.md](tech/TECNOLOGIAS.md) | Stack completo (370 linhas)             | Java 17 + Multi-DB   |
| [REQUISITOS.md](tech/REQUISITOS.md)   | Especificações funcionais               | Spring Boot          |
| [ROADMAP.md](tech/ROADMAP.md)         | Evolução tecnológica                    | Arquitetura          |
|---------------------------------------|-----------------------------------------|----------------------|
```

**Arquitetura Multi-Ambiente:**

- **Desenvolvimento Rápido**: H2 in-memory com massa de dados completa
- **Teste Realístico**: PostgreSQL 15 via Docker com Flyway migrations  
- **Pool de Conexões**: HikariCP otimizado para cada ambiente
- **Configuração por Perfis**: application-h2.yml vs application-local.yml

### Integrações Futuras

```markdown
|------------------------------------------------------------|-----------------------|-------------------------|
| Documento                                                  | Sistema Externo       | Status de Implementação |
|------------------------------------------------------------|-----------------------|-------------------------|
| [INTEGRACAO_WHATSAPP.md](tech/INTEGRACAO_WHATSAPP.md)      | WhatsApp Business API | Em desenvolvimento      |
|------------------------------------------------------------|-----------------------|-------------------------|
```

### Planejamento e Gestão

```markdown
|-------------------------------------------------|----------------------|-------------------------|
| Documento                                       | Fase do Projeto      | Aplicação               |
|-------------------------------------------------|----------------------|-------------------------|
| [CRONOLOGIA_TELAS.md](tech/CRONOLOGIA_TELAS.md) | Planejamento UI      | Gestão de Projeto       |
| [DIVULGACAO.md](plan/DIVULGACAO.md)             | Lançamento           | Marketing               |
| [PROTOTIPO.md](plan/PROTOTIPO.md)               | MVP                  | Validação de Conceito   |
|-------------------------------------------------|----------------------|-------------------------|
```

---

## FLUXOS DE TRABALHO POR FUNÇÃO

### Desenvolvedor Backend/Fullstack

**Sequência recomendada para setup e desenvolvimento:**

1. **Ambiente Multi-Profile:** [CONFIGURACAO_AMBIENTES.md](CONFIGURACAO_AMBIENTES.md) - Setup inicial com H2 e PostgreSQL
2. **Status Atual:** [STATUS_ATUAL.md](../STATUS_ATUAL.md) - Estado atual com Docker e multi-ambiente
3. **Arquitetura:** [TECNOLOGIAS.md](tech/TECNOLOGIAS.md) - Compreensão do stack tecnológico  
4. **Configuração de Perfis:** [APPLICATION_YML_GUIDE.md](APPLICATION_YML_GUIDE.md) - Profiles H2 vs PostgreSQL
5. **Performance:** [POOL_CONEXOES.md](POOL_CONEXOES.md) - Configuração de pools para ambos ambientes
6. **🆕 Agendamento Reformulado:** [AGENDAMENTO_ONLINE.md](AGENDAMENTO_ONLINE.md) - Sistema com navegação JavaScript por seções
7. **Requisitos:** [REQUISITOS.md](tech/REQUISITOS.md) - Especificações funcionais
8. **Sistema Prontuário:** [SISTEMA_PRONTUARIO_RESUMO.md](SISTEMA_PRONTUARIO_RESUMO.md) - Funcionalidade implementada
9. **Testes:** [PRONTUARIO_TESTES_UNITARIOS.md](PRONTUARIO_TESTES_UNITARIOS.md) - Validação de qualidade

**Comandos Quick Start:**

```powershell
# Desenvolvimento rápido com H2
$env:SPRING_PROFILES_ACTIVE='h2'; mvn spring-boot:run

# Desenvolvimento com PostgreSQL (mais próximo da produção)
docker run -d --name postgres-cca -e POSTGRES_DB=cca_db -e POSTGRES_USER=cca_user -e POSTGRES_PASSWORD=cca_pass -p 5432:5432 postgres:15
$env:SPRING_PROFILES_ACTIVE='local'; mvn spring-boot:run
```

### Gestor de Projeto/Product Owner

**Documentos para acompanhamento e planejamento:**

1. **Visão Geral:** [FUNCIONALIDADES_IMPLEMENTADAS.md](FUNCIONALIDADES_IMPLEMENTADAS.md) - Features disponíveis
2. **Roadmap:** [ROADMAP.md](tech/ROADMAP.md) - Planejamento de evolução
3. **Startup:** [STARTUP_CARA_CORE_DENTAL.md](STARTUP_CARA_CORE_DENTAL.md) - Plano de negócios e oportunidade
4. **Métricas:** [MELHORIAS_STARTUP.md](MELHORIAS_STARTUP.md) - Framework de crescimento e KPIs
5. **Business:** [PLANO_DE_NEGOCIO.md](plan/PLANO_DE_NEGOCIO.md) - Estratégia de negócio
6. **Problemas:** [PROBLEMAS_PENDENTES.md](PROBLEMAS_PENDENTES.md) - Issues em andamento

### DevOps/Infraestrutura

**Configuração de ambientes e deploy:**

1. **Scripts:** [SCRIPTS_AMBIENTE.md](SCRIPTS_AMBIENTE.md) - Automação de setup
2. **Multi-Ambiente:** [CONFIGURACAO_AMBIENTES.md](CONFIGURACAO_AMBIENTES.md) - Setup H2 + PostgreSQL Docker
3. **Configuração:** [APPLICATION_YML_GUIDE.md](APPLICATION_YML_GUIDE.md) - Profiles e propriedades
4. **Banco de Dados:** [POOL_CONEXOES.md](POOL_CONEXOES.md) - Otimização multi-ambiente
5. **Migrations:** [VERSIONAMENTO_BANCO.md](VERSIONAMENTO_BANCO.md) - Controle de versão

**Setup Docker PostgreSQL:**

```bash
# Container PostgreSQL para desenvolvimento
docker run -d \
  --name postgres-cca \
  -e POSTGRES_DB=cca_db \
  -e POSTGRES_USER=cca_user \
  -e POSTGRES_PASSWORD=cca_pass \
  -p 5432:5432 \
  postgres:15
```

### Arquiteto de Software

**Decisões técnicas e estruturais:**

1. **Stack:** [TECNOLOGIAS.md](tech/TECNOLOGIAS.md) - Decisões arquiteturais
2. **Multi-Ambiente:** [STATUS_ATUAL.md](../STATUS_ATUAL.md) - Arquitetura H2 + PostgreSQL
3. **APIs:** [SWAGGER_README.md](SWAGGER_README.md) - Documentação de interfaces
4. **Performance:** [POOL_CONEXOES.md](POOL_CONEXOES.md) - Estratégia de pools de conexão
5. **Segurança:** [ESTRATEGIAS_SEGURANCA_AGENDAMENTO.md](ESTRATEGIAS_SEGURANCA_AGENDAMENTO.md) - Políticas de segurança
6. **Integrações:** [INTEGRACAO_WHATSAPP.md](tech/INTEGRACAO_WHATSAPP.md) - Sistemas externos

---

## Desenvolvimento e Testes

### Qualidade de Código e Validação

```markdown
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
| Documento                                                                | Descrição                                   | Cobertura            |
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
| [PRONTUARIO_TESTES_UNITARIOS.md](PRONTUARIO_TESTES_UNITARIOS.md)         | Testes unitários do sistema de prontuário   | 100% (545/545 testes)|
| [CHECKLIST_FRONT_END.md](tech/CHECKLIST_FRONT_END.md)                    | Validação de elementos de interface         | Frontend (UI)        |
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
```

---

## Sistema de Prontuário Odontológico

### Funcionalidades Implementadas

```markdown
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
| Documento                                                                | Descrição                                   | Status               |
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
| [SISTEMA_PRONTUARIO_RESUMO.md](SISTEMA_PRONTUARIO_RESUMO.md)             | Sistema completo de gestão de prontuários   | Implementado         |
| [PRONTUARIO_DOCUMENTACAO.md](PRONTUARIO_DOCUMENTACAO.md)                 | Documentação organizacional do sistema      | Completo             |
| [PRONTUARIO_TESTES_UNITARIOS.md](PRONTUARIO_TESTES_UNITARIOS.md)         | Relatório completo dos testes unitários     | Implementado (100%)  |
| [PRONTUARIO_ARQUITETURA_DTO.md](PRONTUARIO_ARQUITETURA_DTO.md)           | Padrão DTO para gestão de imagens e dados   | Implementado (13/07) |
|--------------------------------------------------------------------------|---------------------------------------------|----------------------|
```

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

## Padrões de Implementação Frontend

### Estrutura de Layouts

1. **Layout para Usuários Autenticados (com sidebar)**
   - Use este layout para todas as páginas que requerem autenticação
   - Sempre inclua o parâmetro `activeLink` para destacar o item de menu correto
   - Estrutura: `th:replace="~{fragments/main-layout :: authenticated-layout('Título', 'activeLink', ~{::section})}`

2. **Layout para Agenda Pública (com header)**
   - Use este layout para todas as páginas de acesso público
   - Mantenha uma interface simplificada focada no agendamento
   - Estrutura: `th:replace="~{fragments/main-layout :: public-layout('Título', ~{::main})}`

### Controllers e Modelo

Para que os menus funcionem corretamente, os controllers devem:

1. Passar o atributo `activeLink` para o modelo
2. Usar o valor que corresponde à seção do menu a ser destacada
3. Exemplo:

```java
@GetMapping("/pacientes")
public String listarPacientes(Model model) {
    model.addAttribute("activeLink", "pacientes");
    // Outras operações
    return "pacientes/lista";
}
```

---

## Informações do Projeto

**Organização:** Cara Core Informática  
**Sistema:** Agendamento para Consultórios Odontológicos  
**Tecnologia Principal:** Spring Boot 3.2.6 + Java 17  
**Banco de Dados:** H2 (desenvolvimento) / PostgreSQL 15 (Docker)  
**Multi-Ambiente:** Perfis H2 e PostgreSQL com Docker Support  
**🆕 Agendamento:** Navegação JavaScript por seções (reformulado 16/07/2025)  
**Versão da Documentação:** 2.6  
**Última Atualização:** 16/07/2025

---

**Nota:** Esta documentação segue princípios de desenvolvimento ágil e é atualizada continuamente conforme a evolução do projeto.
