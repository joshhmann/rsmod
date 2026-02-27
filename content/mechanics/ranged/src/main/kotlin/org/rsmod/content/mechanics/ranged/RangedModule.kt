package org.rsmod.content.mechanics.ranged

import org.rsmod.content.mechanics.ranged.scripts.DwarfMulticannonScript
import org.rsmod.plugin.module.PluginModule

class RangedModule : PluginModule() {
    override fun bind() {
        bindInstance<DwarfMulticannonScript>()
    }
}
