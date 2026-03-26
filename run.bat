@echo off
REM Run script for SimpleDB Main class (Windows)

if not exist bin (
    echo Error: Compiled classes not found. Please run compile.bat first.
    exit /b 1
)

echo Running SimpleDB Main...
java -cp bin com.simpledb.examples.Main
