package org.rsmod.api.quest

public enum class QuestProgressState(public val varp: Int) {
    NotStarted(0),
    InProgress(1),
    Finished(2);

    public companion object {
        public fun fromVarp(value: Int): QuestProgressState =
            entries.find { it.varp == value } ?: NotStarted
    }
}
