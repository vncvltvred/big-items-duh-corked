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

package de.siphalor.bigitemsduh.compat.rei;

import de.siphalor.bigitemsduh.OTEIClient;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.overlay.ScreenOverlay;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class REICompat
{
	public static void drawFluidStackIfHovered(DrawContext drawContext)
	{
		if(!OTEIClient.getCompatChecker().hasREI() || !isEntryFocused() || !isFocusedEntryFluidStack()) return;

		EntryStack<?> focusedEntry = getFocusedEntry();

		if(focusedEntry == null) return;

		focusedEntry.render(drawContext, new Rectangle(0, 0, 16, 16), -1, -1, 0);
	}

	public static boolean isEntryFocused()
	{
		if(!OTEIClient.getCompatChecker().hasREI()) return false;

		REIRuntime reiRuntime = REIRuntime.getInstance();

		if(!reiRuntime.isOverlayVisible()) return false;

		Optional<ScreenOverlay> overlay = reiRuntime.getOverlay();

		if(overlay.isEmpty()) return false;

		EntryStack<?> focusedStack = overlay.get().getEntryList().getFocusedStack();

		return focusedStack != null && !focusedStack.isEmpty();
   	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isFocusedEntryFluidStack()
	{
		if(!OTEIClient.getCompatChecker().hasREI() || REIRuntime.getInstance().getOverlay().isEmpty() || getFocusedEntry() == null) return false;

		return getFocusedEntry().getValue() instanceof FluidStack;
	}

	public static EntryStack<?> getFocusedEntry()
	{
		if(REIRuntime.getInstance().getOverlay().isEmpty()) return null;

		return REIRuntime.getInstance().getOverlay().get().getEntryList().getFocusedStack();
	}

	public static ItemStack getFocusedEntryAsItemStack()
	{
		if(REIRuntime.getInstance().getOverlay().isEmpty() || getFocusedEntry() == null) return ItemStack.EMPTY;

		return (ItemStack) getFocusedEntry().getValue();
	}
}
