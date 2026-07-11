package net.ultrad00d.ForgottenCantrips.dialogue;

import com.mna.api.capabilities.IPlayerProgression;
import com.mna.capabilities.MACapabilityForgeEventHandlers;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
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
    public static final String ICON_L4 = "\uE003";
    public static final int ICON_WIDTH = 36; //px
    public static final int ICON_WIDTH_IN_SPACES = ICON_WIDTH * 2 / 7; //px

    public static void sendWizardReply(Player player, WizardDialogueData cap, String messageKey, DialogueChoice... choices) {
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
        Component row4 = getIconComponent(ICON_L4).append(convertSequenceToComponent(splitLines, 3));

        player.sendSystemMessage(row1);
        player.sendSystemMessage(row2);
        player.sendSystemMessage(row3);
        player.sendSystemMessage(row4);

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
                DialogueChoice choice = choices[i];
                boolean isLocked = isChoiceLocked(player, choice, cap);

                MutableComponent choiceText = Component.translatable("dialogue." + ForgottenCantrips.MOD_ID + ".choice." + choice.getKey());

                // If locked, prefix with a lock icon
                if (isLocked) choiceText = Component.literal("🔒 ").append(choiceText);

                MutableComponent choiceBtn =
                        Component.literal("[")
                            .append(choiceText)
                        .append(Component.literal("]"));

                if (isLocked) {
                    // Style for LOCKED options
                    choiceBtn.withStyle(ChatFormatting.DARK_GRAY)
                            .withStyle(style -> style.withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    Component.translatable("chat." + ForgottenCantrips.MOD_ID + ".locked")
                            )));
                } else {
                    // Style for AVAILABLE options
                    String secureToken = UUID.randomUUID().toString().substring(0, 8);
                    WizardSessionManager.registerToken(secureToken, player, choice, messageKey);

                    choiceBtn.withStyle(ChatFormatting.GRAY)
                            .withStyle(style -> style
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fc_dialogue " + secureToken))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat." + ForgottenCantrips.MOD_ID + ".click_to_select")))
                            );
                }
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

    private static boolean isChoiceLocked(Player player, DialogueChoice choice, WizardDialogueData cap) {
        // Example requirement logic based on branch states
        if (choice == DialogueChoice.LIGHTNING_CANTRIP) {
            int tier = player.getCapability(PlayerProgressionProvider.PROGRESSION)
                    .map(IPlayerProgression::getTier)
                    .orElse(1);

            return cap.getBranchState("lightning") != WizardCantripBranchState.SPELL_LEARNED;
//            return false;
        }

        // Add logic here for any new cantrips you add to DialogueChoice
        // if (choice == DialogueChoice.FIRE_CANTRIP) { ... }

        return false;
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
                if (action == DialogueChoice.CONTINUE) {
                    sendWizardReply(player, cap, "intro.2", DialogueChoice.CONTINUE, DialogueChoice.BYE);
                }
            }
            case "intro.2" -> {
                if (action == DialogueChoice.CONTINUE) {
                    sendWizardReply(player, cap, "intro.3", DialogueChoice.CONTINUE, DialogueChoice.BYE);
                }
            }
            case "intro.3" -> {
                if (action == DialogueChoice.CONTINUE) {
                    sendWizardReply(player, cap, "intro.4", DialogueChoice.CONTINUE, DialogueChoice.BYE);
                }
            }
            case "intro.4" -> {
                if (action == DialogueChoice.CONTINUE) {
                    cap.setGlobalState(WizardGlobalState.INTRODUCED);
                    // TODO: make dialogue choices be locked or unclocked based on some logic
                    sendWizardReply(player, cap, "cantrips", DialogueChoice.CONTINUE, DialogueChoice.BYE);
                }
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
            default -> sendWizardReply(player, cap, "back_again", DialogueChoice.BYE);
        }
    }
}
