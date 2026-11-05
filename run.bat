@echo off
cd /d "%~dp0"
if not exist out mkdir out
echo Compiling Campus Lost ^& Found...
javac -encoding UTF-8 -cp "lib\mysql-connector-j-26.10.0.jar" -d out src\*.java
if errorlevel 1 (
    echo.
    echo Compilation failed. Check the errors above.
    pause
    exit /b 1
)
echo.
echo Starting Campus Lost ^& Found...
java -cp "out;lib\mysql-connector-j-26.10.0.jar" LoginFrame
if errorlevel 1 pause
