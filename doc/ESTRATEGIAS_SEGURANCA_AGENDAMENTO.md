# Estratégias de Segurança para Endpoints de Agendamento Público

## Visão Geral

Este documento apresenta múltiplas estratégias para implementar endpoints de agendamento público seguros, abordando diferentes níveis de segurança e complexidade.

## Estratégias Implementadas

### 1. **Rate Limiting Avançado**

- **Implementação**: `RateLimitService.java`
- **Proteção**: Limita requisições por IP por minuto/hora/dia
- **Configuração**:
  - 5 requisições por minuto
  - 20 requisições por hora
  - 50 requisições por dia
- **Vantagem**: Previne ataques DDoS e uso abusivo
- **Desvantagem**: Pode limitar usuários legítimos em redes compartilhadas

### 2. **Validação e Sanitização Rigorosa**

- **Implementação**: `AgendamentoPublicoMelhoradoController.java`
- **Validações**:
  - Nomes: apenas letras e espaços (2-100 caracteres)
  - Telefones: apenas números (10-11 dígitos)
  - Emails: formato RFC válido
  - Data/Hora: formato ISO, não no passado, horário comercial
- **Sanitização**: Remove caracteres especiais e normaliza entrada
- **Vantagem**: Previne injeção de código e dados maliciosos
- **Desvantagem**: Pode ser restritivo demais para casos legítimos

### 3. **CAPTCHA Integration**

- **Implementação**: `CaptchaService.java`
- **Proteção**: Google reCAPTCHA v2/v3
- **Validação**: Token verificado com Google
- **Vantagem**: Previne bots automatizados
- **Desvantagem**: Adiciona fricção ao usuário

### 4. **Autenticação em Duas Etapas (2FA)**

- **Fluxo**:
  1. Usuário preenche formulário
  2. Sistema envia código SMS/Email
  3. Usuário confirma com código
  4. Agendamento é criado
- **Implementação**: `AgendamentoPublicoSecuroController.java`
- **Vantagem**: Alta segurança, confirma identidade
- **Desvantagem**: Complexidade adicional, dependência de SMS/Email

### 5. **Auditoria e Logging**

- **Rastreamento**: IP, User-Agent, timestamp
- **Logs**: Todas as tentativas (sucesso/falha)
- **Alertas**: Padrões suspeitos automáticos
- **Vantagem**: Permite investigação posterior
- **Desvantagem**: Pode gerar volume alto de dados

### 6. **Segurança do Método HTTP**

#### **Por que POST é o método correto para agendamento:**

- **Confidencialidade**: Dados não aparecem na URL (logs, cache, histórico)
- **Idempotência**: Não é idempotente, cada POST pode criar novo agendamento
- **Semântica REST**: POST para criar recursos, GET apenas para consultar
- **Tamanho**: Pode enviar mais dados que GET (sem limite de URL)

#### **Validações de Content-Type implementadas:**

```java
@PostMapping(value = "/public/agendar", 
             consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
```

- **Aceita apenas**: `application/x-www-form-urlencoded` ou `multipart/form-data`
- **Rejeita**: `application/json`, `text/plain`, outros tipos
- **Interceptor**: `ContentTypeValidationInterceptor` valida antes do processamento

#### **Comparação de métodos HTTP:**

| Método | Segurança | Idempotência | Uso Correto |
|--------|-----------|--------------|-------------|
| GET    | ❌ Baixa  | ✅ Sim       | Apenas consultas |
| POST   | ✅ Alta   | ❌ Não       | ✅ Criar agendamentos |
| PUT    | ✅ Alta   | ✅ Sim       | Atualizar agendamentos |
| DELETE | ✅ Alta   | ✅ Sim       | Cancelar agendamentos |

#### **Implementação HTTPS obrigatório:**

```java
@Component
public class HttpsEnforcementInterceptor implements HandlerInterceptor {
    // Força redirecionamento para HTTPS em produção
    // Configurável via: app.security.enforce-https=true
}
```

## Comparação das Estratégias

```markdown
|---------------|------------|--------------|---------|------------|
| Estratégia    | Segurança  | Complexidade | UX      | Manutenção |
|---------------|------------|--------------|---------|------------|
| Rate Limiting | * * *      | * *          | * * * * | * * *      |
| Validação     | * * * *    | * *          | * * * * | * * * *    |
| CAPTCHA       | * * * *    | * * *        | * * *   | * * *      |
| 2FA           | * * * * *  | * * * *      | * *     | * *        |
| Auditoria     | * *        | * *          | * * * * | * *        |
|---------------|------------|--------------|---------|------------|
```

## Recomendações por Cenário

### **Pequena Clínica** (Recomendado: Validação + Rate Limiting)

```java
@PostMapping("/public/agendar")
public ResponseEntity<?> agendar(@Valid @RequestBody AgendamentoRequest request) {
    // Validação básica + Rate limiting simples
    if (!rateLimitService.isAllowed(getClientIp())) {
        return ResponseEntity.status(429).body("Muitas tentativas");
    }
    
    // Validação de dados
    if (!isValidRequest(request)) {
        return ResponseEntity.badRequest().body("Dados inválidos");
    }
    
    // Criar agendamento
    Agendamento agendamento = agendamentoService.salvar(request.toEntity());
    return ResponseEntity.ok(agendamento);
}
```

### **Clínica Média** (Recomendado: Validação + Rate Limiting + CAPTCHA)
```java
@PostMapping("/public/agendar")
public ResponseEntity<?> agendar(@RequestBody AgendamentoRequest request) {
    // Rate limiting
    if (!rateLimitService.isAllowed(getClientIp())) {
        return ResponseEntity.status(429).body("Muitas tentativas");
    }
    
    // CAPTCHA
    if (!captchaService.validate(request.getCaptchaToken())) {
        return ResponseEntity.badRequest().body("CAPTCHA inválido");
    }
    
    // Validação rigorosa
    ValidationResult validation = validateRequest(request);
    if (!validation.isValid()) {
        return ResponseEntity.badRequest().body(validation.getMessage());
    }
    
    // Criar agendamento
    Agendamento agendamento = agendamentoService.salvar(request.toEntity());
    return ResponseEntity.ok(agendamento);
}
```

### **Grande Clínica/Rede** (Recomendado: Estratégia Completa)

```java
@PostMapping("/public/solicitar-agendamento")
public ResponseEntity<?> solicitar(@RequestBody PreAgendamentoRequest request) {
    // Todas as validações
    if (!isValidAndSecure(request)) {
        return ResponseEntity.badRequest().body("Dados inválidos");
    }
    
    // Gerar token de sessão
    String token = generateSessionToken();
    storeTemporarySession(token, request);
    
    // Enviar código de confirmação
    sendConfirmationCode(request.getTelefone(), token);
    
    return ResponseEntity.ok(Map.of("sessionToken", token));
}

@PostMapping("/public/confirmar-agendamento")
public ResponseEntity<?> confirmar(@RequestBody ConfirmacaoRequest request) {
    // Validar código
    if (!validateConfirmationCode(request)) {
        return ResponseEntity.badRequest().body("Código inválido");
    }
    
    // Criar agendamento
    Agendamento agendamento = createFromSession(request.getSessionToken());
    return ResponseEntity.ok(agendamento);
}
```

## Configuração de Segurança

### **application.yml**

```yaml
# Rate Limiting
rate-limit:
  enabled: true
  requests-per-minute: 5
  requests-per-hour: 20
  requests-per-day: 50

# CAPTCHA
google:
  recaptcha:
    site-key: your-site-key
    secret: your-secret-key
    threshold: 0.5

# SMS Service
sms:
  provider: twilio
  account-sid: your-account-sid
  auth-token: your-auth-token
  from-number: +1234567890

# Security
security:
  public-endpoints:
    - /api/public/**
    - /public/**
  audit:
    enabled: true
    log-level: INFO
```

### **SecurityConfig.java**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/public/**")
            )
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true)
                )
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
```

## Testes de Segurança

### **Teste de Rate Limiting**

```java
@Test
void testRateLimitingPreventsAbuse() {
    String ip = "192.168.1.100";
    
    // Fazer 5 requisições (limite)
    for (int i = 0; i < 5; i++) {
        assertTrue(rateLimitService.isAllowed(ip));
    }
    
    // 6ª requisição deve ser bloqueada
    assertFalse(rateLimitService.isAllowed(ip));
}
```

### **Teste de Validação**

```java
@Test
void testValidationPreventsInjection() {
    AgendamentoRequest request = new AgendamentoRequest();
    request.setPaciente("<script>alert('xss')</script>");
    
    ValidationResult result = validationService.validate(request);
    
    assertFalse(result.isValid());
    assertEquals("Nome do paciente inválido", result.getMessage());
}
```

### **Teste de CAPTCHA**

```java
@Test
void testCaptchaValidation() {
    // Token inválido
    assertFalse(captchaService.validate("invalid-token", "127.0.0.1"));
    
    // Token válido (mock)
    when(restTemplate.getForObject(any(), eq(RecaptchaResponse.class)))
        .thenReturn(new RecaptchaResponse(true));
    
    assertTrue(captchaService.validate("valid-token", "127.0.0.1"));
}
```

## Monitoramento e Alertas

### **Métricas Importantes**

- Taxa de requisições por IP
- Tentativas de agendamento falhadas
- Tempo de resposta dos endpoints
- Uso de recursos (CPU, memória)

### **Alertas Automáticos**

- Mais de 100 tentativas por IP em 1 hora
- Taxa de falhas > 50%
- Tempo de resposta > 5 segundos
- Padrões suspeitos (IPs sequenciais, etc.)

## Conclusão

A escolha da estratégia de segurança deve considerar:

- **Tamanho da clínica**: Pequena = simples, grande = complexa
- **Volume de agendamentos**: Alto volume = mais proteção
- **Recursos técnicos**: Equipe disponível para manutenção
- **Experiência do usuário**: Balancear segurança com usabilidade

O **controller melhorado** (`AgendamentoPublicoMelhoradoController`) oferece um bom equilíbrio entre segurança e usabilidade, sendo adequado para a maioria dos casos de uso.
