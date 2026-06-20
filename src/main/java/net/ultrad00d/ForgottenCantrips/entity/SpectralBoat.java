package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SpectralBoat extends ChestBoat {
    public SpectralBoat(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
        setVariant(Type.OAK);
    }

    @Override
    public Item getDropItem() {
        return Items.AIR;
    }
}
