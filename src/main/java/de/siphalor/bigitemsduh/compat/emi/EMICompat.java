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

import de.siphalor.bigitemsduh.OTEI;
import de.siphalor.bigitemsduh.compat.bumblezone.BumblezoneCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.emi_loot.EMILootCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.emiffect.EMIffectCompat;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.EnlargedObjectDrawer;
import dev.emi.emi.api.stack.*;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiFavorite;
import dev.emi.emi.screen.EmiScreenManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;

import java.util.List;

public class EMICompat
{
    public static boolean drawFocusedEMIStack(DrawContext drawContext, int screenX, int height, int mouseX, int mouseY, Screen screen)
    {
        if(EmiScreenManager.isDisabled()) return false;

        EmiDrawContext context = EmiDrawContext.wrap(drawContext);
        EmiIngredient currentHoveredItem = EmiScreenManager.getHoveredStack(mouseX, mouseY, false).getStack();

        float size = Math.min(screenX * OTEIConfig.getConfigEntries().itemScale, height * OTEIConfig.getConfigEntries().itemScale);
        float scale = size / 16;

        double x = (screenX - size) / 2F;
        double y = (height - size) / 2F;

        if(currentHoveredItem.isEmpty()) return false;

        if(OTEIConfig.getEmiDependent().shouldRenderEntitiesModel)
        {
            if(EnlargedObjectDrawer.isSpawnEgg(getItemStackFromEMIIngredient(currentHoveredItem)))
            {
                EMILootCompat.drawEmiIngredientAsEntity(context.raw(), drawContext, getItemStackFromEMIIngredient(currentHoveredItem), screen, mouseX, mouseY, screenX, height, (int)x, (int)y, scale, false, false, true);
                return true;
            }
            else if(OTEI.hasBumblezone())
            {
                if (BumblezoneCompat.isSentryWatcherSpawnEgg(getItemStackFromEMIIngredient(currentHoveredItem).getItem()))
                {
                    EMILootCompat.drawEmiIngredientAsEntity(context.raw(), drawContext, getItemStackFromEMIIngredient(currentHoveredItem), screen, mouseX, mouseY, screenX, height, (int)x, (int)y, scale, false, false, true);
                    return true;
                }
            }
        }

        boolean drawEffectSprite = EMIffectCompat.isStatusEffect(currentHoveredItem) && OTEIConfig.getEmiDependent().shouldRenderEffectSprite;

        context.push();

        context.matrices().translate(x + EnlargedObjectDrawer.getXTranspose(false, false), y + EnlargedObjectDrawer.getYTranspose(false, false), drawEffectSprite || isFluid(currentHoveredItem) ? 100 : -10);
        context.matrices().scale(scale, scale, Math.min(scale, 20f));

        drawEmiIngredient(context, currentHoveredItem, drawEffectSprite, OTEIConfig.getConfigEntries().shouldDrawBarsOnItems);

        context.pop();
        return true;
    }

    public static void drawEmiIngredient(EmiDrawContext context, EmiIngredient ingredient, boolean drawEffectSprite, boolean drawBar)
    {
        if(isFluid(ingredient) || drawEffectSprite) ingredient.render(context.raw(), 0, 0, 0, 1);
        else if((isItem(ingredient) || isFavorite(ingredient)) && drawBar) context.drawStack(ingredient, 0, 0);
        else if(isListOrTag(ingredient) && OTEIConfig.getEmiDependent().shouldCycleThroughListsAndTags)
        {
            List<EmiStack> emiStackList = ingredient.getEmiStacks();

            int currentStackIndex = (int)(System.currentTimeMillis() / (OTEIConfig.getEmiDependent().adjustItemSwitchTime * 1000) % emiStackList.size());
            EmiIngredient currentStack = emiStackList.get(currentStackIndex);

            if(!drawBar) context.raw().drawItem(getItemStackFromEMIIngredient(currentStack), 0, 0);
            else context.drawStack(currentStack, 0, 0);
        }
        else context.raw().drawItem(getItemStackFromEMIIngredient(ingredient), 0, 0);
    }

    public static ItemStack getItemStackFromEMIIngredient(EmiIngredient ingredient) { return ingredient.getEmiStacks().get(0).getItemStack(); }

    @SuppressWarnings("UnstableApiUsage")
    public static boolean isFluid(EmiIngredient hoveredStack) { return hoveredStack instanceof FluidEmiStack; }
    @SuppressWarnings("UnstableApiUsage")
    public static boolean isItem(EmiIngredient hoveredStack) { return hoveredStack instanceof ItemEmiStack; }
    public static boolean isFavorite(EmiIngredient hoveredStack) { return hoveredStack instanceof EmiFavorite; }
    @SuppressWarnings("UnstableApiUsage")
    public static boolean isListOrTag(EmiIngredient hoveredStack) { return hoveredStack instanceof ListEmiIngredient || hoveredStack instanceof TagEmiIngredient; }
}
