/*
    This is free and unencumbered software released into the public domain.

    Anyone is free to copy, modify, publish, use, compile, sell, or
    distribute this software, either in source code form or as a compiled
    binary, for any purpose, commercial or non-commercial, and by any
    means.

    In jurisdictions that recognize copyright laws, the author or authors
    of this software dedicate any and all copyright interest in the
    software to the public domain. We make this dedication for the benefit
    of the public at large and to the detriment of our heirs and
    successors. We intend this dedication to be an overt act of
    relinquishment in perpetuity of all present and future rights to this
    software under copyright law.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
    IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
    OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
    OTHER DEALINGS IN THE SOFTWARE.

    For more information, please refer to <https://unlicense.org>
*/

package de.siphalor.bigitemsduh.util;

import de.siphalor.bigitemsduh.compat.rei.REICompat;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.client_mixin.screen.invoker.HandledScreenInvoker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;

public class EnlargedObjectDrawer
{
    public static void drawObject(DrawContext drawContext, ItemStack stack, int ix, int iy, float scale, Screen screen)
    {
        stack = (REICompat.isEntryFocused() && !REICompat.isFocusedEntryFluidStack()) ? REICompat.getFocusedEntryAsItemStack() : stack;

        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();

        matrices.translate(ix + getXTranspose(false, false), iy + getYTranspose(false, false), REICompat.isFocusedEntryFluidStack() ? 100 : -10);
        matrices.scale(scale, scale, Math.min(scale, 20f));

        REICompat.drawFluidStack(drawContext);

        if(OTEIConfig.getConfigEntries().shouldDrawBarsOnItems) ((HandledScreenInvoker) screen).invokeDrawItem(drawContext, stack, 0, 0, "");
        else drawContext.drawItem(stack, 0, 0);

        matrices.pop();
    }

    public static int getXTranspose(boolean isEMIStackInRecipeScreen, boolean isRecipeScreen)
    {
        if(!isRecipeScreen) return OTEIConfig.getConfigEntries().xTranspose;

        if(isEMIStackInRecipeScreen) return OTEIConfig.getEmiDependent().shouldTransposeEMIPanelItemsToRSTranspose ? OTEIConfig.getEmiDependent().xTransposeRecipeScreen : OTEIConfig.getConfigEntries().xTranspose;

        return OTEIConfig.getEmiDependent().xTransposeRecipeScreen;
    }

    public static int getYTranspose(boolean isEMIStackInRecipeScreen, boolean isRecipeScreen)
    {
        if(!isRecipeScreen) return OTEIConfig.getConfigEntries().yTranspose;

        if(isEMIStackInRecipeScreen) return OTEIConfig.getEmiDependent().shouldTransposeEMIPanelItemsToRSTranspose ? OTEIConfig.getEmiDependent().yTransposeRecipeScreen : OTEIConfig.getConfigEntries().yTranspose;

        return OTEIConfig.getEmiDependent().yTransposeRecipeScreen;
    }

    public static int getYTranspose(boolean isEMIStackInRecipeScreen, boolean isRecipeScreen, EntityType<?> eggType)
    {
        int eggTypeYTransposeInMapOrDefault = OTEIConfig.getEmiDependent().individualAdjustableEntityYTransposes.getOrDefault(EnlargedObjectDrawer.getEggTypeString(eggType), 0);

        if(!isRecipeScreen) return eggTypeYTransposeInMapOrDefault + OTEIConfig.getConfigEntries().yTranspose;

        if(!isEMIStackInRecipeScreen) return eggTypeYTransposeInMapOrDefault + OTEIConfig.getEmiDependent().yTransposeRecipeScreen;

        return OTEIConfig.getEmiDependent().shouldTransposeEMIPanelItemsToRSTranspose ? eggTypeYTransposeInMapOrDefault + OTEIConfig.getEmiDependent().yTransposeRecipeScreen : eggTypeYTransposeInMapOrDefault + OTEIConfig.getConfigEntries().yTranspose;
    }

    public static boolean isSpawnEgg(ItemStack item) { return item.getItem() instanceof SpawnEggItem; }
    public static String getEggTypeString(EntityType<?> eggType) { return String.valueOf(eggType).substring(7); }
}