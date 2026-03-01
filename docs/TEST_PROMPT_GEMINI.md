# TEST PROMPT: Content Mapper Tool Validation

## Agent: Gemini
## Task: Validate Content Mapper Tools for Build Integration
## Priority: HIGH

---

## Context

Kimi created content mapper tools to solve symbol naming issues. We need to validate they work correctly before integrating into the build system.

**Tools:**
- `tools/content_mapper.py` - Basic lookup
- `tools/content_mapper_advanced.py` - Advanced features with validation

**Cache:** Rev 233 (September 2025)
**Location:** `rsmod/.data/cache/enriched/`

---

## Your Task

Perform technical validation of the tools. Focus on:
1. **Correctness** - Do they return accurate data?
2. **Integration** - Can they be integrated into build/CI?
3. **Edge Cases** - How do they handle errors?
4. **Performance** - Are they fast enough for build-time use?

---

## Phase 1: Correctness Testing

### P1.1: Data Accuracy Check

Verify these specific lookups return correct IDs:

```bash
cd Z:\Projects\OSRS-PS-DEV

# Test 1: Dragon Scimitar (well-known item)
python tools/content_mapper.py --item "dragon_scimitar"
# Expected ID: 4587

# Test 2: Hill Giant (common NPC)
python tools/content_mapper.py --npc "wilderness_hill_giant"
# Expected ID: 13502

# Test 3: Furnace (common location)
python tools/content_mapper.py --loc "furnace"
# Expected: Multiple furnace locations
```

**Report:**
- [ ] IDs match known values
- [ ] Symbol names are correct
- [ ] No data corruption

### P1.2: Symbol Validation

Test the validation feature:

```bash
# Valid symbol
python tools/content_mapper_advanced.py --validate-symbol "dragon_scimitar" --validate-type obj

# Invalid symbol (should fail gracefully)
python tools/content_mapper_advanced.py --validate-symbol "this_does_not_exist" --validate-type obj

# Wrong name (should suggest correct)
python tools/content_mapper_advanced.py --validate-symbol "granite_cannonball_233" --validate-type obj
```

**Report:**
- [ ] Valid symbols accepted
- [ ] Invalid symbols rejected with clear message
- [ ] Suggestions are accurate

### P1.3: Search Functionality

Test search capabilities:

```bash
# Search for giants
python tools/content_mapper_advanced.py --search-npc "giant" --limit 20

# Search for rune items
python tools/content_mapper_advanced.py --search-item "rune" --limit 20
```

**Report:**
- [ ] Search returns relevant results
- [ ] Limit parameter works
- [ ] No duplicates
- [ ] Performance acceptable

---

## Phase 2: Build Integration Testing

### P2.1: Command-Line Interface

Check if tools can be called from build scripts:

```bash
# Test return codes
cd Z:\Projects\OSRS-PS-DEV
python tools/content_mapper_advanced.py --validate-symbol "dragon_scimitar" --validate-type obj
echo "Exit code: $?"

python tools/content_mapper_advanced.py --validate-symbol "invalid_symbol" --validate-type obj
echo "Exit code: $?"
```

**Report:**
- [ ] Return code 0 on success
- [ ] Return code non-zero on failure
- [ ] No interactive prompts
- [ ] Suitable for CI/CD

### P2.2: Output Formats

Check if output can be parsed:

```bash
# Check stats JSON
python tools/content_mapper.py --stats | python -m json.tool > /dev/null && echo "Valid JSON" || echo "Invalid JSON"

# Check if output has consistent format
python tools/content_mapper.py --npc "wilderness_hill_giant" | head -20
```

**Report:**
- [ ] Stats output is valid JSON
- [ ] Regular output is consistent
- [ ] No garbled characters
- [ ] UTF-8 encoding works

### P2.3: Performance for Build Use

Measure execution time:

```bash
# Single lookup
time python tools/content_mapper_advanced.py --validate-symbol "dragon_scimitar" --validate-type obj

# Batch validation (simulate 100 symbols)
time python tools/content_mapper_advanced.py --search-npc "a" --limit 100

# Full generation
time python tools/content_mapper_advanced.py --full-npc "wilderness_hill_giant"
```

**Report:**
- [ ] Single validation < 1 second
- [ ] Batch operations < 5 seconds
- [ ] Acceptable for build-time use

---

## Phase 3: Edge Cases & Error Handling

### P3.1: Missing Cache

Test behavior when cache is missing:

```bash
# Temporarily rename cache (BE CAREFUL - rename back after)
mv rsmod/.data/cache/enriched/main_file_cache.dat2 rsmod/.data/cache/enriched/main_file_cache.dat2.bak
python tools/content_mapper.py --stats
# Restore
mv rsmod/.data/cache/enriched/main_file_cache.dat2.bak rsmod/.data/cache/enriched/main_file_cache.dat2
```

**Report:**
- [ ] Clear error message
- [ ] No crash
- [ ] Helpful recovery suggestion

### P3.2: Invalid Arguments

Test error handling:

```bash
python tools/content_mapper.py --invalid-flag
python tools/content_mapper_advanced.py --validate-symbol "" --validate-type obj
python tools/content_mapper.py --npc "" 
```

**Report:**
- [ ] Graceful error handling
- [ ] Clear error messages
- [ ] Usage help provided

### P3.3: Unicode/Special Characters

```bash
python tools/content_mapper.py --npc "goblin"  # Normal
python tools/content_mapper_advanced.py --search-npc "_" --limit 5  # Underscore
```

**Report:**
- [ ] Special characters handled
- [ ] No encoding issues

---

## Phase 4: Integration Proposal

Based on testing, propose how to integrate into build:

### Questions to Answer:

1. **Should this run during build?**
   - If yes: Which gradle task?
   - If no: Why not?

2. **Should this run in CI/CD?**
   - GitHub Actions integration?
   - Pre-commit hook?

3. **What should happen on failure?**
   - Fail build?
   - Just warn?
   - Auto-fix suggestions?

4. **Performance impact?**
   - Acceptable for every build?
   - Only on release builds?
   - Incremental validation?

### Deliverable:

Provide a proposal document with:
- Test results summary
- Integration recommendations
- Sample gradle configuration (if applicable)
- Risk assessment

---

## Report Structure

```
# Gemini Test Report: Content Mapper Tools

## Executive Summary
- Overall Status: [PASS/NEEDS_WORK/FAIL]
- Recommendation: [INTEGRATE/DELAY/REJECT]

## Phase 1: Correctness
[Detailed results per test]

## Phase 2: Build Integration
[Detailed results per test]

## Phase 3: Edge Cases
[Detailed results per test]

## Phase 4: Integration Proposal
[Your recommendations]

## Appendices
- Sample outputs
- Performance benchmarks
- Known issues
```

---

## Success Criteria

- [ ] Tools return accurate data
- [ ] No crashes or hangs
- [ ] Exit codes are correct
- [ ] Performance is acceptable
- [ ] Clear recommendation provided

Run all phases and provide comprehensive technical report!

