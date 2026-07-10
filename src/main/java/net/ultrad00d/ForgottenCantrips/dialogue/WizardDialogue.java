package net.ultrad00d.ForgottenCantrips.dialogue;

import net.minecraft.ChatFormatting;
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

import java.util.List;
import java.util.UUID;

public class WizardDialogue {
    public static final String ICON_L1 = "\uE000";
    public static final String ICON_L2 = "\uE001";
    public static final String ICON_L3 = "\uE002";
    public static final int ICON_WIDTH = 27; //px
    public static final int ICON_WIDTH_IN_SPACES = 7; //px

    public static void sendWizardReply(Player player, String messageKey, DialogueChoice... choices) {
        Minecraft client = Minecraft.getInstance();

        double chatWidthSetting = client.options.chatWidth().get();
        int chatWidthPX = ChatComponent.getWidth(chatWidthSetting);
        int textSpaceWidth = chatWidthPX - ICON_WIDTH - 4; // 4px = margin

        // first 3 rows contain an NPC icon, so the translated string gets split across rows
        Component fullMessage = Component.translatable("dialogue." + ForgottenCantrips.MOD_ID + ".wizard." + messageKey);
        List<FormattedCharSequence> splitLines = Minecraft.getInstance().font.split(fullMessage, textSpaceWidth);

        Component row1 = getIconComponent(ICON_L1).append(convertSequenceToComponent(splitLines, 0));
        Component row2 = getIconComponent(ICON_L2).append(convertSequenceToComponent(splitLines, 1));
        Component row3 = getIconComponent(ICON_L3).append(convertSequenceToComponent(splitLines, 2));

        player.sendSystemMessage(row1);
        player.sendSystemMessage(row2);
        player.sendSystemMessage(row3);

        if (splitLines.size() > 3) {
            for (int i = 3; i < splitLines.size(); i++) {
                Component indentedRow = getBlankSpacerComponent().append(convertSequenceToComponent(splitLines, i));
                player.sendSystemMessage(indentedRow);
            }
        }

        if (choices.length > 0) {
            MutableComponent choicesRow = getBlankSpacerComponent();

            WizardSessionManager.clearPlayerTokens(player);
            for (int i = 0; i < choices.length; i++) {
                DialogueChoice choice = choices[i];

                String secureToken = UUID.randomUUID().toString().substring(0, 8);
                WizardSessionManager.registerToken(secureToken, player, choice, messageKey);

                // Translate and style choice text
                MutableComponent choiceBtn =
                        Component.literal("[")
                            .append(Component.translatable("dialogue." + ForgottenCantrips.MOD_ID + ".choice." + choice.getKey()))
                        .append(Component.literal("]"))
                        .withStyle(ChatFormatting.GRAY);

                // Attach the Click and Hover events
                choiceBtn.withStyle(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fc_dialogue " + secureToken))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat." + ForgottenCantrips.MOD_ID + ".click_to_select")))
                );

                choicesRow.append(choiceBtn);

                // Add a divider if there are more options coming up
                if (i < choices.length - 1) {
                    choicesRow.append(Component.literal("  |  ").withStyle(ChatFormatting.DARK_GRAY));
                }
            }
            player.sendSystemMessage(choicesRow);
        }
    }

    private static MutableComponent getIconComponent(String icon) {
        // start with space to negate font change and have only the icon child have custom font
        return Component.literal("").append(Component.literal(icon).withStyle(
                style -> style.withFont(ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "wizard_icon"))
        )).append(Component.literal(" "));
    }

    private static Component convertSequenceToComponent(List<FormattedCharSequence> lines, int index) {
        if (index >= lines.size()) return Component.empty();

        StringBuilder builder = new StringBuilder();
        lines.get(index).accept((charIndex, style, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
        });

        return Component.literal(builder.toString());
    }

    private static MutableComponent getBlankSpacerComponent() {
        return Component.literal(" ".repeat(ICON_WIDTH_IN_SPACES));
    }

    public static void advanceDialogueFrom(DialogueChoice action, String from, Player player, WizardDialogueData cap) {
        if (action == DialogueChoice.BYE) {
            sendWizardReply(player, "goodbye");
            return;
        }

        // Example dialogue map (by Gemini)
        switch (from) {
            case "intro.1" -> {
                if (action == DialogueChoice.CONTINUE) {
                    sendWizardReply(player, "intro.2", DialogueChoice.CONTINUE, DialogueChoice.BYE);
                }
            }
            case "intro.2" -> {
                if (action == DialogueChoice.CONTINUE) {
                    sendWizardReply(player, "intro.3", DialogueChoice.CONTINUE, DialogueChoice.BYE);
                }
            }
            case "intro.3" -> {
                if (action == DialogueChoice.CONTINUE) {
                    sendWizardReply(player, "intro.4", DialogueChoice.CONTINUE, DialogueChoice.BYE);
                }
            }
            case "intro.4" -> {
                if (action == DialogueChoice.CONTINUE) {
                    cap.setGlobalState(WizardGlobalState.INTRODUCED);
                    // TODO: make dialogue choices be locked or unclocked based on some logic
                    sendWizardReply(player, "cantrips", DialogueChoice.CONTINUE, DialogueChoice.BYE);
                }
            }
            case "cantrips" -> {
                if (action == DialogueChoice.LIGHTNING_CANTRIP) {
                    if (cap.getBranchState("lightning") == WizardCantripBranchState.NOT_STARTED) {
                        sendWizardReply(player, "cantrip.lightning.1", DialogueChoice.BACK, DialogueChoice.BYE);
                    } else {
                        sendWizardReply(player, "cantrip_already_route", DialogueChoice.BACK, DialogueChoice.BYE);
                    }
                }
            }
            // Fallback or unhandled nodes
            default -> sendWizardReply(player, "back_again", DialogueChoice.BYE);
        }
    }
}
