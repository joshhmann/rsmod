# Tool Verification Task for Codex

## Objective
Verify that `tools/content_mapper.py` and `tools/content_mapper_advanced.py` work correctly and provide accurate results from the Rev 233 cache.

## Background
We discovered our cache is Rev 233 from September 2025 (not 2024). The tools map cache symbols to help developers use correct names instead of guessing from wiki.

## Verification Steps

### Step 1: Test Basic Tool
```bash
cd Z:\Projects\OSRS-PS-DEV
python tools/content_mapper.py --stats
```
Expected: Shows ~31k items, ~14k NPCs, ~57k locations

### Step 2: Test NPC Search
```bash
python tools/content_mapper.py --npc "wilderness_hill_giant"
```
Expected: Shows NPC info, Kotlin reference, combat stats template

### Step 3: Test Item Search
```bash
python tools/content_mapper.py --item "dragon_scimitar"
```
Expected: Shows item ID 4587, Kotlin reference code

### Step 4: Test Advanced Tool - NPC Search
```bash
python tools/content_mapper_advanced.py --search-npc "giant" --limit 5
```
Expected: Lists 5 NPCs with "giant" in name

### Step 5: Test Validation - Correct Symbol
```bash
python tools/content_mapper_advanced.py --validate-symbol "dragon_scimitar" --validate-type obj
```
Expected: [OK] Symbol valid, ID 4587

### Step 6: Test Validation - Wrong Symbol
```bash
python tools/content_mapper_advanced.py --validate-symbol "granite_cannonball_233" --validate-type obj
```
Expected: [ERROR] with suggestion "granite_cannonball"

### Step 7: Test Batch Generation
```bash
python tools/content_mapper_advanced.py --batch-npcs "wilderness_hill_giant" --output-dir test_output/
```
Expected: Creates test_output/wilderness_hill_giant.txt with complete implementation

## Success Criteria
- [ ] All commands run without errors
- [ ] Output format is clear and usable
- [ ] Symbol validation catches wrong names
- [ ] Suggestions are accurate and helpful
- [ ] Generated code follows RSMod patterns

## Report Issues
If any step fails, document:
1. The command that failed
2. The error message
3. Expected vs actual output

## Files to Check
- tools/content_mapper.py
- tools/content_mapper_advanced.py
- Generated output files in test_output/

