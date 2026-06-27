# Customer Meeting Transcript

> **Project:** Mana and Artifice Extra Cantrips<br>
> **Date:** 2026-06-20<br>
> **Location/Platform:** in-person<br>
> **Recording:** Yes  -  customer consented to recording and private sharing with instructors<br>
> **Publication:** Yes  -  customer consented to publishing the sanitized transcript in the repository

## Participants

| Name | Role |
|---|---|
| ultradood | Interviewer |
| dtamindarov5939 | Note taker |
| l1n0n | Note taker |
| Dima280807 | Note taker |

---

## 🗣️ Discussion Points & Feedback

### 1. Lightning Cantrip Demo

* The customer observed the lightning spell summoning a bolt at the targeted location. Works on entities (mobs) for farming charged creepers and mob heads.
* A bug in the dev environment was noted: creepers do not become charged when struck by the spell. The customer confirmed this does not occur in a standard launcher installation and is likely a dev-environment quirk.
* **Lightning rods interaction:** The customer discussed whether summoned lightning should attract to nearby vanilla lightning rods (radius ~128 blocks). Decision was made **not to implement** this — it would allow griefing and create frustrating gameplay scenarios. Optional compromise: only attract to rods when targeting a block (not an entity).
* **Witch spawning:** The customer previously suggested the lightning cantrip could also spawn witches when striking pigs. Acknowledged as a deferred enhancement.

### 2. Spectral Bed Demo

* The bed is summonable and works only at night and during thunderstorms (when vanilla sleep is allowed).
* It does **not** set the player's spawn point — the customer confirmed this is an acceptable behavior.
* Currently permanent (does not disappear during daytime). The disappearance mechanic is not yet implemented.
* The customer suggested examining the spectral blocks implementation in the base mod for reference on how to make the bed vanish at dawn.
* Texture is a temporary recolor of the vanilla bed; the customer approved its appearance.

### 3. Spectral Boat Demo

* Summons a boat in the direction of the player's gaze, can be placed on water.
* Contains a looted inventory due to the chest-boat model (boats with chests are distinct entities from regular boats).
* **Customer design suggestion:** redesign the boat inventory to resemble the Arcane Cache — a central sphere (storage) surrounded by four ores in a workbench/anvil style, with a visual buff effect when opened. This is optional.
* Like the bed, the boat is currently permanent and requires a daytime disappearance mechanic.

### 4. Spectral Blocks Showcase

* The customer reviewed the existing spectral blocks from the base mod: workbench, anvil, cobweb, ice, fence, and others.
* **Fence behavior:** The codex description states non-living entities can pass through, but in practice undead mobs also cannot pass. The discrepancy was flagged for clarification or correction.
* **Anvil behavior:** Does not fall on its own like a vanilla anvil, but falls if summoned with no block beneath it. Not considered critical.
* The customer recommended review these textures for potential inspiration when redesigning cantrip item models.

### 5. Cantrip Unlock System (Proof of Concept)

* The customer demonstrated that the base mod's Ignite cantrip is gated behind a **Minecraft achievement** (smelting vintium iron), not purely behind tier progression. This confirmed that cantrips CAN be hidden/locked via achievements.
* A proof-of-concept was shown: three tier-based achievements (root achievement, item-locating hint, and final unlock) that gate the spell's appearance in the staff menu.

### 6. Brute-Force & Information Leak Concerns

* With 17 symbols and 3 slots, there are ~4,900 possible combinations. At 1 attempt per second, brute-forcing takes ~1.5 hours. Auto-clickers make this trivial.
* **Critical concern:** Once a combination is published on a wiki, the exploration incentive is destroyed for all players.
* **Customer recommendations:**
  * Tie cantrip unlocking to an achievement that cannot be bypassed by brute-forcing.
  * Make found knowledge fragments (pages) **consumable** — using a page unlocks the cantrip for that player only, preventing server-wide sharing.
  * Consider per-player or per-world randomization of symbol combinations.
  * Ensure exploration is strictly more efficient than brute-forcing.

### 7. Sequential Cantrip Chains (Optional Concept)

* The customer proposed an optional mechanic: draw a base cantrip → an entity appears → cast a second cantrip on it to produce the final effect. This creates multi-step spell crafting. Not mandatory to implement.

### 8. Tier Binding

* Tier is a mandatory parameter for all cantrips and cannot be removed.
* To make a cantrip learnable at any stage, set it to Tier 1.
* To restrict learning, set a higher tier and/or gate behind an achievement.

---

## 🛠️ Artifacts & Evidence

* **Delivered MVP v1:** Lightning cantrip, Spectral Bed, Spectral Boat — all functional in-game.
* **PoC branch:** Achievement-based cantrip unlocking system (3-tier achievement chain).
* **Release:** [v0.2 on GitHub](https://github.com/Ultrad00d/MnA_Extra_Cantrips/releases/tag/mvp-v1)
* **Deployment:** [CurseForge](https://www.curseforge.com/minecraft/mc-mods/forgotten-cantrips)

---

## 🎯 Decisions

1. **Lightning rods:** Summoned lightning will NOT be attracted to vanilla lightning rods. Optional: implement attraction only when targeting a block (not an entity).
2. **Cantrip unlocking:** Continue developing the achievement-based unlock system with brute-force protection (consumable fragments + achievement gating).
3. **Tier binding:** New cantrips will default to Tier 1 for maximum accessibility unless a specific progression gate is desired.
4. **Spectral block disappearance:** Bed and boat must disappear during daytime — implement using the base mod's spectral block lifecycle as reference.
5. **Boat inventory:** Redesign optional — customer's Arcane Cache-style design is a suggestion, not a requirement.

---

## 📋 Action Points

* [ ] **Research:** Investigate Forge API for Loot Tables to enable cantrip drops in structure chests.
* [ ] **Research:** Study other mods to understand the "study tech" restriction mechanic.
* [ ] **Bug fixing:** Determine the reproducible environment and find the root cause of random cantrip bugs (e.g., creepers not charging in dev).
* [ ] **Development:** Implement daytime disappearance mechanic for Spectral Bed and Spectral Boat.
* [ ] **Development:** Design and implement brute-force-proof cantrip unlock architecture (achievement gating + consumable knowledge fragments).
* [ ] **Content:** Add tooltips/descriptions for Lightning cantrip and Spectral Boat.
* [ ] **Fix:** Clarify or correct Spectral Fence behavior vs. codex description (undead vs. non-living).

---

## ⚡ Risks & Resulting Changes

* **Brute-force exploit (High Risk):** Hidden cantrips can be discovered via automated symbol combination enumeration (~4,900 attempts, ~1.5 hours).
* *Resulting Change:* Implement achievement-gated unlocking with consumable knowledge fragments so that drawing the correct symbols alone is insufficient — the player must also earn the achievement.

* **Information leak (High Risk):** Once a cantrip's symbol sequence is published online, all players can bypass exploration.
* *Resulting Change:* Consider per-world or per-player randomization of symbol combinations; ensure fragments are consumed on use and cannot be shared between players.

* **Dev environment bugs (Low Risk):** Creepers do not charge from lightning in the dev environment, though they work correctly in standard installations.
* *Resulting Change:* No code change needed; document as a known dev-environment quirk.

---

## ✅ Customer Approvals

* **[✓]** MVP v1 scope delivered and accepted (Lightning, Spectral Bed, Spectral Boat).
* **[✓]** Achievement-based cantrip unlocking concept validated as technically feasible.
* **[✓]** Spectral Bed behavior (no spawn point reset) confirmed as acceptable.
