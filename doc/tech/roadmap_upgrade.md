
# Roadmap de Evolução Tecnológica - Sistema de Agendamento (Cara Core Informática)

Este roadmap orienta a evolução do sistema de agendamento para dentistas e consultórios, equilibrando velocidade de entrega, segurança, escalabilidade e performance.

---

## 🟢 Fase 1 – MVP (Produto Mínimo Viável)

**Objetivo:** Validar o produto e conquistar os primeiros clientes com velocidade.

### Tecnologias e Arquitetura:

- Spring Boot monolítico
- Thymeleaf + Bootstrap para frontend
- PostgreSQL local (sem Redis)
- Spring Security para autenticação
- Deploy em servidor com Tomcat

### Funcionalidades essenciais:

- Cadastro de profissionais e pacientes
- Agendamento manual e online
- Relatórios básicos
- Notificações via e-mail
- Backup automático local

### Benefícios:

- Baixo tempo de desenvolvimento
- Arquitetura simples e testável
- Entrega rápida do produto

---

## 🟡 Fase 2 – Performance e Profissionalização

**Objetivo:** Aumentar desempenho, confiabilidade e capacidade de atendimento com múltiplos clientes.

### Melhorias técnicas:

- Introdução de Redis como cache para:
  - Agendas disponíveis
  - Lista de profissionais e serviços
- Divisão do frontend e backend em camadas separadas
- Integração com ferramentas de logging e monitoramento
- Otimização de queries e índices no banco

### Benefícios - Fase 2:

- Resposta mais rápida em consultas frequentes
- Redução de carga no banco de dados
- Base sólida para múltiplas instâncias (multi-tenant isolado)

---

## 🔵 Fase 3 – Escalabilidade e Modularização

**Objetivo:** Transformar o sistema em uma plataforma SaaS escalável e segura para dezenas de clientes.

### Arquitetura recomendada:

- Redis como cache compartilhado entre instâncias
- Camada de persistência exposta via API RESTful própria
- Banco PostgreSQL por cliente (multi-instância)
- Orquestração via Docker ou script shell
- Monitoramento com Grafana + Prometheus
- Filas de mensagens para sincronização (RabbitMQ, Kafka)

### Funcionalidades adicionais:

- Dashboard em tempo real
- Pagamentos online (PIX/cartão)
- Prontuário digital
- Exportação para Excel/CSV

### Benefícios - Fase 3:

- Escalabilidade horizontal
- Segurança isolada por cliente
- Alta disponibilidade e controle técnico

---

## 📌 Considerações Finais

- Iniciar simples e evoluir com base no uso real é a estratégia mais eficaz.
- Redis e APIs RESTful trazem valor real quando há múltiplos acessos simultâneos ou necessidade de desacoplamento.
- O roadmap permite que a Cara Core entregue valor rápido, com plano de crescimento sustentável.

---

**Autores:** Christian V. Mulato & Guilherme Mulato  
**Empresa:** Cara Core Informática  
**Versão:** Junho/2025
