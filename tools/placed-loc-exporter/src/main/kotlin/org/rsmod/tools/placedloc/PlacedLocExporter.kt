package org.rsmod.tools.placedloc

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.openrs2.cache.Store
import org.openrs2.crypto.SymmetricKey
import java.io.BufferedReader
import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) {
    PlacedLocExportCommand().main(args)
}

class PlacedLocExportCommand : CliktCommand(name = "placed-loc-export") {
    private val cacheDir: String by option("--cache-dir", "-c").default(".data/cache")
    private val xteaFile: String by option("--xtea", "-x").default(".data/cache/xteas.json")
    private val outputDir: String by option("--output", "-o").default("tools/cache-symbol-audit/exports")
    private val locSym: String by option("--loc-sym").default(".data/symbols/loc.sym")

    override fun run() {
        val cpath = Paths.get(cacheDir)
        val xpath = Paths.get(xteaFile)
        val opath = Paths.get(outputDir)
        val lpath = Paths.get(locSym)

        val xteaByName = loadXteaByName(File(xteaFile))
        echo("Loaded " + xteaByName.size + " XTEA key entries")
        val idToSym = loadLocSymbols(File(locSym))
        echo("Loaded " + idToSym.size + " loc symbols")

        val regions = arrayOf(
            arrayOf("50","50","Lumbridge Castle"),
            arrayOf("50","51","Lumbridge Swamp/Fishing"),
            arrayOf("51","52","Varrock West Bank"),
            arrayOf("48","54","Edgeville Bank"),
            arrayOf("46","52","Falador East Bank"),
            arrayOf("49","54","Grand Exchange"),
            arrayOf("50","53","Varrock general"),
            arrayOf("47","52","Falador general"),
            arrayOf("48","53","Edgeville general"),
            arrayOf("49","50","Lumbridge south"),
            arrayOf("50","49","Lumbridge swamp south"),
            // F2P Mining Zones - surface mines
            arrayOf("51","51","Al Kharid Mine"),
            arrayOf("52","51","Al Kharid East"),
            arrayOf("48","50","Draynor Village"),
            arrayOf("48","51","Draynor South"),
            arrayOf("46","51","Falador South / Rimmington"),
            arrayOf("47","51","Falador West / Dwarven Mine entrance"),
            arrayOf("45","51","Crafting Guild"),
            arrayOf("49","49","Lumbridge Swamp Central"),
            arrayOf("49","51","Lumbridge East"),
            arrayOf("44","46","Karamja North"),
            arrayOf("44","47","Karamja Volcano"),
            arrayOf("45","46","Karamja East"),
            arrayOf("45","47","Karamja Southeast"),
            arrayOf("49","56","Ice Mountain"),
            arrayOf("50","56","Ice Mountain East"),
            arrayOf("50","55","Rune Essence Mine"),
            arrayOf("48","55","Wilderness Edge"),
            // Underground mining areas
            arrayOf("47","152","Dwarven Mine / Mining Guild"),
            arrayOf("46","152","Dwarven Mine West"),
            arrayOf("48","152","Dwarven Mine East / Motherlode"),
            // Wilderness mines
            arrayOf("48","62","Wilderness Hobgoblin Mine / Resource Area"),

        )

        echo("Opening cache store...")
        val store = Store.open(cpath)
        val cache = Cache.open(store)
        val allLocs = mutableListOf<PlacedLoc>()

        for (r in regions) {
            val name = "l" + r[0] + "_" + r[1]
            val entry = xteaByName[name]
            if (entry == null) {
                echo("  " + name + " (" + r[2] + "): NO KEY")
                continue
            }
            try {
                val buf = cache.read(5, name, 0, SymmetricKey.fromIntArray(entry.key))
                buf.use { bb ->
                    val bytes = ByteArray(bb.readableBytes())
                    bb.readBytes(bytes)
                    val locs = decodeMapLocList(bytes)
                    val bx = r[0].toInt() * 64
                    val by = r[1].toInt() * 64
                    for (l in locs) {
                        val sym = idToSym[l.locId] ?: ("loc_" + l.locId)
                        allLocs.add(PlacedLoc(l.locId, sym, bx + l.localX, by + l.localZ,
                            l.level, l.localX, l.localZ, l.shape, l.angle, r[0].toInt(), r[1].toInt(), r[2]))
                    }
                    echo("  " + name + " (" + r[2] + "): " + locs.size + " locs")
                }
            } catch (e: Exception) {
                echo("  " + name + " (" + r[2] + "): ERROR: " + e.message)
            }
        }
        if (allLocs.isEmpty()) { echo("No locs!"); return }

        // Write full CSV
        File(opath.toString() + "/placed-locs-rsmod.csv").bufferedWriter().use { w ->
            w.write("loc_id,symbol,world_x,world_y,level,local_x,local_z,shape,angle,region_x,region_y,region_label\n")
            for (loc in allLocs.sortedWith(compareBy({ it.worldY }, { it.worldX }, { it.level }))) {
                w.write(loc.locId.toString() + "," + loc.symbol + "," + loc.worldX.toString() + "," + loc.worldY.toString() + "," + loc.level.toString() + ",")
                w.write(loc.localX.toString() + "," + loc.localZ.toString() + "," + loc.shape.toString() + "," + loc.angle.toString() + ",")
                w.write(loc.regionX.toString() + "," + loc.regionY.toString() + "," + loc.regionLabel + "\n")
            }
        }
        echo("Full export: placed-locs-rsmod.csv (" + allLocs.size + " locs)")

        // Lumbridge CSV
        val lumLocs = allLocs.filter { it.regionLabel.contains("Lumbridge") }
        File(opath.toString() + "/lumbridge-placed-locs-rsmod.csv").bufferedWriter().use { w ->
            w.write("loc_id,symbol,world_x,world_y,level,local_x,local_z,shape,angle,region_x,region_y\n")
            for (loc in lumLocs.sortedWith(compareBy({ it.worldY }, { it.worldX }, { it.level }))) {
                w.write(loc.locId.toString() + "," + loc.symbol + "," + loc.worldX.toString() + "," + loc.worldY.toString() + "," + loc.level.toString() + ",")
                w.write(loc.localX.toString() + "," + loc.localZ.toString() + "," + loc.shape.toString() + "," + loc.angle.toString() + ",")
                w.write(loc.regionX.toString() + "," + loc.regionY.toString() + "\n")
            }
        }
        echo("Lumbridge export: lumbridge-placed-locs-rsmod.csv (" + lumLocs.size + " locs)")

        // Close cache

        try { cache.close() } catch (_: Exception) {}
        try { store.close() } catch (_: Exception) {}
        // Summary
        echo("\n=== REGION SUMMARY ===")
        for (r in regions) {
            val cnt = allLocs.count { it.regionLabel == r[2] }
            echo("  l" + r[0] + "_" + r[1] + " (" + r[2] + "): " + cnt + " locs")
        }

        // Top symbols
        echo("\n=== TOP 40 SYMBOLS ===")
        val counts = mutableMapOf<String, Int>()
        for (loc in allLocs) { counts[loc.symbol] = (counts[loc.symbol] ?: 0) + 1 }
        val sorted = counts.entries.sortedByDescending { it.value }.take(40)
        for ((sym, cnt) in sorted) { echo("  " + sym + ": " + cnt) }

        // Sanity checks
        echo("\n=== SANITY CHECK ===")
        val groups = mutableMapOf<String, MutableList<PlacedLoc>>()
        for (loc in allLocs) {
            val list = groups.getOrPut(loc.regionLabel) { mutableListOf() }
            list.add(loc)
        }
        val checks = mapOf(
            "Lumbridge Castle" to listOf("hundred_lumbridge_door", "ladder", "bank", "range", "anvil", "tree", "gate"),
            "Varrock West Bank" to listOf("bank", "varrock"),
            "Edgeville Bank" to listOf("bank"),
            "Falador East Bank" to listOf("bank", "falador"),
            "Grand Exchange" to listOf("grand", "exchange")
        )
        for ((label, kws) in checks) {
            val locs = groups[label] ?: continue
            val syms = locs.map { it.symbol.lowercase() }.toSet()
            val found = kws.filter { kw -> syms.any { it.contains(kw) } }
            echo("  [" + label + "] " + locs.size + " locs, hits: " + found.size + "/" + kws.size)
            if (found.isNotEmpty()) echo("    " + found.joinToString(", "))
        }
    }
}

data class PlacedLoc(
    val locId: Int, val symbol: String, val worldX: Int, val worldY: Int,
    val level: Int, val localX: Int, val localZ: Int, val shape: Int, val angle: Int,
    val regionX: Int, val regionY: Int, val regionLabel: String
)

data class LocDecoded(val locId: Int, val localX: Int, val localZ: Int, val level: Int, val shape: Int, val angle: Int)
data class XteaEntry(val name: String, val archive: Int, val group: Int, val key: IntArray)

fun loadXteaByName(f: File): Map<String, XteaEntry> {
    val mapper = jacksonObjectMapper()
    @Suppress("UNCHECKED_CAST")
    val raw: List<Map<String, Any>> = mapper.readValue(f)
    val result = mutableMapOf<String, XteaEntry>()
    for (e in raw) {
        val name = e["name"] as? String ?: continue
        val kl = e["key"] as? List<Int> ?: continue
        if (kl.size < 4) continue
        result[name] = XteaEntry(name, (e["archive"] as? Int) ?: 0, (e["group"] as? Int) ?: 0,
            intArrayOf(kl[0], kl[1], kl[2], kl[3]))
    }
    return result
}

fun loadLocSymbols(f: File): Map<Int, String> {
    val m = mutableMapOf<Int, String>()
    if (!f.exists()) return m
    val reader: BufferedReader = f.bufferedReader()
    for (line in reader.lines()) {
        val t = line.trim()
        if (t.contains("\t")) {
            val parts = t.split("\t")
            if (parts.size >= 2) try { m[parts[0].toInt()] = parts[1] } catch (_: Exception) {}
        }
    }
    reader.close()
    return m
}

fun decodeMapLocList(data: ByteArray): List<LocDecoded> {
    val locs = mutableListOf<LocDecoded>()
    var pos = 0
    var cid = -1
    while (pos < data.size) {
        var off = 0
        while (pos < data.size) {
            val (v, p) = rss(data, pos)
            pos = p
            off += v
            if (v != 0x7FFF) break
        }
        if (off == 0) break
        cid += off
        var lc = 0
        while (pos < data.size) {
            val (d, p2) = rss(data, pos)
            pos = p2
            if (d == 0) break
            lc += d - 1
            if (pos >= data.size) break
            val a = data[pos].toInt() and 0xFF
            pos++
            locs.add(LocDecoded(cid, (lc shr 6) and 0x3F, lc and 0x3F,
                (lc shr 12) and 0x3, (a shr 2) and 0x1F, a and 0x3))
        }
    }
    return locs
}

fun rss(data: ByteArray, pos: Int): Pair<Int, Int> {
    if (pos >= data.size) return Pair(0, pos)
    val b = data[pos].toInt() and 0xFF
    return if ((b and 0x80) != 0) {
        if (pos + 2 > data.size) Pair(0, pos)
        else Pair(((b and 0x7F) shl 8) or (data[pos + 1].toInt() and 0xFF), pos + 2)
    } else Pair(b, pos + 1)
}
