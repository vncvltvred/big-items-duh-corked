package de.siphalor.bigitemsduh.client_mixin.network;

import de.siphalor.bigitemsduh.OTEI;
import de.siphalor.bigitemsduh.config.OTEIConfig;
import de.siphalor.bigitemsduh.util.EnlargedObjectDrawer;
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
        if(!OTEIConfig.getEmiDependent().printEntityTypes) return;

        Registries.ENTITY_TYPE.stream().filter(type -> type.getSpawnGroup() != SpawnGroup.MISC).forEachOrdered(type -> OTEI.logInfo(EnlargedObjectDrawer.getEggTypeString(type)));
    }
}
