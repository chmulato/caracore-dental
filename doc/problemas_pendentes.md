# Problemas Pendentes - Cara Core Agendamento (CCA)

Este documento lista os problemas pendentes identificados no sistema de agendamento para dentistas e consultórios odontológicos.

**Projeto:** [https://github.com/chmulato/cara-core_cca/](https://github.com/chmulato/cara-core_cca/)

## Problemas Críticos

### Autenticação e Segurança

- [ ] **AUTH-001** - Implementar bloqueio de conta após múltiplas tentativas de login falhas
- [ ] **AUTH-002** - Adicionar autenticação em dois fatores (2FA) para usuários administradores
- [ ] **AUTH-003** - Implementar política de expiração de senhas (90 dias)

### Banco de Dados

- [ ] **DB-001** - Otimizar consultas de agendamento que estão lentas em bancos grandes
- [ ] **DB-002** - Adicionar índices nas tabelas de pacientes e agendamentos
- [ ] **DB-003** - Resolver problema de conexões de banco de dados não liberadas

### Gestão de Consultas Agendadas

- [ ] **CONSUL-001** - Corrigir testes unitários do ConsultasController e AgendamentoService
  - Métodos testados não correspondem aos implementados no service
  - Necessário ajustar mocks e assinatura de métodos
  - Testar compatibilidade com modelo Agendamento atual
- [ ] **CONSUL-002** - Implementar integração com calendário visual (FullCalendar.js)
  - Incluir biblioteca FullCalendar.js via CDN ou WebJars
  - Criar endpoint `/consultas/api/eventos` para fornecer dados do calendário
  - Implementar interface de drag-and-drop para reagendamento
  - Adicionar visualização mensal/semanal/diária
- [ ] **CONSUL-003** - Adicionar validações de horário de funcionamento
  - Validar horários de funcionamento por dentista
  - Implementar regras de antecedência mínima para agendamento
  - Verificar disponibilidade em feriados/férias
  - Adicionar configuração de intervalos de consulta
- [ ] **CONSUL-004** - Implementar notificações automáticas de confirmação via WhatsApp/Email
  - Configurar templates de mensagens WhatsApp
  - Implementar sistema de lembretes automáticos
  - Adicionar confirmação de presença por WhatsApp
  - Criar logs de envio de notificações
- [ ] **CONSUL-005** - Completar implementação dos métodos faltantes no AgendamentoService
  - confirmarAgendamento(Long id)
  - cancelarAgendamento(Long id, String motivo)
  - reagendar(Long id, LocalDateTime novaDataHora)
  - marcarComoRealizada(Long id)

### Frontend

- [ ] **UI-001** - Corrigir layout responsivo da tela de agendamento em dispositivos móveis
- [ ] **UI-002** - Resolver problema de carregamento lento do calendário com muitos agendamentos
- [ ] **UI-003** - Implementar feedback visual durante operações demoradas (loading spinners)
- [ ] **UI-004** - Adicionar interface de drag-and-drop para reagendamento no calendário

## Problemas de Média Prioridade

### Funcionalidades - Gestão de Consultas

- [x] **FUNC-001** - Implementar integração básica com WhatsApp
- [x] **FUNC-002** - Criar interface de listagem de consultas agendadas
- [x] **FUNC-003** - Implementar dashboard de consultas com métricas
- [x] **FUNC-004** - Criar sistema de reagendamento de consultas
- [x] **FUNC-005** - Implementar controle de status de consultas (AGENDADO, CONFIRMADO, CANCELADO, etc.)
- [ ] **FUNC-006** - Adicionar opção de cancelamento pelo paciente
- [ ] **FUNC-007** - Completar implementação do prontuário digital
- [ ] **FUNC-008** - Adicionar filtros avançados nos relatórios
- [ ] **FUNC-009** - Implementar notificações automáticas por WhatsApp com templates
- [ ] **FUNC-010** - Adicionar sistema de lembretes de consulta

### Validações e Regras de Negócio

- [ ] **VALID-001** - Implementar validação de antecedência mínima para agendamento
- [ ] **VALID-002** - Adicionar validação de conflitos de horário mais robusta
- [ ] **VALID-003** - Implementar regras de horário de funcionamento por dentista
- [ ] **VALID-004** - Validar disponibilidade de dentista em feriados/férias

### Performance

- [ ] **PERF-001** - Otimizar carregamento de imagens no prontuário
- [ ] **PERF-002** - Implementar cache para consultas frequentes do dashboard
- [ ] **PERF-003** - Reduzir tempo de resposta da API de agendamento
- [ ] **PERF-004** - Otimizar queries de busca de conflitos de horário

## Problemas de Baixa Prioridade

### Melhorias de Interface

- [ ] **IMP-001** - Melhorar mensagens de erro para o usuário final
- [ ] **IMP-002** - Adicionar tema escuro (dark mode)
- [x] **IMP-003** - Implementar histórico de alterações nos agendamentos
- [ ] **IMP-004** - Adicionar mais opções de relatórios estatísticos
- [ ] **IMP-005** - Implementar busca avançada de consultas
- [ ] **IMP-006** - Adicionar exportação de relatórios em PDF/Excel
- [ ] **IMP-007** - Melhorar responsividade do dashboard em tablets

### Integrações

- [ ] **INT-001** - Integração com Google Calendar
- [ ] **INT-002** - Integração com sistemas de pagamento
- [ ] **INT-003** - API para integração com sistemas terceiros
- [ ] **INT-004** - Sincronização com agenda pessoal dos dentistas

### Documentação

- [ ] **DOC-001** - Atualizar documentação da API REST
- [ ] **DOC-002** - Criar manual do usuário com screenshots
- [ ] **DOC-003** - Documentar processo de instalação em servidores Linux
- [ ] **DOC-004** - Criar guia de uso do sistema de consultas agendadas

### Bugs Conhecidos

### Consultas Agendadas
- [ ] **BUG-006** - Testes unitários do ConsultasController não passam devido a incompatibilidade de métodos
  - Testes assumem métodos que não existem no service atual
  - Necessário alinhar nomes de métodos entre controller e service
  - Corrigir mocks para usar campos corretos do modelo Agendamento
- [ ] **BUG-007** - Verificação de conflito de horário pode não funcionar corretamente com fusos horários
  - Validar comportamento com diferentes fusos horários
  - Garantir consistência entre frontend e backend
- [ ] **BUG-008** - Interface de reagendamento não valida horários passados
  - Adicionar validação de data/hora futura
  - Melhorar feedback para usuário
- [ ] **BUG-009** - Alguns métodos do service não têm implementação completa
  - Verificar implementação de todos os métodos do AgendamentoService
  - Validar retorno de métodos booleanos (confirmar, cancelar, reagendar)

### Geral
- [ ] **BUG-001** - Erro ao agendar consulta em feriados específicos
- [ ] **BUG-002** - Notificações por email não são enviadas quando o servidor está sobrecarregado
- [ ] **BUG-003** - Relatório mensal não contabiliza corretamente cancelamentos
- [ ] **BUG-004** - Upload de imagens falha com arquivos maiores que 5MB
- [ ] **BUG-005** - Inconsistência na exibição de horários entre fusos horários diferentes

## Itens Resolvidos

### Consultas Agendadas - Recentemente Implementados
- [x] **CONSUL-IMPL-001** - Criados templates Thymeleaf para gestão de consultas (lista, detalhes, dashboard, reagendamento)
  - Template `consultas/lista.html` com filtros por status, período e dentista
  - Template `consultas/detalhes.html` com informações completas e histórico
  - Template `consultas/dashboard.html` com estatísticas e métricas
  - Template `consultas/reagendar.html` com formulário de reagendamento
- [x] **CONSUL-IMPL-002** - Implementado ConsultasController com endpoints completos
  - Listagem com filtros avançados
  - Visualização de detalhes
  - Dashboard com estatísticas
  - Reagendamento com verificação de conflitos
  - APIs para calendário e horários disponíveis
- [x] **CONSUL-IMPL-003** - Adicionados métodos no AgendamentoService (confirmar, cancelar, reagendar, marcar como realizado)
  - Implementação básica dos métodos principais
  - Validação de status e regras de negócio
  - Logs de atividade para auditoria
- [x] **CONSUL-IMPL-004** - Criada interface de dashboard com métricas e estatísticas
  - Métricas principais (total, confirmadas, hoje, canceladas)
  - Gráficos de distribuição por status e dentista
  - Consultas de hoje e próximas
  - Taxa de confirmação e tendências
- [x] **CONSUL-IMPL-005** - Implementado sistema de filtros (status, período, dentista)
  - Filtros dinâmicos na listagem
  - Resumo por status na interface
  - Consultas de hoje destacadas
- [x] **CONSUL-IMPL-006** - Adicionado controle de acesso baseado em roles (ADMIN, DENTIST, RECEPTIONIST)
  - Permissões específicas por tipo de usuário
  - Administradores: acesso completo
  - Recepcionistas: criar, editar, cancelar, reagendar
  - Dentistas: visualizar próprios agendamentos e marcar como realizadas
- [x] **CONSUL-IMPL-007** - Integração com WhatsApp nos templates de consultas
  - Links diretos para WhatsApp Web
  - Botões de contato rápido
  - Integração com telefone do paciente
- [x] **CONSUL-IMPL-008** - Expansão do modelo Agendamento com novos campos
  - Status detalhado (AGENDADO, CONFIRMADO, CANCELADO, REAGENDADO, REALIZADO, FALTA)
  - Campos de duração em minutos
  - Telefone WhatsApp dedicado
  - Timestamps de criação e atualização
- [x] **CONSUL-IMPL-009** - Criação da migration V13 para melhorias do agendamento
  - Novos campos de status e duração
  - Índices para performance
  - Compatibilidade com dados existentes

### Melhorias Gerais
- [x] **FIXED-001** - Corrigido problema de autenticação com BCrypt
- [x] **FIXED-002** - Corrigida integração de Bootstrap Icons via WebJars
- [x] **FIXED-003** - Resolvido problema de porta 8080 já em uso durante inicialização
- [x] **FIXED-004** - Corrigido erro na tela de login em navegadores Safari
- [x] **FIXED-005** - Implementada obrigatoriedade do telefone WhatsApp no agendamento
- [x] **FIXED-006** - Adicionada integração direta com WhatsApp Web na tela de agendamento
- [x] **FIXED-007** - Implementado modelo Paciente com campos LGPD, nome social e gênero
- [x] **FIXED-008** - Criadas migrations para suporte LGPD e melhorias de agendamento
- [x] **FIXED-009** - Atualizada documentação com legislação (Portaria 2.836/2011 e LGPD)

---

## Prioridades de Desenvolvimento

### Próximos Sprints (Ordem de Prioridade)

1. **CONSUL-001** - Corrigir testes unitários (CRÍTICO)
   - Ajustar testes para usar métodos corretos do service
   - Corrigir mocks para usar campos corretos do modelo
   - Validar todos os cenários de teste
2. **CONSUL-005** - Completar implementação dos métodos faltantes no Service (CRÍTICO)
   - Implementar métodos que os templates assumem existir
   - Validar retornos booleanos e tratamento de exceções
3. **CONSUL-003** - Validações de horário de funcionamento (ALTO)
   - Implementar regras de horário por dentista
   - Adicionar validação de antecedência mínima
4. **CONSUL-002** - Integração com calendário visual (ALTO)
   - Incluir FullCalendar.js
   - Criar endpoints de API para eventos
   - Implementar interface de drag-and-drop
5. **VALID-001** - Validação de antecedência mínima (MÉDIO)
   - Configurar regras de antecedência por tipo de consulta
6. **FUNC-009** - Notificações automáticas WhatsApp (MÉDIO)
   - Implementar templates de mensagem
   - Configurar sistema de lembretes

### Teste e Validação Recomendados
```bash
# Para testar a interface atual:
mvn spring-boot:run
# Acessar: http://localhost:8080/consultas

# Para executar testes unitários:
mvn test -Dtest=ConsultasControllerTest
mvn test -Dtest=AgendamentoServiceTest
```

### Métricas de Progresso

- **Gestão de Consultas**: 80% concluído
  - ✅ Templates Thymeleaf implementados (lista, detalhes, dashboard, reagendamento)
  - ✅ Controller completo com endpoints
  - ✅ Service com métodos principais
  - ✅ Dashboard com estatísticas
  - ✅ Sistema de filtros
  - ✅ Controle de acesso por roles
  - ✅ Integração WhatsApp
  - ❌ Testes unitários funcionais
  - ❌ Calendário visual
  - ❌ Validações de horário de funcionamento
  - ❌ Notificações automáticas
- **Interface de Usuário**: 85% concluído
- **Validações e Regras**: 40% concluído  
- **Integrações**: 30% concluído
- **Testes**: 60% concluído

### Arquivos Criados/Modificados (Gestão de Consultas)

#### Templates Thymeleaf:
- `src/main/resources/templates/consultas/lista.html`
- `src/main/resources/templates/consultas/detalhes.html`
- `src/main/resources/templates/consultas/dashboard.html`
- `src/main/resources/templates/consultas/reagendar.html`

#### Controllers:
- `src/main/java/com/caracore/cca/controller/ConsultasController.java`

#### Services:
- `src/main/java/com/caracore/cca/service/AgendamentoService.java` (expandido)

#### Repositories:
- `src/main/java/com/caracore/cca/repository/AgendamentoRepository.java` (expandido)

#### Migrations:
- `src/main/resources/db/migration/V13__melhorias_agendamento.sql`

#### Testes (necessitam correção):
- `src/test/java/com/caracore/cca/controller/ConsultasControllerTest.java`
- `src/test/java/com/caracore/cca/service/AgendamentoServiceTest.java`

### Funcionalidades por Perfil de Usuário

#### Para Administradores:
- Acesso completo: visualizar, criar, editar, cancelar, excluir
- Dashboard com métricas completas
- Gestão de usuários e permissões
- Relatórios avançados

#### Para Recepcionistas:
- Visualizar, criar, editar e cancelar agendamentos
- Reagendar consultas
- Confirmar presença de pacientes
- Dashboard básico

#### Para Dentistas:
- Visualizar agendamentos próprios
- Marcar consultas como realizadas
- Ver dashboard de suas consultas
- Reagendar próprias consultas

### Integração LGPD Mantida ✅

As consultas mantêm compatibilidade com:
- Consentimento LGPD dos pacientes
- Campos de nome social e gênero
- Logs de atividade do usuário
- Controle de acesso a dados pessoais

---

## Como Reportar Novos Problemas

Para reportar novos problemas, por favor inclua:

1. **Identificador**: Um código único seguindo o padrão CATEGORIA-NNN
2. **Descrição**: Descrição clara e concisa do problema
3. **Como Reproduzir**: Passos para reproduzir o problema
4. **Comportamento Esperado**: O que deveria acontecer
5. **Comportamento Atual**: O que está acontecendo
6. **Screenshots**: Se aplicável
7. **Ambiente**: Navegador, sistema operacional, dispositivo
8. **Contexto Adicional**: Qualquer informação relevante

---

**Última atualização:** 3 de julho de 2025  
**Responsável pela documentação:** Christian V. Mulato  
**Repositório oficial:** [https://github.com/chmulato/cara-core_cca/](https://github.com/chmulato/cara-core_cca/)  
**Status do projeto:** 80% concluído - Gestão de consultas agendadas implementada com interface funcional, necessita correção de testes e implementação de calendário visual
