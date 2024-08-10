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

package de.siphalor.bigitemsduh.compat.emi;

import de.siphalor.bigitemsduh.compat.bumblezone.BumblezoneCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.EMILootCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.EMIffectCompat;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.EnlargedObjectDrawer;
import de.siphalor.bigitemsduh.util.IScreenAccessor;
import dev.emi.emi.api.stack.*;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiFavorite;
import dev.emi.emi.screen.EmiScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EMICompat
{
    private static Item currentHoveredItem;

    public static boolean drawFocusedEMIStack(Screen screen)
    {
        if(EmiScreenManager.isDisabled()) return false;

        EmiDrawContext context = EmiDrawContext.wrap(((IScreenAccessor)screen).otei$getContext());
        EmiIngredient currentHoveredStack = EmiScreenManager.getHoveredStack(((IScreenAccessor)screen).otei$getMouseX(), ((IScreenAccessor)screen).otei$getMouseY(), false).getStack();

        if(currentHoveredStack.isEmpty()) return false;

        if(EnlargedObjectDrawer.isSpawnEgg(getItemStackFromEMIIngredient(currentHoveredStack).getItem()) || BumblezoneCompat.isSentryWatcherSpawnEgg(getItemStackFromEMIIngredient(currentHoveredStack).getItem())) currentHoveredItem = getItemStackFromEMIIngredient(currentHoveredStack).getItem();

        if(OTEIConfig.getEmiDependent().shouldDrawEntitiesModel && (EnlargedObjectDrawer.isSpawnEgg(getItemStackFromEMIIngredient(currentHoveredStack).getItem()) || BumblezoneCompat.isSentryWatcherSpawnEgg(getItemStackFromEMIIngredient(currentHoveredStack).getItem())))
        {
            EMILootCompat.drawEmiIngredientAsEntity(screen, false, false, true);
            return true;
        }

        boolean drawEffectSprite = EMIffectCompat.isStatusEffect(currentHoveredStack) && OTEIConfig.getEmiDependent().shouldDrawEffectSprite;

        context.push();

        context.matrices().translate(((IScreenAccessor)screen).otei$getItemX() + EnlargedObjectDrawer.getXTranspose(false, false), ((IScreenAccessor)screen).otei$getItemY() + EnlargedObjectDrawer.getYTranspose(false, false), drawEffectSprite || isFluid(currentHoveredStack) ? 100 : -10);
        context.matrices().scale(((IScreenAccessor)screen).otei$getScale(), ((IScreenAccessor)screen).otei$getScale(), Math.min(((IScreenAccessor)screen).otei$getScale(), 20f));

        drawEmiIngredient(context, currentHoveredStack, drawEffectSprite, OTEIConfig.getConfigEntries().shouldDrawBarsOnItems);

        context.pop();
        return true;
    }

    public static void drawEmiIngredient(EmiDrawContext context, EmiIngredient ingredient, boolean drawEffectSprite, boolean drawBar)
    {
        if(drawEffectSprite) ingredient.render(context.raw(), 0, 0, 0);
        else if(isFluid(ingredient)) EmiAgnos.renderFluid((FluidEmiStack)ingredient, context.matrices(), 0, 0, 0);
        else if((isItem(ingredient) || isFavorite(ingredient)) && drawBar) context.drawStack(ingredient, 0, 0);
        else if(isListOrTag(ingredient) && OTEIConfig.getEmiDependent().shouldCycleThroughListsAndTags)
        {
            List<EmiStack> emiStackList = ingredient.getEmiStacks();

            int currentStackIndex = (int)(System.currentTimeMillis() / (OTEIConfig.getEmiDependent().adjustItemSwitchTime * 1000) % emiStackList.size());
            EmiIngredient currentStack = emiStackList.get(currentStackIndex);

            if(isFluid(currentStack))
            {
                EmiAgnos.renderFluid((FluidEmiStack)ingredient, context.matrices(), 0, 0, 0);
                return;
            }

            if(drawBar) context.drawStack(currentStack, 0, 0);
            else context.raw().drawItem(getItemStackFromEMIIngredient(currentStack), 0, 0);
        }
        else context.raw().drawItem(getItemStackFromEMIIngredient(ingredient), 0, 0);
    }

    public static Item getCurrentHoveredItem() { return currentHoveredItem; }

    public static ItemStack getItemStackFromEMIIngredient(EmiIngredient ingredient) { return ingredient.getEmiStacks().get(0).getItemStack(); }

    public static boolean isHoveringOverPanel(int mouseX, int mouseY) { return EmiScreenManager.getHoveredPanel(mouseX, mouseY) != null; }

    public static boolean isFluid(EmiIngredient hoveredStack) { return hoveredStack instanceof FluidEmiStack; }
    public static boolean isItem(EmiIngredient hoveredStack) { return hoveredStack instanceof ItemEmiStack; }
    public static boolean isFavorite(EmiIngredient hoveredStack) { return hoveredStack instanceof EmiFavorite; }
    public static boolean isListOrTag(EmiIngredient hoveredStack) { return hoveredStack instanceof ListEmiIngredient || hoveredStack instanceof TagEmiIngredient; }
}
