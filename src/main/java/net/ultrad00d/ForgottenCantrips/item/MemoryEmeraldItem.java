package net.ultrad00d.ForgottenCantrips.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MemoryEmeraldItem extends Item {
    public MemoryEmeraldItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    public static final String NAME = "memory_emerald";


    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {

        CompoundTag nbtData = pStack.getTag();

        if (nbtData != null && nbtData.contains("initialized")) {
            if (nbtData.contains("jobless")) {
                pTooltipComponents.add(Component.translatable("item.forgotten_cantrips.memory_emerald.jobless").withStyle(ChatFormatting.LIGHT_PURPLE));
            } else {
                String tooltip = "LV" + nbtData.getInt("level") + " " + (nbtData.getString("profession").substring(0, 1).toUpperCase()) + nbtData.getString("profession").substring(1);
                pTooltipComponents.add(Component.literal(tooltip).withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        } else {
            pTooltipComponents.add(Component.translatable("item.forgotten_cantrips.memory_emerald.empty").withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        pTooltipComponents.add(Component.translatable("item.forgotten_cantrips.memory_emerald.instructions").withStyle(ChatFormatting.WHITE));
    }

}
