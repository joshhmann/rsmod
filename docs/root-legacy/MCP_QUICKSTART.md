# 🚀 MCP Quickstart Card

**One-page reference for using our MCP servers**

> Operational policy: for active task execution, follow `README.md` and `AGENTS.md`.
> `osrs-cache` is primary; do not block task progress on `osrs-wiki-rev233`.

---

## 🎯 What Are These?

Three MCP servers for OSRS development:

| MCP | Icon | Does What? | When to Use |
|-----|------|------------|-------------|
| **rsmod-game** | 🎮 | Controls bots in-game | Testing skills, running bots |
| **osrs-cache** | 📦 | Looks up item/NPC IDs | Finding ID numbers |
| **osrs-wiki-rev233** | 📜 | Historical wiki data | Accurate Rev 233 stats |

---

## ⚡ Install (One Command)

```bash
git clone <repo>
cd OSRS-PS-DEV
./scripts/install-mcps.sh
```

**Requires:** Bun, Node.js, Python (script checks for these)

---

## ✅ Verify Installation

```bash
./scripts/test-mcps.sh
```

Should show:
```
✅ rsmod-game: Dependencies installed
✅ osrs-cache: Dependencies installed
✅ osrs-wiki-rev233: Python dependencies installed
```

---

## 🎮 Use in Claude Code

Open this directory in Claude Code. MCPs auto-detect from `.mcp.json`.

### Example Commands

**Control a bot:**
```
execute_script on "Kimi" to chop trees
```

**Find an ID:**
```
Look up item "Bronze bar" in osrs-cache
```

**Get historical data:**
```
Get Rev 233 stats for "Abyssal Sire" from osrs-wiki-rev233
```

---

## 🔧 Troubleshooting

| Problem | Fix |
|---------|-----|
| "Bun not found" | Run: `curl -fsSL https://bun.sh/install \| bash` |
| "Node not found" | Install from https://nodejs.org/ |
| "Python not found" | Install from https://python.org/ |
| MCP not showing | Restart Claude Code |
| Tests fail | Run `./scripts/install-mcps.sh` again |

---

## 📁 File Locations

```
OSRS-PS-DEV/
├── mcp/              🎮 rsmod-game
├── mcp-osrs/         📦 osrs-cache
├── mcp-osrs-rev233/  📜 osrs-wiki-rev233
├── .mcp.json         MCP config (auto-detected)
└── scripts/
    ├── install-mcps.sh   Install everything
    └── test-mcps.sh      Health check
```

---

## 📚 Full Docs

- **Usage:** `docs/MCP_GUIDE.md`
- **Deployment:** `docs/MCP_DEPLOYMENT.md`
- **Test Plan:** `docs/testing/REV233_TEST_PLAN.md`

---

**That's it!** Install once, use forever. 🎉

