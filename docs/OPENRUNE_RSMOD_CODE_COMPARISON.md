# OpenRune vs RSMod v2 - Code Comparison

## Side-by-Side Examples

---

## 1. Basic NPC Interaction

### OpenRune
```kotlin
class HansPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {
    
    init {
        onNpcOption("npcs.hans", "talk-to") {
            player.queue {
                chatNpc("Welcome to Lumbridge Castle!")
                chatPlayer("Who are you?")
                chatNpc("I'm Hans. I've walked these grounds for years.")
            }
        }
    }
}
```

### RSMod v2
```kotlin
class HansScript @Inject constructor(
    private val repo: NpcRepository
) : PluginScript() {
    
    override fun ScriptContext.startup() {
        onOpNpc1(npcs.hans) {
            chatNpc("Welcome to Lumbridge Castle!")
            chatPlayer("Who are you?")
            chatNpc("I'm Hans. I've walked these grounds for years.")
        }
    }
}
```

**Key Differences:**
- OpenRune: `"npcs.hans"` string, manual queue
- RSMod: `npcs.hans` type-safe reference, suspend function

---

## 2. Woodcutting Skill

### OpenRune
```kotlin
class WoodcuttingPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {
    
    init {
        onObjectOption("objs.tree", "chop") {
            val tree = getTreeType(obj.id)
            
            if (player.getSkillLevel(Skill.WOODCUTTING) < tree.levelReq) {
                player.message("You need level ${tree.levelReq} woodcutting.")
                return@onObjectOption
            }
            
            player.queue {
                while (true) {
                    player.animate(879)  // Woodcutting anim
                    wait(4)
                    
                    if (successfulChop(player, tree)) {
                        player.inventory.add(tree.logId)
                        player.addXp(Skill.WOODCUTTING, tree.xp)
                    }
                    
                    if (treeDepleted()) {
                        transformObject(obj, tree.stumpId, tree.respawnTime)
                        break
                    }
                }
            }
        }
    }
}
```

### RSMod v2
```kotlin
class Woodcutting @Inject constructor(
    private val objTypes: ObjTypeList,
    private val locTypes: LocTypeList,
    private val locRepo: LocRepository,
    private val mapClock: MapClock,
) : PluginScript() {
    
    override fun ScriptContext.startup() {
        onOpLoc1(content.tree) { attempt(it.loc, it.type) }
        onOpLoc3(content.tree) { cut(it.loc, it.type) }
    }
    
    private fun ProtectedAccess.attempt(tree: BoundLocInfo, type: UnpackedLocType) {
        if (player.woodcuttingLvl < type.treeLevelReq) {
            mes("You need a Woodcutting level of ${type.treeLevelReq}...")
            return
        }
        
        if (inv.isFull()) {
            mes("Your inventory is too full...")
            return
        }
        
        val axe = findAxe(player, objTypes) ?: run {
            mes("You need an axe to chop down this tree.")
            return
        }
        
        anim(objTypes[axe].axeWoodcuttingAnim)
        cut(tree, type)
    }
    
    private fun ProtectedAccess.cut(tree: BoundLocInfo, type: UnpackedLocType) {
        // Coroutine-based, auto-handles delays
        delay(3)
        
        val (low, high) = cutSuccessRates(type, objTypes)
        val success = statRandom(stats.woodcutting, low, high)
        
        if (success) {
            val product = objTypes[type.treeLogs]
            spam("You get some ${product.name.lowercase()}.")
            statAdvance(stats.woodcutting, type.treeXp)
            invAdd(inv, product)
        }
        
        if (shouldDeplete(tree, type)) {
            locRepo.change(tree, type.treeStump, type.treeRespawnTime)
            resetAnim()
        }
        
        opLoc3(tree)  // Continue cutting
    }
}
```

**Key Differences:**
- OpenRune: Manual `wait(4)`, manual loop
- RSMod: `delay(3)`, coroutine continuation with `opLoc3()`

---

## 3. Combat Definition

### OpenRune
```kotlin
setCombatDef("npcs.goblin") {
    configs {
        attackSpeed = 4
        respawnDelay = 25
    }
    stats {
        hitpoints = 5
        attack = 1
        strength = 1
        defence = 1
        ranged = 1
        magic = 1
    }
    bonuses {
        attackStab = 0
        attackSlash = 0
        attackCrush = 0
        defenceStab = 0
        defenceSlash = 0
        defenceCrush = 0
    }
    anims {
        attack = 6184
        block = 6183
        death = 6182
    }
    drops {
        always {
            add("items.bones")
        }
        main(weight = 128) {
            add("items.bronze_sq_shield", min = 1, max = 1, weight = 3)
            add("items.coins", min = 5, max = 5, weight = 35)
            add("items.goblin_mail", min = 1, max = 1, weight = 28)
        }
    }
}
```

### RSMod v2
```kotlin
// Combat definitions typically done via JSON/type builders
// Or inline in script:

class GoblinCombat : PluginScript() {
    override fun ScriptContext.startup() {
        // Set up NPC combat
        onAiSpawn(npcs.goblin) { npc ->
            npc.setCombatStats(
                hitpoints = 5,
                attack = 1,
                strength = 1,
                defence = 1
            )
            npc.setAttackSpeed(4)
            npc.setRespawnDelay(25)
            npc.setAnimations(
                attack = anims.goblin_attack,
                block = anims.goblin_block,
                death = anims.goblin_death
            )
        }
        
        // Drops handled separately via drop tables
    }
}

// Or via type builder:
buildType<NpcTypeBuilder>("goblin") {
    name = "Goblin"
    size = 1
    // Combat stats set via params or separate system
}
```

**Key Differences:**
- OpenRune: Unified DSL for combat + drops
- RSMod: Separate systems, more flexible

---

## 4. Drop Tables

### OpenRune
```kotlin
dropTable("goblin_drops") {
    always {
        add("items.bones", min = 1, max = 1)
    }
    main(weight = 128) {
        add("items.bronze_sq_shield", weight = 3)
        add("items.coins", min = 5, max = 5, weight = 35)
        add("items.goblin_mail", weight = 28)
        add("items.water_rune", min = 6, max = 6, weight = 18)
        add("items.body_rune", min = 7, max = 7, weight = 15)
        add("items.bronze_bolts", min = 8, max = 8, weight = 5)
    }
    tertiary(weight = 5000) {
        add("items.goblin_champion_scroll", weight = 1)
    }
}
```

### RSMod v2
```kotlin
// Generated from your scraper:
register(npcs.goblin) {
    guaranteed(objs.bones)
    
    drop(objs.bronze_sq_shield, 1, rate = 3)
    drop(objs.coins, 5, rate = 35)
    drop(objs.goblin_mail, 1, rate = 28)
    drop(objs.water_rune, 6, rate = 18)
    drop(objs.body_rune, 7, rate = 15)
    drop(objs.bronze_bolts, 8, rate = 5)
    
    tertiary(objs.goblin_champion_scroll, 1, rate = 1, denominator = 5000)
}

// Or inline:
onNpcDeath(npcs.goblin) { npc, killer ->
    if (rollDrop(rate = 3, denominator = 128)) {
        spawnDrop(objs.bronze_sq_shield, npc.coords)
    }
    // ... more rolls
}
```

---

## 5. Quest Implementation

### OpenRune
```kotlin
class CooksAssistant : Quest("Cook's Assistant", 1) {
    
    override fun startQuest(player: Player) {
        player.setQuestStage(this, 1)
        player.setQuestVarbits(this, started = true)
    }
    
    override fun completeQuest(player: Player) {
        player.setQuestStage(this, 100)
        player.addXp(Skill.COOKING, 5000.0)
        player.inventory.add("items.gold_bar")
        player.setQuestVarbits(this, completed = true)
    }
    
    override fun getJournal(player: Player) = questJournal {
        description("The Lumbridge Castle cook is in a mess.")
        
        if (player.getQuestStage(this) == 0) {
            objective("Start the quest by talking to the Cook.")
        } else if (player.getQuestStage(this) < 100) {
            objective("Bring items to the Cook:") {
                hasItem("items.pot_of_flour", "Pot of flour")
                hasItem("items.egg", "Egg")
                hasItem("items.bucket_of_milk", "Bucket of milk")
            }
        } else {
            completed()
        }
    }
}

// Register
class CooksAssistantPlugin : PluginEvent() {
    override fun init() {
        registerQuest(CooksAssistant())
        
        onNpcOption("npcs.cook", "talk-to") {
            val quest = getQuest<CooksAssistant>()
            when (player.getQuestStage(quest)) {
                0 -> startDialogue()
                1 -> checkProgress()
                100 -> postQuestDialogue()
            }
        }
    }
}
```

### RSMod v2
```kotlin
// RSMod v2 doesn't have a built-in quest system yet
// You would implement it as:

class CooksAssistantScript : PluginScript() {
    
    // Quest state stored in varbits/attributes
    private val VARBIT_QUEST_STAGE = varbits.cooks_assistant_stage
    
    override fun ScriptContext.startup() {
        onOpNpc1(npcs.cook) {
            when (player.varbits[VARBIT_QUEST_STAGE]) {
                0 -> startQuest()
                1 -> checkItems()
                100 -> postQuest()
            }
        }
    }
    
    private suspend fun ProtectedAccess.startQuest() {
        chatNpc("What am I to do? The Duke's birthday party is today!")
        chatPlayer("Can I help?")
        chatNpc("Yes! I need: a pot of flour, an egg, and a bucket of milk.")
        
        val choice = choice("I'll help.", "I can't help.")
        if (choice == 1) {
            player.varbits[VARBIT_QUEST_STAGE] = 1
            mes("You have started the Cook's Assistant quest.")
        }
    }
    
    private suspend fun ProtectedAccess.checkItems() {
        val hasFlour = inv.contains(objs.pot_of_flour)
        val hasEgg = inv.contains(objs.egg)
        val hasMilk = inv.contains(objs.bucket_of_milk)
        
        if (hasFlour && hasEgg && hasMilk) {
            chatNpc("You brought everything! Thank you!")
            invDel(objs.pot_of_flour)
            invDel(objs.egg)
            invDel(objs.bucket_of_milk)
            statAdvance(stats.cooking, 5000.0)
            player.varbits[VARBIT_QUEST_STAGE] = 100
            mes("Quest complete!")
        } else {
            chatNpc("I still need: ${!hasFlour then "flour "}${!hasEgg then "egg "}${!hasMilk then "milk"}")
        }
    }
}
```

**Key Differences:**
- OpenRune: Built-in quest framework with journal DSL
- RSMod: Manual varbit tracking (more flexible, less structured)

---

## 6. Special Attacks

### OpenRune
```kotlin
class DragonClawsSpecial : Special {
    override fun accept(def: ItemDef, name: String): Boolean = 
        name.contains("dragon claws")
    
    override fun getDrainAmount(): Int = 50
    
    override fun handle(
        player: Player,
        victim: Entity,
        style: AttackStyle,
        type: AttackType,
        maxDamage: Int
    ): Boolean {
        // Complex 4-hit special
        val hit1 = calculateHit(player, victim, maxDamage)
        val hit2 = if (hit1 == 0) maxDamage / 2 else hit1 / 2
        val hit3 = if (hit1 == 0) maxDamage / 2 else hit1 / 2
        val hit4 = if (hit1 > 0) hit1 / 2 else if (hit2 > 0) hit2 else maxDamage
        
        player.dealDamage(victim, hit1, hit2, hit3, hit4)
        return true
    }
}

// Register
SpecialAttack.register(DragonClawsSpecial())
```

### RSMod v2
```kotlin
class DragonClawsSpecial : SpecialAttack {
    override val weaponNames = listOf("dragon claws")
    override val energyCost = 50
    
    override fun activate(player: Player, target: Entity): Boolean {
        val maxHit = calculateMaxHit(player, target)
        
        // 4-hit special
        val hits = listOf(
            calculateClawHit(maxHit, 1),
            calculateClawHit(maxHit, 2),
            calculateClawHit(maxHit, 3),
            calculateClawHit(maxHit, 4)
        )
        
        hits.forEach { hit ->
            target.queueHit(player, hit)
        }
        
        return true
    }
    
    private fun calculateClawHit(maxHit: Int, hitNumber: Int): Int {
        // Complex claw formula
        return when (hitNumber) {
            1 -> rollHit(maxHit)
            2, 3 -> if (hit1 == 0) maxHit / 2 else hit1 / 2
            4 -> // ...
        }
    }
}

// Register
registerSpecialAttack(DragonClawsSpecial())
```

---

## Summary Table

| Feature | OpenRune | RSMod v2 | Winner |
|---------|----------|----------|--------|
| **IDs** | String `"npcs.hans"` | Type-safe `npcs.hans` | RSMod |
| **Events** | Hash maps | EventBus | RSMod |
| **Async** | `wait(ticks)` | `delay(ticks)` | Tie |
| **DI** | Manual | Guice | RSMod |
| **DSL** | Specialized | Uniform | Tie |
| **Quests** | Built-in framework | Manual | OpenRune |
| **Combat Defs** | Unified DSL | Separate systems | OpenRune |
| **Type Safety** | Runtime | Compile-time | RSMod |
| **Beginner Friendly** | Easier | Steeper learning | OpenRune |
| **Production Ready** | Good | Better | RSMod |

---

*Choose based on your team's experience and priorities.*

