# Stable scenario ID
UAT-001

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Start the game with the mod installed

## Preconditions
- Have Minecraft 1.20.1 installed
- Have the Forge mod-loader 1.20.1-v47.4.20 (latest) installed
- Have the [Mana And Artifice Mod](https://www.curseforge.com/minecraft/mc-mods/mana-and-artifice) (version 3.1.11 (latest) for 1.20.1-forge) installed, including dependencies:
  - Have the [Geckolib](https://www.curseforge.com/minecraft/mc-mods/geckolib) Mod (version 4.2.2 or newer for 1.20.1-forge) installed
  - Have the [Curios API](https://www.curseforge.com/minecraft/mc-mods/curios) Mod (version 5.14.1 or newer for 1.20.1-forge) installed

## Step-by-step instructions
1. Locate the Minecraft installation folder (usually `%Appdata%\.minecraft`)
2. Paste the forgotten_cantrips-v%version%.jar into the `mods` folder
3. Launch the game from your desired launcher
4. Once the game loads, navigate yourself to the Mods menu
5. Locate the "Forgotten Cantrips" mod in the list


## Expected outcome
You must see the Forgotten Cantrips mod in the list, signifying it has loaded correctly

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->




---
---







# Stable scenario ID
UAT-002

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Create new world with the mod installed

## Preconditions
- Have Forgotten Cantrips mod installed (UAT-001)
- Have the Minecraft client at Main menu opened

## Step-by-step instructions
1. Click `Singleplayer`
2. Click "Create new World"
3. Wait until the client generates the chunks around the player

## Expected outcome
The world must load correctly

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->




---
---





# Stable scenario ID
UAT-003

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Cast a Lightning cantrip

## Preconditions
- Join a world by either joining multiplayer server, or a singleplayer world (UAT-002)
- Have Mana and Artifice magic progress of tier 3 (you can run the `/mna progression tier 3` for that)

## Step-by-step instructions
1. Get yourself any manaweaver's wand (Chimerite manaweaver's wand is recommended)
2. Hold Left Ctrl and press Right click to open wand's GUI
3. Locate the new cantrips added by the mod and memorize yourself with the combination of symbols needed for Lightning cantrip (default is square, circle, zig-zag)
4. Close the GUI (press Esc) and draw the symbols in the air. If you have trouble doing so, you can hold Z (by default), and select the symbols needed, then hold Right click to draw the selected symbol.

## Expected outcome
Once all symbols for the cantrip are drawn, the Lightning shall strike before you

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->




---
---





# Stable scenario ID
UAT-004

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Cast a Spectral Bed cantrip in the dimension where beds work

## Preconditions
- Join a world by either joining multiplayer server, or a singleplayer world (UAT-002)
- Have Mana and Artifice magic progress of tier 3 (you can run the `/mna progression tier 3` for that)
- Navigate yourself to the dimension, where beds work (f.e., the Overworld)
- Wait for the night or thunderstorm to occur (or force-summon them by `/time set night` or `/weather thunder`)

## Step-by-step instructions
1. Get yourself any manaweaver's wand (Chimerite manaweaver's wand is recommended)
2. Hold Left Ctrl and press Right click to open wand's GUI
3. Locate the new cantrips added by the mod and memorize yourself with the combination of symbols needed for Spectral Bed cantrip (default is TBD)
4. Close the GUI (press Esc) and draw the symbols in the air. If you have trouble doing so, you can hold Z (by default), and select the symbols needed, then hold Right click to draw the selected symbol.
5. Draw the symbols looking at some block nearby

## Expected outcome
Once all symbols for the cantrip are drawn, Spectral Bed shall appear before you.

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->




---
---




# Stable scenario ID
UAT-005

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Make Spectral Bed disappear in the morning

## Preconditions
- Have Spectral Bed summoned (UAT-004)

## Step-by-step instructions
1. Sleep on the Spectral Bed or wait for the morning to come (or force it by `/time set day`)

## Expected outcome
Once the new day starts, the bed blocks shall be destroyed automatically

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->





---
---




# Stable scenario ID
UAT-006

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Cast a Spectral Bed cantrip in the dimension where beds don't work

## Preconditions
- Join a world by either joining multiplayer server, or a singleplayer world (UAT-002)
- Have Mana and Artifice magic progress of tier 3 (you can run the `/mna progression tier 3` for that)
- Navigate yourself to the dimension, where beds don't work (f.e., the Nether)

## Step-by-step instructions
1. Get yourself any manaweaver's wand (Chimerite manaweaver's wand is recommended)
2. Hold Left Ctrl and press Right click to open wand's GUI
3. Locate the new cantrips added by the mod and memorize yourself with the combination of symbols needed for Spectral Bed cantrip (default is TBD)
4. Close the GUI (press Esc) and draw the symbols in the air. If you have trouble doing so, you can hold Z (by default), and select the symbols needed, then hold Right click to draw the selected symbol.
5. Draw the symbols looking at some block nearby
6. Click the new summoned Spectral Bed

## Expected outcome
For Step 5: Once all symbols for the cantrip are drawn, Spectral Bed shall appear before you.
For Step 6: Once an attempt to sleep in the Spectal bed in the dimension where beds aren't working is performed, the bed shall explode

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->




---
---





# Stable scenario ID
UAT-007

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Cast a Spectral Boat cantrip

## Preconditions
- Join a world by either joining multiplayer server, or a singleplayer world (UAT-002)
- Have Mana and Artifice magic progress of tier 3 (you can run the `/mna progression tier 3` for that)

## Step-by-step instructions
1. Get yourself any manaweaver's wand (Chimerite manaweaver's wand is recommended)
2. Hold Left Ctrl and press Right click to open wand's GUI
3. Locate the new cantrips added by the mod and memorize yourself with the combination of symbols needed for Spectral Boat cantrip (default is TBD)
4. Close the GUI (press Esc) and draw the symbols in the air. If you have trouble doing so, you can hold Z (by default), and select the symbols needed, then hold Right click to draw the selected symbol.
5. Shift+Right click the new summoned Spectral Boat

## Expected outcome
For Step 4: Once all symbols for the cantrip are drawn, Spectral Boat shall appear before you.
For Step 5: The action shall open Spectral Boat's internal inventory, bound to you (the player)

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->





---
---





# Stable scenario ID
UAT-008

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Cast a Spectral Donkey cantrip

## Preconditions
- Join a world by either joining multiplayer server, or a singleplayer world (UAT-002)
- Have Mana and Artifice magic progress of tier 3 (you can run the `/mna progression tier 3` for that)

## Step-by-step instructions
1. Get yourself any manaweaver's wand (Chimerite manaweaver's wand is recommended)
2. Hold Left Ctrl and press Right click to open wand's GUI
3. Locate the new cantrips added by the mod and memorize yourself with the combination of symbols needed for Spectral Donkey cantrip (default is TBD)
4. Close the GUI (press Esc) and draw the symbols in the air. If you have trouble doing so, you can hold Z (by default), and select the symbols needed, then hold Right click to draw the selected symbol.
5. Shift+Right click the new summoned Spectral Donkey

## Expected outcome
For Step 4: Once all symbols for the cantrip are drawn, Spectral Donkey shall appear before you.
For Step 5: The action shall open Spectral Donkey's internal inventory, bound to you (the player)

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->
Spectral Donkey is way too fast in the water; it is as fast in the water as on land

<!-- Resulting PBIs or issues after execution -->
The issue with the bug explained: #168



---
---





# Stable scenario ID
UAT-009

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Cast a Devour cantrip

## Preconditions
- Join a world by either joining multiplayer server, or a singleplayer world (UAT-002)
- Have Mana and Artifice magic progress of tier 3 (you can run the `/mna progression tier 3` for that)

## Step-by-step instructions
1. Get yourself any manaweaver's wand (Chimerite manaweaver's wand is recommended)
2. Get one of the following in your off-hand:
- Air
- Cake
- Melon Block
- Dried Kelp Block
- Pumpkin
- Carved Pumpkin
- Hay Block
- Experience Bottle
- Totem of Undying
- Potion items (PotionItem – any potion, splash, or lingering)
- Milk Bucket
- Shulker Shell
- Rabbit's Foot
- Heart of the Sea
- Glow Ink Sac
- Glowstone Dust
- Spectral Arrow
- Glowstone (block)
- Sea Lantern
- Shroomlight
- Redstone Lamp
- Torch
- Redstone Torch
- Soul Torch
- Piston
- Sticky Piston
- Music Discs
- Netherite Scrap
- Dragon's Breath
- Fire Charge
- Arcane Compound (custom item)
- Water Bucket
- Regular food (stews, bread, chorus fruit, etc.)
3. Hold Left Ctrl and press Right click to open wand's GUI
4Locate the new cantrips added by the mod and memorize yourself with the combination of symbols needed for Devour cantrip (default is TBD)
5Close the GUI (press Esc) and draw the symbols in the air. If you have trouble doing so, you can hold Z (by default), and select the symbols needed, then hold Right click to draw the selected symbol.


## Expected outcome
For each of the unique items the following must apply:
- Air: nothing; the cast yields an error
- Cake: restores food (eats 7 bites of cake block)
- Melon Block: restores food (eats 9 melon slices)
- Dried Kelp Block: restores food (eats 9 dried kelp)
- Pumpkin: restores food (eats 1 pumpkin pie)
- Carved Pumpkin: restores food (eats 1 pumpkin pie)
- Hay Block: restores food (eats 3 breads)
- Experience Bottle: gives XP (random 6–21 XP points)
- Totem of Undying: applies Undying effect for 3 minutes
- Potion items: applies the potion's effects (drinks the potion)
- Milk Bucket: removes all status effects
- Shulker Shell: applies Levitation for 10 seconds
- Rabbit's Foot: applies Jump Boost for 30 seconds
- Heart of the Sea: applies Conduit Power for 15 minutes
- Glow Ink Sac: applies Glowing for 30 seconds
- Glowstone Dust: applies Glowing for 30 seconds
- Spectral Arrow: applies Glowing for 30 seconds
- Glowstone Block: applies Illumination (area light) for 3 minutes
- Sea Lantern: applies Illumination for 3 minutes
- Shroomlight: applies Illumination for 3 minutes
- Redstone Lamp: applies Illumination for 3 minutes
- Torch: applies Illumination for 30 seconds
- Redstone Torch: applies Illumination for 30 seconds
- Soul Torch: applies Illumination for 30 seconds
- Piston: applies Aether Stride (+1 step height) for 2 minutes
- Sticky Piston: applies Aether Stride (+1 step height) for 2 minutes
- Music Discs: plays the music disc and stores it in a hidden slot
- Regular food (stews, bread, chorus fruit, etc.): restores hunger/saturation; stews also apply effects from tag; chorus fruit also teleports randomly

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->



---
---





# Stable scenario ID
UAT-010

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Cast a Spectral Armor cantrip

## Preconditions
- Join a world by either joining multiplayer server, or a singleplayer world (UAT-002)
- Have Mana and Artifice magic progress of tier 3 (you can run the `/mna progression tier 3` for that)

## Step-by-step instructions
1. Get yourself any manaweaver's wand (Chimerite manaweaver's wand is recommended)
2. Hold Left Ctrl and press Right click to open wand's GUI
3. Locate the new cantrips added by the mod and memorize yourself with the combination of symbols needed for Spectral Armor cantrip (default is TBD)
4. Close the GUI (press Esc) and draw the symbols in the air. If you have trouble doing so, you can hold Z (by default), and select the symbols needed, then hold Right click to draw the selected symbol.

## Expected outcome
The player shall be granted an effect of Spectral Protection for 30s that will equip them with Spectral armor set filling missing equipment pieces with Spectral's

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->




---
---





# Stable scenario ID
UAT-011

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Remove Spectral Armor piece

## Preconditions
- Have a full set of Spectral Armor from the cast of a Spectral Armor cantrip (UAT-10)

## Step-by-step instructions
1. Open your inventory
2. Try taking the Spectral Helmet off
3. Try throwing away the Spectral Chestplate
4. Try dying with two of the pieces left

## Expected outcome
For Step 2: the helmet shall disappear from your cursor slot
For Step 3: the chestplate shall never be thrown and disappear from your inventory
For Step 4: when you locate your death location afterward, no Spectral armor pieces shall be dropped on the ground or be left in your inventory if the game rule `keepInventory` was set to `true`

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->




---
---




# Stable scenario ID
UAT-012

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Save Villager Mind with Villager Mind-Wiping cantrip

## Preconditions
- Join a world by either joining multiplayer server, or a singleplayer world (UAT-002)
- Have Mana and Artifice magic progress of tier 3 (you can run the `/mna progression tier 3` for that)

## Step-by-step instructions
1. Get yourself any manaweaver's wand (Chimerite manaweaver's wand is recommended)
2. Get yourself an Emerald into the off-hand
3. Hold Left Ctrl and press Right click to open wand's GUI
4. Locate the new cantrips added by the mod and memorize yourself with the combination of symbols needed for Villager Mind-Wiping (default is TBD)
5. Close the GUI (press Esc) and draw the symbols in the air. If you have trouble doing so, you can hold Z (by default), and select the symbols needed, then hold Right click to draw the selected symbol.
6. Look at the Villager after the cantrip is cast

## Expected outcome
The villager profession, levels, trades shall be written into the Magical Emerald given to the player after cast and the villager shall be reset

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->






---
---






# Stable scenario ID
UAT-013

## Scenario status, such as Active, Retired, or Superseded
Active

## User goal
Load Villager Mind with Villager Mind-Wiping cantrip

## Preconditions
- Have a Magical Emerald in your off-hand (UAT-013)

## Step-by-step instructions
1. Get yourself any manaweaver's wand (Chimerite manaweaver's wand is recommended)
2. Hold Left Ctrl and press Right click to open wand's GUI
3. Locate the new cantrips added by the mod and memorize yourself with the combination of symbols needed for Spectral Armor cantrip (default is TBD)
4. Close the GUI (press Esc) and draw the symbols in the air. If you have trouble doing so, you can hold Z (by default), and select the symbols needed, then hold Right click to draw the selected symbol.
5. Look at the Villager after the cantrip is cast

## Expected outcome
The villager profession, levels, trades shall be loaded from the Magical Emerald from the player's off-hand and the emerald shall be turned into a vanilla Emerald

<!-- Assignment-specific execution results when required -->

<!-- Customer comments or observed issues after execution -->

<!-- Resulting PBIs or issues after execution -->
