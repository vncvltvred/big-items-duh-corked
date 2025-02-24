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

import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.OTEICompatChecker;
import de.siphalor.bigitemsduh.util.OTEILogger;
import de.siphalor.bigitemsduh.keybind.OTEIKeybindings;
import net.fabricmc.api.ClientModInitializer;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class OTEIClient implements ClientModInitializer
{
	public static final String MOD_ID = "otei";

	private static boolean enabled = true;

	private static OTEICompatChecker compat;

	@Override
	public void onInitializeClient()
	{
		OTEILogger.logInfo("proceeding to enlarge items...");
		OTEILogger.logInfo("dangbroitsdon says hi :)");
		OTEILogger.logInfo("items have been enlarged!");

		OTEIConfig.register();
		OTEIKeybindings.register();

		compat = OTEICompatChecker.create();
	}

	public static boolean isEnabled() { return enabled; }

	public static void setEnabled(boolean isEnabled) { enabled = isEnabled; }

	public static OTEICompatChecker getCompatChecker() { return compat; }

	// || OTEIKeybindings.ENLARGE_HOLD_KEY_BINDING.isPressed()
	public static boolean shouldItemRender() { return isEnabled(); }
}