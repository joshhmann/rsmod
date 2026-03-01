@echo off
:: dev-server.bat — FOREGROUND development server runner
:: 
:: This script runs the RSMod server in the CURRENT terminal window.
:: When you close the terminal or press Ctrl+C, the server stops cleanly.
::
:: Usage: scripts\dev-server.bat

setlocal

set "SCRIPT_DIR=%~dp0"
for %%A in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fA"
set "RSMOD_ROOT=%PROJECT_ROOT%"
if exist "%PROJECT_ROOT%\rsmod\gradlew.bat" set "RSMOD_ROOT=%PROJECT_ROOT%\rsmod"

:: Check for existing Java processes
echo [dev-server] Checking for existing Java processes...
tasklist /FI "IMAGENAME eq java.exe" /FO CSV 2>nul | find "java.exe" >nul
if %errorlevel% == 0 (
    echo [dev-server] WARNING: Existing Java processes detected:
    tasklist /FI "IMAGENAME eq java.exe" /FO TABLE | findstr "java"
    echo.
    echo [dev-server] You may want to run: scripts\cleanup-java.bat
    echo.
    choice /C YN /M "Continue anyway"
    if errorlevel 2 exit /b 1
)

:: Install if needed
echo [dev-server] Checking installation...
call "%SCRIPT_DIR%install-server.bat"
if errorlevel 1 exit /b 1

:: Show config
echo.
echo ═══════════════════════════════════════════════════════════
echo    RSMod v2 Development Server (FOREGROUND MODE)
echo ═══════════════════════════════════════════════════════════
echo.
echo  Game Server:     http://localhost:43594
echo  AgentBridge:     ws://localhost:43595
echo  Log File:        %TEMP%\rsmod-dev.log
echo.
echo  Press Ctrl+C to stop the server
echo ═══════════════════════════════════════════════════════════
echo.

:: Run server in foreground (no 'start', no background)
cd /d "%RSMOD_ROOT%"
echo [dev-server] Starting server at %date% %time%
echo [dev-server] Log file: %TEMP%\rsmod-dev.log
echo.

:: Create log file header
echo # RSMod Development Server Log > "%TEMP%\rsmod-dev.log"
echo # Started: %date% %time% >> "%TEMP%\rsmod-dev.log"
echo # ---------------------------------------- >> "%TEMP%\rsmod-dev.log"
echo. >> "%TEMP%\rsmod-dev.log"

:: Run gradle (output goes to console, we can't easily tee on Windows)
call gradlew.bat run --console=plain

:: This line only reached if server exits normally
echo.
echo [dev-server] Server stopped at %date% %time%
echo [dev-server] Note: Full logs are in server\app\build\run\logs\

endlocal
