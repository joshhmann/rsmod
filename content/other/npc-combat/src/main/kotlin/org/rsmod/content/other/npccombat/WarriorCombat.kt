package org.rsmod.content.other.npccombat

import jakarta.inject.Inject
import org.rsmod.api.script.onNpcHit
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Warrior Woman and Al-Kharid Warrior combat handlers.
 *
 * Warrior Women and Al-Kharid Warriors are mid-level training NPCs:
 * - Warrior Woman: Level 24, 29 HP, found in Al-Kharid palace
 * - Al-Kharid Warrior: Level 26, found in Al-Kharid palace
 *
 * Combat characteristics (from OSRS wiki):
 * - Warrior Woman: Level 24, 29 HP, aggressive in Al-Kharid palace
 * - Al-Kharid Warrior: Level 26, aggressive in Al-Kharid palace
 * - Attack speed: 4 ticks (2.4 seconds)
 * - Attack style: Crush
 * - Good training targets for mid-level players
 *
 * Note: The actual combat behavior (retaliation, aggression) is handled automatically by the combat
 * engine when NPCs have combat stats defined in npcs.toml. This script registers onNpcHit handlers
 * for any additional custom behavior.
 */
class WarriorCombatScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        registerWarriorWoman()
        // TODO: Al-Kharid Warrior - need to identify correct internal name
        // registerAlkharidWarrior()
    }

    /**
     * Register Warrior Woman combat handlers. Warrior Women are level 24 NPCs found in Al-Kharid
     * palace. Aggressive.
     */
    private fun ScriptContext.registerWarriorWoman() {
        // Warrior Woman (level 24) - found in Al-Kharid palace
        // Aggressive to players, good training target
        onNpcHit(WarriorTypes.warrior_woman) {
            // Aggression and retaliation handled by combat engine via npcs.toml stats
            // Combat stats: 29 HP, attack_level 19, strength_level 18, defence_level 19
        }

        // Additional variants if they exist
        // onNpcHit(WarriorTypes.warrior_woman_variant01) { }
        // onNpcHit(WarriorTypes.warrior_woman_variant02) { }
    }

    /**
     * Register Al-Kharid Warrior combat handlers. Al-Kharid Warriors are level 26 NPCs found in
     * Al-Kharid palace. Aggressive.
     */
    /*
    private fun ScriptContext.registerAlkharidWarrior() {
        // Al-Kharid Warrior (level 26) - found in Al-Kharid palace
        // Aggressive to players, good training target
        onNpcHit(WarriorTypes.alkharid_warrior) {
            // Aggression and retaliation handled by combat engine
            // Slightly tougher than Warrior Woman
        }
    }
    */
}

/** NPC type references for Warrior Woman and Al-Kharid Warrior types. */
internal object WarriorTypes : NpcReferences() {
    // Warrior Woman - level 24, Al-Kharid palace
    val warrior_woman = find("warrior_woman")
    // val warrior_woman_variant01 = find("warrior_woman_variant01")
    // val warrior_woman_variant02 = find("warrior_woman_variant02")

    // TODO: Al-Kharid Warrior - level 26, need correct internal name
    // val alkharid_warrior = find("alkharid_warrior")
}
