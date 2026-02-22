# SKILLS — Content Implementation Guide

**Owner**: Claude (content implementer)

## OVERVIEW

Skill plugins for RSMod v2. 10 complete skills (woodcutting, mining, fishing, cooking, firemaking, thieving, prayer, herblore, fletching, smithing).

## STRUCTURE

```
skill-name/
├── build.gradle.kts           # Module deps
├── src/main/kotlin/.../skill-name/
│   ├── SkillModule.kt         # PluginModule bindings
│   ├── configs/               # Data classes (rates, tools)
│   └── scripts/               # PluginScript handlers
└── src/integration/kotlin/    # GameTestExtension tests
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| Template | `woodcutting/` | Gold standard |
| Patterns | `docs/TRANSLATION_CHEATSHEET.md` | Alter → RSMod v2 |
| XP data | `wiki-data/skills/` | Oracle JSON files |

## CONVENTIONS

- **Plugin:** `class Skill @Inject constructor(...) : PluginScript()`
- **Handlers:** `onOpLoc1`, `onOpNpc1`, `onOpHeldU`, `onOpObj1`
- **Data:** Companion object or enum; large tables → `wiki-data/` JSON
- **Player actions:** Wrap in `ProtectedAccess` for safe inv ops

## ANTI-PATTERNS

| ❌ Don't | ✅ Do |
|----------|-------|
| Hardcode IDs | `find("sym_name")` from BaseObjs |
| No tests | Write `bots/<skill>.ts` |
| Edit `engine/` | Gemini owns — request via AGENTS.md |
| Skip wiki-data | Add XP rates, test locations |

## IMPLEMENTATION CHECKLIST

- [ ] Check `docs/CONTENT_AUDIT.md` — confirm not done
- [ ] Copy `woodcutting/` pattern
- [ ] Add `wiki-data/skills/<name>.json`
- [ ] Write `bots/<skill>.ts` test
- [ ] Run `:content:skills:<name>:build`
- [ ] Update CONTENT_AUDIT.md → ✅

## SKILL ARCHETYPES

| Type | Skills | Pattern |
|------|--------|---------|
| Gathering (Loc) | Woodcutting, Mining | `onOpLoc` → tick loop → depletion |
| Gathering (NPC) | Fishing | `onOpNpc` → tick loop |
| Processing (Loc) | Cooking | `onOpLocU` → delay → transform |
| Processing (Item) | Firemaking, Herblore | `onOpHeldU` → delay → transform |
| Interactive | Thieving | `onOpNpc` → delay → success roll |
| Consumption | Prayer | `onOpHeld` → immediate effect |

## BUILD

```bash
# Single module (10s)
gradlew.bat :content:skills:smithing:build -x test

# With tests
gradlew.bat :content:skills:smithing:test
```
