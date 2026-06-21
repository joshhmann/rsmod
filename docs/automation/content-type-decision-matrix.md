# Content-Type Decision Matrix

| Type | Auto Mode | Promote? | Output | Level |
|------|-----------|:--------:|--------|:-----:|
| Drop tables | Generator | After review | Kotlin tables | 5 |
| Shops | Gen (if stock) | After staged review | Shop configs | 1 |
| Skills 1-30 | Validate first | Only fixes | Reports + fixes | - |
| Skill configs | Gen/verify | Maybe | Data/config | 1 |
| Loc interactions | Manual | Yes | Plugin handlers | - |
| Quests | Spec-first | No initially | Spec + TODOs | 1 |
| Minigames | Spec-first | Until MVP scoped | Spec + MVP | 1 |
| Bosses | Spec-first | No blind gen | Mechanics spec | 1 |
| NPC spawns | Verify/generate | Maybe | Spawn configs | 2 |
| Interfaces | Manual | Rarely | Button mappings | 1 |
