package net.paulem.vanillahammers.utils;

import net.paulem.vanillahammers.VanillaHammers;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RaycastUtils {
    private RaycastUtils() {}

    /**
     * Récupère de manière asynchrone le bloc ciblé par le joueur.
     */
    @NotNull
    public static CompletableFuture<Block> getTargetBlockAsync(@NotNull Player player) {
        CompletableFuture<Block> future = new CompletableFuture<>();
        double playerRange = Utils.getPlayerRange(player);
        Location eyeLocation = player.getEyeLocation();

        player.getScheduler().execute(VanillaHammers.INSTANCE, () -> {
            try {
                RayTraceResult rayTrace = player.getWorld().rayTraceBlocks(
                        eyeLocation,
                        eyeLocation.getDirection(),
                        playerRange,
                        FluidCollisionMode.NEVER,
                        false
                );

                if (rayTrace != null && rayTrace.getHitBlock() != null) {
                    future.complete(rayTrace.getHitBlock());
                } else {
                    future.complete(null);
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, null, 1);

        return future;
    }

    /**
     * Récupère de manière asynchrone la BlockFace du bloc ciblé par le joueur.
     * Cette méthode utilise un seul RayTrace pour de meilleures performances.
     */
    @NotNull
    public static CompletableFuture<BlockFace> getTargetBlockFaceAsync(@NotNull Player player) {
        CompletableFuture<BlockFace> future = new CompletableFuture<>();
        double playerRange = Utils.getPlayerRange(player);
        Location eyeLocation = player.getEyeLocation();

        player.getScheduler().execute(VanillaHammers.INSTANCE, () -> {
            try {
                RayTraceResult rayTrace = player.getWorld().rayTraceBlocks(
                        eyeLocation,
                        eyeLocation.getDirection(),
                        playerRange,
                        FluidCollisionMode.NEVER,
                        false
                );

                // Si on a touché un bloc, la face est directement disponible dans le résultat du RayTrace
                if (rayTrace != null && rayTrace.getHitBlock() != null) {
                    future.complete(rayTrace.getHitBlockFace());
                } else {
                    future.complete(null);
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, null, 1);

        return future;
    }

    /**
     * Récupère de manière asynchrone l'entité ciblée par le joueur.
     */
    @NotNull
    public static CompletableFuture<Entity> getTargetEntityAsync(@NotNull Player player, @Nullable Set<Class<? extends Entity>> except) {
        CompletableFuture<Entity> future = new CompletableFuture<>();
        double entityRange = Utils.getPlayerRange(player);
        Location eyeLocation = player.getEyeLocation();

        player.getScheduler().execute(VanillaHammers.INSTANCE, () -> {
            try {
                RayTraceResult rayTrace = player.getWorld().rayTraceEntities(
                        eyeLocation,
                        eyeLocation.getDirection(),
                        entityRange,
                        entity -> {
                            if (entity == player) return false;
                            if (except == null || except.isEmpty()) return true;
                            return except.stream().noneMatch(clasz -> clasz.isInstance(entity));
                        }
                );

                if (rayTrace != null) {
                    future.complete(rayTrace.getHitEntity());
                } else {
                    future.complete(null);
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, null, 1);

        return future;
    }
}