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

package de.siphalor.bigitemsduh;

import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.keybind.ToggleKeyBinding;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OTEI implements ModInitializer
{
	public static Logger LOGGER = LogManager.getLogger("otei");

	public static final String MOD_ID = "otei";
	public static final String MOD_NAME = "Obviously They're Enlarged Items!";

	public static final String KEY_BINDING_CATEGORY = "key.categories." + MOD_ID;
	public static final KeyBinding ENLARGE_TOGGLE_KEY_BINDING = new ToggleKeyBinding(new Identifier(MOD_ID, "toggle_item_enlargement"), InputUtil.Type.KEYSYM, 90, KEY_BINDING_CATEGORY, new KeyModifiers(false, false, true));
	public static final KeyBinding ENLARGE_HOLD_KEY_BINDING = new KeyBinding("key." + MOD_ID + ".hold_item_enlargement", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), KEY_BINDING_CATEGORY);

	private static boolean enabled = true;

	@Override
	public void onInitialize()
	{
		logInfo("proceeding to enlarge items...");
		logInfo("dangbroitsdon says hi :)");
		logInfo("items have been enlarged!");

		KeyBindingHelper.registerKeyBinding(ENLARGE_TOGGLE_KEY_BINDING);
		KeyBindingHelper.registerKeyBinding(ENLARGE_HOLD_KEY_BINDING);

		OTEIConfig.register();
	}

	public static void logInfo(String message)
	{
        	LOGGER.info("[{}] {}", MOD_NAME, message);
    	}

	public static void logInfo(Object message1, String message2)
	{
		LOGGER.info("[{}] {} {}", MOD_NAME, message1, message2);
	}

	public static void logError(String message)
	{
		LOGGER.error("[{}] {}", MOD_NAME, message);
	}

	public static boolean isEnabled() { return enabled; }

	public static void setEnabled(boolean enabled) { OTEI.enabled = enabled; }

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
    	public static boolean shouldItemRender() { return enabled || ENLARGE_HOLD_KEY_BINDING.isPressed(); }
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean hasREI() { return FabricLoader.getInstance().isModLoaded("roughlyenoughitems"); }
	public static boolean hasEMI() { return FabricLoader.getInstance().isModLoaded("emi"); }
	public static boolean hasEMIffect() { return FabricLoader.getInstance().isModLoaded("emiffect"); }
	public static boolean hasEMILoot() { return FabricLoader.getInstance().isModLoaded("emi_loot"); }
	public static boolean hasEMITrades() { return FabricLoader.getInstance().isModLoaded("emitrades"); }
	public static boolean hasTomsStorage() { return FabricLoader.getInstance().isModLoaded("toms_storage"); }
	public static boolean hasBumblezone() { return FabricLoader.getInstance().isModLoaded("the_bumblezone"); }

}
