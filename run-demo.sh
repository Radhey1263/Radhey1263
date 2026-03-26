#!/bin/bash

# Run script for Data Structures Demo

if [ ! -d "bin" ]; then
    echo "Error: Compiled classes not found. Please run ./compile.sh first."
    exit 1
fi

echo "Running Data Structures Demo..."
java -cp bin com.simpledb.examples.DataStructuresDemo
