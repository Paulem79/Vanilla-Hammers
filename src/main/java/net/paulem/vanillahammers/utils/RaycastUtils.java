package net.paulem.vanillahammers.utils;

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

public class RaycastUtils {
    private RaycastUtils() {}

    @Nullable
    public static BlockFace getTargetBlockFace(@NotNull Player player) {
        int playerBlockRange = Utils.getPlayerBlockRange(player);
        Block block = getTargetBlock(player);

        // Not more checks because we already checked in getTargetBlock
        if(block == null) return null;

        return player.getTargetBlockFace(playerBlockRange);
    }

    @Nullable
    public static Block getTargetBlock(@NotNull Player player) {
        double playerRange = Utils.getPlayerRange(player);
        Location eyeLocation = player.getEyeLocation();

        RayTraceResult rayTrace = player.getWorld().rayTraceBlocks(
                eyeLocation,
                eyeLocation.getDirection(),
                playerRange,
                FluidCollisionMode.NEVER,
                false
        );

        // If the raytrace hit a block, return it
        if (rayTrace != null && rayTrace.getHitBlock() != null) {
            return rayTrace.getHitBlock();
        }

        return null;
    }

    /**
     * This doesn't use entity interaction range, but block one ! Don't rely on this for most entity methods, except some cases !
     */
    @Nullable
    public static Entity getTargetEntity(@NotNull Player player, Set<Class<? extends Entity>> except) {
        double entityRange = Utils.getPlayerRange(player);
        Location eyeLocation = player.getEyeLocation();

        // Raytrace for entities in the world along the player's sight line
        RayTraceResult rayTrace = player.getWorld().rayTraceEntities(
                eyeLocation,
                eyeLocation.getDirection(),
                entityRange,
                entity -> entity != player && except.stream().noneMatch(clasz -> clasz.isInstance(entity))
        );

        // If we hit something, return the entity
        if (rayTrace != null && rayTrace.getHitEntity() != null) {
            return rayTrace.getHitEntity();
        }

        return null;
    }
}
