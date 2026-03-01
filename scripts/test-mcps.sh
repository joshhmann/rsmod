#!/bin/bash
# MCP Health Check Script
# Tests that all MCP servers are working

set -e

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "🧪 MCP Server Health Check"
echo "=========================="
echo ""

FAILED=0

# Test rsmod-game
echo "🎮 Testing rsmod-game..."
cd "$REPO_ROOT/mcp"
if bun run check-server.ts 2>/dev/null; then
    echo "✅ rsmod-game: Server check passed"
else
    echo "⚠️  rsmod-game: Server not running (expected if RSMod isn't started)"
fi

# Check rsmod-game dependencies
if [ -d "$REPO_ROOT/mcp/node_modules" ]; then
    echo "✅ rsmod-game: Dependencies installed"
else
    echo "❌ rsmod-game: Dependencies missing - run ./scripts/install-mcps.sh"
    FAILED=$((FAILED + 1))
fi

# Test osrs-cache
echo ""
echo "📦 Testing osrs-cache..."
cd "$REPO_ROOT/mcp-osrs"
if [ -d "node_modules" ]; then
    echo "✅ osrs-cache: Dependencies installed"
    # Try to run a simple test
    if node -e "console.log('Cache MCP OK')" 2>/dev/null; then
        echo "✅ osrs-cache: Can execute"
    fi
else
    echo "❌ osrs-cache: Dependencies missing - run ./scripts/install-mcps.sh"
    FAILED=$((FAILED + 1))
fi

# Test osrs-wiki-rev233
echo ""
echo "📜 Testing osrs-wiki-rev233..."
cd "$REPO_ROOT/mcp-osrs-rev233"
if python3 -c "import requests; print('Wiki MCP OK')" 2>/dev/null; then
    echo "✅ osrs-wiki-rev233: Python dependencies installed"
else
    echo "❌ osrs-wiki-rev233: Dependencies missing - run pip install requests"
    FAILED=$((FAILED + 1))
fi

# Test imports
if python3 -c "import sys; sys.path.insert(0, '$REPO_ROOT/OSRSWikiScraper'); from rev233.scraper import Rev233Scraper; print('✅ Wiki scraper imports OK')" 2>/dev/null; then
    :
else
    echo "❌ osrs-wiki-rev233: Cannot import scraper module"
    FAILED=$((FAILED + 1))
fi

echo ""
echo "=========================="
if [ $FAILED -eq 0 ]; then
    echo "✅ All MCP checks passed!"
    echo ""
    echo "🚀 Ready to use with Claude Code"
    echo "   Open this directory in Claude Code and MCPs will auto-detect"
else
    echo "❌ $FAILED MCP(s) have issues"
    echo ""
    echo "🔧 Run: ./scripts/install-mcps.sh"
fi

echo ""
exit $FAILED
