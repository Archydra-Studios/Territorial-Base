package io.github.profjb58.territorial.api.event.common;

import io.github.profjb58.territorial.block.LockableBlock;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Variety of helper events for Territorials custom Lockable Block Entities
 * <br><br>
 * Break events aren't provided. Instead, use <code>new LockableBlockEntity(world, blockPos).exists()</code> from
 * within break block events or custom mixins to check if the block being broken is a lockable one.
 */
public final class LockableBlockEvents {

    public enum InteractionType {
        OPEN_KEY_MATCHED,           // Opened lock block using a key
        OPEN_MASTER_KEY,            // Opened locked block using a master key
        OPEN_LOCKPICK,              // Opened locked block using a lockpick
        FAILED,                     // Failed to open locked block
        REMOVED_PADLOCK,            // Removed the padlock from a locked block
        FAILED_REMOVE_PADLOCK       // Failed to remove a padlock from the locked block, denied access
    }

    private LockableBlockEvents() {}

    public static Event<LockableBlockEvents.Interact> INTERACT = EventFactory.createArrayBacked(LockableBlockEvents.Interact.class, (listeners) ->
            (lockableBlock, serverPlayer, action) -> {
                for(LockableBlockEvents.Interact listener : listeners)
                    return listener.interact(lockableBlock, serverPlayer, action);
                return false;
            }
    );

    public static Event<Create> CREATE = EventFactory.createArrayBacked(LockableBlockEvents.Create.class, (listeners) ->
            (lockableBlock, serverPlayer) -> {
                for(Create listener : listeners)
                    listener.create(lockableBlock, serverPlayer);
            }
    );

    @FunctionalInterface
    public interface Interact {
        /**
         * Fired just before a locked block player interaction
         *
         * @param lb Lockable block. Contains additional info on the blocks original owner, lock type and resistance
         * @param player Player that initiated the interaction
         * @param type Type of interaction performed by the player, e.g. failed to remove the padlock
         *
         * @return whether the interaction should be cancelled and the player denied entry to the locked block.
         *         Return true to cancel, false to continue. For interactions that already failed the result is ignored
         */
        boolean interact(LockableBlock lb, ServerPlayerEntity player, InteractionType type);
    }

    @FunctionalInterface
    public interface Create {
        /**
         * Fired just before a locked block is created
         *
         * @param lb Lockable block. Contains additional info on the blocks original owner, lock type and resistance
         * @param player Player that created this locked block
         */
        void create(LockableBlock lb, ServerPlayerEntity player);
    }
}
