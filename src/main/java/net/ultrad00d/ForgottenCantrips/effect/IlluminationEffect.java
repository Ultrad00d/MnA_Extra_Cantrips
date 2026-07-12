package net.ultrad00d.ForgottenCantrips.effect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.ultrad00d.ForgottenCantrips.config.IlluminationConfig;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public class IlluminationEffect extends MobEffect {
    public IlluminationEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFEEE);
    }
    private static final Map<UUID, Set<BlockPos>> ILLUMINATION_LIGHTS = new HashMap<>();

    private static int drift(double fv) {
        final double DRIFT_THRESHOLD = 0.35;
        if (fv <= DRIFT_THRESHOLD) return -1;
        if (fv >= 1.0 - DRIFT_THRESHOLD) return 1;
        return 0;
    }

    public static void onLivingTick(LivingTickEvent event) {
        if (net.minecraftforge.fml.ModList.get().isLoaded("lucent")) return;
        if (!(event.getEntity() instanceof Player player)) return;
        var level = player.level();
        if (level.isClientSide) return;

        var effect = player.getEffect(EffectRegistry.ILLUMINATION.get());
        if (effect == null) {
            var removed = ILLUMINATION_LIGHTS.remove(player.getUUID());
            if (removed != null)
                for (BlockPos pos : removed)
                    if (level.getBlockState(pos).is(Blocks.LIGHT))
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return;
        }

        if (player.tickCount % IlluminationConfig.TICK_PERIOD.get() != 0) return;

        int radius = Math.min(switch (effect.getAmplifier()) {
            case 0 -> 9;
            case 1 -> 11;
            case 2 -> 13;
            default -> 15;
        } + IlluminationConfig.RADIUS_EXT.get(), 15);

        var center = player.blockPosition();
        var current = new HashSet<BlockPos>();
        double fx = player.getX() - Math.floor(player.getX());
        double fz = player.getZ() - Math.floor(player.getZ());

        
        {
            var pos = center.offset(drift(fx), 0, drift(fz));
            if (level.getBlockState(pos).isAir() || level.getBlockState(pos).is(Blocks.LIGHT))
                current.add(pos.immutable());
        }

        for (int y = -1; y <= 1; y++) {
            var pos = center.offset(0, y, 0);
            if (level.getBlockState(pos).isAir() || level.getBlockState(pos).is(Blocks.LIGHT))
                current.add(pos.immutable());
        }

        var previous = ILLUMINATION_LIGHTS.getOrDefault(player.getUUID(), Set.of());

        for (BlockPos pos : previous)
            if (!current.contains(pos))
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        for (BlockPos pos : current)
            if (!previous.contains(pos))
                level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, radius), 3);

        ILLUMINATION_LIGHTS.put(player.getUUID(), current);
    }
    public static void onPlayerLogout(PlayerLoggedOutEvent event) {
        var player = event.getEntity();
        var removed = ILLUMINATION_LIGHTS.remove(player.getUUID());
        if (removed != null) {
            var level = player.level();
            for (BlockPos pos : removed)
                if (level.getBlockState(pos).is(Blocks.LIGHT))
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }
}