package tfcelementia.api.capability.food;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.api.capability.food.*;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.util.agriculture.Food;

import tfcelementia.util.agriculture.FoodTFCE;
import tfcelementia.api.capability.food.IFoodTFCE;

/**
 * This is a combined capability class that delegates to two implementations:
 * - super = ItemHeatHandler
 * - internalFoodCap = FoodHandler
 */

public class FoodHeatHandlerTFCE extends ItemHeatHandler implements IFoodTFCE, ICapabilitySerializable<NBTTagCompound>
{
    private final FoodHandlerTFCE internalFoodCap;

    public FoodHeatHandlerTFCE()
    {
        this(null, new FoodData(), 1, 100);
    }

    public FoodHeatHandlerTFCE(@Nullable NBTTagCompound nbt, @Nonnull FoodTFCE food)
    {
        this(nbt, food.getData(), food.getHeatCapacity(), food.getCookingTemp());
    }

    public FoodHeatHandlerTFCE(@Nullable NBTTagCompound nbt, FoodData data, float heatCapacity, float meltTemp)
    {
        this.heatCapacity = heatCapacity;
        this.meltTemp = meltTemp;

        this.internalFoodCap = new FoodHandlerTFCE(nbt, data);

        deserializeNBT(nbt);
    }

    @Override
    public long getCreationDate()
    {
        return internalFoodCap.getCreationDate();
    }

    @Override
    public void setCreationDate(long creationDate)
    {
        internalFoodCap.setCreationDate(creationDate);
    }

    @Override
    public long getRottenDate()
    {
        return internalFoodCap.getRottenDate();
    }

    @Nonnull
    @Override
    public FoodData getData()
    {
        return internalFoodCap.getData();
    }

    @Override
    public float getDecayDateModifier()
    {
        return internalFoodCap.getDecayDateModifier();
    }

    @Override
    public void setNonDecaying()
    {
        internalFoodCap.setNonDecaying();
    }

    @Nonnull
    @Override
    public List<FoodTrait> getTraits()
    {
        return internalFoodCap.getTraits();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFood.CAPABILITY || capability == CapabilityItemHeat.ITEM_HEAT_CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return hasCapability(capability, facing) ? (T) this : null;
    }

    @Override
    @Nonnull
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setTag("food", internalFoodCap.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            internalFoodCap.deserializeNBT(nbt.getCompoundTag("food"));
            super.deserializeNBT(nbt);
        }
    }
}