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
    private static final Rectangle DEFAULT_RENDER_RECTANGLE = new Rectangle(0, 0, 16, 16);

    public static void renderFocusedFluidStackEntry(DrawContext drawContext)
	{
        if (!isREIEnabled() || !isFocusedEntryNonItem()) return;

        EntryStack<?> focusedEntry = getFocusedEntry();

        if (focusedEntry.isEmpty()) return;

        focusedEntry.render(drawContext, DEFAULT_RENDER_RECTANGLE, -1, -1, 0);
    }

    private static EntryStack<?> getFocusedEntry()
	{
        return getOverlay()
                .map(overlay -> overlay.getEntryList().getFocusedStack())
                .orElse(null);
    }

	private static Optional<ScreenOverlay> getOverlay() { return REIRuntime.getInstance().getOverlay(); }

	public static boolean isEntryFocused()
	{
		if(!isREIEnabled()) return false;

		REIRuntime reiRuntime = REIRuntime.getInstance();

		if(!reiRuntime.isOverlayVisible()) return false;

		if(getOverlay().isEmpty()) return false;

		EntryStack<?> focusedStack = getOverlay().get().getEntryList().getFocusedStack();

		return focusedStack != null && !focusedStack.isEmpty();
	}

    public static boolean isFocusedEntryNonItem()
	{
	    if(!isREIEnabled()) return false;

        EntryStack<?> focusedEntry = getFocusedEntry();

        return !focusedEntry.isEmpty() && focusedEntry.getValue() instanceof FluidStack;
    }

	public static ItemStack getFocusedEntryAsItemStack()
	{
		if(REIRuntime.getInstance().getOverlay().isEmpty() || getFocusedEntry() == null || getFocusedEntry().isEmpty()) return ItemStack.EMPTY;

		return (ItemStack) getFocusedEntry().getValue();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean isREIEnabled() { return OTEIClient.getCompatChecker().hasREI(); }
}