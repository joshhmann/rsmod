package org.rsmod.content.mechanics.ranged.configs

import org.rsmod.api.type.refs.obj.ObjReferences

/**
 * Dwarf Multicannon object references.
 *
 * The cannon consists of four parts that can be combined into a "set" item:
 * - Base (mcannonbase_loc)
 * - Stand (mcannonstand_loc)
 * - Barrels (mcannonbarrels_loc)
 * - Furnace (mcannonfurnace_loc)
 *
 * And the cannonball item: mcannonball (id 2)
 */
object CannonObjs : ObjReferences() {
    /** Complete cannon set (id 12863) */
    val set_cannon = find("set_cannon")

    /** Cannonball item (id 2) */
    val cannonball = find("mcannonball")

    /** Tool kit for repairing cannon (id 1) */
    val tool_kit = find("mcannontoolkit")
}
