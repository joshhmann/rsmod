@echo off
:: start-mcp.bat — Start the RSMod MCP server.
::
:: The MCP server connects to AgentBridge (ws://localhost:43595) and exposes
:: tools for LLM agents: execute_script, get_state, send_action, list_players.
::
:: Prerequisites:
::   - bun installed (https://bun.sh)
::   - RSMod server running (scripts\start-server.bat)
::   - AgentBridge responding on port 43595
::
:: Usage:
::   scripts\start-mcp.bat
::
:: To use with Claude Code, add .mcp.json to project root (already done).

setlocal

set "SCRIPT_DIR=%~dp0"
for %%A in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fA\"
set "MCP_DIR=%PROJECT_ROOT%mcp"

:: Check bun is installed
where bun >nul 2>&1
if errorlevel 1 (
    echo ERROR: bun not found. Install from https://bun.sh
    exit /b 1
)

:: Install deps if node_modules missing
if not exist "%MCP_DIR%\node_modules" (
    echo [mcp] Installing dependencies...
    cd /d "%MCP_DIR%"
    bun install
)

echo [mcp] Starting RSMod MCP server...
echo [mcp] Connecting to AgentBridge at ws://localhost:43595
echo [mcp] Use Ctrl+C to stop.
echo.

cd /d "%MCP_DIR%"
bun server.ts
