package org.rsmod.content.skills.agility.configs

import org.rsmod.api.type.refs.loc.LocReferences

/**
 * Agility obstacle location references for Gnome Stronghold Course.
 *
 * These must match rev-233 cache symbol names exactly.
 */
object AgilityLocs : LocReferences() {
    // Gnome Stronghold Course obstacles
    val log_balance = find("gnome_log_balance1")
    val obstacle_net = find("obstical_net2")
    val tree_branch_up = find("climbing_branch")
    val balancing_rope = find("balancing_rope")
    val tree_branch_down = find("climbing_tree")
    val obstacle_net_2 = find("obstical_net3")
    val obstacle_pipe = find("obstical_pipe1")
    val obstacle_pipe_exit = find("obstical_pipe2")
}
