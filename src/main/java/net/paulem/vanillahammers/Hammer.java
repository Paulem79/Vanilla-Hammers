package net.paulem.vanillahammers;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.paulem.vanillahammers.managers.BlockOutlineManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Hammer {
    public static ItemStack getHammer() {
        ItemStack stack = ItemStack.of(Material.IRON_PICKAXE);
        stack.setAmount(1);

        stack.setData(DataComponentTypes.MAX_DAMAGE, 8192);
        stack.setData(DataComponentTypes.ITEM_MODEL, VanillaHammers.HAMMER_KEY);

        stack.editPersistentDataContainer(persistentDataContainer -> persistentDataContainer.set(VanillaHammers.HAMMER_KEY, PersistentDataType.BOOLEAN, true));

        return stack;
    }

    public static boolean isHammer(ItemStack stack) {
        if(stack == null) return false;

        return stack.getPersistentDataContainer().has(VanillaHammers.HAMMER_KEY) && Boolean.TRUE.equals(stack.getPersistentDataContainer().get(VanillaHammers.HAMMER_KEY, PersistentDataType.BOOLEAN));
    }

    // TODO: Add custom radius support
    public static List<Block> getBlocksFor(Block block, BlockFace face) {
        List<Block> blocks = new ArrayList<>();

        // Looking up or down : x and z
        if(face == BlockFace.UP || face == BlockFace.DOWN) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Block b = block.getRelative(x, 0, z);
                    blocks.add(b);
                }
            }
        }
        // Looking north or south : x and y
        else if(face == BlockFace.NORTH || face == BlockFace.SOUTH) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    Block b = block.getRelative(x, y, 0);
                    blocks.add(b);
                }
            }
        }
        // Looking east or west : z and y
        else if(face == BlockFace.EAST || face == BlockFace.WEST) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 1; y++) {
                    Block b = block.getRelative(0, y, z);
                    blocks.add(b);
                }
            }
        }

        return blocks;
    }
}
