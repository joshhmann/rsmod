# Questing & Area Implementation Guidelines

These guidelines ensure a consistent, systematic approach to building out the F2P world.

---

## 🏗️ Part 1: Quest Implementation

Every quest must follow the **"3-State, n-Stage"** model.

### 1. State Management
*   **Master Varp**: A single `Varp` (e.g., `quest_cooks_assistant`) tracks the global state:
    *   `0`: NOT_STARTED (Grey)
    *   `1`: IN_PROGRESS (Yellow)
    *   `2`: FINISHED (Green)
*   **Quest Stages**: A persistent numeric attribute (e.g., `stages["cooks_assistant"]`) tracks the granular progress (Stage 1: Talked to cook, Stage 2: Found milk, etc.).

### 2. Dialogue Branching
Dialogues must check the current stage before presenting options:
```kotlin
onOpNpc1(npcs.cook) {
    val stage = quest.getStage(player)
    when (stage) {
        0 -> startQuestDialogue()
        1 -> checkItemsDialogue()
        else -> thankYouDialogue()
    }
}
```

### 3. Guidelines for Interactions
*   **Item Checks**: Always use the `inv.hasItem()` helper.
*   **Varp Syncing**: Ensure the Master Varp is updated immediately when a quest finishes so the quest log turns green.
*   **Rewards**: Use the `QuestReward` DSL to handle XP, Items, and the Completion Scroll interface.

---

## 🏛️ Part 2: Area Implementation

An area should not just exist; it must feel "alive."

### 1. The Interaction Checklist
When claiming an area (e.g., "I'm doing Draynor"), you are responsible for:
*   **Locs**: All doors, ladders, gates, and trapdoors must work.
*   **NPC Density**: Every generic "Man" or "Woman" should have at least one branch of dialogue (e.g., "Hello traveler," "Nice weather").
*   **Utility**: Bankers, Shopkeepers, and Tool Leprechauns must be first priority.
*   **Environment**: Benches should be sit-able (optional), and regional music defined.

### 2. Implementation Pattern (Generic NPCs)
Use `onOpNpc1` with a `GameRandom` weighted dialogue system to avoid repetitive text.

```kotlin
onOpNpc1(npcs.man) {
    val roll = random.of(1, 3)
    when (roll) {
        1 -> chat("How's it going?")
        2 -> chat("I've been better.")
        3 -> chat("Beautiful day for a walk.")
    }
}
```

---

## 🏁 Part 3: The "F2P Ready" Requirement

For a quest or area to be marked as **[DONE]** in the Master Matrix, it must:
1.  **Work with Bots**: A bot script must be able to complete the quest/interaction using the `sdk`.
2.  **State Persistence**: Logging out and back in must preserve progress.
3.  **Wiki Accuracy**: Interaction options (Op1, Op2) must match rev-233 OSRS behavior.

