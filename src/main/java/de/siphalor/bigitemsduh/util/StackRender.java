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
import de.siphalor.bigitemsduh.client_mixin.invoker.IHandledScreenInvoker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;

public class StackRender
{
    public static void drawStack(Screen screen)
    {
        IScreenAccessor accessedScreen = ((IScreenAccessor)screen);
        
        if(accessedScreen.otei$getContext() == null || accessedScreen.otei$getStack() == null) return;

        ItemStack stack = (REICompat.isEntryFocused() && !REICompat.isFocusedEntryNonItem()) ? REICompat.getFocusedEntryAsItemStack() : accessedScreen.otei$getStack();

        MatrixStack matrices = accessedScreen.otei$getContext().getMatrices();
        matrices.push();

        matrices.translate(accessedScreen.otei$getItemX() + getXTranspose(false, false), accessedScreen.otei$getItemY() + getYTranspose(false, false), REICompat.isFocusedEntryNonItem() ? 100 : -10);
        matrices.scale(accessedScreen.otei$getScale(), accessedScreen.otei$getScale(), Math.min(accessedScreen.otei$getScale(), 20f));

        REICompat.renderFocusedFluidStackEntry(accessedScreen.otei$getContext());

        if(OTEIConfig.getConfigEntries().shouldDrawBarsOnItems) ((IHandledScreenInvoker) screen).otei$invokeDrawItem(accessedScreen.otei$getContext(), stack, 0, 0, "");
        else accessedScreen.otei$getContext().drawItem(stack, 0, 0);

        matrices.pop();
    }

    public static int getXTranspose(boolean isEMIStackInRecipeScreen, boolean isRecipeScreen)
    {
        if(!isRecipeScreen) return OTEIConfig.getConfigEntries().xTranspose;
        else if(!isEMIStackInRecipeScreen) return OTEIConfig.getEmiDependentEntries().xTransposeRecipeScreen;

        return OTEIConfig.getEmiDependentEntries().shouldTransposeEMIPanelItemsToRSTranspose ? OTEIConfig.getEmiDependentEntries().xTransposeRecipeScreen : OTEIConfig.getConfigEntries().xTranspose;
    }

    public static int getYTranspose(boolean isEMIStackInRecipeScreen, boolean isRecipeScreen)
    {
        if(!isRecipeScreen) return OTEIConfig.getConfigEntries().yTranspose;
        else if(!isEMIStackInRecipeScreen) return OTEIConfig.getEmiDependentEntries().yTransposeRecipeScreen;

        return OTEIConfig.getEmiDependentEntries().shouldTransposeEMIPanelItemsToRSTranspose ? OTEIConfig.getEmiDependentEntries().yTransposeRecipeScreen : OTEIConfig.getConfigEntries().yTranspose;
    }

    public static int getYTranspose(boolean isEMIStackInRecipeScreen, boolean isRecipeScreen, EntityType<?> eggType)
    {
        int eggTypeYTransposeInMapOrDefault = OTEIConfig.getEmiDependentEntries().individualAdjustableEntityYTransposes.getOrDefault(StackRender.getEggTypeString(eggType), 0);

        if(!isRecipeScreen) return eggTypeYTransposeInMapOrDefault + OTEIConfig.getConfigEntries().yTranspose;
        else if(!isEMIStackInRecipeScreen) return eggTypeYTransposeInMapOrDefault + OTEIConfig.getEmiDependentEntries().yTransposeRecipeScreen;

        return OTEIConfig.getEmiDependentEntries().shouldTransposeEMIPanelItemsToRSTranspose ? eggTypeYTransposeInMapOrDefault + OTEIConfig.getEmiDependentEntries().yTransposeRecipeScreen : eggTypeYTransposeInMapOrDefault + OTEIConfig.getConfigEntries().yTranspose;
    }

    public static String getEggTypeString(EntityType<?> eggType) { return String.valueOf(eggType).substring(7); }
    public static boolean isSpawnEgg(Item item) { return item instanceof SpawnEggItem; }
}