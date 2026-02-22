package org.rsmod.content.skills.mining.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType

/**
 * Ore product obj type references — items produced when mining each rock.
 *
 * Cache sym names sourced from obj.sym.
 */
internal object MiningOreObjs : ObjReferences() {
    val clay = find("clay")
    val copper_ore = find("copper_ore")
    val tin_ore = find("tin_ore")
    val iron_ore = find("iron_ore")
    val silver_ore = find("silver_ore")
    val gold_ore = find("gold_ore")
    val coal = find("coal")
    val mithril_ore = find("mithril_ore")
    val adamantite_ore = find("adamantite_ore")
    val runite_ore = find("runite_ore")
}

/**
 * Ore rock and depleted rock loc references.
 *
 * Cache sym names sourced from loc.sym (Kronos IDs used as cross-reference):
 * - copperrock1 / copperrock2 → copper ore (id 11161 / 10943)
 * - tinrock1 / tinrock2 → tin ore (id 11360 / 11361)
 * - clayrock1 / clayrock2 → clay (id 11362 / 11363)
 * - ironrock1 / ironrock2 → iron ore (id 11364 / 11365)
 * - coalrock1 / coalrock2 → coal (id 11366 / 11367)
 * - silverrock1 / silverrock2 → silver ore (id 11368 / 11369)
 * - goldrock1 / goldrock2 → gold ore (id 11370 / 11371)
 * - mithrilrock1 / mithrilrock2 → mithril ore (id 11372 / 11373)
 * - adamantiterock1 / adamantiterock2 → adamantite ore (id 11374 / 11375)
 * - runiterock1 / runiterock2 → runite ore (id 11376 / 11377)
 * - gemrock1 / gemrock → gem rock (id 11380 / 11381)
 * - rocks1 / rocks2 → depleted rock (id 11390 / 11391)
 */
internal object MiningRockLocs : LocReferences() {
    // Copper
    val copperrock1 = find("copperrock1") // loc id 10943
    val copperrock2 = find("copperrock2") // loc id 11161

    // Tin
    val tinrock1 = find("tinrock1")
    val tinrock2 = find("tinrock2")

    // Clay
    val clayrock1 = find("clayrock1")
    val clayrock2 = find("clayrock2")

    // Iron
    val ironrock1 = find("ironrock1")
    val ironrock2 = find("ironrock2")

    // Coal
    val coalrock1 = find("coalrock1")
    val coalrock2 = find("coalrock2")

    // Silver
    val silverrock1 = find("silverrock1")
    val silverrock2 = find("silverrock2")

    // Gold
    val goldrock1 = find("goldrock1")
    val goldrock2 = find("goldrock2")

    // Mithril
    val mithrilrock1 = find("mithrilrock1")
    val mithrilrock2 = find("mithrilrock2")

    // Adamantite
    val adamantiterock1 = find("adamantiterock1")
    val adamantiterock2 = find("adamantiterock2")

    // Runite
    val runiterock1 = find("runiterock1")
    val runiterock2 = find("runiterock2")

    // Depleted rocks (shared by all ore types — the rock becomes this after depletion)
    val rocks1 = find("rocks1") // depleted variant 1
    val rocks2 = find("rocks2") // depleted variant 2
}

/**
 * Applies [content.ore] content group and all required skill parameters to each ore rock.
 *
 * Parameters set per rock:
 * - [params.levelrequire] — Mining level required.
 * - [params.skill_xp] — XP granted per ore, encoded as fine XP via [PlayerStatMap.toFineXP].
 * - [params.skill_productitem] — The ore item given to the player.
 * - [params.next_loc_stage] — The depleted rock loc this rock transforms into.
 * - [params.deplete_chance] — Chance (0–255) to deplete the rock on each successful mine.
 * - [params.respawn_time_low] — Minimum respawn time in ticks (used when fixed respawn is 0).
 * - [params.respawn_time_high] — Maximum respawn time in ticks.
 *
 * XP values are wiki-accurate for OSRS (current revision): clay 5, copper/tin 17.5, iron 35, silver
 * 40, coal 50, gold 65, mithril 80, adamantite 95, runite 125.
 *
 * Depletion chances (0–255) approximate OSRS rates. Higher = more frequent depletion. Based on
 * Kronos depleteChance fractions converted to 0–255 scale: clay 1/5 ≈ 51, copper/tin 1/8 ≈ 32, iron
 * 1/4 ≈ 64, silver 1/4 ≈ 64, coal 1/9 ≈ 28, gold 1/5 ≈ 51, mithril 1/5 ≈ 51, adamant 1/4 ≈ 64, rune
 * 2/5 ≈ 102.
 *
 * Respawn times are approximate OSRS values in game ticks (600 ms each): clay/copper/tin ≈ 4–6
 * ticks, iron ≈ 6–9 ticks, silver ≈ 15–20 ticks, coal ≈ 48–55 ticks, gold ≈ 48–55 ticks, mithril ≈
 * 150–175 ticks, adamant ≈ 250–300 ticks, runite ≈ 480–560 ticks.
 */
internal object MiningRocks : LocEditor() {
    init {
        copper(MiningRockLocs.copperrock1, MiningRockLocs.rocks1)
        copper(MiningRockLocs.copperrock2, MiningRockLocs.rocks2)

        tin(MiningRockLocs.tinrock1, MiningRockLocs.rocks1)
        tin(MiningRockLocs.tinrock2, MiningRockLocs.rocks2)

        clay(MiningRockLocs.clayrock1, MiningRockLocs.rocks1)
        clay(MiningRockLocs.clayrock2, MiningRockLocs.rocks2)

        iron(MiningRockLocs.ironrock1, MiningRockLocs.rocks1)
        iron(MiningRockLocs.ironrock2, MiningRockLocs.rocks2)

        coal(MiningRockLocs.coalrock1, MiningRockLocs.rocks2)
        coal(MiningRockLocs.coalrock2, MiningRockLocs.rocks1)

        silver(MiningRockLocs.silverrock1, MiningRockLocs.rocks1)
        silver(MiningRockLocs.silverrock2, MiningRockLocs.rocks2)

        gold(MiningRockLocs.goldrock1, MiningRockLocs.rocks1)
        gold(MiningRockLocs.goldrock2, MiningRockLocs.rocks2)

        mithril(MiningRockLocs.mithrilrock1, MiningRockLocs.rocks1)
        mithril(MiningRockLocs.mithrilrock2, MiningRockLocs.rocks2)

        adamantite(MiningRockLocs.adamantiterock1, MiningRockLocs.rocks1)
        adamantite(MiningRockLocs.adamantiterock2, MiningRockLocs.rocks2)

        runite(MiningRockLocs.runiterock1, MiningRockLocs.rocks1)
        runite(MiningRockLocs.runiterock2, MiningRockLocs.rocks2)
    }

    private fun ore(
        type: LocType,
        depleted: LocType,
        ore: ObjType,
        levelReq: Int,
        xp: Double,
        depleteChance: Int,
        respawnLow: Int,
        respawnHigh: Int,
    ) {
        edit(type) {
            contentGroup = content.ore
            param[params.levelrequire] = levelReq
            param[params.skill_xp] = PlayerStatMap.toFineXP(xp).toInt()
            param[params.skill_productitem] = ore
            param[params.next_loc_stage] = depleted
            param[params.deplete_chance] = depleteChance
            param[params.respawn_time] = 0
            param[params.respawn_time_low] = respawnLow
            param[params.respawn_time_high] = respawnHigh
        }
    }

    private fun clay(type: LocType, depleted: LocType) =
        ore(type, depleted, MiningOreObjs.clay, 1, 5.0, 51, 3, 6)

    private fun copper(type: LocType, depleted: LocType) =
        ore(type, depleted, MiningOreObjs.copper_ore, 1, 17.5, 32, 3, 6)

    private fun tin(type: LocType, depleted: LocType) =
        ore(type, depleted, MiningOreObjs.tin_ore, 1, 17.5, 32, 3, 6)

    private fun iron(type: LocType, depleted: LocType) =
        ore(type, depleted, MiningOreObjs.iron_ore, 15, 35.0, 64, 5, 9)

    private fun silver(type: LocType, depleted: LocType) =
        ore(type, depleted, MiningOreObjs.silver_ore, 20, 40.0, 64, 12, 20)

    private fun coal(type: LocType, depleted: LocType) =
        ore(type, depleted, MiningOreObjs.coal, 30, 50.0, 28, 45, 55)

    private fun gold(type: LocType, depleted: LocType) =
        ore(type, depleted, MiningOreObjs.gold_ore, 40, 65.0, 51, 45, 55)

    private fun mithril(type: LocType, depleted: LocType) =
        ore(type, depleted, MiningOreObjs.mithril_ore, 55, 80.0, 51, 140, 175)

    private fun adamantite(type: LocType, depleted: LocType) =
        ore(type, depleted, MiningOreObjs.adamantite_ore, 70, 95.0, 64, 240, 300)

    private fun runite(type: LocType, depleted: LocType) =
        ore(type, depleted, MiningOreObjs.runite_ore, 85, 125.0, 102, 480, 560)
}
