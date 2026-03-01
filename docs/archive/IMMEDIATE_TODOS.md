# Immediate TODOs - Start Here

**Your mission:** Get something working THIS WEEK.

---

## 🔥 This Week (Priority: CRITICAL)

### Day 1-2: Hook Up Drop Tables

**What:** Connect your `GeneratedDropTables.kt` to actual combat

**From Repos:** Kronos-184, Alter, Tarnish

**Files to Reference:**
- `Kronos-184: model/item/loot/LootTable.java`
- `Alter: LootTableDsl.kt`, `NpcDeathAction.kt`
- `Tarnish: LootTable.java`

**TODO:**
```kotlin
// In rsmod/content/other/npc-drops/src/...

class NpcDropTablesScript : PluginScript() {
    override fun ScriptContext.startup() {
        // Import your GeneratedDropTables.kt
        loadGeneratedDropTables()
        
        // Hook into NPC death event
        onNpcDeath { npc, killer ->
            val table = getDropTable(npc.id)
            val drops = table.roll(killer)
            drops.forEach { drop ->
                spawnGroundItem(drop.obj, drop.quantity, npc.coords)
            }
        }
    }
}
```

**Checklist:**
- [ ] Create NpcDropTablesScript.kt
- [ ] Hook into death event
- [ ] Test killing a goblin
- [ ] Verify drops appear
- [ ] Adjust rates if needed

---

### Day 3-4: Create First NPC Combat Definition

**What:** Define goblin combat stats using DSL pattern

**From Repos:** Alter (best DSL), OpenRune

**Files to Reference:**
- `Alter: NpcCombatDsl.kt`, `NpcCombatDef.kt`
- `Kronos-184: data/npcs/combat/*.json`

**TODO:**
```kotlin
// Create NpcCombatDefinitions.kt

setCombatDef(npcs.goblin) {
    configs {
        attackSpeed = 4  // 2.4 seconds
        respawnDelay = 25  // ticks
    }
    stats {
        hitpoints = 5
        attack = 1
        strength = 1
        defence = 1
        ranged = 1
        magic = 1
    }
    anims {
        attack = anims.goblin_attack
        block = anims.goblin_block  
        death = anims.goblin_death
    }
    // Drops already in GeneratedDropTables.kt!
}

setCombatDef(npcs.giant_rat) {
    stats { hitpoints = 5 }
    // ... etc
}
```

**Checklist:**
- [ ] Create NpcCombatDsl.kt (port from Alter)
- [ ] Define 5 F2P NPCs (goblin, giant_rat, cow, chicken, man)
- [ ] Test combat
- [ ] Verify HP bars work
- [ ] Verify death animations

---

### Day 5-7: Port Combat Formulas

**What:** Accurate OSRS hit calculation

**From Repos:** OpenRune (cleanest), Kronos-184

**Files to Reference:**
- `OpenRune: combat/formula/MeleeCombatFormula.kt`
- `Kronos-184: model/combat/Combat.java`
- `Tarnish: CombatFormula.java`, `accuracy/`, `maxhit/`

**TODO:**
```kotlin
// Create MeleeCombatFormula.kt

class MeleeCombatFormula {
    fun calculateAccuracyRoll(
        attackLevel: Int,
        attackBonus: Int,
        style: AttackStyle
    ): Int {
        // Port from OpenRune:
        // effectiveLevel = attackLevel + styleBonus + 8
        // accuracyRoll = effectiveLevel * (attackBonus + 64)
    }
    
    fun calculateDefenceRoll(
        defenceLevel: Int,
        defenceBonus: Int
    ): Int {
        // effectiveLevel = defenceLevel + 8
        // defenceRoll = effectiveLevel * (defenceBonus + 64)
    }
    
    fun calculateMaxHit(
        strengthLevel: Int,
        strengthBonus: Int,
        style: AttackStyle
    ): Int {
        // effectiveLevel = (strengthLevel + styleBonus) * prayerMult + 8
        // baseMax = (effectiveLevel * (strengthBonus + 64) / 640) + 0.5
        // maxHit = floor(baseMax)
    }
    
    fun rollHit(accuracy: Int, defence: Int, maxHit: Int): Hit {
        // accuracyChance = accuracy / (defence + 1)
        // if (random() < accuracyChance) hit = random(0..maxHit)
        // else hit = 0
    }
}
```

**Checklist:**
- [ ] Port accuracy formula
- [ ] Port max hit formula
- [ ] Test with different weapons
- [ ] Verify against OSRS Wiki values

---

## 📋 Next Week (Priority: HIGH)

### Task: Implement Thieving Skill

**What:** Complete thieving skill from Alter

**From Repos:** Alter (complete implementation)

**Files to Reference:**
- `Alter: skills/thieving/pickpocket/PickpocketPlugin.kt`
- `Alter: skills/thieving/pickpocket/PickpocketService.kt`
- `Alter: skills/thieving/pickpocket/PickpocketData.kt`
- `Alter: data/cfg/thieving/pickpockets.json`

**TODO:**
```kotlin
// 1. Create PickpocketData.kt
// Copy JSON structure from Alter

data class PickpocketData(
    val npcId: Int,
    val level: Int,
    val xp: Double,
    val stunDamage: Int,
    val stunTicks: Int,
    val loot: List<LootEntry>
)

// 2. Create PickpocketPlugin.kt
class PickpocketPlugin : PluginScript() {
    override fun ScriptContext.startup() {
        // Load all pickpocket data from JSON
        
        onOpNpc3 { // Option 3 = pickpocket
            val data = getPickpocketData(npc.id) ?: return
            
            if (player.thievingLvl < data.level) {
                mes("You need level ${data.level} thieving.")
                return
            }
            
            // Attempt pickpocket
            val success = rollPickpocketSuccess(player, data)
            if (success) {
                giveLoot(player, data.loot)
                grantXp(stats.thieving, data.xp)
            } else {
                stun(player, data.stunTicks)
                damage(player, data.stunDamage)
                npc.animate(anims.angry) // NPC catches you
            }
        }
    }
}
```

**Checklist:**
- [ ] Copy pickpockets.json from Alter
- [ ] Create PickpocketData.kt
- [ ] Create PickpocketPlugin.kt
- [ ] Add pickpocket option to NPCs
- [ ] Test with Man/Woman in Lumbridge
- [ ] Add stall thieving
- [ ] Add chest thieving

---

### Task: Enhance Woodcutting

**What:** Add bird nests, infernal axe

**From Repos:** Kronos-184 (best reference)

**Files to Reference:**
- `Kronos-184: skills/woodcutting/Woodcutting.java`

**TODO:**
```kotlin
// Add to Woodcutting.kt:

// 1. Bird nest drops (1/200 chance, modified by woodcutting cape)
if (random(1, 200) == 1) {
    val nestType = rollNestType() // Clue nests, ring nests, egg nests
    invAdd(nestType)
    mes("A bird's nest falls out of the tree.")
}

// 2. Infernal axe (1/3 chance to burn logs for FM XP)
if (player.hasInfernalAxe() && random(1, 3) == 1) {
    grantXp(stats.firemaking, log.fmXp)
    // Don't give log, it was burned
} else {
    invAdd(log.item)
}

// 3. Woodcutting guild invisible boost (+7)
if (player.inWcGuild()) {
    effectiveLevel += 7
}
```

**Checklist:**
- [ ] Add bird nest system
- [ ] Add nest types (clue, ring, egg)
- [ ] Add infernal axe support
- [ ] Add WC guild boost

---

## 📊 Quick Reference: Where to Find Code

| Need | Best Source | Files |
|------|-------------|-------|
| **Combat formulas** | OpenRune | `combat/formula/MeleeCombatFormula.kt` |
| **NPC combat DSL** | Alter | `NpcCombatDsl.kt`, `NpcCombatDef.kt` |
| **Loot tables** | Alter | `LootTableDsl.kt`, `NpcDeathAction.kt` |
| **Thieving** | Alter | `skills/thieving/*` |
| **Woodcutting** | Kronos | `skills/woodcutting/Woodcutting.java` |
| **Mining** | Kronos | `skills/mining/Mining.java` |
| **Special attacks** | Kronos | `combat/special/melee/*.java` |
| **Quests** | OpenRune | `quest/CooksAssistant.kt` |
| **Bosses** | Tarnish | `combat/strategy/npc/boss/*` |
| **Minigames** | Tarnish | `plugins/minigames/*` |

---

## 🎯 Success Criteria

**By end of this week you should have:**
- ✅ Drops working (kill goblin → get bones)
- ✅ Combat definitions (goblin has 5 HP)
- ✅ Combat formulas (accurate hits)

**By end of next week you should have:**
- ✅ Thieving skill working
- ✅ Enhanced woodcutting
- ✅ At least 1 quest implemented

---

## 🚀 Getting Started NOW

```bash
# 1. Create working branch
cd rsmod
git checkout -b content-implementation

# 2. Create NPC combat module
mkdir -p content/combat/npc-defs/src/main/kotlin/...

# 3. Start with drop tables
code content/other/npc-drops/src/.../NpcDropTablesScript.kt

# 4. Copy from your GeneratedDropTables.kt
# 5. Hook into death event
# 6. Test in-game
```

---

## 💡 Pro Tips

1. **Don't copy-paste blindly** - Adapt to RSMod v2 patterns
2. **Test incrementally** - Don't implement 10 things at once
3. **Use your cache_lookup.py** - Verify obj/npc/anim names
4. **Check OSRS Wiki** - Verify formulas and drop rates
5. **Use Claude skills** - Generate boilerplate quickly

---

## 📞 If You Get Stuck

| Problem | Solution |
|---------|----------|
| Can't find cache ID | Run `cache_lookup.py` |
| Formula doesn't match | Check OSRS Wiki + 2004scape source |
| Animation wrong | Check seq.sym for correct ID |
| Drop rate wrong | Compare with Kronos JSON |
| Not sure about pattern | Check RSMod existing skills |

---

**Stop reading. Start coding.** ⚡

