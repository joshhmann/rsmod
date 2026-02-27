package org.rsmod.api.account.character.social

import jakarta.inject.Inject
import org.rsmod.api.account.character.CharacterDataStage
import org.rsmod.api.account.character.CharacterMetadataList
import org.rsmod.api.db.DatabaseConnection
import org.rsmod.game.entity.Player

public class CharacterSocialPipeline
@Inject
constructor(private val applier: CharacterSocialApplier) : CharacterDataStage.Pipeline {

    override fun append(connection: DatabaseConnection, metadata: CharacterMetadataList) {
        val friends = selectFriends(connection, metadata.characterId)
        val ignores = selectIgnores(connection, metadata.characterId)
        metadata.add(applier, CharacterSocialData(friends, ignores))
    }

    private fun selectFriends(
        connection: DatabaseConnection,
        characterId: Int,
    ): List<CharacterSocialData.Friend> {
        val friends = mutableListOf<CharacterSocialData.Friend>()

        val select =
            connection.prepareStatement(
                """
                SELECT friend_name, friend_character_id
                FROM friends
                WHERE character_id = ?
            """
                    .trimIndent()
            )

        select.use {
            it.setInt(1, characterId)
            it.executeQuery().use { resultSet ->
                while (resultSet.next()) {
                    val name = resultSet.getString("friend_name")
                    val friendCharId = resultSet.getInt("friend_character_id")
                    friends +=
                        CharacterSocialData.Friend(
                            name = name,
                            characterId = if (resultSet.wasNull()) null else friendCharId,
                        )
                }
            }
        }

        return friends
    }

    private fun selectIgnores(
        connection: DatabaseConnection,
        characterId: Int,
    ): List<CharacterSocialData.Ignore> {
        val ignores = mutableListOf<CharacterSocialData.Ignore>()

        val select =
            connection.prepareStatement(
                """
                SELECT ignore_name, ignore_character_id
                FROM ignores
                WHERE character_id = ?
            """
                    .trimIndent()
            )

        select.use {
            it.setInt(1, characterId)
            it.executeQuery().use { resultSet ->
                while (resultSet.next()) {
                    val name = resultSet.getString("ignore_name")
                    val ignoreCharId = resultSet.getInt("ignore_character_id")
                    ignores +=
                        CharacterSocialData.Ignore(
                            name = name,
                            characterId = if (resultSet.wasNull()) null else ignoreCharId,
                        )
                }
            }
        }

        return ignores
    }

    override fun save(connection: DatabaseConnection, player: Player, characterId: Int) {
        val socialData = player.socialData

        // Delete existing entries and re-insert (simpler than upsert for lists)
        deleteFriends(connection, characterId)
        deleteIgnores(connection, characterId)

        // Insert friends
        val insertFriend =
            connection.prepareStatement(
                """
                INSERT INTO friends (character_id, friend_name, friend_character_id)
                VALUES (?, ?, ?)
            """
                    .trimIndent()
            )

        insertFriend.use { stmt ->
            for (friend in socialData.friends) {
                stmt.setInt(1, characterId)
                stmt.setString(2, friend.name)
                if (friend.characterId != null) {
                    stmt.setInt(3, friend.characterId)
                } else {
                    stmt.setNull(3, java.sql.Types.INTEGER)
                }
                stmt.addBatch()
            }
            stmt.executeBatch()
        }

        // Insert ignores
        val insertIgnore =
            connection.prepareStatement(
                """
                INSERT INTO ignores (character_id, ignore_name, ignore_character_id)
                VALUES (?, ?, ?)
            """
                    .trimIndent()
            )

        insertIgnore.use { stmt ->
            for (ignore in socialData.ignores) {
                stmt.setInt(1, characterId)
                stmt.setString(2, ignore.name)
                if (ignore.characterId != null) {
                    stmt.setInt(3, ignore.characterId)
                } else {
                    stmt.setNull(3, java.sql.Types.INTEGER)
                }
                stmt.addBatch()
            }
            stmt.executeBatch()
        }
    }

    private fun deleteFriends(connection: DatabaseConnection, characterId: Int) {
        val delete = connection.prepareStatement("DELETE FROM friends WHERE character_id = ?")
        delete.use {
            it.setInt(1, characterId)
            it.executeUpdate()
        }
    }

    private fun deleteIgnores(connection: DatabaseConnection, characterId: Int) {
        val delete = connection.prepareStatement("DELETE FROM ignores WHERE character_id = ?")
        delete.use {
            it.setInt(1, characterId)
            it.executeUpdate()
        }
    }
}
