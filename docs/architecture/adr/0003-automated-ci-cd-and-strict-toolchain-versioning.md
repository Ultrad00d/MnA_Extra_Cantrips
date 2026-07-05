# ADR 0003: Automated CI/CD and Strict Toolchain Versioning

## Status
Accepted

## Context
Minecraft mods rely heavily on massive, volatile external ecosystems (Minecraft base code, Forge APIs, Mana and Artifice, Curios, GeckoLib). A drift in local Java runtime environments, local build tool states, or dependency variations quickly ruins compilation stability and disrupts collaborative developer velocity.

## Decision
We will anchor all system compiles, dependencies, and publishing configurations within GitHub Actions using a locked container toolchain (JDK 17 and targeted Gradle wrappers). Testing and production packaging will happen exclusively on identical, isolated virtual environments, while technical document rendering is deployed autonomously to GitHub Pages using VitePress.

## Consequences
* **Positive:** Complete elimination of the "works on my machine" development loop.
* **Positive:** Automatic generation and historical tracking of release-ready JAR artifacts.
* **Negative:** Mod updates depend explicitly on pipeline stability and external GitHub runner availability.

## Quality Requirements Tracing
* Addresses **QR-003 (CI Compilation)**: Automates toolchain tracking, unit checks, and delivery configurations.
