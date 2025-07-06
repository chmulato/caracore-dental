<div align="center">
  <img src="./img/sistema_cca.png" alt="Cara Core Agendamento (CCA)" width="100%">
</div>

# Cara Core Agendamento (CCA)

Sistema de agendamento para consultórios odontológicos desenvolvido com Spring Boot, Java 17 e PostgreSQL.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![H2 Database](https://img.shields.io/badge/H2-Database-yellow.svg)](https://www.h2database.com/)
[![Swagger](https://img.shields.io/badge/Swagger-API-green.svg)](https://swagger.io/)

## ✅ **CONFIGURAÇÃO COMPLETA - PRONTO PARA USO!**

### **Execução Rápida**

```bash
# Executar aplicação no perfil desenvolvimento (H2 - padrão)
mvn spring-boot:run

# Executar com PostgreSQL local
mvn spring-boot:run -Dspring.profiles.active=local

# Acessar aplicação
http://localhost:8080

# Acessar documentação da API (Swagger)
http://localhost:8080/swagger-ui.html

# Acessar H2 Console (perfil dev)
http://localhost:8080/h2-console
```

### 👥 **Usuários de Teste Criados**

- **Admin**: `suporte@caracore.com.br` / `admin123`
- **Dentista**: `dentista@caracore.com.br` / `admin123`
- **Recepcionista**: `recepcao@caracore.com.br` / `admin123`
- **Paciente**: `paciente@caracore.com.br` / `admin123`

### **Configuração de Ambientes**

- **Desenvolvimento**: H2 Database (em memória) - Padrão
- **Local**: PostgreSQL local (para desenvolvimento com BD real)
- **Homologação**: PostgreSQL com variáveis de ambiente
- **Produção**: PostgreSQL otimizado e seguro
- **Testes**: H2 com configurações específicas

## **Como Subir a Aplicação (Desenvolvimento e Produção)**

### **1. Subir o banco de dados PostgreSQL com Docker**

```bash
docker-compose up -d
```

### **2. Compilar e executar a aplicação em modo desenvolvimento**

```bash
mvn clean spring-boot:run
```

Ou, para rodar com um profile específico (ex: dev):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

> **Nota:**
> A aplicação utiliza WebJars para Bootstrap (5.3.0), jQuery (3.7.0) e Bootstrap Icons (1.13.1), 
> o que elimina a necessidade de CDNs externas e garante que a aplicação funcione offline.

### **Autenticação e Autorização**

- **Spring Security** com formulário de login
- **BCrypt** para hash de senhas (fator de custo 10)
- **UserDetailsService** personalizado para autenticação contra banco de dados
- **Session-based** authentication
- **CSRF** protection habilitado
- **Roles:** ADMIN, DENTIST, RECEPTIONIST, PATIENT
- **Armazenamento seguro de senha:** Hash BCrypt com salt único por usuário
- **Utilitários:** BCryptUtil e VerificarHash para gerenciamento de senhas
- **Migrations:** Criação automatizada de tabela de usuários via Flyway
- **Login Seguro:** Formulário protegido com Bootstrap e validação client/server-side

## **MVP em Andamento (Q3 2025)**

O MVP do **Cara Core Agendamento (CCA)** está em desenvolvimento e já conta com as seguintes funcionalidades principais:

- [x] **Autenticação e autorização** com Spring Security e BCrypt
- [x] **Gestão de usuários** (Admin, Dentista, Recepcionista, Paciente)
- [x] **Cadastro e gestão de pacientes** com formulário responsivo e WhatsApp obrigatório
- [x] **Cadastro e gestão de dentistas/profissionais** com horários e especialidades
- [x] **Agendamento de consultas** com interface web completa
- [x] **Sistema de consultas** com CRUD completo e controle de acesso
- [x] **Reagendamento e cancelamento** de consultas com histórico
- [x] **Controle de conflitos** de horários automatizado
- [x] **Dashboard de consultas** com filtros e métricas
- [x] **Controle de consentimento LGPD** via WhatsApp integrado
- [x] **Sistema de consentimento LGPD** com envio e confirmação via WhatsApp
- [x] **Testes unitários** com 100% de aprovação em módulos críticos
- [ ] Visualização de agenda por dia/semana/mês
- [ ] Upload de imagens odontológicas no prontuário
- [ ] Relatórios básicos de agendamentos
- [ ] Notificações por email (básico)
- [ ] Interface pública para agendamento online
- [x] **Controle de acesso por perfil** baseado em roles
- [ ] LGPD: consentimento e política de privacidade
- [x] **Integração com WhatsApp** para comunicação com pacientes

### **Funcionalidades Implementadas Recentemente:**

✅ **Gestão de Pacientes Aprimorada:**

- Campo de WhatsApp obrigatório em todos os cadastros
- Sistema de consentimento LGPD integrado via WhatsApp
- Envio automático de termos LGPD via WhatsApp Web
- Controle de confirmação de recebimento do consentimento
- Interface visual para status de conformidade LGPD
- **Nome Social:** Implementação conforme Portaria nº 2.836/2011 do Ministério da Saúde
- **Gênero:** Campo de autodeclaração com opções padronizadas (Feminino, Masculino, Não-binário, Outro, Prefere não informar)
- **Legislação:** Atendimento às diretrizes do SUS para identidade de gênero e nome social

✅ **Sistema de Gestão de Dentistas:**

- Cadastro completo com especialidades, CRO, horários de atendimento
- Interface responsiva seguindo padrão Bootstrap 5.3.0
- Busca avançada por nome, especialidade, email
- Controle de status (ativo/inativo)
- Validações de formulário e máscaras de entrada

✅ **Versionamento do Banco de Dados:**

- Flyway migrations organizadas e consolidadas
- Script V10 para correção de inconsistências
- Documentação completa em `doc/VERSIONAMENTO_BANCO_ANALISE.md`
- Estrutura padronizada para futuras migrações

✅ **Melhorias na Interface:**

- Design unificado com Bootstrap 5.3.0 e Bootstrap Icons
- Navegação consistente entre todos os módulos
- Cards com shadow e elementos visuais modernos
- Responsividade para dispositivos móveis
- Remoção de CSS específico e padronização de estilos

✅ **Estrutura de Testes Robusta:**

- **Total de Testes:** 306 testes unitários com 100% de aprovação (Validado em Julho 2025)
- **Cobertura Completa:** Controllers (165), Services (54), Models/DTOs (49), Repositories (6), Segurança (10), Utilitários (22)
- **ConsultasControllerTest:** 18 testes unitários validando todas as operações do controller
- **AgendamentoServiceTest:** 19 testes unitários cobrindo toda a lógica de negócio
- **PacienteControllerTest:** 37 testes validando CRUD completo e LGPD
- **Testes de Segurança:** 22 testes cobrindo autenticação, autorização e criptografia
- Configuração de mocks para isolamento de dependências
- Testes unitários e de integração
- Relatórios de cobertura com JaCoCo

✅ **Gestão de Agendamentos Aprimorada:**

- **Sistema de Consultas:** CRUD completo com validações e controle de acesso
- **Controle de Conflitos:** Verificação automática de horários ocupados
- **Reagendamento:** Funcionalidade completa com histórico de alterações
- **Status Inteligente:** Controle de estados (Agendado, Confirmado, Realizado, Cancelado)
- **Permissões por Perfil:** Acesso diferenciado por tipo de usuário
- **Integração WhatsApp:** Comunicação direta com pacientes

✅ **Qualidade de Código:**

- **Testes Unitários:** 100% de aprovação em módulos críticos
- **Mocks Adequados:** Isolamento de dependências para testes confiáveis
- **Validações Robustas:** Controle de entrada e saída de dados
- **Logs Estruturados:** Rastreabilidade completa de operações
- **Tratamento de Erros:** Respostas adequadas para cenários de falha

✅ **Documentação Atualizada:**

- Wiki completa com todos os módulos do sistema
- README.md atualizado com guias de instalação e uso
- Documentação de APIs e endpoints
- Guias de contribuição e boas práticas
- Documentação de testes e práticas de qualidade

Funcionalidades previstas para o MVP:

- [ ] Prontuário digital completo
- [ ] Relatórios financeiros
- [ ] Dashboard de produtividade
- [ ] Notificações automáticas via WhatsApp
- [ ] Backup automático

---

## **Como Executar o MVP**

1. Suba o banco de dados PostgreSQL com Docker:

```bash
docker-compose up -d
```

2. Compile e execute a aplicação:

```bash
mvn clean spring-boot:run
```

3. Acesse o sistema em: http://localhost:8080

---

## **Como Subir a Aplicação (Desenvolvimento e Produção)**

### **1. Subir o banco de dados PostgreSQL com Docker**

```bash
docker-compose up -d
```

### **2. Compilar e executar a aplicação em modo desenvolvimento**

```bash
mvn clean spring-boot:run
```

Ou, para rodar com um profile específico (ex: dev):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### **3. Gerar o JAR e executar em produção**

```bash
mvn clean package -Pprod
java -jar target/cca-0.0.1-SNAPSHOT.jar
```

### **4. Acessar a aplicação**

- **Sistema:** http://localhost:8080
- **Agendamento Online:** http://localhost:8080/agendar

### **5. Usuários e Senhas Padrões**

O sistema é inicializado com os seguintes usuários padrões para teste e demonstração:

```markdown
|----------------|----------------------------|------------|----------------------------------|
| **Perfil**     | **Email**                  | **Senha**  | **Observações**                  |
|----------------|----------------------------|------------|----------------------------------|
| Administrador  | suporte@caracore.com.br    | admin123   | Acesso total ao sistema          |
| Dentista       | dentista@caracore.com.br   | admin123   | Gerencia agenda e prontuários    |
| Recepcionista  | recepcao@caracore.com.br   | admin123   | Gerencia agenda e pacientes      |
| Paciente       | paciente@caracore.com.br   | admin123   | Acesso ao portal do paciente     |
|----------------|----------------------------|------------|----------------------------------|
```

> **Importante:** Altere as senhas padrões ao implantar em ambiente de produção!
> 
> **Nota:** Os usuários padrão são criados automaticamente na inicialização do sistema **apenas se não existirem**. Usuários existentes **nunca são sobrescritos** durante a inicialização do sistema, preservando quaisquer alterações feitas nos dados dos usuários ou nas senhas.
>
> Para gerenciar usuários padrão, administradores podem:
> - Verificar e recriar usuários padrão faltantes: `/admin/sistema/verificar-usuarios`
> - Redefinir senha de usuário específico: `/admin/sistema/resetar-senha/{email}`
> - Redefinir todas as senhas padrão: `/admin/sistema/redefinir-todas-senhas-padrao`
> - Verificar status dos usuários padrão: `/admin/sistema/status-usuarios-padrao`

---

## **Roadmap Resumido do MVP**

- [ ] Sistema de agendamento básico
- [ ] Gestão de pacientes e profissionais
- [ ] Agendamento online
- [ ] Prontuário digital
- [ ] Relatórios básicos

---

Para detalhes completos sobre instalação, configuração, funcionalidades e desenvolvimento, consulte a **[Wiki do Projeto](wiki.md)** que contém documentação abrangente incluindo:

- Guia completo de gestão de dentistas e pacientes
- Documentação do versionamento do banco de dados
- Estrutura de testes e práticas de qualidade
- Integração com WhatsApp e outras funcionalidades
- FAQ e solução de problemas
- Roadmap e próximos passos

---

## **Sobre o Projeto**

O **Cara Core Agendamento (CCA)** é uma solução completa para gestão de agendamentos em consultórios odontológicos, desenvolvida pela **Cara Core Informática**. O sistema oferece uma interface moderna e intuitiva para profissionais de saúde, pacientes e administradores.

### **Principais Funcionalidades**

- **Agenda Inteligente**: Calendário interativo com visualização por dia, semana e mês
- **Gestão de Pacientes**: Cadastro completo com histórico e prontuário digital
- **Agendamento Online**: Interface pública para pacientes agendarem consultas
- **Prontuário Digital**: Upload e organização de imagens odontológicas
- **Relatórios Gerenciais**: Métricas de produtividade e análises financeiras
- **Sistema de Notificações**: Lembretes automáticos por email, SMS e integração direta com WhatsApp
- **Multi-usuário**: Controle de acesso baseado em roles (Admin, Dentista, Recepcionista)
- **LGPD Compliant**: Conformidade com a Lei Geral de Proteção de Dados

### **Tecnologias Utilizadas**

**Backend:**

- Java 17 (OpenJDK LTS)
- Spring Boot 3.2.6
- Spring Security 6
- Spring Data JPA
- Flyway (Migrations)
- PostgreSQL 15+
- Maven 3.8+

**Frontend:**

- Thymeleaf 3.1+
- Bootstrap 5.3.0
- Bootstrap Icons 1.13.1
- jQuery 3.7.0
- FullCalendar.js
- DataTables
- Chart.js

**Deploy:**

- Tomcat Embedded
- Docker (opcional)
- Java JAR executável

## **Início Rápido**

### **Pré-requisitos**

- **Java 17+** (OpenJDK recomendado)
- **Maven 3.8+**
- **PostgreSQL 15+**
- **Git**

### **Instalação**

1. **Clone o repositório:**

```bash
git clone https://github.com/chmulato/cara-core_cca.git
cd cca
```

2. **Configure o banco de dados:**

```sql
-- Conecte no PostgreSQL como superuser
CREATE DATABASE cca_db;
CREATE USER cca_user WITH PASSWORD 'cca_password';
GRANT ALL PRIVILEGES ON DATABASE cca_db TO cca_user;
```

3. **Configure as variáveis de ambiente:**

```bash
# Linux/Mac
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=cca_db
export DB_USERNAME=cca_user
export DB_PASSWORD=cca_password
export SPRING_PROFILES_ACTIVE=dev

# Windows
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=cca_db
set DB_USERNAME=cca_user
set DB_PASSWORD=cca_password
set SPRING_PROFILES_ACTIVE=dev
```

4. **Execute o projeto:**

```bash
# Compilar e executar
mvn clean spring-boot:run

# Ou gerar JAR e executar
mvn clean package
java -jar target/cca-0.0.1-SNAPSHOT.jar
```

5. **Acesse a aplicação:**

- **Sistema:** http://localhost:8080
- **Agendamento Online:** http://localhost:8080/agendar
- **Login padrão:** suporte@caracore.com.br / admin123

## **Configuração**

### **Profiles de Ambiente**

O sistema suporta diferentes profiles para diferentes ambientes:

```yaml
# application-dev.yml (Desenvolvimento)
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cca_db
    username: ${DB_USERNAME:cca_user}
    password: ${DB_PASSWORD:cca_password}
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: validate
  thymeleaf:
    cache: false

# application-prod.yml (Produção)
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate
  thymeleaf:
    cache: true
```

### **Variáveis de Ambiente**

```markdown
|--------------------------|---------------------|-----------|
| Variável                 | Descrição           | Padrão    |
|--------------------------|---------------------|-----------|
| `DB_HOST`                | Host do PostgreSQL  | localhost |
| `DB_PORT`                | Porta do PostgreSQL | 5432      |
| `DB_NAME`                | Nome do banco       | cca_db    |
| `DB_USERNAME`            | Usuário do banco    | cca_user  |
| `DB_PASSWORD`            | Senha do banco      | -         |
| `SERVER_PORT`            | Porta da aplicação  | 8080      |
| `SPRING_PROFILES_ACTIVE` | Profile ativo       | dev       |
|--------------------------|---------------------|-----------|
```

## **Dependências WebJar**

O projeto utiliza WebJars para gerenciar bibliotecas front-end através do Maven:

```xml
<!-- Bootstrap 5.3.0 -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>bootstrap</artifactId>
    <version>5.3.0</version>
</dependency>

<!-- jQuery 3.7.0 -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.7.0</version>
</dependency>

<!-- Bootstrap Icons 1.13.1 -->
<dependency>
    <groupId>org.webjars.npm</groupId>
    <artifactId>bootstrap-icons</artifactId>
    <version>1.13.1</version>
</dependency>

<!-- WebJars Locator (facilita o acesso aos recursos) -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>webjars-locator-core</artifactId>
</dependency>
```

### **Utilização nos Templates**

Nos templates Thymeleaf, os recursos são referenciados da seguinte forma:

```html
<!-- CSS do Bootstrap -->
<link rel="stylesheet" th:href="@{/webjars/bootstrap/5.3.0/css/bootstrap.min.css}">

<!-- CSS do Bootstrap Icons -->
<link rel="stylesheet" th:href="@{/webjars/bootstrap-icons/1.13.1/font/bootstrap-icons.css}">

<!-- jQuery -->
<script th:src="@{/webjars/jquery/3.7.0/jquery.min.js}"></script>

<!-- Bootstrap JS -->
<script th:src="@{/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js}"></script>
```

### **Configuração de WebJars**

O projeto utiliza [webjars-locator-core](https://www.webjars.org/documentation#webjars-locator-core) que automaticamente resolve as dependências de WebJars sem necessidade de configuração manual. Embora exista um arquivo `WebjarConfig.java` na estrutura do projeto, ele permanece vazio pois o Spring Boot já fornece uma configuração padrão eficiente para os WebJars.

A resolução de caminhos para arquivos WebJar é feita automaticamente, permitindo o uso simplificado nos templates Thymeleaf conforme mostrado na seção "Utilização nos Templates" abaixo.

## **Estrutura do Projeto**

```markdown
src/main/
├── java/com/caracore/cca/
│   ├── config/             # Configurações Spring
│   ├── controller/         # Controllers MVC e REST
│   ├── dto/                # Data Transfer Objects
│   ├── entity/             # Entidades JPA
│   ├── repository/         # Repositories
│   ├── service/            # Lógica de negócio
│   ├── util/               # Utilitários
│   └── CcaApplication.java # Classe principal
├── resources/
│   ├── templates/          # Templates Thymeleaf
│   │   ├── fragments/      # Fragmentos reutilizáveis (layout, navbar)
│   │   ├── index.html      # Página inicial
│   │   └── login.html      # Página de login
│   ├── static/             # Recursos estáticos
│   │   ├── css/            # Estilos customizados
│   │   ├── js/             # JavaScript customizado
│   │   └── img/            # Imagens (logo, favicon)
│   ├── db/migration/       # Scripts Flyway
│   └── application.yml     # Configurações
└── test/                   # Testes unitários e integração
```

## **Estrutura dos Testes**

O projeto inclui testes abrangentes para todos os componentes principais:

```
src/test/
├── java/com/caracore/cca/
│   ├── config/                     # Configurações de teste
│   │   ├── TestConfig.java         # Configuração geral para testes
│   │   ├── TestDatabaseConfig.java # Configuração de banco de dados para testes
│   │   ├── SecurityTestConfig.java # Configuração de segurança para testes
│   │   └── LoginTestConfig.java    # Configuração específica para testes de login
│   ├── controller/                 # Testes de controllers
│   ├── service/                    # Testes de serviços
│   ├── repository/                 # Testes de repositórios
│   └── util/                       # Testes de utilitários
└── resources/
    ├── application-test.properties # Configurações para ambiente de teste
    ├── data.sql                    # Dados iniciais para testes
    └── schema-test.sql             # Schema para testes
```

### **Execução de Testes Específicos**

```bash
# Executar todos os testes
mvn test

# Executar um teste específico
mvn test -Dtest=LoginControllerTest

# Executar testes de um pacote
mvn test -Dtest="com.caracore.cca.controller.*Test"
```

## **Testes e Qualidade do Código**

O projeto mantém alta qualidade através de uma suíte abrangente de testes automatizados:

### **Cobertura de Testes**

✅ **Testes Unitários:**
- **Modelos (Entities):** Validação de campos, constraints e comportamentos
- **Repositórios:** Testes de queries personalizadas e operações CRUD
- **Serviços:** Lógica de negócio e regras de validação
- **Controladores:** Endpoints REST e responses HTTP

✅ **Testes de Integração:**
- **Spring Boot Test:** Testes com contexto completo da aplicação
- **@WebMvcTest:** Testes focados na camada web
- **@DataJpaTest:** Testes específicos da camada de persistência

### **Estrutura de Testes**

```
src/test/java/
└── com/caracore/cca/
    ├── model/
    │   ├── DentistaTest.java        # Testes do modelo Dentista
    │   ├── UsuarioTest.java         # Testes do modelo Usuario
    │   └── PacienteTest.java        # Testes do modelo Paciente
    ├── repository/
    │   ├── DentistaRepositoryTest.java    # Testes de repositório
    │   └── UsuarioRepositoryTest.java     # Queries personalizadas
    ├── service/
    │   ├── DentistaServiceTest.java       # Lógica de negócio
    │   └── InitServiceTest.java           # Serviços de inicialização
    ├── controller/
    │   ├── DentistaControllerTest.java    # Controllers web
    │   └── LoginControllerTest.java       # Autenticação
    └── config/
        ├── TestWebMvcConfig.java          # Configuração para testes
        └── SecurityConfigTest.java        # Testes de segurança
```

### **Configuração de Testes**

**TestWebMvcConfig.java** - Configuração de mocks para testes:
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

### **Execução de Testes**

```bash
# Executar todos os testes
mvn test

# Executar testes específicos do módulo dentistas
mvn test -Dtest="*Dentista*Test"

# Executar testes com relatório de cobertura
mvn clean test jacoco:report

# Executar apenas testes unitários (sem integração)
mvn test -Dtest="!*IntegrationTest"
```

### **Boas Práticas Implementadas**

- **Isolamento de testes:** Cada teste é independente
- **Mocking apropriado:** Spring Security e interceptors mockados
- **Profiles de teste:** Configuração específica para testes
- **Dados de teste:** Seeds controlados via `data.sql`
- **Cobertura:** Relatórios de cobertura com JaCoCo

## **Comandos Úteis**

### **Desenvolvimento**

```bash
# Executar em modo desenvolvimento
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Hot reload (DevTools)
mvn spring-boot:run

# Executar testes
mvn test

# Executar testes de integração
mvn verify
```

### **Banco de Dados**

```bash
# Executar migrations
mvn flyway:migrate

# Limpar banco (cuidado!)
mvn flyway:clean

# Informações das migrations
mvn flyway:info

# Validar migrations
mvn flyway:validate
```

### **Build e Deploy**

```bash
# Gerar JAR para produção
mvn clean package -Pprod

# Executar JAR
java -jar target/cca-0.0.1-SNAPSHOT.jar

# Build com pulo de testes (não recomendado)
mvn clean package -DskipTests
```

## **API REST**

O sistema oferece APIs REST para integração:

### **Endpoints Principais**

```bash
# Agendamentos
GET    /api/appointments                 # Listar agendamentos
POST   /api/appointments                 # Criar agendamento
GET    /api/appointments/{id}            # Buscar agendamento
PUT    /api/appointments/{id}            # Atualizar agendamento
DELETE /api/appointments/{id}            # Cancelar agendamento

# Pacientes
GET    /api/patients                     # Listar pacientes
POST   /api/patients                     # Criar paciente
GET    /api/patients/{id}                # Buscar paciente
PUT    /api/patients/{id}                # Atualizar paciente

# Profissionais
GET    /api/professionals                # Listar profissionais
GET    /api/professionals/{id}/schedule  # Agenda do profissional
GET    /api/professionals/{id}/available # Horários disponíveis

# Agendamento Online (Público)
GET    /api/public/professionals         # Profissionais disponíveis
GET    /api/public/services              # Serviços oferecidos
GET    /api/public/available-slots       # Horários livres
POST   /api/public/appointments          # Agendar (público)
```

### **Autenticação**

As APIs protegidas requerem autenticação via session ou JWT:

```bash
# Login
POST /api/auth/login
Content-Type: application/json
{
  "username": "suporte@caracore.com.br",
  "password": "senha123"
}

# Logout
POST /api/auth/logout
```

### **Endpoints REST Administrativos**

O sistema possui endpoints REST para administração do sistema, acessíveis apenas por usuários com perfil ADMIN:

#### Gerenciamento de Usuários Padrão

O sistema segue a política de **não sobrescrever usuários existentes** durante a inicialização. Entretanto, os administradores podem forçar a atualização ou recriação dos usuários padrão através dos seguintes endpoints:

| **Método** | **Endpoint**                                | **Descrição**                                     |
|------------|------------------------------------------|--------------------------------------------------|
| POST       | /admin/sistema/verificar-usuarios          | Verifica e recria usuários padrão faltantes      |
| POST       | /admin/sistema/resetar-senha/{email}       | Redefine a senha de um usuário para valor padrão |
| POST       | /admin/sistema/redefinir-todas-senhas-padrao | Redefine todas as senhas de usuários padrão    |
| POST       | /admin/sistema/status-usuarios-padrao      | Obtém status de todos os usuários padrão        |

**Exemplos de uso:**

```bash
# Verificar usuários padrão (requer autenticação como ADMIN)
curl -X POST http://localhost:8080/admin/sistema/verificar-usuarios -H "Cookie: JSESSIONID=sua_session_id"

# Redefinir senha de usuário específico (requer autenticação como ADMIN)
curl -X POST http://localhost:8080/admin/sistema/resetar-senha/dentista@teste.com -H "Cookie: JSESSIONID=sua_session_id"
```

---

## **Documentação Técnica**

- 📚 **[Configuração de Ambientes](doc/CONFIGURACAO_AMBIENTES.md)** - Guia completo dos arquivos application.yml para diferentes ambientes
- 🔗 **[Documentação da API - Swagger](doc/SWAGGER_README.md)** - Como usar e testar os endpoints da API
- 📋 **[Versionamento do Banco](doc/VERSIONAMENTO_BANCO_ANALISE.md)** - Análise e gestão de migrations
- 🧪 **[Estrutura de Testes](doc/status_consultas_agendadas.md)** - Documentação de testes e qualidade

## **Links Úteis**

- 🌐 **Swagger UI (Desenvolvimento)**: http://localhost:8080/swagger-ui.html
- 📊 **API Docs**: http://localhost:8080/api-docs
- 🔍 **Console H2 (Dev)**: http://localhost:8080/h2-console

---

## **Testes**

### **Executar Testes**

```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=PatientServiceTest

# Testes de integração
mvn verify

# Testes com cobertura
mvn test jacoco:report
```

### **Relatório de Cobertura**

Após executar os testes com JaCoCo, o relatório estará disponível em:
`target/site/jacoco/index.html`

## **Deploy**

### **Deploy Local**

```bash
# Desenvolvimento
mvn spring-boot:run

# Produção (JAR)
mvn clean package -Pprod
java -jar target/cca-0.0.1-SNAPSHOT.jar
```

### **Deploy com Docker**

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim
COPY target/cca-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Build da imagem
docker build -t cca:latest .

# Executar container
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_USERNAME=cca_user \
  -e DB_PASSWORD=cca_password \
  cca:latest
```

### **Deploy em Produção**

1. **Servidor de Aplicação:
```bash
# Copiar JAR para servidor
scp target/cca-0.0.1-SNAPSHOT.jar user@servidor:/opt/cca/

# Executar em background
nohup java -jar /opt/cca/cca-0.0.1-SNAPSHOT.jar > /opt/cca/logs/app.log 2>&1 &
```

2. **Reverse Proxy (Nginx):**

```nginx
server {
    listen 80;
    server_name exemplo.com;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    location /static/ {
        root /opt/cca/;
        expires 1y;
    }
}
```

## **Monitoramento**

### **Spring Boot Actuator**

Endpoints de monitoramento disponíveis:

```bash
# Health check
GET /actuator/health

# Métricas
GET /actuator/metrics

# Informações da aplicação
GET /actuator/info

# Logs
GET /actuator/loggers
```

### **Logs**

```bash
# Configuração de logs (logback-spring.xml)
# Desenvolvimento: Console
# Produção: Arquivo rotativo

# Localização dos logs
./logs/cca-application.log
```

## **Segurança**

### **Autenticação e Autorização**

- **Spring Security** com formulário de login
- **BCrypt** para hash de senhas (fator de custo 10)
- **UserDetailsService** personalizado para autenticação contra banco de dados
- **Session-based** authentication
- **CSRF** protection habilitado
- **Roles:** ADMIN, DENTIST, RECEPTIONIST, PATIENT
- **Armazenamento seguro de senha:** Hash BCrypt com salt único por usuário
- **Utilitários:** BCryptUtil e VerificarHash para gerenciamento de senhas
- **Migrations:** Criação automatizada de tabela de usuários via Flyway

### **Ferramentas de Segurança**

- **BCryptUtil:** Ferramenta Java para geração de hashes seguros

```bash
# Gerar um hash BCrypt para uma senha
mvn compile exec:java -Dexec.mainClass="com.caracore.cca.util.BCryptUtil" -Dexec.args="minhasenha"
```

- **VerificarHash:** Utilitário para validação de hashes

```bash
# Verificar se uma senha corresponde a um hash
mvn compile exec:java -Dexec.mainClass="com.caracore.cca.util.VerificarHash" -Dexec.args="minhasenha hash_bcrypt"
```

- **Teste Unitário:** Verificação automática da geração e validação de hashes BCrypt
- **Prefixos BCrypt:** Suporte a formatos `$2a$`, `$2b$` e `$2y$`

### **LGPD Compliance**

- ✅ Consentimento explícito para coleta de dados via WhatsApp
- ✅ Política de privacidade integrada ao sistema
- ✅ Controle de retenção de dados pessoais
- ✅ Logs de auditoria para rastreabilidade
- ✅ Interface de confirmação de consentimento
- ✅ Data/hora de envio do consentimento registrada

### **Portaria nº 2.836/2011 - Ministério da Saúde**

**Implementação dos direitos de nome social e identidade de gênero:**

- ✅ **Nome Social:** Campo opcional no cadastro de pacientes
- ✅ **Gênero:** Autodeclaração com opções padronizadas:
  - Feminino
  - Masculino 
  - Não-binário
  - Outro
  - Prefere não informar
- ✅ **Interface:** Seção informativa sobre a Portaria no formulário
- ✅ **Banco de Dados:** Campos separados para nome civil e nome social
- ✅ **Legislação:** Atendimento às diretrizes do SUS para respeito à identidade de gênero

## **Contribuição**

### **Guidelines de Desenvolvimento**

1. **Code Style:** Google Java Style Guide
2. **Commits:** Conventional Commits
3. **Branches:** GitFlow workflow
4. **Testes:** Mínimo 80% de cobertura
5. **Documentação:** Javadoc obrigatório

### **Processo de Contribuição**

1. Fork o projeto
2. Crie uma branch feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'feat: adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## **Roadmap**

### **Versão 1.0 (MVP) - Q3 2025**

- [x] Sistema de agendamento básico
- [x] Gestão de pacientes e profissionais
- [x] Agendamento online
- [ ] Prontuário digital
- [ ] Relatórios básicos

### **Versão 1.1 - Q4 2025**

- [ ] Sistema de notificações completo
- [ ] Integração com WhatsApp
- [ ] Dashboard avançado
- [ ] Backup automático

### **Versão 2.0 - Q1 2026**

- [ ] Aplicativo mobile
- [ ] Integração com pagamentos (PIX)
- [ ] Teleconsulta
- [ ] Multi-clínica

## **FAQ**

### **Problemas Comuns**

**P: Erro de conexão com banco de dados**

```markdown
R: Verifique se PostgreSQL está rodando e as credenciais estão corretas:
   - Service: sudo systemctl status postgresql
   - Conexão: psql -h localhost -U cca_user -d cca_db
```

**P: Porta 8080 já está em uso**

```markdown
R: Altere a porta no application.yml ou mate o processo:
   - Verificar: netstat -tlnp | grep 8080
   - Matar: kill -9 <PID>
   - Ou usar: SERVER_PORT=8081 mvn spring-boot:run
```

**P: Erro de memória (OutOfMemoryError)**

```markdown
R: Aumente a heap da JVM:
   - export JAVA_OPTS="-Xmx2g -Xms1g"
   - Ou: java -Xmx2g -jar app.jar
```

---

## **Licença**

O Sistema Cara Core Agendamento (CCA) é licenciado sob a Licença MIT.

A Licença MIT é uma licença de software permissiva que é curta e objetiva. Ela permite que as pessoas façam qualquer coisa com o código-fonte, como:

- **Usar o código comercialmente**
- **Modificar o código**
- **Distribuir o código**
- **Sublicenciar o código**
- **Usar o código privadamente**

A única condição é que o aviso de copyright e a permissão sejam incluídos em todas as cópias ou partes substanciais do software.

Isso significa que você pode usar este sistema livremente em projetos comerciais e proprietários. No entanto, não há garantias ou responsabilidade por parte dos autores.

Veja o arquivo [LICENSE](LICENSE) para o texto completo da licença.

```
MIT License

Copyright (c) 2025 Cara Core Informática

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files...
```

---

© 2025 Cara Core Informática. Todos os direitos reservados.
