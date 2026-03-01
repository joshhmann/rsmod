/**
 * bots/test_death_drops.ts — Player death item drops test
 */

async function runTest() {
  console.log("── Starting Death Drops Test ──");

  // 1. Setup: Clear inv and add test items
  sdk.sendSay("::invclear");
  await sdk.waitTicks(1);
  sdk.sendSay("::invadd abyssal_whip 1");
  sdk.sendSay("::invadd dragon_scimitar 1");
  sdk.sendSay("::invadd dragon_dagger 1");
  sdk.sendSay("::invadd dragon_platebody 1");
  sdk.sendSay("::invadd dragon_platelegs 1");
  sdk.sendSay("::invadd shark 10");
  await sdk.waitTicks(2);

  const invBefore = sdk.getInventory();
  console.log("Inventory before death:", invBefore.map(i => `${i.name} (${i.qty})`).join(", "));

  // 2. Equip some items
  await bot.equipItem("Abyssal whip");
  await bot.equipItem("Dragon platebody");
  await bot.equipItem("Dragon platelegs");
  await sdk.waitTicks(2);

  // 3. Activate Protect Item prayer
  console.log("Activating Protect Item prayer...");
  await bot.activatePrayer("Protect Item");
  await sdk.waitTicks(2);

  if (!sdk.isPrayerActive("Protect Item")) {
    console.log("❌ Failed to activate Protect Item prayer. (Maybe level too low? Using ::master)");
    sdk.sendSay("::master");
    await sdk.waitTicks(2);
    await bot.activatePrayer("Protect Item");
    await sdk.waitTicks(2);
  }

  const prayerActive = sdk.isPrayerActive("Protect Item");
  console.log("Protect Item active:", prayerActive);

  // 4. Record position for later check
  const p = sdk.getState().player;
  const deathX = p.x;
  const deathZ = p.z;
  console.log(`Death spot: (${deathX}, ${deathZ})`);

  // 5. Die!
  console.log("Triggering death...");
  sdk.sendSay("::die");
  await sdk.waitTicks(10); // Wait for death sequence and respawn

  // 6. Check inventory after respawn
  const invAfter = sdk.getInventory();
  console.log("Inventory after respawn:", invAfter.map(i => `${i.name} (${i.qty})`).join(", "));

  // In OSRS, with Protect Item and no skull, we keep 4 items.
  // Sorted by price: Abyssal whip, Dragon platebody, Dragon platelegs, Dragon scimitar.
  // Dragon dagger and sharks should be dropped.
  
  const keptWhip = invAfter.some(i => i.name?.includes("Abyssal whip"));
  const keptPlatebody = invAfter.some(i => i.name?.includes("Dragon platebody"));
  const keptPlatelegs = invAfter.some(i => i.name?.includes("Dragon platelegs"));
  const keptScimitar = invAfter.some(i => i.name?.includes("Dragon scimitar"));
  
  console.log("Kept Whip:", keptWhip);
  console.log("Kept Platebody:", keptPlatebody);
  console.log("Kept Platelegs:", keptPlatelegs);
  console.log("Kept Scimitar:", keptScimitar);

  const passed = keptWhip && keptPlatebody && keptPlatelegs && keptScimitar;
  if (passed) {
    console.log("✅ Kept items check passed!");
  } else {
    console.log("❌ Kept items check failed!");
  }

  // 7. Go back to death spot and check for drops
  console.log("Walking back to death spot...");
  await bot.walkTo(deathX, deathZ);
  await sdk.waitTicks(2);

  // Scan ground items
  const groundItems = await sdk.scanGroundItems(5);
  console.log("Ground items at death spot:", groundItems.map(i => `${i.name} (${i.qty})`).join(", "));

  const lostDagger = groundItems.some(i => i.name?.includes("Dragon dagger"));
  const lostSharks = groundItems.some(i => i.name?.includes("Shark"));

  console.log("Found dropped Dagger:", lostDagger);
  console.log("Found dropped Sharks:", lostSharks);

  if (lostDagger && lostSharks) {
    console.log("✅ Dropped items check passed!");
  } else {
    console.log("❌ Dropped items check failed!");
  }

  return { 
    success: passed && lostDagger && lostSharks,
    kept: invAfter.map(i => i.name),
    dropped: groundItems.map(i => i.name)
  };
}

await runTest();
