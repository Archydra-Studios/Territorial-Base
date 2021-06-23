package io.github.profjb58.territorial.config;

import io.github.profjb58.territorial.Territorial;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@SuppressWarnings("unused")
@Config.Gui.Background("minecraft:textures/block/bedrock.png")
@Config(name = Territorial.MOD_ID)
public class TBConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    private static boolean loaded = false;

    @ConfigEntry.Category("locks")
    @ConfigEntry.Gui.TransitiveObject
    LocksModule locks = new LocksModule();

    @ConfigEntry.Category("claims")
    @ConfigEntry.Gui.TransitiveObject
    ClaimsModule claims = new ClaimsModule();

    @ConfigEntry.Category("traps")
    @ConfigEntry.Gui.TransitiveObject
    TrapsModule traps = new TrapsModule();

    @Config(name = "locks")
    private static class LocksModule implements ConfigData {

        // TODO
        @Comment("Shows the lock name in the GUI")
        private boolean showLockName = true;

        // TODO
        @ConfigEntry.Gui.Excluded
        @Comment("Minimum operator level required to bypass protections")
        private int minOpLevel = 3;

        @Comment("Indicates how hard it is to break locked blocks, increasing makes" +
                " locked blocks easier to break. Keep between 1 and 0.001")
        @ConfigEntry.Gui.Excluded
        @ConfigEntry.Gui.RequiresRestart
        private double breakMultiplier = 0.02;

        @ConfigEntry.Gui.Excluded
        @Comment("Make the master key vanish when it's used")
        private boolean makeMasterKeyVanish = true;

        @ConfigEntry.Gui.Excluded
        private boolean enableEnderKey = true;

        @ConfigEntry.Gui.Excluded
        @Comment("Number of attempts made in finding a single item stack from a victims ender chest")
        private int enderKeyRolls = 5;
    }

    @Config(name = "claims")
    private static class ClaimsModule implements ConfigData {

    }

    @Config(name = "traps")
    private static class TrapsModule implements ConfigData {

        @ConfigEntry.Gui.Excluded
        @Comment("Maximum distance the laser transmitter can reach. Keep between 1 and 200")
        private int laserTransmitterMaxReach = 90;

        @ConfigEntry.Gui.Excluded
        @Comment("Use raycasts for more accurate laser transmitter beam collision checks (performance intensive)")
        private boolean laserTransmitterUseRaycasts = true;
    }

    // Cycle through bounded config options to check if they produce a false value
    public void checkBounds() {
        getMinOpLevel();
        getBreakMultiplier();
        getEnderKeyRolls();
        getLaserTransmitterMaxReach();
        loaded = true;
    }

    private <T> T warnFalseValue(String name, T value, T defaultValue) {
        if(!loaded || Territorial.DEBUG_MODE) {
            Territorial.logger.warn("Incorrect value for " + name + ": " + value.toString() + " set in the config file, choosing default value: " + defaultValue.toString());
        }
        return defaultValue;
    }

    public boolean showLockName() { return locks.showLockName; }
    public boolean masterKeyVanish() { return locks.makeMasterKeyVanish; }
    public boolean enderKeyEnabled() { return locks.enableEnderKey; }
    public boolean laserTransmitterUseRaycast() { return traps.laserTransmitterUseRaycasts; }

    public int getMinOpLevel() {
        if(locks.minOpLevel < 1 || locks.minOpLevel > 4) {
            return warnFalseValue("minOpLevel", locks.minOpLevel, 3);
        }
        return locks.minOpLevel;
    }

    public int getEnderKeyRolls() {
        if(locks.enderKeyRolls < 0 || locks.enderKeyRolls > 100) {
            return warnFalseValue("enderKeyRolls", locks.enderKeyRolls, 5);
        }
        return locks.enderKeyRolls;
    }

    public double getBreakMultiplier() {
        if(locks.breakMultiplier < 0.001 || locks.breakMultiplier > 1) {
            return warnFalseValue("breakMultiplier", locks.breakMultiplier,0.02);
        }
        return locks.breakMultiplier;
    }

    public int getLaserTransmitterMaxReach() {
        if(traps.laserTransmitterMaxReach < 1 || traps.laserTransmitterMaxReach > 200) {
            return warnFalseValue("laserTransmitterMaxReach", traps.laserTransmitterMaxReach, 90);
        }
        return traps.laserTransmitterMaxReach;
    }
}
