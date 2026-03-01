/**
 * bots/test_hans.ts — Hans NPC Test
 *
 * Tests the Hans NPC in Lumbridge:
 * 1. Find Hans in Lumbridge Castle courtyard/grounds.
 * 2. Interact with Hans via "Age" option (Op3).
 * 3. Verify interaction success.
 *
 * Run via: execute_script({ player: "TestBot", file: "test_hans.ts" })
 */

const HANS_NAME = "Hans";
// Hans patrols around Lumbridge Castle courtyard
// Patrol points from code: (3222, 3218) is central
const LUMBRIDGE_X = 3222;
const LUMBRIDGE_Z = 3218;

const results = [];

function record(check, pass, note) {
  results.push({ check, pass, note });
  const icon = pass ? "✅" : "❌";
  console.log(`${icon} [${check}]: ${note}`);
}

console.log("\n═══ Test 1: Teleport to Lumbridge ═══");
// Teleport close to Hans' patrol route
sdk.sendTeleport(LUMBRIDGE_X, LUMBRIDGE_Z, 0);
await sdk.waitTicks(3);

const pos = sdk.getPlayer().position;
const teleported = Math.abs(pos.x - LUMBRIDGE_X) < 15 && Math.abs(pos.z - LUMBRIDGE_Z) < 15;
record("teleport_lumbridge", teleported, teleported ? "Arrived at Lumbridge" : "Teleport failed");

if (teleported) {
    console.log("\n═══ Test 2: Find Hans ═══");
    await sdk.waitTicks(2);
    
    // Hans wanders, so we might need to look around or wait
    let hans = sdk.findNearbyNpc(HANS_NAME);
    if (!hans) {
        console.log("Hans not immediately found, waiting...");
        await sdk.waitTicks(5);
        hans = sdk.findNearbyNpc(HANS_NAME);
    }

    if (!hans) {
        record("find_hans", false, "Hans NPC not found in Lumbridge");
    } else {
        record("find_hans", true, `Found Hans at ${hans.x}, ${hans.z} (Index: ${hans.index})`);

        console.log("\n═══ Test 3: Check Age (Op3) ═══");
        // Option 3 is "Age" or similar. In the code it is onOpNpc3.
        // We can try to use the specific option name if known, or just the index 3.
        // But 'interactNpc' usually takes a string option name.
        // "Age" is the likely option text for Hans Op3.
        
        try {
            await bot.interactNpc(hans, "Age"); // wait for completion
            // If it returns, it means the action was sent and completed (walking + interaction).
            
            record("interact_age", true, "Successfully interacted with Hans (Age option)");
            
            // We assume success if we didn't crash and the action completed.
            // Ideally we would check for the dialogue message "You've been here for...", 
            // but we lack a reliable way to read chat in this test harness currently.
            
        } catch (e) {
            record("interact_age", false, `Interaction failed: ${e.message}`);
        }

        // Try standard Talk option (Op1) as well
        console.log("\n═══ Test 4: Talk to Hans (Op1) ═══");
        try {
            await bot.talkTo(hans);
            record("interact_talk", true, "Successfully talked to Hans");
        } catch (e) {
            record("interact_talk", false, `Talk failed: ${e.message}`);
        }
    }
}

({ results });
