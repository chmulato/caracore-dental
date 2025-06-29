# Requisitos do Sistema de Agendamento para Dentistas e Consultórios

### Produto da Cara Core Informática
### Desenvolvedores: Christian V. Mulato & Guilherme Mulato

---

## ✅ Requisitos Funcionais (RF)

### 🧑‍⚕️ Módulo Profissionais

- Cadastro de dentistas e assistentes
- Definição de especialidades e serviços
- Configuração de agenda semanal e horários
- Vínculo com consultório

### 🗓️ Módulo Agenda

- Visualização por dia, semana e mês
- Agendamento manual e online
- Bloqueios (folgas, férias, pausas)
- Status: agendado, confirmado, compareceu, cancelado, reagendado

### 👩 Módulo Pacientes

- Cadastro com dados pessoais e observações
- Histórico de agendamentos
- Consentimento para comunicação (LGPD)

### 📆 Agendamento Online

- Interface pública
- Escolha de profissional, serviço e horário
- Notificações por e-mail/SMS/WhatsApp
- Reagendamento via link

### 💬 Notificações e Alertas

- Lembrete 24h antes
- Confirmação por link
- Alertas de conflitos de horário

### 📊 Relatórios

- Agendamentos por período
- Serviços mais realizados
- Taxa de comparecimento
- Horários de pico
- Profissionais mais requisitados

---

## 🔐 Requisitos Não Funcionais (RNF)

```markdown
|-----------------|---------------------------------------|
| Item            | Descrição                             |
|-----------------|---------------------------------------|
| Segurança       | Spring Security (auth + roles)        |
| Backup          | Diário por instância                  |
| Isolamento      | Banco PostgreSQL separado por cliente |
| Responsividade  | Thymeleaf com Bootstrap               |
| Performance     | < 2 segundos                          |
| Auditoria       | Log de acessos e ações                |
| Escalabilidade  | Deploy via Tomcat ou container        |
| LGPD            | Consentimento e política de dados     |
|-----------------|---------------------------------------|
---

## ⚙️ Entidades Principais

- Usuário (login, senha, permissão)
- Paciente (nome, CPF, contato)
- Profissional (nome, serviços, agenda)
- Serviço (tipo, duração, valor)
- Agendamento (status, data, profissional, paciente)
- Consultório (dados e config)
- Notificação (tipo, status, canal, log)

---

## 🧪 Funcionalidades Futuras (Versões Plus)
- Prontuário odontológico simplificado
- Pagamento online (Pix, cartão)
- Teleconsulta (Jitsi, BigBlueButton)
- Dashboard e métricas em tempo real
- Avaliação de atendimento
- Exportação de dados (CSV, Excel)

---

**Observação:** Este documento serve como base para o desenvolvimento inicial do sistema, com estrutura modular e isolada por instância, visando segurança e escalabilidade no modelo SaaS.

---

## 🖥️ Interfaces do Sistema (Telas)

### 🔐 **Módulo de Autenticação**

#### **Tela de Login**
- Campos: usuário/email, senha
- Links: "Esqueci minha senha", "Primeiro acesso"
- Seleção de perfil (se múltiplos consultórios)
- Botão "Entrar"

#### **Tela de Recuperação de Senha**
- Campo: email
- Botão "Enviar link de recuperação"
- Mensagens de sucesso/erro

#### **Tela de Redefinição de Senha**
- Campos: nova senha, confirmar senha
- Validação de força da senha
- Botão "Redefinir"

---

### 🏠 **Dashboard Principal**

#### **Painel Administrativo**
- Resumo de agendamentos do dia
- Estatísticas rápidas (total pacientes, consultas pendentes)
- Gráficos de ocupação por profissional
- Alertas e notificações importantes
- Acesso rápido às funcionalidades principais

---

### 🧑‍⚕️ **Módulo Profissionais**

#### **Lista de Profissionais**
- Tabela com: nome, especialidade, status, ações
- Filtros: especialidade, status (ativo/inativo)
- Busca por nome
- Botões: "Adicionar", "Editar", "Desativar"

#### **Cadastro/Edição de Profissional**
- Dados pessoais: nome, CPF, CRO, contato
- Especialidades (seleção múltipla)
- Serviços oferecidos
- Configuração de agenda padrão
- Vínculo com consultório
- Foto do profissional

#### **Configuração de Agenda do Profissional**
- Grade semanal com horários
- Definição de intervalos
- Configuração de pausas
- Bloqueios recorrentes
- Férias e folgas

---

### 👩 **Módulo Pacientes**

#### **Lista de Pacientes**
- Tabela com: nome, telefone, último atendimento
- Filtros: data cadastro, status
- Busca por nome, CPF, telefone
- Botões: "Adicionar", "Editar", "Histórico"

#### **Cadastro/Edição de Paciente**
- Dados pessoais: nome, CPF, RG, nascimento
- Contatos: telefone, email, endereço
- Observações médicas
- Consentimentos LGPD
- Histórico de agendamentos
- Foto do paciente

#### **Histórico do Paciente**
- Timeline de consultas
- Status de cada agendamento
- Observações por consulta
- Serviços realizados
- Profissionais que atenderam

---

### 🗓️ **Módulo Agenda**

#### **Agenda Geral**
- Visualização: dia, semana, mês
- Filtros por profissional
- Cores por status do agendamento
- Drag & drop para reagendamento
- Clique duplo para detalhes

#### **Agenda por Profissional**
- Grade de horários individual
- Bloqueios e indisponibilidades
- Agendamentos confirmados/pendentes
- Espaços livres destacados

#### **Novo Agendamento**
- Seleção de paciente (busca/novo)
- Escolha do profissional
- Seleção do serviço
- Data e horário disponível
- Observações
- Confirmação automática ou manual

#### **Detalhes do Agendamento**
- Dados do paciente e profissional
- Serviço e duração
- Status atual
- Histórico de alterações
- Ações: confirmar, cancelar, reagendar
- Observações da consulta

#### **Reagendamento**
- Motivo do reagendamento
- Nova data/horário
- Notificação automática ao paciente
- Confirmação da alteração

---

### 📅 **Agendamento Online (Público)**

#### **Página Inicial**
- Apresentação do consultório
- Botão "Agendar Consulta"
- Informações de contato
- Horários de funcionamento

#### **Seleção de Profissional**
- Lista de profissionais disponíveis
- Foto, nome e especialidades
- Botão "Escolher"

#### **Seleção de Serviço**
- Lista de serviços do profissional
- Duração e descrição
- Valor (se configurado)

#### **Escolha de Data/Horário**
- Calendário com disponibilidade
- Horários livres por dia
- Seleção de horário

#### **Dados do Paciente**
- Formulário de cadastro
- Campos obrigatórios
- Termos de uso e LGPD
- Confirmação de dados

#### **Confirmação do Agendamento**
- Resumo da consulta
- Dados do paciente
- Botão "Confirmar agendamento"
- Instruções para o dia da consulta

---

### 🔧 **Módulo Configurações**

#### **Configurações Gerais**
- Dados do consultório
- Horários de funcionamento
- Configurações de notificação
- Política de cancelamento

#### **Gerenciamento de Usuários**
- Lista de usuários do sistema
- Definição de permissões
- Criação de novos usuários
- Alteração de senhas

#### **Configurações de Serviços**
- Lista de serviços oferecidos
- Duração padrão
- Valores
- Descrições

#### **Configurações de Notificações**
- Templates de email/SMS
- Configuração de provedores
- Horários de envio
- Tipos de notificação

---

### 📊 **Módulo Relatórios**

#### **Dashboard de Relatórios**
- Seleção de período
- Filtros por profissional/serviço
- Gráficos principais
- Exportação de dados

#### **Relatório de Agendamentos**
- Listagem detalhada
- Filtros avançados
- Status e observações
- Exportação CSV/PDF

#### **Relatório de Profissionais**
- Produtividade por profissional
- Horários mais ocupados
- Taxa de comparecimento
- Análise de performance

#### **Relatório Financeiro**
- Receita por período
- Serviços mais rentáveis
- Análise de cancelamentos
- Projeções

---

### 💬 **Módulo Notificações**

#### **Central de Notificações**
- Lista de notificações enviadas
- Status de entrega
- Reenvio manual
- Histórico de comunicação

#### **Configuração de Templates**
- Editor de templates
- Variáveis disponíveis
- Preview das mensagens
- Teste de envio

---

### 📱 **Responsividade Mobile**

#### **Versão Mobile - Agenda**
- Visualização simplificada
- Navegação por swipe
- Ações principais acessíveis
- Interface otimizada para toque

#### **Versão Mobile - Agendamento**
- Fluxo simplificado
- Campos otimizados
- Validação em tempo real
- Experiência mobile-first

---

### 🔍 **Funcionalidades Transversais**

#### **Busca Global**
- Campo de busca no header
- Busca por pacientes, profissionais
- Resultados categorizados
- Acesso rápido aos registros

#### **Notificações do Sistema**
- Alertas em tempo real
- Notificações push
- Badges de pendências
- Log de atividades

#### **Ajuda e Suporte**
- FAQ integrado
- Tutoriais contextuais
- Chat de suporte
- Documentação do usuário

---

## 🎨 **Padrões de Interface**

### **Design System**
- Cores: azul (primário), branco, cinza
- Tipografia: sans-serif moderna
- Ícones: consistentes e intuitivos
- Espaçamentos padronizados

### **Componentes Reutilizáveis**
- Botões (primário, secundário, perigo)
- Formulários padronizados
- Tabelas responsivas
- Modais de confirmação
- Calendários e date pickers

### **Estados da Interface**
- Loading states
- Estados vazios
- Mensagens de erro
- Confirmações de sucesso
- Validações em tempo real

---

## 📊 **Fluxos de Navegação**

### **Fluxo Principal - Agendamento**
1. Login → Dashboard
2. Dashboard → Agenda
3. Agenda → Novo Agendamento
4. Seleção Paciente → Profissional → Serviço → Data/Hora
5. Confirmação → Volta para Agenda

### **Fluxo Público - Agendamento Online**
1. Página Inicial → Profissional
2. Profissional → Serviço → Data/Hora
3. Dados Paciente → Confirmação
4. Email/SMS de confirmação

### **Fluxo Administrativo**
1. Login → Dashboard
2. Dashboard → Relatórios/Configurações
3. Gestão de dados → Volta ao Dashboard
