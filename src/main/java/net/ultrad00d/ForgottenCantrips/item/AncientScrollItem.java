package net.ultrad00d.ForgottenCantrips.item;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import org.jetbrains.annotations.Nullable;
import org.joml.Random;

import java.util.List;



public class AncientScrollItem extends Item {
    public AncientScrollItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) { return 1; }

    @Override
    public Rarity getRarity(ItemStack pStack) { return Rarity.EPIC; }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        ItemStack thisItemStackClient = pPlayer.getItemInHand(pUsedHand);

        if (pPlayer instanceof ServerPlayer serverPlayer) {

            ItemStack thisItemStackServer = serverPlayer.getItemInHand(pUsedHand);

            if (!(thisItemStackServer.hasTag())) {
                initNBT(thisItemStackServer.getOrCreateTag());
            }
            CompoundTag nbtData = thisItemStackServer.getTag();

            String currentAdvancementLocation;
            Advancement currentAdvancement;
            int stageToActivate = 1;

            for (int i = 3; i > 0; i--) {
                currentAdvancementLocation = nbtData.getString("cantripID") + "/part_" + i;
                currentAdvancement = serverPlayer.getServer().getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, currentAdvancementLocation));
                if (serverPlayer.getAdvancements().getOrStartProgress(currentAdvancement).isDone()) {
                    if (i == 3) {
                        serverPlayer.sendSystemMessage(Component.translatable("item.forgotten_cantrips.ancient_scroll.already_known"));
                        return InteractionResultHolder.pass(thisItemStackServer);
                    } else {
                        stageToActivate = i + 1;
                        break;
                    }
                }
            }

            //if the loop completed without changing stageToActivate, it defaults to unlocking the first stage

            currentAdvancementLocation = nbtData.getString("cantripID") + "/part_" + stageToActivate;
            currentAdvancement = serverPlayer.getServer().getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, currentAdvancementLocation));
            serverPlayer.getAdvancements().award(currentAdvancement, "criterion");

            if (!(serverPlayer.isCreative())) {
                thisItemStackServer.setCount(thisItemStackServer.getCount()-1);
            }

            pLevel.playSound(null,serverPlayer.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.MASTER, 2F, 1F);
            pLevel.playSound(null,serverPlayer.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1F, 1F);

            return InteractionResultHolder.pass(thisItemStackServer);
        } else {
            return InteractionResultHolder.pass(thisItemStackClient);
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {


        if (pStack.hasTag()) {

            CompoundTag nbtData = pStack.getTag();

             if (nbtData.contains("cantripID")) {

                 switch (nbtData.getString("cantripID")) {
                     case "reset_villager":
                         pTooltipComponents.add(Component.translatable("item.forgotten_cantrips.ancient_scroll.hint.reset_villager").withStyle(ChatFormatting.GREEN));
                         break;

                     case "devour":
                         pTooltipComponents.add(Component.translatable("item.forgotten_cantrips.ancient_scroll.hint.devour").withStyle(ChatFormatting.GRAY));
                         break;

                     case "colossus_oak":
                         pTooltipComponents.add(Component.translatable("item.forgotten_cantrips.ancient_scroll.hint.colossus_oak").withStyle(ChatFormatting.DARK_GREEN));
                         break;
                 }
             }
        }
        pTooltipComponents.add(Component.translatable("item.forgotten_cantrips.ancient_scroll.hint.usage").withStyle(ChatFormatting.WHITE));
    }

    void initNBT(CompoundTag nbtData) {
        switch (new Random().nextInt(3)) {
            case 0: { nbtData.putString("cantripID","reset_villager"); return; }
            case 1: { nbtData.putString("cantripID","devour"); return; }
            case 2: default: { nbtData.putString("cantripID","colossus_oak"); return; }
        }

    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pLevel.isClientSide()) return;
        if (!(pStack.hasTag())) {
            initNBT(pStack.getOrCreateTag());
        }
    }


    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        CompoundTag tag = stack.getOrCreateTag();

        switch (new Random().nextInt(3)) {
            case 0: { tag.putString("cantripID","reset_villager"); break; }
            case 1: { tag.putString("cantripID","devour"); break; }
            case 2: default: { tag.putString("cantripID","colossus_oak"); break; }
        }

        return stack;
    }

    }

