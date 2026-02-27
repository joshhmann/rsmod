package org.rsmod.content.areas.dungeons.draynorsewer

import org.rsmod.plugin.module.PluginModule

class DraynorSewerModule : PluginModule() {
    override fun bind() {
        bindInstance<DraynorSewerScript>()
    }
}
