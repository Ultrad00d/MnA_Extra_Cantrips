package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;
import net.ultrad00d.ForgottenCantrips.screen.MusicDiscSlotProvider;

public class ForceConsumeCantripLogic extends CantripLogic {
    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        InteractionHand _other_hand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack otherHand = player.getItemInHand(_other_hand);

        if (otherHand.isEmpty()) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.force_consume.noitem"));
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ANVIL_LAND, player.getSoundSource(), 1.0F, 1.0F);
            return;
        }
    
        boolean effectApplied = applyEffect(player, otherHand);

        if (!effectApplied) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.force_consume.baditem"));
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ANVIL_LAND, player.getSoundSource(), 1.0F, 1.0F);
            return;
        }

        otherHand.shrink(1);
        if (otherHand.isEmpty()) {
            player.setItemInHand(_other_hand, ItemStack.EMPTY);
        }
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EAT, player.getSoundSource(), 1.0F, 1.0F);
    }

    private static boolean applyEffect(Player player, ItemStack stack) {
        Item item = stack.getItem();

        // Cake - apply nutrition as if the player ate the cake
        if (item == Blocks.CAKE.asItem()) {
            player.getFoodData().eat(14, 2.8F);
            return true;
        }
        // Melon - apply nutrition as if the player ate 9 melon slices (one melon block)
        if (item == Blocks.MELON.asItem()) {
            applyFoodProperties(player, Items.MELON_SLICE.getDefaultInstance().getFoodProperties(player), 9);
            return true;
        }
        // Dried Kelp Block - apply nutrition as if the player ate 9 dried kelp (one dried kelp block)
        if (item == Blocks.DRIED_KELP_BLOCK.asItem()) {
            applyFoodProperties(player, Items.DRIED_KELP.getDefaultInstance().getFoodProperties(player), 9);
            return true;
        }
        // Pumpkin - apply nutrition as if the player ate pumpkin pie (1 pie = 1 pumpkin block)
        if (item == Blocks.PUMPKIN.asItem() || item == Blocks.CARVED_PUMPKIN.asItem()) {
            applyFoodProperties(player, Items.PUMPKIN_PIE.getDefaultInstance().getFoodProperties(player), 1);
            return true;
        }
        // Hay Bale - apply nutrition as if the player ate 3 breads (1 hay bale = 9 wheats = 3 breads)
        if (item == Blocks.HAY_BLOCK.asItem()) {
            applyFoodProperties(player, Items.BREAD.getDefaultInstance().getFoodProperties(player), 3);
            return true;
        }
        // Bottle o' Enchanting - add experience to the player
        if (item == Items.EXPERIENCE_BOTTLE) {
            player.giveExperiencePoints(player.getRandom().nextInt(16) + 6);
            return true;
        }
        // Totem of Undying - apply the "Undying" effect to the player for 3 minutes, and prevent death once
        if (item == Items.TOTEM_OF_UNDYING) {
            player.addEffect(new MobEffectInstance(EffectRegistry.UNDYING.get(), 3600, 0));
            return true;
        }
        // Potions - apply effects of the potion to the player
        if (item instanceof PotionItem potionItem) {
            potionItem.finishUsingItem(stack, player.level(), player);
            return true;
        }
        // Milk Bucket - remove all effects from the player and remove the bucket
        if (item == Items.MILK_BUCKET) {
            player.removeAllEffects();
            return true;
        }
        // Shulker Shell - apply the "Levitation" effect to the player for 10 seconds
        if (item == Items.SHULKER_SHELL) {
            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 0));
            return true;
        }
        // Rabbit's Foot - apply the "Jump Boost" effect to the player for 30 seconds
        if (item == Items.RABBIT_FOOT) {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 600, 0));
            return true;
        }
        // Heart of the Sea - apply the "Conduit Power" effect to the player for 15 minutes
        if (item == Items.HEART_OF_THE_SEA) {
            player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 18000, 0));
            return true;
        }
        // Glowing Ink Sac, Glowing Dust, Spectral Arrow - apply glowing effect to the player for 30 seconds
        if (item == Items.GLOW_INK_SAC || item == Items.GLOWSTONE_DUST || item == Items.SPECTRAL_ARROW) {
            player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600, 0));
            return true;
        }
        // Glowstone, Sea Lantern, Shroomlight, Redstone Lamp - light up the area around the player for 3 minutes
        if (item == Items.GLOWSTONE || item == Items.SEA_LANTERN || item == Items.SHROOMLIGHT || item == Items.REDSTONE_LAMP) {
            player.addEffect(new MobEffectInstance(EffectRegistry.ILLUMINATION.get(), 3600, 0));
            return true;
        }
        // Redstone Torch, Torch, Soul Torch - light up the area around the player for 30 seconds
        if (item == Items.TORCH || item == Items.REDSTONE_TORCH || item == Items.SOUL_TORCH) {
            player.addEffect(new MobEffectInstance(EffectRegistry.ILLUMINATION.get(), 600, 0));
            return true;
        }

        // Piston - make player's step longer to 1 block for 2 minutes
        if (item == Items.PISTON || item == Items.STICKY_PISTON) {
            player.addEffect(new MobEffectInstance(EffectRegistry.AETHER_STRIDE.get(), 2400, 0));
            return true;
        }

        // Music Discs - play the music disc and store in hidden slot
        if (item instanceof RecordItem record) {
            Level level = player.level();
            if (!level.isClientSide) {
                // Stop previous disc sound and drop existing disc from hidden slot if any
                MusicDiscSlotProvider.stopDiscSound(player);
                ItemStack old = MusicDiscSlotProvider.getStoredDisc(player);
                if (!old.isEmpty()) {
                    player.drop(old, false);
                }

                // Store this disc in the hidden slot with timer data
                long duration = MusicDiscSlotProvider.getDiscDuration(Item.getId(item));
                MusicDiscSlotProvider.setStoredDisc(player, stack, level.getGameTime(), duration);

                // Play the sound
                var sound = record.getSound();
                var packet = new ClientboundSoundEntityPacket(
                    Holder.direct(sound),
                    SoundSource.RECORDS,
                    player,
                    1.0F, 1.0F,
                    player.getRandom().nextLong()
                );
                // Broadcast to all players tracking this player
                ((ServerLevel) level).getChunkSource().chunkMap.broadcast(player, packet);
                // Also send to the consuming player
                if (player instanceof ServerPlayer sp) sp.connection.send(packet);
            }
            return true;
        }

        // Some of the items might be implemented further:
        // (Optional) Dragon's Breath - apply the "Levitation" effect to the player for 10 seconds
        // (Optional) Flint and Steel, Fire Charge - set the player on fire for 5 seconds
        // (Optional) Gunpowder, TNT, End Crystal - create an explosion at the player's location (without destroying blocks)
        

        // (Technically implemented) Water Bucket, Water Bottle - remove the liquid container
        // (Technically implemented) Soups - apply corresponding nutrition and remove the bowl
        // (Technically implemented) Honey Bottle - apply nutrition and remove the bottle
        // Regular food items - apply nutrition based on the food properties of the item
        FoodProperties food = item.getFoodProperties(stack, player);
        if (food != null) {
            applyFoodProperties(player, food, 1);
            
            // Suspicious Stews - apply corresponding nutrition, apply effects, and remove the bowl
            if (item == Items.SUSPICIOUS_STEW) {
                var tag = stack.getTag();
                if (tag != null && tag.contains("Effects", 9)) {
                    var effectsList = tag.getList("Effects", 10);
                    for (int i = 0; i < effectsList.size(); i++) {
                        var effectTag = effectsList.getCompound(i);
                        int id = effectTag.getInt("EffectId");
                        int duration = effectTag.getInt("EffectDuration");
                        player.addEffect(new MobEffectInstance(MobEffect.byId(id), duration));
                    }
                }
                return true;
            }


            // Chorus Fruit - apply nutrition of fruit and teleport the player randomly
            if (item == Items.CHORUS_FRUIT) {
                double d0 = player.getX();
                double d1 = player.getY();
                double d2 = player.getZ();

                for(int i = 0; i < 16; ++i) {
                    double d3 = player.getX() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;
                    double d4 = Mth.clamp(player.getY() + (double)(player.getRandom().nextInt(16) - 8), (double)player.level().getMinBuildHeight(), (double)(player.level().getMinBuildHeight() + ((ServerLevel)player.level()).getLogicalHeight() - 1));
                    double d5 = player.getZ() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;
                    if (player.isPassenger())
                        player.stopRiding();

                    Vec3 vec3 = player.position();
                    player.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(player));
                    net.minecraftforge.event.entity.EntityTeleportEvent.ChorusFruit event = net.minecraftforge.event.ForgeEventFactory.onChorusFruitTeleport(player, d3, d4, d5);
                    if (event.isCanceled()) return false;
                    if (player.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
                        player.level().playSound((Player)null, d0, d1, d2, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                        player.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                        break;
                    }
                }  
            }
            return true;
        }
        return false;
    }

    private static void applyFoodProperties(Player player, FoodProperties food, int modifier) {
        if (food != null) {
            player.getFoodData().eat(food.getNutrition() * modifier, food.getSaturationModifier() * modifier);
            for (var pair : food.getEffects()) {
                if (player.getRandom().nextFloat() < pair.getSecond()) {
                    player.addEffect(new MobEffectInstance(pair.getFirst()));
                }
            }
        }
    }
}
