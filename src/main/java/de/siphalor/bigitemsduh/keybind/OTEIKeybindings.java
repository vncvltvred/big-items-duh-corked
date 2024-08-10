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

package de.siphalor.bigitemsduh.keybind;

import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.api.PriorityKeyBinding;
import de.siphalor.bigitemsduh.OTEIClient;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class OTEIKeybindings
{
    public static final String KEY_BINDING_CATEGORY = String.format("key.categories.%s", OTEIClient.MOD_ID);

    public static KeyBinding ENLARGE_TOGGLE_KEY_BINDING = new ToggleKeyBinding(new Identifier(OTEIClient.MOD_ID, "toggle_object_enlargement"), InputUtil.Type.KEYSYM, 90, KEY_BINDING_CATEGORY, new KeyModifiers(false, false, true));
//    public static KeyBinding ENLARGE_HOLD_KEY_BINDING = new KeyBinding(String.format("key.%s.hold_object_enlargement", OTEIClient.MOD_ID), 90, KEY_BINDING_CATEGORY);

    public static void register()
    {
        // Apparently this key doesn't even work (even in the original mod), we will not register it for now
//        KeyBindingHelper.registerKeyBinding(ENLARGE_HOLD_KEY_BINDING);
        KeyBindingHelper.registerKeyBinding(ENLARGE_TOGGLE_KEY_BINDING);
    }

    private static class ToggleKeyBinding extends AmecsKeyBinding implements PriorityKeyBinding
    {
        public ToggleKeyBinding(Identifier id, InputUtil.Type type, int code, String category, KeyModifiers defaultModifiers) { super(id, type, code, category, defaultModifiers); }

        @Override
        public boolean onPressedPriority()
        {
            OTEIClient.setEnabled(!OTEIClient.isEnabled());
            return false;
        }
    }
}
