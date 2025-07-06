# Documentação da API - Swagger/OpenAPI

Este documento descreve como utilizar a documentação da API do Sistema de Agendamento Cara Core através do Swagger/OpenAPI.

## Acesso ao Swagger UI

### Ambiente de Desenvolvimento

- **URL**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

### Ambiente de Homologação

- **URL**: https://homolog-api.caracore.com.br/swagger-ui.html
- **API Docs**: https://homolog-api.caracore.com.br/api-docs

### Ambiente de Produção

- **URL**: https://api.caracore.com.br/swagger-ui.html
- **API Docs**: https://api.caracore.com.br/api-docs

## Funcionalidades Documentadas

### 1. Agendamento Público

Endpoints públicos que não requerem autenticação:

#### **GET** `/public/api/dentistas`

- **Descrição**: Lista dentistas disponíveis para agendamento público
- **Resposta**: Array de strings com nomes dos dentistas

- **Exemplo**:

```json
[
  "Dr. João Silva - Clínico Geral",
  "Dra. Maria Santos - Ortodontista",
  "Dra. Ana Costa - Implantodontista"
]
```

#### **GET** `/public/api/horarios-disponiveis`

- **Descrição**: Obter horários disponíveis para um dentista em uma data específica

- **Parâmetros**:
  - `dentista` (string, obrigatório): Nome do dentista
  - `data` (string, obrigatório): Data no formato YYYY-MM-DD
- **Exemplo**: `/public/api/horarios-disponiveis?dentista=Dr. João Silva - Clínico Geral&data=2025-07-10`

- **Resposta**:

```json
["09:00", "10:00", "11:00", "14:00", "15:00"]
```

#### **POST** `/public/agendar`

- **Descrição**: Criar um novo agendamento público
- **Content-Type**: `application/x-www-form-urlencoded`
- **Parâmetros**:
  - `paciente` (string, obrigatório): Nome do paciente
  - `dentista` (string, obrigatório): Nome do dentista
  - `dataHora` (string, obrigatório): Data e hora no formato ISO (YYYY-MM-DDTHH:mm)
  - `telefone` (string, opcional): Telefone para contato
- **Resposta de Sucesso**: "Agendamento realizado com sucesso!"
- **Resposta de Erro**: Mensagem de erro específica

#### **GET** `/public/consultar-agendamento`

- **Descrição**: Consultar agendamentos por nome do paciente
- **Parâmetros**:
  - `paciente` (string, obrigatório): Nome do paciente
- **Resposta**: Array de objetos Agendamento

#### **GET** `/api/public/verificar-disponibilidade`

- **Descrição**: Verificar se um horário específico está disponível
- **Parâmetros**:
  - `dentista` (string, obrigatório): Nome do dentista
  - `dataHora` (string, obrigatório): Data e hora no formato ISO

- **Resposta**:

```json
{
  "disponivel": true
}
```

## Como Usar o Swagger UI

### 1. Navegação

- Acesse o Swagger UI através da URL correspondente ao seu ambiente
- Os endpoints estão organizados por tags (categorias)
- Clique em uma tag para expandir e ver os endpoints

### 2. Testando Endpoints

1. **Selecione um endpoint** clicando nele
2. **Clique em "Try it out"** para habilitar o teste
3. **Preencha os parâmetros** necessários
4. **Clique em "Execute"** para fazer a requisição
5. **Veja a resposta** na seção de resultados

### 3. Exemplos de Teste

#### Listar Dentistas Disponíveis

1. Acesse `GET /public/api/dentistas`
2. Clique em "Try it out"
3. Clique em "Execute"
4. Veja a lista de dentistas na resposta

#### Agendar uma Consulta

1. Acesse `POST /public/agendar`
2. Clique em "Try it out"
3. Preencha os campos:
   - `paciente`: "João Silva"
   - `dentista`: "Dr. João Silva - Clínico Geral"
   - `dataHora`: "2025-07-10T10:00"
   - `telefone`: "11999999999"
4. Clique em "Execute"
5. Veja o resultado do agendamento

## Modelos de Dados

### Agendamento

```json
{
  "id": 1,
  "paciente": "João Silva",
  "dentista": "Dr. João Silva - Clínico Geral",
  "dataHora": "2025-07-10T10:00:00",
  "observacao": "Consulta de rotina",
  "status": "AGENDADO",
  "duracaoMinutos": 30,
  "telefoneWhatsapp": "11999999999",
  "dataCriacao": "2025-07-05T21:30:00"
}
```

### Status Possíveis

- `AGENDADO`: Consulta agendada (status inicial)
- `CONFIRMADO`: Paciente confirmou presença
- `REAGENDADO`: Consulta foi reagendada
- `REALIZADO`: Consulta foi realizada
- `CANCELADO`: Consulta foi cancelada
- `NAO_COMPARECEU`: Paciente não compareceu

## Códigos de Resposta HTTP

### Sucessos

- **200 OK**: Requisição processada com sucesso
- **201 Created**: Recurso criado com sucesso

### Erros do Cliente

- **400 Bad Request**: Dados inválidos ou campos obrigatórios ausentes
- **404 Not Found**: Recurso não encontrado

### Erros do Servidor

- **500 Internal Server Error**: Erro interno do servidor

## Configuração

### Habilitando/Desabilitando o Swagger

No arquivo `application.yml` de cada ambiente:

```yaml
springdoc:
  swagger-ui:
    enabled: true  # false para desabilitar
```

### Personalizando a Documentação

As configurações estão em `SwaggerConfig.java`:
- Informações da API (título, versão, descrição)
- Servidores (URLs para diferentes ambientes)
- Tags (categorias de endpoints)

## Segurança

### Acesso Público

O Swagger UI está configurado para ser acessível publicamente. Para restringir o acesso:

1. Remova as regras de permissão em `SecurityConfig.java`:

```java
.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
```

2. Adicione autenticação específica se necessário.

## Troubleshooting

### Swagger UI não carrega

1. Verifique se a aplicação está rodando
2. Confirme se o perfil está correto
3. Verifique os logs para erros de inicialização

### Endpoints não aparecem

1. Verifique se os controllers estão anotados corretamente
2. Confirme se as dependências do SpringDoc estão no classpath
3. Verifique se há conflitos de mapeamento de URLs

### Testes não funcionam

1. Verifique se os parâmetros estão corretos
2. Confirme se o backend está respondendo
3. Verifique os logs da aplicação para erros

## Integração com Ferramentas Externas

### Postman

1. Acesse a URL dos API Docs: `/api-docs`
2. Copie o JSON retornado
3. Importe no Postman como uma coleção OpenAPI

### Insomnia

1. Crie uma nova workspace
2. Importe a URL dos API Docs
3. Os endpoints serão carregados automaticamente

## Manutenção

### Atualizando a Documentação

1. Adicione/modifique as anotações nos controllers
2. Atualize a configuração em `SwaggerConfig.java` se necessário
3. Reinicie a aplicação
4. Verifique se as mudanças aparecem no Swagger UI

### Versionamento

A versão da API é definida em `SwaggerConfig.java` e deve ser atualizada a cada release significativo.
