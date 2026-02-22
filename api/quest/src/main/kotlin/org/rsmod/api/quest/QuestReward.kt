package org.rsmod.api.quest

import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.stat.StatType

public data class QuestReward(
    val xp: Map<StatType, Int> = emptyMap(),
    val items: List<Pair<ObjType, Int>> = emptyList(),
    val extraText: List<String> = emptyList(),
)

@DslMarker public annotation class QuestJournalDsl

@QuestJournalDsl
public class QuestRewardBuilder {
    private val _xp = mutableMapOf<StatType, Int>()
    private val _items = mutableListOf<Pair<ObjType, Int>>()
    private val _extraText = mutableListOf<String>()

    public fun xp(stat: StatType, amount: Int) {
        _xp[stat] = amount
    }

    public fun item(type: ObjType, amount: Int = 1) {
        _items.add(type to amount)
    }

    public fun extra(text: String) {
        _extraText.add(text)
    }

    public fun build(): QuestReward = QuestReward(_xp, _items, _extraText)
}

public fun questRewards(init: QuestRewardBuilder.() -> Unit): QuestReward {
    val builder = QuestRewardBuilder()
    builder.init()
    return builder.build()
}
