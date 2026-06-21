# Next Action

## Status After June 21 Orchestration Sweep

7 blocked cards processed. 6 already-implemented and closed. 1 has child card in todo.

### Closed as Already-Implemented (6)

| Card | Title | Result |
|:-----|:------|:-------|
| t_2d07b142 | Boat travel + Karamja | Full Karamja NPC module + GangplankTravel |
| t_ca6d87a0 | Magic utility spells | Teleports, enchant, non-combat all done |
| t_66b6219b | Magic utility (broad) | Same as above, QA review pending |
| t_0f3eb8b6 | Sheep Shearer quest | 3 files + build.gradle.kts |
| t_35d77cbe | X Marks the Spot | Full implementation |
| t_981e843b | Knight's Sword quest | 4 files, committed 0a855810 |

### Remaining Work (1 card)

| Card | Title | Size | Status | Action |
|:-----|:------|:----:|:------:|:-------|
| t_278afa1c | Ranged skill module (bow/arrow configs) | S | todo → ready soon | Dispatch with pre-flight |
| t_db911e58 | Ranged parent | L | blocked | Wait for child completion |
| t_cb1a7aba | QA Review: Ranged + Magic | M | todo | Dispatch after ranged child done |

### Recommended Next Action

`RS_DISPATCH_RANGED` — Dispatch child card t_278afa1c (ranged skill module files). S-sized, ~3 files, known pattern. Compile with:
```bash
./gradlew :content:skills:ranged:compileKotlin
```

After that completes, dispatch t_cb1a7aba (QA Review by Rei) to validate both ranged and magic utility.

Once all child cards complete, close parent t_db911e58.
