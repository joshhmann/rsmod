# Rune-Server.org Resources Guide for RSMod v2

**Last Updated:** 2026-02-20

This guide compiles the most valuable threads, tutorials, and documentation from rune-server.org for RSMod v2 development.

---

## 🎯 Essential Threads

### 1. RSMod v2 Setup + OpenOSRS/RuneLite
**URL:** https://rune-server.org/threads/setup-rsmod-v2-openosrs-runelite-or-just-openosrs-runelite.701403/

**What it covers:**
- Complete RSMod v2 + RuneLite setup tutorial
- Finding correct OpenOSRS version for your revision
- Setting up the game server
- Connecting RuneLite client to private server
- NGINX reverse proxy configuration
- HTTPS/SSL setup with Let's Encrypt

**Key Takeaways:**
- Use IntelliJ IDEA (recommended over Eclipse)
- Target specific revisions: 194, 199, 200, 201, 202
- Cache sources: https://archive.runestats.com/osrs/ or https://archive.openrs2.org/
- Use Kyle Escobar's RuneLite plugin for RSA key management

**Code Snippet - HTTP Server for Gamepack:**
```kotlin
package org.rsmod

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

fun main() {
    embeddedServer(Netty, port = 80) {
        routing {
            get("/") {
                call.respondFile(File("./all/data/gamepack.jar"))
            }
            get("/gamepack.jar") {
                call.respondFile(File("./all/data/gamepack.jar"))
            }
        }
    }.start(wait = true)
}
```

---

### 2. OSRS Opcode Reference (Server <-> Client)
**URL:** https://rune-server.org/threads/how-to-figure-out-opcodes-for-server-client-and-client-server-for-osrs.693487/

**What it covers:**
- How to find packet opcodes in OpenOSRS
- Server -> Client packet structures
- Client -> Server packet structures
- Opcode changes across revisions

**Important Server -> Client Packets by Revision:**

| Packet | Rev 171 | Rev 181 | Rev 182 | Rev 183 | Rev 184 |
|--------|---------|---------|---------|---------|---------|
| RunClientScript | 3 | 62 | 62 | 18 | 56 |
| UpdateRebootTimer | 5 | 72 | 72 | 1 | 72 |
| UpdateInventoryFull | 7 | 70 | 70 | 29 | 49 |
| UpdateStat | 10 | 22 | 22 | 7 | 9 |
| UpdateInventoryPartial | 22 | 44 | 44 | 5 | 1 |
| UpdateInvStopTransmit | 28 | 46 | 46 | 17 | 57 |

**Finding Packets Method:**
1. Open OpenOSRS client code
2. Look at `PacketType` enum or similar
3. Cross-reference with client scripts (CS2)
4. Use call hierarchy to find packet references

---

### 3. 317 Protocol Documentation (Concepts Apply to OSRS)
**URL:** https://rsps.fandom.com/wiki/317_Protocol

**What it covers:**
- Login protocol overview
- Player updating mechanism
- Packet structures
- RSA encryption
- ISAAC cipher

**Login Protocol Stages:**

```
Stage 1: Client -> Server
- ubyte: 14 (connection type)
- ubyte: name hash

Stage 2: Server -> Client  
- byte[8]: ignored bytes
- byte: response code
- long: server session key

Stage 3: Client -> Server
- byte: connect status (16=new, 18=reconnect)
- byte: size
- byte: 255 (magic)
- short: revision (317)
- byte: client version (0=low, 1=high)
- int[9]: CRC values
- RSA encrypted login block

Stage 4: Server -> Client
- byte: response code (2=success)
- byte: player status (0=normal, 1=mod, 2=admin)
- byte: flagged
```

**Response Codes:**
| Code | Meaning |
|------|---------|
| 2 | Successful login |
| 3 | Invalid username/password |
| 4 | Account disabled |
| 5 | Already logged in |
| 6 | Client outdated |
| 7 | World full |
| 15 | Reconnect (chat preserved) |

---

### 4. Alter + Devious Client Setup (Rev 223)
**URL:** https://rune-server.org/threads/alter-devious-last-commit-before-melxin-223-server-client-setup.706953/

**What it covers:**
- Setting up Alter server (rev 223)
- Setting up Devious client
- RSA key configuration
- Private server plugin development

**Important:** Use commit `63187b0d77dd2e9e0dacd57de0bb0c5397e23e9b` - last trusted commit before controversial changes.

**Private Server Plugin Code:**
```java
@PluginDescriptor(
    name = "Private Server",
    description = "Settings for connecting to non official servers",
    tags = {"RSPS", "Server", "Private"},
    enabledByDefault = true
)
public class PrivateServerPlugin extends Plugin
{
    @Inject
    private Client client;
    
    @Inject
    private PrivateServerConfig config;
    
    @Override
    protected void startUp() throws Exception
    {
        updateConfig();
    }
    
    private void updateConfig()
    {
        if (!config.modulus().equals(""))
        {
            client.setModulus(new BigInteger(config.modulus(), 16));
        }
    }
}
```

---

### 5. OSRS #201 - Dodian Open Source
**URL:** https://rune-server.org/threads/osrs-201-rsmod-v2-based-dodian-open-source.701647/

**What it covers:**
- Central server architecture
- World list management
- HTTP endpoints for cache/gamepack
- NGINX configuration
- Multi-world setup

**Architecture Overview:**
```
Central Server (port 8081)
├── /cache.zip - Cache download
├── /gamepack.jar - Client gamepack
├── /jav_config.ws - Client config
├── /worlds - World list (JSON)
└── /cache/<type> - Cache definitions

Game World (port 43594/8080)
├── Game connections
└── HTTP stats for world list
```

**NGINX Configuration:**
```nginx
# central.my-rsps-domain.com
location / {
    proxy_pass http://127.0.0.1:8081;
}

# world1.my-rsps-domain.com  
location / {
    proxy_pass http://127.0.0.1:8080;
}

# Gamepack from central
location /gamepack.jar {
    proxy_pass http://localhost:8081/gamepack.jar;
}
```

---

## 📚 Code Best Practices (From Community)

### 1. Use Kotlin Over Java
**Source:** Multiple experienced developers on rune-server

**Why:**
- Null safety
- Extension functions
- Data classes
- Coroutines for async
- 100% interoperable with Java

**Example - Extension Function:**
```kotlin
// Add functionality to Player class without inheritance
fun Player.hasItem(item: Int): Boolean {
    return inventory.contains(item)
}

// Usage
if (player.hasItem(Items.COINS)) {
    // ...
}
```

### 2. Use Switch Statements Over If-Else Chains
**Source:** Community code review threads

**Bad:**
```java
if (cmd.equals("spawn")) {
    // spawn
} else if (cmd.equals("tele")) {
    // teleport  
} else if (cmd.equals("item")) {
    // item
}
```

**Good:**
```kotlin
when (cmd) {
    "spawn" -> handleSpawn()
    "tele" -> handleTeleport()
    "item" -> handleItem()
    else -> return
}
```

### 3. Use Configs Instead of Hardcoding
**Source:** Code review threads

**Bad:**
```kotlin
// Sending 10 separate packets to randomize bank pin
for (i in 0..9) {
    player.write(ModifyWidgetString(i, random()))
}
```

**Good:**
```kotlin
// Use client configs - 2 packets, less bandwidth
player.setVarp(BANK_PIN_CONFIG, randomValue)
```

### 4. Proper Packet Handling
**Source:** Opcode threads

**Guidelines:**
- Validate packet size before reading
- Check bounds on array access
- Use appropriate data types
- Handle variable-length packets correctly

**Example:**
```kotlin
fun handleItemOnItem(player: Player, packet: Packet) {
    val slot1 = packet.readUnsignedShort()
    val item1 = packet.readUnsignedShort()
    val slot2 = packet.readUnsignedShort() 
    val item2 = packet.readUnsignedShort()
    
    // Validate slots are within bounds
    if (slot1 < 0 || slot1 >= 28 || slot2 < 0 || slot2 >= 28) {
        return
    }
    
    // Process interaction
    // ...
}
```

---

## 🔧 Development Tools & Tips

### 1. Cache Archives
- **Runestats:** https://archive.runestats.com/osrs/ (maintained by @Polar)
- **OpenRS2:** https://archive.openrs2.org/ (maintained by @Graham)

### 2. IntelliJ IDEA Setup
**Recommended Version:** 2023.2.6
**Download:** https://download.jetbrains.com/idea/ideaIU-2023.2.6.exe

**Why this version?**
- Newer versions have Kotlin script issues
- Stable for RSPS development
- Good Gradle integration

### 3. Git Workflow
```bash
# Clone OpenOSRS
git clone git@github.com:open-osrs/runelite.git

# Find commit for your revision
git log --all --oneline | grep "Update 202"

# Checkout specific commit
git checkout f5e16493b2d633976b167c600cd1e2517eeab5b2

# Create development branch
git checkout -b my-dev-branch
```

### 4. Finding Revisions
**Method:**
1. Go to OpenOSRS repo
2. Press Ctrl+K (Cmd+K on Mac)
3. Search "Update <revision+1>" (e.g., "Update 202" for rev 201)
4. Click Commits tab
5. Copy commit hash from URL
6. Checkout that commit

---

## 🎓 Learning Resources

### For Beginners
1. **Start with commands** - Simple to understand
2. **Look at interfaces** - Bank, shops, dialogue
3. **Study existing content** - Woodcutting, mining plugins
4. **Use Google** - Search for specific problems

### For Intermediate Developers
1. **Study packet handling** - Server/Client communication
2. **Learn player updating** - Movement, appearance
3. **Understand NPC handling** - Spawns, combat AI
4. **Explore region systems** - Map loading, instances

### For Advanced Developers
1. **Client modification** - CS2 scripts, interfaces
2. **Performance optimization** - Tick processing, memory
3. **Security** - Anti-cheat, packet validation
4. **Distributed systems** - Multi-world, central server

---

## 📝 RSMod v2 Specific Tips

### 1. Plugin Structure
```kotlin
class MyPlugin @Inject constructor(
    private val objTypes: ObjTypeList,
    private val locRepo: LocRepository,
) : PluginScript() {
    
    override fun ScriptContext.startup() {
        onOpLoc1(content.tree) { chop(it.loc, it.type) }
    }
    
    private fun ProtectedAccess.chop(loc: BoundLocInfo, type: UnpackedLocType) {
        // Implementation
    }
}
```

### 2. Event Handlers
```kotlin
// Player clicks object
onOpLoc1(content.bank_booth) { event ->
    openBank()
}

// Player uses item on item
onOpHeldU(objs.knife, objs.logs) { event ->
    fletch(event.first, event.second)
}

// NPC death
onNpcDeath(npcs.goblin) { event ->
    dropLoot(event.npc, event.killer)
}
```

### 3. Dependency Injection
```kotlin
@Inject constructor(
    private val objTypes: ObjTypeList,
    private val locTypes: LocTypeList,
    private val npcTypes: NpcTypeList,
    private val locRepo: LocRepository,
    private val npcRepo: NpcRepository,
    private val random: GameRandom,
    private val mapClock: MapClock,
)
```

---

## 🌐 Useful Links

| Resource | URL | Description |
|----------|-----|-------------|
| RSPS Wiki | https://rsps.fandom.com | Protocol documentation |
| RuneWiki | https://www.runewiki.org | Old protocol info |
| Cache Archives | https://archive.runestats.com/osrs/ | OSRS caches |
| OpenRS2 | https://archive.openrs2.org/ | Alternative cache archive |
| RSMod GitHub | https://github.com/rsmod | Official RSMod repos |
| OpenOSRS | https://github.com/open-osrs | Client for OSRS |

---

## ⚠️ Common Pitfalls

### 1. Don't Modify Client Directly
**Instead:** Use plugins/reflection for client modifications

### 2. Don't Hardcode IDs
**Instead:** Use constants, enums, or cache lookups

### 3. Don't Trust Client Input
**Always:** Validate packet data server-side

### 4. Don't Block Main Thread
**Instead:** Use coroutines for long operations

### 5. Don't Ignore Null Safety
**Kotlin Advantage:** Use `?` and `!!` appropriately

---

## 📞 Getting Help

**Discord Servers:**
- RSMod Discord: Best for RSMod-specific questions
- Rune-Server Discord: General RSPS help

**Forum Sections:**
- RSMod v2 subforum: Specific to RSMod
- Informative Threads: Tutorials and guides
- Snippets: Code examples

**Remember:**
- Search before asking
- Provide code examples
- Be specific about your revision
- Show what you've tried

---

*This guide is a compilation of knowledge from the Rune-Server.org community. Special thanks to all contributors who share their knowledge.*

