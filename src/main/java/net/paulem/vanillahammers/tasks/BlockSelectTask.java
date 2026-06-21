package net.paulem.vanillahammers.tasks;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.paulem.vanillahammers.utils.RaycastUtils;
import net.paulem.vanillahammers.events.BlockSelectEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;

import java.util.Set;
import java.util.function.Consumer;

public class BlockSelectTask implements Consumer<ScheduledTask> {
    private final Player player;

    // Track data per instance now since each player has their own task loop
    private Location selectedBlock = null;
    private BlockFace selectedFace = null;

    public BlockSelectTask(Player player) {
        this.player = player;
    }

    @Override
    public void accept(ScheduledTask scheduledTask) {
        if (!player.isOnline()) {
            scheduledTask.cancel();
            return;
        }

        if (player.getGameMode() == GameMode.SPECTATOR) {
            new BlockSelectEvent(player, null, null).callEvent();
            return;
        }

        Entity entityInPath = RaycastUtils.getTargetEntity(player, Set.of(Shulker.class));
        Block block = RaycastUtils.getTargetBlock(player);

        if (block == null) {
            selectedBlock = null;
            selectedFace = null;
            new BlockSelectEvent(player, null, null).callEvent();
            return;
        }

        // Check if an entity is blocking the selection
        if (entityInPath != null) {
            Location playerLocation = player.getLocation();
            double blockDistance = playerLocation.distanceSquared(block.getLocation());
            double entityDistance = playerLocation.distanceSquared(entityInPath.getLocation());

            if (entityDistance < blockDistance) {
                selectedBlock = null;
                selectedFace = null;
                new BlockSelectEvent(player, null, null).callEvent();
                return;
            }
        }

        Location loc = block.getLocation();
        BlockFace face = RaycastUtils.getTargetBlockFace(player);

        // Trigger event only if selection changed
        if (selectedBlock == null || selectedBlock.distanceSquared(loc) >= 0.01 || selectedFace != face) {
            selectedBlock = loc;
            selectedFace = face;
            new BlockSelectEvent(player, block, face).callEvent();
        }
    }
}