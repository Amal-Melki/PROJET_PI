@echo off
echo Vérification de l'environnement...
where java > nul 2>&1
if %errorlevel% neq 0 (
   echo Java non installé ou non dans le PATH
   exit /b 1
)

java -version 2>&1 | find "17" > nul
if %errorlevel% neq 0 (
   echo Version Java incompatible (17+ requise)
   exit /b 1
)

if not exist "C:\javafx-sdk-17.0.2\lib" (
   echo JavaFX SDK introuvable
   exit /b 1
)

echo Environnement validé avec succès
pause
