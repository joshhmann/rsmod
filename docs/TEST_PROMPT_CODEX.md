# TEST PROMPT: Content Mapper Tool Validation

## Agent: Codex
## Task: Verify Content Mapper Tools
## Priority: HIGH

---

## Background
Kimi created content mapper tools to help developers use correct symbol names from the Rev 233 cache instead of guessing from wiki. The tools are:
- `tools/content_mapper.py` (basic)
- `tools/content_mapper_advanced.py` (advanced)

We need thorough testing to ensure they work correctly.

---

## Your Mission

Run comprehensive tests on both tools and report findings. Focus on accuracy, usability, and edge cases.

---

## Test Suite A: Basic Tool (content_mapper.py)

### Test A1: Statistics
```bash
cd Z:\Projects\OSRS-PS-DEV
python tools/content_mapper.py --stats
```
**Verify:**
- [ ] Shows total items (~31,173)
- [ ] Shows total NPCs (~14,793)
- [ ] Shows total locations (~57,690)
- [ ] Output is valid JSON format
- [ ] Sample items/NPCs/locs are displayed

### Test A2: NPC Lookup - Common NPC
```bash
python tools/content_mapper.py --npc "wilderness_hill_giant"
```
**Verify:**
- [ ] NPC ID is correct (13502)
- [ ] Kotlin reference code shown
- [ ] Combat stats template included
- [ ] Drop table template included
- [ ] Spawn template included
- [ ] Format is clear and usable

### Test A3: NPC Lookup - Not Found
```bash
python tools/content_mapper.py --npc "nonexistent_npc_xyz"
```
**Verify:**
- [ ] Clear error message
- [ ] No crash or exception

### Test A4: Item Lookup - Common Item
```bash
python tools/content_mapper.py --item "dragon_scimitar"
```
**Verify:**
- [ ] Item ID is correct (4587)
- [ ] Kotlin reference shown
- [ ] Usage examples provided
- [ ] Format is clear

### Test A5: Item Lookup - Multiple Words
```bash
python tools/content_mapper.py --item "rune_platebody"
```
**Verify:**
- [ ] Finds correct item
- [ ] ID matches cache

### Test A6: Location Lookup
```bash
python tools/content_mapper.py --loc "furnace"
```
**Verify:**
- [ ] Finds furnace locations
- [ ] IDs are correct

---

## Test Suite B: Advanced Tool (content_mapper_advanced.py)

### Test B1: Search NPCs - Partial Match
```bash
python tools/content_mapper_advanced.py --search-npc "giant" --limit 10
```
**Verify:**
- [ ] Finds 10 NPCs with "giant" in name
- [ ] Results include expected NPCs (hill_giant, moss_giant variants)
- [ ] Shows both ID and symbol name
- [ ] Search is case-insensitive

### Test B2: Search Items - Partial Match
```bash
python tools/content_mapper_advanced.py --search-item "rune" --limit 10
```
**Verify:**
- [ ] Finds rune items
- [ ] Results are relevant
- [ ] Limit parameter works

### Test B3: Validate Symbol - Valid
```bash
python tools/content_mapper_advanced.py --validate-symbol "dragon_scimitar" --validate-type obj
```
**Verify:**
- [ ] Reports [OK]
- [ ] Shows correct ID (4587)
- [ ] Shows correct type

### Test B4: Validate Symbol - Invalid with Suggestion
```bash
python tools/content_mapper_advanced.py --validate-symbol "granite_cannonball_233" --validate-type obj
```
**Verify:**
- [ ] Reports [ERROR]
- [ ] Suggests "granite_cannonball" (ID: 21728)
- [ ] Suggestions are helpful

### Test B5: Validate Symbol - NPC
```bash
python tools/content_mapper_advanced.py --validate-symbol "goblin" --validate-type npc
```
**Verify:**
- [ ] Finds goblin NPC
- [ ] Correct ID returned

### Test B6: Validate Symbol - Location
```bash
python tools/content_mapper_advanced.py --validate-symbol "furnace" --validate-type loc
```
**Verify:**
- [ ] Finds furnace locations
- [ ] Multiple results if applicable

### Test B7: Full NPC Generation
```bash
python tools/content_mapper_advanced.py --full-npc "wilderness_hill_giant"
```
**Verify:**
- [ ] Complete implementation template
- [ ] Symbol reference section
- [ ] Combat stats (npcs.toml format)
- [ ] Spawn template
- [ ] Drop table Kotlin code
- [ ] Usage examples

### Test B8: Batch NPC Generation
```bash
python tools/content_mapper_advanced.py --batch-npcs "wilderness_hill_giant,wilderness_moss_giant,wilderness_ice_giant" --output-dir test_generated/
```
**Verify:**
- [ ] Creates output directory
- [ ] Generates 3 files
- [ ] Each file has complete implementation
- [ ] Summary file created

### Test B9: Edge Case - Empty Search
```bash
python tools/content_mapper_advanced.py --search-npc "" --limit 5
```
**Verify:**
- [ ] Handles gracefully
- [ ] No crash

### Test B10: Edge Case - Special Characters
```bash
python tools/content_mapper_advanced.py --validate-symbol "cert_dragon_scimitar" --validate-type obj
```
**Verify:**
- [ ] Handles cert_ prefix
- [ ] Finds correct item

---

## Test Suite C: Integration & Edge Cases

### Test C1: Compare Basic vs Advanced Output
Compare outputs for same NPC:
```bash
python tools/content_mapper.py --npc "wilderness_hill_giant" > basic.txt
python tools/content_mapper_advanced.py --full-npc "wilderness_hill_giant" > advanced.txt
```
**Verify:**
- [ ] Both tools agree on NPC ID
- [ ] Both tools agree on symbol name
- [ ] Stats are consistent (note: may differ, report if so)

### Test C2: Symbol Validation Coverage
Test these common problematic names:
```bash
python tools/content_mapper_advanced.py --validate-symbol "grimy_guam" --validate-type obj
python tools/content_mapper_advanced.py --validate-symbol "grimy_guam_leaf" --validate-type obj
python tools/content_mapper_advanced.py --validate-symbol "coins" --validate-type obj
python tools/content_mapper_advanced.py --validate-symbol "shop_keeper" --validate-type npc
```
**Verify:**
- [ ] Correct names are valid
- [ ] Wrong names (like grimy_guam_leaf) are caught
- [ ] Suggestions are helpful

### Test C3: Performance Test
```bash
time python tools/content_mapper_advanced.py --search-npc "a" --limit 100
```
**Verify:**
- [ ] Runs in reasonable time (< 5 seconds)
- [ ] Returns 100 results

---

## Report Format

For each test, report:
```
TEST: [Test Name]
STATUS: [PASS / FAIL / PARTIAL]
COMMAND: [Command run]
EXPECTED: [What should happen]
ACTUAL: [What actually happened]
ISSUES: [Any problems found]
NOTES: [Additional observations]
```

## Summary Section

At the end, provide:
1. **Overall Status:** How many tests passed/failed
2. **Critical Issues:** Any bugs that must be fixed
3. **Minor Issues:** Improvements that would be nice
4. **Recommendations:** Suggestions for tool enhancement

## Success Criteria

- [ ] 90%+ tests pass
- [ ] No critical bugs
- [ ] Tools are usable for development
- [ ] Output is clear and helpful

---

## Notes

- Tools use cache from: `rsmod/.data/cache/enriched/`
- Symbols from: `rsmod/.data/symbols/`
- Cache is Rev 233 (September 2025)
- Focus on accuracy over speed

Run all tests and report comprehensive findings!

