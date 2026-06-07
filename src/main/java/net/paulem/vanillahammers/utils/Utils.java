package net.paulem.vanillahammers.utils;

import net.kyori.adventure.util.TriState;
import net.paulem.vanillahammers.VanillaHammers;
import net.paulem.vanillahammers.loottables.EmptyLootTable;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class Utils {
    public static double DEFAULT_BLOCK_RANGE = 4.5;

    public static double getPlayerBlockRange(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);
        if (attribute != null) {
            // Get the attribute value modified
            return attribute.getValue();
        } else {
            Attributable defaultAttributes = player.getType().getDefaultAttributes();
            AttributeInstance defaultAttribute = defaultAttributes.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);

            if (defaultAttribute != null) {
                // Get the attribute value from default type
                return defaultAttribute.getValue();
            } else {
                VanillaHammers.INSTANCE.getLogger().warning("Could not get block range for player " + player.getName() + ", using default value");
                // In fallback, from an hardcoded value
                return DEFAULT_BLOCK_RANGE;
            }
        }
    }

    public static void makeOutline(Player player, Block referenceBlock) {
        referenceBlock.getWorld().spawn(referenceBlock.getLocation(), Shulker.class, shulker -> {
            shulker.setLootTable(EmptyLootTable.INSTANCE);
            shulker.setAI(false);
            shulker.setInvulnerable(true);
            shulker.setDespawnInPeacefulOverride(TriState.FALSE);
            shulker.setCustomNameVisible(false);
            shulker.setGravity(false);

            setSize(shulker, 0.99);

            shulker.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, true, false, false));

            try {
                VanillaHammers.INSTANCE.glowingEntities.setGlowing(shulker, player, ChatColor.BLACK);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        });
    }

    public static void removeOutline(@Nullable Player player, Block referenceBlock) {
        Collection<Shulker> nearbyShulkers = referenceBlock.getWorld().getNearbyEntitiesByType(Shulker.class, referenceBlock.getLocation(), 0.01);

        for (Shulker shulker : nearbyShulkers) {
            if(player != null) {
                try {
                    VanillaHammers.INSTANCE.glowingEntities.unsetGlowing(shulker, player);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
            shulker.remove();
        }
    }

    public static void setSize(LivingEntity entity, double newScale) {
        Objects.requireNonNull(entity.getAttribute(Attribute.SCALE)).setBaseValue(newScale);
    }
}
