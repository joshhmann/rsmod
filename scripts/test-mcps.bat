@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
for %%A in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fA"
set FAILED=0

echo.
echo ========================================
echo  MCP Health Check (Windows)
echo ========================================
echo.

echo [1/6] rsmod-game dependencies...
if exist "%PROJECT_ROOT%\mcp\node_modules" (
    echo     PASS
) else (
    echo     FAIL - missing mcp\node_modules
    set /a FAILED+=1
)

echo [2/6] osrs-cache dependencies...
if exist "%PROJECT_ROOT%\mcp-osrs\node_modules" (
    echo     PASS
) else (
    echo     FAIL - missing mcp-osrs\node_modules
    set /a FAILED+=1
)

echo [3/6] osrs-wiki-rev233 python dependency...
python -c "import requests" >nul 2>&1
if errorlevel 1 (
    echo     FAIL - requests not installed
    set /a FAILED+=1
) else (
    echo     PASS
)

echo [4/6] rsmod-game startup smoke test...
powershell -NoProfile -Command "$ErrorActionPreference='Stop'; Set-Location '%PROJECT_ROOT%\mcp'; $p=Start-Process -FilePath 'bun' -ArgumentList 'server-enhanced.ts' -PassThru; Start-Sleep -Seconds 3; if ($p.HasExited) { exit 1 } else { Stop-Process -Id $p.Id -Force; exit 0 }" >nul 2>&1
if errorlevel 1 (
    echo     FAIL - server-enhanced.ts crashed on startup
    set /a FAILED+=1
) else (
    echo     PASS
)

echo [5/6] osrs-cache startup smoke test...
powershell -NoProfile -Command "$ErrorActionPreference='Stop'; Set-Location '%PROJECT_ROOT%\mcp-osrs'; $p=Start-Process -FilePath 'node' -ArgumentList 'dist/index.js' -PassThru; Start-Sleep -Seconds 3; if ($p.HasExited) { exit 1 } else { Stop-Process -Id $p.Id -Force; exit 0 }" >nul 2>&1
if errorlevel 1 (
    echo     FAIL - mcp-osrs dist/index.js crashed on startup
    set /a FAILED+=1
) else (
    echo     PASS
)

echo [6/6] osrs-wiki-rev233 startup smoke test...
powershell -NoProfile -Command "$ErrorActionPreference='Stop'; Set-Location '%PROJECT_ROOT%\mcp-osrs-rev233'; $p=Start-Process -FilePath 'python' -ArgumentList '-u','server.py' -PassThru; Start-Sleep -Seconds 3; if ($p.HasExited) { exit 1 } else { Stop-Process -Id $p.Id -Force; exit 0 }" >nul 2>&1
if errorlevel 1 (
    echo     FAIL - mcp-osrs-rev233 server.py crashed on startup
    set /a FAILED+=1
) else (
    echo     PASS
)

echo.
echo ========================================
if %FAILED%==0 (
    echo  All MCP checks passed.
    echo ========================================
    endlocal & exit /b 0
) else (
    echo  %FAILED% check^(s^) failed.
    echo ========================================
    echo  Run: scripts\install-mcps.bat
    endlocal & exit /b 1
)
