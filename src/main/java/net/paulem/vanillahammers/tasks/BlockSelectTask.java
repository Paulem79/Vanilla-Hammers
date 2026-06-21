package net.paulem.vanillahammers.tasks;

import net.paulem.vanillahammers.VanillaHammers;
import net.paulem.vanillahammers.utils.RaycastUtils;
import net.paulem.vanillahammers.events.BlockSelectEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class BlockSelectTask implements Runnable {
    private static Map<Player, Location> selectedBlocks = new HashMap<>();
    private static Map<Player, BlockFace> selectedFace = new HashMap<>();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(player.getGameMode() == GameMode.SPECTATOR) {
                new BlockSelectEvent(player, null, null).callEvent();
                continue;
            }

            CompletableFuture<Entity> entityFuture = RaycastUtils.getTargetEntityAsync(player, Set.of(Shulker.class));
            CompletableFuture<Block> blockFuture = RaycastUtils.getTargetBlockAsync(player);
            CompletableFuture<BlockFace> faceFuture = RaycastUtils.getTargetBlockFaceAsync(player);

            CompletableFuture.allOf(entityFuture, blockFuture, faceFuture).thenAccept(v -> {
                // On récupère les résultats prêts sans bloquer le thread principal
                Entity entityInPath = entityFuture.join();
                Block block = blockFuture.join();
                BlockFace face = faceFuture.join();


                if (block == null) {
                    selectedBlocks.remove(player);
                    selectedFace.remove(player);

                    new BlockSelectEvent(player, null, null).callEvent();
                    return;
                }

                // If entity isn't null and it's closer to the player than the block, then continue
                if (entityInPath != null) {
                    Location playerLocation = player.getLocation();
                    double blockDistance = playerLocation.distanceSquared(block.getLocation());
                    double entityDistance = playerLocation.distanceSquared(entityInPath.getLocation());
                    if (entityDistance < blockDistance) {
                        selectedBlocks.remove(player);
                        selectedFace.remove(player);

                        new BlockSelectEvent(player, null, null).callEvent();
                        return;
                    }
                }

                Location loc = block.getLocation();

                Location lastLoc = selectedBlocks.get(player);
                BlockFace lastFace = selectedFace.get(player);


                if (lastLoc == null || lastFace != face) {
                    selectedBlocks.put(player, loc);
                    selectedFace.put(player, face);

                    new BlockSelectEvent(player, block, face).callEvent();
                } else {
                    double distanceSquared = lastLoc.distanceSquared(loc);

                    if (distanceSquared >= 0.01) {
                        selectedBlocks.put(player, loc);
                        selectedFace.put(player, face);

                        new BlockSelectEvent(player, block, face).callEvent();
                    }
                }
            }).exceptionally(throwable -> {
                Throwable realException = (throwable instanceof CompletionException)
                        ? throwable.getCause()
                        : throwable;

                VanillaHammers.INSTANCE.getLogger().throwing(
                        "BlockSelectTask",
                        "run#thenAccept",
                        realException
                );
                return null;
            });
        }
    }
}
