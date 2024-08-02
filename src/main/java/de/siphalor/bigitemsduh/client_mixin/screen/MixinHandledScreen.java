/*
	MIT License

	Copyright (c) 2021 Siphalor, 2024 vncvltvred

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
*/

package de.siphalor.bigitemsduh.client_mixin.screen;

import de.siphalor.bigitemsduh.compat.bumblezone.BumblezoneCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.emi_loot.EMILootCompat;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.EnlargedObjectDrawer;
import de.siphalor.bigitemsduh.OTEI;
import org.jetbrains.annotations.Nullable;
import de.siphalor.bigitemsduh.compat.emi.EMICompat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen extends Screen
{
	@Shadow protected int x;
	@Shadow @Final protected ScreenHandler handler;

	@Shadow @Nullable protected abstract Slot getSlotAt(double xPosition, double yPosition);

	protected MixinHandledScreen(Text title) { super(title); }

	@Inject(method = "render", at = @At("RETURN"))
	public void otei$onRendered(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci)
	{
		if(!OTEI.shouldItemRender()) return;

		Slot slot = getSlotAt(mouseX, mouseY);
		ItemStack stack;

		if(slot != null && !slot.getStack().isEmpty()) stack = slot.getStack();
		else
		{
			if(OTEIConfig.getConfigEntries().shouldEnlargeDraggedItems)
			{
				stack = handler.getCursorStack();

				if (stack == null || stack.isEmpty()) return;
			}
			else stack = ItemStack.EMPTY;
		}

		float size = Math.min(this.x * OTEIConfig.getConfigEntries().itemScale, this.height * (OTEIConfig.getConfigEntries().itemScale));
		float scale = size / 16;

		double ix = (this.x - size) / 2F;
		double iy = (this.height - size) / 2F;

		if(OTEI.hasEMI()) { if(EMICompat.drawFocusedEMIStack(drawContext, this.x, this.height, mouseX, mouseY, (HandledScreen<? extends ScreenHandler>)(Object)this)) return; }

		if((OTEI.hasEMI() && OTEI.hasEMILoot()) && OTEIConfig.getEmiDependent().shouldRenderEntitiesModel)
		{
			if(EnlargedObjectDrawer.isSpawnEgg(stack))
			{
				EMILootCompat.drawEmiIngredientAsEntity(drawContext, null, stack, (HandledScreen<? extends ScreenHandler>)(Object)this, mouseX, mouseY, this.x, this.height, (int)ix, (int)iy, scale, false, false, false);
				return;
			}
			else if(OTEI.hasBumblezone())
			{
				if (BumblezoneCompat.isSentryWatcherSpawnEgg(stack.getItem()))
				{
					EMILootCompat.drawEmiIngredientAsEntity(drawContext, null, stack, (HandledScreen<? extends ScreenHandler>)(Object)this, mouseX, mouseY, this.x, this.height, (int)ix, (int)iy, scale, false, false, false);
					return;
				}
			}
		}

		EnlargedObjectDrawer.drawObject(drawContext, stack, (int)ix, (int)iy, scale, (HandledScreen<?>)(Object)this);
	}
}
