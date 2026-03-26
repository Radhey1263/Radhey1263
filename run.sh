#!/bin/bash

# Run script for SimpleDB Main class

if [ ! -d "bin" ]; then
    echo "Error: Compiled classes not found. Please run ./compile.sh first."
    exit 1
fi

echo "Running SimpleDB Main..."
java -cp bin com.simpledb.examples.Main
