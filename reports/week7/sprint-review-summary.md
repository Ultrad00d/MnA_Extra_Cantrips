# Customer Meeting Transcript

> **Project:** Mana and Artifice Extra Cantrips<br>
> **Date:** 2026-07-19<br>
> **Location/Platform:** in-person<br>
> **Recording:** Yes<br>
> **Publication:** Yes

## Participants

| Name | Role |
|---|---|
| ultrad00d | Interviewer |
| dtamindarov5839 | Note taker |
| l1n0n | Note taker |
| Dima280807 | Note taker |

---

## 🗣️ Discussion Points & Progress

### 1. Spectral Armor Enchantment Transfer

* The Spectral Armor cantrip should transfer enchantments from the rune anvil to summoned items.
* Enchantments on anvil need to be copied to summoned armor via tagging system.
* The tagging approach aligns with how other summoned items work in MnA.

### 2. Ancient Scrolls System

* Dmitri added the rune anvil and all achievements, along with unknown cantrips.
* Three new cantrips added: Colossus Oak, Devour, and Villager Mind Wipe.
* Ancient Scrolls generate in chests with random cantrip assignment upon selection.
* Scrolls remain even if all cantrips are already learned.
* Custom loot tables implemented for ruined portal and chest generation.

### 3. Spectral Slime Summon

* When enhanced by Empower effect, the slime size increases 2-3x.
* The slime behaves like a wolf, attacking enemies and applying nausea effect.
* Small slimes combine into larger ones (4:1 ratio).
* Summoned entities follow standard MnA behavior with teleportation.

### 4. Spell Enhancement (Empower)

* Empower cantrip provides spell enhancement effects.
* Currently only gives first level effect.
* Discussion about bone ash absorption to increase enhancement count.
* Enhancement that reduces mana percentage by 10% works but needs rebalancing.

---

## 🎯 Decisions

1. **Spectral Armor Enchantments:** Implement enchantment transfer via shared tags on summoned armor.
2. **Ancient Scrolls:** Maintain random cantrip selection even when all cantrips are learned.
3. **Spectral Slime:** Working as designed, but distance/attack range needs adjustment for larger variants.
4. **Mage Development:** Final remaining work - use MnA movement methods for easier implementation.

---

## 📋 Action Points

- Development: Complete Spectral Armor enchantment transfer system.
- Design: Ancient Scrolls loot table generation and random cantrip assignment.
- Development: Spectral Slime summoned entity with Empower enhancement.
- Future: Mage NPC - complete world integration and dialogue system.

---

## ⚠️ Risks & Resulting Changes

* **Incomplete Mage NPC (High Risk):** Mage NPC still needs working functionality and dialogue system.
* *Resulting Change:* Reference MnA movement methods for simpler implementation.

---

## ✅ Team Acknowledges

* **[✓]** Spectral Armor enchantment transfer approach defined.
* **[✓]** Ancient Scrolls system working with random cantrip assignment.
* **[✓]** Spectral Slime summoned entity functional with Empower enhancement.
* **[✓]** Mage NPC remaining work.