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
        // F2P monsters - combat stats defined in npcs.toml enable automatic retaliation
        // This script registers on-hit handlers for any additional behavior
        registerChicken()
        registerCow()
        registerGoblin()
        registerGiantRat()
        registerGuard()
        registerScorpion()
        registerImp()
        registerDarkWizard()
        registerLesserDemon()
        registerBlackKnight()
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
            listOf(DropTableNpcs.giant_rat, DropTableNpcs.giant_rat_2, DropTableNpcs.giant_rat_3)
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
}
