#!/bin/bash

# Compile script for SimpleDB

echo "Compiling SimpleDB..."

# Create bin directory for compiled classes
mkdir -p bin

# Compile all Java files
find src/main/java -name "*.java" > sources.txt
javac -d bin @sources.txt

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Compiled classes are in the 'bin' directory."
    rm sources.txt
else
    echo "Compilation failed!"
    rm sources.txt
    exit 1
fi
