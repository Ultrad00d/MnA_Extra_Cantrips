package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;
import com.mna.entities.manaweaving.Manaweave;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import net.ultrad00d.ForgottenCantrips.registry.ItemRegistry;

public class ResetVillagerTradingProgressCantripLogic implements ICantripLogic {
    @Override
    public String getCantripId() { return "reset_villager"; }

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        ItemStack offhandItem = player.getOffhandItem();
        ItemStack mainHandItem = player.getMainHandItem();

        boolean isOffhandEmerald = (offhandItem.is(Items.EMERALD) || offhandItem.is(ItemRegistry.MEMORY_EMERALD.get()));
        boolean isMainHandEmerald = (mainHandItem.is(Items.EMERALD) || mainHandItem.is(ItemRegistry.MEMORY_EMERALD.get()));

        if (!isOffhandEmerald && !isMainHandEmerald) {
            player.sendSystemMessage(Component.translatable(getLangKey("no_emerald")));
            return;
        }

        ItemStack activeEmeraldItem = isOffhandEmerald ? offhandItem : mainHandItem;
        boolean usingMemoryEmerald = (activeEmeraldItem.is(ItemRegistry.MEMORY_EMERALD.get()));


        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                serverLevel,
                player,
                player.getEyePosition(1),
                player.getEyePosition(1.0F).add(player.getViewVector(1.0F).scale(player.getEntityReach())),
                player.getBoundingBox().inflate(player.getEntityReach()),
                entity -> !(entity.isSpectator() || entity == player || entity instanceof Manaweave)
        );
        if (entityHit == null) {
            player.sendSystemMessage(Component.translatable(getLangKey("nothing_in_reach")));
            return;
        }

        Entity targetEntity = entityHit.getEntity();
        if (!(targetEntity instanceof Villager villager)) {
            player.sendSystemMessage(Component.translatable(getLangKey("not_a_villager")));
            return;
        }

        if (villager.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
            player.sendSystemMessage(Component.translatable(getLangKey("nitwit")));
            return;
        }

        // writing data FROM emerald
        if (usingMemoryEmerald) {
            if (applyMemories(activeEmeraldItem, villager, serverLevel)) {
                activeEmeraldItem.shrink(1);
                player.getInventory().add(new ItemStack(Items.EMERALD, 1));
            } else {
                player.sendSystemMessage(Component.translatable(getLangKey("empty_warning")));
            }
            return;
        }

        // writing data TO emerald
        activeEmeraldItem.shrink(1);

        ItemStack newEmeraldStack = new ItemStack(ItemRegistry.MEMORY_EMERALD.get(), 1);
        CompoundTag emeraldNBT = newEmeraldStack.getOrCreateTag();
        emeraldNBT.putBoolean("initialized", true);

        if (!(villager.serializeNBT().contains("Offers"))) {
            emeraldNBT.putBoolean("jobless",true);
        } else {

            emeraldNBT.putInt("level", villager.getVillagerData().getLevel());
            emeraldNBT.putInt("exp", villager.getVillagerXp());
            emeraldNBT.put("Offers", villager.serializeNBT().get("Offers"));
            emeraldNBT.putString("profession", villager.getVillagerData().getProfession().toString());
        }

        giveItemOrDrop(player, newEmeraldStack);
        wipeVillagerMemory(villager, serverLevel);
        playEffects(serverLevel, villager);
    }

    public static boolean applyMemories(ItemStack memoryEmeraldStack, Villager target, ServerLevel level) {
        CompoundTag emeraldNBT = memoryEmeraldStack.getOrCreateTag();
        if (!emeraldNBT.contains("initialized")) return false;

        wipeVillagerMemory(target, level);

        if (!(emeraldNBT.contains("jobless"))) {
            target.setVillagerXp(emeraldNBT.getInt("exp"));
            target.setVillagerData(target.getVillagerData().setLevel(emeraldNBT.getInt("level")).setProfession(ForgeRegistries.VILLAGER_PROFESSIONS.getValue(ResourceLocation.parse(emeraldNBT.getString("profession")))));

            CompoundTag originalVillagerNBT = target.serializeNBT();
            originalVillagerNBT.remove("Offers");
            originalVillagerNBT.put("Offers", emeraldNBT.get("Offers"));
            target.deserializeNBT(originalVillagerNBT);
        }

        target.refreshBrain(level);
        playEffects(level, target);
        return true;
    }

    public static void wipeVillagerMemory(Villager target, ServerLevel level) {
        target.setVillagerData(target.getVillagerData().setLevel(1).setProfession(VillagerProfession.NONE));
        target.setVillagerXp(0);

        target.releasePoi(MemoryModuleType.JOB_SITE);
        target.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
        target.getBrain().eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);

        target.refreshBrain(level);
    }

    private static void giveItemOrDrop(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static void playEffects(ServerLevel level, Villager target) {
        target.playSound(SoundEvents.BEACON_ACTIVATE, 1.0F, 1.2F);
        for (int i = 0; i < 15; i++) {
            level.sendParticles(
                    ParticleTypes.CLOUD,
                    target.getX(),
                    target.getY() + 1.5,
                    target.getZ(),
                    3,
                    Math.random() * 0.6,
                    Math.random() * 0.15,
                    Math.random() * 0.6,
                    0.0
            );
        }
    }
}
