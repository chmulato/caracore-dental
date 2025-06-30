# Script para verificar se o hash corresponde à senha "admin123"
Set-Location -Path c:\dev\cara-core_cca
$env:MAVEN_OPTS="-Xmx512m"
& mvn compile exec:java "-Dexec.mainClass=com.caracore.cca.util.VerificarHash" "-Dexec.args=admin123 `$2a`$10`$ktLieeeNJAD9iA5l8VsR6..erCGtsqwWFm57vspe.wsxCT9FDTiXy"
