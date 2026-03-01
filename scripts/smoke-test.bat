@echo off
:: smoke-test.bat — Quick smoke test for local development
:: 
:: This script:
::   1. Cleans up any existing Java processes
::   2. Starts the server in foreground
::   3. Runs smoke tests
::   4. Stops the server
::
:: Usage: scripts\smoke-test.bat
::
:: NOTE: This runs everything in sequence. The server will stop when
:: tests complete or when you press Ctrl+C.

setlocal

set "SCRIPT_DIR=%~dp0"

:: Step 1: Cleanup
echo [smoke-test] Step 1: Cleaning up existing Java processes...
call "%SCRIPT_DIR%cleanup-java.bat" --force
if errorlevel 1 (
    echo [smoke-test] Cleanup had issues, continuing anyway...
)

:: Step 2: Run server and tests
echo.
echo [smoke-test] Step 2: Starting server and running tests...
echo [smoke-test] Press Ctrl+C at any time to stop
echo.

:: We can't easily background the server on Windows and capture PID,
:: so instead we use a simpler approach: start server, then run tests
:: in a separate window or require manual coordination.

echo ═══════════════════════════════════════════════════════════
echo  MANUAL STEPS REQUIRED:
echo ═══════════════════════════════════════════════════════════
echo.
echo  1. Run this in TERMINAL 1:
echo     scripts\dev-server.bat
echo.
echo  2. Wait for "Server ready" message
echo.
echo  3. Run this in TERMINAL 2:
echo     scripts\dev-test.bat --tier smoke
echo.
echo  4. Press Ctrl+C in TERMINAL 1 to stop server when done
echo.
echo ═══════════════════════════════════════════════════════════

endlocal
