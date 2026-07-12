package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.tools.RLoc;
import net.minecraft.resources.ResourceLocation;
import net.ultrad00d.ForgottenCantrips.dialogue.DialogueChoice;
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

    LIGHTNING("lightning",
            3,
            new LightningCantripLogic(),
            false,
            "uc1/part_3",
            RLoc.create("manaweave_patterns/square"), RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/bolt")
    ),
    SPECTRAL_BED("spectral_bed",
            3,
            new SpectralBedCantripLogic(),
            false,
            "uc1/part_3",
            RLoc.create("manaweave_patterns/square"), RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/bolt")
    ),
    SPECTRAL_BOAT("spectral_boat",
            3,
            new SpectralBoatCantripLogic(),
            false,
            "uc1/part_3",
            RLoc.create("manaweave_patterns/square"), RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/bolt")
    ),
    SPECTRAL_DONKEY("spectral_donkey",
            3,
            new SpectralDonkeyCantripLogic(),
            false,
            "uc1/part_3",
            RLoc.create("manaweave_patterns/square"), RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/bolt")
    ),
    FORCE_CONSUME("force_consume",
            3,
            new ForceConsumeCantripLogic(),
            false,
            "uc1/part_3",
            RLoc.create("manaweave_patterns/square"), RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/bolt")
    ),
    SPECTRAL_ARMOR("spectral_armor",
            1,
            new SpectralArmorCantripLogic(),
            true,
            null,
            RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
    ),
    RESET_VILLAGER("reset_villager",
            1,
            new ResetVillagerTradingProgressCantripLogic(),
            false,
            null,
            RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
    ),
    EMPOWER_DAMAGE_BUFF("dmg_buff",
            2,
            new EmpowerDamageBuffCantripLogic(),
            true,
            null,
            RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
    ),
    EMPOWER_MANA_BUFF("mana_cost_buff",
            2,
            new EmpowerManaCostBuffCantripLogic(),
            true,
            null,
            RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
    ),
    EMPOWER_CANTRIP_BUFF("cantrip_buff",
            2,
            new EmpowerCantripBuffCantripLogic(),
            true,
            null,
            RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
    ),
    SPECTRAL_SLIME("spectral_slime",
            1,
            new SpectralSlimeCantripLogic(),
            false,
            null,
            RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
    );

    private final String id;
    private final int tier;
    private final ICantripLogic logic;
    private final boolean instant;
    @Nullable  private final String advancementPath;
    private final ResourceLocation[] shapes;

    CantripType(String id, int tier, ICantripLogic logic, boolean instant, @Nullable String advancementPath, ResourceLocation... shapes) {
        this.id = id;
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

    public static CantripType fromDialogueChoice(DialogueChoice choice) {
        // Maps "cantrip.lightning" -> LIGHTNING, "cantrip.spectral_slime" -> SPECTRAL_SLIME, etc.
        String lookupName = choice.name().replace("_CANTRIP", "");
        return CantripType.valueOf(lookupName);
    }
}
