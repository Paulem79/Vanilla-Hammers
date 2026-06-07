package net.paulem.vanillahammers;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;

import java.util.Objects;

public class Utils {
    public static double DEFAULT_BLOCK_RANGE = 4.5;

    public static double getPlayerBlockRange(Player player) {
        player.registerAttribute(Attribute.BLOCK_INTERACTION_RANGE);

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
        //BreakingStage.startBreakingStages(referenceBlock);
        try {
            VanillaHammers.INSTANCE.glowingBlocks.setGlowing(referenceBlock, player, ChatColor.BLACK);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static void removeOutline(Player player, Block referenceBlock) {
        try {
            VanillaHammers.INSTANCE.glowingBlocks.unsetGlowing(referenceBlock, player);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static void setSize(LivingEntity entity, double newScale) {
        Objects.requireNonNull(entity.getAttribute(Attribute.SCALE)).setBaseValue(newScale);
    }
}
