# Next Action

## Completed — v3.4.0 Task Sizing & Pre-Flight Enforcement

Full sizing policy deployed. 5 broad cards reclassified and blocked.

## Recommended Next Action

`RS_SALVAGE_DIALOGUE` — Re-dispatch the NPC dialogue card (t_b0f33a6a, currently ready) with:
1. Pre-flight: check what exists on CT 123
2. Module compile: `:content:generic:generic-npcs:compileKotlin`
3. Target: S-sized task, 20 max iterations

### What's Ready to Work

| Card | Size | Status | Action |
|:-----|:----:|:------:|:-------|
| t_b0f33a6a — Dialogue handlers | S | ready | Dispatch now |
| t_2d07b142 — Boat travel | XL | blocked | Decompose into 4 cards |
| t_db911e58 — Ranged skill | L | blocked | Spec-first |
| t_0f3eb8b6 — Sheep Shearer quest | XL | blocked | Quest spec card |
| t_35d77cbe — X Marks the Spot quest | XL | blocked | Quest spec card |
| t_981e843b — Knight's Sword quest | XL | blocked | Quest spec card |
| t_66b6219b — Magic Utility Spells | XL | blocked | Decompose into 4 cards |
