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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import net.ultrad00d.ForgottenCantrips.registry.ItemRegistry;

public class ResetVillagerTradingProgressCantripLogic extends CantripLogic {

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {

        if (player.level().isClientSide()) return;


        if (player.getOffhandItem().getItem() != Items.EMERALD) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.reset_villager_trading_progress.no_emerald"));
            return;
        }

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                player.level(),
                player,
                player.getEyePosition(1),
                player.getEyePosition(1.0F).add(player.getViewVector(1.0F).scale(player.getEntityReach())),
                player.getBoundingBox().inflate(player.getEntityReach()),
                entity -> !(entity.isSpectator() || entity == player || entity instanceof Manaweave)
        );
        if (entityHit == null) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.reset_villager_trading_progress.nothing_in_reach"));
            return;
        }

        Entity targetEntity;
        targetEntity = (entityHit).getEntity();

        if (targetEntity.getType() != EntityType.VILLAGER) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.reset_villager_trading_progress.not_a_villager"));
            return;
        }

        Villager currentVillager = (Villager)targetEntity;

        if (currentVillager.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.reset_villager_trading_progress.nitwit"));
            return;
        }



        //записываем память в NBT изумруда
        player.getOffhandItem().setCount(player.getOffhandItem().getCount()-1);


        ItemLike newEmerald = ItemRegistry.MEMORY_EMERALD.get();
        ItemStack newEmeraldStack = new ItemStack(newEmerald,1);

        CompoundTag emeraldNBT = newEmeraldStack.getOrCreateTag();
        emeraldNBT.putInt("level", currentVillager.getVillagerData().getLevel());
        emeraldNBT.putInt("exp", currentVillager.getVillagerXp());
        emeraldNBT.put("Offers", currentVillager.serializeNBT().get("Offers"));
        emeraldNBT.putString("profession", currentVillager.getVillagerData().getProfession().toString());
        emeraldNBT.putBoolean("initialized", true);

        player.getInventory().add(newEmeraldStack);



        wipeVillagerMemory(currentVillager);

        targetEntity.playSound(SoundEvents.BEACON_DEACTIVATE, 1, 1.2F);
        for (int i = 0; i < 15; i++) {
            ((ServerLevel) player.level()).sendParticles(ParticleTypes.CLOUD, targetEntity.getX(), targetEntity.getY()+1.5F, (targetEntity.getZ()), 3, Math.random()*0.6,Math.random()*0.15, Math.random()*0.6, 0.0);
        }
    }

    /*** вызывается из CapabilityEvents на ПКМ по жителю, вызывать только со стороны сервера ***/
    public static boolean applyMemories(ItemStack memoryEmeraldStack, Villager target) {

        CompoundTag emeraldNBT = memoryEmeraldStack.getOrCreateTag();

        if (!emeraldNBT.contains("initialized")) {
            return false;
        }

        wipeVillagerMemory(target);

        target.setVillagerXp(emeraldNBT.getInt("exp"));
        target.setVillagerData(target.getVillagerData().setLevel(emeraldNBT.getInt("level")).setProfession(ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(emeraldNBT.getString("profession")))));

        CompoundTag originalVillagerNBT = target.serializeNBT();
        originalVillagerNBT.remove("Offers");
        originalVillagerNBT.put("Offers", emeraldNBT.get("Offers"));
        target.deserializeNBT(originalVillagerNBT);

        target.refreshBrain((ServerLevel)target.level());


        target.playSound(SoundEvents.BEACON_ACTIVATE, 1, 1.2F);
        for (int i = 0; i < 15; i++) {
            ((ServerLevel) target.level()).sendParticles(ParticleTypes.CLOUD, target.getX(), target.getY()+1.5F, (target.getZ()), 3, Math.random()*0.6,Math.random()*0.15, Math.random()*0.6, 0.0);
        }
        return true;
    }

    /*** вызывать только со стороны сервера ***/
    public static void wipeVillagerMemory(Villager target) {
        target.setVillagerData(target.getVillagerData().setLevel(1));
        target.setVillagerXp(0);

        target.releasePoi(MemoryModuleType.JOB_SITE);
        target.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
        target.getBrain().eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);

        target.setVillagerData(target.getVillagerData().setProfession(VillagerProfession.NONE));
        target.refreshBrain((ServerLevel)target.level());
    }
}
