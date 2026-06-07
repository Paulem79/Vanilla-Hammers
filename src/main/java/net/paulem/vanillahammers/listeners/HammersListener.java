package net.paulem.vanillahammers.listeners;

import net.paulem.vanillahammers.Hammer;
import net.paulem.vanillahammers.managers.BlockOutlineManager;
import net.paulem.vanillahammers.events.BlockSelectEvent;
import net.paulem.vanillahammers.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

// TODO: Hi again, how does the breaking stage works ? I want to make a block show the breaking stage based on the tool the player has in his hand, so that if it collects fast then the breaking stage is fast, exactly like vanilla behaviour + link BreakingStageManager
public class HammersListener implements Listener {
    @EventHandler
    public void onMining(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        Block brokenBlock = event.getBlock();

        if(!Hammer.isHammer(stack)) return;

        int playerBlockRange = (int) Utils.getPlayerBlockRange(player);
        Block lookingAtBlock = player.getTargetBlockExact(playerBlockRange);
        BlockFace face = player.getTargetBlockFace(playerBlockRange);

        // Prevent stack overflow with Player#breakBlock
        if(lookingAtBlock == null || !brokenBlock.getLocation().equals(lookingAtBlock.getLocation())) return;

        if(face == null) return;

        List<Block> blocksToDestroy = Hammer.getBlocksFor(lookingAtBlock, face);
        for (Block b : blocksToDestroy) {
            if(lookingAtBlock.getLocation().equals(b.getLocation())) continue;

            // TODO: Seems like too much durability is taken off, fix this
            player.breakBlock(b);
            player.damageItemStack(EquipmentSlot.HAND, 1);

            ItemStack damagedHammer = player.getInventory().getItemInMainHand();
            // If the hammer is destroyed, don't break blocks anymore
            if(damagedHammer.isEmpty()) break;
        }
    }

    @EventHandler
    public void onSelectBlock(BlockSelectEvent event) {
        Player player = event.getPlayer();

        // If it has previous outlines, remove them
        BlockOutlineManager.removePreviousOutlines(player);

        Block block = event.getBlock();

        if(block == null) return;
        if(block.isEmpty() || !block.isSolid()) return;

        if(Hammer.isHammer(player.getInventory().getItemInMainHand())) {
            BlockFace face = event.getFace();

            List<Block> blocksToOutline = Hammer.getBlocksFor(block, face);
            for (Block b : blocksToOutline) {
                BlockOutlineManager.addToOutlines(player, b);
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        BlockOutlineManager.removePreviousOutlines(event.getPlayer());
    }
}