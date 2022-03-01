package io.github.profjb58.territorial.config;

import io.github.profjb58.territorial.Territorial;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@SuppressWarnings("unused")
@Config.Gui.Background("minecraft:textures/block/bedrock.png")
@Config(name = Territorial.MOD_ID)
public class TerritorialConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    private static boolean loaded = false;

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    ClientModule client = new ClientModule();

    @ConfigEntry.Category("protections")
    @ConfigEntry.Gui.TransitiveObject
    ProtectionsModule protections = new ProtectionsModule();

    @ConfigEntry.Category("traps")
    @ConfigEntry.Gui.TransitiveObject
    TrapsModule traps = new TrapsModule();

    boolean enableEnderKey = true;

    @Comment("Number of attempts made in finding a single item stack from a victims Ender Chest")
    @ConfigEntry.Gui.RequiresRestart
    int enderKeyRolls = 5;

    @Comment("Whether a recipe for Omniscient Obsidian should be generated")
    boolean omniscientObsidianRecipe = false;

    @Config(name = "client")
    private static class ClientModule implements ConfigData {

        @Comment("Shows the lock name in the GUI")
        boolean showLockName = true;
    }

    @Config(name = "protections")
    private static class ProtectionsModule implements ConfigData {

        @Comment("Minimum operator level required to bypass protections")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 4)
        int minOpLevel = 3;

        @Comment("""
                Indicates how hard it is to break locked blocks \s
                increasing makes locked blocks easier to break. \s
                Keep between 1 and 0.001""")
        @ConfigEntry.Gui.RequiresRestart
        double breakMultiplier = 0.02D;
    }

    @Config(name = "traps")
    private static class TrapsModule implements ConfigData {

        @Comment("Maximum distance the laser transmitter can reach")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 60)
        int laserTransmitterMaxReach = 48;

        @Comment("Whether the laser targets all mobs or just players")
        boolean laserTargetsAllMobs = true;

        @Comment("Eclipse trigger radius")
        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        int eclipseTriggerRadius = 8;
    }

    private double getWithinBounds(String name, double configValue, double defaultValue, double min, double max) {
        if(configValue < min || configValue > max) {
            if(!loaded || Territorial.DEBUG_MODE) {
                Territorial.LOGGER.warn("Incorrect value for " + name + ": " + configValue + " set in the config file, choosing default value: " + defaultValue);
            }
            return defaultValue;
        }
        return configValue;
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
        getMinOpLevel();
        getBreakMultiplier();
        getEnderKeyRolls();
        getLaserTransmitterMaxReach();
        loaded = true;
    }

    public boolean enderKeyEnabled() { return enableEnderKey; }
    public boolean laserTargetsAllMobs() { return traps.laserTargetsAllMobs; }
    public boolean omniscientObsidianRecipe() { return omniscientObsidianRecipe; }

    public int getMinOpLevel() { return (int) getWithinBounds("minOpLevel", protections.minOpLevel, 3, 1, 4); }
    public int getEnderKeyRolls() { return (int) getWithinBounds("enderKeyRolls", enderKeyRolls, 5, 0, 100); }
    public int getLaserTransmitterMaxReach() { return (int) getWithinBounds("laserTransmitterMaxReach", traps.laserTransmitterMaxReach, 48, 1, 60); }
    public int getEclipseTriggerRadius() { return (int) getWithinBounds("eclipseTriggerRadius", traps.eclipseTriggerRadius, 8, 1, 16); }
    public double getBreakMultiplier() { return getWithinBounds("breakMultiplier", protections.breakMultiplier, 0.02D, 0.001D, 1D); }
}
