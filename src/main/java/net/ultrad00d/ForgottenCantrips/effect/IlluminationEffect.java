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
    private static final Map<UUID, BlockPos> PREVIOUS_POSITION = new HashMap<>();
    private static final Map<UUID, Set<BlockPos>> ILLUMINATION_LIGHTS = new HashMap<>();

    public static void onLivingTick(LivingTickEvent event) {
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
            case 0 -> 2;
            case 1 -> 3;
            default -> 4;
        } + IlluminationConfig.RADIUS_EXT.get(), IlluminationConfig.MAX_RADIUS.get());

        var center = player.blockPosition();
        if (center.equals(PREVIOUS_POSITION.get(player.getUUID()))) return;
        var current = new HashSet<BlockPos>();

        for (int x = -radius; x <= radius; x++)
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) continue;
                for (int y = -1; y <= 1; y++) {
                    var pos = center.offset(x, y, z);
                    if (level.getBlockState(pos).isAir())
                        current.add(pos.immutable());
                }
            }


        var previous = ILLUMINATION_LIGHTS.getOrDefault(player.getUUID(), Set.of());

        for (BlockPos pos : previous)
            if (!current.contains(pos))
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        for (BlockPos pos : current)
            if (!previous.contains(pos))
                level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);

        ILLUMINATION_LIGHTS.put(player.getUUID(), current);
        PREVIOUS_POSITION.put(player.getUUID(), center);
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