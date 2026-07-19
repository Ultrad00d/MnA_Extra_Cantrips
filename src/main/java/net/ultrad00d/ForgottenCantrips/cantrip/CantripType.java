package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.tools.RLoc;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public enum CantripType {
    // Allowed shapes: [tier] "namespace:path"
    // [1] mna:manaweave_patterns/square                       [2] mna:manaweave_patterns/knot
    // [1] mna:manaweave_patterns/triangle                     [1] mna:manaweave_patterns/circle
    // [3] mna:manaweave_patterns/split_triangle               [1] mna:manaweave_patterns/slash
    // [3] mna:manaweave_patterns/bolt                         [2] mna:manaweave_patterns/knot2
    // [4] mna:manaweave_patterns/hourglass                    [2] mna:manaweave_patterns/knot3
    // [5] mna:manaweave_patterns/inverted_split_triangle      [4] mna:manaweave_patterns/star
    // [1] mna:manaweave_patterns/backslash                    [2] mna:manaweave_patterns/diamond
    // [5] mna:manaweave_patterns/infinity                     [2] mna:manaweave_patterns/knot4
    // [3] mna:manaweave_patterns/inverted_triangle

    LIGHTNING(
            3,
            new LightningCantripLogic(),
            false,
            "lightning/part_3",
            RLoc.create("manaweave_patterns/square"), RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/bolt")
    ),
    SPECTRAL_BED(
            2,
            new SpectralBedCantripLogic(),
            false,
            "spectral_bed/part_3",
            RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/knot4"), RLoc.create("manaweave_patterns/diamond")
    ),
    SPECTRAL_BOAT(
            2,
            new SpectralBoatCantripLogic(),
            false,
            "spectral_boat/part_3",
            RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/diamond"), RLoc.create("manaweave_patterns/circle")
    ),
    SPECTRAL_DONKEY(
            3,
            new SpectralDonkeyCantripLogic(),
            false,
            null,
            RLoc.create("manaweave_patterns/inverted_triangle"), RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/circle")
    ),
    DEVOUR(
            3,
            new DevourCantripLogic(),
            false,
            "devour/part_3",
            RLoc.create("manaweave_patterns/diamond"), RLoc.create("manaweave_patterns/square"), RLoc.create("manaweave_patterns/diamond")
    ),
    SPECTRAL_ARMOR(
            1,
            new SpectralArmorCantripLogic(),
            true,
            null,
            RLoc.create("manaweave_patterns/slash"), RLoc.create("manaweave_patterns/triangle"), RLoc.create("manaweave_patterns/backslash")
    ),
    RESET_VILLAGER(
            3,
            new ResetVillagerTradingProgressCantripLogic(),
            false,
            null,
            RLoc.create("manaweave_patterns/bolt"), RLoc.create("manaweave_patterns/diamond"), RLoc.create("manaweave_patterns/square")
    ),
    EMPOWER_DAMAGE_BUFF(
            2,
            new EmpowerDamageBuffCantripLogic(),
            true,
            null,
            RLoc.create("manaweave_patterns/knot"), RLoc.create("manaweave_patterns/knot_3"), RLoc.create("manaweave_patterns/slash")
    ),
    EMPOWER_MANA_BUFF(
            2,
            new EmpowerManaCostBuffCantripLogic(),
            true,
            null,
            RLoc.create("manaweave_patterns/knot"), RLoc.create("manaweave_patterns/knot_3"), RLoc.create("manaweave_patterns/square")
    ),
    EMPOWER_CANTRIP_BUFF(
            2,
            new EmpowerCantripBuffCantripLogic(),
            true,
            null,
            RLoc.create("manaweave_patterns/knot"), RLoc.create("manaweave_patterns/knot_3"), RLoc.create("manaweave_patterns/triangle")
    ),
    SPECTRAL_SLIME(
            2,
            new SpectralSlimeCantripLogic(),
            false,
            null,
            RLoc.create("manaweave_patterns/square"), RLoc.create("manaweave_patterns/triangle"), RLoc.create("manaweave_patterns/square")
    ),
    BUBBLE_UP(
            2,
            new BubbleUpCantripLogic(),
            true,
            null,
            RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/diamond"), RLoc.create("manaweave_patterns/circle")
    );

    private final String id;
    private final int tier;
    private final ICantripLogic logic;
    private final boolean instant;
    @Nullable  private final String advancementPath;
    private final ResourceLocation[] shapes;

    CantripType(int tier, ICantripLogic logic, boolean instant, @Nullable String advancementPath, ResourceLocation... shapes) {
        this.id = logic.getCantripId();
        this.tier = tier;
        this.logic = logic;
        this.instant = instant;
        this.advancementPath = advancementPath;
        this.shapes = shapes;
    }

    public String getId() { return id; }
    public int getTier() { return tier; }
    public ICantripLogic getLogic() { return logic; }
    public boolean isInstant() { return instant; }
    public @Nullable String getAdvancementPath() { return advancementPath; }
    public ResourceLocation[] getShapes() { return shapes; }

    public static @Nullable CantripType fromId(String id) {
        for (CantripType type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
    }
}
