package org.rsmod.api.cache.map.tile

import io.netty.buffer.ByteBuf
import org.rsmod.api.cache.util.InlineByteBuf
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid

public object MapTileDecoder {
    public fun decode(buf: InlineByteBuf): MapTileSimpleDefinition {
        val def = MapTileSimpleDefinition()
        val cursor = buf.newCursor()
        decodeTiles(buf, cursor, def, 0, 0, 0)
        return def
    }

    private tailrec fun decodeTiles(
        buf: InlineByteBuf,
        cursor: InlineByteBuf.Cursor,
        def: MapTileSimpleDefinition,
        level: Int,
        x: Int,
        z: Int,
    ): InlineByteBuf.Cursor {
        if (level >= CoordGrid.LEVEL_COUNT) {
            return cursor
        }

        val nextCoords = getNextCoords(level, x, z)
        val newCursor = decodeTile(buf, cursor, def, level, x, z)

        return decodeTiles(
            buf,
            newCursor,
            def,
            nextCoords.first,
            nextCoords.second,
            nextCoords.third,
        )
    }

    private fun getNextCoords(level: Int, x: Int, z: Int): Triple<Int, Int, Int> {
        return when {
            z + 1 < MapSquareGrid.LENGTH -> Triple(level, x, z + 1)
            x + 1 < MapSquareGrid.LENGTH -> Triple(level, x + 1, 0)
            else -> Triple(level + 1, 0, 0)
        }
    }

    private tailrec fun decodeTile(
        buf: InlineByteBuf,
        cursor: InlineByteBuf.Cursor,
        def: MapTileSimpleDefinition,
        level: Int,
        x: Int,
        z: Int,
    ): InlineByteBuf.Cursor {
        if (!buf.isReadable(cursor)) {
            return cursor
        }

        val newCursor = buf.readShort(cursor)
        val opcode = newCursor.value

        return when {
            opcode == 0 -> {
                newCursor
            }
            opcode == 1 -> {
                buf.readByte(newCursor)
            }
            opcode <= 49 -> {
                val id = newCursor.value.toShort().toInt()
                if (id != 0) {
                    def[x, z, level] = MapTileSimpleDefinition.COLOURED
                }
                decodeTile(buf, newCursor, def, level, x, z)
            }
            opcode <= 81 -> {
                val rule = (opcode - 49).toByte().toInt()
                if ((rule and MapTileDefinition.BLOCK_MAP_SQUARE) != 0) {
                    def[x, z, level] = MapTileSimpleDefinition.BLOCK_MAP_SQUARE
                }
                if ((rule and MapTileDefinition.LINK_BELOW) != 0) {
                    def[x, z, level] = MapTileSimpleDefinition.LINK_BELOW
                }
                if ((rule and MapTileDefinition.REMOVE_ROOFS) != 0) {
                    def[x, z, level] = MapTileSimpleDefinition.REMOVE_ROOFS
                }
                decodeTile(buf, newCursor, def, level, x, z)
            }
            else -> {
                val id = (opcode - 81).toShort().toInt()
                if (id != 0) {
                    def[x, z, level] = MapTileSimpleDefinition.COLOURED
                }
                decodeTile(buf, newCursor, def, level, x, z)
            }
        }
    }

    // This provides a more detailed view of all the configs per tile.
    public fun decode(buf: ByteBuf): MapTileDefinition {
        val tileHeights = hashMapOf<MapSquareGrid, Int>()
        val overlays = hashMapOf<MapSquareGrid, TileOverlay>()
        val underlays = hashMapOf<MapSquareGrid, TileUnderlay>()
        val rules = hashMapOf<MapSquareGrid, Byte>()
        decodeDetailedTiles(buf, tileHeights, overlays, underlays, rules, 0, 0, 0)
        return MapTileDefinition(tileHeights, rules, overlays, underlays)
    }

    private tailrec fun decodeDetailedTiles(
        buf: ByteBuf,
        tileHeights: HashMap<MapSquareGrid, Int>,
        overlays: HashMap<MapSquareGrid, TileOverlay>,
        underlays: HashMap<MapSquareGrid, TileUnderlay>,
        rules: HashMap<MapSquareGrid, Byte>,
        level: Int,
        x: Int,
        z: Int,
    ) {
        if (level >= CoordGrid.LEVEL_COUNT) {
            return
        }

        val nextCoords = getNextCoords(level, x, z)
        decodeDetailedTile(buf, tileHeights, overlays, underlays, rules, level, x, z)

        decodeDetailedTiles(
            buf,
            tileHeights,
            overlays,
            underlays,
            rules,
            nextCoords.first,
            nextCoords.second,
            nextCoords.third,
        )
    }

    private tailrec fun decodeDetailedTile(
        buf: ByteBuf,
        tileHeights: HashMap<MapSquareGrid, Int>,
        overlays: HashMap<MapSquareGrid, TileOverlay>,
        underlays: HashMap<MapSquareGrid, TileUnderlay>,
        rules: HashMap<MapSquareGrid, Byte>,
        level: Int,
        x: Int,
        z: Int,
    ) {
        if (!buf.isReadable) {
            return
        }

        val opcode = buf.readUnsignedShort()

        when {
            opcode == 0 -> {
                val coords = MapSquareGrid(x, z, level)
                tileHeights[coords] = Int.MIN_VALUE
                return
            }
            opcode == 1 -> {
                val coords = MapSquareGrid(x, z, level)
                tileHeights[coords] = buf.readUnsignedByte().toInt()
                return
            }
            opcode <= 49 -> {
                val id = buf.readShort().toInt()
                if (id != 0) {
                    val path = ((opcode - 2) shr 2)
                    val rot = ((opcode - 2) and 0x3)
                    val coords = MapSquareGrid(x, z, level)
                    overlays[coords] = TileOverlay((id - 1) and 0xFFFF, path, rot)
                }
                decodeDetailedTile(buf, tileHeights, overlays, underlays, rules, level, x, z)
            }
            opcode <= 81 -> {
                val coords = MapSquareGrid(x, z, level)
                rules[coords] = (opcode - 49).toByte()
                decodeDetailedTile(buf, tileHeights, overlays, underlays, rules, level, x, z)
            }
            else -> {
                val coords = MapSquareGrid(x, z, level)
                val id = opcode - 81
                underlays[coords] = TileUnderlay(id and 0xFF)
                decodeDetailedTile(buf, tileHeights, overlays, underlays, rules, level, x, z)
            }
        }
    }
}
