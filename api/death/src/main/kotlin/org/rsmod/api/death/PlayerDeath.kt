package org.rsmod.api.death

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.jingles
import org.rsmod.api.config.refs.midis
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.varps
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.player.deathResetTimers
import org.rsmod.api.player.disablePrayers
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.map.CoordGrid

@Singleton
public class PlayerDeath
@Inject
constructor(
    private val statTypes: StatTypeList,
    private val deathItems: PlayerDeathItems,
    private val objRepo: ObjRepository,
    private val players: PlayerList,
) {
    private var Player.specialAttackType by intVarp(varps.sa_attack)

    public suspend fun death(access: ProtectedAccess) {
        val killer = access.player.findHero(players)
        val items = deathItems.calculateDeathItems(access.player)
        access.deathSequence(killer, items)
    }

    private suspend fun ProtectedAccess.deathSequence(
        killer: Player?,
        items: PlayerDeathItems.DeathItems,
    ) {
        val respawn = CoordGrid(0, 50, 50, 21, 18)
        val randomRespawn = mapFindSquareLineOfWalk(respawn, minRadius = 0, maxRadius = 2)
        stopAction()
        delay(2)
        anim(seqs.human_death)
        delay(4)

        spawnDeathDrops(killer, items.lost)
        resolveKeptItems(items.kept)

        combatClearQueue()
        clearQueue(queues.death)
        midiSong(midis.stop_music)
        midiJingle(jingles.death_jingle_2)
        mes("Oh dear, you are dead!")
        telejump(randomRespawn ?: respawn)
        resetAnim()

        resetPlayerState(statTypes)
        restoreToplevelTabs(
            components.toplevel_osrs_stretch_pvp_icons,
            components.toplevel_osrs_stretch_side1,
            components.toplevel_osrs_stretch_side2,
            components.toplevel_osrs_stretch_side4,
            components.toplevel_osrs_stretch_side5,
            components.toplevel_osrs_stretch_side6,
            components.toplevel_osrs_stretch_side9,
            components.toplevel_osrs_stretch_side8,
            components.toplevel_osrs_stretch_side7,
            components.toplevel_osrs_stretch_side10,
            components.toplevel_osrs_stretch_side11,
            components.toplevel_osrs_stretch_side12,
            components.toplevel_osrs_stretch_side13,
        )
    }

    private fun ProtectedAccess.spawnDeathDrops(killer: Player?, lost: List<InvObj>) {
        val duration = killer?.lootDropDuration ?: constants.lootdrop_duration
        val receiver = killer ?: player
        for (item in lost) {
            objRepo.add(item, player.coords, duration, receiver)
        }
    }

    private fun ProtectedAccess.resolveKeptItems(kept: List<InvObj>) {
        invClear(inv)
        invClear(worn)
        for (item in kept) {
            invAddOrDrop(objRepo, objTypes[item.id]!!, item.count, inv = inv)
        }
    }

    private fun ProtectedAccess.resetPlayerState(stats: StatTypeList) {
        player.disablePrayers()
        player.deathResetTimers()

        player.specialAttackType = 0
        player.skullIcon = null
        player.clearHeroPoints()

        rebuildAppearance()

        camReset()
        statRestoreAll(stats.values)
        minimapReset()
    }
}
