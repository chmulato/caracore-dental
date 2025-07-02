#!/bin/bash

# Script para testar hashes BCrypt no Linux
# Uso: ./verificar-bcrypt.sh senha hash

if [ $# -lt 2 ]; then
  echo "Uso: $0 <senha> <hash>"
  echo "Exemplo: $0 admin123 '\$2a\$10\$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy'"
  exit 1
fi

SENHA="$1"
HASH="$2"

# Método 1: Usando Python com bcrypt
echo "== Verificando com Python bcrypt =="
python3 -c "
import bcrypt
senha = b'$SENHA'
hash = b'$HASH'
try:
    result = bcrypt.checkpw(senha, hash)
    print(f'Senha: {senha.decode()}')
    print(f'Hash: {hash.decode()}')
    print(f'Corresponde: {result}')
except Exception as e:
    print(f'Erro: {e}')
"

# Método 2: Usando htpasswd/Apache tools
echo -e "\n== Verificando com htpasswd =="
echo "Esta verificação criará um arquivo temporário"
TEMP_FILE=$(mktemp)
echo "user:$HASH" > $TEMP_FILE
echo "$SENHA" | htpasswd -v -B $TEMP_FILE user 2>/dev/null
RESULT=$?
if [ $RESULT -eq 0 ]; then
    echo "Corresponde: true"
else
    echo "Corresponde: false"
fi
rm $TEMP_FILE

# Explicação dos prefixos
echo -e "\n== Explicação dos prefixos de hash BCrypt =="
echo '$2a$ - Versão padrão atual do BCrypt'
echo '$2b$ - Correção para tratar caracteres não-ASCII (OpenBSD)'
echo '$2y$ - Formato "crypt_blowfish" que corrige um bug na versão antiga'
echo '$2x$ - Versão obsoleta (BCrypt incorreto/bugado)'

# Explicar o formato do hash
echo -e "\n== Análise do hash fornecido =="
PREFIX=$(echo $HASH | cut -d'$' -f1-3)
COST=$(echo $HASH | cut -d'$' -f3)
SALT_HASH=$(echo $HASH | cut -d'$' -f4)
SALT=${SALT_HASH:0:22}
HASH_PART=${SALT_HASH:22}
echo "Prefixo: $PREFIX"
echo "Custo/iterações: $COST (2^$COST rounds)"
echo "Salt: $SALT"
echo "Hash: $HASH_PART"
