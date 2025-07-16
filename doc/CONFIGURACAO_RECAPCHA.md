# Configuração do reCAPTCHA

Este documento descreve como o reCAPTCHA está implementado no sistema de agendamento online e como configurá-lo em diferentes ambientes.

## Visão Geral

O sistema utiliza o Google reCAPTCHA v2 para proteger o formulário de agendamento contra bots e submissões automatizadas. A implementação é flexível, permitindo habilitar/desabilitar o captcha conforme o ambiente.

Para uma visão visual do funcionamento e dos estados do reCAPTCHA, consulte o [diagrama de fluxo do reCAPTCHA](diagrams/FLUXO_RECAPCHA.md).

## Implementação Atual

O reCAPTCHA está implementado na página única de agendamento como parte do sistema simplificado de accordion. O reCAPTCHA é apresentado na seção de confirmação, garantindo a validação humana antes da efetivação do agendamento. Funciona da seguinte forma:

1. Na seção de confirmação do accordion, o reCAPTCHA é exibido junto com o resumo do agendamento
2. O script do reCAPTCHA é carregado automaticamente na página
3. O usuário deve resolver o reCAPTCHA junto com aceitar os termos
4. O botão de confirmação só é habilitado quando ambas validações são realizadas
5. O token do reCAPTCHA é enviado junto com os dados do formulário
6. O backend valida o token antes de processar o agendamento definitivo

## Configuração por Ambiente

### Desenvolvimento e Testes

Em ambientes de desenvolvimento e teste, o reCAPTCHA geralmente é desabilitado para facilitar o desenvolvimento e testes automatizados.

Configure no arquivo `application.yml` ou via variáveis de ambiente:

```yaml
# Para desenvolvimento e testes
google:
  recaptcha:
    site-key: 6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI
    secret-key: 6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe
```

Ou via variáveis de ambiente:
```bash
RECAPTCHA_SITE_KEY=6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI
RECAPTCHA_SECRET_KEY=6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe
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

2. Configure no arquivo `application.yml`:

```yaml
google:
  recaptcha:
    site-key: SUA_SITE_KEY_AQUI
    secret-key: SUA_SECRET_KEY_AQUI
```

Ou preferencialmente via variáveis de ambiente (recomendado para produção):
```bash
RECAPTCHA_SITE_KEY=SUA_SITE_KEY_AQUI
RECAPTCHA_SECRET_KEY=SUA_SECRET_KEY_AQUI
```

3. Reinicie a aplicação para aplicar as mudanças

## Verificação da Implementação

O sistema gerencia a configuração do reCAPTCHA na seção de confirmação do agendamento:

- O reCAPTCHA é exibido na página de confirmação quando habilitado
- O botão de confirmação permanece desabilitado até que:
  1. O usuário aceite os termos de agendamento
  2. Complete a verificação do reCAPTCHA com sucesso
- Se a configuração falhar ou o reCAPTCHA não puder ser carregado, o usuário receberá uma mensagem de erro
- O status de validação é exibido em tempo real para o usuário

## Fluxo do Backend

1. O backend recebe a requisição POST do formulário de confirmação
2. O token do reCAPTCHA é extraído do parâmetro `g-recaptcha-response`
3. O sistema verifica se o reCAPTCHA está habilitado para o ambiente atual
4. Se habilitado, o token é validado contra a API do Google usando a secret-key configurada
5. Apenas após a validação bem-sucedida o agendamento é processado
6. Em caso de falha na validação, o usuário recebe uma mensagem de erro na mesma página

## Troubleshooting

### Problemas Comuns

1. **O reCAPTCHA não aparece na página de confirmação**
   - Verifique se as chaves do reCAPTCHA estão configuradas no `application.yml` ou nas variáveis de ambiente
   - Confirme se a site-key está correta e autorizada para o domínio
   - Verifique o console do navegador por erros de carregamento do script

2. **Erro de validação do token**
   - Confirme se a secret-key está correta nas configurações
   - Verifique se o domínio está autorizado no console do reCAPTCHA
   - Verifique os logs do servidor para mensagens específicas de erro
   - Garanta que o token está sendo enviado corretamente no formulário

3. **Problemas em ambiente de desenvolvimento**
   - Use as chaves de teste fornecidas pelo Google
   - Para testes locais, certifique-se de que o domínio `localhost` está autorizado
   - Você pode usar o modo de desenvolvimento do reCAPTCHA que sempre valida

## Logs e Monitoramento

O sistema registra os eventos relacionados ao reCAPTCHA nos logs da aplicação:

- Tentativas de submissão sem completar o reCAPTCHA
- Falhas de validação do token com o serviço do Google
- Erros de carregamento ou configuração do reCAPTCHA
- Validações bem-sucedidas para auditoria
- Rate limiting (máximo 5 tentativas por minuto por IP)

## Considerações de Segurança

- Nunca exponha a secret-key no frontend ou no código-fonte
- Use variáveis de ambiente para as chaves em produção
- Sempre valide o token no backend antes de processar o agendamento
- Mantenha as chaves de produção seguras e não as compartilhe
- Considere rotacionar as chaves periodicamente
- Monitore tentativas suspeitas de bypass do reCAPTCHA
- Mantenha logs de auditoria das validações

## Referências

1. [Documentação oficial do Google reCAPTCHA v2](https://developers.google.com/recaptcha/docs/display)
2. [Guia de implementação do reCAPTCHA no Spring Boot](https://developers.google.com/recaptcha/docs/verify)
3. [Boas práticas de segurança com reCAPTCHA](https://developers.google.com/recaptcha/docs/security)
4. [Diagrama de fluxo do reCAPTCHA](diagrams/FLUXO_RECAPCHA.md)
