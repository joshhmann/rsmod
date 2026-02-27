package org.rsmod.content.areas.city.varrock.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.refs.varps
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.varrock.configs.varrock_objs
import org.rsmod.content.areas.city.varrock.configs.varrock_varbits
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Apothecary @Inject constructor(private val shops: Shops, private val objRepo: ObjRepository) :
    PluginScript() {
    override fun ScriptContext.startup() {
        // Op1 removed: RomeoJuliet quest script owns apothecary Op1 across all quest stages
    }

    private suspend fun ProtectedAccess.apothecaryDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "I am the Apothecary. I brew potions. Do you need anything specific?")
            if (
                inv.contains(varrock_objs.adventurepath_combat_voucher) &&
                    vars[varrock_varbits.adventurepath_combat_free_potion_reward] < 2
            ) {
                adventurePathDialogue(npc)
            } else {
                standardDialogue(npc)
            }
        }

    private suspend fun Dialogue.standardDialogue(npc: Npc) {
        val choice = choice2("Can you make potions for me?", 1, "Talk about something else.", 2)
        if (choice == 1) {
            chatPlayer(neutral, "Can you make potions for me?")
            chatNpc(
                happy,
                "I can make you strength potions or anti-poison potions. You'll need to provide the ingredients, though.",
            )
        } else {
            talkAboutSomethingElse(npc)
        }
    }

    private suspend fun Dialogue.talkAboutSomethingElse(npc: Npc) {
        val rjProgress = vars[varps.rjquest]
        val choice =
            if (rjProgress in 10..99) {
                choice3(
                    "Talk about Romeo & Juliet.",
                    1,
                    "Have you got any decent gossip to share?",
                    2,
                    "No thanks.",
                    3,
                )
            } else {
                choice2("Have you got any decent gossip to share?", 1, "No thanks.", 2)
            }

        when (choice) {
            1 -> {
                if (rjProgress in 10..99) {
                    chatPlayer(neutral, "Talk about Romeo & Juliet.")
                } else {
                    gossip(npc)
                }
            }
            2 -> {
                if (rjProgress in 10..99) {
                    gossip(npc)
                } else {
                    chatPlayer(neutral, "No thanks.")
                }
            }
            3 -> chatPlayer(neutral, "No thanks.")
        }
    }

    private suspend fun Dialogue.gossip(npc: Npc) {
        chatPlayer(neutral, "Have you got any decent gossip to share?")
        val rjProgress = vars[varps.rjquest]
        if (rjProgress == 0) {
            chatNpc(
                happy,
                "Well I hear young Romeo's having a little woman trouble but other than that all's quiet on the eastern front. Can I do something for you?",
            )
        } else if (rjProgress >= 100) {
            chatNpc(happy, "Sad about that affair with young Romeo and Juliet...")
            chatNpc(
                happy,
                "I hear every time Romeo sees Juliet now he runs away screaming something about ghosts and Juliet's cousin?",
            )
            chatNpc(happy, "Always did think he was a bit of a strange one...")
            chatNpc(happy, "Anyway! Life goes on and so does business! Can I do something for you?")
        } else {
            chatNpc(happy, "Nothing much at the moment. Can I do something for you?")
        }
        standardDialogue(npc)
    }

    private suspend fun Dialogue.adventurePathDialogue(npc: Npc) {
        chatPlayer(
            neutral,
            "I have this voucher for completing the combat Adventure Path, it says to come talk to you about claiming my Adventure Path reward.",
        )

        val claimed = vars[varrock_varbits.adventurepath_combat_free_potion_reward]
        if (claimed == 0) {
            chatNpc(happy, "Why yes, here are some energy potions.")
            player.invAddOrDrop(objRepo, varrock_objs.energy_potion_3, 5)
            objbox(varrock_objs.energy_potion_3, "The Apothecary gives you 5 energy potions.")
            if (player.members) {
                chatNpc(
                    happy,
                    "Since you are a member, I'll also give you some extra potions for your strength and combat skills.",
                )
                player.invAddOrDrop(objRepo, varrock_objs.strength_potion_3, 5)
                player.invAddOrDrop(objRepo, varrock_objs.combat_potion_3, 5)
                vars[varrock_varbits.adventurepath_combat_free_potion_reward] = 2
            } else {
                chatNpc(
                    happy,
                    "I do have more rewards for you, but you need to be on a members world for me to give you the reward. Maybe there is something else I can help you with.",
                )
                vars[varrock_varbits.adventurepath_combat_free_potion_reward] = 1
            }
        } else if (claimed == 1 && player.members) {
            chatNpc(
                happy,
                "Why yes, here are some extra potions for your strength and combat skills.",
            )
            player.invAddOrDrop(objRepo, varrock_objs.strength_potion_3, 5)
            player.invAddOrDrop(objRepo, varrock_objs.combat_potion_3, 5)
            vars[varrock_varbits.adventurepath_combat_free_potion_reward] = 2
        } else {
            chatNpc(
                happy,
                "I have already given you your reward. Is there anything else I can help you with?",
            )
        }

        standardDialogue(npc)
    }
}
