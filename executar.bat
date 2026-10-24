@echo off
rem Compila (se necessario) e abre o Sistema ERP. Requer apenas Java 17+ instalado.
cd /d "%~dp0"

if not exist config.local.properties (
  copy config.local.properties.example config.local.properties >nul
  echo Criei o arquivo config.local.properties.
  echo Abra-o, troque SUA_SENHA pela senha do banco e rode este arquivo de novo.
  pause
  exit /b 1
)

if not exist target\sistema-erp-hibernate.jar (
  echo Compilando o projeto ^(so na primeira vez^)...
  call mvnw.cmd -q clean package -DskipTests
  if errorlevel 1 (
    pause
    exit /b 1
  )
)

java -jar target\sistema-erp-hibernate.jar
if errorlevel 1 pause
