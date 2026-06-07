package net.paulem.vanillahammers.listeners;

import net.paulem.vanillahammers.ArrayMap;
import net.paulem.vanillahammers.Utils;
import net.paulem.vanillahammers.VanillaHammers;
import net.paulem.vanillahammers.events.BlockSelectEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.function.Consumer;

public class HammersListener implements Listener {
    public ArrayMap<Player, Block> fakeOutlineBlocks = new ArrayMap<>();

    @EventHandler
    public void onMining(BlockBreakEvent event) {
        // Logique pour casser les blocs en 3x3 lors du vrai minage
    }

    @EventHandler
    public void onSelectBlock(BlockSelectEvent event) {
        Player player = event.getPlayer();

        // If it has previous blocks selected, remove them
        if(fakeOutlineBlocks.containsKey(player)) {
            List<Block> blocks = fakeOutlineBlocks.get(player);
            for (Block block : blocks) {
                Utils.removeOutline(player, block);
            }
            fakeOutlineBlocks.removeKey(player);
        }

        Block block = event.getBlock();

        // TODO: Replace with real hammer checking
        if(player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE) {
            BlockFace face = event.getFace();

            Consumer<Block> outlineConsumer = (b) -> {
                if(b.isEmpty() || !b.isSolid()) return;

                fakeOutlineBlocks.put(player, b);
                Utils.makeOutline(player, b);
            };

            // Looking up or down : x and z
            if(face == BlockFace.UP || face == BlockFace.DOWN) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        Block b = block.getRelative(x, 0, z);
                        outlineConsumer.accept(b);
                    }
                }
            }
            // Looking north or south : x and y
            else if(face == BlockFace.NORTH || face == BlockFace.SOUTH) {
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        Block b = block.getRelative(x, y, 0);
                        outlineConsumer.accept(b);
                    }
                }
            }
            // Looking east or west : z and y
            else if(face == BlockFace.EAST || face == BlockFace.WEST) {
                for (int z = -1; z <= 1; z++) {
                    for (int y = -1; y <= 1; y++) {
                        Block b = block.getRelative(0, y, z);
                        outlineConsumer.accept(b);
                    }
                }
            }
        }
    }
}