# Core Systems Implementation Guide

> Missing Tier 1/2 systems that every OSRS player hits constantly.
> Each section: what exists, what's missing, exact API, and a working code template.

**Created:** 2026-02-22 | **Audit source:** RSMod v2 engine + Alter v1 donor + ProtectedAccess API

---

## Quick Reference — System Status

| System | Status | Notes |
|--------|--------|-------|
| **Food eating** | ✅ Done | `content/other/consumables/` — completed (FOOD-1) |
| **Potions** | ✅ Done | `content/other/consumables/` — completed (FOOD-2) |
| **Prayer effects** | ✅ Done | `content/interfaces/prayer-tab/` — toggle, drain, overhead, Rapid Restore/Heal |
| **Make-X interface** | 🟡 Partial | Framework + Smithing wired (MAKEQ-1). Fletching, Herblore, Crafting pending |
| **F2P shop content** | ✅ Done | All major F2P city shops wired |
| **NPC aggression** | ✅ Done | `content/mechanics/npc-aggression/` — MECH-1 + MECH-3 complete |

> Templates below remain valid as **implementation reference** — use them when adding new food, potions, shops, etc.

---

## 1. Food Eating

### What exists in the engine
- `statHeal(stat, constant, percent)` — heals HP, caps at base level
- `statBoost(stat, constant, percent)` — used for overheal (anglerfish)
- `invDel`, `invReplace` — remove consumed item / replace with container
- `anim(seqs.x)` — eat animation
- `delay(ticks)` — tick-accurate eat delay
- `mes("text")` — message to player

### What's missing
A **food consumables plugin** at `rsmod/content/other/consumables/` (or `content/skills/cooking/`).
No module exists. Nothing handles `onOpObj` for "Eat" on food items.

### RSMod v2 implementation template

```kotlin
// content/other/consumables/src/.scripts/FoodScript.kt

private data class FoodDef(
    val obj: ObjType,
    val heal: Int,
    val overheal: Boolean = false,
    val replacement: ObjType? = null,   // null = no container left (e.g. cooked chicken)
    val eatDelay: Int = 3,              // ticks before can eat again
    val comboFood: Boolean = false,     // true = karambwan (can eat alongside potion)
)

private val FOOD_DEFS = listOf(
    FoodDef(objs.shrimps, heal = 3),
    FoodDef(objs.sardine, heal = 4),
    FoodDef(objs.cooked_chicken, heal = 4),
    FoodDef(objs.cooked_meat, heal = 4),
    FoodDef(objs.bread, heal = 5),
    FoodDef(objs.herring, heal = 5),
    FoodDef(objs.mackerel, heal = 6),
    FoodDef(objs.trout, heal = 7),
    FoodDef(objs.cod, heal = 7),
    FoodDef(objs.pike, heal = 8),
    FoodDef(objs.salmon, heal = 9),
    FoodDef(objs.tuna, heal = 10),
    FoodDef(objs.lobster, heal = 12),
    FoodDef(objs.bass, heal = 13),
    FoodDef(objs.swordfish, heal = 14),
    FoodDef(objs.monkfish, heal = 16),
    FoodDef(objs.shark, heal = 20),
    FoodDef(objs.manta_ray, heal = 22),
    // Pizza slices (replacement = pizza with fewer slices):
    FoodDef(objs.plain_pizza, heal = 7, replacement = objs.half_plain_pizza),
    FoodDef(objs.half_plain_pizza, heal = 7),
    FoodDef(objs.meat_pizza, heal = 8, replacement = objs.half_meat_pizza),
    FoodDef(objs.half_meat_pizza, heal = 8),
    FoodDef(objs.anchovies_pizza, heal = 9, replacement = objs.half_anchovy_pizza),
    FoodDef(objs.half_anchovy_pizza, heal = 9),
    // Combo food:
    FoodDef(objs.cooked_karambwan, heal = 18, comboFood = true),
    // Anglerfish (overheal — can exceed base HP):
    FoodDef(objs.anglerfish, heal = 0, overheal = true),  // heal calculated dynamically
)

class FoodScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        FOOD_DEFS.forEach { food ->
            onOpObj1(food.obj) { eatFood(food) }
        }
    }

    private suspend fun ProtectedAccess.eatFood(food: FoodDef) {
        if (!canEat(food)) {
            mes("You must wait a moment before eating again.")
            return
        }

        val slot = player.inv.indexOfFirst { it?.isType(food.obj) == true }
        if (slot < 0) return

        // Remove item (or replace with container/partial)
        if (food.replacement != null) {
            invReplace(invs.inv, food.obj, 1, food.replacement)
        } else {
            invDel(invs.inv, food.obj, 1)
        }

        // Eat animation + sound
        anim(seqs.human_eat)
        // soundSynth(synths.eat_food)  // add when synth sym known

        // Heal HP
        val healAmount = if (food.overheal) {
            // Anglerfish overheal: floor(baseHp/10) + tier bonus
            calculateAnglerHeal()
        } else {
            food.heal
        }

        if (food.overheal) {
            statBoost(stats.hitpoints, healAmount, 0)   // can exceed base HP
        } else {
            statHeal(stats.hitpoints, healAmount, 0)    // caps at base HP
        }

        mes("You eat the ${food.obj.name.lowercase()}.")

        // Eat delay (prevents eating again for N ticks)
        delay(food.eatDelay)
    }

    private fun ProtectedAccess.canEat(food: FoodDef): Boolean {
        if (food.comboFood) return !player.hasTimer(timers.combo_food_delay)
        return !player.hasTimer(timers.food_delay)
    }

    private fun ProtectedAccess.calculateAnglerHeal(): Int {
        val base = player.statBase(stats.hitpoints)
        return (base / 10) + when (base) {
            in 93..99 -> 13
            in 75..92 -> 8
            in 50..74 -> 6
            in 25..49 -> 4
            else -> 2
        }
    }
}
```

### Key eat delay pattern

In OSRS, eating food blocks eating again for 3 ticks (1.8s). Use a soft timer:

```kotlin
// Add to BaseTimers.kt:
val food_delay = find("food_delay")
val combo_food_delay = find("combo_food_delay")

// In FoodScript, after eating:
player.timer(timers.food_delay, food.eatDelay)

// Check before eating:
private fun ProtectedAccess.canEat(food: FoodDef): Boolean {
    if (food.comboFood) return !player.hasTimer(timers.combo_food_delay)
    return !player.hasTimer(timers.food_delay)
}
```

### Sym names to verify in obj.sym

| Food | Sym name to check |
|------|------------------|
| Shrimps | `shrimp` or `raw_shrimps` — check obj.sym |
| Cooked chicken | `chicken_cooked` or `cooked_chicken` |
| Karambwan | `cooked_karambwan` |
| Anglerfish | `anglerfish` |

Always check `.data/symbols/obj.sym` before using `find("name")`.

---

## 2. Potions (Stat Boosts)

### What exists in the engine
- `statBoost(stat, constant, percent)` — temporarily boosts above base level
- `statHeal(stat, constant, percent)` — restores stat toward base (for prayer restore, antipoison, etc.)
- `statDrain(stat, constant, percent)` — reduces below base (for debuffs)
- `invReplace` — replaces 4-dose with 3-dose etc.

### RSMod v2 API for potions

```kotlin
// statBoost: raises current level, cap = base + (constant + percent% of base)
statBoost(stats.attack, constant = 3, percent = 10)   // +3 + 10% of base attack

// statHeal: restores toward base level (doesn't overshoot)
statHeal(stats.prayer, constant = 7, percent = 25)    // restore 7 + 25% of base prayer
```

### Implementation template

```kotlin
private data class PotionDef(
    val doses: List<ObjType>,   // [4-dose, 3-dose, 2-dose, 1-dose, empty vial]
    val action: suspend ProtectedAccess.() -> Unit,
)

private val POTION_DEFS = listOf(
    PotionDef(
        doses = listOf(objs.attack_potion4, objs.attack_potion3, objs.attack_potion2, objs.attack_potion1),
        action = {
            statBoost(stats.attack, 3, 10)
            mes("You drink some of your attack potion.")
        }
    ),
    PotionDef(
        doses = listOf(objs.strength_potion4, objs.strength_potion3, objs.strength_potion2, objs.strength_potion1),
        action = {
            statBoost(stats.strength, 3, 10)
            mes("You drink some of your strength potion.")
        }
    ),
    PotionDef(
        doses = listOf(objs.defence_potion4, objs.defence_potion3, objs.defence_potion2, objs.defence_potion1),
        action = {
            statBoost(stats.defence, 3, 10)
            mes("You drink some of your defence potion.")
        }
    ),
    PotionDef(
        doses = listOf(objs.prayer_potion4, objs.prayer_potion3, objs.prayer_potion2, objs.prayer_potion1),
        action = {
            statHeal(stats.prayer, 7, 25)   // restore 7 + 25% of base prayer
            mes("You drink some of your prayer potion.")
        }
    ),
    PotionDef(
        doses = listOf(objs.super_attack4, objs.super_attack3, objs.super_attack2, objs.super_attack1),
        action = {
            statBoost(stats.attack, 5, 15)
            mes("You drink some of your super attack potion.")
        }
    ),
    // ... add all potion types
)

class PotionScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        POTION_DEFS.forEach { potion ->
            potion.doses.forEachIndexed { index, dose ->
                onOpObj2(dose) { drinkPotion(potion, index) }
            }
        }
    }

    private suspend fun ProtectedAccess.drinkPotion(potion: PotionDef, doseIndex: Int) {
        val isLastDose = doseIndex == potion.doses.size - 1
        if (isLastDose) {
            invReplace(invs.inv, potion.doses[doseIndex], 1, objs.vial)   // last dose → empty vial
        } else {
            invReplace(invs.inv, potion.doses[doseIndex], 1, potion.doses[doseIndex + 1])
        }

        anim(seqs.human_drink)
        potion.action()
        delay(3)   // 3-tick potion delay
    }
}
```

### Stat boost reference (F2P potions)

| Potion | Stat | Formula |
|--------|------|---------|
| Attack potion | Attack | +3 + 10% of base |
| Strength potion | Strength | +3 + 10% of base |
| Defence potion | Defence | +3 + 10% of base |
| Prayer potion | Prayer | +7 + 25% of base (restore) |
| Super attack | Attack | +5 + 15% of base |
| Super strength | Strength | +5 + 15% of base |
| Super defence | Defence | +5 + 15% of base |
| Antipoison | — | Cures poison, immunity for ~90s |

---

## 3. Prayer Active Effects

### Current state
- Prayer **drain** is already implemented (PrayerScript or equivalent)
- Combat formulas **already read prayer bonuses** via `api/combat-accuracy` — so attack/defence prayer bonuses may already work in combat
- What's missing: the **prayer tab button handler** that activates/deactivates prayers via varbit

### How prayer bonuses feed into combat (already wired)
The combat accuracy formula reads equipment + prayer bonuses from the player's current state.
Activating a prayer sets varbits → combat formula reads those varbits → bonus applied.
**The bonus calculation happens automatically** once varbits are set correctly.

### What to implement

**1. Prayer tab button handlers** — listen for clicks on prayer tab interface buttons:

```kotlin
// Interface 541 = prayer book tab (verify in iftypes.txt)
// Each prayer has a known button component ID
onButton(interfaces.prayer_tab, component = 19) { /* Thick Skin */ togglePrayer(prayers.thick_skin) }
onButton(interfaces.prayer_tab, component = 20) { /* Burst of Strength */ togglePrayer(prayers.burst_of_strength) }
// ... one per prayer
```

**2. Prayer enum with bonus values:**

```kotlin
enum class Prayer(
    val varbit: VarBitType,
    val levelReq: Int,
    val drainRate: Int,   // drain points per minute × 10 (OSRS wiki format)
    val attackBonus: Double = 0.0,
    val strengthBonus: Double = 0.0,
    val defenceBonus: Double = 0.0,
    val rangedBonus: Double = 0.0,
    val magicBonus: Double = 0.0,
    val overheadIcon: Int = -1,
) {
    THICK_SKIN(varbit = varbits.prayer_thick_skin, levelReq = 1, drainRate = 10, defenceBonus = 0.05),
    BURST_OF_STRENGTH(varbits.prayer_burst_of_strength, levelReq = 4, drainRate = 10, strengthBonus = 0.05),
    CLARITY_OF_THOUGHT(varbits.prayer_clarity, levelReq = 7, drainRate = 10, attackBonus = 0.05),
    SHARP_EYE(varbits.prayer_sharp_eye, levelReq = 8, drainRate = 10, rangedBonus = 0.05),
    MYSTIC_WILL(varbits.prayer_mystic_will, levelReq = 9, drainRate = 10, magicBonus = 0.05),
    ROCK_SKIN(varbits.prayer_rock_skin, levelReq = 10, drainRate = 20, defenceBonus = 0.10),
    SUPERHUMAN_STRENGTH(varbits.prayer_superhuman, levelReq = 13, drainRate = 20, strengthBonus = 0.10),
    IMPROVED_REFLEXES(varbits.prayer_improved_reflexes, levelReq = 16, drainRate = 20, attackBonus = 0.10),
    // ... all prayers
    PROTECT_FROM_MAGIC(varbits.prayer_prot_magic, levelReq = 37, drainRate = 40, overheadIcon = 2),
    PROTECT_FROM_MISSILES(varbits.prayer_prot_range, levelReq = 40, drainRate = 40, overheadIcon = 1),
    PROTECT_FROM_MELEE(varbits.prayer_prot_melee, levelReq = 43, drainRate = 40, overheadIcon = 0),
    PIETY(varbits.prayer_piety, levelReq = 70, drainRate = 120, attackBonus = 0.20, strengthBonus = 0.23, defenceBonus = 0.25),
}
```

**3. Activate/deactivate on toggle:**

```kotlin
private fun ProtectedAccess.togglePrayer(prayer: Prayer) {
    val currentlyActive = player.varbit(prayer.varbit) != 0
    if (currentlyActive) {
        player.setVarbit(prayer.varbit, 0)
    } else {
        if (player.statBase(stats.prayer) < prayer.levelReq) {
            mes("You need level ${prayer.levelReq} Prayer to use ${prayer.name}.")
            return
        }
        // Deactivate conflicting prayers in same group
        deactivateConflicting(prayer)
        player.setVarbit(prayer.varbit, 1)
        if (prayer.overheadIcon >= 0) setOverheadIcon(prayer.overheadIcon)
    }
}
```

**4. Overhead prayer icon** — set via player avatar extended info:
```kotlin
// RSMod v2 method (verify exact call in avatar API):
player.avatar.extendedInfo.setOverheadIcon(prayer.overheadIcon)
// or via ProtectedAccess — search for "prayerIcon" in ProtectedAccess.kt
```

**5. Protection prayer damage reduction** — in combat hit handler:
```kotlin
// When player is hit by melee, check Protect from Melee:
if (player.varbit(varbits.prayer_prot_melee) != 0) {
    damage = (damage * 0.0).toInt()  // 100% reduction in PvE, 40% in PvP
}
```

### Prayer varbits to add to BaseVarbits.kt

Search `varbittypes.txt` via MCP for each prayer name:
```javascript
search_varbittypes({ query: "prayer_thick_skin" })
search_varbittypes({ query: "prayer_protect_melee" })
```

---

## 4. Make-X Quantity Interface

### What exists
`countDialog(title)` is already implemented in `ProtectedAccess`. It shows the client's count input layer and suspends until the player enters a number.

```kotlin
val quantity = countDialog("How many would you like to make?")
```

### Where to add it

Every artisan skill with a "make X" action needs this. Currently **none** use it:
- Fletching — knife-on-log, bowstring stringing
- Smithing — anvil smithing
- Herblore — herb cleaning, potion mixing
- Crafting — spinning, leather, gem cutting

### Implementation pattern

```kotlin
// In Fletching.kt — knife-on-bow:
onOpHeldU(objs.knife, content.log) { event ->
    val logType = event.second
    val bowDef = BOW_DEFS.find { it.log == logType.toType() } ?: return@onOpHeldU

    if (statMap.getBaseLevel(stats.fletching).toInt() < bowDef.levelReq) {
        mes("You need level ${bowDef.levelReq} Fletching to fletch this.")
        return@onOpHeldU
    }

    val quantity = countDialog("How many would you like to fletch?")   // ADD THIS
    repeat(quantity) {
        if (!inv.contains(bowDef.log)) return@repeat
        invDel(invs.inv, bowDef.log, 1)
        invAdd(invs.inv, bowDef.product, 1)
        anim(seqs.fletching_bow)
        statAdvance(stats.fletching, bowDef.xp)
        delay(3)
    }
}
```

### Notes
- `countDialog` returns a positive Int (never negative — use `numberDialog` if you need negative)
- The player can press Enter or click OK to confirm
- The loop should check `inv.contains(required_material)` each iteration to stop if they run out
- Each item craft inside the loop should `delay(n)` so XP is granted over time, not all at once

---

## 5. F2P Shop Content

### What exists
The entire shop API is complete:
- `Shops.open(player, npc, title, invType)` — opens shop
- `InvEditor` — defines stock, restock rate, scope
- `shops.open(player, npc, title, inv, currency)` — with currency override

### Reference: Bob's Axes (completed example)

```kotlin
// LumbridgeInvs.kt — inventory definition:
edit(lumbridge_invs.axeshop) {
    scope = InvScope.Shared     // all players share same stock
    stack = InvStackType.Always
    autoSize = true
    restock = true
    stock += stock(objs.bronze_axe, count = 10, restockCycles = 100)
    stock += stock(objs.iron_axe, count = 5, restockCycles = 200)
    stock += stock(objs.steel_axe, count = 3, restockCycles = 300)
}

// Bob.kt — NPC plugin:
onOpNpc3(lumbridge_npcs.bob) { player.openShop(it.npc) }

private fun Player.openShop(npc: Npc) {
    shops.open(this, npc, "Bob's Brilliant Axes", lumbridge_invs.axeshop)
}
```

### All F2P shops needed

| Shop | NPC | City | Key items |
|------|-----|------|-----------|
| General Store | Shop Keeper | Multiple cities | Pots, buckets, shears, tinderbox, chisels |
| Lumbridge General Store | Shop keeper | Lumbridge | Standard supplies |
| Varrock General Store | Shop keeper | Varrock | Standard supplies |
| Falador General Store | Shop keeper | Falador | Standard supplies |
| Aubury's Rune Shop | Aubury | Varrock | Air/Mind/Water/Earth/Fire runes, staff of air |
| Betty's Magic Emporium | Betty | Port Sarim | Magic supplies |
| Zaff's Superior Staffs | Zaff | Varrock | Staffs |
| Thessaly's Fine Clothes | Thessaly | Varrock | Clothes |
| Crossbow Shop | Hirko | Keldagrim | Crossbows |
| Fishing Shop | Harry | Port Sarim | Nets, rods, bait |
| Wayne's Chains | Wayne | Falador | Armour |
| Horvik's Armour Shop | Horvik | Varrock | Armour |
| Lowe's Archery Emporium | Lowe | Varrock | Ranged supplies |
| Al Kharid Gem Shop | Ali Morrisane | Al Kharid | Gems |
| Dommik's Crafting | Dommik | Al Kharid | Crafting supplies |

### How to add a shop for a new city

**Step 1 — Add inventory ref to the area's InvReferences:**
```kotlin
// e.g. VarrockInvs.kt
internal object VarrockInvs : InvReferences() {
    val general_store = find("varrock_general_store")
    val auburys_rune_shop = find("auburys_rune_shop")
}
```

**Step 2 — Define the stock in an InvEditor:**
```kotlin
internal object VarrockInvBuilder : InvEditor() {
    init {
        edit(varrock_invs.general_store) {
            scope = InvScope.Shared
            restock = true
            stock += stock(objs.pot, count = 20, restockCycles = 50)
            stock += stock(objs.bucket, count = 20, restockCycles = 50)
            stock += stock(objs.tinderbox, count = 20, restockCycles = 50)
        }
        edit(varrock_invs.auburys_rune_shop) {
            scope = InvScope.Shared
            restock = true
            stock += stock(objs.air_rune, count = 1000, restockCycles = 25)
            stock += stock(objs.mind_rune, count = 1000, restockCycles = 25)
            stock += stock(objs.water_rune, count = 1000, restockCycles = 25)
            stock += stock(objs.earth_rune, count = 1000, restockCycles = 25)
            stock += stock(objs.fire_rune, count = 1000, restockCycles = 25)
            stock += stock(objs.staff_of_air, count = 5, restockCycles = 500)
        }
    }
}
```

**Step 3 — Wire to NPC:**
```kotlin
class Aubury @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.aubury) { talkToAubury(it.npc) }
        onOpNpc3(varrock_npcs.aubury) { openAuburyShop() }
    }

    private suspend fun ProtectedAccess.talkToAubury(npc: Npc) =
        startDialogue(npc) {
            chatNpc(calm, "Can I interest you in some runes?")
        }

    private fun ProtectedAccess.openAuburyShop() {
        shops.open(player, "Aubury's Rune Shop", varrock_invs.auburys_rune_shop)
    }
}
```

---

## 6. NPC Aggression (MECH-1 + MECH-3 — COMPLETE)

> **Status:** ✅ Complete. `content/mechanics/npc-aggression/` handles radius + tolerance timer (de-aggro). Use this section as reference when adding aggression to new NPCs.

### What exists
`huntRange`, `huntMode`, `giveChase` fields in `NpcEditor` — aggression system fully wired.

### OSRS aggression rules
- NPCs with aggression will attack players within their **aggression radius** (typically 8–16 tiles depending on NPC)
- Players become "safe" after ~10 minutes of being in the same area (de-aggro timer)
- Wilderness NPCs never de-aggro
- Combat level is NOT relevant to aggression for most NPCs (some exceptions for slayer monsters)

### Expected implementation pattern

```kotlin
// In NpcEditor for aggressive NPC:
edit(npcs.dark_wizard) {
    hitpoints = 20
    attack = 12
    strength = 14
    defence = 5
    attackRange = 1
    huntRange = 5           // aggression radius
    giveChase = true
    // huntMode = huntModes.aggressive   // when engine supports this
}
```

**Hunt mode plugin (MECH-1 will implement this):**
```kotlin
onNpcTimer(timers.aggro_check) { event ->
    val npc = event.npc
    // Find nearest player within huntRange
    // If player hasn't been in area > 10 minutes, attack
    // npc.setAttackTarget(player)
}
```

### De-aggro tracking
Store last-area-entry timestamp per player. If `mapClock - entryTime > 600 ticks (10 min)`, set player as "safe" for that area and do not target them.

---

## 7. Run Energy & Weight (Already Implemented — Verify)

Per engine audit, `api/inv-weight` and `api/game-process` both exist and handle:
- Weight calculation from inventory + equipped items
- Run energy drain (faster drain when heavier)
- Run energy restore when standing still or walking
- Stamina potion effect on drain rate

**Verify this is working** by testing in-game: equip heavy items → run → check energy drain rate.
If broken, check `rsmod/api/game-process/` for the run energy processor.

---

## Module Locations

| System | Create at |
|--------|-----------|
| Food eating | `rsmod/content/other/consumables/` |
| Potions | `rsmod/content/other/consumables/` (same module, separate script) |
| Prayer effects | `rsmod/content/mechanics/prayer/` (if not existing) |
| Shop content | Inside each area module — e.g. `rsmod/content/areas/city/varrock/` |
| Make-X | In-place modification of existing skill scripts |

## Remaining Work

1. ✅ ~~Food eating (FOOD-1)~~ — Complete
2. ✅ ~~Potions (FOOD-2)~~ — Complete
3. ✅ ~~Shop content per city~~ — All major F2P shops wired
4. 🟡 **Make-X countDialog** (MAKEQ-FLETCH/HERB/CRAFT) — Add `countDialog()` to Fletching, Herblore, Crafting
5. ✅ ~~Prayer effects~~ — Complete
6. ✅ ~~NPC aggression~~ — Complete (MECH-1 + MECH-3)

