@echo off
:: install-server.bat — first-time RSMod v2 setup
:: Downloads game cache, packs it, generates RSA key pair.
:: Safe to run repeatedly — skips if already installed.

setlocal

set "SCRIPT_DIR=%~dp0"
for %%A in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fA\"
set "RSMOD_ROOT=%PROJECT_ROOT%"
if exist "%PROJECT_ROOT%rsmod\gradlew.bat" set "RSMOD_ROOT=%PROJECT_ROOT%rsmod\"
set "GAME_KEY=%RSMOD_ROOT%.data\game.key"

if exist "%GAME_KEY%" (
    echo [install] RSMod already installed ^(game.key present^). Skipping.
    exit /b 0
)

echo [install] Running RSMod install ^(cache download + RSA key generation^)...
echo [install] This may take several minutes on first run.
echo.

cd /d "%RSMOD_ROOT%"
call gradlew.bat install --console=plain

echo.
echo [install] Install complete.
echo [install] RSA modulus written to: %RSMOD_ROOT%.data\client.key

endlocal
