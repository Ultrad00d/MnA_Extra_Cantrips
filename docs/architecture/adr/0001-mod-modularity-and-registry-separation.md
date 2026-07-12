# ADR 0001: Mod Modularity and Registry Separation

## Status
Accepted

## Context
The "Forgotten Cantrips" mod must register diverse components (items, entities, assets, cantrips) within the Minecraft Forge 1.20.1 lifecycle. High coupling between different asset pipelines or initialization phases can cause registration bottlenecks, unstable build environments, and crashes during server initialization when client-only rendering code is accidentally called.

## Decision
We will enforce strict architectural separation by isolating mod registries, cantrips, event hooks, screen renderers, and asset pipelines into distinct structural packages. Registry components will handle raw lifecycle hooks, while gameplay mechanics and client visuals remain strictly segregated.

## Consequences
* **Positive:** Clear separation of client-only code (screens, renderers) prevents dedicated server crashes.
* **Positive:** High internal cohesion simplifies individual component debugging and independent feature additions.
* **Negative:** Slightly increases the boilerplate required to pass registry object references to logic components.

## Quality Requirements Tracing
* Addresses **QR-001 (Separation)**: Guarantees decoupled software structures for complex content types.
