# Alter v1 → RSMod v2 Translation Cheatsheet

Alter is RSMod v1 (Kotlin, rev 228 donor reference). RSMod v2 targets rev 233 and has a different plugin architecture.
This cheatsheet maps every common pattern side-by-side.

---

## 1. Plugin Registration

### Alter v1
```kotlin
class WoodcuttingPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {
    init {
        // register handlers here
    }
}
```

### RSMod v2
```kotlin
class Woodcutting @Inject constructor(
    private val objTypes: ObjTypeList,   // inject what you need
) : PluginScript() {
    override fun ScriptContext.startup() {
        // register handlers here
    }
}
```

**Key differences:**
- No `PluginRepository`, `World`, or `Server` constructor args — use `@Inject` for dependencies
- Registration happens in `startup()` not `init {}`
- DI is Guice; bind extra implementations via `PluginModule` if needed
- The class is auto-discovered — just extend `PluginScript` and place in the right module

---

## 2. Event Handler Syntax

### Alter v1 — string-based IDs
```kotlin
onNpcOption(npc = "npc.banker", option = "talk-to") { /* this = PluginContext */ }
onObjOption(obj = "object.bank_booth", option = "bank") { }
onItemOption(item = "item.logs", option = "use") { }
onGroundItemOption(item = "item.logs", option = "take") { }
onPlayerOption(option = "attack") { }
```

### RSMod v2 — typed references + content groups
```kotlin
// By specific NPC type ID
onOpNpc1(npcs.banker_lumbridge) { /* this = ProtectedAccess, it = NpcEvents.Op1 */ }

// By content group (covers ALL bank booths at once — preferred for generic content)
onOpLoc2(content.bank_booth) { }

// By specific loc type
onOpLoc1(locs.oak_tree_10820) { }

// Inventory item click
onOpObj1(objs.logs) { }

// Ground item pickup
onOpObj1(objs.logs) { }   // same event, engine resolves context

// Use item on NPC
onOpNpcU(content.banker, objs.coins) { }

// Use item on loc
onOpLocU(content.bank_booth, objs.noted_item) { }
```

> **CRITICAL WARNING — Duplicate Event Registration**
>
> If two scripts both register `onOpNpc1(npcs.x)` for the same NPC (or the same item combo for `onOpHeldU`),
> the **second script to load is entirely disabled** at startup — not just the duplicate handler, but ALL
> handlers in that script. The server logs `Skipping script startup for ...` but does NOT crash, so this
> failure is silent unless you check the log.
>
> **Before registering any handler**, grep the codebase:
> ```bash
> grep -rn "onOpNpc1(npcs.cook" rsmod/content --include="*.kt" | grep -v /build/
> ```
> If another script already handles that NPC/loc/obj, you must either extend that script or coordinate ownership.

**Op number mapping (both frameworks):**
- Op1 = left-click / first option
- Op2 = second right-click option
- Op3 = third option
- Op4/Op5 = fourth/fifth options

**Ap (approach) variants** — fired when player is walking toward target, not yet adjacent:
```kotlin
onApNpc1(content.banker) { }   // e.g. start dialogue approach
onApLoc1(content.tree) { }
```

---

## 3. Event Context — Accessing Player, NPC, Loc

### Alter v1
```kotlin
onNpcOption(npc = "npc.banker", option = "talk-to") {
    // `player` is in scope
    // `npc` is in scope
    player.message("Hello")
    npc.forceChat("Welcome!")
}
```

### RSMod v2
```kotlin
onOpNpc1(content.banker) { event ->
    // `this` = ProtectedAccess (wraps Player securely)
    // `event.npc` = the clicked Npc
    // `player` accessible via ProtectedAccess
    mes("Hello")
    event.npc.say("Welcome!")
}
```

For Loc interactions:
```kotlin
onOpLoc1(content.tree) { event ->
    val loc = event.loc       // the LocInfo (id, coords, shape, rotation)
    val locType = event.type  // UnpackedLocType (params, name, etc.)
}
```

---

## 4. XP Granting

### Alter v1
```kotlin
player.addXp(Skills.WOODCUTTING, 25.0)
player.addXp(Skills.FIREMAKING, 40.0)
```

### RSMod v2 (inside ProtectedAccess / suspend context)
```kotlin
statAdvance(stats.woodcutting, 25.0)
statAdvance(stats.firemaking, 40.0)

// With XP modifier support (skill outfits, etc.) — used in woodcutting:
val xp = treeXp * xpMods.get(player, stats.woodcutting)
statAdvance(stats.woodcutting, xp)
```

**Stat refs** (`stats.*`):
`attack, defence, strength, hitpoints, ranged, prayer, magic, cooking, woodcutting,
fletching, fishing, firemaking, crafting, smithing, mining, herblore, agility,
thieving, slayer, farming, runecrafting, hunter, construction`

---

## 5. Animations & Graphics

### Alter v1
```kotlin
player.animate(Animation.WOODCUTTING_RUNE_AXE)  // int ID
player.graphic(245, 124)                          // gfx ID, height
npc.animate(Animation.BANKER_IDLE)
```

### RSMod v2 (inside ProtectedAccess)
```kotlin
anim(seqs.woodcutting_rune_axe)     // SeqType ref
anim(seqs.woodcutting_rune_axe, delay = 0)
resetAnim()
graphic(spotanims.levelup_woodcutting)  // SpotanimType ref
graphic(spotanims.levelup_woodcutting, delay = 0, height = 124)
resetGraphic()
```

**Getting animation from a param (woodcutting pattern):**
```kotlin
val axeAnim = objTypes[axe].param(params.skill_anim)  // SeqType from obj param
anim(axeAnim)
```

**NPC animations** — set via cache params (no runtime call needed for idle/walk BAS):
```kotlin
// In NPC type config (not in plugin code):
// params.attack_anim, params.defend_anim, params.death_anim
```

---

## 6. Messages to Player

### Alter v1
```kotlin
player.message("You chop some logs.")
player.message("You need a higher Woodcutting level.", type = ChatMessageType.GAME)
```

### RSMod v2 (inside ProtectedAccess)
```kotlin
mes("You chop some logs.")
mes("You need a higher Woodcutting level.")  // same function, engine handles type
```

> **Warning — `player.mes()` vs `mes()`:**
> Engine code (AdminCommands.kt, RealmConfigCommands.kt) uses `player.mes("text")` because those
> run in a `Player` extension context. Inside `ProtectedAccess` handlers (all content scripts),
> use `mes("text")` directly — do NOT prefix with `player.`.

**Other output functions:**
```kotlin
// Send a sound effect
soundSynth(synths.chop_tree)

// Force chat bubble above player head
// (not commonly used for player, used on NPCs)
event.npc.say("Moo")
```

---

## 7. Inventory Operations

### Alter v1
```kotlin
val tx = player.inventory.add(item = "item.logs", amount = 1)
if (tx.hasFailed()) {
    player.world.spawn(GroundItem(item = getRSCM("item.logs"), ...))
}
player.inventory.remove(item = "item.tinderbox", amount = 1)
```

### RSMod v2 (inside ProtectedAccess)

**Dependency injection required for `invAddOrDrop`:**
```kotlin
// Your PluginScript must inject ObjTypeList to use invAddOrDrop:
class MySkill @Inject constructor(
    private val objTypes: ObjTypeList,  // required for invAddOrDrop
) : PluginScript() {
    // ...
}
```

```kotlin
// Add to inventory, overflow drops to ground (PREFERRED — requires injected ObjTypeList)
invAddOrDrop(objTypes, objs.logs, count = 1)

// Add to inventory (returns transaction, check result manually)
val tx = invAdd(invs.inv, obj = objs.logs, count = 1)
if (tx.hasFailed()) { /* handle */ }

// Remove from inventory
invDel(invs.inv, obj = objs.tinderbox, count = 1)

// Check if inventory contains item
val hasItem = inv.contains(objs.tinderbox)
val hasItem = player.inv.contains(objs.tinderbox)

// Count items
val count = inv.count(objs.logs)

// Check free slots
val freeSlots = inv.freeSpace()
```

**Common inventory refs:**
- `invs.inv` — main inventory (28 slots)
- `invs.worn` — equipment (worn items)
- `invs.bank` — bank

---

## 8. Delay / Suspend / Ticks

### Alter v1
```kotlin
// Must use player.queue {} to get async context
player.queue {
    player.animate(Animation.CHOP)
    task.wait(3)   // wait 3 ticks
    // do the next thing
}
```

### RSMod v2
Event handlers are already `suspend` — no queue wrapper needed:
```kotlin
onOpLoc1(content.tree) {
    anim(seqs.woodcutting_axe)
    delay(3)                    // suspend 3 ticks
    // continue here after 3 ticks
}
```

**Delay variants:**
```kotlin
delay(1)          // suspend for 1 game tick (600ms)
delay(3)          // suspend for 3 ticks
```

**ActionDelay pattern** (for skill repeat loops — see Woodcutting.kt):
```kotlin
// Prevent action spam — check mapClock
if (actionDelay > mapClock) return
actionDelay = mapClock + 3   // 3-tick cooldown
```

---

## 9. Timers

### Alter v1
```kotlin
val YELL_TIMER = TimerKey()

onNpcSpawn(npc = "npc.cow") {
    npc.timers[YELL_TIMER] = world.random(100..200)
}

onTimer(YELL_TIMER) {
    npc.forceChat("Moo")
    npc.timers[YELL_TIMER] = world.random(100..200)
}
```

### RSMod v2
```kotlin
// Timer types live in config refs
// api/config/refs/BaseTimers.kt adds them, or define in your module's config

onNpcTimer(timers.cow_yell) { event ->
    event.npc.say("Moo")
    event.npc.timer(timers.cow_yell, random.of(100, 200))
}

// Setting a timer on an NPC:
npc.timer(timers.cow_yell, ticks = 150)

// Player timers:
onPlayerTimer(timers.my_player_timer) { event ->
    // this = ProtectedAccess
}
player.timer(timers.my_player_timer, ticks = 10)

// Soft timer (non-suspending, no protected access):
onPlayerSoftTimer(timers.regen_hp) { event ->
    event.player.heal(1)
}
```

---

## 10. Controller (RSMod v2 — no Alter equivalent)

Controllers are lightweight server-side entities that drive timed world events (e.g. tree respawn).
Alter handles this via world-level timers or object state changes.

```kotlin
// Registering a controller timer handler:
onAiConTimer(controllers.woodcutting_tree_duration) {
    controller.treeDespawnTick()   // custom extension on controller
}

// Creating a controller in-world (from Woodcutting.kt):
addController(coords, controllers.woodcutting_tree_duration, args = treeInfo)
```

Controllers are defined in `api/config/refs/BaseControllers.kt` and spawned via game logic.
This is an engine concept — do not try to replicate it with player timers.

---

## 11. Dialogue

### Alter v1
```kotlin
player.queue {
    it.chatNpc(player, "Good day, how may I help you?", animation = 568)
    it.chatPlayer(player, "I'd like to use the bank.", animation = 554)
    when (it.options(player, "Open bank", "Nothing")) {
        1 -> player.openBank()
        2 -> { /* nothing */ }
    }
}
```

### RSMod v2 (fully typed, suspend-based)
```kotlin
private suspend fun ProtectedAccess.talkToBanker(npc: Npc) =
    startDialogue(npc) {
        chatNpc(happy, "Good day, how may I help you?")
        chatPlayer(calm, "I'd like to use the bank.")
        val choice = choice2(
            "Open bank",
            "Nothing, thanks",
        )
        when (choice) {
            1 -> openBank()
            2 -> chatNpc(calm, "Very well.")
        }
    }

// In startup():
onOpNpc1(content.banker) { talkToBanker(it.npc) }
```

> **CRITICAL: `chatPlayer` and `chatNpc` are ONLY valid inside `startDialogue { }` blocks.**
> Outside of dialogue, use `mes("text")` for chatbox messages. Calling `chatPlayer(...)` or
> `chatNpc(...)` outside a dialogue block will not compile.

**Mes-anim refs** (face expressions):
- `happy`, `calm`, `sad`, `angry`, `confused` — constants on `Dialogue`

---

## 12. Shops

### Alter v1
```kotlin
onNpcOption(npc = "npc.bob", option = "trade") {
    player.openShop(shopId)
}
```

### RSMod v2
```kotlin
// In startup():
onOpNpc3(lumbridge_npcs.bob) { player.openShop(it.npc) }

// Shop definitions are in content area modules via Shops API
class Bob @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc3(lumbridge_npcs.bob) { player.openShop(it.npc) }
    }
}
```

---

## 13. Combat Definitions

### Alter v1
```kotlin
setCombatDef("npc.cow") {
    configs { attackSpeed = 6; respawnDelay = 45 }
    stats { hitpoints = 8; attack = 1; strength = 1; defence = 1 }
    bonuses { defenceStab = -21; defenceSlash = -21 }
    anims {
        attack = Animation.COW_ATTACK
        block = Animation.COW_HIT
        death = Animation.COW_DEATH
    }
}
```

### RSMod v2 — NpcEditor (Kotlin class, NOT TOML)

Combat stats are set in a **`NpcEditor` subclass** inside your module's configs file.
This is Kotlin code, not TOML or cache params.

```kotlin
// In configs/MyAreaNpcs.kt (see LumbridgeNpcs.kt for full example):
internal object CombatNpcEditor : NpcEditor() {
    init {
        edit(npcs.cow) {
            hitpoints   = 8
            attack      = 1
            strength    = 1
            defence     = 1
            ranged      = 1       // 1 if melee-only
            magic       = 1       // 1 if melee-only
            attackRange = 1       // 1 = melee, >1 = ranged/magic
            respawnRate = 50      // ticks before respawn
            giveChase   = true    // pursues fleeing player
        }
        edit(npcs.goblin) {
            hitpoints = 5; attack = 1; strength = 1; defence = 1
            ranged = 1; magic = 1; attackRange = 1; respawnRate = 100
        }
    }
}
```

**Available NpcEditor combat fields** (from `NpcPluginBuilder`):
`hitpoints`, `attack`, `strength`, `defence`, `ranged`, `magic`, `attackRange`,
`respawnRate`, `giveChase`, `huntRange`, `huntMode`, `regenRate`

**Data source:** Use `osrs_wiki_parse_page({ page: "Cow" })` plus rev 233 `.sym` verification for accurate integration in Kotlin refs.
Fallback: `Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/combat/Cow.json`

**Retaliation** — basic retaliation is handled automatically by the engine.
For custom retaliation behavior only:
```kotlin
onNpcHit(npcs.cow) { /* engine handles retaliate; add custom logic here if needed */ }
```

See `docs/LEGACY_IMPLEMENTATION_PLAYBOOK.md` section "I need NPC combat stats" for the full workflow.

---

## 14. NPC Spawning

### Alter v1
```kotlin
spawnNpc("npc.banker", x = 3207, z = 3220, level = 0, direction = 2)
```

### RSMod v2
NPC spawns are configured as data (YAML/TOML), not in plugin code.
The `npc-plugin` module handles NPC spawn scripts.
Do not spawn NPCs programmatically in content plugins unless for dynamic content (e.g. quest NPCs).

---

## 15. Object/Loc Spawning

### Alter v1
```kotlin
spawnObj("object.bank_booth", tile = Tile(3207, 3220))
```

### RSMod v2
Same principle — loc spawns are data-driven. Use the loc spawn config system.
Dynamic loc replacement (e.g. chopped tree → stump) uses:
```kotlin
// Change a loc to its depleted stage (woodcutting stump)
locAnim(loc, locTypes[loc].param(params.next_loc_stage))
// or using the loc replace API — see Woodcutting.kt for exact pattern
```

---

## 16. Lifecycle Events

### Alter v1
```kotlin
onWorldInit { /* runs once at startup */ }
onPlayerLogin { /* runs on each player login */ }
onPlayerLogout { /* runs on each logout */ }
```

### RSMod v2
```kotlin
onGameStartup { /* UnboundEvent — once at server startup */ }
onPlayerInit { /* SessionStateEvent.Initialize — player registered to world */ }
onPlayerLogin { /* SessionStateEvent.Login — login sequence begins */ }
onPlayerLogout { /* SessionStateEvent.Logout — before save */ }
```

---

## 17. Random Numbers

### Alter v1
```kotlin
world.random(1..100)
world.randomDouble()
```

### RSMod v2 (inside ProtectedAccess)
```kotlin
random.of(1, 100)          // inclusive both ends
random.of(maxExclusive = 100)
random.randomBoolean()
```

---

## 18. Type Reference Lookups

RSMod v2 has compiled type refs. Always use these instead of raw int IDs:

| Category | Alter v1 | RSMod v2 |
|----------|----------|----------|
| Items | `"item.logs"` string | `objs.logs` → `ObjType` |
| NPCs | `"npc.banker"` string | `npcs.banker_lumbridge` → `NpcType` |
| Locs | `"object.bank_booth"` string | `locs.bank_booth_id` → `LocType` |
| Content groups | N/A | `content.bank_booth` → `ContentGroupType` |
| Stats | `Skills.WOODCUTTING` int | `stats.woodcutting` → `StatType` |
| Animations | `Animation.CHOP` int | `seqs.woodcutting_axe` → `SeqType` |
| Sounds | `Sound.CHOP` int | `synths.axe_chop` → `SynthType` |
| Graphics | `Graphic.LEVELUP` int | `spotanims.levelup` → `SpotanimType` |
| Params | N/A (enums) | `params.levelrequire` → `ParamType<Int>` |
| Interfaces | `Interfaces.BANK` int | `interfaces.bank_main` → `InterfaceType` |
| Inventories | `Inventory.PLAYER_INV` int | `invs.inv` → `InvType` |

**Getting a param from a type:**
```kotlin
val levelReq: Int = objType.param(params.levelrequire)
val anim: SeqType = objType.param(params.skill_anim)
val xp: Double = locType.param(params.skill_xp)

// Nullable variant — returns null if param not set:
val maybeAnim: SeqType? = objType.paramOrNull(params.skill_anim)
```

---

## 19. Inventory Item-on-Item (Use Item on Item in Inventory)

RSMod v2 has `onOpHeldU` for when a player drags/uses one inventory item onto another.

```kotlin
// In startup():
onOpHeldU(objs.tinderbox, objs.logs) { lightFire(it) }

// The engine normalises the order — you only register once regardless of which
// item the player uses on which.

// With content groups (if the group exists):
// onOpHeldU(content.firemaking_log, objs.tinderbox) { lightFire(it) }
```

The event type is `HeldUEvents.Type` (or `HeldUContentEvents` for content groups).
- `it.first` — the first item (matches first param in registration)
- `it.second` — the second item
- Both are `UnpackedObjType` with full param access

**Use cases:** Firemaking (tinderbox + log), Herblore (herb + vial, unf + secondary), Fletching (knife + log), Smithing (hammer + bar on anvil-equivalent)

Import: `org.rsmod.api.script.onOpHeldU`

---

## 19. Content Group vs Specific Type

RSMod v2 prefers content group matching for generic behaviors shared by many entities:

```kotlin
// BAD — register handler 50 times for each tree variant
onOpLoc1(locs.oak_tree_10820) { cut() }
onOpLoc1(locs.oak_tree_10831) { cut() }
// ...

// GOOD — one handler for all entities tagged with content.tree
onOpLoc1(content.tree) { cut(it.loc, it.type) }
```

Content groups are assigned to loc/npc/obj types in cache data.
To add a new content group, it must be defined in cache AND referenced in `BaseContent.kt`.

---

## 20. Module Structure for a New Skill

New skill plugin modules follow this layout:

```
content/skills/my_skill/
├── build.gradle.kts
└── src/main/kotlin/org/rsmod/content/skills/my_skill/
    ├── scripts/
    │   └── MySkill.kt          ← main PluginScript
    ├── configs/
    │   └── MySkillConfigs.kt   ← type builders / param lookups (optional)
    └── MySkillModule.kt        ← Guice module binding (if needed)
```

**Minimal build.gradle.kts:**
```kotlin
plugins {
    id("base-conventions")
    id("integration-test-suite")   // add only if writing integration tests
}

dependencies {
    implementation(projects.api.pluginCommons)
    integrationImplementation(projects.api.player)   // for tests only
}
```

The module is auto-discovered by `settings.gradle.kts` — no explicit include needed.

---

## 21. Interface Button Handlers (`onButton`)

RSMod v2 uses `onButton` for handling interface button clicks (prayer tab, bank actions, etc.).

```kotlin
// In startup():
onButton(interfaces.prayer, component = 5) {
    // this = ProtectedAccess
    // Toggle a prayer on/off
    togglePrayer(prayers.thick_skin)
}

// For bank interface buttons:
onButton(interfaces.bank_main, component = 14) {
    // Handle "Deposit All" button
    depositAll()
}
```

**Key rules:**
- `interfaces.x` must be a valid rev 233 interface (max ID = 924). Verify with `search_iftypes` before using.
- `component` is the child widget index within the interface. Find it from cache data or existing code.
- Handler context is `ProtectedAccess`, same as all other event handlers.

**Import:** `org.rsmod.api.script.onButton`

---

## Patterns Requiring Engine Investigation (Do Not Hack Around)

These are behaviors where Alter has a workaround or different engine behavior.
Flag these rather than porting the hack:

1. **Multi-log per tick (3-tick woodcutting)** — RSMod v2 engine tick rate and action delay
   system must be verified to match OSRS exactly before implementing.
2. **NPC aggression radius** — needs engine-level NPC AI support, not a plugin timer.
3. **Poison/venom tick damage** — tick-based damage, verify engine handles this natively.
4. **Prayer point drain rates** — must match OSRS wiki drain rates exactly, engine-level.
5. **Skill level-up visual** — level-up interface firing; verify `statAdvance` triggers it.
6. **Item degradation / charges** — `api/obj-charges` module handles this; check before
   implementing broken items in a plugin.

