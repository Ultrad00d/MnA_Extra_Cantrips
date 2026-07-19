package net.ultrad00d.ForgottenCantrips.definitions;

import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;

public class MusicDiscDefinitions {
    public static final String DISC_ROOT = ForgottenCantrips.MOD_ID + "_disc";
    public static final String DISC_ITEM = DISC_ROOT + "_item";
    public static final String DISC_START = DISC_ROOT + "_start";
    public static final String DISC_DURATION = DISC_ROOT + "_duration";
    public static final String DISC_PLAYING = DISC_ROOT + "_playing";

    public static final Map<Integer, Long> DISC_DURATIONS = Map.ofEntries(
        Map.entry(Item.getId(Items.MUSIC_DISC_13), 3700L),
        Map.entry(Item.getId(Items.MUSIC_DISC_CAT), 3700L),
        Map.entry(Item.getId(Items.MUSIC_DISC_BLOCKS), 6900L),
        Map.entry(Item.getId(Items.MUSIC_DISC_CHIRP), 3700L),
        Map.entry(Item.getId(Items.MUSIC_DISC_FAR), 3480L),
        Map.entry(Item.getId(Items.MUSIC_DISC_MALL), 3940L),
        Map.entry(Item.getId(Items.MUSIC_DISC_MELLOHI), 1920L),
        Map.entry(Item.getId(Items.MUSIC_DISC_STAL), 3000L),
        Map.entry(Item.getId(Items.MUSIC_DISC_STRAD), 3760L),
        Map.entry(Item.getId(Items.MUSIC_DISC_WARD), 5020L),
        Map.entry(Item.getId(Items.MUSIC_DISC_11), 1420L),
        Map.entry(Item.getId(Items.MUSIC_DISC_WAIT), 4760L),
        Map.entry(Item.getId(Items.MUSIC_DISC_PIGSTEP), 2960L),
        Map.entry(Item.getId(Items.MUSIC_DISC_OTHERSIDE), 3900L),
        Map.entry(Item.getId(Items.MUSIC_DISC_5), 3500L),
        Map.entry(Item.getId(Items.MUSIC_DISC_RELIC), 4360L)
    );
}
