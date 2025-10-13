@echo off
echo Compiling game...
if exist out rmdir /s /q out
mkdir out

javac -d out -sourcepath src src\main\*.java src\entities\*.java src\gameplay\*.java src\inputs\*.java src\levels\*.java src\ui\*.java src\utils\*.java src\audio\*.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!

echo Copying resources...
xcopy /E /I /Y res out

echo Starting game...
cd out
java main.Main
pause

