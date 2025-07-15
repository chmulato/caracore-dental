# Fluxo de Funcionamento do reCAPTCHA

Este documento apresenta os diagramas de fluxo para entendimento da implementação do reCAPTCHA no sistema de agendamento online.

## Diagrama de Sequência

```mermaid
sequenceDiagram
    participant Cliente as Navegador do Cliente
    participant Frontend as Frontend App
    participant Backend as Backend API
    participant Google as Google reCAPTCHA API

    Cliente->>Frontend: Acessa página de agendamento
    Frontend->>Backend: Solicita config do reCAPTCHA
    Backend->>Frontend: Retorna config (enabled, siteKey)
    
    alt reCAPTCHA habilitado
        Frontend->>Google: Carrega script do reCAPTCHA
        Google->>Frontend: Retorna widget do reCAPTCHA
        Frontend->>Cliente: Exibe formulário com reCAPTCHA
        Cliente->>Google: Resolve o desafio
        Google->>Cliente: Gera token de validação
    else reCAPTCHA desabilitado
        Frontend->>Cliente: Exibe formulário sem reCAPTCHA
    end
    
    Cliente->>Frontend: Submete formulário
    
    alt reCAPTCHA habilitado
        Frontend->>Backend: Envia dados + token do reCAPTCHA
        Backend->>Google: Verifica token (secretKey + token)
        Google->>Backend: Resultado da verificação
        
        alt Token válido
            Backend->>Backend: Processa agendamento
            Backend->>Frontend: Retorna sucesso
        else Token inválido
            Backend->>Frontend: Retorna erro de validação
        end
    else reCAPTCHA desabilitado
        Frontend->>Backend: Envia apenas dados do formulário
        Backend->>Backend: Processa agendamento
        Backend->>Frontend: Retorna resultado
    end
    
    Frontend->>Cliente: Exibe resultado ao usuário
```

## Diagrama de Estados do reCAPTCHA

```mermaid
stateDiagram-v2
    [*] --> Desativado: Ambiente de Dev/Test
    [*] --> Ativado: Ambiente de Produção
    
    Ativado --> CarregandoScript: Página carregada
    CarregandoScript --> WidgetExibido: Script carregado
    WidgetExibido --> NaoResolvido: Inicial
    NaoResolvido --> Resolvido: Usuário resolve
    Resolvido --> NaoResolvido: Token expira
    
    Resolvido --> ValidandoNoBackend: Formulário enviado
    ValidandoNoBackend --> TokenValido: Verificação OK
    ValidandoNoBackend --> TokenInvalido: Verificação falhou
    
    TokenValido --> ProcessandoAgendamento
    TokenInvalido --> ErroExibido
    
    Desativado --> ProcessandoAgendamento: Formulário enviado
```

## Configuração e Chaves

```mermaid
graph TD
    A[Ambiente] -->|Desenvolvimento| B[recaptcha.enabled=false]
    A -->|Teste| B
    A -->|Produção| C[recaptcha.enabled=true]
    
    B -->|Opcional| D[Chaves de teste]
    C -->|Obrigatório| E[Chaves de produção]
    
    E -->|Geração| F[Google reCAPTCHA Admin]
    F -->|Site Key| G[Frontend]
    F -->|Secret Key| H[Backend]
```
