package org.rsmod.content.interfaces.social

import jakarta.inject.Inject
import net.rsprot.protocol.game.outgoing.social.UpdateFriendList
import net.rsprot.protocol.game.outgoing.social.UpdateIgnoreList
import org.rsmod.api.account.character.social.addFriend
import org.rsmod.api.account.character.social.addIgnore
import org.rsmod.api.account.character.social.isFriend
import org.rsmod.api.account.character.social.isIgnored
import org.rsmod.api.account.character.social.removeFriend
import org.rsmod.api.account.character.social.removeIgnore
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.output.mes
import org.rsmod.api.script.onIfOpen
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class SocialListScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        // Send friend/ignore lists when interface opens
        onIfOpen(interfaces.friends) { player.sendSocialLists() }
    }

    private fun Player.sendSocialLists() {
        sendFriendList()
        sendIgnoreList()
    }

    private fun Player.sendFriendList() {
        val friends = mutableListOf<UpdateFriendList.Friend>()

        // Always add at least one entry to indicate list is loaded
        // In a real implementation, iterate through friends and check online status
        friends.add(
            UpdateFriendList.OfflineFriend(
                added = false,
                name = "",
                previousName = null,
                rank = 0,
                properties = 0,
                notes = "",
            )
        )

        client.write(UpdateFriendList(friends))
    }

    private fun Player.sendIgnoreList() {
        val ignores = mutableListOf<UpdateIgnoreList.IgnoredPlayer>()

        // Always add at least one entry to indicate list is loaded
        ignores.add(UpdateIgnoreList.RemovedIgnoredEntry(name = ""))

        client.write(UpdateIgnoreList(ignores))
    }
}

// Extension functions for friend/ignore management
public fun Player.handleAddFriend(name: String) {
    if (name.isBlank()) {
        mes("Please enter a valid player name.")
        return
    }
    if (isFriend(name)) {
        mes("$name is already on your friend list.")
        return
    }
    if (addFriend(name)) {
        mes("Added $name to your friend list.")
        // TODO: Send update to client
    }
}

public fun Player.handleRemoveFriend(name: String) {
    if (removeFriend(name)) {
        mes("Removed $name from your friend list.")
        // TODO: Send update to client
    } else {
        mes("$name is not on your friend list.")
    }
}

public fun Player.handleAddIgnore(name: String) {
    if (name.isBlank()) {
        mes("Please enter a valid player name.")
        return
    }
    if (isIgnored(name)) {
        mes("$name is already on your ignore list.")
        return
    }
    if (addIgnore(name)) {
        mes("Added $name to your ignore list.")
        // TODO: Send update to client
    }
}

public fun Player.handleRemoveIgnore(name: String) {
    if (removeIgnore(name)) {
        mes("Removed $name from your ignore list.")
        // TODO: Send update to client
    } else {
        mes("$name is not on your ignore list.")
    }
}
