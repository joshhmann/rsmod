package org.rsmod.content.skills.agility

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Agility skill implementation for RSMod v2.
 *
 * Implements the Gnome Stronghold Agility Course with 7 obstacles + completion bonus. Players must
 * complete obstacles in order. XP is granted per obstacle.
 *
 * Course layout:
 * 1. Log Balance (10 XP)
 * 2. Obstacle Net (10 XP)
 * 3. Tree Branch Up (6.5 XP)
 * 4. Balancing Rope (10 XP)
 * 5. Tree Branch Down (6.5 XP)
 * 6. Obstacle Net (10 XP)
 * 7. Obstacle Pipe (7.5 XP)
 * 8. Completion Bonus (50 XP) Total per lap: 110.5 XP
 */
class Agility @Inject constructor(private val protectedAccess: ProtectedAccessLauncher) :
    PluginScript() {
    override fun ScriptContext.startup() {
        // TODO: Implement obstacle interactions
        // Obstacles need proper LocReferences in configs/AgilityLocs.kt
        // Animation references need to be added to configs/AgilitySeqs.kt
    }
}
