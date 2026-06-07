package net.paulem.vanillahammers.managers;

import net.paulem.vanillahammers.VanillaHammers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class BreakingStageManager {
    public static void startBreakingStages(Block block) {
        Location blockLocation = block.getLocation();
        // Array in order to edit the single stored value
        final int[] stage = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (stage[0] >= 11) {
                    block.setType(Material.AIR);
                    sendBlockDamageToNearbyPlayers(blockLocation, 0, 10.0);
                    cancel();
                    return;
                }

                sendBlockDamageToNearbyPlayers(blockLocation, stage[0] / 10.0f, 10.0);

                stage[0]++;
            }
        }.runTaskTimer(VanillaHammers.INSTANCE, 20L, 20L);
    }

    private static void sendBlockDamageToNearbyPlayers(Location blockLocation, float damage, double radius) {
        if (blockLocation.getWorld() == null) return;

        // Get entities and only get players
        Collection<Player> nearbyPlayers = blockLocation.getWorld()
                .getNearbyEntities(blockLocation, radius, radius, radius)
                .stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .toList();

        int transactionId = ThreadLocalRandom.current().nextInt(0, 100000);

        for (Player player : nearbyPlayers) {
            player.sendBlockDamage(blockLocation, damage, transactionId);
        }
    }
}
