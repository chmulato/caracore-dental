
# Cara Core Dental - Agendamentos

> Sistema de agendamento para consultórios odontológicos desenvolvido com Spring Boot 3.2.6 e Java 17.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![H2](https://img.shields.io/badge/H2-In--Memory-lightgrey.svg)](http://www.h2database.com/)
[![Docker](https://img.shields.io/badge/Docker-Supported-blue.svg)](https://www.docker.com/)
[![Status](https://img.shields.io/badge/Status-Pronto%20Homologa%C3%A7%C3%A3o-brightgreen.svg)](#)

<div align="center">
  <img src="./img/sistema_cca.png" alt="Cara Core Dental - Agendamento (CCA)" width="80%">
</div>


## Início Rápido

### Executar o Sistema (Multi-Ambiente)

```powershell
# 1. Clonar o repositório
git clone https://github.com/chmulato/caracore-dental.git
cd caracore-dental

# 2. OPÇÃO 1: Desenvolvimento rápido com H2 (recomendado para testes)
$env:SPRING_PROFILES_ACTIVE='h2'
mvn spring-boot:run

# 3. OPÇÃO 2: Desenvolvimento com PostgreSQL (Docker)
# 3.1 - Subir PostgreSQL via Docker
docker run -d --name postgres-cca -e POSTGRES_DB=cca_db -e POSTGRES_USER=cca_user -e POSTGRES_PASSWORD=cca_pass -p 5432:5432 postgres:15

# 3.2 - Executar aplicação com PostgreSQL
$env:SPRING_PROFILES_ACTIVE='local'
mvn spring-boot:run

# 4. OPÇÃO 3: Scripts automáticos (Windows/Linux)
.\scr\start-environment.ps1    # Windows PowerShell
./scr/start-environment.sh     # Linux/macOS
.\scr\start-environment.bat    # Windows Batch

# 5. Acessar aplicação
# http://localhost:8080
```


### Usuários de Teste

| Perfil        | Email                    | Senha    | Acesso                    |
|---------------|--------------------------|----------|---------------------------|
| Administrador | suporte@caracore.com.br  | admin123 | Acesso total ao sistema   |
| Dentista      | dentista@caracore.com.br | admin123 | Agenda e prontuários      |
| Recepcionista | recepcao@caracore.com.br | admin123 | Agenda e pacientes        |
| Paciente      | paciente@caracore.com.br | admin123 | Portal do paciente        |



## Funcionalidades Principais

### Sistema Totalmente Implementado

- **Autenticação e Segurança** - Spring Security 6 com controle de acesso
- **Dashboard Interativo** - Estatísticas e métricas em tempo real
- **Gestão de Consultas** - CRUD completo com reagendamento e controle de conflitos
- **Gestão de Pacientes** - Cadastro completo com integração WhatsApp
- **Gestão de Dentistas** - Especialidades, horários e controle CRO
- **Sistema de Prontuário** - Prontuários médicos com upload de imagens radiológicas
- **Multi-Ambiente** - Suporte H2 (desenvolvimento) e PostgreSQL (produção)
- **Navegação Completa** - Interface responsiva dual (autenticada/pública)
- **Sistema de Logs** - Auditoria completa de atividades de usuários
- **Conformidade LGPD** - Controle de consentimento e privacidade


### Arquitetura e Qualidade

- **545 Testes Unitários** - 100% de cobertura e aprovação
- **22 Migrações Flyway** - Controle de versão do banco de dados
- **Pool de Conexões** - HikariCP otimizado para cada ambiente
- **Padrão DTO** - Arquitetura robusta para performance
- **Docker Support** - PostgreSQL containerizado para desenvolvimento


### Próximas Funcionalidades

- Calendário visual avançado com FullCalendar.js
- Integração completa com WhatsApp Business API
- Relatórios avançados em PDF e Excel
- Sistema de notificações automáticas
- Interface mobile otimizada


## Tecnologias

**Backend:** Spring Boot 3.2.6, Spring Security 6, JPA/Hibernate, Flyway 9.22.3  
**Frontend:** Thymeleaf, Bootstrap 5.3.0, jQuery 3.7.0, Bootstrap Icons  
**Banco de Dados:** PostgreSQL 15 (produção), H2 (desenvolvimento/testes)  
**Build:** Maven 3.8+, Java 17 (OpenJDK)  
**Containerização:** Docker para PostgreSQL


## Pré-requisitos

- **Java 17+** (OpenJDK recomendado)
- **Maven 3.8+**
- **Docker** (opcional, para PostgreSQL via container)
- **PostgreSQL 15+** (opcional, usa H2 para desenvolvimento rápido)


## Configuração

### Profile H2 - Desenvolvimento Rápido (Padrão)

```powershell
# Executar com banco H2 em memória
$env:SPRING_PROFILES_ACTIVE='h2'
mvn spring-boot:run

# Console H2 disponível em:
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:devdb
# User: sa | Password: (vazio)
```

### Profile Local - PostgreSQL via Docker

```powershell
# 1. Subir PostgreSQL com Docker
docker run -d --name postgres-cca -e POSTGRES_DB=cca_db -e POSTGRES_USER=cca_user -e POSTGRES_PASSWORD=cca_pass -p 5432:5432 postgres:15

# 2. Executar aplicação
$env:SPRING_PROFILES_ACTIVE='local'
mvn spring-boot:run
```

### PostgreSQL Manual (Opcional)

```sql
-- Criar banco PostgreSQL
CREATE DATABASE cca_db;
CREATE USER cca_user WITH PASSWORD 'cca_pass';
GRANT ALL PRIVILEGES ON DATABASE cca_db TO cca_user;
```


## Testes

```powershell
# Executar todos os testes (545 testes - 100% aprovação)
mvn test

# Relatório de cobertura
mvn clean test jacoco:report

# Testes específicos por módulo
mvn test -Dtest=ConsultasControllerTest
mvn test -Dtest=ProntuarioControllerTest

# Validar setup multi-ambiente
mvn test -Dspring.profiles.active=h2
mvn test -Dspring.profiles.active=local
```


## Status do Projeto

### Sistema Pronto para Homologação

- **Core Features:** Todas as funcionalidades principais implementadas e testadas
- **Multi-Ambiente:** Suporte completo para H2 e PostgreSQL com Docker
- **Navegação:** Interface completa e responsiva com sistema dual
- **Banco de Dados:** PostgreSQL integrado com 22 migrações Flyway
- **Testes:** 545 testes unitários com 100% de aprovação
- **Segurança:** Autenticação, autorização e compliance LGPD funcionando
- **Prontuários:** Sistema completo com upload de imagens radiológicas
- **Logs:** Sistema de auditoria implementado
- **Performance:** Pool de conexões HikariCP otimizado


**Status Atual:** VERDE - Sistema estável e pronto para demonstrações e homologação

Ver [STATUS_ATUAL.md](STATUS_ATUAL.md) para relatório detalhado e [doc/INDEX.md](doc/INDEX.md) para documentação completa.


## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).


## Equipe

**Cara Core Informática**  
Sistema de Agendamento para Consultórios Odontológicos

---

**Última atualização:** 24/07/2025 - Sistema multi-ambiente implementado e pronto para homologação!
