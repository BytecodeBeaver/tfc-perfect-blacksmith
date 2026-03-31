Project: TFC PerfectBlacksmith
Summary:
A Minecraft Forge mod (target: Minecraft 1.20.1) implementing enhancements for anvils and blacksmithing within TerraFirmaCraft (TFC). Primary language: Java (target JDK 17). Uses Forge + Java Bytecode manipulation for accessing mod internal features. Should work fully client-side, without need for a mod to be installed on the server.

Important versions:
- Minecraft: 1.20.1
- Forge: (see build.gradle)
- TerraFirmaCraft (TFC) integration: uses TFC capabilities and recipes
- Java: 17

Build & run:
- Use the Gradle wrapper in the repo root.
- Common commands:
    - ./gradlew build
    - ./gradlew runClient
    - ./gradlew runServer
- Built mod jar ends up in build/libs or build/libs (see build.gradle and build folder)

Key files & entry points:
- `src/main/java/.../TfcPerfectBlacksmith.java` — main mod class and MODID definition.
- `src/main/java/com/bytecodebeaver/tfcperfectblacksmith/AnvilEventHandler.java` — listens to anvil open/interact events and computes/display optimal forge steps (uses TFC forging capability and `AnvilWorkStepSolver`).
- `AnvilWorkStepSolver` — solver logic for optimal forge steps (see same package).
- Resources: `src/main/resources` (mod metadata, assets)

Project constraints & conventions:
- Do not use server-only APIs or classes in the mod code.
- All code should be client-side and compatible with TFC's client-server architecture.
- Follow Forge and TFC threading model: only access world or player network state on server threads (use ServerLevel, ServerPlayer).
- Avoid modifying TFC core classes directly; prefer capabilities and recipe APIs.
- Prefer using capabilities exposed by TFC (e.g., `ForgingCapability.get(stack)`) rather than direct NBT or bytecode injection access.
- Keep code Java-idiomatic: prefer `Deque` over `Queue` if you need both peek/remove semantics, prefer immutability for shared state when possible.

Testing & QA:
- No automated integration tests included; use runClient/runServer to test gameplay interactions.
- Unit tests: add JUnit 5 for pure logic classes (e.g., `AnvilWorkStepSolver`) where possible, mock Minecraft/Forge APIs with Mockito or use small wrapper adapters.

Notes for Copilot:
- Always prefer safe, minimal changes.
- When adding imports or changing Gradle settings, provide a short explanation and exact file paths.
- Prefer small, single-purpose diffs and include test suggestions.