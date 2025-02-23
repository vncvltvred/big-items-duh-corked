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

package de.siphalor.bigitemsduh.config;

import de.siphalor.bigitemsduh.OTEIClient;
import de.siphalor.bigitemsduh.util.StackRender;
import de.siphalor.bigitemsduh.util.OTEILogger;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;

import java.util.Map;
import java.util.TreeMap;

@Config(name = OTEIClient.MOD_ID)
public class OTEIConfig implements ConfigData
{
    @Comment(value = "Sets the x transpose amount for the item displayed. Negative integers will move the item left, whilst positive integers will move it right. Amount is in Pixels")
    public int xTranspose = 0;

    @Comment(value = "Sets the y transpose amount for the item displayed. Negative integers will move the item up, whilst positive integers will move it down. Amount is in Pixels")
    public int yTranspose = 0;

    @Comment(value = "If a drawn item has a bar (durability or power), this option will draw it to that item")
    public boolean shouldDrawBarsOnItems = false;

    @Comment(value = "If you click-n-drag an item, this option will allow it to be enlarged")
    public boolean shouldEnlargeDraggedItems = false;

    @Comment(value = "Sets the scale for the enlarged item (e.g. how enlarged it will be), relative to the space available on the screen")
    public float itemScale = 0.5F;

    @ConfigEntry.Gui.CollapsibleObject
    EMIDependent emiDependent = new EMIDependent();

    public static class EMIDependent
    {
        @Comment(value = "Sets the x transpose amount for the item displayed in EMIs Recipe Screen. Negative integers will move the item left, whilst positive integers will move it right. Amount is in Pixels")
        public int xTransposeRecipeScreen = 0;

        @Comment(value = "Sets the y transpose amount for the item displayed in EMIs Recipe Screen. Negative integers will move the item up, whilst positive integers will move it down. Amount is in Pixels")
        public int yTransposeRecipeScreen = 0;

        @Comment(value = "EMIffect ONLY! Allows OTEI to draw the currently hovered status effects sprite, turning this off will draw the potion item instead")
        public boolean shouldDrawEffectSprite = true;

        @Comment(value = "When you look at a recipe and hover over an item in the EMi panels with a recipe screen transpose set, it will transpose the item to what it is. If you would rather it be the default transpose, set this to false")
        public boolean shouldTransposeEMIPanelItemsToRSTranspose = true;

        @Comment(value = "EMILoot ONLY! Allows hovered entities spawn egg to render the model instead of the spawn egg itself. NOTE: may cause fps decrease depending on your setup")
        public boolean shouldDrawEntitiesModel = true;

        @Comment(value = "Instead of drawing the first item in a list or a tag, this will instead cycle through the list (or tag) and draw each item")
        public boolean shouldCycleThroughListsAndTags = true;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 15)
        @Comment(value = "Companion to shouldCycleThroughListsAndTags, changes the time in seconds the item will switch. NOTE: this will desync if it's a list within a list/tag")
        public int adjustItemSwitchTime = 1;

        @Comment(value = "EMILoot ONLY! Helps finding an entity by their type via printing their names to console (when joining a world) for changing individual Y Transpose, or scale")
        public boolean printEntityTypes = false;

        @Comment(value = "EMILoot ONLY! This will auto-populate the maps with entities rather than looking for them through printEntityTypes. However, this will not work with Marium's Soulslike Weaponry Entities so you should still use printEntityTypes for that.")
        public boolean autoPopulateMapsWithEntities = false;

        @Comment(value = "EMILoot ONLY! Sets the default scale (e.g. how enlarged it will be) for every entity if enabled, relative to the space available on the screen. To change each individual entity you will have to go into the config within the config folder")
        public float defaultEntityScale = 0.30F;

        @Comment(value = "EMILoot ONLY! Sets the default scale (e.g. how enlarged it will be) for an individual entity if enabled, relative to the space available on the screen. Format is shown below.")
        public Map<String, Float> individualAdjustableEntityScales = new TreeMap<>(Map.ofEntries(
                Map.entry("antiquebeasts.frost-cyclops", 0.24F),
                Map.entry("antiquebeasts.cyclops", 0.24F),
                Map.entry("antiquebeasts.chimera", 0.25F),
                Map.entry("minecraft.ghast", 0.15F),
                Map.entry("minecraft.elder_guardian", 0.25F),
                Map.entry("minecraft.warden", 0.255F),
                Map.entry("soulsweapons.evil_forlorn", 0.25F),
                Map.entry("soulsweapons.forlorn", 0.25F),
                Map.entry("soulsweapons.night_prowler", 0.1175F),
                Map.entry("soulsweapons.day_stalker", 0.1175F),
                Map.entry("soulsweapons.chaos_monarch", 0.11F),
                Map.entry("soulsweapons.accursed_lord_boss", 0.11F),
                Map.entry("soulsweapons.withered_demon", 0.25F),
                Map.entry("soulsweapons.moonknight", 0.1025F),
                Map.entry("soulsweapons.soulmass", 0.20F),
                Map.entry("soulsweapons.returning_knight", 0.1025F),
                Map.entry("soulsweapons.draugr_boss", 0.24F)
        ));

        @Comment(value = "EMILoot ONLY! Sets Y Transpose for an individual entity, NOTE: this is added to the transpose in whatever menu you're in. Format is shown below.")
        public Map<String, Integer> individualAdjustableEntityYTransposes = new TreeMap<>(Map.ofEntries(
                Map.entry("antiquebeasts.mummy", -100),
                Map.entry("antiquebeasts.servant", -100),
                Map.entry("antiquebeasts.cyclops", 50),
                Map.entry("antiquebeasts.frost-cyclops", 50),
                Map.entry("soulsweapons.moonknight", 35),
                Map.entry("soulsweapons.returning_knight", 35)
        ));
    }

    @Override
    public void validatePostLoad() throws ValidationException
    {
        // Probably not needed but doesn't hurt to have. unless...
        if(emiDependent.individualAdjustableEntityScales == null || emiDependent.individualAdjustableEntityScales.isEmpty())
        {
            emiDependent.individualAdjustableEntityScales = new TreeMap<>(Map.ofEntries(
                    Map.entry("antiquebeasts.frost-cyclops", 0.24F),
                    Map.entry("antiquebeasts.cyclops", 0.24F),
                    Map.entry("antiquebeasts.chimera", 0.25F),
                    Map.entry("minecraft.ghast", 0.15F),
                    Map.entry("minecraft.elder_guardian", 0.25F),
                    Map.entry("minecraft.warden", 0.255F),
                    Map.entry("soulsweapons.evil_forlorn", 0.25F),
                    Map.entry("soulsweapons.forlorn", 0.25F),
                    Map.entry("soulsweapons.night_prowler", 0.1175F),
                    Map.entry("soulsweapons.day_stalker", 0.1175F),
                    Map.entry("soulsweapons.chaos_monarch", 0.11F),
                    Map.entry("soulsweapons.accursed_lord_boss", 0.11F),
                    Map.entry("soulsweapons.withered_demon", 0.25F),
                    Map.entry("soulsweapons.moonknight", 0.1025F),
                    Map.entry("soulsweapons.soulmass", 0.20F),
                    Map.entry("soulsweapons.returning_knight", 0.1025F),
                    Map.entry("soulsweapons.draugr_boss", 0.24F)
            ));
            OTEILogger.logError("individualAdjustableEntityScales has had an error, rolling back to default values.");

            if(emiDependent.individualAdjustableEntityScales == null || emiDependent.individualAdjustableEntityScales.isEmpty()) throw new ValidationException("individualAdjustableEntityScales could not be corrected!");
        }

        if(emiDependent.individualAdjustableEntityYTransposes == null || emiDependent.individualAdjustableEntityYTransposes.isEmpty())
        {
            emiDependent.individualAdjustableEntityYTransposes = new TreeMap<>(Map.ofEntries(
                    Map.entry("antiquebeasts.mummy", -100),
                    Map.entry("antiquebeasts.servant", -100),
                    Map.entry("antiquebeasts.cyclops", 50),
                    Map.entry("antiquebeasts.frost-cyclops", 50),
                    Map.entry("soulsweapons.moonknight", 35),
                    Map.entry("soulsweapons.returning_knight", 35)
            ));
            OTEILogger.logError("individualAdjustableEntityYTransposes has had an error, rolling back to default values.");

            if(emiDependent.individualAdjustableEntityYTransposes == null || emiDependent.individualAdjustableEntityYTransposes.isEmpty()) throw new ValidationException("individualAdjustableEntityYTransposes could not be corrected!");
        }

        if(emiDependent.autoPopulateMapsWithEntities)
        {
            Registries.ENTITY_TYPE.stream()
                    .filter(type -> type.getSpawnGroup() != SpawnGroup.MISC)
                    .forEachOrdered(type ->
            {
                emiDependent.individualAdjustableEntityYTransposes.putIfAbsent(StackRender.getEggTypeString(type), 0);
                emiDependent.individualAdjustableEntityScales.putIfAbsent(StackRender.getEggTypeString(type), 0.30F);
            });

            if(emiDependent.individualAdjustableEntityScales == null || emiDependent.individualAdjustableEntityScales.isEmpty()) OTEILogger.logError("individualAdjustableEntityScales auto-populating has failed!");

            if(emiDependent.individualAdjustableEntityYTransposes == null || emiDependent.individualAdjustableEntityYTransposes.isEmpty()) OTEILogger.logError("individualAdjustableEntityYTransposes auto-populating has failed!");

            OTEILogger.logInfo(" ");
            OTEILogger.logInfo("Maps have successfully been auto-populated with Entities");
            OTEILogger.logInfo(" ");
            OTEILogger.logInfo("PLEASE KEEP IN MIND...");
            OTEILogger.logInfo("This won't work for Marium's Soulslike Weaponry (AFAIK)");
            OTEILogger.logInfo("You will need to add specific ones manually");
            OTEILogger.logInfo("The config option (printEntityTypes) should aid in helping you find the Entities name");
            OTEILogger.logInfo(" ");
        }

        if(yTranspose < -3500 || yTranspose > 3500)
        {
            yTranspose = 0;
            OTEILogger.logError("yTranspose has had an error, rolling back to default value.");

            if(yTranspose < -3500 || yTranspose > 3500) throw new ValidationException("yTranspose could not be corrected!");
        }

        if(xTranspose < -3500 || xTranspose > 3500)
        {
            xTranspose = 0;
            OTEILogger.logError("xTranspose has had an error, rolling back to default value");

            if(xTranspose < -3500 || xTranspose > 3500) throw new ValidationException("xTranspose could not be corrected!");
        }

        if(emiDependent.yTransposeRecipeScreen < -3500 || emiDependent.yTransposeRecipeScreen > 3500)
        {
            emiDependent.yTransposeRecipeScreen = 0;
            OTEILogger.logError("yTransposeRecipeScreen has had an error, rolling back to default value");

            if(emiDependent.yTransposeRecipeScreen < -3500 || emiDependent.yTransposeRecipeScreen > 3500) throw new ValidationException("yTransposeRecipeScreen could not be corrected!");
        }

        if(emiDependent.xTransposeRecipeScreen < -3500 || emiDependent.xTransposeRecipeScreen > 3500)
        {
            emiDependent.xTransposeRecipeScreen = 0;
            OTEILogger.logError("xTransposeRecipeScreen has had an error, rolling back to default value");

            if(emiDependent.xTransposeRecipeScreen < -3500 || emiDependent.xTransposeRecipeScreen > 3500) throw new ValidationException("xTransposeRecipeScreen could not be corrected!");
        }

        if(emiDependent.adjustItemSwitchTime < 1 || emiDependent.adjustItemSwitchTime > 15)
        {
            emiDependent.adjustItemSwitchTime = 1;
            OTEILogger.logError("adjustItemSwitchTime has had an error, rolling back to default value");

            if(emiDependent.adjustItemSwitchTime < 1 || emiDependent.adjustItemSwitchTime > 15) throw new ValidationException("adjustItemSwitchTime could not be corrected!");
        }

        if(itemScale < 0.05 || itemScale > 1)
        {
            itemScale = 0.50F;
            OTEILogger.logError("itemScale has had an error, rolling back to default value");

            if(itemScale < 0.05 || itemScale > 1) throw new ValidationException("itemScale could not be corrected!");
        }

        if(emiDependent.defaultEntityScale < 0.05 || emiDependent.defaultEntityScale > 1)
        {
            emiDependent.defaultEntityScale = 0.30F;
            OTEILogger.logError("defaultEntityScale has had an error, rolling back to default value");

            if(emiDependent.defaultEntityScale < 0.05 || emiDependent.defaultEntityScale > 1) throw new ValidationException("defaultEntityScale could not be corrected!");
        }

        OTEILogger.logInfo("config has been successfully loaded!");
    }

    public static OTEIConfig getConfigEntries() { return AutoConfig.getConfigHolder(OTEIConfig.class).getConfig(); }
    public static OTEIConfig.EMIDependent getEmiDependentEntries() { return getConfigEntries().emiDependent; }

    public static void register()
    {
        AutoConfig.register(OTEIConfig.class, JanksonConfigSerializer::new);
    }
}