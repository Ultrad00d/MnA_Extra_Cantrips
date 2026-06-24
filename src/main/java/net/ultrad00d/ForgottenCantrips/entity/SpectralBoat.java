package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SpectralBoat extends ChestBoat
{
    private static final int EMPTY_LIFETIME_TICKS = 30 * 20;
    private static final String EMPTY_TICKS_TAG = "EmptyTicks";

    private int emptyTicks;

    public SpectralBoat(EntityType<? extends Boat> entityType, Level level)
    {
        super(entityType, level);
        setVariant(Type.OAK);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (level().isClientSide())
        {
            return;
        }

        if (getPassengers().isEmpty())
        {
            if (++emptyTicks >= EMPTY_LIFETIME_TICKS)
            {
                discard();
            }
        }
        else
        {
            emptyTicks = 0;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt(EMPTY_TICKS_TAG, emptyTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        emptyTicks = tag.getInt(EMPTY_TICKS_TAG);
    }

    @Override
    public Item getDropItem()
    {
        return Items.AIR;
    }
}
