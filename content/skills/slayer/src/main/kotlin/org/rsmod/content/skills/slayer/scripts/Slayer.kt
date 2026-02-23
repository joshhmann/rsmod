package org.rsmod.content.skills.slayer.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.BaseVarps
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.baseSlayerLvl
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.random.GameRandom
import org.rsmod.api.script.onNpcDeath
import org.rsmod.api.script.onOpHeld1
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.content.skills.slayer.SlayerTask
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Slayer @Inject constructor(private val random: GameRandom) : PluginScript() {

    private var Player.slayerCount by intVarp(BaseVarps.slayer_count)
    private var Player.slayerTarget by intVarp(BaseVarps.slayer_target)

    override fun ScriptContext.startup() {
        onOpNpc1(SlayerNpcs.turael) { talkToTurael(it.npc) }

        onNpcDeath {
            val player = killer ?: return@onNpcDeath
            val taskIndex = player.slayerTarget - 1
            val task = SlayerTask.get(taskIndex) ?: return@onNpcDeath

            if (task.npcNames.any { npc.type.name.contains(it, ignoreCase = true) }) {
                if (player.slayerCount > 0) {
                    player.slayerCount--
                    player.statAdvance(stats.slayer, npc.type.hitpoints.toDouble())

                    if (player.slayerCount == 0) {
                        player.mes(
                            "You have finished your Slayer task. Return to a Slayer master for a new one.",
                            ChatType.Engine,
                        )
                    }
                }
            }
        }

        onOpHeld1(SlayerObjs.enchanted_gem) { useEnchantedGem() }
    }

    private suspend fun ProtectedAccess.talkToTurael(npc: Npc) {
        startDialogue(npc) {
            val taskIndex = player.slayerTarget - 1
            val task = SlayerTask.get(taskIndex)

            if (task == null || player.slayerCount <= 0) {
                chatNpc(happy, "Hello, adventurer. I can assign you a slayer task.")
                val assign = choice2("Yes, please.", true, "No, thanks.", false)
                if (assign) {
                    assignTask(npc)
                }
            } else {
                chatNpc(
                    neutral,
                    "You're already on a task to kill ${player.slayerCount} ${task.taskName}.",
                )
                chatNpc(
                    neutral,
                    "Would you like me to reset it for you? This will reset your task streak.",
                )
                val reset = choice2("Yes, reset it.", true, "No, I'll finish it.", false)
                if (reset) {
                    assignTask(npc)
                }
            }
        }
    }

    private suspend fun Dialogue.assignTask(npc: Npc) {
        val availableTasks = SlayerTask.values.filter { it.slayerLevel <= player.baseSlayerLvl }
        if (availableTasks.isEmpty()) {
            chatNpc(neutral, "I don't have any tasks suitable for your level.")
            return
        }

        val totalWeight = availableTasks.sumOf { it.weight }
        var roll = random.of(0, totalWeight - 1)
        var selectedTask = availableTasks[0]

        for (task in availableTasks) {
            roll -= task.weight
            if (roll < 0) {
                selectedTask = task
                break
            }
        }

        val amount = random.of(selectedTask.minAmount, selectedTask.maxAmount)
        player.slayerTarget = selectedTask.ordinal + 1
        player.slayerCount = amount

        chatNpc(happy, "Your new task is to kill $amount ${selectedTask.taskName}.")
    }

    private suspend fun ProtectedAccess.useEnchantedGem() {
        val taskIndex = player.slayerTarget - 1
        val task = SlayerTask.get(taskIndex)

        if (task == null || player.slayerCount <= 0) {
            mes("You don't have a slayer task. Visit a Slayer Master to get one!")
            return
        }

        mes("You still need to kill ${player.slayerCount} ${task.taskName}.")
    }
}

internal object SlayerNpcs : NpcReferences() {
    val turael = find("slayer_master_1_tureal", 13618)
    val vannaka = find("slayer_master_3", 403)
}

internal object SlayerObjs : ObjReferences() {
    val enchanted_gem = find("enchanted_gem")
}
