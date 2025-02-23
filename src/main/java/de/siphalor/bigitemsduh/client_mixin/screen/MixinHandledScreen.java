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

import de.siphalor.bigitemsduh.client_mixin.invoker.IAbstractStorageTerminalScreenInvoker;
import de.siphalor.bigitemsduh.client_mixin.accessor.IHandledScreenAccessor;
import de.siphalor.bigitemsduh.client_mixin.invoker.IHandledScreenInvoker;
import de.siphalor.bigitemsduh.compat.bumblezone.BumblezoneCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.EMILootCompat;
import de.siphalor.bigitemsduh.compat.toms_storage.TomsStorageCompat;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.StackRender;
import de.siphalor.bigitemsduh.OTEIClient;
import de.siphalor.bigitemsduh.util.IScreenAccessor;
import de.siphalor.bigitemsduh.compat.emi.EMICompat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("all")
@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen extends Screen implements IScreenAccessor
{
	protected MixinHandledScreen(Text title) { super(title); }

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
		if(!OTEIClient.shouldItemRender() || this.client == null || this.client.currentScreen == null) return;

		HandledScreen accessibleScreen = ((HandledScreen<? extends ScreenHandler>)(Object)this);
		Screen currentScreen = this.client.currentScreen;

		this.otei$context = drawContext;

		this.otei$mouseX = mouseX;
		this.otei$mouseY = mouseY;
		this.otei$height = this.height;
		this.otei$x = ((IHandledScreenAccessor)this).otei$getX();

		Slot slot = TomsStorageCompat.isStorageTerminalScreen(currentScreen) && TomsStorageCompat.isHoveringOverItemInTerminalScreen(currentScreen) ? ((IAbstractStorageTerminalScreenInvoker)currentScreen).otei$invokeGetSlotUnderMouse() : ((IHandledScreenInvoker)accessibleScreen).otei$invokeGetSlotAt(this.otei$mouseX, this.otei$mouseY);

		if(slot != null && !slot.getStack().isEmpty()) this.otei$stack = slot.getStack();
		else if(OTEIConfig.getConfigEntries().shouldEnlargeDraggedItems && (((IHandledScreenAccessor)accessibleScreen).otei$getHandler().getCursorStack() != ItemStack.EMPTY || ((IHandledScreenAccessor)accessibleScreen).otei$getHandler().getCursorStack() != null)) this.otei$stack = ((IHandledScreenAccessor)accessibleScreen).otei$getHandler().getCursorStack();
		else this.otei$stack = ItemStack.EMPTY;

		float size = Math.min(this.otei$x * OTEIConfig.getConfigEntries().itemScale, this.otei$height * (OTEIConfig.getConfigEntries().itemScale));
		this.otei$scale = size / 16;

		this.otei$itemX = (this.otei$x - size) / 2F;
		this.otei$itemY = (this.otei$height - size) / 2F;

        if(OTEIClient.getCompatChecker().hasEMI())
		{
			if(EMICompat.drawFocusedEMIStack(accessibleScreen)) return;

			if(OTEIConfig.getEmiDependentEntries().shouldDrawEntitiesModel && (StackRender.isSpawnEgg(this.otei$stack.getItem()) || BumblezoneCompat.isSentryWatcherSpawnEgg(this.otei$stack.getItem())))
			{
				if(TomsStorageCompat.isStorageTerminalScreen(currentScreen) && TomsStorageCompat.isHoveringOverItemInTerminalScreen(currentScreen)) { drawContext.drawItemTooltip(this.textRenderer, this.otei$stack, this.otei$mouseX, this.otei$mouseY); }

				EMILootCompat.drawEmiIngredientAsEntity(accessibleScreen, false, false, false);
				return;
			}
		}

		StackRender.drawStack(accessibleScreen);
	}

	@Override public int otei$getMouseX() { return this.otei$mouseX; }
	@Override public int otei$getMouseY() { return this.otei$mouseY; }
	@Override public int otei$getScreenX() { return this.otei$x; }
	@Override public int otei$getScreenHeight() { return this.otei$height; }
	@Override public int otei$getItemX() { return (int)this.otei$itemX; }
	@Override public int otei$getItemY() { return (int)this.otei$itemY; }

	@Override public float otei$getScale() { return this.otei$scale; }

	@Override public ItemStack otei$getStack() { return otei$stack; }

	@Override public DrawContext otei$getContext() { return otei$context; }
}