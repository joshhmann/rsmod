# API — Public Interface Layer

## OVERVIEW
Public API for content developers. Type-safe abstractions over engine.  
**Owner: Gemini** — Coordinate before changes.

## STRUCTURE
```
api/
├── config/refs/        # BaseObjs, BaseNpcs, BaseSeqs (find("symbol"))
├── player/protect/     # ProtectedAccess facade
├── repo/               # LocRepository, NpcRepository, etc.
├── script/             # *ScriptEventExtensions.kt (event DSL)
├── testing/            # GameTestExtension (JUnit 5)
├── combat/             # Combat subsystem APIs
├── controller/         # Entity lifecycle controllers
├── type/               # Type definitions and extensions
└── [40+ modules]       # Modular API packages
```

## WHERE TO LOOK

| Component | Location | Purpose |
|-----------|----------|---------|
| Symbol refs | config/src/.../refs/BaseObjs.kt | `find("symbol_name")` for type-safe IDs |
| Player API | player/src/.../protect/ProtectedAccess.kt | Safe player actions facade |
| Event DSL | script/*ScriptEventExtensions.kt | onOpLoc, onOpNpc, onOpHeld handlers |
| Repositories | repo/src/.../repo/{loc,npc,obj,player}/ | Entity lookup patterns |
| Testing | testing/src/.../GameTestExtension.kt | Module-scoped JUnit 5 tests |

## CONVENTIONS
**Type References:**
```kotlin
// Use symbol-based find(), never raw IDs
val axe = objs.find("rune_axe")    // ✅ Good
val axe = find(1271)               // ✅ Also valid (obj sym name)
val axe = 1271                     // ❌ Bad - magic number

**Player Actions:**
```kotlin
// ProtectedAccess provides type-safe methods
suspend fun ProtectedAccess.gather(resource: LocType) {
    anim(tool.anim)                 // Play animation
    delay(3)                        // Wait ticks
    statAdvance(stats.woodcutting, xp)  // Grant XP
}
```

**Extensions:**
```kotlin
// Define extensions on unpacked types
val UnpackedObjType.axeWoodcuttingReq: Int by objParam(params.skill_requirement)
val UnpackedLocType.treeLevelReq: Int by locParam(params.tree_level_req)
```

## ⚠️ KNOWN ISSUES

**ProtectedAccess is a God Class** (~3600 lines, 200+ methods)
- Impact: High cognitive load, difficult to test
- Solution planned: Split into domain-specific facades
  - `MovementAccess` (walk, teleport)
  - `InventoryAccess` (add, delete, transfer)
  - `CombatAccess` (queue hits, modifiers)
  - `DialogueAccess` (ifChatNpc, ifMesbox, etc.)

## DO NOT TOUCH

| Path | Owner | Reason |
|------|-------|--------|
| `rsmod/engine/` | Gemini | Core engine |
| `rsmod/.data/symbols/` | Generated | Rev 228 symbol tables |

**When adding/changing API:**
1. Update `docs/TRANSLATION_CHEATSHEET.md`
2. Notify Claude (content depends on it)
3. Add tests in `api/testing/`
