# Documentação Técnica - Sistema de Agendamento Cara Core (CCA)

## 📋 Visão Geral

Sistema de agendamento para dentistas e consultórios desenvolvido com tecnologias modernas e robustas, focado em escalabilidade, manutenibilidade e experiência do usuário.

---

## 🛠️ Stack Tecnológica

### **Backend**

- **Java 17** - Linguagem principal (LTS)
- **Spring Boot 3.2.6** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Autenticação e autorização
- **Spring Validation** - Validação de dados
- **Maven** - Gerenciamento de dependências

### **Frontend**

- **Thymeleaf** - Template engine
- **Bootstrap 5.3.0** - Framework CSS
- **jQuery 3.7.0** - Biblioteca JavaScript
- **WebJars** - Gerenciamento de recursos front-end

### **Banco de Dados**

- **PostgreSQL** - Banco principal (produção/desenvolvimento)
- **Flyway** - Controle de versão do banco de dados
- **H2** - Banco em memória (testes)

### **Testes**

- **JUnit 5** - Framework de testes
- **TestContainers** - Testes de integração com banco real
- **Spring Boot Test** - Testes de integração Spring
- **MockMvc** - Testes de controladores web

---

## 🏗️ Arquitetura

### **Padrão Arquitetural**

- **MVC (Model-View-Controller)**
- **Arquitetura em Camadas**
  - **Controller Layer** - Endpoints REST e páginas web
  - **Service Layer** - Lógica de negócio
  - **Repository Layer** - Acesso a dados
  - **Entity Layer** - Modelos de domínio

### **Estrutura de Pacotes**

```text
com.caracore.cca
├── config/          # Configurações do Spring
├── controller/      # Controladores REST e Web
├── dto/            # Data Transfer Objects
├── entity/         # Entidades JPA
├── exception/      # Tratamento de exceções
├── repository/     # Repositories JPA
├── service/        # Lógica de negócio
├── util/           # Utilitários
└── validation/     # Validações customizadas
```

---

## 🔧 Configuração do Ambiente

### **Pré-requisitos**

- **Java 17+** (OpenJDK recomendado)
- **Maven 3.8+**
- **PostgreSQL 15+**
- **Git**
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)

### **Variáveis de Ambiente**

```bash
# Banco de Dados
DB_HOST=localhost
DB_PORT=5432
DB_NAME=cca_db
DB_USERNAME=cca_user
DB_PASSWORD=cca_password

# Aplicação
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

# JWT (se implementado)
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
```

### **Perfis de Execução**

- **dev** - Desenvolvimento local
- **test** - Execução de testes
- **prod** - Ambiente de produção

---

## 📊 Banco de Dados

### **Configuração PostgreSQL**

```sql
-- Criação do banco e usuário
CREATE DATABASE cca_db;
CREATE USER cca_user WITH PASSWORD 'cca_password';
GRANT ALL PRIVILEGES ON DATABASE cca_db TO cca_user;
```

### **Migrations**

- Localização: `src/main/resources/db/migration/`
- Padrão de nomenclatura: `V{versao}__{descricao}.sql`
- Exemplo: `V001__create_user_table.sql`

### **Principais Entidades**

- **User** - Usuários do sistema
- **Dentist** - Profissionais dentistas
- **Patient** - Pacientes
- **Appointment** - Agendamentos
- **Clinic** - Consultórios/Clínicas
- **Schedule** - Horários disponíveis

---

## 🚀 Comandos de Execução

### **Desenvolvimento**

```bash
# Compilar o projeto
mvn clean compile

# Executar aplicação
mvn spring-boot:run

# Executar com perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### **Execução de Testes**

```bash
# Executar todos os testes
mvn test

# Executar testes de integração
mvn verify

# Executar testes com cobertura
mvn test jacoco:report
```

### **Gerenciamento do Banco**

```bash
# Executar migrations
mvn flyway:migrate

# Limpar banco
mvn flyway:clean

# Informações do banco
mvn flyway:info
```

### **Build**

```bash
# Gerar JAR
mvn clean package

# Pular testes no build
mvn clean package -DskipTests

# Build com perfil específico
mvn clean package -Pprod
```

---

## 🔒 Segurança

### **Autenticação**

- **Spring Security** com formulário de login
- **BCrypt** para hash de senhas
- **Session-based** authentication

### **Autorização**

- **Role-based** access control
- Roles: `ADMIN`, `DENTIST`, `RECEPTIONIST`, `PATIENT`

### **Proteções Implementadas**

- **CSRF Protection**
- **XSS Prevention**
- **SQL Injection Prevention** (JPA)
- **Session Fixation Protection**

---

## 📝 Logs

### **Configuração**

- **Logback** (padrão Spring Boot)
- **Níveis**: ERROR, WARN, INFO, DEBUG, TRACE
- **Formato**: `[timestamp] [level] [logger] - message`

### **Arquivos de Log**

- **Desenvolvimento**: Console
- **Produção**: `logs/cca-application.log`
- **Rotação**: Diária, máximo 30 dias

---

## 🧪 Estratégia de Testes

### **Tipos de Teste**

- **Unitários** - Lógica de negócio isolada
- **Integração** - Camadas integradas com banco real
- **Web** - Controladores com MockMvc
- **E2E** - Fluxos completos com TestContainers

### **Cobertura de Código**

- Meta: **80%** de cobertura mínima
- Ferramenta: **JaCoCo**
- Relatórios: `target/site/jacoco/index.html`

---

## 📦 Dependências Principais

| Dependência | Versão | Propósito |
|-------------|--------|-----------|
| Spring Boot | 3.2.6 | Framework principal |
| PostgreSQL | runtime | Driver banco de dados |
| Thymeleaf | 3.x | Template engine |
| Bootstrap | 5.3.0 | Framework CSS |
| Flyway | 9.x | Migrations |
| TestContainers | 1.x | Testes integração |
| JUnit | 5.x | Framework testes |

---

## 🔄 Pipeline CI/CD

### **Etapas Recomendadas**

1. **Build** - Compilação e validação
2. **Test** - Execução de todos os testes
3. **Quality Gate** - Análise de código (SonarQube)
4. **Security Scan** - Verificação de vulnerabilidades
5. **Package** - Geração do artefato
6. **Deploy** - Implantação automatizada

### **Ferramentas Sugeridas**

- **Jenkins** / **GitHub Actions** / **GitLab CI**
- **Docker** para containerização
- **SonarQube** para qualidade de código
- **OWASP Dependency Check** para segurança

---

## 🐛 Troubleshooting

### **Problemas Comuns**

#### **Erro de Conexão com Banco**

```bash
# Verificar se PostgreSQL está rodando
sudo systemctl status postgresql

# Verificar conectividade
psql -h localhost -p 5432 -U cca_user -d cca_db
```

#### **Erro de Memória (OutOfMemoryError)**

```bash
# Aumentar heap da JVM
export JAVA_OPTS="-Xmx2g -Xms1g"
mvn spring-boot:run
```

#### **Porta já em uso**

```bash
# Verificar processo na porta 8080
netstat -tulpn | grep 8080

# Matar processo
kill -9 <PID>
```

---

## 📚 Documentação Adicional

### **Recursos de Referência**

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Flyway Documentation](https://flywaydb.org/documentation/)

### **Convenções de Código**

- **Java Code Style**: Google Java Style Guide
- **Commits**: Conventional Commits
- **Branches**: GitFlow

### **Monitoramento**

- **Spring Boot Actuator** para métricas
- **Micrometer** para coleta de métricas
- **Prometheus** + **Grafana** (produção)

---

## 🔄 Versionamento

### **Estratégia de Versioning**

- **Semantic Versioning** (SemVer)
- Formato: `MAJOR.MINOR.PATCH`
- Tags Git para releases

### **Changelog**

- Manter arquivo `CHANGELOG.md`
- Documentar mudanças por versão
- Categorias: Added, Changed, Deprecated, Removed, Fixed, Security

---

## 👥 Contatos e Suporte

### **Equipe de Desenvolvimento**

- **Tech Lead**: [Nome]
- **Backend Developer**: [Nome]
- **Frontend Developer**: [Nome]
- **DevOps Engineer**: [Nome]

### **Canais de Comunicação**

- **Issues**: GitHub/GitLab Issues
- **Chat**: Slack/Teams
- **Documentação**: Confluence/Wiki

---

**Última atualização:** 29 de junho de 2025