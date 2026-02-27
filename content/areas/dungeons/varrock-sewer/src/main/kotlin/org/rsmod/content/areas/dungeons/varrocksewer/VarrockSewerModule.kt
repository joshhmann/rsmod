package org.rsmod.content.areas.dungeons.varrocksewer

import org.rsmod.plugin.module.PluginModule

class VarrockSewerModule : PluginModule() {
    override fun bind() {
        bindInstance<VarrockSewerScript>()
    }
}
