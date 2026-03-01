@echo off
:: start-server.bat — start the RSMod v2 game server.
:: Runs install first if not already done.
:: Packs cache before startup to keep client/server cache state in sync.
:: AgentBridge WebSocket will be available on port 43595 once a player logs in.

setlocal

set "SCRIPT_DIR=%~dp0"
for %%A in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fA\"
set "RSMOD_ROOT=%PROJECT_ROOT%"
if exist "%PROJECT_ROOT%rsmod\gradlew.bat" set "RSMOD_ROOT=%PROJECT_ROOT%rsmod\"

:: Ensure installed
call "%SCRIPT_DIR%install-server.bat"
if errorlevel 1 exit /b 1

echo [server] Starting RSMod v2...
echo [server] Game port:        43594
echo [server] AgentBridge port: 43595 ^(active after first player login^)
echo.

cd /d "%RSMOD_ROOT%"
echo [server] Running cache pack before startup...
call gradlew.bat packCache -PcachePack=--allow-type-verification-failures --console=plain --no-daemon
if errorlevel 1 exit /b 1

call gradlew.bat :server:app:run --console=plain --args="--skip-type-verification --allow-type-verification-failures"

endlocal
