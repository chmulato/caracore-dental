# Configuração do reCAPTCHA

Este documento descreve como o reCAPTCHA está implementado no sistema de agendamento online e como configurá-lo em diferentes ambientes.

## Visão Geral

O sistema utiliza o Google reCAPTCHA v2 para proteger o formulário de agendamento contra bots e submissões automatizadas. A implementação é flexível, permitindo habilitar/desabilitar o captcha conforme o ambiente.

Para uma visão visual do funcionamento e dos estados do reCAPTCHA, consulte o [diagrama de fluxo do reCAPTCHA](diagrams/FLUXO_RECAPCHA.md).

## Implementação Atual

O reCAPTCHA está implementado no arquivo `agendamento-online-captcha.html` e funciona da seguinte forma:

1. O frontend carrega a configuração do reCAPTCHA do backend via API (`/public/api/recaptcha-config`)
2. Se habilitado, o script do reCAPTCHA é carregado dinamicamente
3. O token do reCAPTCHA é anexado ao formulário durante o envio
4. O backend valida o token antes de processar o agendamento

## Configuração por Ambiente

### Desenvolvimento e Testes

Em ambientes de desenvolvimento e teste, o reCAPTCHA geralmente é desabilitado para facilitar o desenvolvimento e testes automatizados.

1. Configure no arquivo `application-dev.properties` ou `application-test.properties`:

```properties
recaptcha.enabled=false
recaptcha.site-key=6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI
recaptcha.secret-key=6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe
```

> **Nota**: As chaves acima são as chaves de teste oficiais do Google para desenvolvimento e nunca validam (sempre retornam sucesso).

### Produção

Para ativar o reCAPTCHA em produção:

1. Obtenha chaves do Google reCAPTCHA:
   - Acesse [https://www.google.com/recaptcha/admin](https://www.google.com/recaptcha/admin)
   - Faça login com sua conta Google
   - Registre um novo site
   - Selecione reCAPTCHA v2 "Eu não sou um robô"
   - Adicione os domínios da aplicação (ex: caracoredental.com.br)
   - Anote as chaves geradas (site key e secret key)

2. Configure no arquivo `application-prod.properties`:

```properties
recaptcha.enabled=true
recaptcha.site-key=SUA_SITE_KEY_AQUI
recaptcha.secret-key=SUA_SECRET_KEY_AQUI
```

3. Reinicie a aplicação para aplicar as mudanças

## Verificação da Implementação

O sistema verifica automaticamente a configuração do reCAPTCHA em tempo de execução:

- Se `recaptcha.enabled=true`, o reCAPTCHA será exibido e validado no formulário
- Se `recaptcha.enabled=false` ou a configuração falhar, o reCAPTCHA será omitido do formulário
- O sistema exibe o status atual do reCAPTCHA na parte inferior do formulário

## Fluxo do Backend

1. Endpoint REST `/public/api/recaptcha-config` retorna:
   ```json
   {
     "enabled": true|false,
     "siteKey": "chave-do-site"
   }
   ```

2. Durante o processamento do agendamento, se habilitado, o backend valida o token do reCAPTCHA com a API do Google antes de prosseguir

## Troubleshooting

### Problemas Comuns

1. **O reCAPTCHA não aparece no formulário**
   - Verifique se `recaptcha.enabled=true` nas configurações
   - Confirme se a site-key está correta
   - Verifique o console do navegador por erros

2. **Erro de validação do token**
   - Confirme se a secret-key está correta
   - Verifique se o domínio está autorizado no console do reCAPTCHA
   - Verifique os logs do servidor para mensagens específicas de erro

3. **Problemas em ambiente de desenvolvimento**
   - Certifique-se de usar `recaptcha.enabled=false` ou as chaves de teste
   - Para testes locais com reCAPTCHA habilitado, use o domínio `localhost` no console do reCAPTCHA

## Logs e Monitoramento

O sistema registra os resultados das validações de reCAPTCHA nos logs da aplicação:

- Tentativas de submissão sem captcha quando habilitado
- Falhas de validação do token
- Rate limiting (máximo 5 tentativas por minuto por IP)

## Considerações de Segurança

- Nunca exponha a secret-key no frontend
- Sempre valide o token no backend
- Mantenha as chaves de produção seguras e não as compartilhe
- Considere rotacionar as chaves periodicamente

## Referências
