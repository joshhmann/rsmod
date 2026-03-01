#!/usr/bin/env bash
# start-mcp.sh — Start the RSMod MCP server.
#
# The MCP server connects to AgentBridge (ws://localhost:43595) and exposes
# tools for LLM agents: execute_script, get_state, send_action, list_players.
#
# Prerequisites:
#   - bun installed (https://bun.sh)
#   - RSMod server running (scripts/start-server.sh)
#   - AgentBridge responding on port 43595
#
# Usage:
#   ./scripts/start-mcp.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
MCP_DIR="$PROJECT_ROOT/mcp"

if ! command -v bun &>/dev/null; then
    echo "ERROR: bun not found. Install from https://bun.sh"
    exit 1
fi

if [ ! -d "$MCP_DIR/node_modules" ]; then
    echo "[mcp] Installing dependencies..."
    cd "$MCP_DIR"
    bun install
fi

echo "[mcp] Starting RSMod MCP server..."
echo "[mcp] Connecting to AgentBridge at ws://localhost:43595"
echo "[mcp] Use Ctrl+C to stop."
echo

cd "$MCP_DIR"
exec bun server.ts
