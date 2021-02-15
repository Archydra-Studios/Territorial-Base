package io.github.profjb58.territorial;

import io.github.profjb58.territorial.TBConfig.InnerStuff;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@me.sargunvohra.mcmods.autoconfig1u.annotation.Config(name = Territorial.MOD_ID)
class TBConfig implements ConfigData {
    boolean toggleA = true;
    boolean toggleB = false;

    @ConfigEntry.Gui.CollapsibleObject
    InnerStuff stuff = new InnerStuff();

    @ConfigEntry.Gui.Excluded
    InnerStuff invisibleStuff = new InnerStuff();

    static class InnerStuff {
        int a = 0;
        int b = 1;
    }
}

