package net.ultrad00d.ForgottenCantrips.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.cantrip.CantripType;
import net.ultrad00d.ForgottenCantrips.util.ProgressionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AncientScrollItem extends Item {
    public AncientScrollItem(Properties pProperties) { super(pProperties.stacksTo(1)); }

    @Override public int getMaxStackSize(ItemStack stack) { return 1; }
    @Override @NotNull public Rarity getRarity(@NotNull ItemStack pStack) { return Rarity.EPIC; }
    @Override public boolean isFoil(@NotNull ItemStack pStack) { return true; }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);

        if (pPlayer instanceof ServerPlayer serverPlayer) {
            CompoundTag nbtData = itemStack.getOrCreateTag();
            if (!nbtData.contains("cantripID")) initNBT(nbtData);

            String cantripId = nbtData.getString("cantripID");

            CantripType cantrip = CantripType.fromId(cantripId);
            if (cantrip != null) {
                int playerTier = ProgressionUtil.getPlayerTier(serverPlayer);
                if (playerTier < cantrip.getTier()) {
                    serverPlayer.sendSystemMessage(Component.translatable("item.forgotten_cantrips.ancient_scroll.tier_too_low"));
                    return InteractionResultHolder.pass(itemStack);
                }
            }

            int stageToActivate = 1;
            for (int i = 3; i > 0; i--) {
                ResourceLocation advancementLocation = ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, cantripId + "/part_" + i);
                if (ProgressionUtil.hasAdvancement(serverPlayer, advancementLocation)) {
                    if (i == 3) {
                        serverPlayer.sendSystemMessage(Component.translatable("item.forgotten_cantrips.ancient_scroll.already_known"));
                        return InteractionResultHolder.pass(itemStack);
                    } else {
                        stageToActivate = i + 1;
                        break;
                    }
                }
            }

            ResourceLocation nextAdvancement = ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, cantripId + "/part_" + stageToActivate);
            ProgressionUtil.awardAdvancement(serverPlayer, nextAdvancement);

            if (!serverPlayer.isCreative()) itemStack.shrink(1);
            pLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.MASTER, 2.0F, 1.0F);
            pLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1.0F, 1.0F);
        }

        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        if (pStack.hasTag() && pStack.getTag().contains("cantripID")) {
            String cantripId = pStack.getTag().getString("cantripID");

            ChatFormatting color = switch (cantripId) {
                case "reset_villager" -> ChatFormatting.GREEN;
                case "devour" -> ChatFormatting.GRAY;
                case "colossus_oak" -> ChatFormatting.DARK_GREEN;
                default -> ChatFormatting.WHITE;
            };

            pTooltipComponents.add(Component.translatable("item.forgotten_cantrips.ancient_scroll.hint." + cantripId).withStyle(color));
        }
        pTooltipComponents.add(Component.translatable("item.forgotten_cantrips.ancient_scroll.hint.usage").withStyle(ChatFormatting.WHITE));
    }

    private String getRandomCantrip() {
        RandomSource random = RandomSource.create();
        return switch (random.nextInt(3)) {
            case 0 -> "reset_villager";
            case 1 -> "devour";
            default -> "colossus_oak";
        };
    }

    void initNBT(CompoundTag nbtData) {
        if (!nbtData.contains("cantripID")) {
            nbtData.putString("cantripID", getRandomCantrip());
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide()) {
            initNBT(pStack.getOrCreateTag());
        }
    }

    @NotNull
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        initNBT(stack.getOrCreateTag());
        return stack;
    }
}
