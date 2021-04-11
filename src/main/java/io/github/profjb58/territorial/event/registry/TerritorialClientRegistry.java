package io.github.profjb58.territorial.event.registry;

import io.github.profjb58.territorial.client.gui.KeyringScreen;
import io.github.profjb58.territorial.inventory.ItemInventory;
import io.github.profjb58.territorial.item.KeyItem;
import io.github.profjb58.territorial.item.KeyringItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TerritorialClientRegistry {

    public static boolean updateKeyringItemPredicate = false;
    private static float keyringPredicate = 0.0F;

    public static void registerAll() {
        ScreenRegistry.register(TerritorialRegistry.KEYRING_SCREEN_HANDLER_TYPE, KeyringScreen::new);
        registerItemPredicates();
    }

    private static void registerItemPredicates() {
        FabricModelPredicateProviderRegistry.register(TerritorialRegistry.KEYRING, new Identifier("stage"), (itemStack, clientWorld, livingEntity) -> {

            if(updateKeyringItemPredicate) {
                if (livingEntity == null || livingEntity.getActiveItem() != itemStack) {
                    return 0.0F;
                }
                else {
                    if(itemStack.getItem() instanceof KeyringItem) {
                        updateKeyringItemPredicate = false;
                        ItemInventory keyringInventory = new ItemInventory(itemStack, 9);
                        keyringInventory.loadFromAttachedItemTag();

                        int numKeys = 0;
                        for(ItemStack inventoryStack : keyringInventory.getItems()) {
                            if(inventoryStack.getItem() instanceof KeyItem) {
                                numKeys++;
                            }
                        }
                        if(numKeys > 0) {
                            keyringPredicate = numKeys / 9F;
                        }
                    }
                }
            }
            return keyringPredicate;
        });
    }
}
