#!/bin/bash
# MCP Installation Script
# Installs all MCP servers for OSRS-PS-DEVELOPMENT

set -e

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OS="$(uname -s)"

echo "🎮 OSRS-PS MCP Server Installer"
echo "================================"
echo ""

# Check prerequisites
echo "📋 Checking prerequisites..."

# Check for Bun (for rsmod-game)
if ! command -v bun &> /dev/null; then
    echo "⚠️  Bun not found. Installing..."
    curl -fsSL https://bun.sh/install | bash
    export PATH="$HOME/.bun/bin:$PATH"
fi
echo "✅ Bun: $(bun --version)"

# Check for Node (for osrs-cache)
if ! command -v node &> /dev/null; then
    echo "⚠️  Node.js not found. Please install Node.js 18+"
    echo "   Visit: https://nodejs.org/"
    exit 1
fi
echo "✅ Node: $(node --version)"

# Check for Python (for osrs-wiki-rev233)
if ! command -v python3 &> /dev/null; then
    echo "⚠️  Python not found. Please install Python 3.9+"
    echo "   Visit: https://python.org/"
    exit 1
fi
echo "✅ Python: $(python3 --version)"

echo ""
echo "📦 Installing MCP servers..."
echo ""

# Install rsmod-game
echo "🎮 Installing rsmod-game..."
cd "$REPO_ROOT/mcp"
if [ -d "node_modules" ]; then
    echo "   Dependencies already installed, skipping..."
else
    bun install
    echo "✅ rsmod-game installed"
fi

# Install osrs-cache
echo ""
echo "📦 Installing osrs-cache..."
cd "$REPO_ROOT/mcp-osrs"
if [ -d "node_modules" ]; then
    echo "   Dependencies already installed, skipping..."
else
    npm install
    echo "✅ osrs-cache installed"
fi

# Install osrs-wiki-rev233
echo ""
echo "📜 Installing osrs-wiki-rev233..."
cd "$REPO_ROOT/mcp-osrs-rev233"
if python3 -c "import requests" 2>/dev/null; then
    echo "   Python dependencies already installed, skipping..."
else
    pip3 install requests
    echo "✅ osrs-wiki-rev233 dependencies installed"
fi

echo ""
echo "================================"
echo "✅ All MCP servers installed!"
echo ""
echo "📍 Installation locations:"
echo "   rsmod-game:       $REPO_ROOT/mcp/"
echo "   osrs-cache:       $REPO_ROOT/mcp-osrs/"
echo "   osrs-wiki-rev233: $REPO_ROOT/mcp-osrs-rev233/"
echo ""
echo "🚀 Next steps:"
echo "   1. Ensure RSMod server is running on port 43595"
echo "   2. Open Claude Code in this directory"
echo "   3. MCP servers will auto-detect from .mcp.json"
echo ""
echo "🧪 Test the installation:"
echo "   ./scripts/test-mcps.sh"
echo ""
