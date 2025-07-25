# Log de Atividades dos Usuários - Cara Core Agendamento (CCA)

Este documento registra as atividades dos usuários no sistema, permitindo auditar ações e identificar padrões de uso para melhorias futuras.

**Projeto:** [https://github.com/chmulato/cara-core_cca/](https://github.com/chmulato/cara-core_cca/)

## Estrutura do Log

Cada entrada do log segue o formato:

```markdown
## [TIMESTAMP] USERNAME - ACTION
- **IP:** Endereço IP do usuário
- **Dispositivo:** Navegador e sistema operacional
- **Detalhes:** Descrição detalhada da ação
- **Entidades afetadas:** IDs e nomes das entidades alteradas
- **Observações:** Informações adicionais relevantes
```

---

## Logs por Usuário

### admin@caracore.com.br (Administrador do Sistema)

#### [2025-06-30 10:15:23] admin@caracore.com.br - LOGIN

- **IP:** 192.168.1.100
- **Dispositivo:** Chrome 125.0.0.0 / Windows 11
- **Detalhes:** Login bem-sucedido
- **Observações:** Primeiro acesso do dia

#### [2025-06-30 10:22:45] admin@caracore.com.br - CREATE_USER

- **IP:** 192.168.1.100
- **Dispositivo:** Chrome 125.0.0.0 / Windows 11
- **Detalhes:** Criação de novo usuário (dentista)
- **Entidades afetadas:** Usuario ID 56 (maria.silva@exemplo.com.br)
- **Observações:** Usuário criado com permissões padrão de dentista

#### [2025-06-30 11:43:12] admin@caracore.com.br - LOGOUT

- **IP:** 192.168.1.100
- **Dispositivo:** Chrome 125.0.0.0 / Windows 11
- **Detalhes:** Logout normal
- **Observações:** Sessão durou 1h 27m 49s

### maria.silva@exemplo.com.br (Dentista)

#### [2025-06-30 13:05:37] maria.silva@exemplo.com.br - LOGIN

- **IP:** 187.122.45.67
- **Dispositivo:** Safari 17.5.1 / macOS 15.1
- **Detalhes:** Login bem-sucedido (primeiro acesso)
- **Observações:** Usuário completou troca de senha inicial

#### [2025-06-30 13:15:22] maria.silva@exemplo.com.br - VIEW_CALENDAR

- **IP:** 187.122.45.67
- **Dispositivo:** Safari 17.5.1 / macOS 15.1
- **Detalhes:** Visualizou agenda da semana
- **Entidades afetadas:** N/A
- **Observações:** Interagiu com visualização semanal por 12 minutos

#### [2025-06-30 13:28:45] maria.silva@exemplo.com.br - CREATE_APPOINTMENT

- **IP:** 187.122.45.67
- **Dispositivo:** Safari 17.5.1 / macOS 15.1
- **Detalhes:** Criou novo agendamento
- **Entidades afetadas:** Agendamento ID 3478, Paciente ID 562 (João Santos)
- **Observações:** Consulta padrão (30 min)

#### [2025-07-05 09:15:30] maria.silva@exemplo.com.br - LOGIN

- **IP:** 187.122.45.67
- **Dispositivo:** Safari 17.5.1 / macOS 15.1
- **Detalhes:** Login bem-sucedido
- **Observações:** Acesso via desktop

#### [2025-07-05 09:23:12] maria.silva@exemplo.com.br - MODIFY_PACIENTE

- **IP:** 187.122.45.67
- **Dispositivo:** Safari 17.5.1 / macOS 15.1
- **Detalhes:** Modificou registro de paciente
- **Entidades afetadas:** Paciente ID 562 (João Santos)
- **Observações:** Atualizou dados de contato

#### [2025-07-05 10:45:22] maria.silva@exemplo.com.br - DELETE_AGENDAMENTO

- **IP:** 187.122.45.67
- **Dispositivo:** Safari 17.5.1 / macOS 15.1
- **Detalhes:** Excluiu agendamento
- **Entidades afetadas:** Agendamento ID 3480
- **Observações:** Cancelamento solicitado pelo paciente

### recepcionista@exemplo.com.br (Recepcionista)

#### [2025-06-30 08:30:15] recepcionista@exemplo.com.br - LOGIN

- **IP:** 192.168.1.105
- **Dispositivo:** Edge 125.0.0.0 / Windows 11
- **Detalhes:** Login bem-sucedido
- **Observações:** Login automático na inicialização do sistema

#### [2025-06-30 08:45:33] recepcionista@exemplo.com.br - CREATE_PATIENT

- **IP:** 192.168.1.105
- **Dispositivo:** Edge 125.0.0.0 / Windows 11
- **Detalhes:** Criou novo registro de paciente
- **Entidades afetadas:** Paciente ID 891 (Ana Oliveira)
- **Observações:** Paciente encaminhado pela Dra. Maria Silva

#### [2025-06-30 09:12:57] recepcionista@exemplo.com.br - MODIFY_APPOINTMENT

- **IP:** 192.168.1.105
- **Dispositivo:** Edge 125.0.0.0 / Windows 11
- **Detalhes:** Remarcação de consulta
- **Entidades afetadas:** Agendamento ID 3465, Paciente ID 458 (Carlos Ferreira)
- **Observações:** Motivo: paciente solicitou remarcação por telefone

#### [2025-07-05 08:15:42] recepcionista@exemplo.com.br - LOGIN

- **IP:** 192.168.1.105
- **Dispositivo:** Edge 125.0.0.0 / Windows 11
- **Detalhes:** Login bem-sucedido
- **Observações:** Início do expediente

#### [2025-07-05 08:32:15] recepcionista@exemplo.com.br - CREATE_PACIENTE

- **IP:** 192.168.1.105
- **Dispositivo:** Edge 125.0.0.0 / Windows 11
- **Detalhes:** Criou novo registro de paciente
- **Entidades afetadas:** Paciente ID 892 (Roberto Andrade)
- **Observações:** Primeira consulta marcada para amanhã

#### [2025-07-05 11:20:38] recepcionista@exemplo.com.br - MODIFY_AGENDAMENTO

- **IP:** 192.168.1.105
- **Dispositivo:** Edge 125.0.0.0 / Windows 11
- **Detalhes:** Modificou agendamento existente
- **Entidades afetadas:** Agendamento ID 3485
- **Observações:** Alterou horário a pedido do paciente

---

## Como Registrar Novas Entradas

Para adicionar novos registros de atividade, siga o formato acima e mantenha a ordem cronológica. Recomenda-se:

1. Agrupar por usuário para facilitar a auditoria individual
2. Usar formato padronizado de data/hora: [YYYY-MM-DD HH:MM:SS]
3. Categorizar a ação usando verbos claros em maiúsculas (LOGIN, LOGOUT, CREATE, MODIFY, DELETE, VIEW)
4. Incluir todas as informações contextuais nas observações
5. Sempre registrar IDs e nomes das entidades afetadas

## Ações que devem ser registradas

- **LOGIN/LOGOUT:** Todas as entradas e saídas do sistema
- **CREATE/MODIFY/DELETE:** Todas as alterações em entidades importantes (pacientes, agendamentos, usuários)
- **EXPORT:** Download de relatórios e dados
- **PERMISSION_CHANGE:** Alterações em permissões
- **PASSWORD_RESET:** Redefinição de senhas
- **FAILED_LOGIN:** Tentativas de login malsucedidas (importante para segurança)
- **BULK_ACTION:** Ações em massa (cancelamento de agendamentos, envio de notificações)

## Atividades Recentes do Sistema

### Resumo das atividades da última semana (29/06 - 05/07/2025):

- **Total de logins:** 47
- **Novos pacientes cadastrados:** 8
- **Agendamentos criados:** 23
- **Agendamentos modificados:** 12
- **Agendamentos excluídos:** 3
- **Tentativas de login falhadas:** 2

### Principais atividades por usuário:

**Administrador (suporte@caracore.com.br):**

- 5 logins
- 2 usuários criados
- 3 configurações de sistema modificadas

**Dentista (maria.silva@exemplo.com.br):**

- 15 logins
- 18 agendamentos gerenciados
- 5 pacientes atualizados

**Recepcionista (recepcionista@exemplo.com.br):**

- 22 logins
- 8 pacientes cadastrados
- 12 agendamentos processados

### Eventos de Segurança:

#### [2025-07-04 14:32:18] unknown@test.com - FAILED_LOGIN

- **IP:** 203.45.67.89
- **Dispositivo:** Chrome 125.0.0.0 / Windows 10
- **Detalhes:** Tentativa de login com credenciais inválidas
- **Observações:** IP bloqueado temporariamente após 3 tentativas

#### [2025-07-05 02:15:44] sistema - BULK_ACTION

- **IP:** 127.0.0.1
- **Dispositivo:** Sistema Interno
- **Detalhes:** Envio automático de lembretes de consulta
- **Entidades afetadas:** 15 agendamentos para hoje
- **Observações:** Rotina automática executada com sucesso

---

**Última atualização:** 5 de julho de 2025  
**Responsável pela documentação:** Christian V. Mulato  
**Repositório oficial:** [https://github.com/chmulato/cara-core_cca/](https://github.com/chmulato/cara-core_cca/)
