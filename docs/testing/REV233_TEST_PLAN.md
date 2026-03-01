# Rev 233 Comprehensive Test Plan

**Goal:** Validate complete OSRS Rev 233 implementation in RSMod v2
**Tester Mindset:** Break everything. Find edge cases. Document gaps.

---

## 🎯 Test Categories

### 1. Gathering Skills (The Basics)
| Skill | Test Item | Expected | Test Method |
|-------|-----------|----------|-------------|
| Woodcutting | Regular tree | 25 XP, logs | Chop, verify XP + logs |
| Woodcutting | Oak tree | 37.5 XP, oak logs | Level 15 req |
| Woodcutting | Willow tree | 67.5 XP, willow logs | Level 30 req |
| Woodcutting | Maple tree | 100 XP, maple logs | Level 45 req |
| Mining | Copper ore | 17.5 XP, copper ore | Pickaxe required |
| Mining | Tin ore | 17.5 XP, tin ore | Pickaxe required |
| Mining | Iron ore | 35 XP, iron ore | Level 15 req |
| Mining | Coal | 50 XP, coal | Level 30 req |
| Fishing | Shrimp | 10 XP, raw shrimp | Net required |
| Fishing | Sardine | 20 XP, raw sardine | Bait + level 5 |
| Fishing | Trout | 50 XP, raw trout | Fly fishing, level 20 |

**Edge Cases:**
- [ ] Chop tree while inventory full
- [ ] Mine rock that depletes
- [ ] Fish spot moves
- [ ] Wrong tool equipped
- [ ] Level requirement not met

---

### 2. Artisan Skills (Processing)
| Skill | Action | Input | Output | XP |
|-------|--------|-------|--------|-----|
| Cooking | Shrimp | Raw shrimp | Shrimp | 30 |
| Cooking | Sardine | Raw sardine | Sardine | 40 |
| Cooking | Beef | Raw beef | Beef | 30 |
| Firemaking | Logs | Logs | Fire | 40 |
| Firemaking | Oak logs | Oak logs | Fire | 60 |
| Smithing | Bronze bar | 1 copper + 1 tin | Bronze bar | 6.2 |
| Smithing | Bronze dagger | 1 bronze bar | Bronze dagger | 12.5 |
| Crafting | Leather gloves | 1 leather | Leather gloves | 13.8 |
| Fletching | Arrow shafts | 1 log | 15 arrow shafts | 5 |
| Herblore | Attack potion | Guam + eye of newt | Attack potion (3) | 25 |

**Edge Cases:**
- [ ] Burn food (cooking level too low)
- [ ] Fail to light fire
- [ ] Smith without hammer
- [ ] Craft without needle/thread
- [ ] No knife for fletching

---

### 3. Thieving & Stealth
| Target | Level | XP | Loot | Stun? |
|--------|-------|-----|------|-------|
| Man/Woman | 1 | 8 | 3 gp, bronze bolts | 5s |
| Farmer | 10 | 14.5 | Seeds | Yes |
| HAM Member | 15 | 22.2 | Clues, jewelry | Yes |
| Warrior | 25 | 26 | Coins | Yes |
| Baker's stall | 5 | 16 | Cake, bread | No (guards) |

**Edge Cases:**
- [ ] Pickpocket from too far
- [ ] Stunned while attempting
- [ ] Full inventory
- [ ] Click through menu vs packet send
- [ ] Stall safe spots

---

### 4. Combat System
| Aspect | Test | Expected |
|--------|------|----------|
| Melee | Attack goblin | XP in Attack/Strength/Defence |
| Ranged | Shoot goblin with bow | XP in Ranged, ammo consumed |
| Magic | Cast wind strike | XP in Magic, runes consumed |
| Prayer | Turn on thick skin | Drain prayer, +5% defence |
| Eating | Eat shrimp | Heal 3 HP |
| Death | Die to npc | Respawn at Lumbridge, lose items |
| Retaliate | Get attacked | Auto-fight back |
| Flee | Run away | Stop combat, npc resets |

**Edge Cases:**
- [ ] Attack without weapon
- [ ] Magic without runes
- [ ] Ranged without ammo
- [ ] Prayer at 0 points
- [ ] Eat at full HP
- [ ] Die with full inventory
- [ ] Combat while skilling

---

### 5. NPC Interactions
| NPC Type | Interactions | Test |
|----------|--------------|------|
| Banker | Talk-to, Bank | Open bank interface |
| Shopkeeper | Talk-to, Trade | Open shop interface |
| Quest NPC | Talk-to | Dialog opens |
| Attackable | Attack, Talk-to | Combat starts/dialog |
| Random Event | Talk-to | Event triggers |

**NPCs to Test:**
- [ ] Man/Woman (Lumbridge) - Pickpocket, Talk-to, Attack
- [ ] Goblin (Lumbridge) - Attack
- [ ] Farmer (Lumbridge) - Pickpocket
- [ ] Banker (Lumbridge) - Bank
- [ ] Shopkeeper (General store) - Trade
- [ ] Hans (Lumbridge) - Age check
- [ ] Father Aereck - Talk-to

---

### 6. World Objects
| Object | Options | Test |
|--------|---------|------|
| Door | Open, Close | Walk through |
| Gate | Open, Close | Walk through |
| Ladder | Climb-up, Climb-down | Change floor |
| Staircase | Climb-up, Climb-down | Change floor |
| Bank booth | Bank | Open bank |
| Furnace | Smelt | Open smelt interface |
| Anvil | Smith | Open smith interface |
| Range | Cook | Cook food |
| Fire | Cook, Add logs | Multiple options |

**Locations to Test:**
- [ ] Lumbridge castle doors
- [ ] Lumbridge bank (3rd floor)
- [ ] Furnace (Lumbridge or Al Kharid)
- [ ] Fishing spots (Lumbridge swamp)
- [ ] Trees (all types around Lumbridge)

---

### 7. Banking & Economy
| Feature | Test | Expected |
|---------|------|----------|
| Deposit | Deposit 1 item | Item moves to bank |
| Deposit | Deposit all | All items move |
| Withdraw | Withdraw 1 | Item to inventory |
| Withdraw | Withdraw X | X items to inventory |
| Stack | Stackable items | Stack properly |
| Note | Note items | Noted form |
| Tab | Switch tabs | Different items |
| Search | Search items | Find items |

**Edge Cases:**
- [ ] Deposit with full bank
- [ ] Withdraw with full inventory
- [ ] Note unnoteable items
- [ ] Bank while in combat

---

### 8. Equipment & Inventory
| Feature | Test | Expected |
|---------|------|----------|
| Equip | Wear bronze sword | Stats increase |
| Unequip | Remove sword | Stats decrease |
| Two-hand | Equip 2h sword | Shield unequips |
| Ammo | Equip arrows | Ranged bonus |
| Drop | Drop item | Item on ground |
| Pickup | Take item | Item to inventory |
| Use | Use food | Eat/consume |
| Examine | Examine item | Description shown |

**Edge Cases:**
- [ ] Equip without level
- [ ] Equip 2 items in same slot
- [ ] Drop stackable item
- [ ] Pickup with full inventory

---

### 9. Prayer System
| Prayer | Level | Effect | Drain |
|--------|-------|--------|-------|
| Thick Skin | 1 | +5% Defence | 1 pt/tick |
| Burst of Strength | 4 | +5% Strength | 1 pt/tick |
| Clarity of Thought | 7 | +5% Attack | 1 pt/tick |
| Sharp Eye | 8 | +5% Ranged | 1 pt/tick |
| Mystic Will | 9 | +5% Magic | 1 pt/tick |

**Tests:**
- [ ] Activate prayer
- [ ] Prayer drains points
- [ ] Prayer at 0 deactivates
- [ ] Prayer bonus from gear
- [ ] Multiple prayers at once

---

### 10. Movement & Pathing
| Feature | Test | Expected |
|---------|------|----------|
| Walk | Click ground | Walk to spot |
| Run | Toggle run | Move faster, drain energy |
| Door | Walk through door | Auto-open, walk through |
| Obstacle | Walk around tree | Path around |
| Unreachable | Click across river | Path not found |
| Follow | Follow NPC | Stay adjacent |

**Edge Cases:**
- [ ] Walk while stunned
- [ ] Walk while teleporting
- [ ] Door stuck closed
- [ ] Gate requires quest

---

## 🚨 Critical Bugs to Watch For

### Priority 1 (Game-Breaking)
- [ ] Server crashes on skill action
- [ ] Duplication glitch
- [ ] Can walk through walls
- [ ] Items disappear
- [ ] XP not saving

### Priority 2 (Major)
- [ ] Wrong XP rates
- [ ] Missing animations
- [ ] Wrong drop tables
- [ ] NPCs don't spawn
- [ ] Doors don't work

### Priority 3 (Minor)
- [ ] Wrong chat messages
- [ ] Visual glitches
- [ ] Sound missing
- [ ] Typos in dialog

---

## 📝 Test Documentation Format

```
TEST: [Feature Name]
DATE: [Date]
TESTER: [Name]

SETUP:
- Player: [Name, levels]
- Location: [X, Y, Z]
- Items: [Inventory]

ACTION: [What was done]

EXPECTED: [What should happen]

ACTUAL: [What actually happened]

RESULT: [PASS/FAIL]

BUG DETAILS: [If failed]
- Error message:
- Stack trace:
- Screenshot:

NOTES: [Additional observations]
```

---

## 🎮 Gamer Checklist ("Does it feel like OSRS?")

**The Vibe Check:**
- [ ] Tick system feels right (0.6s)
- [ ] Animation timing correct
- [ ] Sound effects present
- [ ] Chat messages match
- [ ] Menu options in right order
- [ ] Right-click priority correct
- [ ] Bank tabs work
- [ ] Inventory shifts properly
- [ ] Equipment bonuses show
- [ ] XP drops visible

**The Nostalgia Check:**
- [ ] Lumbridge looks right
- [ ] Tutorial island works (if present)
- [ ] Music plays
- [ ] Login screen correct
- [ ] Character models right

---

## 🔧 Automated Test Scripts

Create bots that:
1. **Skill Loop Bot** - Train each skill for 5 minutes
2. **Combat Bot** - Fight every NPC type
3. **World Tour Bot** - Visit all locations
4. **Bank Stress Test** - Mass deposit/withdraw
5. **Item Interaction Bot** - Use every item type

---

## 📊 Progress Tracker

| Category | Tests | Passed | Failed | Skipped |
|----------|-------|--------|--------|---------|
| Gathering | 30 | 0 | 0 | 0 |
| Artisan | 40 | 0 | 0 | 0 |
| Combat | 25 | 0 | 0 | 0 |
| Thieving | 15 | 0 | 0 | 0 |
| NPCs | 20 | 0 | 0 | 0 |
| Objects | 25 | 0 | 0 | 0 |
| Banking | 20 | 0 | 0 | 0 |
| Equipment | 20 | 0 | 0 | 0 |
| Prayer | 10 | 0 | 0 | 0 |
| Movement | 15 | 0 | 0 | 0 |
| **TOTAL** | **220** | **0** | **0** | **0** |

---

## 🚀 Execution Plan

1. **Phase 1: Core Skills** (Gathering + Artisan)
   - Test woodcutting, mining, fishing
   - Test cooking, firemaking, smithing basics
   
2. **Phase 2: Combat**
   - Test melee, ranged, magic
   - Test NPC combat stats
   
3. **Phase 3: Advanced Skills**
   - Test thieving with stuns
   - Test prayer system
   
4. **Phase 4: World Systems**
   - Test banking, trading
   - Test doors, ladders
   
5. **Phase 5: Edge Cases**
   - Stress test
   - Boundary testing
   - Error handling

---

**READY TO TEST?** Let the breaking begin! 💥

