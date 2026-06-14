package net.paulem.vanillahammers.hammers;

import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import net.paulem.vanillahammers.VanillaHammers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import ovh.paulem.arcana.registry.RegistryKey;
import ovh.paulem.arcana.registry.WriteableRegistry;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class Hammer implements RegistryKey<Material> {
    // Could have been a FrozenRegistry, but I prefer Writeable here because in case someone wants to add another hammer on the fly, whatever about the resourcepack, then he's welcome !
    public static final WriteableRegistry<Hammer, Material> HAMMERS = new WriteableRegistry<>();

    public static final Hammer WOOD = register(HammerProperties.WOOD);
    public static final Hammer STONE = register(HammerProperties.STONE);
    public static final Hammer IRON = register(HammerProperties.IRON);
    public static final Hammer GOLD = register(HammerProperties.GOLD);
    public static final Hammer DIAMOND = register(HammerProperties.DIAMOND);
    public static final Hammer NETHERITE = register(HammerProperties.NETHERITE);

    @Nullable
    public static Hammer getHammer(ItemStack stack) {
        if(!isHammer(stack)) return null;

        String materialString = stack.getPersistentDataContainer().get(VanillaHammers.HAMMER_PDC_KEY, PersistentDataType.STRING);

        if(materialString == null) return null;

        Material material = Material.matchMaterial(materialString);

        if(material == null) return null;

        return HAMMERS.getOrNull(material);
    }

    public static boolean isHammer(ItemStack stack) {
        if(stack == null) return false;

        return stack.getPersistentDataContainer().has(VanillaHammers.HAMMER_PDC_KEY);
    }

    @Getter
    private final Material material;
    @Getter
    private final int radius;
    @Getter
    private final int maxDamage;
    @Getter
    private final Material recipeMaterial;

    public Hammer(Material material, int radius, int maxDamage, Material recipeMaterial) {
        this.material = material;
        this.radius = radius;
        this.maxDamage = maxDamage;
        this.recipeMaterial = recipeMaterial;
    }

    public static Hammer register(HammerProperties properties) {
        Material material = properties.material();
        int maxDamage = properties.durability();
        int radius = properties.radius();
        Material recipeMaterial = properties.recipeMaterial();

        Hammer hammer = new Hammer(material, radius, maxDamage, recipeMaterial);
        boolean registered = HAMMERS.register(hammer);

        if(!registered) {
            VanillaHammers.INSTANCE.getLogger().log(Level.SEVERE, "Hammer {0} already registered", material);
            return HAMMERS.getOrThrow(material);
        }

        return hammer;
    }

    public ItemStack getStack() {
        ItemStack stack = ItemStack.of(material);
        stack.setAmount(1);

        stack.setData(DataComponentTypes.MAX_DAMAGE, maxDamage);
        stack.setData(DataComponentTypes.ITEM_MODEL, getModelKey());

        stack.editPersistentDataContainer(persistentDataContainer ->
                persistentDataContainer.set(VanillaHammers.HAMMER_PDC_KEY, PersistentDataType.STRING, material.name())
        );

        return stack;
    }

    /**
     * Gets the blocks mineable by the hammer in a radius around a block. (including the block itself)
     */
    public Set<Block> getBlocksFor(Block block, BlockFace face) {
        Set<Block> blocks = new HashSet<>();

        int hammerRadius = getRadius();

        // Looking up or down : x and z
        if (face == BlockFace.UP || face == BlockFace.DOWN) {
            for (int x = -hammerRadius; x <= hammerRadius; x++) {
                for (int z = -hammerRadius; z <= hammerRadius; z++) {
                    Block b = block.getRelative(x, 0, z);
                    blocks.add(b);
                }
            }
        }
        // Looking north or south : x and y
        else if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
            for (int x = -hammerRadius; x <= hammerRadius; x++) {
                for (int y = -hammerRadius; y <= hammerRadius; y++) {
                    Block b = block.getRelative(x, y, 0);
                    blocks.add(b);
                }
            }
        }
        // Looking east or west : z and y
        else if (face == BlockFace.EAST || face == BlockFace.WEST) {
            for (int z = -hammerRadius; z <= hammerRadius; z++) {
                for (int y = -hammerRadius; y <= hammerRadius; y++) {
                    Block b = block.getRelative(0, y, z);
                    blocks.add(b);
                }
            }
        }

        return blocks;
    }

    public NamespacedKey getModelKey() {
        String key = material.name().replace("_PICKAXE", "").toLowerCase() + "_hammer";
        return VanillaHammers.key(key);
    }

    @Override
    public Material getKey() {
        return getMaterial();
    }

    @Override
    public String toString() {
        return "Hammer{" +
                "material=" + material +
                ", radius=" + radius +
                ", maxDamage=" + maxDamage +
                ", recipeMaterial=" + recipeMaterial +
                '}';
    }
}
