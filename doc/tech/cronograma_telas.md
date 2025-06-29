# Cronograma de Implementação - Telas Thymeleaf Spring Boot MVC
## Sistema de Agendamento Odontológico - Cara Core CCA

**Data:** 29 de junho de 2025  
**Responsável Backend:** Christian V. Mulato  
**Responsável Frontend:** Guilherme Mulato  
**Framework:** Spring Boot + Thymeleaf + Bootstrap 5.3.0

---

## 🏗️ **Estrutura de Arquivos no Projeto Spring Boot**

### **Diretórios Principais**

```plaintext
src/main/
├── java/com/caracore/cca/
│   ├── controller/          # Controladores MVC
│   ├── service/            # Lógica de negócio
│   ├── entity/             # Entidades JPA
│   ├── repository/         # Repositories
│   └── dto/                # Data Transfer Objects
├── resources/
│   ├── templates/          # Templates Thymeleaf (.html)
│   ├── static/
│   │   ├── css/           # Arquivos CSS customizados
│   │   ├── js/            # JavaScript customizado
│   │   ├── images/        # Imagens do sistema
│   │   └── uploads/       # Uploads de usuários
│   └── application.yml    # Configurações
```

---

## 📋 **Mapeamento Completo: Telas → Arquivos**

### 🔐 **1. MÓDULO DE AUTENTICAÇÃO**

#### **1.1 Tela de Login**

- **Controller:** `AuthController.java`
- **Template:** `templates/auth/login.html`
- **CSS:** `static/css/auth.css`
- **JS:** `static/js/auth.js`
- **Método:** `@GetMapping("/login")` → `showLoginPage()`
- **Método:** `@PostMapping("/login")` → `processLogin()`

#### **1.2 Tela de Recuperação de Senha**

- **Controller:** `AuthController.java`
- **Template:** `templates/auth/forgot-password.html`
- **Método:** `@GetMapping("/forgot-password")` → `showForgotPasswordPage()`
- **Método:** `@PostMapping("/forgot-password")` → `processForgotPassword()`

#### **1.3 Tela de Redefinição de Senha**

- **Controller:** `AuthController.java`
- **Template:** `templates/auth/reset-password.html`
- **Método:** `@GetMapping("/reset-password")` → `showResetPasswordPage()`
- **Método:** `@PostMapping("/reset-password")` → `processResetPassword()`

---

### 🏠 **2. DASHBOARD PRINCIPAL**

#### **2.1 Dashboard Administrativo**

- **Controller:** `DashboardController.java`
- **Template:** `templates/dashboard/index.html`
- **CSS:** `static/css/dashboard.css`
- **JS:** `static/js/dashboard.js`
- **Método:** `@GetMapping("/dashboard")` → `showDashboard(Model model)`
- **Método:** `@GetMapping("/api/dashboard/metrics")` → `getDashboardMetrics()` (AJAX)

---

### 🧑‍⚕️ **3. MÓDULO PROFISSIONAIS**

#### **3.1 Lista de Profissionais**

- **Controller:** `ProfessionalController.java`
- **Template:** `templates/professionals/list.html`
- **CSS:** `static/css/professionals.css`
- **JS:** `static/js/professionals.js`
- **Método:** `@GetMapping("/professionals")` → `showProfessionalsList(Model model)`
- **Método:** `@GetMapping("/api/professionals")` → `getProfessionalsJson()` (DataTables)

#### **3.2 Cadastro/Edição de Profissional**

- **Controller:** `ProfessionalController.java`
- **Template:** `templates/professionals/form.html`
- **Método:** `@GetMapping("/professionals/new")` → `showNewProfessionalForm()`
- **Método:** `@GetMapping("/professionals/{id}/edit")` → `showEditProfessionalForm()`
- **Método:** `@PostMapping("/professionals")` → `saveProfessional()`
- **Método:** `@PutMapping("/professionals/{id}")` → `updateProfessional()`

#### **3.3 Configuração de Agenda do Profissional**

- **Controller:** `ProfessionalScheduleController.java`
- **Template:** `templates/professionals/schedule-config.html`
- **CSS:** `static/css/schedule-config.css`
- **JS:** `static/js/schedule-config.js`
- **Método:** `@GetMapping("/professionals/{id}/schedule")` → `showScheduleConfig()`
- **Método:** `@PostMapping("/professionals/{id}/schedule")` → `saveScheduleConfig()`

---

### 👩 **4. MÓDULO PACIENTES**

#### **4.1 Lista de Pacientes**

- **Controller:** `PatientController.java`
- **Template:** `templates/patients/list.html`
- **CSS:** `static/css/patients.css`
- **JS:** `static/js/patients.js`
- **Método:** `@GetMapping("/patients")` → `showPatientsList(Model model)`
- **Método:** `@GetMapping("/api/patients")` → `getPatientsJson()` (DataTables)

#### **4.2 Cadastro/Edição de Paciente**

- **Controller:** `PatientController.java`
- **Template:** `templates/patients/form.html`
- **Método:** `@GetMapping("/patients/new")` → `showNewPatientForm()`
- **Método:** `@GetMapping("/patients/{id}/edit")` → `showEditPatientForm()`
- **Método:** `@PostMapping("/patients")` → `savePatient()`
- **Método:** `@PutMapping("/patients/{id}")` → `updatePatient()`

#### **4.3 Histórico do Paciente**

- **Controller:** `PatientController.java`
- **Template:** `templates/patients/history.html`
- **CSS:** `static/css/patient-history.css`
- **JS:** `static/js/patient-history.js`
- **Método:** `@GetMapping("/patients/{id}/history")` → `showPatientHistory()`
- **Método:** `@GetMapping("/api/patients/{id}/appointments")` → `getPatientAppointments()`

#### **4.4 Prontuário com Imagens**

- **Controller:** `PatientRecordsController.java`
- **Template:** `templates/patients/records.html`
- **CSS:** `static/css/patient-records.css`
- **JS:** `static/js/patient-records.js`
- **Método:** `@GetMapping("/patients/{id}/records")` → `showPatientRecords()`
- **Método:** `@PostMapping("/patients/{id}/records/upload")` → `uploadRecordImage()`
- **Método:** `@DeleteMapping("/patients/records/{imageId}")` → `deleteRecordImage()`

---

### 🗓️ **5. MÓDULO AGENDA**

#### **5.1 Agenda Geral**

- **Controller:** `ScheduleController.java`
- **Template:** `templates/schedule/calendar.html`
- **CSS:** `static/css/schedule.css`
- **JS:** `static/js/schedule.js`
- **Método:** `@GetMapping("/schedule")` → `showSchedule(Model model)`
- **Método:** `@GetMapping("/api/schedule/events")` → `getScheduleEvents()` (FullCalendar)

#### **5.2 Agenda por Profissional**

- **Controller:** `ScheduleController.java`
- **Template:** `templates/schedule/professional-agenda.html`
- **Método:** `@GetMapping("/schedule/professional/{id}")` → `showProfessionalSchedule()`
- **Método:** `@GetMapping("/api/schedule/professional/{id}/events")` → `getProfessionalEvents()`

#### **5.3 Novo Agendamento**

- **Controller:** `AppointmentController.java`
- **Template:** `templates/appointments/form.html`
- **CSS:** `static/css/appointments.css`
- **JS:** `static/js/appointments.js`
- **Método:** `@GetMapping("/appointments/new")` → `showNewAppointmentForm()`
- **Método:** `@PostMapping("/appointments")` → `saveAppointment()`
- **Método:** `@GetMapping("/api/appointments/available-times")` → `getAvailableTimes()`

#### **5.4 Detalhes do Agendamento**

- **Controller:** `AppointmentController.java`
- **Template:** `templates/appointments/details.html`
- **Método:** `@GetMapping("/appointments/{id}")` → `showAppointmentDetails()`
- **Método:** `@PutMapping("/appointments/{id}/status")` → `updateAppointmentStatus()`
- **Método:** `@PutMapping("/appointments/{id}/reschedule")` → `rescheduleAppointment()`

---

### 📅 **6. AGENDAMENTO ONLINE (PÚBLICO)**

#### **6.1 Página Inicial Pública**

- **Controller:** `PublicController.java`
- **Template:** `templates/public/index.html`
- **CSS:** `static/css/public.css`
- **JS:** `static/js/public.js`
- **Método:** `@GetMapping("/")` → `showPublicHomePage()`

#### **6.2 Seleção de Profissional**

- **Controller:** `PublicAppointmentController.java`
- **Template:** `templates/public/select-professional.html`
- **Método:** `@GetMapping("/agendar/profissional")` → `showSelectProfessional()`
- **Método:** `@GetMapping("/api/public/professionals")` → `getAvailableProfessionals()`

#### **6.3 Seleção de Serviço**

- **Controller:** `PublicAppointmentController.java`
- **Template:** `templates/public/select-service.html`
- **Método:** `@GetMapping("/agendar/servico")` → `showSelectService()`
- **Método:** `@GetMapping("/api/public/professionals/{id}/services")` → `getProfessionalServices()`

#### **6.4 Escolha de Data/Horário**

- **Controller:** `PublicAppointmentController.java`
- **Template:** `templates/public/select-datetime.html`
- **CSS:** `static/css/public-calendar.css`
- **JS:** `static/js/public-calendar.js`
- **Método:** `@GetMapping("/agendar/horario")` → `showSelectDateTime()`
- **Método:** `@GetMapping("/api/public/available-slots")` → `getAvailableSlots()`

#### **6.5 Dados do Paciente**

- **Controller:** `PublicAppointmentController.java`
- **Template:** `templates/public/patient-data.html`
- **Método:** `@GetMapping("/agendar/dados")` → `showPatientDataForm()`
- **Método:** `@PostMapping("/agendar/dados")` → `processPatientData()`

#### **6.6 Confirmação do Agendamento**

- **Controller:** `PublicAppointmentController.java`
- **Template:** `templates/public/confirmation.html`
- **Método:** `@GetMapping("/agendar/confirmacao")` → `showConfirmation()`
- **Método:** `@PostMapping("/agendar/confirmar")` → `confirmAppointment()`

---

### 🔧 **7. MÓDULO CONFIGURAÇÕES**

#### **7.1 Configurações Gerais**

- **Controller:** `SettingsController.java`
- **Template:** `templates/settings/general.html`
- **CSS:** `static/css/settings.css`
- **JS:** `static/js/settings.js`
- **Método:** `@GetMapping("/settings")` → `showGeneralSettings()`
- **Método:** `@PostMapping("/settings/general")` → `saveGeneralSettings()`

#### **7.2 Gerenciamento de Usuários**

- **Controller:** `UserManagementController.java`
- **Template:** `templates/settings/users.html`
- **Método:** `@GetMapping("/settings/users")` → `showUserManagement()`
- **Método:** `@PostMapping("/settings/users")` → `createUser()`
- **Método:** `@PutMapping("/settings/users/{id}")` → `updateUser()`

#### **7.3 Configuração de Serviços**

- **Controller:** `ServiceController.java`
- **Template:** `templates/settings/services.html`
- **Método:** `@GetMapping("/settings/services")` → `showServicesConfig()`
- **Método:** `@PostMapping("/settings/services")` → `createService()`
- **Método:** `@PutMapping("/settings/services/{id}")` → `updateService()`

#### **7.4 Configurações de Notificações**

- **Controller:** `NotificationSettingsController.java`
- **Template:** `templates/settings/notifications.html`
- **Método:** `@GetMapping("/settings/notifications")` → `showNotificationSettings()`
- **Método:** `@PostMapping("/settings/notifications")` → `saveNotificationSettings()`

---

### 📊 **8. MÓDULO RELATÓRIOS**

#### **8.1 Dashboard de Relatórios**

- **Controller:** `ReportsController.java`
- **Template:** `templates/reports/dashboard.html`
- **CSS:** `static/css/reports.css`
- **JS:** `static/js/reports.js`
- **Método:** `@GetMapping("/reports")` → `showReportsDashboard()`
- **Método:** `@GetMapping("/api/reports/summary")` → `getReportsSummary()`

#### **8.2 Relatório de Agendamentos**

- **Controller:** `ReportsController.java`
- **Template:** `templates/reports/appointments.html`
- **Método:** `@GetMapping("/reports/appointments")` → `showAppointmentsReport()`
- **Método:** `@GetMapping("/api/reports/appointments/data")` → `getAppointmentsReportData()`
- **Método:** `@GetMapping("/reports/appointments/export")` → `exportAppointmentsReport()`

#### **8.3 Relatório de Profissionais**

- **Controller:** `ReportsController.java`
- **Template:** `templates/reports/professionals.html`
- **Método:** `@GetMapping("/reports/professionals")` → `showProfessionalsReport()`
- **Método:** `@GetMapping("/api/reports/professionals/data")` → `getProfessionalsReportData()`

#### **8.4 Relatório Financeiro**

- **Controller:** `FinancialReportsController.java`
- **Template:** `templates/reports/financial.html`
- **Método:** `@GetMapping("/reports/financial")` → `showFinancialReport()`
- **Método:** `@GetMapping("/api/reports/financial/data")` → `getFinancialReportData()`

---

### 💬 **9. MÓDULO NOTIFICAÇÕES**

#### **9.1 Central de Notificações**

- **Controller:** `NotificationController.java`
- **Template:** `templates/notifications/center.html`
- **CSS:** `static/css/notifications.css`
- **JS:** `static/js/notifications.js`
- **Método:** `@GetMapping("/notifications")` → `showNotificationCenter()`
- **Método:** `@GetMapping("/api/notifications")` → `getNotifications()`
- **Método:** `@PostMapping("/notifications/{id}/resend")` → `resendNotification()`

#### **9.2 Templates de Mensagens**

- **Controller:** `NotificationTemplateController.java`
- **Template:** `templates/notifications/templates.html`
- **Método:** `@GetMapping("/notifications/templates")` → `showMessageTemplates()`
- **Método:** `@PostMapping("/notifications/templates")` → `saveTemplate()`
- **Método:** `@PostMapping("/notifications/templates/test")` → `testTemplate()`

---

## 🎨 **10. LAYOUTS E COMPONENTES GLOBAIS**

### **10.1 Layout Principal**

- **Template:** `templates/layout/main.html`
- **Header:** `templates/fragments/header.html`
- **Sidebar:** `templates/fragments/sidebar.html`
- **Footer:** `templates/fragments/footer.html`

### **10.2 Fragmentos Reutilizáveis**

- **Modais:** `templates/fragments/modals.html`
- **Forms:** `templates/fragments/forms.html`
- **Tables:** `templates/fragments/tables.html`
- **Alerts:** `templates/fragments/alerts.html`

### **10.3 CSS Global**

- **Arquivo:** `static/css/main.css`
- **Arquivo:** `static/css/components.css`
- **Arquivo:** `static/css/responsive.css`

### **10.4 JavaScript Global**

- **Arquivo:** `static/js/main.js`
- **Arquivo:** `static/js/components.js`
- **Arquivo:** `static/js/utils.js`

---

## 📱 **11. ARQUIVOS DE CONFIGURAÇÃO THYMELEAF**

### **11.1 application.yml - Configuração Thymeleaf**

```yaml
spring:
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    mode: HTML
    encoding: UTF-8
    cache: false  # Desenvolvimento
    check-template: true
    check-template-location: true
```

### **11.2 Estrutura de Templates**

```markdown
templates/
├── layout/
│   └── main.html              # Layout principal
├── fragments/
│   ├── header.html            # Cabeçalho
│   ├── sidebar.html           # Menu lateral
│   ├── footer.html            # Rodapé
│   ├── modals.html            # Modais reutilizáveis
│   └── alerts.html            # Alertas
├── auth/
│   ├── login.html
│   ├── forgot-password.html
│   └── reset-password.html
├── dashboard/
│   └── index.html
├── professionals/
│   ├── list.html
│   ├── form.html
│   └── schedule-config.html
├── patients/
│   ├── list.html
│   ├── form.html
│   ├── history.html
│   └── records.html
├── schedule/
│   ├── calendar.html
│   └── professional-agenda.html
├── appointments/
│   ├── form.html
│   └── details.html
├── public/
│   ├── index.html
│   ├── select-professional.html
│   ├── select-service.html
│   ├── select-datetime.html
│   ├── patient-data.html
│   └── confirmation.html
├── settings/
│   ├── general.html
│   ├── users.html
│   ├── services.html
│   └── notifications.html
├── reports/
│   ├── dashboard.html
│   ├── appointments.html
│   ├── professionals.html
│   └── financial.html
├── notifications/
│   ├── center.html
│   └── templates.html
└── error/
    ├── 404.html
    ├── 500.html
    └── access-denied.html
```

---

## 🚀 **12. CRONOGRAMA DE IMPLEMENTAÇÃO**

### **Fase 1 - Fundação (Semana 1-2)**

- [ ] Layout principal e fragmentos
- [ ] Sistema de autenticação
- [ ] Dashboard básico
- [ ] CSS/JS globais

### **Fase 2 - Core MVP (Semana 3-4)**

- [ ] Agenda geral
- [ ] Novo agendamento
- [ ] Lista de pacientes
- [ ] Lista de profissionais

### **Fase 3 - Gestão (Semana 5-6)**

- [ ] Formulários de cadastro
- [ ] Histórico de pacientes
- [ ] Prontuário com imagens
- [ ] Configuração de agenda

### **Fase 4 - Público (Semana 7-8)**

- [ ] Agendamento online
- [ ] Páginas públicas
- [ ] Confirmações
- [ ] Notificações

### **Fase 5 - Administrativo (Semana 9-10)**

- [ ] Configurações do sistema
- [ ] Relatórios
- [ ] Central de notificações
- [ ] Testes finais

---

## 📝 **13. CONVENÇÕES DE NOMENCLATURA**

### **13.1 Controllers**

- Padrão: `{Módulo}Controller.java`
- Exemplo: `PatientController.java`, `AppointmentController.java`

### **13.2 Templates Thymeleaf**

- Padrão: `{módulo}/{ação}.html`
- Exemplo: `patients/list.html`, `appointments/form.html`

### **13.3 CSS/JS**

- Padrão: `{módulo}.css`, `{módulo}.js`
- Exemplo: `patients.css`, `schedule.js`

### **13.4 URLs/Endpoints**

- Padrão RESTful: `/resource` (GET), `/resource/{id}` (GET/PUT/DELETE)
- APIs: `/api/{resource}`
- Público: sem prefixo ou `/public/`

### **13.5 Métodos dos Controllers**

- Lista: `show{Resource}List()`
- Formulário: `show{Resource}Form()`, `showNew{Resource}Form()`
- Salvar: `save{Resource}()`, `update{Resource}()`
- Detalhes: `show{Resource}Details()`

---

## ⚙️ **14. CONFIGURAÇÕES ESPECIAIS**

### **14.1 Upload de Arquivos**

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB
      location: ${java.io.tmpdir}
```

### **14.2 Recursos Estáticos**

```yaml
spring:
  web:
    resources:
      static-locations: classpath:/static/
      cache-period: 31536000  # Produção
```

### **14.3 Interceptors para Segurança**

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/login", "/public/**", "/css/**", "/js/**");
    }
}
```

---

**Total de Arquivos Estimados:**

- **Templates:** 35+ arquivos `.html`
- **Controllers:** 15+ arquivos `.java`
- **CSS:** 20+ arquivos `.css`
- **JavaScript:** 20+ arquivos `.js`

**Observação:** Todos os templates utilizarão a extensão `.html` e seguirão as convenções do Thymeleaf com o prefixo `th:` para atributos dinâmicos.