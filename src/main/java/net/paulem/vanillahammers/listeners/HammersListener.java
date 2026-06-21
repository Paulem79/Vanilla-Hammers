package net.paulem.vanillahammers.listeners;

import io.papermc.paper.event.block.BlockBreakProgressUpdateEvent;
import net.paulem.vanillahammers.VanillaHammers;
import net.paulem.vanillahammers.hammers.Hammer;
import net.paulem.vanillahammers.events.PlayerStopDamageBlockEvent;
import net.paulem.vanillahammers.managers.BlockOutlineManager;
import net.paulem.vanillahammers.events.BlockSelectEvent;
import net.paulem.vanillahammers.managers.BreakingStageManager;
import net.paulem.vanillahammers.utils.RaycastUtils;
import net.paulem.vanillahammers.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class HammersListener implements Listener {

    @EventHandler
    public void onBlockProgressEvent(BlockBreakProgressUpdateEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player player)) return;

        ItemStack stack = player.getInventory().getItemInMainHand();
        Hammer hammer = Hammer.getHammer(stack);

        if (hammer == null) return;

        Block block = event.getBlock();

        RaycastUtils.getTargetBlockFaceAsync(player).thenAccept(face -> {
            if (face == null) return;

            Set<Block> blocksToDestroy = hammer.getBlocksFor(block, face);
            for (Block b : blocksToDestroy) {
                // Don't show break stage on the block being broken, client already do it
                if (block.getLocation().equals(b.getLocation())) continue;
                // Don't show break stage on eg: grass
                if (b.isEmpty() || !b.isSolid()) continue;
                // Don't show break stage on unbreakable blocks
                if (Utils.isBlockUnbreakable(player, b)) continue;

                float progress = event.getProgress();
                BreakingStageManager.sendBlockDamageToNearbyPlayers(b.getLocation(), progress, 10.0);
            }
        }).exceptionally(throwable -> {
            Throwable realException = (throwable instanceof java.util.concurrent.CompletionException)
                    ? throwable.getCause()
                    : throwable;

            VanillaHammers.INSTANCE.getLogger().throwing(
                    "HammersListener",
                    "onBlockProgressEvent#thenAccept",
                    realException
            );
            return null;
        });
    }

    @EventHandler
    public void onBlockProgressEvent(PlayerStopDamageBlockEvent event) {
        Player player = event.getPlayer();

        ItemStack stack = player.getInventory().getItemInMainHand();
        Hammer hammer = Hammer.getHammer(stack);

        if (hammer == null) return;

        Block block = event.getBlock();
        BlockFace face = event.getFace();

        if (block == null || face == null) return;

        Set<Block> blocksToDestroy = hammer.getBlocksFor(block, face);
        for (Block b : blocksToDestroy) {
            if (block.getLocation().equals(b.getLocation())) continue;
            // Don't reset break stage on unbreakable blocks
            if (Utils.isBlockUnbreakable(player, b)) continue;

            BreakingStageManager.resetBlockDamageToNearbyPlayers(b.getLocation(), 10.0);
        }
    }

    @EventHandler
    public void onMining(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        Block brokenBlock = event.getBlock();

        Hammer hammer = Hammer.getHammer(stack);

        if (hammer == null) return;

        List<Block> outlines = BlockOutlineManager.getOutlines(player);

        BlockOutlineManager.removePreviousOutlines(player);

        for (Block b : outlines) {
            // Don't break unbreakable blocks
            if (Utils.isBlockUnbreakable(player, b)) continue;

            player.breakBlock(b);
            BreakingStageManager.resetBlockDamageToNearbyPlayers(b.getLocation(), 10.0);

            ItemStack damagedHammer = player.getInventory().getItemInMainHand();
            // If the hammer is destroyed, don't break blocks anymore
            if (damagedHammer.isEmpty()) break;
        }
    }

    @EventHandler
    public void onSelectBlock(BlockSelectEvent event) {
        Player player = event.getPlayer();

        // If it has previous outlines, remove them
        BlockOutlineManager.removePreviousOutlines(player);

        Block block = event.getBlock();

        if (block == null) return;
        if (block.isEmpty() || !block.isSolid()) return;

        ItemStack stack = player.getInventory().getItemInMainHand();
        Hammer hammer = Hammer.getHammer(stack);

        if (hammer == null) return;

        BlockFace face = event.getFace();

        Set<Block> blocksToOutline = hammer.getBlocksFor(block, face);
        for (Block b : blocksToOutline) {
            if (block.getLocation().equals(b.getLocation()) && !Utils.isCube(b)) continue;

            BlockOutlineManager.addToOutlines(player, b, Utils.isBlockUnbreakable(player, b));
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        BlockOutlineManager.removePreviousOutlines(event.getPlayer());
    }
}