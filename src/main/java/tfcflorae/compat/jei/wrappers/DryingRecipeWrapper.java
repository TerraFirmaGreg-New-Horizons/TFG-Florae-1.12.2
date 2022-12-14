package tfcflorae.compat.jei.wrappers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import net.dries007.tfc.compat.jei.wrappers.SimpleRecipeWrapper;
import net.dries007.tfc.util.calendar.ICalendar;

import tfcflorae.objects.recipes.DryingRecipe;

public class DryingRecipeWrapper extends SimpleRecipeWrapper
{
    private final DryingRecipe recipe;

    public DryingRecipeWrapper(DryingRecipe recipeWrapper)
    {
        super(recipeWrapper);
        this.recipe = recipeWrapper;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        float x = 60f;
        float y = 4f;
        String text = (DryingRecipe.getDuration(recipe) / ICalendar.TICKS_IN_HOUR < 48) ?
            DryingRecipe.getDuration(recipe) / ICalendar.TICKS_IN_HOUR + " " + I18n.format("tfcflorae.tooltip.hours") :
            DryingRecipe.getDuration(recipe) / ICalendar.TICKS_IN_DAY + " " + I18n.format("tfcflorae.tooltip.days");

        //String text = DryingRecipe.getDuration(recipe) / ICalendar.TICKS_IN_HOUR + " " + I18n.format("tfcflorae.tooltip.hours");
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFF, false);
    }
}