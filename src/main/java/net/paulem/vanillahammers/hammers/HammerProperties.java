package net.paulem.vanillahammers.hammers;

import org.bukkit.Material;

public record HammerProperties(Material material, int radius, int durability, Material recipeMaterial) {
    public static final HammerProperties WOOD = new HammerProperties(Material.WOODEN_PICKAXE, 1, 120, Material.OAK_PLANKS);
    public static final HammerProperties STONE = new HammerProperties(Material.STONE_PICKAXE, 1, 300, Material.STONE);
    public static final HammerProperties IRON = new HammerProperties(Material.IRON_PICKAXE, 1, 600, Material.IRON_INGOT);
    public static final HammerProperties GOLD = new HammerProperties(Material.GOLDEN_PICKAXE, 1, 64, Material.GOLD_INGOT);
    public static final HammerProperties DIAMOND = new HammerProperties(Material.DIAMOND_PICKAXE, 1, 4500, Material.DIAMOND);
    public static final HammerProperties NETHERITE = new HammerProperties(Material.NETHERITE_PICKAXE, 2, 6000, Material.NETHERITE_INGOT);
}
