package net.ultrad00d.ForgottenCantrips.dialogue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.cantrip.CantripType;
import net.ultrad00d.ForgottenCantrips.util.ProgressionUtil;

import java.util.List;
import java.util.UUID;

public class WizardDialogue { // todo: make dialogue text color slightly purple
    public static final String[] ICON_ROWS = {"\uE000", "\uE001", "\uE002", "\uE003"};
    public static final int ICON_WIDTH = 36; //px
    public static final int ICON_WIDTH_IN_SPACES = ICON_WIDTH * 2 / 7; //px
    public static final int darkColorHEX = 0x504457;
    public static final int midColorHEX = 0x9682a1;
    public static final int lightColorHEX = 0xf5e3ff;

    public static void sendWizardReply(Player player, WizardDialogueData cap, String messageKey, String... choices) {
        Minecraft client = Minecraft.getInstance();
        int chatWidthPX = ChatComponent.getWidth(client.options.chatWidth().get());
        int textSpaceWidth = chatWidthPX - ICON_WIDTH - 6; // 4px = margin

        // first 4 rows contain an NPC icon, so the translated string gets split across rows
        Component fullMessage = Component.translatable("dialogue." + ForgottenCantrips.MOD_ID + ".wizard." + messageKey);
        List<FormattedCharSequence> splitLines = client.font.split(fullMessage, textSpaceWidth);

        //  print the icon for at least 4 rows
        for (int i = 0; i < Math.max(splitLines.size(), 4); i++) {
            Component textLine = convertSequenceToComponent(splitLines, i);
            Component formattedRow = (i < 4)
                    ? getIconComponent(ICON_ROWS[i]).append(textLine)
                    : getBlankSpacerComponent().append(textLine);
            player.sendSystemMessage(formattedRow);
        }

        // if any choices were passed, add each of them
        if (choices.length > 0) {
            WizardSessionManager.clearPlayerTokens(player);

            for (int i = 0; i < choices.length; i += 2) {
                MutableComponent choicesRow = getBlankSpacerComponent();

                // First item in this row chunk
                String firstKey = choices[i];
                choicesRow.append(buildChoiceButton(player, cap, firstKey, messageKey));

                // Second item in this row chunk (if it exists)
                if (i + 1 < choices.length) {
                    String secondKey = choices[i + 1];
                    choicesRow.append(Component.literal("  |  ").withStyle(s -> s.withColor(midColorHEX)));
                    choicesRow.append(buildChoiceButton(player, cap, secondKey, messageKey));
                }
                player.sendSystemMessage(choicesRow);
            }
        }
    }

    private static MutableComponent getIconComponent(String icon) {
        // start with space to negate font change and have only the icon child have custom font
        return Component.literal("").append(Component.literal(icon).withStyle(
                style -> style.withFont(ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "wizard_icon"))
        )).append(Component.literal(" "));
    }

    private static MutableComponent getBlankSpacerComponent() {
        return Component.literal(" ".repeat(ICON_WIDTH_IN_SPACES));
    }

    private static MutableComponent buildChoiceButton(Player player, WizardDialogueData cap, String choiceKey, String messageKey) {
        boolean isLocked = isChoiceLocked(player, choiceKey, cap);

        // Check if this choiceKey belongs to a CantripType
        CantripType cantrip = CantripType.fromId(choiceKey);
        MutableComponent choiceText;

        if (cantrip != null) {
            choiceText = Component.translatable("cantrip." + ForgottenCantrips.MOD_ID + "." + choiceKey);
        } else {
            choiceText = Component.translatable("dialogue." + ForgottenCantrips.MOD_ID + ".choice." + choiceKey);
        }

        choiceText = Component.literal("[").append(choiceText).append(Component.literal("]"));
        if (isLocked) choiceText = Component.literal("🔒 ").withStyle(s -> s.withColor(lightColorHEX))
                .append(choiceText);

        if (isLocked) {
            return choiceText.withStyle(s -> s.withColor(darkColorHEX))
                    .withStyle(style -> style.withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Component.translatable("chat." + ForgottenCantrips.MOD_ID + ".locked")
                    )));
        } else {
            String secureToken = UUID.randomUUID().toString().substring(0, 8);
            WizardSessionManager.registerToken(secureToken, player, choiceKey, messageKey);

            return choiceText.withStyle(s -> s.withColor(midColorHEX))
                    .withStyle(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fc_dialogue " + secureToken))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat." + ForgottenCantrips.MOD_ID + ".click_to_select"))) //[cite: 2]
                    );
        }
    }

    private static Component convertSequenceToComponent(List<FormattedCharSequence> lines, int index) {
        if (index >= lines.size()) return Component.empty();

        StringBuilder builder = new StringBuilder();
        lines.get(index).accept((charIndex, style, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
        });

        return Component.literal(builder.toString()).withStyle(s -> s.withColor(lightColorHEX));
    }

    private static boolean isChoiceLocked(Player player, String key, WizardDialogueData cap) {
        if (key.equals("continue") || key.equals("back") || key.equals("bye") || key.equals("cantrips_menu")) {
            return false;
        }

        CantripType cantrip = CantripType.fromId(key);
        if (cantrip != null) {
            int tier = ProgressionUtil.getPlayerTier(player);
            int requiredTier = cantrip.getTier();
            boolean alreadyLearned = cap.getBranchState(key) == WizardCantripBranchState.SPELL_LEARNED;

            return tier < requiredTier || alreadyLearned;
        }
        return false;
    }

    public static void advanceDialogueFrom(String action, String from, Player player, WizardDialogueData cap) {
        if (DialogueChoice.BYE.getKey().equals(action)) {
            cap.setGlobalState(WizardGlobalState.NOT_MET);
            sendWizardReply(player, cap, "goodbye");
            return;
        } else if (DialogueChoice.CANTRIPS_MENU.getKey().equals(action)) {
            sendWizardReply(player, cap, "cantrips",
                    CantripType.LIGHTNING.getId(),
                    CantripType.SPECTRAL_BED.getId(),
                    CantripType.SPECTRAL_DONKEY.getId(),
                    CantripType.SPECTRAL_BOAT.getId(),
                    CantripType.SPECTRAL_ARMOR.getId(),
                    CantripType.EMPOWER_MANA_BUFF.getId(),
                    CantripType.EMPOWER_DAMAGE_BUFF.getId(),
                    CantripType.EMPOWER_CANTRIP_BUFF.getId(),
                    CantripType.SPECTRAL_SLIME.getId(),
                    CantripType.BUBBLE_UP.getId(),
                    DialogueChoice.BYE.getKey()
            );
            return;
        }

        // Example dialogue map (by Gemini)
        switch (from) {
            case "intro.1" -> {
                sendWizardReply(player, cap, "intro.2", DialogueChoice.CONTINUE.getKey(), DialogueChoice.BYE.getKey());
            }
            case "intro.2" -> {
                sendWizardReply(player, cap, "intro.3", DialogueChoice.CONTINUE.getKey(), DialogueChoice.BYE.getKey());
            }
            case "intro.3" -> {
                sendWizardReply(player, cap, "intro.4", DialogueChoice.CONTINUE.getKey(), DialogueChoice.BYE.getKey());
            }
            case "intro.4", "back_again.2" -> {
                cap.setGlobalState(WizardGlobalState.INTRODUCED);
                sendWizardReply(player, cap, "cantrips",
                        CantripType.LIGHTNING.getId(),
                        CantripType.SPECTRAL_BED.getId(),
                        CantripType.SPECTRAL_DONKEY.getId(),
                        CantripType.SPECTRAL_BOAT.getId(),
                        CantripType.SPECTRAL_ARMOR.getId(),
                        CantripType.EMPOWER_MANA_BUFF.getId(),
                        CantripType.EMPOWER_DAMAGE_BUFF.getId(),
                        CantripType.EMPOWER_CANTRIP_BUFF.getId(),
                        CantripType.SPECTRAL_SLIME.getId(),
                        CantripType.BUBBLE_UP.getId(),
                        DialogueChoice.BYE.getKey()
                );
            }
            case "back_again.1" -> {
                if (DialogueChoice.CONTINUE.getKey().equals(action)) {
                    sendWizardReply(player, cap, "back_again.2",
                            DialogueChoice.CONTINUE.getKey(),
                            DialogueChoice.BYE.getKey()
                    );
                }
            }
            case "cantrips" -> {
                String cantripID = "";
                WizardCantripBranchState state = cap.getBranchState(cantripID);
                switch (state) {
                    case PART_1_ITEM_GIVEN, PART_2_ITEM_GIVEN -> {
                        sendWizardReply(player, cap, "cantrip_item_already_given_today",
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                    case PART_1 -> {
                        sendWizardReply(player, cap, "cantrip_item_1_quest",
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.GIVE_ITEM.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                    case PART_2 -> {
                        sendWizardReply(player, cap, "cantrip_item_2_quest",
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.GIVE_ITEM.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                    case PART_3 -> {
                        sendWizardReply(player, cap, "cantrip_item_3_quest",
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.GIVE_ITEM.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                    case PART_3_ITEM_GIVEN -> {
                        sendWizardReply(player, cap, "cantrip_symbols_already_known",
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                    case SPELL_LEARNED -> {
                        sendWizardReply(player, cap, "cantrip_spell_already_known",
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                    default -> {
                        sendWizardReply(player, cap, "cantrip." + cantripID + ".description",
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                }
            }

            // Fallback or unhandled nodes
            default -> sendWizardReply(player, cap, "fallback", DialogueChoice.BYE.getKey());
        }
    }
}
