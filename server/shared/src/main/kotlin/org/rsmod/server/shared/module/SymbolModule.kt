package org.rsmod.server.shared.module

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Provider
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.PathWalkOption
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.relativeTo
import kotlin.io.path.walk
import org.rsmod.api.type.symbols.name.NameLoader
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.module.ExtendedModule
import org.rsmod.server.shared.DirectoryConstants

object SymbolModule : ExtendedModule() {
    override fun bind() {
        bindProvider(NameMappingProvider::class.java)
    }
}

private class NameMappingProvider : Provider<NameMapping> {
    private val logger = InlineLogger()

    override fun get(): NameMapping {
        val dirs = shallowSymbolDirectories()
        logger.info { "Loading symbols from directories: ${dirs.map { it.toString() }}" }
        val areas = dirs.readSymbols("area")
        val bas = dirs.readSymbols("bas")
        val categories = dirs.readSymbols("category")
        val interfaces = dirs.readSymbols("interface")
        val clientscripts = dirs.readSymbols("clientscript")
        val components = dirs.readComps("component", interfaces = interfaces)
        val content = dirs.readSymbols("content")
        val controllers = dirs.readSymbols("controller")
        val currencies = dirs.readSymbols("currency")
        val dbRows = dirs.readSymbols("dbrow")
        val dbTables = dirs.readSymbols("dbtable")
        val dbCols = dirs.readDbColumns("dbcol", dbTables = dbTables)
        val dropTriggers = dirs.readSymbols("droptrigger")
        val enums = dirs.readSymbols("enum")
        val fonts = dirs.readSymbols("font")
        val headbars = dirs.readSymbols("headbar")
        val hitmarks = dirs.readSymbols("hitmark")
        val hunt = dirs.readSymbols("hunt")
        val invs = dirs.readSymbols("inv")
        val jingles = dirs.readSymbols("jingle")
        val locs = dirs.readSymbols("loc")
        val midis = dirs.readSymbols("midi")
        val mesanims = dirs.readSymbols("mesanim")
        val models = dirs.readSymbols("model")
        val modLevels = dirs.readSymbols("modlevel")
        val npcs = dirs.readSymbols("npc")
        val objs = dirs.readSymbols("obj")
        val params = dirs.readSymbols("param")
        val projanims = dirs.readSymbols("projanim")
        val queues = dirs.readSymbols("queue")
        val seqs = dirs.readSymbols("seq")
        val spotanims = dirs.readSymbols("spotanim")
        val stats = dirs.readSymbols("stat")
        val structs = dirs.readSymbols("struct")
        val synths = dirs.readSymbols("synth")
        val timers = dirs.readSymbols("timer")
        val varcons = dirs.readSymbols("varcon")
        val varconbits = dirs.readSymbols("varconbit")
        val varbits = dirs.readSymbols("varbit")
        val varobjbits = dirs.readSymbols("varobj")
        val varns = dirs.readSymbols("varn")
        val varnbits = dirs.readSymbols("varnbit")
        val varps = dirs.readSymbols("varp")
        val walkTriggers = dirs.readSymbols("walktrigger")
        logger.info { "Loaded NameMapping: enums=${enums.size}, dbRows=${dbRows.size}, dbTables=${dbTables.size}, objs=${objs.size}" }
        return NameMapping(
            areas = areas,
            categories = categories,
            objs = objs,
            interfaces = interfaces,
            components = components,
            varps = varps,
            varbits = varbits,
            params = params,
            npcs = npcs,
            locs = locs,
            varobjbits = varobjbits,
            enums = enums,
            invs = invs,
            modLevels = modLevels,
            seqs = seqs,
            mesanims = mesanims,
            content = content,
            synths = synths,
            fonts = fonts,
            stats = stats,
            currencies = currencies,
            timers = timers,
            queues = queues,
            controllers = controllers,
            varcons = varcons,
            varconbits = varconbits,
            structs = structs,
            dropTriggers = dropTriggers,
            bas = bas,
            spotanims = spotanims,
            jingles = jingles,
            walkTriggers = walkTriggers,
            varns = varns,
            varnbits = varnbits,
            hitmarks = hitmarks,
            headbars = headbars,
            projanims = projanims,
            midis = midis,
            dbTables = dbTables,
            dbRows = dbRows,
            dbColumns = dbCols,
            hunt = hunt,
            clientscripts = clientscripts,
            models = models,
        )
    }

    private fun List<ShallowDirectoryMap>.readSymbols(fileName: String): Map<String, Int> {
        val merged = mutableMapOf<String, Int>()
        for (entry in this) {
            merged += entry.readSymbols(DEFAULT_FILE_DIR, fileName)
        }
        return merged
    }

    private fun List<ShallowDirectoryMap>.readComps(
        fileName: String,
        interfaces: Map<String, Int>,
    ): Map<String, Int> {
        val merged = mutableMapOf<String, Int>()
        for (entry in this) {
            merged += entry.readComps(DEFAULT_FILE_DIR, fileName, interfaces)
        }
        return merged
    }

    private fun List<ShallowDirectoryMap>.readDbColumns(
        fileName: String,
        dbTables: Map<String, Int>,
    ): Map<String, Int> {
        val merged = mutableMapOf<String, Int>()
        for (entry in this) {
            merged += entry.readDbColumns(DEFAULT_FILE_DIR, fileName, dbTables)
        }
        return merged
    }

    private fun ShallowDirectoryMap.readSymbols(
        directory: String,
        fileName: String,
    ): Map<String, Int> {
        val merged = mutableMapOf<String, Int>()
        val files = find(directory, fileName)
        for (file in files) {
            merged += NameLoader.read(file)
        }
        return merged
    }

    private fun ShallowDirectoryMap.readComps(
        directory: String,
        fileName: String,
        interfaces: Map<String, Int>,
    ): Map<String, Int> {
        val merged = mutableMapOf<String, Int>()
        val files = find(directory, fileName)
        for (file in files) {
            merged += NameLoader.readComponents(file, interfaces)
        }
        return merged
    }

    private fun ShallowDirectoryMap.readDbColumns(
        directory: String,
        fileName: String,
        interfaces: Map<String, Int>,
    ): Map<String, Int> {
        val merged = mutableMapOf<String, Int>()
        val files = find(directory, fileName)
        for (file in files) {
            merged += NameLoader.readDbColumns(file, interfaces)
        }
        return merged
    }

    private companion object {
        private const val DEFAULT_FILE_DIR = ""
    }
}

private fun shallowSymbolDirectories(): List<ShallowDirectoryMap> {
    val root = DirectoryConstants.SYMBOL_PATH
    val local = root.resolve(".local")
    return listOf(root.shallowDirectoryMap(), local.shallowDirectoryMap())
}

private fun Path.shallowDirectoryMap(): ShallowDirectoryMap {
    if (!isDirectory()) return ShallowDirectoryMap(emptyMap())
    val map = mutableMapOf<String, MutableList<Path>>()
    walk().filter { it.isRegularFile() && it.toString().endsWith(".sym") }.forEach { path ->
        val name = path.nameWithoutExtension
        map.computeIfAbsent(name) { mutableListOf() }.add(path)
    }
    return ShallowDirectoryMap(map)
}

private fun Path.listFiles(): List<Path> = Files.list(this).filter(Files::isRegularFile).toList()

private data class ShallowDirectoryMap(private val files: Map<String, List<Path>>) :
    Map<String, List<Path>> by files {
    fun find(parent: String, fileName: String): List<Path> = this[fileName] ?: emptyList()
}
