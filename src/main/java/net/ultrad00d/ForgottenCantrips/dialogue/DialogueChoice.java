package net.ultrad00d.ForgottenCantrips.dialogue;

public enum DialogueChoice {
    CONTINUE("continue"),
    GIVE_ITEM("give_item"),
    BYE("bye"),
    CANTRIPS_MENU("cantrips_menu");

    private final String key;
    public String getKey() { return this.key; }

    DialogueChoice(String key) { this.key = key; }
}
