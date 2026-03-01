# OSRS Packet Reference for RSMod v2

**Target Revision:** 233 (late 2023)  
**Source:** Rune-Server.org community documentation

---

## 📡 Server -> Client Packets

### Critical Packets

#### Player Updating (Opcode varies by revision)
**Purpose:** Update player positions, appearance, animations
**Structure:**
```
- Bit flags for each player
- Movement updates (walk/run/teleport)
- Appearance updates (equipment, colors, animations)
- Hit updates (damage, type)
- Graphics/spotanims
- Chat messages
```

**Key Update Masks:**
| Mask | Purpose |
|------|---------|
| 0x400 | Force movement (Agility) |
| 0x100 | Graphics update |
| 0x8 | Animation |
| 0x4 | Forced chat |
| 0x80 | Regular chat |
| 0x1 | Interacting entity |
| 0x10 | Appearance update |
| 0x2 | Facing coordinate |
| 0x20 | Hit update |
| 0x200 | Double hit update |

#### NPC Updating
**Purpose:** Update NPC positions and states
**Structure:** Similar to player updating but for NPCs

#### Interface Packets

**Open Interface:**
```
Opcode: 97 (example - varies by revision)
Structure:
  - short: interface ID
```

**Set Interface Item:**
```
Opcode: 246 (example)
Structure:
  - short: interface ID
  - short: component ID  
  - short: item ID
  - int: item amount
```

**Set Interface Text:**
```
Opcode: 126 (example)
Structure:
  - string: text
  - short: interface ID
```

### Inventory Packets

#### Update Inventory Full
```
Opcode: Varies
Structure:
  - short: interface ID
  - short: items count
  - For each item:
    - short: item ID (-1 if empty)
    - int: item amount (if stackable)
```

#### Update Single Item
```
Opcode: 34 (example)
Structure:
  - short: interface ID
  - short: slot
  - short: item ID
  - int: item amount
```

### Game State Packets

#### Load Map Region
```
Opcode: 73 (example)
Structure:
  - short: region X
  - short: region Y
  - int[4]: map keys (XTEA)
```

#### System Update
```
Opcode: 114
Structure:
  - short: seconds until update
```

#### Logout
```
Opcode: 109
Structure: None (0 bytes)
```

### Skill & Stat Packets

#### Update Stat
```
Opcode: Varies by revision
Structure:
  - byte: skill ID
  - int: XP
  - byte: level
```

#### Run Energy
```
Opcode: 110
Structure:
  - byte: energy (0-100)
```

### Sound & Music

#### Play Sound
```
Opcode: 174 (example)
Structure:
  - short: sound ID
  - byte: volume
  - short: delay
```

#### Play Song
```
Opcode: 74 (example)
Structure:
  - short: song ID
```

---

## 📤 Client -> Server Packets

### Movement Packets

#### Walk Packet
```
Opcode: Varies
Structure:
  - byte: type (0=fixed, 1=variable byte, 2=variable short)
  - byte: click type (1=regular, 2=map)
  - byte[3]: destination coordinates (packed)
  - short[steps]: intermediate waypoints
```

#### Run Toggle
```
Opcode: 70 (example)
Structure: None
```

### Interaction Packets

#### Object Action
```
Opcode: 132 (Op1), 252 (Op2), etc.
Structure:
  - short: object ID
  - short: x
  - short: y
  - byte: action index
```

#### NPC Action
```
Opcode: 72 (Attack), 17 (Op2), etc.
Structure:
  - short: NPC index
  - boolean: run (if applicable)
```

#### Item Action
```
Opcode: 122 (Op1), 75 (Op3), etc.
Structure:
  - short: slot
  - short: interface ID
  - short: item ID
```

#### Item On Item
```
Opcode: 53
Structure:
  - short: slot 1
  - short: item ID 1
  - short: slot 2
  - short: item ID 2
```

#### Item On Object
```
Opcode: 192
Structure:
  - short: object ID
  - short: x
  - short: y
  - short: slot
  - short: item ID
```

#### Item On NPC
```
Opcode: 131
Structure:
  - short: NPC index
  - short: item ID
  - short: slot
```

### Combat Packets

#### Attack Player
```
Opcode: 73
Structure:
  - short: player index
  - boolean: run
```

#### Attack NPC
```
Opcode: 72
Structure:
  - short: NPC index
  - boolean: run
```

### Interface Packets

#### Interface Button Click
```
Opcode: 185
Structure:
  - short: interface ID
  - short: component ID
```

#### Enter Amount
```
Opcode: 208
Structure:
  - int: amount
```

#### Enter Text
```
Opcode: 60
Structure:
  - string: text
```

### Communication

#### Chat Message
```
Opcode: 4
Structure:
  - byte: color
  - byte: effects
  - byte: length
  - bytes[length]: compressed text
```

#### Private Message
```
Opcode: 126
Structure:
  - long: recipient username
  - byte: length
  - bytes[encoded]: message
```

#### Add Friend
```
Opcode: 188
Structure:
  - long: username
```

#### Add Ignore
```
Opcode: 133
Structure:
  - long: username
```

---

## 🔐 Login Protocol

### Connection Types
| Value | Type |
|-------|------|
| 14 | Login request |
| 15 | Update (JS5) |
| 16 | New connection |
| 18 | Reconnecting |

### Login Response Codes
| Code | Response |
|------|----------|
| 2 | Success |
| 3 | Invalid credentials |
| 4 | Account disabled |
| 5 | Already logged in |
| 6 | Outdated client |
| 7 | World full |
| 8 | Login server offline |
| 9 | Login limit exceeded |
| 10 | Bad session ID |
| 11 | Login rejected |
| 12 | Members-only world |
| 13 | Could not complete login |
| 14 | Server updating |
| 15 | Reconnect success (chat preserved) |
| 16 | Login attempts exceeded |
| 17 | Members-only area |
| 20 | Invalid login server |
| 21 | Profile transferring |

---

## 📝 ISAAC Cipher

Packet opcodes are encrypted using ISAAC (Indirection, Shift, Accumulate, Add, and Count) cipher.

### Implementation Notes:
- Seed both client and server ciphers with session keys
- Add 50 to each int when seeding
- Cipher state must remain synchronized
- One cipher for reading, one for writing

```kotlin
// Seeding example
val seed = IntArray(4) { sessionKey[it] + 50 }
val cipher = IsaacCipher(seed)

// Encrypt opcode
val encryptedOpcode = (realOpcode + cipher.nextInt()) and 0xFF
```

---

## 🛡️ Security Best Practices

### Server-Side Validation

**Always validate:**
1. Coordinates are within bounds
2. Inventory slots are valid (0-27)
3. Item IDs exist
4. NPC indices are valid
5. Player has required permissions
6. Distance checks for interactions

**Example validation:**
```kotlin
fun validateInteraction(player: Player, target: Entity): Boolean {
    // Distance check
    if (player.distanceTo(target) > 16) {
        return false
    }
    
    // Line of sight
    if (!hasLineOfSight(player, target)) {
        return false
    }
    
    // Combat check
    if (player.isDead || target.isDead) {
        return false
    }
    
    return true
}
```

### Anti-Cheat Basics

**Common checks:**
1. Movement speed validation
2. Action tick validation
3. Inventory state verification
4. Stat level bounds checking
5. Equipment requirements

---

## 📊 Packet Size Types

| Type | Description |
|------|-------------|
| Fixed | Constant size, known ahead |
| Variable Byte | Size specified in single byte |
| Variable Short | Size specified in short |

**Important:** Always use appropriate size type to minimize bandwidth.

---

## 🔧 Debugging Packets

### Logging Incoming Packets
```kotlin
fun handleIncomingPacket(player: Player, opcode: Int, payload: ByteArray) {
    logger.debug {
        "[${player.username}] Opcode=$opcode, Size=${payload.size}, " +
        "Data=${payload.toHex()}"
    }
    // ... handle packet
}
```

### Common Debugging Tools
1. **Wireshark** - Packet capture
2. **RSProx** - RS-specific proxy
3. **Client debugging** - Enable in OpenOSRS
4. **Server logging** - Verbose packet logging

---

## 📚 Revision-Specific Notes

### Finding Opcodes for Your Revision

1. Download matching OpenOSRS/RuneLite version
2. Search for `PacketType` or similar enum
3. Look for packet encoder/decoder classes
4. Cross-reference with CS2 script calls

### Opcode Changes

Opcodes change frequently between revisions:
- Never hardcode opcodes
- Use named constants
- Document revision in comments

**Example:**
```kotlin
// Rev 233
const val PLAYER_UPDATE_OPCODE = 81
const val NPC_UPDATE_OPCODE = 65
const val UPDATE_STAT_OPCODE = 10
```

---

*Note: Opcodes listed are examples from various revisions. Always verify against your target revision's client.*

