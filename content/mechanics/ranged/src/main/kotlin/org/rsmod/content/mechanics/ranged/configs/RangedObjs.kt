package org.rsmod.content.mechanics.ranged.configs

import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.proj.ProjAnimReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.api.type.refs.spot.SpotanimReferences

/**
 * Animation, projectile, and spot-anim references for ranged combat.
 *
 * These may overlap with BaseSeqs/BaseProjAnims/BaseSpotanims — local declarations
 * are safe because `find("name")` resolves at build time regardless.
 */
internal object RangedSeqs : SeqReferences() {
    val human_bow = find("human_bow")
    val human_crossbow = find("human_crossbow")
    val human_throw = find("human_throw")
}

internal object RangedProjAnims : ProjAnimReferences() {
    val arrow = find("arrow")
    val bolt = find("bolt")
    val thrown = find("thrown")
}

internal object RangedSpotAnims : SpotanimReferences() {
    val bronze_arrow_travel = find("bronze_arrow_travel")
    val iron_arrow_travel = find("iron_arrow_travel")
    val steel_arrow_travel = find("steel_arrow_travel")
    val mithril_arrow_travel = find("mithril_arrow_travel")
    val adamant_arrow_travel = find("adamant_arrow_travel")
    val rune_arrow_travel = find("rune_arrow_travel")

    val bronze_arrow_launch = find("bronze_arrow_launch")
    val iron_arrow_launch = find("iron_arrow_launch")
    val steel_arrow_launch = find("steel_arrow_launch")
    val mithril_arrow_launch = find("mithril_arrow_launch")
    val adamant_arrow_launch = find("adamant_arrow_launch")
    val rune_arrow_launch = find("rune_arrow_launch")

    val crossbowbolt_travel = find("crossbowbolt_travel")
}

/**
 * Configures all F2P bows with combat-critical params.
 *
 * The cache provides base item data (weaponCategory, name, etc.) but the following
 * combat-specific params must be set here so the combat pipeline can:
 * - Play the correct attack animation (`attack_anim_stance1`)
 * - Launch the correct projectile (`proj_type`)
 * - Use the correct attack range (`attack_range`)
 * - Enforce level requirements (`levelrequire`)
 */
internal object BowObjs : ObjEditor() {
    init {
        // ---- T1: Normal bows (lvl 1) ----
        edit(objs.shortbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 4
            param[params.levelrequire] = 1
            param[params.attackrate] = 4
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 8
            param[params.ranged_strength] = 8
        }

        edit(objs.longbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 6
            param[params.levelrequire] = 1
            param[params.attackrate] = 6
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 10
            param[params.ranged_strength] = 10
        }

        // ---- T2: Oak bows (lvl 20-25) ----
        edit(objs.oak_shortbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 4
            param[params.levelrequire] = 20
            param[params.attackrate] = 4
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 20
            param[params.ranged_strength] = 14
        }

        edit(objs.oak_longbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 6
            param[params.levelrequire] = 25
            param[params.attackrate] = 6
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 25
            param[params.ranged_strength] = 16
        }

        // ---- T3: Willow bows (lvl 40-45) ----
        edit(objs.willow_shortbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 4
            param[params.levelrequire] = 40
            param[params.attackrate] = 4
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 30
            param[params.ranged_strength] = 20
        }

        edit(objs.willow_longbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 6
            param[params.levelrequire] = 45
            param[params.attackrate] = 6
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 35
            param[params.ranged_strength] = 22
        }

        // ---- T4: Maple bows (lvl 50-55) ----
        edit(objs.maple_shortbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 4
            param[params.levelrequire] = 50
            param[params.attackrate] = 4
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 41
            param[params.ranged_strength] = 29
        }

        edit(objs.maple_longbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 6
            param[params.levelrequire] = 55
            param[params.attackrate] = 6
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 45
            param[params.ranged_strength] = 31
        }

        // ---- T5: Yew bows (lvl 65-70) ----
        edit(objs.yew_shortbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 4
            param[params.levelrequire] = 65
            param[params.attackrate] = 4
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 47
            param[params.ranged_strength] = 35
        }

        edit(objs.yew_longbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 6
            param[params.levelrequire] = 70
            param[params.attackrate] = 6
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 50
            param[params.ranged_strength] = 37
        }

        // ---- T6: Magic bows (lvl 75-80, members) ----
        edit(objs.magic_shortbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 4
            param[params.levelrequire] = 75
            param[params.attackrate] = 4
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 55
            param[params.ranged_strength] = 39
        }

        edit(objs.magic_longbow) {
            param[params.attack_anim_stance1] = RangedSeqs.human_bow
            param[params.attackrange] = 6
            param[params.levelrequire] = 75
            param[params.attackrate] = 6
            param[params.proj_type] = RangedProjAnims.arrow
            param[params.required_ammo] = categories.arrows
            param[params.attack_ranged] = 55
            param[params.ranged_strength] = 39
        }
    }
}

/**
 * Configures F2P arrows with projectile params.
 *
 * Arrows need `proj_travel` and `proj_launch` spotanims so the combat pipeline can
 * render the arrow in flight. Level requirements are set here so lower-level bows
 * cannot equip higher-tier arrows (the combat system checks `params.levelrequire`).
 */
internal object ArrowObjs : ObjEditor() {
    init {
        edit(objs.bronze_arrow) {
            param[params.proj_travel] = RangedSpotAnims.bronze_arrow_travel
            param[params.proj_launch] = RangedSpotAnims.bronze_arrow_launch
            param[params.levelrequire] = 1
        }

        edit(objs.iron_arrow) {
            param[params.proj_travel] = RangedSpotAnims.iron_arrow_travel
            param[params.proj_launch] = RangedSpotAnims.iron_arrow_launch
            param[params.levelrequire] = 15
        }

        edit(objs.steel_arrow) {
            param[params.proj_travel] = RangedSpotAnims.steel_arrow_travel
            param[params.proj_launch] = RangedSpotAnims.steel_arrow_launch
            param[params.levelrequire] = 30
        }

        edit(objs.mithril_arrow) {
            param[params.proj_travel] = RangedSpotAnims.mithril_arrow_travel
            param[params.proj_launch] = RangedSpotAnims.mithril_arrow_launch
            param[params.levelrequire] = 45
        }

        edit(objs.adamant_arrow) {
            param[params.proj_travel] = RangedSpotAnims.adamant_arrow_travel
            param[params.proj_launch] = RangedSpotAnims.adamant_arrow_launch
            param[params.levelrequire] = 60
        }

        edit(objs.rune_arrow) {
            param[params.proj_travel] = RangedSpotAnims.rune_arrow_travel
            param[params.proj_launch] = RangedSpotAnims.rune_arrow_launch
            param[params.levelrequire] = 75
        }
    }
}
