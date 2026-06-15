package net.paulem.vanillahammers.hammers;

import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.paulem.vanillahammers.VanillaHammers;
import net.paulem.vanillahammers.langs.LangsManager;
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

    public static final Hammer WOOD = register(Material.WOODEN_PICKAXE, 1, 120, Material.OAK_PLANKS, "vanillahammers.wood");
    public static final Hammer STONE = register(Material.STONE_PICKAXE, 1, 300, Material.STONE, "vanillahammers.stone");
    public static final Hammer IRON = register(Material.IRON_PICKAXE, 1, 600, Material.IRON_INGOT, "vanillahammers.iron");
    public static final Hammer GOLD = register(Material.GOLDEN_PICKAXE, 1, 64, Material.GOLD_INGOT, "vanillahammers.gold");
    public static final Hammer DIAMOND = register(Material.DIAMOND_PICKAXE, 1, 4500, Material.DIAMOND, "vanillahammers.diamond");
    public static final Hammer NETHERITE = register(Material.NETHERITE_PICKAXE, 2, 6000, Material.NETHERITE_INGOT, "vanillahammers.netherite");

    public static void init() {
        VanillaHammers.INSTANCE.getLogger().info("Initialized hammers !");

        LangsManager.init();
    }

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
    @Getter
    private final NamespacedKey hammerTranslationKey;

    public Hammer(Material material, int radius, int maxDamage, Material recipeMaterial, String hammerTranslationKey) {
        this.material = material;
        this.radius = radius;
        this.maxDamage = maxDamage;
        this.recipeMaterial = recipeMaterial;
        this.hammerTranslationKey = VanillaHammers.key(hammerTranslationKey);
    }

    public static Hammer register(Material material, int radius, int maxDamage, Material recipeMaterial, String hammerName) {
        Hammer hammer = new Hammer(material, radius, maxDamage, recipeMaterial, hammerName);
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
        // TODO: Make it translatable
        stack.setData(DataComponentTypes.ITEM_NAME, Component.translatable(getHammerTranslationKey().asString()));

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
