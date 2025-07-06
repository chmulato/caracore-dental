# Scripts de Inicialização do Ambiente - CCA

## Scripts Criados

### Scripts na Raiz do Projeto (Conveniência)

1. **start-environment.bat** - Script batch para Windows (duplo clique)
2. **start-environment.ps1** - Script PowerShell para Windows (mais robusto)
3. **start-environment.sh** - Script bash para Linux/macOS

### Scripts Detalhados na Pasta `scripts/`

1. **scripts/start-docker.bat** - Script batch completo para Windows
2. **scripts/start-docker.ps1** - Script PowerShell completo para Windows
3. **scripts/start-docker.sh** - Script bash completo para Linux/macOS

## Como Usar

### Windows

#### Opção 1: Duplo clique (mais fácil)

```text
Duplo clique no arquivo start-environment.bat
```

#### Opção 2: Linha de comando

```cmd
# CMD
start-environment.bat

# PowerShell
.\start-environment.ps1
```

### Linux/macOS

```bash
# Tornar executável (se necessário)
chmod +x start-environment.sh

# Executar
./start-environment.sh
```

## O que os Scripts Fazem

1. **Verificam** se o Docker está rodando
2. **Verificam** se o docker-compose está disponível
3. **Navegam** para o diretório raiz do projeto
4. **Executam** `docker-compose up -d` para iniciar o PostgreSQL
5. **Exibem** informações sobre os serviços iniciados

## Após Executar os Scripts

```bash
# Para iniciar a aplicação com PostgreSQL (Linux/macOS)
mvn spring-boot:run -Dspring.profiles.active=local

# Para iniciar a aplicação com PostgreSQL (Windows PowerShell)
mvn spring-boot:run "-Dspring.profiles.active=local"

# Ou de forma mais simples (qualquer sistema)
mvn spring-boot:run
# (e depois configure o perfil local no application.yml)

# Para verificar containers
docker-compose ps

# Para parar containers
docker-compose down

# Para ver logs
docker-compose logs -f
```

## Serviços Iniciados

- **PostgreSQL**: localhost:5432
- **Database**: cca_db
- **User**: cca_user
- **Password**: cca_password

## Benefícios

- **Automatização**: Setup do ambiente com um clique
- **Verificação**: Detecta problemas antes da execução
- **Multiplataforma**: Funciona em Windows, Linux e macOS
- **Informativo**: Mostra status e próximos passos
- **Confiável**: Trata erros e fornece feedback claro
