
# Plano Estratégico para Construção de Protótipo Navegável (HTML + CSS + JS)
### Sistema de Agendamento - Cara Core Informática

**Objetivo:** Criar um protótipo navegável, responsivo e funcional para demonstração do sistema a clientes, com foco nos requisitos essenciais do MVP. O protótipo será desenvolvido em HTML, CSS (Bootstrap) e JavaScript puro, sem backend funcional.

---

## 🎯 Público-alvo do Protótipo

- Clínicas odontológicas (1 a 5 dentistas)
- Secretárias e gestores de consultórios
- Investidores ou parceiros comerciais

---

## 🧩 Estrutura do Protótipo (Checklist por Tela)

### ✅ 1. Tela de Login

- [ ] Campos: e-mail, senha
- [ ] Layout simples e responsivo
- [ ] Botão "Entrar"
- [ ] Link "Esqueci minha senha"

### ✅ 2. Tela Principal / Dashboard

- [ ] Boas-vindas ao usuário
- [ ] Atalhos: Agendamentos do dia, Nova Consulta, Pacientes, Relatórios
- [ ] Indicadores: total de atendimentos, próximos horários

### ✅ 3. Agenda

- [ ] Visualização por dia e semana
- [ ] Cores por status (confirmado, cancelado, aguardando)
- [ ] Navegação por data
- [ ] Botão "Novo Agendamento"

### ✅ 4. Novo Agendamento

- [ ] Selecionar profissional
- [ ] Selecionar paciente
- [ ] Escolher serviço
- [ ] Escolher data/hora
- [ ] Botões "Confirmar" e "Cancelar"

### ✅ 5. Cadastro de Pacientes

- [ ] Lista de pacientes com busca
- [ ] Botão "Novo Paciente"
- [ ] Campos: nome, CPF, telefone, e-mail, observações

### ✅ 6. Cadastro de Profissionais

- [ ] Lista de dentistas
- [ ] Campos: nome, especialidade, horários disponíveis

### ✅ 7. Serviços

- [ ] Lista de serviços oferecidos
- [ ] Campos: nome, duração, preço sugerido

### ✅ 8. Relatórios (mock)

- [ ] Indicadores de agendamento (gráfico ou tabela)
- [ ] Filtros por período e profissional

### ✅ 9. Tela Pública (Agendamento Online)

- [ ] Escolha de serviço
- [ ] Escolha de profissional
- [ ] Escolha de data/hora
- [ ] Preenchimento de dados do paciente
- [ ] Confirmação de agendamento (fictício)

---

## 🖥️ Requisitos Técnicos do Protótipo

- Responsivo com **Bootstrap 5**
- Ícones com **Font Awesome** ou similares
- Navegação com **JS puro** (sem backend)
- Dados simulados em **JSON local**
- Navegação com `index.html`, `pacientes.html`, `agenda.html`, etc.

---

## 📌 Objetivos do Protótipo

- Usar em reuniões e apresentações comerciais
- Ajudar o time técnico a validar a navegação e lógica visual
- Servir como base para construção da interface final com Spring + Thymeleaf

---

## 📆 Cronograma Sugerido

```markdown
|--------------------------|------------------|
| Etapa                    | Duração Estimada |
|--------------------------|------------------|
| Levantamento de telas    | 1 dia            |
| Wireframe / Figma        | 2 dias           |
| Implementação HTML/CSS/JS| 3-5 dias         |
| Revisão com stakeholders | 1 dia            |
| Ajustes finais           | 1 dia            |
|--------------------------|------------------|
| Total                    | 8-10 dias        |
|--------------------------|------------------|
```

---

**Responsáveis:** Designer UX/UI + Christian V. Mulato (validação funcional)  
**Versão:** Junho/2025  
**Empresa:** Cara Core Informática
