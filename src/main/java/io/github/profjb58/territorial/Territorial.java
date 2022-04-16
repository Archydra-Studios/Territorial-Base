package io.github.profjb58.territorial;

import com.google.common.collect.ImmutableList;
import com.ibm.icu.impl.Pair;
import io.github.profjb58.territorial.config.TerritorialConfig;
import io.github.profjb58.territorial.event.*;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.networking.C2SPackets;
import io.github.profjb58.territorial.util.debug.DebugTimer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
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

	@Override
	public void onInitialize() {
		AutoConfig.register(TerritorialConfig.class, JanksonConfigSerializer::new);

		// Event handlers
		TerritorialRegistry.registerAll();
		AttackHandlers.init();
		ServerTickHandlers.init();
		UseBlockHandler.init();
		DestructionHandlers.init();
		LootTableHandler.init();

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
