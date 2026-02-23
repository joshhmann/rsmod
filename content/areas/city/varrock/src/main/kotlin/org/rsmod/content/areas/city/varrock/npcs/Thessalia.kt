package org.rsmod.content.areas.city.varrock.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc3
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.varrock.configs.varrock_invs
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Thessalia @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.thessalia) { thessaliaDialogue(it.npc) }
        onOpNpc3(varrock_npcs.thessalia) { player.openClothesShop(it.npc) }
    }

    private fun Player.openClothesShop(npc: Npc) {
        shops.open(this, npc, "Thessalia's Fine Clothes", varrock_invs.clothes_shop)
    }

    private suspend fun ProtectedAccess.thessaliaDialogue(npc: Npc) =
        startDialogue(npc) { thessaliaChat(npc) }

    private suspend fun Dialogue.thessaliaChat(npc: Npc) {
        chatNpc(happy, "Hello there, can I interest you in some fine clothes?")
        val choice = choice2("Yes please, what have you got?", 1, "No thanks.", 2)
        if (choice == 1) {
            player.openClothesShop(npc)
        } else {
            chatPlayer(neutral, "No thanks.")
        }
    }
}
