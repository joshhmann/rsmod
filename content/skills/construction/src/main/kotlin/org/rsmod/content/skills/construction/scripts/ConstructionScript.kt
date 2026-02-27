package org.rsmod.content.skills.construction.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.constructionLvl
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.script.onOpLoc3
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.skills.construction.configs.poh_components
import org.rsmod.content.skills.construction.configs.poh_locs
import org.rsmod.content.skills.construction.configs.poh_npcs
import org.rsmod.content.skills.construction.configs.poh_varbits
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/** XP gained when removing a wooden chair (poh_chair1). */
private const val CHAIR_REMOVE_XP = 58.0

class ConstructionScript @Inject constructor(private val locRepo: LocRepository) : PluginScript() {

    override fun ScriptContext.startup() {
        // Estate agent: Talk-to opens house management dialogue.
        onOpNpc1(poh_npcs.estate_agent) { estateAgentDialogue(it.npc) }

        // poh_options interface button handlers.
        onIfModalButton(poh_components.build_mode_on) { toggleBuildMode(on = true) }
        onIfModalButton(poh_components.build_mode_off) { toggleBuildMode(on = false) }
        onIfModalButton(poh_components.leave_house) { leaveHouse() }

        // Chair Remove (op3 = "Remove" in build mode).
        onOpLoc3(poh_locs.chair1) { removeChair(it.loc) }
    }

    private suspend fun ProtectedAccess.estateAgentDialogue(npc: Npc) {
        startDialogue(npc) { estateAgentChat() }
    }

    private suspend fun Dialogue.estateAgentChat() {
        val choice =
            choice3(
                "I'd like to see my house options.",
                1,
                "What is Construction?",
                2,
                "Goodbye.",
                3,
            )
        when (choice) {
            1 -> viewHouseOptions()
            2 -> constructionInfo()
        // choice 3: nothing
        }
    }

    private fun Dialogue.viewHouseOptions() {
        access.ifOpenMainModal(interfaces.poh_options)
    }

    private suspend fun Dialogue.constructionInfo() {
        chatNpc(
            happy,
            "Construction allows you to build and decorate your very own house! " +
                "Come back when you'd like to manage your house options.",
        )
    }

    private fun ProtectedAccess.toggleBuildMode(on: Boolean) {
        player.inBuildMode = on
        ifCloseSub(interfaces.poh_options)
        if (on) {
            mes("Build mode is now <col=00ff00>on</col>.")
        } else {
            mes("Build mode is now <col=ff0000>off</col>.")
        }
    }

    private fun ProtectedAccess.leaveHouse() {
        ifCloseSub(interfaces.poh_options)
        mes("You leave your house.")
    }

    private fun ProtectedAccess.removeChair(loc: BoundLocInfo) {
        if (!player.inBuildMode) {
            mes("You need to be in building mode to remove furniture.")
            return
        }
        if (player.constructionLvl < 1) {
            mes("You need at least level 1 Construction to do this.")
            return
        }
        locRepo.del(loc, Int.MAX_VALUE)
        statAdvance(stats.construction, CHAIR_REMOVE_XP)
        mes("You dismantle the chair.")
    }
}

/** Build mode varbit delegate, readable from any Player context. */
private var Player.inBuildMode: Boolean by boolVarBit(poh_varbits.poh_building_mode)
