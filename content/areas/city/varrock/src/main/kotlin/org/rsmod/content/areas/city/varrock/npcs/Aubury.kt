package org.rsmod.content.areas.city.varrock.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.refs.varps
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpNpc3
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.varrock.configs.varrock_invs
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.content.areas.city.varrock.configs.varrock_objs
import org.rsmod.content.areas.city.varrock.configs.varrock_varbits
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Aubury @Inject constructor(private val shops: Shops, private val objRepo: ObjRepository) :
    PluginScript() {
    override fun ScriptContext.startup() {
        // Op1 removed: RuneMysteries quest script owns aubury Op1 across all quest stages
        onOpNpc3(varrock_npcs.aubury) { player.openRuneShop(it.npc) }
    }

    private fun Player.openRuneShop(npc: Npc) {
        shops.open(this, npc, "Aubury's Rune Shop", varrock_invs.rune_shop)
    }

    private suspend fun ProtectedAccess.auburyDialogue(npc: Npc) =
        startDialogue(npc) {
            if (
                inv.contains(varrock_objs.adventurepath_combat_voucher) &&
                    vars[varrock_varbits.adventurepath_combat_free_potion_reward] < 2
            ) {
                adventurePathDialogue(npc)
            } else {
                chatNpc(happy, "Do you want to buy some runes?")
                standardDialogue(npc)
            }
        }

    private suspend fun Dialogue.standardDialogue(npc: Npc) {
        val runeMysteriesFinished = vars[varps.runemysteries] >= 6

        val choice =
            if (runeMysteriesFinished) {
                choice3(
                    "Yes please!",
                    1,
                    "Oh, it's a rune shop. No thank you, then.",
                    2,
                    "Can you teleport me to the Rune Essence?",
                    3,
                )
            } else {
                choice2("Yes please!", 1, "Oh, it's a rune shop. No thank you, then.", 2)
            }

        when (choice) {
            1 -> player.openRuneShop(npc)
            2 -> {
                chatPlayer(neutral, "Oh, it's a rune shop. No thank you, then.")
                chatNpc(
                    happy,
                    "Well, if you find someone who does want runes, please send them my way.",
                )
            }
            3 -> {
                chatPlayer(quiz, "Can you teleport me to the Rune Essence?")
                chatNpc(
                    happy,
                    "Of course. By the way, if you end up making any runes from the essence you mine, I'll happily buy them from you.",
                )
                chatNpc(happy, "Senventior Disthine Molenko!")
                access.teleport(CoordGrid(2911, 4832, 0))
            }
        }
    }

    private suspend fun Dialogue.adventurePathDialogue(npc: Npc) {
        chatPlayer(
            neutral,
            "I have this voucher for completing the combat Adventure Path, it says to come talk to you about claiming my Adventure Path reward.",
        )

        val claimed = vars[varrock_varbits.adventurepath_combat_free_potion_reward]
        val airRune = varrock_objs.airrune
        val mindRune = varrock_objs.mindrune
        if (claimed == 0) {
            chatNpc(happy, "Why yes, here are some air and mind runes.")
            player.invAddOrDrop(objRepo, airRune, 200)
            player.invAddOrDrop(objRepo, mindRune, 200)
            doubleobjbox(airRune, mindRune, "Aubury gives you 200 air and 200 mind runes.")
            if (player.members) {
                chatNpc(
                    happy,
                    "I do have more rewards for you, but you need some free space for me to give you the rewards.",
                )
                vars[varrock_varbits.adventurepath_combat_free_potion_reward] = 1
            } else {
                chatNpc(
                    happy,
                    "I do have more rewards for you, but you need to be on a members world for me to give you the reward. Maybe there is something else I can help you with.",
                )
                vars[varrock_varbits.adventurepath_combat_free_potion_reward] = 1
            }
        } else {
            chatNpc(
                happy,
                "I do have more rewards for you, but you need to be on a members world for me to give you the reward. Maybe there is something else I can help you with.",
            )
        }

        chatNpc(happy, "Do you want to buy some runes?")
        standardDialogue(npc)
    }
}
