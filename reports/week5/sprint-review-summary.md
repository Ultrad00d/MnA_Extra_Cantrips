# Customer Meeting Transcript

> **Project:** Mana and Artifice Extra Cantrips<br>
> **Date:** 2026-07-05<br>
> **Location/Platform:** in-person<br>
> **Recording:** Yes  -  customer consented to recording and private sharing with instructors<br>
> **Publication:** Yes  -  customer consented to publishing the sanitized transcript in the repository

## Participants

| Name | Role |
|---|---|
| ultradood | Interviewer |
| alexm-gh | Note taker |
| l1n0n | Note taker |
| Dima280807 | Note taker |

---

## 🗣️ Discussion Points & Progress

### 1. Cantrip Delay Mechanics

* Implemented delay for cantrips - spells no longer trigger immediately upon drawing.
* When a cantrip with delay is drawn, there's a brief window before the effect activates.
* Lightning cantrip now summons with delay, bed can be summoned via cantrip with timing.

### 2. Spectral Armor Cantrip

* New cantrip gives spectral armor effect to player.
* Duration: 30 seconds (discussion about whether this should be longer - 2 minutes suggested).
* Armor value: 5 points (stronger than netherite helmet's 3 points).
* No durability - doesn't break during effect duration.

### 3. Villager Trade Mechanics

* Leveled-up villagers don't need profession blocks to update trades.
* Villagers use beds to update their trade assortment.
* Trade updates happen 3-4 times per day at specific times.
* Custom cantrips should not abuse the trade update system.

### 4. Force Consume Effects

* Force Consume has multiple cases/effects.
* Placeholder sound (beacon turning off) selected.
* Works on regular food items - effects transfer properly.
* Eating musical discs triggers their effects.
* Eating glowing food makes player emit light.

### 5. Heart of the Sea Effect

* When Devour cantrip is cast with Heart of the Sea in the offhand it simply grants the player Conduit Power effect for 15 minutes.

---

## 🎯 Decisions

1. **Spectral Armor Duration:** Continue with 30 seconds for now, gather community feedback on balance.
2. **Heart of the Sea:** Pass idea for consideration - either bubble increase or air restoration.
3. **Cantrip Complexity:** Balance concerns about redrawing short-duration effects in combat.

---

## 📋 Action Points

- [x] Development: Finalize Spectral Armor cantrip timings.
- [x] Design: Make missing icons for newly added cantrips (Spectral Armor, Villager Mind-Wipe, Empower)
- [x] Future: Complete mage NPC integration (model, animations and its house are already done).

---

## ⚠️ Risks & Resulting Changes

* **Short Duration Effects (Medium Risk):** 30-second effects may be too short for combat use, requiring constant redrawing.
* *Resulting Change:* Consider extending to 1 minute, test in combat scenarios.

---

## ✅ Team Acknowledges

* **[✓]** Cantrip delay system implemented and working.
* **[✓]** Spectral armor effect functioning with proper armor values.
* **[✓]** Force Consume multiple cases operational.
* **[✓]** Villager trade mechanics understood and documented.
* **[✓]** Mage NPC foundation complete (awaiting integration next week).
