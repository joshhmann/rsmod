# RSMod Implementation Research Report
**Target:** `Z:\Projects\OSRS-PS-DEVsmod` vs `Z:\Projects\OSRS-PS-DEV\original_rsmodsmod`
**Context:** Analysis of recurrent build/boot failures based on `GEMINI.md` and `CORE_SYSTEMS_GUIDE.md` constraints.

---

## 1. The "Duplicate Key" Crash (`blankobject` mapping)
**The Mandate:** `GEMINI.md` explicitly states: *NO Duplicate Functional Stubs: NEVER map multiple functional symbols (e.g. two different bows) to `blankobject`. This crashes `EnumBuilders` and `ObjEditors` at boot due to duplicate map keys.*

**The Finding:** In `rsmod/api/config/src/main/kotlin/org/rsmod/api/config/refs/BaseObjs.kt` and `BaseVarps.kt`, multiple symbols are mapped to `blankobject`.
*Example from `BaseObjs.kt`:*
```kotlin
val bronze_bolts = find("blankobject")
val bryophyta_staff = find("blankobject")
val dragon_hunter_crossbow = find("blankobject")
val grimy_guam = find("blankobject")
```
*Example from `BaseVarps.kt`:*
```kotlin
val below_ice_mountain = find("blankobject")
val corsair_curse = find("blankobject")
```
**Why it fails:** The Gradle build and game boot process pack cache configurations. When it sees multiple `ObjType` definitions pointing to the exact same internal name (`"blankobject"`), the underlying maps (like `Map<String, ObjType>`) throw duplicate key exceptions and halt the boot sequence.

---

## 2. Hardcoded Symbol IDs 
**The Mandate:** `GEMINI.md` states: *NO `find("name", 123)`. If the symbol exists in `.sym` files, use `find("name")` only. Guessing IDs causes hash mismatches.*

**The Finding:** Over 100 instances of hardcoded IDs bypass the safety checks across the `content/` and `api/` directories.
*Examples:*
*   `rsmod/content/mechanics/poison/src/main/kotlin/org/rsmod/content/mechanics/poison/configs/PoisonVarps.kt`: `val hp_orb_toxin = find("hp_orb_toxin", 102)`
*   `rsmod/api/shops/src/main/kotlin/org/rsmod/api/shops/config/ShopConfigs.kt`: `val shop_main: InterfaceType = find("shopmain", 1596431697)`
*   `rsmod/api/config/src/main/kotlin/org/rsmod/api/config/refs/BaseParams.kt`: `val spell_spellbook: ParamInt = find("spell_spellbook", 88673368977)`

**Why it fails:** When you pass an integer ID into `find()`, the engine type-verifier attempts to cross-reference it against the `.sym` files extracted from the Rev 233 cache. If the integer hash doesn't perfectly match the hash generated from the cache, the `Strict` boot gate immediately fails.

---

## 3. Uncontrolled Scope and Duplicate Handlers
**The Mandate:** `GEMINI.md` states: *Duplicate Handlers: Do not register the same `onOp*` for the same entity in different files. It silently disables the second registration. NpcEditor: One module "owns" an NPC type's stats and aggression. Avoid duplicate `edit(npc)` calls in different modules to prevent infinite loops.*

**The Finding:** The `content/` folder has been massively expanded compared to `original_rsmod`. There are now hundreds of files populating `content/areas/city/*`, `content/quests/*`, and `content/mechanics/*`.
For example, Lumbridge has `LumbridgeNpcs.kt`, quests like `CooksAssistant.kt`, `RestlessGhost.kt`, etc. 

**Why it fails:** With content spreading out this rapidly without a strict centralized registry, multiple scripts end up binding to the exact same NPC interactions or editing the exact same Loc/NPC properties. 
For instance, if `LumbridgeScript` defines `onOpNpc1(lumbridge_npcs.hans)` and `CooksAssistantScript` also defines `onOpNpc1(lumbridge_npcs.hans)`, one will override the other or crash Guice bindings during the `startup()` context phase.

---

## 4. Bypassing the Auto-Discovery Pattern
**The Mandate:** *NO `bindScript<T>()` in `PluginModule.kt`.*

**The Finding:** While the codebase successfully removed most instances of `bindScript` inside individual modules, integration tests still heavily rely on manual event binding arrays which can mask failures that only happen during actual server boot.

**Why it fails:** The DI container (`Guice`) automatically scans the class path for anything inheriting from `PluginScript`. Attempting manual implementations conflicts with the auto-injector.

---

## Actionable Next Steps to Unblock the Build
1. **Purge the `blankobject` Mappings:** Open `BaseObjs.kt` and `BaseVarps.kt`. If a symbol cannot be mapped to an actual valid cache name found in `.data/symbols/obj.sym` or `varp.sym`, the variable MUST be commented out or removed. Do NOT map it to `blankobject`.
2. **Remove Hardcoded Hashes:** Do a massive find-and-replace to convert `find("xyz", <number>)` into `find("xyz")`. Only exceptions should be heavily documented root engine overrides.
3. **Audit the `edit(...)` and `onOp...` calls:** Search across `rsmod/content` to see if `edit(npcs.dark_wizard)` or `onOpLoc1(content.tree)` are defined in more than one place. Group editing responsibilities to a single file per entity.
