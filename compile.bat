@echo off
REM Compile script for SimpleDB (Windows)

echo Compiling SimpleDB...

REM Create bin directory for compiled classes
if not exist bin mkdir bin

REM Compile all Java files
dir /s /B src\main\java\*.java > sources.txt
javac -d bin @sources.txt

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Compiled classes are in the 'bin' directory.
    del sources.txt
) else (
    echo Compilation failed!
    del sources.txt
    exit /b 1
)
