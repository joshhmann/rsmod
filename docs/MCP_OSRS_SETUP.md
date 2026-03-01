# MCP OSRS Server Setup

**Installed:** 2026-02-20  
**Source:** https://github.com/jayarrowz/mcp-osrs

---

## What Is This?

An MCP (Model Context Protocol) server that gives Claude direct access to:
- OSRS Wiki search
- Game data files (items, NPCs, interfaces, animations, etc.)

---

## Installation Status

✅ **INSTALLED SUCCESSFULLY**

```
Location: Z:\Projects\OSRS-PS-DEV\mcp-osrs
Node.js: v22.15.0 ✓
npm: 11.7.0 ✓
Packages: 407 installed ✓
Build: Successful ✓
```

---

## Available Tools

### OSRS Wiki Tools
| Tool | Purpose |
|------|---------|
| `osrs_wiki_search` | Search OSRS Wiki |
| `osrs_wiki_get_page_info` | Get page metadata |
| `osrs_wiki_parse_page` | Get page HTML content |

### Game Data Search Tools
| Tool | Searches | Use For |
|------|----------|---------|
| `search_objtypes` | objtypes.txt | Items/objects |
| `search_npctypes` | npctypes.txt | NPCs |
| `search_loctypes` | loctypes.txt | Objects/locations |
| `search_seqtypes` | seqtypes.txt | Animations |
| `search_iftypes` | iftypes.txt | Interface definitions |
| `search_invtypes` | invtypes.txt | Inventory types |
| `search_varptypes` | varptypes.txt | Player variables |
| `search_varbittypes` | varbittypes.txt | Variable bits |
| `search_rowtypes` | rowtypes.txt | Interface rows |
| `search_soundtypes` | soundtypes.txt | Sound effects |
| `search_spottypes` | spottypes.txt | Spot animations |
| `search_spritetypes` | spritetypes.txt | Sprites |
| `search_tabletypes` | tabletypes.txt | Interface tables |

### Generic Tools
| Tool | Purpose |
|------|---------|
| `search_data_file` | Search any data file |
| `get_file_details` | Get file info |
| `list_data_files` | List all data files |

---

## Data Files Available

| File | Size | Contents |
|------|------|----------|
| loctypes.txt | 1.8 MB | Location/object definitions |
| objtypes.txt | 964 KB | Item definitions |
| npctypes.txt | 398 KB | NPC definitions |
| seqtypes.txt | 366 KB | Animation sequences |
| iftypes.txt | 870 KB | Interface definitions |
| varbittypes.txt | 391 KB | Variable bits |
| rowtypes.txt | 174 KB | Interface rows |
| spottypes.txt | 108 KB | Spot animations |
| invtypes.txt | 23 KB | Inventory types |
| soundtypes.txt | 22 KB | Sound effects |
| tabletypes.txt | 22 KB | Interface tables |
| spritetypes.txt | 20 KB | Sprites |
| varptypes.txt | 56 KB | Player variables |

---

## Configuration

Added to `.mcp.json`:

```json
{
  "mcpServers": {
    "osrs": {
      "type": "stdio",
      "command": "node",
      "args": ["mcp-osrs/dist/index.js"],
      "cwd": "."
    }
  }
}
```

---

## Example Usage

### Search for Items
```javascript
// Search for dragon items
search_objtypes({
  query: "dragon",
  page: 1,
  pageSize: 10
})
```

### Search OSRS Wiki
```javascript
// Search wiki for Abyssal whip
osrs_wiki_search({
  search: "Abyssal whip"
})
```

### Get Animation ID
```javascript
// Find animation by name
search_seqtypes({
  query: "attack",
  page: 1,
  pageSize: 20
})
```

---

## Integration with Your Project

This MCP server gives Claude access to:

1. **Accurate game data** - Direct from cache dumps
2. **Fast lookups** - No web scraping needed
3. **Wiki integration** - Search official wiki
4. **ID resolution** - Find IDs for items/NPCs/animations

---

## Updating

```bash
cd mcp-osrs
git pull
npm install
npm run build
```

---

## Troubleshooting

### Server Won't Start
```bash
# Check if dist exists
ls mcp-osrs/dist/

# Rebuild
npm run build
```

### Data Files Missing
```bash
# Check data directory
ls mcp-osrs/data/

# Re-copy data
npm run copy-data
```

---

## Next Steps

1. **Restart Claude** (if using Claude Desktop)
2. **Test the tools** - Try searching for items/NPCs
3. **Use in development** - Query game data as needed

---

*MCP OSRS Server is now ready to use!* 🎉

