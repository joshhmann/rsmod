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

class Aubury @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.aubury) { auburyDialogue(it.npc) }
        onOpNpc3(varrock_npcs.aubury) { player.openRuneShop(it.npc) }
    }

    private fun Player.openRuneShop(npc: Npc) {
        shops.open(this, npc, "Aubury's Rune Shop", varrock_invs.rune_shop)
    }

    private suspend fun ProtectedAccess.auburyDialogue(npc: Npc) =
        startDialogue(npc) { auburyChat(npc) }

    private suspend fun Dialogue.auburyChat(npc: Npc) {
        chatNpc(happy, "Hello there. Can I interest you in some runes?")
        val choice = choice2("Yes please, what have you got?", 1, "No thanks.", 2)
        if (choice == 1) {
            player.openRuneShop(npc)
        } else {
            chatPlayer(neutral, "No thanks.")
        }
    }
}
