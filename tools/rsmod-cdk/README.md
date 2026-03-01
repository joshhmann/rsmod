# RSMod Content Development Kit (CDK)

**Rapid content creation framework for RSMod v2**

## 🎯 Philosophy

**One command = Complete implementation**

```bash
# Create a complete NPC with combat, drops, and dialogue
rsmod-cdk npc create "Zombie" --combat --drops --dialogue

# Create a complete quest
rsmod-cdk quest create "Shield of Arrav" --stages 6

# Create a drop table from wiki
rsmod-cdk drops create "Moss Giant" --from-wiki

# Create a complete area
rsmod-cdk area create "Edgeville Dungeon" --dungeon
```

## 🚀 Quick Start

```bash
# Use directly
python tools/rsmod-cdk/cli.py --help

# Or create alias
python tools/rsmod-cdk/install.py
```

## 📦 Generators

| Generator | Command | What It Creates |
|-----------|---------|-----------------|
| **NPC** | `npc create` | Combat, drops, dialogue |
| **Quest** | `quest create` | Full quest structure |
| **Drops** | `drops create` | Drop table files |
| **Area** | `area create` | City/dungeon modules |
| **Shop** | `shop create` | Shop configs |
| **Skill** | `skill add` | Resources & recipes |

## 📝 Examples

### Create Complete Zombie

```bash
python tools/rsmod-cdk/cli.py npc create "Zombie" --full
```

Creates:
- `rsmod/content/other/npc-drops/tables/ZombieDropTables.kt`
- `rsmod/content/other/npc-combat/ZombieCombat.kt`
- `rsmod/content/generic/generic-npcs/npcs/ZombieDialogue.kt`

### Create Quest

```bash
python tools/rsmod-cdk/cli.py quest create "Knight's Sword" --stages 7 --npcs "Squire,Sir Vyvin,Reldo,Thurgo"
```

### Batch Create

```bash
python tools/rsmod-cdk/cli.py npc batch --tier 1
```

## 🧪 Validation

```bash
# Validate all content
python tools/rsmod-cdk/cli.py validate

# Test NPC
python tools/rsmod-cdk/cli.py test npc "Zombie"
```

## 📊 Status

```bash
# Show implementation status
python tools/rsmod-cdk/cli.py status

# Show gaps
python tools/rsmod-cdk/cli.py gaps
```

---

**Create complete RSMod content in minutes, not hours.**
