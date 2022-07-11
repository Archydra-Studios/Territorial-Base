package io.github.profjb58.territorial;

import io.github.profjb58.territorial.config.LockablesBlacklistHandler;
import io.github.profjb58.territorial.config.TerritorialConfig;
import io.github.profjb58.territorial.event.*;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.debug.DebugTimer;
import io.github.profjb58.territorial.server.team.ServerTeamManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Territorial implements ModInitializer {

	// Debug
	public static final boolean DEBUG_MODE = false;
	public static DebugTimer dt;

	// Basic info + loggers
	public static final String MOD_ID = "territorial";
	public static final String BASE_GROUP_ID = "territorial_base";
	public static final Logger LOGGER = LogManager.getLogger();

	public static final ItemGroup BASE_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MOD_ID, BASE_GROUP_ID),
			() -> new ItemStack(TerritorialRegistry.LOCKPICK));

	private static final ServerTeamManager TEAM_MANAGER = new ServerTeamManager();
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
	private static final LockablesBlacklistHandler LOCKABLES_BLACKLIST = new LockablesBlacklistHandler(SCHEDULER);

	@Override
	public void onInitialize() {
		AutoConfig.register(TerritorialConfig.class, JanksonConfigSerializer::new); // Config
		TerritorialRegistry.registerAll(this); // Registries

		// Event initialization
		AttackHandlers.init();
		ServerTickHandlers.init();
		UseBlockHandlers.init();
		DestructionHandlers.init();
		LootTableHandler.init();
		ServerConnectionHandlers.init(this);
		ChunkSyncHandler.init();
	}

	public ServerTeamManager getTeamManager() {
		return TEAM_MANAGER;
	}
	public LockablesBlacklistHandler getLockablesBlacklist() { return LOCKABLES_BLACKLIST; }
	public ScheduledExecutorService getScheduler() { return SCHEDULER; }

	public static TerritorialConfig getConfig() {
		return AutoConfig.getConfigHolder(TerritorialConfig.class).getConfig();
	}
	public static boolean isDedicatedServer() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
	}
}
