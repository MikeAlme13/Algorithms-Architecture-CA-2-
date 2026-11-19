@echo off
REM ============================================================
REM  Grafton & Hughes Department Store - Staff Management System
REM  CCT College Dublin - Algorithms & Architecture - CA2
REM  Compiles the project and starts the application.
REM ============================================================
cd /d "%~dp0"

if not exist out mkdir out

echo Compiling the Java sources...
javac -encoding UTF-8 -d out src\Main.java src\model\*.java src\view\*.java src\controller\*.java
if errorlevel 1 (
    echo.
    echo COMPILATION FAILED. Check the errors above.
    pause
    exit /b 1
)

echo Compilation successful.
echo.
java -cp out Main

echo.
pause
