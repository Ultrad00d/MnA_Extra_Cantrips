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
