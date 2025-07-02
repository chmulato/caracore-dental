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

### Frontend

- [ ] **UI-001** - Corrigir layout responsivo da tela de agendamento em dispositivos móveis
- [ ] **UI-002** - Resolver problema de carregamento lento do calendário com muitos agendamentos
- [ ] **UI-003** - Implementar feedback visual durante operações demoradas (loading spinners)

## Problemas de Média Prioridade

### Funcionalidades

- [x] **FUNC-001** - Implementar integração básica com WhatsApp
- [ ] **FUNC-002** - Adicionar opção de cancelamento pelo paciente
- [ ] **FUNC-003** - Completar implementação do prontuário digital
- [ ] **FUNC-004** - Adicionar filtros avançados nos relatórios
- [ ] **FUNC-005** - Implementar notificações automáticas por WhatsApp

### Performance

- [ ] **PERF-001** - Otimizar carregamento de imagens no prontuário
- [ ] **PERF-002** - Implementar cache para consultas frequentes
- [ ] **PERF-003** - Reduzir tempo de resposta da API de agendamento

## Problemas de Baixa Prioridade

### Melhorias

- [ ] **IMP-001** - Melhorar mensagens de erro para o usuário final
- [ ] **IMP-002** - Adicionar tema escuro (dark mode)
- [ ] **IMP-003** - Implementar histórico de alterações nos agendamentos
- [ ] **IMP-004** - Adicionar mais opções de relatórios estatísticos

### Documentação

- [ ] **DOC-001** - Atualizar documentação da API REST
- [ ] **DOC-002** - Criar manual do usuário com screenshots
- [ ] **DOC-003** - Documentar processo de instalação em servidores Linux

## Bugs Conhecidos

- [ ] **BUG-001** - Erro ao agendar consulta em feriados específicos
- [ ] **BUG-002** - Notificações por email não são enviadas quando o servidor está sobrecarregado
- [ ] **BUG-003** - Relatório mensal não contabiliza corretamente cancelamentos
- [ ] **BUG-004** - Upload de imagens falha com arquivos maiores que 5MB
- [ ] **BUG-005** - Inconsistência na exibição de horários entre fusos horários diferentes

## Itens Resolvidos

- [x] **FIXED-001** - Corrigido problema de autenticação com BCrypt
- [x] **FIXED-002** - Corrigida integração de Bootstrap Icons via WebJars
- [x] **FIXED-003** - Resolvido problema de porta 8080 já em uso durante inicialização
- [x] **FIXED-004** - Corrigido erro na tela de login em navegadores Safari
- [x] **FIXED-005** - Implementada obrigatoriedade do telefone WhatsApp no agendamento
- [x] **FIXED-006** - Adicionada integração direta com WhatsApp Web na tela de agendamento

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

**Última atualização:** 2 de julho de 2025  
**Responsável pela documentação:** Christian V. Mulato  
**Repositório oficial:** [https://github.com/chmulato/cara-core_cca/](https://github.com/chmulato/cara-core_cca/)
