# Lumbridge Castle Bank — Coverage Verification

> Generated: 2026-06-19
> Source: `tools:placed-loc-exporter` (authoritative OpenRS2 name resolution)
> Banking system: `content/generic/generic-locs/banks/`
> Bank interface: `content/interfaces/bank/`

## Coverage Summary

Banking in Lumbridge Castle is fully handled by the existing generic banking system.
**No Lumbridge-specific handlers are needed.** All placed bank booths and deposit
boxes are already registered in `BankLocs`/`BankConfigs`.

## Verified Bank Objects

All bank-related objects are on **level 2** (2nd floor) of Lumbridge Castle, not
the basement. Coordinates verified against placed-loc export.

### Bank Booths

| Symbol | Loc ID | Location | Bound? | Handler |
|--------|--------|----------|--------|---------|
| `aide_bankbooth` | 18491 | (3208, 3221, 2) | ✅ `content.bank_booth` | `BankBooth.kt` |
| `aide_bankbooth_multi` | 27291 | (3209, 3221, 2) | ✅ `content.bank_booth` | `BankBooth.kt` |
| `aide_bankbooth_closed` | 18492 | (3207, 3221, 2) | ❌ decorative only | Not bound (unused booth) |
| `aide_bankbooth_closed` | 18492 | (3210, 3221, 2) | ❌ decorative only | Not bound (unused booth) |

### Deposit Box

| Symbol | Loc ID | Location | Bound? | Handler |
|--------|--------|----------|--------|---------|
| `bank_deposit_box` | 10529 | (3210, 3217, 2) | ✅ `content.bank_deposit_box` | `BankDepositBox.kt` |

### Other Bank-Related Locs (decorative/documented)

| Symbol | Loc ID | Location | Notes |
|--------|--------|----------|-------|
| `banktable` | 590 | (3207, 3215, 2) | Decorative table |
| `tutor_bank_icon` | 33151 | (3206, 3221, 2) | Tutorial map icon |
| `bank_store_icon` | 2738 | (3210, 3221, 2) | Bank store icon |

## Content Group Bindings (from `BankConfigs.kt`)

- `find("bankbooth")` → `contentGroup = content.bank_booth`
- `find("aide_bankbooth")` → `contentGroup = content.bank_booth`
- `find("aide_bankbooth_multi")` → `contentGroup = content.bank_booth`
- `find("bank_deposit_box")` → `contentGroup = content.bank_deposit_box`
- `find("bank_deposit_box_2")` → `contentGroup = content.bank_deposit_box`

## Generic Handlers

- **BankBooth**: Op1/Op2 → open bank interface, Op3 → collection box, Use-item → banknote
- **BankChest**: Op1 → open bank interface, Op2 → collection box, Use-item → banknote
- **BankDepositBox**: Op1/Use-item → open deposit box interface

## Verification Commands

```text
./gradlew :content:generic:generic-locs:compileKotlin -> BUILD SUCCESS
0 raw IDs in banking + lumbridge content
```
