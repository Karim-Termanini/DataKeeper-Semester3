#!/bin/bash
set -e
cd "$(dirname "$0")"

echo "Starting DATA KEEPER..."

# Prefer packaged JAR if available (end-user friendly; no javac needed)
if [ -f "dist/DATA_KEEPER.jar" ]; then
	if command -v java >/dev/null 2>&1; then JAVA_BIN="java"; elif [ -x "dist/jre/bin/java" ]; then JAVA_BIN="dist/jre/bin/java"; else echo "Error: Java Runtime (java) not found. Please install Java 8+ (JRE)." >&2; exit 1; fi
	exec "$JAVA_BIN" -jar dist/DATA_KEEPER.jar
fi

# Fallback for developers: run from compiled classes in out/ with res/ on classpath
if [ -d out ]; then
	if command -v java >/dev/null 2>&1; then JAVA_BIN="java"; elif [ -x "jre/bin/java" ]; then JAVA_BIN="jre/bin/java"; else echo "Error: Java Runtime (java) not found. Please install Java 8+ (JRE)." >&2; exit 1; fi
	exec "$JAVA_BIN" -cp out:res main.Main
fi

echo "No runnable build found. Options:"
echo "  1) Developer run: ./build_and_run.sh"
echo "  2) Release JAR:   ./package.sh (then rerun this script)"
exit 1

