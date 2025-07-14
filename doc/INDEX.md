# Índice da Documentação - Cara Core Dental - Agendamentos

- **Sistema de Agendamento para Consultórios Odontológicos**

Este diretório contém toda a documentação técnica, estratégica e operacional do projeto CCA. A organização segue uma lógica construtiva para facilitar o desenvolvimento e manutenção da aplicação.

---

## Status e Controle do Projeto

```markdown
|----------------------------------------------------------------------|------------------------------------------------------|-------------------|
| Documento                                                            | Descrição                                            | Responsabilidade  |
|----------------------------------------------------------------------|------------------------------------------------------|-------------------|
| [STATUS_ATUAL.md](../STATUS_ATUAL.md)                                | Status completo do desenvolvimento e funcionalidades | Gestão de Projeto |
| [FUNCIONALIDADES_IMPLEMENTADAS.md](FUNCIONALIDADES_IMPLEMENTADAS.md) | Lista detalhada de funcionalidades já implementadas  | Desenvolvimento   |
| [PROBLEMAS_PENDENTES.md](PROBLEMAS_PENDENTES.md)                     | Problemas conhecidos e suas soluções                 | Suporte Técnico   |
|----------------------------------------------------------------------|------------------------------------------------------|-------------------|
```

### 🆕 Novidades Recentes (Julho 2025)

**Sistema Multi-Ambiente Implementado:**

- **Profile H2** (`application-h2.yml`): Desenvolvimento rápido com banco em memória
- **Profile Local** (`application-local.yml`): PostgreSQL via Docker para testes realísticos
- **Docker Integration**: Setup automático do PostgreSQL 15 para desenvolvimento
- **Pool de Conexões Otimizado**: Configurações específicas para cada ambiente
- **22 Migrações Flyway**: Aplicadas com sucesso em ambos os ambientes

**Status Atual:** ✅ **VERDE** - Sistema estável e pronto para homologação

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
| [POOL_CONEXOES.md](POOL_CONEXOES.md)                             | Otimização multi-ambiente (H2/PostgreSQL) | Performance          |
| [VERSIONAMENTO_BANCO_ANALISE.md](VERSIONAMENTO_BANCO_ANALISE.md) | Controle de migrations com Flyway     | Integridade de Dados |
| [CONFIGURACAO_AMBIENTES.md](CONFIGURACAO_AMBIENTES.md)           | Setup Docker PostgreSQL + H2          | DevOps Multi-Ambiente|
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

### Interface do Usuário

```markdown
|----------------------------------------------------------------|-----------------------------------------|-------------------|
| Área                                                           | Comportamento                           | Tipo de Acesso    |
|----------------------------------------------------------------|-----------------------------------------|-------------------|
| **Sistema Interno**                                            | Menu lateral com todas funcionalidades  | Usuário Logado    |
| **Agenda Pública**                                             | Header "Cara Core Dental - Agendamento" | Acesso Público    |
|----------------------------------------------------------------|-----------------------------------------|-------------------|
```

**Detalhes da Interface:**

- [TELAS_E_DESIGN.md](tech/TELAS_E_DESIGN.md) - Especificações completas das telas
- [INTERFACE_USUARIO.md](INTERFACE_USUARIO.md) - Guia de navegação e experiência do usuário
- [NAVEGACAO_IMPLEMENTACAO.md](NAVEGACAO_IMPLEMENTACAO.md) - Guia de implementação do sistema de navegação dual

**Componentes de Navegação:**

```markdown
|----------------------------------|------------------------------------------|-------------------------------------|
| Componente                       | Implementação                            | Uso                                 |
|----------------------------------|------------------------------------------|-------------------------------------|
| Sidebar (Menu Lateral)           | fragments/sidebar.html + sidebar.css/js  | Páginas internas autenticadas       |
| Header (Cabeçalho Público)       | fragments/header.html + header.css/js    | Agenda pública de acesso externo    |
| Layout Principal                 | fragments/main-layout.html               | Template base para todas as páginas |
|----------------------------------|------------------------------------------|-------------------------------------|
```

**Exemplo de Implementação:**

Para páginas autenticadas:

```html
<div th:replace="~{fragments/main-layout :: authenticated-layout('Título', 'activeLink', ~{::section})}">
    <section>
        <!-- Conteúdo específico da página -->
    </section>
</div>
```

Para páginas públicas:

```html
<div th:replace="~{fragments/main-layout :: public-layout('Título', ~{::main})}">
    <main>
        <!-- Conteúdo específico da página -->
    </main>
</div>
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
| [STARTUP_CARA_CORE_DENTAL.md](STARTUP_CARA_CORE_DENTAL.md)      | Plano de negócios da startup            | Investidores     |
| [MELHORIAS_STARTUP.md](MELHORIAS_STARTUP.md)                    | Framework de métricas e crescimento     | Gestão Executiva |
| [PLANO_DE_NEGOCIO.md](plan/PLANO_DE_NEGOCIO.md)                 | Plano de negócio completo               | Gestão Comercial |
| [VENDAS.md](plan/VENDAS.md)                                     | Plano de vendas e taxa de retorno       | Vendas           |
| [ESTRATEGIA.md](plan/ESTRATEGIA.md)                             | Estratégia técnica e comercial          | Operações        |
|-----------------------------------------------------------------|-----------------------------------------|------------------|
```

### Roadmap e Desenvolvimento

```markdown
|-----------------------------------------------|----------------------|-------------------------|
| Documento                                     | Fase do Projeto      | Aplicação               |
|-----------------------------------------------|----------------------|-------------------------|
| [DIVULGACAO.md](plan/DIVULGACAO.md)           | Lançamento           | Marketing e Comunicação |
| [PROTOTIPO.md](plan/PROTOTIPO.md)             | MVP                  | Validação de Conceito   |
| [ROADMAP.md](tech/ROADMAP.md)                 | Evolução Tecnológica | Arquitetura de Software |
|-----------------------------------------------|----------------------|-------------------------|
```

---

## Especificações Técnicas

### Frontend e Interface

```markdown
|----------------------------------------------------------|---------------------------------|-----------------------|
| Documento                                                | Componente                      | Framework             |
|----------------------------------------------------------|---------------------------------|-----------------------|
| [CHECKLIST_FRONT_END.md](tech/CHECKLIST_FRONT_END.md)    | Validação de desenvolvimento    | Thymeleaf + Bootstrap |
| [TELAS_E_DESIGN.md](tech/TELAS_E_DESIGN.md)              | Especificação de interfaces     | UI/UX                 |
| [CRONOLOGIA_TELAS.md](tech/CRONOLOGIA_TELAS.md)          | Planejamento de desenvolvimento | Gestão de Projeto     |
|----------------------------------------------------------|---------------------------------|-----------------------|
```

**Sistema de Navegação:**

```markdown
|-----------------------------|-----------------------------------------|--------------------------------|
| Componente                  | Arquivos                                | Responsabilidade              |
|-----------------------------|-----------------------------------------|--------------------------------|
| Layout Principal           | fragments/main-layout.html               | Template base para o sistema  |
| Menu Lateral (Autenticado) | fragments/sidebar.html                   | Navegação por perfil de acesso|
| CSS do Menu Lateral        | static/css/layout/sidebar.css            | Estilos do menu lateral       |
| JavaScript do Menu Lateral | static/js/layout/sidebar.js              | Comportamento do menu lateral |
| Header Público             | fragments/header.html                    | Navegação para agenda pública |
| CSS do Header              | static/css/layout/header.css             | Estilos do header público     |
| JavaScript do Header       | static/js/layout/header.js               | Comportamento do header       |
| Página Exemplo - Sistema   | templates/dashboard-example.html         | Modelo para páginas internas  |
| Página Exemplo - Público   | templates/public-example.html            | Modelo para páginas públicas  |
|-----------------------------|-----------------------------------------|--------------------------------|
```

**Estrutura de Interface:**

- **Sistema Interno (Autenticado):**
  - Layout com menu lateral contendo todas as funcionalidades
  - Navegação intuitiva entre módulos (Agenda, Prontuários, Configurações)
  - Acesso baseado em perfis (ADMIN, DENTIST, STAFF, PATIENT)

- **Agenda Pública (Não-Autenticado):**
  - Header institucional "Cara Core Dental - Agendamento"
  - Interface simplificada para marcação de consultas
  - Sem acesso a dados sensíveis ou funcionalidades restritas

### Arquitetura de Software

```markdown
|---------------------------------------|-----------------------------------------|----------------------|
| Documento                             | Área Técnica                            | Framework/Tecnologia |
|---------------------------------------|-----------------------------------------|----------------------|
| [REQUISITOS.md](tech/REQUISITOS.md)   | Especificação funcional e não funcional | Spring Boot          |
| [TECNOLOGIAS.md](tech/TECNOLOGIAS.md) | Decisões arquiteturais                  | Java 17 + Multi-DB   |
| [ROADMAP.md](tech/ROADMAP.md)         | Evolução tecnológica                    | Stack Completo       |
|---------------------------------------|-----------------------------------------|----------------------|
```

**Arquitetura Multi-Ambiente:**

- **Desenvolvimento Rápido**: H2 in-memory com massa de dados completa
- **Teste Realístico**: PostgreSQL 15 via Docker com Flyway migrations
- **Pool de Conexões**: HikariCP otimizado para cada ambiente
- **Configuração por Perfis**: application-h2.yml vs application-local.yml

### Integrações Externas

```markdown
|------------------------------------------------------------|-----------------------|-------------------------|
| Documento                                                  | Sistema Externo       | Status de Implementação |
|------------------------------------------------------------|-----------------------|-------------------------|
| [INTEGRACAO_WHATSAPP.md](tech/INTEGRACAO_WHATSAPP.md)      | WhatsApp Business API | Em desenvolvimento      |
|------------------------------------------------------------|-----------------------|-------------------------|
```

---

## Fluxo de Trabalho por Função

### Desenvolvedor Backend/Fullstack

**Sequência recomendada para setup e desenvolvimento:**

1. **Ambiente Multi-Profile:** [CONFIGURACAO_AMBIENTES.md](CONFIGURACAO_AMBIENTES.md) - Setup inicial com H2 e PostgreSQL
2. **Status Atual:** [STATUS_ATUAL.md](../STATUS_ATUAL.md) - Estado atual com Docker e multi-ambiente
3. **Arquitetura:** [TECNOLOGIAS.md](tech/TECNOLOGIAS.md) - Compreensão do stack tecnológico  
4. **Configuração de Perfis:** [APPLICATION_YML_GUIDE.md](APPLICATION_YML_GUIDE.md) - Profiles H2 vs PostgreSQL
5. **Performance:** [POOL_CONEXOES.md](POOL_CONEXOES.md) - Configuração de pools para ambos ambientes
6. **Requisitos:** [REQUISITOS.md](tech/REQUISITOS.md) - Especificações funcionais
7. **Sistema Prontuário:** [SISTEMA_PRONTUARIO_RESUMO.md](SISTEMA_PRONTUARIO_RESUMO.md) - Funcionalidade implementada
8. **Testes:** [PRONTUARIO_TESTES_UNITARIOS.md](PRONTUARIO_TESTES_UNITARIOS.md) - Validação de qualidade

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
5. **Migrations:** [VERSIONAMENTO_BANCO_ANALISE.md](VERSIONAMENTO_BANCO_ANALISE.md) - Controle de versão

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
**Versão da Documentação:** 2.4  
**Última Atualização:** 14/07/2025

---

**Nota:** Esta documentação segue princípios de desenvolvimento ágil e é atualizada continuamente conforme a evolução do projeto.
