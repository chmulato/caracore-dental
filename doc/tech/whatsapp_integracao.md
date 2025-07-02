# Integração com WhatsApp - Documentação Técnica

**Cara Core Informática - CCA (Cara Core Agendamento)**  
**Data da implementação:** Julho de 2025  
**Desenvolvido por:** Equipe Cara Core

## 📱 Visão Geral da Funcionalidade

A integração com WhatsApp do sistema CCA permite aos profissionais da clínica odontológica manter uma comunicação eficiente e direta com os pacientes, utilizando a plataforma mais popular de mensagens no Brasil. Esta funcionalidade foi desenvolvida para simplificar a comunicação com pacientes e reduzir o índice de faltas em consultas, além de criar um canal direto e eficiente para envio de orientações pré e pós-procedimentos.

## 🔧 Componentes Técnicos

### 1. Formulário de Agendamento

A integração inicia no formulário de agendamento, onde implementamos:

- Campo obrigatório para telefone WhatsApp (`telefoneWhatsapp`)
- Validação com regex para garantir formato adequado `(99) 99999-9999`
- Atualização automática do banco de dados (tabela `paciente`)
- Botão de integração direta com WhatsApp Web

### 2. DTO e Entidades Atualizadas

- **AgendamentoForm.java**: Adicionado campo `telefoneWhatsapp` como obrigatório
- **Paciente.java**: Campo `telefone` agora armazena o número de WhatsApp do paciente

```java
// Novo campo obrigatório com validação de padrão brasileiro
@NotBlank(message = "O telefone WhatsApp é obrigatório")
@Pattern(regexp = "^\\(?[1-9]{2}\\)? ?(?:9[1-9]|[2-9])[0-9]{3}\\-?[0-9]{4}$", 
         message = "Telefone inválido. Use o formato: (99) 99999-9999")
private String telefoneWhatsapp;
```

### 3. Endpoints REST Adicionados

- **/api/buscar-paciente**: Retorna os dados do paciente, incluindo telefone WhatsApp
- **/api/gerar-link-whatsapp**: Converte um número de telefone em link válido para WhatsApp Web

### 4. JavaScript para Integração

- Formatação automática do número enquanto o usuário digita
- Atualização dinâmica do link do WhatsApp
- Autopreenchimento ao selecionar paciente existente

```javascript
// Função para atualizar o link do WhatsApp
function atualizarLinkWhatsApp() {
    const telefoneInput = document.getElementById('telefoneWhatsapp');
    const whatsappLink = document.getElementById('whatsappLink');
    
    if (telefoneInput.value) {
        // Remove caracteres não numéricos
        const numeroLimpo = telefoneInput.value.replace(/\D/g, '');
        
        // Verifica se o número tem pelo menos 10 dígitos (DDD + número)
        if (numeroLimpo.length >= 10) {
            // Formata o número para o WhatsApp (com código do Brasil +55)
            const linkWhatsApp = `https://wa.me/55${numeroLimpo}`;
            whatsappLink.href = linkWhatsApp;
            whatsappLink.classList.remove('disabled');
            whatsappLink.setAttribute('title', 'Enviar mensagem pelo WhatsApp');
        }
    }
}
```

## 🛠️ Implementação no Backend

### 1. Controller (AgendamentoController.java)

O controller foi modificado para:

1. **Atualizar o cadastro do paciente** com o telefone WhatsApp informado durante o agendamento
2. **Criar paciente novo** caso não exista
3. **Prover métodos utilitários** para gerar links do WhatsApp
4. **Expor endpoints REST** para busca de pacientes e geração de links

```java
/**
 * Gera um link para o WhatsApp Web a partir de um número de telefone
 */
public String gerarLinkWhatsApp(String telefone) {
    if (telefone == null || telefone.isEmpty()) {
        return "#";
    }
    
    // Remove caracteres não numéricos
    String numeroLimpo = telefone.replaceAll("\\D", "");
    
    // Verifica se o número tem pelo menos 10 dígitos (DDD + número)
    if (numeroLimpo.length() < 10) {
        return "#";
    }
    
    // Formata o número para o WhatsApp (com código do Brasil +55)
    return "https://wa.me/55" + numeroLimpo;
}
```

### 2. View (novo-agendamento.html)

A view foi atualizada com:

1. **Campo para telefone WhatsApp** (obrigatório)
2. **Botão de integração** com WhatsApp Web
3. **Validação cliente-side** e formatação automática
4. **Busca automática** do telefone ao selecionar paciente

## 📊 Benefícios Esperados

1. **Redução de 30% nas faltas** devido a lembretes eficientes
2. **Melhoria na experiência do paciente** com comunicação simplificada
3. **Banco de dados atualizado** com telefones válidos dos pacientes
4. **Aumento na eficiência da recepção** com comunicação direta pelo navegador
5. **Facilitação do acompanhamento** pré e pós-consulta

## 🔄 Fluxo de Trabalho

1. **Recepcionista** busca/cadastra paciente no sistema
2. **Sistema** exige confirmação do telefone WhatsApp
3. **Recepcionista** confirma/atualiza o telefone WhatsApp
4. **Sistema** salva o agendamento e atualiza o cadastro do paciente
5. **Recepcionista** utiliza o botão WhatsApp para iniciar conversa
6. **WhatsApp Web** abre com o número do paciente pré-preenchido

## ⚠️ Observações e Limitações

- A funcionalidade requer que o WhatsApp Web esteja autenticado no navegador
- A implementação atual não utiliza a API oficial do WhatsApp Business
- A formatação suporta apenas números brasileiros (código +55)
- É necessário que pop-ups estejam habilitados no navegador

## 🔮 Melhorias Futuras

- **Integração com API oficial do WhatsApp Business**
- **Envio automatizado de lembretes** 24h antes da consulta
- **Templates personalizáveis** para mensagens pré-definidas
- **Dashboard de comunicações** realizadas via WhatsApp
- **Estatísticas de efetividade** dos lembretes em relação às faltas
