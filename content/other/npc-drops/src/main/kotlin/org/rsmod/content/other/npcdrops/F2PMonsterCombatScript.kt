package org.rsmod.content.other.npcdrops

import jakarta.inject.Inject
import org.rsmod.api.script.onNpcHit
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * F2P monster combat handlers with retaliation. Ported from Kronos-184 JSON data to RSMod v2. Data
 * source: Kronos-184-Fixed/.../data/npcs/combat/
 *
 * Note: The actual retaliation behavior is handled automatically by the combat engine when NPCs
 * have combat stats defined in npcs.toml (attack_type, attack_anim, etc.). This script registers
 * onNpcHit handlers for any custom hit behavior if needed.
 */
class F2PMonsterCombatScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // All onNpcHit handlers in this file were empty stubs — removed to avoid conflicting
        // with content/other/npc-combat/ scripts which register the same NPC types.
        // Retaliation is handled automatically by the combat engine for NPCs with combat stats.
    }

    private fun ScriptContext.registerChicken() {
        val npcs = listOf(DropTableNpcs.chicken, DropTableNpcs.chicken_brown, DropTableNpcs.rooster)
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerCow() {
        val npcs =
            listOf(
                DropTableNpcs.cow,
                DropTableNpcs.cow2,
                DropTableNpcs.cow3,
                DropTableNpcs.cow_beef,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerGoblin() {
        val npcs = listOf(DropTableNpcs.goblin, DropTableNpcs.goblin_2, DropTableNpcs.goblin_3)
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerGiantRat() {
        val npcs =
            listOf(DropTableNpcs.giantrat, DropTableNpcs.giantrat2, DropTableNpcs.giantrat3)
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerGuard() {
        val npcs = listOf(DropTableNpcs.guard, DropTableNpcs.guard_2, DropTableNpcs.guard_3)
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerScorpion() {
        onNpcHit(DropTableNpcs.scorpion) { /* Retaliation handled by engine */ }
    }

    private fun ScriptContext.registerImp() {
        onNpcHit(DropTableNpcs.imp) { /* Retaliation handled by engine */ }
    }

    private fun ScriptContext.registerDarkWizard() {
        val npcs =
            listOf(
                // Note: dark_wizard symbol does not exist in rev 228
                DropTableNpcs.bearded_dark_wizard,
                DropTableNpcs.young_dark_wizard,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerLesserDemon() {
        onNpcHit(DropTableNpcs.lesser_demon) { /* Retaliation handled by engine */ }
    }

    private fun ScriptContext.registerBlackKnight() {
        val npcs = listOf(DropTableNpcs.black_knight, DropTableNpcs.aggressive_black_knight)
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerHillGiant() {
        val npcs =
            listOf(
                DropTableNpcs.hill_giant,
                DropTableNpcs.hill_giant2,
                DropTableNpcs.hill_giant3,
                DropTableNpcs.wilderness_hill_giant,
                DropTableNpcs.wilderness_hill_giant2,
                DropTableNpcs.wilderness_hill_giant3,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerSkeleton() {
        val npcs =
            listOf(
                DropTableNpcs.skeleton_unarmed,
                DropTableNpcs.skeleton_unarmed2,
                DropTableNpcs.skeleton_unarmed3,
                DropTableNpcs.skeleton_unarmed4,
                DropTableNpcs.skeleton_armed,
                DropTableNpcs.skeleton_armed2,
                DropTableNpcs.skeleton_armed3,
                DropTableNpcs.skeleton_armed4,
                DropTableNpcs.skeleton_armed5,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerMossGiant() {
        val npcs =
            listOf(
                DropTableNpcs.mossgiant,
                DropTableNpcs.mossgiant2,
                DropTableNpcs.mossgiant3,
                DropTableNpcs.mossgiant4,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerOutlaw() {
        val npcs =
            listOf(
                DropTableNpcs.surok_outlaw1,
                DropTableNpcs.surok_outlaw2,
                DropTableNpcs.surok_outlaw3,
                DropTableNpcs.surok_outlaw4,
                DropTableNpcs.surok_outlaw5,
                DropTableNpcs.surok_outlaw6,
                DropTableNpcs.surok_outlaw7,
                DropTableNpcs.surok_outlaw8,
                DropTableNpcs.surok_outlaw9,
                DropTableNpcs.surok_outlaw10,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerBear() {
        // Bear variants: brownbear (level 21 Grizzly), darkbear (level 19 Black bear)
        // Cubs are level 15 but non-aggressive
        val npcs =
            listOf(
                DropTableNpcs.brownbear,
                DropTableNpcs.darkbear,
                DropTableNpcs.brownbear_cub_1,
                DropTableNpcs.brownbear_cub_2,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerThief() {
        // Thief variants: level 16 NPCs found in Varrock, Port Sarim, etc.
        val npcs = listOf(DropTableNpcs.thief1, DropTableNpcs.thief2)
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerRogue() {
        // Rogue variants: level 15 NPCs found in Wilderness (Rogues' Castle)
        // wilderness_rogue is level 135 and aggressive when caught stealing from chests
        val npcs = listOf(DropTableNpcs.rogue, DropTableNpcs.wilderness_rogue)
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerZombie() {
        // Zombie variants: levels 13, 18, 24 - found in Edgeville Dungeon, Draynor Sewers, Varrock
        // Sewers
        val npcs =
            listOf(
                DropTableNpcs.zombie_unarmed,
                // DropTableNpcs.zombie_unarmed2, // TODO: Add to DropTableNpcs
                // DropTableNpcs.zombie_unarmed3, // TODO: Add to DropTableNpcs
                // DropTableNpcs.zombie_unarmed4, // TODO: Add to DropTableNpcs
                DropTableNpcs.zombie2,
                DropTableNpcs.zombie2_b,
                DropTableNpcs.zombie2_c,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerMugger() {
        // Mugger - level 6 aggressive NPC found in Lumbridge, Varrock, Wilderness
        onNpcHit(DropTableNpcs.mugger) { /* Retaliation handled by engine */ }
    }

    private fun ScriptContext.registerUnicorn() {
        // Unicorn variants: level 15 (unicorn) and level 27 (black_unicorn)
        // Non-aggressive, found in Barbarian Village pen and Wilderness
        val npcs = listOf(DropTableNpcs.unicorn, DropTableNpcs.black_unicorn)
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerBarbarian() {
        // Barbarian variants: levels 9, 10, 15, 17
        // Found in Barbarian Village, aggressive to low-level players
        val npcs =
            listOf(
                DropTableNpcs.barbarian,
                DropTableNpcs.barbarian_2,
                DropTableNpcs.barbarian_3,
                DropTableNpcs.barbarian_4,
                DropTableNpcs.barbarian_5,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerDwarf() {
        // Dwarf variants: level 10-11 (normal), 10 (chaos), 14 (mountain)
        // Found in Dwarven Mine and Ice Mountain, non-aggressive
        val npcs =
            listOf(
                DropTableNpcs.dwarf_normal,
                DropTableNpcs.dwarf_chaos,
                DropTableNpcs.dwarf_mountain,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerJailGuard() {
        // Jail Guard variants: level 26 NPCs found in Draynor and Port Sarim jails
        val npcs =
            listOf(
                DropTableNpcs.jail_guard_1,
                DropTableNpcs.jail_guard_2,
                DropTableNpcs.jail_guard_3,
                DropTableNpcs.jail_guard_4,
                DropTableNpcs.jail_guard_5,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }

    private fun ScriptContext.registerManWoman() {
        // Man and Woman variants: levels 2-3, found in cities
        // Non-aggressive, thievable (pickpocket already implemented)
        val npcs =
            listOf(
                DropTableNpcs.man,
                DropTableNpcs.man2,
                DropTableNpcs.man3,
                DropTableNpcs.woman,
                DropTableNpcs.woman2,
                DropTableNpcs.woman3,
            )
        npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
    }
}
