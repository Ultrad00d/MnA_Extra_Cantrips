# Customer Meeting Transcript

> **Project:** Mana and Artifice Extra Cantrips<br>
> **Date:** 2026-06-28<br>
> **Location/Platform:** in-person<br>
> **Recording:** Yes  -  customer consented to recording and private sharing with instructors<br>
> **Publication:** Yes  -  customer consented to publishing the sanitized transcript in the repository

## Participants

| Name | Role |
|---|---|
| ultradood | Interviewer |
| notwindstone | Note taker |
| l1n0n | Note taker |
| Dima280807 | Note taker |

---

## �️ Discussion Points & Feedback

### 1. Cantrip Icons & Scaling Idea

* Icons for cantrips were shown. The customer proposed an idea: the more complex the symbols drawn, the stronger the effect. Noted as optional — "if it's too much hassle, you don't have to do it."
* A technical limitation was identified: there is no API to read which symbols the player has placed, so implementing this would require custom tracking. The customer said to ask the mod author or skip if not ready.

### 2. Spectral Bed

* The bed is translucent, breaks in the morning (like amethyst), and has nice particle effects.
* A bug was observed: mana continued draining after the bed was drawn. Likely caused by a saturation effect interaction — not a critical issue.

### 3. Spectral Boat & Donkey

* The boat is functional, can be placed on water, and has a chest inventory.
* The donkey (still textured in Paint) was shown swimming — moves on water like on land, doesn't sink.
* **Dayan's PR was merged before the interview:** pressing E on the donkey/boat now opens the chest inventory directly (not the entity inventory). The customer praised this.
* **Rift storage:** confirmed that Rift does not overlap with Ender Chest — they are separate inventories. Rift with Precision modifier opens Ender Chest; without it opens the Rift itself (54 slots).
* The donkey and boat both disappear on a timer. The customer noted the timer should reset when opening the chest, but currently it doesn't.

### 4. Empower Cantrip (New)

* A new cantrip "Empower" was shown (in a separate branch, WIP). Applies a damage buff that scales with the player's tier.
* Three buff levels depending on tier. Duration varies: 15s (tier 2, 1 buff), 20s (tier 3, 2 buffs), 30s (both buffs at level 2).
* The customer suggested using the Ender rune as an icon if no better idea comes up, or a simple upward arrow outline. Placeholder icon currently used.
* Visualization of the buff effect is not implemented yet — customer said it's fine to skip if too complex.

### 5. Structure-Based Spell Learning

* The customer proposed building a physical structure with **mana projectors** placed in rooms. Each projector would have specific symbols, and right-clicking it would grant an achievement (unlocking the cantrip).
* This would require: (1) a custom structure, (2) projectors with defined symbols, (3) achievement triggers on right-click of structure projectors specifically.
* The customer emphasized this is **optional** — just creative freedom. "If you don't do it, I won't be mad."
* Technical feasibility: structures can be added via datapack. Authors mentioned tools to convert real-world builds into structure files.

---

## �️ Artifacts & Evidence

* **Delivered:** Spectral Bed (with daytime break), Spectral Boat, Spectral Donkey, chest inventory on both (E to open).
* **New (WIP):** Empower cantrip — tier-scaled damage buff.
* **Demo video:** recorded during session.

---

## 🎯 Decisions

1. **Empower cantrip:** Continue development — tier-scaled damage buff with duration scaling. Icon TBD (Ender rune or arrow outline acceptable as placeholder).
2. **Symbol complexity scaling:** Deferred — requires custom symbol tracking, no API support. May revisit later.
3. **Donkey/boat timer:** Consider resetting the disappearance timer when opening the chest inventory (balance question).

---

## 📋 Action Points

* [ ] **Development:** Continue Empower cantrip — finalize tier scaling, duration balance, and icon.
* [ ] **Bug fix:** Investigate mana drain bug with spectral bed (saturation effect interaction).
* [ ] **Quality of life:** Consider resetting donkey/boat disappearance timer on chest open.

---

## � Risks & Resulting Changes

* **Mana drain bug (Low Risk):** Spectral bed appears to drain mana continuously due to a saturation effect. Likely a dev-environment quirk but worth investigating.
* *Resulting Change:* Monitor in further testing; may need to isolate the bed's tick logic from saturation checks.

* **Timer not resetting (Low Risk):** Donkey/boat disappearance timer doesn't reset when opening the chest, potentially causing the entity to vanish while the player is using it.
* *Resulting Change:* Consider adding a timer reset on chest open.

---

## ✅ Customer Approvals

* **[✓]** Spectral Bed, Boat, and Donkey with chest inventories accepted.
* **[✓]** E-to-open chest PR praised and approved.
* **[✓]** Rift and Ender Chest confirmed as separate inventories — no overlap.
* **[✓]** Empower cantrip concept approved for continued development.
* **[✓]** Structure-based learning and wall-drawing concepts acknowledged as optional — no pressure to implement.
