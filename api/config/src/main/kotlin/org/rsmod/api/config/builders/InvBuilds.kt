package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.inv.InvBuilder
import org.rsmod.game.type.inv.InvScope

internal object InvBuilds : InvBuilder() {
    init {
        build("tradeoffer") {
            scope = InvScope.Perm
            protect = false
            size = 28
        }
    }
}
