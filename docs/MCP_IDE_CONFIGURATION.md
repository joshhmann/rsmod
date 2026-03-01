# MCP IDE Configuration Guide

**How to configure MCP servers for different IDEs**

> Operational note: this doc covers IDE/server configuration.
> Active task execution policy is in `README.md` and `AGENTS.md` (`osrs-cache` primary; do not block on `osrs-wiki-rev233`).

---

## 🎯 Overview

We provide 5 MCP servers:

| MCP | Purpose | Needs Local Files |
|-----|---------|-------------------|
| 🎮 **rsmod-game** | Control RSMod server | ✅ Yes |
| 📦 **osrs-cache** | Cache ID lookup | ✅ Yes |
| 📜 **osrs-wiki-rev233** | Historical wiki data | ✅ Yes |
| 📚 **context7** | Documentation search | ❌ No (npx) |
| 🔍 **web-search** | Web search (Brave) | ❌ No (npx) |

---

## ⚡ Quick Reference by IDE

| IDE | Config File | Auto-Detect | Notes |
|-----|-------------|-------------|-------|
| **Claude Code** | `.mcp.json` | ✅ Yes | Just open project |
| **Kimi CLI** | `.mcp.json` | ✅ Yes | Just open project |
| **AntiGravity** | Custom config | ❌ Manual | Copy from below |
| **Cursor** | Settings > MCP | ❌ Manual | Paste JSON |
| **VS Code** | `settings.json` | ❌ Manual | Add mcp.servers |
| **Windsurf** | `config.yaml` | ❌ Manual | Add mcpServers |

---

## 🎮 Claude Code / Kimi CLI

**Easiest - just works!**

1. Open project directory:
   ```bash
   cd Z:/Projects/OSRS-PS-DEV
   claude  # or kimi
   ```

2. MCPs auto-load from `.mcp.json`

3. Verify in chat:
   ```
   What MCP tools do you have?
   ```

**That's it!** No additional config needed.

---

## 🛸 AntiGravity IDE

**Requires manual configuration**

### Step 1: Find AntiGravity Config Location

AntiGravity typically stores MCP config in one of these locations:

- **Windows:** `%APPDATA%/AntiGravity/mcp.json`
- **macOS:** `~/Library/Application Support/AntiGravity/mcp.json`
- **Linux:** `~/.config/AntiGravity/mcp.json`

Or in your project workspace settings.

### Step 2: Add MCP Configuration

Copy this into your AntiGravity MCP config:

```json
{
  "mcpServers": {
    "rsmod-game": {
      "type": "stdio",
      "command": "bun",
      "args": ["Z:/Projects/OSRS-PS-DEV/mcp/server-enhanced.ts"],
      "env": {
        "BRIDGE_URL": "ws://localhost:43595"
      }
    },
    "osrs-cache": {
      "type": "stdio",
      "command": "node",
      "args": ["Z:/Projects/OSRS-PS-DEV/mcp-osrs/dist/index.js"]
    },
    "osrs-wiki-rev233": {
      "type": "stdio",
      "command": "python",
      "args": ["Z:/Projects/OSRS-PS-DEV/mcp-osrs-rev233/server.py"]
    },
    "context7": {
      "type": "stdio",
      "command": "npx",
      "args": ["-y", "@upstash/context7-mcp@latest"]
    },
    "web-search": {
      "type": "stdio",
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-brave-search@latest"],
      "env": {
        "BRAVE_API_KEY": "YOUR_BRAVE_API_KEY_HERE"
      }
    }
  }
}
```

### Step 3: Get Brave API Key (for web-search)

1. Go to https://brave.com/search/api/
2. Sign up for free tier (2000 queries/month)
3. Copy your API key
4. Replace `YOUR_BRAVE_API_KEY_HERE` in config

### Step 4: Restart AntiGravity

Close and reopen AntiGravity to load new MCPs.

### Step 5: Verify

Ask in chat:
```
List your available MCP tools
```

Should show all 5 MCPs.

---

## 🎯 Cursor IDE

**Settings > MCP > Add Server**

### Method 1: Project Settings (Recommended)

Create `.cursor/mcp.json` in your project:

```json
{
  "mcpServers": {
    "rsmod-game": {
      "command": "bun",
      "args": ["${workspaceFolder}/mcp/server-enhanced.ts"],
      "env": {
        "BRIDGE_URL": "ws://localhost:43595"
      }
    },
    "osrs-cache": {
      "command": "node",
      "args": ["${workspaceFolder}/mcp-osrs/dist/index.js"]
    },
    "osrs-wiki-rev233": {
      "command": "python",
      "args": ["${workspaceFolder}/mcp-osrs-rev233/server.py"]
    },
    "context7": {
      "command": "npx",
      "args": ["-y", "@upstash/context7-mcp@latest"]
    },
    "web-search": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-brave-search@latest"],
      "env": {
        "BRAVE_API_KEY": "YOUR_API_KEY"
      }
    }
  }
}
```

### Method 2: Global Settings

1. Open Cursor Settings (Ctrl+,)
2. Search for "MCP"
3. Click "Add MCP Server"
4. Paste the JSON above

---

## 🔷 VS Code

**Add to `.vscode/settings.json`**

```json
{
  "mcp.servers": {
    "rsmod-game": {
      "type": "stdio",
      "command": "bun",
      "args": ["${workspaceFolder}/mcp/server-enhanced.ts"],
      "env": {
        "BRIDGE_URL": "ws://localhost:43595"
      }
    },
    "osrs-cache": {
      "type": "stdio",
      "command": "node",
      "args": ["${workspaceFolder}/mcp-osrs/dist/index.js"]
    },
    "osrs-wiki-rev233": {
      "type": "stdio",
      "command": "python",
      "args": ["${workspaceFolder}/mcp-osrs-rev233/server.py"]
    },
    "context7": {
      "type": "stdio",
      "command": "npx",
      "args": ["-y", "@upstash/context7-mcp@latest"]
    },
    "web-search": {
      "type": "stdio",
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-brave-search@latest"],
      "env": {
        "BRAVE_API_KEY": "${env:BRAVE_API_KEY}"
      }
    }
  }
}
```

**Set environment variable for Brave key:**
```bash
# Windows
setx BRAVE_API_KEY "your_key_here"

# macOS/Linux
export BRAVE_API_KEY="your_key_here"
```

---

## 🌊 Windsurf

**Add to `config.yaml` or `.windsurf/mcp.json`**

```yaml
mcpServers:
  rsmod-game:
    type: stdio
    command: bun mcp/server-enhanced.ts
    env:
      BRIDGE_URL: ws://localhost:43595
  
  osrs-cache:
    type: stdio
    command: node mcp-osrs/dist/index.js
  
  osrs-wiki-rev233:
    type: stdio
    command: python mcp-osrs-rev233/server.py
  
  context7:
    type: stdio
    command: npx -y @upstash/context7-mcp@latest
  
  web-search:
    type: stdio
    command: npx -y @modelcontextprotocol/server-brave-search@latest
    env:
      BRAVE_API_KEY: ${BRAVE_API_KEY}
```

---

## 🔧 Environment Variables

### Required

| Variable | Used By | How to Set |
|----------|---------|------------|
| `BRIDGE_URL` | rsmod-game | Already set in config |
| `BRAVE_API_KEY` | web-search | Get from brave.com/search/api |

### Setting BRAVE_API_KEY

**Windows (PowerShell):**
```powershell
[Environment]::SetEnvironmentVariable("BRAVE_API_KEY", "your_key", "User")
```

**macOS/Linux:**
```bash
echo 'export BRAVE_API_KEY="your_key"' >> ~/.bashrc
source ~/.bashrc
```

**Or in IDE config:**
Most IDEs support `${env:VAR}` or `${VAR}` syntax in MCP config.

---

## 🧪 Testing Your Configuration

After setup, test each MCP:

### Test rsmod-game
```
Check server status with rsmod-game
```

### Test osrs-cache
```
Look up item "Bronze bar" in osrs-cache
```

### Test osrs-wiki-rev233
```
Get Rev 233 stats for "Goblin" from osrs-wiki-rev233
```

### Test context7
```
Search for "NPC combat definitions" using context7
```

### Test web-search
```
Search web for "OSRS Revision 233 changelog"
```

---

## 🚨 Troubleshooting

### "Command not found: bun"

**Fix:** Install Bun
```bash
curl -fsSL https://bun.sh/install | bash
```

### "Command not found: npx"

**Fix:** Install Node.js
```bash
# Windows: Download from nodejs.org
# macOS: brew install node
# Linux: sudo apt install nodejs npm
```

### "Module not found" errors

**Fix:** Install local MCPs
```bash
./scripts/install-mcps.sh
```

### "BRAVE_API_KEY not set"

**Fix:** Set the environment variable (see above)

### MCP not showing in IDE

**Fix:** 
1. Check IDE's MCP config location
2. Verify JSON syntax (no trailing commas)
3. Restart IDE completely
4. Check IDE's MCP logs

---

## 📁 Config Files Summary

| IDE | Config Location | File Name |
|-----|-----------------|-----------|
| Claude Code | Project root | `.mcp.json` |
| Kimi CLI | Project root | `.mcp.json` |
| AntiGravity | App data or workspace | `mcp.json` |
| Cursor | Project or global | `.cursor/mcp.json` |
| VS Code | Project | `.vscode/settings.json` |
| Windsurf | Project or global | `.windsurf/mcp.json` or `config.yaml` |

---

## 💡 Pro Tips

1. **Use full paths for AntiGravity** - It may not resolve relative paths
2. **Set env vars in IDE** - Some IDEs have their own environment settings
3. **Test one MCP at a time** - Easier to debug
4. **Check IDE logs** - Most IDEs have MCP error logs
5. **Use npx for external MCPs** - Auto-downloads latest version

---

## 🔗 Reference Files

- `.mcp.json` - Claude Code / Kimi CLI config
- `.mcp-ide-configs.json` - All IDE configs in one file
- `docs/MCP_GUIDE.md` - MCP usage guide
- `docs/MCP_DEPLOYMENT.md` - Deployment guide

---

**Need help?** Check your IDE's MCP documentation or ask in chat!

