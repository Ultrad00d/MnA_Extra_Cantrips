# Customer Meeting Summary

> **Project:** Mana and Artifice Extra Cantrips<br>
> **Date:** 2026-07-12<br>
> **Location/Platform:** in-person<br>
> **Recording:** Yes  -  customer consented to recording and private sharing with instructors<br>
> **Publication:** Yes  -  customer consented to publishing the sanitized transcript in the repository

## Participants

| Name | Role |
|---|---|
| ultradood | Interviewer |
| dtamindarov5839 | Note taker |
| l1n0n | Note taker |
| Dima280807 | Note taker |

---

## 🗣️ Discussion Points & Progress

### 1. Empower Buff System

* Three new Empower cantrips added by Dmitry - Mana buff, Damage buff, and Cantrip buff.
* Player can have up to 3 effects simultaneously on tier 5, 2 effects on tier 4.
* Runes rotate around player visibly during activation.
* Mana reduction is currently 3 units flat - discussion about making it percentage-based for balance.

### 2. Spectral Slime Cantrip

* Spectral Slime entity exists but currently has no functionality.
* Disappears at 8-16 blocks distance from player.
* Requires further development for proper behavior.

### 3. Bubble Up Cantrip Mechanics

* Bubble Up cantrip activates only in deep water (requires 2+ blocks of water above).
* Should lift player with acceleration up to high speeds.
* Currently missing icon and some functionality.

### 4. Learning System Progress

* Mage NPC foundation complete (model, animations, house, dialogue system).
* Tier-gating for cantrip unlocks implemented.
* Houses need to spawn in world and be made indestructible.

---

## 🎯 Decisions

1. **Mana Reduction Balance:** Consider percentage-based reduction (10-20%) instead of flat 3 units for better balance across different spell costs.
2. **Devour Enhancement:** Pass additional functionality ideas to team - bone meal consumption could temporarily increase effect limits.
3. **Learning System Completion:** Focus on dialogue system and world spawning for mage NPC.

---

## 📋 Action Points

- Development: Finalize Empower buff system with proper tier restrictions.
- Design: Complete Bubble Up cantrip functionality and icon.
- Future: Complete learning system integration with mage NPC dialogues and world spawning.
- Balance: Implement percentage-based mana reduction for Empower.

---

## ⚠️ Risks & Resulting Changes

* **Percentage-based Mana Reduction (Medium Risk):** Need to determine proper percentage values per tier.
* *Resulting Change:* Consider 10% per tier with additional 20% for third tier.
* **Learning System Integration (High Risk):** World spawning and indestructible houses require example use from the base mod.
* *Resulting Change:* Look at base mod code for implementation.

---

## ✅ Team Acknowledges

* **[✓]** Empower buff system implemented with working mana, damage, and cantrip reductions.
* **[✓]** Spectral Slime entity model and basic behavior implemented.
* **[✓]** Bubble Up cantrip activation conditions implemented.