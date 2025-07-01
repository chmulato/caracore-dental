# Cara Core Agendamento (CCA)

Sistema de agendamento para consultórios odontológicos desenvolvido com Spring Boot, Java 17 e PostgreSQL.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3.0-purple.svg)](https://getbootstrap.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

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

- [ ] Cadastro e autenticação de usuários (Admin, Dentista, Recepcionista)
- [ ] Cadastro de pacientes
- [ ] Cadastro de profissionais
- [ ] Agendamento de consultas (com interface web)
- [ ] Visualização de agenda por dia/semana/mês
- [ ] Edição e cancelamento de agendamentos
- [ ] Upload de imagens odontológicas no prontuário
- [ ] Relatórios básicos de agendamentos
- [ ] Notificações por email (básico)
- [ ] Interface pública para agendamento online
- [ ] Controle de acesso por perfil
- [ ] LGPD: consentimento e política de privacidade

Funcionalidades previstas para o MVP:

- [ ] Prontuário digital completo
- [ ] Relatórios financeiros
- [ ] Dashboard de produtividade
- [ ] Integração com WhatsApp (notificações)
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
- **Login padrão:** suporte@caracore.com.br / admin123

> **Dica:**
> - Para rodar os testes: `mvn test`
> - Para rodar com Docker: veja a seção "Deploy com Docker" abaixo.

---

## **Roadmap Resumido do MVP**

- [ ] Sistema de agendamento básico
- [ ] Gestão de pacientes e profissionais
- [ ] Agendamento online
- [ ] Prontuário digital
- [ ] Relatórios básicos

---

Para detalhes completos, consulte as seções abaixo ou a [Wiki do Projeto](https://github.com/chmulato/cara-core_cca/wiki).

## **Sobre o Projeto**

O **Cara Core Agendamento (CCA)** é uma solução completa para gestão de agendamentos em consultórios odontológicos, desenvolvida pela **Cara Core Informática**. O sistema oferece uma interface moderna e intuitiva para profissionais de saúde, pacientes e administradores.

### **Principais Funcionalidades**

- **Agenda Inteligente**: Calendário interativo com visualização por dia, semana e mês
- **Gestão de Pacientes**: Cadastro completo com histórico e prontuário digital
- **Agendamento Online**: Interface pública para pacientes agendarem consultas
- **Prontuário Digital**: Upload e organização de imagens odontológicas
- **Relatórios Gerenciais**: Métricas de produtividade e análises financeiras
- **Sistema de Notificações**: Lembretes automáticos por email e SMS
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

1. **Servidor de Aplicação:**
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

- Consentimento explícito para coleta de dados
- Política de privacidade integrada
- Controle de retenção de dados
- Logs de auditoria

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

## **Licença**

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## **Suporte**

- **Documentação:** [Wiki do Projeto](https://github.com/chmulato/cara-core_cca/wiki)
- **Issues:** [GitHub Issues](https://github.com/chmulato/cara-core_cca/issues)
- **Email:** [suporte@caracore.com.br](mailto:suporte@caracore.com.br)
- **Website:** [www.caracore.com.br](https://www.caracore.com.br)

---

## **Equipe**

**Desenvolvedores:**

- **Christian V. Mulato** - Tech Lead & Backend Developer
- **Guilherme Mulato** - Frontend Developer & UI/UX

**Cara Core Informática**  
*Soluções tecnológicas para área da saúde*

---

**Última atualização:** 01 de julho de 2025  
**Versão dos WebJars:** Bootstrap 5.3.0, Bootstrap Icons 1.13.1, jQuery 3.7.0

## **Como Executar Rapidamente**

Para iniciar a aplicação de forma rápida, execute:

```bash
# Subir o banco de dados PostgreSQL (necessário apenas na primeira vez)
docker-compose up -d

# Iniciar a aplicação Spring Boot
mvn spring-boot:run
```

Acesse a aplicação em [http://localhost:8080](http://localhost:8080)  
Login padrão: 
- Email: `suporte@caracore.com.br`
- Senha: `admin123`
- Perfil: Administrador

Para verificar os logs e confirmar que a aplicação está rodando corretamente:
```bash
# Ver logs da aplicação
tail -f logs/user-activity.log

# No Windows PowerShell
Get-Content -Path .\logs\user-activity.log -Wait
```

## **Atualizações Recentes**

- **01/07/2025**: Correção de erros nos testes unitários e de integração, melhorias na configuração dos ambientes de teste
- **30/06/2025**: Atualização da documentação e ajustes no README
- **25/06/2025**: Implementação de funcionalidades de agendamento e autenticação
- **20/06/2025**: Configuração inicial do projeto e estrutura de banco de dados

## **Inicialização e Dados Padrão**

### **Usuário Administrador Inicial**

O sistema cria automaticamente um usuário administrador no primeiro acesso:

```markdown
Email: suporte@caracore.com.br
Senha: admin123
Perfil: ADMIN
```

Este usuário é criado de duas formas redundantes para garantir sua existência:

1. **Migrations Flyway**: No script `V3__criar_tabela_usuario.sql` através de um INSERT com ON CONFLICT para evitar duplicidade
2. **InitService**: Um serviço Java que verifica na inicialização se o usuário existe e o cria se necessário

### **Validação da Senha Criptografada**

A senha padrão "admin123" é armazenada como hash BCrypt:

```text
$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy
```

Para validar que o hash corresponde corretamente à senha, você pode:

1. **Executar o utilitário VerificarHash**:

   ```bash
   mvn test -Dtest=VerificarHashTest
   ```

   O teste verifica se a senha "admin123" corresponde ao hash armazenado.

2. **Verificar manualmente via consulta SQL** (após a aplicação em execução):

   ```sql
   SELECT email, senha FROM usuario WHERE email='suporte@caracore.com.br';
   ```

   O hash retornado deve ser: `$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy`

3. **Validar com o BCryptPasswordEncoder**:

   ```java
   BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
   boolean corresponde = encoder.matches("admin123", "$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy");
   // Deve retornar true
   ```

### **Como modificar o usuário padrão**

Para alterar as credenciais padrão, você precisará editar:

```bash
# 1. O arquivo de migração Flyway
src/main/resources/db/migration/V3__criar_tabela_usuario.sql

# 2. A classe InitService 
src/main/java/com/caracore/cca/service/InitService.java
```

Para gerar um novo hash BCrypt para a senha:

```bash
# Usando a ferramenta BCryptUtil do projeto
mvn compile exec:java -Dexec.mainClass="com.caracore.cca.util.BCryptUtil" -Dexec.args="minhaNovasenha"

# Ou no console do Spring Boot
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
encoder.encode("minhaNovasenha");
```

> **Importante:** Por segurança, é altamente recomendado mudar a senha do usuário administrador após o primeiro acesso em produção.

### **Verificação da Integridade da Senha**

Para verificar se a integridade da senha está mantida após a criação do usuário inicial, os testes unitários na classe `VerificarHashTest` garantem que o hash BCrypt `$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy` corresponde corretamente à senha `admin123`.

Os testes verificam:

1. A correspondência entre a senha e o hash (teste `deveVerificarHashComSenhaCorrespondente`)
2. A rejeição de senhas incorretas (teste `deveVerificarHashComSenhaIncorreta`)
3. O uso dos valores padrão para verificação (teste `deveUsarValoresPadraoQuandoArgumentosNaoFornecidos`)

Estes testes são executados automaticamente durante o ciclo de build e podem ser executados manualmente com:

```bash
mvn test -Dtest=VerificarHashTest
```
