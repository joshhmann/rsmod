package org.rsmod.content.other.agentbridge

import org.rsmod.api.type.refs.timer.TimerReferences

internal typealias agent_timers = AgentBridgeTimers

internal object AgentBridgeTimers : TimerReferences() {
    val agent_bridge = find("agent_bridge")
}
