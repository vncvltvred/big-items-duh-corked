package de.siphalor.bigitemsduh.compat.emi.plugin.emi_trades;

import de.siphalor.bigitemsduh.OTEI;
import dev.emi.emi.api.stack.EmiIngredient;
import io.github.prismwork.emitrades.util.EntityEmiStack;

public class EMITradesCompat
{
    public static boolean isEMITradesEntityStack(EmiIngredient hoveredStack)
    {
        if(!OTEI.hasEMITrades()) return false;

        return hoveredStack instanceof EntityEmiStack;
    }
}
