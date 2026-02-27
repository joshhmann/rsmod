package org.rsmod.api.account.character.social

import jakarta.inject.Inject
import org.rsmod.api.account.character.CharacterDataStage
import org.rsmod.game.entity.Player

public class CharacterSocialApplier @Inject constructor() :
    CharacterDataStage.Applier<CharacterSocialData> {

    override fun apply(player: Player, data: CharacterSocialData) {
        // Convert loaded data to mutable lists and attach to player via repository
        val friends = data.friends.map { SocialEntry(it.name, it.characterId) }.toMutableList()
        val ignores = data.ignores.map { SocialEntry(it.name, it.characterId) }.toMutableList()
        SocialRepository.set(player.userId, PlayerSocialData(friends, ignores))
    }
}

public data class SocialEntry(val name: String, val characterId: Int? = null)

public data class PlayerSocialData(
    val friends: MutableList<SocialEntry>,
    val ignores: MutableList<SocialEntry>,
)

// Social data storage repository (in-memory with persistence via pipeline)
public object SocialRepository {
    private val socialData = mutableMapOf<Long, PlayerSocialData>()

    public fun get(playerId: Long): PlayerSocialData {
        return socialData.getOrPut(playerId) { PlayerSocialData(mutableListOf(), mutableListOf()) }
    }

    public fun set(playerId: Long, data: PlayerSocialData) {
        socialData[playerId] = data
    }

    public fun remove(playerId: Long) {
        socialData.remove(playerId)
    }
}

// Extension property using repository
public var Player.socialData: PlayerSocialData
    get() = SocialRepository.get(this.userId)
    set(value) = SocialRepository.set(this.userId, value)

// Helper functions for Player
public fun Player.isFriend(name: String): Boolean =
    socialData.friends.any { it.name.equals(name, ignoreCase = true) }

public fun Player.isIgnored(name: String): Boolean =
    socialData.ignores.any { it.name.equals(name, ignoreCase = true) }

public fun Player.addFriend(name: String, characterId: Int? = null): Boolean {
    if (isFriend(name)) return false
    socialData.friends.add(SocialEntry(name, characterId))
    return true
}

public fun Player.removeFriend(name: String): Boolean {
    return socialData.friends.removeIf { it.name.equals(name, ignoreCase = true) }
}

public fun Player.addIgnore(name: String, characterId: Int? = null): Boolean {
    if (isIgnored(name)) return false
    socialData.ignores.add(SocialEntry(name, characterId))
    return true
}

public fun Player.removeIgnore(name: String): Boolean {
    return socialData.ignores.removeIf { it.name.equals(name, ignoreCase = true) }
}
