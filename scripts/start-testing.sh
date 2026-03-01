#!/usr/bin/env bash
# start-testing.sh — full Sprint 0 testing session orchestrator.
#
# Workflow:
#   1. Install RSMod if needed
#   2. Configure RSProx proxy-targets.yaml
#   3. Start RSMod server in background
#   4. Wait for AgentBridge (port 43595) to respond
#   5. Run agent-runner smoke tests
#   6. Print report and exit with 0 (pass) or 1 (fail)
#
# Usage:
#   bash scripts/start-testing.sh [--tier smoke|nightly] [--no-server] [--no-rsprox]
#
# Options:
#   --tier <name>   Test tier to run (default: smoke)
#   --no-server     Skip starting RSMod (assume already running)
#   --no-rsprox     Skip RSProx (wire-level tests disabled, uses bridge only)

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
RSMOD_ROOT="$PROJECT_ROOT"
if [ -f "$PROJECT_ROOT/rsmod/gradlew" ] || [ -f "$PROJECT_ROOT/rsmod/gradlew.bat" ]; then
    RSMOD_ROOT="$PROJECT_ROOT/rsmod"
fi
AGENT_RUNNER_ROOT="$PROJECT_ROOT/agent-runner"
if [ ! -f "$AGENT_RUNNER_ROOT/run.py" ] && [ -f "$PROJECT_ROOT/rsmod/agent-runner/run.py" ]; then
    AGENT_RUNNER_ROOT="$PROJECT_ROOT/rsmod/agent-runner"
fi

TIER="smoke"
START_SERVER=true
START_RSPROX=false   # RSProx is optional for smoke tier
RSMOD_PID=""

# Parse args
while [[ $# -gt 0 ]]; do
    case $1 in
        --tier)     TIER="$2"; shift 2 ;;
        --no-server) START_SERVER=false; shift ;;
        --rsprox)   START_RSPROX=true; shift ;;
        *)          echo "Unknown option: $1"; exit 1 ;;
    esac
done

cleanup() {
    echo ""
    echo "[test] Cleaning up..."
    if [ -n "$RSMOD_PID" ] && kill -0 "$RSMOD_PID" 2>/dev/null; then
        echo "[test] Stopping RSMod server (PID $RSMOD_PID)..."
        kill "$RSMOD_PID" 2>/dev/null || true
    fi
}
trap cleanup EXIT

# ── Step 1: Install RSMod if needed ─────────────────────────────────────────
if $START_SERVER; then
    bash "$SCRIPT_DIR/install-server.sh"
fi

# ── Step 2: Configure RSProx ─────────────────────────────────────────────────
if [ -f "$RSMOD_ROOT/.data/client.key" ]; then
    bash "$SCRIPT_DIR/setup-rsprox.sh"
else
    echo "[test] WARNING: client.key not found — RSProx not configured."
fi

# ── Step 3: Start RSMod server in background ─────────────────────────────────
if $START_SERVER; then
    echo "[test] Starting RSMod server in background..."
    cd "$RSMOD_ROOT"
    ./gradlew run --console=plain > /tmp/rsmod-server.log 2>&1 &
    RSMOD_PID=$!
    echo "[test] RSMod PID: $RSMOD_PID (logs: /tmp/rsmod-server.log)"
fi

# ── Step 4: Wait for AgentBridge port 43595 ───────────────────────────────────
echo "[test] Waiting for AgentBridge on port 43595..."
python3 "$AGENT_RUNNER_ROOT/scripts/wait_for_bridge.py" || {
    echo "[test] ERROR: AgentBridge did not start. Check /tmp/rsmod-server.log"
    exit 1
}
echo "[test] AgentBridge ready."

# ── Step 5: (Optional) start RSProx in background ────────────────────────────
RSPROX_PID=""
if $START_RSPROX; then
    echo "[test] Starting RSProx in background..."
    cd "$PROJECT_ROOT/rsprox"
    ./gradlew proxy > /tmp/rsprox.log 2>&1 &
    RSPROX_PID=$!
    echo "[test] RSProx PID: $RSPROX_PID"
    sleep 5  # Give RSProx time to start
fi

# ── Step 6: Run agent-runner ──────────────────────────────────────────────────
echo ""
echo "[test] Running $TIER tests..."
echo "─────────────────────────────────────────────────"
cd "$AGENT_RUNNER_ROOT"

COMMIT=$(git -C "$PROJECT_ROOT" rev-parse --short HEAD 2>/dev/null || echo "unknown")
python3 run.py --tier "$TIER" --commit "$COMMIT"
TEST_EXIT=$?

echo ""
echo "─────────────────────────────────────────────────"
if [ $TEST_EXIT -eq 0 ]; then
    echo "[test] ✅ All tests passed."
else
    echo "[test] ❌ Tests failed. Check test-reports/$TIER-latest.md"
fi

exit $TEST_EXIT
