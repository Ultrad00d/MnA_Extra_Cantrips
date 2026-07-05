package net.ultrad00d.ForgottenCantrips.integration.lucent;

import com.legacy.lucent.api.EntityBrightness;
import com.legacy.lucent.api.plugin.ILucentPlugin;
import com.legacy.lucent.api.plugin.LucentPlugin;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

@OnlyIn(Dist.CLIENT)
@LucentPlugin
public class ForgottenCantripsLucentPlugin implements ILucentPlugin {

    @Override
    public String ownerModID() {
        return ForgottenCantrips.MOD_ID;
    }

    @Override
    public void getEntityLightLevel(EntityBrightness brightness) {
        if (brightness.getEntity() instanceof Player player) {
            var effect = player.getEffect(EffectRegistry.ILLUMINATION.get());
            if (effect != null) {
                int level = switch (effect.getAmplifier()) {
                    case 0 -> 12;
                    case 1 -> 14;
                    default -> 15;
                };
                brightness.setLightLevel(level);
            }
        }
    }
}