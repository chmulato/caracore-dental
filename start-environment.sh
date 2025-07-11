#!/bin/bash

# ====================================================================
# Cara Core Agendamento (CCA) - Script de Inicialização Unix/Linux
# Sistema de agendamento para consultórios odontológicos
# ====================================================================

# Configurações
APP_NAME="Cara Core Agendamento (CCA)"
JAVA_MIN_VERSION="17"
MAVEN_MIN_VERSION="3.8"
POSTGRES_DEFAULT_PORT="5432"
APP_PORT="8080"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;36m'
NC='\033[0m' # No Color

# Variáveis de controle
PROFILE="local"
SKIP_TESTS=false
CLEAN_BUILD=false
SHOW_HELP=false

# Funções de utilidade
print_color() {
    printf "${2}${1}${NC}\n"
}

show_help() {
    print_color "============================================" "$BLUE"
    print_color " $APP_NAME - Script de Inicialização" "$BLUE"
    print_color "============================================" "$BLUE"
    echo ""
    print_color "USO:" "$YELLOW"
    echo "  ./start-environment.sh [OPÇÕES]"
    echo ""
    print_color "OPÇÕES:" "$YELLOW"
    echo "  -p, --profile <perfil>   Perfil do Spring (local, test, prod) [padrão: local]"
    echo "  -s, --skip-tests        Pular execução dos testes"
    echo "  -c, --clean-build       Executar limpeza completa antes do build"
    echo "  -h, --help              Exibir esta ajuda"
    echo ""
    print_color "EXEMPLOS:" "$YELLOW"
    echo "  ./start-environment.sh                     # Executar com perfil local"
    echo "  ./start-environment.sh -p test            # Executar com perfil de teste"
    echo "  ./start-environment.sh --clean-build      # Executar com limpeza completa"
    echo "  ./start-environment.sh --skip-tests       # Executar sem testes"
    echo ""
    print_color "PERFIS DISPONÍVEIS:" "$YELLOW"
    echo "  local  - PostgreSQL local (requer banco configurado)"
    echo "  test   - H2 in-memory (desenvolvimento rápido)"
    echo ""
}

check_java() {
    if command -v java >/dev/null 2>&1; then
        java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
        major_version=$(echo "$java_version" | cut -d'.' -f1)
        
        if [ "$major_version" -ge "$JAVA_MIN_VERSION" ]; then
            print_color "✓ Java $java_version encontrado" "$GREEN"
            return 0
        else
            print_color "✗ Java $JAVA_MIN_VERSION+ requerido. Encontrado: $java_version" "$RED"
            return 1
        fi
    else
        print_color "✗ Java não encontrado. Instale Java $JAVA_MIN_VERSION+" "$RED"
        return 1
    fi
}

check_maven() {
    if command -v mvn >/dev/null 2>&1; then
        maven_version=$(mvn --version 2>/dev/null | head -n 1 | cut -d' ' -f3)
        print_color "✓ Maven $maven_version encontrado" "$GREEN"
        return 0
    else
        print_color "✗ Maven não encontrado. Instale Maven $MAVEN_MIN_VERSION+" "$RED"
        return 1
    fi
}

check_postgresql() {
    if [ "$PROFILE" = "local" ]; then
        if command -v nc >/dev/null 2>&1; then
            if nc -z localhost "$POSTGRES_DEFAULT_PORT" 2>/dev/null; then
                print_color "✓ PostgreSQL detectado na porta $POSTGRES_DEFAULT_PORT" "$GREEN"
                return 0
            else
                print_color "⚠ PostgreSQL não detectado. Usando H2 como fallback." "$YELLOW"
                return 1
            fi
        elif command -v telnet >/dev/null 2>&1; then
            if timeout 3 telnet localhost "$POSTGRES_DEFAULT_PORT" 2>/dev/null | grep -q "Connected"; then
                print_color "✓ PostgreSQL detectado na porta $POSTGRES_DEFAULT_PORT" "$GREEN"
                return 0
            else
                print_color "⚠ PostgreSQL não detectado. Usando H2 como fallback." "$YELLOW"
                return 1
            fi
        else
            print_color "⚠ Não foi possível verificar PostgreSQL (nc/telnet não encontrado). Usando H2 como fallback." "$YELLOW"
            return 1
        fi
    fi
    return 0
}

show_system_info() {
    print_color "============================================" "$BLUE"
    print_color " $APP_NAME" "$BLUE"
    print_color " Sistema de Inicialização" "$BLUE"
    print_color "============================================" "$BLUE"
    echo ""
    print_color "Verificando pré-requisitos..." "$YELLOW"
    echo ""
}

show_startup_info() {
    echo ""
    print_color "============================================" "$GREEN"
    print_color " SISTEMA INICIADO COM SUCESSO!" "$GREEN"
    print_color "============================================" "$GREEN"
    echo ""
    print_color "🌐 Aplicação:" "$BLUE"
    echo "   http://localhost:$APP_PORT"
    echo ""
    print_color "👤 Usuários de Teste:" "$BLUE"
    echo "   Admin:         suporte@caracore.com.br  / admin123"
    echo "   Dentista:      dentista@caracore.com.br / admin123"
    echo "   Recepcionista: recepcao@caracore.com.br / admin123"
    echo "   Paciente:      paciente@caracore.com.br / admin123"
    echo ""
    if [ "$PROFILE" = "test" ] || ! check_postgresql >/dev/null 2>&1; then
        print_color "🗄️ Console H2:" "$BLUE"
        echo "   http://localhost:$APP_PORT/h2-console"
        echo "   JDBC URL: jdbc:h2:mem:testdb"
        echo "   User: sa / Password: (vazio)"
    fi
    echo ""
    print_color "Para parar a aplicação, pressione Ctrl+C" "$YELLOW"
    echo ""
}

# Parsing de argumentos
while [[ $# -gt 0 ]]; do
    case $1 in
        -p|--profile)
            PROFILE="$2"
            shift 2
            ;;
        -s|--skip-tests)
            SKIP_TESTS=true
            shift
            ;;
        -c|--clean-build)
            CLEAN_BUILD=true
            shift
            ;;
        -h|--help)
            SHOW_HELP=true
            shift
            ;;
        *)
            echo "Opção desconhecida: $1"
            show_help
            exit 1
            ;;
    esac
done

# Função principal
main() {
    if [ "$SHOW_HELP" = true ]; then
        show_help
        exit 0
    fi

    show_system_info

    # Verificar pré-requisitos
    java_ok=true
    maven_ok=true
    postgres_ok=true

    check_java || java_ok=false
    check_maven || maven_ok=false
    check_postgresql || postgres_ok=false

    if [ "$java_ok" = false ] || [ "$maven_ok" = false ]; then
        echo ""
        print_color "❌ Pré-requisitos não atendidos. Instale as dependências necessárias." "$RED"
        exit 1
    fi

    # Ajustar perfil se PostgreSQL não estiver disponível
    if [ "$PROFILE" = "local" ] && [ "$postgres_ok" = false ]; then
        print_color "Alterando para perfil 'test' (H2) devido à indisponibilidade do PostgreSQL." "$YELLOW"
        PROFILE="test"
    fi

    echo ""
    print_color "Configuração:" "$BLUE"
    echo "  Perfil: $PROFILE"
    echo "  Pular Testes: $SKIP_TESTS"
    echo "  Limpeza: $CLEAN_BUILD"
    echo ""

    # Executar limpeza se solicitado
    if [ "$CLEAN_BUILD" = true ]; then
        print_color "Executando limpeza..." "$YELLOW"
        mvn clean
        if [ $? -ne 0 ]; then
            print_color "❌ Falha na limpeza" "$RED"
            exit 1
        fi
    fi

    # Executar testes se não foi solicitado para pular
    if [ "$SKIP_TESTS" = false ]; then
        print_color "Executando testes..." "$YELLOW"
        mvn test
        if [ $? -ne 0 ]; then
            print_color "❌ Falha nos testes" "$RED"
            exit 1
        fi
    fi

    # Iniciar aplicação
    print_color "Iniciando aplicação..." "$YELLOW"
    show_startup_info
    
    if [ "$PROFILE" = "local" ]; then
        mvn spring-boot:run -Dspring.profiles.active=local
    else
        mvn spring-boot:run -Dspring.profiles.active="$PROFILE"
    fi
}

# Verificar se o script está sendo executado do diretório correto
if [ ! -f "pom.xml" ]; then
    print_color "❌ Erro: pom.xml não encontrado. Execute este script do diretório raiz do projeto." "$RED"
    exit 1
fi

# Executar função principal
main "$@"
