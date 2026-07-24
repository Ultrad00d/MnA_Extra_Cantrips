package net.ultrad00d.ForgottenCantrips.dialogue;

import com.mna.items.ItemInit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.ultrad00d.ForgottenCantrips.cantrip.CantripType;

public enum CantripQuestItem {
    LIGHTNING(CantripType.LIGHTNING.getId(),
            Items.FLINT_AND_STEEL, Items.LIGHTNING_ROD, Items.TRIDENT
    ),
    SPECTRAL_BED(CantripType.SPECTRAL_BED.getId(),
            Items.LIGHT_BLUE_BED, Items.SOUL_LANTERN, Items.RESPAWN_ANCHOR
    ),
    SPECTRAL_DONKEY(CantripType.SPECTRAL_DONKEY.getId(),
            Items.SADDLE, Items.CHEST, Items.GOLDEN_CARROT
    ),
    SPECTRAL_BOAT(CantripType.SPECTRAL_BOAT.getId(),
            Items.OAK_BOAT, Items.CHEST, Items.PACKED_ICE
    ),
    SPECTRAL_ARMOR(CantripType.SPECTRAL_ARMOR.getId(),
            Items.AMETHYST_SHARD, Items.CHAINMAIL_CHESTPLATE, Items.DIAMOND
    ),
    EMPOWER_MANA_BUFF(CantripType.EMPOWER_MANA_BUFF.getId(),
            Items.POTION, Items.CAULDRON, Items.AMETHYST_SHARD
    ),
    EMPOWER_DAMAGE_BUFF(CantripType.EMPOWER_DAMAGE_BUFF.getId(),
            Items.POTION, Items.BLAZE_POWDER, ItemInit.RESONATING_DUST.get()
    ),
    EMPOWER_CANTRIP_BUFF(CantripType.EMPOWER_CANTRIP_BUFF.getId(),
            Items.POTION, Items.PHANTOM_MEMBRANE, Items.GLOWSTONE_DUST
    ),
    SPECTRAL_SLIME(CantripType.SPECTRAL_SLIME.getId(),
            Items.SLIME_BALL, ItemInit.ANIMUS_DUST.get(), ItemInit.ARCANIST_INK.get()
    ),
    BUBBLE_UP(CantripType.BUBBLE_UP.getId(),
            Items.WATER_BUCKET, Items.SOUL_SAND, Items.HEART_OF_THE_SEA
    );

    private final String cantripId;
    private final Item[] items = new Item[3];

    CantripQuestItem(String cantripId, Item item1, Item item2, Item item3) {
        this.cantripId = cantripId;
        this.items[0] = item1;
        this.items[1] = item2;
        this.items[2] = item3;
    }

    public Item getItem(int index) { return items[index]; }

    public static CantripQuestItem fromId(String id) {
        for (CantripQuestItem questItem : values()) {
            if (questItem.cantripId.equals(id)) {
                return questItem;
            }
        }
        return null;
    }
}
