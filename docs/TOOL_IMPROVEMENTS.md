# Tool Improvements Based on Verification

## Issues Found & Fixes

### 1. HP Inconsistency
**Issue:** Basic tool shows HP=10, Advanced shows HP=35 for same NPC

**Fix:** Unify data source - both tools should use same default values or read from cache

### 2. Fuzzy Search
**Issue:** "hill giant" doesn't match "wilderness_hill_giant"

**Fix:** Add fuzzy matching to search

### 3. JSON Output
**Suggestion:** Add `--format json` for easier integration

### 4. Help Examples
**Suggestion:** Add more examples to `--help`

---

## Implementation

Let me create improved versions with these fixes:

