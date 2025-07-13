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
  - Visível por padrão em desktop (≥992px) após o login
  - Recolhível através de botão toggle (estado preservado entre sessões)
  - Oculto por padrão em dispositivos móveis (<992px), acessível via botão de menu
  - Organizado por módulos (Agenda, Pacientes, Prontuários, etc.)
  - Personalizado conforme o perfil do usuário (ADMIN, DENTIST, RECEPTIONIST)
  - Indicador visual da página atual através de classe 'active'
  - Suporte a submenus expansíveis para agrupamento de funcionalidades relacionadas

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

- Cabeçalho fixo (fixed-header) com título da página atual
- Perfil do usuário com dropdown para opções de configuração e logout
- Conteúdo específico da funcionalidade selecionada
- Ajusta-se automaticamente conforme estado da sidebar (expandida/recolhida)
- Possui padding superior para acomodar o cabeçalho fixo
- Possui padding inferior para acomodar o rodapé fixo

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

- **Desktop (≥992px):**  
  - Menu lateral visível por padrão após login
  - Estado da sidebar (expandida/recolhida) preservado no localStorage
  - Conteúdo principal com margem à esquerda quando sidebar está visível
  - Header e footer fixos ajustam-se ao estado da sidebar

- **Tablet/Mobile (<992px):**  
  - Menu lateral oculto por padrão, acessível via botão hamburguer
  - Sidebar aparece como overlay sobre o conteúdo quando aberta
  - Fecha automaticamente após seleção de opção do menu
  - Barra superior móvel com acesso ao perfil do usuário

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

### 6.1. Tecnologias Utilizadas

- **Frontend:** Thymeleaf + Bootstrap 5.3.0
- **Componentes JS:** JavaScript ES6+ (sem dependência de jQuery)
- **Icones:** Bootstrap Icons 1.13.1
- **Estilização:** CSS modular com organização por componentes

### 6.2. Estrutura de Arquivos

- **Templates Principais:**
  - `fragments/main-layout.html`: Layout base com estrutura para páginas autenticadas e públicas
  - `fragments/sidebar.html`: Implementação do menu lateral
  - `fragments/fixed-header.html`: Implementação do cabeçalho fixo
  - `fragments/fixed-footer.html`: Implementação do rodapé fixo

- **Estilos CSS:**
  - `css/main.css`: Estilos globais e variáveis da aplicação
  - `css/sidebar.css`: Estilos específicos para o menu lateral
  - `css/fixed-header.css`: Estilos para o cabeçalho fixo
  - `css/fixed-footer.css`: Estilos para o rodapé fixo
  - `css/[funcionalidade].css`: Estilos específicos para cada funcionalidade

- **Scripts JavaScript:**
  - `js/layout/sidebar.js`: Comportamento do menu lateral
  - `js/layout/fixed-header.js`: Comportamento do cabeçalho fixo
  - `js/layout/fixed-footer.js`: Comportamento do rodapé fixo
  - `js/[funcionalidade].js`: Scripts específicos para cada funcionalidade

### 6.3. Comportamento da Sidebar

O menu lateral (sidebar) possui um comportamento específico implementado via CSS e JavaScript:

#### Visibilidade Inicial

- Em desktop (≥992px):  
  - Visível por padrão após o login
  - CSS: `.sidebar { transform: translateX(0); }` aplicado via media query
  - O conteúdo principal tem margem à esquerda para acomodar a sidebar

- Em mobile (<992px):  
  - Oculto por padrão
  - CSS: `.sidebar { transform: translateX(-100%); }`
  - O conteúdo principal não tem margem adicional

#### Persistência de Estado

O JavaScript (`sidebar.js`) gerencia o estado da sidebar:

```javascript
// Em desktop, a sidebar começa aberta por padrão
if (window.innerWidth > 991) {
    // Se houver uma preferência salva no localStorage, use-a
    const sidebarState = localStorage.getItem('sidebarOpen');
    // Se não houver preferência ou a preferência for 'true', abra a sidebar
    if (sidebarState === null || sidebarState === 'true') {
        body.classList.add('sidebar-open');
    }
}
```

Quando o usuário altera o estado da sidebar (expandir/recolher):

1. A classe `sidebar-open` é adicionada/removida do elemento `body`
2. O novo estado é salvo no localStorage
3. Media queries no CSS ajustam automaticamente o layout

#### Interação com Header e Footer Fixos

O cabeçalho e rodapé fixos também respondem ao estado da sidebar:

```css
/* Em desktop, o header deve ser ajustado por padrão */
@media (min-width: 992px) {
    .header-fixed {
        left: 250px;
    }
    
    /* Quando a sidebar está explicitamente fechada em desktop */
    body:not(.sidebar-open) .header-fixed {
        left: 0;
    }
}
```

Este comportamento garante que todos os elementos da interface se ajustem corretamente ao estado da sidebar, tanto em desktop quanto em dispositivos móveis.

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

## Guia de Integração com o Layout

Para integrar novas páginas ao sistema de layout autenticado, utilize o seguinte template Thymeleaf:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security" lang="pt-BR">
<head th:replace="~{fragments/main-layout :: head('Título da Página')}">
    <!-- Este conteúdo será substituído pelo fragmento head -->
</head>
<body>
    <!-- Usando layout autenticado com sidebar -->
    <div th:replace="~{fragments/main-layout :: authenticated-layout('Título da Página', 'identificador-menu', ~{::section})}">
        <section>
            <!-- Conteúdo específico da página aqui -->
        </section>
    </div>
</body>
</html>
```

Onde:

- `'Título da Página'` será exibido no cabeçalho e título da janela
- `'identificador-menu'` deve corresponder ao valor usado na sidebar para destacar o item de menu ativo
- `~{::section}` encapsula o conteúdo específico da página

---

**Nota:** Esta documentação é um guia vivo e será atualizada conforme evoluções na interface do sistema. Última atualização em 13/07/2025.
