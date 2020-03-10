package tfcelementia.api.capability.food;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.api.capability.food.Nutrient;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.capability.food.CapabilityFood;

import tfcelementia.util.agriculture.FoodTFCE;

/**
 * This is a combined capability class that delegates to two implementations:
 * - super = ItemHeatHandler
 * - internalFoodCap = FoodHandler
 */

public class FoodHeatHandlerTFCE extends ItemHeatHandler implements IFood, ICapabilitySerializable<NBTTagCompound>
{
    private final FoodHandlerTFCE internalFoodCap;

    public FoodHeatHandlerTFCE()
    {
        this(null, new float[] {0f, 0f, 0f, 0f, 0f}, 0.5f, 0f, 1f, 1, 100);
    }

    public FoodHeatHandlerTFCE(@Nullable NBTTagCompound nbt, @Nonnull FoodTFCE food)
    {
        this(nbt, food.getNutrients(), food.getCalories(), food.getWater(), food.getDecayModifier(), food.getHeatCapacity(), food.getCookingTemp());
    }

    public FoodHeatHandlerTFCE(@Nullable NBTTagCompound nbt, float[] nutrients, float calories, float water, float decayModifier, float heatCapacity, float meltTemp)
    {
        this.heatCapacity = heatCapacity;
        this.meltTemp = meltTemp;

        this.internalFoodCap = new FoodHandlerTFCE(nbt, nutrients, calories, water, decayModifier);

        deserializeNBT(nbt);
    }

    @Override
    public float getNutrient(ItemStack stack, Nutrient nutrient)
    {
        return internalFoodCap.getNutrient(stack, nutrient);
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

    @Override
    public float getWater()
    {
        return internalFoodCap.getWater();
    }

    @Override
    public float getCalories()
    {
        return internalFoodCap.getCalories();
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