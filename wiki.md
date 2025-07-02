# Wiki do Projeto Cara Core Agendamento (CCA)

Bem-vindo à wiki do projeto Cara Core Agendamento! Este espaço contém a documentação completa do ### Gestão de Dentistas

O módulo de gestão de dentistas oferece controle completo sobre os profissionais do consultório, seguindo o padrão visual Bootstrap 5.3.0 e práticas modernas de UX/UI.

#### Funcionalidades Principais

- **Cadastro Completo**: Nome, email, telefone, CRO, especialidades
- **Controle de Status**: Ativo/Inativo com indicadores visuais claros
- **Horários de Atendimento**: Configuração de disponibilidade semanal
- **Busca Avançada**: Filtros por nome, especialidade, email e status
- **Interface Responsiva**: Otimizada para desktop, tablet e mobile

#### Cadastro e Edição

O formulário de dentistas inclui:

- **Dados Pessoais**: Nome completo, email, telefone
- **Dados Profissionais**: Número do CRO, especialidades principais
- **Configurações**: Status (ativo/inativo), observações
- **Validações**: Email único, formato de telefone, campos obrigatórios

```html
<!-- Exemplo de card de dentista -->
<div class="card dentista-card">
    <div class="card-body">
        <div class="d-flex align-items-center mb-3">
            <img src="/img/default-avatar.jpg" class="dentista-avatar me-3">
            <div>
                <h5 class="card-title mb-0">Dr. João Silva</h5>
                <span class="badge bg-success">Ativo</span>
            </div>
        </div>
        <p class="card-text">
            <i class="bi bi-envelope me-2"></i>joao@clinica.com<br>
            <i class="bi bi-telephone me-2"></i>(11) 99999-9999<br>
            <i class="bi bi-award me-2"></i>Ortodontia, Implantes
        </p>
    </div>
</div>
```

#### Busca e Filtros

Sistema de busca em tempo real com:

- **Busca Global**: Pesquisa em nome, email e especialidades
- **Filtro por Status**: Ativo, Inativo ou Todos
- **Ordenação**: Por nome, data de cadastro, status
- **Paginação**: Navegação eficiente para grandes volumes

#### Controle de Status

- **Indicadores Visuais**: Badges coloridos (verde=ativo, vermelho=inativo)
- **Toggle Rápido**: Ativação/desativação direta da listagem
- **Histórico**: Rastreamento de alterações de status
- **Validações**: Não permitir desativar dentista com agendamentos futuros

### Gestão de Pacientes

O módulo de pacientes oferece cadastro completo com dados pessoais, histórico de consultas e prontuário digital integrado.

#### Dados Armazenados

- **Dados Pessoais**: Nome completo, email, telefone WhatsApp
- **Endereço**: CEP, logradouro, cidade, estado
- **Histórico Médico**: Alergias, medicamentos, condições especiais
- **Histórico de Agendamentos**: Consultas realizadas e agendadas
- **Prontuário Digital**: Upload e organização de documentos
- **Observações**: Notas importantes sobre o pacientesde a instalação até os detalhes técnicos de cada funcionalidade.

## Índice

- [Wiki do Projeto Cara Core Agendamento (CCA)](#wiki-do-projeto-cara-core-agendamento-cca)
  - [Índice](#índice)
  - [Visão Geral do Sistema](#visão-geral-do-sistema)
    - [Tecnologias Utilizadas](#tecnologias-utilizadas)
    - [Arquitetura](#arquitetura)
  - [Instalação e Configuração](#instalação-e-configuração)
    - [Pré-requisitos](#pré-requisitos)
    - [Passos para Instalação](#passos-para-instalação)
    - [Configuração para Produção](#configuração-para-produção)
  - [Funcionalidades Principais](#funcionalidades-principais)
    - [Gestão de Dentistas](#gestão-de-dentistas)
      - [Cadastro e Edição](#cadastro-e-edição)
      - [Busca e Filtros](#busca-e-filtros)
      - [Controle de Status](#controle-de-status)
    - [Gestão de Pacientes](#gestão-de-pacientes)
      - [Dados Armazenados](#dados-armazenados)
    - [Agendamento de Consultas](#agendamento-de-consultas)
      - [Fluxo de Agendamento](#fluxo-de-agendamento)
      - [Validações](#validações)
    - [Integração com WhatsApp](#integração-com-whatsapp)
      - [Funcionalidades Implementadas](#funcionalidades-implementadas)
      - [Benefícios](#benefícios)
      - [Como utilizar](#como-utilizar)
      - [Código de Exemplo](#código-de-exemplo)
  - [Versionamento do Banco de Dados](#versionamento-do-banco-de-dados)
    - [Flyway Migrations](#flyway-migrations)
    - [Scripts Consolidados](#scripts-consolidados)
    - [Boas Práticas](#boas-práticas)
  - [Testes e Qualidade](#testes-e-qualidade)
    - [Estrutura de Testes](#estrutura-de-testes)
    - [Configuração de Mocks](#configuração-de-mocks)
  - [FAQ e Solução de Problemas](#faq-e-solução-de-problemas)
    - [Perguntas Frequentes](#perguntas-frequentes)
    - [Problemas Comuns](#problemas-comuns)
  - [Contribuindo com o Projeto](#contribuindo-com-o-projeto)
    - [Como Contribuir](#como-contribuir)
    - [Padrões de Código](#padrões-de-código)
    - [Próximos Passos](#próximos-passos)

---

## Visão Geral do Sistema

O Cara Core Agendamento (CCA) é uma solução completa para gestão de agendamentos em consultórios odontológicos. O sistema foi desenvolvido com foco na usabilidade, eficiência e comunicação com o paciente.

### Tecnologias Utilizadas

- **Backend:** Java 17 + Spring Boot 3.2.6
- **Frontend:** Thymeleaf + Bootstrap 5.3.0
- **Banco de Dados:** PostgreSQL 15+
- **Segurança:** Spring Security + BCrypt
- **DevOps:** Docker + Maven

### Arquitetura

O sistema segue o padrão MVC (Model-View-Controller) com a seguinte estrutura:

```
src/main/
├── java/com/caracore/cca/
│   ├── config/             # Configurações Spring
│   ├── controller/         # Controllers MVC e REST
│   ├── dto/                # Data Transfer Objects
│   ├── model/              # Entidades JPA
│   ├── repository/         # Repositories
│   ├── service/            # Lógica de negócio
│   └── util/               # Utilitários
├── resources/
│   ├── templates/          # Templates Thymeleaf
│   ├── static/             # Recursos estáticos
│   └── application.yml     # Configurações da aplicação
```

---

## Instalação e Configuração

### Pré-requisitos

- Java 17+
- Docker e Docker Compose
- PostgreSQL 15+ (ou use o container Docker)
- Maven 3.8+

### Passos para Instalação

1. Clone o repositório:
   ```bash
   git clone https://github.com/chmulato/cara-core_cca.git
   cd cara-core_cca
   ```

2. Inicie o banco de dados:
   ```bash
   docker-compose up -d
   ```

3. Compile e execute a aplicação:
   ```bash
   mvn clean spring-boot:run
   ```

4. Acesse o sistema em: [http://localhost:8080](http://localhost:8080)

### Configuração para Produção

Para ambientes de produção, configure as seguintes variáveis de ambiente:

- `DB_HOST` - Host do PostgreSQL
- `DB_PORT` - Porta do PostgreSQL
- `DB_NAME` - Nome do banco de dados
- `DB_USERNAME` - Usuário do banco
- `DB_PASSWORD` - Senha do banco
- `SERVER_PORT` - Porta da aplicação (padrão: 8080)
- `SPRING_PROFILES_ACTIVE` - Profile ativo (dev, prod, test)

---

## Funcionalidades Principais

### Agendamento de Consultas

O sistema permite o agendamento de consultas de forma fácil e intuitiva, tanto pela recepção quanto pelo próprio paciente através da interface pública.

#### Fluxo de Agendamento

1. Acessar o formulário de novo agendamento
2. Selecionar paciente ou cadastrar novo
3. Confirmar/atualizar o telefone WhatsApp do paciente (obrigatório)
4. Selecionar dentista, data e hora
5. Definir duração e observações
6. Confirmar agendamento

#### Validações

- Verificação de disponibilidade de horário
- Validação de dias úteis (segunda a sexta)
- Horário comercial (8h às 18h)
- Formato correto de telefone WhatsApp

### Gestão de Pacientes

O módulo de pacientes permite o cadastro completo com dados pessoais, histórico de consultas e prontuário digital.

#### Dados Armazenados

- Nome completo
- E-mail
- Telefone WhatsApp (validado e formatado)
- Histórico de agendamentos
- Observações e condições especiais

### Integração com WhatsApp

Uma das principais funcionalidades do sistema é a integração direta com WhatsApp para comunicação eficiente com pacientes.

#### Funcionalidades Implementadas

- **Campo obrigatório de WhatsApp**: Telefone validado e formatado automaticamente
- **Atualização do cadastro**: Telefone atualizado automaticamente no cadastro do paciente
- **Integração com WhatsApp Web**: Botão que abre conversa diretamente pelo navegador
- **Formatação automática**: Conversão para o formato internacional compatível com a API do WhatsApp

#### Benefícios

- **Redução de faltas**: Lembretes eficientes reduzem o número de consultas perdidas
- **Comunicação rápida**: Envio de instruções pré e pós-procedimento
- **Melhor experiência**: Pacientes apreciam a facilidade de comunicação
- **Confirmações eficientes**: Confirmação de horários com antecedência

#### Como utilizar

1. Durante o agendamento, preencha ou confirme o telefone WhatsApp do paciente
2. Clique no ícone verde do WhatsApp ao lado do campo telefone
3. O navegador abrirá uma nova aba com o WhatsApp Web e a conversa iniciada
4. Envie a mensagem desejada ao paciente

![Exemplo de integração com WhatsApp](./img/whatsapp_integration_example.png)

#### Código de Exemplo

```javascript
// Função para gerar link do WhatsApp
function gerarLinkWhatsApp(telefone) {
    // Remove caracteres não numéricos
    const numeroLimpo = telefone.replace(/\D/g, '');
    
    // Verifica se o número tem pelo menos 10 dígitos (DDD + número)
    if (numeroLimpo.length >= 10) {
        // Formata o número para o WhatsApp (com código do Brasil +55)
        return `https://wa.me/55${numeroLimpo}`;
    }
    return '#';
}
```

---

## Versionamento do Banco de Dados

O sistema utiliza **Flyway** para versionamento e controle de migrations do banco de dados, garantindo consistência entre ambientes e facilidade de deploy.

### Flyway Migrations

As migrations estão organizadas na pasta `src/main/resources/db/migration/` seguindo o padrão:

```
V{versão}__{descrição}.sql
```

#### Exemplo de Structure:

```
db/migration/
├── V1__create_initial_schema.sql
├── V2__add_usuarios_table.sql
├── V3__add_dentistas_table.sql
├── V4__add_pacientes_table.sql
├── V5__add_agendamentos_table.sql
└── V10__consolidar_estrutura_banco.sql
```

### Scripts Consolidados

Para corrigir inconsistências e otimizar a estrutura, foi criado o script `V10__consolidar_estrutura_banco.sql` que:

- **Corrige constraints**: PKs, FKs e checks faltantes
- **Padroniza tipos**: Unifica tipos de dados entre tabelas
- **Otimiza índices**: Remove duplicados e adiciona índices necessários
- **Valida integridade**: Verifica referências entre tabelas

```sql
-- Exemplo do script V10
-- Corrigir tipo da coluna email na tabela usuarios
ALTER TABLE usuarios ALTER COLUMN email TYPE VARCHAR(100);

-- Adicionar constraint única para email
ALTER TABLE usuarios ADD CONSTRAINT uk_usuarios_email UNIQUE (email);

-- Criar índice para performance
CREATE INDEX idx_agendamentos_data_hora ON agendamentos(data_agendamento, hora_agendamento);
```

### Boas Práticas

1. **Versionamento Sequencial**: Sempre incrementar o número da versão
2. **Descrições Claras**: Usar nomes descritivos para as migrations
3. **Scripts Idempotentes**: Migrations devem ser executáveis múltiplas vezes
4. **Backup Antes de Migrations**: Sempre fazer backup antes de executar
5. **Testes em Ambiente de Dev**: Validar migrations antes do produção

#### Comandos Úteis:

```bash
# Executar migrations
mvn flyway:migrate

# Verificar status das migrations
mvn flyway:info

# Validar migrations pendentes
mvn flyway:validate

# Limpar banco (apenas desenvolvimento!)
mvn flyway:clean
```

---

## Testes e Qualidade

O projeto mantém alta qualidade através de uma suíte abrangente de testes automatizados, configurações de mock e práticas de TDD.

### Estrutura de Testes

```
src/test/java/com/caracore/cca/
├── config/                 # Configurações de teste
│   ├── TestWebMvcConfig.java       # Mocks para controllers
│   ├── TestDatabaseConfig.java     # Configuração de BD para testes
│   └── SecurityTestConfig.java     # Segurança em testes
├── model/                  # Testes de entidades
│   ├── DentistaTest.java
│   ├── PacienteTest.java
│   └── UsuarioTest.java
├── repository/             # Testes de repositórios
│   ├── DentistaRepositoryTest.java
│   └── UsuarioRepositoryTest.java
├── service/                # Testes de serviços
│   ├── DentistaServiceTest.java
│   └── InitServiceTest.java
└── controller/             # Testes de controllers
    ├── DentistaControllerTest.java
    └── LoginControllerTest.java
```

### Configuração de Mocks

Para isolar os testes de dependências externas e interceptors, utilizamos configurações de mock:

```java
@TestConfiguration
public class TestWebMvcConfig {
    @Bean @Primary
    public UserActivityLogger mockUserActivityLogger() {
        return Mockito.mock(UserActivityLogger.class);
    }
    
    @Bean @Primary
    public UserActivityInterceptor mockUserActivityInterceptor() {
        return Mockito.mock(UserActivityInterceptor.class);
    }
}
```

#### Tipos de Teste Implementados:

- **@SpringBootTest**: Testes de integração com contexto completo
- **@WebMvcTest**: Testes focados na camada web
- **@DataJpaTest**: Testes específicos de repositórios
- **@MockBean**: Mocking de serviços e dependências

#### Execução de Testes:

```bash
# Executar todos os testes
mvn test

# Executar testes específicos
mvn test -Dtest=DentistaServiceTest

# Executar testes com relatório de cobertura
mvn test jacoco:report

# Executar apenas testes de um módulo
mvn test -Dtest="*Dentista*Test"
```

#### Cobertura de Código:

O projeto mantém cobertura mínima de **80%** em:
- Classes de modelo (entities)
- Serviços de negócio
- Controladores REST e Web
- Repositórios customizados

---

## FAQ e Solução de Problemas

### Perguntas Frequentes

**P: Como cadastrar um novo dentista no sistema?**  
R: Acesse o menu "Dentistas" > "Novo Dentista", preencha os dados obrigatórios (nome, email, CRO) e clique em "Salvar". O sistema validará automaticamente se o email é único e se o formato do CRO está correto.

**P: Como desativar um dentista sem excluir o cadastro?**  
R: Na listagem de dentistas, clique no toggle de status ao lado do nome do profissional ou edite o cadastro e altere o status para "Inativo". Dentistas inativos não aparecem na seleção de agendamentos.

**P: É possível filtrar dentistas por especialidade?**  
R: Sim, na tela de listagem há um campo de busca que permite filtrar por nome, email, especialidade e status. A busca é feita em tempo real conforme você digita.

**P: Como atualizar o telefone de um paciente existente?**  
R: Na tela de agendamento, ao selecionar um paciente existente, o sistema buscará automaticamente o telefone cadastrado. Você pode então atualizá-lo se necessário, e o sistema salvará o novo número.

**P: O WhatsApp Web não abre quando clico no botão. O que fazer?**  
R: Verifique se:

1. O navegador não está bloqueando popups
2. O número de telefone está no formato correto (com DDD)
3. O WhatsApp Web já está autenticado no navegador

**P: Como executar as migrations do banco de dados manualmente?**  
R: Use os comandos Maven:
- `mvn flyway:migrate` - Executa migrations pendentes
- `mvn flyway:info` - Mostra status das migrations
- `mvn flyway:validate` - Valida migrations

**P: Como executar os testes do sistema?**  
R: Use os comandos:
- `mvn test` - Executa todos os testes
- `mvn test -Dtest=DentistaServiceTest` - Executa testes específicos
- `mvn test jacoco:report` - Gera relatório de cobertura

**P: É possível enviar mensagens automáticas pelo WhatsApp?**  
R: A versão atual suporta apenas a abertura da conversa. O envio automático está planejado para futuras atualizações através da API oficial do WhatsApp Business.

### Problemas Comuns

```markdown
|--------------------------------|--------------------------------------------------------------------|
| Problema                       | Solução                                                            |
|--------------------------------|--------------------------------------------------------------------|
| Erro de formatação do telefone | Verifique se o número inclui DDD e segue o formato (99) 99999-9999 |
| Botão do WhatsApp desabilitado | O número precisa ter pelo menos 10 dígitos válidos                 |
| Mensagens não chegam           | Confirme se o número está correto e se o paciente utiliza WhatsApp |
|--------------------------------|--------------------------------------------------------------------|
```

---

## Contribuindo com o Projeto

### Como Contribuir

1. Faça um fork do repositório
2. Crie uma branch para sua contribuição (`git checkout -b feature/nova-funcionalidade`)
3. Faça commit das alterações (`git commit -m 'Implementa nova funcionalidade'`)
4. Envie para o GitHub (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

### Padrões de Código

- Siga as convenções de nomenclatura do Java
- Documente métodos públicos com JavaDoc
- Escreva testes unitários para novas funcionalidades
- Mantenha a cobertura de testes acima de 80%

### Próximos Passos

#### Funcionalidades Implementadas Recentemente

✅ **Sistema de Gestão de Dentistas Completo:**
- Interface padronizada com Bootstrap 5.3.0
- Busca avançada e filtros por especialidade
- Controle de status ativo/inativo
- Formulários responsivos com validação

✅ **Padronização Visual:**
- Templates unificados seguindo o design system
- Remoção de CSS específico e migração para classes globais
- Componentes reutilizáveis (cards, badges, botões)
- Responsividade aprimorada para todos os dispositivos

✅ **Versionamento do Banco de Dados:**
- Script consolidado V10 para correção de inconsistências
- Documentação completa das migrations
- Práticas padronizadas para futuras atualizações

✅ **Estrutura de Testes Robusta:**
- Cobertura de testes para módulo de dentistas
- Configuração de mocks para isolamento
- Testes unitários e de integração

#### Próximas Funcionalidades Planejadas

- **Envio automático de lembretes por WhatsApp**
  - Integração com API oficial do WhatsApp Business
  - Templates personalizáveis para diferentes tipos de mensagem
  - Agendamento de envios automáticos

- **Dashboard de comunicações com pacientes**
  - Histórico de mensagens enviadas
  - Métricas de engajamento
  - Relatórios de efetividade dos lembretes

- **Agenda visual avançada**
  - Visualização em calendário (dia/semana/mês)
  - Drag & drop para reagendamentos
  - Sincronização com Google Calendar

- **Prontuário digital completo**
  - Upload de imagens e documentos
  - Editor de texto rico para anotações
  - Histórico médico detalhado

- **Relatórios gerenciais**
  - Dashboard de produtividade por dentista
  - Análise de agendamentos e cancelamentos
  - Relatórios financeiros básicos

- **Aplicativo móvel**
  - App nativo para Android e iOS
  - Notificações push
  - Acesso offline limitado

---

**Última atualização:** 2 de julho de 2025  
**Responsável pela documentação:** Equipe Cara Core  
**Versão do Sistema:** 1.0.0-beta  
**Cobertura de Testes:** 85%+  
**Status do MVP:** Em desenvolvimento (Q3 2025)

---

*Esta documentação é mantida em sincronia com o desenvolvimento do sistema. Para sugestões de melhorias ou correções, abra uma issue no repositório do projeto.*
