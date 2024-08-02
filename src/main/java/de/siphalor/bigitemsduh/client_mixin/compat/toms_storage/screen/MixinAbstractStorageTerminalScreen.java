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

package de.siphalor.bigitemsduh.client_mixin.compat.toms_storage.screen;

import com.tom.storagemod.gui.AbstractStorageTerminalScreen;
import com.tom.storagemod.gui.StorageTerminalMenu;
import com.tom.storagemod.platform.PlatformContainerScreen;
import de.siphalor.bigitemsduh.compat.bumblezone.BumblezoneCompat;
import de.siphalor.bigitemsduh.compat.emi.plugin.emi_loot.EMILootCompat;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.EnlargedObjectDrawer;
import de.siphalor.bigitemsduh.OTEI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractStorageTerminalScreen.class)
public abstract class MixinAbstractStorageTerminalScreen<T extends StorageTerminalMenu> extends PlatformContainerScreen<T>
{
    public MixinAbstractStorageTerminalScreen(T p_97741_, PlayerInventory p_97742_, Text p_97743_) { super(p_97741_, p_97742_, p_97743_); }

    @Inject(method = "render", at = @At("RETURN"))
    public void otei$onRendered(DrawContext st, int mouseX, int mouseY, float partialTicks, CallbackInfo ci)
    {
        if(!OTEI.shouldItemRender() || !OTEI.hasTomsStorage()) return;

        Slot slot = getSlotUnderMouse();
        ItemStack stack;

        if(slot == null || slot.getStack().isEmpty()) return;

        stack = slot.getStack();

        float size = Math.min(x * OTEIConfig.getConfigEntries().itemScale, this.height * OTEIConfig.getConfigEntries().itemScale);
        float scale = size / 16;

        double ix = (x - size) / 2F;
        double iy = (height - size) / 2F;

        if((OTEI.hasEMI() && OTEI.hasEMILoot()) && OTEIConfig.getEmiDependent().shouldRenderEntitiesModel)
        {
            if(EnlargedObjectDrawer.isSpawnEgg(stack))
            {
                // Same for Tom's Simple Storage Mod
                st.drawItemTooltip(this.textRenderer, slot.getStack(), mouseX, mouseY);
                EMILootCompat.drawEmiIngredientAsEntity(st, null, stack, (AbstractStorageTerminalScreen<? extends StorageTerminalMenu>)(Object)this, mouseX, mouseY, this.x, this.height, (int)ix, (int)iy, scale, false, false, false);

                return;
            }
            else if(OTEI.hasBumblezone())
            {
                if (BumblezoneCompat.isSentryWatcherSpawnEgg(stack.getItem()))
                {
                    EMILootCompat.drawEmiIngredientAsEntity(st, null, stack, (AbstractStorageTerminalScreen<? extends StorageTerminalMenu>)(Object)this, mouseX, mouseY, this.x, this.height, (int)ix, (int)iy, scale, false, false, false);
                    return;
                }
            }
        }

        EnlargedObjectDrawer.drawObject(st, stack, (int)ix, (int)iy, scale, (AbstractStorageTerminalScreen<? extends StorageTerminalMenu>)(Object)this);
    }
}
