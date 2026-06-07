package net.paulem.vanillahammers.loottables;

import net.paulem.vanillahammers.VanillaHammers;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class EmptyLootTable implements LootTable {
    public static final @Nullable LootTable INSTANCE = new EmptyLootTable();

    private EmptyLootTable() {}

    @Override
    public @NotNull Collection<ItemStack> populateLoot(@Nullable Random random, @NotNull LootContext context) {
        return List.of();
    }

    @Override
    public void fillInventory(@NotNull Inventory inventory, @Nullable Random random, @NotNull LootContext context) {}

    @Override
    public @NotNull NamespacedKey getKey() {
        return VanillaHammers.key("empty_loot_table");
    }
}
