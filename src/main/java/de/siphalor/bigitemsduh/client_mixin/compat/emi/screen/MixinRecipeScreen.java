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

package de.siphalor.bigitemsduh.client_mixin.compat.emi.screen;

import de.siphalor.bigitemsduh.client_mixin.compat.emi.emi_loot.accessor.IconGroupEmiWidgetAccessor;
import de.siphalor.bigitemsduh.OTEI;
import de.siphalor.bigitemsduh.compat.bumblezone.BumblezoneCompat;
import de.siphalor.bigitemsduh.compat.emi.EMICompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.emi_loot.EMILootCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.emi_trades.EMITradesCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.emiffect.EMIffectCompat;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.EnlargedObjectDrawer;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.RecipeScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeScreen.class)
public abstract class MixinRecipeScreen extends Screen
{
    @Shadow(remap = false) int x;
    @Shadow(remap = false) private Widget hoveredWidget;

    @Shadow(remap = false) public abstract EmiIngredient getHoveredStack();

    protected MixinRecipeScreen(Text title) { super(title); }

    @Inject(method = "render", at = @At("RETURN"))
    public void otei$onRendered(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        if(!OTEI.shouldItemRender() || OTEIConfig.getEmiDependent().disableRecipeScreenMixin) return;

        EmiDrawContext context = EmiDrawContext.wrap(drawContext);
        EmiIngredient recipeScreenStack = this.getHoveredStack();
        EmiIngredient emiHoveredStack = EmiScreenManager.getHoveredStack(mouseX, mouseY, true).getStack();

        if(!emiHoveredStack.isEmpty())
        {
            this.handleRecipeScreenRendering(emiHoveredStack, context, drawContext, mouseX, mouseY, true);
            return;
        }

        // May need to be adjusted but should work fine for now...
        if(OTEI.hasEMILoot()) { if(EMILootCompat.isIconGroupEmiWidget(hoveredWidget)) { recipeScreenStack = ((IconGroupEmiWidgetAccessor)hoveredWidget).getItems().get(0).getStack(); } }

        if(recipeScreenStack.isEmpty()) return;

        this.handleRecipeScreenRendering(recipeScreenStack, context, drawContext, mouseX, mouseY, false);
    }

    @Unique
    private void handleRecipeScreenRendering(EmiIngredient stack, EmiDrawContext context, DrawContext tooltipContext, int mouseX, int mouseY, boolean isEMIPanelStackInRecipeScreen)
    {
        float size = Math.min(x * OTEIConfig.getConfigEntries().itemScale, this.height * OTEIConfig.getConfigEntries().itemScale);
        float scale = size / 16;

        double ix = (x - size) / 2F;
        double iy = (height - size) / 2F;

        if(OTEI.hasEMILoot())
        {
            if(EMILootCompat.isEntity(stack))
            {
                EMILootCompat.drawEmiIngredientAsEntity(context.raw(), tooltipContext, EMICompat.getItemStackFromEMIIngredient(stack), (RecipeScreen)(Object)this, mouseX, mouseY, this.x, this.height, (int)ix, (int)iy, scale, true, isEMIPanelStackInRecipeScreen, true);
                return;
            }

            if(OTEIConfig.getEmiDependent().shouldRenderEntitiesModel)
            {
                if(EnlargedObjectDrawer.isSpawnEgg(EMICompat.getItemStackFromEMIIngredient(stack)))
                {
                    EMILootCompat.drawEmiIngredientAsEntity(context.raw(), tooltipContext, EMICompat.getItemStackFromEMIIngredient(stack), (RecipeScreen)(Object)this, mouseX, mouseY, this.x, this.height, (int)ix, (int)iy, scale, true, isEMIPanelStackInRecipeScreen, true);
                    return;
                }
                else if(OTEI.hasBumblezone())
                {
                    if (BumblezoneCompat.isSentryWatcherSpawnEgg(EMICompat.getItemStackFromEMIIngredient(stack).getItem()))
                    {
                        EMILootCompat.drawEmiIngredientAsEntity(context.raw(), tooltipContext, EMICompat.getItemStackFromEMIIngredient(stack), (RecipeScreen)(Object)this, mouseX, mouseY, this.x, this.height, (int)ix, (int)iy, scale, true, isEMIPanelStackInRecipeScreen, true);
                        return;
                    }
                }
            }
        }

        if(OTEI.hasEMITrades())
        {
            if (EMITradesCompat.isEMITradesEntityStack(stack))
            {
                EMILootCompat.drawEmiIngredientAsEntity(context.raw(), tooltipContext, EMICompat.getItemStackFromEMIIngredient(stack), (RecipeScreen) (Object) this, mouseX, mouseY, this.x, this.height, (int) ix, (int) iy, scale, true, isEMIPanelStackInRecipeScreen, true);
                return;
            }
        }

        boolean drawEffectSprite = EMIffectCompat.isStatusEffect(stack) && OTEIConfig.getEmiDependent().shouldRenderEffectSprite;

        context.push();

        context.matrices().translate((ix - 2.25) + (EnlargedObjectDrawer.getXTranspose(isEMIPanelStackInRecipeScreen, true)), (iy + 2.25) + (EnlargedObjectDrawer.getYTranspose(isEMIPanelStackInRecipeScreen, true)), drawEffectSprite ? 100 : -10);
        context.matrices().scale(scale, scale, Math.min(scale, 20f));

        EMICompat.drawEmiIngredient(context, stack, drawEffectSprite, OTEIConfig.getConfigEntries().shouldDrawBarsOnItems);

        context.pop();
    }
}
