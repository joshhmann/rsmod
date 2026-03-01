# RSMod v2 Feature Completeness Matrix

This document tracks the granular progress of each skill towards "1:1 Vanilla Authenticity". Use this to identify exactly what is missing for a skill to be considered feature-complete.

---

## 🖐️ Thieving

| Category        | Component         | Status | Detail                                                                             |
| :-------------- | :---------------- | :----- | :--------------------------------------------------------------------------------- |
| **Mechanics**   | Pickpocketing     | ✅      | Base success formula, stun/damage loop, loot rolling.                              |
|                 | Stall Stealing    | ✅      | Animation, depletion, respawn, loot.                                               |
|                 | Chest Stealing    | ✅      | Trap checking, dismantling, stun damage.                                           |
|                 | Guard Interaction | ❌      | Guards/Citizens noticing thefts from stalls nearby.                                |
| **Pickpockets** | F2P Targets       | ✅      | Men, Women, Guards.                                                                |
|                 | P2P Targets       | 🟡      | Most standard humans (Knights, Paladins, Hero) done. Elves, Vyres, TzHaar missing. |
|                 | Master Farmer     | ✅      | Specialized seed loot rolling.                                                     |
| **Stalls**      | Standard          | ✅      | Veg, Baker, Tea, Silk, Seed, Fur, Fish, Silver, Spice, Gem.                        |
|                 | Specialized       | ❌      | Kourend stalls, TzHaar stalls.                                                     |
| **Gears/Boons** | Rogue Outfit      | ❌      | 100% chance for double loot (full set).                                            |
|                 | Dodgy Necklace    | ❌      | 25% chance to avoid stun on fail (consumes charge).                                |
|                 | Ardy Cloak        | ❌      | Global pickpocket success boosts.                                                  |
|                 | Glove of Silence  | ❌      | Success boost (break on damage).                                                   |

---

## ⛏️ Mining

| Category        | Component       | Status | Detail                                                            |
| :-------------- | :-------------- | :----- | :---------------------------------------------------------------- |
| **Mechanics**   | Basic Loop      | ✅      | Pickaxe detection, swing speed, depletion/respawn.                |
|                 | Formula         | 🟡      | Approximated OSRS formula; needs validation against Kronos logic. |
|                 | Shift-Drop      | ✅      | Handled by client/engine.                                         |
| **Tools**       | Pickaxes        | ✅      | Bronze through Crystal (requirements and anims).                  |
|                 | Specialized     | ❌      | Infernal pickaxe (auto-smelt), 3rd Age, Gilded.                   |
| **Content**     | Standard Ores   | ✅      | Copper through Runite.                                            |
|                 | Gem Rocks       | ❌      | Random gem table (Shilo Village).                                 |
|                 | Amethyst        | ❌      | Specialized high-level mining.                                    |
|                 | Granite/Sand    | ❌      | Quarries.                                                         |
| **Gears/Boons** | Prospector Gear | ❌      | 2.5% XP bonus for full set.                                       |
|                 | Mining Gloves   | ❌      | Depletion prevention probability.                                 |
|                 | Guild Boost     | ❌      | Invisible +7 boost in Mining Guild.                               |
|                 | Geode Drops     | ❌      | 1/250 chance for clue geodes.                                     |

---

## 🪓 Woodcutting

| Category        | Component       | Status | Detail                                          |
| :-------------- | :-------------- | :----- | :---------------------------------------------- |
| **Mechanics**   | Basic Loop      | ✅      | Axe detection, depletion, controller respawn.   |
|                 | Formula         | ✅      | Enum-based success rates matching OSRS tiers.   |
| **Tools**       | Hatchets        | ✅      | Bronze through Crystal.                         |
|                 | Specialized     | 🟡      | Infernal axe (logic present, needs validation). |
| **Content**     | Standard Trees  | ✅      | Tree through Magic.                             |
|                 | Redwoods        | ❌      | High-level multi-part trees.                    |
|                 | Blisterwood     | 🟡      | Logic present for Firemaking interaction.       |
| **Gears/Boons** | Lumberjack Gear | ❌      | 2.5% XP bonus.                                  |
|                 | Bird Nests      | ❌      | Random event drop during chopping.              |
|                 | Guild Boost     | ✅      | Invisible +7 boost implemented.                 |

---

## 🧱 Smithing (Planned)

| Category        | Component       | Status | Detail                                           |
| :-------------- | :-------------- | :----- | :----------------------------------------------- |
| **Mechanics**   | Smelting        | ❌      | Furnace interaction, coal usage requirements.    |
|                 | Forging         | ❌      | Anvil interaction, Bar selection UI.             |
|                 | Blast Furnace   | ❌      | Multi-player minigame environment.               |
| **Interface**   | Anvil UI        | ❌      | The standard 317/OSRS smithing selection screen. |
| **Gears/Boons** | Goldsmith Gaunt | ❌      | Significant XP boost for Gold smelting.          |

