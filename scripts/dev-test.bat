@echo off
:: dev-test.bat — Run tests against a running server
::
:: This script assumes the server is already running (via dev-server.bat).
:: It runs tests in foreground and exits cleanly.
::
:: Usage: scripts\dev-test.bat [--tier smoke|nightly]

@echo off
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
for %%A in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fA"
set "TIER=smoke"

:: Parse arguments
:parse_args
if "%~1"=="" goto :args_done
if /i "%~1"=="--tier" (
    set "TIER=%~2"
    shift & shift
    goto :parse_args
)
echo Unknown option: %~1
exit /b 1
:args_done

echo.
echo ═══════════════════════════════════════════════════════════
echo    RSMod v2 Test Runner (FOREGROUND MODE)
echo ═══════════════════════════════════════════════════════════
echo.
echo  Test Tier:       %TIER%
echo  Project Root:    %PROJECT_ROOT%
echo.

:: Check if server is running
echo [dev-test] Checking if server is running...
tasklist /FI "IMAGENAME eq java.exe" /FO CSV 2>nul | find "java.exe" >nul
if errorlevel 1 (
    echo [dev-test] ERROR: No Java process found!
    echo [dev-test] Please start the server first:
    echo [dev-test]   scripts\dev-server.bat
    exit /b 1
)

:: Check if AgentBridge is responding
echo [dev-test] Checking AgentBridge on port 43595...
python -c "
import socket, sys
try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(3)
    s.connect(('localhost', 43595))
    s.close()
    sys.exit(0)
except:
    sys.exit(1)
"
if errorlevel 1 (
    echo [dev-test] WARNING: AgentBridge not responding on port 43595
    echo [dev-test] Server may still be starting up...
    timeout /t 5 /nobreak >nul
)

:: Run tests
echo.
echo [dev-test] Running %TIER% tests...
echo ---------------------------------------------------------

cd /d "%PROJECT_ROOT%agent-runner"

:: Try to get git commit hash
set "COMMIT=dev"
for /f "tokens=*" %%H in ('git -C "%PROJECT_ROOT%" rev-parse --short HEAD 2^>nul') do set "COMMIT=%%H"

python run.py --tier "%TIER%" --commit "%COMMIT%"
set TEST_EXIT=%errorlevel%

echo.
echo ---------------------------------------------------------
if "%TEST_EXIT%"=="0" (
    echo [dev-test] ✅ All tests passed
) else (
    echo [dev-test] ❌ Tests failed
    echo [dev-test] Check: test-reports\%TIER%-latest.md
)

echo.
echo [dev-test] Done at %date% %time%

endlocal
exit /b %TEST_EXIT%
