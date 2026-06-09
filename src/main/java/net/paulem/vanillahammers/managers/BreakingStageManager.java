package net.paulem.vanillahammers.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class BreakingStageManager {

    public static void sendBlockDamageToNearbyPlayers(Location blockLocation, float progress, double radius) {
        if (blockLocation.getWorld() == null) return;

        Collection<Player> nearbyPlayers = blockLocation.getWorld()
                .getNearbyEntities(blockLocation, radius, radius, radius)
                .stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .toList();

        for (Player player : nearbyPlayers) {
            // progress must be between 0.0f and 1.0f, hashcode used for transactionid, because it's always the same for the specific block, and it's cheap
            player.sendBlockDamage(blockLocation, Math.clamp(progress, 0.0f, 1.0f), blockLocation.hashCode());
        }
    }

    public static void resetBlockDamageToNearbyPlayers(Location blockLocation, double radius) {
        sendBlockDamageToNearbyPlayers(blockLocation, 0.0f, radius);
    }
}