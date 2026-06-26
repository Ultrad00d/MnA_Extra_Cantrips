package net.ultrad00d.ForgottenCantrips.registry;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.tools.RLoc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.ChorusFruitItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.gameevent.GameEvent;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBedBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBoat;

public class CantripRegistry {
    public static void register() {
        com.mna.cantrips.CantripRegistry registry = com.mna.cantrips.CantripRegistry.INSTANCE;

        // Lightning Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "lightning"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/lightning.png"),
                1,
                CantripRegistry::lightning,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Bed Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_bed"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_bed.png"),
                1,
                CantripRegistry::placeBed,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Boat Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_boat"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_boat.png"),
                1,
                CantripRegistry::summonBoat,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Force Consume Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "force_consume"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/force_consume.png"),
                1,
                CantripRegistry::consume,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
    }

    public static void lightning(Player player, ICantrip cantrip, InteractionHand hand) {
        double range;
        try {
            range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        } catch (Throwable var14) {
            range = 4.5F;
        }
        Vec3 target = player.pick(range, 0.0F, true).getLocation();
        Level level = player.level();
        if (!level.canSeeSky(new BlockPos((int) target.x, (int) target.y, (int) target.z))) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.lightning.obstructed"));
            return;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.moveTo(target);
                lightning.setCause((ServerPlayer) player);
                serverLevel.addFreshEntity(lightning);
            }
        }
    }

    public static void placeBed(Player player, ICantrip cantrip, InteractionHand hand) {

        HitResult rayHit = player.pick(player.getBlockReach(), 0.0F, false);

        if (rayHit.getType() != BlockHitResult.Type.BLOCK) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.toofar"));
            return;
        }

        long dayTime = player.level().getDayTime() % 24000L;
        boolean isDaytime = dayTime < 13000L;

        if (player.level().dimensionType().bedWorks()) {

            if (isDaytime && !(player.level().isThundering())) {
                player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.badtime"));
                return;
            }
        }


        BlockPos targetBlock = ((BlockHitResult) rayHit).getBlockPos();

        BlockPos footBlockPos, headBlockPos;
        // if player is looking at the top of the targeted block, simply try placing the bed on top of that block
        if (((BlockHitResult) rayHit).getDirection() == Direction.UP) {
            footBlockPos = targetBlock.above();
        }
        // if the player is looking at the bottom of the targeted block, try placing the bed below the block
        else if (((BlockHitResult) rayHit).getDirection() == Direction.DOWN) {
            footBlockPos = targetBlock.below();
        } else {
            //otherwise, player is looking at the side of the block, so try placing the bed one block in that direction
            footBlockPos = targetBlock.relative(((BlockHitResult) rayHit).getDirection(), 1);
        }

        headBlockPos = footBlockPos.relative(player.getDirection(), 1);

        if (!(player.level().getBlockState(footBlockPos.below()).isSolid())) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.badtarget"));
            return;
        }

        if (!((player.level().getBlockState(footBlockPos).isAir()) && (player.level().getBlockState(headBlockPos).isAir()))) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.nospace"));
            return;
        }



        BedBlock b1 = (BedBlock) BlockRegistry.SPECTRAL_BED.get();
        BlockState bedState = b1
                .defaultBlockState()
                .setValue(BedBlock.FACING, player.getDirection())
                .setValue(BedBlock.PART, BedPart.HEAD);

        BlockState footState = bedState.setValue(BedBlock.PART, BedPart.FOOT);

        player.level().setBlock(headBlockPos, bedState, 3);
        player.level().setBlock(footBlockPos, footState, 3);

        BlockEntity bedBlockEntity = new SpectralBedBlockEntity(headBlockPos, bedState);
        player.level().setBlockEntity(bedBlockEntity);
    }
    public static void summonBoat(Player player, ICantrip cantrip, InteractionHand hand)
    {
        double range;
        try
        {
            range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        } 
        catch (Throwable var14)
        {
            return;
        }

        Level level = player.level();
        Vec3 target = player.pick(range, 0.0F, true).getLocation();

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel)
        {
            SpectralBoat boat = EntityRegistry.SPECTRAL_BOAT.get().create(serverLevel);
            if (boat != null)
            {
                boat.moveTo(target.x, target.y, target.z, player.getYRot(), 0.0F);
                serverLevel.addFreshEntity(boat);
            }
        }
    }
    public static void consume(Player player, ICantrip cantrip, InteractionHand hand) {
        InteractionHand _other_hand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack otherHand = player.getItemInHand(_other_hand);

        if (otherHand.isEmpty()) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.force_consume.noitem"));
            return;
        }
    
        applyEffect(player, otherHand);

        // if (!player.isCreative()) {
        otherHand.shrink(1);
        if (otherHand.isEmpty()) {
            player.setItemInHand(_other_hand, ItemStack.EMPTY);
        }
        // }
    }

    // Shall be moved to a separate file (TBD)
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

        // Some of the items might be implemented further: 
        // (Mandatory) Glowing Ink Sac, Glowing Dust, Spectral Arrow - apply glowing effect to the player for 30 seconds
        // (Mandatory) Shulker Shell - apply the "Levitation" effect to the player for 10 seconds
        // (Mandatory) Rabbit's Foot - apply the "Jump Boost" effect to the player for 30 seconds
        // (Mandatory) Heart of the Sea - apply the "Conduit Power" effect to the player for 15 minutes
        // (Mandatory) Glowstone, Sea Lantern, Shroomlight - light up the area around the player for 3 minutes
        // (Mandatory) Redstone Torch, Torch, Soul Torch - light up the area around the player for 30 seconds
        // (Mandatory) Piston - make player's step longer to 1 block
        // (Mandatory) Music Discs - play the music disc for the player, throw after playing
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

    // Shall be moved to a separate file (TBD)
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
