package org.rsmod.content.skills.agility.configs

import org.rsmod.api.type.refs.loc.LocReferences

/**
 * Agility obstacle location references for Gnome Stronghold Course. Source:
 * wiki-data/skills/agility.json (rev 228 verified)
 *
 * Course Layout: Start: Log Balance (23145) -> Obstacle Net (23134) -> Tree Branch Up (23559) ->
 * Balancing Rope (23135) -> Tree Branch Down (23560) -> Obstacle Net (23136) -> Obstacle Pipe
 * (23137/23138) -> Finish
 */
object AgilityLocs : LocReferences() {
    // Gnome Stronghold Course obstacles
    // TODO: These need proper symbol names from cache
    val log_balance = find("log_balance")
    val obstacle_net = find("obstacle_net")
    val tree_branch_up = find("tree_branch_up")
    val balancing_rope = find("balancing_rope")
    val tree_branch_down = find("tree_branch_down")
    val obstacle_net_2 = find("obstacle_net_2")
    val obstacle_pipe = find("obstacle_pipe")
    val obstacle_pipe_exit = find("obstacle_pipe_exit")
}
