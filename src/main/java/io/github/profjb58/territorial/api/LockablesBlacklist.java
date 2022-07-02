package io.github.profjb58.territorial.api;

/**
 * Lists for all the blocks that cannot be locked using Territorial's Block Entity locking system. <br><br>
 *
 * I decided not to use Minecraft's in-built <code>LockableContainerBlockEntity</code> because most mods completely
 * ignore it. I'd rather all block entities be lockable than only a very select few, it's the better of two evils.
 */
public class LockablesBlacklist {
    /**
     * To create a blacklist for your mod simply type <code>/territorial blacklist add [block]</code> for each block you want to add
     * and then look for the generated file in <code>config/territorial/lockables_blacklist.txt<br><br></code>
     *
     * You can send this list over to the github issues page where I can add it into the mod: https://github.com/profjb58/Territorial-Base/issues
     * or alternatively submit a PR with the changes.
     */
    public static final String[] MODDED_BLOCKS = {
            // Nothing here yet
    };

    /**
     * Same as MODDED_BLOCKS but For blocks that have a variant for all 16 colours
     */
    public static final String[] MODDED_COLOURED_BLOCKS = {
            // Nothing here yet
    };

    public static final String[] COLOURED_BLOCKS = {
            "minecraft:bed", "minecraft:banner"
    };

    public static final String[] VANILLA_BLOCKS = {
            "minecraft:beacon", "minecraft:comparator", "minecraft:conduit", "minecraft:daylight_detector",
            "minecraft:end_gateway", "minecraft:end_portal", "minecraft:piston", "minecraft:sticky_piston",
            "minecraft:sculk_sensor", "minecraft:bell"
    };

    public static final String[] TERRITORIAL_BLOCKS = {
            "territorial:laser_transmitter", "territorial:laser_receiver",
            "territorial:boundary_beacon"
    };
}
