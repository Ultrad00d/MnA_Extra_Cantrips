# Customer Handover Documentation

> **Project:** Mana and Artifice Forgotten Cantrips  
> **Handover Date:** 2026-07-12  
> **Current Status:** In Development (Pre-release)

## Repository & Access

- **Repository:** [MnA_Extra_Cantrips on GitHub](https://github.com/Ultrad00d/MnA_Extra_Cantrips)
- **Ownership:** Repository remains under team control; customer has read access
- **Branch:** `main` branch contains latest stable code
- **Build artifacts:** Available via GitHub Actions or local build

## Deployment & Installation

### Requirements
- Minecraft 1.20.1
- Forge modloader 1.20.1-v47.4.20 (latest)
- [Mana And Artifice Mod](https://www.curseforge.com/minecraft/mc-mods/mana-and-artifice) (v3.1.11)
- [Geckolib](https://www.curseforge.com/minecraft/mc-mods/geckolib) (v4.2.2+)
- [Curios API](https://www.curseforge.com/minecraft/mc-mods/curios) (v5.14.1+)

### Installation Steps
1. Locate your Minecraft installation folder (`%Appdata%\.minecraft`)
2. Paste the `forgotten_cantrips-v%version%.jar` into the `mods` folder
3. Launch the game from your desired launcher

### Build Steps
1. Clone the repository
2. Open project in IntelliJ IDEA via **File | Open | Project from Existing Sources**
3. Run `./gradlew build` to compile

## Configuration

No environment variables or external configuration files are required. All mod configuration is handled through Minecraft's standard mod configuration system.

## Current Handover Scope

### Completed Features (Ready for Use)
| Feature | Status | Notes |
|---------|--------|-------|
| Spectral Bed | ✅ Complete | Summon with delay, sleep functionality |
| Lightning Spell | ✅ Complete | Summon with delay |
| Spectral Boat | ✅ Complete | Entity and movement working |
| Spectral Donkey | ✅ Complete | Entity and chest linking |
| Force Consume | ✅ Complete | Multiple effect cases (food, discs, totems) |
| Empower | ✅ Complete | Mana, damage, and cantrip reduction buffs |
| Villager Mind-Wipe | ✅ Complete | Memory reset with Memory Emerald |
| Spectral Armor | ⚠️ Partial | Effect works, needs icon refinement |

### In-Progress Features
| Feature | Status | Notes |
|---------|--------|-------|
| Bubble Up | ⚠️ Partial | Activation conditions work, effect removal on exit incomplete |
| Learning System | ⚠️ Partial | Mage NPC model/animations done, needs world spawning and dialogues |
| Spectral Slime | ⚠️ Partial | Entity exists, no functionality yet |

## Documentation Entry Points

- **User Stories & Requirements:** [docs/user-stories.md](docs/user-stories.md)
- **Build & Run Instructions:** [README.md](README.md)
- **Quality Requirements:** [docs/quality-requirements.md](docs/quality-requirements.md)
- **Testing Guide:** [docs/testing.md](docs/testing.md)
- **Weekly Reports:** [reports/](reports/) directory

## Support & Limitations

### Current Limitations
- Some cantrips (Bubble Up, Spectral Slime) are incomplete
- Learning system mage NPC not yet spawning in world
- Percentage-based mana reduction not yet implemented for Empower
- Some icons missing or placeholder

### Ongoing Support
- Team will continue development through Week 7
- Bug fixes and balance adjustments ongoing
- Learning system completion planned

## Verification Steps

1. Launch Minecraft with mod installed
2. Test cantrip drawing in creative mode
3. Verify effects apply correctly
4. Check that spectral entities spawn and behave as expected
5. Confirm villager trade reset works with Memory Emerald