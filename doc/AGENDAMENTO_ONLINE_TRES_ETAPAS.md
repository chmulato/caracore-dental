# Implementação de Agendamento Online em Três Etapas

Para facilitar o processo de agendamento online pelos pacientes, foi implementada uma nova jornada de agendamento em três etapas, tornando o processo mais intuitivo e melhorando a experiência do usuário.

## Etapas do Agendamento

O processo de agendamento foi dividido em três etapas sequenciais:

1. **Dados Pessoais e Escolha do Dentista**
   - Captação de informações do paciente (nome, telefone, email)
   - Seleção do dentista para atendimento

2. **Seleção de Data e Horário**
   - Visualização de um calendário interativo
   - Exibição de horários disponíveis específicos para o dentista selecionado
   - Seleção visual do horário preferido

3. **Confirmação e Finalização**
   - Resumo dos dados informados e horário selecionado
   - Aceitação dos termos de agendamento
   - Confirmação final da consulta

## Tecnologias Utilizadas

- **Frontend**:
  - Thymeleaf para renderização de templates
  - Bootstrap 5.3.0 para layout responsivo
  - FullCalendar.js 5.11.3 para visualização do calendário interativo
  - JavaScript para validações e interatividade
  - Bootstrap Icons para ícones consistentes

- **Backend**:
  - Spring MVC para controle do fluxo de agendamento
  - Armazenamento temporário em sessão entre etapas
  - Validações em cada etapa para garantir integridade dos dados

## Melhorias Implementadas

- **Experiência do Usuário**:
  - Indicador visual de progresso mostrando em qual etapa o usuário está
  - Botões de navegação entre etapas (avançar/retornar)
  - Resumo das informações já fornecidas em cada etapa
  - Design responsivo otimizado para dispositivos móveis

- **Calendário Visual**:
  - Substituição dos campos de data e hora por um calendário interativo
  - Visualização de horários disponíveis por cores
  - Filtragem automática de horários indisponíveis
  - Alternância entre visualização mensal e semanal

- **Segurança e Validação**:
  - Validações em cada etapa antes de prosseguir
  - Verificação dupla de disponibilidade no momento da confirmação
  - Proteção contra agendamentos duplicados

## Estrutura de Arquivos

```plaintext
src/main/resources/templates/public/
├── agendamento-landing.html     # Página inicial de agendamento
├── agendamento-etapa1.html      # Etapa 1: Dados pessoais e escolha do dentista
├── agendamento-etapa2.html      # Etapa 2: Seleção de data e horário no calendário
├── agendamento-etapa3.html      # Etapa 3: Confirmação e finalização
└── agendamento-confirmado.html  # Página de confirmação após agendamento finalizado
```

```plaintext
src/main/java/com/caracore/agendamento/controllers/
└── AgendamentoPublicoController.java  # Controlador para o fluxo de agendamento
```

## Fluxo de Navegação

1. O usuário acessa a landing page de agendamento (`/public/agendamento`)
2. É redirecionado para a Etapa 1 (`/public/agendamento/etapa1`)
3. Após preencher os dados pessoais, avança para a Etapa 2 (`/public/agendamento/etapa2`)
4. Seleciona data e horário no calendário e avança para a Etapa 3 (`/public/agendamento/etapa3`)
5. Confirma os dados e finaliza o agendamento
6. É direcionado para a página de confirmação (`/public/agendamento/confirmado`)

## Próximas Melhorias Planejadas

- Integração com autenticação para pacientes recorrentes
- Pré-preenchimento de dados para pacientes logados
- Histórico de consultas anteriores
- Opção de reagendamento e cancelamento online
- Envio automático de lembretes por WhatsApp/Email

## Status de Implementação

- [x] Página de landing do agendamento
- [x] Etapa 1 - Formulário de dados pessoais
- [x] Etapa 2 - Calendário interativo
- [x] Etapa 3 - Página de confirmação
- [x] Página de confirmação final
- [x] Controlador para gerenciar o fluxo entre etapas
- [x] Persistência temporária de dados entre etapas
- [ ] Testes automatizados do fluxo completo
- [ ] Monitoramento de falhas no processo de agendamento
