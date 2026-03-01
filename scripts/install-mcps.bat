@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
for %%A in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fA"

echo.
echo ========================================
echo  OSRS-PS MCP Installer (Windows)
echo ========================================
echo.

where bun >nul 2>&1
if errorlevel 1 (
    echo ERROR: bun not found. Install from https://bun.sh
    exit /b 1
)

where node >nul 2>&1
if errorlevel 1 (
    echo ERROR: node not found. Install from https://nodejs.org/
    exit /b 1
)

where python >nul 2>&1
if errorlevel 1 (
    echo ERROR: python not found. Install from https://python.org/
    exit /b 1
)

echo [1/3] rsmod-game (mcp)
if exist "%PROJECT_ROOT%\mcp\node_modules" (
    echo     dependencies already installed
) else (
    cd /d "%PROJECT_ROOT%\mcp"
    bun install || exit /b 1
)

echo [2/3] osrs-cache (mcp-osrs)
if exist "%PROJECT_ROOT%\mcp-osrs\node_modules" (
    echo     dependencies already installed
) else (
    cd /d "%PROJECT_ROOT%\mcp-osrs"
    npm install || exit /b 1
)

echo [3/3] osrs-wiki-rev233 (python deps)
python -c "import requests" >nul 2>&1
if errorlevel 1 (
    python -m pip install -r "%PROJECT_ROOT%\mcp-osrs-rev233\requirements.txt" || exit /b 1
) else (
    echo     python dependencies already installed
)

echo.
echo MCP install complete.
echo Run: scripts\test-mcps.bat
echo.

endlocal
