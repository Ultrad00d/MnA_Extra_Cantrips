package net.ultrad00d.ForgottenCantrips.dialogue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;

import java.util.List;

public class WizardDialogue {
    public static final String ICON_L1 = "\uE000";
    public static final String ICON_L2 = "\uE001";
    public static final String ICON_L3 = "\uE002";
    public static final int ICON_WIDTH = 27; //px

    public static void sendWizardReply(Player player, String messageKey) {
        Minecraft client = Minecraft.getInstance();

        double chatWidthSetting = client.options.chatWidth().get();
        int chatWidthPX = ChatComponent.getWidth(chatWidthSetting);
        int textSpaceWidth = chatWidthPX - ICON_WIDTH - 4; // 4px = margin

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
                player.sendSystemMessage(convertSequenceToComponent(splitLines, i));
            }
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

//    public static void displaySpellOptions(Player player) {
//        player.sendSystemMessage(Component.literal("--- Select a Spell Topic ---").withStyle(ChatFormatting.GOLD));
//
//        // Create clickable buttons for each option
//        player.sendSystemMessage(createClickableOption("🔥 Fireball", "fireball"));
//        player.sendSystemMessage(createClickableOption("❄ Frostbolt", "frostbolt"));
//        player.sendSystemMessage(createClickableOption("⚡ Teleport", "teleport"));
//    }
//
//    public static Component createClickableOption(String visibleText, String spellId) {
//        return Component.literal(visibleText)
//                .withStyle(style -> style.withColor(ChatFormatting.AQUA).withUnderlined(true)
//                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wizardtalk branch " + spellId)));
//    }
}
