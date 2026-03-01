# MCP Server Deployment Guide

**How to deploy and distribute our MCP servers for team use**

> Operational note: deployment availability does not change task-execution policy.
> For day-to-day agent work, follow `README.md` and `AGENTS.md`.

---

## 📦 Deployment Options

| Method | Best For | Complexity | Audience |
|--------|----------|------------|----------|
| **Local Dev** | Development, testing | Low | Developers |
| **Package Managers** | Distribution to team | Medium | Team members |
| **Docker** | Consistent environments | Medium | CI/CD, servers |
| **Smithery** | Public distribution | Low | Community |
| **GitHub Releases** | Versioned binaries | Low | End users |

---

## 🚀 Option 1: Local Development (Current)

**Best for:** Active development, testing changes

### Setup
```bash
# Clone repo
git clone https://github.com/your-org/OSRS-PS-DEV.git
cd OSRS-PS-DEV

# Install all MCPs
./scripts/install-mcps.sh

# Or install individually:

# rsmod-game
cd mcp && bun install

# osrs-cache  
cd mcp-osrs && npm install

# osrs-wiki-rev233
cd mcp-osrs-rev233 && pip install -r requirements.txt
```

### Claude Code Integration
```json
// .mcp.json (already configured)
{
  "mcpServers": {
    "rsmod-game": { "command": "bun", "args": ["mcp/server-enhanced.ts"] },
    "osrs-cache": { "command": "node", "args": ["mcp-osrs/dist/index.js"] },
    "osrs-wiki-rev233": { "command": "python", "args": ["mcp-osrs-rev233/server.py"] }
  }
}
```

**Pros:**
- ✅ Instant changes
- ✅ Debuggable
- ✅ No packaging needed

**Cons:**
- ❌ Requires dev environment
- ❌ Not portable
- ❌ Dependencies must be installed

---

## 📦 Option 2: Package Manager Distribution

**Best for:** Team members who need stable versions

### rsmod-game → npm

```json
// mcp/package.json (updated)
{
  "name": "@osrs-ps-dev/rsmod-mcp",
  "version": "1.0.0",
  "description": "RSMod Game Server MCP",
  "main": "dist/server.js",
  "bin": {
    "rsmod-mcp": "dist/server.js"
  },
  "scripts": {
    "build": "bun build server-enhanced.ts --outdir=dist --target=node",
    "prepublishOnly": "npm run build"
  },
  "files": ["dist/"],
  "publishConfig": {
    "registry": "https://npm.pkg.github.com"
  }
}
```

**Publish:**
```bash
cd mcp
npm run build
npm publish --access public
```

**Install:**
```bash
npm install -g @osrs-ps-dev/rsmod-mcp
```

**Use:**
```json
{
  "mcpServers": {
    "rsmod-game": {
      "command": "rsmod-mcp"
    }
  }
}
```

---

### osrs-cache → npm (already published as @jayarrowz/mcp-osrs)

**Fork/extend existing:**
```bash
# If we need custom changes:
git clone https://github.com/jayarrowz/mcp-osrs.git mcp-osrs-fork
# Make changes
npm publish --scope @osrs-ps-dev
```

---

### osrs-wiki-rev233 → PyPI

```python
# mcp-osrs-rev233/setup.py
from setuptools import setup

setup(
    name="osrs-wiki-rev233",
    version="1.0.0",
    packages=["rev233"],
    entry_points={
        "console_scripts": [
            "osrs-wiki-rev233=rev233.server:main",
        ],
    },
    install_requires=["requests>=2.31.0"],
)
```

**Publish:**
```bash
cd mcp-osrs-rev233
python setup.py sdist bdist_wheel
twine upload dist/*
```

**Install:**
```bash
pip install osrs-wiki-rev233
```

**Use:**
```json
{
  "mcpServers": {
    "osrs-wiki-rev233": {
      "command": "osrs-wiki-rev233"
    }
  }
}
```

---

## 🐳 Option 3: Docker Deployment

**Best for:** Consistent environments, CI/CD

### Dockerfile for rsmod-game

```dockerfile
# mcp/Dockerfile
FROM oven/bun:1

WORKDIR /app

COPY package.json bun.lock ./
RUN bun install --frozen-lockfile

COPY . .
RUN bun build server-enhanced.ts --outdir=dist --target=node

EXPOSE 43595

CMD ["bun", "dist/server.js"]
```

**Build & Push:**
```bash
cd mcp
docker build -t ghcr.io/osrs-ps-dev/rsmod-mcp:latest .
docker push ghcr.io/osrs-ps-dev/rsmod-mcp:latest
```

**Run:**
```bash
docker run -e BRIDGE_URL=ws://host.docker.internal:43595 ghcr.io/osrs-ps-dev/rsmod-mcp
```

---

### Docker Compose (All MCPs)

```yaml
# docker-compose.mcps.yml
version: '3.8'

services:
  rsmod-game:
    build: ./mcp
    environment:
      - BRIDGE_URL=ws://host.docker.internal:43595
    network_mode: host

  osrs-cache:
    build: ./mcp-osrs
    volumes:
      - ./mcp-osrs/data:/app/data:ro

  osrs-wiki-rev233:
    build: ./mcp-osrs-rev233
    environment:
      - PYTHONUNBUFFERED=1
```

**Run all:**
```bash
docker-compose -f docker-compose.mcps.yml up
```

---

## 🌐 Option 4: Smithery (MCP Registry)

**Best for:** Public distribution, easy discovery

### What is Smithery?
Smithery is a registry for MCP servers - like npm but for MCPs.

### Publish rsmod-game

```yaml
# mcp/smithery.yaml
name: rsmod-game
description: RSMod Game Server MCP for OSRS private server control
version: 1.0.0
author: OSRS-PS-Dev Team
entrypoint:
  type: stdio
  command: bun
  args: ["server-enhanced.ts"]
install:
  - bun install
env:
  BRIDGE_URL: ws://localhost:43595
```

**CLI Install for Users:**
```bash
# Users can install with one command:
npx @smithery/cli install rsmod-game

# Or add to Claude Code:
npx @smithery/cli add rsmod-game --claude
```

---

## 📥 Option 5: One-Click Install Scripts

**Best for:** Quick team onboarding

### Universal Install Script

```bash
#!/bin/bash
# scripts/install-mcps.sh

echo "🎮 Installing OSRS-PS MCP Servers..."

# Detect OS
OS="$(uname -s)"

# Install rsmod-game
if [ ! -d "mcp/node_modules" ]; then
    echo "Installing rsmod-game..."
    cd mcp && bun install && cd ..
fi

# Install osrs-cache
if [ ! -d "mcp-osrs/node_modules" ]; then
    echo "Installing osrs-cache..."
    cd mcp-osrs && npm install && cd ..
fi

# Install osrs-wiki-rev233
if ! python -c "import requests" 2>/dev/null; then
    echo "Installing Python dependencies..."
    pip install requests
fi

echo "✅ All MCPs installed!"
echo ""
echo "Add to your .mcp.json:"
cat .mcp.json
```

**Windows Version:**
```powershell
# scripts/install-mcps.ps1
Write-Host "🎮 Installing OSRS-PS MCP Servers..."

# rsmod-game
if (!(Test-Path "mcp/node_modules")) {
    Write-Host "Installing rsmod-game..."
    Set-Location mcp; bun install; Set-Location ..
}

# osrs-cache
if (!(Test-Path "mcp-osrs/node_modules")) {
    Write-Host "Installing osrs-cache..."
    Set-Location mcp-osrs; npm install; Set-Location ..
}

# osrs-wiki-rev233
python -c "import requests" 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "Installing Python dependencies..."
    pip install requests
}

Write-Host "✅ All MCPs installed!"
```

---

## 🔄 Option 6: CI/CD Deployment

**Best for:** Automated releases

### GitHub Actions Workflow

```yaml
# .github/workflows/release-mcps.yml
name: Release MCP Servers

on:
  push:
    tags:
      - 'mcp-v*'

jobs:
  build-and-release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      # Build rsmod-game
      - name: Setup Bun
        uses: oven-sh/setup-bun@v1
      - name: Build rsmod-game
        run: |
          cd mcp
          bun install
          bun build server-enhanced.ts --outdir=dist --target=node
          tar -czf rsmod-game.tar.gz dist/ package.json
      
      # Build osrs-cache
      - name: Setup Node
        uses: actions/setup-node@v3
      - name: Build osrs-cache
        run: |
          cd mcp-osrs
          npm ci
          npm run build
          tar -czf osrs-cache.tar.gz dist/ package.json data/
      
      # Create Release
      - name: Create Release
        uses: softprops/action-gh-release@v1
        with:
          files: |
            mcp/rsmod-game.tar.gz
            mcp-osrs/osrs-cache.tar.gz
          body: |
            ## MCP Server Release
            
            ### Installation
            ```bash
            # Download and extract
            wget https://github.com/osrs-ps-dev/releases/download/${{ github.ref_name }}/rsmod-game.tar.gz
            tar -xzf rsmod-game.tar.gz
            
            # Run
            cd rsmod-game && bun dist/server.js
            ```
```

---

## 🎯 Recommended Deployment Strategy

### For Development Team (You + Gemini)

**Use:** Local development + Git tracking

```bash
# Clone once
git clone <repo>

# Install deps
./scripts/install-mcps.sh

# .mcp.json uses local paths
# Edit files directly, changes reflected immediately
```

---

### For External Contributors

**Use:** Package managers

```bash
# Published packages
npm install -g @osrs-ps-dev/rsmod-mcp
pip install osrs-wiki-rev233

# Or Docker
docker run ghcr.io/osrs-ps-dev/rsmod-mcp
```

---

### For End Users (Players)

**Use:** Smithery + one-click install

```bash
# Simplest possible
npx @smithery/cli add rsmod-game --claude
```

---

## 📋 Deployment Checklist

Before deploying, ensure:

- [ ] All dependencies documented
- [ ] Version numbers updated
- [ ] README with install instructions
- [ ] Error handling tested
- [ ] Logging configured
- [ ] Environment variables documented
- [ ] LICENSE file present
- [ ] .gitignore excludes node_modules/

---

## 🔧 Post-Deployment Verification

### Test Installation
```bash
# Fresh machine test
docker run --rm -it ubuntu:22.04 bash
apt-get update && apt-get install -y curl git

# Run install script
curl -sSL https://raw.githubusercontent.com/osrs-ps-dev/main/scripts/install-mcps.sh | bash

# Verify MCPs work
claude  # Should detect MCPs automatically
```

### Health Check Script
```bash
#!/bin/bash
# scripts/health-check.sh

echo "🔍 Checking MCP Health..."

# Check rsmod-game
cd mcp && bun run check-server.ts && echo "✅ rsmod-game OK" || echo "❌ rsmod-game FAIL"

# Check osrs-cache  
cd mcp-osrs && node -e "console.log('✅ osrs-cache OK')" || echo "❌ osrs-cache FAIL"

# Check osrs-wiki-rev233
cd mcp-osrs-rev233 && python -c "import rev233; print('✅ osrs-wiki-rev233 OK')" || echo "❌ FAIL"
```

---

## 🎉 Summary

| Audience | Method | Command |
|----------|--------|---------|
| **You (dev)** | Local + Git | `bun server-enhanced.ts` |
| **Team** | Package managers | `npm install -g @osrs-ps-dev/rsmod-mcp` |
| **CI/CD** | Docker | `docker run ghcr.io/...` |
| **Public** | Smithery | `npx @smithery/cli add rsmod-game` |

**Next Steps:**
1. Choose deployment method(s)
2. Set up CI/CD pipeline
3. Publish to registries
4. Test fresh installs
5. Document for users

Ready to deploy? 🚀

