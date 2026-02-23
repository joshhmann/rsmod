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

class Horvik @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.horvik) { horvikDialogue(it.npc) }
        onOpNpc3(varrock_npcs.horvik) { player.openArmourShop(it.npc) }
    }

    private fun Player.openArmourShop(npc: Npc) {
        shops.open(this, npc, "Horvik's Armour Shop", varrock_invs.armour_shop)
    }

    private suspend fun ProtectedAccess.horvikDialogue(npc: Npc) =
        startDialogue(npc) { horvikChat(npc) }

    private suspend fun Dialogue.horvikChat(npc: Npc) {
        chatNpc(happy, "Can I interest you in some armour?")
        val choice = choice2("Yes please, what have you got?", 1, "No thanks.", 2)
        if (choice == 1) {
            player.openArmourShop(npc)
        } else {
            chatPlayer(neutral, "No thanks.")
        }
    }
}
