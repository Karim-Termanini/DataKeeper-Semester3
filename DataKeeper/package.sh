#!/bin/bash
set -e
cd "$(dirname "$0")"
echo "Packaging DATA KEEPER..."
chmod +x compile.sh
# Ensure a clean class output (avoid old resources lingering in out/)
rm -rf out
./compile.sh
mkdir -p dist
MANIFEST_FILE="dist/manifest.mf"
cat > "$MANIFEST_FILE" <<EOF
Manifest-Version: 1.0
Main-Class: main.Main

EOF
# Create runnable jar with classes and resources (resources at classpath root)
jar cfm dist/DATA_KEEPER.jar "$MANIFEST_FILE" -C out . -C res .
echo "Built dist/DATA_KEEPER.jar"
echo "Run with: java -jar dist/DATA_KEEPER.jar"

# Create one-click launchers in dist
LINUX_LAUNCHER="dist/Run-DataKeeper.sh"
cat > "$LINUX_LAUNCHER" <<'EOSH'
#!/bin/bash
set -e
cd "$(dirname "$0")"
JAVA_BIN="java"
if command -v java >/dev/null 2>&1; then
	JAVA_BIN="java"
elif [ -x "jre/bin/java" ]; then
	JAVA_BIN="jre/bin/java"
else
	echo "Error: Java Runtime (java) not found. Please install Java 8+ (JRE)." >&2
	exit 1
fi
exec "$JAVA_BIN" -jar "$(pwd)/DATA_KEEPER.jar"
EOSH
chmod +x "$LINUX_LAUNCHER"

WIN_LAUNCHER="dist/Run-DataKeeper.bat"
cat > "$WIN_LAUNCHER" <<'EOBAT'
@echo off
cd /d "%~dp0"
set JAVA_BIN=
where java >nul 2>nul && set JAVA_BIN=java
if not defined JAVA_BIN if exist jre\bin\java.exe set JAVA_BIN=jre\bin\java.exe
if not defined JAVA_BIN (
	echo Error: Java Runtime not found. Please install Java 8+ (JRE).
	pause
	exit /b 1
)
"%JAVA_BIN%" -jar DATA_KEEPER.jar
EOBAT

MAC_LAUNCHER="dist/DataKeeper.command"
cat > "$MAC_LAUNCHER" <<'EOCMD'
#!/bin/bash
set -e
cd "$(dirname "$0")"
JAVA_BIN="java"
if command -v java >/dev/null 2>&1; then
	JAVA_BIN="java"
elif [ -x "jre/bin/java" ]; then
	JAVA_BIN="jre/bin/java"
else
	osascript -e 'display alert "Java nicht gefunden" message "Bitte Java 8+ (JRE) installieren."'
	exit 1
fi
exec "$JAVA_BIN" -jar "$(pwd)/DATA_KEEPER.jar"
EOCMD
chmod +x "$MAC_LAUNCHER"

# Optional: Windows VBS launcher (no console window)
WIN_VBS="dist/Run-DataKeeper.vbs"
cat > "$WIN_VBS" <<'EOVBS'
Set WshShell = CreateObject("Wscript.Shell")
WshShell.Run "cmd /c java -jar DATA_KEEPER.jar", 0, False
EOVBS

# Include README_RELEASE in dist for end users
cp -f README_RELEASE.md dist/ 2>/dev/null || true

# Linux .desktop launcher (optional convenience)
DESKTOP_FILE="dist/DataKeeper.desktop"
cat > "$DESKTOP_FILE" <<'EODESK'
[Desktop Entry]
Type=Application
Name=DATA KEEPER
Comment=Start DATA KEEPER
Exec=sh -c 'cd "$(dirname %k)" && ./Run-DataKeeper.sh'
Path=
Terminal=false
Categories=Game;
EODESK

# Optional: create a zipped release bundle if zip is available
if command -v zip >/dev/null 2>&1; then
	(cd dist && zip -q -r DATA_KEEPER_release.zip DATA_KEEPER.jar Run-DataKeeper.sh Run-DataKeeper.bat DataKeeper.command Run-DataKeeper.vbs README_RELEASE.md)
	echo "Wrote dist/DATA_KEEPER_release.zip"
fi

# Create a Linux-friendly tar.gz (preserves executable bits)
if command -v tar >/dev/null 2>&1; then
	(cd dist && tar -czf DATA_KEEPER_linux.tar.gz DATA_KEEPER.jar Run-DataKeeper.sh DataKeeper.command README_RELEASE.md)
	echo "Wrote dist/DATA_KEEPER_linux.tar.gz"
fi