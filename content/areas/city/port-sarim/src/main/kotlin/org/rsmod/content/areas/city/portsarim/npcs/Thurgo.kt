package org.rsmod.content.areas.city.portsarim.npcs

import jakarta.inject.Inject
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.invtx.invDel
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.content.areas.city.portsarim.configs.PortSarimNpcs
import org.rsmod.content.areas.city.portsarim.configs.portsarim_objs
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/** Quest-specific items defined locally to avoid cross-module dependency. */
object ThurgoObjs : ObjReferences() {
    val knights_portrait = find("knights_portrait")
    val faladian_sword = find("faladian_sword")
    val blurite_ore = find("blurite_ore")
    val iron_bar = find("iron_bar")
}

class ThurgoScript @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(PortSarimNpcs.thurgo) { thurgoDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.thurgoDialogue(npc: Npc) =
        startDialogue(npc) {
            val stage = getQuestStage(QuestList.knights_sword)

            when {
                stage <= 1 -> thurgoGenericDialogue()
                stage == 2 -> thurgoPieDialogue()
                stage == 3 -> thurgoPortraitDialogue()
                stage == 4 -> thurgoForgingDialogue()
                stage in 5..6 -> thurgoSwordReadyDialogue()
                stage >= 7 -> thurgoPostQuestDialogue()
                else -> thurgoGenericDialogue()
            }
        }

    private suspend fun Dialogue.thurgoGenericDialogue() {
        chatPlayer(quiz, "Hello there.")
        chatNpc(angry, "Go away! I'm busy.")
        val choice =
            choice3("Why are you so angry?", 1, "I need a sword made.", 2, "Goodbye.", 3)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Why are you so angry?")
                chatNpc(angry, "I'm hungry! I want my redberry pie!")
                chatNpc(sad, "Nobody brings me pie anymore...")
                chatPlayer(quiz, "Redberry pie?")
                chatNpc(happy, "Yes! Redberry pie is the food of champions!")
                chatNpc(neutral, "Bring me a redberry pie and I might help you.")
            }
            2 -> {
                chatPlayer(quiz, "I need a sword made.")
                chatNpc(angry, "Do I look like a blacksmith to you?")
                chatNpc(neutral, "Wait... I am a blacksmith. An Imcando blacksmith!")
                chatNpc(sad, "But I'm too hungry to work. My stomach is empty...")
                chatNpc(happy, "Bring me a redberry pie and I'll make you the finest sword!")
            }
            3 -> chatPlayer(neutral, "Goodbye.")
        }
    }

    private suspend fun Dialogue.thurgoPieDialogue() {
        val hasPie = player.inv.contains(portsarim_objs.redberry_pie)

        if (hasPie) {
            chatNpc(happy, "Hmm? What's that I smell? Is that... redberry pie?")
            chatPlayer(happy, "I brought you a redberry pie!")
            chatNpc(happy, "A redberry pie! For me? Oh, you wonderful person!")
            chatNpc(happy, "Mmm! Delicious! I feel so much better now!")
            chatNpc(happy, "You said you needed a sword made, right? I can help you!")
            chatNpc(neutral, "But I need to see what the sword looks like first.")
            chatNpc(neutral, "Do you have a picture or a drawing of the sword?")
            chatPlayer(neutral, "I can get one. Sir Vyvin has a portrait of his father holding the sword.")
            chatNpc(happy, "Perfect! Bring me a drawing of the sword and I'll see what I can do!")

            val removed = player.invDel(player.inv, portsarim_objs.redberry_pie, 1).success
            if (removed) {
                access.setQuestStage(QuestList.knights_sword, 3)
            } else {
                chatNpc(sad, "Hmm, the pie seems to have vanished...")
            }
        } else {
            chatNpc(angry, "I'm still hungry! Where's my redberry pie?")
            chatNpc(sad, "I can't work on an empty stomach...")
            chatPlayer(neutral, "I'll find you a redberry pie.")
            chatNpc(happy, "Please do! Redberry pies are the best!")
        }
    }

    private suspend fun Dialogue.thurgoPortraitDialogue() {
        val hasPortrait = player.inv.contains(ThurgoObjs.knights_portrait)

        if (hasPortrait) {
            chatPlayer(happy, "I have the portrait of the sword!")
            chatNpc(happy, "Let me see that! Ah yes, a fine ceremonial blade!")
            chatNpc(neutral, "I can make something like this, but I'll need materials.")
            chatNpc(neutral, "Bring me 2 iron bars and some blurite ore.")
            chatNpc(neutral, "I've heard there's blurite ore in the Asgarnian Ice Dungeon nearby.")
            chatNpc(happy, "Get me those materials and I'll forge the sword!")

            val removed = player.invDel(player.inv, ThurgoObjs.knights_portrait, 1).success
            if (removed) {
                access.setQuestStage(QuestList.knights_sword, 4)
            } else {
                chatNpc(sad, "Hmm, the portrait seems to have disappeared...")
            }
        } else {
            chatNpc(quiz, "Did you get the portrait of the sword?")
            chatPlayer(sad, "Not yet. I need to find it.")
            chatNpc(neutral, "You said Sir Vyvin has one. Check his chambers.")
            chatNpc(neutral, "I need to see the design before I can forge it!")
        }
    }

    private suspend fun Dialogue.thurgoForgingDialogue() {
        val hasBlurite = player.inv.contains(ThurgoObjs.blurite_ore)
        val hasIronBars = invCount(ThurgoObjs.iron_bar) >= 2

        if (hasBlurite && hasIronBars) {
            chatPlayer(happy, "I have blurite ore and two iron bars!")
            chatNpc(happy, "Splendid! Let me get to work!")
            chatNpc(happy, "The forge is hot, the metal is ready... This will be a masterpiece!")

            val removedBlurite = player.invDel(player.inv, ThurgoObjs.blurite_ore, 1).success
            val removedIron1 = player.invDel(player.inv, ThurgoObjs.iron_bar, 1).success
            val removedIron2 = player.invDel(player.inv, ThurgoObjs.iron_bar, 1).success

            if (removedBlurite && removedIron1 && removedIron2) {
                val added = player.invAdd(player.inv, ThurgoObjs.faladian_sword, 1).success
                if (added) {
                    chatNpc(happy, "Here you go! A perfect replica of Sir Vyvin's ceremonial sword!")
                    chatNpc(happy, "The Imcando craftsmanship is second to none!")
                    access.setQuestStage(QuestList.knights_sword, 5)
                } else {
                    chatNpc(sad, "You need a free inventory slot for the finished sword.")
                    player.invAddOrDrop(objRepo, ThurgoObjs.blurite_ore, 1)
                    player.invAddOrDrop(objRepo, ThurgoObjs.iron_bar, 1)
                    player.invAddOrDrop(objRepo, ThurgoObjs.iron_bar, 1)
                }
            } else {
                chatNpc(sad, "It seems you don't have all the materials after all.")
                if (removedBlurite) player.invAddOrDrop(objRepo, ThurgoObjs.blurite_ore, 1)
                if (removedIron1) player.invAddOrDrop(objRepo, ThurgoObjs.iron_bar, 1)
                if (removedIron2) player.invAddOrDrop(objRepo, ThurgoObjs.iron_bar, 1)
            }
        } else {
            val missing = mutableListOf<String>()
            if (!hasBlurite) missing += "blurite ore"
            if (!hasIronBars) missing += "2 iron bars"

            chatNpc(quiz, "Did you bring the materials?")
            chatPlayer(sad, "I still need: ${missing.joinToString(", ")}.")
            chatNpc(neutral, "Blurite ore can be found in the Asgarnian Ice Dungeon.")
            chatNpc(neutral, "Iron bars you can smelt from iron ore, or buy from a store.")
        }
    }

    private suspend fun Dialogue.thurgoSwordReadyDialogue() {
        chatNpc(happy, "The sword is ready! You should take it to the Squire in Falador.")
        chatNpc(happy, "He'll be overjoyed to have it back!")
        chatPlayer(happy, "Thank you, Thurgo! You're a true master smith!")
    }

    private suspend fun Dialogue.thurgoPostQuestDialogue() {
        chatNpc(happy, "Hello again! If you ever need another sword, you know where to find me!")
        chatNpc(happy, "The forge is always hot in Thurgo's workshop!")
        chatPlayer(happy, "Thanks, Thurgo!")
    }

    private fun Dialogue.invCount(obj: ObjType): Int {
        return player.inv.filterNotNull { it.id == obj.id }.sumOf { it.count }
    }
}
