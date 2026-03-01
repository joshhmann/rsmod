# Kronos/Alter Quest Implementation Reference

> **Source**: Analysis of Kronos-184 (Java), OpenRune-Server (Kotlin/Alter), and RSMod v2 (Kotlin)
> **Last Updated**: 2026-02-22

---

## Table of Contents

1. [Overview](#overview)
2. [Quest Framework Comparison](#quest-framework-comparison)
3. [RSMod v2 Quest Implementation Patterns](#rsmod-v2-quest-implementation-patterns)
4. [F2P Quest Implementations](#f2p-quest-implementations)
   - [Cook's Assistant](#cooks-assistant)
   - [Sheep Shearer](#sheep-shearer)
   - [Rune Mysteries](#rune-mysteries)
   - [The Restless Ghost](#the-restless-ghost)
   - [Doric's Quest](#dorics-quest)
   - [Witch's Potion](#witchs-potion)
   - [Black Knights' Fortress](#black-knights-fortress)
5. [Quest Translation Guide](#quest-translation-guide)
6. [Common Patterns](#common-patterns)

---

## Overview

This document extracts quest implementation patterns from legacy Java codebases (Kronos, Tarnish) and modern Kotlin implementations (OpenRune/Alter, RSMod v2). It serves as a reference for understanding:

- Quest stage management systems
- Dialogue handling patterns
- Item requirement checks
- NPC interaction logic
- Reward handling
- Varp/varbit usage

---

## Quest Framework Comparison

### Kronos (Java) - Legacy Pattern

Kronos uses a traditional Java approach with:
- Static NPC action registration
- Direct player attribute manipulation
- No built-in quest framework abstraction

```java
// Example from Kronos HeadChef.java
static {
    NPCAction.register(HEAD_CHEF, "talk-to", (player, npc) -> player.dialogue(
        new NPCDialogue(npc, "Hello, welcome to the Cooking Guild...")));
}
```

**Key Observations:**
- No centralized quest registry
- Dialogue constructed imperatively
- Direct varp manipulation via player methods
- No type-safe symbol references

---

### OpenRune/Alter (Kotlin) - Modern DSL Pattern

OpenRune introduces a sophisticated quest DSL with:
- `QuestScript` base class with dbrow integration
- Quest attribute system for tracking progress
- Quest journal builder DSL
- Symbol-based referencing (RSCM)

```kotlin
// OpenRune CooksAssistant.kt
class CooksAssistant : QuestScript(
    "dbrows.quest_cooksassistant", 
    "varp.cookquest", 
    rewards { xp(Skills.COOKING, 300) }
) {
    private val GIVEN_EGG = quest.attribute(name = "GIVEN_EGG", default = false)
    private val GIVEN_MILK = quest.attribute(name = "GIVEN_MILK", default = false)
    private val GIVEN_FLOUR = quest.attribute(name = "GIVEN_FLOUR", default = false)

    override fun questLog(player: Player) = questJournal(player) {
        description("It's the <red>Duke of Lumbridge's</red> birthday...")
        objective("I need to find a <red>bucket of milk</red>...") {
            attribute(GIVEN_MILK, "I have given the cook...").strike()
            hasItem("items.bucket_milk", "I have found...")
        }
    }
}
```

**Key Features:**
- `QuestScript` abstract base class
- `QuestAttribute<T>` for persistent quest state
- `questJournal` DSL for quest log interface
- `QuestReward` builder for XP/items
- `advanceQuestStage()` for stage progression
- Symbol reference via `RSCM` (`.asRSCM()`)

---

### RSMod v2 (Kotlin) - Minimalist Pattern

RSMod v2 takes a simpler approach:
- Direct `PluginScript` extension
- Varp-based stage tracking
- Dialogue in separate suspend functions
- Symbol references via type-safe `find()`

```kotlin
// RSMod v2 CooksAssistant.kt
class CooksAssistant : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(cooks_assistant_npcs.cook) { startCookDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startCookDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.cooks_assistant)) {
                0 -> cookStartQuestDialogue()
                1 -> cookInProgressDialogue()
                else -> cookFinishedDialogue()
            }
        }
}
```

**Key Features:**
- `PluginScript` base class
- `QuestList` registry with varp mappings
- `getQuestStage()` / `setQuestStage()` extension functions
- `showCompletionScroll()` for quest completion UI
- `giveQuestReward()` for automated reward distribution

---

## RSMod v2 Quest Implementation Patterns

### 1. Quest Definition (QuestList.kt)

```kotlin
public object QuestList {
    public val cooks_assistant: Quest = Quest(
        id = 1,
        name = "Cook's Assistant",
        varp = BaseVarps.cookquest,  // VarpType for stage tracking
        maxStage = 2,                  // 0: Not started, 1: In progress, 2: Finished
        rewards = questRewards {
            xp(BaseStats.cooking, 300)
            extra("1 Quest Point")
        },
    )
}
```

**Fields:**
- `id`: Unique quest identifier
- `name`: Display name
- `varp`: VarpType for persisting quest stage
- `maxStage`: Maximum stage value (0 to maxStage)
- `rewards`: QuestReward with XP, items, and extra text

---

### 2. NPC Configuration Pattern

```kotlin
// CooksAssistantNpcs.kt
@file:Suppress("unused", "SpellCheckingInspection")
package org.rsmod.content.quests.cooksassistant.configs

import org.rsmod.api.type.refs.npc.NpcReferences

internal typealias cooks_assistant_npcs = CooksAssistantNpcs

internal object CooksAssistantNpcs : NpcReferences() {
    val cook = find("cook")  // Symbol-based lookup
}
```

**Pattern:**
- Create `configs/` subpackage
- Define `NpcReferences` subclass
- Use `find("symbol_name")` for type-safe ID resolution
- Export via `typealias` for convenient access

---

### 3. Object Configuration Pattern

```kotlin
// CooksAssistantObjs.kt
package org.rsmod.content.quests.cooksassistant.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias cooks_assistant_objs = CooksAssistantObjs

internal object CooksAssistantObjs : ObjReferences() {
    val bucket_milk = find("bucket_milk")
    val pot_flour = find("pot_flour")
    val egg = find("egg")
}
```

---

### 4. Main Quest Script Structure

```kotlin
class QuestName : PluginScript() {
    override fun ScriptContext.startup() {
        // Register NPC interactions
        onOpNpc1(quest_npcs.npc_name) { startNpcDialogue(it.npc) }
        
        // Register object interactions
        onOpLoc1(quest_locs.loc_name) { interactWithLoc() }
        onOpLocU(quest_locs.loc_name, quest_objs.obj_name) { useItemOnLoc() }
    }

    // Dialogue entry point with stage-based routing
    private suspend fun ProtectedAccess.startNpcDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.quest_name)) {
                0 -> npcStartQuestDialogue()
                1 -> npcInProgressDialogue()
                else -> npcFinishedDialogue()
            }
        }

    // Stage-specific dialogue implementations
    private suspend fun Dialogue.npcStartQuestDialogue() { /* ... */ }
    private suspend fun Dialogue.npcInProgressDialogue() { /* ... */ }
    private suspend fun Dialogue.npcFinishedDialogue() { /* ... */ }
}
```

---

### 5. Dialogue Patterns

#### Choice Dialogues

```kotlin
private suspend fun Dialogue.npcStartQuestDialogue() {
    chatNpc(quiz, "What am I to do?")
    val option = choice4(
        "What's wrong?", 1,
        "Can you make me a cake?", 2,
        "You don't look very happy.", 3,
        "Nice hat.", 4,
    )
    when (option) {
        1 -> { /* Handle choice 1 */ }
        2 -> { /* Handle choice 2 */ }
        3 -> { /* Handle choice 3 */ }
        4 -> { /* Handle choice 4 */ }
    }
}
```

#### Branching Dialogue with Nested Choices

```kotlin
private suspend fun Dialogue.explainQuestProblem() {
    chatNpc(sad, "It's the Duke's birthday today...")
    val option = choice2(
        "I'll help you. What do you need?", 1,
        "I can't help, I have important things to do.", 2,
    )
    when (option) {
        1 -> {
            chatPlayer(happy, "I'll help you. What do you need?")
            access.setQuestStage(QuestList.quest_name, 1)  // Advance to stage 1
        }
        2 -> chatPlayer(neutral, "I can't help...")
    }
}
```

---

### 6. Item Checking Patterns

#### Simple Presence Check

```kotlin
private suspend fun Dialogue.cookInProgressDialogue() {
    val hasMilk = cooks_assistant_objs.bucket_milk in player.inv
    val hasFlour = cooks_assistant_objs.pot_flour in player.inv
    val hasEgg = cooks_assistant_objs.egg in player.inv

    when {
        hasMilk && hasFlour && hasEgg -> completeQuest()
        hasMilk || hasFlour || hasEgg -> partialItemsDialogue()
        else -> noItemsDialogue()
    }
}
```

#### Count Check with Threshold

```kotlin
private suspend fun Dialogue.doricInProgressDialogue() {
    val hasClay = inventoryCount(dorics_quest_objs.clay) >= 6
    val hasCopper = inventoryCount(dorics_quest_objs.copper_ore) >= 4
    val hasIron = inventoryCount(dorics_quest_objs.iron_ore) >= 2
    // ...
}

private fun Dialogue.inventoryCount(obj: ObjType): Int {
    return player.inv.filterNotNull { it.id == obj.id }.sumOf { it.count }
}
```

---

### 7. Item Transaction Patterns

#### Safe Removal (with Rollback)

```kotlin
private suspend fun Dialogue.completeQuest() {
    // Attempt removal
    val removedMilk = player.invDel(player.inv, objs.bucket_milk, 1).success
    val removedFlour = player.invDel(player.inv, objs.pot_flour, 1).success
    val removedEgg = player.invDel(player.inv, objs.egg, 1).success

    // Check all succeeded
    if (!removedMilk || !removedFlour || !removedEgg) {
        chatNpc(sad, "Hmm, it seems you don't have all the ingredients.")
        // Return any removed items
        if (removedMilk) player.invAdd(player.inv, objs.bucket_milk, 1)
        if (removedFlour) player.invAdd(player.inv, objs.pot_flour, 1)
        if (removedEgg) player.invAdd(player.inv, objs.egg, 1)
        return
    }

    // Proceed with completion
    access.setQuestStage(QuestList.quest_name, 2)
    access.showCompletionScroll(...)
}
```

#### Conditional Removal

```kotlin
private suspend fun Dialogue.removeIngredients() {
    if (objs.rats_tail in player.inv) {
        player.invDel(player.inv, objs.rats_tail, 1)
    }
    // ...
}
```

---

### 8. Quest Completion Pattern

```kotlin
private suspend fun ProtectedAccess.completeQuest() {
    setQuestStage(QuestList.quest_name, 2)  // Mark as complete
    showCompletionScroll(
        quest = QuestList.quest_name,
        rewards = listOf("1 Quest Point", "300 Cooking XP"),
        itemModel = quest_objs.item_for_display,  // Optional 3D model
        questPoints = 1,
    )
}
```

---

### 9. Multi-NPC Quest Pattern (Rune Mysteries)

```kotlin
class RuneMysteries : PluginScript() {
    override fun ScriptContext.startup() {
        // Multiple NPCs with same quest
        onOpNpc1(npcs.duke_of_lumbridge) { startDukeDialogue(it.npc) }
        onOpNpc1(npcs.sedridor) { startSedridorDialogue(it.npc) }
        onOpNpc1(npcs.aubury) { startAuburyDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDukeDialogue(npc: Npc) =
        startDialogue(npc) {
            when (getQuestStage(QuestList.rune_mysteries)) {
                0 -> dukeStartQuestDialogue()
                1 -> dukeInProgressDialogue()
                else -> dukeFinishedDialogue()
            }
        }
    // ... similar for other NPCs
}
```

---

### 10. Object Interaction Pattern (Restless Ghost)

```kotlin
class RestlessGhost : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(locs.skull_altar) { searchSkullAltar() }
        onOpLocU(locs.coffin, objs.ghostskull) { useSkullOnCoffin() }
    }

    private suspend fun ProtectedAccess.searchSkullAltar() {
        when (getQuestStage(QuestList.restless_ghost)) {
            0 -> mes("You search the altar but find nothing...")
            1 -> {
                if (objs.ghostskull in player.inv) {
                    mes("You've already taken the skull.")
                } else {
                    mes("You search the altar...")
                    val added = invAdd(player.inv, objs.ghostskull, 1).success
                    if (added) mes("You take the skull.")
                }
            }
            else -> mes("You search the altar but find nothing...")
        }
    }
}
```

---

## F2P Quest Implementations

### Cook's Assistant

**Quest ID**: 1  
**Varp**: `cookquest`  
**Stages**: 0 (Not started), 1 (In progress), 2 (Complete)  
**Rewards**: 1 QP, 300 Cooking XP

**Flow:**
1. Player talks to Cook in Lumbridge Castle kitchen
2. Accept quest -> Stage 1
3. Gather: bucket of milk, egg, pot of flour
4. Return items to Cook -> Stage 2 (Complete)

**Key Code Patterns:**

```kotlin
// Stage-based dialogue routing
when (getQuestStage(QuestList.cooks_assistant)) {
    0 -> cookStartQuestDialogue()
    1 -> cookInProgressDialogue()
    else -> cookFinishedDialogue()
}

// Item checking
val hasMilk = cooks_assistant_objs.bucket_milk in player.inv
val hasFlour = cooks_assistant_objs.pot_flour in player.inv
val hasEgg = cooks_assistant_objs.egg in player.inv

// Complete quest when all items present
if (hasMilk && hasFlour && hasEgg) {
    // Remove items
    player.invDel(player.inv, cooks_assistant_objs.bucket_milk, 1)
    player.invDel(player.inv, cooks_assistant_objs.pot_flour, 1)
    player.invDel(player.inv, cooks_assistant_objs.egg, 1)
    
    access.setQuestStage(QuestList.cooks_assistant, 2)
    access.showCompletionScroll(...)
}
```

---

### Sheep Shearer

**Quest ID**: 2  
**Varp**: `sheep`  
**Stages**: 0 (Not started), 1 (In progress), 2 (Complete)  
**Rewards**: 1 QP, 150 Crafting XP, 60 coins  
**Status**: ⚠️ NOT IMPLEMENTED in RSMod v2

**Expected Flow:**
1. Talk to Fred the Farmer (north of Lumbridge)
2. Accept quest -> Stage 1
3. Shear 20 sheep (using shears on sheep)
4. Return 20 wool to Fred -> Stage 2 (Complete)

**Implementation Notes:**
- Requires `onOpNpcU` for shears-on-sheep interaction
- Requires wool counting mechanism
- Fred the Farmer NPC: ID 731/732 (verify in npc.sym)

---

### Rune Mysteries

**Quest ID**: 8  
**Varp**: `runemysteries`  
**Stages**: 0 (Not started), 1 (In progress), 2 (Complete)  
**Rewards**: 1 QP, Access to Runecrafting skill

**Flow:**
1. Talk to Duke of Lumbridge in castle -> Receive talisman -> Stage 1
2. Take talisman to Sedridor in Wizards' Tower
3. Sedridor replaces talisman with research package
4. Take package to Aubury in Varrock -> Stage 2 (Complete)

**Key Code Patterns:**

```kotlin
// Item giving with inventory check
private suspend fun Dialogue.giveTalismanAndStartQuest() {
    if (!player.inv.hasFreeSpace()) {
        chatNpc(sad, "It seems your inventory is full...")
        return
    }
    val added = player.invAdd(player.inv, objs.digtalisman, 1).success
    if (added) {
        access.setQuestStage(QuestList.rune_mysteries, 1)
        chatNpc(happy, "Take this talisman to Sedridor...")
    }
}

// Item exchange (talisman -> package)
private suspend fun Dialogue.handInTalismanToSedridor() {
    val removed = player.invDel(player.inv, objs.digtalisman, 1).success
    if (!removed) { /* Handle failure */ return }
    
    val added = player.invAdd(player.inv, objs.research_package, 1).success
    if (!added) {
        // Rollback: return talisman
        player.invAdd(player.inv, objs.digtalisman, 1)
        return
    }
}
```

---

### The Restless Ghost

**Quest ID**: 3  
**Varp**: `haunted`  
**Stages**: 0 (Not started), 1 (In progress), 2 (Complete)  
**Rewards**: 1 QP, 1,162 Prayer XP

**Flow:**
1. Talk to Father Aereck in Lumbridge church -> Stage 1
2. Talk to Father Urhney in Lumbridge Swamp -> Receive Ghostspeak amulet
3. Talk to Restless Ghost in cemetery (requires amulet)
4. Ghost reveals missing skull at Wizards' Tower
5. Search skull altar in Wizards' Tower basement -> Receive skull
6. Use skull on coffin -> Stage 2 (Complete)

**Key Code Patterns:**

```kotlin
// Object interaction with quest stage check
private suspend fun ProtectedAccess.searchSkullAltar() {
    when (getQuestStage(QuestList.restless_ghost)) {
        1 -> {
            if (objs.ghostskull in player.inv) {
                mes("You've already taken the skull.")
            } else {
                val added = invAdd(player.inv, objs.ghostskull, 1).success
                if (added) mes("You take the skull.")
            }
        }
        else -> mes("You search the altar but find nothing...")
    }
}

// Use item on location
private suspend fun ProtectedAccess.useSkullOnCoffin() {
    when (getQuestStage(QuestList.restless_ghost)) {
        1 -> {
            if (objs.ghostskull !in player.inv) {
                mes("You need the ghost's skull...")
                return
            }
            invDel(player.inv, objs.ghostskull, 1)
            completeQuest()
        }
        else -> mes("The ghost has been laid to rest.")
    }
}
```

---

### Doric's Quest

**Quest ID**: 7  
**Varp**: `doricquest`  
**Stages**: 0 (Not started), 1 (In progress), 2 (Complete)  
**Rewards**: 1 QP, 1,300 Mining XP, 180 coins, Anvil access

**Flow:**
1. Talk to Doric north of Falador -> Stage 1
2. Gather: 6 clay, 4 copper ore, 2 iron ore
3. Return to Doric -> Stage 2 (Complete)

**Key Code Patterns:**

```kotlin
// Count-based requirements
private suspend fun Dialogue.doricInProgressDialogue() {
    val hasClay = inventoryCount(objs.clay) >= 6
    val hasCopper = inventoryCount(objs.copper_ore) >= 4
    val hasIron = inventoryCount(objs.iron_ore) >= 2

    when {
        hasClay && hasCopper && hasIron -> completeQuest()
        hasClay || hasCopper || hasIron -> {
            // Report missing items
            val missing = buildList {
                if (!hasClay) add("6 clay")
                if (!hasCopper) add("4 copper ore")
                if (!hasIron) add("2 iron ore")
            }
            chatPlayer(neutral, "Not yet. I still need: ${missing.joinToString(", ")}.")
        }
        else -> chatPlayer(neutral, "Not yet. I'm still gathering them.")
    }
}

// Completion with item reward
private suspend fun Dialogue.completeQuest() {
    // Remove ores
    player.invDel(player.inv, objs.clay, 6)
    player.invDel(player.inv, objs.copper_ore, 4)
    player.invDel(player.inv, objs.iron_ore, 2)
    
    // Give coins
    player.invAdd(player.inv, objs.coins, 180)
    
    access.setQuestStage(QuestList.dorics_quest, 2)
    access.showCompletionScroll(...)
}
```

---

### Witch's Potion

**Quest ID**: 6  
**Varp**: `hetty`  
**Stages**: 0 (Not started), 1 (In progress), 2 (Complete)  
**Rewards**: 1 QP, 325 Magic XP

**Flow:**
1. Talk to Hetty in Rimmington -> Stage 1
2. Gather: rat's tail, eye of newt, burnt meat, onion
3. Return to Hetty -> Stage 2 (Complete)

**Key Code Patterns:**

```kotlin
// Inventory-based requirements
private suspend fun Dialogue.hasAllIngredients(): Boolean {
    val inv = player.inv
    return inv.contains(objs.rats_tail) &&
        inv.contains(objs.eye_of_newt) &&
        inv.contains(objs.burnt_meat) &&
        inv.contains(objs.onion)
}

// Using giveQuestReward() for XP-only rewards
private suspend fun Dialogue.completeQuest() {
    access.setQuestStage(QuestList.witchs_potion, 2)
    access.giveQuestReward(QuestList.witchs_potion)  // Gives 325 Magic XP
    access.showCompletionScroll(
        quest = QuestList.witchs_potion,
        rewards = listOf("1 Quest Point", "325 Magic XP"),
        questPoints = 1,
    )
}
```

---

### Black Knights' Fortress

**Quest ID**: 11  
**Varp**: `hunt`  
**Stages**: 0, 1, 2, 3 (4 stages for multi-step quest)  
**Rewards**: 3 QP, 2,500 coins  
**Requirements**: 12 Quest Points

**Flow:**
1. Talk to Sir Amik Varze in Falador (requires 12 QP) -> Stage 1
2. Infiltrate fortress with iron chainbody + bronze med helm
3. Listen at grill to overhear witch's plan
4. Use cabbage on hole to sabotage cauldron -> Stage 2
5. Return to Sir Amik Varze -> Stage 3 (Complete)

**Key Code Patterns:**

```kotlin
// Multi-stage quest with different behaviors
private suspend fun ProtectedAccess.startSirAmikDialogue(npc: Npc) =
    startDialogue(npc) {
        when (getQuestStage(QuestList.black_knights_fortress)) {
            0 -> sirAmikStartQuestDialogue()
            1 -> sirAmikInProgressDialogue()
            2 -> sirAmikSabotagedDialogue()
            else -> sirAmikFinishedDialogue()
        }
    }

// Conditional item removal (may not have item)
private suspend fun Dialogue.completeQuest() {
    // Remove dossier if player still has it
    if (objs.bk_dossier in player.inv) {
        player.invDel(player.inv, objs.bk_dossier, 1)
    }
    
    access.setQuestStage(QuestList.black_knights_fortress, 3)
    access.showCompletionScroll(...)
}
```

---

## Quest Translation Guide

### From OpenRune/Alter to RSMod v2

| OpenRune Pattern | RSMod v2 Equivalent |
|-----------------|---------------------|
| `QuestScript` base class | `PluginScript` base class |
| `quest.attribute()` for tracking | Use varbits or inventory checks |
| `quest.advanceQuestStage()` | `access.setQuestStage()` |
| `quest.isQuestCompleted()` | `access.isQuestComplete()` |
| `quest.giveQuestReward()` | `access.giveQuestReward()` |
| `QuestReward` builder | `questRewards` DSL in QuestList |
| RSCM symbols (`"npcs.cook".asRSCM()`) | `NpcReferences.find("cook")` |
| `player.inventory.contains(itemId)` | `obj in player.inv` |
| `player.inventory.remove(itemId)` | `player.invDel(...)` |
| `suspend fun QueueTask.dialogue()` | `suspend fun Dialogue.xxx()` |
| `chatNpc()` with expression | `chatNpc(expression, "text")` |
| `options()` | `choice2()`, `choice3()`, `choice4()` |

### From Kronos Java to RSMod v2

| Kronos Pattern | RSMod v2 Equivalent |
|---------------|---------------------|
| `NPCAction.register(id, "talk-to", handler)` | `onOpNpc1(npc) { handler() }` |
| `player.dialogue(new NPCDialogue(...))` | `startDialogue(npc) { chatNpc(...) }` |
| Direct varp manipulation | `getQuestStage()` / `setQuestStage()` |
| Raw IDs (`2658`) | Symbol references (`find("head_chef")`) |
| Static registration in `static {}` | `override fun ScriptContext.startup()` |

---

## Common Patterns

### Quest Stage Constants

```kotlin
// Define as companion object or top-level constants
companion object {
    const val STAGE_NOT_STARTED = 0
    const val STAGE_IN_PROGRESS = 1
    const val STAGE_COMPLETED = 2
}
```

### Dialogue Expressions

Available expressions:
- `neutral` - Default
- `happy` - Smiling
- `sad` - Frowning
- `quiz` - Questioning
- `confused` - Confused
- `laugh` - Laughing
- `angry` - Angry

```kotlin
chatNpc(happy, "Excellent work!")
chatPlayer(sad, "I don't have the items...")
```

### Inventory Space Check

```kotlin
if (!player.inv.hasFreeSpace()) {
    chatNpc(sad, "You need a free inventory slot.")
    return
}
```

### Multi-NPC Coordination

For quests with multiple NPCs, use consistent stage checking:

```kotlin
// NPC 1 starts quest
when (getQuestStage(QuestList.quest_name)) {
    0 -> startQuestDialogue()
    1 -> inProgressDialogue()
    else -> finishedDialogue()
}

// NPC 2 only responds after quest started
when (getQuestStage(QuestList.quest_name)) {
    0 -> beforeQuestDialogue()
    1 -> inProgressDialogue()
    else -> finishedDialogue()
}
```

### Error Handling

Always handle item transaction failures:

```kotlin
val added = player.invAdd(player.inv, obj, count).success
if (!added) {
    chatNpc(sad, "You don't have room for this item.")
    return
}
```

---

## Reference Files

### RSMod v2 Quest Implementations

| Quest | File Path |
|-------|-----------|
| Cook's Assistant | `rsmod/content/quests/cooks-assistant/.../CooksAssistant.kt` |
| Rune Mysteries | `rsmod/content/quests/rune-mysteries/.../RuneMysteries.kt` |
| The Restless Ghost | `rsmod/content/quests/restless-ghost/.../RestlessGhost.kt` |
| Doric's Quest | `rsmod/content/quests/dorics-quest/.../DoricsQuest.kt` |
| Witch's Potion | `rsmod/content/quests/witchs-potion/.../WitchsPotion.kt` |
| Black Knights' Fortress | `rsmod/content/quests/black-knights-fortress/.../BlackKnightsFortress.kt` |

### OpenRune/Alter Reference

| Component | File Path |
|-----------|-----------|
| QuestScript | `OpenRune-Server/content/.../quest/manager/QuestScript.kt` |
| Quest | `OpenRune-Server/content/.../quest/manager/Quest.kt` |
| QuestJournal | `OpenRune-Server/content/.../quest/manager/QuestJournal.kt` |
| CooksAssistant | `OpenRune-Server/content/.../quest/CooksAssistant.kt` |

### Kronos Legacy

**Note**: Kronos does not have a centralized quest framework. Quest logic is typically:
- Embedded in NPC action handlers
- Uses direct player attribute manipulation
- No quest registry abstraction

---

## Notes for Implementation

### Sheep Shearer (Not Yet Implemented)

**Missing Implementation Details:**
1. Create `rsmod/content/quests/sheep-shearer/` module
2. Define NPC configs for Fred the Farmer
3. Implement shears-on-sheep interaction using `onOpNpcU`
4. Track wool count in inventory or use quest attribute
5. Dialogue with Fred for turn-in

**Required NPCs:**
- Fred the Farmer (verify ID in `rsmod/.data/symbols/npc.sym`)

**Required Items:**
- Shears
- Wool (20x for quest completion)

---

*End of Document*
