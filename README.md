# Cara Core Agendamento (CCA)

> Sistema de agendamento para consultórios odontológicos desenvolvido com Spring Boot 3.2.6 e Java 17.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16.9-blue.svg)](https://www.postgresql.org/)
[![Status](https://img.shields.io/badge/Status-Totalmente%20Funcional-brightgreen.svg)](#)

<div align="center">
  <img src="./img/sistema_cca.png" alt="Cara Core Agendamento (CCA)" width="80%">
</div>

## Início Rápido

### **Executar o Sistema**

```bash
# Clonar o repositório
git clone https://github.com/chmulato/cara-core_cca.git
cd cara-core_cca

# Opção 1: Usar script de inicialização (Recomendado)
.\scr\start-environment.ps1    # Windows PowerShell
./scr/start-environment.sh     # Linux/macOS
.\scr\start-environment.bat    # Windows Batch (compatibilidade)

# Opção 2: Comando direto
mvn spring-boot:run

# Acessar aplicação
# http://localhost:8080
```

### **Usuários de Teste**

| Perfil        | Email                    | Senha    | Acesso                    |
|---------------|--------------------------|----------|---------------------------|
| Administrador | suporte@caracore.com.br  | admin123 | Acesso total ao sistema   |
| Dentista      | dentista@caracore.com.br | admin123 | Agenda e prontuários      |
| Recepcionista | recepcao@caracore.com.br | admin123 | Agenda e pacientes        |
| Paciente      | paciente@caracore.com.br | admin123 | Portal do paciente        |

## Funcionalidades Principais

### **Totalmente Implementado**

- **Sistema de Autenticação** - Login/logout com Spring Security
- **Dashboard Interativo** - Estatísticas e métricas em tempo real
- **Gestão de Consultas** - CRUD completo com controle de conflitos
- **Gestão de Pacientes** - Cadastro completo com WhatsApp obrigatório
- **Gestão de Dentistas** - Especialidades, horários e CRO
- **Navegação Completa** - Interface responsiva com Bootstrap 5.3.0
- **Banco de Dados** - PostgreSQL com Flyway migrations
- **Sistema de Logs** - Auditoria completa de atividades
- **Conformidade LGPD** - Controle de consentimento via WhatsApp

### **Em Desenvolvimento**

- Integração completa com WhatsApp API
- Relatórios avançados em PDF
- Agendamento online público
- Prontuário digital com upload de imagens

## Tecnologias

**Backend:** Spring Boot 3.2.6, Spring Security 6, JPA/Hibernate, Flyway  
**Frontend:** Thymeleaf, Bootstrap 5.3.0, jQuery 3.7.0, Bootstrap Icons  
**Banco de Dados:** PostgreSQL 16.9 (produção), H2 (testes)  
**Build:** Maven 3.8+, Java 17 (OpenJDK)

## Pré-requisitos

- **Java 17+** (OpenJDK recomendado)
- **Maven 3.8+**
- **PostgreSQL 15+** (opcional - usa H2 para desenvolvimento rápido)

## Configuração

### **Ambiente de Desenvolvimento (H2 - Padrão)**

```bash
# Executar com banco H2 em memória
mvn spring-boot:run

# Console H2 disponível em:
# http://localhost:8080/h2-console
```

### **Ambiente Local (PostgreSQL)**

```bash
# 1. Criar banco PostgreSQL
CREATE DATABASE cca_db;
CREATE USER cca_user WITH PASSWORD 'cca_password';
GRANT ALL PRIVILEGES ON DATABASE cca_db TO cca_user;

# 2. Executar com perfil local
mvn spring-boot:run -Dspring.profiles.active=local
```

### **Docker (Opcional)**

```bash
# Subir PostgreSQL com Docker
docker-compose up -d

# Executar aplicação
mvn spring-boot:run -Dspring.profiles.active=local
```

## Testes

```bash
# Executar todos os testes (306 testes - 100% aprovação)
mvn test

# Relatório de cobertura
mvn clean test jacoco:report

# Testes específicos
mvn test -Dtest=ConsultasControllerTest
```

## Status do Projeto

### Sistema Totalmente Funcional

- **Core Features:** Todas as funcionalidades principais implementadas
- **Navegação:** Interface completa e responsiva
- **Banco de Dados:** PostgreSQL integrado com migrations
- **Testes:** 306 testes unitários com 100% de aprovação
- **Segurança:** Autenticação e autorização funcionando
- **Logs:** Sistema de auditoria implementado

Ver [STATUS_ATUAL.md](STATUS_ATUAL.md) para relatório detalhado.

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

## Equipe

**Cara Core Informática**  
Sistema de Agendamento para Consultórios Odontológicos

---

**Última atualização:** 11/07/2025 - Sistema totalmente funcional e pronto para uso!
