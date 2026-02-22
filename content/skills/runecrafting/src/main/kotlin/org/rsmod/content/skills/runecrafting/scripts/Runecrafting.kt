package org.rsmod.content.skills.runecrafting.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.hat
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.runecraftingLvl
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.quest.QuestList
import org.rsmod.api.quest.isQuestComplete
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLocU
import org.rsmod.content.skills.runecrafting.scripts.configs.runecrafting_locs
import org.rsmod.content.skills.runecrafting.scripts.configs.runecrafting_objs
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.type.obj.ObjType
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Runecrafting @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(runecrafting_locs.runetemple_ruined) { enterViaWornTiara(it.loc) }
        onOpLoc1(runecrafting_locs.runetemple) { exitRuins() }

        for (altar in F2P_ALTARS) {
            onOpLocU(runecrafting_locs.runetemple_ruined, altar.talisman) {
                enterViaItem(it.loc, altar)
            }
            onOpLocU(runecrafting_locs.runetemple_ruined, altar.tiara) {
                enterViaItem(it.loc, altar)
            }
        }

        onOpLocU(runecrafting_locs.runetemple_altar_new, runecrafting_objs.blankrune) {
            craftRunes(it.loc)
        }
        onOpLocU(runecrafting_locs.runetemple_altar_old, runecrafting_objs.blankrune) {
            craftRunes(it.loc)
        }
    }

    private suspend fun ProtectedAccess.enterViaItem(loc: BoundLocInfo, itemAltar: F2pAltar) {
        if (!isQuestComplete(QuestList.rune_mysteries)) {
            mes("You need to complete Rune Mysteries before you can enter.")
            return
        }
        val ruinsAltar = resolveByRuins(loc.coords) ?: itemAltar
        if (ruinsAltar != itemAltar) {
            mes("The talisman has no effect on these ruins.")
            return
        }
        teleport(ruinsAltar.interiorSpawn)
        mes("You enter the ${ruinsAltar.name} Altar.")
    }

    private suspend fun ProtectedAccess.enterViaWornTiara(loc: BoundLocInfo) {
        if (!isQuestComplete(QuestList.rune_mysteries)) {
            mes("You need to complete Rune Mysteries before you can enter.")
            return
        }
        val altar = resolveByRuins(loc.coords)
        if (altar == null) {
            mes("Nothing interesting happens.")
            return
        }
        if (player.hat?.id != altar.tiara.id) {
            mes("A matching talisman or tiara is required to enter these ruins.")
            return
        }
        teleport(altar.interiorSpawn)
        mes("You enter the ${altar.name} Altar.")
    }

    private suspend fun ProtectedAccess.exitRuins() {
        val altar = resolveByInterior(coords)
        if (altar == null) {
            mes("Nothing interesting happens.")
            return
        }
        teleport(altar.exteriorExit)
    }

    private suspend fun ProtectedAccess.craftRunes(loc: BoundLocInfo) {
        if (!isQuestComplete(QuestList.rune_mysteries)) {
            mes("You do not know how to craft runes yet.")
            return
        }
        val altar = resolveByInterior(coords) ?: resolveByInterior(loc.coords)
        if (altar == null) {
            mes("You cannot work out what this altar produces.")
            return
        }
        if (player.runecraftingLvl < altar.levelReq) {
            mes("You need a Runecrafting level of ${altar.levelReq} to craft these runes.")
            return
        }

        val essenceCount = countItem(runecrafting_objs.blankrune)
        if (essenceCount <= 0) {
            mes("You need rune essence to craft runes.")
            return
        }

        val removed = invDel(inv, runecrafting_objs.blankrune, count = essenceCount).success
        if (!removed) {
            mes("You need rune essence to craft runes.")
            return
        }

        val multiplier = 1 + (player.runecraftingLvl / altar.multiplierStep)
        val produced = essenceCount * multiplier
        invAdd(inv, altar.rune, count = produced)
        statAdvance(stats.runecrafting, essenceCount * altar.xpPerEssence)

        val runeName = altar.name.lowercase()
        mes("You bind the temple's power into $produced $runeName runes.")
    }

    private fun ProtectedAccess.countItem(obj: ObjType): Int {
        return inv.filterNotNull { it.id == obj.id }.sumOf { it.count }
    }

    private fun resolveByRuins(coords: CoordGrid): F2pAltar? {
        val match =
            F2P_ALTARS.minByOrNull { it.exteriorRuins.chebyshevDistance(coords) } ?: return null
        return match.takeIf { it.exteriorRuins.chebyshevDistance(coords) <= 10 }
    }

    private fun resolveByInterior(coords: CoordGrid): F2pAltar? {
        val match =
            F2P_ALTARS.minByOrNull { it.interiorSpawn.chebyshevDistance(coords) } ?: return null
        return match.takeIf { it.interiorSpawn.chebyshevDistance(coords) <= 40 }
    }

    private data class F2pAltar(
        val name: String,
        val levelReq: Int,
        val xpPerEssence: Double,
        val multiplierStep: Int,
        val rune: ObjType,
        val talisman: ObjType,
        val tiara: ObjType,
        val exteriorRuins: CoordGrid,
        val exteriorExit: CoordGrid,
        val interiorSpawn: CoordGrid,
    )

    private companion object {
        private val F2P_ALTARS =
            listOf(
                F2pAltar(
                    name = "Air",
                    levelReq = 1,
                    xpPerEssence = 5.0,
                    multiplierStep = 11,
                    rune = objs.air_rune,
                    talisman = runecrafting_objs.air_talisman,
                    tiara = runecrafting_objs.tiara_air,
                    exteriorRuins = CoordGrid(3127, 3405),
                    exteriorExit = CoordGrid(3129, 3407),
                    interiorSpawn = CoordGrid(2841, 4829),
                ),
                F2pAltar(
                    name = "Mind",
                    levelReq = 1,
                    xpPerEssence = 5.5,
                    multiplierStep = 14,
                    rune = objs.mind_rune,
                    talisman = runecrafting_objs.mind_talisman,
                    tiara = runecrafting_objs.tiara_mind,
                    exteriorRuins = CoordGrid(2980, 3514),
                    exteriorExit = CoordGrid(2982, 3516),
                    interiorSpawn = CoordGrid(2793, 4828),
                ),
                F2pAltar(
                    name = "Water",
                    levelReq = 5,
                    xpPerEssence = 6.0,
                    multiplierStep = 19,
                    rune = objs.water_rune,
                    talisman = runecrafting_objs.water_talisman,
                    tiara = runecrafting_objs.tiara_water,
                    exteriorRuins = CoordGrid(3184, 3165),
                    exteriorExit = CoordGrid(3186, 3167),
                    interiorSpawn = CoordGrid(2725, 4832),
                ),
                F2pAltar(
                    name = "Earth",
                    levelReq = 9,
                    xpPerEssence = 6.5,
                    multiplierStep = 26,
                    rune = objs.earth_rune,
                    talisman = runecrafting_objs.earth_talisman,
                    tiara = runecrafting_objs.tiara_earth,
                    exteriorRuins = CoordGrid(3305, 3474),
                    exteriorExit = CoordGrid(3307, 3476),
                    interiorSpawn = CoordGrid(2655, 4830),
                ),
                F2pAltar(
                    name = "Fire",
                    levelReq = 14,
                    xpPerEssence = 7.0,
                    multiplierStep = 35,
                    rune = objs.fire_rune,
                    talisman = runecrafting_objs.fire_talisman,
                    tiara = runecrafting_objs.tiara_fire,
                    exteriorRuins = CoordGrid(3312, 3253),
                    exteriorExit = CoordGrid(3314, 3255),
                    interiorSpawn = CoordGrid(2584, 4836),
                ),
            )
    }
}
