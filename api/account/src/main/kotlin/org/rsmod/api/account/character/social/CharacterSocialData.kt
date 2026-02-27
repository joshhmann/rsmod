package org.rsmod.api.account.character.social

import org.rsmod.api.account.character.CharacterDataStage

public class CharacterSocialData(
    public val friends: List<Friend>,
    public val ignores: List<Ignore>,
) : CharacterDataStage.Segment {
    public data class Friend(val name: String, val characterId: Int? = null)

    public data class Ignore(val name: String, val characterId: Int? = null)
}
