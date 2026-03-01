# Rev 233 Testing Quick Start

## 🚀 Run the Full Test Suite

```bash
# Run comprehensive tests on your character
bun bots/rev233_tester.ts Kimi

# Or specify different player
bun bots/rev233_tester.ts YourPlayerName
```

## 🎯 Run Individual Tests

### Test Specific Skills
```bash
# Woodcutting only
bun bots/test_woodcutting.ts Kimi

# Thieving only
bun bots/test_thieving_enhanced.ts Kimi

# Mining only
bun bots/test_mining.ts Kimi
```

### Manual Gamer Tests

**The "Does It Feel Right?" Check:**
1. Log in with RSProx
2. Walk around Lumbridge - do doors open?
3. Click a tree - does woodcutting work?
4. Try to cook shrimp - does it work?
5. Attack a goblin - does combat feel right?
6. Open the bank - does interface show?
7. Try to pickpocket - does stun work?

## 📊 Understanding Test Results

```
✅ PASS - Working correctly
❌ FAIL - Something broken (see details)
⏭️ SKIP - Couldn't test (missing prereqs)
```

**Common SKIP reasons:**
- No trees nearby → Move to forest
- No raw food → Go fishing first
- No pickaxe → Buy from shop

## 🐛 Reporting Bugs

When something fails:

1. **Note exact error message**
2. **Check console logs**
3. **Try in RSProx manually** - does it work there?
4. **Document:**
   - What you did
   - What you expected
   - What actually happened
   - Player state (/get_state)

## 🔥 Priority Test Order

### Phase 1: Core (Must Work)
- [ ] Walking
- [ ] Woodcutting
- [ ] Mining
- [ ] Combat (attack)
- [ ] Banking

### Phase 2: Skills
- [ ] Cooking
- [ ] Firemaking
- [ ] Thieving
- [ ] Smithing
- [ ] Fishing

### Phase 3: Advanced
- [ ] Prayer
- [ ] Equipment
- [ ] Shops
- [ ] Magic
- [ ] Ranged

## 💡 Pro Tester Tips

**Before testing:**
- Clear inventory (or know what's in it)
- Stand in Lumbridge
- Have basic tools (axe, pickaxe, etc.)

**During testing:**
- Watch the console output
- Check XP gains
- Verify animations play
- Test edge cases (full inv, etc.)

**After testing:**
- Document all FAILs
- Re-test after fixes
- Update test results

## 📋 Test Checklist Template

```
Date: ___
Tester: ___
Server: Rev 233

CRITICAL:
[ ] Server starts
[ ] Can login
[ ] Can walk
[ ] Can skill
[ ] Can bank

GATHERING:
[ ] Woodcutting - 25 XP
[ ] Mining - 17.5 XP
[ ] Fishing - 10 XP

ARTISAN:
[ ] Cooking - 30 XP
[ ] Firemaking - 40 XP
[ ] Smithing - 6.2 XP (bronze bar)

COMBAT:
[ ] Melee works
[ ] XP gained
[ ] NPC dies/respawns

THIEVING:
[ ] Pickpocket works
[ ] Stun works
[ ] XP gained

KNOWN BUGS:
1. ___
2. ___
3. ___
```

## 🎮 Let's Break Some Stuff!

Ready? Run the tester and let's find those bugs! 💥

```bash
bun bots/rev233_tester.ts Kimi
```

