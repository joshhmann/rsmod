package org.rsmod.content.mechanics.clues.scripts

import jakarta.inject.Inject
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onOpHeld1
import org.rsmod.api.script.onPlayerLogin
import org.rsmod.content.mechanics.clues.configs.BeginnerClueSteps
import org.rsmod.content.mechanics.clues.configs.ClueRewardTablesImpl
import org.rsmod.content.mechanics.clues.configs.ClueScrollObjs
import org.rsmod.content.mechanics.clues.configs.ClueScrollVarps
import org.rsmod.content.mechanics.clues.configs.ClueTier
import org.rsmod.content.mechanics.clues.configs.EasyClueSteps
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Clue Scroll system implementation.
 *
 * Handles:
 * - Clue scroll reading/examining
 * - Step progression
 * - Casket rewards
 * - Completion tracking
 */
class ClueScrollScript
@Inject
constructor(
    private val objRepo: org.rsmod.api.repo.obj.ObjRepository,
    private val objTypes: org.rsmod.game.type.obj.ObjTypeList,
) : PluginScript() {

    override fun ScriptContext.startup() {
        onPlayerLogin { player.initializeClueTracking() }

        // Handle reading clue scrolls
        onOpHeld1(ClueScrollObjs.beginner_clue_scroll) { player.readClue(ClueTier.BEGINNER) }
        onOpHeld1(ClueScrollObjs.easy_clue_scroll) { player.readClue(ClueTier.EASY) }

        // Handle opening caskets
        onOpHeld1(ClueScrollObjs.beginner_casket) { player.openCasket(ClueTier.BEGINNER) }
        onOpHeld1(ClueScrollObjs.easy_casket) { player.openCasket(ClueTier.EASY) }
    }

    /** Initializes clue tracking for a player on login. */
    private fun Player.initializeClueTracking() {
        // Check for active clue and update status
        val activeClue = getActiveClue()
        if (activeClue != null) {
            mes("You have an active ${activeClue.displayName} clue scroll.")
        }
    }

    /** Reads a clue scroll and displays the current step. */
    private fun Player.readClue(tier: ClueTier) {
        val step = getCurrentClueStep(tier)
        if (step != null) {
            mes("The clue reads:")
            mes(step.description)

            // Show hint if available
            when (step.type) {
                org.rsmod.content.mechanics.clues.configs.ClueStepType.EMOTE -> {
                    mes("Hint: Perform the ${step.emote} emote.")
                }
                org.rsmod.content.mechanics.clues.configs.ClueStepType.DIG -> {
                    mes("Hint: You need to dig at this location.")
                }
                org.rsmod.content.mechanics.clues.configs.ClueStepType.SIMPLE -> {
                    mes("Hint: Talk to ${step.answer}.")
                }
                else -> {
                    mes("Hint: Follow the instructions carefully.")
                }
            }
        } else {
            mes("This clue has expired or is invalid.")
        }
    }

    /** Opens a reward casket and gives rewards. */
    private fun Player.openCasket(tier: ClueTier) {
        // Remove the casket
        // invRemove(inv, getCasketObj(tier), 1)

        // Roll rewards
        val rewards = rollClueRewards(tier)

        mes("You open the ${tier.displayName.lowercase()} casket...")

        if (rewards.isEmpty()) {
            mes("The casket is empty!")
            return
        }

        // Give rewards
        for ((rewardName, count) in rewards) {
            mes("You receive: $rewardName x$count")
            val type = objTypes.values.find { it.internalName == rewardName }
            if (type != null) {
                invAddOrDrop(objRepo, type, count)
            }
        }

        // Update completion count
        incrementClueCompletions(tier)
    }

    /** Advances to the next clue step. */
    fun advanceClueStep(player: Player, tier: ClueTier) {
        val currentStep = player.getCurrentClueStepNumber(tier)
        val maxSteps = tier.maxSteps

        if (currentStep >= maxSteps) {
            // Clue complete - convert to casket
            player.completeClue(tier)
        } else {
            // Advance to next step
            player.setCurrentClueStep(tier, currentStep + 1)
            player.mes("You've completed this step! Read your clue scroll for the next step.")
        }
    }

    /** Gets the active clue tier for a player. */
    private fun Player.getActiveClue(): ClueTier? {
        // Check inventory for clue scrolls
        // TODO: Implement inventory check
        return null
    }

    /** Gets the current clue step for a player. */
    private fun Player.getCurrentClueStep(
        tier: ClueTier
    ): org.rsmod.content.mechanics.clues.configs.ClueStep? {
        val stepNumber = getCurrentClueStepNumber(tier)
        return when (tier) {
            ClueTier.BEGINNER -> BeginnerClueSteps.steps.getOrNull(stepNumber - 1)
            ClueTier.EASY -> EasyClueSteps.steps.getOrNull(stepNumber - 1)
            else -> null // TODO: Implement other tiers
        }
    }

    /** Rolls rewards for a clue tier. */
    private fun rollClueRewards(tier: ClueTier): List<Pair<String, Int>> {
        return when (tier) {
            ClueTier.BEGINNER -> ClueRewardTablesImpl.rollBeginnerRewards()
            ClueTier.EASY -> ClueRewardTablesImpl.rollEasyRewards()
            else -> emptyList()
        }
    }

    /** Completes a clue and converts it to a casket. */
    private fun Player.completeClue(tier: ClueTier) {
        // Remove clue scroll
        // invRemove(inv, getClueScrollObj(tier), 1)

        // Add casket
        invAddOrDrop(objRepo, objTypes[getCasketObj(tier)]!!, 1)

        mes("You've completed the ${tier.displayName.lowercase()} clue! You receive a casket.")
    }

    /** Increments the completion counter for a clue tier. */
    private fun Player.incrementClueCompletions(tier: ClueTier) {
        val current = clueCompletions
        clueCompletions = current + 1
    }
}

/** Player extension properties for clue tracking. */
private var Player.clueCompletions by intVarp(ClueScrollVarps.completed_clues)
private var Player.clueQuestMain by intVarp(ClueScrollVarps.cluequest_main)

/** Gets the current clue step number for a tier. */
private fun Player.getCurrentClueStepNumber(tier: ClueTier): Int {
    // Extract step number from varp bits
    // TODO: Implement proper bit extraction
    return 1
}

/** Sets the current clue step number for a tier. */
private fun Player.setCurrentClueStep(tier: ClueTier, step: Int) {
    // Store step number in varp bits
    // TODO: Implement proper bit storage
}

/** Gets the obj reference for a clue scroll tier. */
private fun getClueScrollObj(tier: ClueTier): Int {
    return when (tier) {
        ClueTier.BEGINNER -> ClueScrollObjs.beginner_clue_scroll.id
        ClueTier.EASY -> ClueScrollObjs.easy_clue_scroll.id
        else -> -1
    }
}

/** Gets the obj reference for a casket tier. */
private fun getCasketObj(tier: ClueTier): Int {
    return when (tier) {
        ClueTier.BEGINNER -> ClueScrollObjs.beginner_casket.id
        ClueTier.EASY -> ClueScrollObjs.easy_casket.id
        else -> -1
    }
}
