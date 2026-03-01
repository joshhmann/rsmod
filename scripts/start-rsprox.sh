#!/usr/bin/env bash
# start-rsprox.sh — start RSProx configured for local RSMod.
# Configures ~/.rsprox/proxy-targets.yaml first (needs client.key from install).
# RSProx GUI will open — select "RSMod Local" from the target dropdown.
# AgentBroadcaster (port 43596) starts automatically with RSProx.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
TARGETS_FILE="$HOME/.rsprox/proxy-targets.yaml"

# Write/refresh proxy-targets.yaml
bash "$SCRIPT_DIR/setup-rsprox.sh"

echo ""
echo "[rsprox] Starting RSProx..."
echo "[rsprox] Proxy port:         43600 (RSProx HTTP, client connects here)"
echo "[rsprox] Game server:        localhost:43594 (RSMod)"
echo "[rsprox] AgentBroadcaster:   43596 (wire packet events for agent-runner)"
echo "[rsprox] Select 'RSMod Local' from the target dropdown in the GUI."
echo ""

cd "$PROJECT_ROOT/rsprox"
./gradlew proxy
