#!/bin/bash
set -e
cd "$(dirname "$0")"
echo "Packaging DATA KEEPER..."
chmod +x compile.sh
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