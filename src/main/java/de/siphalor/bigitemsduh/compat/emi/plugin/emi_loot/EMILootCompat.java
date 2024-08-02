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

package de.siphalor.bigitemsduh.compat.emi.plugin.emi_loot;

import de.siphalor.bigitemsduh.OTEI;
import de.siphalor.bigitemsduh.compat.bumblezone.BumblezoneCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.emi_trades.EMITradesCompat;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.EnlargedObjectDrawer;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.RecipeScreen;
import fzzyhmstrs.emi_loot.util.EntityEmiStack;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;

public class EMILootCompat
{
    public static void drawEmiIngredientAsEntity(DrawContext entityContext, DrawContext tooltipContext, ItemStack stack, Screen screen, int mouseX, int mouseY, int screenX, int height, int x, int y, float scale, boolean isRecipeScreen, boolean isEMIPanelStackInRecipeScreen, boolean inEmi)
    {
        // I got a null pointer for drawContext in a large
        // modpack setting so we will check in case
        if(entityContext == null) return;

        Item egg = stack.getItem();
        EmiStack placeholderStack = EmiStack.EMPTY;
        EntityType<?> eggType;

        if(screen instanceof RecipeScreen recipeScreen) { if(isEntity(recipeScreen.getHoveredStack()) || (OTEI.hasEMITrades() && EMITradesCompat.isEMITradesEntityStack(recipeScreen.getHoveredStack()))) { placeholderStack = (EmiStack) recipeScreen.getHoveredStack(); } }

        eggType = !placeholderStack.isEmpty() ? ((Entity)placeholderStack.getKey()).getType() : handleCompat(egg);

        // Account for potential null values and draw their item instead
        if(eggType == null)
        {
            OTEI.logInfo(egg.getName(), "has an invalid entity type, please report this ASAP!");
            entityContext.getMatrices().push();

            entityContext.getMatrices().translate(x + (EnlargedObjectDrawer.getXTranspose(isEMIPanelStackInRecipeScreen, isRecipeScreen)), y + (EnlargedObjectDrawer.getYTranspose(isEMIPanelStackInRecipeScreen, isRecipeScreen)), -10);
            entityContext.getMatrices().scale(scale, scale, Math.min(scale, 20f));
            entityContext.drawItem(egg.getDefaultStack(), 0, 0);

            entityContext.getMatrices().pop();
            return;
        }

        float eggEntityScale = OTEIConfig.getEmiDependent().individualAdjustableEntityScales.getOrDefault(EnlargedObjectDrawer.getEggTypeString(eggType), OTEIConfig.getEmiDependent().defaultEntityScale);
        float size = Math.min(screenX * eggEntityScale, height * eggEntityScale);
        float entityScale = size / 16;

        double ex = (screenX - size) / 2F;
        double ey = (height - size) / 2F;

        entityContext.getMatrices().push();

        entityContext.getMatrices().translate(ex + (EnlargedObjectDrawer.getXTranspose(isEMIPanelStackInRecipeScreen, isRecipeScreen)), ey + (EnlargedObjectDrawer.getYTranspose(isEMIPanelStackInRecipeScreen, isRecipeScreen, eggType)), -10);
        entityContext.getMatrices().scale(entityScale, entityScale, Math.min(entityScale, 20f));

        EmiStack entityStack;

        if(!placeholderStack.isEmpty()) entityStack = placeholderStack;
        else
        {
            ClientWorld clientWorld = MinecraftClient.getInstance().world;
            Entity eggEntity = eggType.create(clientWorld);

            entityStack = createEntityEmiStack(eggEntity);

            if(eggEntity != null) eggEntity.discard();
        }

        if(inEmi)
        {
            EmiDrawContext drawTooltipContext = EmiDrawContext.wrap(tooltipContext);

            // Drawing an Entity breaks EMis tooltip rendering for spawn eggs, re-render it
            EmiRenderHelper.drawTooltip(screen, drawTooltipContext, entityStack.getTooltip(), mouseX, mouseY);
        }

        entityStack.render(entityContext, 0, 0, 0, 1);

        entityContext.getMatrices().pop();
    }

    private static EntityType<?> handleCompat(Item egg)
    {
        if(OTEI.hasBumblezone()) return BumblezoneCompat.isSentryWatcherSpawnEgg(egg) || BumblezoneCompat.isDispenserSpawnEgg(egg) ? BumblezoneCompat.handleBumblezoneCompat(egg) : ((SpawnEggItem)egg).getEntityType(egg.getDefaultStack().getNbt());

        return ((SpawnEggItem)egg).getEntityType(egg.getDefaultStack().getNbt());
    }

    public static boolean isEntity(EmiIngredient hoveredStack) { return hoveredStack instanceof EntityEmiStack; }
    public static boolean isIconGroupEmiWidget(Widget hoveredWidget) { return hoveredWidget instanceof IconGroupEmiWidget; }

    public static EntityEmiStack createEntityEmiStack(Entity ent) { return EntityEmiStack.of(ent); }
}
