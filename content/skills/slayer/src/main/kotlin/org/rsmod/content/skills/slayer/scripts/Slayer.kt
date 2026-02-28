package org.rsmod.content.skills.slayer.scripts

import jakarta.inject.Inject
import kotlin.random.Random
import org.rsmod.api.config.refs.BaseVarps
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.baseSlayerLvl
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.random.GameRandom
import org.rsmod.api.script.onNpcDeath
import org.rsmod.api.script.onNpcHit
import org.rsmod.api.script.onOpHeld1
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc3
import org.rsmod.api.shops.Shops
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.content.skills.slayer.SlayerTask
import org.rsmod.content.skills.slayer.configs.slayer_invs
import org.rsmod.content.skills.slayer.configs.slayer_varbits
import org.rsmod.content.skills.slayer.configs.slayer_varps
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.hit.HitType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Slayer
@Inject
constructor(
    private val random: GameRandom,
    private val shops: Shops,
    private val playerList: PlayerList,
) : PluginScript() {

    private var Player.slayerCount by intVarp(BaseVarps.slayer_count)
    private var Player.slayerTarget by intVarp(BaseVarps.slayer_target)
    private var Player.slayerTaskStreak by intVarp(slayer_varps.tasks_completed_1)
    private var Player.slayerPoints by intVarBit(slayer_varbits.points)
    private val slayerProtectionCooldownByPlayer = mutableMapOf<Int, Int>()

    override fun ScriptContext.startup() {
        // Turael - Burthorpe (combat 0+, slayer 1+)
        onOpNpc1(SlayerNpcs.turael) { talkToMaster(it.npc, SlayerMaster.Turael) }
        onOpNpc3(SlayerNpcs.turael) { player.openSlayerShop(it.npc) }

        // Mazchna - Canifis (combat 20+, slayer 1+)
        onOpNpc1(SlayerNpcs.mazchna) { talkToMaster(it.npc, SlayerMaster.Mazchna) }
        onOpNpc3(SlayerNpcs.mazchna) { player.openSlayerShop(it.npc) }

        // Vannaka - Edgeville Dungeon (combat 40+, slayer 1+)
        onOpNpc1(SlayerNpcs.vannaka) { talkToMaster(it.npc, SlayerMaster.Vannaka) }
        onOpNpc3(SlayerNpcs.vannaka) { player.openSlayerShop(it.npc) }

        // Chaeldar - Zanaris (combat 70+, slayer 1+)
        onOpNpc1(SlayerNpcs.chaeldar) { talkToMaster(it.npc, SlayerMaster.Chaeldar) }
        onOpNpc3(SlayerNpcs.chaeldar) { player.openSlayerShop(it.npc) }

        // Nieve - Tree Gnome Stronghold (combat 85+, slayer 1+)
        onOpNpc1(SlayerNpcs.nieve) { talkToMaster(it.npc, SlayerMaster.Nieve) }
        onOpNpc3(SlayerNpcs.nieve) { player.openSlayerShop(it.npc) }

        // Duradel - Shilo Village (combat 100+, slayer 50+)
        onOpNpc1(SlayerNpcs.duradel) { talkToMaster(it.npc, SlayerMaster.Duradel) }
        onOpNpc3(SlayerNpcs.duradel) { player.openSlayerShop(it.npc) }

        // Handle Slayer monster kills
        onNpcDeath {
            val player = killer ?: return@onNpcDeath
            val taskIndex = player.slayerTarget - 1
            val task = SlayerTask.get(taskIndex) ?: return@onNpcDeath

            if (task.npcNames.any { npc.type.name.contains(it, ignoreCase = true) }) {
                if (player.slayerCount > 0) {
                    player.slayerCount--
                    player.statAdvance(stats.slayer, npc.type.hitpoints.toDouble())

                    if (player.slayerCount == 0) {
                        completeTask(player, task)
                    }
                }
            }
        }

        onOpHeld1(SlayerObjs.enchanted_gem) { useEnchantedGem() }
        onOpHeld1(SlayerObjs.slayer_ring) { useSlayerRing() }

        onNpcHit(SlayerDangerNpcs.banshee) {
            val source = hit.resolvePlayerSource(playerList) ?: return@onNpcHit
            applySlayerProtectionEffect(
                sourceNpc = npc,
                player = source,
                requiredObj = SlayerObjs.earmuffs,
                failMessage = "The banshee's scream tears through your ears!",
                minDamage = 6,
                maxDamage = 14,
            )
        }
        onNpcHit(SlayerDangerNpcs.dust_devil) {
            val source = hit.resolvePlayerSource(playerList) ?: return@onNpcHit
            applySlayerProtectionEffect(
                sourceNpc = npc,
                player = source,
                requiredObj = SlayerObjs.facemask,
                failMessage = "The dust devil's sandstorm chokes you!",
                minDamage = 5,
                maxDamage = 12,
            )
        }
        onNpcHit(SlayerDangerNpcs.wall_beast) {
            val source = hit.resolvePlayerSource(playerList) ?: return@onNpcHit
            applySlayerProtectionEffect(
                sourceNpc = npc,
                player = source,
                requiredObj = SlayerObjs.wallbeast_spike_helmet,
                failMessage = "The wall beast mauls you through your exposed head!",
                minDamage = 4,
                maxDamage = 10,
            )
        }
    }

    private fun applySlayerProtectionEffect(
        sourceNpc: Npc,
        player: Player,
        requiredObj: org.rsmod.game.type.obj.ObjType,
        failMessage: String,
        minDamage: Int,
        maxDamage: Int,
    ) {
        if (player.worn.contains(requiredObj)) {
            return
        }
        val now = player.currentMapClock
        val nextAllowed = slayerProtectionCooldownByPlayer[player.slotId] ?: Int.MIN_VALUE
        if (now < nextAllowed) {
            return
        }
        slayerProtectionCooldownByPlayer[player.slotId] = now + 4
        val damage = Random.nextInt(minDamage, maxDamage + 1)
        player.queueHit(source = sourceNpc, delay = 0, type = HitType.Typeless, damage = damage)
        player.mes(failMessage, ChatType.Engine)
    }

    private fun completeTask(player: Player, task: SlayerTask) {
        player.slayerTaskStreak++
        val points = calculateSlayerPoints(player, task)

        if (player.slayerTaskStreak <= 4) {
            player.mes(
                "You've completed ${player.slayerTaskStreak} task(s). " +
                    "Slayer points start after your first 4 tasks.",
                ChatType.Engine,
            )
        } else {
            player.slayerPoints += points
            player.mes(
                "You receive $points Slayer points. Total: ${player.slayerPoints}.",
                ChatType.Engine,
            )
        }
        player.mes(
            "You have finished your Slayer task. Return to a Slayer master for a new one.",
            ChatType.Engine,
        )
    }

    private fun calculateSlayerPoints(player: Player, task: SlayerTask): Int {
        val streak = player.slayerTaskStreak
        val basePoints =
            when {
                streak % 50 == 0 -> 150 // Every 50th task
                streak % 10 == 0 -> 75 // Every 10th task
                else ->
                    when (getCurrentMaster(player)) {
                        SlayerMaster.Turael -> 0
                        SlayerMaster.Mazchna -> 2
                        SlayerMaster.Vannaka -> 4
                        SlayerMaster.Chaeldar -> 10
                        SlayerMaster.Nieve -> 12
                        SlayerMaster.Duradel -> 15
                        null -> 0
                    }
            }
        return basePoints
    }

    private fun getCurrentMaster(player: Player): SlayerMaster? {
        // This is a simplified version - in reality we'd track which master assigned the task
        // For now, we estimate based on player's combat level and available masters
        val combat = player.combatLevel
        return when {
            combat >= 100 && player.baseSlayerLvl >= 50 -> SlayerMaster.Duradel
            combat >= 85 -> SlayerMaster.Nieve
            combat >= 70 -> SlayerMaster.Chaeldar
            combat >= 40 -> SlayerMaster.Vannaka
            combat >= 20 -> SlayerMaster.Mazchna
            else -> SlayerMaster.Turael
        }
    }

    private suspend fun ProtectedAccess.talkToMaster(npc: Npc, master: SlayerMaster) {
        startDialogue(npc) {
            // Check combat level requirement
            if (player.combatLevel < master.combatReq) {
                chatNpc(
                    neutral,
                    "You need a combat level of ${master.combatReq} to receive tasks from me.",
                )
                return@startDialogue
            }

            // Check Slayer level requirement
            if (player.baseSlayerLvl < master.slayerReq) {
                chatNpc(
                    neutral,
                    "You need a Slayer level of ${master.slayerReq} to receive tasks from me.",
                )
                return@startDialogue
            }

            val taskIndex = player.slayerTarget - 1
            val task = SlayerTask.get(taskIndex)

            if (task == null || player.slayerCount <= 0) {
                // No active task - offer new one
                chatNpc(happy, "Hello, adventurer. I can assign you a slayer task.")
                val choice =
                    choice3(
                        "I'd like a Slayer task.",
                        1,
                        "Do you have anything for trade?",
                        2,
                        "Er... Nothing.",
                        3,
                    )
                when (choice) {
                    1 -> assignTask(master)
                    2 -> player.openSlayerShop(npc)
                    3 -> chatPlayer(neutral, "Er... Nothing.")
                }
            } else {
                // Already has a task
                chatNpc(
                    neutral,
                    "You're still hunting ${task.taskName}. You have ${player.slayerCount} to go.",
                )
                val choice =
                    choice3(
                        "I need another assignment.",
                        1,
                        "Could you tell me about my current task?",
                        2,
                        "Er... Nothing.",
                        3,
                    )
                when (choice) {
                    1 -> offerSkipOrCancel(master)
                    2 -> {
                        chatNpc(
                            neutral,
                            "You're assigned to kill ${task.taskName}; only ${player.slayerCount} more to go.",
                        )
                    }
                    3 -> chatPlayer(neutral, "Er... Nothing.")
                }
            }
        }
    }

    private suspend fun Dialogue.offerSkipOrCancel(master: SlayerMaster) {
        chatNpc(
            neutral,
            "I can give you a new task, but this will reset your current task streak. " +
                "Alternatively, you can cancel this task type permanently using Slayer points.",
        )
        val choice =
            choice3(
                "Reset my streak and give me a new task.",
                1,
                "I'd like to block this task (100 points).",
                2,
                "Never mind, I'll keep my current task.",
                3,
            )
        when (choice) {
            1 -> {
                // Reset streak and assign new task
                player.slayerTaskStreak = 0
                assignTask(master, skipped = true)
            }
            2 -> blockCurrentTask()
            3 -> chatPlayer(neutral, "Never mind, I'll keep my current task.")
        }
    }

    private suspend fun Dialogue.blockCurrentTask() {
        // TODO: task blocking requires multiple per-task varps (OSRS uses slayer_blocked_* series)
        chatNpc(neutral, "Task blocking is not yet available on this server.")
    }

    private suspend fun Dialogue.assignTask(master: SlayerMaster, skipped: Boolean = false) {
        if (skipped) {
            chatNpc(neutral, "Your streak has been reset. Let me assign you a new task...")
        }

        // Get available tasks for this master
        val availableTasks = getTasksForMaster(master, player)

        if (availableTasks.isEmpty()) {
            chatNpc(neutral, "I don't have any tasks suitable for your combat and Slayer levels.")
            return
        }

        // Weighted random selection
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

        // Calculate task amount based on master's tier
        val amountMultiplier =
            when (master) {
                SlayerMaster.Turael -> 0.8
                SlayerMaster.Mazchna -> 0.9
                SlayerMaster.Vannaka -> 1.0
                SlayerMaster.Chaeldar -> 1.1
                SlayerMaster.Nieve -> 1.2
                SlayerMaster.Duradel -> 1.3
            }
        val baseAmount = random.of(selectedTask.minAmount, selectedTask.maxAmount)
        val amount = (baseAmount * amountMultiplier).toInt().coerceAtLeast(5)

        player.slayerTarget = selectedTask.ordinal + 1
        player.slayerCount = amount

        chatNpc(happy, "Excellent! You're assigned to kill $amount ${selectedTask.taskName}.")
        chatNpc(neutral, "See me when you've finished your task.")
    }

    private fun getTasksForMaster(master: SlayerMaster, player: Player): List<SlayerTask> {
        return SlayerTask.values.filter { task ->
            task.slayerLevel <= player.baseSlayerLvl &&
                task.combatLevel <= player.combatLevel &&
                isTaskForMaster(task, master)
        }
    }

    private fun isTaskForMaster(task: SlayerTask, master: SlayerMaster): Boolean {
        return when (master) {
            SlayerMaster.Turael -> task.slayerLevel <= 1 && task.weight >= 6
            SlayerMaster.Mazchna -> task.slayerLevel <= 20
            SlayerMaster.Vannaka -> task.slayerLevel <= 40
            SlayerMaster.Chaeldar -> task.slayerLevel <= 70 || task.weight <= 6
            SlayerMaster.Nieve -> true // Can assign almost anything
            SlayerMaster.Duradel -> true // Can assign almost anything, prefers harder tasks
        }
    }

    private suspend fun ProtectedAccess.useEnchantedGem() {
        val taskIndex = player.slayerTarget - 1
        val task = SlayerTask.get(taskIndex)

        if (task == null || player.slayerCount <= 0) {
            mes("You don't have a Slayer task. Visit a Slayer Master to get one!")
            return
        }

        mes(
            "You're currently assigned to kill ${task.taskName}; only ${player.slayerCount} more to go."
        )
    }

    private suspend fun ProtectedAccess.useSlayerRing() {
        val taskIndex = player.slayerTarget - 1
        val task = SlayerTask.get(taskIndex)

        if (task == null || player.slayerCount <= 0) {
            mes("You don't have a Slayer task.")
            return
        }

        mes("You need to kill ${player.slayerCount} more ${task.taskName}.")

        // Slayer ring also provides teleport to Slayer Tower, etc.
        // This would be implemented with a teleport menu
    }

    private fun Player.openSlayerShop(npc: Npc) {
        shops.open(this, npc, "Slayer Equipment", slayer_invs.slayer_shop)
    }
}

internal object SlayerNpcs : NpcReferences() {
    val turael = find("slayer_master_1_tureal")
    val mazchna = find("slayer_master_2_mazchna")
    val vannaka = find("slayer_master_3")
    val chaeldar = find("slayer_master_4")
    val nieve = find("slayer_master_nieve")
    val duradel = find("slayer_master_5_duradel")
}

internal object SlayerDangerNpcs : NpcReferences() {
    val banshee = find("slayer_banshee_1")
    val dust_devil = find("slayer_dustdevil")
    val wall_beast = find("swamp_wallbeast")
}

internal object SlayerObjs : ObjReferences() {
    val enchanted_gem = find("slayer_gem")
    val slayer_ring = find("slayer_ring_8")
    val slayer_ring_eternal = find("slayer_ring_eternal")
    val earmuffs = find("slayer_earmuffs")
    val facemask = find("slayer_facemask")
    val wallbeast_spike_helmet = find("wallbeast_spike_helmet")
}

private enum class SlayerMaster(val combatReq: Int, val slayerReq: Int = 1) {
    Turael(0),
    Mazchna(20),
    Vannaka(40),
    Chaeldar(70),
    Nieve(85),
    Duradel(100, 50),
}
