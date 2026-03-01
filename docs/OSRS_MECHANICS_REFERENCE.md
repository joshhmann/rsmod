# OSRS Mechanics Reference

> Wiki-accurate formulas and quirks for every core system.
> Source: OSRS Wiki (Rev 233 snapshot, Jan 2025 engine changes included).
> Use this to verify implementations — private servers commonly get several of these wrong.

---

## 1. Run Energy

### Storage
Energy is stored as **0–10,000 units** internally. The client displays it as 0–100% (floor division by 100).

### Drain formula (updated Jan 8 2025)
```
UnitsLost per tick = (60 + (67 × Weight / 64)) × (1 - Agility / 300)
```
- **Weight** = total carried weight in kg (inventory + worn items combined)
- **Agility** = current agility level (not boosted)
- At 0 kg: drains in 170 ticks (lvl 1 agility) to 250 ticks (lvl 99 agility)
- Minimum drain: clamped so you can't go negative per tick

### Recovery formula (updated Jan 8 2025)
```
UnitsRecovered per tick = (Agility / 10) + 15
```
- **Old formula (before Jan 2025):** `(Agility / 6) + 8` — do NOT use this
- Only recovers when **not running** (standing or walking)
- Does NOT recover during most skilling actions

### Graceful outfit bonus
- Full graceful set: energy regeneration rate multiplied (exact multiplier: `× 1.3` per wiki)
- Each piece gives a partial bonus — full set required for the 30% bonus

### Stamina potion
- Reduces drain rate by **70%** for **2 minutes** (200 ticks) per dose
- Additionally restores **20% run energy** per dose

### Energy potion
- Restores **10% run energy** per dose (instantly)
- Super energy potion: **20% per dose**

### Quirks
- Weight of 0 kg or less clamps to 0 kg for drain calculation (negative weight is possible with graceful but floors at 0)
- On the tick energy hits 0: player only loses their remaining energy (no "overshoot" past 0)

---

## 2. Food & Eating

### Eat delay
- Standard food: **3-tick delay** before eating again (1.8 seconds)
- Combo food (Karambwan): **2-tick delay**, can be eaten immediately after any non-combo food on the SAME tick

### How combo eating works
1. Player eats regular food (e.g., shark) — sets 3-tick food timer
2. On the SAME tick: player also eats Karambwan — Karambwan has its own 2-tick timer
3. Both heals apply in the same tick
4. You cannot eat two regular foods in the same tick (timer blocks it)

### Heal values (F2P foods)
| Food | Heal (HP) | Notes |
|------|-----------|-------|
| Shrimps | 3 | |
| Sardine | 4 | |
| Cooked chicken | 4 | |
| Cooked meat | 4 | |
| Bread | 5 | |
| Herring | 5 | |
| Mackerel | 6 | |
| Trout | 7 | |
| Cod | 7 | |
| Pike | 8 | |
| Salmon | 9 | |
| Tuna | 10 | |
| Jug of wine | 11 HP, -2 Attack | Drains attack temporarily |
| Lobster | 12 | |
| Bass | 13 | |
| Swordfish | 14 | |
| Monkfish | 16 | Members |
| Shark | 20 | Members |
| Manta ray | 22 | Members |
| Dark crab | 22 | Members |
| Plain pizza (half) | 7 each half = 14 total | 2 bites |
| Meat pizza (half) | 8 each half = 16 total | 2 bites |
| Anchovy pizza (half) | 9 each half = 18 total | 2 bites |
| Meat pie (half) | 6 each half = 12 total | Pie dish remains |
| Apple pie (half) | 7 each half = 14 total | Pie dish remains |
| Garden pie (half) | 6 each half = 12 total | Pie dish remains; +3 Farming |

### Anglerfish (overheal)
```
Immediate heal:
  base = floor(HitpointsLevel / 10)
  extra = base_level range → 2, 4, 6, 8, or 13
  total_immediate = base + extra

Delayed heal (7 ticks / 4.2 seconds later):
  +10 run energy (NOT HP)
```
Overheal can raise HP **above base level** — use `statBoost(stats.hitpoints, amount, 0)` not `statHeal`.
Second anglerfish cancels the delayed effect of the first (only latest delayed heal fires).

### Container items
| Food | What returns |
|------|-------------|
| Any pie | Pie dish (empty) |
| Bowl foods | Empty bowl |
| Potions | Vial (on last dose) |
| Pizza | Nothing — consumed completely |
| Most fish/bread | Nothing |

### Quirks
- Eating blocks attacking for **3 ticks** (same as eat delay) — implement as shared timer
- Karambwan eating sets a separate 2-tick combo timer, does NOT share the main food timer
- Jug of wine: -2 Attack is a temporary drain using `statDrain` — not a permanent loss

---

## 3. Potions

### Boost formula structure
```
Boost = constant + floor(level × percent / 100)
```
Applied via `statBoost(stat, constant, percent)` — capped at base + boost.

### F2P potion reference
| Potion | Stat | Constant | Percent | Notes |
|--------|------|---------|---------|-------|
| Attack potion | Attack | 3 | 10% | |
| Strength potion | Strength | 3 | 10% | |
| Defence potion | Defence | 3 | 10% | |
| Prayer potion | Prayer | 7 | 25% | RESTORE, not boost — use `statHeal` |
| Super attack | Attack | 5 | 15% | |
| Super strength | Strength | 5 | 15% | |
| Super defence | Defence | 5 | 15% | |
| Super restore | All drained stats | 8 | 25% | Restores all stats including prayer |
| Antipoison | — | — | — | Cures poison; immunity varies by tier |
| Energy potion | Run energy | +10% absolute | — | Not a stat boost |
| Ranging potion | Ranged | 4 | 10% | |
| Magic potion | Magic | 4 | 0 | Flat +4, no percentage |

### Boost decay
- Boosts decay by **1 level per minute** (100 ticks)
- Preserve prayer: **1 level per 90 seconds** (150 ticks) — requires 55 Prayer
- Decay happens to ALL active boosts simultaneously (not reset by drinking again)
- Re-drinking a potion refreshes the boost to max and resets decay to 0

### Dose containers
- 4-dose → 3-dose → 2-dose → 1-dose → empty vial (all via `invReplace`)
- Empty vials stack in inventory

### Potion timing
- **3-tick cooldown** shared between most potions (prevents drinking two in same window)
- Karambwan (combo food) ALSO sets a 3-tick potion cooldown after being eaten
- Eating Karambwan blocks potion use for 3 ticks (and vice versa)

---

## 4. Prayer

### Drain system (tick-based accumulator)
Each active prayer contributes its **drain effect** value per tick to an internal counter.
When the counter reaches **drain resistance**, one prayer point is deducted.

```
Drain Resistance = 60 + (2 × Prayer Bonus)
```

- **Prayer Bonus** = total prayer bonus from equipped items (armor, holy symbol, etc.)
- **+1 prayer bonus** → resistance +2 → slightly longer between deductions
- At +0 bonus: resistance = 60
- At +30 bonus: resistance = 120 (double duration — 2× prayer points last as long)

### Drain rates per prayer
| Prayer | Level | Drain effect per tick |
|--------|-------|-----------------------|
| Thick Skin | 1 | 1 |
| Burst of Strength | 4 | 1 |
| Clarity of Thought | 7 | 1 |
| Sharp Eye | 8 | 1 |
| Mystic Will | 9 | 1 |
| Rock Skin | 10 | 2 |
| Superhuman Strength | 13 | 2 |
| Improved Reflexes | 16 | 2 |
| Rapid Restore | 19 | 1 |
| Rapid Heal | 22 | 2 |
| Protect Item | 25 | 2 |
| Hawk Eye | 26 | 2 |
| Mystic Lore | 27 | 2 |
| Steel Skin | 28 | 4 |
| Ultimate Strength | 31 | 4 |
| Incredible Reflexes | 34 | 4 |
| Protect from Magic | 37 | 4 |
| Protect from Missiles | 40 | 4 |
| Protect from Melee | 43 | 4 |
| Eagle Eye | 44 | 4 |
| Mystic Might | 45 | 4 |
| Retribution | 46 | 2 |
| Redemption | 49 | 2 |
| Smite | 52 | 6 |
| Preserve | 55 | 2 |
| Chivalry | 60 | 8 |
| Piety | 70 | 8 |
| Rigour | 74 | 8 |
| Augury | 77 | 8 |

### Combat bonus values
| Prayer | Attack | Strength | Defence | Ranged | Magic |
|--------|--------|----------|---------|--------|-------|
| Thick Skin | — | — | +5% | — | — |
| Burst of Strength | — | +5% | — | — | — |
| Clarity of Thought | +5% | — | — | — | — |
| Rock Skin | — | — | +10% | — | — |
| Superhuman Strength | — | +10% | — | — | — |
| Improved Reflexes | +10% | — | — | — | — |
| Steel Skin | — | — | +15% | — | — |
| Ultimate Strength | — | +15% | — | — | — |
| Incredible Reflexes | +15% | — | — | — | — |
| Chivalry | +15% | +18% | +20% | — | — |
| Piety | +20% | +23% | +25% | — | — |
| Sharp Eye | — | — | — | +5% | — |
| Hawk Eye | — | — | — | +10% | — |
| Eagle Eye | — | — | — | +15% | — |
| Rigour | — | — | +25% | +20% | — |
| Mystic Will | — | — | — | — | +5% |
| Mystic Lore | — | — | — | — | +10% |
| Mystic Might | — | — | — | — | +15% |
| Augury | — | — | +25% | — | +25% |

### Protection prayers
- **vs NPCs (PvE):** 100% damage reduction
- **vs Players (PvP):** 40% damage reduction (60% still hits)
- Protect from Melee, Protect from Missiles, Protect from Magic are the three

### Overhead prayer icons
| Prayer | Icon ID |
|--------|---------|
| Protect from Melee | 0 |
| Protect from Missiles | 1 |
| Protect from Magic | 2 |
| Retribution | 3 |
| Redemption | 4 |
| Smite | 5 |

### Prayer flicking
One-tick flicking: rapidly toggle prayer on/off to keep its bonuses without consuming points.
Engine allows this by design — do NOT prevent it. It's a known OSRS mechanic.

### Altar restore
- Full prayer restore at any altar (sets current prayer = base level)
- No tick delay on altar restore
- God Wars Dungeon altars: +1 prayer per aligned god item (not relevant for F2P)

---

## 5. Combat Formulas (Melee)

### Max hit calculation
```
Step 1 — Effective Strength:
  effectiveStr = floor(floor(strLevel × prayerMultiplier) + styleBonus + 8) × voidBonus

Step 2 — Max Hit:
  baseDamage = 0.5 + effectiveStr × (strengthBonus + 64) / 640
  maxHit = floor(baseDamage)
```

**Prayer multipliers:**
- None: 1.0
- Burst of Strength: 1.05
- Superhuman Strength: 1.10
- Ultimate Strength: 1.15
- Chivalry: 1.18
- Piety: 1.23

**Style bonus (combat stance):**
- Aggressive: +3 to effectiveStr
- Controlled: +1 to effectiveStr
- Accurate/Defensive: 0

**Void bonus:** 1.1 with full Void Melee set, else 1.0

### Accuracy / hit chance
```
attackRoll = (attackLevel + 8) × (attackBonus + 64)   × prayer × style
defenceRoll = (defenceLevel + 8) × (defenceBonus + 64) × prayer

// BOTH are re-rolled randomly 0..roll EVERY attack
randomAttack = random(0..attackRoll)
randomDefence = random(0..defenceRoll)

if randomAttack > randomDefence:
  hitChance = 1 - (defenceRoll + 2) / (2 × (attackRoll + 1))
else:
  hitChance = attackRoll / (2 × (defenceRoll + 1))
```

### Damage roll
```
damage = random(0..maxHit)   // inclusive both ends
// IMPORTANT: successful hit by PLAYER dealing 0 → rounds up to 1
// NPC hits CAN deal 0 on a "successful" roll (hitsplat shows 0)
```

### XP rates
- Melee combat XP: **4 XP per HP of damage dealt** (split between attack style and hitpoints)
  - Accurate: 4 attack XP + 4/3 hp XP (roughly)
  - Exact: hitting 10 damage = 40 XP, split per style
- HP XP: always 1.33 XP per HP of damage (regardless of style)

---

## 6. NPC Aggression

### Combat level rule
```
NPC attacks player if: playerCombatLevel ≤ 2 × npcCombatLevel
```
- Combat triangle (melee/ranged/magic advantage) does NOT affect aggression
- Level 63+ combat NPCs are aggressive to ALL players regardless of level

### Aggression range
- Typically **8 tiles** (Chebyshev distance — diagonal counts)
- Hunt range is measured from the **NPC's current position** (moves with NPC)
- Melee NPCs attack within 1 tile; use huntRange for initial aggro detection

### Tolerance (de-aggro) system
- **Tolerance zone:** 21×21 tile square centered on the **PLAYER** (10 tiles in each direction)
- **Timer:** 10 minutes (1,000 ticks) of continuous presence in same zone
- After 10 minutes: all NPCs in that zone stop attacking the player
- **Reset conditions:** Player leaves the 21×21 zone and returns → timer resets, NPCs re-aggro
- **Does NOT reset:** Logging out and back in (timer persists if player stays in zone)
- Some NPCs **never** lose aggression: bosses, some slayer monsters, wilderness NPCs

### Implementation notes
- Track per-player: `entryTick` (when they entered the area) and `toleranceZoneSW` (tile)
- Each tick: check if player moved >10 tiles from zone origin → if so, reset entryTick
- If `currentTick - entryTick >= 1000`: mark player as "tolerant" → skip aggro for that NPC type

---

## 7. Natural HP Regeneration

### Base rate
- **1 HP every 100 ticks (60 seconds)**
- Does **NOT** scale with Hitpoints level — flat 1 HP/min at all levels
- Fires even while standing still, walking, skilling

### Boosts to regen rate
| Method | Rate |
|--------|------|
| Base (no boost) | 1 HP per 100 ticks |
| Rapid Heal prayer | 1 HP per 50 ticks (2×) |
| Regen Bracelet | 1 HP per 50 ticks (2×) |
| Hitpoints cape | 1 HP per 50 ticks (2×) |

### Max HP cap
- Can never heal above **base HP level** via natural regen
- Boosts (e.g. Saradomin brew overheal) can exceed base — natural regen still caps at base

---

## 8. Commonly Mis-Implemented Mechanics

These are the most frequent bugs in OSRS private servers. Check each one.

### 1. Damage roll minimum (player hits)
**Wrong:** `damage = random(1..maxHit)` — never 0
**Correct:** `damage = random(0..maxHit)` — then if damage == 0 AND it's a successful hit by PLAYER, set to 1
NPC hits can show splat 0. Player hits on a successful roll always deal minimum 1.

### 2. Both attack AND defence are re-rolled per attack
**Wrong:** Pre-calculate attack roll once, compare to defence roll each attack
**Correct:** Both `random(0..attackRoll)` and `random(0..defenceRoll)` are re-rolled fresh every attack attempt

### 3. Tolerance zone is centered on PLAYER, not NPC
**Wrong:** Track each NPC's "has player been near me long enough?" individually
**Correct:** Track a 21×21 zone anchored to the player's position. If player moves 10+ tiles, zone resets.

### 4. Prayer drain is per game tick, not per second
**Wrong:** Implement drain timer at 600ms intervals and subtract directly
**Correct:** Each tick, add drain effects of active prayers to accumulator. When accumulator ≥ resistance, subtract a point and reduce accumulator.

### 5. Potion boost decay is NOT instant on next drink
**Wrong:** Drinking a second attack potion resets the decay timer on the existing boost
**Correct:** Boost value is recalculated fresh (if at base already, boost back to max+boost). Decay timer resets. The boost does NOT stack beyond the cap.

### 6. Run energy formula changed in Jan 2025
**Wrong:** Using old formula `67 + (67 * Weight / 64)` for drain rate
**Correct:** `(60 + (67 * Weight / 64)) * (1 - Agility / 300)`

**Wrong:** Using old regen formula `(Agility / 6) + 8`
**Correct:** `(Agility / 10) + 15`

### 7. Combat level does NOT affect prayer efficacy
Prayer bonuses (thick skin, protect prayers, etc.) apply equally to all players regardless of level. Only Prayer Bonus stat (from equipment) affects drain rate.

### 8. Food timers are SEPARATE from attack timers (mostly)
Eating food sets a **food delay** timer (3 ticks) AND an **attack delay** timer (3 ticks).
These are separate. Eating does NOT reset your weapon's attack speed cycle — it delays the NEXT attack by 3 ticks.

### 9. Protect Item prayer on death
- With Protect Item: keep 4 items instead of 3 on death (inside wilderness: still lose the most valuable item if skulled)
- Skulling overrides the normal death behavior
- Not relevant for F2P (wilderness deaths keep 3 items, skulled keeps 0)

### 10. NPC retaliation is NOT the same as NPC aggression
- **Retaliation:** NPC attacks back when hit first (reactive)
- **Aggression:** NPC initiates attack unprovoked based on proximity (proactive)
- Both need separate implementations. Most servers implement retaliation but forget aggression.

### 11. Anglerfish overheal resets correctly
Eating a second anglerfish while the first's delayed heal is pending:
- Cancels the FIRST delayed heal entirely
- The second anglerfish's delayed heal fires 7 ticks after eating the second one
- Use a timer that gets overwritten, not a queue

### 12. Prayer point on login
Players always login with their prayer points at the level they logged out with (not restored). Do NOT restore prayer on login — that only happens at an altar.

---

## 9. Hitpoints Orb & HP Display

- HP orb shows current / base HP
- Poison: green text, venom: purple text on the orb
- HP orb turns yellow when player is at low HP (below 10 HP typically)
- Orb flashes during HP regen tick

---

## 10. Slayer XP (Reference for when Slayer is implemented)

```
Slayer XP = NPC's Hitpoints (base HP, not current HP when killed)
```
- 1 Slayer XP per 1 HP of the NPC's max HP
- Confirmed: killing a goblin (5 HP) gives 5 Slayer XP

---

## Sources

- OSRS Wiki: Energy, Prayer, Combat, Food, Agility, Hitpoints pages (Rev 233 snapshot)
- OSRS Wiki: Run Energy update history (Jan 8, 2025 formula change)
- Research date: 2026-02-22

