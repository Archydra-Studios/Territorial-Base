package io.github.profjb58.territorial.event.registry;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.BoundaryBeaconBlock;
import io.github.profjb58.territorial.client.gui.BaseBeaconScreen;
import io.github.profjb58.territorial.client.gui.BoundaryBeaconScreen;
import io.github.profjb58.territorial.client.gui.KeyringScreen;
import io.github.profjb58.territorial.client.render.entity.BoundaryBeaconBlockEntityRenderer;
import io.github.profjb58.territorial.client.render.entity.LaserBlockEntityRenderer;
import io.github.profjb58.territorial.event.ScreenHandlers;
import io.github.profjb58.territorial.inventory.ItemInventory;
import io.github.profjb58.territorial.networking.s2c.S2CPacket;
import io.github.profjb58.territorial.networking.s2c.SyncLockInfoPacket;
import io.github.profjb58.territorial.networking.s2c.SyncTeamDataPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TerritorialClientRegistry {

    // S2C Packets
    public static final Identifier SYNC_TEAM_DATA_PACKET_ID = new Identifier(Territorial.MOD_ID, "sync_team_data_packet");
    public static final Identifier SYNC_LOCK_INFO_PACKET_ID = new Identifier(Territorial.MOD_ID, "sync_lock_info_packet");

    public static void registerAll() {
        registerPackets();
        registerItemPredicates();
        registerRenderers();
        registerScreens();

        // Colour provider registers
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> state.get(BoundaryBeaconBlock.DYE_COLOUR).getFireworkColor(), TerritorialRegistry.BOUNDARY_BEACON);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            NbtCompound subNbt = stack.getSubNbt("BlockStateTag");
            if(subNbt != null && subNbt.contains("dye_colour")) {
                return DyeColor.byName(subNbt.getString("dye_colour"), DyeColor.WHITE).getFireworkColor();
            }
            return ((BlockItem) stack.getItem()).getBlock().getDefaultState().get(BoundaryBeaconBlock.DYE_COLOUR).getFireworkColor();
        }, TerritorialRegistry.BOUNDARY_BEACON.asItem());


    }

    private static void registerRenderers() {
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
    }

    private static void registerScreens() {
        ScreenRegistry.register(TerritorialRegistry.KEYRING_SCREEN_HANDLER_TYPE, KeyringScreen::new);
        ScreenRegistry.register(TerritorialRegistry.BOUNDARY_BEACON_SCREEN_HANDLER_TYPE, BoundaryBeaconScreen::new);
        ScreenRegistry.register(TerritorialRegistry.BASE_BEACON_SCREEN_HANDLER_TYPE, BaseBeaconScreen::new);

        // Events...
        ScreenHandlers.init();
    }

    private static void registerPackets() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            S2CPacket.register(SYNC_TEAM_DATA_PACKET_ID, new SyncTeamDataPacket());
            S2CPacket.register(SYNC_LOCK_INFO_PACKET_ID, new SyncLockInfoPacket());
        });
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
