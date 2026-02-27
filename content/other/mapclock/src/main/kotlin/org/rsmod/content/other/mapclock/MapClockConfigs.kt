package org.rsmod.content.other.mapclock

import org.rsmod.api.type.refs.timer.TimerReferences
import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias clock_timers = MapClockTimers

typealias clock_varbits = MapClockVarBits

object MapClockTimers : TimerReferences() {
    val playtime_clock = find("playtime_clock")
}

object MapClockVarBits : VarBitReferences() {
    val date_ms_past_minute = find("date_milliseconds_past_minute")
    val date_secs_past_minute = find("date_seconds_past_minute")
}
