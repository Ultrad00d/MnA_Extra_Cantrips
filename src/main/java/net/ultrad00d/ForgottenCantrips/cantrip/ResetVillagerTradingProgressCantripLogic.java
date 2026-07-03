package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;
import com.mna.entities.manaweaving.Manaweave;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.phys.EntityHitResult;

public class ResetVillagerTradingProgressCantripLogic extends CantripLogic {
    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        if (player.level().isClientSide()) return;

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

        currentVillager.setVillagerData(currentVillager.getVillagerData().setLevel(1));
        currentVillager.setVillagerXp(0);

        currentVillager.releasePoi(MemoryModuleType.JOB_SITE);
        currentVillager.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
        currentVillager.getBrain().eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);

        currentVillager.setVillagerData(currentVillager.getVillagerData().setProfession(VillagerProfession.NONE));
        currentVillager.refreshBrain((ServerLevel)player.level());



        targetEntity.playSound(SoundEvents.BEACON_DEACTIVATE, 1, 1.2F);
        for (int i = 0; i < 15; i++) {
            ((ServerLevel) player.level()).sendParticles(ParticleTypes.CLOUD, targetEntity.getX(), targetEntity.getY()+1.5F, (targetEntity.getZ()), 3, Math.random()*0.6,Math.random()*0.15, Math.random()*0.6, 0.0);
        }
    }
}
