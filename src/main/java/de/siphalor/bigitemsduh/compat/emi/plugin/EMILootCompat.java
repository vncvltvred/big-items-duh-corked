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
import de.siphalor.bigitemsduh.util.IScreenAccessor;
import de.siphalor.bigitemsduh.util.OTEILogger;
import de.siphalor.bigitemsduh.util.StackRender;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.RecipeScreen;
import dev.emi.emi.screen.WidgetGroup;
import fzzyhmstrs.emi_loot.util.EntityEmiStack;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import fzzyhmstrs.emi_loot.util.QuantityListEmiIngredient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;

import java.util.concurrent.atomic.AtomicReference;

public class EMILootCompat
{
    public static void drawEmiIngredientAsEntity(Screen screen, boolean isRecipeScreen, boolean isPanelStackInRecipeScreen, boolean inEmi)
    {
        IScreenAccessor accessedScreen = (IScreenAccessor) screen;

        if (!isValidContext(accessedScreen)) return;

        Item currentItem = getCurrentItem(accessedScreen);
        EntityType<?> entityType = getEntityType(screen, currentItem);

        if (entityType == null) logAndDrawSpawnEggFallback(accessedScreen, currentItem, isRecipeScreen, isPanelStackInRecipeScreen);
        else renderEntity(accessedScreen, entityType, isRecipeScreen, isPanelStackInRecipeScreen, inEmi);
    }

    private static boolean isValidContext(IScreenAccessor accessedScreen) { return accessedScreen.otei$getContext() != null && OTEIClient.getCompatChecker().hasEMILoot(); }

    private static Item getCurrentItem(IScreenAccessor screen)
    {
        Item item = screen.otei$getStack().getItem();

        if (item instanceof AirBlockItem && EMICompat.isHoveringOverPanel(screen.otei$getMouseX(), screen.otei$getMouseY())) item = EMICompat.getCurrentHoveredItem();

        return item;
    }

    private static EntityType<?> getEntityType(Screen screen, Item item)
    {
        if (screen instanceof RecipeScreen recipeScreen)
        {
            EmiStack hoveredStack = (EmiStack) recipeScreen.getHoveredStack();

            if (isEntityStack(hoveredStack)) return ((Entity) hoveredStack.getKey()).getType();
        }

        return handleCompat(item);
    }

    private static boolean isEntityStack(EmiStack stack) { return stack != null && (isEntity(stack) || EMITradesCompat.isEMITradesEntityStack(stack)); }

    private static void logAndDrawSpawnEggFallback(IScreenAccessor accessedScreen, Item item, boolean isRecipeScreen, boolean isPanelStackInRecipeScreen)
    {
        OTEILogger.logInfo(item.getName(), "has an invalid entity type, please report this ASAP!");

        accessedScreen.otei$getContext().getMatrices().push();
        drawSpawnEgg(accessedScreen, item, isRecipeScreen, isPanelStackInRecipeScreen);
        accessedScreen.otei$getContext().getMatrices().pop();
    }

    private static void drawSpawnEgg(IScreenAccessor accessedScreen, Item item, boolean isRecipeScreen, boolean isPanelStackInRecipeScreen)
    {
        accessedScreen.otei$getContext().getMatrices().translate(
                accessedScreen.otei$getItemX() + StackRender.getXTranspose(isPanelStackInRecipeScreen, isRecipeScreen),
                accessedScreen.otei$getItemY() + StackRender.getYTranspose(isPanelStackInRecipeScreen, isRecipeScreen),
                -10
        );
        accessedScreen.otei$getContext().getMatrices().scale(
                accessedScreen.otei$getScale(),
                accessedScreen.otei$getScale(),
                Math.min(accessedScreen.otei$getScale(), 20f)
        );
        accessedScreen.otei$getContext().drawItem(item.getDefaultStack(), 0, 0);
    }

    private static void renderEntity(IScreenAccessor accessedScreen, EntityType<?> eggType, boolean isRecipeScreen, boolean isPanelStackInRecipeScreen, boolean inEmi)
    {
        float scale = getEntityScale(eggType, accessedScreen);
        double positionX = calculatePositionX(accessedScreen, scale);
        double positionY = calculatePositionY(accessedScreen, scale, isRecipeScreen, isPanelStackInRecipeScreen, eggType);

        MatrixStack matrices = accessedScreen.otei$getContext().getMatrices();
        matrices.push();
        matrices.translate(positionX, positionY, -10);
        matrices.scale(scale, scale, Math.min(scale, 20f));

        EmiStack entityStack = createEntityEmiStackOrPlaceholder(eggType);
        renderEntityStack(entityStack, accessedScreen, inEmi);

        matrices.pop();
    }

    private static double calculatePositionX(IScreenAccessor accessedScreen, float scale)
    {
        return (accessedScreen.otei$getScreenX() - scale) / 2F
                + StackRender.getXTranspose(false, false);
    }

    private static double calculatePositionY(IScreenAccessor accessedScreen, float scale, boolean isRecipeScreen, boolean isPanelStackInRecipeScreen, EntityType<?> eggType)
    {
        return (accessedScreen.otei$getScreenHeight() - scale) / 2F
                + StackRender.getYTranspose(isPanelStackInRecipeScreen, isRecipeScreen, eggType);
    }

    private static float getEntityScale(EntityType<?> eggType, IScreenAccessor accessedScreen)
    {
        float baseScale = OTEIConfig.getEmiDependentEntries().defaultEntityScale;
        float customScale = OTEIConfig.getEmiDependentEntries().individualAdjustableEntityScales.getOrDefault(StackRender.getEggTypeString(eggType), baseScale);

        return Math.min(accessedScreen.otei$getScreenX() * customScale, accessedScreen.otei$getScreenHeight() * customScale) / 16;
    }

    private static EmiStack createEntityEmiStackOrPlaceholder(EntityType<?> eggType)
    {
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        Entity entityInstance = eggType.create(clientWorld);
        EmiStack stack = createEntityEmiStack(entityInstance);

        if (entityInstance != null) entityInstance.discard();

        return stack;
    }

    private static void renderEntityStack(EmiStack entityStack, IScreenAccessor accessedScreen, boolean inEmi)
    {
        if (inEmi)
        {
            EmiDrawContext drawTooltipContext = EmiDrawContext.wrap(accessedScreen.otei$getContext());
            EmiRenderHelper.drawTooltip((Screen) accessedScreen, drawTooltipContext, entityStack.getTooltip(), accessedScreen.otei$getMouseX(), accessedScreen.otei$getMouseY());
        }

        entityStack.render(accessedScreen.otei$getContext(), 0, 0, 0);
    }

    private static EntityType<?> handleCompat(Item egg)
    {
        if (BumblezoneCompat.isSentryWatcherSpawnEgg(egg)) return BumblezoneCompat.handleSentryWatcherSpawnEgg(egg);

        return ((SpawnEggItem) egg).getEntityType(egg.getDefaultStack().getNbt());
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

    public static boolean isQuantityListEmiIngredient(EmiIngredient hoveredStack)
    {
        if(!OTEIClient.getCompatChecker().hasEMILoot()) return false;

        return hoveredStack instanceof QuantityListEmiIngredient;
    }

    public static EntityEmiStack createEntityEmiStack(Entity ent) { return EntityEmiStack.of(ent); }
}