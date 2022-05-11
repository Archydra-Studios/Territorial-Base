package io.github.profjb58.territorial.config;

import io.github.profjb58.territorial.Territorial;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@SuppressWarnings("unused")
@Config.Gui.Background("minecraft:textures/block/bedrock.png")
@Config(name = Territorial.MOD_ID + "/" + Territorial.MOD_ID)
public class TerritorialConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    private static boolean loaded = false;

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    ClientModule client = new ClientModule();

    @ConfigEntry.Category("protections")
    @ConfigEntry.Gui.TransitiveObject
    ProtectionsModule protections = new ProtectionsModule();

    @ConfigEntry.Category("teams")
    @ConfigEntry.Gui.TransitiveObject
    TeamsModule teams = new TeamsModule();

    @ConfigEntry.Category("traps")
    @ConfigEntry.Gui.TransitiveObject
    TrapsModule traps = new TrapsModule();

    boolean enableEnderKey = true;

    boolean omniscientObsidianRecipe = false;

    @Comment("Allow spreading to adjacent Obsidian blocks")
    boolean omniscientObsidianSpread = true;

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
                Indicates how hard it is to break locked blocks.
                Increasing makes locked blocks easier to break.
                Keep between 1 and 0.001""")
        @ConfigEntry.Gui.RequiresRestart
        double breakMultiplier = 0.02D;
    }

    @Config(name = "teams")
    private static class TeamsModule implements ConfigData {

        @ConfigEntry.Gui.CollapsibleObject
        Limits limits = new Limits();

        @ConfigEntry.Gui.CollapsibleObject
        Advanced advanced = new Advanced();

        static class Limits {
            int maxWaypointsPerTeam = 0;
            int maxTeamsPerPlayer = 1;
            int maxTeamsPerServer = 100;
            int maxNumOfChunkClaims = 9999;
        }

        static class Advanced {
            @Comment("Purge (remove) teams if they become inactive")
            boolean purgeTeams = false;

            int numOfDaysBeforeInactive = 14;
            int numOfDaysBeforePurge = 30;
        }
    }

    @Config(name = "traps")
    private static class TrapsModule implements ConfigData {

        @ConfigEntry.BoundedDiscrete(min = 1, max = 60)
        int laserTransmitterMaxReach = 48;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        int eclipseRoseMaxReach = 8;

        @Comment("Whether the laser targets all mobs or just players")
        boolean laserTargetsAllMobs = true;
    }

    private double getWithinBounds(String name, double configValue, double defaultValue, double min, double max) {
        if(configValue < min || configValue > max) {
            if(!loaded || Territorial.DEBUG_MODE)
                Territorial.LOGGER.warn("Incorrect value for " + name + ": " + configValue + " set in the config file, choosing default value: " + defaultValue);
            return defaultValue;
        }
        return configValue;
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
        getMinOpLevel();
        getBreakMultiplier();
        getLaserTransmitterMaxReach();
        loaded = true;
    }

    public boolean enderKeyEnabled() { return enableEnderKey; }
    public boolean laserTargetsAllMobs() { return traps.laserTargetsAllMobs; }
    public boolean omniscientObsidianRecipe() { return omniscientObsidianRecipe; }
    public boolean omniscientObsidianSpread() { return omniscientObsidianSpread; }
    public boolean purgeTeams() { return teams.advanced.purgeTeams; }

    public int getMinOpLevel() { return (int) getWithinBounds("minOpLevel", protections.minOpLevel, 3, 1, 4); }
    public int getLaserTransmitterMaxReach() { return (int) getWithinBounds("laserTransmitterMaxReach", traps.laserTransmitterMaxReach, 48, 1, 60); }
    public int getEclipseRoseMaxReach() { return (int) getWithinBounds("eclipseTriggerRadius", traps.eclipseRoseMaxReach, 8, 1, 16); }
    public double getBreakMultiplier() { return getWithinBounds("breakMultiplier", protections.breakMultiplier, 0.02D, 0.001D, 1D); }
    public int getNumDaysBeforeInactive() { return teams.advanced.numOfDaysBeforeInactive; }
    public int getNumDaysBeforePurge() { return teams.advanced.numOfDaysBeforePurge; }
}
