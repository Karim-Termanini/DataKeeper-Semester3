@echo off
setlocal
cd /d "%~dp0"

echo Packaging DATA KEEPER (Windows)...
if not exist out (
  echo Compiling sources...
  javac -d out -sourcepath src @sources.list 2>nul
  if %errorlevel% neq 0 (
    rem Fall back to simple find if sources.list missing
    for /r src %%f in (*.java) do @echo %%f >> sources.list
    javac -d out -sourcepath src @sources.list
  )
)
if errorlevel 1 (
  echo Compilation failed.
  exit /b 1
)
if not exist dist mkdir dist
>dist\manifest.mf echo Manifest-Version: 1.0
>>dist\manifest.mf echo Main-Class: main.Main
>>dist\manifest.mf echo.

rem Create runnable JAR with classes and resources
jar cfm dist\DATA_KEEPER.jar dist\manifest.mf -C out . -C res .
echo Built dist\DATA_KEEPER.jar
echo Run with: java -jar dist\DATA_KEEPER.jar

rem Create one-click launchers in dist
>dist\Run-DataKeeper.bat echo @echo off
>>dist\Run-DataKeeper.bat echo cd /d "%%~dp0"
>>dist\Run-DataKeeper.bat echo where java ^>nul 2^>nul
>>dist\Run-DataKeeper.bat echo if errorlevel 1 ^(
>>dist\Run-DataKeeper.bat echo   echo Error: Java Runtime ^(java^) not found. Please install Java 8+ ^(JRE^).
>>dist\Run-DataKeeper.bat echo   pause
>>dist\Run-DataKeeper.bat echo   exit /b 1
>>dist\Run-DataKeeper.bat echo ^)
>>dist\Run-DataKeeper.bat echo java -jar DATA_KEEPER.jar

>dist\Run-DataKeeper.vbs echo Set WshShell = CreateObject("Wscript.Shell")
>>dist\Run-DataKeeper.vbs echo WshShell.Run "cmd /c java -jar DATA_KEEPER.jar", 0, False

copy /y README_RELEASE.md dist\ >nul 2>nul

where zip >nul 2>nul
if %errorlevel%==0 (
  pushd dist
  zip -q -r DATA_KEEPER_release.zip DATA_KEEPER.jar Run-DataKeeper.bat Run-DataKeeper.vbs README_RELEASE.md
  popd
)
endlocal
