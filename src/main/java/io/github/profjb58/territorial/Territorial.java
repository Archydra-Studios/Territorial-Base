package io.github.profjb58.territorial;

import io.github.profjb58.territorial.config.TBConfig;
import io.github.profjb58.territorial.database.DatabaseManager;
import io.github.profjb58.territorial.event.*;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.networking.C2SPackets;
import io.github.profjb58.territorial.util.debug.DebugTimer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
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
	public static final Logger logger = LogManager.getLogger();

	// SQLite database
	public static DatabaseManager dbm;
	public static Thread dbmThread;

	public static final ItemGroup BASE_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MOD_ID, BASE_GROUP_ID),
			() -> new ItemStack(TerritorialRegistry.LOCKPICK));

	@Override
	public void onInitialize() {
		// Database managers
		dbm = DatabaseManager.getInstance();

		AutoConfig.register(TBConfig.class, JanksonConfigSerializer::new);
		getConfig().checkBounds();

		// Event handlers
		TerritorialRegistry.registerAll();
		AttackHandlers.init();
		ServerTickHandlers.init();
		UseBlockHandler.init();
		DestructionHandlers.init();

		// Packet handlers
		C2SPackets.init();
	}

	public static TBConfig getConfig() {
		return AutoConfig.getConfigHolder(TBConfig.class).getConfig();
	}
}
