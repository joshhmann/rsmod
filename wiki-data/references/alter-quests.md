# Alter (RSMod v1) Quest Plugin Reference

This document extracts and documents quest plugin implementations from the Alter (RSMod v1) codebase, showing patterns for quest stage management, dialogue handling, and the translation to RSMod v2.

---

## Table of Contents

1. [Core Quest Framework (Alter v1)](#core-quest-framework-alter-v1)
2. [Quest Implementations](#quest-implementations)
3. [v1 → v2 Translation Guide](#v1--v2-translation-guide)
4. [Common Patterns](#common-patterns)

---

## Core Quest Framework (Alter v1)

### QuestScript Base Class

```kotlin
abstract class QuestScript(
    questKey: String, 
    val questVarp: String, 
    val rewards: QuestReward
) : PluginEvent() {
    protected val quest: Quest
    
    abstract fun subTitle(): String
    abstract fun questLog(player: Player): String
    abstract fun completedLog(player: Player): String
}
```

### Quest Registration

```kotlin
// Quests are registered via dbrow definitions
class CooksAssistant : QuestScript(
    "dbrows.quest_cooksassistant", 
    "varp.cookquest", 
    rewards { xp(Skills.COOKING, 300) }
)
```

### Quest Progress States

```kotlin
enum class QuestProgressState(val varp: Int) {
    NOT_STARTED(0),
    IN_PROGRESS(1),
    FINISHED(2),
}
```

### Quest Attribute System

```kotlin
// Define quest-specific attributes
private val GIVEN_EGG = quest.attribute(name = "GIVEN_EGG", default = false)
private val GIVEN_MILK = quest.attribute(name = "GIVEN_MILK", default = false)
private val GIVEN_FLOUR = quest.attribute(name = "GIVEN_FLOUR", default = false)

// Usage
GIVEN_MILK.set(player, true)
if (GIVEN_EGG.get(player)) { ... }
```

---

## Quest Implementations

### 1. Cook's Assistant

**File:** `OpenRune-Server/content/src/main/kotlin/org/alter/quest/CooksAssistant.kt`

#### Stage Definitions
- Stage 0: Not started
- Stage 1: In progress (collecting ingredients)
- Stage 2: Finished

#### Quest Attributes
```kotlin
private val GIVEN_EGG = quest.attribute(name = "GIVEN_EGG", default = false)
private val GIVEN_MILK = quest.attribute(name = "GIVEN_MILK", default = false)
private val GIVEN_FLOUR = quest.attribute(name = "GIVEN_FLOUR", default = false)
```

#### onOpNpc Handler Pattern
```kotlin
on<NpcClickEvent> {
    where { npc.id == "npcs.cook".asRSCM() }
    then {
        player.queue {
            when {
                quest.isQuestCompleted(player) -> dialogAfterCook(player)
                quest.questState(player) == QuestProgressState.IN_PROGRESS -> dialogDuringCook(player)
                else -> dialogQuestNotStarted(player)
            }
        }
    }
}
```

#### Stage Check Pattern
```kotlin
when {
    quest.isQuestCompleted(player) -> handleCompleted()
    quest.questState(player) == QuestProgressState.IN_PROGRESS -> handleInProgress()
    else -> handleNotStarted()
}
```

#### Item Check & Delivery Pattern
```kotlin
private suspend fun QueueTask.deliverItem(player: Player, itemId: String, message: String, flag: () -> Unit) {
    if (player.inventory.contains(itemId)) {
        player.inventory.remove(itemId)
        flag()
        chatPlayer(player, message)
    }
}

// Usage
deliverItem(player, "items.bucket_milk", "Here's a bucket of milk.") { 
    GIVEN_MILK.set(player, true) 
}
```

#### Quest Completion Pattern
```kotlin
if (allItemsDelivered(player)) {
    quest.advanceQuestStage(player)  // Advances to completion
}
```

#### Quest Journal Definition
```kotlin
override fun questLog(player: Player) = questJournal(player) {
    description("It's the <red>Duke of Lumbridge's</red> birthday...")
    
    objective("I need to find a <red>bucket of milk</red>.") {
        attribute(GIVEN_MILK, "I have given the cook a <red>bucket of milk</red>.").strike()
        hasItem("items.bucket_milk", "I have found a <red>bucket of milk</red>.")
    }
    
    objective("I need to find a <red>pot of flour</red>.") {
        attribute(GIVEN_FLOUR, "I have given the cook a <red>pot of flour</red>.").strike()
        hasItem("items.pot_flour", "I have found a pot of flour.")
    }
    
    objective("I need to find an <red>egg</red>.") {
        attribute(GIVEN_EGG, "I have given the cook an egg.").strike()
        hasItem("items.egg", "I have found an egg.")
    }
}
```

---

## RSMod v2 Quest Implementations (Ported Examples)

### 2. Rune Mysteries (RSMod v2)

**File:** `rsmod/content/quests/rune-mysteries/src/.../RuneMysteries.kt`

#### Stage Definitions
- Stage 0: Not started
- Stage 1: In progress (has talisman or package)
- Stage 2: Finished

#### v2 Plugin Structure
```kotlin
class RuneMysteries : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(rune_mysteries_npcs.duke_of_lumbridge) { startDukeDialogue(it.npc) }
        onOpNpc1(rune_mysteries_npcs.sedridor) { startSedridorDialogue(it.npc) }
        onOpNpc1(rune_mysteries_npcs.aubury) { startAuburyDialogue(it.npc) }
    }
}
```

#### v2 Stage-Based Dialogue Dispatch
```kotlin
private suspend fun ProtectedAccess.startDukeDialogue(npc: Npc) =
    startDialogue(npc) {
        when (getQuestStage(QuestList.rune_mysteries)) {
            0 -> dukeStartQuestDialogue()
            1 -> dukeInProgressDialogue()
            else -> dukeFinishedDialogue()
        }
    }
```

#### v2 Quest Start with Item Check
```kotlin
private suspend fun Dialogue.giveTalismanAndStartQuest() {
    if (!player.inv.hasFreeSpace()) {
        chatNpc(sad, "It seems your inventory is full.")
        return
    }

    val added = player.invAdd(player.inv, rune_mysteries_objs.digtalisman, 1).success
    if (!added) {
        chatNpc(sad, "It seems your inventory is full.")
        return
    }
    access.setQuestStage(QuestList.rune_mysteries, 1)
    chatNpc(happy, "Take this talisman to Sedridor, the head wizard, for me.")
}
```

#### v2 Quest Completion
```kotlin
private suspend fun Dialogue.auburyInProgressDialogue() {
    if (rune_mysteries_objs.research_package !in player.inv) {
        chatNpc(quiz, "If Sedridor has sent you, bring me his research package.")
        return
    }

    // ... dialogue ...

    val removed = player.invDel(player.inv, rune_mysteries_objs.research_package, 1).success
    if (!removed) {
        chatNpc(sad, "Hmm. It seems the package is no longer in your pack.")
        return
    }

    access.setQuestStage(QuestList.rune_mysteries, 2)
    access.showCompletionScroll(
        quest = QuestList.rune_mysteries,
        rewards = listOf("1 Quest Point", "Access to the Runecrafting skill"),
        itemModel = rune_mysteries_objs.digtalisman,
        questPoints = 1,
    )
}
```

---

### 3. The Restless Ghost (RSMod v2)

**File:** `rsmod/content/quests/restless-ghost/src/.../RestlessGhost.kt`

#### Stage Definitions
- Stage 0: Not started
- Stage 1: In progress (ghost haunting)
- Stage 2: Finished

#### Complex Object Interaction Pattern
```kotlin
override fun ScriptContext.startup() {
    onOpNpc1(restless_ghost_npcs.father_aereck) { startFatherAereckDialogue(it.npc) }
    onOpNpc1(restless_ghost_npcs.father_urhney) { startFatherUrhneyDialogue(it.npc) }
    onOpNpc1(restless_ghost_npcs.restless_ghost) { startGhostDialogue(it.npc) }
    onOpLoc1(restless_ghost_locs.skull_altar) { searchSkullAltar() }
    onOpLocU(restless_ghost_locs.coffin, restless_ghost_objs.ghostskull) { useSkullOnCoffin() }
}
```

#### Loc (Object) Interaction with Stage Check
```kotlin
private suspend fun ProtectedAccess.searchSkullAltar() {
    when (getQuestStage(QuestList.restless_ghost)) {
        0 -> mes("You search the altar but find nothing of interest.")
        1 -> {
            if (restless_ghost_objs.ghostskull in player.inv) {
                mes("You've already taken the skull.")
            } else {
                mes("You search the altar...")
                mes("You find a skull among the dusty bones.")
                val added = invAdd(player.inv, restless_ghost_objs.ghostskull, 1).success
                if (added) {
                    mes("You take the skull.")
                } else {
                    mes("You don't have room for the skull.")
                }
            }
        }
        else -> mes("You search the altar but find nothing of interest.")
    }
}
```

#### Item-on-Loc Interaction Pattern
```kotlin
private suspend fun ProtectedAccess.useSkullOnCoffin() {
    when (getQuestStage(QuestList.restless_ghost)) {
        0 -> mes("The coffin seems empty.")
        1 -> {
            if (restless_ghost_objs.ghostskull !in player.inv) {
                mes("You need the ghost's skull to place in the coffin.")
                return
            }

            mes("You place the skull back in the coffin.")

            val removed = invDel(player.inv, restless_ghost_objs.ghostskull, 1).success
            if (!removed) {
                mes("You don't seem to have the skull anymore.")
                return
            }

            completeQuest()
        }
        else -> mes("The ghost has been laid to rest.")
    }
}
```

---

### 4. Doric's Quest (RSMod v2)

**File:** `rsmod/content/quests/dorics-quest/src/.../DoricsQuest.kt`

#### Stage Definitions
- Stage 0: Not started
- Stage 1: In progress (gathering materials)
- Stage 2: Finished

#### Multiple Item Requirement Pattern
```kotlin
private suspend fun Dialogue.doricInProgressDialogue() {
    val hasClay = inventoryCount(dorics_quest_objs.clay) >= 6
    val hasCopper = inventoryCount(dorics_quest_objs.copper_ore) >= 4
    val hasIron = inventoryCount(dorics_quest_objs.iron_ore) >= 2

    when {
        hasClay && hasCopper && hasIron -> {
            chatPlayer(happy, "I've brought all the materials you asked for.")
            chatNpc(happy, "Excellent! This is exactly what I needed.")
            completeQuest()
        }
        hasClay || hasCopper || hasIron -> {
            chatNpc(quiz, "Did you bring me the materials?")
            val missing = buildList {
                if (!hasClay) add("6 clay")
                if (!hasCopper) add("4 copper ore")
                if (!hasIron) add("2 iron ore")
            }
            chatPlayer(neutral, "Not yet. I still need: ${missing.joinToString(", ")}.")
        }
        else -> {
            chatNpc(quiz, "Did you bring me 6 clay, 4 copper ore, and 2 iron ore?")
            chatPlayer(neutral, "Not yet. I'm still gathering them.")
        }
    }
}
```

#### Transactional Item Removal with Rollback
```kotlin
private suspend fun Dialogue.completeQuest() {
    val removedClay = player.invDel(player.inv, dorics_quest_objs.clay, 6).success
    val removedCopper = player.invDel(player.inv, dorics_quest_objs.copper_ore, 4).success
    val removedIron = player.invDel(player.inv, dorics_quest_objs.iron_ore, 2).success

    if (!removedClay || !removedCopper || !removedIron) {
        chatNpc(sad, "Hmm, you don't seem to have everything I asked for.")
        // Rollback any removed items
        if (removedClay) player.invAdd(player.inv, dorics_quest_objs.clay, 6)
        if (removedCopper) player.invAdd(player.inv, dorics_quest_objs.copper_ore, 4)
        if (removedIron) player.invAdd(player.inv, dorics_quest_objs.iron_ore, 2)
        return
    }

    val gaveCoins = player.invAdd(player.inv, dorics_quest_objs.coins, 180).success
    if (!gaveCoins) {
        chatNpc(sad, "You need one free inventory slot for your coin reward.")
        // Full rollback
        player.invAdd(player.inv, dorics_quest_objs.clay, 6)
        player.invAdd(player.inv, dorics_quest_objs.copper_ore, 4)
        player.invAdd(player.inv, dorics_quest_objs.iron_ore, 2)
        return
    }

    access.setQuestStage(QuestList.dorics_quest, 2)
    access.showCompletionScroll(...)
}
```

---

## v1 → v2 Translation Guide

### Event Handler Mapping

| Alter (v1) | RSMod (v2) | Notes |
|------------|------------|-------|
| `on<NpcClickEvent> { where { npc.id == "..." } }` | `onOpNpc1(npcs.name)` | Direct NPC ref |
| `on<LocClickEvent>` | `onOpLoc1(locs.name)` | Object interaction |
| `on<UseWithEvent>` | `onOpLocU(loc, obj)` | Item-on-object |
| `on<UseWithItemEvent>` | `onOpHeldU(objA, objB)` | Item-on-item |

### Quest API Translation

| Alter (v1) | RSMod (v2) |
|------------|------------|
| `quest.getQuestStage(player)` | `getQuestStage(QuestList.quest_name)` |
| `quest.setQuestStage(player, stage)` | `access.setQuestStage(QuestList.quest_name, stage)` |
| `quest.advanceQuestStage(player)` | `access.advanceQuestStage(QuestList.quest_name)` |
| `quest.isQuestCompleted(player)` | `isQuestComplete(QuestList.quest_name)` |
| `quest.questState(player)` | `getQuestStage(QuestList.quest_name)` comparison |
| `quest.attribute(name, default)` | Use varbits or direct stage checks |

### Dialogue Translation

**Alter v1:**
```kotlin
player.queue {
    chatNpc(player, "Hello", "npc.name".asRSCM())
    when (options(player, "Yes", "No")) {
        1 -> { }
        2 -> { }
    }
}
```

**RSMod v2:**
```kotlin
private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
    startDialogue(npc) {
        chatNpc(happy, "Hello")
        val choice = choice2("Yes", "No")
        when (choice) {
            1 -> { }
            2 -> { }
        }
    }
```

### Inventory Operations

| Alter (v1) | RSMod (v2) |
|------------|------------|
| `player.inventory.contains(item)` | `obj in player.inv` |
| `player.inventory.remove(item)` | `player.invDel(player.inv, obj, count).success` |
| `player.inventory.add(item)` | `player.invAdd(player.inv, obj, count).success` |
| `player.inventory.freeSlotCount` | `player.inv.freeSpace()` |

### String IDs vs Typed References

**Alter v1:**
```kotlin
"npcs.cook".asRSCM()
"items.bucket_milk"
"varp.cookquest"
```

**RSMod v2:**
```kotlin
// Typed references from generated configs
cooks_assistant_npcs.cook
cooks_assistant_objs.bucket_milk
QuestList.cooks_assistant.varp
```

---

## Common Patterns

### Quest Stage Pattern

```kotlin
// RSMod v2 Pattern
private suspend fun ProtectedAccess.startNpcDialogue(npc: Npc) =
    startDialogue(npc) {
        when (getQuestStage(QuestList.quest_name)) {
            0 -> startQuestDialogue()      // Not started
            1 -> inProgressDialogue()      // In progress
            else -> finishedDialogue()     // Complete
        }
    }
```

### Item Delivery Quest Pattern

```kotlin
private suspend fun Dialogue.inProgressDialogue() {
    val hasItem = quest_obj.item_name in player.inv
    
    when {
        hasItem -> {
            chatPlayer(happy, "I have the item.")
            val removed = player.invDel(player.inv, quest_obj.item_name, 1).success
            if (removed) {
                access.setQuestStage(QuestList.quest_name, 2)
                access.showCompletionScroll(...)
            }
        }
        else -> {
            chatNpc(quiz, "Have you found the item yet?")
            chatPlayer(sad, "Not yet.")
        }
    }
}
```

### Fetch Quest Pattern (Multiple Items)

```kotlin
private suspend fun Dialogue.inProgressDialogue() {
    val hasA = quest_obj.item_a in player.inv
    val hasB = quest_obj.item_b in player.inv
    val hasC = quest_obj.item_c in player.inv

    when {
        hasA && hasB && hasC -> completeQuest()
        hasA || hasB || hasC -> partialProgressDialogue()
        else -> noProgressDialogue()
    }
}
```

### Conditional Branching Pattern

```kotlin
when (getQuestStage(QuestList.quest_name)) {
    0 -> {
        // Quest not started
        when {
            required_item in player.inv -> skipToCompleteDialogue()
            else -> startQuestDialogue()
        }
    }
    1 -> {
        // In progress
        when {
            objective_completed -> advanceStage()
            else -> remindObjective()
        }
    }
}
```

---

## Quest Definition (QuestList)

```kotlin
public object QuestList {
    public val cooks_assistant: Quest = Quest(
        id = 1,
        name = "Cook's Assistant",
        varp = BaseVarps.cookquest,
        maxStage = 2,
        rewards = questRewards {
            xp(BaseStats.cooking, 300)
            extra("1 Quest Point")
        },
    )

    public val restless_ghost: Quest = Quest(
        id = 3,
        name = "The Restless Ghost",
        varp = BaseVarps.haunted,
        maxStage = 2,
        rewards = questRewards {
            xp(BaseStats.prayer, 1162)
            extra("1 Quest Point")
        },
    )
}
```

---

## Summary of Key Differences

1. **Event Registration**: v1 uses generic `on<NpcClickEvent>` with `where` clauses; v2 uses specific `onOpNpc1(ref)`
2. **Stage Management**: v1 uses `quest.attribute()` for sub-states; v2 uses stage numbers with `getQuestStage()`
3. **Item Handling**: v1 uses string IDs; v2 uses typed object references
4. **Dialogue**: v1 uses `player.queue { chatNpc(...) }`; v2 uses `startDialogue(npc) { chatNpc(...) }`
5. **Quest Completion**: v1 auto-handles via `advanceQuestStage()`; v2 uses explicit `showCompletionScroll()`
6. **Attributes**: v1 has rich attribute system; v2 prefers direct stage checks or varbits for complex state
