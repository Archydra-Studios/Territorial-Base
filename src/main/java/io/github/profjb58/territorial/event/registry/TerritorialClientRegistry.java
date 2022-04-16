package io.github.profjb58.territorial.event.registry;

import io.github.profjb58.territorial.client.gui.BoundaryBeaconScreen;
import io.github.profjb58.territorial.client.gui.KeyringScreen;
import io.github.profjb58.territorial.client.render.entity.BoundaryBeaconBlockEntityRenderer;
import io.github.profjb58.territorial.client.render.entity.LaserBlockEntityRenderer;
import io.github.profjb58.territorial.inventory.ItemInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TerritorialClientRegistry {

    public static void registerAll() {
        // Screen Registry
        ScreenRegistry.register(TerritorialRegistry.KEYRING_SCREEN_HANDLER_TYPE, KeyringScreen::new);

        // Item Predicates
        registerItemPredicates();

        // Block render layer maps
        BlockRenderLayerMap.INSTANCE.putBlock(TerritorialRegistry.LASER_TRANSMITTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                TerritorialRegistry.ECLIPSE_ROSE,
                TerritorialRegistry.ECLIPSE_ROSE_BUSH,
                TerritorialRegistry.BOUNDARY_BEACON
        );

        // Block Entity renderers
        BlockEntityRendererRegistry.register(TerritorialRegistry.LASER_BLOCK_ENTITY, ctx -> new LaserBlockEntityRenderer());
        BlockEntityRendererRegistry.register(TerritorialRegistry.BOUNDARY_BEACON_BLOCK_ENTITY, ctx -> new BoundaryBeaconBlockEntityRenderer());

        // Colour provider registers
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 0x34eb67, TerritorialRegistry.BOUNDARY_BEACON);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            return 0x34eb67;
        }, TerritorialRegistry.BOUNDARY_BEACON.asItem());

        ScreenRegistry.register(TerritorialRegistry.BOUNDARY_BEACON_SCREEN_HANDLER_TYPE, BoundaryBeaconScreen::new);
    }

    private static void registerItemPredicates() {
        FabricModelPredicateProviderRegistry.register(TerritorialRegistry.KEYRING, new Identifier("stage"), (itemStack, clientWorld, livingEntity, seed) -> {
            var keyringInventory = new ItemInventory(itemStack, 9);
            keyringInventory.loadFromAttachedItemTag();
            int numKeys = keyringInventory.getAmountOfFilledSlots();
            return numKeys / 9F;
        });

        FabricModelPredicateProviderRegistry.register(TerritorialRegistry.LENS, new Identifier("colour"), (itemStack, clientWorld, livingEntity, seed) -> {
            NbtCompound tag = itemStack.getSubNbt("beam");

            if(tag != null) {
                int colourId = tag.getInt("colour");
                return colourId / 16F;
            }
            return 0F;
        });


    }
}
