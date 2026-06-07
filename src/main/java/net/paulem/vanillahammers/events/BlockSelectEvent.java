package net.paulem.vanillahammers.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

// FIXME: On disconnect and when reconnecting, the event isn't called when the player has not moved before (so trigger it on player join event)
/**
 * Represents an event triggered when a player changes the block they are targeting with their range.
 * <p>
 * This event is called periodically by a scheduled task ({@code BlockSelectCallingTask})
 * which compares the position of the block currently targeted by the player with the last recorded block.
 * </p>
 * @author Paulem
 * @version 1.0
 */
public class BlockSelectEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private Player player;
    private @Nullable Block block;
    private @Nullable BlockFace face;

    public BlockSelectEvent(Player player, @Nullable Block block, @Nullable BlockFace face) {
        this.player = player;
        this.block = block;
        this.face = face;
    }

    public Player getPlayer() {
        return player;
    }

    public @Nullable Block getBlock() {
        return block;
    }

    public @Nullable BlockFace getFace() {
        return face;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setBlock(@Nullable Block block) {
        this.block = block;
    }

    public void setFace(@Nullable BlockFace face) {
        this.face = face;
    }

    public boolean isTargetingAir() {
        return getBlock() == null;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}