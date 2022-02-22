package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.UuidUtils;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class LootTableHandler implements LootTableLoadingCallback {

    private static final Identifier END_CITY_TREASURE_ID = new Identifier("chests/end_city_treasure");

    public static void init() { LootTableLoadingCallback.EVENT.register(new LootTableHandler());}

    @Override
    public void onLootTableLoading(ResourceManager resourceManager, LootManager manager, Identifier id, FabricLootSupplierBuilder supplier, LootTableSetter setter) {
        if (END_CITY_TREASURE_ID.equals(id)) {
            var builder = FabricLootPoolBuilder.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .withCondition(RandomChanceWithLootingLootCondition.builder(0.35f, 1f).build())
                    .withFunction(SetNbtLootFunction.builder(UuidUtils.LootStack.create()).build())
                    .with(ItemEntry.builder(TerritorialRegistry.ENDER_KEY));
            supplier.pool(builder);
        }
    }
}
