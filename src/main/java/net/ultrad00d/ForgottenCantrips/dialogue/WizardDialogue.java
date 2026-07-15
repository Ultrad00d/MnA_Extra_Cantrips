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
import net.ultrad00d.ForgottenCantrips.cantrip.CantripType;
import net.ultrad00d.ForgottenCantrips.util.ProgressionUtil;

import java.util.List;
import java.util.UUID;

public class WizardDialogue {
    public static final String[] ICON_ROWS = {"\uE000", "\uE001", "\uE002", "\uE003"};
    public static final int ICON_WIDTH = 36; //px
    public static final int ICON_WIDTH_IN_SPACES = ICON_WIDTH * 2 / 7; //px

    public static void sendWizardReply(Player player, WizardDialogueData cap, String messageKey, DialogueChoice... choices) {
        Minecraft client = Minecraft.getInstance();
        int chatWidthPX = ChatComponent.getWidth(client.options.chatWidth().get());
        int textSpaceWidth = chatWidthPX - ICON_WIDTH - 6; // 4px = margin

        // first 4 rows contain an NPC icon, so the translated string gets split across rows
        Component fullMessage = Component.translatable("dialogue." + ForgottenCantrips.MOD_ID + ".wizard." + messageKey);
        List<FormattedCharSequence> splitLines = client.font.split(fullMessage, textSpaceWidth);

        for (int i = 0; i < Math.max(splitLines.size(), 4); i++) {
            Component textLine = convertSequenceToComponent(splitLines, i);
            Component formattedRow = (i < 4)
                    ? getIconComponent(ICON_ROWS[i]).append(textLine)
                    : getBlankSpacerComponent().append(textLine);
            player.sendSystemMessage(formattedRow);
        }

        if (splitLines.size() > 4) {
            for (int i = 4; i < splitLines.size(); i++) {
                Component indentedRow = getBlankSpacerComponent().append(convertSequenceToComponent(splitLines, i));
                player.sendSystemMessage(indentedRow);
            }
        }

        if (choices.length > 0) {
            MutableComponent choicesRow = getBlankSpacerComponent();
            WizardSessionManager.clearPlayerTokens(player);
            for (int i = 0; i < choices.length; i++) {
                choicesRow.append(buildChoiceButton(player, cap, choices[i], messageKey));
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

    private static MutableComponent getBlankSpacerComponent() {
        return Component.literal(" ".repeat(ICON_WIDTH_IN_SPACES));
    }

    private static MutableComponent buildChoiceButton(Player player, WizardDialogueData cap, DialogueChoice choice, String messageKey) {
        boolean isLocked = isChoiceLocked(player, choice, cap);
        MutableComponent choiceText = Component.translatable("dialogue." + ForgottenCantrips.MOD_ID + ".choice." + choice.getKey());

        if (isLocked) {
            choiceText = Component.literal("🔒 ").append(choiceText);
        }

        MutableComponent choiceBtn = Component.literal("[").append(choiceText).append(Component.literal("]"));

        if (isLocked) {
            return choiceBtn.withStyle(ChatFormatting.DARK_GRAY)
                    .withStyle(style -> style.withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Component.translatable("chat." + ForgottenCantrips.MOD_ID + ".locked")
                    )));
        } else {
            String secureToken = UUID.randomUUID().toString().substring(0, 8);
            WizardSessionManager.registerToken(secureToken, player, choice, messageKey);

            return choiceBtn.withStyle(ChatFormatting.GRAY)
                    .withStyle(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fc_dialogue " + secureToken))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat." + ForgottenCantrips.MOD_ID + ".click_to_select")))
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

        return Component.literal(builder.toString());
    }

    private static boolean isChoiceLocked(Player player, DialogueChoice choice, WizardDialogueData cap) {
        return switch (choice) {
            case CONTINUE, BACK, BYE, CANTRIPS_MENU -> false;
            default -> {
                int tier = ProgressionUtil.getPlayerTier(player);
                int requiredTier = CantripType.fromDialogueChoice(choice).getTier();

                boolean alreadyLearned = cap.getBranchState(choice.getKey()) == WizardCantripBranchState.SPELL_LEARNED;

                yield tier < requiredTier || alreadyLearned;
            }
        };
    }

    public static void advanceDialogueFrom(DialogueChoice action, String from, Player player, WizardDialogueData cap) {
        if (action == DialogueChoice.BYE) {
            cap.setGlobalState(WizardGlobalState.NOT_MET);
            sendWizardReply(player, cap, "goodbye");
            return;
        }

        // Example dialogue map (by Gemini)
        switch (from) {
            case "intro.1" -> {
                sendWizardReply(player, cap, "intro.2", DialogueChoice.CONTINUE, DialogueChoice.BYE);
            }
            case "intro.2" -> {
                sendWizardReply(player, cap, "intro.3", DialogueChoice.CONTINUE, DialogueChoice.BYE);
            }
            case "intro.3" -> {
                sendWizardReply(player, cap, "intro.4", DialogueChoice.CONTINUE, DialogueChoice.BYE);
            }
            case "intro.4" -> {
                cap.setGlobalState(WizardGlobalState.INTRODUCED);
                sendWizardReply(player, cap, "cantrips",
                        DialogueChoice.LIGHTNING_CANTRIP,
                        DialogueChoice.SPECTRAL_BED_CANTRIP,
                        DialogueChoice.SPECTRAL_DONKEY_CANTRIP,
                        DialogueChoice.SPECTRAL_BOAT_CANTRIP,
//                        DialogueChoice.SPECTRAL_ARMOR_CANTRIP,
//                        DialogueChoice.EMPOWER_CANTRIP,
//                        DialogueChoice.SPECTRAL_SLIME,
//                        DialogueChoice.BUBBLE_UP_CANTRIP,
                        DialogueChoice.BYE);
            }
            case "back_again.1" -> {
                if (action == DialogueChoice.CONTINUE) {
                    sendWizardReply(player, cap, "back_again.2",
                            DialogueChoice.CONTINUE,
                            DialogueChoice.BYE);
                }
            }
            case "back_again.2" -> {
                sendWizardReply(player, cap, "cantrips",
                        DialogueChoice.LIGHTNING_CANTRIP,
                        DialogueChoice.SPECTRAL_BED_CANTRIP,
                        DialogueChoice.SPECTRAL_DONKEY_CANTRIP,
                        DialogueChoice.SPECTRAL_BOAT_CANTRIP,
//                        DialogueChoice.SPECTRAL_ARMOR_CANTRIP,
//                        DialogueChoice.EMPOWER_CANTRIP,
//                        DialogueChoice.SPECTRAL_SLIME,
//                        DialogueChoice.BUBBLE_UP_CANTRIP,
                        DialogueChoice.BYE);
            }
            case "cantrips" -> {
                if (action == DialogueChoice.LIGHTNING_CANTRIP) {
                    if (cap.getBranchState("lightning") == WizardCantripBranchState.NOT_STARTED) {
                        sendWizardReply(player, cap, "cantrip.lightning.1", DialogueChoice.BACK, DialogueChoice.BYE);
                    } else {
                        sendWizardReply(player, cap, "cantrip_already_route", DialogueChoice.BACK, DialogueChoice.BYE);
                    }
                }
            }
            // Fallback or unhandled nodes
            default -> sendWizardReply(player, cap, "fallback", DialogueChoice.BYE);
        }
    }
}
