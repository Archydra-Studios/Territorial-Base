package io.github.profjb58.territorial;

import io.github.profjb58.territorial.config.TerritorialConfig;
import io.github.profjb58.territorial.event.*;
import io.github.profjb58.territorial.event.registry.TerritorialNetworkRegistry;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.networking.C2SPackets;
import io.github.profjb58.territorial.util.debug.DebugTimer;
import io.github.profjb58.territorial.world.team.ServerTeamsHandler;
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

	public static final ServerTeamsHandler TEAMS_HANDLER = new ServerTeamsHandler();

	@Override
	public void onInitialize() {
		AutoConfig.register(TerritorialConfig.class, JanksonConfigSerializer::new);

		// Event handlers
		TerritorialRegistry.registerAll();
		TerritorialNetworkRegistry.registerServerPackets();
		AttackHandlers.init();
		ServerTickHandlers.init();
		UseBlockHandler.init();
		DestructionHandlers.init();
		LootTableHandler.init();
		ServerConnectionHandlers.init();

		// Packet handlers
		C2SPackets.init();
	}

	public static TerritorialConfig getConfig() {
		return AutoConfig.getConfigHolder(TerritorialConfig.class).getConfig();
	}
	public static boolean isDedicatedServer() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
	}
}
