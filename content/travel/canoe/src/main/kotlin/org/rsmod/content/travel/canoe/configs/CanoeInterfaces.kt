package org.rsmod.content.travel.canoe.configs

import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias canoe_interfaces = CanoeInterfaces

object CanoeInterfaces : InterfaceReferences() {
    val shaping = find("canoeing")
    val destination = find("canoe_map")
}
