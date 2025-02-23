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

package de.siphalor.bigitemsduh.client_mixin.network;

import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.StackRender;
import de.siphalor.bigitemsduh.util.OTEILogger;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler
{
    // Marium's Soulslike Weaponry entities didn't print
    // on Init so we shall send it to World Load instead
    @Inject(at = @At("TAIL"), method = "onGameJoin")
    private void printEntityTypesOnGameJoin(CallbackInfo info)
    {
        if(!OTEIConfig.getEmiDependentEntries().printEntityTypes) return;

        Registries.ENTITY_TYPE.stream()
                .filter(type -> type.getSpawnGroup() != SpawnGroup.MISC)
                .forEachOrdered(type -> OTEILogger.logInfo(StackRender.getEggTypeString(type)));
    }
}