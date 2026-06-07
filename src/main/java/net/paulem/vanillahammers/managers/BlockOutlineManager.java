package net.paulem.vanillahammers.managers;

import net.paulem.vanillahammers.utils.ArrayMap;
import net.paulem.vanillahammers.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class BlockOutlineManager {
    private static final ArrayMap<Player, Block> FAKE_OUTLINE_BLOCKS = new ArrayMap<>();

    /**
     * Removes all outlines from all players
     */
    public static void removeAllOutlines() {
        for (List<Block> blocks : FAKE_OUTLINE_BLOCKS.values()) {
            for (Block block : blocks) {
                Utils.removeOutline(null, block);
            }
        }
    }

    /**
     * Removes all outlines from a player
     * @param player Player to remove outlines from
     */
    public static void removePreviousOutlines(Player player) {
        if(FAKE_OUTLINE_BLOCKS.containsKey(player)) {
            List<Block> blocks = FAKE_OUTLINE_BLOCKS.get(player);
            for (Block block : blocks) {
                Utils.removeOutline(player, block);
            }
            FAKE_OUTLINE_BLOCKS.removeKey(player);
        }
    }

    /**
     * Shows an outline to a player
     * @param player Player to show outline to
     * @param block Block to outline
     */
    public static void addToOutlines(Player player, Block block) {
        if(block.isEmpty() || !block.isSolid()) return;

        FAKE_OUTLINE_BLOCKS.put(player, block);
        Utils.makeOutline(player, block);
    }
}
