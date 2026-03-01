# MCP Server Guide - Clear Names & Usage

**Last Updated:** 2026-02-21

> Operational note: for task execution, follow `README.md`/`AGENTS.md` policy.
> `osrs-cache` is the primary data MCP; do not block active tasks on `osrs-wiki-rev233` availability.

---

## 🎯 Quick Reference

| MCP Name | Folder | Purpose | Icon |
|----------|--------|---------|------|
| **rsmod-game** | `mcp/` | 🎮 Control RSMod game server | 🎮 |
| **osrs-cache** | `mcp-osrs/` | 📦 Look up item/NPC IDs | 📦 |
| **osrs-wiki-rev233** | `mcp-osrs-rev233/` | 📜 Historical wiki data aligned to OpenRS2 `runescape/2293` (build 233, 2025-09-10) | 📜 |

---

## 🎮 rsmod-game

**Location:** `mcp/` folder  
**Language:** Bun/TypeScript  
**What it does:** Controls the RSMod v2 private server and bots

### When to Use
- Execute bot scripts
- Get player state (position, inventory, skills)
- Send actions (walk, attack, interact)
- Build RSMod modules
- Check server status

### Example
```typescript
// Make bot chop a tree
execute_script({
  player: "Kimi",
  code: "await bot.chopTree(10820, 3223, 3219);"
})

// Check player state
get_state({ player: "Kimi" })

// Build smithing module
build_server({ module: "smithing" })
```

### Key Tools
| Tool | Description |
|------|-------------|
| `execute_script` | Run bot code |
| `get_state` | Get player info |
| `send_action` | Walk/interact |
| `build_server` | Compile modules |
| `server_status` | Check if up |

---

## 📦 osrs-cache

**Location:** `mcp-osrs/` folder  
**Language:** Node.js/TypeScript  
**What it does:** Looks up IDs from OSRS cache files

### When to Use
- Find item ID by name
- Find NPC ID by name
- Find animation ID
- Look up object/scenery IDs

### Example
```typescript
// Find item ID
getItem({ name: "Bronze bar" })
→ { id: 2349, name: "Bronze bar" }

// Find NPC ID
getNpc({ name: "Goblin" })
→ { id: 3078, name: "Goblin" }

// Find animation
getSeqType({ name: "Chop" })
→ { id: 879 }
```

### Key Tools
| Tool | Description |
|------|-------------|
| `getItem` | Item lookup |
| `getNpc` | NPC lookup |
| `getSeqType` | Animation lookup |
| `getObjType` | Object/scenery lookup |

---

## 📜 osrs-wiki-rev233

**Location:** `mcp-osrs-rev233/` folder  
**Language:** Python  
**What it does:** Scrapes OSRS Wiki from a revision-locked historical point aligned to OpenRS2 `runescape/2293` (build `233`, built `2025-09-10`)

### When to Use
- Get historical NPC stats
- Check elemental weakness (Project Rebalance)
- Check ranged defense type
- Verify Varlamore Part 2 content
- Get drop tables from the revision-locked date window

### Example
```python
# Get NPC stats from Rev 233
get_npc_rev233({ name: "Abyssal Sire" })
→ { combat_stats: {...}, elemental_weakness: {...} }

# Check if Varlamore 2 content
check_varlamore2({ name: "Colossal Wyrm" })
→ { is_varlamore2_content: true }

# Get elemental weakness
get_elemental_weakness({ name: "Abyssal Sire" })
→ "Fire weakness (40%)"
```

### Key Tools
| Tool | Description |
|------|-------------|
| `get_npc_rev233_openrs2_2293` | NPC stats (canonical rev233/openrs2-2293 name) |
| `get_item_rev233_openrs2_2293` | Item data (canonical rev233/openrs2-2293 name) |
| `check_varlamore2_rev233_openrs2_2293` | Varlamore2-era check (canonical name) |
| `get_npc_rev233` / `get_item_rev233` / `check_varlamore2` | Legacy aliases (still supported) |
| `get_elemental_weakness` | Elemental weakness info |
| `get_ranged_defense` | Ranged defense type |

---

## 🗺️ Decision Flowchart

```
What do you need?
│
├─► Control a bot in-game?
│   └─► Use 🎮 rsmod-game
│
├─► Find an ID number?
│   └─► Use 📦 osrs-cache
│
├─► Get historical stats for rev 233?
│   └─► Use 📜 osrs-wiki-rev233
│
└─► Check if content is Varlamore 2?
    └─► Use 📜 osrs-wiki-rev233
```

---

## 📊 Comparison Table

| Feature | rsmod-game | osrs-cache | osrs-wiki-rev233 |
|---------|------------|------------|------------------|
| **Controls bots** | ✅ Yes | ❌ No | ❌ No |
| **Reads game state** | ✅ Yes | ❌ No | ❌ No |
| **Finds IDs** | ❌ No | ✅ Yes | ✅ Yes |
| **Live game data** | ✅ Yes | ✅ Yes | ❌ No |
| **Historical data** | ❌ No | ❌ No | ✅ Yes |
| **Revision-locked historical snapshot** | ❌ No | ❌ No | ✅ Yes |
| **Needs RSMod running** | ✅ Yes | ❌ No | ❌ No |
| **Needs internet** | ❌ No | ❌ No | ✅ Yes |

---

## 🔧 File Locations

```
OSRS-PS-DEV/
├── .mcp.json                    # MCP configuration
├── mcp/                         # 🎮 rsmod-game
│   ├── server-enhanced.ts       # Main entry
│   ├── bot-api.ts               # Bot actions
│   ├── sdk-api.ts               # SDK wrapper
│   └── bridge-client.ts         # WebSocket client
├── mcp-osrs/                    # 📦 osrs-cache
│   ├── dist/index.js            # Compiled entry
│   ├── index.ts                 # Source
│   └── data/                    # Cache data files
├── mcp-osrs-rev233/             # 📜 osrs-wiki-rev233
│   ├── server.py                # Main entry
│   └── requirements.txt         # Python deps
└── docs/
    └── MCP_GUIDE.md             # This file
```

---

## 🚀 Quick Start Commands

### Test rsmod-game
```bash
cd mcp && bun server-enhanced.ts
# Or with Claude: execute_script({player: "Kimi", code: "sdk.getState()"})
```

### Test osrs-cache
```bash
cd mcp-osrs && npm start
# Or with Claude: getItem({name: "Bronze bar"})
```

### Test osrs-wiki-rev233
```bash
cd mcp-osrs-rev233 && pip install -r requirements.txt && python server.py
# Or with Claude: get_npc_rev233({name: "Abyssal Sire"})
```

Offline mode (no wiki network):
```bash
set REV233_MCP_OFFLINE_ONLY=1
python mcp-osrs-rev233/server.py
```
In offline mode, tools return local symbol-backed fallback payloads using rev233/openrs2-2293 IDs.

---

## ❓ FAQ

**Q: Why 3 separate MCPs?**  
A: Each does a different job - game control, ID lookup, and historical data.

**Q: Which one should I use for finding item IDs?**  
A: 📦 osrs-cache - it's fastest and uses local cache files.

**Q: Which one for testing bot scripts?**  
A: 🎮 rsmod-game - it actually controls the bot in-game.

**Q: Which one for accurate Rev 233 NPC stats?**  
A: 📜 osrs-wiki-rev233 - use it with `osrs-cache`; rev source anchor is OpenRS2 `runescape/2293` (build 233, 2025-09-10).

**Q: Can I use multiple MCPs at once?**  
A: Yes! Use 📦 osrs-cache to find IDs, then 🎮 rsmod-game to test them.

---

## 📞 Troubleshooting

| Problem | Solution |
|---------|----------|
| rsmod-game not connecting | Check RSMod server is running on port 43595 |
| osrs-cache not found | Run `npm install` in mcp-osrs/ folder |
| osrs-wiki-rev233 fails | Run `pip install requests` |
| MCP not showing in Claude | Restart Claude Code after .mcp.json changes |

---

**Remember:**
- 🎮 **rsmod-game** = "The game controller"  
- 📦 **osrs-cache** = "The ID encyclopedia"  
- 📜 **osrs-wiki-rev233** = "The time machine"

