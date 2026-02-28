@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.jingle.JingleReferences

typealias jingles = BaseJingles

object BaseJingles : JingleReferences() {
    // Rev233 canonical symbol name is `death_jingle_2`.
    val death_jingle = find("death_jingle_2")
    val death_jingle_2 = find("death_jingle_2")
}
