package net.ultrad00d.ForgottenCantrips.dialogue;

public enum DialogueChoice {
    CONTINUE("continue"),
    BACK("back"),
    BYE("bye"),
    CANTRIPS_MENU("cantrips_menu"),

    LIGHTNING_CANTRIP("cantrip.lightning"),
    SPECTRAL_BED_CANTRIP("cantrip.spectral_bed"),
    SPECTRAL_DONKEY_CANTRIP("cantrip.spectral_donkey"),
    SPECTRAL_BOAT_CANTRIP("cantrip.spectral_boat"),
    SPECTRAL_ARMOR_CANTRIP("cantrip.spectral_armor"),
    EMPOWER_CANTRIP("cantrip.empower"),
    SPECTRAL_SLIME("cantrip.spectral_slime"),
    BUBBLE_UP_CANTRIP("cantrip.bubble_up");
    // Colossus Oak, Devour and Villager Mind-wipe are cantrips to be found in world loot

    private final String key;
    public String getKey() { return this.key; }

    DialogueChoice(String key) { this.key = key; }
}
