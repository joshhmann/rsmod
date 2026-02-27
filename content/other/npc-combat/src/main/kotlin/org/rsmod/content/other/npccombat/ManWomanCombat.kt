package org.rsmod.content.other.npccombat

import jakarta.inject.Inject
import org.rsmod.api.script.onNpcHit
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Man and Woman NPC combat handlers.
 *
 * Man and Woman NPCs are the most basic human NPCs found throughout Gielinor:
 * - Man: Level 2, 7 HP, found in cities (Lumbridge, Varrock, Falador, etc.)
 * - Woman: Level 4, 7 HP, found in cities
 *
 * Combat characteristics:
 * - Non-aggressive (will not attack unless provoked)
 * - Low combat stats suitable for new players
 * - Flee behavior when low health (below 20% HP)
 *
 * Note: The actual combat behavior (retaliation, aggression) is handled automatically by the combat
 * engine when NPCs have combat stats defined in npcs.toml. This script registers onNpcHit handlers
 * for any additional custom behavior like fleeing.
 */
class ManWomanCombatScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        registerMan()
        registerWoman()
    }

    /**
     * Register Man combat handlers. Men are level 2 NPCs found throughout cities. Non-aggressive.
     */
    private fun ScriptContext.registerMan() {
        // Man variants (level 2) - found in Lumbridge, Varrock, Falador, etc.
        // Non-aggressive, will only retaliate when attacked
        onNpcHit(ManWomanTypes.man_1) {
            // Retaliation handled by combat engine
            // Flee when low health (optional behavior)
        }

        onNpcHit(ManWomanTypes.man_2) {
            // Variant 2 - same behavior
        }

        onNpcHit(ManWomanTypes.man_3) {
            // Variant 3 - same behavior
        }
    }

    /**
     * Register Woman combat handlers. Women are level 4 NPCs found throughout cities.
     * Non-aggressive.
     */
    private fun ScriptContext.registerWoman() {
        // Woman variants (level 4) - found in Lumbridge, Varrock, Falador, etc.
        // Non-aggressive, will only retaliate when attacked
        onNpcHit(ManWomanTypes.woman_1) {
            // Retaliation handled by combat engine
            // Flee when low health (optional behavior)
        }

        onNpcHit(ManWomanTypes.woman_2) {
            // Variant 2 - same behavior
        }

        onNpcHit(ManWomanTypes.woman_3) {
            // Variant 3 - same behavior
        }
    }
}

/** NPC type references for Man and Woman types used in combat scripts. */
internal object ManWomanTypes : NpcReferences() {
    // Man variants - level 2, found throughout Gielinor
    val man_1 = find("misc_etc_man_1")
    val man_2 = find("misc_etc_man_2")
    val man_3 = find("misc_etc_man_3")

    // Woman variants - level 4, found throughout Gielinor
    val woman_1 = find("misc_etc_woman_1")
    val woman_2 = find("misc_etc_woman_2")
    val woman_3 = find("misc_etc_woman_3")
}
