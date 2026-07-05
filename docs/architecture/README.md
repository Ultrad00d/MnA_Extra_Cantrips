# Architecture

Forgotten Cantrips is a Minecraft Forge 1.20.1 mod that adds new Mana and Artifice cantrips, items, effects, entities, UI, and assets.

| View | Rendered diagram | Source |
|---|---|---|
| Static | [static_view.svg](static_view.svg) | [component-diagram.puml](static-view/component-diagram.puml) |
| Dynamic | [dynamic_view.svg](dynamic_view.svg) | [spectral-bed-sequence.puml](dynamic-view/spectral-bed-sequence.puml) |
| Deployment | [deployment_view.svg](deployment_view.svg) | [deployment-diagram.puml](deployment-view/deployment-diagram.puml) |

## Static View

### Component Diagram

![Static component diagram](static_view.svg)

The component diagram shows the mod as an in-process Forge extension. Forge loads `ForgottenCantrips`, which registers content, config, event hooks, and Mana and Artifice callbacks. Cohesion is good since registries, cantrips, events, screens, renderers, and assets are separated. Coupling is strongest to Minecraft/Forge/Mana and Artifice, so upgrades need integration testing. This supports QR-001 separation, QR-002 delayed runtime work, and QR-003 CI compilation.

## Dynamic View

### Sequence Diagram: Spectral Bed Cantrip

![Spectral bed sequence diagram](dynamic_view.svg)

The sequence diagram shows the Spectral Bed flow: cast, Mana and Artifice resolution, delayed execution, server-side placement validation, and world update. It crosses client input, cantrip dispatch, mod logic, server authority, and persistence. The key decision is server-validated mutation: invalid casts return feedback without changing the world.

## Deployment View

### Deployment Diagram

![Deployment diagram](deployment_view.svg)

The deployment diagram shows GitHub Actions builds with JDK 17/Gradle, release JARs, and VitePress/GitHub Pages docs. A Forge mod JAR fits because the product runs inside Minecraft, not as a hosted service. This is simple but version-constrained: clients/servers need compatible Minecraft, Forge, Mana and Artifice, Curios, GeckoLib, Java, matching mod versions, backups, and preserved config when needed.
