# RS Mod Development Guide

Welcome to the RS Mod project! This document provides essential context for navigating and developing within this RuneScape game-server emulator.

## Project Overview

RS Mod is a highly modular, mechanically accurate Old School RuneScape (OSRS) game-server emulator written in **Kotlin** and targeting **Java 21+**. It prioritizes mechanical fidelity to the original game while maintaining a clean, modern codebase.

### Core Technologies
- **Language:** Kotlin (Java 21+)
- **Build System:** Gradle (Kotlin DSL)
- **Logging:** Logback
- **Client Recommendation:** [RSProx](https://github.com/blurite/rsprox) is the preferred client for development and debugging.

## Architecture

The project is organized into several key directories, each serving a specific purpose in the server's lifecycle:

- **`api/`**: High-level interfaces, definitions, and extensions. Provides the "language" for content developers.
- **`engine/`**: The core game engine. Handles low-level mechanics like movement, map management, entity processing, networking, and collision.
- **`content/`**: Implementation of game-specific features.
    - `skills/`: Individual skill logic (e.g., Woodcutting, Mining).
    - `quests/`: Quest logic and state management.
    - `mechanics/`: Core gameplay mechanics (e.g., combat, interactions).
    - `interfaces/`: UI behavior and bank logic.
- **`server/`**: The application's entry point, installation scripts, logging configuration, and global services.
- **`.data/`**: Local storage for the game cache, player saves, and symbols.

## Building and Running

### Initial Setup
Before running the server for the first time, you must perform the installation steps to download the required cache and generate security keys:

```bash
./gradlew install
```

### Running the Server
To start the game server:

```bash
./gradlew run
```

### Running Tests
The project includes an extensive test suite. You can run all tests using:

```bash
./gradlew test
```

### Additional Tasks
- `cleanInstall`: Removes partial or corrupted installation artifacts.
- `downloadCache`: Manually triggers the cache download and extraction.
- `packCache`: Packs the cache files.
- `generateRsa`: Generates new RSA keys for network security.

## Development Conventions

### Mechanical Accuracy
The primary goal is mechanical accuracy. When implementing features, ensure they align with OSRS behavior.

### Type-Safety & Magic Numbers
Avoid using magic numbers (literal IDs) for game objects, NPCs, or items. Use the project's type-safe configuration system (e.g., `ObjType`, `NpcType`) and symbol-based references.

### Game Logic Patterns
- **Randomness:** Use `GameRandom` functions for all game-related random generation to ensure consistency and testability.
- **Delays:** The `delay(n)` function (often referred to as `p_delay` in scripts) uses a direct tick-to-delay mapping. `delay(1)` results in a 1-tick delay.
- **Inventory Transactions:** Use `InvTransactions` for modifications to player inventories or banks. Note that these are not thread-safe in tests; mark such tests with `@Execution(ExecutionMode.SAME_THREAD)`.
- **Collision:** Teleportation and movement mechanics must interact correctly with the `CollisionFlagMap`.

### Documentation & Resources
- **`docs/quirks.md`**: Contains a list of known technical quirks, antipatterns, and specific design decisions. Read this before making architectural changes.
- **`LICENSE.md`**: The project is licensed under the ISC license.
