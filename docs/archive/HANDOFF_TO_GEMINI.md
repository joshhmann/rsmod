# 🤝 Handoff: Gemini Implementation Plan

**From:** Claude (Content Implementer + Tester)  
**To:** Gemini (Engine/Infrastructure Lead)  
**Date:** 2026-02-21  
**Subject:** Rev 233 Implementation Coordination

---

## 📦 What We've Built (Ready for You)

### 1. Testing Infrastructure ✅
- **Test Suite:** `bots/rev233_tester.ts` - 220 automated tests
- **Test Plan:** `docs/testing/REV233_TEST_PLAN.md` - Complete test matrix
- **Skill Learnings:** `docs/bot-patterns/*.md` - 14 guides with coords

### 2. MCP Framework ✅
- **Enhanced MCP:** `mcp/server-enhanced.ts` - Resources, cancellation
- **BotActions API:** `bots/lib/bot-actions.ts` - High-level porcelain
- **Script Runner:** `bots/lib/runner.ts` - Timeout, retry, waitFor

### 3. Reference Data ✅
- **rs-sdk:** Full extracted - skill patterns, coordinates, code
- **rsinf_233:** Deobfuscated client - reference for mechanics
- **Cache:** Rev 233 downloaded and ready

---

## 🔧 Your Domain (Engine/Infrastructure)

### What You Own
- `rsmod/engine/` - Core engine
- `rsmod/api/` - Game APIs
- `rsmod/server/` - Server infrastructure
- Build system, performance, stability

### What We Need From You

#### 1. Server Stability
```
Current Issue: Server startup cache loading
Status: Need restart to pick up NPC edits (NpcEdits.kt)
Need: Hot-reload or faster restart
```

#### 2. Missing APIs for Skills
Check what skills need these:
- [ ] **Dialog system** - For shops, quests, banking
- [ ] **Shop interface** - Buy/sell items
- [ ] **Bank interface** - Full bank UI
- [ ] **Equipment bonuses** - Stat calculations
- [ ] **Prayer drain** - Tick-based drain
- [ ] **Magic spellbook** - Casting system
- [ ] **Ranged ammo** - Arrow consumption

#### 3. NPC Combat Definitions
Need combat stats for:
- [ ] All Lumbridge NPCs (goblins, cows, chickens, etc.)
- [ ] All thievable NPCs (farmers, HAM, warriors, etc.)
- [ ] All attackable NPCs

**Source:** `Kronos-184-Fixed/Kronos-master/.../data/npcs/combat/`
**Format:** Port to RSMod v2

#### 4. Object Definitions
Need interaction configs for:
- [ ] All doors/gates (open/close)
- [ ] All ladders/stairs (climb)
- [ ] All crafting stations (furnace, anvil, range)
- [ ] All resource nodes (detailed ore/tree/fish data)

---

## 🎯 Coordination Protocol

### How We'll Work Together

#### When Implementing a Skill:

**Gemini (You):**
1. Check if engine/API supports needed features
2. Add any missing core functionality
3. Document new APIs in `docs/TRANSLATION_CHEATSHEET.md`
4. Notify Claude when ready

**Claude (Me):**
1. Wait for your API confirmation
2. Implement content using your APIs
3. Write bot tests
4. Update `docs/CONTENT_AUDIT.md`
5. Report back with test results

### Communication Pattern
```
Gemini: "Smithing API ready - anvils supported, see anvil.kt example"
Claude: "Implementing smithing..."
Claude: "Smithing complete - 42 items, tested with bot"
Gemini: "Reviewed - build passes, merging"
```

---

## 📋 Implementation Plan Template

Fill this out: `docs/IMPLEMENTATION_PLAN.md`

### Phase 1: Foundation (Week 1)
```
Gemini Tasks:
- [ ] Fix server hot-reload
- [ ] Add dialog system API
- [ ] Add shop interface API
- [ ] Add bank interface API
- [ ] Port NPC combat definitions (Lumbridge area)

Claude Tasks:
- [ ] Test current implementation with tester
- [ ] Document gaps
- [ ] Prepare wiki data for remaining skills
```

### Phase 2: Core Skills (Week 2-3)
```
Gemini:
- [ ] Smithing support (anvils, furnaces)
- [ ] Crafting support (leather, gems)
- [ ] Fletching support (bows, arrows)

Claude:
- [ ] Implement smithing content
- [ ] Implement crafting content  
- [ ] Implement fletching content
```

### Phase 3: Advanced Skills (Week 4-5)
```
Gemini:
- [ ] Magic spellbook system
- [ ] Prayer drain system
- [ ] Equipment bonus calculations

Claude:
- [ ] Implement magic
- [ ] Implement prayer
- [ ] Implement herblore
```

### Phase 4: Polish (Week 6)
```
Both:
- [ ] Run full test suite
- [ ] Fix remaining bugs
- [ ] Performance optimization
- [ ] Documentation
```

---

## 🔗 Integration Points

### AgentBridge (Port 43595)
```kotlin
// Your side: Server sends state
// Our side: MCP reads state, sends actions

// State format:
{
  player: {
    name: string,
    position: {x, z, plane},
    skills: {woodcutting: {level, xp}, ...},
    inventory: [...],
    nearbyNpcs: [...],
    nearbyLocs: [...]
  }
}
```

### MCP Protocol
```typescript
// Our MCP calls your server via AgentBridge
// execute_script -> bridgeClient.sendAction()
```

### Build System
```bash
# Single module builds (fast)
cd rsmod && gradlew :content:skills:smithing:build

# Full build (slow)
cd rsmod && gradlew build
```

---

## 📚 Documentation Standards

### For New APIs You Add:
```kotlin
/**
 * Opens shop interface for player
 * @param npcId The shopkeeper NPC ID
 * @param shopInventory List of shop items with prices
 * 
 * Usage:
 * ```kotlin
 * openShop(player, npcs.shopkeeper_lumbridge, LumbridgeGeneralShop)
 * ```
 */
fun openShop(player: Player, npcId: Int, shop: ShopDef)
```

### For New Content I Add:
```kotlin
// Reference to wiki data
// wiki-data/skills/smithing.json
// wiki-data/monsters/goblin.json
```

---

## 🧪 Testing Requirements

### Before Marking Complete:
- [ ] Bot test passes (10 iterations)
- [ ] XP rate matches wiki exactly
- [ ] No console errors
- [ ] Build passes
- [ ] Integration test with MCP

### Test Command:
```bash
bun bots/rev233_tester.ts Kimi
```

---

## 🚨 Known Blockers

### Current Issues Needing Your Help:

1. **NPC Cache Edits Don't Hot-Reload**
   - Location: `rsmod/api/config/editors/NpcEdits.kt`
   - Issue: Changes require server restart
   - Need: Cache reload on file change OR faster restart

2. **Pickpocket Menu Missing**
   - Root: Cache doesn't have "Pickpocket" for Man/Woman
   - Fix: `NpcEdits.kt` sets `op2 = "Pickpocket"`
   - Status: Works via bot (packet), not via client menu

3. **Missing Dialog System**
   - Need: Multi-step dialog with choices
   - Use: Shops, quests, banking
   - Current: No dialog support visible

4. **Missing Shop Interface**
   - Need: Buy/sell with stock tracking
   - Current: No shop UI

---

## 📊 Success Metrics

### We'll Know We're Done When:
- [ ] 220 automated tests pass
- [ ] All 23 skills functional
- [ ] All Lumbridge NPCs combat-ready
- [ ] All doors/ladders work
- [ ] Bot can achieve all 99s
- [ ] Wiki XP rates match exactly
- [ ] No game-breaking bugs

---

## 🎮 Demo Goal

**"AI Agents Playing OSRS"**
- Kimi (you) creates infrastructure
- Claude (me) creates content
- Bots train skills autonomously
- Full Rev 233 implementation

---

## 📞 Questions?

**Ask in:**
- `docs/TRANSLATION_CHEATSHEET.md` - API mapping
- `docs/CONTENT_AUDIT.md` - What's done
- `docs/agent-notes/gemini.md` - Your notes

**Emergency:**
- Server won't start: Run `scripts/diagnose.bat`
- Build fails: Check `rsmod/.data/cache/` exists
- Cache issues: `gradlew downloadCache`

---

**Ready to build the ultimate OSRS server? Let's do this!** 🚀

---

## ✅ Action Items for You

- [ ] Read `docs/IMPLEMENTATION_PLAN_TEMPLATE.md`
- [ ] Fill in current status of each skill
- [ ] Identify missing engine features
- [ ] Prioritize Phase 1 tasks
- [ ] Set up your dev environment
- [ ] Create first engine PR

**Let's ship this thing!** 💪

