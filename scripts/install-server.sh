#!/usr/bin/env bash
# install-server.sh — first-time RSMod v2 setup
# Downloads game cache, packs it, generates RSA key pair.
# Safe to run repeatedly — skips if already installed.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
RSMOD="$PROJECT_ROOT"
if [ -f "$PROJECT_ROOT/rsmod/gradlew" ] || [ -f "$PROJECT_ROOT/rsmod/gradlew.bat" ]; then
    RSMOD="$PROJECT_ROOT/rsmod"
fi
GAME_KEY="$RSMOD/.data/game.key"

if [ -f "$GAME_KEY" ]; then
    echo "[install] RSMod already installed (game.key present). Skipping."
    exit 0
fi

echo "[install] Running RSMod install (cache download + RSA key generation)..."
echo "[install] This may take several minutes on first run."
echo ""

cd "$RSMOD"
./gradlew install --console=plain

echo ""
echo "[install] Install complete."
echo "[install] RSA modulus written to: $RSMOD/.data/client.key"
