package org.rsmod.content.other.progressivebots

/**
 * Bot personality — defines behavior patterns per planner archetype.
 *
 * Each archetype has a tick-interval decision function that picks
 * what action the bot should take next based on its state.
 */
sealed class BotPersonality {
    /** Pick the next action for this bot. Default: wander around. */
    open fun pickAction(player: BotPlayerView, state: BotState): BotTaskAction =
        BotTaskAction.Wander

    companion object {
        fun forPlanner(planner: BotPlanner): BotPersonality = when (planner) {
            BotPlanner.Skiller -> SkillerPersonality()
            BotPlanner.Fighter -> FighterPersonality()
            BotPlanner.Balanced -> BalancedPersonality()
            BotPlanner.Social -> SocialPersonality()
            BotPlanner.Vendor -> VendorPersonality()
            BotPlanner.PKer -> PKerPersonality()
        }
    }
}

/** Gather resources: chop trees, mine rocks, fish, etc. */
class SkillerPersonality : BotPersonality() {
    override fun pickAction(player: BotPlayerView, state: BotState): BotTaskAction {
        return BotTaskAction.Gather
    }
}

/** Fight NPCs to train combat. */
class FighterPersonality : BotPersonality() {
    override fun pickAction(player: BotPlayerView, state: BotState): BotTaskAction {
        if (player.inCombat) return BotTaskAction.Idle
        return BotTaskAction.Fight
    }
}

/** Mix of skills, combat, and questing. */
class BalancedPersonality : BotPersonality() {
    override fun pickAction(player: BotPlayerView, state: BotState): BotTaskAction {
        val roll = (0..99).random()
        return when {
            roll < 40 -> BotTaskAction.Gather
            roll < 70 -> BotTaskAction.Fight
            roll < 85 -> BotTaskAction.Socialize
            else -> BotTaskAction.Wander
        }
    }
}

/** Wander around high-traffic areas and socialize. */
class SocialPersonality : BotPersonality() {
    override fun pickAction(player: BotPlayerView, state: BotState): BotTaskAction {
        return BotTaskAction.Socialize
    }
}

/** Buy low, sell high at shops. */
class VendorPersonality : BotPersonality() {
    override fun pickAction(player: BotPlayerView, state: BotState): BotTaskAction {
        return BotTaskAction.Shop
    }
}

/** Hunt players in the Wilderness. */
class PKerPersonality : BotPersonality() {
    override fun pickAction(player: BotPlayerView, state: BotState): BotTaskAction {
        return BotTaskAction.Wander
    }
}

/**
 * Lightweight view of a player's state for bot decision-making.
 */
data class BotPlayerView(
    val x: Int,
    val z: Int,
    val level: Int,
    val inCombat: Boolean,
    val animating: Boolean,
)
