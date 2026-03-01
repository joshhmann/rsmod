# 🆕 New MCP Additions - context7 & web-search

**Added 2026-02-21**

---

## 📚 context7 MCP

**Purpose:** Search indexed documentation and codebases

**What it does:**
- Searches technical documentation
- Indexes your codebase for semantic search
- Finds relevant code snippets and docs
- Great for finding implementation examples

**Use cases:**
```
"Find examples of woodcutting implementations in our codebase"
"Search docs for 'NPC combat definitions'"
"Show me how to implement elemental weakness"
```

**Install:** Already configured (uses npx)
```json
{
  "context7": {
    "command": "npx",
    "args": ["-y", "@upstash/context7-mcp@latest"]
  }
}
```

**No setup required** - auto-downloads on first use

---

## 🔍 web-search MCP

**Purpose:** Search the web using Brave Search API

**What it does:**
- Searches the live web
- Gets current information
- Finds OSRS wiki pages
- Researches game mechanics

**Use cases:**
```
"Search web for OSRS woodcutting guide"
"Find the OSRS wiki page for Smithing"
"Look up Rev 233 patch notes"
"Search for 'OSRS elemental weakness mechanics'"
```

**Install:** Configured but needs API key
```json
{
  "web-search": {
    "command": "npx",
    "args": ["-y", "@modelcontextprotocol/server-brave-search@latest"],
    "env": {
      "BRAVE_API_KEY": "BSABujJVbKHoFN0KrlBB4gaHew46aTs"
    }
  }
}
```

**Setup:**
1. Go to https://brave.com/search/api/
2. Sign up (free tier: 2000 queries/month)
3. Get API key
4. Set environment variable:
   ```bash
   # Windows
   setx BRAVE_API_KEY "your_key"
   
   # macOS/Linux
   export BRAVE_API_KEY="your_key"
   ```

---

## 🎯 Updated MCP List

| MCP                  | Icon | Type         | Needs Setup                   |
| -------------------- | ---- | ------------ | ----------------------------- |
| **rsmod-game**       | 🎮    | Local        | Run ./scripts/install-mcps.sh |
| **osrs-cache**       | 📦    | Local        | Run ./scripts/install-mcps.sh |
| **osrs-wiki-rev233** | 📜    | Local        | Run ./scripts/install-mcps.sh |
| **context7**         | 📚    | Remote (npx) | None - auto-downloads         |
| **web-search**       | 🔍    | Remote (npx) | Needs BRAVE_API_KEY           |

---

## 🔄 Updated .mcp.json

```json
{
  "mcpServers": {
    "rsmod-game": { ... },
    "osrs-cache": { ... },
    "osrs-wiki-rev233": { ... },
    "context7": { ... },          // ← NEW
    "web-search": { ... }         // ← NEW
  }
}
```

---

## 🔧 IDE Configuration

### For Claude Code / Kimi CLI
Already works! Just open the project.

### For AntiGravity
Copy from `.mcp-ide-configs.json` → `antigravity` section

**Key points for AntiGravity:**
1. Use **full absolute paths** (e.g., `Z:/Projects/OSRS-PS-DEV/mcp/server-enhanced.ts`)
2. Set `BRAVE_API_KEY` in your environment or config
3. Restart AntiGravity after config changes

---

## 💡 How to Use New MCPs

### context7 Examples

```
"Search our codebase for thieving implementation using context7"
"Find NPC combat definitions with context7"
"Show me examples of skill implementations using context7"
```

### web-search Examples

```
"Search web for OSRS woodcutting XP rates"
"Find OSRS wiki page for 'Elemental Weakness'"
"Look up Rev 233 Varlamore Part 2 release notes"
"Search for 'OSRS NPC drop tables'"
```

---

## ⚡ Quick Start

1. **For local MCPs (rsmod-game, osrs-cache, osrs-wiki-rev233):**
   ```bash
   ./scripts/install-mcps.sh
   ```

2. **For context7:**
   - Just use it! Auto-installs via npx

3. **For web-search:**
   - Get Brave API key: https://brave.com/search/api/
   - Set environment variable
   - Ready to use!

---

## 📊 Cost/Usage

| MCP              | Cost                          | Limits               |
| ---------------- | ----------------------------- | -------------------- |
| rsmod-game       | Free                          | Unlimited (local)    |
| osrs-cache       | Free                          | Unlimited (local)    |
| osrs-wiki-rev233 | Free                          | Unlimited (local)    |
| context7         | Free tier available           | Check Upstash limits |
| web-search       | Free tier: 2000 queries/month | Paid tiers available |

---

**Ready to search the web and your codebase!** 🚀

