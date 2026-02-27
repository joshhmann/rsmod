package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.obj.ObjBuilder
import org.rsmod.game.type.obj.Dummyitem

internal object ObjBuilds : ObjBuilder() {
    init {
        build("template_for_transform") { dummyitem = Dummyitem.GraphicOnly }

        // Custom server-side items — sym entries in .local/obj.sym (IDs 31200-31201).
        build("corrupted_platebody") { dummyitem = Dummyitem.GraphicOnly }
        build("beacon_ring") { dummyitem = Dummyitem.GraphicOnly }
    }
}
