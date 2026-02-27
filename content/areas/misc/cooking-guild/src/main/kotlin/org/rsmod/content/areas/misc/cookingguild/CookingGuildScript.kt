package org.rsmod.content.areas.misc.cookingguild

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.hat
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.cookingLvl
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.misc.cookingguild.configs.cooking_guild_locs
import org.rsmod.content.areas.misc.cookingguild.configs.cooking_guild_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.game.inv.isType
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Cooking Guild area script. Handles:
 * - Entry door with skill requirement (32 Cooking + chef's hat)
 * - Head Chef NPC dialogue
 * - Windmill access (handled by WindmillScript in content/other/windmill/)
 */
class CookingGuildScript @Inject constructor(private val locRepo: LocRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        // Cooking Guild entry door - requires 32 Cooking and chef's hat
        onOpLoc1(cooking_guild_locs.door) { attemptEntry(it.loc) }

        // Head Chef NPC
        onOpNpc1(cooking_guild_npcs.head_chef) { talkToHeadChef(it.npc) }
    }

    private suspend fun ProtectedAccess.attemptEntry(door: BoundLocInfo) {
        // Check Cooking level requirement (32)
        if (player.cookingLvl < 32) {
            mes("You need a Cooking level of 32 to enter the Cooking Guild.")
            return
        }

        // Check for chef's hat equipped
        if (!isChefsHatEquipped()) {
            mes("You need to be wearing a chef's hat to enter the Cooking Guild.")
            return
        }

        // Open the door and walk through
        openDoor(door)
    }

    private fun ProtectedAccess.isChefsHatEquipped(): Boolean {
        return player.hat.isType(objs.chefs_hat)
    }

    private suspend fun ProtectedAccess.openDoor(door: BoundLocInfo) {
        // Play door sound and animation
        soundSynth(synths.door_open)

        // Determine destination based on current position
        // Outside coords: around 3143, 3444
        // Inside coords: around 3143, 3445
        val destCoords =
            if (coords.z <= 3444) {
                // Outside -> Inside
                CoordGrid(3143, 3445, 0)
            } else {
                // Inside -> Outside
                CoordGrid(3143, 3444, 0)
            }

        // Walk through the door
        teleport(destCoords)
        mes("You enter the Cooking Guild.")
    }

    private suspend fun ProtectedAccess.talkToHeadChef(npc: Npc) {
        startDialogue(npc) { headChefDialogue(npc) }
    }

    private suspend fun Dialogue.headChefDialogue(npc: Npc) {
        chatNpc(happy, "Hello! Welcome to the Cooking Guild.")
        chatNpc(
            neutral,
            "Here you can find various cooking equipment, a wheat field, and a windmill to make flour.",
        )

        val choice =
            choice3("What can I do here?", 1, "Do you have any tips for cooking?", 2, "Goodbye.", 3)

        when (choice) {
            1 -> explainGuildFeatures()
            2 -> giveCookingTips()
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }

    private suspend fun Dialogue.explainGuildFeatures() {
        chatPlayer(quiz, "What can I do here?")
        chatNpc(
            neutral,
            "We have a wheat field just outside, and a windmill upstairs to grind grain into flour. " +
                "There's also a range for cooking, and various respawning ingredients on the tables.",
        )
        chatNpc(happy, "It's a chef's paradise!")
    }

    private suspend fun Dialogue.giveCookingTips() {
        chatPlayer(quiz, "Do you have any tips for cooking?")
        chatNpc(
            neutral,
            "Always use the best range you can find - it reduces the chance of burning food. " +
                "Also, some foods require specific cooking levels, so keep training!",
        )
        chatNpc(happy, "And remember - wearing cooking gauntlets helps prevent burns!")
    }
}
