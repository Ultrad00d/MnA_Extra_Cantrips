package net.ultrad00d.ForgottenCantrips.dialogue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.cantrip.CantripType;
import net.ultrad00d.ForgottenCantrips.util.ProgressionUtil;

import java.util.List;
import java.util.UUID;

public class WizardDialogue {
    public static final String[] ICON_ROWS = {"\uE000", "\uE001", "\uE002", "\uE003"};
    public static final int ICON_WIDTH = 36; //px
    public static final int ICON_WIDTH_IN_SPACES = ICON_WIDTH * 2 / 7; //px
    public static final int darkColorHEX = 0x504457;
    public static final int midColorHEX = 0x9682a1;
    public static final int lightColorHEX = 0xf5e3ff;
    public static final int highlightColorHex = 0xffffff;

    /**
     * @param player player whom to send dialogue message to
     * @param messageKey translation key for dialogue, example: dialogue.forgotten_cantrips.messageKey
     * @param cap player dialogue capability
     * @param choices available choices list
     */
    public static void sendWizardReply(Player player, String messageKey, WizardDialogueData cap, String... choices) {
        sendWizardReply(player, messageKey, messageKey, cap, choices);
    }

    /**
     * @param player player whom to send dialogue message to
     * @param pageKey string key that will be used to advance the dialogue from and as a session token
     * @param messageKey translation key for dialogue, example: dialogue.forgotten_cantrips.messageKey
     * @param cap player dialogue capability
     * @param choices available choices list
     */
    public static void sendWizardReply(Player player, String pageKey, String messageKey, WizardDialogueData cap, String... choices) {
        sendWizardReply(player, pageKey, Component.translatable("dialogue." + ForgottenCantrips.MOD_ID + ".wizard." + messageKey), cap, choices);
    }

    /**
     * @param player player whom to send dialogue message to
     * @param pageKey string key that will be used to advance the dialogue from and as a session token
     * @param messageComponent translated message component to send
     * @param cap player dialogue capability
     * @param choices available choices list
     */
    public static void sendWizardReply(Player player, String pageKey, Component messageComponent, WizardDialogueData cap, String... choices) {
        Minecraft client = Minecraft.getInstance();
        int chatWidthPX = ChatComponent.getWidth(client.options.chatWidth().get());
        int textSpaceWidth = chatWidthPX - ICON_WIDTH - 6; // 4px = margin

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSoundPacket(
                    ForgeRegistries.SOUND_EVENTS.getDelegateOrThrow(SoundEvents.VILLAGER_AMBIENT),
                    SoundSource.VOICE,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    1.0F,
                    1.6F,
                    player.getRandom().nextLong()
            ));
        }

        // first 4 rows contain an NPC icon, so the translated string gets split across rows
        List<FormattedCharSequence> splitLines = client.font.split(messageComponent, textSpaceWidth);

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
                choicesRow.append(buildChoiceButton(player, cap, firstKey, pageKey));

                // Second item in this row chunk (if it exists)
                if (i + 1 < choices.length) {
                    String secondKey = choices[i + 1];
                    choicesRow.append(Component.literal("  |  ").withStyle(s -> s.withColor(midColorHEX)));
                    choicesRow.append(buildChoiceButton(player, cap, secondKey, pageKey));
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

    private static MutableComponent buildChoiceButton(Player player, WizardDialogueData cap, String choiceKey, String pageKey) {
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
            WizardSessionManager.registerToken(secureToken, player, choiceKey, pageKey);

            return choiceText.withStyle(s -> s.withColor(midColorHEX))
                    .withStyle(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fc_dialogue " + secureToken))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat." + ForgottenCantrips.MOD_ID + ".click_to_select"))) //[cite: 2]
                    );
        }
    }

    private static Component convertSequenceToComponent(List<FormattedCharSequence> lines, int index) {
        if (index >= lines.size()) return Component.empty();

        MutableComponent lineComponent = Component.empty();
        StringBuilder currentChunkText = new StringBuilder();
        final Style[] lastStyle = {null};

        // Iterate through characters while preserving their exact layout/formatting styles
        lines.get(index).accept((charIndex, style, codePoint) -> {
            if (lastStyle[0] != null && !style.equals(lastStyle[0])) {
                // If the text chunk has no explicit color, apply the wizard's default light purple
                Style finalStyle = lastStyle[0].getColor() == null ? lastStyle[0].withColor(lightColorHEX) : lastStyle[0];
                lineComponent.append(Component.literal(currentChunkText.toString()).withStyle(finalStyle));
                currentChunkText.setLength(0);
            }
            currentChunkText.appendCodePoint(codePoint);
            lastStyle[0] = style;
            return true;
        });

        if (!currentChunkText.isEmpty() && lastStyle[0] != null) {
            Style finalStyle = lastStyle[0].getColor() == null ? lastStyle[0].withColor(lightColorHEX) : lastStyle[0];
            lineComponent.append(Component.literal(currentChunkText.toString()).withStyle(finalStyle));
        }

        return lineComponent;
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

    /** Advances the dialogue to the given player from given page and choice key
     * @param choice either CantripType cantripID or DialogueChoice value
     * @param from page id that this choice was chosen from
     * @param player player whom advance the dialogue to
     * @param cap player's dialogue capability
     */
    public static void advanceDialogueFrom(String choice, String from, Player player, WizardDialogueData cap) {
        if (DialogueChoice.BYE.getKey().equals(choice)) {
            sendWizardReply(player, "goodbye", cap);
            return;
        }
        else if (DialogueChoice.CANTRIPS_MENU.getKey().equals(choice)) {
            sendWizardReply(player, "cantrips", cap,
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

        switch (from) {
            case "intro.1" -> {
                sendWizardReply(player, "intro.2", cap, DialogueChoice.CONTINUE.getKey(), DialogueChoice.BYE.getKey());
            }
            case "intro.2" -> {
                sendWizardReply(player, "intro.3", cap, DialogueChoice.CONTINUE.getKey(), DialogueChoice.BYE.getKey());
            }
            case "intro.3" -> {
                sendWizardReply(player, "intro.4", cap, DialogueChoice.CONTINUE.getKey(), DialogueChoice.BYE.getKey());
            }
            case "intro.4", "back_again.2" -> {
                cap.setGlobalState(WizardGlobalState.INTRODUCED);
                sendWizardReply(player, "cantrips", cap,
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
                if (DialogueChoice.CONTINUE.getKey().equals(choice)) {
                    sendWizardReply(player, "back_again.2", cap,
                            DialogueChoice.CONTINUE.getKey(),
                            DialogueChoice.BYE.getKey()
                    );
                }
            }
            case "cantrips" -> {
                WizardCantripBranchState state = cap.getBranchState(choice, player.level().getDayTime());
                switch (state) {
                    case PART_1_ITEM_GIVEN, PART_2_ITEM_GIVEN -> {
                        sendWizardReply(player,
                                "cantrip_item_already_given_today",
                                cap,
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                    case PART_1, PART_2, PART_3 -> {
                        int partNum = state == WizardCantripBranchState.PART_1 ? 1 : (state == WizardCantripBranchState.PART_2 ? 2 : 3);
                        CantripQuestItem questItem = CantripQuestItem.fromId(choice);
                        Item requiredItem = questItem != null ? questItem.getItem(partNum - 1) : Items.BEDROCK;

                        Component itemHighlight = requiredItem.getDescription().copy().withStyle(s -> s.withColor(highlightColorHex).withItalic(true));
                        Component message = Component.translatable("dialogue." + ForgottenCantrips.MOD_ID + ".wizard.cantrip_item_" + partNum + "_quest", itemHighlight);

                        sendWizardReply(player,
                                "cantrip." + choice + ".part_" + partNum,
                                message,
                                cap,
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.GIVE_ITEM.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                    case PART_3_ITEM_GIVEN -> {
                        sendWizardReply(player, "cantrip_symbols_already_known", cap,
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                    case SPELL_LEARNED -> {
                        sendWizardReply(player, "cantrip_spell_already_known", cap,
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                    default -> {
                        sendWizardReply(player,
                                "cantrip." + choice + ".description",
                                cap,
                                DialogueChoice.CANTRIPS_MENU.getKey(),
                                DialogueChoice.CONTINUE.getKey(),
                                DialogueChoice.BYE.getKey());
                    }
                }
            }
            case "cantrip.lightning.description",
                 "cantrip.spectral_bed.description",
                 "cantrip.spectral_donkey.description",
                 "cantrip.spectral_boat.description",
                 "cantrip.spectral_armor.description",
                 "cantrip.empower_mana_cost_buff.description",
                 "cantrip.empower_damage_buff.description",
                 "cantrip.empower_cantrip_buff.description",
                 "cantrip.spectral_slime.description",
                 "cantrip.bubble_up.description" -> {

                // continue was chosen
                String cantripId = from.split("\\.")[1];
                cap.setBranchState(cantripId, WizardCantripBranchState.PART_1);

                CantripQuestItem questItem = CantripQuestItem.fromId(cantripId);
                Item requiredItem = questItem != null ? questItem.getItem(0) : Items.BEDROCK;

                Component itemHighlight = requiredItem.getDescription().copy().withStyle(s -> s.withColor(highlightColorHex).withItalic(true));
                Component message = Component.translatable("dialogue." + ForgottenCantrips.MOD_ID + ".wizard.cantrip_item_1_quest", itemHighlight);

                sendWizardReply(player,
                        from.substring(0, from.lastIndexOf('.')) + ".part_1",
                        message,
                        cap,
                        DialogueChoice.CANTRIPS_MENU.getKey(),
                        DialogueChoice.GIVE_ITEM.getKey(),
                        DialogueChoice.BYE.getKey());
            }
            case "cantrip.lightning.part_1",        "cantrip.lightning.part_2",         "cantrip.lightning.part_3",
                 "cantrip.spectral_bed.part_1",     "cantrip.spectral_bed.part_2",      "cantrip.spectral_bed.part_3",
                 "cantrip.spectral_donkey.part_1",  "cantrip.spectral_donkey.part_2",   "cantrip.spectral_donkey.part_3",
                 "cantrip.spectral_boat.part_1",    "cantrip.spectral_boat.part_2",     "cantrip.spectral_boat.part_3",
                 "cantrip.spectral_armor.part_1",   "cantrip.spectral_armor.part_2",    "cantrip.spectral_armor.part_3",
                 "cantrip.spectral_slime.part_1",   "cantrip.spectral_slime.part_2",    "cantrip.spectral_slime.part_3",
                 "cantrip.bubble_up.part_1",        "cantrip.bubble_up.part_2",         "cantrip.bubble_up.part_3",
                 "cantrip.empower_damage_buff.part_1", "cantrip.empower_damage_buff.part_2", "cantrip.empower_damage_buff.part_3",
                 "cantrip.empower_cantrip_buff.part_1", "cantrip.empower_cantrip_buff.part_2", "cantrip.empower_cantrip_buff.part_3",
                 "cantrip.empower_mana_cost_buff.part_1", "cantrip.empower_mana_cost_buff.part_2", "cantrip.empower_mana_cost_buff.part_3" -> {

                // give item was chosen
                int partNum = Integer.parseInt(from.substring(from.lastIndexOf('_') + 1));

                if (tryTakingItem(player, from)) {
                    WizardCantripBranchState nextState = switch (partNum) {
                        case 1 -> WizardCantripBranchState.PART_1_ITEM_GIVEN;
                        case 2 -> WizardCantripBranchState.PART_2_ITEM_GIVEN;
                        default -> WizardCantripBranchState.PART_3_ITEM_GIVEN;
                    };
                    String cantripId = from.split("\\.")[1];
                    cap.setBranchState(cantripId, nextState);

                    ResourceLocation partAdvancementId = ResourceLocation.fromNamespaceAndPath(
                            ForgottenCantrips.MOD_ID,
                            cantripId + "/part_" + partNum
                    );
                    ProgressionUtil.awardAdvancement(player, partAdvancementId);

                    long currentTicks = player.level().getDayTime();
                    long nextMorningTicks = ((currentTicks / 24000L) + 1) * 24000L;
                    cap.setUnlockTime(cantripId, nextMorningTicks);

                    sendWizardReply(player,
                            "cantrip_item_" + partNum + "_got",
                            cap,
                            DialogueChoice.CANTRIPS_MENU.getKey(),
                            DialogueChoice.BYE.getKey());
                } else {
                    sendWizardReply(player,
                            "wrong_item_given",
                            cap,
                            DialogueChoice.CANTRIPS_MENU.getKey(),
                            DialogueChoice.BYE.getKey());
                }
            }

            // Fallback or unhandled nodes
            default -> {
                ForgottenCantrips.LOGGER.error("Unknown key was used: {}", from);
                sendWizardReply(player, "fallback", cap, DialogueChoice.BYE.getKey());
            }
        }
    }

    private static boolean tryTakingItem(Player player, String dialoguePage) {
        String[] parts = dialoguePage.split("\\.");
        String cantripId = parts[1];
        int part = Integer.parseInt(parts[2].replace("part_", ""));
        CantripQuestItem questItems = CantripQuestItem.fromId(cantripId);
        if (questItems == null) { return false; }

        Item requiredItem = questItems.getItem(part - 1);
        ItemStack mainHandStack = player.getMainHandItem();
        ItemStack offHandStack = player.getOffhandItem();

        if (mainHandStack.is(requiredItem)) {
            if (!player.isCreative()) mainHandStack.shrink(1);
            return true;
        }
        if (offHandStack.is(requiredItem)) {
            if (!player.isCreative()) offHandStack.shrink(1);
            return true;
        }

        return false;
    }
}
