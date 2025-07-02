# Checklist de Telas e Design - Sistema de Agendamento Odontológico

## Cara Core Informática - CCA (Cara Core Agendamento)

**Responsável Frontend:** Guilherme Mulato  
**Tecnologias:** HTML5, CSS3, Bootstrap 5.3.0, JavaScript (ES6+)  
**Data Criação:** 29 de junho de 2025

---

## 📋 Status de Desenvolvimento

**Legenda:**

- ✅ **Concluído** - Tela finalizada e testada
- 🚧 **Em Desenvolvimento** - Tela em progresso
- ⏳ **Pendente** - Aguardando desenvolvimento
- 🔍 **Em Revisão** - Aguardando aprovação
- ❌ **Bloqueado** - Impedimento identificado

---

## 🔐 **1. MÓDULO DE AUTENTICAÇÃO**

### 1.1 Tela de Login

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta
- **Componentes:**
  - [ ] Formulário de login centralizado
  - [ ] Campos: email/usuário e senha
  - [ ] Checkbox "Lembrar-me"
  - [ ] Link "Esqueci minha senha"
  - [ ] Botão "Entrar" com loading state
  - [ ] Validação client-side
  - [ ] Mensagens de erro responsivas
- **Arquivos:** `login.html`, `login.css`, `login.js`
- **Observações:** Design clean e profissional, foco na usabilidade

### 1.2 Tela de Recuperação de Senha

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Formulário com campo de email
  - [ ] Botão "Enviar link de recuperação"
  - [ ] Mensagem de sucesso/erro
  - [ ] Link "Voltar ao login"
- **Arquivos:** `forgot-password.html`, `forgot-password.css`, `forgot-password.js`

### 1.3 Tela de Redefinição de Senha

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Campos: nova senha e confirmação
  - [ ] Indicador de força da senha
  - [ ] Botão "Redefinir senha"
  - [ ] Validação de senhas idênticas
- **Arquivos:** `reset-password.html`, `reset-password.css`, `reset-password.js`

---

## 🏠 **2. DASHBOARD PRINCIPAL**

### 2.1 Dashboard Administrativo

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta
- **Componentes:**
  - [ ] Header com navegação principal
  - [ ] Sidebar colapsável
  - [ ] Cards de métricas (4 principais)
  - [ ] Gráfico de agendamentos (Chart.js)
  - [ ] Lista de agendamentos do dia
  - [ ] Área de notificações
  - [ ] Botões de ação rápida
- **Arquivos:** `dashboard.html`, `dashboard.css`, `dashboard.js`
- **Observações:** Interface responsiva, gráficos interativos

---

## 🧑‍⚕️ **3. MÓDULO PROFISSIONAIS**

### 3.1 Lista de Profissionais

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta
- **Componentes:**
  - [ ] Tabela responsiva com DataTables
  - [ ] Filtros: especialidade, status
  - [ ] Campo de busca global
  - [ ] Botões de ação por linha
  - [ ] Botão "Adicionar Profissional"
  - [ ] Paginação
- **Arquivos:** `professionals-list.html`, `professionals-list.css`, `professionals-list.js`

### 3.2 Cadastro/Edição de Profissional

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta
- **Componentes:**
  - [ ] Formulário em abas/steps
  - [ ] Upload de foto com preview
  - [ ] Campos de dados pessoais
  - [ ] Seleção múltipla de especialidades
  - [ ] Configuração de serviços
  - [ ] Validação completa do formulário
  - [ ] Botões "Salvar" e "Cancelar"
- **Arquivos:** `professional-form.html`, `professional-form.css`, `professional-form.js`

### 3.3 Configuração de Agenda do Profissional

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Grade semanal interativa
  - [ ] Seleção de horários por dia
  - [ ] Configuração de pausas
  - [ ] Bloqueios recorrentes
  - [ ] Calendário para férias
- **Arquivos:** `professional-schedule.html`, `professional-schedule.css`, `professional-schedule.js`

---

## 👩 **4. MÓDULO PACIENTES**

### 4.1 Lista de Pacientes

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta
- **Componentes:**
  - [ ] Tabela responsiva com DataTables
  - [ ] Filtros avançados
  - [ ] Busca por nome, CPF, telefone
  - [ ] Avatar padrão para pacientes
  - [ ] Ações contextuais
- **Arquivos:** `patients-list.html`, `patients-list.css`, `patients-list.js`

### 4.2 Cadastro/Edição de Paciente

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta
- **Componentes:**
  - [ ] Formulário responsivo
  - [ ] Upload de foto do paciente
  - [ ] Campos de dados pessoais
  - [ ] Endereço com CEP automático
  - [ ] Área de observações médicas
  - [ ] Checkboxes de consentimento LGPD
- **Arquivos:** `patient-form.html`, `patient-form.css`, `patient-form.js`

### 4.3 Histórico do Paciente

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Timeline de consultas
  - [ ] Cards por agendamento
  - [ ] Status coloridos
  - [ ] Filtros por período
  - [ ] Botão "Nova consulta"
- **Arquivos:** `patient-history.html`, `patient-history.css`, `patient-history.js`

### 4.4 Prontuário com Imagens

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Upload múltiplo de imagens
  - [ ] Galeria com lightbox
  - [ ] Categorização de imagens
  - [ ] Anotações por imagem
  - [ ] Controle de versões
  - [ ] Compressão automática
- **Arquivos:** `patient-records.html`, `patient-records.css`, `patient-records.js`
- **Observações:** Implementar drag & drop, progress bar

---

## 🗓️ **5. MÓDULO AGENDA**

### 5.1 Agenda Geral

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta (MVP)
- **Componentes:**
  - [ ] Calendário FullCalendar.js
  - [ ] Visualização: dia, semana, mês
  - [ ] Filtros por profissional
  - [ ] Cores por status
  - [ ] Drag & drop para reagendamento
  - [ ] Modal de detalhes
- **Arquivos:** `schedule.html`, `schedule.css`, `schedule.js`

### 5.2 Agenda por Profissional

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Grade de horários individual
  - [ ] Bloqueios destacados
  - [ ] Espaços livres evidenciados
  - [ ] Navegação entre profissionais
- **Arquivos:** `professional-agenda.html`, `professional-agenda.css`, `professional-agenda.js`

### 5.3 Novo Agendamento

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta (MVP)
- **Componentes:**
  - [ ] Modal ou página dedicada
  - [ ] Autocomplete para pacientes
  - [ ] Seleção de profissional
  - [ ] Dropdown de serviços
  - [ ] Date/time picker
  - [ ] Área de observações
  - [ ] Validação de conflitos
- **Arquivos:** `new-appointment.html`, `new-appointment.css`, `new-appointment.js`

### 5.4 Detalhes do Agendamento

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Modal com informações completas
  - [ ] Dados do paciente e profissional
  - [ ] Histórico de alterações
  - [ ] Botões de ação
  - [ ] Status colorido
- **Arquivos:** `appointment-details.html`, `appointment-details.css`, `appointment-details.js`

---

## 📅 **6. AGENDAMENTO ONLINE (PÚBLICO)**

### 6.1 Página Inicial Pública

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Header com logo e menu
  - [ ] Hero section
  - [ ] Informações do consultório
  - [ ] Botão CTA "Agendar"
  - [ ] Footer informativo
- **Arquivos:** `public-home.html`, `public-home.css`, `public-home.js`

### 6.2 Seleção de Profissional

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Cards de profissionais
  - [ ] Foto e especialidades
  - [ ] Disponibilidade básica
  - [ ] Botão "Escolher"
- **Arquivos:** `select-professional.html`, `select-professional.css`, `select-professional.js`

### 6.3 Seleção de Serviço

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Lista de serviços
  - [ ] Duração e descrição
  - [ ] Valores (se configurado)
  - [ ] Seleção única
- **Arquivos:** `select-service.html`, `select-service.css`, `select-service.js`

### 6.4 Escolha de Data/Horário

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Calendário interativo
  - [ ] Horários disponíveis por dia
  - [ ] Indicação de ocupação
  - [ ] Navegação fluida
- **Arquivos:** `select-datetime.html`, `select-datetime.css`, `select-datetime.js`

### 6.5 Dados do Paciente

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Formulário simplificado
  - [ ] Validação em tempo real
  - [ ] Termos de uso
  - [ ] Consentimento LGPD
- **Arquivos:** `patient-data.html`, `patient-data.css`, `patient-data.js`

### 6.6 Confirmação do Agendamento

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Resumo completo
  - [ ] Dados confirmados
  - [ ] Instruções para o dia
  - [ ] Botões de ação
- **Arquivos:** `appointment-confirmation.html`, `appointment-confirmation.css`, `appointment-confirmation.js`

---

## 🔧 **7. MÓDULO CONFIGURAÇÕES**

### 7.1 Configurações Gerais

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Abas organizadas
  - [ ] Dados do consultório
  - [ ] Horários de funcionamento
  - [ ] Configurações de sistema
- **Arquivos:** `settings-general.html`, `settings-general.css`, `settings-general.js`

### 7.2 Gerenciamento de Usuários

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Tabela de usuários
  - [ ] Controle de permissões
  - [ ] Modal de novo usuário
  - [ ] Reset de senhas
- **Arquivos:** `users-management.html`, `users-management.css`, `users-management.js`

### 7.3 Configuração de Serviços

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] CRUD de serviços
  - [ ] Configuração de duração
  - [ ] Definição de valores
  - [ ] Categorização
- **Arquivos:** `services-config.html`, `services-config.css`, `services-config.js`

### 7.4 Configurações de Notificações

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟢 Baixa
- **Componentes:**
  - [ ] Templates de mensagens
  - [ ] Configuração de provedores
  - [ ] Horários de envio
  - [ ] Teste de envio
- **Arquivos:** `notifications-config.html`, `notifications-config.css`, `notifications-config.js`

---

## 📊 **8. MÓDULO RELATÓRIOS**

### 8.1 Dashboard de Relatórios

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟢 Baixa
- **Componentes:**
  - [ ] Filtros de período
  - [ ] Gráficos principais
  - [ ] Métricas resumidas
  - [ ] Botões de exportação
- **Arquivos:** `reports-dashboard.html`, `reports-dashboard.css`, `reports-dashboard.js`

### 8.2 Relatório de Agendamentos

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟢 Baixa
- **Componentes:**
  - [ ] Tabela detalhada
  - [ ] Filtros avançados
  - [ ] Exportação CSV/PDF
  - [ ] Gráficos contextuais
- **Arquivos:** `appointments-report.html`, `appointments-report.css`, `appointments-report.js`

### 8.3 Relatório de Profissionais

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟢 Baixa
- **Componentes:**
  - [ ] Análise de produtividade
  - [ ] Taxa de comparecimento
  - [ ] Horários de pico
  - [ ] Comparativos
- **Arquivos:** `professionals-report.html`, `professionals-report.css`, `professionals-report.js`

### 8.4 Relatório Financeiro

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟢 Baixa
- **Componentes:**
  - [ ] Receita por período
  - [ ] Serviços rentáveis
  - [ ] Análise de cancelamentos
  - [ ] Projeções
- **Arquivos:** `financial-report.html`, `financial-report.css`, `financial-report.js`

---

## 💬 **9. MÓDULO NOTIFICAÇÕES**

### 9.1 Central de Notificações

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟢 Baixa
- **Componentes:**
  - [ ] Lista de notificações
  - [ ] Status de entrega
  - [ ] Filtros por tipo
  - [ ] Reenvio manual
- **Arquivos:** `notifications-center.html`, `notifications-center.css`, `notifications-center.js`

### 9.2 Templates de Mensagens

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟢 Baixa
- **Componentes:**
  - [ ] Editor de templates
  - [ ] Variáveis dinâmicas
  - [ ] Preview em tempo real
  - [ ] Teste de envio
- **Arquivos:** `message-templates.html`, `message-templates.css`, `message-templates.js`

### 9.3 ✅ Integração WhatsApp (Implementado)

- [x] **Status:** ✅ Concluído
- [x] **Prioridade:** 🔴 Alta
- **Componentes:**
  - [x] Campo de telefone WhatsApp obrigatório
  - [x] Validação formato brasileiro (99) 99999-9999
  - [x] Formatação automática durante digitação
  - [x] Botão de integração com WhatsApp Web
  - [x] Atualização automática do cadastro do paciente
- **Arquivos:** `novo-agendamento.html` (Integrado no formulário existente)
- **Scripts:**
  ```javascript
  // Formatação de telefone
  function formatarTelefone(input) {...}
  
  // Integração com WhatsApp
  function atualizarLinkWhatsApp() {...}
  ```
- **Design:**
  - Uso de ícone Bootstrap Icons bi-whatsapp em verde
  - Botão verde padrão WhatsApp
  - Campo com máscara e validação visual
  - Feedback imediato ao usuário

---

## 🎨 **10. COMPONENTES GLOBAIS**

### 10.1 Layout Principal

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta
- **Componentes:**
  - [ ] Header responsivo
  - [ ] Sidebar colapsável
  - [ ] Breadcrumbs
  - [ ] Footer
  - [ ] Loading states
- **Arquivos:** `layout.html`, `layout.css`, `layout.js`

### 10.2 Componentes Reutilizáveis

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta
- **Componentes:**
  - [ ] Modais padronizados
  - [ ] Tooltips informativos
  - [ ] Alerts customizados
  - [ ] Cards responsivos
  - [ ] Botões com estados
- **Arquivos:** `components.css`, `components.js`

### 10.3 Sistema de Notificações Toast

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Toast de sucesso
  - [ ] Toast de erro
  - [ ] Toast de informação
  - [ ] Toast de warning
- **Arquivos:** Integrado em `components.js`

---

## 📱 **11. RESPONSIVIDADE**

### 11.1 Breakpoints Bootstrap

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta
- **Resoluções:**
  - [ ] Mobile: 320px - 575px
  - [ ] Tablet: 576px - 991px
  - [ ] Desktop: 992px+
  - [ ] Large Desktop: 1200px+

### 11.2 Componentes Mobile-First

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🔴 Alta
- **Componentes:**
  - [ ] Navegação mobile
  - [ ] Tabelas responsivas
  - [ ] Formulários otimizados
  - [ ] Calendário touch-friendly
- **Arquivos:** `mobile.css`, `mobile.js`

---

## 🔍 **12. FUNCIONALIDADES ESPECIAIS**

### 12.1 Busca Global

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟡 Média
- **Componentes:**
  - [ ] Campo de busca no header
  - [ ] Autocomplete com resultados
  - [ ] Categorização de resultados
  - [ ] Navegação por teclado
- **Arquivos:** `global-search.html`, `global-search.css`, `global-search.js`

### 12.2 Sistema de Ajuda

- [ ] **Status:** ⏳ Pendente
- [ ] **Prioridade:** 🟢 Baixa
- **Componentes:**
  - [ ] FAQ integrado
  - [ ] Tooltips contextuais
  - [ ] Tour guiado
  - [ ] Central de ajuda
- **Arquivos:** `help-system.html`, `help-system.css`, `help-system.js`

---

## 📋 **RESUMO EXECUTIVO**

### **Estatísticas do Projeto**

- **Total de Telas:** 35+ interfaces
- **Prioridade Alta:** 8 telas (MVP)
- **Prioridade Média:** 15 telas
- **Prioridade Baixa:** 12+ telas

### **Tecnologias Obrigatórias**

- **Bootstrap 5.3.0** - Framework CSS
- **jQuery 3.7.0** - Manipulação DOM
- **DataTables** - Tabelas interativas
- **FullCalendar** - Calendário de agendamentos
- **Chart.js** - Gráficos e métricas
- **SweetAlert2** - Alertas customizados

### **Ordem de Desenvolvimento Sugerida**

1. **Layout Principal + Componentes** (Base)
2. **Login + Dashboard** (Autenticação)
3. **Agenda Geral + Novo Agendamento** (Core)
4. **Lista Pacientes + Lista Profissionais** (Gestão)
5. **Prontuário com Imagens** (Diferencial)
6. **Agendamento Online** (Público)
7. **Configurações + Relatórios** (Admin)

### **Entregáveis por Tela**

- **HTML:** Estrutura semântica e acessível
- **CSS:** Estilos responsivos com Bootstrap
- **JavaScript:** Funcionalidades interativas
- **Documentação:** Comentários no código

---

## 📝 **OBSERVAÇÕES IMPORTANTES**

### **Padrões de Desenvolvimento**

- Usar **Bootstrap 5.3.0** como base
- Implementar **mobile-first** approach
- Seguir **convenções semânticas** HTML5
- Aplicar **Progressive Enhancement**
- Garantir **acessibilidade** (WCAG 2.1)

### **Performance**

- Otimizar **imagens** (WebP quando possível)
- Implementar **lazy loading**
- Minificar **CSS/JS** em produção
- Usar **CDNs** para bibliotecas externas

### **Compatibilidade**

- **Chrome 90+**
- **Firefox 88+**
- **Safari 14+**
- **Edge 90+**
- **Mobile browsers** (iOS Safari, Chrome Mobile)

---

**Documento criado por:** Christian V. Mulato  
**Para desenvolvimento:** Guilherme Mulato  
**Projeto:** Sistema de Agendamento Odontológico - Cara Core CCA  
**Última atualização:** 29 de junho de 2025