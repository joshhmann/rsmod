#!/usr/bin/env bash
# start-server.sh — start the RSMod v2 game server.
# Runs install first if not already done.
# AgentBridge WebSocket will be available on port 43595 once a player logs in.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
RSMOD_ROOT="$PROJECT_ROOT"
if [ -f "$PROJECT_ROOT/rsmod/gradlew" ] || [ -f "$PROJECT_ROOT/rsmod/gradlew.bat" ]; then
    RSMOD_ROOT="$PROJECT_ROOT/rsmod"
fi

# Ensure installed
bash "$SCRIPT_DIR/install-server.sh"

echo "[server] Starting RSMod v2..."
echo "[server] Game port:        43594"
echo "[server] AgentBridge port: 43595 (active after first player login)"
echo ""

cd "$RSMOD_ROOT"
./gradlew run --console=plain
