@echo off
:: start-testing.bat — full Sprint 0 testing session orchestrator.
::
:: Workflow:
::   1. Install RSMod if needed
::   2. Configure RSProx proxy-targets.yaml
::   3. Start RSMod server in a new window (background)
::   4. Wait for AgentBridge (port 43595) to respond
::   5. Run agent-runner smoke tests
::   6. Print report and exit with 0 (pass) or 1 (fail)
::
:: Usage:
::   scripts\start-testing.bat [--tier smoke|nightly] [--no-server]
::
:: Options:
::   --tier <name>   Test tier to run (default: smoke)
::   --no-server     Skip starting RSMod (assume already running)

setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
for %%A in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fA\"
set "RSMOD_ROOT=%PROJECT_ROOT%"
if exist "%PROJECT_ROOT%rsmod\gradlew.bat" set "RSMOD_ROOT=%PROJECT_ROOT%rsmod\"
set "AGENT_RUNNER_ROOT=%PROJECT_ROOT%agent-runner"
if not exist "%AGENT_RUNNER_ROOT%\run.py" set "AGENT_RUNNER_ROOT=%PROJECT_ROOT%rsmod\agent-runner"
set "TIER=smoke"
set "START_SERVER=1"

:: Parse arguments
:parse_args
if "%~1"=="" goto :args_done
if /i "%~1"=="--tier" (
    set "TIER=%~2"
    shift & shift
    goto :parse_args
)
if /i "%~1"=="--no-server" (
    set "START_SERVER=0"
    shift
    goto :parse_args
)
echo Unknown option: %~1
exit /b 1
:args_done

:: ── Step 1: Install RSMod if needed ─────────────────────────────────────────
if "%START_SERVER%"=="1" (
    call "%SCRIPT_DIR%install-server.bat"
    if errorlevel 1 exit /b 1
)

:: ── Step 2: Configure RSProx ─────────────────────────────────────────────────
if exist "%RSMOD_ROOT%.data\client.key" (
    call "%SCRIPT_DIR%setup-rsprox.bat"
) else (
    echo [test] WARNING: client.key not found — RSProx not configured.
)

:: ── Step 3: Start RSMod server in a new window ───────────────────────────────
if "%START_SERVER%"=="1" (
    echo [test] Starting RSMod server in background window...
    start "RSMod Server" cmd /c "cd /d "%RSMOD_ROOT%" && gradlew.bat run --console=plain > "%TEMP%\rsmod-server.log" 2>&1"
    echo [test] Server started ^(logs: %TEMP%\rsmod-server.log^)
)

:: ── Step 4: Wait for AgentBridge port 43595 ───────────────────────────────────
echo [test] Waiting for AgentBridge on port 43595...
python "%AGENT_RUNNER_ROOT%\scripts\wait_for_bridge.py"
if errorlevel 1 (
    echo [test] ERROR: AgentBridge did not start. Check %TEMP%\rsmod-server.log
    exit /b 1
)
echo [test] AgentBridge ready.

:: ── Step 5: Run agent-runner ──────────────────────────────────────────────────
echo.
echo [test] Running %TIER% tests...
echo ---------------------------------------------------------

cd /d "%AGENT_RUNNER_ROOT%"

:: Try to get git commit hash (optional, graceful fallback)
set "COMMIT=unknown"
for /f "tokens=*" %%H in ('git -C "%PROJECT_ROOT%" rev-parse --short HEAD 2^>nul') do set "COMMIT=%%H"

python run.py --tier "%TIER%" --commit "%COMMIT%"
set TEST_EXIT=%errorlevel%

echo.
echo ---------------------------------------------------------
if "%TEST_EXIT%"=="0" (
    echo [test] All tests passed.
) else (
    echo [test] Tests failed. Check test-reports\%TIER%-latest.md
)

exit /b %TEST_EXIT%
