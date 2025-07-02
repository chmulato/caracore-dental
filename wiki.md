# Wiki do Projeto Cara Core Agendamento (CCA)

Bem-vindo à wiki do projeto Cara Core Agendamento! Este espaço contém a documentação completa do sistema, desde a instalação até os detalhes técnicos de cada funcionalidade.

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
    - [Agendamento de Consultas](#agendamento-de-consultas)
      - [Fluxo de Agendamento](#fluxo-de-agendamento)
      - [Validações](#validações)
    - [Gestão de Pacientes](#gestão-de-pacientes)
      - [Dados Armazenados](#dados-armazenados)
    - [Integração com WhatsApp](#integração-com-whatsapp)
      - [Funcionalidades Implementadas](#funcionalidades-implementadas)
      - [Benefícios](#benefícios)
      - [Como utilizar](#como-utilizar)
      - [Código de Exemplo](#código-de-exemplo)
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

## FAQ e Solução de Problemas

### Perguntas Frequentes

**P: Como atualizar o telefone de um paciente existente?**  
R: Na tela de agendamento, ao selecionar um paciente existente, o sistema buscará automaticamente o telefone cadastrado. Você pode então atualizá-lo se necessário, e o sistema salvará o novo número.

**P: O WhatsApp Web não abre quando clico no botão. O que fazer?**  
R: Verifique se:
1. O navegador não está bloqueando popups
2. O número de telefone está no formato correto (com DDD)
3. O WhatsApp Web já está autenticado no navegador

**P: É possível enviar mensagens automáticas pelo WhatsApp?**  
R: A versão atual suporta apenas a abertura da conversa. O envio automático está planejado para futuras atualizações através da API oficial do WhatsApp Business.

### Problemas Comuns

| Problema | Solução |
|----------|---------|
| Erro de formatação do telefone | Verifique se o número inclui DDD e segue o formato (99) 99999-9999 |
| Botão do WhatsApp desabilitado | O número precisa ter pelo menos 10 dígitos válidos |
| Mensagens não chegam | Confirme se o número está correto e se o paciente utiliza WhatsApp |

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

- Implementar envio automático de lembretes por WhatsApp
- Criar templates personalizáveis para mensagens
- Adicionar dashboard de comunicações com pacientes
- Implementar integração com a API oficial do WhatsApp Business

---

**Última atualização:** 2 de julho de 2025  
**Responsável pela documentação:** Equipe Cara Core  
**Versão do Sistema:** 0.9.5-beta
