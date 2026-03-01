# F2P Task Contract Review

**Date:** 2026-02-23  
**Status:** CONTRACT TEMPLATE CREATED

---

## Issue

255 F2P tasks missing explicit acceptance criteria (contract).

Per AGENTS.md Definition of Done, tasks must include:
1. Module path
2. Template reference
3. Details
4. Done when criteria
5. Build requirement
6. Test requirement

---

## Contract Template Created

**Location:** `.sisyphus/plans/F2P_TASK_CONTRACT_TEMPLATE.md`

Contains contract structure, examples by category, and DoD reference.

---

## Tasks Needing Contracts

| Category | Count | Example Tasks |
|----------|-------|---------------|
| World/Location | 15 | WORLD-LADDERS-F2P, WORLD-BANKS-F2P |
| NPC Combat | 11 | NPC-SKELETON-F2P, NPC-HILL-GIANT-F2P |
| Drop Tables | 13 | NPC-DROP-SKELETON, NPC-DROP-HILL-GIANT |
| Shops | 22 | SHOP-LUMBRIDGE-GEN, NPC-ELLIS |
| Quest Tests | 7 | QUEST-BOT-COOK, QUEST-BOT-SHEEP |
| Systems | 10 | SYSTEM-DEATH-RESPAWN, SYSTEM-TUTOR-F2P |
| Wave 3-5 | 167 | Various |
| **TOTAL** | **255** | All need updates |

---

## Notes Added

Contract requirement notes added to 6 representative tasks:
- NPC-SKELETON-F2P
- NPC-DROP-SKELETON
- SHOP-LUMBRIDGE-GEN
- QUEST-BOT-COOK
- SYSTEM-DEATH-RESPAWN
- AREA-COW-PEN

---

## Contract Format

```
Module: rsmod/content/<path>/
Template: Follow <reference>.kt
Details: <NPCs/items/locations>
Done when:
- <acceptance criterion 1>
- <acceptance criterion 2>
- <acceptance criterion 3>
- <acceptance criterion 4>
- Build passes for <module>
- Bot test verifies <functionality>
```

---

## Next Steps

As agents claim tasks, they must:
1. Review task description
2. Check contract template
3. Add proper Done when criteria
4. Include build/test requirements
5. Then implement

---

**Total: 255 tasks need contract updates**

