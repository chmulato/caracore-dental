# Estrutura de Pastas - WebApp Frontend

## Sistema de Agendamento Odontológico - Cara Core CCA

**Framework:** Spring Boot + Thymeleaf + Bootstrap 5.3.0  
**Data:** 29 de junho de 2025

---

## 📁 **Estrutura Completa do WebApp**

```plaintext
src/main/
├── java/com/caracore/cca/        # Código Java (Controllers, Services, etc.)
└── resources/
    ├── templates/                # Templates Thymeleaf (.html)
    │   ├── layout/               # Layouts principais
    │   │   ├── main.html         # Layout base do sistema
    │   │   ├── public.html       # Layout para páginas públicas
    │   │   └── minimal.html      # Layout minimalista (login)
    │   │
    │   ├── fragments/            # Fragmentos reutilizáveis
    │   │   ├── header.html       # Cabeçalho principal
    │   │   ├── sidebar.html      # Menu lateral
    │   │   ├── footer.html       # Rodapé
    │   │   ├── modals.html       # Modais padrão
    │   │   ├── forms.html        # Formulários comuns
    │   │   ├── alerts.html       # Alertas e notificações
    │   │   └── breadcrumb.html   # Navegação estrutural
    │   │
    │   ├── auth/                 # Autenticação
    │   │   ├── login.html        # Tela de login
    │   │   ├── forgot-password.html # Recuperar senha
    │   │   └── reset-password.html  # Redefinir senha
    │   │
    │   ├── dashboard/            # Dashboard principal
    │   │   └── index.html        # Painel administrativo
    │   │
    │   ├── professionals/        # Módulo Profissionais
    │   │   ├── list.html         # Lista de profissionais
    │   │   ├── form.html         # Cadastro/edição
    │   │   └── schedule-config.html # Configuração agenda
    │   │
    │   ├── patients/             # Módulo Pacientes
    │   │   ├── list.html         # Lista de pacientes
    │   │   ├── form.html         # Cadastro/edição
    │   │   ├── history.html      # Histórico do paciente
    │   │   └── records.html      # Prontuário com imagens
    │   │
    │   ├── schedule/             # Módulo Agenda
    │   │   ├── calendar.html     # Agenda geral
    │   │   ├── professional-agenda.html # Agenda individual
    │   │   ├── new-appointment.html     # Novo agendamento
    │   │   └── appointment-details.html # Detalhes agendamento
    │   │
    │   ├── public/               # Agendamento Online (Público)
    │   │   ├── home.html         # Página inicial pública
    │   │   ├── select-professional.html # Escolher profissional
    │   │   ├── select-service.html      # Escolher serviço
    │   │   ├── select-datetime.html     # Escolher data/hora
    │   │   ├── patient-data.html        # Dados do paciente
    │   │   └── confirmation.html        # Confirmação agendamento
    │   │
    │   ├── settings/             # Configurações
    │   │   ├── general.html      # Configurações gerais
    │   │   ├── users.html        # Gerenciar usuários
    │   │   ├── services.html     # Configurar serviços
    │   │   └── notifications.html # Config. notificações
    │   │
    │   ├── reports/              # Relatórios
    │   │   ├── dashboard.html    # Dashboard relatórios
    │   │   ├── appointments.html # Relatório agendamentos
    │   │   ├── professionals.html # Relatório profissionais
    │   │   └── financial.html    # Relatório financeiro
    │   │
    │   ├── notifications/        # Notificações
    │   │   ├── center.html       # Central notificações
    │   │   └── templates.html    # Templates mensagens
    │   │
    │   └── error/                # Páginas de erro
    │       ├── 404.html          # Não encontrado
    │       ├── 500.html          # Erro interno
    │       └── access-denied.html # Acesso negado
    │
    ├── static/                   # Recursos estáticos
    │   ├── css/                  # Folhas de estilo
    │   │   ├── bootstrap/        # Bootstrap customizado
    │   │   │   ├── bootstrap.min.css
    │   │   │   └── bootstrap-custom.css
    │   │   │
    │   │   ├── vendor/           # CSS de terceiros
    │   │   │   ├── datatables.min.css
    │   │   │   ├── fullcalendar.min.css
    │   │   │   ├── chart.min.css
    │   │   │   └── sweetalert2.min.css
    │   │   │
    │   │   ├── components/       # Componentes reutilizáveis
    │   │   │   ├── buttons.css   # Estilos de botões
    │   │   │   ├── cards.css     # Cards personalizados
    │   │   │   ├── forms.css     # Formulários
    │   │   │   ├── tables.css    # Tabelas
    │   │   │   ├── modals.css    # Modais
    │   │   │   └── alerts.css    # Alertas
    │   │   │
    │   │   ├── modules/          # CSS por módulo
    │   │   │   ├── auth.css      # Autenticação
    │   │   │   ├── dashboard.css # Dashboard
    │   │   │   ├── professionals.css # Profissionais
    │   │   │   ├── patients.css  # Pacientes
    │   │   │   ├── schedule.css  # Agenda
    │   │   │   ├── public.css    # Páginas públicas
    │   │   │   ├── settings.css  # Configurações
    │   │   │   ├── reports.css   # Relatórios
    │   │   │   └── notifications.css # Notificações
    │   │   │
    │   │   ├── layout/           # CSS de layout
    │   │   │   ├── header.css    # Cabeçalho
    │   │   │   ├── sidebar.css   # Menu lateral
    │   │   │   ├── footer.css    # Rodapé
    │   │   │   └── main.css      # Layout principal
    │   │   │
    │   │   ├── responsive/       # CSS responsivo
    │   │   │   ├── mobile.css    # Mobile (320px-575px)
    │   │   │   ├── tablet.css    # Tablet (576px-991px)
    │   │   │   └── desktop.css   # Desktop (992px+)
    │   │   │
    │   │   └── main.css          # CSS principal (importa todos)
    │   │
    │   ├── js/                   # JavaScript
    │   │   ├── vendor/           # Bibliotecas terceiros
    │   │   │   ├── jquery.min.js
    │   │   │   ├── bootstrap.bundle.min.js
    │   │   │   ├── datatables.min.js
    │   │   │   ├── fullcalendar.min.js
    │   │   │   ├── chart.min.js
    │   │   │   └── sweetalert2.min.js
    │   │   │
    │   │   ├── components/       # Componentes JS
    │   │   │   ├── modals.js     # Gerenciamento modais
    │   │   │   ├── forms.js      # Validação formulários
    │   │   │   ├── tables.js     # Configuração DataTables
    │   │   │   ├── calendar.js   # Configuração FullCalendar
    │   │   │   ├── charts.js     # Gráficos Chart.js
    │   │   │   ├── upload.js     # Upload de arquivos
    │   │   │   └── notifications.js # Sistema notificações
    │   │   │
    │   │   ├── modules/          # JS por módulo
    │   │   │   ├── auth.js       # Autenticação
    │   │   │   ├── dashboard.js  # Dashboard
    │   │   │   ├── professionals.js # Profissionais
    │   │   │   ├── patients.js   # Pacientes
    │   │   │   ├── schedule.js   # Agenda
    │   │   │   ├── public.js     # Páginas públicas
    │   │   │   ├── settings.js   # Configurações
    │   │   │   ├── reports.js    # Relatórios
    │   │   │   └── notifications-module.js # Notificações
    │   │   │
    │   │   ├── utils/            # Utilitários JS
    │   │   │   ├── helpers.js    # Funções auxiliares
    │   │   │   ├── validators.js # Validações customizadas
    │   │   │   ├── formatters.js # Formatação dados
    │   │   │   ├── api.js        # Chamadas AJAX
    │   │   │   └── constants.js  # Constantes globais
    │   │   │
    │   │   ├── layout/           # JS de layout
    │   │   │   ├── header.js     # Funcionalidades header
    │   │   │   ├── sidebar.js    # Menu lateral
    │   │   │   └── main.js       # Inicialização geral
    │   │   │
    │   │   └── app.js            # JS principal (bootstrap app)
    │   │
    │   ├── images/               # Imagens do sistema
    │   │   ├── logo/             # Logos e branding
    │   │   │   ├── logo.png      # Logo principal
    │   │   │   ├── logo-sm.png   # Logo pequeno
    │   │   │   ├── favicon.ico   # Favicon
    │   │   │   └── icon-192.png  # PWA icon
    │   │   │
    │   │   ├── icons/            # Ícones do sistema
    │   │   │   ├── dentist.svg   # Ícone dentista
    │   │   │   ├── patient.svg   # Ícone paciente
    │   │   │   ├── calendar.svg  # Ícone calendário
    │   │   │   └── settings.svg  # Ícone configurações
    │   │   │
    │   │   ├── avatars/          # Avatars padrão
    │   │   │   ├── default-user.png
    │   │   │   ├── default-dentist.png
    │   │   │   └── default-patient.png
    │   │   │
    │   │   ├── illustrations/    # Ilustrações
    │   │   │   ├── welcome.svg   # Ilustração boas-vindas
    │   │   │   ├── empty-state.svg # Estado vazio
    │   │   │   └── error-404.svg # Erro 404
    │   │   │
    │   │   └── backgrounds/      # Imagens de fundo
    │   │       ├── login-bg.jpg  # Fundo tela login
    │   │       └── public-bg.jpg # Fundo páginas públicas
    │   │
    │   ├── fonts/                # Fontes customizadas
    │   │   ├── inter/            # Fonte Inter
    │   │   │   ├── Inter-Regular.woff2
    │   │   │   ├── Inter-Medium.woff2
    │   │   │   └── Inter-Bold.woff2
    │   │   │
    │   │   └── icons/            # Fonte de ícones
    │   │       ├── cca-icons.woff2
    │   │       └── cca-icons.css
    │   │
    │   ├── uploads/              # Uploads de usuários
    │   │   ├── avatars/          # Fotos de perfil
    │   │   │   ├── professionals/ # Fotos profissionais
    │   │   │   └── patients/     # Fotos pacientes
    │   │   │
    │   │   ├── records/          # Prontuários
    │   │   │   └── images/       # Imagens prontuário
    │   │   │       ├── original/ # Imagens originais
    │   │   │       ├── thumbnails/ # Miniaturas
    │   │   │       └── compressed/ # Comprimidas
    │   │   │
    │   │   └── documents/        # Documentos
    │   │       ├── reports/      # Relatórios exportados
    │   │       └── temp/         # Arquivos temporários
    │   │
    │   └── data/                 # Dados estáticos
    │       ├── states.json       # Estados brasileiros
    │       ├── cities.json       # Cidades
    │       ├── specialties.json  # Especialidades odontológicas
    │       └── dental-procedures.json # Procedimentos
    │
    ├── messages/                  # Mensagens i18n
    │   ├── messages.properties    # Português (padrão)
    │   ├── messages_en.properties # Inglês
    │   └── validation.properties  # Mensagens validação
    │
    └── application.yml            # Configurações Spring Boot
```

---

## 📋 **Arquivos Principais por Funcionalidade**

### **🔐 Login e Autenticação**

```plaintext
templates/auth/login.html
static/css/modules/auth.css
static/js/modules/auth.js
static/images/backgrounds/login-bg.jpg
```

### **🏠 Dashboard**

```plaintext
templates/dashboard/index.html
static/css/modules/dashboard.css
static/js/modules/dashboard.js
static/js/components/charts.js
```

### **👨‍⚕️ Profissionais**

```plaintext
templates/professionals/list.html
templates/professionals/form.html
static/css/modules/professionals.css
static/js/modules/professionals.js
static/uploads/avatars/professionals/
```

### **👩 Pacientes**

```plaintext
templates/patients/list.html
templates/patients/form.html
templates/patients/records.html
static/css/modules/patients.css
static/js/modules/patients.js
static/js/components/upload.js
static/uploads/records/images/
```

### **📅 Agenda**

```plaintext
templates/schedule/calendar.html
templates/schedule/new-appointment.html
static/css/modules/schedule.css
static/js/modules/schedule.js
static/js/components/calendar.js
```

### **🌐 Agendamento Online**

```plaintext
templates/public/home.html
templates/public/select-professional.html
templates/public/confirmation.html
static/css/modules/public.css
static/js/modules/public.js
```

---

## 🎯 **Prioridades de Desenvolvimento**

### **🔴 Prioridade ALTA (MVP)**

1. `layout/main.html` + `fragments/` - Base do sistema
2. `auth/login.html` + CSS/JS - Autenticação
3. `dashboard/index.html` + CSS/JS - Dashboard
4. `schedule/calendar.html` + CSS/JS - Agenda principal
5. `schedule/new-appointment.html` + CSS/JS - Novo agendamento

### **🟡 Prioridade MÉDIA**

1. `professionals/list.html` + `professionals/form.html`
2. `patients/list.html` + `patients/form.html`
3. `patients/records.html` - Prontuário com imagens
4. `public/` - Todo módulo agendamento online

### **🟢 Prioridade BAIXA**

1. `settings/` - Configurações
2. `reports/` - Relatórios
3. `notifications/` - Notificações

---

## 📝 **Convenções de Nomenclatura**

### **Templates Thymeleaf**

- **Padrão:** `{módulo}/{ação}.html`
- **Exemplo:** `patients/form.html`, `schedule/calendar.html`

### **CSS**

- **Módulos:** `modules/{módulo}.css`
- **Componentes:** `components/{componente}.css`
- **Layout:** `layout/{seção}.css`

### **JavaScript**

- **Módulos:** `modules/{módulo}.js`
- **Componentes:** `components/{funcionalidade}.js`
- **Utilitários:** `utils/{utilitário}.js`

### **Imagens**

- **Sistema:** `images/{categoria}/{arquivo}`
- **Uploads:** `uploads/{tipo}/{categoria}/`

---

## 🔧 **Configurações Importantes**

### **Spring Boot Static Resources**

```yaml
spring:
  web:
    resources:
      static-locations:
        - classpath:/static/
      cache-period: 3600
```

### **Thymeleaf Configuration**

```yaml
spring:
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    mode: HTML
    encoding: UTF-8
    cache: false # desenvolvimento
```

### **Upload Configuration**

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB
      location: ${java.io.tmpdir}
```

---

## 📱 **Otimizações**

### **Performance**

- **Minificação:** CSS/JS em produção
- **Compressão:** Gzip para recursos estáticos
- **Cache:** Headers apropriados para imagens
- **CDN:** Bootstrap e jQuery externos

### **SEO (Páginas Públicas)**

- **Meta tags:** Configuradas no layout
- **Schema.org:** Markup estruturado
- **Sitemap:** Para agendamento online
- **Robots.txt:** Configuração crawler

### **Acessibilidade**

- **ARIA labels:** Em todos os componentes
- **Contrast ratio:** WCAG 2.1 AA
- **Keyboard navigation:** Suporte completo
- **Screen readers:** Compatibilidade

---

**Esta estrutura garante organização, escalabilidade e manutenibilidade do frontend do sistema de agendamento odontológico!**