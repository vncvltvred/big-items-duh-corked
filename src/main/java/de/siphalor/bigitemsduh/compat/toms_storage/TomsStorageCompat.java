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

package de.siphalor.bigitemsduh.compat.toms_storage;

import com.tom.storagemod.gui.CraftingTerminalScreen;
import com.tom.storagemod.gui.StorageTerminalScreen;
import de.siphalor.bigitemsduh.OTEIClient;
import de.siphalor.bigitemsduh.client_mixin.invoker.IAbstractStorageTerminalScreenInvoker;
import net.minecraft.client.gui.screen.Screen;

public class TomsStorageCompat
{
    public static boolean isStorageTerminalScreen(Screen screen)
    {
        if(!OTEIClient.getCompatChecker().hasTomsStorage()) return false;

        return (screen instanceof StorageTerminalScreen || screen instanceof CraftingTerminalScreen);
    }

    public static boolean isHoveringOverItemInTerminalScreen(Screen screen)
    {
        if(!OTEIClient.getCompatChecker().hasTomsStorage()) return false;

        return ((IAbstractStorageTerminalScreenInvoker)screen).otei$invokeGetSlotUnderMouse() != null && !((IAbstractStorageTerminalScreenInvoker)screen).otei$invokeGetSlotUnderMouse().getStack().isEmpty();
    }
}
