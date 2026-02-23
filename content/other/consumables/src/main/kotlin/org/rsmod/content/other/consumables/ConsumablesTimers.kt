package org.rsmod.content.other.consumables

import org.rsmod.api.type.refs.timer.TimerReferences

/** Timer references for consumables module. */
internal typealias consumables_timers = ConsumablesTimers

internal object ConsumablesTimers : TimerReferences() {
    /** Food eating delay timer. Prevents eating multiple foods too quickly. */
    val food_delay = find("food_delay")

    /** Potion drinking delay timer. Prevents drinking multiple potions too quickly. */
    val potion_delay = find("potion_delay")
}
