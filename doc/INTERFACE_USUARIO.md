# Interface do Usuário - Cara Core Dental

**Versão:** 1.0  
**Data:** 13/07/2025  
**Autor:** Christian V. Mulato  
**Status:** Implementado

## 1. Visão Geral

Este documento descreve a interface do usuário do sistema Cara Core Dental, focando na experiência de navegação e layout para diferentes perfis de usuário. A interface foi projetada para ser intuitiva, responsiva e adequada ao contexto odontológico.

## 2. Estrutura de Interface

### 2.1. Sistema Interno (Usuários Autenticados)

Após o login bem-sucedido, todos os usuários autenticados terão acesso a uma interface que consiste em:

#### Menu Lateral

- **Comportamento:** Menu lateral fixo que contém todas as funcionalidades do sistema
- **Características:**
  - Expansível/Retrátil para otimizar espaço em dispositivos menores
  - Organizado por módulos (Agenda, Pacientes, Prontuários, etc.)
  - Personalizado conforme o perfil do usuário (ADMIN, DENTIST, STAFF)
  - Indicador visual da página atual
  - Acesso rápido às funcionalidades mais utilizadas

#### Itens do Menu (por perfil)

**Administrador (ADMIN):**

- Dashboard
- Agenda
- Pacientes
- Dentistas
- Prontuários
- Relatórios
- Configurações
- Usuários

**Dentista (DENTIST):**

- Dashboard
- Minha Agenda
- Meus Pacientes
- Prontuários
- Consultas

**Recepção (STAFF):**

- Dashboard
- Agenda
- Pacientes
- Consultas
- Lembretes

#### Área de Conteúdo Principal

- Cabeçalho com breadcrumbs para navegação contextual
- Área de notificações e alertas
- Perfil do usuário com opções de configuração e logout
- Conteúdo específico da funcionalidade selecionada

### 2.2. Agenda Pública (Acesso Não-Autenticado)

A área pública para agendamento de consultas apresenta:

#### Header Institucional

- **Comportamento:** Header fixo com identificação "Cara Core Dental - Agendamento"
- **Características:**
  - Logo da clínica
  - Menu simplificado (Home, Serviços, Contato, Login)
  - Design clean e profissional

#### Formulário de Agendamento

- Interface simplificada e intuitiva
- Passos sequenciais para o agendamento
- Campos obrigatórios claramente identificados
- Confirmação visual do agendamento bem-sucedido

## 3. Princípios de Design

### 3.1. Consistência Visual

- Paleta de cores institucional (azuis, branco e detalhes em verde)
- Tipografia consistente (Open Sans para texto, Montserrat para títulos)
- Iconografia padronizada em todo o sistema
- Componentes de interface reutilizáveis

### 3.2. Responsividade

O sistema é completamente responsivo, adaptando-se a diferentes dispositivos:

- **Desktop:** Layout completo com menu lateral expansível
- **Tablet:** Menu lateral retrátil e reorganização de componentes
- **Mobile:** Menu hambúrguer e layout simplificado para fácil navegação

### 3.3. Acessibilidade

- Conformidade com WCAG 2.1 nível AA
- Alto contraste para melhor legibilidade
- Suporte para navegação por teclado
- Textos alternativos para elementos não-textuais

## 4. Fluxos de Navegação

### 4.1. Fluxo de Login

1. Usuário acessa a página inicial
2. Clica em "Login" ou tenta acessar área restrita
3. Insere credenciais na tela de login
4. Sistema valida e redireciona para dashboard apropriado ao perfil
5. Menu lateral é apresentado com as funcionalidades disponíveis

### 4.2. Fluxo de Agenda

**Para usuários autenticados:**

1. Acesso via menu lateral → Agenda
2. Visualização da agenda (diária, semanal, mensal)
3. Criação/edição de agendamentos
4. Confirmação de consultas

**Para agendamento público:**

1. Acesso à página pública de agendamento
2. Seleção de serviço e profissional
3. Seleção de data/hora disponível
4. Preenchimento de dados de contato
5. Confirmação do agendamento

### 4.3. Fluxo de Prontuário

1. Acesso via menu lateral → Prontuários
2. Busca do paciente
3. Seleção do prontuário
4. Visualização/edição de informações clínicas
5. Upload/visualização de imagens radiológicas

## 5. Validação e Feedback

- Mensagens claras de sucesso/erro
- Indicadores de carregamento para operações longas
- Confirmações para ações irreversíveis
- Tooltips para ajuda contextual

## 6. Implementação Técnica

- **Frontend:** Thymeleaf + Bootstrap 5.3.0
- **Componentes JS:** jQuery + plugins específicos
- **Interatividade:** JavaScript ES6+
- **Estilização:** CSS/SASS com design responsivo

## 7. Testes e Validação

A interface do usuário é validada através de:

- Testes de usabilidade com usuários reais
- Validação de responsividade em múltiplos dispositivos
- Checklist de acessibilidade
- Revisão de performance e carregamento

---

## Referências e Documentos Relacionados

- [TELAS_E_DESIGN.md](tech/TELAS_E_DESIGN.md) - Especificações detalhadas de cada tela
- [CHECKLIST_FRONT_END.md](tech/CHECKLIST_FRONT_END.md) - Validação de componentes de interface

---

**Nota:** Esta documentação é um guia vivo e será atualizada conforme evoluções na interface do sistema.
