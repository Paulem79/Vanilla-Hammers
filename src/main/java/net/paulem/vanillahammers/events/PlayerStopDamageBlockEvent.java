package net.paulem.vanillahammers.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * This is not tick perfect ! But calls after ~4 ticks
 */
public class PlayerStopDamageBlockEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Setter
    @Getter
    private Player player;
    @Setter
    @Getter
    private @Nullable Block block;
    @Setter
    @Getter
    private @Nullable BlockFace face;

    public PlayerStopDamageBlockEvent(Player player, @Nullable Block block, @Nullable BlockFace face) {
        this.player = player;
        this.block = block;
        this.face = face;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}