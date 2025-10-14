#!/bin/bash
cd "$(dirname "$0")"
echo "Compiling Platformer Expanded..."
mkdir -p out
javac -d out -sourcepath src $(find src -name "*.java")
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Run the game with: ./run.sh"
else
    echo "Compilation failed!"
    exit 1
fi

