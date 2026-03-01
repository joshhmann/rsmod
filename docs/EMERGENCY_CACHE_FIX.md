# 🚨 EMERGENCY: Cache from WRONG YEAR!

## The Problem:
- ❌ Current cache: September **2025** (cache ID 2293)
- ✅ Needed cache: September **2024** (Varlamore Part 2)
- ❌ ALL symbols are wrong
- ❌ ALL item IDs are from 2025
- ❌ Foundation is completely wrong

---

## Solution Steps:

### Step 1: Find Correct OpenRS2 Cache

Visit: https://archive.openrs2.org/caches

Look for caches from **September 2024** (before September 2025).

**Target dates:**
- September 25, 2024 (Varlamore Part 2 release)
- Any date in September 2024
- Must be BEFORE September 10, 2025

### Step 2: Update Build.kt

File: `rsmod/api/core/src/main/kotlin/org/rsmod/api/core/Build.kt`

Change:
```kotlin
// WRONG (2025 cache)
public const val CACHE_URL: String =
    "https://archive.openrs2.org/caches/runescape/2293/disk.zip"

// CORRECT (replace XXXX with correct 2024 cache ID)
public const val CACHE_URL: String =
    "https://archive.openrs2.org/caches/runescape/XXXX/disk.zip"
```

### Step 3: Delete Wrong Cache

```bash
cd rsmod
rm -rf .data/cache/enriched
rm -rf .data/symbols
```

### Step 4: Download Correct Cache

```bash
cd rsmod
./gradlew downloadCache
```

### Step 5: Regenerate Symbols

Symbols will be auto-generated from new cache during build.

### Step 6: Rebuild Everything

```bash
./gradlew clean build
```

---

## After Cache Fix:

**EVERYTHING needs to be re-verified:**
- ❌ All item references
- ❌ All NPC references  
- ❌ All drop tables
- ❌ All quest items
- ❌ All symbols

**This is a COMPLETE reset.**

---

## Finding the Right Cache:

**Option A: Browse OpenRS2**
1. Go to https://archive.openrs2.org/caches
2. Look for September 2024 dates
3. Find cache ID (4-digit number)

**Option B: Try Earlier Cache IDs**
- Current: 2293 (Sept 2025)
- Try: 2292, 2291, 2290... (going back in time)
- Check each one until you find September 2024

**Option C: Ask Community**
- Rune-Server.org forums
- OSRS private server Discord servers
- Ask for "September 2024 OSRS cache"

---

## Can You Help?

**Right now we need:**
1. Someone to browse https://archive.openrs2.org/caches
2. Find a cache from September 2024
3. Get the cache ID number
4. Update Build.kt
5. Redownload

**Without the correct cache ID, we can't fix this!**

---

## Quick Test:

Once you find a candidate cache, verify:
1. Download it
2. Check item count (should be ~30,000 not 31,174)
3. Check for Varlamore items (should exist)
4. Check for 2025 items (should NOT exist)

**The cache must be from September 2024, not 2025!**

