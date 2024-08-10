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

package de.siphalor.bigitemsduh.client_mixin.screen;

import de.siphalor.bigitemsduh.client_mixin.accessor.IRecipeScreenAccessor;
import de.siphalor.bigitemsduh.client_mixin.invoker.IRecipeScreenInvoker;
import de.siphalor.bigitemsduh.OTEIClient;
import de.siphalor.bigitemsduh.compat.bumblezone.BumblezoneCompat;
import de.siphalor.bigitemsduh.compat.emi.EMICompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.EMILootCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.EMITradesCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.EMIffectCompat;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.EnlargedObjectDrawer;
import de.siphalor.bigitemsduh.util.IScreenAccessor;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.RecipeScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeScreen.class)
public abstract class MixinRecipeScreen extends Screen implements IScreenAccessor
{
    protected MixinRecipeScreen(Text title) { super(title); }

    @Unique private int otei$mouseX;
    @Unique private int otei$mouseY;
    @Unique private int otei$x;
    @Unique private int otei$height;

    @Unique private double otei$itemX;
    @Unique private double otei$itemY;

    @Unique private float otei$scale;

    @Unique private ItemStack otei$stack;

    @Unique private DrawContext otei$context;

    @Inject(method = "render", at = @At("RETURN"))
    public void otei$onRendered(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        if(!OTEIClient.shouldItemRender() || OTEIConfig.getEmiDependent().disableRecipeScreenMixin) return;

        this.otei$context = drawContext;

        this.otei$mouseX = mouseX;
        this.otei$mouseY = mouseY;
        this.otei$x = ((IRecipeScreenAccessor)this).otei$getX();
        this.otei$height = this.height;

        EmiDrawContext context = EmiDrawContext.wrap(drawContext);
        EmiIngredient recipeScreenStack = ((IRecipeScreenInvoker)this).otei$invokeGetHoveredStack();
        EmiIngredient emiHoveredStack = EmiScreenManager.getHoveredStack(this.otei$mouseX, this.otei$mouseY, true).getStack();

        if(!emiHoveredStack.isEmpty())
        {
            this.otei$handleRecipeScreenRendering(emiHoveredStack, context, true);
            return;
        }

        if(EMILootCompat.isIconGroupEmiWidget(((IRecipeScreenAccessor)this).otei$getHoveredWidget())) { recipeScreenStack = EMILootCompat.getIconGroupEmiWidgetSlotStack((RecipeScreen)(Object)this); }

        if(recipeScreenStack.isEmpty()) return;

        this.otei$handleRecipeScreenRendering(recipeScreenStack, context, false);
    }

    @Unique
    private void otei$handleRecipeScreenRendering(EmiIngredient stack, EmiDrawContext context, boolean isEMIPanelStackInRecipeScreen)
    {
        float size = Math.min(((IRecipeScreenAccessor)this).otei$getX() * OTEIConfig.getConfigEntries().itemScale, this.height * OTEIConfig.getConfigEntries().itemScale);
        this.otei$scale = size / 16;

        this.otei$itemX = (this.otei$x - size) / 2F;
        this.otei$itemY = (this.otei$height - size) / 2F;

        this.otei$stack = EMICompat.getItemStackFromEMIIngredient(stack);

        if((OTEIConfig.getEmiDependent().shouldDrawEntitiesModel && (EnlargedObjectDrawer.isSpawnEgg(EMICompat.getItemStackFromEMIIngredient(stack).getItem()) || BumblezoneCompat.isSentryWatcherSpawnEgg(EMICompat.getItemStackFromEMIIngredient(stack).getItem()))) || EMILootCompat.isEntity(stack) || EMITradesCompat.isEMITradesEntityStack(stack))
        {
            EMILootCompat.drawEmiIngredientAsEntity((RecipeScreen)(Object)this, true, isEMIPanelStackInRecipeScreen, true);
            return;
        }

        boolean drawEffectSprite = EMIffectCompat.isStatusEffect(stack) && OTEIConfig.getEmiDependent().shouldDrawEffectSprite;

        context.push();

        context.matrices().translate(this.otei$itemX + (EnlargedObjectDrawer.getXTranspose(isEMIPanelStackInRecipeScreen, true)), this.otei$itemY + (EnlargedObjectDrawer.getYTranspose(isEMIPanelStackInRecipeScreen, true)), drawEffectSprite ? 100 : -10);
        context.matrices().scale(this.otei$scale, this.otei$scale, Math.min(this.otei$scale, 20f));

        EMICompat.drawEmiIngredient(context, stack, drawEffectSprite, OTEIConfig.getConfigEntries().shouldDrawBarsOnItems);

        context.pop();
    }

    @Override public int otei$getMouseX() { return this.otei$mouseX; }
    @Override public int otei$getMouseY() { return this.otei$mouseY; }
    @Override public int otei$getScreenX() { return this.otei$x; }
    @Override public int otei$getScreenHeight() { return this.otei$height; }
    @Override public int otei$getItemX() { return (int)this.otei$itemX; }
    @Override public int otei$getItemY() { return (int)this.otei$itemY; }

    @Override public float otei$getScale() { return this.otei$scale; }

    @Override public ItemStack otei$getStack() { return this.otei$stack; }

    @Override public DrawContext otei$getContext() { return otei$context; }
}
