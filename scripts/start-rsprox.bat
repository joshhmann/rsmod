@echo off
:: start-rsprox.bat — start RSProx configured for local RSMod.
:: Configures %USERPROFILE%\.rsprox\proxy-targets.yaml first.
:: Resets RSProx local cache by default for deterministic startup.
:: RSProx GUI will open — select "RSMod Local" from the target dropdown.
:: AgentBroadcaster (port 43596) starts automatically with RSProx.

setlocal

set "SCRIPT_DIR=%~dp0"
for %%A in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fA\"
set "PWSH=C:\Program Files\PowerShell\7\pwsh.exe"

:: Force a full reset for now to ensure stale client caches are cleared after cache/symbol overhaul.
set "RSPROX_FULL_RESET=1"

:: Optional: pass extra RuneLite launcher args to RSProx via system property.
:: RSProx reads: System.getProperty("net.rsprox.gui.args") and splits on '|'.
:: Default: safe mode ON for stability (disable with `set RSPROX_SAFE_MODE=0`).
set "RSPROX_RL_ARGS=%RSPROX_RUNELITE_ARGS%"
if "%RSPROX_RL_ARGS%"=="" (
    if /I not "%RSPROX_SAFE_MODE%"=="0" (
        set "RSPROX_RL_ARGS=--safe-mode"
    )
)
if not "%RSPROX_RL_ARGS%"=="" (
    set "JAVA_TOOL_OPTIONS=%JAVA_TOOL_OPTIONS% -Dnet.rsprox.gui.args=%RSPROX_RL_ARGS%"
)

if /I not "%RSPROX_SKIP_RESET%"=="1" (
    echo [rsprox] Resetting local RSProx cache...
    if exist "%PWSH%" (
        if /I "%RSPROX_FULL_RESET%"=="1" (
            call "%PWSH%" -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%reset-rsprox-cache.ps1" -FullReset
        ) else (
            call "%PWSH%" -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%reset-rsprox-cache.ps1"
        )
    ) else (
        if /I "%RSPROX_FULL_RESET%"=="1" (
            powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%reset-rsprox-cache.ps1" -FullReset
        ) else (
            powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%reset-rsprox-cache.ps1"
        )
    )
)

:: Write/refresh proxy-targets.yaml
call "%SCRIPT_DIR%setup-rsprox.bat"
if errorlevel 1 exit /b 1

echo.
echo [rsprox] Starting RSProx...
echo [rsprox] Proxy port:        43600 ^(RSProx HTTP, client connects here^)
echo [rsprox] Game server:       localhost:43594 ^(RSMod^)
echo [rsprox] AgentBroadcaster:  43596 ^(wire packet events for agent-runner^)
if not "%RSPROX_RL_ARGS%"=="" echo [rsprox] RuneLite args:      %RSPROX_RL_ARGS%
echo [rsprox] Select 'RSMod Local' from the target dropdown in the GUI.
echo.

cd /d "%PROJECT_ROOT%rsprox"
call gradlew.bat proxy

endlocal
