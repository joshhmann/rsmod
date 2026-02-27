package org.rsmod.content.areas.city.barbarian_village

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.shops.Shops
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Barbarian Village content script.
 *
 * Features:
 * - Barbarians (levels 9, 10, 15, 17) - aggressive NPCs with combat stats
 * - Unicorn pen - unicorns drop unicorn horns for herblore
 * - Peksa's Helmet Shop - run by Peksa (handled in edgeville module)
 * - Spinning wheel hut - for crafting (handled by crafting skill module)
 * - Pottery studio - pottery wheel and oven with Tassie Slipcast (handled by crafting skill module)
 * - Stronghold of Security entrance - hole in the ground
 * - Fishing spots - lure/bait spots for trout/salmon/pike
 * - Long Hall - firemaking area with permanent fire
 */
class BarbarianVillageScript @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        // Tassie Slipcast dialogue - pottery instructor
        onOpNpc1(BarbarianVillageNpcs.tassie_slipcast) { tassieDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.tassieDialogue(npc: Npc) =
        startDialogue(npc) { tassieChat(npc) }

    private suspend fun Dialogue.tassieChat(npc: Npc) {
        chatNpc(happy, "Hello! I'm Tassie Slipcast. I teach pottery here.")
        val choice = choice3("Can you teach me pottery?", 1, "What can I make?", 2, "Goodbye.", 3)
        when (choice) {
            1 -> teachPottery(npc)
            2 -> explainPottery(npc)
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }

    private suspend fun Dialogue.teachPottery(npc: Npc) {
        chatPlayer(quiz, "Can you teach me pottery?")
        chatNpc(
            happy,
            "Of course! To make pottery, you need soft clay. " +
                "Use clay on a water source to make it soft. " +
                "Then use the soft clay on the pottery wheel to shape it. " +
                "Finally, fire your unfired pots in the pottery oven.",
        )
        chatNpc(
            happy,
            "You can make pots at level 1 Crafting, and bowls at level 8. " +
                "It's a great way to train your Crafting skill!",
        )
    }

    private suspend fun Dialogue.explainPottery(npc: Npc) {
        chatPlayer(quiz, "What can I make?")
        chatNpc(
            happy,
            "You can make pots and bowls! Pots require level 1 Crafting, " +
                "and bowls require level 8. You'll get experience for both " +
                "shaping the clay on the wheel and firing it in the oven.",
        )
        chatNpc(
            happy,
            "Pots are useful for holding flour, and bowls are used " +
                "in Cooking for making various dishes.",
        )
    }
}

/** NPC references for Barbarian Village. */
internal object BarbarianVillageNpcs : NpcReferences() {
    /** Tassie Slipcast - pottery instructor in the pottery studio. */
    val tassie_slipcast = find("favour_tassie_slipcast")
}
