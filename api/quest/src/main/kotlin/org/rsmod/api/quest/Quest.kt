package org.rsmod.api.quest

import org.rsmod.game.type.varp.VarpType

public data class Quest(
    val id: Int,
    val name: String,
    val varp: VarpType,
    val maxStage: Int,
    val rewards: QuestReward,
)
