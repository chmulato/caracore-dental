@echo off
echo Verificador de Hash BCrypt - Sistema CCA
echo =======================================

call mvn compile

echo.
echo Digite a senha para verificar:
set /p senha=

if "%senha%"=="" set senha=admin123

echo.
echo Digite o hash BCrypt para comparar (ou deixe em branco para usar o hash padrão):
set /p hash=

if "%hash%"=="" set hash=$2a$10$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy

:: Usar uma abordagem simplificada para o classpath com Maven
call mvn exec:java -Dexec.mainClass="com.caracore.cca.util.VerificarHash" -Dexec.args="%senha% %hash%"

echo.
pause
