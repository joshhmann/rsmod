package org.rsmod.content.quests.vampyreslayer

import jakarta.inject.Inject
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.getQuestStage
import org.rsmod.api.quest.giveQuestReward
import org.rsmod.api.quest.setQuestStage
import org.rsmod.api.quest.showCompletionScroll
import org.rsmod.api.script.onModifyNpcHit
import org.rsmod.api.script.onNpcHit
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.vampyreslayer.configs.vampyre_slayer_npcs
import org.rsmod.content.quests.vampyreslayer.configs.vampyre_slayer_objs
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.game.hit.HitType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Vampyre Slayer quest implementation for RSMod v2.
 *
 * Slay Count Draynor the vampyre using garlic to weaken him and a stake to finish him off.
 *
 * Reward: 4,825 Attack XP + 1 Quest Point
 */
class VampyreSlayer
@Inject
constructor(
    private val playerList: PlayerList,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    private val biteCooldownByNpc = ConcurrentHashMap<Int, Int>()
    private val nonStakeWarnByPlayer = ConcurrentHashMap<String, Int>()

    override fun ScriptContext.startup() {
        // Morgan in Draynor Village (quest start)
        onOpNpc1(vampyre_slayer_npcs.morgan) { startMorganDialogue(it.npc) }

        // Dr Harlow in Blue Moon Inn (gives stake)
        onOpNpc1(vampyre_slayer_npcs.dr_harlow) { startHarlowDialogue(it.npc) }

        // Count Draynor in Draynor Manor (boss)
        onOpNpc1(vampyre_slayer_npcs.count_draynor) { startDraynorDialogue(it.npc) }

        onModifyNpcHit(vampyre_slayer_npcs.count_draynor) {
            if (!hit.isFromPlayer) {
                return@onModifyNpcHit
            }
            val sourceUid = hit.sourceUid ?: return@onModifyNpcHit
            val source = PlayerUid(sourceUid).resolve(playerList) ?: return@onModifyNpcHit

            if (source.inv.contains(vampyre_slayer_objs.garlic)) {
                hit.damage = (hit.damage * 3) / 2
            }

            val finisherWithStake = hit.isRighthandObj(vampyre_slayer_objs.stake)
            val lethalWithoutStake = npc.hitpoints - hit.damage <= 0 && !finisherWithStake
            if (lethalWithoutStake) {
                hit.damage = max(0, npc.hitpoints - 1)
                val now = source.currentMapClock
                val key = source.avatar.name.lowercase()
                val lastWarn = nonStakeWarnByPlayer[key] ?: Int.MIN_VALUE
                if (now - lastWarn >= 4) {
                    nonStakeWarnByPlayer[key] = now
                    protectedAccess.launch(source) {
                        mes("Count Draynor can only be finished with a stake.")
                    }
                }
            }
        }

        onNpcHit(vampyre_slayer_npcs.count_draynor) {
            val source = hit.resolvePlayerSource(playerList) ?: return@onNpcHit

            // Vampyre bite special every 4 ticks while alive.
            if (npc.hitpoints > 0) {
                val now = npc.currentMapClock
                val npcKey = npc.uid.packed
                val nextAllowed = biteCooldownByNpc[npcKey] ?: Int.MIN_VALUE
                if (now >= nextAllowed) {
                    biteCooldownByNpc[npcKey] = now + 4
                    npc.anim(seqs.human_unarmedblock)
                    val biteDamage = 4
                    source.queueHit(
                        source = npc,
                        delay = 0,
                        type = HitType.Melee,
                        damage = biteDamage,
                    )
                    val heal = max(1, biteDamage / 2)
                    npc.hitpoints = min(npc.baseHitpointsLvl, npc.hitpoints + heal)
                }
                return@onNpcHit
            }

            if (!hit.isRighthandObj(vampyre_slayer_objs.stake)) {
                return@onNpcHit
            }
            protectedAccess.launch(source) {
                if (getQuestStage(QuestList.vampyre_slayer) != 1) {
                    return@launch
                }
                setQuestStage(QuestList.vampyre_slayer, 2)
                giveQuestReward(QuestList.vampyre_slayer)
                showCompletionScroll(
                    quest = QuestList.vampyre_slayer,
                    rewards = listOf("3 Quest Points", "4,825 Attack XP"),
                    itemModel = vampyre_slayer_objs.stake,
                    questPoints = 3,
                )
            }
        }
    }

    private suspend fun ProtectedAccess.startMorganDialogue(npc: Npc) {
        startDialogue(npc) {
            when (getQuestStage(QuestList.vampyre_slayer)) {
                0 -> morganStartDialogue()
                1 -> morganInProgressDialogue()
                else -> morganFinishedDialogue()
            }
        }
    }

    private suspend fun ProtectedAccess.startHarlowDialogue(npc: Npc) {
        startDialogue(npc) {
            when (getQuestStage(QuestList.vampyre_slayer)) {
                0 -> {
                    chatNpc(neutral, "Not now, friend. You look like you need Morgan first.")
                }
                1 -> harlowInProgressDialogue()
                else -> chatNpc(happy, "You did it! You took that fiend down.")
            }
        }
    }

    private suspend fun ProtectedAccess.startDraynorDialogue(npc: Npc) {
        startDialogue(npc) {
            when (getQuestStage(QuestList.vampyre_slayer)) {
                0 -> {
                    chatNpc(angry, "Fresh blood...")
                    chatPlayer(sad, "I should probably talk to Morgan first.")
                }
                1 -> {
                    chatNpc(angry, "You dare challenge me?")
                    chatPlayer(angry, "Your reign ends here, vampyre.")
                }
                else -> chatNpc(angry, "You are fortunate this time, human.")
            }
        }
    }

    private suspend fun Dialogue.morganStartDialogue() {
        chatNpc(sad, "Please, can you help us? Count Draynor is terrorising the village.")
        val choice = choice2("I'll help. Tell me what to do.", 1, "That sounds dangerous.", 2)
        when (choice) {
            1 -> {
                chatPlayer(happy, "I'll help. Tell me what to do.")
                chatNpc(
                    neutral,
                    "Find Dr Harlow in the Blue Moon Inn. He knows how to kill vampyres.",
                )
                chatNpc(neutral, "Take garlic with you. It weakens Count Draynor.")
                access.setQuestStage(QuestList.vampyre_slayer, 1)
            }
            2 -> chatPlayer(sad, "That sounds dangerous.")
        }
    }

    private suspend fun Dialogue.morganInProgressDialogue() {
        chatNpc(quiz, "Have you destroyed Count Draynor yet?")
        if (player.inv.contains(vampyre_slayer_objs.garlic)) {
            chatPlayer(happy, "I'm still working on it. I have garlic ready.")
        } else {
            chatPlayer(neutral, "Not yet.")
            chatNpc(neutral, "Bring garlic and a stake. You'll need both.")
        }
    }

    private suspend fun Dialogue.morganFinishedDialogue() {
        chatNpc(happy, "Thank you! Draynor can sleep in peace again.")
    }

    private suspend fun Dialogue.harlowInProgressDialogue() {
        if (!player.inv.contains(vampyre_slayer_objs.stake)) {
            chatNpc(neutral, "You here for vampyre hunting? You'll need this.")
            val addedStake = player.invAdd(player.inv, vampyre_slayer_objs.stake, 1).success
            if (addedStake) {
                chatNpc(happy, "Use this stake for the final blow.")
                chatNpc(neutral, "Garlic will weaken Count Draynor while you fight.")
            } else {
                chatNpc(sad, "Clear one inventory slot and talk to me again for the stake.")
            }
        } else {
            chatNpc(neutral, "You already have a stake. Finish the job in Draynor Manor.")
        }
    }
}
