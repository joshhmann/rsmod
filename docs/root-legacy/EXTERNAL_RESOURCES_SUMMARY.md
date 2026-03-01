# External Resources Summary for RSMod v2 Development

**Date:** 2026-02-20

This document provides a curated list of external resources, primarily from rune-server.org, to help with RSMod v2 development.

---

## 🌟 Top Priority Resources

### 1. RSMod v2 + RuneLite Setup (Essential)
**Thread:** https://rune-server.org/threads/setup-rsmod-v2-openosrs-runelite-or-just-openosrs-runelite.701403/

**Why it's important:**
- Complete setup tutorial
- NGINX reverse proxy config
- HTTPS/SSL setup
- Multi-world architecture
- Cache and revision matching

**Must-read sections:**
- Prerequisites and cache archives
- Finding correct OpenOSRS version
- Setting up the game server
- Connecting RuneLite client

---

### 2. Packet Opcodes Guide (Essential)
**Thread:** https://rune-server.org/threads/how-to-figure-out-opcodes-for-server-client-and-client-server-for-osrs.693487/

**Why it's important:**
- Shows opcode changes across revisions
- How to find opcodes in client
- Server <-> Client packet structures

**Key tables:**
- Revision comparison (171 through 189+)
- Opcode progression for common packets

---

### 3. Alter + Devious Setup (Rev 223)
**Thread:** https://rune-server.org/threads/alter-devious-last-commit-before-melxin-223-server-client-setup.706953/

**Why it's important:**
- Rev 223 specific setup
- Private server plugin code
- RSA key configuration
- Trusted commit reference

**Important:** Use commit `63187b0d77dd2e9e0dacd57de0bb0c5397e23e9b`

---

## 📚 Protocol Documentation

### RSPS Wiki (Fandom)
**URL:** https://rsps.fandom.com/

**Key pages:**
- 317 Protocol: https://rsps.fandom.com/wiki/317_Protocol
- OSRS83 Protocol: https://rsps.fandom.com/wiki/OSRS83_Protocol

**Value:** Detailed packet structures, login protocol, player updating

### RuneWiki
**URL:** https://www.runewiki.org/

**Value:** Old but gold documentation from Graham and blakeman8192 (2009-2010)

---

## 🛠️ Cache & Client Archives

### Runestats Archive
**URL:** https://archive.runestats.com/osrs/
**Maintainer:** @Polar

**Contents:**
- OSRS caches by revision
- XTEA keys
- Gamepacks

### OpenRS2 Archive
**URL:** https://archive.openrs2.org/
**Maintainer:** @Graham

**Contents:**
- Alternative cache source
- Multiple revision support
- Historical archives

---

## 💻 Development Tools

### IntelliJ IDEA
**Recommended Version:** 2023.2.6
**Download:** https://download.jetbrains.com/idea/ideaIU-2023.2.6.exe

**Why this version?**
- Avoids Kotlin script issues in newer versions
- Stable Gradle integration
- Community tested for RSPS

### Client Options

| Client | Use Case | URL |
|--------|----------|-----|
| OpenOSRS | Most popular | https://github.com/open-osrs/runelite |
| Devious | Rev 223 support | https://github.com/jbx5/devious-client |
| RuneLite | Official (needs patching) | https://github.com/runelite/runelite |

---

## 📝 Code Repository References

### RSMod Official
- **GitHub:** https://github.com/rsmod
- **Main Repo:** https://github.com/rsmod/rsmod

### RSMod v2 Revisions
| Revision | URL | Status |
|----------|-----|--------|
| 194 | https://github.com/rsmod/rsmod | Official |
| 199 | Community fork | Available |
| 200 | Community fork | Partial |
| 201 | Dodian fork | Documented |
| 202 | Community | Planned |

### Alternative Bases
| Base | Language | Notes |
|------|----------|-------|
| Kronos | Java | Rev 184, well-documented |
| Alter | Kotlin | Rev 228, RSMod v1 based |
| OS-Scape | Java | Popular choice |

---

## 🤔 Reality Check: Can You Actually Build This?

### Common Concern
> "Don't I need official server packets to implement features?"

### Answer
**NO.** You have everything needed for content development.

**Documentation:**
- `docs/IMPLEMENTATION_REALITY_CHECK.md` - Detailed breakdown
- `docs/WHAT_YOU_CAN_BUILD.md` - Feature checklist

**The Truth:**
- RSMod engine handles all networking/packets ✅
- OSRS Wiki has accurate formulas ✅
- Cache symbols map names to IDs ✅
- You only write content logic (skills, NPCs, quests)

**Example:**
```kotlin
// You write this:
onOpLoc1(content.tree) {
    if (player.woodcuttingLvl >= 1) {
        giveLogs()
        grantXp(25.0)
    }
}

// RSMod handles:
// - Receiving click packet
// - Pathfinding to tree
// - Animation playback
// - Inventory updates
// - XP drops
```

---

## 🛠️ MCP OSRS Server (NEW!)

### What Is It?
An MCP (Model Context Protocol) server that gives **instant access** to OSRS game data.

**Installed:** `mcp-osrs/`  
**Source:** https://github.com/jayarrowz/mcp-osrs

### Available Tools
| Tool | Purpose | Records |
|------|---------|---------|
| `search_objtypes` | Find items | 30,706 |
| `search_npctypes` | Find NPCs | 14,179 |
| `search_seqtypes` | Find animations | 12,087 |
| `search_loctypes` | Find world objects | ~40,000 |
| `search_iftypes` | Find interfaces | ~15,000 |
| `osrs_wiki_search` | Search OSRS Wiki | Live |

### Example Usage
```javascript
// Find dragon claws ID
search_objtypes({ query: "dragon claws" })
→ ID: 13652

// Find goblin NPC ID  
search_npctypes({ query: "goblin" })
→ ID: 3078

// Find attack animation
search_seqtypes({ query: "goblin attack" })
→ ID: 6184
```

**Why use it:** Instant lookups without manual file searching!

---

## 🔬 Advanced Topics

### True OSRS Emulation (CRITICAL READ)
**Thread:** https://rune-server.org/threads/so-you-think-your-server-is-an-emulation.706150/

**Why it's essential:**
Most private servers get these mechanics wrong:
- **OP vs AP interactions** - Operable vs Approachable distance checks
- **Line of Walk vs Line of Sight** - Different collision requirements  
- **AP to OP Switching** - How melee combat actually works
- **Dynamic AP Range** - `p_aprange` command for content scripts

**Impact on gameplay:**
- Combat feel (especially ranged/magic)
- NPC interaction (bankers behind counters)
- Pathfinding behavior
- Anti-cheat validity

**Source:** 2004scape project (Lost City) - meticulously reverse-engineered

**Documentation:** See `docs/TRUE_OSRS_EMULATION.md`

---

## 🎓 Learning Path

### Beginner (Week 1-2)
1. **Setup:** Follow RSMod + RuneLite tutorial
2. **Explore:** Look at existing plugins (woodcutting, mining)
3. **Experiment:** Create simple ::commands
4. **Study:** Packet structures and login protocol

### Intermediate (Week 3-4)
1. **Implement:** Simple skills (firemaking, cooking)
2. **Learn:** Player updating mechanism
3. **Create:** NPC combat AI
4. **Debug:** Use RSProx for packet inspection

### Advanced (Month 2+)
1. **Optimize:** Performance tuning
2. **Secure:** Anti-cheat implementation
3. **Scale:** Multi-world setup
4. **Customize:** Client modifications

---

## 🔍 Finding Information

### Search Strategies

**On Rune-Server.org:**
1. Use specific revision numbers: "rev 201", "rsmod v2"
2. Check "Informative Threads" section
3. Look for stickied threads in RSMod subforum
4. Use advanced search with date filters

**On GitHub:**
1. Search by commit message: "Update 202"
2. Look at forks for different revisions
3. Check issues for common problems
4. Browse pull requests for improvements

**General:**
1. Archive.org for dead links
2. Discord search for specific errors
3. Cache viewer tools for game data

---

## 🤝 Community Resources

### Discord Servers
- **RSMod Discord:** #free-help channel
- **Rune-Server Discord:** General RSPS help
- **OpenOSRS Discord:** Client-specific

### Active Contributors
| Username | Contribution |
|----------|--------------|
| @Polar | Cache archives |
| @Graham | Cache archives, RuneWiki |
| @Tomm0017 | RSMod v2 creator |
| @Pazaz | Protocol documentation |

---

## ⚠️ Important Notes

### Security Warnings
1. **Never** use production RSMod v2 - not ready
2. **Never** expose game ports directly - use firewall
3. **Always** validate client input server-side
4. **Don't** hardcode credentials in repos

### Legal Considerations
1. Use clean-room implementations
2. Don't distribute Jagex assets
3. Keep server private/limited access
4. Respect intellectual property

---

## 📖 Recommended Reading Order

1. **Start here:** `docs/RUNE_SERVER_RESOURCES.md` (this collection)
2. **Setup:** Rune-Server RSMod + RuneLite thread
3. **Protocol:** RSPS Wiki 317 Protocol page
4. **Packets:** Rune-Server opcode finding thread
5. **Emulation:** True OSRS Emulation thread (OP/AP system)
6. **Advanced:** Alter + Devious setup thread

---

## 🔗 Quick Links Reference

### Documentation
```
RSPS Wiki:          https://rsps.fandom.com/
RuneWiki:           https://www.runewiki.org/
OSRS Wiki:          https://oldschool.runescape.wiki/
```

### Archives
```
Runestats:          https://archive.runestats.com/osrs/
OpenRS2:            https://archive.openrs2.org/
```

### Repositories
```
RSMod:              https://github.com/rsmod
OpenOSRS:           https://github.com/open-osrs/runelite
Devious:            https://github.com/jbx5/devious-client
```

### Forums
```
Rune-Server:        https://rune-server.org/
RSMod Section:      https://rune-server.org/forums/rsmod.82/
```

---

## 📊 Resource Quality Ratings

| Resource | Quality | Up-to-date | Value |
|----------|---------|------------|-------|
| RSMod + RuneLite Thread | ⭐⭐⭐⭐⭐ | ✅ | Essential |
| True OSRS Emulation Thread | ⭐⭐⭐⭐⭐ | ✅ | Essential |
| Packet Opcodes Thread | ⭐⭐⭐⭐⭐ | ⚠️ | Very High |
| RSPS Wiki | ⭐⭐⭐⭐ | ⚠️ | High |
| Alter Setup Thread | ⭐⭐⭐⭐ | ✅ | High |
| RuneWiki | ⭐⭐⭐ | ❌ | Historical |
| OpenRS2 Archive | ⭐⭐⭐⭐⭐ | ✅ | Essential |
| Runestats Archive | ⭐⭐⭐⭐⭐ | ✅ | Essential |

**Legend:**
- ✅ Current
- ⚠️ Partially outdated but still useful
- ❌ Outdated but historically valuable

---

## 🎯 Next Steps

1. **Bookmark** the essential threads
2. **Download** cache for your target revision
3. **Setup** RSMod v2 with RuneLite following the tutorial
4. **Join** Discord communities for help
5. **Read** the protocol documentation
6. **Start** implementing simple features

---

*This summary is a living document. Update it as you discover new resources.*

