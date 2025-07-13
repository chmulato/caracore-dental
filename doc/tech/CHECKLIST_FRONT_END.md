# Estrutura Simplificada - WebApp Frontend

## Sistema de Agendamento Odontológico - Cara Core CCA

**Framework:** Spring Boot + Thymeleaf + Bootstrap 5.3.0  
**Data:** 13 de julho de 2025

---

## **Estrutura Simplificada do WebApp**

```plaintext
src/main/
├── java/com/caracore/cca/        # Código Java (Controllers, Services, etc.)
└── resources/
    ├── templates/                # Templates Thymeleaf (.html)
    │   ├── layout/               # Layouts principais
    │   │   ├── main.html         # Layout base do sistema
    │   │   └── public.html       # Layout para páginas públicas
    │   │
    │   ├── fragments/            # Fragmentos reutilizáveis
    │   │   ├── header.html       # Cabeçalho principal (Cara Core Dental - Agendamento)
    │   │   ├── sidebar.html      # Menu lateral para usuários autenticados
    │   │   └── footer.html       # Rodapé
    │   │
    │   ├── auth/                 # Autenticação
    │   │   └── login.html        # Tela de login
    │   │
    │   ├── dashboard/            # Dashboard principal
    │   │   └── index.html        # Painel administrativo
    │   │
    │   ├── professionals/        # Módulo Profissionais
    │   │   ├── list.html         # Lista de profissionais
    │   │   └── form.html         # Cadastro/edição
    │   │
    │   ├── patients/             # Módulo Pacientes
    │   │   ├── list.html         # Lista de pacientes
    │   │   ├── form.html         # Cadastro/edição
    │   │   └── records.html      # Prontuário com imagens
    │   │
    │   ├── schedule/             # Módulo Agenda
    │   │   ├── calendar.html     # Agenda geral
    │   │   └── new-appointment.html # Novo agendamento
    │   │
    │   └── public/               # Agendamento Online (Público)
    │       ├── home.html         # Página inicial pública
    │       └── confirmation.html # Confirmação agendamento
    │
    ├── static/                   # Recursos estáticos
    │   ├── css/                  # Folhas de estilo
    │   │   ├── bootstrap/        # Bootstrap customizado
    │   │   ├── layout/           # CSS de layout
    │   │   │   ├── header.css    # Cabeçalho
    │   │   │   ├── sidebar.css   # Menu lateral
    │   │   │   └── main.css      # Layout principal
    │   │   │
    │   │   └── modules/          # CSS por módulo
    │   │       ├── auth.css      # Autenticação
    │   │       ├── dashboard.css # Dashboard
    │   │       ├── patients.css  # Pacientes
    │   │       └── schedule.css  # Agenda
    │   │
    │   ├── js/                   # JavaScript
    │   │   ├── vendor/           # Bibliotecas terceiros
    │   │   │   ├── jquery.min.js
    │   │   │   └── bootstrap.bundle.min.js
    │   │   │
    │   │   ├── components/       # Componentes JS
    │   │   │   ├── calendar.js   # Configuração FullCalendar
    │   │   │   └── upload.js     # Upload de arquivos (prontuário)
    │   │   │
    │   │   ├── layout/           # JS de layout
    │   │   │   ├── header.js     # Funcionalidades header
    │   │   │   └── sidebar.js    # Menu lateral
    │   │   │
    │   │   └── app.js            # JS principal
    │   │
    │   └── images/               # Imagens do sistema
    │       ├── logo/             # Logos e branding
    │       │   └── logo.png      # Logo principal
    │       │
    │       └── uploads/          # Uploads de usuários
    │           └── records/      # Prontuários com imagens
    │
    └── application.yml           # Configurações Spring Boot
```

---

## **Componentes de Navegação Principais**

### **Menu Lateral (Usuários Autenticados)**

O arquivo `fragments/sidebar.html` implementa o menu lateral para usuários logados, com as seguintes características:

```plaintext
templates/fragments/sidebar.html        # Template Thymeleaf do menu lateral
static/css/layout/sidebar.css           # Estilização do menu
static/js/layout/sidebar.js             # Funcionalidades JavaScript
static/images/logo/logo.png             # Logo no topo do menu
```

**Características:**

- Layout responsivo (expandido/recolhido)
- Itens de menu condicionais por perfil de acesso (ROLE_ADMIN, ROLE_DENTIST, etc.)
- Indicador visual de seção ativa
- Suporte a submenus expansíveis
- Informações do usuário logado no rodapé do menu

### **Header para Agenda Pública**

O arquivo `fragments/header.html` implementa o header para a agenda pública, com as seguintes características:

```plaintext
templates/fragments/header.html         # Template Thymeleaf do header
static/css/layout/header.css            # Estilização do header
static/js/layout/header.js              # Funcionalidades JavaScript
static/images/logo/logo.png             # Logo da Cara Core Dental
```

**Características:**

- Header responsivo para todas as resoluções
- Navegação simplificada para usuários não-autenticados
- Destaque para o botão "Agendar Consulta"
- Implementação de navbar colapsável em mobile

---

## **Arquivos Principais por Funcionalidade**

### **Login e Autenticação**

```plaintext
templates/auth/login.html
static/css/modules/auth.css
static/js/modules/auth.js
static/images/backgrounds/login-bg.jpg
```

### **Dashboard**

```plaintext
templates/dashboard/index.html
static/css/modules/dashboard.css
static/js/modules/dashboard.js
static/js/components/charts.js
```

### **Profissionais**

```plaintext
templates/professionals/list.html
templates/professionals/form.html
static/css/modules/professionals.css
static/js/modules/professionals.js
static/uploads/avatars/professionals/
```

### **Pacientes**

```plaintext
templates/patients/list.html
templates/patients/form.html
templates/patients/records.html
static/css/modules/patients.css
static/js/modules/patients.js
static/js/components/upload.js
static/uploads/records/images/
```

### **Agenda**

```plaintext
templates/schedule/calendar.html
templates/schedule/new-appointment.html
static/css/modules/schedule.css
static/js/modules/schedule.js
static/js/components/calendar.js
```

### **Agendamento Online**

```plaintext
templates/public/home.html
templates/public/select-professional.html
templates/public/confirmation.html
static/css/modules/public.css
static/js/modules/public.js
```

---

## **Prioridades de Desenvolvimento**

### **Prioridade ALTA (MVP)**

1. `layout/main.html` + `fragments/` - Base do sistema
2. `auth/login.html` + CSS/JS - Autenticação
3. `dashboard/index.html` + CSS/JS - Dashboard
4. `schedule/calendar.html` + CSS/JS - Agenda principal
5. `schedule/new-appointment.html` + CSS/JS - Novo agendamento

### **Prioridade MÉDIA**

1. `professionals/list.html` + `professionals/form.html`
2. `patients/list.html` + `patients/form.html`
3. `patients/records.html` - Prontuário com imagens
4. `public/` - Todo módulo agendamento online

### **Prioridade BAIXA**

1. `settings/` - Configurações
2. `reports/` - Relatórios
3. `notifications/` - Notificações

---

## **Convenções de Nomenclatura**

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

## **Configurações Importantes**

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

## **Otimizações**

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
