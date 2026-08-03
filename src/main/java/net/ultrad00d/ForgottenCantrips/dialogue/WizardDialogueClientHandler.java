package net.ultrad00d.ForgottenCantrips.dialogue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.cantrip.CantripType;
import net.ultrad00d.ForgottenCantrips.network.ClientboundWizardDialoguePacket;

import java.util.List;

public class WizardDialogueClientHandler {
    public static void renderDialogue(Component messageComponent, List<ClientboundWizardDialoguePacket.ChoiceData> choices) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        int chatWidthPX = ChatComponent.getWidth(client.options.chatWidth().get());
        int textSpaceWidth = chatWidthPX - WizardDialogue.ICON_WIDTH - 6;

        List<FormattedCharSequence> splitLines = client.font.split(messageComponent, textSpaceWidth);

        for (int i = 0; i < Math.max(splitLines.size(), 4); i++) {
            Component textLine = convertSequenceToComponent(splitLines, i);
            Component formattedRow = (i < 4)
                    ? getIconComponent(WizardDialogue.ICON_ROWS[i]).append(textLine)
                    : getBlankSpacerComponent().append(textLine);
            client.player.sendSystemMessage(formattedRow);
        }

        if (!choices.isEmpty()) {
            for (int i = 0; i < choices.size(); i += 2) {
                MutableComponent choicesRow = getBlankSpacerComponent();

                choicesRow.append(buildChoiceButton(choices.get(i)));

                if (i + 1 < choices.size()) {
                    choicesRow.append(Component.literal("  |  ").withStyle(s -> s.withColor(WizardDialogue.midColorHEX)));
                    choicesRow.append(buildChoiceButton(choices.get(i + 1)));
                }
                client.player.sendSystemMessage(choicesRow);
            }
        }
    }

    private static MutableComponent getIconComponent(String icon) {
        return Component.literal("").append(Component.literal(icon).withStyle(
                style -> style.withFont(ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "wizard_icon"))
        )).append(Component.literal(" "));
    }

    private static MutableComponent getBlankSpacerComponent() {
        return Component.literal(" ".repeat(WizardDialogue.ICON_WIDTH_IN_SPACES));
    }

    private static MutableComponent buildChoiceButton(ClientboundWizardDialoguePacket.ChoiceData choice) {
        CantripType cantrip = CantripType.fromId(choice.choiceKey());
        MutableComponent choiceText = (cantrip != null)
                ? Component.translatable("cantrip." + ForgottenCantrips.MOD_ID + "." + choice.choiceKey())
                : Component.translatable("dialogue." + ForgottenCantrips.MOD_ID + ".choice." + choice.choiceKey());

        choiceText = Component.literal("[").append(choiceText).append(Component.literal("]"));

        if (choice.isLocked()) {
            choiceText = Component.literal("🔒 ").withStyle(s -> s.withColor(WizardDialogue.lightColorHEX)).append(choiceText);
            return choiceText.withStyle(s -> s.withColor(WizardDialogue.darkColorHEX))
                    .withStyle(style -> style.withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Component.translatable("chat." + ForgottenCantrips.MOD_ID + ".locked")
                    )));
        } else {
            return choiceText.withStyle(s -> s.withColor(WizardDialogue.midColorHEX))
                    .withStyle(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fc_dialogue " + choice.token()))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat." + ForgottenCantrips.MOD_ID + ".click_to_select")))
                    );
        }
    }

    private static Component convertSequenceToComponent(List<FormattedCharSequence> lines, int index) {
        if (index >= lines.size()) return Component.empty();

        MutableComponent lineComponent = Component.empty();
        StringBuilder currentChunkText = new StringBuilder();
        final Style[] lastStyle = {null};

        lines.get(index).accept((charIndex, style, codePoint) -> {
            if (lastStyle[0] != null && !style.equals(lastStyle[0])) {
                Style finalStyle = lastStyle[0].getColor() == null ? lastStyle[0].withColor(WizardDialogue.lightColorHEX) : lastStyle[0];
                lineComponent.append(Component.literal(currentChunkText.toString()).withStyle(finalStyle));
                currentChunkText.setLength(0);
            }
            currentChunkText.appendCodePoint(codePoint);
            lastStyle[0] = style;
            return true;
        });

        if (!currentChunkText.isEmpty() && lastStyle[0] != null) {
            Style finalStyle = lastStyle[0].getColor() == null ? lastStyle[0].withColor(WizardDialogue.lightColorHEX) : lastStyle[0];
            lineComponent.append(Component.literal(currentChunkText.toString()).withStyle(finalStyle));
        }

        return lineComponent;
    }
}
