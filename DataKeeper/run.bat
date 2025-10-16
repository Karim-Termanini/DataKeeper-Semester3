@echo off
setlocal
cd /d "%~dp0"
echo Starting DATA KEEPER...

set JAVA_BIN=
where java >nul 2>nul && set JAVA_BIN=java
if not defined JAVA_BIN if exist dist\jre\bin\java.exe set JAVA_BIN=dist\jre\bin\java.exe
if not defined JAVA_BIN (
	echo Error: Java Runtime (java) not found. Please install Java 8+ (JRE) and try again.
	pause
	exit /b 1
)

if exist dist\DATA_KEEPER.jar (
	"%JAVA_BIN%" -jar dist\DATA_KEEPER.jar
) else if exist out (
	"%JAVA_BIN%" -cp out;res main.Main
) else (
	echo No runnable build found.
	echo   1) Developer run: build_and_run.bat
	echo   2) Release JAR:   package.bat (or package.sh), then run again
)

endlocal

