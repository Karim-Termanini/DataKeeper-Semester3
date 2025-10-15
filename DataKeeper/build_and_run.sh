#!/bin/bash

echo "🔨 Compiling game..."
rm -rf out
mkdir out

# Compile Java files
javac -d out -sourcepath src $(find src -name "*.java")

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "✅ Compilation successful!"

# Copy resources to out folder
echo "📦 Copying resources..."
cp -r res/* out/

echo "🎮 Starting DATA KEEPER..."
cd out
java main.Main

