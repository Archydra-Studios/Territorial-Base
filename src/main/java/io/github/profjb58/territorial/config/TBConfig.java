package io.github.profjb58.territorial.config;

import io.github.profjb58.territorial.Territorial;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = Territorial.MOD_ID)
public class TBConfig implements ConfigData {

    private final String __comment = "Determines how hard it is to break locked blocks, increasing this value makes" +
            " locked blocks easier to break. Should always keep between 1 and 0.001 for the best results";

    @ConfigEntry.Gui.Excluded
    @ConfigEntry.Gui.RequiresRestart
    public double breakMultiplier = 0.015;
}
