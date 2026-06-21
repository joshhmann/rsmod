# Generator Promotion Checklist

## Pre-Promotion Gate

Use this checklist before promoting any generated content to production.

### Target Definition
- [ ] Target NPCs / zone / system identified
- [ ] Scope is small and reversible
- [ ] No combat/aggression changes in scope (unless explicitly noted)

### Inspection
- [ ] Existing handler located and read
- [ ] Existing NPC symbols verified in production
- [ ] Existing item references (`objs.*` vs local refs) understood
- [ ] Module path and `build.gradle.kts` dependencies verified
- [ ] Existing handler registration confirmed in startup()

### Corpus Data Review
- [ ] Corpus `drops_by_source.json` queried for target
- [ ] All items resolved through shared resolver
- [ ] Resolver smoke test passed
- [ ] Unresolved items logged with reason
- [ ] Post-2013 items identified and filtered
- [ ] Contextual items (quest/wilderness/slayer/clue) identified and filtered
- [ ] Corpus "Always" entries validated (may be sub-table always, not guaranteed)

### Comparison
- [ ] Existing item count recorded
- [ ] Corpus item count recorded
- [ ] Resolved item count recorded
- [ ] Skipped item count recorded
- [ ] Classification assigned per target (PROMOTE / KEEP / STAGE / SKIP / REVIEW)

### Safety Filters
- [ ] No raw cache IDs in generated Kotlin
- [ ] All item references use `.sym` symbols
- [ ] No post-2013 items present in rev 233 cache but missing from gameplay
- [ ] No wilderness-only items in non-wilderness NPCs
- [ ] No quest-only items in generic drop tables
- [ ] No slayer-only items in non-slayer NPCs
- [ ] No clue scroll items unless explicitly supported
- [ ] No named/quest/regional NPC variant leakage
- [ ] No duplicate handlers created
- [ ] Existing production drops preserved where corpus is weaker

### Promotion
- [ ] Correct module path used
- [ ] Registration call added to startup() if new file
- [ ] Old inline function removed if refactoring
- [ ] Inline function call removed from startup()
- [ ] File follows established coding patterns (`dropTable {}` DSL, `NpcReferences`, `ObjReferences`)

### Validation
- [ ] Resolver smoke test (`resolver.resolve_item()` for all new items)
- [ ] Module compile: `./gradlew :content:other:npc-drops:compileKotlin`
- [ ] Full server compile: `./gradlew :server:app:compileKotlin`
- [ ] Raw cache ID scan (no 5-digit numbers except year comments and coin ranges)
- [ ] `git diff --stat` reviewed for unexpected changes
- [ ] `git status --short` reviewed for stray files

### Commit
- [ ] Focused commit message with scope prefix
- [ ] Commit includes summary of changes per NPC
- [ ] Commit includes skipped items and reasons
- [ ] Commit includes verification results

### Post-Commit
- [ ] Automation status matrix updated
- [ ] Zone readiness checklist updated (if applicable)
- [ ] Drop certification doc updated (if applicable)
- [ ] Known issues / skipped items documented
- [ ] Next recommended slice identified

## Quick Reference — Common Filter Rules

| Item Pattern | Rule | Reason |
|-------------|------|--------|
| `Clue scroll (*)` | SKIP | Post-2013, needs explicit handler support |
| `Key (*)` | SKIP | Post-2013 wilderness/chest content |
| `Looting bag` | SKIP | Wilderness-only |
| `Ensouled * head` | SKIP | Post-2015 Arceuus spellbook |
| `* champion scroll` | SKIP | Not in rev 233 cache |
| `Larran's key` | SKIP | Post-2016 wilderness |
| `Slayer's enchantment` | SKIP | Post-2016 wilderness |
| `Ecumenical key` | SKIP | Wilderness-only |
| `Shield left half` | SKIP | Post-2013 RDT expansion |
| `Loop half of key` | SKIP | Post-2013 RDT expansion |
| `Tooth half of key` | SKIP | Post-2013 RDT expansion |
| `Staff` (generic) | SKIP | Not in rev 233 cache (named staves only) |
| `Bear fur` | SKIP | Not in rev 233 cache |
| `Flyer` | SKIP | Not a valid generic drop |
| `Iron bolts` | SKIP | Not in rev 233 cache |
| `Bronze bolts` | SKIP | Not in rev 233 cache (`bolts` exists, not `bronze_bolts`) |
| `Blue wizard hat` | SKIP | Cache has `bluewizhat` not `blue_wizard_hat` |
| `Wizard hat` | SKIP | Cache has `blackwizhat` / `bluewizhat` |
| `Grimy marrentill` | SKIP | Not in rev 233 cache |
