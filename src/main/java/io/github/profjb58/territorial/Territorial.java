package io.github.profjb58.territorial;

import io.github.profjb58.territorial.event.TerritorialRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Territorial implements ModInitializer {

	public static final String MOD_ID = "territorial";
	public static final String BASE_GROUP_ID = "territorial_base";

	public static final ItemGroup BASE_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MOD_ID, BASE_GROUP_ID),
			() -> new ItemStack(TerritorialRegistry.LOCKPICK));

	@Override
	public void onInitialize() {
		TerritorialRegistry.registerAll();
	}
}
