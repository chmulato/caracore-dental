# Acesso à Agenda Pública - Sistema CCA

## Quem Terá Acesso à Agenda Pública?

### 🌐 **ACESSO PÚBLICO (Sem Autenticação)**

A agenda pública foi projetada para ser **completamente acessível ao público geral**, sem necessidade de login ou cadastro prévio. Qualquer pessoa pode:

#### **Usuários com Acesso:**

- **Pacientes existentes** - podem agendar novas consultas
- **Novos pacientes** - podem fazer seu primeiro agendamento
- **Acompanhantes** - podem agendar para familiares
- **Público em geral** - qualquer pessoa interessada em consultas odontológicas

#### **URLs Públicas Disponíveis:**

```markdown
/public/agendamento                    - Página principal de agendamento
/public/agendamento-confirmado         - Confirmação do agendamento
/api/public/dentistas                  - Lista de dentistas disponíveis
/api/public/horarios-disponiveis       - Horários disponíveis
/api/public/verificar-disponibilidade  - Verificar horário específico
```

---

## 🔒 **Configuração de Segurança**

### **Endpoints Liberados (Sem Autenticação):**

```java
// No SecurityConfig.java
.requestMatchers("/api/public/**", "/public/**").permitAll()
```

### **Endpoints Protegidos (Somente Admins):**

```java
// Controle administrativo da agenda pública
.requestMatchers("/admin/**").hasRole("ADMIN")
```

---

## 👥 **Controle Administrativo**

### **Quem Pode Gerenciar a Agenda Pública:**

#### **ADMINISTRADORES (ROLE: ADMIN)**

- **Ativar/Desativar** a agenda pública
- **Configurar horários** públicos disponíveis
- **Controlar exposição** de dentistas específicos
- **Monitorar estatísticas** de agendamentos públicos
- **Gerenciar usuários** padrão do sistema

#### **Outros Perfis SEM Acesso de Controle:**

- **DENTIST** - Não pode alterar configurações públicas
- **RECEPTIONIST** - Não pode alterar configurações públicas  
- **PATIENT** - Não pode alterar configurações públicas

---

## **Funcionalidades por Tipo de Usuário**

### **PÚBLICO GERAL (Sem Login)**

**Pode:**

- Visualizar dentistas disponíveis *(apenas os ativos e expostos publicamente)*
- Consultar horários disponíveis
- Fazer agendamentos online
- Verificar confirmação de agendamento
- Acessar página inicial de agendamento

**Não Pode:**

- Cancelar agendamentos
- Reagendar consultas  
- Ver histórico de agendamentos
- Acessar área administrativa

### ** ADMINISTRADORES (Com Login)**

**Pode:**

-**TUDO** que o público pode fazer, MAIS:

- Ativar/desativar agenda pública globalmente
- Definir horários de funcionamento público
- Escolher quais dentistas ficam visíveis publicamente
- Monitorar estatísticas de uso
- Gerenciar configurações do sistema
- Ver logs de atividade administrativa

### ** DENTISTAS (Com Login)**

**Pode:**

- Ver seus próprios agendamentos
- Gerenciar consultas da área interna
- Acessar dashboard interno

**Não Pode:**

- Alterar configurações da agenda pública
- Controlar sua própria exposição pública *(somente admins)*

---

## **Controle de Exposição Pública**

### **Dentistas Visíveis ao Público:**

- Somente dentistas **ATIVOS** são elegíveis
- Admins podem **ocultar/expor** dentistas individualmente
- Controle fino por dentista via ferramentas administrativas

### **Método de Controle:**

```java
// No AgendamentoPublicoController
agendamentoService.listarDentistasAtivos(); // Apenas dentistas ativos/expostos
```

---

## **Monitoramento e Auditoria**

### **Logs de Atividade:**

- Todas as alterações administrativas são registradas
- Ativação/desativação da agenda é auditada
- Mudanças de exposição de dentistas são logadas

### **Estatísticas Disponíveis:**

- Total de dentistas no sistema
- Dentistas ativos vs inativos
- Dentistas expostos publicamente
- Atividade de agendamentos públicos

---

## **Como Funciona na Prática**

### **Para o Público:**

1. Acessa `http://localhost:8080/public/agendamento`
2. Vê apenas dentistas ativos e autorizados pelo admin
3. Escolhe dentista, data e horário
4. Preenche dados pessoais
5. Confirma agendamento
6. Recebe confirmação

### **Para Administradores:**

1. Login no sistema como ADMIN
2. Vai para `http://localhost:8080/admin/sistema`
3. Usa ferramentas administrativas para:
   - Ativar/desativar agenda pública
   - Configurar horários disponíveis
   - Controlar quais dentistas aparecem publicamente
   - Monitorar estatísticas

---

## **Segurança e Proteção**

### **Medidas de Segurança:**

- **URLs públicas** claramente separadas (`/public/*`)
- **APIs públicas** com prefixo identificável (`/api/public/*`)
- **Validações** de entrada em todos os formulários
- **Rate limiting** implícito via Spring Security
- **Logs de auditoria** para ações administrativas

### **Proteções Implementadas:**

- Validação de dados obrigatórios
- Prevenção de agendamento no passado
- Verificação de dentistas ativos
- Sanitização de entradas
- Tratamento adequado de erros

---

## **Resumo**

**ACESSO PÚBLICO:**

- Qualquer pessoa pode agendar consultas
- Não precisa criar conta ou fazer login
- Acesso 24/7 via web

**CONTROLE ADMINISTRATIVO:**

- Apenas ADMINs controlam configurações
- Flexibilidade total de configuração
- Monitoramento completo de atividades

**SEGURANÇA:**

- URLs claramente separadas
- Validações robustas
- Auditoria completa de mudanças administrativas
