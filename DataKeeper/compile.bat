@echo off
cd /d "%~dp0"
echo Compiling Platformer Expanded...
if not exist out mkdir out
dir /s /B src\*.java > sources.txt
javac -d out -sourcepath src @sources.txt
del sources.txt
if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Run the game with: run.bat
) else (
    echo Compilation failed!
    pause
    exit /b 1
)
pause

