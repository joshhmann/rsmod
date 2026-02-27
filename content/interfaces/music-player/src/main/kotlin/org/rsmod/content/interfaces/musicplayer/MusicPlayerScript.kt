package org.rsmod.content.interfaces.musicplayer

import jakarta.inject.Inject
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.music.MusicRepository
import org.rsmod.api.player.music.MusicPlayer
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.script.onIfOpen
import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Music Player Interface Script for F2P.
 *
 * Handles the music player tab interface interactions. The actual music track unlocking is handled
 * automatically by the core music system when players enter areas (see
 * [org.rsmod.api.music.plugin.scripts.MusicAreaScript]).
 */
public class MusicPlayerScript
@Inject
constructor(private val musicPlayer: MusicPlayer, private val musicRepo: MusicRepository) :
    PluginScript() {

    override fun ScriptContext.startup() {
        // Handle music interface open - set up the track list events
        onIfOpen(interfaces.music) { player.onMusicTabOpen() }
    }

    private fun Player.onMusicTabOpen() {
        // Set up events for the track list (0-500 covers all possible tracks)
        ifSetEvents(music_components.track_list, 0..500, IfEvent.Op1)

        // Update the track count display
        updateTrackCount()
    }

    private fun Player.updateTrackCount() {
        val allMusic = musicRepo.getAll()
        val total = allMusic.count { !it.hidden }
        val unlocked = allMusic.count { !it.hidden && hasUnlocked(it.id) }

        // The interface should show something like "Unlocked: X / Y"
        // This is typically handled by the client based on varp values
        // For now, just log the count (could be sent to interface via vars)
    }

    private fun Player.hasUnlocked(musicId: Int): Boolean {
        val music = musicRepo.forId(musicId) ?: return false
        return if (music.canUnlock) {
            val varp = music.unlockVarp
            if (varp != null) {
                val varpValue = vars[varp]
                val bitflag = music.unlockBitflag
                (varpValue.toInt() and bitflag) != 0
            } else {
                false
            }
        } else {
            !music.hidden
        }
    }
}

/**
 * Music interface component references. These would normally be in a separate config file, but are
 * defined here for simplicity since the music interface is cache-defined.
 */
internal object music_components : ComponentReferences() {
    // Main scrollable track list component (music:scrollable)
    val track_list: ComponentType = find("music:scrollable")
}
