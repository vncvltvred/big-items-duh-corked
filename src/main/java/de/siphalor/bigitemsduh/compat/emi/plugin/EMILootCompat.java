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

package de.siphalor.bigitemsduh.compat.emi.plugin;

import de.siphalor.bigitemsduh.OTEIClient;
import de.siphalor.bigitemsduh.client_mixin.accessor.IIconGroupEmiWidgetAccessor;
import de.siphalor.bigitemsduh.client_mixin.accessor.IRecipeScreenAccessor;
import de.siphalor.bigitemsduh.client_mixin.invoker.IRecipeScreenInvoker;
import de.siphalor.bigitemsduh.compat.bumblezone.BumblezoneCompat;
import de.siphalor.bigitemsduh.compat.emi.EMICompat;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.EnlargedObjectDrawer;
import de.siphalor.bigitemsduh.util.IScreenAccessor;
import de.siphalor.bigitemsduh.util.OTEILogger;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.RecipeScreen;
import dev.emi.emi.screen.WidgetGroup;
import fzzyhmstrs.emi_loot.util.EntityEmiStack;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;

import java.util.concurrent.atomic.AtomicReference;

public class EMILootCompat
{
    public static void drawEmiIngredientAsEntity(Screen screen, boolean isRecipeScreen, boolean isEMIPanelStackInRecipeScreen, boolean inEmi)
    {
        // I got a null pointer for drawContext in a large
        // modpack setting so we will check in case
        if(((IScreenAccessor)screen).otei$getContext() == null || !OTEIClient.getCompatChecker().hasEMILoot()) return;

        Item egg = ((IScreenAccessor)screen).otei$getStack().getItem();
        EmiStack placeholderStack = EmiStack.EMPTY;
        EntityType<?> eggType;

        if(screen instanceof RecipeScreen recipeScreen) { if(isEntity(recipeScreen.getHoveredStack()) || EMITradesCompat.isEMITradesEntityStack(recipeScreen.getHoveredStack())) placeholderStack = (EmiStack) recipeScreen.getHoveredStack(); }

        if(egg instanceof AirBlockItem && EMICompat.isHoveringOverPanel(((IScreenAccessor)screen).otei$getMouseX(), ((IScreenAccessor)screen).otei$getMouseY())) egg = EMICompat.getCurrentHoveredItem();

        eggType = !placeholderStack.isEmpty() ? ((Entity)placeholderStack.getKey()).getType() : handleCompat(egg);

        // Account for potential null values and draw their item instead
        if(eggType == null)
        {
            OTEILogger.logInfo(egg.getName(), "has an invalid entity type, please report this ASAP!");
            ((IScreenAccessor)screen).otei$getContext().getMatrices().push();

            ((IScreenAccessor)screen).otei$getContext().getMatrices().translate(((IScreenAccessor)screen).otei$getItemX() + (EnlargedObjectDrawer.getXTranspose(isEMIPanelStackInRecipeScreen, isRecipeScreen)), ((IScreenAccessor)screen).otei$getItemY() + (EnlargedObjectDrawer.getYTranspose(isEMIPanelStackInRecipeScreen, isRecipeScreen)), -10);
            ((IScreenAccessor)screen).otei$getContext().getMatrices().scale(((IScreenAccessor)screen).otei$getScale(), ((IScreenAccessor)screen).otei$getScale(), Math.min(((IScreenAccessor)screen).otei$getScale(), 20f));
            ((IScreenAccessor)screen).otei$getContext().drawItem(egg.getDefaultStack(), 0, 0);

            ((IScreenAccessor)screen).otei$getContext().getMatrices().pop();
            return;
        }

        float eggEntityScale = OTEIConfig.getEmiDependent().individualAdjustableEntityScales.getOrDefault(EnlargedObjectDrawer.getEggTypeString(eggType), OTEIConfig.getEmiDependent().defaultEntityScale);
        float size = Math.min(((IScreenAccessor)screen).otei$getScreenX() * eggEntityScale, ((IScreenAccessor)screen).otei$getScreenHeight() * eggEntityScale);
        float entityScale = size / 16;

        double ex = (((IScreenAccessor)screen).otei$getScreenX() - size) / 2F;
        double ey = (((IScreenAccessor)screen).otei$getScreenHeight() - size) / 2F;

        ((IScreenAccessor)screen).otei$getContext().getMatrices().push();

        ((IScreenAccessor)screen).otei$getContext().getMatrices().translate(ex + (EnlargedObjectDrawer.getXTranspose(isEMIPanelStackInRecipeScreen, isRecipeScreen)), ey + (EnlargedObjectDrawer.getYTranspose(isEMIPanelStackInRecipeScreen, isRecipeScreen, eggType)), -10);
        ((IScreenAccessor)screen).otei$getContext().getMatrices().scale(entityScale, entityScale, Math.min(entityScale, 20f));

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
            EmiDrawContext drawTooltipContext = EmiDrawContext.wrap(((IScreenAccessor)screen).otei$getContext());

            // Drawing an Entity breaks EMis tooltip rendering for spawn eggs, re-render it
            EmiRenderHelper.drawTooltip(screen, drawTooltipContext, entityStack.getTooltip(), ((IScreenAccessor)screen).otei$getMouseX(), ((IScreenAccessor)screen).otei$getMouseY());
        }

        entityStack.render(((IScreenAccessor)screen).otei$getContext(), 0, 0, 0);

        ((IScreenAccessor)screen).otei$getContext().getMatrices().pop();
    }

    private static EntityType<?> handleCompat(Item egg)
    {
        if(BumblezoneCompat.isSentryWatcherSpawnEgg(egg)) return BumblezoneCompat.handleSentryWatcherSpawnEgg(egg);

        return ((SpawnEggItem)egg).getEntityType(egg.getDefaultStack().getNbt());
    }

    public static EmiIngredient getIconGroupEmiWidgetSlotStack(Screen screen)
    {
        Widget hoveredWidget = ((IRecipeScreenAccessor)screen).otei$getHoveredWidget();
        WidgetGroup currentWidgetGroup = ((IRecipeScreenInvoker)screen).otei$invokeGetGroup(hoveredWidget);

        int mouseX = ((IScreenAccessor)screen).otei$getMouseX() - currentWidgetGroup.x();
        int mouseY = ((IScreenAccessor)screen).otei$getMouseY() - currentWidgetGroup.y();

        AtomicReference<EmiIngredient> stack = new AtomicReference<>();

        ((IIconGroupEmiWidgetAccessor)hoveredWidget).otei$getItems().stream()
                .filter(slot -> slot.getBounds().contains(mouseX, mouseY))
                .findAny()
                .ifPresentOrElse(slot -> stack.set(slot.getStack()), () -> stack.set(EmiStack.EMPTY));

        return stack.get();
    }

    public static boolean isEntity(EmiIngredient hoveredStack)
    {
        if(!OTEIClient.getCompatChecker().hasEMILoot()) return false;

        return hoveredStack instanceof EntityEmiStack;
    }
    public static boolean isIconGroupEmiWidget(Widget hoveredWidget)
    {
        if(!OTEIClient.getCompatChecker().hasEMILoot()) return false;

        return hoveredWidget instanceof IconGroupEmiWidget;
    }

    public static EntityEmiStack createEntityEmiStack(Entity ent) { return EntityEmiStack.of(ent); }
}
