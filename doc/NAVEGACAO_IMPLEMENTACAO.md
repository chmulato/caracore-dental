# Guia de Implementação: Sistema de Navegação Dual

Este guia explica como implementar corretamente o sistema de navegação dual do Cara Core Dental, que consiste em:

- **Menu Lateral (Sidebar)** para usuários autenticados
- **Header** para agenda pública de acesso externo

## Arquivos Principais

### Componentes de Layout

- `fragments/main-layout.html` - Template base contendo ambos os layouts
- `fragments/sidebar.html` - Menu lateral para usuários autenticados
- `fragments/header.html` - Cabeçalho para agenda pública

### Recursos de Estilo e Comportamento

- `css/layout/sidebar.css` - Estilos do menu lateral
- `css/layout/header.css` - Estilos do cabeçalho
- `js/layout/sidebar.js` - Comportamento do menu lateral
- `js/layout/header.js` - Comportamento do cabeçalho

### Exemplos

- `templates/dashboard-example.html` - Página de exemplo para interface autenticada
- `templates/public-example.html` - Página de exemplo para interface pública

## Implementação em Novas Páginas

### 1. Para Páginas Autenticadas (com Menu Lateral)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security" lang="pt-BR">
<head th:replace="~{fragments/main-layout :: head('Título da Página')}"></head>
<body>
    <div th:replace="~{fragments/main-layout :: authenticated-layout('Título da Página', 'activeLink', ~{::section})}">
        <section>
            <!-- Conteúdo específico da página -->
        </section>
    </div>
    
    <!-- Scripts específicos da página, se houver -->
</body>
</html>
```

Onde:

- `'Título da Página'` - Título que aparecerá na barra do navegador e no cabeçalho da página
- `'activeLink'` - ID do link que deve ser destacado no menu lateral (ex: 'home', 'agenda', 'pacientes')
- `~{::section}` - Conteúdo específico da página encapsulado em uma tag section

### 2. Para Páginas Públicas (com Header)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="pt-BR">
<head th:replace="~{fragments/main-layout :: head('Título da Página')}"></head>
<body>
    <div th:replace="~{fragments/main-layout :: public-layout('Título da Página', ~{::main})}">
        <main>
            <!-- Conteúdo específico da página -->
        </main>
    </div>
    
    <!-- Scripts específicos da página, se houver -->
</body>
</html>
```

Onde:

- `'Título da Página'` - Título que aparecerá na barra do navegador
- `~{::main}` - Conteúdo específico da página encapsulado em uma tag main

## Configuração do Controller

Para que o menu lateral destaque o item correto, é necessário passar o atributo `activeLink` para o modelo:

```java
@GetMapping("/pacientes")
public String listarPacientes(Model model) {
    model.addAttribute("activeLink", "pacientes");
    // Outras operações
    return "pacientes/lista";
}
```

## Valores para activeLink

Os valores válidos para o parâmetro `activeLink` correspondem aos identificadores no sidebar.html:

- `home` - Dashboard/Home
- `agenda` - Menu de Agenda
- `pacientes` - Gestão de Pacientes
- `dentistas` - Gestão de Dentistas
- `prontuarios` - Sistema de Prontuários
- `relatorios` - Relatórios e Estatísticas
- `financeiro` - Gestão Financeira
- `config` - Configurações do Sistema
- `admin` - Administração (para administradores)

## Comportamento Responsivo

- O menu lateral se adapta automaticamente a diferentes tamanhos de tela
- Em dispositivos móveis, o menu lateral é ocultado e pode ser acessado via botão toggle
- O header público também é totalmente responsivo, com menu hamburger em telas pequenas

## Segurança e Controle de Acesso

O menu lateral usa as diretivas `sec:authorize` do Spring Security para controle de acesso:

```html
<!-- Item de menu visível apenas para administradores -->
<li class="nav-item" sec:authorize="hasRole('ROLE_ADMIN')">
    <a class="nav-link sidebar-link" th:classappend="${activeLink == 'admin'} ? 'active' : ''" href="/admin">
        <i class="bi bi-gear me-2"></i>
        <span class="sidebar-text">Administração</span>
    </a>
</li>
```

## Personalização

Para personalizar os menus:

1. **Menu Lateral**: Editar o arquivo `fragments/sidebar.html`
2. **Header Público**: Editar o arquivo `fragments/header.html`
3. **Estilos**: Modificar os arquivos CSS em `css/layout/`

## Boas Práticas

1. Sempre encapsule o conteúdo da página em uma tag semântica (section, main, article)
2. Use o layout apropriado para cada tipo de acesso (autenticado vs. público)
3. Mantenha a consistência no uso dos ícones e estilos
4. Respeite a estrutura de navegação conforme o tipo de usuário
5. Sempre passe o `activeLink` correto para o menu lateral
