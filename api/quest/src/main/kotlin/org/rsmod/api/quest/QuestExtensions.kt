package org.rsmod.api.quest

import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.varbit.VarBitType

public fun ProtectedAccess.getQuestStage(quest: Quest): Int {
    return vars[quest.varp]
}

public fun ProtectedAccess.setQuestStage(quest: Quest, stage: Int) {
    vars[quest.varp] = stage
}

public fun ProtectedAccess.advanceQuestStage(quest: Quest, amount: Int = 1) {
    val current = getQuestStage(quest)
    setQuestStage(quest, current + amount)
}

public fun ProtectedAccess.isQuestComplete(quest: Quest): Boolean {
    return getQuestStage(quest) >= quest.maxStage
}

public fun ProtectedAccess.getQuestAttribute(varbit: VarBitType): Boolean {
    return vars[varbit] != 0
}

public fun ProtectedAccess.setQuestAttribute(varbit: VarBitType, value: Boolean) {
    vars[varbit] = if (value) 1 else 0
}

public fun ProtectedAccess.giveQuestReward(quest: Quest) {
    quest.rewards.xp.forEach { (stat, amount) -> player.statAdvance(stat, amount.toDouble()) }
}

public fun ProtectedAccess.showCompletionScroll(
    quest: Quest,
    rewards: List<String>,
    itemModel: ObjType? = null,
    questPoints: Int = 1,
) {
    ifOpenMainModal(interfaces.quest_scroll)
    ifSetText(components.quest_scroll_title, "You have completed ${quest.name}!")
    ifSetText(components.quest_scroll_award, "You are awarded:")
    ifSetText(components.quest_scroll_points, questPoints.toString())
    if (itemModel != null) {
        ifSetObj(components.quest_scroll_model, itemModel, 1)
    }

    val rewardComponents =
        listOf(
            components.quest_scroll_reward1,
            components.quest_scroll_reward2,
            components.quest_scroll_reward3,
            components.quest_scroll_reward4,
            components.quest_scroll_reward5,
            components.quest_scroll_reward6,
            components.quest_scroll_reward7,
        )

    for (i in rewardComponents.indices) {
        val component = rewardComponents[i]
        if (i < rewards.size) {
            ifSetText(component, rewards[i])
            ifSetHide(component, false)
        } else {
            ifSetHide(component, true)
        }
    }
}
