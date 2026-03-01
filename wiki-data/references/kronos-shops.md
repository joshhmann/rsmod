# Kronos Shop Implementation Reference

This document extracts shop implementation patterns from the Kronos-184 codebase for RSMod v2 translation.

## Table of Contents

1. [Core Architecture](#core-architecture)
2. [Shop Data Model](#shop-data-model)
3. [Currency System](#currency-system)
4. [Price Calculations](#price-calculations)
5. [Restocking Logic](#restocking-logic)
6. [Inventory Management](#inventory-management)
7. [Interface Handling](#interface-handling)
8. [Shop Types](#shop-types)
9. [YAML Configuration](#yaml-configuration)
10. [RSMod v2 Translation Notes](#rsmod-v2-translation-notes)

---

## Core Architecture

### Key Classes

| Class | Purpose |
|-------|---------|
| `Shop.java` | Core shop logic, buy/sell operations |
| `ShopItem.java` | Item representation with price, requirements |
| `ShopManager.java` | Shop registry, tick loop, UI handlers |
| `ShopItemContainer.java` | Inventory container for shop stock |
| `ShopContainerListener.java` | Updates UI for viewing players |
| `CurrencyHandler.java` | Abstract currency operations |
| `ItemCurrencyHandler.java` | Item-based currency (coins, tokkul, etc.) |
| `RestockRules.java` | Restocking configuration |

### Shop Lifecycle

```
ShopLoader (YAML) → ShopManager.registerShop() → shop.init() → shop.populate()
                                    ↓
                            EventWorker (shopTick loop)
                                    ↓
                            Restock every N ticks
```

---

## Shop Data Model

### Shop.java Key Fields

```java
public class Shop {
    public String identifier;          // UUID string
    public String title;               // Shop name
    public Currency currency;          // Enum or custom handler
    public CurrencyHandler currencyHandler;
    public boolean generalStore;       // Accepts any tradeable item
    public boolean canSellToStore;     // Player can sell to this shop
    public RestockRules restockRules;  // Restock timing config
    public List<ShopItem> defaultStock;// Initial stock
    public ShopItemContainer shopItems;// Current stock container
    public boolean accessibleByIronMan;
    public List<ShopContainerListener> viewingPlayers;
}
```

### ShopItem.java Key Fields

```java
public class ShopItem extends Item {
    public int price;                  // Override price (0 = use item value)
    public boolean defaultStockItem;   // True if in defaultStock
    public PlaceHolderRule placeholderRule;
    public int placeholderId;          // Item to show when empty
    public List<Achievement> requiredAchievements;
    public List<StatRequirement> requiredLevels;
    public List<Item> additionalItems; // Bundled items
    public RequirementCheckType requirementCheckType;
    public Consumer<Player> onBuy;     // Callback on purchase
}
```

### PlaceHolderRule Enum

```java
public enum PlaceHolderRule {
    NONE,                           // No placeholder
    SHOW_ON_EMPTY,                  // Show placeholder when stock 0
    SHOW_ON_REQUIREMENT_MISSING,    // Show when player lacks requirements
    SHOW_ON_REQUIREMENT_MET         // Show when player meets requirements
}
```

### RequirementCheckType Enum

```java
public enum RequirementCheckType {
    NONE,               // No requirements
    REQUIRED_TO_SEE,    // Must meet reqs to see item
    REQUIRED_TO_BUY,    // Must meet reqs to buy
    REQUIRED_TO_BUY_SELL // Must meet reqs for both
}
```

---

## Currency System

### Currency Enum (Item-Based Currencies)

```java
public enum Currency {
    COINS(new ItemCurrencyHandler(ItemID.COINS_995)),
    BLOOD_MONEY(new ItemCurrencyHandler(ItemID.BLOOD_MONEY)),
    TOKKUL(new ItemCurrencyHandler(ItemID.TOKKUL)),
    MARKS_OF_GRACE(new ItemCurrencyHandler(ItemID.MARK_OF_GRACE)),
    GOLDEN_NUGGETS(new ItemCurrencyHandler(ItemID.GOLDEN_NUGGET)),
    WARRIOR_GUILD_TOKEN(new ItemCurrencyHandler(ItemID.WARRIOR_GUILD_TOKEN)),
    // ... etc
}
```

### CurrencyHandler Abstract Class

```java
public abstract class CurrencyHandler {
    protected String name, pluralName;

    // Returns current amount player has
    public abstract int getCurrencyCount(Player player);

    // Returns amount that can be bought (considering price + inv space)
    public int getPossibleBuyAmount(Player player, ShopItem shopItem);

    // Returns amount actually removed (0 if insufficient)
    public abstract int removeCurrency(Player player, int amount);

    // Returns amount added
    public abstract int addCurrency(Player player, int amount);
}
```

### ItemCurrencyHandler (For Item-Based Currencies)

```java
public class ItemCurrencyHandler extends CurrencyHandler {
    private int currencyItemId;

    @Override
    public int getCurrencyCount(Player player) {
        return player.getInventory().count(currencyItemId);
    }

    @Override
    public int removeCurrency(Player player, int amount) {
        if(amount > player.getInventory().count(currencyItemId))
            return 0;
        return player.getInventory().remove(currencyItemId, amount);
    }

    @Override
    public int addCurrency(Player player, int amount) {
        return player.getInventory().add(currencyItemId, amount);
    }
}
```

### Point-Based Currencies (Custom Implementation)

```java
TASK_POINTS(new CurrencyHandler("daily task points") {
    @Override
    public int getCurrencyCount(Player player) {
        return player.dailyTaskPoints;
    }

    @Override
    public int removeCurrency(Player player, int amount) {
        if(amount > player.dailyTaskPoints) return 0;
        player.dailyTaskPoints -= amount;
        return amount;
    }

    @Override
    public int addCurrency(Player player, int amount) {
        player.dailyTaskPoints += amount;
        return amount;
    }
})
```

---

## Price Calculations

### Buy Price (Shop sells to player)

```java
public int getSellPrice(ShopItem itemForSlot) {
    if(itemForSlot != null) {
        ItemDef itemDef = itemForSlot.getDef().isNote()
            ? itemForSlot.getDef().fromNote()
            : itemForSlot.getDef();

        // If custom price set, use it
        if(itemForSlot.getPrice() > 0) {
            return itemForSlot.getPrice();
        }

        // Otherwise use high alch value
        return itemDef.highAlchValue;
    }
    return Integer.MAX_VALUE;
}
```

### Sell Price (Shop buys from player)

```java
public int getBuyPrice(Item itemForSlot) {
    ItemDef itemDef = itemForSlot.getDef().isNote()
        ? itemForSlot.getDef().fromNote()
        : itemForSlot.getDef();

    // Cannot sell untradeable, free items, or currency
    if(!itemDef.tradeable || itemDef.free || itemDef.isCurrency()) {
        return -1;
    }

    // General store: pays low alch value (minimum 1)
    if(generalStore) {
        return Math.max(itemDef.lowAlchValue, 1);
    }

    // Cannot sell to stores that don't allow it
    if(!canSellToStore) {
        return -1;
    }

    // Find matching item in shop stock
    ShopItem matchingItem = shopItems.findItem(itemForSlot.getId(), true);
    if(matchingItem == null && !generalStore) {
        return -1;
    }

    // If item has custom price, sell for 75% of that (minimum 1)
    if(matchingItem != null && matchingItem.getPrice() > 0) {
        return Math.max((int)(matchingItem.getPrice() * 0.75), 1);
    }

    return 1;
}
```

### Price Calculation Flow

```
Player sells to shop:
1. Check item is tradeable and not free/currency
2. If general store: use lowAlchValue
3. If specialty shop: find matching stock item
4. If has custom price: return price * 0.75
5. Otherwise return 1

Player buys from shop:
1. Check requirements met
2. If shopItem.price > 0: use custom price
3. Otherwise: use itemDef.highAlchValue
4. Check player has enough currency
5. Check player has inventory space
```

---

## Restocking Logic

### RestockRules Class

```java
@Data
public class RestockRules {
    public static final RestockRules generateDefault() {
        return new RestockRules(52, 1); // OSRS default: 52 ticks, 1 per tick
    }

    public int restockTicks;    // Ticks between restock
    public int restockPerTick;  // Amount to restock per tick
}
```

### Shop Tick Implementation

```java
public static void shopTick(Event event, Shop shop) {
    while(true) {
        RestockRules restockRules = shop.restockRules;
        event.delay(restockRules.restockTicks);

        shop.shopItems.forEach(shopItem -> {
            ShopItem original = shopItem.getSlot() >= shop.defaultStock.size()
                ? null
                : shop.defaultStock.get(shopItem.getSlot());

            if(original != null) {
                // Default stock item: restock toward default amount
                int difference = Integer.compare(shopItem.getAmount(), original.getAmount());
                if(difference != 0) {
                    shopItem.setAmount(shopItem.getAmount() - difference);
                    shopItem.update();
                }
            } else {
                // Non-default item: slowly remove (decays)
                shopItem.setAmount(shopItem.getAmount() - 1);
                shopItem.update();
                if(!shopItem.defaultStockItem && shopItem.getAmount() <= 0) {
                    shopItem.remove();
                }
            }
        });

        shop.sendUpdates();

        if(shop.onTick != null) {
            shop.onTick.accept(shop);
        }
    }
}
```

### Restock Behavior Summary

| Scenario | Behavior |
|----------|----------|
| Item below default stock | Restock up toward default amount |
| Item above default stock | Decrease toward default amount |
| Non-default item (player-sold) | Decrease by 1 per tick until removed |

---

## Inventory Management

### ShopItemContainer

```java
public class ShopItemContainer extends ItemContainerG<ShopItem> {
    public static final int SHOP_MAX_CAPACITY = 40;

    // Add item with specific price
    public int add(int id, int amount, int price, Map<String, String> attributes) {
        // Handle stackable items
        // Handle non-stackable items (adds to separate slots)
        // Returns amount actually added
    }

    // Send full container to player
    public void send(Player player, int interfaceHash, int containerId) {
        player.getPacketSender().sendShopItems(interfaceHash, containerId, items, items.length);
    }

    // Send only updated slots
    public void sendUpdates(Player player, int interfaceHash, int containerId) {
        player.getPacketSender().updateItems(interfaceHash, containerId, items, updatedSlots, items.length);
    }
}
```

### Inventory Constraints

```java
// General store selling constraints:
- Shop must have free slots for new items
- Max sell amount = Integer.MAX_VALUE - currentStock
- Must handle noted items (convert to unnoted)

// Specialty shop selling constraints:
- Item must exist in default stock
- Max sell limited by remaining capacity
```

---

## Interface Handling

### Shop Interface IDs

| Interface | Purpose |
|-----------|---------|
| 300 | Main shop interface |
| 301 | Player inventory interface (when shop open) |

### UI Registration

```java
public static void registerUI() {
    // Player inventory (interface 301)
    InterfaceHandler.register(301, h -> {
        h.actions[0] = (DefaultAction) (player, option, slot, itemId) -> {
            Shop shop = player.viewingShop;
            if(shop == null) return;

            int buyAmt = 0;
            Item playerItem = player.getInventory().getSafe(slot);
            if(playerItem == null) return;

            switch(option) {
                case PRICE_CHECK: // 1
                    int sellToShopPrice = shop.getBuyPrice(playerItem);
                    if(sellToShopPrice < 0) {
                        player.sendMessage(CANNOT_SELL_TO_SHOP);
                    } else {
                        player.sendMessage("Shop will buy " + playerItem.getDef().name +
                            " for " + sellToShopPrice + " " + shop.currencyHandler.name());
                    }
                    return;
                case ONE: buyAmt = 1; break;
                case FIVE: buyAmt = 5; break;
                case TEN: buyAmt = 10; break;
                case FIFTY: buyAmt = 50; break;
                case EXAMINE: playerItem.examine(player); break;
            }

            if(buyAmt > 0) {
                shop.sellToShop(player, new Item(playerItem.getId(), buyAmt));
            }
        };
    });

    // Shop stock (interface 300)
    InterfaceHandler.register(300, h -> {
        h.actions[16] = (DefaultAction) (player, option, slot, itemId) -> {
            Shop shop = (Shop) player.viewingShop;
            if(shop == null) return;

            ShopItem itemForSlot = shop.shopItems.getSafe(slot);
            if(itemForSlot == null) return;

            switch(option) {
                case PRICE_CHECK:
                    player.sendMessage("Shop sells " + itemForSlot.getDef().name +
                        " for " + shop.getSellPrice(itemForSlot) + " " + shop.currencyHandler.name());
                    if(!itemForSlot.hasRequirements(player)) {
                        itemForSlot.printRequirements(player);
                    }
                    return;
                case ONE: buyAmt = 1; break;
                case FIVE: buyAmt = 5; break;
                case TEN: buyAmt = 10; break;
                case FIFTY: buyAmt = 50; break;
                case EXAMINE: itemForSlot.examine(player); break;
            }

            if(buyAmt > 0) {
                shop.buyFromShop(player, new Item(itemForSlot.getId(), buyAmt));
            }
        };
    });
}
```

### Option Constants

```java
private static final int PRICE_CHECK = 1;
private static final int ONE = 2;
private static final int FIVE = 3;
private static final int TEN = 4;
private static final int FIFTY = 5;
private static final int EXAMINE = 6;
```

---

## Shop Types

### 1. General Store

**Characteristics:**
- `generalStore: true`
- `canSellToStore: true`
- Accepts any tradeable item from players
- Sells at low alch value, buys at 75% of custom price

**Example:**
```yaml
identifier: "e522b145-fbaa-4746-8d71-6ad9808d2338"
title: "General Store"
currency: "COINS"
generalStore: true
canSellToStore: true
restockRules:
  restockTicks: 52
  restockPerTick: 1
defaultStock:
  - id: 1931  # Pot
    amount: 5
  - id: 1935  # Jug
    amount: 2
```

### 2. Weapon/Armor Shops (Specialty)

**Characteristics:**
- `generalStore: false`
- `canSellToStore: false` (usually)
- Only sells specific items
- Custom prices per item

**Example:**
```yaml
title: "Melee Shop"
currency: "COINS"
generalStore: false
canSellToStore: false
restockRules:
  restockTicks: 6
  restockPerTick: 10
defaultStock:
  - id: 1321  # Bronze scimitar
    amount: 100
  - id: 4587  # Dragon scimitar
    amount: 100
```

### 3. Rune Shops

**Characteristics:**
- Stackable items (runes)
- Usually have placeholder display
- Custom pricing per rune type

**Example:**
```yaml
title: "Battle Runes"
currency: "COINS"
defaultStock:
  - id: 554  # Fire rune
    amount: 1000
    placeholderId: 0
  - id: 555  # Water rune
    amount: 1000
    placeholderId: 0
```

### 4. Alternative Currency Shops

**Characteristics:**
- Uses non-coin currency (Tokkul, Marks of Grace, etc.)
- Cannot sell items back (usually)

**Example (TzHaar Rune Store):**
```yaml
title: "TzHaar-Mej-Roh's Rune Store"
currency: "TOKKUL"
canSellToStore: false
defaultStock:
  - id: 554
    amount: 1000
    price: 6
    placeholderId: 0
  - id: 560  # Death rune
    amount: 1000
    price: 252
```

**Example (Grace's Graceful):**
```yaml
title: "Grace's Graceful Clothing"
currency: "MARKS_OF_GRACE"
canSellToStore: false
defaultStock:
  - id: 11850  # Graceful hood
    amount: 1000
    price: 35
```

### 5. Point Shops

**Characteristics:**
- Uses account points (PVM points, Slayer points, etc.)
- Custom CurrencyHandler implementation
- Points stored in player attributes

---

## YAML Configuration

### Shop YAML Structure

```yaml
---
identifier: "unique-uuid-string"
title: "Shop Name"
currency: "COINS"  # Or other Currency enum value
accessibleByIronMan: true
canSellToStore: true
generalStore: false
restockRules:
  restockTicks: 52
  restockPerTick: 1
defaultStock:
  - id: 1321           # Item ID
    amount: 100        # Stock amount
    price: 0           # Custom price (0 = use item value)
    placeholderId: 0   # Show placeholder when empty (-1 for default)
    placeholderRule: "SHOW_ON_EMPTY"  # NONE, SHOW_ON_EMPTY, SHOW_ON_REQUIREMENT_MISSING, SHOW_ON_REQUIREMENT_MET
    requiredAchievements: []  # List of achievement names
    requiredLevels: []        # List of {statType, requiredLevel}
    additionalItems: []       # Bundled items given on purchase
```

### ShopItem YAML Options

| Field | Type | Description |
|-------|------|-------------|
| `id` | int | Item ID from cache |
| `amount` | int | Stock amount |
| `price` | int | Custom price (0 = use high alch) |
| `placeholderId` | int | Item ID to show when empty (0 = grayed out, -1 = default) |
| `placeholderRule` | enum | When to show placeholder |
| `requirementCheckType` | enum | NONE, REQUIRED_TO_SEE, REQUIRED_TO_BUY, REQUIRED_TO_BUY_SELL |
| `requiredAchievements` | list | Achievement names required |
| `requiredLevels` | list | {statType, requiredLevel} pairs |
| `additionalItems` | list | Extra items given on purchase |

---

## RSMod v2 Translation Notes

### Key Differences from Kronos

| Aspect | Kronos (Java) | RSMod v2 (Kotlin) |
|--------|---------------|-------------------|
| Registration | ShopManager.registerShop() | content/mechanics/shops/ plugin |
| Currency | Enum + CurrencyHandler | Interface-based currency system |
| Restocking | EventWorker tick loop | Controller or scheduled task |
| UI | InterfaceHandler | onInterfaceOpen / packet handlers |
| Data | YAML files | Kotlin DSL or config files |

### Shop Implementation Pattern for RSMod v2

```kotlin
// 1. Define shop data structure
object LumbridgeGeneralStore : Shop(
    name = "General Store",
    currency = Currency.COINS,
    isGeneralStore = true,
    canSellToStore = true
) {
    override val defaultStock = listOf(
        ShopItem(objs.pot_empty, amount = 5),
        ShopItem(objs.jug_empty, amount = 2),
        // ...
    )
}

// 2. Create NPC handler
onOpNpc1(npc = npcs.shopkeeper) {
    player.openShop(LumbridgeGeneralStore)
}

// 3. Implement currency handler
interface CurrencyHandler {
    fun getAmount(player: Player): Int
    fun remove(player: Player, amount: Int): Int
    fun add(player: Player, amount: Int): Int
}

// 4. Shop container
class ShopContainer(val shop: Shop) : ItemContainer() {
    // Handle restocking
    // Handle updates to viewers
}
```

### Restocking Translation

```kotlin
// Kronos: EventWorker with delay
// RSMod v2: Use coroutine or timer

fun startRestocking(shop: Shop) {
    world.schedule(shop.restockTicks) {
        shop.restock()
        startRestocking(shop) // Reschedule
    }
}

// Or use a controller
class ShopRestockController : Controller() {
    override fun onTick() {
        if (tick % shop.restockRules.restockTicks == 0) {
            shop.restock()
        }
    }
}
```

### Price Calculation Translation

```kotlin
// Buy price (shop sells to player)
fun getSellPrice(item: ShopItem): Int {
    return when {
        item.customPrice > 0 -> item.customPrice
        else -> item.def.highAlchValue
    }
}

// Sell price (shop buys from player)
fun getBuyPrice(item: Item): Int {
    if (!item.def.tradeable || item.def.isCurrency) return -1

    if (isGeneralStore) {
        return max(item.def.lowAlchValue, 1)
    }

    val matchingStock = stock.find { it.id == item.id }
    return when {
        matchingStock == null -> -1
        matchingStock.customPrice > 0 -> max(matchingStock.customPrice * 0.75, 1).toInt()
        else -> 1
    }
}
```

### Interface Handling Translation

```kotlin
// Shop stock interface
onIfButton(ifId = 300, component = 16) {
    val option = when (opIndex) {
        1 -> ShopOption.VALUE
        2 -> ShopOption.BUY_1
        3 -> ShopOption.BUY_5
        4 -> ShopOption.BUY_10
        5 -> ShopOption.BUY_50
        else -> return@onIfButton
    }
    player.handleShopOption(shop, slot, option)
}

// Player inventory interface
onIfButton(ifId = 301, component = 0) {
    val option = when (opIndex) {
        1 -> ShopOption.VALUE
        2 -> ShopOption.SELL_1
        3 -> ShopOption.SELL_5
        4 -> ShopOption.SELL_10
        5 -> ShopOption.SELL_50
        else -> return@onIfButton
    }
    player.handleShopOption(shop, slot, option)
}
```

### File Structure for RSMod v2

```
rsmod/content/mechanics/shops/
├── Shops.kt                    # Shop base class and registry
├── ShopItem.kt                 # Shop item data class
├── CurrencyHandlers.kt         # Currency implementations
├── ShopPrices.kt               # Price calculation logic
├── ShopRestocking.kt           # Restocking logic
├── ShopInterface.kt            # Interface handlers
└── shops/                      # Individual shop definitions
    ├── GeneralStores.kt
    ├── WeaponShops.kt
    ├── RuneShops.kt
    └── SpecialtyShops.kt
```

---

## References

### Kronos Source Files

| File | Path |
|------|------|
| Shop.java | `io.ruin.model.shop.Shop` |
| ShopItem.java | `io.ruin.model.shop.ShopItem` |
| ShopManager.java | `io.ruin.model.shop.ShopManager` |
| Currency.java | `io.ruin.model.shop.Currency` |
| CurrencyHandler.java | `io.ruin.model.shop.CurrencyHandler` |
| ItemCurrencyHandler.java | `io.ruin.model.shop.ItemCurrencyHandler` |
| RestockRules.java | `io.ruin.model.shop.RestockRules` |
| ShopItemContainer.java | `io.ruin.model.item.containers.ShopItemContainer` |
| ShopContainerListener.java | `io.ruin.model.shop.ShopContainerListener` |
| ShopLoader.java | `io.ruin.data.yaml.impl.ShopLoader` |

### Example Shop Data Files

- `data/shops/General_Store.yaml`
- `data/shops/Melee_Shop.yaml`
- `data/shops/Magic_Shop.yaml`
- `data/shops/Battle_Runes.yaml`
- `data/shops/TzHaar-Mej-Roh's_Rune_Store.yaml`
- `data/shops/Grace's_Graceful_Clothing.yaml`
- `data/shops/Farmer's_Shop.yaml`
- `data/shops/Fishing_Guild_Shop.yaml`

---

*Document generated from Kronos-184-Fixed codebase analysis*
