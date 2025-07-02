@echo off
echo Gerador de Hash BCrypt - Sistema CCA
echo =======================================

call mvn compile

echo.
echo Digite a senha para gerar o hash (ou deixe em branco para usar "admin123"):
set /p senha=

if "%senha%"=="" set senha=admin123

:: Usar uma abordagem simplificada para o classpath com Maven
call mvn exec:java -Dexec.mainClass="com.caracore.cca.util.BCryptGen" -Dexec.args="%senha%"

echo.
pause

echo.
pause
