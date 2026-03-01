package org.rsmod.api.type.builders.map.loc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongArrayList
import jakarta.inject.Inject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.logging.Logger
import org.rsmod.api.cache.map.loc.MapLocDefinition
import org.rsmod.api.cache.map.loc.MapLocListDecoder
import org.rsmod.api.cache.map.loc.MapLocListDefinition
import org.rsmod.api.cache.util.InlineByteBuf
import org.rsmod.api.parsers.toml.Toml
import org.rsmod.api.type.builders.resource.TypeResourceFile
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey

public class MapLocSpawnCollector
@Inject
constructor(@Toml private val objectMapper: ObjectMapper, private val nameMapping: NameMapping) {
    private val logger: Logger = Logger.getLogger(MapLocSpawnCollector::class.java.name)

    public fun loadAndCollect(
        builders: Iterable<MapLocSpawnBuilder>
    ): Map<MapSquareKey, MapLocListDefinition> {
        builders.forEach(MapLocSpawnBuilder::onPackMapTask)
        return builders.toMapDefinitions()
    }

    private fun Iterable<MapLocSpawnBuilder>.toMapDefinitions():
        Map<MapSquareKey, MapLocListDefinition> {
        val resources = flatMap(MapLocSpawnBuilder::resources).resourceSpawnTypes()
        return resources.groupDistinctKeys()
    }

    private fun Iterable<TypeResourceFile>.resourceSpawnTypes(): List<MapSpawnType> {
        return flatMap { it.mapSpawnType() }
    }

    private fun TypeResourceFile.mapSpawnType(): List<MapSpawnType> {
        val fileName = relativePath.substringAfterLast('/')
        val input = clazz.getResourceAsStream(relativePath)
        if (input == null) {
            val message = "Loc resource file not found: path=$relativePath, class=$clazz"
            throw FileNotFoundException(message)
        }

        return when {
            fileName.endsWith(".toml") -> {
                decodeTomlSpawn(input, relativePath)
            }
            fileName.startsWith('l') -> {
                if (fileName.contains('.')) {
                    val message = "Loc binary file must not have an extension: $relativePath"
                    throw IOException(message)
                }
                listOf(decodeBinarySpawn(fileName, input))
            }
            else -> {
                val message = "Unsupported loc spawn file format: $relativePath"
                throw IOException(message)
            }
        }
    }

    private fun decodeTomlSpawn(input: InputStream, sourcePath: String): List<MapSpawnType> {
        val reference = object : TypeReference<Map<String, List<TomlLocSpawn>>>() {}
        val parsed = input.use { objectMapper.readValue(it, reference) }
        val spawns = parsed[TOML_SPAWN_KEY] ?: return emptyList()
        val names = nameMapping.locs
        val grouped = Int2ObjectOpenHashMap<LongArrayList>()
        val skipped = mutableSetOf<String>()
        for (spawn in spawns) {
            val loc =
                names[spawn.loc]
                    ?: run {
                        skipped.add(spawn.loc)
                        continue
                    }
            val coords = parseCoords(spawn.coords)
            val grid = MapSquareGrid.from(coords)
            val localCoords = (grid.level shl 12) or (grid.x shl 6) or grid.z
            val attributes = (spawn.shape shl 2) or (spawn.angle and 0x3)
            val def = MapLocDefinition(id = loc, localCoords = localCoords, attributes = attributes)
            val mapSquare = MapSquareKey.from(coords)
            val spawnList = grouped.computeIfAbsent(mapSquare.id) { LongArrayList() }
            spawnList.add(def.packed)
        }
        if (skipped.isNotEmpty()) {
            logger.warning(
                "Skipped ${skipped.size} unknown loc spawn name(s) in $sourcePath: ${skipped.joinToString()}"
            )
        }
        return grouped.map { MapSpawnType(MapSquareKey(it.key), MapLocListDefinition(it.value)) }
    }

    private fun decodeBinarySpawn(fileName: String, input: InputStream): MapSpawnType {
        val mapSquare = parseMapSquare(fileName)
        val bytes = input.use(InputStream::readAllBytes)
        val definition = MapLocListDecoder.decode(InlineByteBuf(bytes))
        return MapSpawnType(mapSquare, definition)
    }

    private fun parseMapSquare(fileName: String): MapSquareKey {
        val parts = fileName.removePrefix("l").split('_')
        if (parts.size != 2) {
            val message = "Loc file name must be in format `l[x]_[z]` (e.g., `l50_50`): $fileName"
            throw IOException(message)
        }
        val x = parts[0].toIntOrNull() ?: error("Invalid x-coordinate in file name: $fileName")
        val z = parts[1].toIntOrNull() ?: error("Invalid z-coordinate in file name: $fileName")
        return MapSquareKey(x, z)
    }

    private fun List<MapSpawnType>.groupDistinctKeys(): Map<MapSquareKey, MapLocListDefinition> {
        val grouped = groupBy { it.mapSquare }

        val duplicates = grouped.filterValues { it.size > 1 }
        if (duplicates.isNotEmpty()) {
            val duplicateKeys = duplicates.keys.joinToString(", ")
            val message = "Duplicate MapSquareKeys found for loc spawn files: $duplicateKeys"
            throw IllegalStateException(message)
        }

        return grouped.mapValues { (_, entries) -> entries.single().spawns }
    }

    private data class MapSpawnType(val mapSquare: MapSquareKey, val spawns: MapLocListDefinition)

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class TomlLocSpawn(
        val loc: String,
        val coords: String,
        val shape: Int = 10,
        val angle: Int = 0,
    )

    private fun parseCoords(raw: String): CoordGrid {
        val parts = raw.split('_')
        if (parts.size != 5) {
            throw IllegalArgumentException("Loc spawn coords must be `level_mx_mz_lx_lz`: $raw")
        }
        val level = parts[0].toInt()
        val mx = parts[1].toInt()
        val mz = parts[2].toInt()
        val x = parts[3].toInt()
        val z = parts[4].toInt()
        return if (x in 0 until MapSquareGrid.LENGTH && z in 0 until MapSquareGrid.LENGTH) {
            CoordGrid(level, mx, mz, x, z)
        } else {
            CoordGrid(x, z, level)
        }
    }

    private companion object {
        // HYGIENE: "spawn" is a TOML config key, not a LocType reference.
        const val TOML_SPAWN_KEY = "spawn"
    }
}
