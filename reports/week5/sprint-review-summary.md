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

* Eating Heart of the Sea permanently increases underwater air bubbles.
* Maximum effect requires eating 5 hearts (adds ~10 bubbles).
* Suggested change: make it restore air instead of bubbles if current implementation isn't satisfactory.

---

## 🎯 Decisions

1. **Spectral Armor Duration:** Continue with 30 seconds for now, gather community feedback on balance.
2. **Heart of the Sea:** Pass idea to Alexander for consideration - either bubble increase or air restoration.
3. **Cantrip Complexity:** Balance concerns about redrawing short-duration effects in combat.

---

## 📋 Action Points

* [ ] **Development:** Finalize cantrip delay timing across all spells.
* [ ] **Audio:** Investigate missing sound when spectral armor effect expires.
* [ ] **Design:** Address piston signal pass-through issue.
* [ ] **Future:** Complete mage NPC integration (model, animations, hut already done).

---

## ⚠️ Risks & Resulting Changes

* **Short Duration Effects (Medium Risk):** 30-second effects may be too short for combat use, requiring constant redrawing.
* *Resulting Change:* Consider extending to 1 minute, test in combat scenarios.

* **Piston Signal Issue (Low Risk):** Piston doesn't register signals when repeater is activated.
* *Resulting Change:* Refactor redstone mechanics in cantrip implementation.

---

## ✅ Team Acknowledges

* **[✓]** Cantrip delay system implemented and working.
* **[✓]** Spectral armor effect functioning with proper armor values.
* **[✓]** Force Consume multiple cases operational.
* **[✓]** Villager trade mechanics understood and documented.
* **[✓]** Mage NPC foundation complete (awaiting integration next week).