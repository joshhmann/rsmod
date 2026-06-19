package org.rsmod.content.quests.knightssword

import jakarta.inject.Inject
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.content.quests.knightssword.configs.knights_sword_locs
import org.rsmod.content.quests.knightssword.configs.knights_sword_objs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class KnightsSword @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        // Search Sir Vyvin's cupboard for the portrait
        onOpLoc1(knights_sword_locs.vyvincupboard) { searchCupboard() }
    }

    private suspend fun ProtectedAccess.searchCupboard() {
        val stage = getQuestStage(QuestList.knights_sword)
        val hasPortrait = player.inv.contains(knights_sword_objs.knights_portrait)

        when {
            // Quest not started or completed — nothing in the cupboard
            stage == 0 || stage >= 7 -> {
                mes("You search the cupboard but find nothing of interest.")
            }
            // Player already has the portrait
            hasPortrait -> {
                mes("You've already taken the portrait. There's nothing else here.")
            }
            // Quest is active — find the portrait
            stage in 1..6 -> {
                mes(
                    "You search the cupboard and find a portrait of Sir Vyvin's father holding a ceremonial sword!"
                )
                val added =
                    player.invAdd(player.inv, knights_sword_objs.knights_portrait, 1).success
                if (added) {
                    mes("You carefully take the portrait from the cupboard.")
                } else {
                    mes("You don't have enough free inventory space to take the portrait.")
                }
            }
            else -> {
                mes("You search the cupboard but find nothing of interest.")
            }
        }
    }
}
